package home.project.util;


public class searchUtil {
    public static String handleShirts(String contents) {
        if (contents.contains("티")) {
            if (contents.contains("반팔")) {
                return "010101";
            } else if (contents.contains("긴팔")) {
                return "010102";
            } else {
                return "0101";
            }
        } else if (contents.contains("블라우스") || contents.contains("와이셔츠")) {
            if (contents.contains("반팔")) {
                return "010201";
            } else if (contents.contains("긴팔")) {
                return "010202";
            } else {
                return "0102";
            }
        } else {
            return "0102"; // 기본값 설정
        }
    }

    public static String handleKnit(String contents) {
        if (contents.contains("반팔")) {
            return "010301";
        } else if (contents.contains("긴팔")) {
            return "010302";
        } else {
            return "0103";
        }
    }

    public static String handlePants(String contents) {
        if (contents.contains("면")) {
            return "0201";
        } else if (contents.contains("청")) {
            return "0203";
        } else {
            return "02";
        }
    }
}
