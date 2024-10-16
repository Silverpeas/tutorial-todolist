This project is an example of how to develop a Silverpeas Component or, in another term, an
application embedded in Silverpeas. Like any other Silverpeas components, it relies upon
Silverpeas Core.

A Silverpeas Component is a project that is made up of three modules:

* *Configuration*: it gathers all the resources required by the application to run like, for
  examples, the l10n bundles, the database migration scripts, its global configuration
  properties, and so on;
* *Library*: it provides the business model of the application as well as the technical details,
  atop of the Silverpeas APIs, required by the model;
* *War*: it provides the Web Archive of the application with in it all of its front-end to allow
  the user to access the features of the application.

Because this project is dedicated as a backbone for a tutorial about how to code a Silverpeas
Component, some business and technical details aren't implemented.

## The application settings

The settings includes all the resources required by the application to run and they are located in
the following different directories:

* *migrations*: a set of SQL or Groovy scripts to create and to upgrade the datasource used by the
  application. The migration steps are provided by a descriptor in the *modules* subfolder.
* *properties*: all of the global configuration files as well as the l10n bundles;
* *resources*: different types of resources used by the application to perform its tasks.
  Usually, in the *StringTemplates* subfolder, the templates of localized content used in the
  user notifications or in the front-end's user information.

## The business layer

The business model in Silverpeas should satisfy the OOP principles in order to avoid both anemic
business objets and procedural alike approach (aka service/data oriented programming). To ease
this, use the Inversion of Control principle as well as the domain and design patterns. To make
this easier, the class `ServiceProvider` provides to unmanaged beans (like the entities and POJOs)
useful methods to retrieve an IoC managed service (repository, notifier, ...).

The business layer is made up of several parts.

### The business model

It provides the objects representing the business concepts handled by the application and their
sustaining technical services.

The business model in the project is made up of:

* the persistent entity `Todo` and of the POJO `TodoList` aimed to manage the todos;
* the repository `TodoRepository` to handle the persistence of the todos;
* the system event notifier `TodoEventNotifier` to notify about changes in a todo (creation,
  modification, deletion, ...).

The persistence and the notification mechanisms aren't to be used directly by the client to access
a todo or to notify about an operation applied on a todo. Those are technical mechanisms and they
should be encapsulated by the business concepts in the model; here the todo and the todolist. It
is their own responsibility to invoke such mechanisms when necessary.

The system event notification is a way to invoke in a decoupled way additional services which
aren't related to the business model of the application.

### The transversal technical and functional services

Silverpeas provides APIs for technical and functional services like, for example, in this
project, the Indexation API and the User Notification API. Those services are transversal to
all Silverpeas Components as well as to the Silverpeas business services. They are usually invoked
by using the Silverpeas system event notification mechanism. For doing, the business model
should implement and use an event notifier to send events about any changes occurring on a
business object of the model, and the services have to implement the listening interface to be
notified about such events. In this project, this is the responsibility of the abstract class
`TodoEventListener`.

Two specific services are implemented here:

* the technical service `TodoIndexation` to index the todos;
* the functional service `SubscriberNotification` to inform users subscribed to a todolist about
  changes on the todos of the todolist.

These two services are also a `TodoEventListener` in order to be executed each time an operation
occurred on a todo.

### The technical sustaining sublayer (aka infrastructure)

The goal of such a layer is to sustain the business model and the transversal service use
implemented within the project. For doing, Silverpeas provides several technical APIs to perform the
more usual functions like the persistence, the indexation, the user notifications, and so on.
Those APIs are in fact frameworks and, as any frameworks, some specific codes have to be written,
extending and using peculiar classes of the frameworks. Those specific codes belong to this
sublayer.

As you have yet figured out, the persistence mechanism and the system event notification
mechanism is one of the bricks in this layer. The `TodoRepository` and the
`TodoEventNotifier`/`TotoEventListener` are just the visible tip of the technical iceberg. They
are the suppliers for the business and functional objects and they abstract all the underlying
technical details that are themselves provided by a framework of Silverpeas (named as *engine*).
For example, `TodoRepository` extends the generic class `SilverpeasJpaEntityRepository` provided by
the Silverpeas Persistence Engine (its JPA counterpart).

Beside these classes, a note about the others in the project:

* The user notification is motorized by the Silverpeas User Notification Engine, and it is here
  provided by the `TodoSubscribedUserNotificationBuilder` to notify todolist subscribers. This
  builder aims to build a notification whose the content is generated by a template;
* `TodolistInstancePostCreation` and `TodolistInstancePreDestruction` are technical services
  that are invoked automatically and in a transparent way by Silverpeas. This is why their name
  has to satisfy some convention rule in order to be retrieved by Silverpeas. When an
  instance of the application is created or deleted by an administrator in Silverpeas, some
  resources specific to the application instance can be respectively allocated and cleaned. Such
  tasks are of the responsibility of specific technical services named *post-constructor* and
  *pre-destructor*. Those are optional but, usually, the pre-destructor should be implemented by
  the application as only itself knows how to delete all the business data and all the additional
  resources used by the application instance (or by the business model).

## The web layer

The web layer is usually composed of two parts:

* The REST-based web services dedicated both to Ajax requests by the front-end and to external
  applications;
* The Web front-end which is also made up of two parts:
    * The server-side to answer the request coming from the client-side and which is built atop of
      the Silverpeas MVC framework;
    * The client-side to provide the user with web UI to interact with the application and
      which is made up of JSP pages, Javascript files, and CSS and pictures resources.

### The REST-based web services

The REST-based Web services are built atop of JAX-RS. Silverpeas provides an API to take into
account for web resources some usual common characteristics such as, for example, the
validation of the access rights on the targeted web resource. All of those are provided through
the `RESTWebService` abstract class which has to be implemented by all the web resources in
Silverpeas. The API provides also some annotations like `@Authenticated` or `@Authorized`
to specify the access security level of the web resource (meaning respectively the user must
be authenticated and hence identified, and the user must be authenticated and authorized to
access the targeted web resource).

In order to ease the integration tests on the REST-based web services, Silverpeas provides also
an API with a set of test abstract classes: `ResourceGettingTest`, `ResourceDeletionTest`,
`ResourceCreationTest` and `ResourceUpdateTest`. These tests already implement some usual use case
tests like forbidden access, non-existing resource access, and so on.

### The front-end

The front-end in Silverpeas is made up of two sublayers:

* The Web pages with which the user interact with the application and running within a web browser;
* The MVC framework listening for HTTP requests coming from the web pages to trigger the
  asked functions of the application and to redirect the user to the next web pages or on an
  updated part of the web page.

#### The Web pages

Web pages generation is based on the following approach:

* Silverpeas provides a canvas along with a look&feel for the applications web UI;
* The HTML page is generated with possibly an initial content from the business data on the
  server-side;
* The dynamic of the HTML pages is driven by Javascript on the client-side and the content is
  updated and possibly loaded by AJAX.

Silverpeas still uses the old JSP and JSTL technologies to generate the HTML pages because they
are the mode efficient and flexible way for this purpose in the Java world. The counterpart of
their use is they require more rigor from the developer (in order to avoid, for example, to write
business or application code in the web pages).

Because Silverpeas provides the canvas and the look&feel for the web UI of Silverpeas, the Web
pages of the applications only have to worry about rendering their functionalities and their
business data. To make the writing of JSP pages easier, and in order to avoid to write usual HTML
and Javascript codes, and CSS/Javascript inclusion, satisfying by the way the conventions in
Silverpeas, several sets of custom JSTL are provided (`silverfunction`, `viewgenerator`, ...)
along with the default JSTL.

The web pages generated for a given application are rendered within the layout of the Silverpeas
main web page. According to the interaction of the user with some parts of the main web page,
one or several parts within the layout can be updated (by requesting the corresponding web
controllers (see later)).

The dynamic of Web pages is motorized by both plain Javascript, JQuery and VueJS. (Because
previously the dynamic was driven by AngularJS, there is again some codes in Silverpeas using
this old and deprecated Javascript framework.) Silverpeas provides a custom Javascript framework
built atop of both plain Javascript, JQuery and VueJS in order to make the writing of UI
interactions easier without having to worry about a lot of details peculiar to Silverpeas. The goal
of the framework with VueJS code in the applications is to give them a more web
component-oriented and asynchronous approach. Therefore, a knowledge of the Silverpeas framework
is required to profit of all of its capabilities.

In this project, the application is a single-page one, generated from the `home.jsp` JSP. The
whole UI rendering and interaction in this web page is performed by a custom view plugin,
`todolist_plugin`, that is defined and registered by the `TodolistJavascriptPluginRegistering` Java
code, and that is declared in the single page of the application. This plugin defines the
VueJS web component *todolist* dedicated to manage in the client side the todos of a todolist. 
It is made up of the following parts, all of them defined in the *todolist/jsp/javascript/vuejs* 
directory in the web root folder:

* `silverpeas-todolist-template.jsp` in which are defined the different view components of the
  web component *todolist*;
* `silverpeas-todolist.css` in which are defined the look of the different view components and 
  their inner parts;
* `silverpeas-todolist.js` in which is defined the behavior (and hence the user interaction) of 
  the web component *todolist*.

#### The MVC framework

Since its inception, Silverpeas provides a custom MVC framework. The first one was developed at
the time Struts didn't yet existed. It was composed of two bricks:

* The request router whose goal is to translate the incoming user request to an application
  function on the controller;
* The controller whose goal is to delegate the application function requested by the user from the
  UI to one or more calls on the business layers of the application and of Silverpeas. The
  controller is user session life-scoped; this is why it is also named *session controller*.

Currently, Silverpeas provides a second version of the MVC framework, based upon the first one,
but providing some facilities and with a more modern programming approach :

* No need to write a request router: the generic one, `WebComponentRequestRouter`, is used and
  for this it requires to be declared as serving the root URL of the application, with the
  web controller with which it has to be mapped, in the `web.xml` web descriptor of the application;
* The web component request context which provides useful methods (like, for example, getting the
  highest roles the user play in the application instance) and access to the HTTP request and
  response. The request context is always passed as the single parameter to the session
  controller's methods;
* The session controller is now abstracted by the `WebComponentController` class which requires
  to be extended by the web controller specific to the application. The web controller
  implementation has to be annotated with `@WebComponentController` which accepts as value the
  technical name of the application;
* The JAX-RS annotations `@Path`, `@GET`, `@POST` used on the web controller's methods to
  specify to which function name in the targeted URI and for which HTTP method the function of
  the session controller has to be invoked by the request router.
* A set of custom annotations (`@Homepage`, `@RedirectToInternalJsp`, `@LowestRoleAccess`,
  `@InvokeBefore`, `@InvokeAfter`, and so on) to specify either some additional behaviors to
  invoke before or after a controller's function or the next navigation step in the current
  request processing flow.

In this tutorial, the web controller, `TodoListWebController` is only used to redirect to the home
page `home.jsp` of the application. All the interaction and the todolist rendering is performed by
specific VueJS components in an asynchronous way.

