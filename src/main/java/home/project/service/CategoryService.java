package home.project.service;

import home.project.domain.Category;
import home.project.domain.Member;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    void save(CreateCategoryRequestDTO createCategoryRequestDTO);

    Page<Category> findAllCategory(Pageable pageable);
}
