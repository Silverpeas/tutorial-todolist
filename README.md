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

The settings includes all the resources required by the application to run located in the 
following different directories:

* *migrations*: a set of SQL or Groovy scripts tp create and to upgrade the datasource used by the 
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
should implement and use an event notifier to send events about any changes occurring on a todo, 
and the services have to implement the listening interface to be notified about such events. 
This is the responsibility of the abstract class `TodoEventListener` in the project.

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
are the suppliers of the business and functional objects and they abstract all the underlying 
technical details that are themselves provided by a framework of Silverpeas (named as *engine*). 
For example, `TodoRepository` extends the generic class `SilverpeasJpaEntityRepository` provided by 
the Silverpeas Persistence Engine (its JPA counterpart).

Beside these classes, a note about the others in the project:

* The user notification is motorized by the Silverpeas User Notification Engine, and it is here 
  provided by the `TodoSubscribedUserNotificationBuilder` to notify todolist subscribers. This 
  builder aims to build a notification whose the content is generated from a template;
* `TodolistInstancePostCreation` and `TodolistInstancePreDestruction` are technical services 
  that are invoked automatically and in a transparent way by Silverpeas. This is why their name 
  has to satisfy some convention rule in order to be retrieved by Silverpeas. When an 
  instance of the application is created or deleted by an administrator in Silverpeas, some 
  resources specific to the application instance can be respectively allocated and cleaned. Such 
  tasks are of the responsibility of specific technical services named *post-constructor* and 
  *pre-destructor*. Those are optional but, usually, the pre-destructor should be implemented by 
  the application as only itself knows how to delete all the business data and all the additional 
  resources used by the application instance (or by the business model).



  

