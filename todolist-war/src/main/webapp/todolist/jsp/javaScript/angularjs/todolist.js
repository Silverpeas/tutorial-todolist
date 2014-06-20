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

/* The angularjs application with its dependencies */
var todoListApp = angular.module('silverpeas.todolist',
    ['silverpeas.services', 'silverpeas.directives']);

/* the main controller of the application */
todoListApp.controller('mainController',
    ['context', 'TodoList', '$scope', function(context, TodoList, $scope) {
      var todolist = TodoList.get(context.todolistId);
      todolist.getAllTodos().then(function(theTodos) {
        $scope.alltodos = theTodos;
      });

      if (context.userRole !== 'admin' && context.userRole !== 'publisher' &&
          context.userRole !== 'writer') {
        $scope.readonly = true;
      }

      $scope.addTodo = function(elementId) {
        var description = $('#' + elementId).val();
        if (description) {
          description = description.trim();
          if (description.length > 1 && description.length < 2000) {
            todolist.addTodo({description : description}).then(function(todos) {
              $scope.alltodos = todos;
              $('#' + elementId).val("");
            });
          }
        } else {
          notyError(context.errorText);
        }
      };

      $scope.clear = function(elementId) {
        $('#' + elementId).val("");
      };

      $scope.deleteTodo = function(todo) {
        todolist.removeTodo(todo).then(function() {
          todolist.getAllTodos().then(function(theTodos) {
            $scope.alltodos = theTodos;
          });
        });
      };
    }]);
