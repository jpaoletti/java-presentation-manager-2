package jpaoletti.jpm2.core.sysparam;

/**
 * A single finding of the sysparam health report: a reconciliation issue between the
 * code-declared catalog and what is actually stored in {@code jpm_sysparam}.
 *
 * @author jpaoletti
 */
public class SysparamHealthItem {

    /** Finding severity, ordered from most to least critical. */
    public enum Severity {
        ERROR, WARNING, INFO
    }

    private final Severity severity;
    private final String category;
    private final String key;
    private final String detail;

    public SysparamHealthItem(Severity severity, String category, String key, String detail) {
        this.severity = severity;
        this.category = category;
        this.key = key;
        this.detail = detail;
    }

    public Severity getSeverity() {
        return severity;
    }

    /** Machine name of the finding kind (suffix of its i18n key). */
    public String getCategory() {
        return category;
    }

    /** i18n key for the human-readable category label. */
    public String getCategoryKey() {
        return "jpm.sysparam.health.cat." + category;
    }

    /** The offending parameter key, or {@code -} for a global finding. */
    public String getKey() {
        return key;
    }

    public String getDetail() {
        return detail;
    }

    /** Bootstrap badge class for the severity, for the admin view. */
    public String getBadgeClass() {
        switch (severity) {
            case ERROR:
                return "bg-danger";
            case WARNING:
                return "bg-warning text-dark";
            default:
                return "bg-secondary";
        }
    }
}
