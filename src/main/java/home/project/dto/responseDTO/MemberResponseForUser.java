package home.project.dto.responseDTO;

import home.project.domain.MemberCoupon;
import home.project.domain.MemberGender;
import home.project.domain.MemberGrade;
import home.project.domain.RoleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * 회원 정보를 클라이언트에게 반환하기 위한 응답 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 서버에서 클라이언트로 회원의 기본 정보를 전달하는 데 사용됩니다.
 */
@Getter
@Setter
public class MemberResponseForUser {

    /**
     * 회원의 고유 식별자입니다.
     */
    private Long id;

    /**
     * 회원의 이메일 주소입니다.
     */
    private String email;

    /**
     * 회원의 이름입니다.
     */
    private String name;

    /**
     * 회원의 전화번호입니다.
     */
    private String phone;

    private MemberGender gender;

    private LocalDate birthDate;

    private String defaultAddress;

    private String secondAddress;

    private String thirdAddress;

    private MemberGrade grade;

    private Long point;

    private List<MemberCoupon> memberCoupons;

    /**
     * MemberResponseDTO의 모든 필드를 초기화하는 생성자입니다.
     *
     * @param id    회원의 고유 식별자
     * @param email 회원의 이메일 주소
     * @param name  회원의 이름
     * @param phone 회원의 전화번호
     */
    public MemberResponseForUser(Long id, String email, String name, String phone, MemberGender gender,
                                 LocalDate birthDate, String defaultAddress, String secondAddress, String thirdAddress, MemberGrade grade, Long point, List<MemberCoupon> memberCoupons) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.birthDate = birthDate;
        this.defaultAddress = defaultAddress;
        this.secondAddress = secondAddress;
        this.thirdAddress = thirdAddress;
        this.grade = grade;
        this.point = point;
        this.memberCoupons = memberCoupons;
    }

    // Lombok @Getter와 @Setter 어노테이션으로 인해 getter와 setter 메서드가 자동으로 생성됩니다.
}