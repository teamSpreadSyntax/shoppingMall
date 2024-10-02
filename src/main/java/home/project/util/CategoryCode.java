/*
package home.project.util;

public class CategoryCode {
    public static String generateCategoryCode(String category, String content) {
        if (category == null || category.isEmpty()) {
            return handleAllCategories(content);
        }
        category = category.toLowerCase();
        switch (category) {
            case "shirts":
            case "셔츠":
            case "티셔츠":
            case "티":
                return SearchUtil.handleShirts(content);
            case "knit":
            case "니트":
                return SearchUtil.handleKnit(content);
            case "pants":
            case "바지":
                return SearchUtil.handlePants(content);
            case "gloves":
            case "장갑":
                return SearchUtil.handleGloves(content);
            case "hat":
            case "모자":
                return SearchUtil.handleHat(content);
            case "hoodie":
            case "후드":
                return SearchUtil.handleHoodie(content);
            case "jacket":
            case "재킷":
                return SearchUtil.handleJacket(content);
            case "shoes":
            case "신발":
                return SearchUtil.handleShoes(content);
            case "shorts":
            case "반바지":
                return SearchUtil.handleShorts(content);
            case "socks":
            case "양말":
                return SearchUtil.handleSocks(content);
            case "sweater":
            case "스웨터":
                return SearchUtil.handleSweater(content);
            default:
                return handleAllCategories(content);
        }
    }

    private static String handleAllCategories(String content) {
        if (content == null || content.isEmpty()) {
            return null; // 기본값 반환
        }
        content = content.toLowerCase();
        if (content.contains("셔츠") || content.contains("t-shirt") ||
                content.contains("블라우스") || content.contains("와이셔츠") ||
                content.contains("blouse") || content.contains("dress shirt")) {
            return SearchUtil.handleShirts(content);
        } else if (content.contains("니트") || content.contains("knit")) {
            return SearchUtil.handleKnit(content);
        } else if (content.contains("바지") || content.contains("pants")) {
            return SearchUtil.handlePants(content);
        } else if (content.contains("장갑") || content.contains("gloves")) {
            return SearchUtil.handleGloves(content);
        } else if (content.contains("모자") || content.contains("hat")) {
            return SearchUtil.handleHat(content);
        } else if (content.contains("후드") || content.contains("hoodie")) {
            return SearchUtil.handleHoodie(content);
        } else if (content.contains("재킷") || content.contains("jacket")) {
            return SearchUtil.handleJacket(content);
        } else if (content.contains("신발") || content.contains("shoes")) {
            return SearchUtil.handleShoes(content);
        } else if (content.contains("반바지") || content.contains("shorts")) {
            return SearchUtil.handleShorts(content);
        } else if (content.contains("양말") || content.contains("socks")) {
            return SearchUtil.handleSocks(content);
        } else if (content.contains("스웨터") || content.contains("sweater")) {
            return SearchUtil.handleSweater(content);
        } else {
            return "00"; // 기본값 설정
        }
    }
}
*/
