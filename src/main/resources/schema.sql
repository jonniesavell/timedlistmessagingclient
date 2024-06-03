CREATE SCHEMA products;
CREATE SCHEMA appraisals;

CREATE TABLE products.product
(
    product_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_name VARCHAR(256) NOT NULL UNIQUE,
    product_desc VARCHAR(1024) NOT NULL
);

CREATE TABLE appraisals.appraisal
(
    appraisal_id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL,
    correlation_id VARCHAR(1024) NOT NULL,
    appraisal_date TIMESTAMP,
    appraisal_amount BIGINT
);

CREATE INDEX product_name_idx   ON products.product(product_name);
CREATE INDEX product_id_idx     ON appraisals.appraisal(product_id);
CREATE INDEX correlation_id_idx ON appraisals.appraisal(correlation_id);
CREATE INDEX appraisal_date_idx ON appraisals.appraisal(appraisal_date);