package home.project.service;

import home.project.domain.Member;
import home.project.domain.ShippingMessage;
import home.project.dto.requestDTO.CreateShippingMessageRequestDTO;
import home.project.dto.responseDTO.ShippingMessageResponse;
import home.project.exceptions.exception.IdNotFoundException;
import home.project.repository.ShippingMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ShippingMessageServiceImpl implements ShippingMessageService {
    private final MemberService memberService;
    private final ShippingMessageRepository shippingMessageRepository;
    private final Converter converter;

    @Transactional
    @Override
    public ShippingMessageResponse createShippingMessage(CreateShippingMessageRequestDTO requestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberService.findByEmail(email);

        ShippingMessage message = new ShippingMessage();
        message.setContent(requestDTO.getMessages().toString());
        message.setCreatedAt(LocalDateTime.now());
        message.setMember(member);

        shippingMessageRepository.save(message);
        return converter.convertFromShippingMessageToShippingMessageResponse(message);
    }

    @Override
    public ShippingMessage findById(Long shippingId) {
        return shippingMessageRepository.findById(shippingId)
                .orElseThrow(() -> new IdNotFoundException(shippingId + "(으)로 등록된 배송 메시지가 없습니다."));
    }

    @Override
    public Page<ShippingMessageResponse> findAll(Pageable pageable){
        Page<ShippingMessage> pagedShippingMessage = shippingMessageRepository.findAll(pageable);
        return converter.convertFromPagedShippingMessageToPagedShippingMessageResponse (pagedShippingMessage);
    }

    @Override
    @Transactional
    public ShippingMessageResponse updateShippingMessage(Long id, String updatedContent) {
        ShippingMessage message = shippingMessageRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException(id + "번 배송 메시지가 없습니다."));

        message.setContent(updatedContent);
        message.setCreatedAt(LocalDateTime.now());

        return converter.convertFromShippingMessageToShippingMessageResponse(message);
    }

    @Override
    @Transactional
    public void deleteShippingMessage(Long id) {
        if (!shippingMessageRepository.existsById(id)) {
            throw new IdNotFoundException(id + "번 배송 메시지가 없습니다.");
        }
        shippingMessageRepository.deleteById(id);
    }
}
