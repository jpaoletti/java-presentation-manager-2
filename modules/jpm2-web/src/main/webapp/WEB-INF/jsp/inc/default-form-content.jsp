<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:if test="${empty entity.panels}">
    <s:iterator value="instance.values" var="value">
        <div id="control-group-${key}" class="form-group">
            <label class="col-lg-2 control-label" for="f_${key}">
                <jpm:field-title entity="${entity}" fieldId="${key}" />
            </label>
            <div class="col-lg-10">
                <c:set var="field" value="${key}" scope="request" />
                <c:set var="convertedValue" value="${value}"/>
                <c:if test="${fn:startsWith(convertedValue, '@page:')}">
                    <jsp:include page="converter/${fn:replace(convertedValue, '@page:', '')}" flush="true" />
                </c:if>
                <c:if test="${not fn:startsWith(convertedValue, '@page:')}">
                    ${convertedValue}
                </c:if>
                <c:if test="${not empty fieldMessages[field]}">
                    <p class="help-block">
                        <c:set var="messages" value="${fieldMessages[field]}" scope='request' />
                        <c:forEach var="m" items="${fieldMessages[field]}" varStatus="st">
                            * <spring:message code="${m.key}" text="${m.key}" arguments="${m.arguments}" argumentSeparator=";" />${!st.last ? '<br/>':''}
                        </c:forEach>
                    </p>
                </c:if>
            </div>
        </div>
    </s:iterator>
</c:if>
<c:if test="${not empty entity.panels}">
    <s:iterator value="entity.panels" var="row">
        <div class="row jpm-content-panels">
            <s:iterator value="panels" var="panel">
                <div class="col-lg-${panel.blocks}">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title">
                                <span class="glyphicon ${panel.icon}"></span> &nbsp;
                                <spring:message code="${panel.title}" text="${panel.title}" />
                            </h3>
                        </div>
                        <div class="panel-body">
                            <s:iterator value="fieldList" var="field">
                                <c:if test="${not empty instance.values[field]}">
                                    <div id="control-group-${field}" class="form-group ${not empty fieldMessages[field] ? 'has-error':''}">
                                        <label class="col-lg-2 control-label" for="f_${field}">
                                            <jpm:field-title entity="${entity}" fieldId="${field}" />
                                        </label>
                                        <div class="col-lg-10">
                                            <c:set var="convertedValue" value="${instance.values[field]}"/>
                                            <c:if test="${fn:startsWith(convertedValue, '@page:')}">
                                                <jsp:include page="converter/${fn:replace(convertedValue, '@page:', '')}" flush="true" />
                                            </c:if>
                                            <c:if test="${not fn:startsWith(convertedValue, '@page:')}">
                                                ${convertedValue}
                                            </c:if>
                                            <c:if test="${not empty fieldMessages[field]}">
                                                <p class="help-block">
                                                    <c:set var="messages" value="${fieldMessages[field]}" scope='request' />
                                                    <c:forEach var="m" items="${fieldMessages[field]}" varStatus="st">
                                                        * <spring:message code="${m.key}" text="${m.key}" arguments="${m.arguments}" argumentSeparator=";" />${!st.last ? '<br/>':''}
                                                    </c:forEach>
                                                </p>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:if>
                            </s:iterator>
                        </div>
                    </div>
                </div>
            </s:iterator>
        </div>
    </s:iterator>
</c:if>