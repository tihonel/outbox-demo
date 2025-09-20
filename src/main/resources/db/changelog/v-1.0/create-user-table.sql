CREATE SEQUENCE user_id_seq
    START WITH 1
    INCREMENT BY 10
    CACHE 100;

CREATE TABLE user_table
(
    id                 BIGINT DEFAULT nextval('user_id_seq') PRIMARY KEY,
    username             VARCHAR           NOT NULL
);