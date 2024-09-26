package home.project.service;

import home.project.domain.Category;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public void save(CreateCategoryRequestDTO createCategoryRequestDTO) {
        Category category = new Category();
        category.setCode(createCategoryRequestDTO.getCode());
        category.setName(createCategoryRequestDTO.getName());
        category.setLevel(createCategoryRequestDTO.getLevel());

        if (createCategoryRequestDTO.getLevel() <= 0) {
            throw new IllegalArgumentException("Category level must be greater than 0");
        } else if (createCategoryRequestDTO.getLevel() == 1) {
            // 최상위 카테고리인 경우 부모를 null로 설정
            category.setParent(null);
        } else {
            // 레벨이 2 이상인 경우 부모 카테고리를 찾아 설정
            String parentCode = createCategoryRequestDTO.getCode().substring(0, createCategoryRequestDTO.getCode().length() - 2);
            Category parentCategory = categoryRepository.findByCode(parentCode);
            if (parentCategory == null) {
                throw new IllegalArgumentException("Parent category not found for code: " + parentCode);
            }
            category.setParent(parentCategory);
        }

        categoryRepository.save(category);

        // 부모 카테고리가 있는 경우, 부모의 자식 목록에 현재 카테고리를 추가
        if (category.getParent() != null) {
            Category parent = category.getParent();
            parent.getChildren().add(category);
            categoryRepository.save(parent);  // 부모 카테고리 업데이트
        }
    }
}
