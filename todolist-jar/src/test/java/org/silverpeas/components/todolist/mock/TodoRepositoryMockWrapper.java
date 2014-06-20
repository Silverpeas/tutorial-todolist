package org.silverpeas.components.todolist.mock;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.mockito.Mockito;
import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.components.todolist.repository.TodoRepository;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Named;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;

/**
 * @author mmoquillon
 */
@Named("todoRepository")
public class TodoRepositoryMockWrapper extends TodoRepository {

  private static final TodoRepository mock = mock(TodoRepository.class);

  public TodoRepository getMock() {
    return mock;
  }

  @Override
  public List<Todo> getAllTodoInTodoList(final String todolistId) {
    return mock.getAllTodoInTodoList(todolistId);
  }
}
