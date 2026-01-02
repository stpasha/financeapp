CREATE TABLE rule_info.rules (
    rule_id uuid PRIMARY KEY,
    currency_code varchar(3) NOT NULL,
    operation_type varchar(32) NOT NULL,
    min_amount decimal(19,2) NOT NULL,
    max_amount decimal(19,2) NOT NULL
);

CREATE INDEX rules_idx_operation_type ON rule_info.rules(operation_type);
CREATE INDEX rules_idx_currency_code ON rule_info.rules(currency_code);
CREATE UNIQUE INDEX rules_uq_operation_currency ON rule_info.rules(operation_type, currency_code);


