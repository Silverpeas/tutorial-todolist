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
 * "http://www.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.silverpeas.components.todolist;

import org.silverpeas.core.test.BasicCoreWarBuilder;

/**
 * A builder of a WAR archive dedicated to the integration tests. Defining a specific build for
 * the project is a way to customize it for the context of the project and for all the
 * integration tests classes. The {@link BasicCoreWarBuilder} class provides useful methods to
 * easy the creation of the WAR archive (like for example adding a library from a Maven
 * dependency of the project).
 */
public class TodolistWarBuilder extends BasicCoreWarBuilder {

  /**
   * Constructs a war builder for the specified test class. It will load all the resources in the
   * same packages of the specified test class.
   * @param test the class of the test for which a war archive will be build.
   */
  protected <T> TodolistWarBuilder(final Class<T> test) {
    super(test);
    addPackages(true, this.getClass().getPackageName());
  }

  /**
   * Gets an instance of a war archive builder for the specified test class.
   * @return the instance of the war archive builder.
   */
  public static <T> TodolistWarBuilder onWarForTestClass(Class<T> test) {
    return new TodolistWarBuilder(test);
  }

}