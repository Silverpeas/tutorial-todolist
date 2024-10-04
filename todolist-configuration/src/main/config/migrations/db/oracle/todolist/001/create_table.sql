CREATE TABLE SC_Todo (
  id             VARCHAR(40) PRIMARY KEY,
  instanceId     VARCHAR(30)   NOT NULL,
  createDate     TIMESTAMP     NOT NULL,
  createdBy      VARCHAR(40)   NOT NULL,
  lastUpdateDate TIMESTAMP     NOT NULL,
  lastUpdatedBy  VARCHAR(40)   NOT NULL,
  version        NUMBER(19, 0) NOT NULL,
  description    NVARCHAR2     NOT NULL,
  title          NVARCHAR2     NOT NULL,
);

CREATE INDEX idx_Todo ON SC_Todo (instanceId, id);