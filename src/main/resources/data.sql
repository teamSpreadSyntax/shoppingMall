-- src/main/resources/data.sql

-- 카테고리 테이블이 비어있는지 확인
INSERT INTO category (id, category_code, category_name, level, parent_id)
SELECT 1, '00', '의류', 1, NULL
WHERE NOT EXISTS (SELECT 1 FROM category);

-- 첫 번째 삽입이 성공했다면(테이블이 비어있었다면) 나머지 레벨 1 데이터 삽입
INSERT INTO category (id, category_code, category_name, level, parent_id)
SELECT * FROM (
    SELECT 2, '01', '가방', 1, NULL UNION ALL
    SELECT 3, '02', '슈즈', 1, NULL UNION ALL
    SELECT 4, '03', '액세서리', 1, NULL
) AS tmp
WHERE EXISTS (SELECT 1 FROM category WHERE id = 1);

-- 레벨 2 데이터 삽입
INSERT INTO category (id, category_code, category_name, level, parent_id)
SELECT * FROM (
    SELECT 5, '0001', '상의', 2, 1 UNION ALL
    SELECT 15, '0002', '아우터', 2, 1 UNION ALL
    SELECT 26, '0003', '바지', 2, 1 UNION ALL
    SELECT 33, '0004', '스커트', 2, 1 UNION ALL
    SELECT 37, '0005', '원피스', 2, 1 UNION ALL
    SELECT 41, '0100', '백팩', 2, 2 UNION ALL
    SELECT 42, '0101', '메신저/크로스 백', 2, 2 UNION ALL
    SELECT 43, '0102', '숄더백', 2, 2 UNION ALL
    SELECT 44, '0103', '클러치 백', 2, 2 UNION ALL
    SELECT 45, '0200', '구두', 2, 3 UNION ALL
    SELECT 46, '0201', '부츠', 2, 3 UNION ALL
    SELECT 47, '0202', '샌들', 2, 3 UNION ALL
    SELECT 48, '0203', '스니커즈', 2, 3 UNION ALL
    SELECT 49, '0300', '주얼리', 2, 4 UNION ALL
    SELECT 50, '0301', '모자', 2, 4 UNION ALL
    SELECT 51, '0302', '양말', 2, 4 UNION ALL
    SELECT 52, '0303', '벨트', 2, 4 UNION ALL
    SELECT 53, '0304', '스카프/머플러', 2, 4 UNION ALL
    SELECT 54, '0000', '없음', 2, 1
) AS tmp
WHERE EXISTS (SELECT 1 FROM category WHERE id = 1);

-- 레벨 3 데이터 삽입
INSERT INTO category (id, category_code, category_name, level, parent_id)
SELECT * FROM (
    SELECT 6, '000100', '반소매 티셔츠', 3, 5 UNION ALL
    SELECT 7, '000101', '긴소매 티셔츠', 3, 5 UNION ALL
    SELECT 8, '000102', '민소매 티셔츠', 3, 5 UNION ALL
    SELECT 9, '000103', '셔츠/블라우스', 3, 5 UNION ALL
    SELECT 10, '000104', '니트/스웨터', 3, 5 UNION ALL
    SELECT 11, '000105', '후드 티셔츠', 3, 5 UNION ALL
    SELECT 12, '000106', '맨투맨/스웨트셔츠', 3, 5 UNION ALL
    SELECT 13, '000107', '피케/카라 티셔츠', 3, 5 UNION ALL
    SELECT 14, '000108', '튜닉', 3, 5 UNION ALL
    SELECT 16, '000200', '후드 집업', 3, 15 UNION ALL
    SELECT 17, '000201', '블루종/MA-1', 3, 15 UNION ALL
    SELECT 18, '000202', '레더 재킷', 3, 15 UNION ALL
    SELECT 19, '000203', '트러커 재킷', 3, 15 UNION ALL
    SELECT 20, '000204', '수트/블레이저 재킷', 3, 15 UNION ALL
    SELECT 21, '000205', '카디건', 3, 15 UNION ALL
    SELECT 22, '000206', '플리스/뽀글이', 3, 15 UNION ALL
    SELECT 23, '000207', '트레이닝 재킷', 3, 15 UNION ALL
    SELECT 24, '000208', '환절기 코트', 3, 15 UNION ALL
    SELECT 25, '000209', '겨울 싱글 코트', 3, 15 UNION ALL
    SELECT 27, '000300', '데님 팬츠', 3, 26 UNION ALL
    SELECT 28, '000301', '코튼 팬츠', 3, 26 UNION ALL
    SELECT 29, '000302', '슈트 팬츠/슬랙스', 3, 26 UNION ALL
    SELECT 30, '000303', '트레이닝/조거 팬츠', 3, 26 UNION ALL
    SELECT 31, '000304', '레깅스', 3, 26 UNION ALL
    SELECT 32, '000305', '점프 슈트/오버올', 3, 26 UNION ALL
    SELECT 34, '000400', '미니스커트', 3, 33 UNION ALL
    SELECT 35, '000401', '미디스커트', 3, 33 UNION ALL
    SELECT 36, '000402', '롱스커트', 3, 33 UNION ALL
    SELECT 38, '000500', '미니 원피스', 3, 37 UNION ALL
    SELECT 39, '000501', '미디 원피스', 3, 37 UNION ALL
    SELECT 40, '000502', '맥시 원피스', 3, 37
) AS tmp
WHERE EXISTS (SELECT 1 FROM category WHERE id = 1);
