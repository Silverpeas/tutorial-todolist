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
<fmt:setLocale value="${currentUserLanguage}"/>
<view:setBundle bundle="${requestScope.resources.multilangBundle}"/>
<view:setBundle bundle="${requestScope.resources.iconsBundle}" var="icons"/>

<fmt:message key="GML.validate"           var="addTodo"/>
<fmt:message key="GML.cancel"             var="clear"/>
<fmt:message key="todolist.label.newTodo" var="newTodoLabel"/>

<c:set var="todolistTitle"    value="${requestScope.todoListTitle}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html>
  <head>
    <view:looknfeel />
    <script type="application/javascript">
      function addTodo() {
        var error = false;
        var text = $('#new_todo-description').val();
        if (text) {
          text = text.trim();
          if (text.length > 1 && text.length < 1000)
            $('#todolist-new_todo-edition').submit();
          else
            error = true;
        } else {
          error = true;
        }
        if (error)
          notyError("<b><fmt:message key='GML.description'/></b> <fmt:message key='GML.MustBeFilled'/><br/>")
      }

      function comeBack() {
        window.location.href = '${componentUriBase}Main';
      }

    </script>
  </head>
  <body>
    <view:browseBar componentId="${componentId}" extraInformations="${newTodoLabel}"/>
    <view:window>
      <view:frame>
        <h2 class="todolist-title">${todolistTitle}</h2>
        <view:board>
        <div id="todolist-new_todo">
          <form id="todolist-new_todo-edition" method="post" action="${componentUriBase}addtodo">
            <textarea id="new_todo-description" name="description" rows="5" cols="100%" maxlength="1000" autofocus="autofocus"></textarea>
            <view:buttonPane>
              <view:button label="${addTodo}" action="javascript:addTodo();"/>
              <view:button label="${clear}" action="javascript:comeBack();"/>
</view:buttonPane>
          </form>
        </div>
        </view:board>
      </view:frame>
    </view:window>
  </body>
</html>