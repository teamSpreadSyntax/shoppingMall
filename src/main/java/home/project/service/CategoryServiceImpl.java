package home.project.service;

import home.project.domain.Category;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public void save(CreateCategoryRequestDTO dto) {
        validateCategoryLevel(dto.getLevel());
        Category category = createCategory(dto);
        setCategoryParent(category, dto);
        categoryRepository.save(category);
    }

    @Override
    public Page<Category> findAllCategory(Pageable pageable){
        Page<Category> allCategory = categoryRepository.findAll(pageable);
        return allCategory;
    }

    private void validateCategoryLevel(int level) {
        if (level <= 0) {
            throw new IllegalArgumentException("카테고리 레벨은 0보다 작을 수 없습니다. 최상위 카테고리라면 레벨을 1로 설정해주세요.");
        }
    }

    private Category createCategory(CreateCategoryRequestDTO dto) {
        Category category = new Category();
        category.setCode(dto.getCode());
        category.setName(dto.getName());
        category.setLevel(dto.getLevel());
        return category;
    }

    private void setCategoryParent(Category category, CreateCategoryRequestDTO dto) {
        if (dto.getLevel() == 1) {
            category.setParent(null);
        } else {
            String parentCode = dto.getCode().substring(0, dto.getCode().length() - 2);
            Category parentCategory = categoryRepository.findByCode(parentCode);
            if (parentCategory == null) {
                throw new IllegalArgumentException("상위 카테고리 코드를 찾을 수 없습니다. 상위 카테고리를 먼저 생성하고 다시 시도해주세요.");
            }
            category.setParent(parentCategory);
            parentCategory.getChildren().add(category);
        }
    }
}
