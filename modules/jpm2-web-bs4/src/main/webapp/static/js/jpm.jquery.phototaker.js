(function ($) {
    $.fn.phototaker = function (options) {
        //we wrap the image with a label and append an input
        var cropper;
        var input;
        var original = this[0];

        var settings = $.extend({
            postUrl: "${cp}cambiarFotoPerfil",
            postParam: "foto1",
            okMessage: null,
            okMessageDelay: 3000,
            cropperAspectRatio: Number.Nan,
            cancelBtnLabel: "Cancel",
            confirmBtnLabel: "Confirm",
            width: 3072,
            height: 3072,
            croppable: true, //No implemented
            afterUpload: null
        }, options);

        input = $('<input type="file" accept="image/png" capture="camera" style="display: none;" />')
                .uniqueId()
                .change(function () {
                    if (this.files && this.files[0]) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            BootstrapDialog.show({
                                title: "",
                                message: '<img src="' + e.target.result + '" id="jpm-phototaker-newpic" style="max-width: 100%; max-height: 100%">',
                                onshown: function (dialogRef) {
                                    cropper = new Cropper($("#jpm-phototaker-newpic")[0], {
                                        aspectRatio: settings.cropperAspectRatio,
                                        viewMode: 3
                                    });
                                },
                                type: BootstrapDialog.TYPE_DEFAULT,
                                buttons: [{
                                        label: settings.cancelBtnLabel,
                                        action: function (dialogRef) {
                                            dialogRef.close();
                                            cropper.destroy();
                                            cropper = null;
                                        }
                                    }, {
                                        label: settings.confirmBtnLabel,
                                        cssClass: "btn-primary",
                                        action: function (dialogRef) {
                                            dialogRef.close();
                                            jpmBlock();
                                            var initialAvatarURL = original.src;
                                            var canvas = cropper.getCroppedCanvas({
                                                imageSmoothingQuality: 'high',
                                                width: settings.width,
                                                height: settings.height,
                                                minWidth: 256,
                                                minHeight: 256,
                                                maxWidth: 4000,
                                                maxHeight: 4000
                                            });
                                            original.src = canvas.toDataURL();
                                            canvas.toBlob(function (blob) {
                                                var data = new FormData();
                                                data.append(settings.postParam, blob);
                                                jpmBlock();
                                                $.ajax({
                                                    data: data,
                                                    cache: false,
                                                    contentType: false,
                                                    processData: false,
                                                    method: 'POST',
                                                    url: settings.postUrl
                                                }).done(function (data) {
                                                    input.prop("disabled", true);
                                                    if (settings.okMessage !== null) {
                                                        var ok = BootstrapDialog.show({title: "", message: settings.okMessage, type: BootstrapDialog.TYPE_SUCCESS, buttons: []});
                                                        setTimeout(function () {
                                                            ok.close();
                                                        }, settings.okMessageDelay);
                                                    }
                                                    if (settings.afterUpload !== null) {
                                                        settings.afterUpload(data);
                                                    }
                                                }).fail(function (jqXHR, textStatus, errorThrown) {
                                                    original.src = initialAvatarURL;
                                                    alert("Error: " + textStatus);
                                                }).always(function () {
                                                    jpmUnBlock();
                                                });
                                            });
                                        }
                                    }]
                            });
                        };
                        reader.readAsDataURL(this.files[0]);
                    }
                });
        this.css("cursor", "pointer");
        this.after(input);
        this.wrap("<label for='" + input.attr("id") + "'></label>");
        return this;
    };
}(jQuery));
