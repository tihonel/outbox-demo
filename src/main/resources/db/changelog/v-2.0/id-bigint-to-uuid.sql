ALTER TABLE outbox
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE outbox
    ALTER COLUMN id TYPE uuid USING gen_random_uuid(),
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE user_table
    ADD COLUMN temp_uuid uuid DEFAULT gen_random_uuid();
UPDATE user_table
SET temp_uuid = gen_random_uuid()
WHERE temp_uuid IS NULL;

UPDATE outbox o
SET payload = jsonb_set(
        o.payload,
        '{userId}',
        to_jsonb(u.temp_uuid::text)
              )
FROM user_table u
WHERE (o.payload ->> 'userId')::bigint = u.id;

ALTER TABLE user_table
    DROP COLUMN id;
ALTER TABLE user_table
    RENAME COLUMN temp_uuid TO id;
ALTER TABLE user_table
    ADD PRIMARY KEY (id);