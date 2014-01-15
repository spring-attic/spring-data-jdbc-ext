create table customer(id BIGINT IDENTITY, first_name VARCHAR(255), last_name VARCHAR(255));
create view customer_names as select last_name as name from customer; 
insert into customer(first_name, last_name) values('Thomas', 'Risberg');
insert into customer(first_name, last_name) values('Mark', 'Pollack');
