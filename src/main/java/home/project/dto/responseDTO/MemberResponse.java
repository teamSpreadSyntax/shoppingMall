package home.project.dto.responseDTO;

import home.project.domain.RoleType;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 정보를 클라이언트에게 반환하기 위한 응답 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 서버에서 클라이언트로 회원의 기본 정보를 전달하는 데 사용됩니다.
 */
@Getter
@Setter
public class MemberResponse {

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

    /**
     * 회원의 권한 유형입니다.
     */
    private RoleType role;

    /**
     * MemberResponseDTO의 모든 필드를 초기화하는 생성자입니다.
     *
     * @param id    회원의 고유 식별자
     * @param email 회원의 이메일 주소
     * @param name  회원의 이름
     * @param phone 회원의 전화번호
     * @param role  회원의 권한 유형
     */
    public MemberResponse(Long id, String email, String name, String phone, RoleType role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }

    // Lombok @Getter와 @Setter 어노테이션으로 인해 getter와 setter 메서드가 자동으로 생성됩니다.
}