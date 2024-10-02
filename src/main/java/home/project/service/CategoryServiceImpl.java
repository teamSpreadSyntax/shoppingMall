package home.project.service;

import home.project.domain.Category;
import home.project.domain.Product;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.dto.requestDTO.UpdateCategoryRequestDTO;
import home.project.dto.responseDTO.CategoryResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.CategoryRepository;
import home.project.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void join(CreateCategoryRequestDTO dto) {
        boolean codeExists = categoryRepository.existsByCode(dto.getCode());
        boolean nameExists = categoryRepository.existsByName(dto.getName());
        if (codeExists && nameExists) {
            throw new DataIntegrityViolationException("이미 존재하는 카테고리 코드와 카테고리 이름입니다.");
        } else if (codeExists) {
            throw new DataIntegrityViolationException("이미 존재하는 카테고리 코드입니다.");
        } else if (nameExists) {
            throw new DataIntegrityViolationException("이미 존재하는 카테고리 이름입니다.");
        }
        validateCategoryCode(dto.getCode(), dto.getLevel());
        validateCategoryLevel(dto.getLevel());
        Category category = createCategory(dto);
        setCategoryParentForJoin(category, dto);
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
    public CategoryResponse update(UpdateCategoryRequestDTO updateCategoryRequestDTO) {
        Category existingCategory = findById(updateCategoryRequestDTO.getId());

        boolean isModified = false;
        boolean isCategoryCodeModified = false;
        boolean isCodeDuplicate = false;
        boolean isNameDuplicate = false;

        String oldCategoryCode = existingCategory.getCode();
        String newCategoryCode = updateCategoryRequestDTO.getCode();
        Integer newLevel = updateCategoryRequestDTO.getLevel();

        if (newCategoryCode != null || newLevel != null) {
            String codeToValidate = newCategoryCode != null ? newCategoryCode : oldCategoryCode;
            int levelToValidate = newLevel != null ? newLevel : existingCategory.getLevel();
            validateCategoryCode(codeToValidate, levelToValidate);
        }

        if (newCategoryCode != null && !newCategoryCode.equals(oldCategoryCode)) {
            if (categoryRepository.existsByCode(newCategoryCode)) {
                throw new DataIntegrityViolationException("이미 사용 중인 코드입니다.");
            }
            isCategoryCodeModified = true;
            isModified = true;
        }

        if (newLevel != null && !newLevel.equals(existingCategory.getLevel())) {
            existingCategory.setLevel(newLevel);
            isModified = true;
        }


        if (updateCategoryRequestDTO.getName() != null && !updateCategoryRequestDTO.getName().equals(existingCategory.getName())) {
            if (categoryRepository.existsByName(updateCategoryRequestDTO.getName())) {
                throw new DataIntegrityViolationException("이미 사용 중인 카테고리명 입니다.");
            }
            existingCategory.setName(updateCategoryRequestDTO.getName());
            isModified = true;
        }

//        if (updateCategoryRequestDTO.getLevel() != null && !updateCategoryRequestDTO.getLevel().equals(existingCategory.getLevel())) {
//            validateCategoryLevel(updateCategoryRequestDTO.getLevel());
//            existingCategory.setLevel(updateCategoryRequestDTO.getLevel());
//            isModified = true;
//        }


        if (!isModified) {
            throw new NoChangeException("변경된 카테고리 정보가 없습니다.");
        }

        setCategoryParentForUpdate(existingCategory, updateCategoryRequestDTO);


        if (isCategoryCodeModified) {
            updateCategoryAndProductCodes(oldCategoryCode, newCategoryCode);
        }

        entityManager.flush();
        entityManager.clear();


        Category updatedCategory = categoryRepository.findById(existingCategory.getId()).orElseThrow();

        if (isCategoryCodeModified) {
            updatedCategory.setCode(newCategoryCode);
        }

        categoryRepository.save(existingCategory);

        CategoryResponse categoryResponse = new CategoryResponse(existingCategory.getId(), existingCategory.getCode(),existingCategory.getName(), existingCategory.getLevel(), updatedCategory.getParent() != null ? updatedCategory.getParent().getId() : null
        );

        return categoryResponse;
    }

    private void updateCategoryAndProductCodes(String oldCode, String newCode) {
        List<Category> categoriesToUpdate = categoryRepository.findAllByCodeStartingWith(oldCode);
        List<Product> productsToUpdate = new ArrayList<>();

        for (Category category : categoriesToUpdate) {
            String oldCategoryCode = category.getCode();
            String newCategoryCode = oldCategoryCode.equals(oldCode) ? newCode : newCode + oldCategoryCode.substring(oldCode.length());

            category.setCode(newCategoryCode);

            List<Product> categoryProducts = productRepository.findAllByCategory(category);
            for (Product product : categoryProducts) {
                String oldProductNum = product.getProductNum();
                String newProductNum = oldProductNum.replace(oldCategoryCode, newCategoryCode);
                product.setProductNum(newProductNum);
                productsToUpdate.add(product);
            }
        }

        categoryRepository.saveAll(categoriesToUpdate);
        productRepository.saveAll(productsToUpdate);
    }

    @Transactional
    @Override
    public void delete(Long categoryId) {
        findById(categoryId);
        categoryRepository.deleteById(categoryId);
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

    private void setCategoryParentForJoin(Category category, CreateCategoryRequestDTO dto) {
        if (dto.getLevel() == 1) {
            category.setParent(null);
        } else {
            String parentCode = dto.getCode().substring(0, dto.getCode().length() - 2);
            Category parentCategory = categoryRepository.findByCode(parentCode).orElseThrow(() -> new IllegalArgumentException("상위 카테고리 코드를 찾을 수 없습니다. 상위 카테고리를 먼저 생성하고 다시 시도해주세요."));

            category.setParent(parentCategory);
            parentCategory.getChildren().add(category);
        }
    }

    private void setCategoryParentForUpdate(Category category, UpdateCategoryRequestDTO dto) {
        if (dto.getLevel() == 1) {
            category.setParent(null);
        } else if (dto.getCode() != null) {
            String parentCode = dto.getCode().substring(0, dto.getCode().length() - 2);
            Category parentCategory = categoryRepository.findByCode(parentCode)
                    .orElseThrow(() -> new IllegalArgumentException("상위 카테고리 코드를 찾을 수 없습니다. 상위 카테고리를 먼저 생성하고 다시 시도해주세요."));

            category.setParent(parentCategory);
        }
    }
}
