//package home.project.dto.requestDTO;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//public class ProductVariantDTO {
//    private Long id;
//
//    @NotBlank(message = "상품의 사이즈를 입력해주세요.")
//    private String size;
//
//    @NotBlank(message = "상품의 색깔을 입력해주세요.")
//    private String color;
//
//    @NotNull(message = "상품의 현재 재고를 입력해주세요.")
//    private Long stock;
//
//    @NotNull(message = "상품의 불량 수량을 입력해주세요.")
//    private Long defectiveStock = 0L;
//
//    private Long soldQuantity = 0L;
//
//}
