
DELIMITER $$

DROP PROCEDURE IF EXISTS populate_products$$

CREATE PROCEDURE populate_products()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 10000 DO
        INSERT INTO product (title, description, price)
        VALUES (
            CONCAT(
                ELT(FLOOR(1 + RAND() * 6), 'Laptop','Phone','Tablet','Monitor','Keyboard','Mouse'),
                ' Model ', i
            ),
            CONCAT('This is a description for product ', i),
            ROUND(RAND() * 1000000000 + 5000, 2)  -- price between 5000 and 1000000000
        );
        SET i = i + 1;
END WHILE;
END$$

DELIMITER ;

-- Call the procedure to populate products
CALL populate_products();

-- =====================================
-- 2. Product stock table
-- =====================================
DELIMITER $$

DROP PROCEDURE IF EXISTS populate_product_inventory$$

CREATE PROCEDURE populate_product_inventory()
BEGIN

insert ignore into  inventory(id,totalQuantity)  select  p.product_id , FLOOR(RAND() * 1000 + 1) from product p ;

END$$

DELIMITER ;

-- Call the procedure to populate product_stock
CALL populate_product_inventory();

-- add electronics category
insert into category(cate_id,name) values(1,'electronics');


DROP PROCEDURE IF EXISTS addCategoryToAllProducts;
DELIMITER $$

CREATE PROCEDURE addCategoryToAllProducts()
BEGIN
    -- Insert category_id = 1 for all products that don't already have it
    INSERT INTO product_category (product_id, category_id)
    SELECT p.product_id, 1
    FROM product p
    WHERE NOT EXISTS (
        SELECT 1
        FROM product_category pc
        WHERE pc.product_id = p.product_id
          AND pc.category_id = 1
    );
END $$

DELIMITER ;

call addCategoryToAllProducts();

