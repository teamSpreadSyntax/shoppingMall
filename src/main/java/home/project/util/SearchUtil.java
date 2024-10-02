/*
package home.project.util;

public class SearchUtil {

    public static String handleShirts(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "0101"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("셔츠") || contents.contains("t-shirt")) {
            if (contents.contains("반팔") || contents.contains("short-sleeve")) {
                return "010101";
            } else if (contents.contains("긴팔") || contents.contains("long-sleeve")) {
                return "010102";
            } else {
                return "0101";
            }
        } else if (contents.contains("블라우스") || contents.contains("와이셔츠") || contents.contains("blouse") || contents.contains("dress shirt")) {
            if (contents.contains("반팔") || contents.contains("short-sleeve")) {
                return "010201";
            } else if (contents.contains("긴팔") || contents.contains("long-sleeve")) {
                return "010202";
            } else {
                return "0102";
            }
        } else {
            return "0101"; // 일치하지 않을 때도 셔츠 기본값
        }
    }

    public static String handleKnit(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "0103"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("니트") || contents.contains("knit")) {
            if (contents.contains("반팔") || contents.contains("short-sleeve")) {
                return "010301";
            } else if (contents.contains("긴팔") || contents.contains("long-sleeve")) {
                return "010302";
            } else {
                return "0103";
            }
        } else {
            return "0103"; // 기본값 설정
        }
    }

    public static String handlePants(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "02"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("바지") || contents.contains("pants")) {
            if (contents.contains("면") || contents.contains("cotton")) {
                return "0201";
            } else if (contents.contains("청") || contents.contains("denim")) {
                return "0203";
            } else {
                return "02";
            }
        } else {
            return "02"; // 기본값 설정
        }
    }

    public static String handleGloves(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "03"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("장갑") || contents.contains("gloves")) {
            return "0301";
        } else {
            return "03"; // 기본값 설정
        }
    }

    public static String handleHat(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "04"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("모자") || contents.contains("hat")) {
            return "0401";
        } else {
            return "04"; // 기본값 설정
        }
    }

    public static String handleHoodie(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "05"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("후드") || contents.contains("hoodie")) {
            return "0501";
        } else {
            return "05"; // 기본값 설정
        }
    }

    public static String handleJacket(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "06"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("재킷") || contents.contains("jacket")) {
            return "0601";
        } else {
            return "06"; // 기본값 설정
        }
    }

    public static String handleShoes(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "07"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("신발") || contents.contains("shoes")) {
            return "0701";
        } else {
            return "07"; // 기본값 설정
        }
    }

    public static String handleShorts(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "08"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("반바지") || contents.contains("shorts")) {
            return "0801";
        } else {
            return "08"; // 기본값 설정
        }
    }

    public static String handleSocks(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "09"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("양말") || contents.contains("socks")) {
            return "0901";
        } else {
            return "09"; // 기본값 설정
        }
    }

    public static String handleSweater(String contents) {
        if (contents == null || contents.isEmpty()) {
            return "10"; // 기본값 반환
        }
        contents = contents.toLowerCase();
        if (contents.contains("스웨터") || contents.contains("sweater")) {
            return "1001";
        } else {
            return "10"; // 기본값 설정
        }
    }
}
*/
