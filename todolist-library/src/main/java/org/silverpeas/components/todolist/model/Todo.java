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
package org.silverpeas.components.todolist.model;

import org.silverpeas.components.todolist.repository.TodoRepository;
import org.silverpeas.core.admin.component.model.SilverpeasComponentInstance;
import org.silverpeas.core.admin.user.model.SilverpeasRole;
import org.silverpeas.core.admin.user.model.User;
import org.silverpeas.core.contribution.model.Contribution;
import org.silverpeas.core.contribution.model.ContributionIdentifier;
import org.silverpeas.core.persistence.Transaction;
import org.silverpeas.core.persistence.datasource.model.identifier.UuidIdentifier;
import org.silverpeas.core.persistence.datasource.model.jpa.SilverpeasJpaEntity;
import org.silverpeas.core.security.SecurableRequestCache;
import org.silverpeas.core.security.authorization.ComponentAccessControl;
import org.silverpeas.kernel.annotation.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

import static org.silverpeas.kernel.util.StringUtil.isNotDefined;

/**
 * A task in a todolist that has to be done by a user.
 * <p>
 * Its persistence is managed by the
 * {@link org.silverpeas.components.todolist.repository.TodoRepository} object.
 * <p>
 * In order to satisfy the OOP and hence to avoid anemic entities, all the business methods related
 * to the concept of a task to do should be declared within this entity. Nevertheless, their
 * execution can be delegated to a dedicated business service; we inverse here the control. Another
 * way to implement this inversion of control is to define a business entity wrapping (decorating
 * with the business operations) this persistent entity. Which has the responsibility of the
 * business operations is in fact an implementation detail; what is important, in the point of view
 * of the client, is to ask for such business operations to the object concerned by them, to the
 * concept related by them.
 */
@Entity
@Table(name = "SC_Todo")
@NamedQuery(name = "alltodos",
    query = "select t from Todo t where t.instanceId = :instanceId order by t.creationDate")
@NamedQuery(name = "deleteTodoList",
    query = "delete from Todo t where t.instanceId = :instanceId")
@NamedQuery(name = "deleteTodo",
    query = "delete from Todo t where t.id = :todoId and t.instanceId = :instanceId")
public class Todo extends SilverpeasJpaEntity<Todo, UuidIdentifier>
    implements Contribution {

  public static final String TYPE = "Todo";

  @Column(name = "instanceId", nullable = false)
  @NotNull
  private String instanceId;
  @Column(nullable = false)
  @Size(min = 1, max = 4000)
  @NotNull
  private String description;
  @Column(nullable = false)
  @NotEmpty
  @Size(min = 1, max = 4000)
  private String title;

  protected Todo() {
  }

  /**
   * Sets the todolist application instance that manages this task.
   *
   * @param instanceId the unique identifier of a todolist component instance.
   */
  void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  /**
   * Constructs a new task authored by the given user and with the specified title and description.
   *
   * @param author the user in Silverpeas authoring the task.
   * @param title a title, the subject of the task. It shouldn't be null nor empty.
   * @param description a description of the task to do. Can be empty but not null.
   */
  public Todo(@NonNull User author, @NonNull String title, @NonNull String description) {
    Objects.requireNonNull(author);
    Objects.requireNonNull(title);
    Objects.requireNonNull(description);
    setCreator(author);
    this.title = title;
    this.description = description;
  }

  @Override
  public ContributionIdentifier getIdentifier() {
    return ContributionIdentifier.from(instanceId, getId(), TYPE);
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * Updates the description of this task.
   * @param description the new description.
   */
  public void setDescription(@NonNull String description) {
    Objects.requireNonNull(description);
    this.description = description;
  }

  /**
   * Saves the change in this task. Fails if this task isn't persisted.
   * @throws IllegalStateException if this entity isn't persisted.
   */
  public void save() {
    if (isNotDefined(instanceId) || ! isPersisted()) {
      throw new IllegalStateException("No change can be saved as the todo isn't persisted!");
    }
    Transaction.performInOne(() -> {
      TodoRepository.get().save(this);
      return null;
    });
  }

  @Override
  public String getContributionType() {
    return TYPE;
  }

  @Override
  public boolean canBeAccessedBy(User user) {
    return SecurableRequestCache.canBeAccessedBy(user, getId(),
        u -> ComponentAccessControl.get().isUserAuthorized(user.getId(), instanceId));
  }

  @Override
  public boolean canBeModifiedBy(User user) {
    return SecurableRequestCache.canBeModifiedBy(user, getId(), u -> canBeAccessedBy(u) &&
        SilverpeasComponentInstance.getById(instanceId)
            .map(c -> c.getHighestSilverpeasRolesFor(u))
            .filter(r -> r.isGreaterThanOrEquals(SilverpeasRole.WRITER))
            .isPresent());
  }

  @Override
  public String toString() {
    return "Todo{" +
        "id='" + getId() +
        "', authorId=" + getCreatorId() +
        ", title='" + title + '\'' +
        "', description='" + description +
        "', creationDate='" + getCreationDate() +
        "', lastUpdaterId=" + getLastUpdaterId() +
        ", lastUpdateDate='" + getLastUpdateDate() +
        '}';
  }
}