CREATE SCHEMA IF NOT EXISTS vision_idc;

CREATE TABLE IF NOT EXISTS vision_idc.department
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE
);

INSERT INTO vision_idc.department(name)
VALUES ('Administration');

CREATE TABLE IF NOT EXISTS vision_idc.position
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE
);

INSERT INTO vision_idc.position(name)
VALUES ('Area Manager');

CREATE TABLE IF NOT EXISTS vision_idc.role(
    id SERIAL PRIMARY KEY,
    name VARCHAR (64) NOT NULL UNIQUE,
    weight INT NOT NULL DEFAULT 1
);

INSERT INTO vision_idc.role(name, weight)
VALUES ('Admin', 999);

CREATE TABLE IF NOT EXISTS vision_idc.users(
    id SERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    second_name VARCHAR(256) NOT NULL,
    expertis VARCHAR(128) NOT NULL UNIQUE,
    login VARCHAR(128) NOT NULL UNIQUE,
    is_first_login BOOLEAN DEFAULT TRUE,
    encrypted_password VARCHAR(512) NOT NULL ,
    position_id INT REFERENCES vision_idc.position(id) NOT NULL,
    department_id INT REFERENCES vision_idc.department(id) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    role_id INT REFERENCES vision_idc.role(id) NOT NULL,
    created_by_user_id INT REFERENCES vision_idc.users(id)
);

CREATE TABLE IF NOT EXISTS vision_idc.users_history(
    audit_id BIGSERIAL PRIMARY KEY,
    original_id INT,
    name VARCHAR(256) NOT NULL,
    second_name VARCHAR(256) NOT NULL,
    expertis VARCHAR(128) NOT NULL UNIQUE,
    login VARCHAR(128) NOT NULL UNIQUE,
    is_first_login BOOLEAN DEFAULT TRUE,
    position_id INT REFERENCES vision_idc.position(id) NOT NULL,
    department_id INT REFERENCES vision_idc.department(id) NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    role_id INT REFERENCES vision_idc.role(id) NOT NULL,
    user_id INT NOT NULL ,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE OR REPLACE FUNCTION vision_idc.users_history_audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO vision_idc.users_history (
            original_id, name, second_name, expertis, login, is_first_login, position_id, department_id, is_active, role_id, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.name, NEW.second_name, NEW.expertis, NEW.login, NEW.is_first_login, NEW.position_id, NEW.department_id, NEW.is_active, NEW.role_id,
            COALESCE(current_setting('app.current_user_id', true), -1),
            'INSERT',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE' AND NEW IS DISTINCT FROM OLD) THEN
        INSERT INTO vision_idc.users_history (
            original_id, name, second_name, expertis, login, is_first_login, position_id, department_id, is_active, role_id, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.name, NEW.second_name, NEW.expertis, NEW.login, NEW.is_first_login, NEW.position_id, NEW.department_id, NEW.is_active, NEW.role_id,
            COALESCE(current_setting('app.current_user_id', true), -1),
            'UPDATE',
            NOW()
        );
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER users_history_audit
AFTER INSERT OR UPDATE
ON vision_idc.users
FOR EACH ROW
EXECUTE FUNCTION vision_idc.users_history_audit_trigger();

CREATE TABLE vision_idc.login_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES vision_idc.users(id),
    login_time TIMESTAMP DEFAULT NOW(),
    logout_time TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    success BOOLEAN DEFAULT TRUE
);

ALTER TABLE vision_idc.department
    ADD COLUMN user_id INT,
    ADD CONSTRAINT fk_department_user FOREIGN KEY (user_id) REFERENCES vision_idc.users(id);

ALTER TABLE vision_idc.position
    ADD COLUMN user_id INT,
    ADD CONSTRAINT fk_position_user FOREIGN KEY (user_id) REFERENCES vision_idc.users(id);