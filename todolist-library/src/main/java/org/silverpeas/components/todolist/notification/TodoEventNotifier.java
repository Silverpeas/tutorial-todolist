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
import org.silverpeas.core.annotation.Bean;
import org.silverpeas.core.notification.system.CDIResourceEventNotifier;
import org.silverpeas.core.notification.system.ResourceEvent;
import org.silverpeas.core.util.ServiceProvider;
import org.silverpeas.kernel.annotation.Technical;

/**
 * A notifier of events related to the todos. Such events are about actions that are operated on
 * a task to do, like the creation of a new task or its deletion. The system notification
 * mechanism is a way in Silverpeas to decorrelate the main business operation from both the
 * transverse business operations (like, for example, the user notification) and the technical ones
 * (like, for example, the indexation).
 * @author mmoquillon
 */
@Technical
@Bean
public class TodoEventNotifier extends CDIResourceEventNotifier<Todo, TodoEvent> {

  /**
   * Gets a notifier of {@link TodoEvent}s.
   * @return a {@link TodoEventNotifier} instance.
   */
  public static TodoEventNotifier get() {
    return ServiceProvider.getService(TodoEventNotifier.class);
  }

  @Override
  protected TodoEvent createResourceEventFrom(ResourceEvent.Type type, Todo... todos) {
    return new TodoEvent(type, todos);
  }
}
  