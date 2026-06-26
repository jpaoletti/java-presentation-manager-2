package jpaoletti.jpm2.core.model.persistent;

public enum NotificationType {
    None("secondary", "fa-regular fa-circle"),
    Information("info", "fa-solid fa-circle-info"),
    Warning("warning", "fa-solid fa-triangle-exclamation"),
    Urgency("danger", "fa-solid fa-bell");

    private final String cssClass;
    private final String iconClass;

    NotificationType(String cssClass, String iconClass) {
        this.cssClass = cssClass;
        this.iconClass = iconClass;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getIconClass() {
        return iconClass;
    }
}
