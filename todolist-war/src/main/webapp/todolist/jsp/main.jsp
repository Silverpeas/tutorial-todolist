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
<c:url var="componentUriBase"    value="${requestScope.componentUriBase}"/>
<c:set var="greaterUserRole"     value="${requestScope.greaterUserRole}"/>
<fmt:setLocale value="${currentUserLanguage}"/>
<view:setBundle bundle="${requestScope.resources.multilangBundle}"/>
<view:setBundle bundle="${requestScope.resources.iconsBundle}" var="icons"/>

<fmt:message key="todolist.menu.item.subscribe"   var="subscribeLabel"/>
<fmt:message key="todolist.menu.item.unsubscribe" var="unsubscribeLabel"/>
<fmt:message key="todolist.label.delete"          var="todoDeletionLabel"/>
<fmt:message key="todolist.label.addTodo"         var="addTodoLabel"/>
<fmt:message key="todolist.label.creator"         var="authorLabel"/>
<fmt:message key="todolist.message.deletionConfirmation" var="todoDeletionConfirmationMessage"/>

<c:set var="isUserSubscribed" value="${requestScope.isUserSubscribed}"/>
<c:set var="todolistTitle"    value="${requestScope.todoListTitle}"/>
<c:set var="todos"            value="${requestScope.alltodos}"/>
<c:url var="todoDeletionIcon" value="/util/icons/delete.gif"/>

<view:setConstant var="writerRole" constant="com.stratelia.webactiv.SilverpeasRole.writer"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html>
  <head>
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

      function deleteTodo(id) {
        $('#todolist-todo_deletion_confirmation').popup('confirmation', {
          callback: function() {
            $('#todoId').val(id);
            $('#todo-deletion').submit();
          }});
      }

    </script>
  </head>
  <body>
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
    <view:operation action="newtodo" altText="${addTodoLabel}"/>
  </view:operationPane>
    <view:window>
      <view:frame>
        <h2 class="todolist-title">${todolistTitle}</h2>
        <view:board>
        <div id="todolist-alltodos">

          <div style="display: none">
            <div id="todolist-todo_deletion_confirmation">
              <span>${todoDeletionConfirmationMessage}</span>
              <form id="todo-deletion" action="${componentUriBase}removetodo" method="POST">
                <input type="hidden" name="todoId" id="todoId" value=""/>
              </form>
            </div>
          </div>

          <c:forEach items="${todos}" var="todo">
            <div class="todolist-todo" id="${todo.id}>">
              <p>${authorLabel} ${todo.creator.displayedName}</p>
              <textarea readonly="readonly" cols="100%">
                <c:out value="${todo.description}"/>
              </textarea>
              <c:if test="${greaterUserRole.isGreaterThanOrEquals(writerRole)}">
              <a onclick="deleteTodo('${todo.id}')" title="${todoDeletionLabel}" href="#" class="delete">
                <img src=${todoDeletionIcon} alt="${todoDeletionLabel}"/>
              </a>
              </c:if>
            </div>
          </c:forEach>
        </div>
        </view:board>
      </view:frame>
    </view:window>
  </body>
</html>