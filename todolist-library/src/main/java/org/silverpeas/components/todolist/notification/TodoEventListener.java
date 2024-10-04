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

import org.silverpeas.core.notification.system.CDIAfterSuccessfulTransactionResourceEventListener;

/**
 * A listener of a system events relating the Todos. Any services dedicated to perform additional
 * treatments after some operations applied on the todos have to extend this class. The listeners
 * extending this class will be invoked only if the transaction within which the operations have
 * been done isn't succeeds.
 *
 * @author mmoquillon
 */
public abstract class TodoEventListener
    extends CDIAfterSuccessfulTransactionResourceEventListener<TodoEvent> {
}
  