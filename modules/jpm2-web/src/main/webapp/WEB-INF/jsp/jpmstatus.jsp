<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <%@include file="inc/header.jsp" %>
        <%@include file="inc/menu.jsp" %>
        <div id="container">
            <div id="content">
                <div id="content-header">
                    <h1>${jpm.subtitle}</h1>
                </div>
                <div id="breadcrumb">
                    <a href="#" title="" class="tip-bottom" class="current"><i class="glyphicon glyphicon-home"></i> <spring:message code="jpm.index.home" text="Home" /></a>
                    <a href="#" class="current"><spring:message code="jpm.menu.status" text="Status" /></a>
                </div>
                <div class="container-fluid">
                    <div class="row">
                        <div class="widget-box">
                            <div class="widget-title"></span><h5>General Status</h5></div>
                            <div class="widget-content">
                                <table class="table table-bordered table-compact">
                                    <tr>
                                        <th>CSS</th>
                                        <td>${jpm.cssMode}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                        <s:iterator value="pm.entities" var="e">
                            <div class="widget-box">
                                <div class="widget-title"></span><h5>Entity for class: ${e.clazz}</h5></div>
                                <div class="widget-content">
                                    <table class="table table-bordered table-compact">
                                        <tr>
                                            <th>DAO</th>
                                            <td>${e.dao}</td>
                                        </tr>
                                        <tr>
                                            <th>Order</th>
                                            <td>${e.order}</td>
                                        </tr>
                                    </table>
                                    <table class="table table-bordered table-compact">
                                        <label>Operations</label>
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Enabled</th>
                                                <th>Scope</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <s:iterator value="#e.operations" var="o">
                                                <tr>
                                                    <td>${o.id}</td>
                                                    <td>${o.enabled}</td>
                                                    <td>${o.scope}</td>
                                                </tr>
                                            </s:iterator>
                                        </tbody>
                                    </table>
                                    <table class="table table-bordered table-compact">
                                        <label>Fields</label>
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <s:iterator value="#e.fields" var="f">
                                                <tr>
                                                    <td>${f.id}</td>
                                                </tr>
                                            </s:iterator>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </s:iterator>
                    </div>
                </div>
            </div>
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
        <script type="text/javascript">
            jpmLoad(function() {
                $("#menu-status").addClass("active");
            });
        </script>
    </body>
</html>