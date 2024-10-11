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

package org.silverpeas.components.todolist.service;

import org.silverpeas.components.todolist.model.Todo;
import org.silverpeas.components.todolist.notification.TodoEvent;
import org.silverpeas.components.todolist.notification.TodoEventListener;
import org.silverpeas.core.annotation.Service;
import org.silverpeas.core.index.indexing.model.FullIndexEntry;
import org.silverpeas.core.index.indexing.model.IndexEngineProxy;
import org.silverpeas.core.index.indexing.model.IndexEntryKey;
import org.silverpeas.kernel.annotation.Technical;

/**
 * Service to add and update indexes on the tasks to do when a new task is created, updated or
 * deleted.
 *
 * @author mmoquillon
 */
// For instance, only events about the creation of todos are fired. TODO implement the others notifications
@Technical
@Service
public class TodoIndexation extends TodoEventListener {

  @Override
  public void onDeletion(TodoEvent event) {
    // the deletion of an entity is translated as a state transition from the entity itself to
    // nothing (hence null). This is why we get only the starting point of the transition
    Todo deleted = event.getTransition().getBefore();
    var indexEntryKey = new IndexEntryKey(deleted.getIdentifier().getComponentInstanceId(),
        deleted.getContributionType(), deleted.getId());
    IndexEngineProxy.removeIndexEntry(indexEntryKey);
  }

  @Override
  public void onUpdate(TodoEvent event) {
    // the update of an existing entity is translated as a state transition of such an entity from
    // itself before change to itself after change. And as we are only interested by the change,
    // id est by the new state of the entity, we get only the endpoint of the transition
    Todo updated = event.getTransition().getAfter();
    index(updated);
  }

  @Override
  public void onCreation(TodoEvent event) {
    // the creation of a new entity is translated as a state transition of such an entity from
    // nothing (hence null) to the entity itself. This is why we get only the endpoint of the
    // transition
    Todo created = event.getTransition().getAfter();
    index(created);
  }

  /**
   * Indexes the specified task to do.
   * @param theTodo the task to index
   */
  public void index(Todo theTodo) {
    if (theTodo.isIndexable()) {
      // always check the entity is indexable. Indeed, this attribute can be computed from some
      // entity properties and, with as the application can evolve over time, we ensure here to
      // take into account any possible evolutions
      String appId = theTodo.getIdentifier().getComponentInstanceId();
      FullIndexEntry indexEntry =
          new FullIndexEntry(new IndexEntryKey(appId, theTodo.getContributionType(),
              theTodo.getId()));
      indexEntry.setTitle(theTodo.getTitle());
      indexEntry.setPreview(theTodo.getDescription());
      indexEntry.setCreationDate(theTodo.getCreationDate());
      indexEntry.setCreationUser(theTodo.getCreatorId());

      IndexEngineProxy.addIndexEntry(indexEntry);
    }
  }
}
  