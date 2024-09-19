package home.project.service;

import home.project.domain.Category;
import home.project.dto.CategoryDTOWithoutId;
import home.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public void save(CategoryDTOWithoutId categoryDTO) {
        Category category = new Category();
        category.setCode(categoryDTO.getCode());
        category.setName(categoryDTO.getName());
        category.setParentCode(categoryDTO.getParentCode());
        category.setLevel(categoryDTO.getLevel());

        categoryRepository.save(category);
    }
}
