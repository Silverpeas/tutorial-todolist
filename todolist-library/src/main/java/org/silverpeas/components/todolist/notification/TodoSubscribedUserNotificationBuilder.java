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
import org.silverpeas.core.notification.user.UserSubscriptionNotificationBehavior;
import org.silverpeas.core.notification.user.builder.AbstractContributionTemplateUserNotificationBuilder;
import org.silverpeas.core.notification.user.client.constant.NotifAction;
import org.silverpeas.core.subscription.constant.SubscriberType;
import org.silverpeas.core.subscription.util.SubscriptionSubscriberMapBySubscriberType;

import java.util.Collection;

import static org.silverpeas.core.subscription.service.ResourceSubscriptionProvider.getSubscribersOfComponent;

/**
 * A notification of users subscribed to the change in the application. The content of the
 * notification is taken from a template and the notification is built with the data of the
 * {@link org.silverpeas.components.todolist.model.Todo} concerned by the change.
 * <p>
 * The notification builder is a way to parameterize the notification to send. With it, a
 * parameterized {@link org.silverpeas.core.notification.user.UserNotification} object can be then
 * created before using it to send the actual notification to the users by using their preferred
 * way of communication (popup, mail, ...)
 * </p>
 */
// TODO the notification to the subscribers and only about new todos. Implement also for an
//  update and a deletion.
public final class TodoSubscribedUserNotificationBuilder
    extends AbstractContributionTemplateUserNotificationBuilder<Todo>
    implements UserSubscriptionNotificationBehavior {

  private final SubscriptionSubscriberMapBySubscriberType subscriberIdsByTypes;

  public TodoSubscribedUserNotificationBuilder(final Todo resource) {
    super(resource);
    subscriberIdsByTypes =
        getSubscribersOfComponent(getComponentInstanceId()).indexBySubscriberType();
  }

  @Override
  protected NotifAction getAction() {
    return NotifAction.CREATE;
  }

  @Override
  protected String getTemplatePath() {
    return "todolist";
  }

  @Override
  protected String getSender() {
    return getResource().getCreatorId();
  }

  @Override
  protected String getTemplateFileName() {
    return "todoSubscribedUserNotification";
  }

  @Override
  protected Collection<String> getUserIdsToNotify() {
    return subscriberIdsByTypes.get(SubscriberType.USER).getAllIds();
  }

  @Override
  protected Collection<String> getGroupIdsToNotify() {
    return subscriberIdsByTypes.get(SubscriberType.GROUP).getAllIds();
  }

}