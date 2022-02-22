CREATE TABLE users
(
    ID   integer auto_increment not null,
    LAST_NAME varchar(255) not null,
    FIRST_NAME varchar(255) not null,
    EMAIL varchar(255) not null unique,
    PASSWORD varchar(255) not null,
    primary key (ID)
);

CREATE TABLE addresses
(
    ID     integer auto_increment not null,
    ADDRESS_1   varchar(255)   not null,
    ADDRESS_2   varchar(255)   null,
    CITY varchar(255) not null,
    STATE varchar(255) not null,
    COUNTRY varchar(255) not null,
    ZIP varchar(255) not null,
    primary key (ID)
);

CREATE TABLE user_addresses
(
    ID  integer auto_increment not null,
    USER_ID integer not null,
    ADDRESS_ID integer not null,
    primary key (ID)
);

ALTER TABLE user_addresses
ADD CONSTRAINT fk_user_id FOREIGN KEY (USER_ID) REFERENCES users (ID);

ALTER TABLE user_addresses
ADD CONSTRAINT fk_address_id FOREIGN KEY (ADDRESS_ID) REFERENCES addresses (ID);