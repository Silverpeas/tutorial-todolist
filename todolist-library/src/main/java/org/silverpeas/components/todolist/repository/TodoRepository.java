/**
 * Copyright (C) 2000 - 2014 Silverpeas
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * As a special exception to the terms and conditions of version 3.0 of the GPL, you may
 * redistribute this Program in connection with Free/Libre Open Source Software ("FLOSS")
 * applications as described in Silverpeas's FLOSS exception.  You should have received a copy of
 * the text describing the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.com/legal/licensing"
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.components.todolist.repository;

import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.core.annotation.Repository;
import org.silverpeas.core.persistence.datasource.model.identifier.UuidIdentifier;
import org.silverpeas.core.persistence.datasource.repository.jpa.NamedParameters;
import org.silverpeas.core.persistence.datasource.repository.jpa.SilverpeasJpaEntityRepository;
import org.silverpeas.core.util.ServiceProvider;
import org.silverpeas.kernel.annotation.NonNull;

import java.util.List;

/**
 * This repository manages the persistence of {@link org.silverpeas.components.todolist.model.Todo}
 * entities, whatever the underlying data sources.
 * For doing, it extends the
 * {@link SilverpeasJpaEntityRepository} base repository that provides all the basic and
 * necessary methods to save, to update, to delete and to get the
 * business entities.
 */
@Repository
public class TodoRepository extends SilverpeasJpaEntityRepository<Todo> {

  private static final String INSTANCE_ID = "instanceId";

  public static TodoRepository get() {
    return ServiceProvider.getService(TodoRepository.class);
  }

  /**
   * Constructors dedicated to only the IoC container.
   */
  protected TodoRepository() {
  }

  /**
   * Gets all the Todos in the specified todolist.
   * @param todolistId the unique identifier of the todolist, that is the identifier of the
   * component instance representing the todolist.
   * @return a list of todos or an empty list if there is no todos in the todolist.
   */
  @NonNull
  public List<Todo> getAllTodoInTodoList(String todolistId) {
    NamedParameters parameters = newNamedParameters()
        .add(INSTANCE_ID, todolistId);
    return listFromNamedQuery("alltodos", parameters);
  }

  /**
   * Deletes the specified todolist and hence all the todos related to this totolist.
   * @param todolistId the unique identifier of the todolist, that is the identifier of the
   * component instance representing the todolist.
   */
  public void deleteTodoList(final String todolistId) {
    NamedParameters parameters = newNamedParameters()
        .add(INSTANCE_ID, todolistId);
    deleteFromNamedQuery("deleteTodoList", parameters);
  }

  /**
   * Removes the specified task to do from the given todolist.
   * @param todoId the unique identifier of a todo.
   * @param todoListId the unique identifier of a todolist.
   */
  public void removeTodoFromTodoList(final String todoId, final String todoListId) {
    NamedParameters parameters = newNamedParameters()
        .add("todoId", UuidIdentifier.from(todoId))
        .add(INSTANCE_ID, todoListId);
    deleteFromNamedQuery("deleteTodo", parameters);
  }
}