package home.project.dto.requestDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 새로운 카테고리를 생성하기 위한 요청 데이터 전송 객체(DTO)입니다.
 * 이 클래스는 클라이언트로부터 카테고리 생성에 필요한 정보를 받아 서버로 전달합니다.
 * 모든 필드는 유효성 검사 어노테이션이 적용되어 있습니다.
 */
@Getter
@Setter
public class CreateCategoryRequestDTO {

    /**
     * 생성할 카테고리의 코드입니다.
     * 이 필드는 null이 될 수 없습니다.
     * 유효성 검사: @NotNull
     */
    @NotNull(message = "코드를 입력해주세요.")
    private String code;

    /**
     * 생성할 카테고리의 이름입니다.
     * 이 필드는 비어있거나 null이 될 수 없습니다.
     * 유효성 검사: @NotEmpty
     */
    @NotEmpty(message = "이름을 입력해주세요.")
    private String name;

    /**
     * 생성할 카테고리의 계층 레벨입니다.
     * 이 필드는 null이 될 수 없습니다.
     * 유효성 검사: @NotNull
     */
    @NotNull(message = "레벨을 입력해주세요.")
    private Integer level;

    // Lombok @Getter와 @Setter 어노테이션으로 인해 getter와 setter 메서드가 자동으로 생성됩니다.
}