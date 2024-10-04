CREATE TABLE SC_Todo (
  id             VARCHAR(40) PRIMARY KEY,
  instanceId     VARCHAR(30) NOT NULL,
  createDate     TIMESTAMP   NOT NULL,
  createdBy      VARCHAR(40) NOT NULL,
  lastUpdateDate TIMESTAMP   NOT NULL,
  lastUpdatedBy  VARCHAR(40) NOT NULL,
  version        INT8        NOT NULL,
  description    VARCHAR     NOT NULL,
  title          VARCHAR     NOT NULL
);

CREATE INDEX idx_Todo ON SC_Todo (instanceId, id);