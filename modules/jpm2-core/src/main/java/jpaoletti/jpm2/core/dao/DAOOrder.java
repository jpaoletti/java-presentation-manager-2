package jpaoletti.jpm2.core.dao;

/**
 *
 * @author jpaoletti
 */
public class DAOOrder {

    private String order;
    private boolean asc;

    public DAOOrder(String order, boolean asc) {
        this.order = order;
        this.asc = asc;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

}
