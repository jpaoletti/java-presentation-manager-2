<span class="btn btn-success btn-sm fileinput-button">
    <i class="glyphicon glyphicon-plus"></i>
    <input name="file" id="field_${field}"  type="file" data-url="${cp}jpm/uploadFileConverter" ${param.multiple?'multiple=""':''} />
</span>
<input name="field_${field}" type="text" />
<div id="progress_${field}" class="progress"><div class="progress-bar progress-bar-success"></div></div>
<script src="${cp}static/js/jquery.fileupload/jquery.ui.widget.js"></script>
<script src="${cp}static/js/jquery.fileupload/jquery.iframe-transport.js"></script>
<script src="${cp}static/js/jquery.fileupload/jquery.fileupload.js"></script>
<script>
    jpmLoad(function() {
        if (!$("link[href='${cp}static/css/jquery.fileupload-ui.css']").length) {
            $('<link href="${cp}static/css/jquery.fileupload-ui.css" rel="stylesheet">').appendTo("head");
        }
        $('#field_${field}').fileupload({
            dataType: 'json'
        }).on('fileuploadprogressall', function(e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress_${field} .progress-bar').css('width', progress + '%');
        }).on('fileuploaddone', function(e, data) {
            alert(data);
            $.each(data.result.files, function(index, file) {
                $("input[name='field_${field}']").val(file.name);
            });
        }).on('fileuploadfail', function(e, data) {
            $.each(data.files, function(index, file) {
                $("input[name='field_${field}']").val("File upload failed");
            });
        })
    });
</script>