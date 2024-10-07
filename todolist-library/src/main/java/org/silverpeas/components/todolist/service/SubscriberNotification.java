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

package org.silverpeas.components.todolist.service;

import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.components.todolist.notification.TodoEvent;
import org.silverpeas.components.todolist.notification.TodoEventListener;
import org.silverpeas.components.todolist.notification.UserNotificationBuilderFactory;
import org.silverpeas.core.annotation.Service;

/**
 * Service to notify the users subscribed to changes in a todolist about the creation, the deletion,
 * or the update of a given task to do.
 *
 * @author mmoquillon
 */
// For instance, only events about the creation of todos are fired. TODO implement the others notifications
@Service
public class SubscriberNotification extends TodoEventListener {

  // TODO implement it
  @Override
  public void onDeletion(TodoEvent event) throws Exception {
    super.onDeletion(event);
  }

  // TODO implement it
  @Override
  public void onUpdate(TodoEvent event) throws Exception {
    super.onUpdate(event);
  }

  @Override
  public void onCreation(TodoEvent event) {
    // the creation of a new entity is translated as a state transition of such an entity from
    // nothing (hence null) to the entity itself. This is why we get only the endpoint of the
    // transition
    Todo newTodo = event.getTransition().getAfter();
    UserNotificationBuilderFactory.get()
        .createForSubscribers()
        .aNotificationBuilderAbout(newTodo)
        .build()
        .send();
  }
}
  