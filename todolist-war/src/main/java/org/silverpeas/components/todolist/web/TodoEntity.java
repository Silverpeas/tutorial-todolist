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
import org.silverpeas.core.web.rs.WebEntity;
import org.silverpeas.core.webapi.util.UserEntity;
import org.silverpeas.kernel.util.StringUtil;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static org.silverpeas.kernel.util.StringUtil.isDefined;

/**
 * The web entity representing a task to do. Such entities are carries by HTTP requests and
 * responses and are usually encoded in JSON. Nevertheless, in order to support both XML and JSON
 * serialization of the web entities carried in the HTTP requests and responses, the JAXB
 * annotations are used to qualify the entity properties to serialize.
 *
 * @author mmoquillon
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TodoEntity implements WebEntity {

  @XmlElement(nillable = true)
  private URI uri;
  @XmlElement(nillable = true)
  private String id;
  @XmlElement(required = true)
  @NotNull
  @Size(min = 1, max = 2000)
  private String description;
  @XmlElement(required = true)
  @NotNull
  private UserEntity author;
  @XmlElement(nillable = true)
  private String title;

  protected TodoEntity(final Todo todo) {
    this.id = todo.getId();
    this.title = todo.getTitle();
    this.description = todo.getDescription();
    this.author = new UserEntity(todo.getCreator());
  }

  @SuppressWarnings("unused")
  protected TodoEntity() {
    // dedicated to the deserialization mechanism
  }

  @Override
  public URI getURI() {
    return uri;
  }

  /**
   * Constructs a new entity representation of the specified task.
   *
   * @param theTodo the task to represent
   * @param uriBuilder the builder of the URI of the requested web resource initialized with path to
   * the todolist
   * @return the web entity
   */
  public static TodoEntity fromTodo(final Todo theTodo, final UriBuilder uriBuilder) {
    return new TodoEntity(theTodo)
        .withAsUri(uriBuilder.path(theTodo.getId()).build());
  }

  /**
   * Constructs a list of web entity representations of the specified todos.
   *
   * @param todos a list of todos to represent
   * @param uriBuilderSupplier a supplier of a builder of URI initialized with the base URI of the
   * todolist web resource.
   * @return a list with all the web entities
   */
  public static List<TodoEntity> fromTodos(final List<Todo> todos,
      final Supplier<UriBuilder> uriBuilderSupplier) {
    List<TodoEntity> entities = new ArrayList<>();
    for (Todo aTodo : todos) {
      entities.add(fromTodo(aTodo, uriBuilderSupplier.get()));
    }
    return entities;
  }

  private TodoEntity withAsUri(final URI uri) {
    this.uri = uri;
    return this;
  }

  /**
   * Is this task a new one?
   *
   * @return true if this task doesn't exist in the referred todolist, false otherwise.
   */
  public boolean isNew() {
    return StringUtil.isDefined(this.id);
  }

  /**
   * Is the data of this task are valid?
   * @return true if all the required properties are defined, false otherwise.
   */
  public boolean isValid() {
    return isDefined(title) && isDefined(description) && author != null;
  }

  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public UserEntity getAuthor() {
    return author;
  }

  public String getTitle() {
    return title;
  }

  public void setDescription(String newDescription) {
    this.description = newDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    TodoEntity that = (TodoEntity) o;
    return Objects.equals(uri, that.uri)
        && Objects.equals(id, that.id)
        && Objects.equals(description, that.description)
        && Objects.equals(author.getId(), that.author.getId())
        && Objects.equals(title, that.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri, id, description, author.getId(), title);
  }

  @Override
  public String toString() {
    return "TodoEntity{" +
        "uri=" + uri +
        ", id='" + id + '\'' +
        ", description='" + description + '\'' +
        ", author=" + author.getId() +
        ", title='" + title + '\'' +
        '}';
  }
}
