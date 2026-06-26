package jpaoletti.jpm2.core.service;

public class NotificationBellPayloadItem {

    private Long id;
    private String description;
    private String createdAt;
    private String typeCssClass;
    private String typeIconClass;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTypeCssClass() {
        return typeCssClass;
    }

    public void setTypeCssClass(String typeCssClass) {
        this.typeCssClass = typeCssClass;
    }

    public String getTypeIconClass() {
        return typeIconClass;
    }

    public void setTypeIconClass(String typeIconClass) {
        this.typeIconClass = typeIconClass;
    }
}
