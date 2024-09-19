package home.project.service;

import home.project.domain.Category2;
import home.project.dto.Category2DTOWithoutId;
import home.project.dto.CategoryDTOWithoutId;
import home.project.repository.CategoryRepository;
import home.project.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public void save(Category2DTOWithoutId categoryDTO) {
        Category2 category = new Category2();
        category.setCode(categoryDTO.getCode());
        category.setName(categoryDTO.getName());
        category.setParent(category.getParent());
        category.addChild(category);
        category.setLevel(categoryDTO.getLevel());

        categoryRepository.save(category);
    }
}
