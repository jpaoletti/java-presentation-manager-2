<%@tag description="Default body" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@attribute name = "showMenu" required="false" type="java.lang.Boolean" %>
<%@attribute name = "showHeader" required="false" type="java.lang.Boolean" %>
<body lang="${locale.language}">
    <div class="page-wrapper default-theme sidebar-bg toggled">
        <nav id="sidebar" class="sidebar-wrapper">
            <span style="position: absolute; top: 3px; right: -30px;">
                <button id="pin-sidebar" class="btn btn-sm btn-dark d-none d-sm-flex" style="width: 35px;"><i class="fas fa-thumbtack"></i></button>&nbsp;
            </span>
            <span style="position: absolute; top: 27px; right: -30px;">
                <button id="addFavoriteLink" class="btn btn-sm btn-secondary d-flex" style="width: 35px;" title="<spring:message code="jpm.usernav.addfavorite" text="Add to Favorites" />"><i class="fas fa-star"></i></button>&nbsp;
            </span>
            <span style="position: absolute; top: 27px; right: -30px;">
                <button id="removeFavoriteLink" class="btn btn-sm btn-warning  d-flex" style="width: 35px;" title="<spring:message code="jpm.usernav.removefavorite" text="Add to Favorites" />"><i class="fas fa-star"></i></button>&nbsp;
            </span>
            <div class="sidebar-content mCustomScrollbar _mCS_1 mCS-autoHide desktop">
                <div id="mCSB_1" class="mCustomScrollBox mCS-light mCSB_vertical mCSB_inside" style="max-height: none;" tabindex="0">
                    <div id="mCSB_1_container" class="mCSB_container" style="position:relative; top:0; left:0;" dir="ltr">
                        <!-- sidebar-brand  -->
                        <div class="sidebar-item sidebar-brand">
                            <!--TO-DO-->
                            <!--<button id="pin-sidebar" class="btn btn-sm btn-light d-none d-sm-block"><i class="fas fa-thumbtack"></i></button>&nbsp;-->
                            <a href="${cp}">${jpm.title}<br/> <small>v${jpm.appversion}</small></a>
                        </div>
                        <!-- sidebar-header  -->
                        <div class="sidebar-item sidebar-header d-flex flex-nowrap">
                            <div class="user-pic">
                                <!-- TO-DO --> 
                                <img class="img-responsive img-rounded mCS_img_loaded" src="${cp}static/img/user.jpg" alt="">
                            </div>
                            <div class="user-info">
                                <span class="user-name">${user.name}</span>
                                <span class="user-role">${user.userGroups}</span>
                            </div>
                        </div>
                        <!-- sidebar-search  -->
                        <div class="sidebar-item sidebar-search">
                            <div>
                                    <div class="dropdown" id="searchDropdown">
                                <div class="input-group">
                                    <!-- TO-DO --> 
                                    <input type="text" class="form-control search-menu" placeholder="<spring:message code="jpm.usernav.search" text="Search..." />">
                                    <div class="input-group-append">
                                        <span class="input-group-text">
                                            <i class="fa fa-search" aria-hidden="true"></i>
                                        </span>
                                    </div>
                                        
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- sidebar-menu  -->
                        <div class=" sidebar-item sidebar-menu">
                            <%@include file="../jsp/inc/menu/sidemenu.jsp" %>
                        </div>
                        <!-- sidebar-menu  -->
                    </div><div id="mCSB_1_scrollbar_vertical" class="mCSB_scrollTools mCSB_1_scrollbar mCS-light mCSB_scrollTools_vertical" style="display: block;"><div class="mCSB_draggerContainer"><div id="mCSB_1_dragger_vertical" class="mCSB_dragger" style="position: absolute; min-height: 30px; height: 167px; top: 0px; display: block; max-height: 339px;"><div class="mCSB_dragger_bar" style="line-height: 30px;"></div></div><div class="mCSB_draggerRail"></div></div></div></div></div>
            <!-- sidebar-footer  -->
            <div class="sidebar-footer">
                <security:authorize access="hasAnyRole('ROLE_USER_FAVORITE')">
                    <div class="dropdown" id="userNavFavorite">
                        <a href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" title="<spring:message code="jpm.usernav.favorite" text="Favorites" />">
                            <i class="fa fa-star"></i>
                        </a>
                            <div class="dropdown-menu messages" aria-labelledby="dropdownMenuMessage">
                        </div>
                    </div>
                </security:authorize>
                <security:authorize access="hasAnyRole('ROLE_USER_RECENT')">
                    <div class="dropdown" id="userNavRecent">
                        <a href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" title="<spring:message code="jpm.usernav.recent" text="Recent" />">
                            <i class="fa fa-history"></i>
                        </a>
                        <div class="dropdown-menu messages" aria-labelledby="dropdownMenuMessage">
                        </div>
                    </div>
                </security:authorize>
                <div class="dropdown">
                    <a href="#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="fa fa-cog"></i>
                        <!--<span class="badge-sonar"></span>-->
                    </a>
                    <div class="dropdown-menu" aria-labelledby="dropdownMenuMessage">
                        <a class="dropdown-item" href="${cp}jpm/user/${user.username}/profile" id="userProfileLink"><spring:message code="jpm.login.profile" text="My profile" /></a>
                        <!--<a class="dropdown-item" href="#">Help</a>-->
                        <!--<a class="dropdown-item" href="#">Setting</a>-->
                    </div>
                </div>
                <div>
                    <a href="${cp}logout">
                        <i class="fa fa-power-off"></i>
                    </a>
                </div>
                <div class="pinned-footer">
                    <a href="#">
                        <i class="fas fa-ellipsis-h"></i>
                    </a>
                </div>
            </div>
        </nav>

        <!-- page-content  -->
        <main class="page-content pt-2">
            <div id="overlay" class="overlay"></div>
            <div class="container-fluid p-5">
                <jsp:doBody />
            </div>
        </main>
        <!-- page-content" -->
        <%--<%@include  file="../jsp/inc/footer.jsp" %>--%>
    </div>
    <%@include  file="../jsp/inc/default-javascript.jsp" %>
</body>