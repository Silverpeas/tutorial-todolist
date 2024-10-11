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
import org.silverpeas.web.test.ResourceDeletionTest;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration test on the deletion of todos by using the REST-based web services. This test class
 * extends {@link ResourceDeletionTest} in which are defined a set of tests on usual errors with web
 * services requesting (resource not found, resource unauthorized, and so).
 */
@RunWith(Arquillian.class)
public class TodoListResourceDeletionIT extends ResourceDeletionTest {

  private static final int STATUS_OK = Response.Status.NO_CONTENT.getStatusCode();

  private final TestContext testContext = new TestContext();
  private String authToken;
  private TodoEntity expectedEntity;

  @Deployment
  public static Archive<?> createTestArchive() {
    return TodolistWarBuilder.onWarForTestClass(TodoListResourceDeletionIT.class)
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
    Todo todo = testContext.getExistingTodo();
    expectedEntity = TodoEntity.fromTodo(todo,
        getWebResourceBaseURIBuilder().path(TodoListResource.PATH));
  }

  @Test
  public void deleteATodo() {
    try (Response response = deleteAt(aResourceURI(), Response.class)) {
      assertThat(response.getStatus(), is(STATUS_OK));

      var mayBeTodo = testContext.getTodo(expectedEntity.getId());
      assertThat(mayBeTodo.isEmpty(), is(true));
    }
  }

  @Override
  public String[] getExistingComponentInstances() {
    return new String[]{TestContext.TODOLIST_ID};
  }

  @Override
  public String aResourceURI() {
    return TodoListResource.PATH + "/" + TestContext.TODOLIST_ID + "/" + expectedEntity.getId();
  }

  @Override
  public String anUnexistingResourceURI() {
    return TodoListResource.PATH + "/" + TestContext.TODOLIST_ID + "/666";
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