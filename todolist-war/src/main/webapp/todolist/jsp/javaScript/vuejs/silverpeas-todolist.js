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

(function () {

  // repository of VueJS templates by specifying the file in which they are stored
  const templateRepository = new VueJsAsyncComponentTemplateRepository(webContext +
      '/todolist/jsp/javaScript/vuejs/silverpeas-todolist-template.jsp');


  /**
   * Main component handling the different actions on the todolist:
   * - creation of a new todo,
   * - update of the text of an existing todo,
   * - delete an existing todo.
   * The rendition of each todo is done by a dedicated child component which delegates the update
   * and the deletion to this component. The edition of a new todo is done directly within this
   * component.
   */
  // spVue is a Silverpeas decorator of VueJS in order to carry additional features specific to
  // Silverpeas
  SpVue.component('silverpeas-todolist', templateRepository.get('todolist', {
    // VuejsApiMixin and VuejsI18nTemplateMixin are predefined Silverpeas VueJS mixins:
    // - VuejsApiMixin is to dedicated to define an API to the component accessible out of this
    //   component (by old plain javascript code for example);
    // - VuejsI18nTemplateMixin is dedicated to provide l10n messages to the component;
    //   the messages are then accessible through the this.messages property. All l10n messages
    //   are loaded through the v-init qualified element (see template)
    mixins: [VuejsApiMixin, VuejsI18nTemplateMixin],
    // properties of the component
    props: {
      componentId: {
        'type': String,
        'mandatory': true
      },
      user: {
        'type': Object,
        'mandatory': true
      }
    },
    // the data handled by the component
    data: function () {
      return {
        todolist: [],
        url: undefined,
        processingTodo: undefined,
        updatePopin: undefined,
        creationPopin: undefined,
        todoDescription: '',
        todoTitle: ''
      }
    },
    // hook to some life-cycle states of the component
    created: function () {
      // define an API accessible out of this component
      this.extendApiWith({
        // ask for the edition of a new todo
        addNewTodo: function () {
          return this.createNewTodo();
        }
      })
      // webContext is a global constant set by Silverpeas. It is the URI path node context under
      // which Silverpeas is deployed in the web.
      this.url = webContext + '/services/todolist/' + this.componentId;
      this.loadTodoList();
    },
    mounted : function() {
      // include the Silverpeas userZoom plugin to use it in the web component
      sp.dom.includePlugin('userZoom');
    },
    // now the methods of the component
    methods: {
      // validate the specified todo title satisfies the requirement
      validateTitle : function(title) {
        // - StringUtil is a global utility object defined by Silverpeas
        // - isValidText is a global function provided by checkForm.js
        // - SilverpeasError is global utility object to handle and render error messages to the
        // user
        if (StringUtil.isNotDefined(title)) {
          SilverpeasError.add(this.messages.invalidMinTitleError);
        } else if (!isValidText(title, 100)) {
          SilverpeasError.add(this.messages.invalidMaxTitleError);
        }
        return !SilverpeasError.show();
      },
      // validate the specified todo text (description) satisfies the requirement
      validateText : function(text) {
        // - StringUtil is a global utility object defined by Silverpeas
        // - isValidTextArea is a global function provided by checkForm.js
        // - SilverpeasError is global utility object to handle and render error messages to the
        // user
        if (StringUtil.isNotDefined(text)) {
          SilverpeasError.add(this.messages.invalidMinTextError);
        } else if (!isValidTextArea(text)) {
          SilverpeasError.add(this.messages.invalidMaxTextError);
        }
        return !SilverpeasError.show();
      },
      // load the todolist with all its todos
      loadTodoList: function () {
        // sp.ajaxRequest is a Silverpeas function (in the sp namespace) performing an Ajax
        // request that returns a promise on the response of the targeted web resource: in our
        // case the todolist REST-based web service
        return sp.ajaxRequest(this.url).sendAndPromiseJsonResponse().then(function (todolist) {
          this.todolist = todolist;
        }.bind(this));
      },
      // delete the specified todo
      deleteTodo: function(todo) {
        // we wrap the deletion by a confirmation popin: the deletion is done only if the user
        // confirm the operation
        sp.popup.confirm(this.messages.deletionConfirmation, function () {
          return sp.ajaxRequest(this.url + '/' + todo.id).byDeleteMethod().send().then(function () {
            return this.loadTodoList();
          }.bind(this));
        }.bind(this));
      },
      // update the specified todo
      updateTodo: function(todo) {
        // the content of the textarea (silverpeas-multiline-text-input) is bound to the
        // updatedTodoDescription property. Any change on it will be reported into the textarea
        // and any change in the textarea will be reported into the property
        this.todoDescription = todo.description;
        this.todoTitle = this.messages.updateFormTitle + ' ' + todo.title;
        this.updatePopin.open({
          callback: function() {
            if (this.validateText(this.todoDescription)) {
              const updatedTodo = extendsObject({}, todo, {
                description: this.todoDescription
              })
              return sp.ajaxRequest(this.url + '/' + todo.id).byPutMethod()
                  .sendAndPromiseJsonResponse(updatedTodo).then(function () {
                    return this.loadTodoList();
                  }.bind(this));
            }
            return false;
          }.bind(this)
        });
      },
      // create and add a new todo in the todolist
      createNewTodo: function() {
        // - the content of the text input (silverpeas-text-input) is bound to the todoTitle
        // property. Any change on the input be reported to the property.
        // - the content of the textarea (silverpeas-multiline-text-input) is bound to the
        // todoDescription property. Any change on the textarea will be reported to the property.
        this.todoDescription = '';
        this.todoTitle = '';
        this.creationPopin.open({
          callback: function() {
            if (this.validateTitle(this.todoTitle) && this.validateText(this.todoDescription)) {
              const newTodo = extendsObject({}, {
                author: this.user,
                title: this.todoTitle,
                description: this.todoDescription
              })
              return sp.ajaxRequest(this.url).byPostMethod()
                  .sendAndPromiseJsonResponse(newTodo).then(function (todolist) {
                    this.todolist = todolist;
                    return true;
                  }.bind(this));
            }
            return false;
          }.bind(this)
        });
      }
    }
  }));

  /**
   * The todo component dedicated to render a todo and to handle interactions against the todo.
   */
  SpVue.component('silverpeas-todo', templateRepository.get('todo', {
    // events the component can emit
    emits: ['todo-update', 'todo-deletion'],
    // properties of the component
    props: {
      currentUser: {
        'type': Object,
        'mandatory': true
      },
      todo: {
        'type': Object,
        'mandatory': true
      }
    },
    // hook to some life-cycle states of the component
    mounted: function () {
      // this function is defined in silverpeas-userZoom.js and it is automatically loaded by
      // Silverpeas
      activateUserZoom();
    },
    // computed data (id est data whose change is dynamically reported)
    computed: {
      todoTitle: function () {
        return this.todo.title.noHTML().convertNewLineAsHtml();
      },
      todoDescription: function () {
        return this.todo.description.noHTML().convertNewLineAsHtml();
      },
      displayUserZoom: function () {
        return this.currentUser.id !== this.todo.author.id && !this.currentUser.anonymous;
      },
      canBeUpdated: function () {
        return this.currentUser.hasWritePermission || this.todo.author.id === this.currentUser.id;
      },
      canBeDeleted: function () {
        return this.currentUser.hasWritePermission || this.todo.author.id === this.currentUser.id;
      }
    }
  }));
})();