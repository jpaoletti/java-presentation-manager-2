<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <div class="row mb-3">
                <div class="col-lg-12">
                    <div class="d-flex flex-wrap align-items-center gap-2">
                        <c:choose>
                            <c:when test="${report.healthy}">
                                <span class="badge bg-success fs-6">
                                    <i class="fas fa-check-circle"></i>
                                    <spring:message code="jpm.sysparam.health.ok" text="OK" />
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-danger fs-6">${report.errorCount}
                                    <spring:message code="jpm.sysparam.health.errors" text="errors" />
                                </span>
                                <span class="badge bg-warning text-dark fs-6">${report.warningCount}
                                    <spring:message code="jpm.sysparam.health.warnings" text="warnings" />
                                </span>
                            </c:otherwise>
                        </c:choose>
                        <span class="badge bg-secondary fs-6">${report.infoCount}
                            <spring:message code="jpm.sysparam.health.infos" text="info" />
                        </span>
                        <span class="ms-auto text-muted small">
                            <spring:message code="jpm.sysparam.health.defs" text="Definitions" />: ${report.totalDefs}
                            &nbsp;|&nbsp;
                            <spring:message code="jpm.sysparam.health.rows" text="Stored" />: ${report.totalRows}
                            &nbsp;|&nbsp;
                            <spring:message code="jpm.sysparam.health.cipher" text="Cipher" />:
                            <c:choose>
                                <c:when test="${report.cipherEnabled}">
                                    <span class="text-success"><i class="fas fa-lock"></i></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-danger"><i class="fas fa-lock-open"></i></span>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12">
                    <c:choose>
                        <c:when test="${empty report.items}">
                            <div class="alert alert-success mb-0">
                                <i class="fas fa-check-circle"></i>
                                <spring:message code="jpm.sysparam.health.allGood" text="No issues found." />
                            </div>
                        </c:when>
                        <c:otherwise>
                            <table class="table table-sm table-bordered w-100 align-middle">
                                <thead>
                                    <tr>
                                        <th style="width: 110px;"><spring:message code="jpm.sysparam.health.col.severity" text="Severity" /></th>
                                        <th style="width: 220px;"><spring:message code="jpm.sysparam.health.col.category" text="Finding" /></th>
                                        <th style="width: 280px;"><spring:message code="jpm.sysparam.health.col.key" text="Key" /></th>
                                        <th><spring:message code="jpm.sysparam.health.col.detail" text="Detail" /></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${report.items}">
                                        <tr>
                                            <td>
                                                <span class="badge ${item.badgeClass}">
                                                    <spring:message code="jpm.sysparam.health.severity.${item.severity}" text="${item.severity}" />
                                                </span>
                                            </td>
                                            <td><spring:message code="${item.categoryKey}" text="${item.category}" /></td>
                                            <td><code>${item.key}</code></td>
                                            <td>${item.detail}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
    <script type="text/javascript">
        jpmLoad(function () {
        });
    </script>
</html>
