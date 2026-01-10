
CREATE TABLE account_info.users (
    user_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_id uuid,
    user_name varchar(255) NOT NULL,
    full_name varchar(255) NOT NULL,
    dob timestamp,
    is_enabled boolean NOT NULL,
    created_at timestamp NOT NULL DEFAULT now(),
    updated_at timestamp NOT NULL
);

CREATE TABLE account_info.accounts (
    account_id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    CONSTRAINT fk_accounts_user
    FOREIGN KEY (user_id)
    REFERENCES account_info.users(user_id),
    balance decimal(19,2) NOT NULL,
    currency_code varchar(3) NOT NULL,
    is_active boolean NOT NULL DEFAULT false,
    created_at timestamp NOT NULL DEFAULT now(),
    updated_at timestamp NOT NULL
);

CREATE INDEX accounts_idx_user_id ON account_info.accounts(user_id);
CREATE UNIQUE INDEX ux_users_keycloak_id ON account_info.users(keycloak_id);
CREATE UNIQUE INDEX ux_users_user_name ON account_info.users(user_name);
CREATE UNIQUE INDEX ux_active_account ON account_info.accounts(user_id, currency_code) WHERE is_active = true;






