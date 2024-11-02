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
INSERT INTO seller (id, name, phone_number, email, address, member_id) VALUES
(1, 'Seller One', '010-1111-1111', 'seller1@example.com', '123 First St', 1),
(2, 'Seller Two', '010-2222-2222', 'seller2@example.com', '456 Second St', 2),
(3, 'Seller Three', '010-3333-3333', 'seller3@example.com', '789 Third St', 3),
(4, 'Seller Four', '010-4444-4444', 'seller4@example.com', '101 Fourth St', 4),
(5, 'Seller Five', '010-5555-5555', 'seller5@example.com', '202 Fifth St', 5),
(6, 'Seller Six', '010-6666-6666', 'seller6@example.com', '303 Sixth St', 6),
(7, 'Seller Seven', '010-7777-7777', 'seller7@example.com', '404 Seventh St', 7),
(8, 'Seller Eight', '010-8888-8888', 'seller8@example.com', '505 Eighth St', 8),
(9, 'Seller Nine', '010-9999-9999', 'seller9@example.com', '606 Ninth St', 9),
(10, 'Seller Ten', '010-1010-1010', 'seller10@example.com', '707 Tenth St', 10);

-- Product 테이블 초기화

INSERT INTO product (id, product_name, brand, category_id, product_num, stock, sold_quantity, price, discount_rate, defective_stock, description, createAt_product, imageUrl, seller_id) VALUES
(1, 'Product One', 'Brand A', 1, 'P0001', 100, 10, 50000, 10, 2, 'This is the first product.', NOW(), 'http://example.com/image1.jpg', 1),
(2, 'Product Two', 'Brand B', 2, 'P0002', 150, 20, 60000, 15, 3, 'This is the second product.', NOW(), 'http://example.com/image2.jpg', 2),
(3, 'Product Three', 'Brand C', 3, 'P0003', 200, 30, 70000, 20, 4, 'This is the third product.', NOW(), 'http://example.com/image3.jpg', 3),
(4, 'Product Four', 'Brand D', 4, 'P0004', 250, 40, 80000, 25, 5, 'This is the fourth product.', NOW(), 'http://example.com/image4.jpg', 4),
(5, 'Product Five', 'Brand E', 5, 'P0005', 300, 50, 90000, 30, 6, 'This is the fifth product.', NOW(), 'http://example.com/image5.jpg', 5),
(6, 'Product Six', 'Brand F', 6, 'P0006', 350, 60, 100000, 35, 7, 'This is the sixth product.', NOW(), 'http://example.com/image6.jpg', 6),
(7, 'Product Seven', 'Brand G', 7, 'P0007', 400, 70, 110000, 40, 8, 'This is the seventh product.', NOW(), 'http://example.com/image7.jpg', 7),
(8, 'Product Eight', 'Brand H', 8, 'P0008', 450, 80, 120000, 45, 9, 'This is the eighth product.', NOW(), 'http://example.com/image8.jpg', 8),
(9, 'Product Nine', 'Brand I', 9, 'P0009', 500, 90, 130000, 50, 10, 'This is the ninth product.', NOW(), 'http://example.com/image9.jpg', 9),
(10, 'Product Ten', 'Brand J', 10, 'P0010', 550, 100, 140000, 55, 11, 'This is the tenth product.', NOW(), 'http://example.com/image10.jpg', 10);

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
 (1, '12320241018162204', NOW(), 1, 150000, 5000, 7500.0),-- 상품 ID 1, 2, 3 포함
 (2, '4520241018162204', NOW(), 2, 100000, 5000, 5000.0), -- 상품 ID 4, 5 포함
 (3, '67820241018162204', NOW(), 3, 150000, 5000, 7500.0),-- 상품 ID 6, 7, 8 포함
 (4, '9120241018162204', NOW(), 4, 100000, 5000, 5000.0); -- 상품 ID 9, 10 포함

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

-- QnA 테이블 초기화
INSERT INTO qna (member_id, qna_type, subject, product_id, orders_id, create_at_qna, description, answer, answer_date, answerer_id, answer_status) VALUES
(1, 'SHIPPING', 'Is this product durable?', 1, NULL, NOW(), 'I want to know if this product can last for more than a year.', NULL, NULL, NULL, 'WAITING'),
(2, 'ORDER', 'Can I track my order?', NULL, 1, NOW(), 'I would like to know how to track my order after shipping.', NULL, NULL, NULL, 'WAITING'),
(3, 'OTHER', 'What are the product dimensions?', 2, NULL, NOW(), 'Could you please provide the dimensions for the product?', NULL, NULL, NULL, 'WAITING'),
(4, 'REFUND', 'What is the return policy?', NULL, 2, NOW() - INTERVAL 1 DAY, 'Can I return the product if I am not satisfied?', 'Yes, returns are accepted within 30 days.', NOW(), 1, 'ANSWERED'),
(5, 'ORDER', 'How long does it take for delivery?', NULL, 3, NOW() - INTERVAL 2 DAY, 'I would like to know the expected delivery time.', 'Delivery takes 3-5 business days.', NOW(), 2, 'ANSWERED'),
(6, 'OTHER', 'Is there a warranty?', 3, NULL, NOW() - INTERVAL 3 DAY, 'Does this product come with a warranty?', 'Yes, a 1-year warranty is included.', NOW(), 3, 'ANSWERED');


/*-- QnA 테이블 초기화
INSERT INTO qna (member_id, qna_type, subject, product_id, orders_id, createat_qna, description, answer, answer_date, answerer_id, answer_status) VALUES
-- WAITING 상태의 질문 3개
(1, 'SHIPPING', 'Is this product durable?', 1, NULL, NOW(), 'I want to know if this product can last for more than a year.', NULL, NULL, NULL, 'WAITING'),
(2, 'ORDER', 'Can I track my order?', NULL, 1, NOW(), 'I would like to know how to track my order after shipping.', NULL, NULL, NULL, 'WAITING'),
(3, 'PRODUCT', 'What are the product dimensions?', 2, NULL, NOW(), 'Could you please provide the dimensions for the product?', NULL, NULL, NULL, 'WAITING'),
*/
/*-- ANSWERED 상태의 질문 3개
(4, 'RETURN', 'What is the return policy?', NULL, 2, NOW() - INTERVAL 1 DAY, 'Can I return the product if I am not satisfied?', 'Yes, returns are accepted within 30 days.', NOW(), 1, 'ANSWERED'),
(5, 'ORDER', 'How long does it take for delivery?', NULL, 3, NOW() - INTERVAL 2 DAY, 'I would like to know the expected delivery time.', 'Delivery takes 3-5 business days.', NOW(), 2, 'ANSWERED'),
(6, 'PRODUCT', 'Is there a warranty?', 3, NULL, NOW() - INTERVAL 3 DAY, 'Does this product come with a warranty?', 'Yes, a 1-year warranty is included.', NOW(), 3, 'ANSWERED');

-- qna_answer 테이블 초기화
INSERT INTO qna_nswer (qna_id, content, answer_date, answerer_id) VALUES
                                                                      (4, 'Yes, returns are accepted within 30 days.', NOW(), 1),
                                                                      (5, 'Delivery takes 3-5 business days.', NOW(), 2),
                                                                      (6, 'Yes, a 1-year warranty is included.', NOW(), 3);

*/


























