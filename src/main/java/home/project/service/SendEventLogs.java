//package home.project.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import home.project.dto.kafkaDTO.CouponEventDTO;
//import home.project.dto.kafkaDTO.MemberEventDTO;
//import home.project.dto.responseDTO.MemberResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@RequiredArgsConstructor
//@Service
//@Transactional(readOnly = true)
//public class SendEventLogs {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//    private final ObjectMapper objectMapper;
//
//    public void sendCouponEvent(CouponEventDTO event) {
//        try {
//            String message = objectMapper.writeValueAsString(event);
//            kafkaTemplate.send("coupon-events", message);
//        } catch (JsonProcessingException e) {
//            // 에러 처리
//            e.printStackTrace();
//        }
//    }
//
//    public void sendMemberJoinEvent(MemberEventDTO memberEventDTO) {
//        try {
//            String message = objectMapper.writeValueAsString(memberEventDTO);
//            kafkaTemplate.send("member-join-events", message);
//        } catch (JsonProcessingException e) {
//            // 에러 처리
//            e.printStackTrace();
//        }
//    }
//}
