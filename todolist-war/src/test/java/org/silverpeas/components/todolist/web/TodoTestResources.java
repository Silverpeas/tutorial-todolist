package org.silverpeas.components.todolist.web;

import com.silverpeas.web.TestResources;
import com.stratelia.webactiv.beans.admin.UserDetail;
import org.silverpeas.components.todolist.mock.TodoRepositoryMockWrapper;
import org.silverpeas.components.todolist.repository.TodoRepository;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * @author mmoquillon
 */
@Named(TestResources.TEST_RESOURCES_NAME)
public class TodoTestResources extends TestResources {

  public static final String JAVA_PACKAGE = "org.silverpeas.components.todolist.web";
  public static final String COMPONENT_INSTANCE_ID = "todolist3";
  public static final String TODO_ID = "todo_1";

  public static final String URI_BASE = "todolist";

  public Todo aNewTodo() {
    UserDetail author = registerUser(aUser());
    return new Todo(COMPONENT_INSTANCE_ID, author, "this is a new todo");
  }

  public Todo anExistingTodo() {
    UserDetail author = registerUser(aUser());
    Todo todo = new Todo(COMPONENT_INSTANCE_ID, author, "this is a new todo");
    ReflectionTestUtils.invokeSetterMethod(todo, "setId", TODO_ID);
    return todo;
  }

  public List<Todo> getExistingTodos() {
    List<Todo> todos = new ArrayList<Todo>();
    todos.add(anExistingTodo());
    TodoRepository mock = getTodoRepository();
    when(mock.getAllTodoInTodoList(COMPONENT_INSTANCE_ID)).thenReturn(todos);
    return todos;
  }

  public TodoRepository getTodoRepository() {
    TodoRepositoryMockWrapper mockWrapper =
        (TodoRepositoryMockWrapper) TodoRepository.get();
    return mockWrapper.getMock();
  }
}
