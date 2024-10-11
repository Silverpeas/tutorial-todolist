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

package org.silverpeas.components.todolist;

import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.components.todolist.model.TodoList;
import org.silverpeas.core.admin.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Context of the test. It provides methods to get existing todos as expected ones and to ensure the
 * result of a test is the expected ones.
 *
 * @author mmoquillon
 */
public class TestContext {

  private static final Random RANDOM = new Random();

  /**
   * The SQL script in the classpath to invoke to create in the database the tables required by the
   * tests.
   */
  public static final String DATABASE_SCRIPT = "/todolist_database.sql";
  /**
   * The SQL script in the classpath to invoked to insert a test dataset into the database.
   */
  public static final String DATASET_SCRIPT = "/todolist_dataset.sql";

  /**
   * The unique identifier of the default todolist to use in the tests
   */
  public static final String TODOLIST_ID = "todolist42";

  /**
   * Gets randomly an existing task from the todolist {@link TestContext#TODOLIST_ID}.
   *
   * @return a task to do.
   */
  public Todo getExistingTodo() {
    int idx = RANDOM.nextInt(4);
    TodoList todoList = TodoList.getById(TODOLIST_ID);
    return todoList.getAllTodos().get(idx);
  }

  /**
   * Gets all the todos of the todolist {@link TestContext#TODOLIST_ID}.
   *
   * @return a list with all the todos of an existing todolist.
   */
  public List<Todo> getAllExistingTodos() {
    TodoList todoList = TodoList.getById(TODOLIST_ID);
    return todoList.getAllTodos();
  }

  /**
   * Gets the specified task in the todolist {@link TestContext#TODOLIST_ID}.
   * @param todoId the unique identifier of a task to do.
   * @return optionally the asked task or nothing if no such task exists.
   */
  public Optional<Todo> getTodo(String todoId) {
    TodoList todoList = TodoList.getById(TODOLIST_ID);
    return todoList.getAllTodos().stream()
        .filter(t -> t.getId().equals(todoId))
        .findFirst();
  }

  /**
   * Computes a new task to do for testing purpose.
   *
   * @return a new and non yet persisted task
   */
  public Todo computeNewTodo(User user) {
    return new Todo(user, "My new todo", "The new task to do");
  }
}
  