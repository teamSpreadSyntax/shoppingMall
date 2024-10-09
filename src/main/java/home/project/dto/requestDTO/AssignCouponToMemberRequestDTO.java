package home.project.dto.requestDTO;

import home.project.domain.AssignType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

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
