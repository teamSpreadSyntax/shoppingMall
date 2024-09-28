package home.project.service;

import home.project.domain.Category;
import home.project.domain.Product;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.dto.requestDTO.UpdateCategoryRequestDTO;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.CategoryRepository;
import home.project.repository.ProductRepository;
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
    public void update(UpdateCategoryRequestDTO updateCategoryRequestDTO) {
        Category existingCategory = findById(updateCategoryRequestDTO.getId());

        boolean isModified = false;
        boolean isCategoryCodeModified = false;
        boolean isCodeDuplicate = false;
        boolean isNameDuplicate = false;

        String oldCategoryCode = existingCategory.getCode();

        if (updateCategoryRequestDTO.getCode() != null && !updateCategoryRequestDTO.getCode().equals(existingCategory.getCode())) {
            if (categoryRepository.existsByCode(updateCategoryRequestDTO.getCode())) {
                isCodeDuplicate = true;
            }
            existingCategory.setCode(updateCategoryRequestDTO.getCode());
            isModified = true;
            isCategoryCodeModified = true;
        }

        if (updateCategoryRequestDTO.getName() != null && !updateCategoryRequestDTO.getName().equals(existingCategory.getName()))  {
                if (categoryRepository.existsByName(updateCategoryRequestDTO.getName())) {
                isNameDuplicate = true;
            }
            existingCategory.setName(updateCategoryRequestDTO.getName());
            isModified = true;
        }

        if (updateCategoryRequestDTO.getLevel() != null && !updateCategoryRequestDTO.getLevel().equals(existingCategory.getLevel())) {
            validateCategoryLevel(updateCategoryRequestDTO.getLevel());
            existingCategory.setLevel(updateCategoryRequestDTO.getLevel());
            isModified = true;
        }

        if (isCodeDuplicate && isNameDuplicate) {
            throw new DataIntegrityViolationException("이미 사용 중인 코드와 카테고리명 입니다.");
        } else if (isCodeDuplicate) {
            throw new DataIntegrityViolationException("이미 사용 중인 코드입니다.");
        } else if (isNameDuplicate) {
            throw new DataIntegrityViolationException("이미 사용 중인 카테고리명 입니다.");
        }

        if (!isModified) {
            throw new NoChangeException("변경된 카테고리 정보가 없습니다.");
        }


        setCategoryParentFofUpdate(existingCategory, updateCategoryRequestDTO);

        if(isCategoryCodeModified){
            updateCategoryAndProductCodes(oldCategoryCode, existingCategory.getCode());
        }

        categoryRepository.save(existingCategory);
    }

    private void updateCategoryAndProductCodes(String oldCode, String newCode) {
        List<Category> categoriesToUpdate = categoryRepository.findAllByCodeStartingWith(oldCode);
        List<Product> productsToUpdate = new ArrayList<>();

        for (Category category : categoriesToUpdate) {
            String oldCategoryCode = category.getCode();
            String newCategoryCode;

            if (oldCategoryCode.equals(oldCode)) {
                newCategoryCode = newCode;
            } else {
                newCategoryCode = newCode + oldCategoryCode.substring(oldCode.length());
            }

            category.setCode(newCategoryCode);

            List<Product> categoryProducts = productRepository.findAllByCategory(oldCategoryCode);
            for (Product product : categoryProducts) {
                product.setCategory(newCategoryCode);
                productsToUpdate.add(product);
            }
        }

        List<Product> subCategoryProducts = productRepository.findAllByCategoryStartingWith(oldCode);
        for (Product product : subCategoryProducts) {
            if (!productsToUpdate.contains(product)) {
                String oldProductCategory = product.getCategory();
                String newProductCategory = newCode + oldProductCategory.substring(oldCode.length());
                product.setCategory(newProductCategory);

                String oldProductNum = product.getProductNum();
                String newProductNum = oldProductNum.replace(oldProductCategory, newProductCategory);
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

    private void setCategoryParentFofUpdate(Category category, UpdateCategoryRequestDTO dto) {
        if (dto.getLevel() == 1) {
            category.setParent(null);
        } else {
            String parentCode = dto.getCode().substring(0, dto.getCode().length() - 2);
            Category parentCategory = categoryRepository.findByCode(parentCode).orElseThrow(() -> new IllegalArgumentException("상위 카테고리 코드를 찾을 수 없습니다. 상위 카테고리를 먼저 생성하고 다시 시도해주세요."));

            category.setParent(parentCategory);
            parentCategory.getChildren().add(category);
        }
    }
}
