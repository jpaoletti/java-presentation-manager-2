<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <jpm:jpm-body>
        <jpm:jpm-item-operation>
            <!-- Información General del Thread Runner -->
            <h4><spring:message code="jpm.threadRunner.status.generalInfo" text="Informaci&oacute;n General" /></h4>
            <table class="table table-compact table-bordered">
                <tbody>
                    <tr>
                        <th style="width: 30%;"><spring:message code="jpm.threadRunner.status.name" text="Nombre" /></th>
                        <td>${runnerName}</td>
                    </tr>
                    <tr>
                        <th><spring:message code="jpm.threadRunner.status.description" text="Descripci&oacute;n" /></th>
                        <td>${runnerDescription}</td>
                    </tr>
                    <tr>
                        <th><spring:message code="jpm.threadRunner.status.class" text="Clase" /></th>
                        <td><code>${runnerClass}</code></td>
                    </tr>
                    <tr>
                        <th><spring:message code="jpm.threadRunner.status.enabled" text="Habilitado" /></th>
                        <td>
                            <c:choose>
                                <c:when test="${runnerEnabled}">
                                    <span class="label label-success"><spring:message code="jpm.threadRunner.status.yes" text="S&iacute;" /></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="label label-danger"><spring:message code="jpm.threadRunner.status.no" text="No" /></span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </tbody>
            </table>

            <!-- Estado del Thread -->
            <h4><spring:message code="jpm.threadRunner.status.threadState" text="Estado del Thread" /></h4>
            <table class="table table-compact table-bordered">
                <tbody>
                    <tr>
                        <th style="width: 30%;"><spring:message code="jpm.threadRunner.status.exists" text="Thread Creado" /></th>
                        <td>
                            <c:choose>
                                <c:when test="${threadStatus.exists}">
                                    <span class="label label-success"><spring:message code="jpm.threadRunner.status.yes" text="S&iacute;" /></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="label label-warning"><spring:message code="jpm.threadRunner.status.no" text="No" /></span>
                                    <br/><small><spring:message code="jpm.threadRunner.status.notCreated" text="El thread no ha sido creado a&uacute;n" /></small>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <c:if test="${threadStatus.exists}">
                        <tr>
                            <th><spring:message code="jpm.threadRunner.status.threadName" text="Nombre del Thread" /></th>
                            <td><code>${threadStatus.name}</code></td>
                        </tr>
                        <tr>
                            <th><spring:message code="jpm.threadRunner.status.state" text="Estado" /></th>
                            <td>
                                <c:choose>
                                    <c:when test="${threadStatus.state == 'RUNNABLE'}">
                                        <span class="label label-success">${threadStatus.state}</span>
                                    </c:when>
                                    <c:when test="${threadStatus.state == 'WAITING' || threadStatus.state == 'TIMED_WAITING'}">
                                        <span class="label label-info">${threadStatus.state}</span>
                                    </c:when>
                                    <c:when test="${threadStatus.state == 'BLOCKED'}">
                                        <span class="label label-warning">${threadStatus.state}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-default">${threadStatus.state}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th><spring:message code="jpm.threadRunner.status.alive" text="Activo" /></th>
                            <td>
                                <c:choose>
                                    <c:when test="${threadStatus.alive}">
                                        <span class="label label-success"><spring:message code="jpm.threadRunner.status.yes" text="S&iacute;" /></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-danger"><spring:message code="jpm.threadRunner.status.no" text="No" /></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th><spring:message code="jpm.threadRunner.status.daemon" text="Daemon" /></th>
                            <td>
                                <c:choose>
                                    <c:when test="${threadStatus.daemon}">
                                        <span class="label label-info"><spring:message code="jpm.threadRunner.status.yes" text="S&iacute;" /></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-default"><spring:message code="jpm.threadRunner.status.no" text="No" /></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th><spring:message code="jpm.threadRunner.status.interrupted" text="Interrumpido" /></th>
                            <td>
                                <c:choose>
                                    <c:when test="${threadStatus.interrupted}">
                                        <span class="label label-warning"><spring:message code="jpm.threadRunner.status.yes" text="S&iacute;" /></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-success"><spring:message code="jpm.threadRunner.status.no" text="No" /></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th><spring:message code="jpm.threadRunner.status.doWork" text="Trabajando (do-work)" /></th>
                            <td>
                                <c:choose>
                                    <c:when test="${threadStatus.doWork}">
                                        <span class="label label-primary"><spring:message code="jpm.threadRunner.status.yes" text="S&iacute;" /></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-default"><spring:message code="jpm.threadRunner.status.no" text="No" /></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <!-- Parámetros Comparativos (BD vs Ejecución) -->
            <h4><spring:message code="jpm.threadRunner.status.parametersComparison" text="Par&aacute;metros - Comparaci&oacute;n BD vs Ejecuci&oacute;n" /></h4>
            <c:set var="hasConfiguredParams" value="${not empty configuredParameters}" />
            <c:set var="hasRuntimeParams" value="${threadStatus.exists and not empty threadStatus.parameters}" />

            <c:choose>
                <c:when test="${hasConfiguredParams or hasRuntimeParams}">
                    <table class="table table-compact table-bordered">
                        <thead>
                            <tr>
                                <th style="width: 25%;"><spring:message code="jpm.threadRunner.status.parameterName" text="Par&aacute;metro" /></th>
                                <th style="width: 37.5%;"><spring:message code="jpm.threadRunner.status.parameterValueDB" text="Valor en BD" /></th>
                                <th style="width: 37.5%;"><spring:message code="jpm.threadRunner.status.parameterValueRuntime" text="Valor en Ejecuci&oacute;n" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Primero: parámetros que están en BD (configurados) -->
                            <c:forEach var="paramEntry" items="${configuredParameters}">
                                <c:set var="paramKey" value="${paramEntry.key}" />
                                <c:set var="paramValue" value="${paramEntry.value}" />
                                <tr>
                                    <td><strong><c:out value="${paramKey}" /></strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty paramValue}">
                                                <c:out value="${paramValue}" />
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted"><em><spring:message code="jpm.threadRunner.status.emptyValue" text="(vac&iacute;o)" /></em></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${hasRuntimeParams}">
                                                <c:set var="runtimeValue" value="${threadStatus.parameters[paramKey]}" />
                                                <c:choose>
                                                    <c:when test="${not empty runtimeValue}">
                                                        <c:choose>
                                                            <c:when test="${runtimeValue eq paramValue}">
                                                                <span class="text-success"><c:out value="${runtimeValue}" /></span>
                                                                <span class="label label-success" style="margin-left: 5px;">OK</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-warning"><c:out value="${runtimeValue}" /></span>
                                                                <span class="label label-warning" style="margin-left: 5px;"><spring:message code="jpm.threadRunner.status.different" text="Diferente" /></span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted"><em><spring:message code="jpm.threadRunner.status.notInRuntime" text="(no presente)" /></em></span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted"><em><spring:message code="jpm.threadRunner.status.threadNotRunning" text="(thread no iniciado)" /></em></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>

                            <!-- Segundo: parámetros que solo están en runtime (no en BD) -->
                            <c:if test="${hasRuntimeParams}">
                                <c:forEach var="runtimeEntry" items="${threadStatus.parameters}">
                                    <c:set var="runtimeKey" value="${runtimeEntry.key}" />
                                    <c:set var="runtimeVal" value="${runtimeEntry.value}" />
                                    <c:if test="${empty configuredParameters[runtimeKey]}">
                                        <tr class="warning">
                                            <td><strong><c:out value="${runtimeKey}" /></strong></td>
                                            <td>
                                                <span class="text-muted"><em><spring:message code="jpm.threadRunner.status.notConfigured" text="(no configurado en BD)" /></em></span>
                                            </td>
                                            <td>
                                                <c:out value="${runtimeVal}" />
                                                <span class="label label-info" style="margin-left: 5px;"><spring:message code="jpm.threadRunner.status.runtimeOnly" text="Solo en runtime" /></span>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">
                        <spring:message code="jpm.threadRunner.status.noParameters" text="No hay par&aacute;metros configurados ni en ejecuci&oacute;n" />
                    </div>
                </c:otherwise>
            </c:choose>

        </jpm:jpm-item-operation>
    </jpm:jpm-body>
</html>
