<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <div id="content-header" class="page-header">
            <h1>${jpm.subtitle}</h1>
        </div>
        <ol class="breadcrumb">
            <li>
                <a href="#" title="" class="tip-bottom" class="current"><i class="glyphicon glyphicon-home"></i> <spring:message code="jpm.index.home" text="Home" /></a>
            </li>
            <li>
                <a href="#" class="current"><spring:message code="jpm.menu.status" text="Status" /></a>
            </li>
        </ol>
        <div class="container-fluid">
            <div class="row">
                <div class="col-lg-6">
                    <div class="panel panel-default">
                        <div class="panel-heading"></span><h5>General Status</h5></div>
                        <div class="panel-body">
                            <table class="table table-bordered table-compact">
                                <tr>
                                    <th>App version</th>
                                    <td>${jpm.appversion}</td>
                                </tr>
                                <tr>
                                    <th>Title</th>
                                    <td>${jpm.title}</td>
                                </tr>
                                <tr>
                                    <th>Subtitle</th>
                                    <td>${jpm.subtitle}</td>
                                </tr>

                                <tr>
                                    <th>Contact</th>
                                    <td>${jpm.contact}</td>
                                </tr>
                                <tr>
                                    <th>CSS</th>
                                    <td>${jpm.cssMode}</td>
                                </tr>
                                <tr>
                                    <th>Service</th>
                                    <td>${jpm.service.class}</td>
                                </tr>
                                <tr>
                                    <th>AuditService</th>
                                    <td>${jpm.auditService.class}</td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6">

                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel-group" id="accordion">
                        <c:forEach var="entry" items="${jpm.entities}" varStatus="st">
                            <c:set var="e" value="${entry.value}" />
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    <h4 class="panel-title">
                                        <a class="accordion-toggle collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapse${st.index}">
                                            <h5>${e.clazz}</h5>
                                        </a>
                                    </h4>
                                </div>
                                <div id="collapse${st.index}" class="panel-body collapse ${st.first?'in':''}">
                                    <div class="panel-body">
                                        <div class="row">
                                            <div class="col-lg-6">
                                                <table class="table table-bordered table-compact">
                                                    <tr>
                                                        <th>ID</th>
                                                        <td>${e.id}</td>
                                                    </tr>
                                                    <tr>
                                                        <th>Class</th>
                                                        <td>${e.clazz}</td>
                                                    </tr>
                                                    <tr>
                                                        <th>DAO</th>
                                                        <td>${e.dao}</td>
                                                    </tr>
                                                    <tr>
                                                        <th>Parent</th>
                                                        <td>${e.parent}</td>
                                                    </tr>
                                                    <tr>
                                                        <th>Order</th>
                                                        <td>${e.order}</td>
                                                    </tr>
                                                    <tr>
                                                        <th>Owner</th>
                                                        <td>${e.owner}</td>
                                                    </tr>
                                                    <tr>
                                                        <th>Countable</th>
                                                        <td>${e.countable}</td>
                                                    </tr>
                                                    <tr>
                                                        <th>Paginable</th>
                                                        <td>${e.paginable}</td>
                                                    </tr>
                                                    <tr>
                                                        <th>Auth</th>
                                                        <td>${e.auth}</td>
                                                    </tr>
                                                    <tr>
                                                        <th>Home</th>
                                                        <td>${e.home}</td>
                                                    </tr>
                                                </table>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-lg-12">
                                                <table class="table table-bordered table-compact">
                                                    <caption>Operations</caption>
                                                    <thead>
                                                        <tr>
                                                            <th>ID</th>
                                                            <th>Scope</th>
                                                            <th>Display</th>
                                                            <th>Confirm</th>
                                                            <th>Context</th>
                                                            <th>Validator</th>
                                                            <th>Follows</th>
                                                            <th>Condition</th>
                                                            <th>Auditable</th>
                                                            <th>Auth</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="o" items="${e.operations}">
                                                            <tr>
                                                                <td>${o.id}</td>
                                                                <td>${o.scope}</td>
                                                                <td>${o.display}</td>
                                                                <td>${o.confirm}</td>
                                                                <td>${o.context.class}</td>
                                                                <td>${o.validator.class}</td>
                                                                <td>${o.follows}</td>
                                                                <td>${o.condition.class}</td>
                                                                <td>${o.auditable}</td>
                                                                <td>${o.auth}</td>
                                                            </tr>                                                                    
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                            <div class="row">
                                                <div class="col-12">
                                                    <table class="table table-bordered table-compact">
                                                        <caption>Fields</caption>
                                                        <thead>
                                                            <tr>
                                                                <th>ID</th>
                                                                <th>Property</th>
                                                                <th>Width</th>
                                                                <th>Default</th>
                                                                <th>Align</th>
                                                                <th>Searcher</th>
                                                                <th>Sort.</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="f" items="${e.fields}">
                                                                <tr>
                                                                    <th rowspan="2" style="vertical-align: middle">${f.id}</th>
                                                                    <td>${f.property}</td>
                                                                    <td>${f.width}</td>
                                                                    <td>${f.defaultValue}</td>
                                                                    <td>${f.align}</td>
                                                                    <td>${f.searcher.class}</td>
                                                                    <td>${f.sortable}</td>
                                                                </tr>
                                                                <tr>
                                                                    <td colspan="6">
                                                                        <table class="table-bordered table-compact" style="width: 100%; font-size: x-small">
                                                                            <tbody>
                                                                                <c:forEach var="c" items="${f.configs}">
                                                                                    <tr>
                                                                                        <td><b>${c.operations}</b></td>
                                                                                        <td>Auth: ${c.auth}</td>
                                                                                        <td>${c.converter.class}</td>
                                                                                        <td>${c.validator.class}</td>
                                                                                    </tr>
                                                                                </c:forEach>
                                                                            </tbody>
                                                                        </table>
                                                                    </td>
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
                        </c:forEach>
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
    </jpm:jpm-body>
</html>