//package home.project.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import home.project.domain.Coupon;
//import home.project.dto.kafkaDTO.CouponEventDTO;
//import home.project.domain.Member;
//import home.project.domain.Product;
//import home.project.repository.CouponRepository;
//import home.project.repository.MemberRepository;
//import home.project.repository.ProductRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class CouponEventListenerService {
//    private final ObjectMapper objectMapper;
//    private final CouponRepository couponRepository;
//    private final MemberRepository memberRepository;
//    private final ProductRepository productRepository;
//    private final SimpMessagingTemplate messagingTemplate;
//
//
//    @KafkaListener(topics = "coupon-events", groupId = "coupon-group")
//    public void listen(String message) throws JsonProcessingException {
//        CouponEventDTO event = objectMapper.readValue(message, CouponEventDTO.class);
//
//        switch (event.getEventType()) {
//            case "coupon_created":
//                handleCouponCreated(event);
//                break;
//            case "coupon_updated":
//                handleCouponUpdated(event);
//                break;
//            case "coupon_assigned_to_member":
//                handleCouponAssignedToMember(event);
//                break;
//            case "coupon_assigned_to_product":
//                handleCouponAssignedToProduct(event);
//                break;
//        }
//    }
//
//    private void handleCouponCreated(CouponEventDTO couponEventDTO) {
//        Coupon coupon = couponRepository.findById(couponEventDTO.getCouponId())
//                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + couponEventDTO.getCouponId()));
//        // 쿠폰 생성 이벤트 처리 로직
//    }
//
//    private void handleCouponUpdated(CouponEventDTO couponEventDTO) {
//        Coupon coupon = couponRepository.findById(couponEventDTO.getCouponId())
//                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + couponEventDTO.getCouponId()));
//        // 쿠폰 업데이트 이벤트 처리 로직
//    }
//
//    private void handleCouponAssignedToMember(CouponEventDTO couponEventDTO) {
//        Coupon coupon = couponRepository.findById(couponEventDTO.getCouponId())
//                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + couponEventDTO.getCouponId()));
//        Member member = memberRepository.findById(couponEventDTO.getMemberId())
//                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + couponEventDTO.getMemberId()));
//        // 회원에게 쿠폰 할당 처리 로직
//
//        sendWebSocketNotification(member.getEmail(), "New coupon assigned: " + coupon.getName());
//        // WebSocket 알림 전송
//
//    }
//
//    private void handleCouponAssignedToProduct(CouponEventDTO couponEventDTO) {
//        Coupon coupon = couponRepository.findById(couponEventDTO.getCouponId())
//                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + couponEventDTO.getCouponId()));
//        Product product = productRepository.findById(couponEventDTO.getProductId())
//                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + couponEventDTO.getProductId()));
//        // 상품에 쿠폰 할당 처리 로직
//
//        sendWebSocketNotification(product.getProductNum(), "New coupon assigned: " + coupon.getName());
//        // WebSocket 알림 전송
//
//    }
//
//    private void sendWebSocketNotification(String userEmail, String message) {
//        messagingTemplate.convertAndSendToUser(
//                userEmail,
//                "/queue/notifications",
//                message
//        );
//    }
//}
