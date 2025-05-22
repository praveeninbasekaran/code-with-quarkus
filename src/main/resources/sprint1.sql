CREATE TABLE role_dropdown_mapping (
    id SERIAL PRIMARY KEY,
    role VARCHAR NOT NULL,
    risk_level1 VARCHAR,
    risk_level2 VARCHAR,
    risk_level3 VARCHAR,
    business_function_l1 VARCHAR,
    business_function_l2 VARCHAR,
    business_function_l3 VARCHAR,
    country VARCHAR,
    legality VARCHAR
);

