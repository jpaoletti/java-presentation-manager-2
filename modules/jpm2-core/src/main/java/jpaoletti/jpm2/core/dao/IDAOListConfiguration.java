package jpaoletti.jpm2.core.dao;

import java.util.Map;

/**
 *
 * @author jpaoletti
 */
public interface IDAOListConfiguration {

    public Integer getFrom();

    public Integer getMax();

    public void setFrom(Integer from);

    public void setMax(Integer max);

    public IDAOListConfiguration withFrom(Integer from);

    public IDAOListConfiguration withMax(Integer max);

    public Map<String, String> getProperties();

    public boolean containsAlias(String alias);

}
