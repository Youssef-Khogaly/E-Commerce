
create database if not exists E_Commerce ;

use E_Commerce;
set default_storage_engine = InnoDB;

-- USERS
CREATE TABLE IF NOT EXISTS `user` (
    usr_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usr_name VARCHAR(16) UNIQUE NOT NULL,
    usr_email VARCHAR(127) UNIQUE NOT NULL,
    usr_pass VARCHAR(128) NOT NULL,
    `role` ENUM('customer','admin') NOT NULL DEFAULT 'customer',
    isDeleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT usrNameCheck CHECK (usr_name REGEXP '^[A-Za-z][A-Za-z0-9_]{5,15}$'),
    CONSTRAINT usr_passLen CHECK (CHAR_LENGTH(usr_pass) > 7),
    CONSTRAINT usr_passCheck CHECK (usr_pass REGEXP '^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$')
);

-- CUSTOMERS & ADMINS
CREATE TABLE IF NOT EXISTS customer (
    cust_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_customer_user FOREIGN KEY (cust_id)
        REFERENCES `user`(usr_id)
        ON UPDATE CASCADE
        ON DELETE restrict
);

CREATE TABLE IF NOT EXISTS `admin` (
    admin_id BIGINT PRIMARY KEY,
    lastLogin TIMESTAMP DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ,
    CONSTRAINT fk_admin_user FOREIGN KEY (admin_id)
        REFERENCES `user`(usr_id)
        ON UPDATE CASCADE
        ON DELETE restrict
);

-- CUSTOMER ADDRESSES
CREATE TABLE IF NOT EXISTS customer_address (
    cust_id BIGINT NOT NULL,
    country VARCHAR(63) NOT NULL,
    city VARCHAR(63) NOT NULL,
    street VARCHAR(63) NOT NULL,
    building VARCHAR(63) NOT NULL,
    PRIMARY KEY (cust_id, country, city, street, building),
    CONSTRAINT fk_cust_addr FOREIGN KEY (cust_id)
        REFERENCES customer(cust_id)
        ON UPDATE CASCADE
        ON DELETE restrict
);

-- PRODUCTS & IMAGES
CREATE TABLE IF NOT EXISTS product (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    stock INT DEFAULT 0 CHECK (stock >= 0),
    title VARCHAR(63) NOT NULL,
    description MEDIUMTEXT,
    price DECIMAL(12,2) NOT NULL DEFAULT 0.00 check(price >= 0),
    isDeleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS product_image (
    prod_id BIGINT NOT NULL,
    image_url VARCHAR(512) NOT NULL,
    isMain BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (prod_id, image_url),
    CONSTRAINT fk_product_image FOREIGN KEY (prod_id)
        REFERENCES product(product_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- ====================================================
-- CATEGORY
-- ====================================================
CREATE TABLE IF NOT EXISTS category (
    cate_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(16) UNIQUE NOT NULL,
    parent_id INT DEFAULT NULL,
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_id)
        REFERENCES category(cate_id)
);

CREATE TABLE IF NOT EXISTS product_category (
    product_id BIGINT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_pc_product FOREIGN KEY (product_id)
        REFERENCES product(product_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_pc_category FOREIGN KEY (category_id)
        REFERENCES category(cate_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- CART
CREATE TABLE IF NOT EXISTS cart (
    cust_id BIGINT PRIMARY KEY,
    cart_total_price DECIMAL(12,2) DEFAULT 0.00 CHECK (cart_total_price >= 0),
    CONSTRAINT fk_cart_customer FOREIGN KEY (cust_id)
        REFERENCES customer(cust_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS cart_item (
    prod_id BIGINT NOT NULL,
    cust_id BIGINT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL CHECK (unit_price > 0),
    quantity INT NOT NULL CHECK (quantity > 0),
    quantityPrice DECIMAL(12,2) AS (unit_price * quantity) VIRTUAL,
    PRIMARY KEY (prod_id, cust_id),
    CONSTRAINT fk_cartitem_product FOREIGN KEY (prod_id)
        REFERENCES product(product_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_cartitem_customer FOREIGN KEY (cust_id)
        REFERENCES customer(cust_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- ====================================================
-- REVIEWS
-- ====================================================
CREATE TABLE IF NOT EXISTS review (
    cust_id BIGINT,
    product_id BIGINT,
    comment TEXT DEFAULT NULL,
    rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id, cust_id),
    CONSTRAINT fk_review_customer FOREIGN KEY (cust_id)
        REFERENCES customer(cust_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_review_product FOREIGN KEY (product_id)
        REFERENCES product(product_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) DEFAULT CHARSET=utf8mb4;

-- ORDERS
CREATE TABLE IF NOT EXISTS `order` (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipientName VARCHAR(63) NOT NULL,
    recipientPhone VARCHAR(63) NOT NULL,
    country VARCHAR(64) NOT NULL,
    city VARCHAR(64) NOT NULL,
    street VARCHAR(63) NOT NULL,
    building VARCHAR(63) NOT NULL,
    order_state ENUM('canceled','refunded','pending','shipping','delivered'),
    total_price DECIMAL(12,2) NOT NULL DEFAULT 0.00 CHECK (total_price >= 0.00),
    cust_id BIGINT,
    CONSTRAINT fk_order_customer FOREIGN KEY (cust_id)
        REFERENCES customer(cust_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS order_item (
    order_id BIGINT,
    product_id BIGINT,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12,2) NOT NULL CHECK (unit_price > 0),
    quantity_price DECIMAL(12,2) AS (quantity * unit_price) VIRTUAL,
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id)
        REFERENCES `order`(order_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id)
        REFERENCES product(product_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- PAYMENTS
CREATE TABLE IF NOT EXISTS payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
    paymentState ENUM('failed','pending','confirmed','refunded') DEFAULT 'pending' not null,
    card_type VARCHAR(31) NOT NULL,
    card_last_4 CHAR(4) NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id)
        REFERENCES `order`(order_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- CUSTOMER CREDIT CARDS

CREATE TABLE IF NOT EXISTS customerCreditCards (
    cust_id BIGINT NOT NULL,
    card_num CHAR(16) NOT NULL,
    ccv CHAR(3) NOT NULL,
    expMonth TINYINT NOT NULL CHECK (expMonth BETWEEN 1 AND 12),
    expYear TINYINT NOT NULL CHECK (expYear BETWEEN 0 AND 99),
    expDate CHAR(5) AS (
        CONCAT(LPAD(expMonth,2,'0'),'/',LPAD(expYear,2,'0'))
    ) VIRTUAL,
    PRIMARY KEY (cust_id, card_num),
    CONSTRAINT fk_cc_customer FOREIGN KEY (cust_id)
        REFERENCES customer(cust_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
-- ============ Triggers ======================

DELIMITER $$
--  When a new user is inserted → create either customer or admin
create trigger addNewUser after insert on `user` for each row
begin
	if NEW.`role` = 'customer' then
        insert into customer values(new.usr_id);
	ELSEIF new.role = 'admin' then
		insert into admin values(new.usr_id);	
	end if; 
end$$
-- When a new customer is created → create their cart automatically
create trigger newCustomerCartCreation after insert on customer for each row
begin

	insert into cart(cust_id) values(new.cust_id);
end$$

-- Prevent user physical deletion → convert to soft delete instead
create trigger user_deletion before delete on `user` for each row
begin
        -- prevernt physical deletion
        Signal sqlstate '45000'
			set MESSAGE_TEXT = 'user physical deletion not allowed';
end$$

-- Update admin timestamp when related user data changes
create trigger admin_update after update on `user` for each row
begin
	if new.`role` = 'admin'and (old.usr_name <> new.usr_name or old.usr_email <> new.usr_email  or old.usr_pass <> new.usr_pass) then
		update `admin` set updated_at = CURRENT_timestamp where admin_id = NEW.usr_id;
	end if; 
end$$
-- Prevent product physical deletion → apply soft delete
create trigger product_delete before delete on product for each row
begin
        Signal sqlstate '45000'
			set MESSAGE_TEXT = 'physical product deletion are not allowed';
end$$

-- Prevent Payment deletion
create trigger Prevent_PaymentHistory_deletion before delete on payment  for each row
begin
	signal SQLSTATE '45000'
		set MESSAGE_TEXT = 'Payment histroy deletion are not allowed';
end$$

-- prevent order history deletion
create trigger prevent_orderDeletion before delete on `order` for each row
begin
	signal SQLSTATE '45000'
		set MESSAGE_TEXT = 'Order deletion are not allowed';
end $$

DELIMITER ;