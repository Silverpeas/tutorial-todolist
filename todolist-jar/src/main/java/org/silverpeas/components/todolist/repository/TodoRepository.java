/**
 * Copyright (C) 2000 - 2014 Silverpeas
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
 * "http://www.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.components.todolist.repository;

import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.persistence.model.identifier.UuidIdentifier;
import org.silverpeas.persistence.repository.jpa.NamedParameters;
import org.silverpeas.persistence.repository.jpa.SilverpeasJpaEntityManager;

import javax.inject.Named;
import java.util.List;

/**
 * This repository manages the persistence of Todo instances, whatever the data
 * underlying data sources.
 * For doing, it extends the
 * {@link org.silverpeas.persistence.repository.jpa.SilverpeasJpaEntityManager} base repository that
 * provides all the basic and necessary methods to save, to update, to delete and to get the
 * business entities.
 */
@Named
public class TodoRepository
    extends SilverpeasJpaEntityManager<Todo, UuidIdentifier>{

  /**
   * Gets all the Todos in the specified todolist.
   * @param todolistId the unique identifier of the todolist, that is the identifier of the
   * component instance representing the todolist.
   * @return a list of todos.
   */
  public List<Todo> getAllTodoInTodoList(String todolistId) {
    NamedParameters parameters = newNamedParameters();
    parameters.add("instanceId", todolistId);
    return listFromNamedQuery("alltodos", parameters);
  }

  /**
   * Deletes the specified todolist and hence all the todos related to this totolist.
   * @param todolistId the unique identifier of the todolist, that is the identifier of the
   * component instance representing the todolist.
   */
  public void deleteTodoList(final String todolistId) {
    NamedParameters parameters = newNamedParameters();
    parameters.add("instanceId", todolistId);
    deleteFromNamedQuery("deleteTodoList", parameters);
  }
}