<input name="field_${field}" id="field_${field}" value="${param.value}" />
<script type="text/javascript" src="static/js/select2.min.js"></script>
<script type="text/javascript" src="static/js/locale/select2_locale_${locale.language}.js"></script>
<script type="text/javascript">
    jpmLoad(function() {
        if (!$("link[href='static/css/select2.css']").length) {
            $('<link href="static/css/select2.css" rel="stylesheet">').appendTo("head");
        }
        $("#field_${field}").select2({
            placeholder: "...",
            minimumInputLength: ${param.minSearch},
            ajax: {
                url: "objectConverterGetData",
                dataType: 'json',
                data: function(term, page) {
                    return {
                        entityId: "${param.entityId}",
                        textField: "${param.textField}",
                        query: term, // search term
                        pageSize: ${param.pageSize},
                        page: page
                    };
                },
                results: function(data, page) {
                    return data;
                }
            },
            initSelection: function(element, callback) {
                var id = $(element).val();
                if (id !== "") {
                    $.ajax("objectConverterGetData", {
                        data: {
                            entityId: "${param.entityId}",
                            textField: "${param.textField}",
                            id: id
                        },
                        dataType: "json"
                    }).done(function(data) {
                        callback(data);
                    });
                }
            },
            formatResult: function(data) {
                return data.text;
            },
            formatSelection: function(data) {
                return data.text;
            },
            escapeMarkup: function(m) {
                return m;
            },
            dropdownCssClass: "bigdrop"
        });
    });
</script>