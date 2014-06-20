package org.silverpeas.components.todolist.web;

import com.silverpeas.web.ResourceCreationTest;
import com.silverpeas.web.ResourceDeletionTest;
import com.stratelia.webactiv.beans.admin.UserDetail;
import org.junit.Before;
import org.junit.Test;
import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.components.todolist.repository.TodoRepository;

import static com.silverpeas.web.TestResources.getTestResources;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.silverpeas.components.todolist.web.TodoTestResources.*;

public class TodoResourceDeletionTest extends ResourceDeletionTest<TodoTestResources> {

  private String sessionKey;
  private UserDetail authenticatedUser;

  public TodoResourceDeletionTest() {
    super(JAVA_PACKAGE, SPRING_CONTEXT);
  }

  @Before
  public void prepareTest() {
    authenticatedUser = aUser();
    sessionKey = authenticate(authenticatedUser);
  }

  @Test
  public void deleteATodo() {
    TodoRepository mock = getTestResources().getTodoRepository();
    Todo todo = getTestResources().anExistingTodo();
    when(mock.getById(todo.getId())).thenReturn(todo);

    deleteAt(aResourceURI());

    verify(mock).delete(todo);
  }

  @Override
  public String[] getExistingComponentInstances() {
    return new String[]{COMPONENT_INSTANCE_ID};
  }

  @Override
  public String aResourceURI() {
    return URI_BASE + "/" + COMPONENT_INSTANCE_ID + "/" + TODO_ID;
  }

  @Override
  public String anUnexistingResourceURI() {
    return URI_BASE + "/todolist1000/300";
  }

  @Override
  public TodoEntity aResource() {
    Todo todo = getTestResources().anExistingTodo();
    return new TodoEntity(todo);
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