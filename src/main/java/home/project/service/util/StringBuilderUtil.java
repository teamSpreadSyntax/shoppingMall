package home.project.service.util;

import home.project.domain.product.AssignType;
import home.project.dto.requestDTO.AssignCouponToMemberRequestDTO;
import home.project.dto.requestDTO.AssignCouponToProductRequestDTO;
import org.springframework.data.domain.Page;

public class StringBuilderUtil {
    public static String buildMemberSearchCriteria(String name, String email, String phone, String role, String content, Page<?> page) {
        StringBuilder searchCriteria = new StringBuilder();
        if (name != null) searchCriteria.append(name).append(", ");
        if (email != null) searchCriteria.append(email).append(", ");
        if (phone != null) searchCriteria.append(phone).append(", ");
        if (role != null) searchCriteria.append(role).append(", ");
        if (content != null) searchCriteria.append(content).append(", ");

        return finalizeSearchCriteria(searchCriteria, page, "전체 회원입니다.");
    }

    public static String buildProductSearchCriteria(String brand, String category, String productName, String content, Page<?> page) {
        StringBuilder searchCriteria = new StringBuilder();
        if (brand != null) searchCriteria.append(brand).append(", ");
        if (category != null) searchCriteria.append(category).append(", ");
        if (productName != null) searchCriteria.append(productName).append(", ");
        if (content != null) searchCriteria.append(content).append(", ");

        return finalizeSearchCriteria(searchCriteria, page, "전체 상품입니다.");
    }

    public static String buildCouponSearchCriteria(String name, String startDate, String endDate, String assignBy, String content, Page<?> page) {
        StringBuilder searchCriteria = new StringBuilder();
        if (name != null) searchCriteria.append(name).append(", ");
        if (startDate != null) searchCriteria.append(startDate).append(", ");
        if (endDate != null) searchCriteria.append(endDate).append(", ");
        if (assignBy != null) searchCriteria.append(assignBy).append(", ");
        if (content != null) searchCriteria.append(content).append(", ");

        return finalizeSearchCriteria(searchCriteria, page, "전체 쿠폰입니다.");
    }

    public static String buildEventSearchCriteria(String name, String startDate, String endDate, Integer discountRate, String content, Page<?> page) {
        StringBuilder searchCriteria = new StringBuilder();
        if (name != null) searchCriteria.append(name).append(", ");
        if (startDate != null) searchCriteria.append(startDate).append(", ");
        if (endDate != null) searchCriteria.append(endDate).append(", ");
        if (discountRate != null) searchCriteria.append(discountRate).append(", ");
        if (content != null) searchCriteria.append(content).append(", ");

        return finalizeSearchCriteria(searchCriteria, page, "전체 이벤트입니다.");
    }

    public static String buildOrderSearchCriteria(String orderNum, String orderDate, String productNumber, String email, String content, Page<?> page) {
        StringBuilder searchCriteria = new StringBuilder();
        if (orderNum != null) searchCriteria.append(orderNum).append(", ");
        if (orderDate != null) searchCriteria.append(orderDate).append(", ");
        if (productNumber != null) searchCriteria.append(productNumber).append(", ");
        if (email != null) searchCriteria.append(email).append(", ");
        if (content != null) searchCriteria.append(content).append(", ");

        return finalizeSearchCriteria(searchCriteria, page, "전체 주문입니다.");
    }

    public static String buildShippingSearchCriteria(String deliveryNum, String orderDate, String orderNum, String email, String content, Page<?> page) {
        StringBuilder searchCriteria = new StringBuilder();
        if (deliveryNum != null) searchCriteria.append(deliveryNum).append(", ");
        if (orderDate != null) searchCriteria.append(orderDate).append(", ");
        if (orderNum != null) searchCriteria.append(orderNum).append(", ");
        if (email != null) searchCriteria.append(email).append(", ");
        if (content != null) searchCriteria.append(content).append(", ");

        return finalizeSearchCriteria(searchCriteria, page, "전체 배송정보입니다.");
    }

    public static String buildMemberCouponSearchCriteria(Long couponId, String name, String email, String phone, String role, String content, Page<?> page) {
        StringBuilder searchCriteria = new StringBuilder();
        if (name != null && !name.trim().isEmpty()) searchCriteria.append(name.trim()).append(", ");
        if (email != null && !email.trim().isEmpty()) searchCriteria.append(email.trim()).append(", ");
        if (phone != null && !phone.trim().isEmpty()) searchCriteria.append(phone.trim()).append(", ");
        if (role != null && !role.trim().isEmpty()) searchCriteria.append(role.trim()).append(", ");
        if (content != null && !content.trim().isEmpty()) searchCriteria.append(content.trim()).append(", ");

        if (searchCriteria.length() > 2) {
            searchCriteria.setLength(searchCriteria.length() - 2);
        }

        return finalizeSearchCriteriaForMemberCoupon(searchCriteria, couponId, page, "전체 회원입니다.");
    }

    public static String buildProductCouponSearchCriteria( Long couponId, String brand, String category, String productName,
                                                            String content, Page<?> page) {
        StringBuilder searchCriteria = new StringBuilder();
        if (brand != null && !brand.trim().isEmpty()) searchCriteria.append(brand.trim()).append(", ");
        if (category != null && !category.trim().isEmpty()) searchCriteria.append(category.trim()).append(", ");
        if (productName != null && !productName.trim().isEmpty()) searchCriteria.append(productName.trim()).append(", ");
        if (content != null && !content.trim().isEmpty()) searchCriteria.append(content.trim()).append(", ");

        if (searchCriteria.length() > 2) {
            searchCriteria.setLength(searchCriteria.length() - 2);
        }

        return finalizeSearchCriteriaForProductCoupon(searchCriteria, couponId, page, "전체 상품입니다.");
    }

    private static String finalizeSearchCriteria(StringBuilder searchCriteria, Page<?> page, String defaultMessage) {
        String successMessage;
        if (searchCriteria.length() > 0) {
            searchCriteria.setLength(searchCriteria.length() - 2);
            successMessage = "검색 키워드 : " + searchCriteria;
        } else {
            successMessage = defaultMessage;
        }

        long totalCount = page.getTotalElements();
        if (totalCount == 0) {
            successMessage = "검색 결과가 없습니다. 검색 키워드 : " + searchCriteria;
        }

        return successMessage;
    }

    private static String finalizeSearchCriteriaForMemberCoupon(StringBuilder searchCriteria, Long couponId, Page<?> page, String defaultMessage) {
        String successMessage;
        if (searchCriteria.length() > 0) {
            successMessage = "쿠폰"+couponId+"을 부여받은 회원 조건 : " + searchCriteria;
        } else {
            successMessage = defaultMessage;
        }

        long totalCount = page.getTotalElements();
        if (totalCount == 0) {
            successMessage = "검색 결과가 없습니다. 쿠폰을 부여를 시도한 회원 조건 : " + searchCriteria;
        }

        return successMessage;
    }

    private static String finalizeSearchCriteriaForProductCoupon(StringBuilder searchCriteria, Long couponId, Page<?> page, String defaultMessage) {
        String successMessage;
        if (searchCriteria.length() > 0) {
            successMessage = "쿠폰"+couponId+"을 부여받은 상품 조건 : " + searchCriteria;
        } else {
            successMessage = defaultMessage;
        }

        long totalCount = page.getTotalElements();
        if (totalCount == 0) {
            successMessage = "검색 결과가 없습니다. 쿠폰을 부여를 시도한 상품 조건 : " + searchCriteria;
        }

        return successMessage;
    }

    public static String buildAssignCondition(AssignCouponToMemberRequestDTO dto) {
        StringBuilder condition = new StringBuilder();
        if (dto.getAssignType() == AssignType.SPECIFIC_MEMBERS) {
            if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
                condition.append("이름: ").append(dto.getName().trim()).append(", ");
            }
            if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
                condition.append("이메일: ").append(dto.getEmail().trim()).append(", ");
            }
            if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
                condition.append("전화번호: ").append(dto.getPhone().trim()).append(", ");
            }
            if (dto.getRole() != null && !dto.getRole().trim().isEmpty()) {
                condition.append("역할: ").append(dto.getRole().trim()).append(", ");
            }
            if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                condition.append("내용: ").append(dto.getContent().trim()).append(", ");
            }
            if (condition.length() > 2) {
                condition.setLength(condition.length() - 2);
            }
        } else {
            condition.append("전체 회원");
        }
        return condition.toString();
    }
    public static String buildAssignCondition(AssignCouponToProductRequestDTO dto) {
        StringBuilder condition = new StringBuilder();
        if (dto.getAssignType() == AssignType.SPECIFIC_PRODUCTS) {
            if (dto.getBrand() != null && !dto.getBrand().trim().isEmpty()) {
                condition.append("브랜드: ").append(dto.getBrand().trim()).append(", ");
            }
            if (dto.getCategory() != null && !dto.getCategory().trim().isEmpty()) {
                condition.append("카테고리: ").append(dto.getCategory().trim()).append(", ");
            }
            if (dto.getProductName() != null && !dto.getProductName().trim().isEmpty()) {
                condition.append("상품명: ").append(dto.getProductName().trim()).append(", ");
            }
            if (dto.getContent() != null && !dto.getContent().trim().isEmpty()) {
                condition.append("내용: ").append(dto.getContent().trim()).append(", ");
            }
            if (condition.length() > 2) {
                condition.setLength(condition.length() - 2);
            }
        } else {
            condition.append("전체 상품");
        }
        return condition.toString();
    }
}
