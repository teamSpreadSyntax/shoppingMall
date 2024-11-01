//package home.project.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import home.project.dto.CouponEventDTO;
//
//
//@RequiredArgsConstructor
//@Service
//public class EventListenerService {
//    private final ObjectMapper objectMapper;
//    private final ElkLogSenderService elkLogSenderService;
//    private final WebSocketNotificationService webSocketNotificationService;
//
//    @KafkaListener(topics = "product-view-log", groupId = "log-consumers")
//    public void listenProductViewLog(String message) {
//        processEvent("product-view-log", message);
//    }
//
//    @KafkaListener(topics = "purchase-activity-log", groupId = "log-consumers")
//    public void listenPurchaseLog(String message) {
//        processEvent("purchase-activity-log", message);
//    }
//
//    @KafkaListener(topics = "coupon-events", groupId = "coupon-group")
//    public void listenCouponEvent(String message) throws Exception {
//        CouponEventDTO event = objectMapper.readValue(message, CouponEventDTO.class);
//        processCouponEvent(event);
//    }
//
//    private void processEvent(String logType, String message) {
//        elkLogSenderService.sendToElk(logType, message);
//        webSocketNotificationService.sendWebSocketNotification("/topic/logs", message);
//    }
//
//    private void processCouponEvent(CouponEventDTO event) {
//        String logType = "coupon-events";
//        elkLogSenderService.sendToElk(logType, event.toString());
//        webSocketNotificationService.sendCouponNotification(event);
//    }
//}
