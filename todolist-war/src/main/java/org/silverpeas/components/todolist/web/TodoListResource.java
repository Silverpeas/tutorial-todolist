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
package org.silverpeas.components.todolist.web;

import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.components.todolist.model.TodoList;
import org.silverpeas.core.annotation.WebService;
import org.silverpeas.core.web.rs.RESTWebService;
import org.silverpeas.core.web.rs.annotation.Authorized;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

/**
 * A web resource representing a todolist. It is a REST-based Web service based upon JAX-RS.
 * Invocations of this web resource can only be done by an authorized (and hence authenticated) user
 * (annotation {@link Authorized}). The authentication and authorization is checked automatically by
 * Silverpeas; the authorisation validation checks the user has the rights to access the application
 * instance. The validation of the specific permissions to invoke some operations have to be done in
 * the corresponding methods by delegating both the control and the execution of the operation code
 * itself to the {@link RESTWebService#process(WebTreatment)} protected method; by default this
 * method ensures the user plays WRITER as lower role.
 * <p>
 * The use of REST-based web service in Silverpeas is both for AJAX requests (to perform in an
 * asynchronous way some operations and to update dynamically the web view) and for external tool.
 * </p>
 *
 * @author mmoquillon
 */
@WebService
@Path(TodoListResource.PATH + "/{componentId}")
@Authorized
public class TodoListResource extends RESTWebService {

  static final String PATH = "todolist";

  @PathParam("componentId")
  private String componentId;

  @Override
  public String getComponentId() {
    return componentId;
  }

  @Override
  protected String getResourceBasePath() {
    return PATH;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<TodoEntity> getAllTodos() {
    TodoList todoList = TodoList.getById(getComponentId());
    return asWebEntities(todoList.getAllTodos());
  }

  @Path("{todoId}")
  @DELETE
  public Response removeTodo(@PathParam("todoId") String todoId) {
    return process(() -> {
      TodoList todoList = TodoList.getById(getComponentId());
      Todo todo = todoList.getAllTodos().stream()
          .filter(t -> t.getId().equals(todoId))
          .findFirst()
          .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
      todoList.removeTodo(todo.getId());
      return Response.noContent().build();
    }).execute();

  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addTodo(TodoEntity todo) {
    return process(() -> {
      TodoList todoList = TodoList.getById(getComponentId());
      Todo created = todoList.addTodo(new Todo(getUser(), todo.getTitle(), todo.getDescription()));
      URI newTodoUri = getUriBuilder().path(created.getId()).build();
      return Response.created(newTodoUri)
          .entity(asWebEntities(todoList.getAllTodos())).build();
    }).execute();
  }

  private List<TodoEntity> asWebEntities(final List<Todo> allTodos) {
    return TodoEntity.fromTodos(allTodos, this::getUriBuilder);
  }

  private UriBuilder getUriBuilder() {
    return getUri().getBaseUriBuilder().path(PATH).path(getComponentId());
  }
}
