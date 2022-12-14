<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="file-converter-container-${field}">
    <input name="field_${field}" type="hidden" value="@current:" />
    <c:if test="${param.delete}">
        <a class="file-converter-delete ${field}_delete" href="javascript: ;"><span class="glyphicon glyphicon-remove"></span> <spring:message code="jpm.converter.file.delete" text="Delete" /></a>
        <span class="file-converter-text"><spring:message code="jpm.converter.file.bytes.text" text="?" arguments="${param.len}" /></span><br/>
        <img src="${cp}static/img/${param.entityId}-${param.fieldId}-${param.instanceId}.png" title="" id="showImageConverter${param.entityId}${param.fieldId}" />
    </c:if>
    <c:if test="${not param.delete}">
        <img src="${cp}static/img/noimage.png" title="" id="showImageConverter${param.entityId}${param.fieldId}" />
    </c:if>
</div>
<script type="text/javascript" src="${cp}static/js/cropper.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/jquery-cropper.min.js?v=${jpm.appversion}"></script>
<script type="text/javascript" src="${cp}static/js/jpm.jquery.phototaker.js?v=${jpm.appversion}"></script>

<script>
    jpmLoad(function () {
        if (!$("link[href='${cp}static/css/cropper.min.css']").length) {
            $('<link href="${cp}static/css/cropper.min.css" rel="stylesheet">').appendTo("head");
        }
        $(".${field}_delete").on("click", function () {
            $("#showImageConverter${param.entityId}${param.fieldId}").parent().next("input").val("");
            $("#showImageConverter${param.entityId}${param.fieldId}").attr("src", "${cp}static/img/noimage.png");
            $("#file-converter-container-${field} .file-converter-text").css("text-decoration", "line-through");
            //TODO finish delete
        });
        $("#showImageConverter${param.entityId}${param.fieldId}").phototaker({
            postUrl: "${cp}jpm/uploadFileConverter",
            postParam: "file",
            cropperAspectRatio: ${param.cropperAspectRatio},
            confirmBtnLabel: "<spring:message text="${param.confirmBtnLabel}" code="${param.confirmBtnLabel}" />",
            cancelBtnLabel: "<spring:message text="${param.cancelBtnLabel}" code="${param.cancelBtnLabel}" />",
            afterUpload: function (data) {
                $.each(data.files, function (index, file) {
                    $("input[name='field_${field}']").val(file.name);
                });
            }
        });
    });
</script>