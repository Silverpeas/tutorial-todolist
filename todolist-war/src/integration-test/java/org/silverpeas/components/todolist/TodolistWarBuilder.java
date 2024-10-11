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
package org.silverpeas.components.todolist;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.silverpeas.components.todolist.test.SessionManagementService;
import org.silverpeas.core.test.BasicCoreWarBuilder;
import org.silverpeas.web.test.WarBuilder4Web;

import java.io.File;

/**
 * A builder of a WAR archive dedicated to the integration tests. Defining a specific build for
 * the project is a way to customize it for the context of the project and for all the
 * integration tests classes. The {@link BasicCoreWarBuilder} class provides useful methods to
 * easy the creation of the WAR archive (like for example adding a library from a Maven
 * dependency of the project).
 */
public class TodolistWarBuilder extends WarBuilder4Web {

  /**
   * Constructs a war builder for the specified test class. It will load all the resources in the
   * same packages of the specified test class.
   * @param test the class of the test for which a war archive will be build.
   */
  protected <T> TodolistWarBuilder(final Class<T> test) {
    super(test);
    addMavenDependenciesWithPersistence("org.silverpeas.components.todolist:silverpeas-todolist");
    addMavenDependencies("org.silverpeas.core:silverpeas-core-web-test");
    addMavenDependencies("org.silverpeas.core:silverpeas-core-web");
    addPackages(true, test.getPackageName());
    addClasses(SessionManagementService.class, TestContext.class);
    addRequiredDependenciesForIT();
  }

  /**
   * Gets an instance of a war archive builder for the specified test class.
   * @return the instance of the war archive builder.
   */
  public static <T> TodolistWarBuilder onWarForTestClass(Class<T> test) {
    return new TodolistWarBuilder(test);
  }

  /**
   * Sets REST Web Service environment.
   * @return the instance of the war archive builder.
   */
  public TodolistWarBuilder addRESTWebServiceEnvironment() {
    addMavenDependencies("edu.psu.swe.commons:commons-jaxrs");
    addMavenDependenciesWithoutTransitivity("org.silverpeas.core:silverpeas-core-rs");
    return this;
  }

  private void addRequiredDependenciesForIT() {
    // dependencies required to run an integration test in a web project
    createMavenDependencies("org.silverpeas.core.services:silverpeas-core-pdc");
    createMavenDependencies("org.silverpeas.core.services:silverpeas-core-search");
    createMavenDependencies("org.silverpeas.core.services:silverpeas-core-viewer");
    createMavenDependencies("org.silverpeas.core.services:silverpeas-core-sharing");
    createMavenDependencies("org.silverpeas.core.services:silverpeas-core-comment");
    createMavenDependencies("org.silverpeas.core.services:silverpeas-core-personalorganizer");
    createMavenDependencies("org.silverpeas.core.services:silverpeas-core-workflow");
    createMavenDependencies("org.silverpeas.core.services:silverpeas-core-chat");
    createMavenDependencies("org.silverpeas.core.services:silverpeas-core-mylinks");
    applyManually(w -> {
      File[] jars = Maven.resolver()
          .loadPomFromFile("pom.xml")
          .resolve("org.jboss.resteasy:resteasy-html")
          .withoutTransitivity()
          .asFile();
      w.addAsLibraries(jars);
    });
  }
}