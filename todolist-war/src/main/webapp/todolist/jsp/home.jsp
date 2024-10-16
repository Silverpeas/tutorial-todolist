<%--
  Copyright (C) 2000 - 2024 Silverpeas

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.

  As a special exception to the terms and conditions of version 3.0 of
  the GPL, you may redistribute this Program in connection with Free/Libre
  Open Source Software ("FLOSS") applications as described in Silverpeas's
  FLOSS exception. You should have received a copy of the text describing
  the FLOSS exception, and it is also available here:
  "http://www.silverpeas.org/docs/core/legal/floss_exception.html"

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <https://www.gnu.org/licenses/>.
  --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.silverpeas.com/tld/silverFunctions" prefix="silfn" %>
<%@ taglib uri="http://www.silverpeas.com/tld/viewGenerator" prefix="view" %>

<view:setConstant var="writerRole"
                  constant="org.silverpeas.core.admin.user.model.SilverpeasRole.WRITER"/>

<c:set var="componentId" value="${requestScope.browseContext[3]}"/>
<c:set var="currentUserLanguage" value="${requestScope.resources.language}"/>
<c:set var="isUserSubscribed" value="${requestScope.isUserSubscribed}"/>
<c:set var="greaterUserRole" value="${requestScope.highestUserRole}"/>
<jsp:useBean id="greaterUserRole" type="org.silverpeas.core.admin.user.model.SilverpeasRole"/>
<c:set var="user" value="${silfn:currentUser()}"/>
<c:set var="hasWritePermission" value="${greaterUserRole.isGreaterThanOrEquals(writerRole)}"/>

<fmt:setLocale value="${currentUserLanguage}"/>
<view:setBundle bundle="${requestScope.resources.multilangBundle}"/>
<view:setBundle bundle="${requestScope.resources.iconsBundle}" var="icons"/>
<fmt:message key="todolist.label.addTodo" var="addTodoLabel"/>
<fmt:message key="todolist.icon.newTodo" var="newTodoIcon" bundle="${icons}"/>
<c:url var="newTodoIcon" value="${newTodoIcon}"/>

<view:sp-page>
    <view:sp-head-part withCheckFormScript="true" withFieldsetStyle="true">
        <view:includePlugin name="subscription"/>
        <view:includePlugin name="todolist_plugin"/>
        <script>
          // this global promise is provided by the subscription plugin to set up the subscription
          // management for the todolist
          SUBSCRIPTION_PROMISE.then(function() {
            window.spSubManager = new SilverpeasSubscriptionManager('${componentId}');
          });
        </script>
    </view:sp-head-part>
    <view:sp-body-part>
        <view:browseBar componentId="${componentId}" path="${requestScope.navigationContext}"/>
        <view:operationPane>
            <view:operationOfCreation action="javascript:todolistApp.api.addNewTodo()"
                                      icon="${newTodoIcon}"
                                      altText="${addTodoLabel}"/>
            <view:operationSeparator/>
            <c:if test="${isUserSubscribed != null}">
                <view:operationSeparator/>
                <view:operation action="javascript:spSubManager.switchUserSubscription()"
                                icon=""
                                altText="<span id='subscriptionMenuLabel'></span>"/>
            </c:if>
        </view:operationPane>
        <view:window>
            <view:frame>
                <view:areaOfOperationOfCreation/>
                <div id="silverpeas-todolist">
                    <silverpeas-todolist class="todolist-block"
                                         component-id='${componentId}'
                                         v-on:api="api = $event"
                                         v-bind:user='user'>
                    </silverpeas-todolist>
                </div>
            </view:frame>
        </view:window>
        <script type="text/javascript">
          whenSilverpeasReady(function () {
            // bootstrap VueJS
            window.todolistApp = SpVue.createApp({
              data: function () {
                return {
                  user: {
                    id: '${user.id}',
                    firstName: '${user.firstName}',
                    lastName: '${user.lastName}',
                    fullName: '${user.displayedName}',
                    avatar: '${webContext}${user.smallAvatar}',
                    anonymous: ${user.anonymous},
                    guestAccess: ${user.accessGuest},
                    hasWritePermission: ${hasWritePermission}
                  },
                  api: undefined
                };
              }
            }).mount('#silverpeas-todolist');
          });
        </script>
    </view:sp-body-part>
</view:sp-page>