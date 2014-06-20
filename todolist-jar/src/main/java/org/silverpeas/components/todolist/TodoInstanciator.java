/**
 * Copyright (C) 2000 - 2014 Silverpeas
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
 * "http://www.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.components.todolist;

import com.silverpeas.admin.components.ComponentsInstanciatorIntf;
import com.silverpeas.admin.components.InstanciationException;
import com.silverpeas.subscribe.SubscriptionServiceFactory;
import com.silverpeas.subscribe.service.ComponentSubscriptionResource;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import org.silverpeas.components.todolist.model.TodoList;
import org.silverpeas.components.todolist.repository.TodoRepository;
import org.silverpeas.components.todolist.repository.TodoRepositoryProvider;
import org.silverpeas.persistence.Transaction;

import java.sql.Connection;

/**
 * The instanciator is in charge of the spawning and of the cleaning up of an instance of the
 * todolist application.
 */
public class TodoInstanciator implements ComponentsInstanciatorIntf {

  public TodoInstanciator() {
  }

  @Override
  public void create(Connection con, String spaceId, String componentId, String userId) throws InstanciationException {
	  SilverTrace.info("todolist","TodoInstanciator.create()","root.MSG_GEN_ENTER_METHOD", "space = "+spaceId+", componentId = "+componentId+", userId ="+userId);

	  SilverTrace.info("todolist","TodoInstanciator.create()","root.MSG_GEN_EXIT_METHOD");
  }

  @Override
  public void delete(Connection con, String spaceId, String componentId, String userId) throws InstanciationException {
	  SilverTrace.info("todolist","TodoInstanciator.delete()","root.MSG_GEN_ENTER_METHOD","space = "+spaceId+", componentId = "+componentId+", userId ="+userId);

    TodoList todoList = TodoList.getById(componentId);
    todoList.delete();

	  SilverTrace.info("todolist","TodoInstanciator.delete()","root.MSG_GEN_EXIT_METHOD");
  }
}