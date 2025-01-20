package home.project.service.product;

import home.project.domain.product.Category;
import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.dto.requestDTO.UpdateCategoryRequestDTO;
import home.project.dto.responseDTO.CategoryResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.exceptions.exception.NoChangeException;
import home.project.repository.product.CategoryRepository;
import home.project.repository.product.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category parentCategory;
    private Category testCategory;
    private CreateCategoryRequestDTO createCategoryRequestDTO;
    private UpdateCategoryRequestDTO updateCategoryRequestDTO;

    @BeforeEach
    void setUp() {
        // 상위 카테고리 생성
        parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setCode("00");
        parentCategory.setName("ParentCategory");
        parentCategory.setLevel(1);

        // 테스트용 카테고리 생성
        testCategory = new Category();
        testCategory.setId(2L);
        testCategory.setCode("0001");
        testCategory.setName("TestCategory");
        testCategory.setLevel(2);
        testCategory.setParent(parentCategory);

        // 카테고리 생성 요청 DTO
        createCategoryRequestDTO = new CreateCategoryRequestDTO();
        createCategoryRequestDTO.setCode("0001");
        createCategoryRequestDTO.setName("TestCategory");
        createCategoryRequestDTO.setLevel(2);

        // 카테고리 업데이트 요청 DTO
        updateCategoryRequestDTO = new UpdateCategoryRequestDTO();
        updateCategoryRequestDTO.setId(2L);
        updateCategoryRequestDTO.setCode("0002");
        updateCategoryRequestDTO.setName("UpdatedCategory");
        updateCategoryRequestDTO.setLevel(2);
    }

    @Nested
    @DisplayName("카테고리 생성 테스트")
    class JoinTest {

        @Test
        @DisplayName("정상적으로 카테고리를 생성한다")
        void joinSuccess() {
            // given
            when(categoryRepository.existsByCode(anyString())).thenReturn(false);
            when(categoryRepository.existsByName(anyString())).thenReturn(false);
            when(categoryRepository.findByCode("00")).thenReturn(Optional.of(parentCategory));

            // when
            categoryService.join(createCategoryRequestDTO);

            // then
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("상위 카테고리가 없는 경우 실패한다")
        void joinFailNoParentCategory() {
            // given
            when(categoryRepository.existsByCode(anyString())).thenReturn(false);
            when(categoryRepository.existsByName(anyString())).thenReturn(false);
            when(categoryRepository.findByCode("00")).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.join(createCategoryRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("상위 카테고리 코드를 찾을 수 없습니다. 상위 카테고리를 먼저 생성하고 다시 시도해주세요.");

            verify(categoryRepository, never()).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("카테고리 조회 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 카테고리를 조회한다")
        void findByIdSuccess() {
            // given
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

            // when
            CategoryResponse response = categoryService.findByIdReturnCategoryResponse(2L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(2L);
            assertThat(response.getCode()).isEqualTo("0001");
            verify(categoryRepository).findById(anyLong());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 카테고리를 조회할 수 없다")
        void findByIdFailNotFound() {
            // given
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.findByIdReturnCategoryResponse(2L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("2(으)로 등록된 카테고리가 없습니다.");
        }
    }

    @Nested
    @DisplayName("카테고리 업데이트 테스트")
    class UpdateTest {

        @Test
        @DisplayName("카테고리를 업데이트한다")
        void updateSuccess() {
            // given
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
            when(categoryRepository.existsByCode(anyString())).thenReturn(false);
            when(categoryRepository.findByCode("00")).thenReturn(Optional.of(parentCategory));

            // when
            CategoryResponse response = categoryService.update(updateCategoryRequestDTO);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getCode()).isEqualTo("0002");
            verify(categoryRepository).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 테스트")
    class DeleteTest {

        @Test
        @DisplayName("카테고리를 삭제한다")
        void deleteSuccess() {
            // given
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

            // when
            categoryService.delete(2L);

            // then
            verify(categoryRepository).deleteById(anyLong());
        }
    }
}
