package jpaoletti.jpm2.core.model;

/**
 *
 * @author jpaoletti
 */
public interface Progresable {

    public Long getMaxProgress();

    public void setMaxProgress(Long maxProgress);

    public Long getCurrentProgress();

    public void setCurrentProgress(Long currentProgress);

    public Double getPercent();

    public String getStatus();

    public void setStatus(String status);

    public void inc();

}
