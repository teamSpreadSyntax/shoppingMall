package home.project.service;

import home.project.domain.Category;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.dto.requestDTO.UpdateCategoryRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryService {
    void join(CreateCategoryRequestDTO createCategoryRequestDTO);

    Optional<Category> findById(Long categoryId);

    Page<Category> findAllCategory(Pageable pageable);

    void validateCategoryCode(String code, int level);

    void update(UpdateCategoryRequestDTO updateCategoryRequestDTO);

    void delete(Long categoryId);
}