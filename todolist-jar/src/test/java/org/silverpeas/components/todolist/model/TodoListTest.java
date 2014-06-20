package org.silverpeas.components.todolist.model;

import com.stratelia.webactiv.beans.admin.UserDetail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.silverpeas.components.todolist.mock.TodoRepositoryMockWrapper;
import org.silverpeas.components.todolist.repository.TodoRepository;
import org.silverpeas.components.todolist.repository.TodoRepositoryProvider;
import org.silverpeas.persistence.repository.OperationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TodoListTest {

  private static int id = 1;
  public static final String INSTANCE_ID = "todolist1";

  private ConfigurableApplicationContext context;
  private UserDetail user;

  @Before
  public void setUp() throws Exception {
    context = new ClassPathXmlApplicationContext("/spring-todolist.xml");
    context.start();
  }

  @After
  public void tearDown() throws Exception {
    context.stop();
  }

  @Test
  public void getAllTodos() throws Exception {
    List<Todo> expectedTodos = Arrays
        .asList(new Todo[]{aTodo("First todo"), aTodo("Second todo"), aTodo("Third todo")});
    when(getTodoRepository().getAllTodoInTodoList(INSTANCE_ID)).thenReturn(expectedTodos);

    TodoList todoList = TodoList.getById(INSTANCE_ID);
    List<Todo> actualTodos = todoList.getAllTodos();
    assertThat(actualTodos.size(), is(expectedTodos.size()));
    assertThat(actualTodos.get(0).getDescription(), is(expectedTodos.get(0).getDescription()));
    assertThat(actualTodos.get(0).getId(), is(expectedTodos.get(0).getId()));
    assertThat(actualTodos.get(0).getCreator(), is(expectedTodos.get(0).getCreator()));
    assertThat(actualTodos.get(1).getDescription(), is(expectedTodos.get(1).getDescription()));
    assertThat(actualTodos.get(1).getId(), is(expectedTodos.get(1).getId()));
    assertThat(actualTodos.get(1).getCreator(), is(expectedTodos.get(0).getCreator()));
    assertThat(actualTodos.get(2).getDescription(), is(expectedTodos.get(2).getDescription()));
    assertThat(actualTodos.get(2).getId(), is(expectedTodos.get(2).getId()));
    assertThat(actualTodos.get(2).getCreator(), is(expectedTodos.get(0).getCreator()));
  }

  @Test
  public void addANewTodo() throws Exception {
    final String expectedDescription = "This is a new todo";

    TodoRepository mock = getTodoRepository();
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
        Object[] arguments = invocationOnMock.getArguments();
        Todo actualTodo = (Todo) arguments[1];
        assertThat(actualTodo.getDescription(), is(expectedDescription));
        assertThat(actualTodo.getCreatedBy(), is(aUserDetail().getId()));
        return null;
      }
    }).when(mock).save(any(OperationContext.class), any(Todo.class));

    TodoList todoList = TodoList.getById(INSTANCE_ID);
    todoList.addTodo(aUserDetail(), expectedDescription);
  }

  private UserDetail aUserDetail() {
    if (user == null) {
      user = new UserDetail();
      user.setId("0");
      user.setFirstName("Toto");
      user.setLastName("Chez-les-papoos");
    }
    return user;
  }

  private Todo aTodo(String description) {
    Todo todo = new Todo(INSTANCE_ID, aUserDetail(), description);
    ReflectionTestUtils.invokeSetterMethod(todo, "setId", String.valueOf(id++));
    return todo;
  }

  private TodoRepository getTodoRepository() {
    TodoRepositoryMockWrapper mockWrapper =
        (TodoRepositoryMockWrapper) TodoRepositoryProvider.getTodoRepository();
    return mockWrapper.getMock();
  }
}