create table customer(id BIGINT IDENTITY PRIMARY KEY, name VARCHAR(255));
CREATE TABLE address (id BIGINT IDENTITY PRIMARY KEY, customer_id BIGINT CONSTRAINT address_customer_ref FOREIGN KEY REFERENCES customer (id), street VARCHAR(255), city VARCHAR(255));
insert into customer(id, name) values(1, 'Thomas');
insert into customer(id, name) values(2, 'Mark');
insert into customer(id, name) values(3, 'Oliver');
INSERT INTO address(customer_id, street, city) VALUES(1, '6 Main St', 'Newtown');
INSERT INTO address(customer_id, street, city) VALUES(1, '128 N. South St', 'Middletown');
INSERT INTO address(customer_id, street, city) VALUES(2, '512 North St', 'London');
ALTER TABLE customer ALTER COLUMN id RESTART WITH 20;
