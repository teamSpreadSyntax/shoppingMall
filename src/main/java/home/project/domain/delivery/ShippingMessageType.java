package home.project.domain.delivery;

import lombok.Getter;

@Getter
public enum ShippingMessageType {
    LEAVE_AT_DOOR("문 앞에 놓아주세요"),
    DIRECT_HANDOFF("직접 전달해주세요"),
    LEAVE_WITH_CONCIERGE("경비실에 맡겨주세요"),
    CONTACT_BEFORE_DELIVERY("배달 전에 연락 주세요"),
    CUSTOM("");

    private final String defaultMessage;

    ShippingMessageType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
