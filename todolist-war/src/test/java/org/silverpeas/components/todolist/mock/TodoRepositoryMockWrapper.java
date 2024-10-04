package org.silverpeas.components.todolist.mock;

import org.silverpeas.components.todolist.repository.TodoRepository;
import org.silverpeas.kernel.annotation.NonNull;
import org.silverpeas.persistence.repository.OperationContext;

import javax.inject.Named;
import java.util.List;

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
  @NonNull
  public List<Todo> getAllTodoInTodoList(final String todolistId) {
    return mock.getAllTodoInTodoList(todolistId);
  }

  @Override
  public Todo getById(final String id) {
    return mock.getById(id);
  }

  @Override
  public void delete(final Todo... entity) {
    mock.delete(entity);
  }

  @Override
  public Todo save(final OperationContext context, final Todo entity) {
    return mock.save(context, entity);
  }
}
