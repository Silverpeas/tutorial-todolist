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

import org.silverpeas.components.todolist.repository.TodoRepository;
import org.silverpeas.core.admin.component.ComponentInstancePreDestruction;
import org.silverpeas.core.annotation.Service;
import org.silverpeas.kernel.annotation.Technical;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 * A technical service invoked by Silverpeas when a todolist application instance is being deleted.
 * The service, when invoked, deletes the todolist mapped with the component instance and all its
 * todos. Although such a service is optional, it is always implemented in order to ensure all the
 * resources and contributions managed by the application instance are correctly freed before its
 * deletion.
 * <p>
 * Services implementing the {@link ComponentInstancePreDestruction} interface are used to freed the
 * resources and contributions managed by the instances of the application.
 * <p>
 * The pre-destruction services have to follow the convention rule on their naming:
 * <code>&lt;APPLICATION_NAME&gt;InstancePreDestruction</code>. Such a name can be explicitly set
 * by using the {@link Named} annotation.
 *
 * @author mmoquillon
 */
@Technical
@Service
@Named
public class TodolistInstancePreDestruction implements ComponentInstancePreDestruction {

  @Inject
  private TodoRepository repository;

  @Override
  @Transactional
  public void preDestroy(String componentId) {
    TodoRepository todoRepository = TodoRepository.get();
    todoRepository.deleteTodoList(componentId);
  }
}
