<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <div id="content-header" class="page-header">
            <h1>${jpm.subtitle}</h1>
        </div>
        <%@include file="inc/default-breadcrumb.jsp" %>
        <div class="container-fluid"><br/>
            <div class="row">
                <div class="col-lg-6">
                    <div class="card">
                        <div class="card-header">
                            <h5>
                                <span class="fa fa-chart-bar"></span>
                                <spring:message code="jpm.security.home.securityStatistics" text="Security Statistics"/>
                            </h5>
                        </div>
                        <div class="card-body">
                            <ul class="site-stats">
                                <li>
                                    <div class="cc"><i class="fas fa-user"></i>
                                        <spring:message code="jpm.security.home.enabledUsersCount" arguments="${enabledUsersCount}" />
                                    </div>
                                </li>
                                <li>
                                    <div class="cc"><i class="fas fa-ban"></i>
                                        <spring:message code="jpm.security.home.disabledUsersCount" arguments="${disabledUsersCount}" />
                                    </div>
                                </li>
                                <li>
                                    <div class="cc"><i class="fas fa-bullhorn"></i>
                                        <spring:message code="jpm.security.home.groupCount" arguments="${groupCount}" />
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="card">
                        <div class="card-header">
                            <span class="icon"><i class="fas fa-lock"></i></span> &nbsp;
                            <h5>
                                <spring:message code="jpm.security.home.authorities" text="Security Statistics"/>
                            </h5>
                        </div>
                        <div class="card-body">
                            <table class="table table-sm table-bordered">
                                <thead>
                                    <tr>
                                        <th style="width: 1px;"><spring:message code="jpm.security.authority" text="Authority"/></th>
                                        <th><spring:message code="jpm.security.authorityDesc" text="Description"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="a" items="${authorities}">
                                        <tr>
                                            <td>${a}</td>
                                            <td><spring:message code="jpm.security.authority.${a}" text="-"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript">
            jpmLoad(function() {
                $("#menu-home").addClass("active");
            });
        </script>
    </jpm:jpm-body>
</html>