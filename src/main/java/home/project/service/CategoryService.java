package home.project.service;

import home.project.domain.Category;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.dto.requestDTO.UpdateCategoryRequestDTO;
import home.project.dto.responseDTO.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryService {
    void join(CreateCategoryRequestDTO createCategoryRequestDTO);

    Category findById(Long categoryId);

    Page<Category> findAllCategory(Pageable pageable);

    void validateCategoryCode(String code, int level);

    CategoryResponse update(UpdateCategoryRequestDTO updateCategoryRequestDTO);

    void delete(Long categoryId);
}