/*
 * Copyright (C) 2000-2014 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Writer Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

var services = angular.module('silverpeas.services');

services.factory('TodoList', ['RESTAdapter', function(RESTAdapter) {
  return new function() {
    // the converter of Todo objects from a json stream */
    var converter = function(data) {
      var objects;
      if (data instanceof Array) {
        objects = [];
        for (var i = 0; i < data.length; i++) {
          objects.push(new Todo(data[i]));
        }
      } else {
        objects = new Todo(data);
      }
      return objects;
    };

    // the type Todo
    var Todo = function() {
      if (arguments.length > 0) {
        for (var prop in arguments[0]) {
          this[prop] = arguments[0][prop];
        }
      }
    };

    // the type TodoList
    var TodoList = function(todolistId) {
      this.id = todolistId;
      // the REST adapter to work with todos
      var adapter = RESTAdapter.get(webContext + '/services/todolist/' + this.id, converter);

      this.getAllTodos = function() {
        return adapter.find();
      };

      this.addTodo = function(todo) {
        return adapter.post(todo);
      };

      this.removeTodo = function(todo) {
        return adapter.remove(todo.id);
      };
    }

    this.get = function(todoListId) {
      return new TodoList(todoListId);
    };
  };
}]);
