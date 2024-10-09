package home.project.util;

import home.project.domain.AssignType;
import org.springframework.data.domain.Page;

import java.util.List;

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

    public static String buildMemberCouponSearchCriteria( Long couponId, String name, String email, String phone, String role,
                                                          String content, Page<?> page) {
        StringBuilder searchCriteria = new StringBuilder();
        if (name != null) searchCriteria.append(name).append(", ");
        if (email != null) searchCriteria.append(email).append(", ");
        if (phone != null) searchCriteria.append(phone).append(", ");
        if (role != null) searchCriteria.append(role).append(", ");
        if (content != null) searchCriteria.append(content).append(", ");
        if (couponId != null) searchCriteria.append(couponId);

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
            searchCriteria.setLength(searchCriteria.length() - 2);
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

}
