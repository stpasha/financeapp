CREATE TABLE exchange_info.exchange_operations (
    operation_id uuid PRIMARY KEY,
    target_account_id  uuid NOT NULL,
    source_account_id uuid NOT NULL,
    amount decimal(19,2) NOT NULL,
    created_at timestamp DEFAULT now() NOT NULL,
    operation_type varchar(32) NOT NULL,
    status varchar(16) NOT NULL
);

CREATE INDEX exchange_operations_idx_target_account_id ON exchange_info.exchange_operations(target_account_id);

CREATE INDEX exchange_operations_idx_source_account_id ON exchange_info.exchange_operations(source_account_id);
