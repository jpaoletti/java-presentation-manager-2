package jpaoletti.jpm2.core.sysparam;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of reconciling the code-declared {@link SysparamCatalog} against the stored
 * {@code jpm_sysparam} rows: required-without-value, type/validation mismatches, secrets
 * kept in plaintext, orphan (undeclared) rows and deprecated parameters still set. It is a
 * read-only diagnostic snapshot produced on demand for the admin health screen.
 *
 * @author jpaoletti
 */
public class SysparamHealthReport {

    private final List<SysparamHealthItem> items = new ArrayList<>();
    private boolean cipherEnabled;
    private int totalDefs;
    private int totalRows;

    public void add(SysparamHealthItem.Severity severity, String category, String key, String detail) {
        items.add(new SysparamHealthItem(severity, category, key, detail));
    }

    public List<SysparamHealthItem> getItems() {
        return items;
    }

    public int getErrorCount() {
        return count(SysparamHealthItem.Severity.ERROR);
    }

    public int getWarningCount() {
        return count(SysparamHealthItem.Severity.WARNING);
    }

    public int getInfoCount() {
        return count(SysparamHealthItem.Severity.INFO);
    }

    /** @return true when there are no errors and no warnings (info findings are tolerated). */
    public boolean isHealthy() {
        return getErrorCount() == 0 && getWarningCount() == 0;
    }

    private int count(SysparamHealthItem.Severity severity) {
        int n = 0;
        for (SysparamHealthItem item : items) {
            if (item.getSeverity() == severity) {
                n++;
            }
        }
        return n;
    }

    public boolean isCipherEnabled() {
        return cipherEnabled;
    }

    public void setCipherEnabled(boolean cipherEnabled) {
        this.cipherEnabled = cipherEnabled;
    }

    public int getTotalDefs() {
        return totalDefs;
    }

    public void setTotalDefs(int totalDefs) {
        this.totalDefs = totalDefs;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }
}
