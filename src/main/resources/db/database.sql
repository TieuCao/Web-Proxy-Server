--
-- File generated with SQLiteStudio v3.1.1 on Thu Aug 24 08:18:15 2017
--
-- Text encoding used: UTF-8
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: Cookie_Blocking
DROP TABLE IF EXISTS Cookie_Blocking;

CREATE TABLE Cookie_Blocking (
    ID           INTEGER  PRIMARY KEY,
    TYPE_BLOCK   BOOLEAN  NOT NULL,
    COOKIE       TEXT     NOT NULL,
    DATE_CREATED DATETIME
);


-- Table: Request
DROP TABLE IF EXISTS Request;

CREATE TABLE Request (
    ID           INTEGER,
    HOSTNAME     TEXT,
    URI          TEXT,
    PORT         INTEGER,
    DATE_CREATED DATETIME,
    PRIMARY KEY (
        ID
    )
);


-- Table: Request_Blocking
DROP TABLE IF EXISTS Request_Blocking;

CREATE TABLE Request_Blocking (
    ID           INTEGER,
    HOSTNAME     TEXT,
    URI          TEXT,
    PORT         INTEGER,
    DATE_CREATED DATETIME,
    PRIMARY KEY (
        ID
    )
);


-- Trigger: TIGGER_AFTER_INSERT_COOKIE_BLOCKING
DROP TRIGGER IF EXISTS TIGGER_AFTER_INSERT_COOKIE_BLOCKING;
CREATE TRIGGER TIGGER_AFTER_INSERT_COOKIE_BLOCKING
         AFTER INSERT
            ON Cookie_Blocking
      FOR EACH ROW
BEGIN
    UPDATE Cookie_Blocking
       SET date_created = datetime('now', 'localtime') 
     WHERE id = new.ID;
END;


-- Trigger: TRIGGER_AFTER_INSERT_REQUEST
DROP TRIGGER IF EXISTS TRIGGER_AFTER_INSERT_REQUEST;
CREATE TRIGGER TRIGGER_AFTER_INSERT_REQUEST
         AFTER INSERT
            ON Request
      FOR EACH ROW
BEGIN
    UPDATE Request
       SET date_created = datetime('now', 'localtime') 
     WHERE id = new.id;
END;


-- Trigger: TRIGGER_AFTER_INSERT_REQUEST_BLOCK
DROP TRIGGER IF EXISTS TRIGGER_AFTER_INSERT_REQUEST_BLOCK;
CREATE TRIGGER TRIGGER_AFTER_INSERT_REQUEST_BLOCK
         AFTER INSERT
            ON Request_Blocking
BEGIN
    UPDATE Request_Blocking
       SET date_created = datetime('now', 'localtime') 
     WHERE id = new.id;
END;


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
