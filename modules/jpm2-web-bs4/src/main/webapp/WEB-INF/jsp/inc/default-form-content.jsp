<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div id="form-content-container-${entity.id}">
    <c:if test="${empty entity.panels}">
        <div class="row jpm-content-panels">
            <div class="col-lg-12">
                <c:forEach items="${instance.values}" var="value">
                    <c:set var="field" value="${value.key}" scope="request" />
                    <jpm:form-item id="${field}" entity="${entity}" fieldId="${value.key}" clazz="${not empty fieldMessages[field] ? 'has-error':''}" displayTitle='${entity.getFieldById(field,null).displayTitle}'>
                        <c:set var="convertedValue" value="${value.value}"/>
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
                    </jpm:form-item>
                </c:forEach>
            </div>
        </div>
    </c:if>
    <c:if test="${not empty entity.panels}">
        <c:forEach items="${entity.panels}" var="row">
            <div class="row jpm-content-panels">
                <c:forEach items="${row.panels}" var="panel">
                    <div class="col-lg-${panel.blocks}">
                        <div class="card">
                            <div class="card-header d-flex align-items-center h-100">
                                <h5 class="card-title my-0 font-weight-normal flex-grow-1">
                                    <span class="${panel.icon}"></span> &nbsp;
                                    <spring:message code="${panel.title}" text="${panel.title}" />
                                </h5>
                            </div>
                            <div class="card-body flex-column h-100">
                                <c:forEach items="${panel.fieldList}" var="field">
                                    <c:if test="${not empty instance.values[field]}">
                                        <jpm:form-item id="${field}" entity="${entity}" fieldId="${field}" clazz="${not empty fieldMessages[field] ? 'has-error':''}" displayTitle='${entity.getFieldById(field,null).displayTitle}'>
                                            <c:set var="convertedValue" value="${instance.values[field]}"/>
                                            <c:set var="field" value="${field}" scope="request" />
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
                                        </jpm:form-item>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:forEach>
    </c:if>
</div>