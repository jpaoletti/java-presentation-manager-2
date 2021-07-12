<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="file-converter-container-${field}">
    <button type="button" class="btn btn-secondary btn-sm fileinput-button pull-left">
        <span class="fas fa-upload"></span>
        <input name="file" id="field_${field}"  type="file" data-url="${cp}jpm/${param.postAction}" ${param.multiple?'multiple=""':''} data-role="none" accept="${param.accept}"/>
    </button>
    <input name="field_${field}" type="hidden" value="@current:" />
    <div id="progress_${field}" class="progress file-converter-progress"><div class="progress-bar"></div></div>
        <c:if test="${param.delete}">
        <a class="file-converter-delete" href="javascript: ;" class="${field}_delete"><span class="fas fa-trash-alt"></span> <spring:message code="jpm.converter.file.delete" text="Delete" /></a>
        <span class="file-converter-text"><spring:message code="jpm.converter.file.bytes.text" text="?" arguments="${param.len}" /></span>
    </c:if>
    <c:if test="${not param.delete}">
        <span class="file-converter-text"><spring:message code="jpm.converter.file.null.file.text" text="-" /></span>
    </c:if>
</div>
<link href="${cp}static/css/jquery.fileupload.css" rel="stylesheet">
<script src="${cp}static/js/jquery.fileupload/jquery.ui.widget.js?v=${jpm.appversion}"></script>
<script src="${cp}static/js/jquery.fileupload/jquery.iframe-transport.js?v=${jpm.appversion}"></script>
<script src="${cp}static/js/jquery.fileupload/jquery.fileupload.js?v=${jpm.appversion}"></script>
<script>
    jpmLoad(function () {
        if (!$("link[href='${cp}static/css/jquery.fileupload-ui.css']").length) {
            $('<link href="${cp}static/css/jquery.fileupload-ui.css" rel="stylesheet">').appendTo("head");
        }
        $("#file-converter-container-${field} .file-converter-delete").on("click", function () {
            $("input[name='field_${field}']").val("");
            $("#file-converter-container-${field} .file-converter-text").css("text-decoration", "line-through");
            $('#progress_${field} .progress-bar').css('width', '0%');
        });
        $('#field_${field}').fileupload({dataType: 'json'}).on('fileuploadprogressall', function (e, data) {
            $("button[type='submit']").addClass("disabled");
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress_${field} .progress-bar').css('width', progress + '%');
        }).on('fileuploaddone', function (e, data) {
            $.each(data.result.files, function (index, file) {
                $("input[name='field_${field}']").val(file.name);
            });
            $('#progress_${field} .progress-bar').addClass("bg-success");
            $("button[type='submit']").removeClass("disabled");
            $("#file-converter-container-${field} .file-converter-text").css("text-decoration", "line-through");
        }).on('fileuploadfail', function (e, data) {
            $.each(data.files, function (index, file) {
                alert("File upload failed");
                $("button[type='submit']").removeClass("disabled");
            });
        });
    });
</script>