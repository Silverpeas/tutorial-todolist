/*
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
package org.silverpeas.components.todolist.notification;

import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.core.notification.system.AbstractResourceEvent;

import javax.validation.constraints.NotNull;

/**
 * A system event about actions implying a task to do. The construction of the object is usually
 * done by the notifier itself.
 *
 * @author mmoquillon
 */
public class TodoEvent extends AbstractResourceEvent<Todo> {

  /**
   * Constructs a new event about the specified action implying the given states of a task to do.
   *
   * @param type the type of the event. Its type qualify the action about which the event is
   * related.
   * @param todos the states of a task to do. There is no reification of the concept of a state of a
   * business entity, so it is carried by the entity itself. For example, in the case of the update
   * of a task to do, its state before the update and its state after the update will be passed
   * through two different instances but having the same identity.
   */
  public TodoEvent(Type type, @NotNull Todo... todos) {
    super(type, todos);
  }
}
  