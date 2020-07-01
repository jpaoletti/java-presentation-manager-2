<%@include file="default-taglibs.jsp" %>
<%@include file="loading.jsp" %>
<header>
    <nav class="navbar navbar-expand-md navbar-light fixed-top bg-light">
        <a class="navbar-brand" href="${cp}index">
            <span class="fa fa-home"></span>
        </a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarCollapse">
            <ul class="navbar-nav mr-auto">
                <%@include file="menu/top-menu.jsp" %>
            </ul>
            <ul class="navbar-nav">
                <security:authorize access="hasAnyRole('ROLE_USER_FAVORITE')">
                    <li class="nav-item dropdown" id="userNavFavorite">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <span class="fa fa-star"></span> <span class="text d-none d-sm-none d-md-inline"><spring:message code="jpm.usernav.favorite" text="Favorites" /></span> <b class="caret"></b>
                        </a>
                        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                            <a class="dropdown-item" href="#" id="addFavoriteLink"><span class="fa fa-plus"></span> <spring:message code="jpm.usernav.addfavorite" text="Add to Favorites" /></a>
                            <a class="dropdown-item" href="#" id="removeFavoriteLink"><span class="fa fa-minus"></span> <spring:message code="jpm.usernav.removefavorite" text="Remove from Favorites" /></a>
                            <div class="dropdown-divider"></div>
                        </div>
                    </li>
                </security:authorize>
                <security:authorize access="hasAnyRole('ROLE_USER_RECENT')">
                    <li class="nav-item dropdown" id="userNavRecent">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <span class="fa fa-time"></span> <span class="d-sm-none d-md-inline-block"><spring:message code="jpm.usernav.recent" text="Recent" /></span> <b class="caret"></b>
                        </a>
                        <div class="dropdown-menu" aria-labelledby="navbarDropdown"></div>
                    </li>
                </security:authorize>
                <li class="nav-item">
                    <a class="nav-link" href="${cp}jpm/user/${user.username}/profile" id="userProfileLink" > <span class="fa fa-cog"></span> <span class="d-sm-none d-md-inline-block"><spring:message code="jpm.login.profile" text="Logout" /></span></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${cp}logout" id="logoutLink" ><span class="fa fa-sign-out-alt"></span> <span class="d-sm-none d-md-inline-block">  <spring:message code="jpm.login.logout" text="Logout" /></span></a>
                </li>
            </ul>
        </div>
    </nav>
</header>