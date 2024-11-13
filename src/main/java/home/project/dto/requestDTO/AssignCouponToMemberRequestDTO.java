package home.project.dto.requestDTO;

import home.project.domain.product.AssignType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignCouponToMemberRequestDTO {
    private Long couponId;
    private AssignType assignType;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String content;


//    private MemberCriteria criteria;




//    public static class MemberCriteria {
//        private LocalDate registrationDateAfter;
//        private LocalDate registrationDateBefore;
//
//    }
}
