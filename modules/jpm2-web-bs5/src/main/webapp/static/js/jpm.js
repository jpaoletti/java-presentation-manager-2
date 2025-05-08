function safeLocale(locale) {
    const supportedLocales = ['en', 'es', 'fr', 'de', 'it', 'pt', 'ru']; // los que sepas que tenés disponibles
    if (!locale || typeof locale !== 'string')
        return 'en';
    const norm = locale.toLowerCase().split('-')[0]; // e.g. "es-AR" → "es"
    return supportedLocales.includes(norm) ? norm : 'en';
}

function postToIframe(url, data, target) {
    $('body').append('<form action="' + url + '" method="post" target="' + target + '" id="postToIframe"></form>');
    $.each(data, function (n, v) {
        $('#postToIframe').append('<input type="hidden" name="' + n + '" value="' + v + '" />');
    });
    $('#postToIframe').submit().remove();
}

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
    $(".inline-edit,.showObjectConverter").addClass("form-control");
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
    $("body").on("click", ".confirm-true", function (e) {
        e.preventDefault();
        var $href = $(this).attr("href");
        //@@ are SELECTED scoped operations
        if (!$href.includes("@@")) {
            var _this = $(this);
            _this.parents(".instance-row").addClass("highligthed-instance-row");
            jpmDialogConfirm({
                callback: function () {
                    jpmBlock();
                    var form = $('<form method="POST" action="' + $href + '" id="tmpjpmForm"></form>');
                    buildAjaxJpmFormObject(form, function (data) {
                        processFormResponse(data);
                    }).submit();
                    return true;
                },
                onHide: function () {
                    _this.parents(".instance-row").removeClass("highligthed-instance-row");
                }
            });
        }
    });
}

function initMenu() {
//    document.addEventListener('swiped-right', function (e) {
//        $('.page-wrapper').toggleClass('toggled');
//    });
//    document.addEventListener('swiped-left', function (e) {
//        $('.page-wrapper').toggleClass('toggled');
//    });
    // Dropdown menu
    $('.sidebar-dropdown > a').click(function () {
        $('.sidebar-submenu').slideUp(200);
        if ($(this).parent().hasClass('active')) {
            $('.sidebar-dropdown').removeClass('active');
            $(this).parent().removeClass('active');
        } else {
            $('.sidebar-dropdown').removeClass('active');
            $(this).next('.sidebar-submenu').slideDown(200);
            $(this).parent().addClass('active');
        }
    });

    //toggle sidebar
    $('#toggle-sidebar').click(function () {
        $('.page-wrapper').toggleClass('toggled');
        if (!isMobile()) {
            $("#search-menu").trigger("focus");
        }
    });

    // bind hover if pinned is initially enabled
    if ($('.page-wrapper').hasClass('pinned')) {
        $('#sidebar').hover(
                function () {
                    $('.page-wrapper').addClass('sidebar-hovered');
                },
                function () {
                    $('.page-wrapper').removeClass('sidebar-hovered');
                }
        );
    }

    //Pin sidebar
    $('#pin-sidebar').click(function () {
        if ($('.page-wrapper').hasClass('pinned')) {
            // unpin sidebar when hovered
            $('.page-wrapper').removeClass('pinned');
            $('#sidebar').unbind('hover');
        } else {
            $('.page-wrapper').addClass('pinned');
            $('#sidebar').hover(
                    function () {
                        $('.page-wrapper').addClass('sidebar-hovered');
                    },
                    function () {
                        $('.page-wrapper').removeClass('sidebar-hovered');
                    }
            );
        }
        $(':focus').blur();
    });

    //toggle sidebar overlay
    $('#overlay').click(function () {
        $('.page-wrapper').toggleClass('toggled');
    });
    //custom scroll bar is only used on desktop
    if (!isMobile()) {
        $('.sidebar-content').mCustomScrollbar({
            axis: 'y',
            autoHideScrollbar: true,
            scrollInertia: 300
        });
        $('.sidebar-content').addClass('desktop');
    }
    if (window.matchMedia("(max-width: 767px)").matches) {
        $('.page-wrapper').removeClass("toggled");
    }
    document.addEventListener("DOMContentLoaded", function () {
        /////// Prevent closing from click inside dropdown
        document.querySelectorAll('.dropdown-menu').forEach(function (element) {
            element.addEventListener('click', function (e) {
                e.stopPropagation();
            });
        });
        // make it as accordion for smaller screens
        if (window.innerWidth < 992) {
            // close all inner dropdowns when parent is closed
            document.querySelectorAll('.navbar .dropdown').forEach(function (everydropdown) {
                everydropdown.addEventListener('hidden.bs.dropdown', function () {
                    // after dropdown is hidden, then find all submenus
                    this.querySelectorAll('.submenu').forEach(function (everysubmenu) {
                        // hide every submenu as well
                        everysubmenu.style.display = 'none';
                    });
                });
            });
            document.querySelectorAll('.dropdown-menu a').forEach(function (element) {
                element.addEventListener('click', function (e) {
                    let nextEl = this.nextElementSibling;
                    if (nextEl && nextEl.classList.contains('submenu')) {
                        // prevent opening link if link needs to open dropdown
                        e.preventDefault();
                        console.log(nextEl);
                        if (nextEl.style.display === 'block') {
                            nextEl.style.display = 'none';
                        } else {
                            nextEl.style.display = 'block';
                        }
                    }
                });
            });
        }
    });
    // DOMContentLoaded  end
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
        //Clean empty help-blocks
        $(".help-block:empty").remove();
        $(".card-body:not(:has(*))").parent(".card").parent().remove();
        $(".row-fluid:not(:has(div))").remove();
        $(".sortable").on("click", function () {
            window.location = $(this).attr("data-cp") + $(this).attr("data-entity") + "/sort?fieldId=" + $(this).attr("data-field");
        });
        initConfirm();
        // === Sidebar navigation === //
        initMenu();

        // === Tooltips === //
        //TODO repleace with something lighter
        $('.tip').tooltip();
        $('.tip-left').tooltip({placement: 'left'});
        $('.tip-right').tooltip({placement: 'right'});
        $('.tip-top').tooltip({placement: 'top'});
        $('.tip-bottom').tooltip({placement: 'bottom'});

        if (currentUser && currentUser !== '') {
            if (ROLE_USER_FAVORITE) {
                //Favorite navbar
                $.getJSON(getContextPath() + "jpm/favorites", function (data) {
                    var isFav = false;
                    $.each(data, function (i, item) {
                        var html = '<li><a class="dropdown-item" href="' + item.link + '" data-id="fav' + item.id + '">';
                        html += '<div class="content"><div class="notification-detail">' + item.title + '</div></div></div></a></li>';
                        if (item.link === document.location.href) {
                            isFav = true;
                        } else {
                            $("#userNavFavorite").find(".dropdown-menu").append(html);
                        }
                    });
                    if (isFav) {
                        $("#content-header").prepend('<a id="removeFavoriteLink" style="color: goldenrod" href="#" title="' + messages["jpm.usernav.removefavorite"] + '"><i class="fas fa-star"></i></a>&nbsp;');
                    } else {
                        $("#content-header").prepend('<a id="addFavoriteLink"    style="color: gray" href="#" title="' + messages["jpm.usernav.addfavorite"] + '"><i class="fas fa-star"></i></a>&nbsp;');
                    }
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
                    var html = "";
                    html = html + '  <div class="form-group" id="note-group">';
                    html = html + '    <label for="title">' + messages["jpm.addfavorite.popupTitle"] + '</label>';
                    html = html + '    <input type="text" name="title" class="form-control" id="title" placeholder="...">';
                    html = html + '  </div>';
                    jpmDialogConfirm({
                        title: messages["jpm.usernav.addfavorite"],
                        message: html,
                        callback: function () {
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
                            return true;
                        }
                    });
                });
            }
//            $("#content-header").prepend('<button id="pin-sidebar" class="btn btn-sm btn-dark d-none d-sm-flex" style="width: 35px;"><i class="fas fa-thumbtack"></i></button>&nbsp;');
            $("#search-menu").on("keyup", function (e) {
                let value = e.target.value;
                var normalizedValue = value.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
                $("#searchDropdown .dropdown-menu").remove();
                $("#searchDropdown").append('<ul class="dropdown-menu"></ul>');
                if (value.length >= 2) {
                    let empty = true;
                    $(".jpm-menu-item").each(function () {
                        if ($(this).last().text().toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').includes(normalizedValue)) {
                            let html = '<li><a class="dropdown-item" href="' + $(this).attr("href") + '">' + $(this).html() + '</a></li>';
                            $("#searchDropdown .dropdown-menu").append(html);
                            empty = false;
                        }
                    });
                    if (empty) {
                        $("#searchDropdown .dropdown-menu").append('<li><a class="dropdown-item disabled" href="javascript:;"><span class="fa fa-eye-slash"></span></a></li>');
                    }
                    $("#searchDropdown .dropdown-menu").toggle();
                }
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
                    var html = '<li><a class="dropdown-item" href="' + item.url + '">';
                    html += '<div class="notification-content">';
                    html += '<div class="content"><div class="notification-detail">' + item.title + '</div></div></div></a></li>';
                    $("#userNavRecent").find(".dropdown-menu").append(html);
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

        $(".inline-edit-combo").each(function () {
            $(this).editable(getContextPath() + 'jpm/' + $(this).attr("data-contextualEntity") + '/' + $(this).attr("data-instanceId") + '/iledit?name=' + $(this).attr("data-name"), {
                placeholder: "-",
                type: "select",
                //tooltip: 'Modificar', //TODO
                cssclass: 'inline-select',
                data: $(this).attr("data-options")
            });
        });
        $("a").each(function () {
            if ($(this).attr("href")) {
                $(this).attr("href", $(this).attr("href").replace("@cp@/", getContextPath()));
            }
        });

        $(".selected-operation").on("click", function (e) {
            e.preventDefault();
            var instanceIds = $.map($('.selectable:checked'), function (n, i) {
                return $(n).attr("data-id");
            }).join(',');
            if (instanceIds !== "") {
                var btn = $(this);
                var confirm = btn.hasClass("confirm-true");
                var $href = $(this).attr("href").replace("@@", instanceIds);
                if (confirm) {
                    jpmDialogConfirm({
                        callback: function () {
                            jpmBlock();
                            var form = $('<form method="POST" action="' + $href + '" id="tmpjpmForm"></form>');
                            buildAjaxJpmFormObject(form, function (data) {
                                processFormResponse(data);
                            }).submit();
                            return true;
                        }
                    });
                } else {
                    jpmBlock();
                    document.location = $href;
                }
            }
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
                 field: "<REQUIRED>",
                 textField: "<REQUIRED>",
                 placeholder: "...",
                 minSearch: 0,
                 filter: "",
                 ownerId: "",
                 related: null, (use another select2 object here)
                 sortBy: "",
                 pageSize: 10
                 });
                 */
                buildJpmSelect2: function (params) {
                    return this.each(function () {
                        select = $(this);
                        var s2 = select.select2({
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
                                        relatedValue: (params.related) ? (Array.isArray(params.related) ? params.related.map(function (i, sel) {
                                            return sel.val();
                                        }).toArray().join(',') : params.related.val()) : "",
                                        textField: params.textField || "",
                                        query: p.term,
                                        sortBy: params.sortBy || "",
                                        pageSize: params.pageSize || 10,
                                        page: p.page
                                    };
                                }
                            }
                        });
                        //All related must be reseted
                        s2.on("change", function () {
                            select.parents(".jpm-content-panels:first").find("select[data-related='" + params.field + "']").val(null).trigger("change");
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

let jpmUnBlock = function () {
    $.unblockUI();
};

var processFormResponse = function (data) {
    $("#jpm_confirm").remove();
    if (data.ok) {
        if (data.messages.length > 0) {
            var $message = '<ul>';
            $.each(data.messages, function (m, text) {
                $message += "<li>" + text.text + "</li>";
            });
            $message += "</ul>";
            let params = {
                title: messages["jpm.modal.confirm.close"],
                titleBackground: 'bg-success text-light',
                message: $message
            };
            if (!data.next || data.next === "" || data.next === "-") {
                jpmUnBlock();
            } else if (data.next === "EXECUTOR_RELOAD") {
                params.closeTimeout = data.messageDelay;
                params.callback = function () {
                    document.location.reload();
                };
            } else {
                params.closeTimeout = data.messageDelay;
                params.callback = function () {
                    document.location = getContextPath() + (data.next.startsWith("/") ? data.next.substr(1) : data.next);
                };
            }
            jpmDialog(params);
        }
    } else {
        $(".form-control").removeClass("is-invalid");
        $(".jpm-validator-text").remove();
        if (data.confirmation) {
            jpmDialogConfirm({
                message: data.confirmation.text,
                callback: function () {
                    var form = $('#jpmForm'); //TODO
                    form.append("<input type='hidden' name='jpm_confirm' value='true' id='jpm_confirm' />");
                    var extraInputs = $("<div style='display: none;'></div>");
                    $(".jpm-dialog-confirm").find("input,select,textarea").each(function () {
                        extraInputs.append($(this));
                    });
                    form.append(extraInputs);
                    form.submit();
                    return true;
                }
            });
        }
        //Entity
        if (data.messages.length > 0) {
            var $message = '<div><ul>';
            $.each(data.messages, function (m, text) {
                $message += "<li>" + text.text + "</li>";
            });
            $message = $message + "</ul></div>";
            jpmDialog({
                title: "",
                message: $message,
                titleBackground: 'bg-danger text-light',
                okBtn: messages["jpm.modal.confirm.close"],
                callback: jpmUnBlock
            });
        }
        //field
        $.each(data.fieldMessages, function (fieldId, msgs) {
            var controlGroup = $("#control-group-" + fieldId);
            controlGroup.addClass("has-error");
            $.each(msgs, function (i, item) {
                controlGroup.find(".form-control").addClass('is-invalid');
                controlGroup.find(".form-select").addClass('is-invalid');
                controlGroup.find(".converted-field-container").append('<div class="invalid-feedback jpm-validator-text">' + item.text + '</div>');
            });
        });
        jpmUnBlock();
    }
};

function buildAjaxJpmForm(formId, callback, beforeSubmit, beforeSerialize) {
    if (!formId) {
        formId = "jpmForm";
    }
    return buildAjaxJpmFormObject($('#' + formId), callback, beforeSubmit, beforeSerialize);
}

function buildAjaxJpmFormObject(form, callback, beforeSubmit, beforeSerialize) {
    return form.ajaxForm({
        dataType: 'json',
        beforeSerialize: function ($form, options) {
            if (beforeSerialize) {
                if (!beforeSerialize($form, options)) {
                    return false;
                }
            }
        },
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
            jpmDialog({
                title: "",
                message: "Unexpected error!",
                titleBackground: 'bg-danger text-light',
                okBtn: messages["jpm.modal.confirm.close"],
                callback: jpmUnBlock
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
                    $(id).show();
                    $(id + " > .progress-bar").css("width", (Math.round(r.percent * 100) / 100) + "%");
                    $(id + "_status").text((Math.round(r.percent * 100) / 100) + "% (" + r.status + ")");
                });
                stompClient.subscribe('/asynchronicOperationExecutor/done/' + id, function (s) {
                    var res = JSON.parse(s.body);
                    if (res.error !== null && res.error !== '') {
                        jpmDialog({
                            message: res.error,
                            titleBackground: "bg-danger",
                            callback: function () {
                                window.history.back();
                            }
                        });
                    } else {
                        var status = res.status;
                        if (status !== '') {
                            jpmDialogConfirm({
                                message: status,
                                callback: function () {
                                    document.location.reload();
                                }
                            });
                            setTimeout(function () {
                                document.location.reload();
                            }, 2000);
                        }
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
    var ct = $(this).data("type");
    var id = $(this).data("id");
    var entity = $(this).data("entity");
    var downloadable = $(this).data("downloadable");
    var $textAndPic = $('<div id="attachmentPopup"></div>');
    var html = "";
    if (ct.includes("image")) {
        html = html + "<img id='attachmentImg' src='" + getContextPath() + "static/" + entity + "/" + id + "/downloadAttachment?download=false" + "'/>";
    } else if (ct.contains("pdf")) {
        html = html + "<iframe src='" + getContextPath() + "static/" + entity + "/" + id + "/downloadAttachment?download=false" + "' style='height:500px;width:100%;'></iframe>";
    } else {
        html = html + "<div class='alert alert-info' >" + messages["jpm.modal.attachment.preview"] + "</div>";
    }
    $textAndPic.append(html);
    if (downloadable) {
        jpmDialogConfirm({
            title: messages["jpm.modal.attachment.title"],
            okBtn: messages["jpm.modal.attachment.download"],
            message: $textAndPic.html(),
            addClass: "modal-lg",
            callback: function () {
                document.location = getContextPath() + "static/" + entity + "/" + id + "/downloadAttachment?download=true";
            }
        });
    } else {
        jpmDialog({
            title: messages["jpm.modal.attachment.title"],
            message: $textAndPic.html(),
            addClass: "modal-lg"
        });
    }
});
$(window).on("load", initPage);