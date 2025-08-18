CREATE TABLE fcm_tokens (
    fcm_token_id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES member(member_id) ON DELETE CASCADE,
    device_token VARCHAR(255) NOT NULL,
    device_type VARCHAR(20) NOT NULL,
    device_id VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CHECK (device_type IN ('ANDROID', 'IOS', 'WEB')),
    UNIQUE (member_id, device_id)
);