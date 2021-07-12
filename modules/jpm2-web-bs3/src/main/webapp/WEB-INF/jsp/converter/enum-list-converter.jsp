<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:forEach var="option" items="${fn:split(param.options, ',')}">
    <input type='checkbox' ${fn:split(option, '@')[2] == '1' ? 'checked':''} value='${fn:split(option, '@')[0]}' name='field_${field}' />&nbsp;${fn:split(option, '@')[1]} <br/>
</c:forEach>