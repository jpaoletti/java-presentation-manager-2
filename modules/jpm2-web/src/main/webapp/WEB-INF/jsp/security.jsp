<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <%@include file="inc/header.jsp" %>
        <%@include file="inc/menu/security-menu.jsp" %>
        <div id="container">
            <div id="content">
                <div id="content-header">
                    <h1>${jpm.subtitle}</h1>
                </div>
                <%@include file="inc/default-breadcrumb.jsp" %>
                <div class="container-fluid"><br/>
                    <div class="row">
                        <div class="col-lg-6">
                            <div class="widget-box">
                                <div class="widget-title">
                                    <span class="icon"><i class="glyphicon glyphicon-stats"></i></span> &nbsp;
                                    <h5>
                                        <spring:message code="jpm.security.home.securityStatistics" text="Security Statistics"/>
                                    </h5>
                                </div>
                                <div class="widget-content">
                                    <ul class="site-stats">
                                        <li>
                                            <div class="cc"><i class="glyphicon glyphicon-user"></i>
                                                <spring:message code="jpm.security.home.enabledUsersCount" arguments="${enabledUsersCount}" />
                                            </div>
                                        </li>
                                        <li>
                                            <div class="cc"><i class="glyphicon glyphicon-ban-circle"></i>
                                                <spring:message code="jpm.security.home.disabledUsersCount" arguments="${disabledUsersCount}" />
                                            </div>
                                        </li>
                                        <li>
                                            <div class="cc"><i class="glyphicon glyphicon-bullhorn"></i>
                                                <spring:message code="jpm.security.home.groupCount" arguments="${groupCount}" />
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="widget-box">
                                <div class="widget-title">
                                    <span class="icon"><i class="glyphicon glyphicon-lock"></i></span> &nbsp;
                                    <h5>
                                        <spring:message code="jpm.security.home.authorities" text="Security Statistics"/>
                                    </h5>
                                </div>
                                <div class="widget-content">
                                    <table class="table table-compact table-bordered">
                                        <thead>
                                            <tr>
                                                <th style="width: 1px;"><spring:message code="jpm.security.home.authority" text="Authority"/></th>
                                                <th><spring:message code="jpm.security.home.authorityDesc" text="Description"/></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="a" items="${authorities}">
                                                <tr>
                                                    <td>${a}</td>
                                                    <td><spring:message code="jpm.security.home.authority.${a}" text="-"/></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
        <script type="text/javascript">
            jpmLoad(function() {
                $("#menu-home").addClass("active");
            });
        </script>
    </body>
</html>