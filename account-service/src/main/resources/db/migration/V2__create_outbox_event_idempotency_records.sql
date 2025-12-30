
CREATE TABLE account_info.outbox_events (
    outbox_id uuid PRIMARY KEY NOT NULL,
    aggregate_type varchar(255) NOT NULL,
    account_id uuid,
    aggregate_id uuid,
    operation_type varchar(32) NOT NULL,
    payload text,
    status varchar(16) NOT NULL,
    retry_count integer DEFAULT 0,
    last_attempt_at timestamp,
    next_attempt_at timestamp,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL
);

CREATE TABLE account_info.idempotency_records (
    outbox_id uuid PRIMARY KEY NOT NULL,
    created_at timestamp NOT NULL,
    expire_at timestamp
);

CREATE INDEX idx_outbox_status ON account_info.outbox_events(status);
CREATE INDEX idx_outbox_retryable ON account_info.outbox_events(status, retry_count, next_attempt_at);
CREATE INDEX idx_idempotency_created_at ON account_info.idempotency_records(created_at);
