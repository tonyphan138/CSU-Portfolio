

DROP TABLE IF EXISTS  
    written_by,
    borrowed,
    author_phone, 
    publisher_phone,
    located_at,
    book,
    library,
    phone,
    member,
    publisher,
    author,
    audit; 

DROP TRIGGER IF EXISTS Insert_Author;
DROP TRIGGER IF EXISTS Insert_Book;
DROP TRIGGER IF EXISTS Delete_Book;
DROP TRIGGER IF EXISTS Modify_Copies;
DROP VIEW IF EXISTS Book_Info;

CREATE TABLE author (
    author_id   INT             NOT NULL,
    last_name   VARCHAR(16)     NOT NULL,
    first_name  VARCHAR(16)     NOT NULL,

    PRIMARY KEY (author_id)
);

CREATE TABLE phone (
    p_number     VARCHAR(16)            NOT NULL,
    p_type       ENUM('c', 'h', 'o')    NOT NULL,
    PRIMARY KEY (p_number)
);

CREATE TABLE publisher (
    pub_id      INT             NOT NULL,
    pub_name    VARCHAR(40)     NOT NULL,
    PRIMARY KEY (pub_id)
);

CREATE TABLE book (
    isbn            VARCHAR(16)     NOT NULL,
    title           VARCHAR(50)     NOT NULL,
    year_published  DATE            NOT NULL,
    pub_id          INT             NOT NULL,

    FOREIGN KEY (pub_id) REFERENCES publisher (pub_id)    ON DELETE RESTRICT,
    PRIMARY KEY (isbn)
);


CREATE TABLE member (
    member_id       INT             NOT NULL,
    last_name       VARCHAR(16)     NOT NULL,
    first_name      VARCHAR(16)     NOT NULL,
    dob             DATE            NOT NULL,
    gender          ENUM('M', 'F')  NOT NULL,

    PRIMARY KEY (member_id)
);

CREATE TABLE written_by (
    isbn        VARCHAR(16)     NOT NULL,
    author_id   INT             NOT NULL,

    FOREIGN KEY (author_id) REFERENCES author (author_id)   ON DELETE RESTRICT,
    FOREIGN KEY (isbn)      REFERENCES book (isbn)          ON DELETE RESTRICT,
    PRIMARY KEY (isbn, author_id)
);


CREATE TABLE author_phone (
    author_id       INT                 NOT NULL,    
    p_number        VARCHAR(16)         NOT NULL,

    FOREIGN KEY (author_id)      REFERENCES author (author_id)      ON DELETE RESTRICT,
    FOREIGN KEY (p_number)       REFERENCES phone (p_number)        ON DELETE RESTRICT,
    PRIMARY KEY (p_number, author_id)
);

CREATE TABLE publisher_phone (
    pub_id          INT                 NOT NULL,
    p_number        VARCHAR(16)         NOT NULL,

    FOREIGN KEY (pub_id)      REFERENCES publisher (pub_id) ON DELETE RESTRICT,
    FOREIGN KEY (p_number)    REFERENCES phone (p_number)   ON DELETE RESTRICT,
    PRIMARY KEY (p_number, pub_id)
);

CREATE TABLE library (
    lib_name        VARCHAR(16)     NOT NULL,
    lstreet         VARCHAR(32)     NOT NULL,
    lcity           VARCHAR(32)     NOT NULL,
    lstate          VARCHAR(2)      NOT NULL,

    PRIMARY KEY (lib_name)
);

CREATE TABLE located_at (
    lib_name        VARCHAR(16)     NOT NULL,
    isbn            VARCHAR(16)     NOT NULL,
    total_copies    INT             NOT NULL,
    shelf           INT             NOT NULL,
    floor           INT             NOT NULL,
    available       INT             NOT NULL,

    FOREIGN KEY (lib_name)    REFERENCES  library(lib_name)   ON DELETE RESTRICT,
    FOREIGN KEY (isbn)        REFERENCES  book(isbn)          ON DELETE RESTRICT,
    PRIMARY KEY (lib_name, isbn)
);

CREATE TABLE borrowed (
    member_id       INT             NOT NULL,
    isbn            VARCHAR(16)     NOT NULL,
    lib_name        VARCHAR(16)     NOT NULL,
    checkout_date   DATE            NOT NULL,
    checkin_date    DATE,

    FOREIGN KEY (member_id) REFERENCES member (member_id)       ON DELETE RESTRICT,
    FOREIGN KEY (isbn)      REFERENCES located_at (isbn)        ON DELETE RESTRICT,
    FOREIGN KEY (lib_name)  REFERENCES located_at (lib_name)    ON DELETE RESTRICT,

    PRIMARY KEY (member_id, isbn, lib_name, checkout_date)
);

CREATE TABLE audit (
    audit_table      VARCHAR(16)    NOT NULL, 
    audit_action    VARCHAR(16)     NOT NULL,
    audit_time      datetime        NOT NULL,
    audit_key       int(11)         NOT NULL AUTO_INCREMENT,

    PRIMARY KEY (audit_key)
);