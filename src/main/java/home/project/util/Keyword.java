package home.project.util;

public class Keyword {
    // 키워드 배열
    private static final String[][] SHIRT_KEYWORDS = {
            {"셔츠", "0101" },
            {"블라우스", "와이셔츠", "0102" }
    };
    private static final String[][] SLEEVE_KEYWORDS = {
            {"반팔", "01" },
            {"긴팔", "02" }
    };
    private static final String[][] CATEGORY_KEYWORDS = {
            {"니트", "0103" },
            {"바지", "02" },
            {"장갑", "03" },
            {"모자", "04" },
            {"후드", "05" },
            {"재킷", "06" },
            {"신발", "07" },
            {"반바지", "08" },
            {"양말", "09" },
            {"스웨터", "10" }
    };

    public static String handleShirts(String contents) {
        return handleCategory(contents, SHIRT_KEYWORDS, SLEEVE_KEYWORDS, "0101");
    }

    public static String handleKnit(String contents) {
        return handleCategory(contents, new String[][]{{"니트", "0103" }}, SLEEVE_KEYWORDS, "0103");
    }

    public static String handlePants(String contents) {
        return handleCategory(contents, new String[][]{
                {"바지", "02" },
                {"면", "0201" },
                {"청", "0203" }
        }, null, "02");
    }

    public static String handleGloves(String contents) {
        return handleSimpleCategory(contents, "장갑", "03");
    }

    public static String handleHat(String contents) {
        return handleSimpleCategory(contents, "모자", "04");
    }

    public static String handleHoodie(String contents) {
        return handleSimpleCategory(contents, "후드", "05");
    }

    public static String handleJacket(String contents) {
        return handleSimpleCategory(contents, "재킷", "06");
    }

    public static String handleShoes(String contents) {
        return handleSimpleCategory(contents, "신발", "07");
    }

    public static String handleShorts(String contents) {
        return handleSimpleCategory(contents, "반바지", "08");
    }

    public static String handleSocks(String contents) {
        return handleSimpleCategory(contents, "양말", "09");
    }

    public static String handleSweater(String contents) {
        return handleSimpleCategory(contents, "스웨터", "10");
    }

    // 카테고리 처리를 위한 공통 메서드
    private static String handleCategory(String contents, String[][] categoryKeywords, String[][] subKeywords, String defaultCode) {
        if (contents == null || contents.isEmpty()) {
            return defaultCode;
        }
        contents = contents.toLowerCase();

        for (String[] keywords : categoryKeywords) {
            for (String keyword : keywords) {
                if (contents.contains(keyword)) {
                    if (subKeywords != null) {
                        for (String[] subKeyword : subKeywords) {
                            if (contents.contains(subKeyword[0])) {
                                return keywords[keywords.length - 1] + subKeyword[1];
                            }
                        }
                    }
                    return keywords[keywords.length - 1];
                }
            }
        }
        return defaultCode;
    }

    // 간단한 카테고리 처리를 위한 메서드
    private static String handleSimpleCategory(String contents, String keyword, String defaultCode) {
        if (contents == null || contents.isEmpty()) {
            return defaultCode;
        }
        contents = contents.toLowerCase();
        if (contents.contains(keyword)) {
            return defaultCode + "01";
        } else {
            return defaultCode;
        }
    }
}
