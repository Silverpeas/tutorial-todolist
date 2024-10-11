INSERT INTO uniqueid (maxid, tablename)
VALUES ('1', 'st_space'),
       ('42', 'st_componentinstance'),
       ('2', 'st_user'),
       ('2', 'st_userrole');

INSERT INTO st_space (id, domainFatherId, name, lang, firstPageType, isInheritanceBlocked)
VALUES (1, NULL, 'Collaborative Workspace', 'en', 0, 0);

INSERT INTO st_componentinstance (id, spaceId, name, componentName, isPublic, isInheritanceBlocked)
VALUES (42, 1, 'Collaborative Todolist', 'todolist', 0, 0);

INSERT INTO st_user (id, domainId, specificId, firstName, lastName, login, accessLevel,
                     state, stateSaveDate, notifManualReceiverLimit)
VALUES (1, 0, '1', 'Toto', 'Chez-les-Papoos', 'toto', 'U', 'VALID', '2012-01-01 00:00:00.000', 0),
       (2, 0, '2', 'Gustave', 'Eiffel', 'gustave', 'U', 'VALID', '2012-01-01 00:00:00.000', 0);

INSERT INTO st_userrole (id, instanceId, roleName, name, description, isInherited)
VALUES (0, 42, 'admin', '', '', 0),
       (1, 42, 'writer', '', '', 0),
       (2, 42, 'user', '', '', 0);

INSERT INTO st_userrole_user_rel (userroleId, userId)
VALUES (0, 0),
       (1, 1),
       (2, 2);

INSERT INTO SC_Todo (id, instanceId, title, description,
                     createDate, createdBy, lastUpdateDate, lastUpdatedBy, version)
VALUES ('UUID-1', 'todolist42', 'Todo 1', 'Do my stuff 1',
        '2016-07-29T16:50:00Z', '1', '2016-07-29T16:50:00Z', '1', 0),
       ('UUID-2', 'todolist42', 'Todo 2', 'Do my stuff 2',
        '2016-07-29T16:50:00Z', '1', '2016-07-29T16:50:00Z', '1', 0),
       ('UUID-3', 'todolist42', 'Todo 3', 'Do my stuff 3',
        '2016-07-29T16:50:00Z', '1', '2016-07-29T16:50:00Z', '1', 0),
       ('UUID-4', 'todolist42', 'Todo 4', 'Do my stuff 4',
        '2016-07-29T16:50:00Z', '1', '2016-07-29T16:50:00Z', '1', 0);
