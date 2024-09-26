package home.project.controller;

import home.project.domain.Category;
import home.project.response.CustomResponseEntity;
import home.project.service.CategoryService;
import home.project.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller

public class CategoryController {
    private final PageUtil pageUtil;
    private final CategoryService categoryService;

    @Operation(summary = "전체 카테고리 조회 메서드", description = "전체 카테고리 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedCategoryListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema")))
    })
    @GetMapping("/categories")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> findAllCategory(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "category_code", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

        pageable = pageUtil.pageable(pageable);

        Page<Category> categoryPage = categoryService.findAllCategory(pageable);

        long totalCount = categoryPage.getTotalElements();

        int page = categoryPage.getNumber();

        String successMessage ="전체 카테고리입니다.";

        return new CustomResponseEntity<>(categoryPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }
}
