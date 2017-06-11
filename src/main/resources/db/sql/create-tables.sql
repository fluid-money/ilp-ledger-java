CREATE TABLE ACCOUNT
(
    ID VARCHAR(255) PRIMARY KEY NOT NULL,
    AMOUNT DECIMAL(10,2) DEFAULT 0
);

CREATE TABLE ESCROW_ACCOUNT
(
    -- The identifier for this escrow account...
    ID VARCHAR(255) PRIMARY KEY NOT NULL,
    -- The amount escrowed.
    AMOUNT DECIMAL(10,2) DEFAULT 0,
    -- The account identifier for the source of funds
    SOURCE_ACCOUNT VARCHAR(255) NOT NULL, --
    -- The account identifier for the destination of funds if the escrow is executed.
    DESTINATION_ACCOUNT VARCHAR(255) NOT NULL,
);

-- CREATE TABLE PUBLIC.SUBSCRIPTION
-- (
--     SUBSCRIBER_ID VARCHAR(36) NOT NULL,
--     ACCOUNT_ID VARCHAR(36) NOT NULL,
--     RESOURCE VARCHAR(36) NOT NULL,
--     EVENT VARCHAR(36) NOT NULL,
--     CONSTRAINT SUBSCRIPTION__PK PRIMARY KEY (SUBSCRIBER_ID, ACCOUNT_ID)
-- )