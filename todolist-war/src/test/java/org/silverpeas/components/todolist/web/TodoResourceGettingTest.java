package org.silverpeas.components.todolist.web;

import com.silverpeas.web.RESTWebServiceTest;
import com.silverpeas.web.ResourceGettingTest;
import com.stratelia.webactiv.beans.admin.UserDetail;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static com.silverpeas.web.TestResources.getTestResources;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.silverpeas.components.todolist.web.TodoTestResources.*;

public class TodoResourceGettingTest extends ResourceGettingTest<TodoTestResources> {

  private String sessionKey;
  private UserDetail authenticatedUser;

  public TodoResourceGettingTest() {
    super(JAVA_PACKAGE, SPRING_CONTEXT);
  }

  @Before
  public void prepareTest() {
    authenticatedUser = aUser();
    sessionKey = authenticate(authenticatedUser);
  }

  @Test
  public void getAllTodos() {
    List<Todo> expectedTodos = getTestResources().getExistingTodos();

    List<HashMap<String, String>> actualTodos = getAt(aResourceURI(), List.class);

    assertThat(actualTodos.size(), is(expectedTodos.size()));
    assertThat(actualTodos.get(0).get("id"), is(expectedTodos.get(0).getId()));
    assertThat(actualTodos.get(0).get("description"), is(expectedTodos.get(0).getDescription()));
  }

  @Override
  public String[] getExistingComponentInstances() {
    return new String[] {COMPONENT_INSTANCE_ID};
  }

  @Override
  public String aResourceURI() {
    return URI_BASE + "/" + COMPONENT_INSTANCE_ID;
  }

  @Override
  public String anUnexistingResourceURI() {
    return URI_BASE + "/todolist1000";
  }

  @Override
  public <T> T aResource() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getSessionKey() {
    return sessionKey;
  }

  @Override
  public Class<TodoEntity> getWebEntityClass() {
    return TodoEntity.class;
  }
}