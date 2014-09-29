<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="row">
    <div class="col-lg-3">
        <label>
            <input name='value' type='checkbox' value="0">
            <spring:message code="${param.label}" text="${param.label}" />
        </label>
    </div>
</div>
