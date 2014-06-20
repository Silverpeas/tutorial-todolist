<%--

    Copyright (C) 2000 - 2014 Silverpeas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    As a special exception to the terms and conditions of version 3.0 of
    the GPL, you may redistribute this Program in connection with Free/Libre
    Open Source Software ("FLOSS") applications as described in Silverpeas's
    FLOSS exception.  You should have received a copy of the text describing
    the FLOSS exception, and it is also available here:
    "http://www.silverpeas.com/legal/licensing"

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ include file="check.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.silverpeas.com/tld/viewGenerator" prefix="view"%>

<c:set var="componentId"         value="${requestScope.browseContext[3]}"/>
<c:set var="currentUserLanguage" value="${requestScope.resources.language}"/>
<c:set var="currentUser"         value="${requestScope.currentUser}"/>
<c:set var="currentUserId"       value="${currentUser.id}"/>
<c:url var="componentUriBase"    value="${requestScope.componentUriBase}"/>
<c:set var="greaterUserRole"     value="${requestScope.greaterUserRole}"/>
<fmt:setLocale value="${currentUserLanguage}"/>
<view:setBundle bundle="${requestScope.resources.multilangBundle}"/>
<view:setBundle bundle="${requestScope.resources.iconsBundle}" var="icons"/>

<fmt:message key="todolist.menu.item.subscribe"   var="subscribeLabel"/>
<fmt:message key="todolist.menu.item.unsubscribe" var="unsubscribeLabel"/>
<fmt:message key="GML.validate"                   var="addTodo"/>
<fmt:message key="GML.cancel"                     var="clear"/>
<fmt:message key="todolist.label.delete"          var="todoDeletionLabel"/>
<fmt:message key="todolist.message.deletionConfirmation" var="todoDeletionConfirmationMessage"/>

<c:set var="isUserSubscribed" value="${requestScope.isUserSubscribed}"/>
<c:set var="todolistTitle"    value="${requestScope.todoListTitle}"/>
<c:set var="todos"            value="${requestScope.alltodos}"/>
<c:url var="todoDeletionIcon" value="/util/icons/delete.gif"/>

<view:setConstant var="writerRole" constant="com.stratelia.webactiv.SilverpeasRole.writer"/>
<c:url var="todolistJS"            value="/todolist/jsp/javaScript/angularjs/todolist.js"/>
<c:url var="todolistServiceJS"     value="/todolist/jsp/javaScript/angularjs/services/todolist.js"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html id="ng-app" ng-app="silverpeas.todolist">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <view:looknfeel />
    <view:includePlugin name="popup"/>
    <script type="application/javascript">
      function successUnsubscribe() {
        setSubscriptionMenu('<view:encodeJs string="${subscribeLabel}" />', 'subscribe');
      }

      function successSubscribe() {
        setSubscriptionMenu('<view:encodeJs string="${unsubscribeLabel}" />', 'unsubscribe');
      }
      function setSubscriptionMenu(label, actionMethodName) {
        var $menuLabel = $("#subscriptionMenuLabel");
        $menuLabel.html(label);
        $menuLabel.parents('a').attr('href', "javascript:" + actionMethodName + "();");
      }

      function unsubscribe() {
        $.post('<c:url value="/services/unsubscribe/${componentId}" />', successUnsubscribe(),
            'json');
      }

      function subscribe() {
        $.post('<c:url value="/services/subscribe/${componentId}" />', successSubscribe(), 'json');
      }
    </script>
  </head>
  <body ng-controller="mainController">
  <view:operationPane>
    <c:if test="${isUserSubscribed != null}">
      <c:choose>
        <c:when test="${isUserSubscribed}">
          <view:operation altText="<span id='subscriptionMenuLabel'>${unsubscribeLabel}</span>" icon="" action="javascript:unsubscribe();"/>
        </c:when>
        <c:otherwise>
          <view:operation altText="<span id='subscriptionMenuLabel'>${subscribeLabel}</span>" icon="" action="javascript:subscribe();"/>
        </c:otherwise>
      </c:choose>
    </c:if>
  </view:operationPane>
    <view:window>
      <view:frame>
        <h2 class="todolist-title">${todolistTitle}</h2>
        <c:if test="${greaterUserRole.isGreaterThanOrEquals(writerRole)}">
        <view:board>
        <div id="todolist-new_todo">
          <h3><fmt:message key="todolist.label.newTodo"/></h3>
          <form id="todolist-new_todo-edition" method="post">
            <textarea id="new_todo-description" name="description" rows="5" cols="100%" maxlength="1000" autofocus="autofocus"></textarea>
            <view:buttonPane>
              <view:button label="${addTodo}" action="angularjs:addTodo('new_todo-description');"/>
              <view:button label="${clear}" action="angularjs:clear('new_todo-description');"/>
</view:buttonPane>
          </form>
        </div>
        </view:board>
        </c:if>
        <view:board>
        <div id="todolist-alltodos">
          <h3><fmt:message key="todolist.label.allTodos"/></h3>

          <div style="display: none">
            <div id="todolist-todo_deletion_confirmation">
              <span>${todoDeletionConfirmationMessage}</span>
            </div>
          </div>

            <div ng-repeat="todo in alltodos" class="todolist-todo">
              <p>${authorLabel} {{ todo.authorName }}</p>
              <textarea readonly="readonly" cols="100%">{{ todo.description }}</textarea>
              <a ng-if="!readonly" title="${todoDeletionLabel}" href="" ng-click="deleteTodo(todo)" class="delete">
                <img src=${todoDeletionIcon} alt="${todoDeletionLabel}"/>
              </a>
            </div>
        </div>
        </view:board>
      </view:frame>
    </view:window>
    <script type="text/javascript">
      angular.module('silverpeas').value('context', {
        currentUserId : '${currentUserId}',
        todolistId : '${componentId}',
        componentUriBase : '${componentUriBase}',
        userRole: '${greaterUserRole}',
        errorText: '<b><fmt:message key="GML.description"/></b> <fmt:message key="GML.MustBeFilled"/><br/>'
      });
      </script>
      <script type="text/javascript" src="${todolistJS}"></script>
      <script type="text/javascript" src="${todolistServiceJS}"></script>
      </body>
</html>