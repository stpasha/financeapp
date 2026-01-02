CREATE TABLE cash_info.cash_operations (
    operation_id uuid PRIMARY KEY,
    account_id uuid,
    user_id uuid NOT NULL,
    currency_code varchar(3) NOT NULL,
    operation_type varchar(32) NOT NULL,
    amount decimal(19,2) NOT NULL,
    created_at timestamp DEFAULT now() NOT NULL,
    status varchar(16) NOT NULL
);

CREATE INDEX cash_operations_idx_account_id ON cash_info.cash_operations(account_id);

