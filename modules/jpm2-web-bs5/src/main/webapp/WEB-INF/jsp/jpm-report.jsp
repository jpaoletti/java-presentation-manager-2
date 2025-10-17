<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
        <script type='text/javascript' src="${cp}static/js/jpm-report.js?v=${jpm.appversion}" ></script>
        <script type="text/javascript" src="${cp}static/js/select2.min.js?v=${jpm.appversion}"></script>
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <jpm:jpm-body showMenu="false">
        <h2><spring:message code="${report.title}" text="Generic Report" /></h2>
        <c:if test="${not empty userSave}"><h4><a class="btn btn-danger confirm-true" id="btnDeleteCurrent" href="${cp}jpm/report/${reportId}/${savedReportId}/delete" title="Delete"><span class="fa fa-trash-alt"></span></a> ${userSave.name}</h4></c:if>
            <div class="row">
                <div class="offcanvas offcanvas-start"  tabindex="-1" id="offcanvasControls" aria-labelledby="offcanvasControlsLabel">
                    <div class="offcanvas-header">
                        <h5 class="offcanvas-title" id="offcanvasExampleLabel"><spring:message code="jpm.reports.filter.controls" text="Controls" /></h5>
                    <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                </div>
                <div class="offcanvas-body">
                    <div>
                        <div class="input-group">
                            <span style="width: 150px;" class="input-group-text"><spring:message code="jpm.reports.filter.from" text="First" /></span>
                            <input type="number" step="1" min="0" id="from" name="from" class="form-control" value="${(not empty savedReport)?savedReport.from:'0'}" style="text-align: right">
                        </div>
                        <div class="input-group">
                            <span style="width: 150px;" class="input-group-text"><spring:message code="jpm.reports.filter.count" text="Count" /></span>
                            <input type="number" step="10" min="50" id="count" name="from" class="form-control" value="${(not empty savedReport)?savedReport.max:'100'}" style="text-align: right">
                        </div>
                    </div>
                    <hr class="hr-sm"/>
                    <div class="btn-group">
                        <select id="sortDirection" style="padding:10px;">
                            <option value="ASC" ${(not empty savedReport and savedReport.sortDirection=='ASC')?'selected':''}><spring:message code="jpm.reports.sort.title" text="Sorting" /> &DownArrow; </option>
                            <option value="DESC" ${(not empty savedReport and savedReport.sortDirection=='DESC')?'selected':''}><spring:message code="jpm.reports.sort.title" text="Sorting" /> &UpArrow; </option>
                        </select>
                        <select id="sortField" style="padding:10px;">
                            <c:forEach items="${report.sortableFieldList}" var="fs" varStatus="st">
                                <option value="${fs}" ${(not empty savedReport and savedReport.sortField==fs)?'selected':''}><jpm:field-title entity="${report.entity}" fieldId="${fs}"  /></option>
                            </c:forEach>
                        </select>
                    </div>
                    <hr class="hr-sm"/>

                    <div class="dropdown">
                        <button class="btn btn-primary btn-sm dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown">
                            <spring:message code="jpm.reports.filter.btn" text="Add Filter" /> <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                            <c:forEach items="${fieldSearchs}" var="fs" varStatus="st">
                                <li>
                                    <a id="search-link-${st.index+1}" class="dropdown-item" data-field='${fs.key.id}' href="javascript:addSearch('${fs.key.id}');">${st.index+1}. <jpm:field-title entity="${report.entity}" fieldId="${fs.key.id}" /></a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>

                    <div id="filterZone">
                        <c:if test="${not empty savedReport}"><c:forEach var="f" items="${savedReport.filters}"></c:forEach></c:if>
                            </div>


                            <hr class="hr-sm"/>


                            <div class="dropdown">
                                <button class="btn btn-primary btn-sm dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown">
                            <spring:message code="jpm.reports.group.btn" text="Add Group" /> <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                            <li><a  class="dropdown-item" data-field='${fs}' href="javascript:addGroup('', '<spring:message code="jpm.reports.group.all" text="All" />');"><spring:message code="jpm.reports.group.all" text="All" /></a></li>
                            <li class="divider"></li>
                                <c:forEach items="${report.groupableFieldList}" var="fs" varStatus="st">
                                <li ><a  class="dropdown-item" data-field='${fs}' href="javascript:addGroup('${fs}', '<jpm:field-title entity="${report.entity}" fieldId="${fs}"  />');"><jpm:field-title entity="${report.entity}" fieldId="${fs}"/></a></li>
                                </c:forEach>
                        </ul>
                    </div>
                    <div id="groupsZone">
                        <ul>
                            <c:if test="${not empty savedReport}">
                                <c:forEach var="f" items="${savedReport.groups}">
                                    <li class='group' data-field='${f}'><jpm:field-title entity="${report.entity}" fieldId="${f}"  /> <span class='closer fa fa-times'></span></li>
                                    </c:forEach>
                                </c:if>
                        </ul>
                    </div>
                    <hr class="hr-sm"/>


                    <c:if test="${not empty report.numericFieldList}">
                        <div class="btn-group" id='select-formula'>
                            <button id="search-dropdown" type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown">
                                <spring:message code="jpm.reports.formula.btn" text="Add Formula" /> <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" role="menu">
                                <c:forEach items="${report.numericFieldList}" var="fs" varStatus="st">
                                    <li role="presentation">
                                        <a role="menuitem" tabindex="-1" data-field='${fs}' href="javascript:addFormula('${fs}', '<jpm:field-title entity="${report.entity}" fieldId="${fs}"/>', 'formulaZone', 'general-formula');"><jpm:field-title entity="${report.entity}" fieldId="${fs}" /></a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                        <div id="formulaZone">
                            <c:if test="${not empty savedReport}">
                                <c:forEach var="f" items="${savedReport.formulas}">
                                    <div class='general-formula' data-field='${f.field}'><select><option value='SUM' ${f.formula=='SUM'?'selected':''}>SUM</option><option value='AVG' ${f.formula=='AVG'?'selected':''}>AVG</option><option value='MAX' ${f.formula=='MAX'?'selected':''}>MAX</option><option value='MIN' ${f.formula=='MIN'?'selected':''}>MIN</option></select> (<jpm:field-title entity="${report.entity}" fieldId="${f.field}"/>) <span class='closer fa fa-times'></span></div>
                                            </c:forEach>
                                        </c:if>
                        </div>
                        <hr class="hr-sm"/>
                    </c:if>
                    <c:if test="${not empty report.descriptiveFieldList}">
                        <div id='select-descriptive-fields'>
                            <h4><spring:message code="jpm.reports.filter.columns" text="Columns" /></h4>
                            <c:forEach items="${report.descriptiveFieldList}" var="fs" varStatus="st">
                                <label><input type="checkbox" class="visibleField" value="${fs}" ${(not empty savedReport)?((savedReport.visibleFields.contains(fs))?'checked':''):'checked'}/> <jpm:field-title entity="${report.entity}" fieldId="${fs}"/></label><br/>
                                </c:forEach>
                        </div>
                        <div id="descriptiveFieldsZone"><ul></ul></div>
                            </c:if>
                    <!--
                    <hr class="hr-sm"/>
                                    <div class="btn-group">
                                        <button id="search-dropdown" type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown">
                    <spring:message code="jpm.reports.graphics.btn" text="Add Graphic" /> <span class="caret"></span>
                </button>
                <ul class="dropdown-menu" role="menu">
                    <c:forEach items="${report.groupableFieldList}" var="fs" varStatus="st">
                        <li role="presentation">
                            <a role="menuitem" tabindex="-1" data-field='${fs}' href="javascript:addGraphic('${fs}', '<jpm:field-title entity="${report.entity}" fieldId="${fs}" />', numericFields);"><jpm:field-title entity="${report.entity}" fieldId="${fs}" /></a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
            <div id="graphicZone"><ul></ul></div>-->
                </div>
            </div>
            <div class="col-lg-9 mainzone">
                <div class="btn-group">
                    <button class="btn btn-primary" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasControls" aria-controls="offcanvasControls"><span class="fa fa-bars"></span></button>
                    <button class="btn btn-secondary" id="btnHtml">HTML</button>
                    <button class="btn btn-success" id="btnExcel">EXCEL</button>

                    <!--<button class="btn btn-danger"  id="btnPdf">PDF</button>-->
                    
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" id="dropdownMenuButton" data-bs-toggle="dropdown">
                        <spring:message code="jpm.reports.savedReport.btn" text="Save Report" /> <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                        <li><a  class="dropdown-item" href="javascript: void(0)" id="saveReportBtn" data-toggle="modal" data-target="#saveReportModal"><spring:message code="jpm.reports.saveReport.btn" text="Save Report" /></a></li>
                        <li class="divider"></li>

                        <c:forEach items="${savedReports}" var="fs" varStatus="st">
                            <li>
                                <a class="dropdown-item" data-report='${fs.id}' href="${cp}jpm/report/${reportId}?savedReportId=${fs.id}" class="${fs.id == savedReportId?'selected-saved-report':''}">${fs.name}</span></a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>

                </div>

                <div id="htmlReportZone">&nbsp;</div>
                <div id="reportGraphicZone">&nbsp;</div>
            </div>
        </div>
        <!-- SEARCHERS-->
        <div class="hide" id='fieldSearchForms'>
            <c:forEach items="${fieldSearchs}" var="fs">
                <div id='fieldSearchForm_${fs.key.id}'>
                    <jpm:field-title entity="${report.entity}" fieldId="${fs.key.id}" /><br/>
                    <c:if test="${fn:startsWith(fs.value, '@page:')}">
                        <jsp:include page="searcher/${fn:replace(fs.value, '@page:', '')}" flush="true" />
                    </c:if>
                    <c:if test="${not fn:startsWith(fs.value, '@page:')}">
                        ${fs.value}
                    </c:if>
                </div>
            </c:forEach>
        </div>
        <!-- /SEARCHERS-->
        <div class="modal fade" id="saveReportModal" tabindex="-1" role="dialog" aria-labelledby="saveReportModalLabel">
            <form method="POST" action="${cp}jpm/report/${reportId}/save">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="saveReportModalLabel"><spring:message code="jpm.reports.saveReport.title" text="New Report" /></h4>
                        </div>
                        <div class="modal-body">
                            <div class="form-group" id="note-group">
                                <label for="note"><spring:message code="jpm.reports.saveReport.nameLabel" text="New Report Name" /></label>
                                <input type="text" class="form-control" name="name" id="newReportName" required="">
                            </div>
                            <input type="hidden" value="" name="content" id="newReportContent" />
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal"><spring:message code="jpm.reports.saveReport.closeBtn" text="Close" /></button>
                            <button type="submit" class="btn btn-primary"><spring:message code="jpm.reports.saveReport.saveBtn" text="Save" /></button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </jpm:jpm-body>
    <c:if test="${not empty savedReport}">
        <script type="text/javascript">
            <c:forEach var="f" items="${savedReport.filters}">
            var s = addSearch('${f.field}');
                <c:forEach var="p" items="${f.parameters}">
            s.find("[name='${p.name}']").val("${p.value}");
                </c:forEach>
            </c:forEach>
        </script>
    </c:if>
    <script type="text/javascript">
        var numericFields = [];
        <c:forEach items="${report.numericFieldList}" var="fs" varStatus="st">numericFields["${fs}"] = "<jpm:field-title entity="${report.entity}" fieldId="${fs}" />";</c:forEach>
            jpmLoad(function () {
                $("#sortDirection").select2();
                $("#sortField").select2();
                $(".navbar-toggler").on("click", function () {
                    if ($(".offcanvas").hasClass("d-none")) {
                        $(".offcanvas").removeClass("d-none");
                        $(".mainzone").removeClass("col-lg-12").addClass("col-lg-9");
                    } else {
                        $(".offcanvas").addClass("d-none");
                        $(".mainzone").removeClass("col-lg-9").addClass("col-lg-12");
                    }
                    ;
                });
                $(document).on("keypress", function (e) {
                    if ($(e.target).closest("input")[0]) {
                        return;
                    }
                    if (e.which === parseInt("<spring:message code='jpm.list.shortcut.search' text='102' />")) { //search 'f'
                        $("#search-dropdown").dropdown('toggle').trigger('focus');
                    }
                });
                $("#search-dropdown").on("keypress", function (e) {
                    if (e.which > 48 && e.which <= 57) { // 1 - 9
                        openSearchModal($("#search-link-" + (e.which - 48)).attr("data-field"));
                    }
                });
                $("#btnHtml").on("click", function () {
                    $("#htmlReportZone").html("<img src='${cp}static/img/loading.gif' />");
                    var data = buildReportData();
                    console.log(data);
                    $.post("${cp}jpm/report/${reportId}/html", {
                        reportData: JSON.stringify(data)
                    }, function (result) {
                        $("#htmlReportZone").html(result);
                    });
                });
                $("#btnExcel").on("click", function () {
                    var data = buildReportData();
                    $("#htmlReportZone").html("<img src='${cp}static/img/loading.gif' />");

                    var ifr = document.createElement("iframe");
                    ifr.src = "${cp}jpm/report/${reportId}/xls?reportData=" + encodeURI(JSON.stringify(data));
                    document.body.appendChild(ifr);
                    var inter = window.setInterval(function () {
                        if (ifr.contentWindow.document.readyState === "complete") {
                            window.clearInterval(inter);
                            $("#htmlReportZone").html("");
                        }
                    }, 1000);
                });
                $(document).on("click", ".closer", function () {
                    $(this).parent().remove();
                });
                $("#saveReportBtn").on("click", function () {
                    $("#newReportContent").val(JSON.stringify(buildReportData()));
                    $("#saveReportModal").modal("show");
                });
            });
    </script>
</html>