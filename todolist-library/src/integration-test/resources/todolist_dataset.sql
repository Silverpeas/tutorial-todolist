INSERT INTO st_accesslevel (id, name)
VALUES ('U', 'User'),
       ('A', 'Administrator'),
       ('G', 'Guest'),
       ('R', 'Removed'),
       ('K', 'KMManager'),
       ('D', 'DomainManager');

INSERT INTO st_componentinstance (id, spaceId, name, componentName, isInheritanceBlocked)
VALUES (42, 1, 'Collaborative Todolist', 'todolist', 0);

INSERT INTO st_user (id, domainId, specificId, firstName, lastName, login, accessLevel,
                     state, stateSaveDate, notifManualReceiverLimit)
VALUES
    (1, 0, '1', 'Toto', 'Chez-les-Papoos', 'toto', 'U', 'VALID', '2012-01-01 00:00:00.000', 0),
    (2, 0, '2', 'Gustave', 'Eiffel', 'gustave', 'U', 'VALID', '2012-01-01 00:00:00.000', 0);

INSERT INTO SC_Todo (id, instanceId, title, description,
                     createDate, createdBy, lastUpdateDate, lastUpdatedBy, version)
VALUES
    ('UUID-1', 'todolist42', 'Todo 1', 'Do my stuff 1',
     '2016-07-29T16:50:00Z', '1', '2016-07-29T16:50:00Z', '1', 0),
    ('UUID-2', 'todolist42', 'Todo 2', 'Do my stuff 2',
     '2016-07-29T16:50:00Z', '1', '2016-07-29T16:50:00Z', '1', 0),
    ('UUID-3', 'todolist42', 'Todo 3', 'Do my stuff 3',
     '2016-07-29T16:50:00Z', '1', '2016-07-29T16:50:00Z', '1', 0);