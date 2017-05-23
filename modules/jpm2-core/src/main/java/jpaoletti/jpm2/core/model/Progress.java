package jpaoletti.jpm2.core.model;

import java.util.Observable;

/**
 *
 * @author jpaoletti
 */
public class Progress extends Observable implements Progresable {

    private Long maxProgress;
    private Long currentProgress = 1l;
    private String status;
    private String error; //TODO
    private Double lastPercent = 0d;

    public void inc() {
        currentProgress++;
        if (getPercent() > lastPercent) {
            lastPercent = getPercent();
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public Double getPercent() {
        try {
            return (double) Math.round(((getCurrentProgress().doubleValue() - 1) * 100) / getMaxProgress().doubleValue());
        } catch (Exception e) {
            return 0d;
        }
    }

    @Override
    public Long getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(Long maxProgress) {
        this.maxProgress = maxProgress;
        setChanged();
        notifyObservers();
    }

    @Override
    public Long getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(Long currentProgress) {
        this.currentProgress = currentProgress;
        this.lastPercent = 0d;
        setChanged();
        notifyObservers();
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
