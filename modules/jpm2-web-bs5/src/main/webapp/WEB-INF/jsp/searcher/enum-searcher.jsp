<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="row">
    <div class="col-lg-12">
        <label>
            <input type="checkbox" id="${param.ofield}selectAll" /> <spring:message code="jpm.converter.collection.selectall" text="Select all" />
        </label><br/><br/>
        <c:forEach var="option" items="${fn:split(param.options, ',')}">
            <label><input type="checkbox" value="${fn:split(option, '@')[0]}" name="value" class="field_${param.field}"/> ${fn:split(option, '@')[1]} </label><br/>
        </c:forEach>
    </div>
</div>
<script type="text/javascript">
    jpmLoad(function () {
        $(document).on("click", "#${param.field}selectAll", function () {
            $("input.field_${param.field}").prop("checked", $(this).is(":checked")).trigger("change");
        });
        $(document).on("click", "input.field_${param.field}", function () {
            $("#${param.field}selectAll").prop("checked", $("input.field_${param.field}:checked").length === $("input.field_${param.field}").length);
        });
    });
</script>