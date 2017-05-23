package jpaoletti.jpm2.core.model;

/**
 *
 * @author jpaoletti
 */
public interface Progresable {

    public Long getMaxProgress();

    public Long getCurrentProgress();

    public Double getPercent();

    public String getStatus();

}
