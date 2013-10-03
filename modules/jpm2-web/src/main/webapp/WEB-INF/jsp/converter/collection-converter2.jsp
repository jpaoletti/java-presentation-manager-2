<div id="${field}CollectionContainer"></div>
<input name="field_${field}" id="field_${field}" value="${param.value}" type="hidden"/>
<script type="text/javascript">
    jpmLoad(function() {
        $.ajax("${cp}jpm/${param.entityId}/" + item + ".json?textField=${param.textField}", {
            dataType: "json",
            async: false,
            success: function(data) {
                var ids = "${param.value}".split(",");
                $.each(data.results, function(i, item) {
                    var checked = ($.inArray(item.id, ids)) ? 'checked' : '';
                    $("#${field}CollectionContainer").append("<input type='checkbox' " + checked + " value='" + item.id + "' name="field_${param.f}" />&nbsp;" + item.text + "<br/>");
                });
            }
        });
    });
</script>