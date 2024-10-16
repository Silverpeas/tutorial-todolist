<%--
  ~ Copyright (C) 2000 - 2024 Silverpeas
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as
  ~ published by the Free Software Foundation, either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ As a special exception to the terms and conditions of version 3.0 of
  ~ the GPL, you may redistribute this Program in connection with Free/Libre
  ~ Open Source Software ("FLOSS") applications as described in Silverpeas's
  ~ FLOSS exception.  You should have received a copy of the text describing
  ~ the FLOSS exception, and it is also available here:
  ~ "https://www.silverpeas.org/legal/floss_exception.html"
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program.  If not, see <https://www.gnu.org/licenses/>.
  --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.silverpeas.com/tld/silverFunctions" prefix="silfn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.silverpeas.com/tld/viewGenerator" prefix="view" %>

<c:set var="userLanguage" value="${sessionScope['SilverSessionController'].favoriteLanguage}"/>
<fmt:setLocale value="${userLanguage}"/>
<view:setBundle basename="org.silverpeas.components.todolist.multilang.todolistBundle"/>

<fmt:message key="GML.update" var="updateAction"/>
<fmt:message key="GML.delete" var="deletionAction"/>
<fmt:message key="todolist.label.noTodos" var="noTodos"/>
<fmt:message key="todolist.label.todoTitle" var="todoTitle"/>
<fmt:message key="todolist.label.todoDescription" var="todoDescription"/>
<fmt:message key="todolist.label.allTodos" var="allTodos"/>
<fmt:message key="todolist.label.updateTodo" var="todoUpdateTitle"/>
<fmt:message key="todolist.label.newTodo" var="todoCreationTitle"/>
<fmt:message key="todolist.message.deletionConfirmation" var="todoDeletionConfirmationMessage"/>
<fmt:message key="todolist.error.invalidMinLengthTitle" var="todoInvalidMinTitleError"/>
<fmt:message key="todolist.error.invalidMaxLengthTitle" var="todoInvalidMaxTitleError"/>
<fmt:message key="todolist.error.invalidMinLengthText" var="todoInvalidMinTextError"/>
<fmt:message key="todolist.error.invalidMaxLengthText" var="todoInvalidMaxTextError"/>

<c:url var="deletionIcon" value="/util/icons/delete.gif"/>
<c:url var="updateIcon" value="/util/icons/update.gif"/>

<%--
Definition of the view template of the web component todolist
--%>
<silverpeas-component-template name="todolist">
    <%-- We have to define a root div as a container for the web component, so that its $el VueJS
     property will be hooked to it (otherwise, the property won't be hooked to anything) --%>
    <div class="todolist">

        <%-- set up all the l10n messages to pass to the component (taken in charge by
        VuejsI18nTemplateMixin --%>
        <div v-sp-init>
            {{ addMessages({
            updateFormTitle : '${silfn:escapeJs(todoUpdateTitle)}',
            deletionConfirmation : '${silfn:escapeJs(todoDeletionConfirmationMessage)}',
            invalidMinTitleError : '${silfn:escapeJs(todoInvalidMinTitleError)}',
            invalidMaxTitleError : '${silfn:escapeJs(todoInvalidMaxTitleError)}',
            invalidMinTextError : '${silfn:escapeJs(todoInvalidMinTextError)}',
            invalidMaxTextError : '${silfn:escapeJs(todoInvalidMaxTextError)}'
            }) }}
        </div>

        <%-- use of the Silverpeas popin VueJS component to edit a new todo --%>
        <silverpeas-popin title="${todoCreationTitle}"
                          type="validation"
                          v-on:api="creationPopin = $event"
                          v-bind:minWidth="650">
            <%-- the HTML form is wrapped by a VueJS component in which some
            Silverpeas dedicated behaviors have been implemented --%>
            <silverpeas-form-pane id="todo-creation-box"
                                  v-bind:manual-actions="true"
                                  v-bind:mandatory-legend="true">
                <div class="field">
                    <%-- the HTML text input is wrapped by a VueJS component in which some
                    Silverpeas dedicated behaviors have been implemented --%>
                    <silverpeas-text-input  id="todo-creation-title" class="text" name="title"
                           placeholder="${todoTitle}"
                           mandatory="true"
                           v-model="todoTitle">
                    </silverpeas-text-input>
                </div>
                <div class="field">
                    <%-- the HTML text area is wrapped by a VueJS component in which some
                    Silverpeas dedicated behaviors have been implemented --%>
                    <silverpeas-multiline-text-input id="todo-creation-description"
                              class="text"
                              placeholder="${todoDescription}"
                              autoresize="true"
                              mandatory="true"
                              v-model="todoDescription">
                    </silverpeas-multiline-text-input>
                </div>
            </silverpeas-form-pane>
        </silverpeas-popin>

        <%-- use of the Silverpeas popin VueJS component to update a given todo --%>
        <silverpeas-popin v-bind:title="todoTitle"
                          type="validation"
                          v-on:api="updatePopin = $event"
                          v-bind:minWidth="650">
            <silverpeas-form-pane id="todo-update-box"
                                  v-bind:manual-actions="true"
                                  v-bind:mandatory-legend="true">
                <silverpeas-multiline-text-input id="todo-update-description"
                          class="text"
                          autoresize="true"
                          mandatory="true"
                          v-model="todoDescription">
                </silverpeas-multiline-text-input>
            </silverpeas-form-pane>
        </silverpeas-popin>

        <p class="txt-no-content" v-if="todolist.length == 0">${noTodos}</p>
        <div id="todolist-alltodos" v-if="todolist.length > 0">
            <%-- define a transition effect on the content --%>
            <silverpeas-fade-transition-group>
                <%-- insert here the view of the web component todo for each todo in the todolist --%>
                <silverpeas-todo v-for="todo in todolist"
                                 v-bind:key="todo.id"
                                 v-bind:current-user="user"
                                 v-bind:todo="todo"
                                 v-on:todo-update="updateTodo"
                                 v-on:todo-deletion="deleteTodo">
                </silverpeas-todo>
            </silverpeas-fade-transition-group>
        </div>

    </div>
</silverpeas-component-template>

<%--
Definition of the view template of the web component todo
--%>
<silverpeas-component-template name="todo">
    <div v-bind:id="'todo' + todo.id" class="todolist-todo">
        <div>
            <div class="action">
                <img v-if="canBeUpdated"
                     v-on:click="$emit('todo-update', todo)"
                     src="${updateIcon}" alt="${updateAction}">
                <span>&nbsp;</span>
                <img v-if="canBeDeleted"
                     v-on:click="$emit('todo-deletion', todo)"
                     src="${deletionIcon}" alt="${deletionAction}">
            </div>
            <div class="header">
                <pre class="title" v-html="todoTitle"></pre>
                <p class="author">
                <span class="name" v-bind:class="{ userToZoom : displayUserZoom }"
                      v-bind:rel="todo.author.id">
                    {{ todo.author.fullName }}
                </span>
                </p>
            </div>
            <div class="description">
                <pre class="text" v-html="todoDescription"></pre>
            </div>
        </div>
    </div>
</silverpeas-component-template>
