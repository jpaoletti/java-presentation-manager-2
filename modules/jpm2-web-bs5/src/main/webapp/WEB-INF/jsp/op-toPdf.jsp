<!DOCTYPE html>
<%@include file="inc/default-taglibs.jsp" %>
<html lang="${locale.language}">
    <head>
        <%@include file="inc/default-head.jsp" %>
    </head>
    <c:set var="entityName" value="${entity.title}" />
    <spring:message var="operationName" code="${operation.title}" arguments="${entityName}" />
    <jpm:jpm-body>
        <%@include file="inc/default-content-header.jsp" %>
        <%@include file="inc/default-breadcrumb.jsp" %>
        <div class="container-fluid" id="container-${fn:replace(contextualEntity,'!', '-')}-${operation.id}">
            <div class="row"><br/>
                <div class="col-lg-4">
                    <div class="card">
                        <div class="card-header">
                            <span>${operationName}</span>
                        </div>
                        <div class="card-body">
                            <div class="mb-3">
                                <label class="form-label"><b><spring:message code="jpm.toPdf.columns"/></b></label>
                                <ul id="pdfColumns" class="list-group">
                                    <c:forEach var="f" items="${availableFields}">
                                        <li class="list-group-item d-flex align-items-center pdf-col" draggable="true" data-id="${f.id}">
                                            <span class="me-2 text-muted" style="cursor:move" title="Arrastrar">&#9776;</span>
                                            <div class="form-check mb-0">
                                                <input class="form-check-input pdfField" type="checkbox" value="${f.id}" id="pdfcol-${f.id}" checked>
                                                <label class="form-check-label" for="pdfcol-${f.id}">${f.title}</label>
                                            </div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                            <div class="mb-3">
                                <label class="form-label"><b><spring:message code="jpm.toPdf.orientation"/></b></label>
                                <div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="pdfOrientation" id="pdfPortrait" value="portrait" ${orientation ne 'landscape' ? 'checked' : ''}>
                                        <label class="form-check-label" for="pdfPortrait"><spring:message code="jpm.toPdf.portrait"/></label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="pdfOrientation" id="pdfLandscape" value="landscape" ${orientation eq 'landscape' ? 'checked' : ''}>
                                        <label class="form-check-label" for="pdfLandscape"><spring:message code="jpm.toPdf.landscape"/></label>
                                    </div>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label" for="pdfSize"><b><spring:message code="jpm.toPdf.size"/></b></label>
                                <select class="form-select" id="pdfSize">
                                    <option value="A4" ${size eq 'A4' ? 'selected' : ''}>A4</option>
                                    <option value="A3" ${size eq 'A3' ? 'selected' : ''}>A3</option>
                                    <option value="A5" ${size eq 'A5' ? 'selected' : ''}>A5</option>
                                    <option value="Letter" ${size eq 'Letter' ? 'selected' : ''}>Letter</option>
                                    <option value="Legal" ${size eq 'Legal' ? 'selected' : ''}>Legal</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label" for="pdfTitle"><b><spring:message code="jpm.toPdf.title"/></b></label>
                                <input type="text" class="form-control" id="pdfTitle" value="${title}">
                            </div>
                            <div class="d-flex gap-2">
                                <button type="button" class="btn btn-primary" id="pdfRefresh"><i class="fas fa-sync"></i> <spring:message code="jpm.toPdf.refresh"/></button>
                                <button type="button" class="btn btn-danger" id="pdfDownload"><i class="fas fa-file-pdf"></i> <spring:message code="jpm.toPdf.download"/></button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-8">
                    <div class="card">
                        <div class="card-body p-0">
                            <iframe id="pdfPreview" title="PDF" style="width:100%;height:82vh;border:0"></iframe>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script>
            jpmLoad(function () {
                var base = "${cp}${pdfBaseUrl}";

                function buildUrl(download) {
                    var cols = [];
                    document.querySelectorAll("#pdfColumns input.pdfField:checked").forEach(function (cb) {
                        cols.push(cb.value);
                    });
                    var orientation = document.querySelector("input[name='pdfOrientation']:checked").value;
                    var size = document.getElementById("pdfSize").value;
                    var title = document.getElementById("pdfTitle").value;
                    var u = base + "?fields=" + encodeURIComponent(cols.join(","))
                            + "&orientation=" + orientation
                            + "&size=" + size
                            + "&title=" + encodeURIComponent(title);
                    if (download) {
                        u += "&download=true";
                    }
                    return u;
                }

                function refresh() {
                    document.getElementById("pdfPreview").src = buildUrl(false) + "&_=" + new Date().getTime();
                }

                document.getElementById("pdfRefresh").addEventListener("click", refresh);
                document.getElementById("pdfDownload").addEventListener("click", function () {
                    window.open(buildUrl(true), "_blank");
                });

                // Native HTML5 drag & drop to reorder columns.
                var dragged = null;
                var listEl = document.getElementById("pdfColumns");
                listEl.querySelectorAll("li.pdf-col").forEach(function (li) {
                    li.addEventListener("dragstart", function () {
                        dragged = li;
                        li.classList.add("opacity-50");
                    });
                    li.addEventListener("dragend", function () {
                        li.classList.remove("opacity-50");
                    });
                    li.addEventListener("dragover", function (e) {
                        e.preventDefault();
                    });
                    li.addEventListener("drop", function (e) {
                        e.preventDefault();
                        if (dragged && dragged !== li) {
                            var rect = li.getBoundingClientRect();
                            var after = (e.clientY - rect.top) > (rect.height / 2);
                            listEl.insertBefore(dragged, after ? li.nextSibling : li);
                        }
                    });
                });

                refresh();
            });
        </script>
    </jpm:jpm-body>
</html>
