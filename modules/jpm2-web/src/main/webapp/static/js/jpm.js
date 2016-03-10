String.prototype.trim = function () {
    return this.replace(/(?:(?:^|\n)\s+|\s+(?:$|\n))/g, "");
};


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
        BootstrapDialog.confirm(messages["jpm.modal.confirm.text"], function (result) {
            if (result) {
                document.location = $href;
            }
        });
    });
}

function initWindowsResize() {
    var ul = $('#sidebar > ul');
    var ul2 = $('#sidebar li.open ul');
    // === jPanelMenu === //
    var jPanel = $.jPanelMenu({
        menu: '#sidebar',
        trigger: '#menu-trigger'
    });
    $(window).resize(function () {
        if ($(window).width() > 480 && $(window).width() < 769) {
            ul2.css({'display': 'none'});
            ul.css({'display': 'block'});
        }

        if ($(window).width() <= 480) {
            ul.css({'display': 'none'});
            ul2.css({'display': 'block'});
            if (!$('html').hasClass('jPanelMenu')) {
                jPanel.on();
            }

            if ($(window).scrollTop() > 35) {
                $('body').addClass('fixed');
            }
            $(window).scroll(function () {
                if ($(window).scrollTop() > 35) {
                    $('body').addClass('fixed');
                } else {
                    $('body').removeClass('fixed');
                }
            });
        } else {
            jPanel.off();
        }
        if ($(window).width() > 768) {
            ul.css({'display': 'block'});
            ul2.css({'display': 'block'});
            $('#user-nav > ul').css({width: 'auto', margin: '0'});
        }
    });

    if ($(window).width() <= 480) {
        if ($(window).scrollTop() > 35) {
            $('body').addClass('fixed');
        }
        $(window).scroll(function () {
            if ($(window).scrollTop() > 35) {
                $('body').addClass('fixed');
            } else {
                $('body').removeClass('fixed');
            }
        });
        jPanel.on();
    }

    if ($(window).width() > 480) {
        ul.css({'display': 'block'});
        jPanel.off();
    }
    if ($(window).width() > 480 && $(window).width() < 769) {
        ul2.css({'display': 'none'});
    }
}

function initMenu() {
    $('li.submenu > a').click(function (e) {
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

$(window).unload(function () {
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
        $(".panel-body:not(:has(div))").parent(".panel").parent().remove();
        $(".row-fluid:not(:has(div))").remove();
        $(".sortable").click(function () {
            window.location = $(this).attr("data-cp") + $(this).attr("data-entity") + "/sort?fieldId=" + $(this).attr("data-field");
        });
        initConfirm();
        //Init Menu
        initWindowsResize();
        // === Sidebar navigation === //
        initMenu();

        // === Tooltips === //
        $('.tip').tooltip();
        $('.tip-left').tooltip({placement: 'left'});
        $('.tip-right').tooltip({placement: 'right'});
        $('.tip-top').tooltip({placement: 'top'});
        $('.tip-bottom').tooltip({placement: 'bottom'});

        if (currentUser && currentUser !== '') {
            $("#userNavRecent");
            var url = $(location).attr('href');
            if (!url.match(/#$/)) {
                var name = "jpm_recent_" + currentUser;
                var name2 = "jpm_lastUser";
                if ($.cookie(name2) !== currentUser) {
                    $.cookie(name2, currentUser);
                    $.cookie(name, "", {path: '/'});
                    $.removeCookie(name, {path: '/'});
                }
                var _recentArray = $.cookie(name);
                var recentArray = new Array();
                if (typeof _recentArray !== "undefined" && _recentArray !== "") {
                    recentArray = $.parseJSON(_recentArray);
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
                    $("#userNavRecent").find(".dropdown-menu").append("<li><a title='' href='" + item.url + "'>" + item.title + "</a></li>")
                });
            }
        }

        $("body").on("click", ".inline-boolean", function () {
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
                            width: 'copy',
                            dropdownCssClass: "bigdrop",
                            minimumInputLength: params.minSearch || 0,
                            ajax: {
                                url: getContextPath() + "jpm/" + params.entity + ".json?filter=" + (params.filter || "") + "&ownerId=" + (params.ownerId || ""),
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
                document.location = getContextPath() + data.next;
            }, 1000);
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
}

function buildAjaxJpmForm(formId, callback, beforeSubmit) {
    if (!formId)
        formId = "jpmForm";
    $('#' + formId).ajaxForm({
        dataType: 'json',
        beforeSubmit: function () {
            if (beforeSubmit) {
                if (!beforeSubmit()) {
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
    $.getJSON(getContextPath() + "jpm/" + select.attr('data-entity') + "/" + id + ".json" + ((!!select.attr('data-textField')) ? "?textField=" + select.attr('data-textField') : ""), function (data) {
        select.append("<option value=" + id + ">" + data.text + "</option>");
        select.val(id).trigger("change");
        if (callback) {
            callback(select, data);
        }
    });
    return select;
}

$(window).load(initPage);
