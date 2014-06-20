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
package org.silverpeas.components.todolist.control;

import com.silverpeas.subscribe.SubscriptionService;
import com.silverpeas.subscribe.SubscriptionServiceFactory;
import com.silverpeas.subscribe.service.ComponentSubscription;
import com.stratelia.silverpeas.peasCore.AbstractComponentSessionController;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.peasCore.ComponentContext;
import com.stratelia.silverpeas.peasCore.servlets.annotation.Homepage;
import com.stratelia.silverpeas.peasCore.servlets.annotation.Invokable;
import com.stratelia.silverpeas.peasCore.servlets.annotation.InvokeAfter;
import com.stratelia.silverpeas.peasCore.servlets.annotation.LowestRoleAccess;
import com.stratelia.silverpeas.peasCore.servlets.annotation.RedirectToInternal;
import com.stratelia.silverpeas.peasCore.servlets.annotation.RedirectToInternalJsp;
import com.stratelia.silverpeas.peasCore.servlets.annotation.WebComponentController;
import com.stratelia.webactiv.SilverpeasRole;
import org.silverpeas.components.todolist.TodoComponentSettings;
import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.components.todolist.model.TodoList;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

/**
 * The Web Component Controller of the application.
 * <p/>
 * It takes in charge, per user, the web navigation of the user in the application. It is a session
 * scoped bean; it is instantiated for each user session.
 */
@WebComponentController(TodoComponentSettings.COMPONENT_NAME)
public class TodoWebController extends
    com.stratelia.silverpeas.peasCore.servlets.WebComponentController<TodoWebRequestContext> {

  private final TodoList currentTodoList;

  /**
   * Standard Web Controller Constructor.
   * @param mainSessionCtrl the main user session controller.
   * @param componentContext The component's context.
   */
  public TodoWebController(MainSessionController mainSessionCtrl,
    ComponentContext componentContext) {
    super(mainSessionCtrl, componentContext, TodoComponentSettings.MESSAGES_PATH,
    TodoComponentSettings.ICONS_PATH, TodoComponentSettings.SETTINGS_PATH);
    String componentId = componentContext.getCurrentComponentId();
    this.currentTodoList = TodoList.getById(componentId);
  }

  /**
   * This method is called one times once this web component controller is instantiated for a given
   * user.
   * You can perform here some specific treatments here. For example, you can register Web
   * navigation listeners that will be invoked at each navigation step change. For simple web
   * navigation, this method is usually empty.
   * @param context the web request context.
   */
  @Override
  protected void onInstantiation(final TodoWebRequestContext context) {
  }

  @Override
  protected void beforeRequestProcessing(final TodoWebRequestContext context) {
    super.beforeRequestProcessing(context);
    context.getRequest().setAttribute("todoListTitle", getCurrentTodoList().getTitle(getLanguage()));
  }

  /**
   * Prepares the rendering of the home page.
   * @param context the context of the incoming request.
   */
  @GET
  @Path("Main")
  @Homepage
  @RedirectToInternalJsp("main.jsp")
  @InvokeAfter({"isUserSubscribed"})
  public void home(TodoWebRequestContext context) {
    TodoList todoList = getCurrentTodoList();
    List<Todo> allTodos = todoList.getAllTodos();
    context.getRequest().setAttribute("alltodos", allTodos);
  }

  @GET
  @Path("newtodo")
  @RedirectToInternalJsp("newtodo.jsp")
  @LowestRoleAccess(SilverpeasRole.writer)
  public void newTodo(TodoWebRequestContext context) {
  }

  @POST
  @Path("addtodo")
  @RedirectToInternal("Main")
  @LowestRoleAccess(SilverpeasRole.writer)
  public void addTodo(TodoWebRequestContext context) {
    String description = context.getRequest().getParameter("description").trim();
    TodoList todoList = getCurrentTodoList();
    todoList.addTodo(context.getUser(), description);
  }

  @POST
  @Path("removetodo")
  @RedirectToInternal("Main")
  @LowestRoleAccess(SilverpeasRole.writer)
  public void removeTodo(TodoWebRequestContext context) {
    String todoId = context.getRequest().getParameter("todoId");
    TodoList todoList = getCurrentTodoList();
    todoList.removeTodo(todoId);
  }

  /**
   * Sets into request attributes the isUserSubscribed constant.
   * @param context the context of the incoming request.
   */
  @Invokable("isUserSubscribed")
  public void setIsUserSubscribed(TodoWebRequestContext context) {
    if (!getUserDetail().isAccessGuest()) {
      SubscriptionService subscriptionService = SubscriptionServiceFactory.getFactory().
        getSubscribeService();
      boolean isUserSubscribed = subscriptionService.existsSubscription(
          new ComponentSubscription(context.getUser().getId(), context.getComponentInstanceId()));
      context.getRequest().setAttribute("isUserSubscribed", isUserSubscribed);
    }
  }

  private TodoList getCurrentTodoList() {
    return this.currentTodoList;
  }
}