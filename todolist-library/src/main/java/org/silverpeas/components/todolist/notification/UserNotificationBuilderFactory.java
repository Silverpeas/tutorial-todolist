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
package org.silverpeas.components.todolist.notification;

import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.core.annotation.Provider;
import org.silverpeas.core.notification.user.builder.UserNotificationBuilder;
import org.silverpeas.core.util.ServiceProvider;

/**
 * A factory of a {@link org.silverpeas.core.notification.user.builder.UserNotificationBuilder}
 * object. The factory wraps the concrete builder implementation used to notify users according to
 * the context of the notification. For instance, only one context is provided: the notification to
 * subscribers.
 * <p>
 * This factory is a way both to centralize the notification builder construction according to the
 * notification context, and to enable the unit tests to mock or stub such builders. For doing, it
 * is managed by the underlying IoC system.
 * </p>
 *
 * @author mmoquillon
 */
// TODO only the notification to subscribers is implemented here. Implement the manual
//  notification to users
@Provider
public class UserNotificationBuilderFactory {

  /**
   * We protects the constructor to impose to get an instance from the IoC container.
   */
  protected UserNotificationBuilderFactory() {
  }

  /**
   * Gets an instance of this factory.
   *
   * @return a {@link UserNotificationBuilderFactory} instance.
   */
  public static UserNotificationBuilderFactory get() {
    return ServiceProvider.getService(UserNotificationBuilderFactory.class);
  }

  /**
   * Creates a user notification builder for notifying the subscribers.
   *
   * @return a supplier of a builder of a notification to subscribers.
   */
  public UserNotificationBuilderSupplier createForSubscribers() {
    return TodoSubscribedUserNotificationBuilder::new;
  }

  /**
   * A supplier of a {@link UserNotificationBuilder} instance produced for the specified task to
   * do.
   */
  @FunctionalInterface
  public interface UserNotificationBuilderSupplier {

    /**
     * Supplies a {@link UserNotificationBuilder} instance to notify an event about the specified
     * task to do.
     *
     * @param theTodo the task to do
     * @return a {@link UserNotificationBuilder} initialized for the specified task.
     */
    UserNotificationBuilder aNotificationBuilderAbout(Todo theTodo);
  }
}
  