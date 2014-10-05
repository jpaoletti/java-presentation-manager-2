<input name="field_${field}" id="field_${field}" value="${param.value}" type="hidden"/>
<script type="text/javascript" src="${cp}static/js/select2.min.js"></script>
<script type="text/javascript" src="${cp}static/js/locale/select2_locale_${locale.language}.js"></script>
<script type="text/javascript">
    jpmLoad(function() {
        if (!$("link[href='${cp}static/css/select2.css']").length) {
            $('<link href="${cp}static/css/select2.css" rel="stylesheet">').appendTo("head");
        }
        $("#field_${field}").select2({
            multiple: true,
            placeholder: "...",
            minimumInputLength: ${param.minSearch},
            ajax: {
                url: "${cp}jpm/${param.entityId}.json?filter=${param.filter}",
                dataType: 'json',
                data: function(term, page) {
                    return {
                        relatedValue: ${(not empty param.related)?'$("#field_'.concat(param.related).concat('").select2("val")'):'""'},
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
                    var ids = id.split(",");
                    var result = new Array();
                    var f = function(item) {
                        $.ajax("${cp}jpm/${param.entityId}/" + item + ".json?textField=${param.textField}", {
                            dataType: "json",
                            async: false,
                            success: function(data) {
                                result.push(data);
                            }})
                    };
                    var def = [];
                    $.each(ids, function(i, item) {
                        def.push(f(item))
                    });
                    $.when.apply($, def).done(function() {
                        callback(result);
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