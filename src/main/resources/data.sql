-- Member 테이블 초기화
INSERT INTO member (email, password, member_name, phone, gender, birth_date, default_address, second_address, third_address, role, accumulated_purchase, member_grade, point) VALUES
                                                                                                                                                                                  ('user@user.com', '$2a$10$6yfRwLUaiCTOw1eFRxSZBuF9gGxArav6npcMe/ziKUgJueAil4K6S', 'User One', '010-1234-5678', 'M', '1990-01-01', '123 Main St', NULL, NULL, 'user', 0, 'BRONZE', 1000),
                                                                                                                                                                                  ('admin@admin.com', '$2a$10$KzxX38xCfuUWrjt0vCz13u8Wlj7FuGYbMSKvyzz3bevqwbZbPuhOC', 'Admin User', '010-9876-5432', 'F', '1985-05-15', '789 Admin St', NULL, NULL, 'admin', 50000, 'BRONZE', 2000),
                                                                                                                                                                                  ('center@center.com', '$2a$10$BJITQqnE.EkyC4bn9g7y0OJv0R7fCeKQud/Um8lMuFEufcfeZL.UK', 'Center User', '010-1111-2222', 'N', '1980-08-20', '456 Center St', NULL, NULL, 'center', 150000, 'SILVER', 1500),
                                                                                                                                                                                  ('user2@user.com', 'Uuser123456!', 'User Two', '010-2345-6789', 'F', '1991-02-02', '234 Another St', NULL, NULL, 'user', 100000, 'SILVER', 500),
                                                                                                                                                                                  ('user3@user.com', 'Uuser123456!', 'User Three', '010-3456-7890', 'M', '1992-03-03', '345 More St', NULL, NULL, 'user', 200000, 'GOLD', 800),
                                                                                                                                                                                  ('user4@user.com', 'Uuser123456!', 'User Four', '010-4567-8901', 'F', '1993-04-04', '456 Street', NULL, NULL, 'user', 250000, 'GOLD', 1000),
                                                                                                                                                                                  ('user5@user.com', 'Uuser123456!', 'User Five', '010-5678-9012', 'M', '1994-05-05', '567 Another St', NULL, NULL, 'user', 300000, 'PLATINUM', 1500),
                                                                                                                                                                                  ('user6@user.com', 'Uuser123456!', 'User Six', '010-6789-0123', 'F', '1995-06-06', '678 New St', NULL, NULL, 'user', 350000, 'PLATINUM', 2000),
                                                                                                                                                                                  ('user7@user.com', 'Uuser123456!', 'User Seven', '010-7890-1234', 'M', '1996-07-07', '789 Street', NULL, NULL, 'user', 45000, 'BRONZE', 700),
                                                                                                                                                                                  ('user8@user.com', 'Uuser123456!', 'User Eight', '010-8901-2345', 'F', '1997-08-08', '890 Road', NULL, NULL, 'user', 95000, 'BRONZE', 900),
                                                                                                                                                                                  ('user9@user.com', 'Uuser123456!', 'User Nine', '010-9012-3456', 'N', '1998-09-09', '901 Highway', NULL, NULL, 'user', 105000, 'SILVER', 1200),
                                                                                                                                                                                  ('user10@user.com', 'Uuser123456!', 'User Ten', '010-0123-4567', 'F', '1999-10-10', '012 Lane', NULL, NULL, 'user', 195000, 'SILVER', 1600),
                                                                                                                                                                                  ('user11@user.com', 'Uuser123456!', 'User Eleven', '010-1234-5678', 'M', '1990-01-01', '123 Main St', NULL, NULL, 'user', 205000, 'GOLD', 2000),
                                                                                                                                                                                  ('user12@user.com', 'Uuser123456!', 'User Twelve', '010-2345-6789', 'F', '1991-02-02', '234 Another St', NULL, NULL, 'user', 255000, 'GOLD', 300),
                                                                                                                                                                                  ('user13@user.com', 'Uuser123456!', 'User Thirteen', '010-3456-7890', 'M', '1992-03-03', '345 More St', NULL, NULL, 'user', 305000, 'PLATINUM', 400),
                                                                                                                                                                                  ('user14@user.com', 'Uuser123456!', 'User Fourteen', '010-4567-8901', 'F', '1993-04-04', '456 Street', NULL, NULL, 'user', 355000, 'PLATINUM', 600),
                                                                                                                                                                                  ('user15@user.com', 'Uuser123456!', 'User Fifteen', '010-5678-9012', 'M', '1994-05-05', '567 Another St', NULL, NULL, 'user', 45000, 'BRONZE', 800),
                                                                                                                                                                                  ('user16@user.com', 'Uuser123456!', 'User Sixteen', '010-6789-0123', 'F', '1995-06-06', '678 New St', NULL, NULL, 'user', 85000, 'BRONZE', 1000),
                                                                                                                                                                                  ('user17@user.com', 'Uuser123456!', 'User Seventeen', '010-7890-1234', 'M', '1996-07-07', '789 Street', NULL, NULL, 'user', 150000, 'SILVER', 500),
                                                                                                                                                                                  ('user18@user.com', 'Uuser123456!', 'User Eighteen', '010-8901-2345', 'F', '1997-08-08', '890 Road', NULL, NULL, 'user', 250000, 'GOLD', 700);

-- Category 테이블 초기화
INSERT INTO category (category_code, category_name, level, parent_id) VALUES
                                                                          ('00', '의류', 1, NULL),
                                                                          ('01', '가방', 1, NULL),
                                                                          ('02', '슈즈', 1, NULL),
                                                                          ('03', '액세서리', 1, NULL);

INSERT INTO category (category_code, category_name, level, parent_id)
SELECT '0001', '상의', 2, id FROM category WHERE category_code = '00'
UNION ALL
SELECT '0002', '아우터', 2, id FROM category WHERE category_code = '00'
UNION ALL
SELECT '0100', '백팩', 2, id FROM category WHERE category_code = '01'
UNION ALL
SELECT '0200', '구두', 2, id FROM category WHERE category_code = '02';

INSERT INTO category (category_code, category_name, level, parent_id)
SELECT '000100', '반소매 티셔츠', 3, id FROM category WHERE category_code = '0001'
UNION ALL
SELECT '000101', '긴소매 티셔츠', 3, id FROM category WHERE category_code = '0001'
UNION ALL
SELECT '000200', '후드 집업', 3, id FROM category WHERE category_code = '0002';

-- Product 테이블 초기화
INSERT INTO product (product_name, brand, category_id, product_num, stock, sold_quantity, price, discount_rate, defective_stock, description, create_at_product, image_url)
SELECT
    p.product_name, p.brand, c.id as category_id, p.product_num, p.stock, p.sold_quantity, p.price, p.discount_rate, p.defective_stock, p.description, NOW() as create_at_product, p.image_url
FROM (
         VALUES
             ('반소매 티셔츠', 'BrandA', '000100', CONCAT('PN', 1), 150, 0, 30000, 0, 0, 'Short sleeve t-shirt', 'http://example.com/images/short_sleeve_tshirt.jpg'),
             ('긴소매 티셔츠', 'BrandB', '000101', CONCAT('PN', 2), 120, 0, 40000, 0, 0, 'Long sleeve t-shirt', 'http://example.com/images/long_sleeve_tshirt.jpg'),
             ('후드 집업', 'BrandC', '000200', CONCAT('PN', 3), 30, 0, 120000, 0, 0, 'Hooded zip-up', 'http://example.com/images/hooded_zipup.jpg'),
             ('청바지', 'BrandD', '000100', CONCAT('PN', 4), 200, 0, 50000, 0, 0, 'Blue jeans', 'http://example.com/images/blue_jeans.jpg'),
             ('블랙진', 'BrandE', '000100', CONCAT('PN', 5), 180, 0, 55000, 0, 0, 'Black jeans', 'http://example.com/images/black_jeans.jpg'),
             ('미니 스커트', 'BrandF', '000100', CONCAT('PN', 6), 100, 0, 45000, 0, 0, 'Mini skirt', 'http://example.com/images/mini_skirt.jpg'),
             ('맥시 스커트', 'BrandG', '000100', CONCAT('PN', 7), 80, 0, 60000, 0, 0, 'Maxi skirt', 'http://example.com/images/maxi_skirt.jpg'),
             ('원피스', 'BrandH', '000100', CONCAT('PN', 8), 90, 0, 70000, 0, 0, 'One-piece dress', 'http://example.com/images/one_piece.jpg'),
             ('블레이저', 'BrandI', '000200', CONCAT('PN', 9), 60, 0, 80000, 0, 0, 'Blazer', 'http://example.com/images/blazer.jpg'),
             ('트렌치 코트', 'BrandJ', '000200', CONCAT('PN', 10), 50, 0, 120000, 0, 0, 'Trench coat', 'http://example.com/images/trench_coat.jpg'),
             ('패딩', 'BrandK', '000200', CONCAT('PN', 11), 70, 0, 150000, 0, 0, 'Padded jacket', 'http://example.com/images/padded_jacket.jpg'),
             ('백팩', 'BrandL', '0100', CONCAT('PN', 12), 200, 0, 50000, 0, 0, 'Backpack', 'http://example.com/images/backpack.jpg'),
             ('토트백', 'BrandM', '0100', CONCAT('PN', 13), 150, 0, 60000, 0, 0, 'Tote bag', 'http://example.com/images/tote_bag.jpg'),
             ('숄더백', 'BrandN', '0100', CONCAT('PN', 14), 130, 0, 55000, 0, 0, 'Shoulder bag', 'http://example.com/images/shoulder_bag.jpg'),
             ('구두', 'BrandO', '0200', CONCAT('PN', 15), 80, 0, 80000, 0, 0, 'Dress shoes', 'http://example.com/images/dress_shoes.jpg'),
             ('운동화', 'BrandP', '0200', CONCAT('PN', 16), 150, 0, 70000, 0, 0, 'Sneakers', 'http://example.com/images/sneakers.jpg'),
             ('부츠', 'BrandQ', '0200', CONCAT('PN', 17), 60, 0, 90000, 0, 0, 'Boots', 'http://example.com/images/boots.jpg'),
             ('샌들', 'BrandR', '0200', CONCAT('PN', 18), 120, 0, 40000, 0, 0, 'Sandals', 'http://example.com/images/sandals.jpg'),
             ('목걸이', 'BrandS', '03', CONCAT('PN', 19), 200, 0, 30000, 0, 0, 'Necklace', 'http://example.com/images/necklace.jpg'),
             ('반지', 'BrandT', '03', CONCAT('PN', 20), 250, 0, 20000, 0, 0, 'Ring', 'http://example.com/images/ring.jpg')
     ) as p (product_name, brand, category_code, product_num, stock, sold_quantity, price, discount_rate, defective_stock, description, image_url)
         JOIN category c ON c.category_code = p.category_code;

-- Cart 테이블 초기화
INSERT INTO cart (member_id)
SELECT id FROM member WHERE email = 'user@user.com'
UNION ALL
SELECT id FROM member WHERE email = 'admin@admin.com'
UNION ALL
SELECT id FROM member WHERE email = 'center@center.com';

-- Coupon 테이블 초기화
INSERT INTO coupon (coupon_name, coupon_discount_rate, start_date, end_date) VALUES
                                                                                 ('WELCOME10', 10, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
                                                                                 ('SUMMER20', 20, '2024-06-01 00:00:00', '2024-06-30 23:59:59');

-- Event 테이블 초기화
INSERT INTO event (event_name, coupon_discount_rate, description, start_date, end_date, image) VALUES
                                                                                                   ('Black Friday Sale', 50, 'Huge discounts on all products', '2024-11-25 00:00:00', '2024-11-30 23:59:59', 'http://example.com/images/black_friday.jpg'),
                                                                                                   ('Book Fair', 30, 'Discounts on all books', '2024-03-01 00:00:00', '2024-03-05 23:59:59', 'http://example.com/images/book_fair.jpg');

-- Orders 테이블 초기화
INSERT INTO orders (order_num, delivery_date, member_id, amount, points_used, points_earned)
SELECT 'ORD1', NOW(), id, 60000, 0, 3000 FROM member WHERE email = 'user@user.com'
UNION ALL
SELECT 'ORD2', NOW(), id, 40000, 0, 2000 FROM member WHERE email = 'admin@admin.com';

-- Shipping 테이블 초기화
INSERT INTO shipping (delivery_type, delivery_num, delivery_address, arrived_date, arriving_date, delivery_cost, delivery_status, orders_id)
SELECT
    'ORDINARY_DELIVERY',
    'DN123456',
    '123 Main St, Cityville',
    null,
    DATE_ADD(NOW(), INTERVAL 5 DAY),
    0,
    'READY_FOR_SHIPMENT',
    o.id
FROM orders o
         JOIN member m ON o.member_id = m.id
WHERE m.email = 'user@user.com'
UNION ALL
SELECT
    'STRAIGHT_DELIVERY',
    'DN654321',
    '456 Oak St, Townsville',
    null,
    DATE_ADD(NOW(), INTERVAL 3 DAY),
    3000,
    'DELIVERY_STARTED',
    o.id
FROM orders o
         JOIN member m ON o.member_id = m.id
WHERE m.email = 'admin@admin.com';

-- MemberProduct 테이블 초기화
INSERT INTO member_product (member_id, product_id)
SELECT m.id, p.id
FROM member m
         CROSS JOIN product p
WHERE m.email = 'user@user.com'
  AND p.product_name IN ('반소매 티셔츠', '긴소매 티셔츠')
UNION ALL
SELECT m.id, p.id
FROM member m
         CROSS JOIN product p
WHERE m.email = 'admin@admin.com'
  AND p.product_name IN ('후드 집업', '청바지');

-- MemberCoupon 테이블 초기화
INSERT INTO member_coupon (member_id, coupon_id, issued_at, is_used)
SELECT m.id, c.id, NOW(), false
FROM member m
         CROSS JOIN coupon c
WHERE m.email = 'user@user.com' AND c.coupon_name = 'WELCOME10'
UNION ALL
SELECT m.id, c.id, NOW(), false
FROM member m
         CROSS JOIN coupon c
WHERE m.email = 'admin@admin.com' AND c.coupon_name = 'SUMMER20';

-- ProductCoupon 테이블 초기화
INSERT INTO product_coupon (product_id, coupon_id, issued_at, is_used)
SELECT p.id, c.id, NOW(), false
FROM product p
         CROSS JOIN coupon c
WHERE p.product_name = '반소매 티셔츠' AND c.coupon_name = 'WELCOME10'
UNION ALL
SELECT p.id, c.id, NOW(), false
FROM product p
         CROSS JOIN coupon c
WHERE p.product_name = '긴소매 티셔츠' AND c.coupon_name = 'SUMMER20';

-- MemberEvent 테이블 초기화
INSERT INTO member_event (member_id, event_id, created_at)
SELECT m.id, e.id, NOW()
FROM member m
         CROSS JOIN event e
WHERE m.email = 'user@user.com' AND e.event_name = 'Black Friday Sale'
UNION ALL
SELECT m.id, e.id, NOW()
FROM member m
         CROSS JOIN event e
WHERE m.email = 'admin@admin.com' AND e.event_name = 'Book Fair';

-- ProductEvent 테이블 초기화
INSERT INTO product_event (product_id, event_id, created_at)
SELECT p.id, e.id, NOW()
FROM product p
         CROSS JOIN event e
WHERE p.product_name = '반소매 티셔츠' AND e.event_name = 'Black Friday Sale'
UNION ALL
SELECT p.id, e.id, NOW()
FROM product p
         CROSS JOIN event e
WHERE p.product_name = '긴소매 티셔츠' AND e.event_name = 'Book Fair';

-- ProductOrders 테이블 초기화
INSERT INTO product_orders (orders_id, product_id, quantity, price)
SELECT o.id, p.id, 2, p.price
FROM orders o
         JOIN member m ON o.member_id = m.id
         JOIN product p ON p.product_name = '반소매 티셔츠'
WHERE m.email = 'user@user.com'
UNION ALL
SELECT o.id, p.id, 1, p.price
FROM orders o
         JOIN member m ON o.member_id = m.id
         JOIN product p ON p.product_name = '긴소매 티셔츠'
WHERE m.email = 'admin@admin.com';

-- ProductCart 테이블 초기화
INSERT INTO product_cart (cart_id, product_id, quantity)
SELECT c.id, p.id, 2
FROM cart c
         JOIN member m ON c.member_id = m.id
         JOIN product p ON p.product_name = '반소매 티셔츠'
WHERE m.email = 'user@user.com'
UNION ALL
SELECT c.id, p.id, 1
FROM cart c
         JOIN member m ON c.member_id = m.id
         JOIN product p ON p.product_name = '긴소매 티셔츠'
WHERE m.email = 'admin@admin.com';

-- 회원의 누적 구매 금액과 등급 업데이트
UPDATE member SET accumulated_purchase = accumulated_purchase + 60000 WHERE email = 'user@user.com';
UPDATE member SET accumulated_purchase = accumulated_purchase + 40000 WHERE email = 'admin@admin.com';

-- 등급 재설정
UPDATE member SET member_grade = 'BRONZE' WHERE accumulated_purchase < 100000;
UPDATE member SET member_grade = 'SILVER' WHERE accumulated_purchase >= 100000 AND accumulated_purchase < 200000;
UPDATE member SET member_grade = 'GOLD' WHERE accumulated_purchase >= 200000 AND accumulated_purchase < 300000;
UPDATE member SET member_grade = 'PLATINUM' WHERE accumulated_purchase >= 300000;