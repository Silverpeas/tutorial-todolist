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
package org.silverpeas.components.todolist.control;

import org.silverpeas.components.todolist.TodolistSettings;
import org.silverpeas.components.todolist.model.TodoList;
import org.silverpeas.core.subscription.SubscriptionService;
import org.silverpeas.core.subscription.SubscriptionServiceProvider;
import org.silverpeas.core.subscription.service.ComponentSubscription;
import org.silverpeas.core.web.mvc.controller.ComponentContext;
import org.silverpeas.core.web.mvc.controller.MainSessionController;
import org.silverpeas.core.web.mvc.webcomponent.annotation.*;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * The Web Component Controller of the application. The controller is instantiated by Silverpeas
 * when the user access the first time the component instance. It is user session scoped, meaning
 * its life-cycle belongs to the session of the user. The goal of the controller is first to
 * translate the asks of the user (through incoming HTTP requests) to invocations to the business
 * layer to perform the asked functional operations, and then, according to the result of the
 * invocations, to navigate the user to the next web view (which can be either a web page or a an
 * updated widget).
 * <p>
 * The Web controller belongs to the Silverpeas MVC framework. The framework provides its own
 * annotations for user navigation and uses the JAX-RS ones to declare web functions (with the
 * {@link Path} annotation) and supported HTTP method (mainly {@link GET} et {@link POST}
 * annotations). For each incoming request, the MVC framework looks for those annotations to
 * figuring out the correct method of the controller to invoke and then the next step of the web
 * user navigation once the request is processed. Additional treatments can be also invoked by the
 * framework if the corresponding annotations are found at the method level, like for example the
 * {@link InvokeBefore} or the {@link InvokeAfter} ones which specify what operations to invoke
 * respectively before and after the method of the controller.
 * </p>
 */
@WebComponentController(TodolistSettings.COMPONENT_NAME)
public class TodoWebController extends
    org.silverpeas.core.web.mvc.webcomponent.WebComponentController<TodoWebRequestContext> {

  private final transient TodoList currentTodoList;

  /**
   * Standard Web Controller Constructor. The constructor is invoked automatically by Silverpeas.
   *
   * @param mainSessionCtrl the main user session controller.
   * @param componentContext The component instance's context.
   */
  public TodoWebController(MainSessionController mainSessionCtrl,
      ComponentContext componentContext) {
    super(mainSessionCtrl, componentContext, TodolistSettings.MESSAGES_PATH,
        TodolistSettings.ICONS_PATH, TodolistSettings.SETTINGS_PATH);
    String componentId = componentContext.getCurrentComponentId();
    this.currentTodoList = TodoList.getById(componentId);
  }

  /**
   * This method is invoked one time by the Silverpeas MVC framework once this web component
   * controller is instantiated for a given user. You can perform here some specific treatments
   * here. For example, you can register Web navigation listeners that will be invoked at each
   * navigation step change. For simple web navigation, this method is usually leaved empty like
   * here.
   *
   * @param context the web request context.
   */
  @Override
  protected void onInstantiation(final TodoWebRequestContext context) {
    // nothing to do
  }

  /**
   * This method is invoked by the Silverpeas MVN framework each time an HTTP request is received
   * and just before to be processed by a method of this controller. It is the opportunity to set
   * additional request attributes or to set up some properties required for further treatments.
   *
   * @param context the web context of the user request.
   */
  @Override
  protected void beforeRequestProcessing(final TodoWebRequestContext context) {
    super.beforeRequestProcessing(context);
    context.getRequest().setAttribute("todoListTitle",
        getCurrentTodoList().getTitle(getLanguage()));
  }

  /**
   * This method is invoked each time the user accesses the main page of the application instance
   * (the todolist). The mapping between this method and the home page of the application
   * instance is defined by the {@link Homepage} annotation. Only the GET HTTP method is taken in
   * charge ({@link GET} annotation). Once the resources are prepared for their rendering in the
   * web page, an additional method has to be invoked (the one indicated by the {@link InvokeAfter}
   * annotation) and then the user is redirected to the web page generated by the specified JSP
   * template indicated as value in the {@link RedirectToInternalJsp} annotation.
   *
   * @param context the context of the user request.
   */
  @GET
  @Path("Main")
  @Homepage
  @RedirectToInternalJsp("home.jsp")
  @InvokeAfter({"isUserSubscribed"})
  @SuppressWarnings("VoidMethodAnnotatedWithGET")
  public void home(TodoWebRequestContext context) {
    // nothing to do here. Data will be loaded by javascript in web page
  }

  /**
   * Sets into request attributes the isUserSubscribed constant.
   *
   * @param context the context of the incoming request.
   */
  @Invokable("isUserSubscribed")
  public void setIsUserSubscribed(TodoWebRequestContext context) {
    if (!getUserDetail().isAccessGuest()) {
      SubscriptionService subscriptionService = SubscriptionServiceProvider.getSubscribeService();
      boolean isUserSubscribed = subscriptionService.existsSubscription(
          new ComponentSubscription(context.getUser().getId(), context.getComponentInstanceId()));
      context.getRequest().setAttribute("isUserSubscribed", isUserSubscribed);
    }
  }

  private TodoList getCurrentTodoList() {
    return this.currentTodoList;
  }
}