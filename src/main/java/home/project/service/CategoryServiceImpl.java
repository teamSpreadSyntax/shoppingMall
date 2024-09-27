package home.project.service;

import home.project.domain.Category;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.dto.requestDTO.UpdateCategoryRequestDTO;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void join(CreateCategoryRequestDTO dto) {
        validateCategoryCode(dto.getCode(), dto.getLevel());
        validateCategoryLevel(dto.getLevel());
        Category category = createCategory(dto);
        setCategoryParent(category, dto.getCode(), dto.getLevel());
        categoryRepository.save(category);
    }

    @Override
    public Category findById(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalStateException("id가 입력되지 않았습니다.");
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IdNotFoundException(categoryId + "(으)로 등록된 카테고리가 없습니다."));
    }

    @Override
    public Page<Category> findAllCategory(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public void validateCategoryCode(String code, int level) {
        int expectedLength = level * 2;
        if (code.length() != expectedLength) {
            throw new IllegalStateException("카테고리 코드의 길이는 레벨 " + level + "에 대해 " + expectedLength + "여야 합니다.");
        }

        if (level > 1) {
            String parentCode = code.substring(0, expectedLength - 2);
            categoryRepository.findByCode(parentCode)
                    .orElseThrow(() -> new IllegalArgumentException("상위 카테고리 코드를 찾을 수 없습니다. 상위 카테고리를 먼저 생성하고 다시 시도해주세요."));
        }
    }

    @Override
    @Transactional
    public void update(UpdateCategoryRequestDTO updateCategoryRequestDTO) {
        Category existingCategory = categoryRepository.findById(updateCategoryRequestDTO.getId())
                .orElseThrow(() -> new IdNotFoundException(updateCategoryRequestDTO.getId() + "(으)로 등록된 카테고리가 없습니다."));

        boolean isModified = false;
        boolean isCodeDuplicate = false;
        boolean isNameDuplicate = false;

        if (updateCategoryRequestDTO.getCode() != null && !Objects.equals(existingCategory.getCode(), updateCategoryRequestDTO.getCode())) {
            if (categoryRepository.existsByCode(updateCategoryRequestDTO.getCode())) {
                isCodeDuplicate = true;
            }
            existingCategory.setCode(updateCategoryRequestDTO.getCode());
            isModified = true;
        }

        if (updateCategoryRequestDTO.getName() != null && !Objects.equals(existingCategory.getName(), updateCategoryRequestDTO.getName())) {
            if (categoryRepository.existsByName(updateCategoryRequestDTO.getName())) {
                isNameDuplicate = true;
            }
            existingCategory.setName(updateCategoryRequestDTO.getName());
            isModified = true;
        }

        if (updateCategoryRequestDTO.getLevel() != null && !Objects.equals(existingCategory.getLevel(), updateCategoryRequestDTO.getLevel())) {
            validateCategoryLevel(updateCategoryRequestDTO.getLevel());
            existingCategory.setLevel(updateCategoryRequestDTO.getLevel());
            isModified = true;
        }

        if (!isModified) {
            throw new NoChangeException("변경된 카테고리 정보가 없습니다.");
        }

        setCategoryParent(existingCategory, updateCategoryRequestDTO.getCode(), updateCategoryRequestDTO.getLevel());
        categoryRepository.save(existingCategory);
    }

    private void validateCategoryLevel(int level) {
        if (level <= 0) {
            throw new IllegalStateException("카테고리 레벨은 0보다 작을 수 없습니다. 최상위 카테고리라면 레벨을 1로 설정해주세요.");
        }
    }

    private Category createCategory(CreateCategoryRequestDTO dto) {
        Category category = new Category();
        category.setCode(dto.getCode());
        category.setName(dto.getName());
        category.setLevel(dto.getLevel());
        return category;
    }

    @Transactional
    void setCategoryParent(Category category, String code, int level) {
        if (category.getParent() != null) {
            Category oldParent = category.getParent();
            oldParent.getChildren().remove(category);
            category.setParent(null);
            categoryRepository.save(oldParent);
        }

        if (level == 1) {
            category.setParent(null);
        } else {
            String parentCode = code.substring(0, code.length() - 2);
            Category parentCategory = categoryRepository.findByCode(parentCode)
                    .orElseThrow(() -> new IllegalArgumentException("상위 카테고리 코드를 찾을 수 없습니다. 상위 카테고리를 먼저 생성하고 다시 시도해주세요."));
            category.setParent(parentCategory);
            parentCategory.getChildren().add(category);
            categoryRepository.save(parentCategory);
        }
    }
}
