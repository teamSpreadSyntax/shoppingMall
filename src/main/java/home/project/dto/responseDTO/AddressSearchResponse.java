package home.project.dto.responseDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddressSearchResponse {
    private List<Document> documents;
    private Meta meta;

    @Getter
    @Setter
    public static class Document {
        private String addressName;        // 전체 주소
        private String roadAddressName;    // 도로명 주소
        private String jibunAddressName;   // 지번 주소
        private String postalCode;         // 우편번호
        private Double x;                  // 경도(longitude)
        private Double y;                  // 위도(latitude)
    }

    @Getter
    @Setter
    public static class Meta {
        private Integer totalCount;
        private Integer pageableCount;
        private Boolean isEnd;
    }
}