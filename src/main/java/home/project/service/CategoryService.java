package home.project.service;

import home.project.dto.CategoryDTOWithoutId;

public interface CategoryService {
    void save(CategoryDTOWithoutId product);
}
