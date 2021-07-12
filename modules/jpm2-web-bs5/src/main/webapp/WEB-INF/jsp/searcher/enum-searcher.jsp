<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="row">
    <div class="col-lg-12">
        <c:forEach var="option" items="${fn:split(param.options, ',')}">
            <input type="checkbox" value="${fn:split(option, '@')[0]}" name="value"/> ${fn:split(option, '@')[1]} <br/>
        </c:forEach>
    </div>
</div>
