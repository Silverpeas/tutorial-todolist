package org.silverpeas.components.todolist.model;

import org.silverpeas.components.todolist.notification.TodoEventNotifier;
import org.silverpeas.components.todolist.repository.TodoRepository;
import org.silverpeas.core.admin.component.model.SilverpeasComponentInstance;
import org.silverpeas.core.notification.system.ResourceEvent;
import org.silverpeas.core.persistence.Transaction;
import org.silverpeas.kernel.annotation.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * A Todolist is defined for a given Todolist application and gathers within it the todos of the
 * users. In our case, the concept of todolist is reified into this object and this one provides,
 * consequently, the business services on the todos.
 *
 * @author mmoquillon
 */
public class TodoList {

  private final String id;
  private final TodoRepository repository = TodoRepository.get();

  /**
   * Private constructor to avoid its invocation by a client. By doing this, we delegate its
   * obtaining to a class method (static method) so that we can encapsulate the mechanism of how it
   * is got (either by constructing it directly as a transient object or by getting it through the
   * persistence context as a persistent entity).
   *
   * @param id the unique identifier of the todolist. According to the implementation, this
   * identifier can be either the identifier of the todolist component instance (1 instance = 1
   * todolist) or the persistence identifier of the entity (1 instance = several todolist).
   */
  private TodoList(String id) {
    this.id = id;
  }

  /**
   * Gets the todolist of the specified component instance.
   *
   * @param id the unique identifier of a todolist component instance.
   * @return a {@link TodoList} object with which all the todos can be managed for the users.
   */
  @NonNull
  public static TodoList getById(String id) {
    return new TodoList(id);
  }

  /**
   * Gets all the todos in this list.
   *
   * @return an immutable list of todos. If there are no todos in this todolist, then an empty on
   * list is returned.
   */
  @NonNull
  public List<Todo> getAllTodos() {
    return List.copyOf(repository.getAllTodoInTodoList(getId()));
  }

  /**
   * Gets the unique identifier of this todolist.
   *
   * @return the unique identifier of this todolist.
   */
  @NonNull
  public String getId() {
    return id;
  }

  /**
   * Gets the title of this todolist as expressed in the specified language. If no such title exists
   * in the given language, then the title in the default language is returned (id est in French).
   *
   * @param language the language in which the title should be.
   * @return the todolist title.
   */
  @NonNull
  public String getTitle(@NonNull final String language) {
    Objects.requireNonNull(language);
    return SilverpeasComponentInstance.getById(id)
        .map(c -> c.getLabel(language))
        .orElse("");
  }

  /**
   * Adds the specified task to do into this todolist. The adding persists the added task. If the
   * task cannot be added, then a {@link org.silverpeas.kernel.SilverpeasRuntimeException} is
   * thrown. If the task is already in this todolist, it doesn't added again, only its changes are
   * persisted.
   *
   * @param theTodo the task to add into this todolist.
   * @return the persisted task with its new persistence unique identifier.
   */
  public Todo addTodo(@NonNull final Todo theTodo) {
    Objects.requireNonNull(theTodo);
    return Transaction.performInOne(() -> {
      theTodo.setInstanceId(id);
      Todo savedTodo = repository.save(theTodo);
      TodoEventNotifier.get().notifyEventOn(ResourceEvent.Type.CREATION, savedTodo);
      return savedTodo;
    });
  }

  /**
   * Removes the task to do with the specified unique identifier from this todolist. If no such task
   * exists in this list, then nothing is performed.
   *
   * @param todoId the unique identifier of the task.
   */
  public void removeTodo(@NonNull final String todoId) {
    Objects.requireNonNull(todoId);
    Transaction.performInOne(() -> {
      repository.removeTodoFromTodoList(todoId, getId());
      return null;
    });
  }
}
