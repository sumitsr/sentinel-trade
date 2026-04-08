-- Enable pgcrypto for UUIDv7 support
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Generate a UUIDv7 (timestamp-ordered UUID, RFC 9562)
CREATE OR REPLACE FUNCTION generate_uuidv7()
RETURNS UUID
LANGUAGE plpgsql
AS $$
DECLARE
    unix_ts_ms  BIGINT;
    rand_a      BYTEA;
    rand_b      BYTEA;
    msb         BIGINT;
    lsb         BIGINT;
    hex_str     TEXT;
BEGIN
    unix_ts_ms := (EXTRACT(EPOCH FROM CLOCK_TIMESTAMP()) * 1000)::BIGINT;
    rand_a     := gen_random_bytes(2);
    rand_b     := gen_random_bytes(8);

    -- MSB: [unix_ts_ms 48 bits][ver 4 bits = 7][rand_a 12 bits]
    msb := (unix_ts_ms << 16)
         | (7::BIGINT << 12)
         | ((get_byte(rand_a, 0)::BIGINT << 4) | (get_byte(rand_a, 1)::BIGINT >> 4));

    -- LSB: [variant 2 bits = 10][rand_b 62 bits]
    lsb := (2::BIGINT << 62)
         | ((get_byte(rand_b, 0)::BIGINT & x'3F'::INT) << 56)
         | (get_byte(rand_b, 1)::BIGINT << 48)
         | (get_byte(rand_b, 2)::BIGINT << 40)
         | (get_byte(rand_b, 3)::BIGINT << 32)
         | (get_byte(rand_b, 4)::BIGINT << 24)
         | (get_byte(rand_b, 5)::BIGINT << 16)
         | (get_byte(rand_b, 6)::BIGINT << 8)
         | get_byte(rand_b, 7)::BIGINT;

    hex_str := lpad(to_hex(msb), 16, '0') || lpad(to_hex(lsb), 16, '0');
    RETURN (
        substring(hex_str, 1, 8)  || '-' ||
        substring(hex_str, 9, 4)  || '-' ||
        substring(hex_str, 13, 4) || '-' ||
        substring(hex_str, 17, 4) || '-' ||
        substring(hex_str, 21, 12)
    )::UUID;
END;
$$;

-- BOLA protection: RLS policies must be applied per deployment to ensure
-- users can only access rows matching their own account_id.
-- Example: CREATE POLICY trades_account_isolation ON trades
--   USING (account_id = current_setting('app.current_account_id'));
CREATE TABLE IF NOT EXISTS trades (
    id            UUID         PRIMARY KEY DEFAULT generate_uuidv7(),
    account_id    VARCHAR(50)  NOT NULL,
    instrument_id VARCHAR(50)  NOT NULL,
    quantity      NUMERIC(20,8) NOT NULL,
    price         NUMERIC(20,8) NOT NULL,
    type          VARCHAR(10)  NOT NULL CHECK (type IN ('BUY', 'SELL')),
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    executed_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_trades_account_id  ON trades (account_id);
CREATE INDEX IF NOT EXISTS idx_trades_status      ON trades (status);
CREATE INDEX IF NOT EXISTS idx_trades_executed_at ON trades (executed_at DESC);
