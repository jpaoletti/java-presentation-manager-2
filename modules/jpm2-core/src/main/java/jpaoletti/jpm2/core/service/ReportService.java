 package jpaoletti.jpm2.core.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jpaoletti.jpm2.core.PMException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.dao.DAOListConfiguration;
import jpaoletti.jpm2.core.dao.EntityReportUserSaveDAO;
import jpaoletti.jpm2.core.dao.GenericDAO;
import jpaoletti.jpm2.core.exception.FieldNotFoundException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.reports.EntityReport;
import jpaoletti.jpm2.core.model.reports.EntityReportData;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.SearchCriteria;
import jpaoletti.jpm2.core.model.reports.EntityReportDataFilter;
import jpaoletti.jpm2.core.model.reports.EntityReportDataFormula;
import jpaoletti.jpm2.core.model.reports.EntityReportDataGraphic;
import jpaoletti.jpm2.core.model.reports.EntityReportDescriptiveField;
import jpaoletti.jpm2.core.model.reports.EntityReportResult;
import jpaoletti.jpm2.core.model.reports.EntityReportUserSave;
import jpaoletti.jpm2.core.search.Searcher;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.util.XlsUtils;
import static jpaoletti.jpm2.util.XlsUtils.replaceHtmlCodeAccents;
import static jpaoletti.jpm2.util.XlsUtils.xlsAmountStyle;
import static jpaoletti.jpm2.util.XlsUtils.xlsBoldStyle;
import static jpaoletti.jpm2.util.XlsUtils.xlsCellWithStyle;
import static jpaoletti.jpm2.util.XlsUtils.xlsDateStyle;
import static jpaoletti.jpm2.util.XlsUtils.xlsNewPage;
import static jpaoletti.jpm2.util.XlsUtils.xlsStrechColumns;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 *
 * @author jpaoletti
 */
@Service
public class ReportService extends JPMServiceBase {

    @Autowired
    private EntityReportUserSaveDAO entityReportUserSaveDAO;

    public EntityReportResult getResult(EntityReport report, EntityReportData reportData, boolean converted) throws FieldNotFoundException {
        final DAOListConfiguration cfg = new DAOListConfiguration();

        if (report.getFixedData() != null) {
            if (!CollectionUtils.isEmpty(report.getFixedData().getFilters())) {
                JPMUtils.getLogger().debug("ReportService.getResult --- FIXED FILTERS ");
                reportData.getFilters().addAll(report.getFixedData().getFilters());
            }
        }

        JPMUtils.getLogger().debug("ReportService.getResult --- MAX  : " + reportData.getMax());
        cfg.setMax(reportData.getMax());
        JPMUtils.getLogger().debug("ReportService.getResult --- FROM : " + reportData.getFrom());
        cfg.setFrom(reportData.getFrom());

        final SearchCriteria searchCriteria = new SearchCriteria();
        for (EntityReportDataFilter filter : reportData.getFilters()) {
            final Field field = report.getEntity().getFieldById(filter.getField(), null);
            final Searcher.DescribedCriterion build = field.getSearcher().build(report.getEntity(), field, filter.getParameterMap());
            searchCriteria.addDefinition(field.getId(), build);
            JPMUtils.getLogger().debug("ReportService.getResult --- FILTER : " + field.getId() + " | " + build.getDescription());
        }
        final Criterion search = searchCriteria.getCriterion();
        if (search != null) {
            cfg.getRestrictions().add(search);
            cfg.getAliases().addAll(searchCriteria.getAliases());
        }
        //Not very happy with this. Needs generalization.
        final Criteria c = ((GenericDAO) report.getEntity().getDao()).getBaseCriteria(cfg);

        //Only show descriptive fields
        final List<String> visibleFields = new ArrayList<>();
        if (isDetailed(report, reportData)) {
            visibleFields.addAll(reportData.getVisibleFields());
            if (!StringUtils.isEmpty(reportData.getSortField())) {
                final Field field = report.getEntity().getFieldById(reportData.getSortField(), null);
                JPMUtils.getLogger().debug("ReportService.getResult --- SORT FIELD  : " + reportData.getSortField());
                JPMUtils.getLogger().debug("ReportService.getResult --- SORT DIREC  : " + reportData.getSortDirection());
                //We may need to sort some of the internal data , like an object attribute
                switch (reportData.getSortDirection()) {
                    case ASC:
                        c.addOrder(Order.asc(field.getProperty()));
                        break;
                    case DESC:
                        c.addOrder(Order.desc(field.getProperty()));
                        break;
                }
            }
        } else {
            final ProjectionList pl = Projections.projectionList();
            for (String group : reportData.getGroups()) {
                if (!StringUtils.isBlank(group)) {
                    pl.add(Projections.groupProperty(group));
                    visibleFields.add(group);
                    JPMUtils.getLogger().debug("ReportService.getResult --- GROUP : " + group);
                }
            }
            for (EntityReportDataFormula formula : reportData.getFormulas()) {
                addFormulaProjections(report, formula, pl);
                visibleFields.add(formula.getField());
                JPMUtils.getLogger().debug("ReportService.getResult --- FORMULA : " + formula.getField());
            }
            pl.add(Projections.rowCount());
            visibleFields.add("#");
            JPMUtils.getLogger().debug("ReportService.getResult --- VIEW COUNT ");
            c.setProjection(pl);
        }
        if (!converted) {
            return new EntityReportResult(c.list(), null, visibleFields);
        }
        final List list = c.list();
        JPMUtils.getLogger().debug("ReportService.getResult --- CONVERTING DATA ");
        final List<List<String>> result = new ArrayList<>();
        JPMUtils.getLogger().debug("ReportService.getResult --- DATA COUNT: " + list.size());
        for (Object obj : list) {
            final List<String> item = new ArrayList<>();
            int i = 0;
            if (obj instanceof Object[]) {
                for (String fieldId : visibleFields) {
                    final Object[] values = (Object[]) obj;
                    final Object value = values[i++];
                    try {
                        final Field field = report.getEntity().getFieldById(fieldId, null);
                        final EntityReportDescriptiveField descriptiveField = report.getDescriptiveField(fieldId);
                        if (descriptiveField == null) {
                            JPMUtils.getLogger().debug("ReportService.getResult --- CONVERTING PLAIN '" + value + "'");
                            item.add(String.valueOf(value));
                        } else {
                            JPMUtils.getLogger().debug("ReportService.getResult --- CONVERTING '" + value + "' WITH '" + descriptiveField.getField() + "' CONVERTER ");
                            item.add(String.valueOf(descriptiveField.getConverter().visualizeValue(new ContextualEntity(report.getEntity(), null), field, value, null)));
                        }
                    } catch (Exception ex) {
                        item.add(String.valueOf(value));
                    }
                }
            } else if (obj instanceof Long) {
                JPMUtils.getLogger().debug("ReportService.getResult --- COUNT ONLY: " + obj);
                item.add(String.valueOf(obj));
            } else {
                JPMUtils.getLogger().debug("ReportService.getResult --- DETAILED: " + obj);
                for (String fieldId : visibleFields) {
                    try {
                        final Field field = report.getEntity().getFieldById(fieldId, null);
                        final EntityReportDescriptiveField descriptiveField = report.getDescriptiveField(fieldId);
                        if (descriptiveField == null) {
                            final Object value = Converter.getValue(obj, field.getProperty());
                            JPMUtils.getLogger().debug("ReportService.getResult --- CONVERTING PLAIN '" + value + "'");
                            item.add(String.valueOf(value));
                        } else {
                            final String value = String.valueOf(descriptiveField.getConverter().visualize(new ContextualEntity(report.getEntity(), null), field, obj, null));
                            JPMUtils.getLogger().debug("ReportService.getResult --- CONVERTED '" + value + "' WITH '" + descriptiveField.getField() + "' CONVERTER ");
                            item.add(value);
                        }
                    } catch (Exception ex) {
                        JPMUtils.getLogger().error(ex);
                        item.add("??");
                    }
                }
            }
            result.add(item);
        }
        return new EntityReportResult(result, null, visibleFields);
    }

    public Workbook getXls(EntityReport report, EntityReportData reportData) throws PMException {
        final Workbook wb = new HSSFWorkbook();
        final EntityReportResult result = getResult(report, reportData, false);

        final Sheet sheet = xlsNewPage(wb, new XlsUtils.XlsFormatTitle(
                getMessage("jpm.toExcel.pageName", null, LocaleContextHolder.getLocale()),
                getMessage(report.getTitle(), null, LocaleContextHolder.getLocale())
        ));
        sheet.createFreezePane(0, 3);
        final CellStyle bold = xlsBoldStyle(wb);
        final CellStyle xlsDateStyle = xlsDateStyle(wb);
        final CellStyle xlsAmountStyle = xlsAmountStyle(wb);
        final Row headerRow = sheet.createRow(2);
        int i = 0;
        //Titles
        if (reportData.getGroups().isEmpty() && reportData.getFormulas().isEmpty() && !report.getDescriptiveFieldList().isEmpty() && report.isAllowDetail()) {
            for (String f : reportData.getVisibleFields()) {
                if (!StringUtils.isEmpty(f)) {

                    final Cell cell = headerRow.createCell(i++);
                    cell.setCellStyle(bold);
                    cell.setCellValue(replaceHtmlCodeAccents(report.getEntity().getFieldById(f, null).getTitle(report.getEntity())));
                }
            }
        } else {
            for (String f : reportData.getGroups()) {
                if (!StringUtils.isEmpty(f)) {
                    final Cell cell = headerRow.createCell(i++);
                    cell.setCellStyle(bold);
                    cell.setCellValue(replaceHtmlCodeAccents(report.getEntity().getFieldById(f, null).getTitle(report.getEntity())));
                }
            }
            for (EntityReportDataFormula f : reportData.getFormulas()) {
                final Cell cell = headerRow.createCell(i++);
                cell.setCellStyle(bold);
                cell.setCellValue(String.format("%s(%s)", f.getFormula().toString(), replaceHtmlCodeAccents(report.getEntity().getFieldById(f.getField(), null).getTitle(report.getEntity()))));
            }
            final Cell cell = headerRow.createCell(i++);
            cell.setCellStyle(bold);
            cell.setCellValue(getMessage("jpm.reports.list.count"));
        }
        //End titles
        //Content
        int r = 3;
        for (Object obj : result.getMainData()) {
            final Row row = sheet.createRow(r++);
            i = 0;
            if (obj instanceof Object[]) {
                final Object[] values = (Object[]) obj;
                for (Object value : values) {
                    addDataCell(row, xlsDateStyle, xlsAmountStyle, i++, value);
                }
            } else if (obj instanceof Long) {
                addDataCell(row, xlsDateStyle, xlsAmountStyle, i++, obj);
            } else {
                for (String fieldId : result.getVisibleFields()) {
                    try {
                        final Field field = report.getEntity().getFieldById(fieldId, null);
                        final Object value = Converter.getValue(obj, field.getProperty());
                        addDataCell(row, xlsDateStyle, xlsAmountStyle, i++, value);
                    } catch (Exception ex) {
                        JPMUtils.getLogger().error(ex);
                        addDataCell(row, xlsDateStyle, xlsAmountStyle, i++, "??");
                    }
                }
            }
        }
        return xlsStrechColumns(wb);
    }

    protected void addDataCell(final Row row, CellStyle xlsDateStyle, CellStyle xlsAmountStyle, int i, Object convertedValue) {
        if (convertedValue == null) {
            row.createCell(i).setCellValue("");
        } else if (convertedValue instanceof String) {
            row.createCell(i).setCellValue((String) convertedValue);
        } else if (convertedValue instanceof Date) {
            xlsCellWithStyle(row.createCell(i), xlsDateStyle).setCellValue((Date) convertedValue);
        } else if (convertedValue instanceof Boolean) {
            row.createCell(i).setCellValue((Boolean) convertedValue);
        } else if (convertedValue instanceof BigDecimal) {
            xlsCellWithStyle(row.createCell(i), xlsAmountStyle).setCellValue(((BigDecimal) convertedValue).doubleValue());
        } else {
            row.createCell(i).setCellValue(convertedValue.toString());
        }
    }

    protected static boolean isDetailed(EntityReport report, EntityReportData reportData) {
        return report.isAllowDetail() && !report.getDescriptiveFieldList().isEmpty() && (reportData.getGroups() == null || reportData.getGroups().isEmpty()) && (reportData.getFormulas() == null || reportData.getFormulas().isEmpty());
    }

    public List getGraphicResult(EntityReport report, EntityReportData reportData, EntityReportDataGraphic graphicData, boolean converted) throws FieldNotFoundException {
        final DAOListConfiguration cfg = new DAOListConfiguration();

        final SearchCriteria searchCriteria = new SearchCriteria();
//        for (EntityReportDataFilter filter : graphicData.getFilters()) {
//            final Field field = report.getEntity().getFieldById(filter.getField(), null);
//            final Searcher.DescribedCriterion build = field.getSearcher().build(field, filter.getParameterMap());
//            searchCriteria.addDefinition(field.getId(), build);
//        }
        final Criterion search = searchCriteria.getCriterion();
        if (search != null) {
            cfg.getRestrictions().add(search);
            cfg.getAliases().addAll(searchCriteria.getAliases());
        }
        //Not very happy with this. Needs generalization.
        final Criteria c = ((GenericDAO) report.getEntity().getDao()).getBaseCriteria(cfg);

        //Only show descriptive fields
        final ProjectionList pl = Projections.projectionList();
        final List<String> visibleFields = new ArrayList<>();
        return c.list();
    }

    protected void addFormulaProjections(EntityReport report, EntityReportDataFormula formula, final ProjectionList pl) throws FieldNotFoundException {
        final Field field = report.getEntity().getFieldById(formula.getField(), null);
        switch (formula.getFormula()) {
            case AVG:
                pl.add(Projections.avg(field.getProperty()));
                break;
            case SUM:
                pl.add(Projections.sum(field.getProperty()));
                break;
            case MIN:
                pl.add(Projections.min(field.getProperty()));
                break;
            case MAX:
                pl.add(Projections.max(field.getProperty()));
                break;
        }
    }

    public void addAlias(Criteria c, Field field, List<String> aliases) {
        if (field.getProperty().contains(".")) {//need an alias
            final String alias = field.getProperty().substring(0, field.getProperty().indexOf("."));
            if (!aliases.contains(alias)) {
                c.createAlias(alias, alias);
                aliases.add(alias);
            }
        }
    }

    @Transactional
    public EntityReportUserSave saveReport(EntityReport report, String reportId, String reportName, String username, String content) {
        final EntityReportUserSave save = new EntityReportUserSave();
        save.setContent(content);
        save.setName(reportName);
        save.setReportId(reportId);
        save.setUsername(username);
        entityReportUserSaveDAO.save(save);
        return save;
    }

    public List<EntityReportUserSave> getUserSaves(String username) {
        return entityReportUserSaveDAO.list(new DAOListConfiguration(Restrictions.eq("username", username)));
    }

    public EntityReportUserSave getUserSave(Long id, String username) {
        return entityReportUserSaveDAO.find(new DAOListConfiguration(Restrictions.eq("username", username), Restrictions.eq("id", id)));
    }

    @Transactional
    public void deleteUserSave(Long savedReportId, String username) {
        final EntityReportUserSave userSave = getUserSave(savedReportId, username);
        if (userSave != null) {
            entityReportUserSaveDAO.delete(userSave);
        }
    }
}
