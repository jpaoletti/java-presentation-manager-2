package jpaoletti.jpm2.web.controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.message.MessageFactory;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.reports.EntityReport;
import jpaoletti.jpm2.core.model.reports.EntityReportData;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.reports.EntityReportResult;
import jpaoletti.jpm2.core.model.reports.EntityReportUserSave;
import jpaoletti.jpm2.core.service.ReportService;
import jpaoletti.jpm2.util.XlsUtils;
import static jpaoletti.jpm2.util.XlsUtils.MIME_XLS;
import static jpaoletti.jpm2.util.XlsUtils.xlsAmountStyle;
import static jpaoletti.jpm2.util.XlsUtils.xlsCellWithStyle;
import static jpaoletti.jpm2.util.XlsUtils.xlsDateStyle;
import static jpaoletti.jpm2.util.XlsUtils.xlsNewPage;
import static jpaoletti.jpm2.util.XlsUtils.xlsStrechColumns;
import static jpaoletti.jpm2.util.XlsUtils.xlsTobytes;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class JPMReportController extends BaseController {

    @Autowired
    private ReportService reportService;

    @RequestMapping(value = "/jpm/report/{reportId}/save", method = RequestMethod.POST)
    public String saveReport(@PathVariable String reportId, @RequestParam String name, @RequestParam String content) throws PMException {
        final EntityReport report = getJpm().getReport(reportId);
        final EntityReportUserSave saveReport = reportService.saveReport(report, reportId, name, getUserDetails().getUsername(), content);
        return "redirect:/jpm/report/" + reportId + "?savedReportId=" + saveReport.getId();
    }

    @RequestMapping(value = "/jpm/report/{reportId}", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable String reportId, @RequestParam(required = false) Long savedReportId) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-report");
        final EntityReport report = getJpm().getReport(reportId);
        if (report == null) {
            throw new PMException(MessageFactory.error("jpm.reports.report.not.found", reportId));
        }
        if (savedReportId != null) {
            final EntityReportUserSave userSave = reportService.getUserSave(savedReportId, getUserDetails().getUsername());
            if (userSave != null) {
                final Gson gson = new Gson();
                mav.addObject("userSave", userSave);
                mav.addObject("savedReport", gson.fromJson(userSave.getContent(), EntityReportData.class));
                mav.addObject("savedReportId", savedReportId);
            }

        }
        mav.addObject("reportId", reportId);
        mav.addObject("report", report);
        mav.addObject("savedReports", reportService.getUserSaves(getUserDetails().getUsername()));

        final Map<Field, String> fieldSearchs = new LinkedHashMap<>();
        for (String filter : report.getFilteringFieldList()) {
            final Field field = report.getEntity().getFieldById(filter, null);
            if (field == null) {
                throw new PMException(MessageFactory.error("jpm.reports.field.not.found", reportId));
            }
            if (field.getSearcher() != null) {
                fieldSearchs.put(field, field.getSearcher().visualization(report.getEntity(), field));
            }
        }
        mav.addObject("fieldSearchs", fieldSearchs);
        return mav;
    }

    @RequestMapping(value = "/jpm/report/{reportId}/html", method = RequestMethod.POST)
    public ModelAndView reportHtml(@PathVariable String reportId, @RequestParam String reportData) throws PMException {
        final ModelAndView mav = new ModelAndView("jpm-report.html");
        final EntityReport report = getJpm().getReport(reportId);
        if (report == null) {
            throw new PMException(MessageFactory.error("report.not.found", reportId));
        }
        mav.addObject("reportId", reportId);
        mav.addObject("report", report);
        final EntityReportData data = new Gson().fromJson(reportData, EntityReportData.class);
        mav.addObject("reportData", data);
        mav.addObject("result", reportService.getResult(report, data, true));
        return mav;
    }

    @RequestMapping(value = {"/jpm/report/{reportId}/xls"})
    public void reportXls(@PathVariable String reportId, @RequestParam String reportData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final EntityReport report = getJpm().getReport(reportId);
        if (report == null) {
            throw new PMException(MessageFactory.error("report.not.found", reportId));
        }
        final EntityReportData data = new Gson().fromJson(reportData, EntityReportData.class);
        final Workbook wb = reportService.getXls(report, data);
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "attachment;filename=" + getMessageSource().getMessage(report.getTitle(), null, "Generic Report", Locale.getDefault()) + ".xls");
        response.getOutputStream().write(xlsTobytes(wb));
    }

    @RequestMapping(value = "/jpm/report/{reportId}/{savedReportId}/delete", method = RequestMethod.GET)
    public String deleteReport(@PathVariable String reportId, @PathVariable Long savedReportId) throws PMException {
        final EntityReport report = getJpm().getReport(reportId);
        reportService.deleteUserSave(savedReportId, getUserDetails().getUsername());
        return "redirect:/jpm/report/" + reportId;
    }

}
