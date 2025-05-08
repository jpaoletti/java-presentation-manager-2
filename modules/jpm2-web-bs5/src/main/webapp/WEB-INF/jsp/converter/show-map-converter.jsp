<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty wsjsonc_values}">
    <c:forEach var="e" items="${wsjsonc_values}">
        <div id="control-group-address" class="form-group row ">
            <label class="col-md-4 control-label" for="address">
                ${e.key}
            </label>
            <div class="col-md-8 converted-field-container">
                <span class="to-string" >${e.value}</span>
            </div>
        </div>
    </c:forEach>
</c:if>