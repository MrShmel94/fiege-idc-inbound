CREATE SCHEMA IF NOT EXISTS unloading;

CREATE TABLE IF NOT EXISTS unloading.delivery_type(
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE,
    user_id INT REFERENCES vision_idc.users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS unloading.status(
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE,
    user_id INT REFERENCES vision_idc.users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS unloading.product_type(
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE,
    user_id INT REFERENCES vision_idc.users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS unloading.supplier_type(
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE,
    user_id INT REFERENCES vision_idc.users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS unloading.pallet_exchange(
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE,
    user_id INT REFERENCES vision_idc.users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS unloading.process_type(
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE,
    user_id INT REFERENCES vision_idc.users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS unloading.bram(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) UNIQUE NOT NULL,
    status VARCHAR(128) NOT NULL,
    max_buffer INT DEFAULT 1,
    actual_buffer INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS unloading.bram_history(
    history_id BIGSERIAL PRIMARY KEY,
    original_id INT,
    name VARCHAR(64),
    status VARCHAR(128),
    max_buffer INT,
    actual_buffer INT,
    user_id INT ,
    operation VARCHAR(10),
    changed_at TIMESTAMP DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION unloading.bram_history_audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO unloading.bram_history (
            original_id, name, status, max_buffer, actual_buffer, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.name, NEW.status, NEW.max_buffer, NEW.actual_buffer,
            COALESCE(current_setting('app.current_user_id', true)::int, -1),
            'INSERT',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE' AND NEW IS DISTINCT FROM OLD) THEN
        INSERT INTO unloading.bram_history (
            original_id, name, status, max_buffer, actual_buffer, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.name, NEW.status, NEW.max_buffer, NEW.actual_buffer,
            COALESCE(current_setting('app.current_user_id', true)::int, -1),
            'UPDATE',
            NOW()
        );
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER bram_history_audit
AFTER INSERT OR UPDATE
ON unloading.bram
FOR EACH ROW
EXECUTE FUNCTION unloading.bram_history_audit_trigger();

CREATE TABLE IF NOT EXISTS unloading.ramp(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) UNIQUE NOT NULL,
    status VARCHAR(128) NOT NULL,
    max_buffer INT DEFAULT 1,
    actual_buffer INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS unloading.ramp_history(
    history_id BIGSERIAL PRIMARY KEY,
    original_id INT,
    name VARCHAR(64),
    status VARCHAR(128),
    max_buffer INT DEFAULT 1,
    actual_buffer INT DEFAULT 0,
    user_id INT ,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE OR REPLACE FUNCTION unloading.ramp_history_audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO unloading.ramp_history (
            original_id, name, status, max_buffer, actual_buffer, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.name, NEW.status, NEW.max_buffer, NEW.actual_buffer,
            COALESCE(current_setting('app.current_user_id', true)::int, -1),
            'INSERT',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE' AND NEW IS DISTINCT FROM OLD) THEN
        INSERT INTO unloading.ramp_history (
            original_id, name, status, max_buffer, actual_buffer, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.name, NEW.status, NEW.max_buffer, NEW.actual_buffer,
            COALESCE(current_setting('app.current_user_id', true)::int, -1),
            'UPDATE',
            NOW()
        );
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER ramp_history_audit
AFTER INSERT OR UPDATE
ON unloading.ramp
FOR EACH ROW
EXECUTE FUNCTION unloading.ramp_history_audit_trigger();

CREATE TABLE IF NOT EXISTS unloading.type_error(
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    user_id INT REFERENCES vision_idc.users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS unloading.booking(
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    ramp_id INT REFERENCES unloading.ramp(id),
    bram_id INT REFERENCES unloading.bram(id),
    status_id INT REFERENCES unloading.status(id),
    delivery_type_id INT REFERENCES unloading.delivery_type(id) NOT NULL,
    qty_pal INT,
    qty_boxes INT,
    qty_items INT,
    estimated_arrival_time TIME NOT NULL ,
    arrival_time TIME,
    notification_number VARCHAR(128) NOT NULL,
    booking_id VARCHAR(128) NOT NULL,
    product_type_id INT NOT NULL REFERENCES unloading.product_type(id),
    actual_coli INT,
    actual_eu_pal INT,
    actual_eu_pal_defect INT,
    actual_oneway_pal INT,
    process_type_id INT REFERENCES unloading.process_type(id),
    supplier_type_id INT REFERENCES unloading.supplier_type(id),
    pallet_exchange_id INT REFERENCES unloading.pallet_exchange(id) NOT NULL ,
    comments TEXT,
    is_behind_the_gate BOOLEAN DEFAULT false,
    is_in_the_yard BOOLEAN DEFAULT false,
    is_at_the_yard BOOLEAN DEFAULT false,
    type_error_id INT REFERENCES unloading.type_error,
    start_time TIME,
    finish_time TIME,
    who_processing INT REFERENCES vision_idc.users(id),
    UNIQUE (date, notification_number, booking_id)
);

CREATE TABLE IF NOT EXISTS unloading.booking_history(
    history_id BIGSERIAL PRIMARY KEY,
    original_id BIGINT ,
    date DATE,
    ramp_id INT REFERENCES unloading.ramp(id),
    bram_id INT REFERENCES unloading.bram(id),
    status_id INT REFERENCES unloading.status(id) ,
    delivery_type_id INT REFERENCES unloading.delivery_type(id),
    qty_pal INT,
    qty_boxes INT,
    qty_items INT,
    estimated_arrival_time TIME,
    arrival_time TIME,
    notification_number VARCHAR(128),
    booking_id VARCHAR(128),
    product_type_id INT REFERENCES unloading.product_type(id),
    actual_coli INT,
    actual_eu_pal INT,
    actual_eu_pal_defect INT,
    actual_oneway_pal INT,
    process_type_id INT REFERENCES unloading.process_type(id),
    supplier_type_id INT REFERENCES unloading.supplier_type(id),
    pallet_exchange_id INT REFERENCES unloading.pallet_exchange(id),
    comments TEXT,
    is_behind_the_gate BOOLEAN DEFAULT false,
    is_in_the_yard BOOLEAN DEFAULT false,
    is_at_the_yard BOOLEAN DEFAULT false,
    type_error_id INT,
    start_time TIME,
    finish_time TIME,
    who_processing INT,
    user_id INT NOT NULL ,
    operation VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE OR REPLACE FUNCTION unloading.booking_history_audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO unloading.booking_history (
            original_id, date, ramp_id, bram_id, status_id, delivery_type_id, qty_pal, qty_boxes, qty_items, estimated_arrival_time, arrival_time, notification_number, booking_id, product_type_id, actual_coli, actual_eu_pal,
                                               actual_eu_pal_defect, actual_oneway_pal, process_type_id, supplier_type_id, pallet_exchange_id, comments, is_behind_the_gate, is_in_the_yard, is_at_the_yard, type_error_id, start_time, finish_time, who_processing, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.date, NEW.ramp_id, NEW.bram_id, NEW.status_id, NEW.delivery_type_id,
            NEW.qty_pal, NEW.qty_boxes, NEW.qty_items, NEW.estimated_arrival_time, NEW.arrival_time, NEW.notification_number,
            NEW.booking_id, NEW.product_type_id, NEW.actual_coli, NEW.actual_eu_pal, NEW.actual_eu_pal_defect, NEW.actual_oneway_pal,
            NEW.process_type_id, NEW.supplier_type_id, NEW.pallet_exchange_id, NEW.comments, NEW.is_behind_the_gate, NEW.is_in_the_yard, NEW.is_at_the_yard,
            NEW.type_error_id, NEW.start_time, NEW.finish_time, NEW.who_processing,
            COALESCE(current_setting('app.current_user_id', true)::int , -1),
            'INSERT',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE' AND NEW IS DISTINCT FROM OLD) THEN
        INSERT INTO unloading.booking_history (
            original_id, date, ramp_id, bram_id, status_id, delivery_type_id, qty_pal, qty_boxes, qty_items, estimated_arrival_time, arrival_time, notification_number, booking_id, product_type_id, actual_coli, actual_eu_pal,
                                                actual_eu_pal_defect, actual_oneway_pal, process_type_id, supplier_type_id, pallet_exchange_id, comments, is_behind_the_gate, is_in_the_yard, is_at_the_yard, type_error_id, start_time, finish_time, who_processing, user_id, operation, changed_at
        ) VALUES (
            NEW.id, NEW.date, NEW.ramp_id, NEW.bram_id, NEW.status_id, NEW.delivery_type_id,
            NEW.qty_pal, NEW.qty_boxes, NEW.qty_items, NEW.estimated_arrival_time, NEW.arrival_time, NEW.notification_number,
            NEW.booking_id, NEW.product_type_id, NEW.actual_coli, NEW.actual_eu_pal, NEW.actual_eu_pal_defect, NEW.actual_oneway_pal,
            NEW.process_type_id, NEW.supplier_type_id, NEW.pallet_exchange_id, NEW.comments, NEW.is_behind_the_gate, NEW.is_in_the_yard, NEW.is_at_the_yard,
            NEW.type_error_id, NEW.start_time, NEW.finish_time, NEW.who_processing,
            COALESCE(current_setting('app.current_user_id', true)::int , -1),
            'UPDATE',
            NOW()
        );
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO unloading.booking_history (
            original_id, date, ramp_id, bram_id, status_id, delivery_type_id, qty_pal, qty_boxes, qty_items, estimated_arrival_time, arrival_time, notification_number, booking_id, product_type_id, actual_coli, actual_eu_pal,
            actual_eu_pal_defect, actual_oneway_pal, process_type_id, supplier_type_id, pallet_exchange_id, comments, is_behind_the_gate, is_in_the_yard, is_at_the_yard, type_error_id, start_time, finish_time, who_processing, user_id, operation, changed_at
        ) VALUES (
                             OLD.id, OLD.date, OLD.ramp_id, OLD.bram_id, OLD.status_id, OLD.delivery_type_id,
                             OLD.qty_pal, OLD.qty_boxes, OLD.qty_items, OLD.estimated_arrival_time, OLD.arrival_time, OLD.notification_number,
                             OLD.booking_id, OLD.product_type_id, OLD.actual_coli, OLD.actual_eu_pal, OLD.actual_eu_pal_defect, OLD.actual_oneway_pal,
                             OLD.process_type_id, OLD.supplier_type_id, OLD.pallet_exchange_id, OLD.comments, OLD.is_behind_the_gate, OLD.is_in_the_yard, OLD.is_at_the_yard,
                             OLD.type_error_id, OLD.start_time, OLD.finish_time, OLD.who_processing,
                     COALESCE(current_setting('app.current_user_id', true)::int , -1),
                     'DELETE',
                     NOW()
                 );
        RETURN OLD;
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER booking_history_audit
AFTER INSERT OR UPDATE OR DELETE
ON unloading.booking
FOR EACH ROW
EXECUTE FUNCTION unloading.booking_history_audit_trigger();