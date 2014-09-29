<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="${field}CollectionContainer">
    <label>
        <input type="checkbox" id="${field}selectAll" /> <spring:message code="jpm.converter.collection.selectall" text="Select all" />
    </label><br/><br/>
</div>
<script type="text/javascript">
    jpmLoad(function() {
        $("body").on("click", "#${field}selectAll", function() {
            $("input[name='field_${field}']").prop("checked", $(this).is(":checked"));
        });
        $.ajax("${cp}jpm/${param.entityId}.json?textField=${param.textField}&filter=${param.filter}&ownerId=${not empty owner?ownerId:''}", {
                    dataType: "json",
                    async: false,
                    success: function(data) {
                        var ids = "${param.value}".split(",");
                        $.each(data.results, function(i, item) {
                            var checked = ($.inArray(item.id, ids) >= 0) ? 'checked' : '';
                            $("#${field}CollectionContainer").append("<label><input type='checkbox' " + checked + " value='" + item.id + "' name='field_${field}' />&nbsp;" + item.text + "</label><br/>");
                        });
                    }
                });
            });
</script>