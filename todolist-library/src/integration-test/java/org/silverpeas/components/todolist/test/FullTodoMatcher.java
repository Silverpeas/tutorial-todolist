package org.silverpeas.components.todolist.test;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.silverpeas.components.todolist.model.Todo;

import java.util.Objects;

/**
 * A matcher on all the properties of a task to do
 *
 * @author mmoquillon
 */
public class FullTodoMatcher extends TypeSafeMatcher<Todo> {

  private final Todo expected;

  public static FullTodoMatcher isTodo(Todo expected) {
    return new FullTodoMatcher(expected);
  }

  private FullTodoMatcher(Todo todo) {
    this.expected = todo;
  }

  @Override
  protected boolean matchesSafely(Todo actual) {
    return actual.getId().equals(expected.getId()) &&
        actual.getTitle().equals(expected.getTitle()) &&
        actual.getDescription().equals(expected.getDescription()) &&
        actual.getCreator().equals(expected.getCreator()) &&
        Objects.equals(actual.getCreationDate(), expected.getCreationDate()) &&
        Objects.equals(actual.getLastUpdateDate(), expected.getLastUpdateDate()) &&
        Objects.equals(actual.getLastUpdaterId(), expected.getLastUpdaterId());
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(expected.toString());
  }
}
  