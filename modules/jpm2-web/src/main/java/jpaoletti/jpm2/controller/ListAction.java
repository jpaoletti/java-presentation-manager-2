package jpaoletti.jpm2.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.List;
import jpaoletti.jpm2.core.model.EntityInstanceList;

/**
 *
 * @author jpaoletti
 */
public class ListAction extends OperationAction {

    private EntityInstanceList list;

    @Override
    public String execute() throws Exception {
        final String prepare = prepare();
        if (prepare.equals(SUCCESS)) {
            list = new EntityInstanceList();
            final List l = getEntity().getDao().list();
            list.load(l, getEntity(), getOperation(), null);
            return SUCCESS;
        } else {
            return prepare;
        }
    }

    public EntityInstanceList getList() {
        return list;
    }

    public void setList(EntityInstanceList list) {
        this.list = list;
    }
}
