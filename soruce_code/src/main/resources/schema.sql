create database if not exists E_Commerce ;

use E_Commerce;
set default_storage_engine = InnoDB;

-- USERS
CREATE TABLE IF NOT EXISTS `user` (
                                      usr_id  bigint PRIMARY KEY AUTO_INCREMENT,
                                      usr_name VARCHAR(16) UNIQUE NOT NULL,
    usr_email VARCHAR(127) UNIQUE NOT NULL,
    usr_pass VARCHAR(128) NOT NULL,
    `role` ENUM('customer','admin') NOT NULL DEFAULT 'customer',
    isDeleted BOOLEAN DEFAULT FALSE,
    isEmailVerified boolean default false,
    constraint usr_email_delete unique (usr_email,isDeleted)
    );

-- CUSTOMERS & ADMINS
CREATE TABLE IF NOT EXISTS customer (
                                        cust_id  bigint PRIMARY KEY,
                                        CONSTRAINT fk_customer_user FOREIGN KEY (cust_id)
    REFERENCES `user`(usr_id)
    ON UPDATE CASCADE
    ON DELETE restrict
    );

CREATE TABLE IF NOT EXISTS `admin` (
                                       admin_id  bigint PRIMARY KEY,
                                       lastLogin TIMESTAMP DEFAULT NULL,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP  DEFAULT NULL on update current_timestamp,
                                       CONSTRAINT fk_admin_user FOREIGN KEY (admin_id)
    REFERENCES `user`(usr_id)
    ON UPDATE CASCADE
    ON DELETE restrict
    );

-- PRODUCTS & IMAGES
CREATE TABLE IF NOT EXISTS product (
    product_id  bigint PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(63) NOT NULL,
    description MEDIUMTEXT,
    price DECIMAL(12,2) NOT NULL DEFAULT 0.00 check(price >= 0),
    addedAt TIMESTAMP default CURRENT_TIMESTAMP not null
    );
create table if not exists product_stock(
    product_id bigint primary key ,
    stock INT  not null DEFAULT 0 CHECK (stock >= 0),
    reservedStock INT not null default 0 check ( reservedStock >= 0),
    availableStock int  generated always as (stock-reservedStock)virtual,
    constraint frg_p foreign key (product_id) references product(product_id)
);
CREATE TABLE IF NOT EXISTS image
(
    image_id  bigint primary key auto_increment not null,
    image_url VARCHAR(1024)  NOT NULL,
    product_id bigint references product(product_id) on update cascade  on delete set null
);

-- ====================================================
-- CATEGORY
-- ====================================================
CREATE TABLE IF NOT EXISTS category (
                                        cate_id bigint PRIMARY KEY AUTO_INCREMENT not null ,
                                        name VARCHAR(64) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS product_category (
   product_id  bigint NOT NULL,
   category_id bigint NOT NULL,
   PRIMARY KEY (product_id,category_id),

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

create table if not exists cart(
        cart_id bigint not null primary key ,
       subTotalInCents bigint not null ,
        constraint fk_customer foreign key (cart_id) references customer(cust_id)
);

CREATE TABLE IF NOT EXISTS cart_item (
                                         prod_id  bigint NOT NULL,
                                         cart_id bigint not null ,
                                         quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (cart_id,prod_id),
    constraint fk_2product foreign key(prod_id) references product(product_id),
    constraint fk_cart foreign key (cart_id) references cart(cart_id)
    );
CREATE TABLE IF NOT EXISTS CustomerOrder (
                                             order_id  binary(16) primary key,
                                             cust_id  bigint  ,
                                             recipientName VARCHAR(63) not null ,
                                             recipientPhone VARCHAR(63) not null ,
                                             country VARCHAR(64)  not null ,
                                             city VARCHAR(64) not null ,
                                             street VARCHAR(63) not null ,
                                             building VARCHAR(63) not null ,
                                             order_state ENUM('CANCELED','REFUNDED','PENDING','PAID','SHIPPING','DELIVERED') not null ,
                                             subTotal long ,
                                             CONSTRAINT fk_order_customer FOREIGN KEY (cust_id)
                                                 REFERENCES customer(cust_id)
                                                 ON DELETE RESTRICT
                                                 ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS order_item (
                                          id bigint auto_increment primary key not null ,
                                          order_id  binary(16) not null ,
                                          product_id_reference bigint ,
                                          name  varchar(64) ,
                                          description varchar(256),
                                          quantity INT NOT NULL CHECK (quantity > 0),
                                          unitPriceInCents bigint not null ,
                                          discountInCents bigint not null,
                                          subTotalInCents bigint  not null ,

                                          CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id)
                                              REFERENCES CustomerOrder(order_id)
                                              ON UPDATE CASCADE
                                              ON DELETE RESTRICT
);

-- PAYMENTS
CREATE TABLE IF NOT EXISTS Payment (
                                       payment_id  binary(16) primary key ,
                                       order_id  binary(16) not null ,
                                       paymentState ENUM('canceled' ,'expired' ,'pending' ,'confirmed','refunded') DEFAULT 'pending' not null,
                                       paymentMethod enum('Stripe'),
                                       transaction_id varchar(255) default null,
                                       session_id varchar(255) default null unique ,
                                       expireAt long not null ,
                                       CONSTRAINT fk_payment_order FOREIGN KEY (order_id)
                                           REFERENCES CustomerOrder(order_id)
                                           ON UPDATE CASCADE
                                           ON DELETE RESTRICT
);

-- ====================================================
-- REVIEWS
-- ====================================================
CREATE TABLE IF NOT EXISTS review (
                                      cust_id  bigint,
                                      product_id  bigint,
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


insert into user(usr_name, usr_email, usr_pass, role, isDeleted, isEmailVerified)
values ('test','test@email.com','12345678' , 'customer',false,true);

insert into customer(cust_id)values (1);
insert into product(title, description, price)values ('laptop' , 'for sale' , 15400);
insert into product_stock (product_id, stock, reservedStock)
values (1,10,0);


DELIMITER $$
create trigger afterCustomerInsert  after insert on customer
    for each row
    begin
        insert into cart(cart_id , subTotalInCents)values (new.cust_id,0);
    end;
DELIMITER ;

drop database E_Commerce;

insert into CustomerOrder (cust_id, recipientName, recipientPhone, country, city, street, building, order_state, subTotal)
values (1,'name','123','aa','aa'
       , '111','112','PENDING',1234,'EGP')
