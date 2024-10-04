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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.silverpeas.components.todolist.notification.TodoEventNotifier;
import org.silverpeas.components.todolist.repository.TodoRepository;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.notification.system.ResourceEvent;
import org.silverpeas.core.persistence.Transaction;
import org.silverpeas.core.persistence.datasource.model.identifier.UuidIdentifier;
import org.silverpeas.core.persistence.datasource.model.jpa.EntityManagerProvider;
import org.silverpeas.core.persistence.datasource.model.jpa.JpaPersistOperation;
import org.silverpeas.core.persistence.datasource.model.jpa.JpaUpdateOperation;
import org.silverpeas.core.test.unit.extention.JEETestContext;
import org.silverpeas.core.test.unit.extention.RequesterProvider;
import org.silverpeas.kernel.test.annotations.TestManagedBeans;
import org.silverpeas.kernel.test.annotations.TestManagedMock;
import org.silverpeas.kernel.test.extension.EnableSilverTestEnv;
import org.silverpeas.kernel.test.util.Reflections;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.silverpeas.components.todolist.test.TodoMatcher.isTodo;
import static org.silverpeas.kernel.util.StringUtil.isNotDefined;

/**
 * Unit tests on the {@link TodoList} behaviors. Because a lot of objects on which it depends for
 * performing its behavior is managed by an IoC container, we enable here the Silverpeas execution
 * environment for unit tests in which the IoC container is simulated and in which a simplified IoD
 * mechanism is implemented. The test execution environment is extended for a JEE context in which
 * some resources and beans required by Silverpeas are prepared.
 * <p>
 * Because the persistence layer is implied in the tested behaviors, we have to ensure to stub it.
 * This is done with the {@link TestManagedBeans} annotation in which are listed all the objects to
 * be managed by the simulated IoC.
 * </p>
 */
@EnableSilverTestEnv(context = JEETestContext.class)
@TestManagedBeans({Transaction.class,
    JpaPersistOperation.class,
    JpaUpdateOperation.class})
class TodoListTest {

  private static int id = 1;
  public static final String INSTANCE_ID = "todolist1";

  /**
   * List of all expected todos. This list is prepared with an initial dataset that will be updated
   * during the unit tests.
   */
  private final List<Todo> expectedTodos = new ArrayList<>(List.of(
      aPersistedTodo("First todo", "Do a thing 1"),
      aPersistedTodo("Second todo", "Do a thing 2"),
      aPersistedTodo("Third todo", "Do a thing 3")));

  /**
   * The {@link TodoEventNotifier} is used by the model to inform any interested business
   * services about changes on the todos. So we have here to mock it. The
   * {@link TestManagedMock} annotation tells the Silverpeas test environment to mock and
   * manage in the simulated IoC container the annotated bean.
   */
  @TestManagedMock
  private TodoEventNotifier notifier;

  /**
   * The {@link TodoRepository} object here is mocked and then injected into the simulated IoC
   * container. We can then mock the expected behavior of the repository.
   */
  @TestManagedMock
  private TodoRepository repository;

  /**
   * The {@link EntityManagerProvider} is here mocked and then injected into the simulated IoC
   * container in order to return a mocked entity manager.
   */
  @TestManagedMock
  private EntityManagerProvider entityManagerProvider;

  /**
   * The user to use in the execution of the unit tests. Such an annotated method is used to
   * indicate to the test execution environment the user behind a incoming request. This
   * information is used in the inner mechanism of Silverpeas (like the persistence).
   *
   * @return the current requester.
   */
  @RequesterProvider
  public User getCurrentRequester() {
    User user = mock(User.class);
    when(user.getId()).thenReturn("0");
    when(user.getFirstName()).thenReturn("Toto");
    when(user.getLastName()).thenReturn("Chez-les-Papoos");
    return user;
  }

  @BeforeEach
  public void mockExpectedRepositoryBehavior() {
    assertThat(repository, is(notNullValue()));
    when(repository.getAllTodoInTodoList(INSTANCE_ID)).thenReturn(expectedTodos);
    when(repository.save(any(Todo.class))).thenAnswer(a -> {
      Todo todo = a.getArgument(0);
      if (isNotDefined(todo.getId())) {
        Reflections.setField(todo, "id", UuidIdentifier.from(String.valueOf(id++)));
        expectedTodos.add(todo);
      }
      return todo;
    });
    doAnswer(a -> {
      String todoId = a.getArgument(0);
      String instanceId = a.getArgument(1);
      if (instanceId.equals(INSTANCE_ID)) {
        expectedTodos.removeIf(t -> t.getId().equals(todoId));
      }
      return null;
    }).when(repository).removeTodoFromTodoList(anyString(), anyString());
  }

  @BeforeEach
  public void mockExpectedEntityManagerBehavior() {
    assertThat(entityManagerProvider, is(notNullValue()));
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManagerProvider.getEntityManager()).thenReturn(entityManager);
    when(entityManager.find(any(), any())).thenAnswer(a -> {
      UuidIdentifier uuid = a.getArgument(1);
      return expectedTodos.stream().filter(t -> t.getId().equals(uuid.asString()))
          .findFirst()
          .orElse(null);
    });
  }

  @Test
  void createAnInvalidTodoShouldFail() {
    User user = User.getCurrentRequester();
    String title = "My new todo";
    String description = "Do my new stuff";

    //noinspection DataFlowIssue
    assertThrows(NullPointerException.class, () -> new Todo(null, title, description));

    //noinspection DataFlowIssue
    assertThrows(NullPointerException.class, () -> new Todo(user, null, description));

    //noinspection DataFlowIssue
    assertThrows(NullPointerException.class, () -> new Todo(user, title, null));
  }

  @Test
  void getAllTodosShouldReturnAllExpected() {
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    List<Todo> actualTodos = todoList.getAllTodos();
    assertThat(actualTodos.size(), is(expectedTodos.size()));
    for (int i = 0; i < actualTodos.size(); i++) {
      assertThat(actualTodos.get(i), isTodo(expectedTodos.get(i)));
    }
  }

  @Test
  void addANewTodoShouldPersistItAndNotify() {
    String expectedTitle = "My new todo";
    String expectedDescription = "Do my new stuff";
    int previousSize = expectedTodos.size();

    TodoList todoList = TodoList.getById(INSTANCE_ID);
    Todo newTodo = new Todo(User.getCurrentRequester(), expectedTitle, expectedDescription);
    todoList.addTodo(newTodo);
    assertThat(expectedTodos.size(), is(previousSize + 1));
    assertThat(expectedTodos.get(3).getTitle(), is(expectedTitle));
    assertThat(expectedTodos.get(3).getDescription(), is(expectedDescription));

    // verify the saving of newTodo is invoked against the repository
    verify(repository).save(newTodo);
    // verify the event about the creation is sent
    verify(notifier).notifyEventOn(ResourceEvent.Type.CREATION, newTodo);
  }

  @Test
  void addATodoDirectlyInTheListOfTodosShouldFail() {
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    List<Todo> todos = todoList.getAllTodos();
    Todo aTodo = new Todo(User.getCurrentRequester(), "my todo", "my todo description");
    assertThrows(UnsupportedOperationException.class, () -> todos.add(aTodo));
  }

  @Test
  void addAnAlreadyExistingTodoShouldDoesNothing() {
    int todoIdx = 1;
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    List<Todo> todos = todoList.getAllTodos();
    int expectedSize = todos.size();
    Todo todo = todos.get(todoIdx);
    todoList.addTodo(todo);

    List<Todo> actualTodos = todoList.getAllTodos();
    assertThat(actualTodos.size(), is(expectedSize));
    assertThat(actualTodos.size(), is(expectedTodos.size()));
  }

  @Test
  void updateAnExistingTodoShouldPersistChanges() {
    String expectedDescription = "My new description";
    int todoIdx = 1;
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    List<Todo> actualTodos = todoList.getAllTodos();
    Todo todo = actualTodos.get(todoIdx);
    todo.setDescription(expectedDescription);
    todo.save();

    assertThat(expectedTodos.get(todoIdx).getDescription(), is(expectedDescription));
  }

  @Test
  void addNullAsTodoShouldFail() {
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    //noinspection DataFlowIssue
    assertThrows(NullPointerException.class, () -> todoList.addTodo(null));
  }

  @Test
  void deleteATodoShouldSucceed() {
    int size = expectedTodos.size();
    Todo todo = expectedTodos.get(1);

    TodoList todoList = TodoList.getById(INSTANCE_ID);
    todoList.removeTodo(todo.getId());
    assertThat(expectedTodos.size(), is(size - 1));
    assertThat(expectedTodos, not(contains(todo)));
  }

  @Test
  void deleteANonExistingTodoShouldDoesNothing() {
    int size = expectedTodos.size();

    TodoList todoList = TodoList.getById(INSTANCE_ID);
    todoList.removeTodo("666");
    assertThat(expectedTodos.size(), is(size));
  }

  @Test
  void removeATodoDirectlyFromTheListOfTodosShouldFail() {
    TodoList todoList = TodoList.getById(INSTANCE_ID);
    List<Todo> todos = todoList.getAllTodos();
    assertThrows(UnsupportedOperationException.class, () -> todos.remove(0));
  }

  private static Todo aPersistedTodo(String title, String description) {
    User requester = mock(User.class);
    when(requester.getId()).thenReturn("1");
    when(requester.getFirstName()).thenReturn("John");
    when(requester.getLastName()).thenReturn("Foo");

    Todo todo = new Todo(requester, title, description);
    todo.setInstanceId(INSTANCE_ID);
    Reflections.setField(todo, "id", UuidIdentifier.from(String.valueOf(id++)));
    return todo;
  }
}