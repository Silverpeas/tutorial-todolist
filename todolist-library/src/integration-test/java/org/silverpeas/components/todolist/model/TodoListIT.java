/*
 * Copyright (C) 2000 - 2024 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "https://www.silverpeas.org/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.silverpeas.components.todolist.model;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.silverpeas.components.todolist.TodolistWarBuilder;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.admin.user.model.UserDetail;
import org.silverpeas.core.cache.service.CacheAccessorProvider;
import org.silverpeas.core.persistence.datasource.model.identifier.UuidIdentifier;
import org.silverpeas.core.test.integration.SQLRequester;
import org.silverpeas.core.test.integration.rule.DbSetupRule;
import org.silverpeas.kernel.test.util.Reflections;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.silverpeas.components.todolist.test.FullTodoMatcher.isTodo;
import static org.silverpeas.core.persistence.jdbc.sql.JdbcSqlQuery.select;

/**
 * Integration tests on the {@link TodoList} behaviors. We ensure all the technical and business
 * services used by a {@link TodoList} object work as expected within a Wildfly runtime. The name of
 * the integration tests classes have to end with the <code>IT</code> suffix to be detected as an
 * integration test by the test runtime.
 * <p>
 * Whereas unit tests are executed by a JUnit 5 runtime, the integration tests are executed by a
 * JUnit 4 runtime. This is because the JEE test framework Arquillian isn't yet ready for JUnit 5.
 * </p>
 * <p>
 * To be executed, the integration tests require to have a Wildfly dedicated to testing on the host.
 * This Wildfly has to be located into the directory referred by the Maven property
 * <code>temp.directory</code>. The value of this property has to be set in your
 * <em>.m2/settings.xml</em> Maven configuration file. The starting and the stopping of Wildfly
 * has to be done by yourself. Because all the tests will be executed, one after one, in the same
 * Wildfly execution context, you have to take care of shared objects.
 * </p>
 *
 * @author mmoquillon
 */
@RunWith(Arquillian.class)
public class TodoListIT {

  /**
   * The SQL script in the classpath to invoke to create in the database the tables required by the
   * tests.
   */
  private static final String DATABASE_SCRIPT = "/todolist_database.sql";
  /**
   * The SQL script in the classpath to invoked to insert a test dataset into the database.
   */
  private static final String DATASET_SCRIPT = "/todolist_dataset.sql";

  /**
   * The todolist component instance to use in the tests.
   */
  private static final String INSTANCE_ID = "todolist42";

  /**
   * The current user behind the tests.
   */
  private UserDetail requester;

  /**
   * We use here a JUnit 4 rule to load and unload automatically the dataset to use in the tests.
   */
  @Rule
  public DbSetupRule dbSetupRule = DbSetupRule.createTablesFrom(DATABASE_SCRIPT)
      .loadInitialDataSetFrom(DATASET_SCRIPT);

  /**
   * We build here the deployment artefact (a WAR archive in our case) with all the code and
   * resources required by the integration tests. The build is usually delegated to a deployment
   * archive builder (here the {@link TodolistWarBuilder} object).
   * <p>
   * Because the failure of the deployments isn't correctly traced by Arquillian, we wrap the build
   * of the archive by a try-catch statement in order to catch and output the error.
   * </p>
   *
   * @return the archive to deploy in Wildfly.
   */
  @Deployment
  public static Archive<?> createTestArchive() {
    try {
      return TodolistWarBuilder.onWarForTestClass(TodoListIT.class)
          .addAsResource(DATABASE_SCRIPT.substring(1))
          .addAsResource(DATASET_SCRIPT.substring(1))
          .build();
    } catch (Throwable e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /**
   * Setting up the session cache explicitly to simulate an existing user session for business
   * layer.
   */
  @Before
  public void setUpSessionCache() {
    requester = new UserDetail();
    requester.setId("42");
    CacheAccessorProvider.getSessionCacheAccessor()
        .newSessionCache(requester);
  }

  @After
  public void cleanSessionCache() {
    CacheAccessorProvider.getSessionCacheAccessor().getCache().clear();
  }

  @Test
  public void gettingAllTodosShouldReturnsAllOfThem() {
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    var allTodos = todoList.getAllTodos();
    assertThat(allTodos.size(), is(3));
    for (int i = 1; i <= allTodos.size(); i++) {
      var actual = allTodos.get(i - 1);
      assertThat(actual, isTodo(theTodoAt(i)));
    }
  }

  @Test
  public void addANewTodoInTheTodoListShouldPersistIt() throws SQLException {
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    String title = "My new Todo";
    String description = "My new stuff to do";
    Todo savedTodo = todoList.addTodo(new Todo(requester, title, description));

    var actualData = getPersistedDataOf(savedTodo.getId());
    assertThat(actualData.get("TITLE"), is(title));
    assertThat(actualData.get("DESCRIPTION"), is(description));
    assertThat(actualData.get("CREATEDBY"), is(requester.getId()));
  }

  @Test
  public void addAnExistingTodoInTheTodoListShouldDoesNothing() throws SQLException {
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    var expectedTodos = todoList.getAllTodos();
    Todo todo = expectedTodos.get(1);
    todoList.addTodo(todo);

    var actualTodos = todoList.getAllTodos();
    assertThat(actualTodos, is(expectedTodos));

    var allActualData = getAllPersistedData();
    assertThat(allActualData.size(), is(actualTodos.size()));
  }

  @Test
  public void addAnUpdatedTodoInTheTodoListShouldBeReportedAndPersisted() throws SQLException {
    int todoIdx = 1;
    String newDescription = "My new description";
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    Todo todo = todoList.getAllTodos().get(todoIdx);
    assertThat(todo.getDescription(), is(not(newDescription)));

    todo.setDescription(newDescription);
    todoList.addTodo(todo);

    // change should be reported in the TodoList
    Todo expectedTodo = todoList.getAllTodos().get(todoIdx);
    assertThat(expectedTodo.getDescription(), is(newDescription));

    // change should be persisted
    var actualData = getPersistedDataOf(todo.getId());
    assertThat(actualData.get("DESCRIPTION"), is(newDescription));
  }

  @Test
  public void updateExplicitlyAnExistingTodoShouldBeReportedAndPersisted() throws SQLException {
    int todoIdx = 1;
    String newDescription = "My new description";
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    Todo todo = todoList.getAllTodos().get(todoIdx);
    assertThat(todo.getDescription(), is(not(newDescription)));

    todo.setDescription(newDescription);
    todo.save();

    // change should be reported in the TodoList
    Todo expectedTodo = todoList.getAllTodos().get(todoIdx);
    assertThat(expectedTodo.getDescription(), is(newDescription));

    // change should be persisted
    var actualData = getPersistedDataOf(todo.getId());
    assertThat(actualData.get("DESCRIPTION"), is(newDescription));
  }

  @Test
  public void saveANonPersistedTodoShouldFail() {
    Todo todo = new Todo(requester, "My todo", "My todo description");
    assertThrows(IllegalStateException.class, todo::save);
  }

  @Test
  public void deleteAnExistingTodoShouldRemoveIt() throws SQLException {
    String todoId = "UUID-3";
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    todoList.removeTodo(todoId);

    var actualData = getPersistedDataOf(todoId);
    assertThat(actualData.isEmpty(), is(true));
  }

  @Test
  public void deleteANonExistingTodoShouldDoesNothing() throws SQLException {
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    var expectedTodos = todoList.getAllTodos();
    todoList.removeTodo("foo");

    var allActualData = getAllPersistedData();
    assertThat(allActualData.size(), is(expectedTodos.size()));
  }

  static Todo theTodoAt(int idx) {
    Date date = java.sql.Timestamp.valueOf("2016-07-29 18:50:00.0");
    Todo todo = new Todo(User.getById("1"), "Todo " + idx, "Do my stuff " + idx);
    Reflections.setField(todo, "id", UuidIdentifier.from("UUID-" + idx));
    Reflections.setField(todo, "creationDate", date);
    Reflections.setField(todo, "lastUpdateDate", date);
    Reflections.setField(todo, "lastUpdaterId", "1");
    return todo;
  }

  static Map<String, Object> getPersistedDataOf(String todoId) throws SQLException {
    return SQLRequester.findOne("select title, description, createdBy from sc_todo where id = ?",
        todoId);
  }

  static List<SQLRequester.ResultLine> getAllPersistedData() throws SQLException {
    return SQLRequester.list(select("title, description, createdBy").from("sc_todo"));
  }
}
  