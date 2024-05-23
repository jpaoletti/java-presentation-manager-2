<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<select name="value" id="fieldSearcher_${param.field}" multiple="multiple" class="jpm-collection-converter form-control"></select>
<script type="text/javascript" src="${cp}static/js/select2.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript">
    $("#searchModal").on("shown.bs.modal", function () {
        $("#fieldSearcher_${param.field}").select2({
            allowDuplicates: false,
            dropdownParent: $("#searchModal"),
            placeholder: "...",
            minimumInputLength: 0,
            dropdownCssClass: "bigdrop",
            escapeMarkup: function (m) {
                return m;
            },
            ajax: {
                url: "${cp}jpm/${param.entityId}.json",
                dataType: 'json',
                data: function (params) {
                    return {
                        filter: "${param.filter}",
                        textField: "${param.textField}",
                        query: params.term, // search term
                        pageSize: 10,
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