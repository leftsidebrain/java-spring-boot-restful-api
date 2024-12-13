SHOW DATABASES;

CREATE DATABASE belajar_spring_restful_api;

USE belajar_spring_restful_api;


CREATE TABLE users
(
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    token VARCHAR(100),
    token_expired_at BIGINT,
    PRIMARY KEY (username),
    UNIQUE (token)
) ENGINE InnoDb;

select * from users;

DESC users;





create table contacts
(
    id VARCHAR(100) NOT NULL ,
    username VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) ,
    phone VARCHAR(100) ,
    email VARCHAR(100) ,
    PRIMARY KEY (id),
    FOREIGN KEY fk_users_contacts (username) REFERENCES users(username)
) ENGINE InnoDb;


select * from contacts;

DESC contacts;

DROP TABLE addresses;

create table addresses
(
    id VARCHAR(100) NOT NULL ,
    contact_id VARCHAR(100) NOT NULL,
    street VARCHAR(200) ,
    city VARCHAR(100) ,
    province VARCHAR(100) ,
    country VARCHAR(100) ,
    postal_code VARCHAR(100) ,
    PRIMARY KEY (id),
    FOREIGN KEY fk_contacs_addresses (contact_id) REFERENCES contacts(id)
) ENGINE InnoDb;

select * from addresses;

DESC adresses;

DELETE FROM users;
DELETE FROM contacts;
DELETE FROM addresses;