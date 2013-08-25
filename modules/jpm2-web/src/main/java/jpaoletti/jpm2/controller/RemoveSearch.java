package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 *
 * @author jpaoletti
 */
public class RemoveSearch extends EntityAction {

    private Integer i;

    @Override
    public String execute() throws Exception {
        final String loadEntity = loadEntity();
        if (!loadEntity.equals(SUCCESS)) {
            return loadEntity;
        }
        getSessionEntityData().getSearchCriteria().removeDefinition(getI());
        return SUCCESS;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }
}
