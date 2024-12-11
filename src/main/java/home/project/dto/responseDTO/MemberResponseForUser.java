package home.project.dto.responseDTO;

import home.project.domain.member.MemberGenderType;
import home.project.domain.member.MemberGradeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Schema(description = "회원 응답 (사용자)")
public class MemberResponseForUser {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "회원 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "회원 이름", example = "홍길동")
    private String name;

    @Schema(description = "회원 전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "회원 성별", example = "MALE")
    private MemberGenderType gender;

    @Schema(description = "회원 생년월일", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "기본 주소", example = "서울특별시 강남구 테헤란로")
    private String defaultAddress;

    @Schema(description = "보조 주소 1", example = "서울특별시 서초구 서초대로")
    private String secondAddress;

    @Schema(description = "보조 주소 2", example = "서울특별시 송파구 올림픽로")
    private String thirdAddress;

    @Schema(description = "회원 등급", example = "GOLD")
    private MemberGradeType grade;

    @Schema(description = "회원 포인트", example = "1000")
    private Long point;

    @Schema(description = "회원 쿠폰 목록")
    private List<MemberCouponResponse> memberCouponResponse;

    public MemberResponseForUser(Long id, String email, String name, String phone, MemberGenderType gender,
                                 LocalDate birthDate, String defaultAddress, String secondAddress, String thirdAddress, MemberGradeType grade, Long point, List<MemberCouponResponse> memberCouponResponse) {
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
        this.memberCouponResponse = memberCouponResponse;
    }
}
