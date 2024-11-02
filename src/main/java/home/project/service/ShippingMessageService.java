package home.project.service;

import home.project.domain.ShippingMessage;
import home.project.dto.requestDTO.CreateShippingMessageRequestDTO;
import home.project.dto.responseDTO.ShippingMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ShippingMessageService {
    @Transactional
    ShippingMessageResponse createShippingMessage(CreateShippingMessageRequestDTO requestDTO);

    ShippingMessage findById(Long shippingId);

    Page<ShippingMessageResponse> findAll(Pageable pageable);

    @Transactional
    ShippingMessageResponse updateShippingMessage(Long id, String updatedContent);

    @Transactional
    void deleteShippingMessage(Long id);
}
