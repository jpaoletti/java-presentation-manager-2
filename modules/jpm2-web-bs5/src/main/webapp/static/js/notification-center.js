(function ($) {
    function notificationCenter() {
        var $root = $("#notificationCenter");
        if ($root.length === 0 || $root.data("initialized")) {
            return;
        }
        $root.data("initialized", true);

        var contextPath = (window.contextPath || "") + "";
        var bellEndpoint = contextPath + "jpm/notifications/bell";
        var readAllEndpoint = contextPath + "jpm/notifications/read-all";
        var websocketLoaded = false;

        function normalizePayload(payload) {
            if (!payload || typeof payload !== "object") {
                return {unreadCount: 0, items: [], viewAllUrl: "jpm/notification/list"};
            }
            if (!Array.isArray(payload.items)) {
                payload.items = [];
            }
            if (!payload.viewAllUrl) {
                payload.viewAllUrl = "jpm/notification/list";
            }
            if (typeof payload.unreadCount !== "number") {
                payload.unreadCount = payload.items.length;
            }
            return payload;
        }

        function render(payload) {
            payload = normalizePayload(payload);
            var unreadCount = payload.unreadCount || 0;
            var $badge = $root.find(".notification-badge");
            var $items = $root.find(".notification-items");
            var $empty = $root.find(".notification-empty");
            var $markAll = $root.find(".notification-mark-all");
            var $viewAll = $root.find(".notification-view-all");

            if (unreadCount > 0) {
                $badge.text(unreadCount > 99 ? "99+" : unreadCount).removeClass("d-none");
            } else {
                $badge.addClass("d-none").text("0");
            }

            $items.empty();
            if (!payload.items || payload.items.length === 0) {
                $empty.removeClass("d-none");
                $markAll.addClass("disabled");
            } else {
                $empty.addClass("d-none");
                $markAll.removeClass("disabled");
                payload.items.forEach(function (item) {
                    var html = ''
                            + '<li class="notification-item">'
                            + '  <div class="dropdown-item-text border-bottom py-2">'
                            + '    <div class="d-flex gap-2 align-items-start">'
                            + '      <a href=' + getContextPath() + 'jpm/notification/' + item.id + '/show.exec><span class="text-' + item.typeCssClass + ' mt-1"><i class="' + item.typeIconClass + '"></i></span></a>'
                            + '      <div class="flex-grow-1">'
                            + '        <div class="small text-muted">' + item.createdAt + '</div>'
                            + '        <div class="small">' + item.description + '</div>'
                            + '      </div>'
                            + '      <button type="button" class="btn btn-sm btn-link text-decoration-none notification-mark-read" data-id="' + item.id + '">'
                            + $root.data("markReadLabel")
                            + '      </button>'
                            + '    </div>'
                            + '  </div>'
                            + '</li>';
                    $items.append(html);
                });
            }

            $viewAll.attr("href", contextPath + payload.viewAllUrl.replace(/^\//, ""));
        }

        function refresh() {
            $.getJSON(bellEndpoint, render);
        }

        function connectWebsocket() {
            if (websocketLoaded) {
                return;
            }
            websocketLoaded = true;
            $.getScript(contextPath + "static/js/sockjs.min.js", function () {
                $.getScript(contextPath + "static/js/stomp.min.js", function () {
                    var socket = new SockJS(getContextPath() + "jpm-websocket");
                    var stompClient = Stomp.over(socket);
                    stompClient.debug = null;
                    stompClient.connect({}, function () {
                        stompClient.subscribe("/user/notifications", function (message) {
                            try {
                                render(JSON.parse(message.body));
                            } catch (e) {
                                refresh();
                            }
                        });
                    });
                });
            });
        }

        $root.on("click", ".notification-mark-read", function (e) {
            e.preventDefault();
            var id = $(this).data("id");
            $.post(contextPath + "jpm/notifications/" + id + "/read")
                    .done(render)
                    .fail(refresh);
        });

        $root.on("click", ".notification-mark-all", function (e) {
            e.preventDefault();
            if ($(this).hasClass("disabled")) {
                return;
            }
            $.post(readAllEndpoint)
                    .done(render)
                    .fail(refresh);
        });

        refresh();
        connectWebsocket();
    }

    $(notificationCenter);
    if (typeof window.jpmLoad === "function") {
        window.jpmLoad(notificationCenter);
    }
})(jQuery);
