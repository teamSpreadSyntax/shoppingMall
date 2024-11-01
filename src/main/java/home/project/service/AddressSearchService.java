package home.project.service;

import home.project.dto.responseDTO.AddressSearchResponse;
import org.springframework.data.domain.Pageable;

public interface AddressSearchService {
    AddressSearchResponse searchAddress(String query, Pageable pageable);
    AddressSearchResponse searchByZipcode(String zipcode);
    AddressSearchResponse searchByRoadAddress(String roadAddress, Pageable pageable);
}
