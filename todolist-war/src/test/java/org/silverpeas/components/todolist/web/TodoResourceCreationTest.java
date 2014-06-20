package org.silverpeas.components.todolist.web;

import com.silverpeas.web.ResourceCreationTest;
import com.stratelia.webactiv.beans.admin.UserDetail;
import org.junit.Before;
import org.junit.Test;
import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.components.todolist.repository.TodoRepository;
import org.silverpeas.persistence.repository.OperationContext;

import java.net.URI;

import static com.silverpeas.web.TestResources.getTestResources;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.silverpeas.components.todolist.web.TodoTestResources.*;

public class TodoResourceCreationTest extends ResourceCreationTest<TodoTestResources> {

  private String sessionKey;
  private UserDetail authenticatedUser;

  public TodoResourceCreationTest() {
    super(JAVA_PACKAGE, SPRING_CONTEXT);
  }

  @Before
  public void prepareTest() {
    authenticatedUser = aUser();
    sessionKey = authenticate(authenticatedUser);
  }

  @Test
  public void addANewTodo() {
    TodoRepository mock = getTestResources().getTodoRepository();

    post(aResource(), aResourceURI());

    verify(mock).save(any(OperationContext.class), any(Todo.class));
  }

  @Override
  public String[] getExistingComponentInstances() {
    return new String[]{COMPONENT_INSTANCE_ID};
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
  public TodoEntity aResource() {
    Todo todo = getTestResources().aNewTodo();
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