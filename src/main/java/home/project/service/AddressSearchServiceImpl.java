package home.project.service;

import home.project.dto.responseDTO.AddressSearchResponse;
import home.project.exceptions.exception.AddressNotFoundException;
import home.project.exceptions.exception.AddressSearchException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AddressSearchServiceImpl implements AddressSearchService {
    private final WebClient webClient;

    public AddressSearchResponse searchAddress(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            throw new AddressSearchException("검색어를 입력해주세요.");
        }

        AddressSearchResponse response = callKakaoApi(query);
        validateSearchResult(response, query);
        return response;
    }

    public AddressSearchResponse searchByZipcode(String zipcode) {
        if (!zipcode.matches("\\d{5}")) {
            throw new AddressSearchException("올바른 우편번호 형식이 아닙니다. (5자리 숫자)");
        }

        AddressSearchResponse response = callKakaoApi(zipcode);
        validateSearchResult(response, "우편번호 " + zipcode);
        return response;
    }

    public AddressSearchResponse searchByRoadAddress(String roadAddress, Pageable pageable) {
        if (roadAddress == null || roadAddress.trim().isEmpty()) {
            throw new AddressSearchException("도로명 주소를 입력해주세요.");
        }

        AddressSearchResponse response = callKakaoApi(roadAddress);
        validateSearchResult(response, roadAddress);
        return response;
    }

    private AddressSearchResponse callKakaoApi(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/address.json")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .onStatus(status -> status.value() >= 400 && status.value() < 500, response -> {
                    if (response.statusCode().value() == 404) {
                        return Mono.error(new AddressNotFoundException("'" + query + "'에 대한 검색 결과가 없습니다."));
                    }
                    return Mono.error(new AddressSearchException("잘못된 요청입니다. 요청을 확인해 주세요."));
                })
                .onStatus(status -> status.value() >= 500, response ->
                        Mono.error(new AddressSearchException("카카오 API 서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")))
                .bodyToMono(AddressSearchResponse.class)
                .onErrorMap(ex -> new AddressSearchException("카카오 API 호출 중 오류가 발생했습니다: " + ex.getMessage()))
                .block();
    }

    private void validateSearchResult(AddressSearchResponse response, String query) {
        if (response == null || response.getDocuments() == null) {
            throw new AddressSearchException("주소 검색 결과를 가져오는데 실패했습니다.");
        }

        if (response.getDocuments().isEmpty()) {
            throw new AddressNotFoundException("'" + query + "'에 대한 검색 결과가 없습니다.");
        }
        System.out.println("Total count: " + (response.getMeta().getTotalCount() != null ? response.getMeta().getTotalCount() : 0));
        System.out.println("Documents: " + response.getDocuments().toString());
    }
}
