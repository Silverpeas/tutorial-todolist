package org.silverpeas.components.todolist.model;

import com.silverpeas.notification.builder.helper.UserNotificationHelper;
import com.silverpeas.subscribe.SubscriptionServiceFactory;
import com.silverpeas.subscribe.service.ComponentSubscriptionResource;
import com.stratelia.webactiv.beans.admin.ComponentInstLight;
import com.stratelia.webactiv.beans.admin.UserDetail;
import org.silverpeas.components.todolist.notification.TodoSubscribedUserNotification;
import org.silverpeas.components.todolist.repository.TodoRepository;
import org.silverpeas.components.todolist.repository.TodoRepositoryProvider;
import org.silverpeas.core.admin.OrganisationController;
import org.silverpeas.core.admin.OrganisationControllerFactory;
import org.silverpeas.persistence.Transaction;
import org.silverpeas.persistence.repository.OperationContext;
import org.silverpeas.search.indexEngine.model.FullIndexEntry;
import org.silverpeas.search.indexEngine.model.IndexEngineProxy;
import org.silverpeas.search.indexEngine.model.IndexEntryPK;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Todolist
 * @author mmoquillon
 */
public class TodoList {

  private String id;

  private TodoList(String id) {
    this.id = id;
  }

  public static final TodoList getById(String id) {
    return new TodoList(id);
  }

  /**
   * Gets all the todos in this list.
   * @return a list of todos.
   */
  public List<Todo> getAllTodos() {
    return TodoRepositoryProvider.getTodoRepository().getAllTodoInTodoList(getId());
  }

  /**
   * Gets the unique identifier of this todolist. The todolist is yet an instance of the TodoList
   * component and then its identifier is the component instance identifier.
   * @return the unique identifier of this todolist.
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the title of this todolist as expressed in the specified language. If no such title
   * exists
   * in the given language, then the title in the default language is returned (id est in French).
   * @param language the language in which the title should be.
   * @return the todolist title.
   */
  public String getTitle(final String language) {
    OrganisationController organisationController =
        OrganisationControllerFactory.getOrganisationController();
    ComponentInstLight componentInst = organisationController.getComponentInstLight(getId());
    return componentInst.getLabel(language);
  }

  /**
   * Adds a new todo into this todolist with the specified description. If the todo cannot be
   * added, then a runtime exception is thrown.
   * @param author the author of the new todo.
   * @param description a description of the task to do.
   */
  public void addTodo(final UserDetail author, final String description) {
    Transaction.performInOne(new Transaction.Process<Void>() {
      @Override
      public Void execute() {
        Todo todo = new Todo(getId(), author, description);
        TodoRepositoryProvider.getTodoRepository().save(OperationContext.fromUser(author), todo);

        indexTodo(todo);

        try {
          TodoSubscribedUserNotification notification = new TodoSubscribedUserNotification(todo);
          UserNotificationHelper.buildAndSend(notification);
        } catch (Exception ex) {
          Logger.getLogger(getClass().getSimpleName()).log(Level.WARNING, ex.getMessage());
        }

        return null;
      }
    });
  }

  /**
   * Removes the todo with the specified unique identifier from this todolist. If no such todo
   * exists in this list, then nothing is performed.
   * @param todoId the unique identifier of the todo.
   */
  public void removeTodo(final String todoId) {
    Transaction.performInOne(new Transaction.Process<Void>() {
      @Override
      public Void execute() {
        TodoRepository repository = TodoRepositoryProvider.getTodoRepository();
        Todo todo = repository.getById(todoId);
        if (todo != null) {
          repository.delete(todo);
        }
        return null;
      }
    });
  }

  /**
   * Deletes this todolist. It will not be longer available.
   * <p/>
   * All the ressources allocated for this todolist are also freed (user subscription, indexes,
   * ....)
   */
  public void delete() {
    Transaction.performInOne(new Transaction.Process<Void>() {
      @Override
      public Void execute() {
        List<Todo> todos = getAllTodos();
        for (Todo aTodo : todos) {
          unindexTodo(aTodo);
        }
        
        TodoRepository todoRepository = TodoRepositoryProvider.getTodoRepository();
        todoRepository.deleteTodoList(getId());

        SubscriptionServiceFactory.getFactory().getSubscribeService()
            .unsubscribeByResource(ComponentSubscriptionResource.from(getId()));
        return null;
      }
    });
  }

  public void index() {
    List<Todo> todos = getAllTodos();
    for (Todo aTodo : todos) {
      indexTodo(aTodo);
    }
  }

  private void indexTodo(final Todo todo) {
    FullIndexEntry indexEntry = new FullIndexEntry(getId(), Todo.TYPE, todo.getId());
    indexEntry.setTitle(todo.getDescription());
    indexEntry.setCreationDate(todo.getCreateDate());
    indexEntry.setCreationUser(todo.getCreatedBy());
    IndexEngineProxy.addIndexEntry(indexEntry);
  }

  private void unindexTodo(final Todo todo) {
    IndexEntryPK indexId = new IndexEntryPK(getId(), Todo.TYPE, todo.getId());
    IndexEngineProxy.removeIndexEntry(indexId);
  }
}
