package jpaoletti.jpm2.controller.ajax;

import jpaoletti.jpm2.controller.EntityAction;

/**
 * Base class for ajax actions. Based on jSON response.
 *
 * @author jpaoletti
 */
public class BaseAjaxAction extends EntityAction {

    private Object result; //jSON

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
