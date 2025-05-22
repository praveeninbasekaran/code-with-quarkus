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

-- Role: Risk level 1 framework owner → Only Risk Level 1 is applicable
INSERT INTO role_dropdown_mapping (role, risk_level1)
VALUES
('Risk level 1 framework owner', 'Financial Crime'),
('Risk level 1 framework owner', 'Compliance'),
('Risk level 1 framework owner', 'Operations');

-- Role: Risk level 2 framework owner → Risk Level 1 and 2
INSERT INTO role_dropdown_mapping (role, risk_level1, risk_level2)
VALUES
('Risk level 2 framework owner', 'Financial Crime', 'Sanctions'),
('Risk level 2 framework owner', 'Financial Crime', 'Transaction failure');

-- Role: Business function L1 (ILOD) → Business Function L1, L2, Country
INSERT INTO role_dropdown_mapping (role, business_function_l1, business_function_l2, country)
VALUES
('Business function L1 (ILOD)', 'Investment banking', 'Financial Market', 'Srilanka'),
('Business function L1 (ILOD)', 'Investment banking', 'Financial Market', 'India');

-- Role: Country Risk owner → Business Function L1, L2, Country
INSERT INTO role_dropdown_mapping (role, business_function_l1, business_function_l2, country)
VALUES
('Country Risk owner', 'Investment banking', 'Financial Market', 'Srilanka'),
('Country Risk owner', 'Investment banking', 'Financial Market', 'India');