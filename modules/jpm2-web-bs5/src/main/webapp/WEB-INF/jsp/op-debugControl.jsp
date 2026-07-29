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
                    <span class="me-2"><spring:message code="jpm.debug.control.globalLevel" text="Global level" />:</span>
                    <span class="badge ${debugLevel > 0 ? 'bg-success' : 'bg-secondary'} fs-6">${debugLevel}</span>
                    <c:if test="${not empty debugChannels}">
                        <table class="table table-sm table-bordered mt-3" style="max-width:480px;">
                            <thead><tr>
                                <th><spring:message code="jpm.debug.control.channel" text="Channel" /></th>
                                <th style="width:80px;"><spring:message code="jpm.debug.control.level" text="Level" /></th>
                            </tr></thead>
                            <tbody>
                                <c:forEach var="e" items="${debugChannels}">
                                    <tr><td>${fn:escapeXml(e.key)}</td><td class="text-center">${e.value}</td></tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-8 col-md-10 col-sm-12">
                    <form class="form-horizontal" role="form" method="POST" id="jpmForm" action="${cp}jpm/syslog/debugControl.exec">
                        <input type="hidden" name="action" id="dbgAction" value="set" />
                        <div class="form-group row mb-3">
                            <label class="col-md-3 control-label"><spring:message code="jpm.debug.control.channel" text="Channel" /></label>
                            <div class="col-md-9">
                                <input type="text" name="channel" class="form-control" placeholder="<spring:message code="jpm.debug.control.channelHint" text="empty = global" />" />
                            </div>
                        </div>
                        <div class="form-group row mb-3">
                            <label class="col-md-3 control-label"><spring:message code="jpm.debug.control.level" text="Level" /></label>
                            <div class="col-md-9">
                                <select name="level" class="form-select">
                                    <c:forEach var="n" begin="0" end="${debugMaxLevel}">
                                        <option value="${n}" ${debugLevel == n ? 'selected="selected"' : ''}>${n}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="form-group row mb-3">
                            <label class="col-md-3 control-label"><spring:message code="jpm.debug.control.ttl" text="TTL (s)" /></label>
                            <div class="col-md-9">
                                <input type="number" name="ttl" min="0" step="1" class="form-control" placeholder="<spring:message code="jpm.debug.control.ttlHint" text="optional; auto-off after N seconds" />" />
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="col-md-9 offset-md-3">
                                <button type="submit" class="btn btn-primary" onclick="document.getElementById('dbgAction').value='set';">
                                    <i class="fas fa-bug"></i> <spring:message code="jpm.button.apply" text="Apply" />
                                </button>
                                <button type="submit" class="btn btn-outline-secondary ms-2" onclick="document.getElementById('dbgAction').value='reset';">
                                    <i class="fas fa-power-off"></i> <spring:message code="jpm.debug.control.reset" text="Reset (all off)" />
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </jpm:jpm-item-operation>
    </jpm:jpm-body>
    <script type="text/javascript">
        jpmLoad(function () {
            buildAjaxJpmForm();
        });
    </script>
</html>
