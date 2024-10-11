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

import org.silverpeas.components.todolist.model.TodoList;
import org.silverpeas.components.todolist.service.TodoIndexation;
import org.silverpeas.core.admin.component.model.SilverpeasComponentInstance;
import org.silverpeas.core.annotation.Service;
import org.silverpeas.core.web.index.components.ComponentIndexation;
import org.silverpeas.kernel.annotation.Technical;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * The indexer is in charge of the indexation of all the todos managed by this application. Is is
 * invoked when an administrator asks to reindex all the resources managed by the application
 * instance.
 */
@Technical
@Service
@Singleton
@Named("todolist" + ComponentIndexation.QUALIFIER_SUFFIX)
public class TodolistIndexer implements ComponentIndexation {

  @Inject
  private TodoIndexation indexation;

  @Override
  public void index(SilverpeasComponentInstance silverpeasComponentInstance) {
    TodoList todoList = TodoList.getById(silverpeasComponentInstance.getId());
    todoList.getAllTodos().forEach(indexation::index);
  }
}