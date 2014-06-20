package org.silverpeas.components.todolist.web;

import com.silverpeas.annotation.Authorized;
import com.silverpeas.annotation.RequestScoped;
import com.silverpeas.annotation.Service;
import com.silverpeas.web.RESTWebService;
import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.components.todolist.model.TodoList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * A REST-based web service representing a todo in a todolist.
 * @author mmoquillon
 */
@Service
@RequestScoped
@Path("todolist/{componentId}")
@Authorized
public class TodoResource extends RESTWebService {

  @PathParam("componentId")
  private String componentId;

  @Override
  public String getComponentId() {
    return componentId;
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
    TodoList todoList = TodoList.getById(getComponentId());
    todoList.removeTodo(todoId);
    return Response.ok().build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<TodoEntity> addTodo(TodoEntity todo) {
    TodoList todoList = TodoList.getById(getComponentId());
    todoList.addTodo(getUserDetail(), todo.getDescription());
    return asWebEntities(todoList.getAllTodos());
  }

  private List<TodoEntity> asWebEntities(final List<Todo> allTodos) {
    return TodoEntity.fromTodos(allTodos, getUriInfo());
  }

}
