<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html>
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <body>
        <spring:message var="entityName" code="${entity.title}" text="${entity.title}" />
        <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
        <%@include file="inc/header.jsp" %>
        <%@include file="inc/menu.jsp" %>
        <div id="container">
            <div id="content">
                <%@include file="inc/default-content-header.jsp" %>
                <%@include file="inc/default-breadcrumb.jsp" %>
                <div class="container-fluid">
                    <div class="row"><br/>
                        <div class="panel panel-default">
                            <div class="panel-heading operation-list">
                                <div class="row">
                                    <div class="col-lg-offset-6 col-lg-6">
                                        <form action="" class="form-inline pull-right" role="form">
                                            <input type="hidden" name="entityId" value="${entityId}" />
                                            <input type="hidden" name="page" value="${paginatedList.page}" />
                                            <div class="form-group">
                                                <label class="sr-only">Email address</label>
                                                <input class="form-control" type="number" min="0" max="100" value="${paginatedList.pageSize}" name="pageSize" />
                                            </div>
                                            <button type="submit" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-arrow-right"></span></button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                            <table class="table table-bordered table-compact">
                                <thead>
                                    <tr>
                                        <th style="width: 5px;"><i class="glyphicon glyphicon-cog"></i></th>
                                            <s:iterator value="list.fields" var="field">
                                            <th><jpm:field-title entity="${entity}" fieldId="${field.id}" /></th>
                                            </s:iterator>
                                    </tr>
                                </thead>
                                <tbody>
                                    <s:iterator value="paginatedList.contents" var="item">
                                        <tr data-id="${item.id}">
                                            <td>
                                                <div class="btn-group nowrap">
                                                    <s:iterator value="operations" var="o">
                                                        <a 
                                                            class="btn btn-mini btn-default confirm-${o.confirm}" 
                                                            title="<spring:message code="${o.title}" text="${o.title}" arguments="${entityName}" />"
                                                            href="${o.id}?entityId=${entity.id}&instanceId=${item.id}">
                                                            <i class="glyphicon jpmicon-${o.id}"></i>
                                                        </a>
                                                    </s:iterator>
                                                </div>
                                            </td>
                                            <s:iterator value="values" var="v">
                                                <td>${v.value}</td>
                                            </s:iterator>
                                        </tr>
                                    </s:iterator>
                                </tbody>
                            </table>
                            <div class="panel-footer row list-pagination"><%@include file="inc/default-paginator.jsp" %></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%@include  file="inc/footer.jsp" %>
        <%@include  file="inc/default-javascript.jsp" %>
    </body>
</html>