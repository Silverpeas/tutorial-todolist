package org.silverpeas.components.todolist.repository;

import javax.inject.Inject;

/**
 * A provider of a {@link org.silverpeas.components.todolist.repository.TodoRepository} instances.
 * It abstracts the way a TodoRepository is get and hence the life-cycle of such objects.
 * @author mmoquillon
 */
public class TodoRepositoryProvider {

  private static final TodoRepositoryProvider instance = new TodoRepositoryProvider();
  @Inject
  private TodoRepository repository;

  private TodoRepositoryProvider() {

  }

  public static final TodoRepositoryProvider getInstance() {
    return instance;
  }

  public static final TodoRepository getTodoRepository() {
    return instance.repository;
  }
}
