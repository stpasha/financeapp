CREATE TABLE notification_info.notifications (
    notification_id uuid PRIMARY KEY,
    operation_type varchar(32) NOT NULL,
    user_id uuid NOT NULL,
    notification_description varchar(255),
    created_at timestamp NOT NULL DEFAULT now(),
    delivered boolean DEFAULT false NOT NULL
);

CREATE INDEX notifications_idx_operation_type ON notification_info.notifications(operation_type);