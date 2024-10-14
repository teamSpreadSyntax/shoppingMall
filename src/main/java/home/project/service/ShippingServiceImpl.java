package home.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.project.domain.*;
import home.project.dto.requestDTO.CreateOrderRequestDTO;
import home.project.dto.requestDTO.CreateShippingRequestDTO;
import home.project.dto.responseDTO.OrderResponse;
import home.project.dto.responseDTO.ShippingResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.MemberRepository;
import home.project.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ShippingServiceImpl implements ShippingService{

    private final ShippingRepository shippingRepository;
    private final Converter converter;

    @Override
    @Transactional
    public ShippingResponse update(Long id, DeliveryStatusType deliveryStatusType){

        Shipping shipping = findById(id);
        shipping.setDeliveryStatus(deliveryStatusType);
        shippingRepository.save(shipping);
        return converter.convertShippingFromShippingResponse(shipping);
    }

//    @Override
    public Shipping findById(Long shippingId){
        return shippingRepository.findById(shippingId)
                .orElseThrow(() -> new IdNotFoundException(shippingId + "(으)로 등록된 배송정보가 없습니다."));
    }
}
