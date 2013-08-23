package jpaoletti.jpm2.core.model;

/**
 *
 * @author jpaoletti
 */
public class SessionEntityData {

    private String entityId;
    private Integer page;
    private Integer pageSize;

    public SessionEntityData(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
