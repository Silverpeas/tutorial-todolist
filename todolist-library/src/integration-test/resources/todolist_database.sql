// In which are stored the Silverpeas application instances
CREATE TABLE IF NOT EXISTS ST_ComponentInstance
(
    id                   int             NOT NULL,
    spaceId              int             NOT NULL,
    name                 varchar(100)    NOT NULL,
    componentName        varchar(100)    NOT NULL,
    description          varchar(400),
    createdBy            int,
    orderNum             int DEFAULT (0) NOT NULL,
    createTime           varchar(20),
    updateTime           varchar(20),
    removeTime           varchar(20),
    componentStatus      char(1),
    updatedBy            int,
    removedBy            int,
    isPublic             int DEFAULT (0) NOT NULL,
    isHidden             int DEFAULT (0) NOT NULL,
    lang                 char(2),
    isInheritanceBlocked int default (0) NOT NULL
);

// translations of the application instance's attributes
CREATE TABLE IF NOT EXISTS ST_ComponentInstanceI18N
(
    id          int          NOT NULL,
    componentId int          NOT NULL,
    lang        char(2)      NOT NULL,
    name        varchar(100) NOT NULL,
    description varchar(400)
);

// the access level of the users
CREATE TABLE IF NOT EXISTS ST_AccessLevel
(
    id   CHAR(1)      NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT PK_AccessLevel PRIMARY KEY (id),
    CONSTRAINT UN_AccessLevel_1 UNIQUE (name)
);

// the subscriptions of the users to changes in the contributions handled in an application
// instance
CREATE TABLE subscribe
(
    subscriberId       VARCHAR(100) NOT NULL,
    subscriberType     VARCHAR(50)  NOT NULL,
    subscriptionMethod VARCHAR(50)  NOT NULL,
    resourceId         VARCHAR(100) NOT NULL,
    resourceType       VARCHAR(50)  NOT NULL,
    space              VARCHAR(50)  NOT NULL,
    instanceId         VARCHAR(50)  NOT NULL,
    creatorId          VARCHAR(100) NOT NULL,
    creationDate       TIMESTAMP    NOT NULL
);

// the users in Silverpeas, whatever their user domain
CREATE TABLE IF NOT EXISTS ST_User
(
    id                            INT                   NOT NULL,
    domainId                      INT                   NOT NULL,
    specificId                    VARCHAR(500)          NOT NULL,
    firstName                     VARCHAR(100),
    lastName                      VARCHAR(100)          NOT NULL,
    email                         VARCHAR(100),
    login                         VARCHAR(50)           NOT NULL,
    loginMail                     VARCHAR(100),
    accessLevel                   CHAR(1) DEFAULT 'U'   NOT NULL,
    loginquestion                 VARCHAR(200),
    loginanswer                   VARCHAR(200),
    creationDate                  TIMESTAMP,
    saveDate                      TIMESTAMP,
    version                       INT     DEFAULT 0     NOT NULL,
    tosAcceptanceDate             TIMESTAMP,
    lastLoginDate                 TIMESTAMP,
    nbSuccessfulLoginAttempts     INT     DEFAULT 0     NOT NULL,
    lastLoginCredentialUpdateDate TIMESTAMP,
    expirationDate                TIMESTAMP,
    state                         VARCHAR(30)           NOT NULL,
    stateSaveDate                 TIMESTAMP             NOT NULL,
    notifManualReceiverLimit      INT,
    sensitiveData                 BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT PK_User PRIMARY KEY (id),
    CONSTRAINT UN_User_1 UNIQUE (specificId, domainId),
    CONSTRAINT UN_User_2 UNIQUE (login, domainId),
    CONSTRAINT FK_User_1 FOREIGN KEY (accessLevel) REFERENCES ST_AccessLevel (id)
);

// tables used by this application
CREATE TABLE IF NOT EXISTS SC_Todo
(
    id             VARCHAR(40) PRIMARY KEY,
    instanceId     VARCHAR(30) NOT NULL,
    createDate     TIMESTAMP   NOT NULL,
    createdBy      VARCHAR(40) NOT NULL,
    lastUpdateDate TIMESTAMP   NOT NULL,
    lastUpdatedBy  VARCHAR(40) NOT NULL,
    version        INT8        NOT NULL,
    title          VARCHAR     NOT NULL,
    description    VARCHAR     NOT NULL
);

CREATE INDEX idx_Todo ON SC_Todo (instanceId, id);