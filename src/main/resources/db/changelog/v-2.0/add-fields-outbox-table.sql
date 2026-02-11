ALTER TABLE outbox
    ADD COLUMN time_created timestamp,
    ADD COLUMN time_updated timestamp;

UPDATE outbox
SET time_created = outbox.time_to_send
WHERE outbox.time_created IS NULL;