package home.project.service;

import home.project.dto.requestDTO.CreateCategoryRequestDTO;

public interface CategoryService {
    void save(CreateCategoryRequestDTO createCategoryRequestDTO);
}
