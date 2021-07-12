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
        <div class="container-fluid">
            <div class="row">
                <div class="col-lg-6">
                    <div class="card">
                        <div class="card-header"></span><h5>General Status</h5></div>
                        <div class="card-body">
                            <div>
                                <table class="table table-bordered table-sm">
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
                                        <th>Service</th>
                                        <td>${jpm.service}</td>
                                    </tr>
                                    <tr>
                                        <th>AuditService</th>
                                        <td>${jpm.auditService}</td>
                                    </tr>
                                </table>
                            </div>
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
                            <c:if test="${not empty e.clazz}">
                                <div class="card">
                                    <div class="card-header">
                                        <h4 class="card-title">
                                            <a class="accordion-toggle collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapse${st.index}">
                                                <h5>${e.clazz}</h5>
                                            </a>
                                        </h4>
                                    </div>
                                    <div id="collapse${st.index}" class="panel-body collapse ${st.first?'in':''}">
                                        <div class="card-body">
                                            <div class="row">
                                                <div class="col-lg-6">
                                                    <table class="table table-bordered table-sm">
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
                                                    <table class="table table-bordered table-sm">
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
                                                                    <td>${o.context}</td>
                                                                    <td>${o.validator}</td>
                                                                    <td>${o.follows}</td>
                                                                    <td>${o.condition}</td>
                                                                    <td>${o.auditable}</td>
                                                                    <td>${o.auth}</td>
                                                                </tr>                                                                    
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-lg-12">
                                                    <table class="table table-bordered table-sm">
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
                                                                    <td>${f.searcher}</td>
                                                                    <td>${f.sortable}</td>
                                                                </tr>
                                                                <tr>
                                                                    <td colspan="6">
                                                                        <table class="table-bordered table-sm" style="width: 100%; font-size: x-small">
                                                                            <tbody>
                                                                                <c:forEach var="c" items="${f.configs}">
                                                                                    <tr>
                                                                                        <td><b>${c.operations}</b></td>
                                                                                        <td>Auth: ${c.auth}</td>
                                                                                        <td>${c.converter}</td>
                                                                                        <td>${c.validator}</td>
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
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </jpm:jpm-body>
    <script type="text/javascript">
        jpmLoad(function () {
            $("#menu-status").addClass("active");
        });
    </script>
</html>