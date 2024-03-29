<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<select name="field_${field}" id="field_${field}" multiple="multiple">
    <c:forEach var="v" items="${requestScope['values_'.concat(field)]}">
        <option value="${v.id}" selected="selected">${v.text}</option>
    </c:forEach>    
</select>
<script type="text/javascript" src="${cp}static/js/select2.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/locale/select2/${locale.language}.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    jpmLoad(function () {
        $("#field_${field}").select2({
            language: "${locale.language}",
            //allowDuplicates: ${param.allowDuplicates},
            placeholder: "...",
            minimumInputLength: ${param.minSearch},
            dropdownCssClass: "bigdrop",
            escapeMarkup: function (m) {
                return m;
            },
            processResults: function (data, params) {
                params.page = params.page || 1;
                return {
                    results: data.items,
                    pagination: {
                        more: (params.page * 30) < data.total_count
                    }
                };
            },
            ajax: {
                url: "${cp}jpm/${param.entityId}.json",
                                dataType: 'json',
                                data: function (params) {
                                    return {
                                        filter: "${param.filter}",
                                        currentId: "${instance.id}",
                                        ownerId: "${not empty owner?ownerId:''}",
                                        relatedValue: ${(not empty param.related)?'$("#field_'.concat(param.related).concat('").val()'):'""'},
                                        textField: "${param.textField}",
                                        query: params.term, // search term
                                        pageSize: ${param.pageSize},
                                        page: params.page
                                    };
                                },
                                results: function (data, page) {
                                    return data;
                                }
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