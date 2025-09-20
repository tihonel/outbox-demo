CREATE SEQUENCE outbox_id_seq
    START WITH 1
    INCREMENT BY 10
    CACHE 100;

CREATE TABLE outbox
(
    id                 BIGINT DEFAULT nextval('outbox_id_seq') PRIMARY KEY,
    payload            jsonb           NOT NULL,
    status             varchar NOT NULL,
    event_type             varchar NOT NULL,
    time_to_send       timestamp
);