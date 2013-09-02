package jpaoletti.jpm2.core.model;

import org.hibernate.criterion.Order;

/**
 * Entity list order
 *
 * @author jpaoletti
 */
public class ListSort {

    private Field field;
    private SortDirection direction;

    public ListSort() {
        this.direction = SortDirection.ASC;
    }

    public ListSort(Field field, SortDirection direction) {
        this.field = field;
        this.direction = direction;
    }

    public Order getOrder() {
        if (isSorted()) {
            if (isAsc()) {
                return Order.asc(getField().getProperty());
            } else {
                return Order.desc(getField().getProperty());
            }
        } else {
            return null;
        }
    }

    public void set(Field f) {
        //Same field, toggle asc/desc
        if (f.equals(getField())) {
            if (isAsc()) {
                setDirection(SortDirection.DESC);
            } else {
                setDirection(SortDirection.ASC);
            }
        } else {
            setField(f);
            setDirection(SortDirection.ASC);
        }
    }

    public boolean isDesc() {
        return getDirection().equals(SortDirection.DESC);
    }

    public boolean isAsc() {
        return getDirection().equals(SortDirection.ASC);
    }

    public boolean isSorted() {
        return getField() != null;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public static enum SortDirection {

        ASC, DESC;
    }
}
