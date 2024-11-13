package home.project.dto.kafkaDTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import home.project.domain.member.MemberGenderType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberEventDTO {
    private String eventType;
    private MemberGenderType gender;
    private int age;

    @JsonCreator
    public MemberEventDTO(
            @JsonProperty("eventType") String eventType,
            @JsonProperty("gender") MemberGenderType gender,
            @JsonProperty("age") int age
    ) {
        this.eventType = eventType;
        this.gender = gender;
        this.age = age;
    }
}
