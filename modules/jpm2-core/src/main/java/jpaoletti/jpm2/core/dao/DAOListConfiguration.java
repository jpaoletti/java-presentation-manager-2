package jpaoletti.jpm2.core.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

/**
 * Options for dao.list().
 *
 * @author jpaoletti
 */
public class DAOListConfiguration {

    private Integer from;
    private Integer max;
    private Order order;
    private List<Criterion> restrictions;
    private Map<String, String> aliases;

    public DAOListConfiguration() {
    }

    public DAOListConfiguration(Integer from, Integer max) {
        this.from = from;
        this.max = max;
    }

    public DAOListConfiguration(Criterion... restrictions) {
        this.restrictions = new ArrayList<>();
        this.restrictions.addAll(Arrays.asList(restrictions));
    }

    public DAOListConfiguration(Integer from, Integer max, Criterion... restrictions) {
        this.from = from;
        this.max = max;
        this.restrictions = Arrays.asList(restrictions);
    }

    public DAOListConfiguration(Order order, Criterion... restrictions) {
        this.order = order;
        this.restrictions = Arrays.asList(restrictions);
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<Criterion> getRestrictions() {
        if (restrictions == null) {
            restrictions = new ArrayList<>();
        }
        return restrictions;
    }

    public void setRestrictions(List<Criterion> restrictions) {
        this.restrictions = restrictions;
    }

    public Map<String, String> getAliases() {
        if (aliases == null) {
            aliases = new HashMap<>();
        }
        return aliases;
    }

    public void setAliases(Map<String, String> aliases) {
        this.aliases = aliases;
    }

    public DAOListConfiguration withMax(Integer max) {
        setMax(max);
        return this;
    }

    public DAOListConfiguration withFrom(Integer from) {
        setFrom(from);
        return this;
    }

    public DAOListConfiguration withAliases(Map<String, String> aliases) {
        setAliases(aliases);
        return this;
    }

    public DAOListConfiguration withAlias(String key, String value) {
        getAliases().put(key, value);
        return this;
    }

    public DAOListConfiguration withRestrictions(List<Criterion> restrictions) {
        setRestrictions(restrictions);
        return this;
    }

    public DAOListConfiguration withOrder(Order order) {
        setOrder(order);
        return this;
    }
}
