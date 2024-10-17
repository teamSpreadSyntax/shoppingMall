-- Member 테이블 초기화
INSERT INTO member (email, password, member_name, phone, gender, birth_date, default_address, second_address, third_address, role, accumulated_purchase, member_grade, point) VALUES
('user@user.com', 'Uuser123456!', 'User One', '010-1234-5678', 'M', '1990-01-01', '123 Main St', NULL, NULL, 'user', 0, 'BRONZE', 1000),
('admin@admin.com', 'Aadmin123456!', 'Admin User', '010-9876-5432', 'F', '1985-05-15', '789 Admin St', NULL, NULL, 'admin', 5000, 'BRONZE', 2000),
('center@center.com', 'Ccenter123456!', 'Center User', '010-1111-2222', 'M', '1980-08-20', '456 Center St', NULL, NULL, 'center', 1000, 'BRONZE', 1500),
('user2@user.com', 'Uuser123456!', 'User Two', '010-2345-6789', 'F', '1991-02-02', '234 Another St', NULL, NULL, 'user', 100, 'BRONZE', 500),
('user3@user.com', 'Uuser123456!', 'User Three', '010-3456-7890', 'M', '1992-03-03', '345 More St', NULL, NULL, 'user', 200, 'BRONZE', 800),
('user4@user.com', 'Uuser123456!', 'User Four', '010-4567-8901', 'F', '1993-04-04', '456 Street', NULL, NULL, 'user', 300, 'BRONZE', 1000),
('user5@user.com', 'Uuser123456!', 'User Five', '010-5678-9012', 'M', '1994-05-05', '567 Another St', NULL, NULL, 'user', 400, 'BRONZE', 1500),
('user6@user.com', 'Uuser123456!', 'User Six', '010-6789-0123', 'F', '1995-06-06', '678 New St', NULL, NULL, 'user', 500, 'BRONZE', 2000),
('user7@user.com', 'Uuser123456!', 'User Seven', '010-7890-1234', 'M', '1996-07-07', '789 Street', NULL, NULL, 'user', 600, 'BRONZE', 700),
('user8@user.com', 'Uuser123456!', 'User Eight', '010-8901-2345', 'F', '1997-08-08', '890 Road', NULL, NULL, 'user', 700, 'BRONZE', 900),
('user9@user.com', 'Uuser123456!', 'User Nine', '010-9012-3456', 'M', '1998-09-09', '901 Highway', NULL, NULL, 'user', 800, 'BRONZE', 1200),
('user10@user.com', 'Uuser123456!', 'User Ten', '010-0123-4567', 'F', '1999-10-10', '012 Lane', NULL, NULL, 'user', 900, 'BRONZE', 1600),
('user11@user.com', 'Uuser123456!', 'User Eleven', '010-1234-5678', 'M', '1990-01-01', '123 Main St', NULL, NULL, 'user', 1000, 'BRONZE', 2000),
('user12@user.com', 'Uuser123456!', 'User Twelve', '010-2345-6789', 'F', '1991-02-02', '234 Another St', NULL, NULL, 'user', 1100, 'BRONZE', 300),
('user13@user.com', 'Uuser123456!', 'User Thirteen', '010-3456-7890', 'M', '1992-03-03', '345 More St', NULL, NULL, 'user', 1200, 'BRONZE', 400),
('user14@user.com', 'Uuser123456!', 'User Fourteen', '010-4567-8901', 'F', '1993-04-04', '456 Street', NULL, NULL, 'user', 1300, 'BRONZE', 600),
('user15@user.com', 'Uuser123456!', 'User Fifteen', '010-5678-9012', 'M', '1994-05-05', '567 Another St', NULL, NULL, 'user', 1400, 'BRONZE', 800),
('user16@user.com', 'Uuser123456!', 'User Sixteen', '010-6789-0123', 'F', '1995-06-06', '678 New St', NULL, NULL, 'user', 1500, 'BRONZE', 1000),
('user17@user.com', 'Uuser123456!', 'User Seventeen', '010-7890-1234', 'M', '1996-07-07', '789 Street', NULL, NULL, 'user', 1600, 'BRONZE', 500),
('user18@user.com', 'Uuser123456!', 'User Eighteen', '010-8901-2345', 'F', '1997-08-08', '890 Road', NULL, NULL, 'user', 1700, 'BRONZE', 700),
('user19@user.com', 'Uuser123456!', 'User Nineteen', '010-9012-3456', 'M', '1998-09-09', '901 Highway', NULL, NULL, 'user', 1800, 'BRONZE', 900);

-- Category 테이블 초기화
INSERT INTO category (category_code, category_name, level, parent_id) VALUES
('00', '의류', 1, NULL),
('01', '가방', 1, NULL),
('02', '슈즈', 1, NULL),
('03', '액세서리', 1, NULL);

-- 레벨 2 카테고리 데이터
INSERT INTO category (category_code, category_name, level, parent_id)
SELECT '0001', '상의', 2, id FROM category WHERE category_code = '00'
UNION ALL
SELECT '0002', '아우터', 2, id FROM category WHERE category_code = '00'
UNION ALL
SELECT '0100', '백팩', 2, id FROM category WHERE category_code = '01'
UNION ALL
SELECT '0200', '구두', 2, id FROM category WHERE category_code = '02';

-- 레벨 3 카테고리 데이터
INSERT INTO category (category_code, category_name, level, parent_id)
SELECT '000100', '반소매 티셔츠', 3, id FROM category WHERE category_code = '0001'
UNION ALL
SELECT '000101', '긴소매 티셔츠', 3, id FROM category WHERE category_code = '0001'
UNION ALL
SELECT '000200', '후드 집업', 3, id FROM category WHERE category_code = '0002';

-- Product 테이블 초기화
INSERT INTO product (product_name, price, category_id, stock_quantity)
SELECT '반소매 티셔츠', 30, id, 150 FROM category WHERE category_code = '000100'
UNION ALL
SELECT '긴소매 티셔츠', 40, id, 120 FROM category WHERE category_code = '000101'
UNION ALL
SELECT '후드 집업', 120, id, 30 FROM category WHERE category_code = '000200';

-- Coupon 테이블 초기화
INSERT INTO coupon (code, discount, expiration_date) VALUES
('WELCOME10', 10, '2024-12-31'),
('SUMMER20', 20, '2024-06-30');

-- Event 테이블 초기화
INSERT INTO event (name, description, start_date, end_date) VALUES
('Black Friday Sale', 'Huge discounts on all electronics', '2024-11-25', '2024-11-30'),
('Book Fair', 'Discounts on all books', '2024-03-01', '2024-03-05');

-- Cart 테이블 초기화
INSERT INTO cart (member_id)
SELECT id FROM member WHERE email = 'user@user.com'
UNION ALL
SELECT id FROM member WHERE email = 'admin@admin.com'
UNION ALL
SELECT id FROM member WHERE email = 'center@center.com';

-- Orders 테이블 초기화
INSERT INTO orders (member_id, total_price, status)
SELECT id, 1520, 'pending' FROM member WHERE email = 'user@user.com'
UNION ALL
SELECT id, 820, 'completed' FROM member WHERE email = 'admin@admin.com';

-- Shipping 테이블 초기화
INSERT INTO shipping (order_id, address, status)
SELECT id, '123 Main St, Cityville', 'shipped' FROM orders WHERE total_price = 1520
UNION ALL
SELECT id, '456 Oak St, Townsville', 'delivered' FROM orders WHERE total_price = 820;

-- MemberCoupon 테이블 초기화
INSERT INTO member_coupon (member_id, coupon_id)
SELECT m.id, c.id
FROM member m, coupon c
WHERE m.email = 'user@user.com' AND c.code = 'WELCOME10'
UNION ALL
SELECT m.id, c.id
FROM member m, coupon c
WHERE m.email = 'admin@admin.com' AND c.code = 'SUMMER20';

-- MemberEvent 테이블 초기화
INSERT INTO member_event (member_id, event_id)
SELECT m.id, e.id
FROM member m, event e
WHERE m.email = 'user@user.com' AND e.name = 'Black Friday Sale'
UNION ALL
SELECT m.id, e.id
FROM member m, event e
WHERE m.email = 'admin@admin.com' AND e.name = 'Book Fair';

-- ProductCoupon 테이블 초기화
-- ProductCoupon 테이블 초기화
INSERT INTO product_coupon (product_id, coupon_id)
SELECT p.id, c.id
FROM product p, coupon c
WHERE p.product_name = '반소매 티셔츠' AND c.code = 'WELCOME10'
UNION ALL
SELECT p.id, c.id
FROM product p, coupon c
WHERE p.product_name = '긴소매 티셔츠' AND c.code = 'SUMMER20';

-- ProductEvent 테이블 초기화
INSERT INTO product_event (product_id, event_id)
SELECT p.id, e.id
FROM product p, event e
WHERE p.product_name = '반소매 티셔츠' AND e.name = 'Black Friday Sale'
UNION ALL
SELECT p.id, e.id
FROM product p, event e
WHERE p.product_name = '긴소매 티셔츠' AND e.name = 'Book Fair';

-- ProductOrder 테이블 초기화
INSERT INTO product_order (product_id, order_id)
SELECT p.id, o.id
FROM product p, orders o
WHERE p.product_name = '반소매 티셔츠' AND o.total_price = 1520
UNION ALL
SELECT p.id, o.id
FROM product p, orders o
WHERE p.product_name = '긴소매 티셔츠' AND o.total_price = 820;

-- ProductCart 테이블 초기화
INSERT INTO product_cart (product_id, cart_id)
SELECT p.id, c.id
FROM product p, cart c, member m
WHERE p.product_name = '반소매 티셔츠' AND m.email = 'user@user.com' AND c.member_id = m.id
UNION ALL
SELECT p.id, c.id
FROM product p, cart c, member m
WHERE p.product_name = '긴소매 티셔츠' AND m.email = 'admin@admin.com' AND c.member_id = m.id;