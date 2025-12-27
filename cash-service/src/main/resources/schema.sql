/*
CREATE SCHEMA IF NOT EXISTS account_info;


CREATE SEQUENCE user_id_seq START 1 OWNED BY account_info.users.user_id;

CREATE TABLE IF NOT EXISTS account_info.users  (
    user_id integer PRIMARY KEY DEFAULT nextval('account_info.user_id_seq'),
    user_name varchar (255) UNIQUE,
    password varchar(255) NOT NULL,
    full_name varchar(255) NOT NULL,
    dob timestamp
);



CREATE SEQUENCE account_id_seq START 1 OWNED BY account_info.accounts.account_id;

CREATE TABLE IF NOT EXISTS  account_info.accounts  (
    account_id integer PRIMARY KEY DEFAULT nextval('account_info.account_id_seq'),
    user_id integer NOT NULL,
    balance decimal(12,2) NOT NULL,
    currency_code varchar(3) NOT NULL,
    is_active boolean DEFAULT false,
    FOREIGN KEY (user_id) REFERENCES account_info.users (user_id)
);

CREATE INDEX accounts_idx_user_id ON account_info.accounts(user_id);




CREATE SCHEMA IF NOT EXISTS cash_info;

CREATE SEQUENCE cash_operations_id_seq START 1 OWNED BY cash_info.cash_operations.operation_id;

CREATE TABLE IF NOT EXISTS cash_info.cash_operations (
    operation_id integer PRIMARY KEY DEFAULT nextval('cash_info.cash_operations_id_seq'),
    account_id integer NOT NULL,
    currency_code varchar(3) NOT NULL,
    operation_type integer NOT NULL,
    amount decimal(12,2) NOT NULL,
    created_at timestamp,
    status_id integer NOT NULL
);



CREATE INDEX cash_operations_idx_account_id ON cash_info.cash_operations(account_id);





CREATE SCHEMA IF NOT EXISTS exchange_info;


CREATE SEQUENCE exchange_operations_id_seq START 1 OWNED BY exchange_info.exchange_operations.operation_id;

CREATE TABLE IF NOT EXISTS exchange_info.exchange_operations (
    operation_id integer PRIMARY KEY DEFAULT nextval('exchange_info.exchange_operations_id_seq'),
    target_account_id integer NOT NULL,
    source_account_id integer NOT NULL,
    amount decimal(12,2) NOT NULL,
    created_at timestamp,
    operation_type integer NOT NULL
);


CREATE INDEX exchange_operations_idx_target_account_id ON exchange_info.exchange_operations(target_account_id);
CREATE INDEX exchange_operations_idx_source_account_id ON exchange_info.exchange_operations(source_account_id);


CREATE SCHEMA IF NOT EXISTS rule_info;

CREATE SEQUENCE rule_info.rule_id_seq START 1 OWNED BY rule_info.rules.rule_id;

CREATE TABLE IF NOT EXISTS rule_info.rules (
    rule_id integer PRIMARY KEY DEFAULT nextval('rule_info.rule_id_seq'),
    operation_type integer NOT NULL,
    rule_condition varchar(255) NOT NULL,
    field varchar(100) NOT NULL
);


CREATE INDEX rules_idx_operation_type ON rule_info.rules(operation_type);


CREATE SCHEMA IF NOT EXISTS notification_info;

CREATE SEQUENCE notification_info.notification_id_seq START 1 OWNED BY notification_info.notifications.notification_id;

CREATE TABLE IF NOT EXISTS notification_info.notifications (
    notification_id integer PRIMARY KEY DEFAULT nextval('notification_info.notification_id_seq'),
    operation_type integer NOT NULL,
    notification_description varchar(255),
    created_at timestamp
);

CREATE INDEX notifications_idx_operation_type ON notification_info.notifications(operation_type);
*/