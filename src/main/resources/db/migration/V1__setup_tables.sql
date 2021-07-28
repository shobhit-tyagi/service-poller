CREATE TABLE service_details
(
    id           VARCHAR(255)  NOT NULL PRIMARY KEY,
    name         VARCHAR(255)  NOT NULL UNIQUE,
    url          VARCHAR(1000) NOT NULL,
    status       VARCHAR(10)   NOT NULL,
    last_checked TIMESTAMP     NOT NULL,
    created_at   TIMESTAMP     NOT NULL
);