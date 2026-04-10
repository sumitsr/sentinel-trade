-- trades table
-- Note: UUIDv7 is generated in Java (@PrePersist on TradeEntity).
-- gen_random_uuid() here is a DB-level safety net only (no extension required).
-- BOLA protection: apply RLS per deployment, e.g.:
--   ALTER TABLE trades ENABLE ROW LEVEL SECURITY;
--   CREATE POLICY account_isolation ON trades USING (account_id = current_setting('app.current_account_id'));

CREATE TABLE IF NOT EXISTS trades (
    id            UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id    VARCHAR(50)   NOT NULL,
    instrument_id VARCHAR(50)   NOT NULL,
    quantity      NUMERIC(20,8) NOT NULL,
    price         NUMERIC(20,8) NOT NULL,
    type          VARCHAR(10)   NOT NULL CHECK (type IN ('BUY', 'SELL')),
    status        VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    executed_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_trades_account_id  ON trades (account_id);
CREATE INDEX IF NOT EXISTS idx_trades_status      ON trades (status);
CREATE INDEX IF NOT EXISTS idx_trades_executed_at ON trades (executed_at DESC);
