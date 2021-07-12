<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="${field}CollectionContainer">
    <label>
        <input type="checkbox" id="${field}selectAll" /> <spring:message code="jpm.converter.collection.selectall" text="Select all" />
    </label><br/><br/>
</div>
<script type="text/javascript">
    jpmLoad(function () {
        $(document).on("click", "#${field}selectAll", function () {
            $("input[name='field_${field}']").prop("checked", $(this).is(":checked"));
        });
        $.ajax({
            url: "${cp}jpm/${param.entityId}.json",
            dataType: "json",
            async: false,
            data: {
                textField: "${param.textField}",
                filter: "${param.filter}",
                ownerId: "${not empty owner?ownerId:''}"
            },
            success: function (data) {
                var ids = "${param.value}".split(",");
                $.each(data.results, function (i, item) {
                    var checked = ($.inArray(item.id, ids) >= 0) ? 'checked' : '';
                    $("#${field}CollectionContainer").append("<label><input type='checkbox' " + checked + " value='" + item.id + "' name='field_${field}' />&nbsp;" + item.text + "</label><br/>");
                });
            }
        });
    });
</script>
<c:if test="${param.addable}">
    <script type="text/javascript">
        jpmLoad(function () {
            $("#control-group-${field} label.control-label").append("<a href='${cp}jpm/${param.entityId}/add?close=true' target='_blank'>[<span class='fas fa-plus'></span>]</a>");
        });
    </script>
</c:if>