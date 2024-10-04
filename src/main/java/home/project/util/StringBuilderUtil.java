package home.project.util;

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

}
