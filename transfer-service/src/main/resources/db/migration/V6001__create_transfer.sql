
CREATE TABLE transfer_info.transfer_operations (
    operation_id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    target_account_id uuid NOT NULL,
    source_account_id uuid NOT NULL,
    amount decimal(19,2) NOT NULL,
    created_at timestamp NOT NULL DEFAULT now(),
    operation_type varchar(32) NOT NULL,
    status varchar(16) NOT NULL
);

CREATE INDEX transfer_operations_idx_target_account_id ON transfer_info.transfer_operations(target_account_id);
CREATE INDEX transfer_operations_idx_source_account_id ON transfer_info.transfer_operations(source_account_id);
CREATE INDEX transfer_operations_idx_user_id ON transfer_info.transfer_operations(user_id);
