package home.project.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryMapper {
    private static final Map<String, List<String>> categoryMap = new HashMap<>();

    static {
        // 대분류
        categoryMap.put("00", Arrays.asList("00", "의류", "옷", "패션"));
        categoryMap.put("01", Arrays.asList("01", "가방", "백"));
        categoryMap.put("02", Arrays.asList("02", "슈즈", "신발"));
        categoryMap.put("03", Arrays.asList("03", "액세서리", "악세사리"));

        // 의류 하위 카테고리
        categoryMap.put("0001", Arrays.asList("0001", "상의", "톱"));
        categoryMap.put("000100", Arrays.asList("000100", "반소매 티셔츠", "반팔", "티셔츠", "티"));
        categoryMap.put("000101", Arrays.asList("000101", "긴소매 티셔츠", "긴팔"));
        categoryMap.put("000102", Arrays.asList("000102", "민소매 티셔츠", "민소매", "나시"));
        categoryMap.put("000103", Arrays.asList("000103", "셔츠/블라우스", "셔츠", "블라우스"));
        categoryMap.put("000104", Arrays.asList("000104", "니트/스웨터", "니트", "스웨터"));
        categoryMap.put("000105", Arrays.asList("000105", "후드 티셔츠", "후드티", "후드"));
        categoryMap.put("000106", Arrays.asList("000106", "맨투맨/스웨트셔츠", "맨투맨", "스웨트셔츠"));
        categoryMap.put("000107", Arrays.asList("000107", "피케/카라 티셔츠", "피케티", "카라티"));
        categoryMap.put("000108", Arrays.asList("000108", "튜닉", "롱티"));

        categoryMap.put("0002", Arrays.asList("0002", "아우터", "겉옷"));
        categoryMap.put("000200", Arrays.asList("000200", "후드 집업", "집업후드"));
        categoryMap.put("000201", Arrays.asList("000201", "블루종/MA-1", "블루종", "MA-1"));
        categoryMap.put("000202", Arrays.asList("000202", "레더 재킷", "가죽자켓"));
        categoryMap.put("000203", Arrays.asList("000203", "트러커 재킷", "청자켓"));
        categoryMap.put("000204", Arrays.asList("000204", "수트/블레이저 재킷", "수트", "블레이저"));
        categoryMap.put("000205", Arrays.asList("000205", "카디건", "가디건"));
        categoryMap.put("000206", Arrays.asList("000206", "플리스/뽀글이", "플리스", "뽀글이"));
        categoryMap.put("000207", Arrays.asList("000207", "트레이닝 재킷", "트레이닝자켓"));
        categoryMap.put("000208", Arrays.asList("000208", "환절기 코트", "봄코트", "가을코트"));
        categoryMap.put("000209", Arrays.asList("000209", "겨울 싱글 코트", "싱글코트"));

        categoryMap.put("0003", Arrays.asList("0003", "바지", "팬츠"));
        categoryMap.put("000300", Arrays.asList("000300", "데님 팬츠", "청바지", "진"));
        categoryMap.put("000301", Arrays.asList("000301", "코튼 팬츠", "면바지"));
        categoryMap.put("000302", Arrays.asList("000302", "슈트 팬츠/슬랙스", "슬랙스"));
        categoryMap.put("000303", Arrays.asList("000303", "트레이닝/조거 팬츠", "트레이닝바지", "조거팬츠"));
        categoryMap.put("000304", Arrays.asList("000304", "레깅스", "타이츠"));
        categoryMap.put("000305", Arrays.asList("000305", "점프 슈트/오버올", "점프슈트", "오버올"));

        categoryMap.put("0004", Arrays.asList("0004", "스커트", "치마"));
        categoryMap.put("000400", Arrays.asList("000400", "미니스커트", "미니치마"));
        categoryMap.put("000401", Arrays.asList("000401", "미디스커트", "미디치마"));
        categoryMap.put("000402", Arrays.asList("000402", "롱스커트", "롱치마"));

        categoryMap.put("0005", Arrays.asList("0005", "원피스", "드레스"));
        categoryMap.put("000500", Arrays.asList("000500", "미니 원피스", "미니드레스"));
        categoryMap.put("000501", Arrays.asList("000501", "미디 원피스", "미디드레스"));
        categoryMap.put("000502", Arrays.asList("000502", "맥시 원피스", "맥시드레스"));

        // 가방 하위 카테고리
        categoryMap.put("0100", Arrays.asList("0100", "백팩", "배낭"));
        categoryMap.put("0101", Arrays.asList("0101", "메신저/크로스 백", "메신저백", "크로스백"));
        categoryMap.put("0102", Arrays.asList("0102", "숄더백", "토트백"));
        categoryMap.put("0103", Arrays.asList("0103", "클러치 백", "클러치"));

        // 슈즈 하위 카테고리
        categoryMap.put("0200", Arrays.asList("0200", "구두", "힐"));
        categoryMap.put("0201", Arrays.asList("0201", "부츠", "워커"));
        categoryMap.put("0202", Arrays.asList("0202", "샌들", "슬리퍼"));
        categoryMap.put("0203", Arrays.asList("0203", "스니커즈", "운동화"));

        // 액세서리 하위 카테고리
        categoryMap.put("0300", Arrays.asList("0300", "주얼리", "쥬얼리"));
        categoryMap.put("0301", Arrays.asList("0301", "모자", "캡", "버킷햇"));
        categoryMap.put("0302", Arrays.asList("0302", "양말", "삭스"));
        categoryMap.put("0303", Arrays.asList("0303", "벨트", "허리띠"));
        categoryMap.put("0304", Arrays.asList("0304", "스카프/머플러", "스카프", "머플러"));
    }

    public static String getCode(String category) {
        String lowercaseCategory = category.toLowerCase();
        return categoryMap.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(keyword -> keyword.toLowerCase().contains(lowercaseCategory)
                                || lowercaseCategory.contains(keyword.toLowerCase())))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse("0000");  // 기본값
    }
}