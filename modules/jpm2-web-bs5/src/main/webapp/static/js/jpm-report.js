var graphicId = 1;

function buildReportData() {
    var data = {
        max: 100,
        from: 0,
        sortField: null,
        sortDirection: "ASC",
        filters: [],
        formulas: [],
        groups: [],
        graphics: [],
        visibleFields: []
    };
    $(".visibleField:checked").each(function () {
        data.visibleFields.push($(this).val());
    });
    $(".filter").each(function () {
        var params = [];
        $(this).find(".form-control").each(function () {
            params.push({
                name: $(this).attr("name"),
                value: $(this).val()
            });
        });
        $(this).find(".form-select").each(function () {
            params.push({
                name: $(this).attr("name"),
                value: $(this).val()
            });
        });
        data.filters.push({
            field: $(this).data("field"),
            parameters: params
        });
    });
    $(".group").each(function () {
        data.groups.push($(this).data("field"));
    });
    $(".general-formula").each(function () {
        data.formulas.push({
            field: $(this).data("field"),
            formula: $(this).find("select").val()
        });
    });
    $(".graphic-config").each(function () {
        var formulas = [];
        $(".graphic-formula").each(function () {
            formulas.push({
                field: $(this).data("field"),
                formula: $(this).find("select").val()
            });
        });
        data.graphics.push({
            title: $(this).data("title"),
            type: "Pie",
            groupField: $(this).data("field"),
            formulas: formulas
        });
    });
    data.max = $("#count").val();
    data.from = $("#from").val();
    data.sortField = $("#sortField").val();
    data.sortDirection = $("#sortDirection").val();
    return data;
}

function addSearch(id) {
    var newSearch = $("<div class='filter' data-field='" + id + "'> <span class='closer fa fa-times'></span> " + $("#fieldSearchForm_" + id).html() + "</div>");
    $("#filterZone").append(newSearch);
    return newSearch;
}

function addGroup(id, text) {
    if ($(".group[data-field='" + id + "']").length === 0) {
        $("#groupsZone > ul").append("<li class='group' data-field='" + id + "'>" + text + " <span class='closer fa fa-times'></span></li>");
    }
}

function addFormula(id, text, dropzoneID, clazz) {
    $("#" + dropzoneID).append("<div class='" + clazz + "' data-field='" + id + "'><select><option value='SUM'>SUM</option><option value='AVG'>AVG</option><option value='MAX'>MAX</option><option value='MIN'>MIN</option></select> (" + text.trim() + ") <span class='closer fa fa-times'></span></div>");
}

function addGraphic(groupField, groupFieldName, numericFields) {
    var graphicConfigId = "graphic-config-" + (graphicId++);
    var html = "";
    html += "<div class='graphic-config' data-field='" + groupField + "' data-title='" + groupFieldName + "'>";
    html += graphicId + ". " + groupFieldName + "<br/>";
    html += '<div id="' + graphicConfigId + '"></div>';
    var selectFormula = $("#select-formula").clone();
    selectFormula.find("#search-dropdown").removeClass("btn-primary").addClass("btn-secondary");
    selectFormula.find("a").each(function () {
        var hr = $(this).attr("href");
        $(this).prop("href", hr.replace("formulaZone", graphicConfigId).replace("general-formula", "graphic-formula"));
    });
    /*for (var m in numericFields) {
     
     console.log(numericFields[m]);
     html += "<div data-field='" + m + "' class='graphic-config-formula'><select><option value='SUM'>SUM</option><option value='AVG'>AVG</option><option value='MAX'>MAX</option><option value='MIN'>MIN</option></select> (" + numericFields[m] + ") <span class='closer fa fa-times'></span></div>";
     }*/

    html += "</div>";

    $("#graphicZone").append(html).append(selectFormula);
}
