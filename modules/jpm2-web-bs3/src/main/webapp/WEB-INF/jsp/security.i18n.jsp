<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
var authMessages = new Array();
authMessages["jpm.security.authority.title"] = "<spring:message code='jpm.security.authority.title' text="Special" javaScriptEscape='true' />";
authMessages["jpm.security.authority.jpmauth.title"] = "<spring:message code='jpm.security.authority.jpmauth.title' text="Authorizations" javaScriptEscape='true' />";
<c:forEach var="key" items="${keys}">authMessages["jpm.security.authority.${key.id}"] = "<spring:message code='jpm.security.authority.${key.id}' text="${key.id}" javaScriptEscape='true' />";
</c:forEach>
function sec_i18n(key) {return authMessages[key];}