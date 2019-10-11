<%@include file="loading.jsp" %>
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${cp}index"><span class="glyphicon glyphicon-home"></span> <span class="hidden-sm">${jpm.title}</span></a>
        </div>
        <div class="collapse navbar-collapse">
            <%@include file="menu/top-menu.jsp" %>
            <ul class="nav navbar-nav navbar-right">
                <security:authorize access="hasAnyRole('ROLE_USER_FAVORITE')">
                    <li id="userNavFavorite">
                        <a id="userNavFavoriteLink"  href="#" data-toggle="dropdown" data-target="#userNavFavorite" class="dropdown-toggle"><i class="glyphicon glyphicon-star"></i> <span class="text"><spring:message code="jpm.usernav.favorite" text="Favorites" /></span> <b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li id="addFavoriteLi"><a href="#" id="addFavoriteLink"><span class="glyphicon glyphicon-plus"></span> <spring:message code="jpm.usernav.addfavorite" text="Add to Favorites" /></a></li>
                            <li id="removeFavoriteLi"><a href="#" id="removeFavoriteLink"><span class="glyphicon glyphicon-minus"></span> <spring:message code="jpm.usernav.removefavorite" text="Remove from Favorites" /></a></li>
                            <li class="divider"></li>
                        </ul>
                    </li>
                </security:authorize>
                <security:authorize access="hasAnyRole('ROLE_USER_RECENT')">
                <li id="userNavRecent">
                    <a id="userNavRecentLink"  href="#" data-toggle="dropdown" data-target="#userNavRecent" class="dropdown-toggle"><i class="glyphicon glyphicon-time"></i> <span class="text hidden-sm"><spring:message code="jpm.usernav.recent" text="Recent" /></span> <b class="caret"></b></a>
                    <ul class="dropdown-menu"></ul>
                </li>
                </security:authorize>
                <li><a id="userProfileLink" title="" href="${cp}jpm/user/${user.username}/profile"><span class="glyphicon glyphicon-cog"></span> <span class="text hidden-sm"><spring:message code="jpm.login.profile" text="Logout" /></span></a></li>
                <li><a id="logoutLink" title="" href="${cp}logout"><span class="glyphicon glyphicon-share-alt"></span> <span class="text hidden-sm"><spring:message code="jpm.login.logout" text="Logout" /></span></a></li>
            </ul>
        </div>
    </div>
</div>