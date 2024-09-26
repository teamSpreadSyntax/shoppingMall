package home.project.dto.responseDTO;

import home.project.domain.RoleType;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 권한 정보를 클라이언트에게 반환하기 위한 응답 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 서버에서 클라이언트로 사용자의 권한 정보를 전달하는 데 사용됩니다.
 */
@Getter
@Setter
public class RoleResponseDTO {

    /**
     * 역할의 고유 식별자입니다.
     */
    private Long id;

    /**
     * 사용자의 권한 유형입니다.
     */
    private RoleType role;

    /**
     * 역할과 연관된 사용자의 이름입니다.
     */
    private String name;

    /**
     * RoleResponseDTO의 모든 필드를 초기화하는 생성자입니다.
     *
     * @param id   역할의 고유 식별자
     * @param role 사용자의 권한 유형
     * @param name 역할과 연관된 사용자의 이름
     */
    public RoleResponseDTO(Long id, RoleType role, String name) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

}