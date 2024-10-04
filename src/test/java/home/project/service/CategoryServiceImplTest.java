package home.project.service;

import home.project.domain.Category;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.dto.requestDTO.UpdateCategoryRequestDTO;
import home.project.dto.responseDTO.CategoryResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.CategoryRepository;
import home.project.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ProductRepository productRepository;

    private CreateCategoryRequestDTO createCategoryRequestDTO;
    private UpdateCategoryRequestDTO updateCategoryRequestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setCode("01");
        category1.setName("Parent Category Level1");
        category1.setLevel(1);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setCode("0100");
        category2.setName("Test Category Level2");
        category2.setLevel(2);

        Category category3 = new Category();
        category3.setId(3L);
        category3.setCode("010000");
        category3.setName("Test Category Level3");
        category3.setLevel(3);

        createCategoryRequestDTO = new CreateCategoryRequestDTO();
        createCategoryRequestDTO.setCode("0100");
        createCategoryRequestDTO.setName("Test Category Level2");
        createCategoryRequestDTO.setLevel(2);

        updateCategoryRequestDTO = new UpdateCategoryRequestDTO();
        updateCategoryRequestDTO.setId(2L);
        updateCategoryRequestDTO.setCode("0120");
        updateCategoryRequestDTO.setName("Updated Category");
        updateCategoryRequestDTO.setLevel(2);

        pageable = PageRequest.of(0, 5);
    }

    @Nested
    class JoinTests {
        @Test
        void join_ValidInput_SavesCategory() {
            when(categoryRepository.existsByCode(anyString())).thenReturn(false);
            when(categoryRepository.existsByName(anyString())).thenReturn(false);
            when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(ca));
            when(categoryRepository.save(any(Category.class))).thenReturn(catgory2);

            assertDoesNotThrow(() -> categoryService.join(createCategoryRequestDTO));

            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        void join_DuplicateCode_ThrowsDataIntegrityViolationException() {
            when(categoryRepository.existsByCode(anyString())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> categoryService.join(createCategoryRequestDTO));
        }
    }

    @Nested
    class FindByIdReturnCategoryResponseTests {
        @Test
        void findByIdReturnCategoryResponse_ExistingCategory_ReturnsCategoryResponse() {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

            CategoryResponse response = categoryService.findByIdReturnCategoryResponse(2L);
            System.out.println(response);
            assertNotNull(response);
            assertEquals(category.getId(), response.getId());
            assertEquals(category.getName(), response.getName());
            assertEquals(category.getCode(), response.getCode());
            assertEquals(category.getLevel(), response.getLevel());
        }

        @Test
        void findByIdReturnCategoryResponse_NonExistingCategory_ThrowsIdNotFoundException() {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(IdNotFoundException.class, () -> categoryService.findByIdReturnCategoryResponse(3L));
        }
    }

    @Nested
    class FindAllCategoryTests {
        @Test
        void findAllCategory_CategoriesExist_ReturnsPageOfCategoryResponses() {
            Page<Category> categoryPage = new PageImpl<>(Arrays.asList(category, parentCategory));
            when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);

            Page<CategoryResponse> result = categoryService.findAllCategory(pageable);

            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
        }

        @Test
        void findAllCategory_NoCategoriesExist_ReturnsEmptyPage() {
            Page<Category> emptyPage = Page.empty(pageable);
            when(categoryRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

            Page<CategoryResponse> result = categoryService.findAllCategory(pageable);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void update_ValidInput_ReturnsUpdatedCategoryResponse() {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            CategoryResponse result = categoryService.update(updateCategoryRequestDTO);

            assertNotNull(result);
            assertEquals(updateCategoryRequestDTO.getName(), result.getName());
            assertEquals(updateCategoryRequestDTO.getCode(), result.getCode());
        }

        @Test
        void update_NoChanges_ThrowsNoChangeException() {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

            UpdateCategoryRequestDTO noChangeDTO = new UpdateCategoryRequestDTO();
            noChangeDTO.setId(2L);
            noChangeDTO.setCode(category.getCode());
            noChangeDTO.setName(category.getName());
            noChangeDTO.setLevel(category.getLevel());

            assertThrows(NoChangeException.class, () -> categoryService.update(noChangeDTO));
        }
    }

    @Nested
    class DeleteTests {
        @Test
        void delete_ExistingCategory_DeletesSuccessfully() {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

            assertDoesNotThrow(() -> categoryService.delete(2L));

            verify(categoryRepository).deleteById(2L);
        }

        @Test
        void delete_NonExistingCategory_ThrowsIdNotFoundException() {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(IdNotFoundException.class, () -> categoryService.delete(3L));
        }
    }
}