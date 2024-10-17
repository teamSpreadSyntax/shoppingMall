-- Member 테이블 초기화
INSERT INTO member (id, email, password, member_name, phone, gender, birth_date, default_address, second_address, third_address, role, accumulated_purchase, member_grade, point) VALUES
                                                                                                                                                                                      (1, 'user@user.com', '$2a$10$6yfRwLUaiCTOw1eFRxSZBuF9gGxArav6npcMe/ziKUgJueAil4K6S', 'User One', '010-1234-5678', 'M', '1990-01-01', '123 Main St', NULL, NULL, 'user', 0, 'BRONZE', 1000),
                                                                                                                                                                                      (2, 'admin@admin.com', '$2a$10$KzxX38xCfuUWrjt0vCz13u8Wlj7FuGYbMSKvyzz3bevqwbZbPuhOC', 'Admin User', '010-9876-5432', 'F', '1985-05-15', '789 Admin St', NULL, NULL, 'admin', 50000, 'BRONZE', 2000),
                                                                                                                                                                                      (3, 'center@center.com', '$2a$10$BJITQqnE.EkyC4bn9g7y0OJv0R7fCeKQud/Um8lMuFEufcfeZL.UK', 'Center User', '010-1111-2222', 'N', '1980-08-20', '456 Center St', NULL, NULL, 'center', 150000, 'SILVER', 1500),
                                                                                                                                                                                      (4, 'user2@user.com', 'Uuser123456!', 'User Two', '010-2345-6789', 'F', '1991-02-02', '234 Another St', NULL, NULL, 'user', 100000, 'SILVER', 500),
                                                                                                                                                                                      (5, 'user3@user.com', 'Uuser123456!', 'User Three', '010-3456-7890', 'M', '1992-03-03', '345 More St', NULL, NULL, 'user', 200000, 'GOLD', 800),
                                                                                                                                                                                      (6, 'user4@user.com', 'Uuser123456!', 'User Four', '010-4567-8901', 'F', '1993-04-04', '456 Street', NULL, NULL, 'user', 250000, 'GOLD', 1000),
                                                                                                                                                                                      (7, 'user5@user.com', 'Uuser123456!', 'User Five', '010-5678-9012', 'M', '1994-05-05', '567 Another St', NULL, NULL, 'user', 300000, 'PLATINUM', 1500),
                                                                                                                                                                                      (8, 'user6@user.com', 'Uuser123456!', 'User Six', '010-6789-0123', 'F', '1995-06-06', '678 New St', NULL, NULL, 'user', 350000, 'PLATINUM', 2000),
                                                                                                                                                                                      (9, 'user7@user.com', 'Uuser123456!', 'User Seven', '010-7890-1234', 'M', '1996-07-07', '789 Street', NULL, NULL, 'user', 45000, 'BRONZE', 700),
                                                                                                                                                                                      (10, 'user8@user.com', 'Uuser123456!', 'User Eight', '010-8901-2345', 'F', '1997-08-08', '890 Road', NULL, NULL, 'user', 95000, 'BRONZE', 900),
                                                                                                                                                                                      (11, 'user9@user.com', 'Uuser123456!', 'User Nine', '010-9012-3456', 'N', '1998-09-09', '901 Highway', NULL, NULL, 'user', 105000, 'SILVER', 1200),
                                                                                                                                                                                      (12, 'user10@user.com', 'Uuser123456!', 'User Ten', '010-0123-4567', 'F', '1999-10-10', '012 Lane', NULL, NULL, 'user', 195000, 'SILVER', 1600),
                                                                                                                                                                                      (13, 'user11@user.com', 'Uuser123456!', 'User Eleven', '010-1234-5678', 'M', '1990-01-01', '123 Main St', NULL, NULL, 'user', 205000, 'GOLD', 2000),
                                                                                                                                                                                      (14, 'user12@user.com', 'Uuser123456!', 'User Twelve', '010-2345-6789', 'F', '1991-02-02', '234 Another St', NULL, NULL, 'user', 255000, 'GOLD', 300),
                                                                                                                                                                                      (15, 'user13@user.com', 'Uuser123456!', 'User Thirteen', '010-3456-7890', 'M', '1992-03-03', '345 More St', NULL, NULL, 'user', 305000, 'PLATINUM', 400),
                                                                                                                                                                                      (16, 'user14@user.com', 'Uuser123456!', 'User Fourteen', '010-4567-8901', 'F', '1993-04-04', '456 Street', NULL, NULL, 'user', 355000, 'PLATINUM', 600),
                                                                                                                                                                                      (17, 'user15@user.com', 'Uuser123456!', 'User Fifteen', '010-5678-9012', 'M', '1994-05-05', '567 Another St', NULL, NULL, 'user', 45000, 'BRONZE', 800),
                                                                                                                                                                                      (18, 'user16@user.com', 'Uuser123456!', 'User Sixteen', '010-6789-0123', 'F', '1995-06-06', '678 New St', NULL, NULL, 'user', 85000, 'BRONZE', 1000),
                                                                                                                                                                                      (19, 'user17@user.com', 'Uuser123456!', 'User Seventeen', '010-7890-1234', 'M', '1996-07-07', '789 Street', NULL, NULL, 'user', 150000, 'SILVER', 500),
                                                                                                                                                                                      (20, 'user18@user.com', 'Uuser123456!', 'User Eighteen', '010-8901-2345', 'F', '1997-08-08', '890 Road', NULL, NULL, 'user', 250000, 'GOLD', 700);

-- Category 테이블 초기화
-- 레벨 1 카테고리 데이터
INSERT INTO category (id, category_code, category_name, level, parent_id) VALUES
                                                                              (1, '00', '의류', 1, NULL),
                                                                              (2, '01', '가방', 1, NULL),
                                                                              (3, '02', '슈즈', 1, NULL),
                                                                              (4, '03', '액세서리', 1, NULL);

-- 레벨 2 카테고리 데이터
INSERT INTO category (id, category_code, category_name, level, parent_id) VALUES
                                                                              (5, '0001', '상의', 2, 1),
                                                                              (6, '0002', '아우터', 2, 1),
                                                                              (7, '0100', '백팩', 2, 2),
                                                                              (8, '0200', '구두', 2, 3);

-- 레벨 3 카테고리 데이터
INSERT INTO category (id, category_code, category_name, level, parent_id) VALUES
                                                                              (9, '000100', '반소매 티셔츠', 3, 5),
                                                                              (10, '000101', '긴소매 티셔츠', 3, 5),
                                                                              (11, '000200', '후드 집업', 3, 6);

-- Product 테이블 초기화 (상품 20개 추가)
INSERT INTO product (id, product_name, brand, category_id, product_num, stock, sold_quantity, price, discount_rate, defective_stock, description, create_at_product, image_url) VALUES
                                                                                                                                                                                    (1, '반소매 티셔츠', 'BrandA', 9, CONCAT('PN', 1), 150, 0, 30000, 0, 0, 'Short sleeve t-shirt', NOW(), 'http://example.com/images/short_sleeve_tshirt.jpg'),
                                                                                                                                                                                    (2, '긴소매 티셔츠', 'BrandB', 10, CONCAT('PN', 2), 120, 0, 40000, 0, 0, 'Long sleeve t-shirt', NOW(), 'http://example.com/images/long_sleeve_tshirt.jpg'),
                                                                                                                                                                                    (3, '후드 집업', 'BrandC', 11, CONCAT('PN', 3), 30, 0, 120000, 0, 0, 'Hooded zip-up', NOW(), 'http://example.com/images/hooded_zipup.jpg'),
                                                                                                                                                                                    (4, '청바지', 'BrandD', 9, CONCAT('PN', 4), 200, 0, 50000, 0, 0, 'Blue jeans', NOW(), 'http://example.com/images/blue_jeans.jpg'),
                                                                                                                                                                                    (5, '블랙진', 'BrandE', 9, CONCAT('PN', 5), 180, 0, 55000, 0, 0, 'Black jeans', NOW(), 'http://example.com/images/black_jeans.jpg'),
                                                                                                                                                                                    (6, '미니 스커트', 'BrandF', 9, CONCAT('PN', 6), 100, 0, 45000, 0, 0, 'Mini skirt', NOW(), 'http://example.com/images/mini_skirt.jpg'),
                                                                                                                                                                                    (7, '맥시 스커트', 'BrandG', 9, CONCAT('PN', 7), 80, 0, 60000, 0, 0, 'Maxi skirt', NOW(), 'http://example.com/images/maxi_skirt.jpg'),
                                                                                                                                                                                    (8, '원피스', 'BrandH', 9, CONCAT('PN', 8), 90, 0, 70000, 0, 0, 'One-piece dress', NOW(), 'http://example.com/images/one_piece.jpg'),
                                                                                                                                                                                    (9, '블레이저', 'BrandI', 11, CONCAT('PN', 9), 60, 0, 80000, 0, 0, 'Blazer', NOW(), 'http://example.com/images/blazer.jpg'),
                                                                                                                                                                                    (10, '트렌치 코트', 'BrandJ', 11, CONCAT('PN', 10), 50, 0, 120000, 0, 0, 'Trench coat', NOW(), 'http://example.com/images/trench_coat.jpg'),
                                                                                                                                                                                    (11, '패딩', 'BrandK', 11, CONCAT('PN', 11), 70, 0, 150000, 0, 0, 'Padded jacket', NOW(), 'http://example.com/images/padded_jacket.jpg'),
                                                                                                                                                                                    (12, '백팩', 'BrandL', 7, CONCAT('PN', 12), 200, 0, 50000, 0, 0, 'Backpack', NOW(), 'http://example.com/images/backpack.jpg'),
                                                                                                                                                                                    (13, '토트백', 'BrandM', 7, CONCAT('PN', 13), 150, 0, 60000, 0, 0, 'Tote bag', NOW(), 'http://example.com/images/tote_bag.jpg'),
                                                                                                                                                                                    (14, '숄더백', 'BrandN', 7, CONCAT('PN', 14), 130, 0, 55000, 0, 0, 'Shoulder bag', NOW(), 'http://example.com/images/shoulder_bag.jpg'),
                                                                                                                                                                                    (15, '구두', 'BrandO', 8, CONCAT('PN', 15), 80, 0, 80000, 0, 0, 'Dress shoes', NOW(), 'http://example.com/images/dress_shoes.jpg'),
                                                                                                                                                                                    (16, '운동화', 'BrandP', 8, CONCAT('PN', 16), 150, 0, 70000, 0, 0, 'Sneakers', NOW(), 'http://example.com/images/sneakers.jpg'),
                                                                                                                                                                                    (17, '부츠', 'BrandQ', 8, CONCAT('PN', 17), 60, 0, 90000, 0, 0, 'Boots', NOW(), 'http://example.com/images/boots.jpg'),
                                                                                                                                                                                    (18, '샌들', 'BrandR', 8, CONCAT('PN', 18), 120, 0, 40000, 0, 0, 'Sandals', NOW(), 'http://example.com/images/sandals.jpg'),
                                                                                                                                                                                    (19, '목걸이', 'BrandS', 4, CONCAT('PN', 19), 200, 0, 30000, 0, 0, 'Necklace', NOW(), 'http://example.com/images/necklace.jpg'),
                                                                                                                                                                                    (20, '반지', 'BrandT', 4, CONCAT('PN', 20), 250, 0, 20000, 0, 0, 'Ring', NOW(), 'http://example.com/images/ring.jpg');


-- Coupon 테이블 초기화
INSERT INTO coupon (id, coupon_name, coupon_discount_rate, start_date, end_date) VALUES
                                                                              (1, 'WELCOME10', 10, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
                                                                              (2, 'SUMMER20', 20, '2024-06-01 00:00:00', '2024-06-30 23:59:59');

-- Event 테이블 초기화
INSERT INTO event (id, event_name, coupon_discount_rate, description, start_date, end_date, image) VALUES
                                                                                                (1, 'Black Friday Sale', 50, 'Huge discounts on all products', '2024-11-25 00:00:00', '2024-11-30 23:59:59', 'http://example.com/images/black_friday.jpg'),
                                                                                                (2, 'Book Fair', 30, 'Discounts on all books', '2024-03-01 00:00:00', '2024-03-05 23:59:59', 'http://example.com/images/book_fair.jpg');

-- Cart 테이블 초기화
INSERT INTO cart (id, member_id) VALUES
                                     (1, 1),
                                     (2, 2),
                                     (3, 3);

-- ProductCart 테이블 초기화
INSERT INTO product_cart (id, cart_id, product_id, quantity) VALUES
                                                                 (1, 1, 1, 2),
                                                                 (2, 2, 2, 1);

-- Orders 테이블 초기화
INSERT INTO orders (id, order_num, delivery_date, member_id, amount, points_used, points_earned) VALUES
                                                                                                  (1, 'ORD1', NOW(), 1, 60000, 0, 3000),
                                                                                                  (2, 'ORD2', NOW(), 2, 40000, 0, 2000);

-- ProductOrder 테이블 초기화
INSERT INTO product_orders (id, orders_id, product_id, quantity, price) VALUES
                                                                           (1, 1, 1, 2, 30000),
                                                                           (2, 2, 2, 1, 40000);

-- Shipping 테이블 초기화
INSERT INTO shipping (id, delivery_type, delivery_num, delivery_address, arrived_date, arriving_date, delivery_cost, delivery_status, orders_id) VALUES
                                                                                                                                       (1, 'ORDINARY_DELIVERY', 'DN123456', '123 Main St, Cityville', null,DATE_ADD(NOW(), INTERVAL 5 DAY), 0, 'READY_FOR_SHIPMENT', 1),
                                                                                                                                       (2, 'STRAIGHT_DELIVERY', 'DN654321', '456 Oak St, Townsville', null,DATE_ADD(NOW(), INTERVAL 3 DAY), 3000, 'DELIVERY_STARTED', 2);

-- 회원의 누적 구매 금액과 등급 업데이트
UPDATE member SET accumulated_purchase = accumulated_purchase + 60000 WHERE id = 1;
UPDATE member SET accumulated_purchase = accumulated_purchase + 40000 WHERE id = 2;

-- 등급 재설정 로직에 따라 회원 등급 업데이트
UPDATE member SET member_grade = 'BRONZE' WHERE accumulated_purchase < 100000;
UPDATE member SET member_grade = 'SILVER' WHERE accumulated_purchase >= 100000 AND accumulated_purchase < 200000;
UPDATE member SET member_grade = 'GOLD' WHERE accumulated_purchase >= 200000 AND accumulated_purchase < 300000;
UPDATE member SET member_grade = 'PLATINUM' WHERE accumulated_purchase >= 300000;

-- MemberCoupon 테이블 초기화
INSERT INTO member_coupon (id, member_id, coupon_id, issued_at, is_used) VALUES
                                                                             (1, 1, 1, NOW(), false),
                                                                             (2, 2, 2, NOW(), false);

-- ProductCoupon 테이블 초기화
INSERT INTO product_coupon (id, product_id, coupon_id, issued_at, is_used) VALUES
                                                                               (1, 1, 1, NOW(), false),
                                                                               (2, 2, 2, NOW(), false);

-- MemberEvent 테이블 초기화
INSERT INTO member_event (id, member_id, event_id, created_at) VALUES
                                                                   (1, 1, 1, NOW()),
                                                                   (2, 2, 2, NOW());

-- ProductEvent 테이블 초기화
INSERT INTO product_event (id, product_id, event_id, created_at) VALUES
                                                                     (1, 1, 1, NOW()),
                                                                     (2, 2, 2, NOW());



























