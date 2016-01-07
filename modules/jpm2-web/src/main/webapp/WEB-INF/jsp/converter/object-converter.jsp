<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<select name="field_${field}" id="field_${field}" class="objectConverterInput form-control" data-entity="${param.entityId}" data-textField="${param.textField}">
    <c:if test="${not empty param.value}">
        <option value="${param.value}">${param.valueText}</option>
    </c:if>
</select>
<script type="text/javascript" src="${cp}static/js/select2.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    jpmLoad(function () {
        $("#field_${field}").select2({
            placeholder: "${param.placeHolder}",
            allowClear: true,
            width: 'copy',
            dropdownCssClass: "bigdrop",
            minimumInputLength: ${param.minSearch},
            ajax: {
                url: "${cp}jpm/${param.entityId}.json?filter=${param.filter}&ownerId=${not empty owner?ownerId:''}",
                                dataType: 'json',
                                data: function (params) {
                                    return {
                                        relatedValue: ${(not empty param.related)?'$("#field_'.concat(param.related).concat('").val()'):'""'},
                                        textField: "${param.textField}",
                                        query: params.term, // search term
                                        sortBy: '${param.sortBy}',
                                        pageSize: ${param.pageSize},
                                        page: params.page
                                    };
                                },
                                results: function (data, page) {
                                    return data;
                                }
                            },
                            processResults: function (data, params) {
                                params.page = params.page || 1;
                                return {
                                    results: data.items,
                                    pagination: {
                                        more: (params.page * ${param.pageSize}) < data.total_count
                                    }
                                };
                            },
                            escapeMarkup: function (m) {
                                return m;
                            }
                        }).prop("disabled", ${param.readonly});
                    });
</script>
<c:if test="${param.addable}">
    <script type="text/javascript">
        jpmLoad(function () {
            $("#control-group-${field} label").append("<a href='${cp}jpm/${param.entityId}/add?close=true' target='_blank'>[<span class='glyphicon glyphicon-plus'></span>]</a>");
        });
    </script>
</c:if>