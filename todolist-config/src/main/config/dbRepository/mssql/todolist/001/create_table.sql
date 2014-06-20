CREATE TABLE SC_Todo (
  id             VARCHAR(40) PRIMARY KEY,
  instanceId     VARCHAR(30) NOT NULL,
  createDate     DATETIME    NOT NULL,
  createdBy      VARCHAR(40) NOT NULL,
  lastUpdateDate DATETIME    NOT NULL,
  lastUpdatedBy  VARCHAR(40) NOT NULL,
  version        BIGINT      NOT NULL,
  description    VARCHAR(1000) NOT NULL
);