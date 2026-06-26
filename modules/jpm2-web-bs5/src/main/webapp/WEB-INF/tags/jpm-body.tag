<%@tag description="Default body" pageEncoding="UTF-8"%>
<%@include file="../jsp/inc/default-taglibs.jsp" %>
<%@attribute name = "showMenu" required="false" type="java.lang.Boolean" %>
<%@attribute name = "showHeader" required="false" type="java.lang.Boolean" %>
<body lang="${locale.language}" data-menuMode="${jpm.menuMode}" data-menuTheme="${jpm.menuTheme}">
    <c:choose>
        <c:when test = "${jpm.menuMode == 'none'}">
            <main class="page-content pt-2">
                <div class="container-fluid p-1">
                    <jsp:doBody />
                </div>
            </main>
        </c:when>
        <c:when test = "${jpm.menuMode == 'top'}">
            <nav class="navbar navbar-expand-md fixed-top ${jpm.menuTheme}">
                <div class="container-fluid">
                    <a class="navbar-brand" href="${cp}index"><span class="fas fa-home"></span> <span class="hidden-sm">${jpm.title}</span></a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarCollapse">
                        <ul class="navbar-nav me-auto mb-2 mb-md-0">
                            <%@include file="../jsp/inc/menu/topmenu.jsp" %>
                        </ul>
                        <form class="d-flex">
                            <div class="dropdown" id="searchDropdown">
                                <input class="form-control me-2" type="search" id="search-menu" placeholder="<spring:message code="jpm.usernav.search" text="Search..." />" aria-label="Search">
                            </div>
                            <security:authorize access="hasAnyAuthority('ROLE_USER_FAVORITE')">
                                <div class="dropdown" id="userNavFavorite">
                                    <button class="btn my-2 my-sm-0 dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" title="<spring:message code="jpm.usernav.favorite" text="Favorites" />">
                                        <i class="fa fa-star text-warning"></i>
                                    </button>
                                    <ul class="dropdown-menu messages" style="left: auto; right: 0; position: absolute;" aria-labelledby="dropdownMenuMessage"></ul>
                                </div>
                            </security:authorize>
                            <security:authorize access="hasAnyAuthority('ROLE_USER_RECENT')">
                                <div class="btn-group dropleft">
                                    <div class="dropdown" id="userNavRecent">
                                        <button class="btn my-2 my-sm-0 dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" title="<spring:message code="jpm.usernav.recent" text="Recent" />">
                                            <i class="fa fa-history text-info"></i>
                                        </button>
                                        <ul class="dropdown-menu messages" style="left: auto; right: 0; position: absolute;" aria-labelledby="dropdownMenuMessage"></ul>
                                    </div>
                                </div>
                            </security:authorize>
                            <security:authorize access="hasAnyAuthority('jpm.auth.operation.notification.show')">
                                <div class="dropdown" id="notificationCenter" data-empty-label="<spring:message code='jpm.notification.empty' text='Sin novedades' />" data-mark-read-label="<spring:message code='jpm.notification.markRead' text='Marcar' />">
                                    <button class="btn my-2 my-sm-0 dropdown-toggle position-relative" type="button" data-bs-toggle="dropdown" aria-expanded="false" title="<spring:message code='jpm.notification.center.title' text='Notificaciones' />">
                                        <i class="fa-solid fa-bell text-danger"></i>
                                        <span class="notification-badge position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-none">0</span>
                                    </button>
                                    <ul class="dropdown-menu messages" style="left: auto; right: 0; position: absolute; min-width: 26rem;" aria-labelledby="dropdownMenuMessage">
                                        <li class="px-3 py-2 border-bottom d-flex justify-content-between align-items-center">
                                            <span class="fw-semibold"><spring:message code="jpm.notification.center.title" text="Notificaciones" /></span>
                                            <a class="small text-decoration-none notification-view-all" href="#"><spring:message code="jpm.notification.viewAll" text="Ver todas" /></a>
                                            <a class="small text-decoration-none notification-mark-all" href="#"><spring:message code="jpm.notification.markAll" text="Marcar todas" /></a>
                                        </li>
                                        <li class="dropdown-item-text small text-muted notification-empty px-3 py-3"><spring:message code="jpm.notification.empty" text="Sin novedades" /></li>
                                        <li class="notification-list-shell"><ul class="list-unstyled mb-0 notification-items"></ul></li>
                                    </ul>
                                </div>
                            </security:authorize>
                            <a class="btn my-2 my-sm-0" href="${cp}jpm/user/${user.username}/profile" id="userProfileLink" title="<spring:message code="jpm.login.profile" text="My profile" />"><i class="fa fa-id-card text-success"></i></a>
                            <a class="btn my-2 my-sm-0" href="${cp}logout" title="<spring:message code="jpm.login.logout" text="Logout" />"><i class="fa fa-power-off text-danger"></i></a>
                        </form>
                    </div>
                </div>
            </nav>
        </header>
        <main role="main" class="flex-shrink-0">
            <div id="jpm-main-content" class="container-fluid container-if-fixed-footer">
                <jsp:doBody />
            </div>
        </main>
        <div id="jpm-layout-footer">
            <%@include  file="../jsp/inc/footer.jsp" %>
        </div>
    </c:when>
    <c:when test = "${jpm.menuMode == 'left'}">
        <div class="page-wrapper ${jpm.menuTheme} sidebar-bg ${(empty showMenu or showMenu)?'toggled':''}">
            <c:if test="${empty showMenu or showMenu}">
                <security:authorize access="hasAnyAuthority('ROLE_USER')">
                    <nav id="sidebar" class="sidebar-wrapper">
                        <span style="position: absolute; top: 3px; right: -25px;">
                            <button id="pin-sidebar" class="btn btn-sm btn-dark d-none d-sm-flex"><i class="fas fa-thumbtack"></i></button>&nbsp;
                        </span>
                        <span style="position: absolute; top: 3px; right: -27px;">
                            <button id="toggle-sidebar" class="btn btn-sm btn-dark d-block d-sm-none"><i class="fas fa-bars"></i></button>&nbsp;
                        </span>
                        <div class="sidebar-content mCustomScrollbar _mCS_1 mCS-autoHide desktop">
                            <div id="mCSB_1" class="mCustomScrollBox mCS-light mCSB_vertical mCSB_inside" style="max-height: none;" tabindex="0">
                                <div id="mCSB_1_container" class="mCSB_container" style="position:relative; top:0; left:0;" dir="ltr">
                                    <div class="sidebar-item sidebar-brand">
                                        <a href="${cp}">${jpm.title}<br/> <small>v${jpm.appversion}</small></a>
                                    </div>
                                    <div class="sidebar-item sidebar-header d-flex flex-nowrap">
                                        <div class="user-pic">
                                            <img class="img-responsive img-rounded mCS_img_loaded" src="${cp}static/img/user.jpg" alt="" id="userPicture">
                                        </div>
                                        <div class="user-info">
                                            <span class="user-name"><c:out value="${user.name}"/></span>
                                            <span class="user-role"><c:out value="${user.userGroups}"/></span>
                                        </div>
                                    </div>
                                    <div class="sidebar-item sidebar-search">
                                        <div>
                                            <div class="dropdown" id="searchDropdown">
                                                <input type="text" class="form-control search-menu" id="search-menu" placeholder="<spring:message code="jpm.usernav.search" text="Search..." />">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="sidebar-item sidebar-menu">
                                        <%@include file="../jsp/inc/menu/sidemenu.jsp" %>
                                    </div>
                                </div><div id="mCSB_1_scrollbar_vertical" class="mCSB_scrollTools mCSB_1_scrollbar mCS-light mCSB_scrollTools_vertical" style="display: block;"><div class="mCSB_draggerContainer"><div id="mCSB_1_dragger_vertical" class="mCSB_dragger" style="position: absolute; min-height: 30px; height: 167px; top: 0px; display: block; max-height: 339px;"><div class="mCSB_dragger_bar" style="line-height: 30px;"></div></div><div class="mCSB_draggerRail"></div></div></div></div></div>
                        <div class="sidebar-footer">
                            <security:authorize access="hasAnyAuthority('ROLE_USER_FAVORITE')">
                                <div class="dropdown" id="userNavFavorite">
                                    <a href="#" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" title="<spring:message code="jpm.usernav.favorite" text="Favorites" />">
                                        <i class="fa fa-star text-warning"></i>
                                    </a>
                                    <ul class="dropdown-menu messages" aria-labelledby="dropdownMenuMessage"></ul>
                                </div>
                            </security:authorize>
                            <security:authorize access="hasAnyAuthority('ROLE_USER_RECENT')">
                                <div class="dropdown" id="userNavRecent">
                                    <a href="#" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" title="<spring:message code="jpm.usernav.recent" text="Recent" />">
                                        <i class="fa fa-history text-info"></i>
                                    </a>
                                    <ul class="dropdown-menu messages" aria-labelledby="dropdownMenuMessage"></ul>
                                </div>
                            </security:authorize>
                            <security:authorize access="hasAnyAuthority('jpm.auth.operation.notification.show')">
                                <div class="dropdown" id="notificationCenter" data-empty-label="<spring:message code='jpm.notification.empty' text='Sin novedades' />" data-mark-read-label="<spring:message code='jpm.notification.markRead' text='Marcar' />">
                                    <a href="#" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false" title="<spring:message code='jpm.notification.center.title' text='Notificaciones' />" class="position-relative">
                                        <i class="fa-solid fa-bell text-danger"></i>
                                        <span class="notification-badge position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-none">0</span>
                                    </a>
                                    <ul class="dropdown-menu messages" aria-labelledby="dropdownMenuMessage" style="min-width: 24rem;">
                                        <li class="px-3 py-2 border-bottom d-flex justify-content-between align-items-center">
                                            <span class="fw-semibold"><spring:message code="jpm.notification.center.title" text="Notificaciones" /></span>
                                            <a class="small text-decoration-none notification-view-all" href="#"><spring:message code="jpm.notification.viewAll" text="Ver todas" /></a>
                                            <a class="small text-decoration-none notification-mark-all" href="#"><spring:message code="jpm.notification.markAll" text="Marcar todas" /></a>
                                        </li>
                                        <li class="dropdown-item-text small text-muted notification-empty px-3 py-3"><spring:message code="jpm.notification.empty" text="Sin novedades" /></li>
                                        <li class="notification-list-shell"><ul class="list-unstyled mb-0 notification-items"></ul></li>
                                    </ul>
                                </div>
                            </security:authorize>
                            <div>
                                <a href="${cp}jpm/user/${user.username}/profile" id="userProfileLink" title="<spring:message code="jpm.login.profile" text="My profile" />"><i class="fa fa-id-card text-success"></i></a>
                            </div>
                            <div>
                                <a href="${cp}logout" title="<spring:message code="jpm.login.logout" text="Logout" />">
                                    <i class="fa fa-power-off text-danger"></i>
                                </a>
                            </div>
                            <div class="pinned-footer">
                                <a href="#">
                                    <i class="fas fa-ellipsis-h"></i>
                                </a>
                            </div>
                        </div>
                    </nav>
                </security:authorize>
            </c:if>
            <main class="page-content pt-2">
                <c:if test="${empty showMenu or showMenu}">
                    <div id="overlay" class="overlay"></div>
                </c:if>
                <div id="jpm-main-content" class="container-fluid p-1 ps-4 pe-4">
                    <jsp:doBody />
                </div>
            </main>
        </div>
    </c:when>
</c:choose>
<script type="text/javascript" src="${cp}static/js/notification-center.js?v=${jpm.appversion}"></script>
<%@include  file="../jsp/inc/default-javascript.jsp" %>
</body>
