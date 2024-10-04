package org.silverpeas.components.todolist.control;

import com.silverpeas.web.mock.OrganizationControllerMockWrapper;
import com.stratelia.silverpeas.peasCore.ComponentContext;
import com.stratelia.silverpeas.peasCore.MainSessionController;
import com.stratelia.silverpeas.peasCore.servlets.WebMessager;
import com.stratelia.webactiv.beans.admin.UserDetail;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.silverpeas.core.admin.OrganisationController;
import org.silverpeas.core.admin.OrganisationControllerFactory;
import org.silverpeas.persistence.jpa.RepositoryBasedTest;
import org.silverpeas.search.indexEngine.model.IndexEngineProxy;
import org.silverpeas.servlet.HttpRequest;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IndexEngineProxy.class)
public class TodoWebControllerTest extends RepositoryBasedTest {

  private static final String INSTANCE_ID = "todolist1";

  private TodoWebController webController;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    MainSessionController mainSessionCtrl = mock(MainSessionController.class);
    ComponentContext componentContext = mock(ComponentContext.class);
    when(componentContext.getCurrentComponentId()).thenReturn(INSTANCE_ID);
    webController = new TodoWebController(mainSessionCtrl, componentContext);
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
  }

  @Override
  public String getDataSetPath() {
    return "org/silverpeas/components/todolist/todolist-dataset.xml";
  }

  @Test
  public void testHome() throws Exception {
    TodoWebRequestContext ctx = aTodoWebRequestContext();
    HttpRequest request = ctx.getRequest();
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
        Object[] arguments = invocationOnMock.getArguments();
        List<Todo> todos = (List<Todo>) arguments[1];
        assertThat(todos.size(), is(3));
        assertThat(todos.get(0).getId(), is("todo_1"));
        assertThat(todos.get(1).getId(), is("todo_2"));
        assertThat(todos.get(2).getId(), is("todo_3"));
        return null;
      }
    }).when(request).setAttribute("alltodos", eq(any(List.class)));

    webController.home(ctx);
  }

  @Test
  public void testAddTodo() throws Exception {
    PowerMockito.mockStatic(IndexEngineProxy.class);

    IDataSet ds = getDataSet();
    int todoCount = ds.getTable("sc_todo").getRowCount();
    TodoWebRequestContext ctx = aTodoWebRequestContext();
    HttpRequest request = ctx.getRequest();
    when(request.getParameter("description")).thenReturn("a new todo");

    webController.addTodo(ctx);

    ds = getActualDataSet();
    ITable todos = ds.getTable("sc_todo");
    assertThat(todos.getRowCount(), is(todoCount + 1));
  }

  @Test
  public void testRemoveTodo() throws Exception {
    PowerMockito.mockStatic(IndexEngineProxy.class);

    IDataSet ds = getDataSet();
    int todoCount = ds.getTable("sc_todo").getRowCount();
    TodoWebRequestContext ctx = aTodoWebRequestContext();
    HttpRequest request = ctx.getRequest();
    when(request.getParameter("todoId")).thenReturn("todo_1");

    webController.removeTodo(ctx);

    ds = getActualDataSet();
    ITable todos = ds.getTable("sc_todo");
    assertThat(todos.getRowCount(), is(todoCount - 1));
    for (int i = 0; i < todoCount - 1; i++) {
      assertThat((String) todos.getValue(i, "id"), is(not("todo_1")));
    }
  }

  private TodoWebRequestContext aTodoWebRequestContext() {
    TodoWebRequestContext context = mock(TodoWebRequestContext.class);
    HttpRequest request = mock(HttpRequest.class);
    Map<String, String> pathVariables = mock(Map.class);
    when(context.getRequest()).thenReturn(request);
    when(context.getPathVariables()).thenReturn(pathVariables);
    when(context.getUser()).thenReturn(aUser());
    when(context.getComponentInstanceId()).thenReturn(INSTANCE_ID);
    when(context.getComponentUriBase()).thenReturn("/" + INSTANCE_ID);
    when(context.getMessager()).thenReturn(WebMessager.getInstance());
    return context;
  }

  private UserDetail aUser() {
    UserDetail user = new UserDetail();
    user.setId("3");
    user.setFirstName("Toto");
    user.setLastName("Chez-les-papoos");
    return user;
  }

  private OrganisationController getOrganisationController() {
    OrganizationControllerMockWrapper mockWrapper =
        (OrganizationControllerMockWrapper) OrganisationControllerFactory
            .getOrganisationController();
    return mockWrapper.getOrganizationControllerMock();
  }
}