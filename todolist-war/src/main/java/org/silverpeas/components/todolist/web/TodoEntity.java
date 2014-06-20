package org.silverpeas.components.todolist.web;

import com.silverpeas.util.StringUtil;
import com.silverpeas.web.Exposable;
import com.stratelia.webactiv.beans.admin.UserDetail;
import org.silverpeas.components.todolist.model.Todo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The entity of an HTTP request representing the state of a given Todo instance.
 * @author mmoquillon
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TodoEntity implements Exposable {

  @XmlElement(nillable = true)
  private URI uri;
  @XmlElement(nillable = true)
  private String id;
  @XmlElement(nillable = false, required = true)
  @NotNull
  @Size(min = 1, max = 2000)
  private String description;
  @XmlElement(nillable = true)
  private String authorName;

  protected TodoEntity(final Todo todo) {
    this.id = todo.getId();
    this.description = todo.getDescription();
    this.authorName = todo.getCreator().getDisplayedName();
  }

  protected TodoEntity() {
  }

  @Override
  public URI getURI() {
    return uri;
  }

  public final static TodoEntity fromTodo(final Todo todo, final UriInfo uri) {
    return new TodoEntity(todo).withAsUri(uri.getAbsolutePathBuilder().path(todo.getId()).build());
  }

  public final static List<TodoEntity> fromTodos(final List<Todo> todos, final UriInfo uri) {
    List<TodoEntity> entities = new ArrayList<TodoEntity>();
    for (Todo aTodo: todos) {
      entities.add(fromTodo(aTodo, uri));
    }
    return entities;
  }

  private TodoEntity withAsUri(final URI uri) {
    this.uri = uri;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public String getAuthorName() {
    return authorName;
  }
}
