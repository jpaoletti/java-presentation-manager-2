package jpaoletti.jpm2.core.dao;

/**
 *
 * @author jpaoletti
 */
public interface IDAOListConfiguration {

    Integer getFrom();

    Integer getMax();

    void setFrom(Integer from);

    void setMax(Integer max);

    DAOListConfiguration withFrom(Integer from);

    DAOListConfiguration withMax(Integer max);

}
