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
package org.silverpeas.components.todolist.web;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.silverpeas.components.todolist.TestContext;
import org.silverpeas.components.todolist.TodolistWarBuilder;
import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.web.test.ResourceCreationTest;

import javax.ws.rs.core.Response;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration test on the creation of todos by using the REST-based web services. This test class
 * extends {@link ResourceCreationTest} in which are defined a set of tests on usual errors with
 * web services requesting (resource not found, resource unauthorized, and so).
 */
@RunWith(Arquillian.class)
public class TodoListResourceCreationIT extends ResourceCreationTest {

  private static final int STATUS_CREATED = Response.Status.CREATED.getStatusCode();

  private final TestContext testContext = new TestContext();
  private String authToken;
  private TodoEntity expectedEntity;

  @Deployment
  public static Archive<?> createTestArchive() {
    return TodolistWarBuilder.onWarForTestClass(TodoListResourceCreationIT.class)
        .addRESTWebServiceEnvironment()
        .addAsResource(TestContext.DATABASE_SCRIPT.substring(1))
        .addAsResource(TestContext.DATASET_SCRIPT.substring(1))
        .build();
  }

  @Override
  protected String getTableCreationScript() {
    return TestContext.DATABASE_SCRIPT;
  }

  @Override
  protected String getDataSetScript() {
    return TestContext.DATASET_SCRIPT;
  }

  @Before
  public void prepareTestResources() {
    User requester = User.getById("1");
    authToken = getTokenKeyOf(requester);
    Todo todo = testContext.computeNewTodo(requester);
    expectedEntity = new TodoEntity(todo);
  }

  @Test
  public void addANewTodo() {
    Pattern uriPattern = Pattern.compile(
        "^http://localhost:8080/silverpeas/services/" + aResourceURI() + "/[a-z0-9\\-]+$");
    var todosBefore = testContext.getAllExistingTodos();

    try (Response response = post(aResource(), aResourceURI())) {
      assertThat(response.getStatus(), is(STATUS_CREATED));

      TodoEntity[] actualTodos = response.readEntity(TodoEntity[].class);
      assertThat(actualTodos, notNullValue());
      assertThat(actualTodos.length, is(todosBefore.size() + 1));

      TodoEntity createdTodo = actualTodos[actualTodos.length - 1];
      assertThat(createdTodo.getURI(), is(response.getLocation()));
      assertThat(uriPattern.matcher(createdTodo.getURI().toString()).matches(), is(true));
      assertThat(createdTodo.getId(), is(notNullValue()));
      assertThat(createdTodo.getTitle(), is(expectedEntity.getTitle()));
      assertThat(createdTodo.getDescription(), is(expectedEntity.getDescription()));
      assertThat(createdTodo.getAuthorName(), is(expectedEntity.getAuthorName()));
    }
  }

  @Override
  public String[] getExistingComponentInstances() {
    return new String[]{TestContext.TODOLIST_ID};
  }

  @Override
  public String aResourceURI() {
    return TodoListResource.PATH + "/" + TestContext.TODOLIST_ID;
  }

  @Override
  public String anUnexistingResourceURI() {
    return TodoListResource.PATH + "/todolist666";
  }

  @Override
  public String getAPITokenValue() {
    return authToken;
  }

  @SuppressWarnings("unchecked")
  @Override
  public TodoEntity aResource() {
    return expectedEntity;
  }

  @Override
  public Class<TodoEntity> getWebEntityClass() {
    return TodoEntity.class;
  }
}