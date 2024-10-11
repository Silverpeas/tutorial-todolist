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
import org.silverpeas.web.test.ResourceGettingTest;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

/**
 * Integration test on the getting of todos by using the REST-based web services. This test class
 * extends {@link ResourceGettingTest} in which are defined a set of tests on usual errors with
 * web services requesting (resource not found, resource unauthorized, and so).
 */
@RunWith(Arquillian.class)
public class TodoListResourceGettingIT extends ResourceGettingTest {

  private static final int STATUS_OK = Response.Status.OK.getStatusCode();

  private final TestContext testContext = new TestContext();
  private String authToken;
  private List<TodoEntity> expectedTodos;

  @Deployment
  public static Archive<?> createTestArchive() {
    return TodolistWarBuilder.onWarForTestClass(TodoListResourceGettingIT.class)
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
    List<Todo> todos = testContext.getAllExistingTodos();
    expectedTodos = TodoEntity.fromTodos(todos, () ->
        getWebResourceBaseURIBuilder()
            .path(TodoListResource.PATH)
            .path(TestContext.TODOLIST_ID));
  }

  @Test
  public void getAllTodos() {
    Response response = getAt(aResourceURI(), Response.class);
    assertThat(response.getStatus(), is(STATUS_OK));

    TodoEntity[] actualTodos = response.readEntity(TodoEntity[].class);
    assertThat(actualTodos.length, is(expectedTodos.size()));
    assertThat(expectedTodos, containsInAnyOrder(actualTodos));
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
    return TodoListResource.PATH + "/666";
  }

  @Override
  public TodoEntity[] aResource() {
    return expectedTodos.toArray(new TodoEntity[0]);
  }

  @Override
  public String getAPITokenValue() {
    return authToken;
  }


  @Override
  public Class<TodoEntity[]> getWebEntityClass() {
    return TodoEntity[].class;
  }
}