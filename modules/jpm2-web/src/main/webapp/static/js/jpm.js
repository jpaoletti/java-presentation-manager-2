function initConfirm() {
    $("body").on("click", ".confirm-true", function(e) {
        $("#confirmModal").remove();
        e.preventDefault();
        var x = $("<div class='modal fade' id='confirmModal' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>"
                + "<div class='modal-dialog'><div class='modal-content'><div class='modal-header'>"
                + "<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"
                + "<h4 class='modal-title'>" + messages["jpm.modal.confirm.title"] + "</h4>"
                + "</div>"
                + "<div class='modal-body'>" + messages["jpm.modal.confirm.text"] + "</div>"
                + "<div class='modal-footer'>"
                + "<button type='button' class='btn btn-default' data-dismiss='modal' >" + messages["jpm.modal.confirm.cancel"] + "</button>"
                + "<a class='btn btn-primary' href='" + $(this).attr("href") + "' >" + messages["jpm.modal.confirm.submit"] + "</button>"
                + "</div></div></div></div>"
                );
        x.appendTo("body");
        x.modal("show");
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
    $(window).resize(function() {
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
            $(window).scroll(function() {
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
        $(window).scroll(function() {
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
    $('li.submenu > a').click(function(e) {
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

$(document).ready(function() {
    //Clean empty help-blocks
    $(".help-block:empty").remove();
    $(".panel-body:not(:has(div))").parent(".panel").parent().remove();
    $(".row-fluid:not(:has(div))").remove();
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
});
