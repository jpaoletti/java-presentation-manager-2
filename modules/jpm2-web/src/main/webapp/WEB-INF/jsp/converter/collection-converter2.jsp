<div id="${field}CollectionContainer"></div>
<script type="text/javascript">
    jpmLoad(function() {
        $.ajax("${cp}jpm/${param.entityId}.json?textField=${param.textField}&filter=${param.filter}", {
            dataType: "json",
            async: false,
            success: function(data) {
                var ids = "${param.value}".split(",");
                $.each(data.results, function(i, item) {
                    var checked = ($.inArray(item.id, ids) >= 0) ? 'checked' : '';
                    $("#${field}CollectionContainer").append("<input type='checkbox' " + checked + " value='" + item.id + "' name='field_${field}' />&nbsp;" + item.text + "<br/>");
                });
            }
        });
    });
</script>