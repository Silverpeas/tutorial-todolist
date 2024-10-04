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

import org.silverpeas.core.admin.component.ComponentInstancePostConstruction;
import org.silverpeas.core.annotation.Service;
import org.silverpeas.kernel.annotation.Technical;

import javax.inject.Named;

/**
 * A technical service invoked by Silverpeas when a todolist application instance is being created.
 * The service does nothing here; it is just for illustration, for the tutorial. Such a service
 * is optional.
 * <p>
 * Services implementing the {@link ComponentInstancePostConstruction} interface are used to
 * allocate the resources required by the instances of the application. For example, in this
 * example, in the case the {@link org.silverpeas.components.todolist.model.TodoList} is an entity
 * mapped one-to-one to an application instance, it would be to create and persist it.
 * <p>
 * The post-construction services have to follow the convention rule on their naming:
 * <code>&lt;APPLICATION_NAME&gt;InstancePostConstruction</code>. Such a name can be explicitly set
 * by using the {@link Named} annotation.
 *
 * @author mmoquillon
 */
@Technical
@Service
@Named
public class TodolistInstancePostConstruction implements ComponentInstancePostConstruction {

  @Override
  public void postConstruct(String componentInstanceId) {
    // nothing to do: it is just for illustration
  }
}
  