function appendScript(filepath) {
    if ($('script[src="' + filepath + '"]').length > 0)
        return;
    var ele = document.createElement('script');
    ele.setAttribute("type", "text/javascript");
    ele.setAttribute("src", filepath);
    $('head').append(ele);
}

function appendStyle(filepath) {
    if ($('link[href="' + filepath + '"]').length > 0)
        return;
    var ele = document.createElement('link');
    ele.setAttribute("type", "text/css");
    ele.setAttribute("rel", "Stylesheet");
    ele.setAttribute("href", filepath);
    $('head').append(ele);
}

function isMobile() {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
}

String.prototype.trim = function () {
    return this.replace(/(?:(?:^|\n)\s+|\s+(?:$|\n))/g, "");
};

(function ($) {
    $.fn.uniqueId = function () {
        this.each(function () {
            $(this).attr("id", 'jpm-' + Math.random().toString(36).substr(2, 9));
        });
        return this;
    };
}(jQuery));

function openSearchModal(field) {
    $("#searchModal .modal-body").html($("#fieldSearchForm_" + field).html());
    $("#addSearchForm [name='fieldId']").val(field);
    $("#searchModal").modal("show").on("shown.bs.modal", function () {
        $("#searchModal .modal-body").find("input").trigger('focus');
    });
}

function supports_html5_storage() {
    try {
        return 'localStorage' in window && window['localStorage'] !== null;
    } catch (e) {
        return false;
    }
}

function getLocalStorage() {
    return window['localStorage'];
}

var wrapToString = function () {
    $(".inline-edit").addClass("form-control");
    $(".to-string").each(function () {
        var v = $(this).html();
        $(this).html("<input disabled class='form-control' type='text' value='" + v + "' style='text-align:" + $(this).attr("data-align") + "' />");
    });
};

var delay = (function () {
    var timer = 0;
    return function (callback, ms) {
        clearTimeout(timer);
        timer = setTimeout(callback, ms);
    };
})();

function initConfirm() {
    $(document).on("click", ".confirm-true", function (e) {
        e.preventDefault();
        var $href = $(this).attr("href");
        //@@ are SELECTED scoped operations
        if (!$href.includes("@@")) {
            BootstrapDialog.confirm(messages["jpm.modal.confirm.text"], function (result) {
                if (result) {
                    jpmBlock();
                    setTimeout(function () {
                        document.location = $href;
                    }, 300);
                }
            });
        }
    });
}

function initMenu() {
    $('li.submenu > a').on("click", function (e) {
        e.preventDefault();
        var submenu = $(this).siblings('ul');
        var li = $(this).parents('li');
        if ($(window).width() > 480) {
            var submenus = $('#sidebar li.submenu ul');
            var submenus_parents = $('#sidebar li.submenu');
        } else {
            var submenus = $('#jPanelMenu-menu li.submenu ul');
            var submenus_parents = $('#jPanelMenu-menu li.submenu');
        }

        if (li.hasClass('open')) {
            if (($(window).width() > 768) || ($(window).width() <= 480)) {
                submenu.slideUp();
            } else {
                submenu.fadeOut(250);
            }
            li.removeClass('open');
        } else {
            if (($(window).width() > 768) || ($(window).width() <= 480)) {
                submenus.slideUp();
                submenu.slideDown();
            } else {
                submenus.fadeOut(250);
                submenu.fadeIn(250);
            }
            submenus_parents.removeClass('open');
            li.addClass('open');
        }
    });
}

$(window).on("unload", function () {
    $("#loading-div").fadeIn();
});

function initFunctions() {
    $.each(PM_onLoadFunctions, function () {
        try {
            this();
        } catch (e) {
            console.log(e.stack);
            alert("Error: " + e);
        }
    });
}

var uniqBy = function (ary, key) {
    var seen = {};
    return ary.filter(function (elem) {
        var k = key(elem);
        return (seen[k] === 1) ? 0 : seen[k] = 1;
    });
};

var initPage = function () {
    try {
        BootstrapDialog.DEFAULT_TEXTS['OK'] = messages["jpm.modal.confirm.submit"];
        BootstrapDialog.DEFAULT_TEXTS['CANCEL'] = messages["jpm.modal.confirm.cancel"];
        //Clean empty help-blocks
        $(".help-block:empty").remove();
        $(".panel-body:not(:has(*))").parent(".panel").parent().remove();
        $(".row-fluid:not(:has(div))").remove();
        $(".sortable").on("click", function () {
            window.location = $(this).attr("data-cp") + $(this).attr("data-entity") + "/sort?fieldId=" + $(this).attr("data-field");
        });
        initConfirm();
        // === Sidebar navigation === //
        initMenu();

        // === Tooltips === //
        $('.tip').tooltip();
        $('.tip-left').tooltip({placement: 'left'});
        $('.tip-right').tooltip({placement: 'right'});
        $('.tip-top').tooltip({placement: 'top'});
        $('.tip-bottom').tooltip({placement: 'bottom'});

        if (currentUser && currentUser !== '') {
            //Favorite navbar
            $("#addFavoriteLi").show();
            $("#removeFavoriteLi").hide();
            $.getJSON(getContextPath() + "jpm/favorites", function (data) {
                $.each(data, function (i, item) {
                    if (item.link === document.location.href) {
                        $("#addFavoriteLi").hide();
                        $("#removeFavoriteLi").show();
                        $("#userNavFavorite").find(".dropdown-menu").append("<li class='active'><a title='' href='" + item.link + "' data-id='fav" + item.id + "'>" + item.title + "</a></li>");
                    } else {
                        $("#userNavFavorite").find(".dropdown-menu").append("<li><a title='' href='" + item.link + "' data-id='fav" + item.id + "'>" + item.title + "</a></li>");
                    }
                });
            });
            $(document).on("click", "#removeFavoriteLink", function (e) {
                e.preventDefault();
                $.ajax({
                    type: "POST",
                    dataType: 'text',
                    url: getContextPath() + "jpm/removeFavorite?url=" + document.location.href,
                    success: function (data) {
                        document.location.reload();
                    }
                });
            });
            $(document).on("click", "#addFavoriteLink", function (e) {
                e.preventDefault();
                var $textAndPic = $('<div></div>');
                var html = "";
                html = html + '  <div class="form-group" id="note-group">';
                html = html + '    <label for="title">' + messages["jpm.addfavorite.popupTitle"] + '</label>';
                html = html + '    <input type="text" name="title" class="form-control" id="title" placeholder="...">';
                html = html + '  </div>';
                $textAndPic.append(html);

                BootstrapDialog.show({
                    title: messages["jpm.usernav.addfavorite"],
                    message: $textAndPic,
                    buttons: [{
                            label: messages["jpm.modal.confirm.submit"],
                            cssClass: 'btn-success',
                            action: function (dialogRef) {
                                if ($("#note").val() === "") {
                                    $("#note-group").addClass("has-error");
                                    $("#note").trigger('focus');
                                } else {
                                    $.ajax({
                                        type: "POST",
                                        url: getContextPath() + "jpm/addFavorite?url=" + document.location.href + "&title=" + $("#title").val(),
                                        success: function (data) {
                                            document.location.reload();
                                        }
                                    });
                                }
                            }
                        }, {
                            label: messages["jpm.modal.confirm.cancel"],
                            action: function (dialogRef) {
                                dialogRef.close();
                            }
                        }],
                    onshown: function (dialogRef) {
                        $("#title").trigger('focus');
                    }
                });
            });

            //Recent navbar
            var url = $(location).attr('href');
            if (!url.match(/#$/)) {
                var name = "JPM_RECENT";
                var _recentArray = $.cookie(name);
                var recentArray = new Array();
                if (typeof _recentArray !== "undefined" && _recentArray !== "") {
                    recentArray = JSON.parse(_recentArray);
                }
                var array = Array.prototype.slice.call(recentArray);
                if (array.length >= 10) {
                    array.shift();
                }
                var title = document.title;
                title = title.substring(title.indexOf("- ") + 1);
                array.push({'url': url, 'title': title});
                var finalArray = uniqBy(array, JSON.stringify);
                $.cookie(name, JSON.stringify(finalArray), {path: '/'});
                $.each(finalArray, function (i, item) {
                    $("#userNavRecent").find(".dropdown-menu").append("<li><a title='' href='" + item.url + "'>" + item.title + "</a></li>");
                });
            }
        }

        $(document).on("click", ".inline-boolean", function () {
            var instanceId = $(this).attr("data-id");
            var field = $(this).attr("data-field-name");
            var entity = $(this).attr("data-entity-id");
            var i = $(this).find("span");
            var icon = i.attr("class");
            var iconT = $(this).attr("data-true-icon");
            var iconF = $(this).attr("data-false-icon");
            $.ajax({
                url: getContextPath() + "jpm/" + entity + "/" + instanceId + "/iledit?name=" + field + "&value=" + ((icon === iconT) ? "" : "1"),
                type: "POST",
                success: function () {
                    if (icon === iconT) {
                        i.removeClass(iconT).addClass(iconF);
                    } else {
                        i.removeClass(iconF).addClass(iconT);
                    }
                }
            });
        });
        if ($.fn.select2) {
            $("<script type=text/javascript' src='" + getContextPath() + "static/js/locale/select2/" + localeLanguage + ".js'></script>").appendTo("head");
            $("<link href='" + getContextPath() + "static/css/select2.css' rel='stylesheet'>").appendTo("head");
            jQuery.fn.extend({
                disableSelect2: function () {
                    return this.each(function () {
                        select = $(this);
                        var n = select.attr("name");
                        select.select2("enable", false);
                        select.parents("form").append("<input type='hidden' name='" + n + "' id='select2Disabled_" + n + "' value='" + select.val() + "'/>");
                    });
                },
                enableSelect2: function () {
                    return this.each(function () {
                        select = $(this);
                        var n = select.attr("name");
                        select.select2("enable", true);
                        $("#select2Disabled_" + n).remove();
                    });
                },
                /*
                 How to use (those are the defaults) : 
                 
                 $("#select2ID").buildJpmSelect2({
                 entity: "<REQUIRED>",
                 textField: "<REQUIRED>",
                 placeholder: "...",
                 minSearch: 0,
                 filter: "",
                 owner: "",
                 ownerId: "",
                 related: null, (use another select2 object here)
                 sortBy: "",
                 pageSize: 10
                 });
                 */
                buildJpmSelect2: function (params) {
                    return this.each(function () {
                        select = $(this);
                        select.select2({
                            placeholder: params.placeHolder || "...",
                            allowClear: true,
                            width: 'resolve',
                            dropdownCssClass: "bigdrop",
                            minimumInputLength: params.minSearch || 0,
                            ajax: {
                                url: getContextPath() + "jpm/" + params.entity + ".json",
                                dataType: 'json',
                                processResults: function (data, params) {
                                    params.page = params.page || 1;
                                    return {
                                        results: data.results,
                                        pagination: {
                                            more: data.more
                                        }
                                    };
                                },
                                data: function (p) {
                                    return {
                                        owner: (params.owner) ? params.owner : "",
                                        ownerId: (params.ownerId) ? params.ownerId : "",
                                        filter: (params.filter) ? params.filter : "",
                                        currentId: params.currentId || "",
                                        relatedValue: (params.related) ? params.related.val() : "",
                                        textField: params.textField || "",
                                        query: p.term,
                                        sortBy: params.sortBy || "",
                                        pageSize: params.pageSize || 10,
                                        page: p.page
                                    };
                                }
                            }
                        });
                    });
                }
            });
        }
        initFunctions();
    } finally {
        $("#loading-div").fadeOut();
    }
};

function jpmBlock() {
    $.blockUI({
        css: {
            border: 'none',
            padding: '15px',
            backgroundColor: 'none',
            opacity: .6
        },
        overlayCSS: {
            backgroundColor: '#000',
            opacity: .6
        },
        message: '<img style="width: 150px; height: 150px;" src="' + getContextPath() + 'static/img/main_loading.gif" />'
    });
}

function jpmUnBlock() {
    $.unblockUI();
}

var processFormResponse = function (data) {
    if (data.ok) {
        if (data.messages.length > 0) {
            var $message = '<div><ul>';
            $.each(data.messages, function (m, text) {
                $message = "<li>" + text.text + "</li>";
            });
            $message = $message + "</ul></div>";
            var okbuttons = [];
            if (!data.next || data.next === "") {
                okbuttons.push({
                    label: messages["jpm.modal.confirm.close"],
                    action: function (dialogRef) {
                        dialogRef.close();
                        jpmUnBlock();
                    }}
                );
            }
            BootstrapDialog.show({
                title: "",
                message: $($message),
                type: BootstrapDialog.TYPE_SUCCESS,
                buttons: okbuttons
            });
        }
        if (data.next && data.next !== "") {
            setTimeout(function () {
                document.location = getContextPath() + (data.next.startsWith("/") ? data.next.substr(1) : data.next);
            }, data.messageDelay);
        }
    } else {
        $(".form-group").removeClass("has-error");
        $(".jpm-validator-text").remove();
        //Entity
        if (data.messages.length > 0) {
            var $message = '<div><ul>';
            $.each(data.messages, function (m, text) {
                $message = "<li>" + text.text + "</li>";
            });
            $message = $message + "</ul></div>";
            BootstrapDialog.show({
                title: "",
                message: $($message),
                type: BootstrapDialog.TYPE_DANGER,
                buttons: [{
                        label: messages["jpm.modal.confirm.close"],
                        action: function (dialogRef) {
                            dialogRef.close();
                            jpmUnBlock();
                        }
                    }]
            });
        }
        //field
        $.each(data.fieldMessages, function (fieldId, msgs) {
            var controlGroup = $("#control-group-" + fieldId);
            controlGroup.addClass("has-error");
            $.each(msgs, function (i, item) {
                controlGroup.find(".converted-field-container").append('<p class="help-block jpm-validator-text">' + item.text + '</p>');
            });
        });
        jpmUnBlock();
    }
};

function buildAjaxJpmForm(formId, callback, beforeSubmit) {
    if (!formId)
        formId = "jpmForm";
    $('#' + formId).ajaxForm({
        dataType: 'json',
        beforeSubmit: function (arr, $form, options) {
            if (beforeSubmit) {
                if (!beforeSubmit(arr, $form, options)) {
                    return false;
                }
            }
            jpmBlock();
        },
        success: function (data) {
            if (callback) {
                try {
                    callback(data);
                } finally {
                    jpmUnBlock();
                }
            } else {
                processFormResponse(data);
            }
        },
        error: function (data) {
            console.log(data);
            BootstrapDialog.show({
                title: "",
                message: "Unexpected error!",
                type: BootstrapDialog.TYPE_DANGER,
                buttons: [{
                        label: messages["jpm.modal.confirm.close"],
                        action: function (dialogRef) {
                            dialogRef.close();
                            jpmUnBlock();
                        }
                    }]
            });
        }
    });
}

function setEntityVal(select, id, callback) {
    $.getJSON(getContextPath() + "jpm/" + select.attr('data-entity') + "/" + id + ".json", {
        textField: ((!!select.attr('data-textField')) ? select.attr('data-textField') : "")
    }, function (data) {
        select.append("<option value=" + id + ">" + data.text + "</option>");
        select.val(id).trigger("change");
        if (callback) {
            callback(select, data);
        }
    });
    return select;
}

function asynchronicOperationProgress(id) {
    $.getScript(getContextPath() + "static/js/sockjs.min.js", function (data, textStatus, jqxhr) {
        $.getScript(getContextPath() + "static/js/stomp.min.js", function (data, textStatus, jqxhr) {
            var socket = new SockJS(getContextPath() + 'jpm-websocket');
            var stompClient = Stomp.over(socket);
            stompClient.debug = null;
            stompClient.connect({}, function (frame) {
                stompClient.subscribe('/asynchronicOperationExecutor/progress/' + id, function (s) {
                    $(".asynchronic").addClass('disabled');
                    var id = "#asynchronicProgress";
                    var r = JSON.parse(s.body);
                    $(id).removeClass("hide").show();
                    $(id + " > .progress-bar").css("width", (Math.round(r.percent * 100) / 100) + "%");
                    $(id + "_status").text((Math.round(r.percent * 100) / 100) + "% (" + r.status + ")");
                });
                stompClient.subscribe('/asynchronicOperationExecutor/done/' + id, function (s) {
                    var status = JSON.parse(s.body).status;
                    if (status !== '') {
                        BootstrapDialog.show({
                            title: "",
                            message: status,
                            type: BootstrapDialog.TYPE_SUCCESS,
                            buttons: []
                        });
                        setTimeout(function () {
                            document.location.reload();
                        }, 2000);
                    }
                    $("#asynchronicProgress").hide();
                    $(".asynchronic").removeClass('disabled');
                });
                stompClient.send("/jpm/asynchronicOperationExecutorProgress", {}, JSON.stringify({'id': id}));
            });
        });
    });
}

$(document).on("click", ".viewAttachmentIco", function (e) {
    e.preventDefault();
    var ct = $(this).attr("data-type");
    var id = $(this).attr("data-id");
    var entity = $(this).attr("data-entity");
    var $textAndPic = $('<div id="attachmentPopup"></div>');
    var html = "";
    if (ct.contains("image")) {
        html = html + "<img id='attachmentImg' src='" + getContextPath() + "static/" + entity + "/" + id + "/downloadAttachment?download=false" + "'/>";
    } else if (ct.contains("pdf")) {
        html = html + "<iframe src='" + getContextPath() + "static/" + entity + "/" + id + "/downloadAttachment?download=false" + "' style='height:500px;width:100%;'></iframe>";
    } else {
        html = html + "<div class='alert alert-info' >" + messages["jpm.modal.attachment.preview"] + "</div>";
    }
    $textAndPic.append(html);

    BootstrapDialog.show({
        title: messages["jpm.modal.attachment.title"],
        message: $textAndPic,
        cssClass: "modal-attachment",
        onshown: function () {
            //$(".modal-body").height($(document).height()-500);
        },
        buttons: [{
                label: messages["jpm.modal.attachment.download"],
                cssClass: 'btn-success',
                action: function (dialogRef) {
                    document.location = getContextPath() + "static/" + entity + "/" + id + "/downloadAttachment?download=true";
                    dialogRef.close();
                }
            }, {
                label: messages["jpm.modal.confirm.close"],
                action: function (dialogRef) {
                    dialogRef.close();
                }
            }]
    });
});
$(window).on("load", initPage);
