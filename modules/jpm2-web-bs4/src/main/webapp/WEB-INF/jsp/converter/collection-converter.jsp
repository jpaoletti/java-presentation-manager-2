<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<select name="field_${field}" id="field_${field}" multiple="multiple" class="jpm-collection-converter form-control">
    <c:forEach var="v" items="${requestScope['values_'.concat(field)]}">
        <option value="${v.id}" selected="selected">${v.text}</option>
    </c:forEach>    
</select>
<script type="text/javascript" src="${cp}static/js/select2.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    jpmLoad(function () {
        $("#field_${field}").select2({
            //allowDuplicates: ${param.allowDuplicates},
            placeholder: "...",
            minimumInputLength: ${param.minSearch},
            dropdownCssClass: "bigdrop",
            escapeMarkup: function (m) {
                return m;
            },
            ajax: {
                url: "${cp}jpm/${param.entityId}.json",
                dataType: 'json',
                data: function (params) {
                    var related = ${not empty param.related?param.related:'null'};
                    return {
                        filter: "${param.filter}",
                        currentId: "${instance.id}",
                        ownerId: "${not empty owner?ownerId:''}",
                        relatedValue: (related != null) ? related.map(function (i, sel) {
                            return $("#field_" + i).val();
                        }).join(',') : "",
                        textField: "${param.textField}",
                        query: params.term, // search term
                        pageSize: ${param.pageSize},
                        page: params.page
                    };
                },
                processResults: function (data, params) {
                    params.page = params.page || 1;
                    return {
                        results: data.results,
                        pagination: {
                            more: data.more
                        }
                    };
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