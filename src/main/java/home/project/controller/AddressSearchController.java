package home.project.controller;

import home.project.dto.responseDTO.AddressSearchResponse;
import home.project.response.CustomResponseEntity;
import home.project.service.AddressSearchService;
import home.project.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "주소 검색", description = "카카오 주소 검색 관련 API입니다")
@RequestMapping(path = "/api/address-search")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RequiredArgsConstructor
@RestController
public class AddressSearchController {

    private final AddressSearchService addressSearchService;
    private final PageUtil pageUtil;

    @Operation(summary = "주소 검색 메서드", description = "키워드로 주소를 검색하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/AddressSearchResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/search")
    public CustomResponseEntity<?> searchAddress(
            @RequestParam String query,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "address", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

        pageable = pageUtil.pageable(pageable);
        AddressSearchResponse searchResult = addressSearchService.searchAddress(query, pageable);

        String successMessage = "'" + query + "' 검색 결과입니다.";
        long totalCount = searchResult.getMeta().getTotalCount();
        int page = pageable.getPageNumber();

        return new CustomResponseEntity<>(searchResult.getDocuments(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "우편번호로 주소 검색 메서드", description = "우편번호로 주소를 검색하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/zipcode")
    public CustomResponseEntity<?> searchByZipcode(@RequestParam String zipcode) {
        AddressSearchResponse searchResult = addressSearchService.searchByZipcode(zipcode);

        String successMessage = "우편번호 '" + zipcode + "' 검색 결과입니다.";
        long totalCount = searchResult.getMeta().getTotalCount();

        return new CustomResponseEntity<>(searchResult.getDocuments(), successMessage, HttpStatus.OK, totalCount, 0);
    }

    @Operation(summary = "도로명주소 검색 메서드", description = "도로명주소로 주소를 검색하는 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/road-address")
    public CustomResponseEntity<?> searchByRoadAddress(
            @RequestParam String roadAddress,
            @PageableDefault(page = 1, size = 5) @ParameterObject Pageable pageable) {

        pageable = pageUtil.pageable(pageable);
        AddressSearchResponse searchResult = addressSearchService.searchByRoadAddress(roadAddress, pageable);

        String successMessage = "도로명주소 '" + roadAddress + "' 검색 결과입니다.";
        long totalCount = searchResult.getMeta().getTotalCount();
        int page = pageable.getPageNumber();

        return new CustomResponseEntity<>(searchResult.getDocuments(), successMessage, HttpStatus.OK, totalCount, page);
    }
}