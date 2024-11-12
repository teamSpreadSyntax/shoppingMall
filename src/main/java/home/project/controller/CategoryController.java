package home.project.controller;

import home.project.dto.requestDTO.CreateCategoryRequestDTO;
import home.project.dto.requestDTO.UpdateCategoryRequestDTO;
import home.project.dto.responseDTO.CategoryResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.CategoryService;
import home.project.util.PageUtil;
import home.project.util.ValidationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "카테고리", description = "카테고리 관련 API입니다")
@RequestMapping(path = "/api/category")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController

public class CategoryController {
    private final PageUtil pageUtil;
    private final CategoryService categoryService;

    @Operation(summary = "id로 카테고리 조회 메서드", description = "id로 카테고리 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ProductResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/category")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findCategoryById(@RequestParam("categoryId") Long categoryId) {
        CategoryResponse categoryResponse = categoryService.findByIdReturnCategoryResponse(categoryId);
        String successMessage = categoryId + "에 해당하는 카테고리 입니다.";
        return new CustomResponseEntity<>(categoryResponse, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 카테고리 조회 메서드", description = "전체 카테고리 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedCategoryListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/categories")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAllCategory(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "category_code", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

        pageable = pageUtil.pageable(pageable);

        Page<CategoryResponse> categoryPage = categoryService.findAllCategory(pageable);

        long totalCount = categoryPage.getTotalElements();

        int page = categoryPage.getNumber();

        String successMessage = "전체 카테고리입니다.";

        return new CustomResponseEntity<>(categoryPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }


}
