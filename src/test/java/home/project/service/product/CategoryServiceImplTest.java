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
        parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setCode("00");
        parentCategory.setName("ParentCategory");
        parentCategory.setLevel(1);

        testCategory = new Category();
        testCategory.setId(2L);
        testCategory.setCode("0001");
        testCategory.setName("TestCategory");
        testCategory.setLevel(2);
        testCategory.setParent(parentCategory);

        createCategoryRequestDTO = new CreateCategoryRequestDTO();
        createCategoryRequestDTO.setCode("0001");
        createCategoryRequestDTO.setName("TestCategory");
        createCategoryRequestDTO.setLevel(2);

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
            when(categoryRepository.existsByCode(anyString())).thenReturn(false);
            when(categoryRepository.existsByName(anyString())).thenReturn(false);
            when(categoryRepository.findByCode("00")).thenReturn(Optional.of(parentCategory));

            categoryService.join(createCategoryRequestDTO);

            verify(categoryRepository).save(argThat(category -> {
                return category.getCode().equals(createCategoryRequestDTO.getCode())
                        && category.getName().equals(createCategoryRequestDTO.getName())
                        && category.getLevel() == createCategoryRequestDTO.getLevel()
                        && category.getParent() != null
                        && category.getParent().getId().equals(parentCategory.getId());
            }));
            verify(categoryRepository, times(2)).findByCode("00"); // findByCode가 2번 호출되어야 함
            verify(categoryRepository).existsByCode(anyString());
            verify(categoryRepository).existsByName(anyString());
        }

        @Test
        @DisplayName("중복된 카테고리 코드로 생성 시 실패한다")
        void joinFailDuplicateCode() {
            when(categoryRepository.existsByCode(anyString())).thenReturn(true);
            when(categoryRepository.existsByName(anyString())).thenReturn(false);

            assertThatThrownBy(() -> categoryService.join(createCategoryRequestDTO))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessage("이미 존재하는 카테고리 코드입니다.");
        }

        @Test
        @DisplayName("중복된 카테고리 이름으로 생성 시 실패한다")
        void joinFailDuplicateName() {
            when(categoryRepository.existsByCode(anyString())).thenReturn(false);
            when(categoryRepository.existsByName(anyString())).thenReturn(true);

            assertThatThrownBy(() -> categoryService.join(createCategoryRequestDTO))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessage("이미 존재하는 카테고리 이름입니다.");
        }

        @Test
        @DisplayName("잘못된 레벨의 카테고리 코드로 생성 시 실패한다")
        void joinFailInvalidCodeLength() {
            createCategoryRequestDTO.setCode("001");  // 레벨2는 4자리여야 함

            assertThatThrownBy(() -> categoryService.join(createCategoryRequestDTO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("카테고리 코드의 길이는 레벨 2에 대해 4여야 합니다.");
        }
    }

    @Nested
    @DisplayName("카테고리 조회 테스트")
    class FindTest {
        @Test
        @DisplayName("모든 카테고리를 페이징하여 조회한다")
        void findAllSuccess() {
            Page<Category> categoryPage = new PageImpl<>(List.of(testCategory));
            when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(categoryPage);

            Page<CategoryResponse> result = categoryService.findAllCategory(PageRequest.of(0, 10));

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getCode()).isEqualTo("0001");
            verify(categoryRepository).findAll(any(PageRequest.class));
        }

        @Test
        @DisplayName("ID가 null인 경우 조회에 실패한다")
        void findByIdFailWithNullId() {
            assertThatThrownBy(() -> categoryService.findByIdReturnCategoryResponse(null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("id가 입력되지 않았습니다.");
        }
    }

    @Nested
    @DisplayName("카테고리 수정 테스트")
    class UpdateTest {
        @Test
        @DisplayName("변경사항이 없는 경우 업데이트에 실패한다")
        void updateFailNoChange() {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
            when(categoryRepository.findByCode(anyString())).thenReturn(Optional.of(parentCategory));  // 상위 카테고리 mock

            updateCategoryRequestDTO.setCode(testCategory.getCode());
            updateCategoryRequestDTO.setName(testCategory.getName());
            updateCategoryRequestDTO.setLevel(testCategory.getLevel());

            assertThatThrownBy(() -> categoryService.update(updateCategoryRequestDTO))
                    .isInstanceOf(NoChangeException.class)
                    .hasMessage("변경된 카테고리 정보가 없습니다.");

            verify(categoryRepository).findById(anyLong());
            verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        @DisplayName("중복된 이름으로 수정 시 실패한다")
        void updateFailDuplicateName() {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
            when(categoryRepository.existsByName(anyString())).thenReturn(true);
            when(categoryRepository.findByCode("00")).thenReturn(Optional.of(parentCategory)); // 상위 카테고리 mock 추가

            // when & then
            assertThatThrownBy(() -> categoryService.update(updateCategoryRequestDTO))
                    .isInstanceOf(DataIntegrityViolationException.class)
                    .hasMessage("이미 사용 중인 카테고리명 입니다.");
        }

        @Test
        @DisplayName("잘못된 레벨의 카테고리 코드로 수정 시 실패한다")
        void updateFailInvalidCodeLength() {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

            updateCategoryRequestDTO.setLevel(3);
            updateCategoryRequestDTO.setCode("0001");

            assertThatThrownBy(() -> categoryService.update(updateCategoryRequestDTO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("카테고리 코드의 길이는 레벨 3에 대해 6여야 합니다.");
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 테스트")
    class DeleteTest {
        @Test
        @DisplayName("존재하지 않는 카테고리 삭제 시 실패한다")
        void deleteFailNotFound() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.delete(99L))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("99(으)로 등록된 카테고리가 없습니다.");
        }
    }
}
