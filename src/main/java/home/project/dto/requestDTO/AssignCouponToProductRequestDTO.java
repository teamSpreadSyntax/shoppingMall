package home.project.dto.requestDTO;

import home.project.domain.AssignType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class AssignCouponToProductRequestDTO {
    private Long couponId;
    private AssignType assignType;
    private String brand;
    private String category;
    private String productName;
    private String content;


//    private MemberCriteria criteria;




//    public static class MemberCriteria {
//        private LocalDate registrationDateAfter;
//        private LocalDate registrationDateBefore;
//
//    }
}
