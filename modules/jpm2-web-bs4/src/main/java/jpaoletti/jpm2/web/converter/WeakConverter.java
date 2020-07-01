package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.NotAuthorizedException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityContext;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;
import jpaoletti.jpm2.web.controller.ListController;

/**
 *
 * @author jpaoletti
 */
public class WeakConverter extends Converter {

    private Entity entity;
    private boolean showList;
    private boolean showBtn;
    private String btnText;
    private String btnIcon;
    private String context;
    private boolean showOperations; //if true, weak list show items operations

    public WeakConverter() {
        this.showBtn = true;
        this.showList = true;
        this.btnIcon = "fas fa-th-list";
        this.showOperations = false;
    }

    @Override
    public Object visualize(ContextualEntity contextualEntity, Field field, Object object, String instanceId) throws ConverterException, ConfigurationException {
        final StringBuilder res = new StringBuilder("@page:weak-converter.jsp");
        res.append("?showList=").append(isShowList());
        res.append("&showBtn=").append(isShowBtn());
        res.append("&btnText=").append(getBtnText());
        res.append("&btnIcon=").append(getBtnIcon());
        res.append("&weakId=").append(getEntity().getId());
        final EntityContext weakEntityContext = getEntity().getContext(getContext());
        try {
            res.append("&weakAuth=").append(getEntity().getOperation(ListController.OP_LIST, weakEntityContext).getAuthKey(getEntity(), weakEntityContext));
        } catch (NotAuthorizedException ex) {
            res.append("&weakAuth=").append(ex.getMsg().getText());
        } catch (Exception ex) {
            JPMUtils.getLogger().error("Error in weak converter: " + getEntity(), ex);
            throw new ConverterException("unexpected.exception");
        }
        res.append("&ownerId=").append(instanceId);
        res.append("&showOperations=").append(isShowOperations());
        if (getContext() != null) {
            res.append("&context=!").append(getContext());
        } else {
            res.append("&context=");
        }
        return res.toString();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean isShowList() {
        return showList;
    }

    public void setShowList(boolean showList) {
        this.showList = showList;
    }

    public boolean isShowBtn() {
        return showBtn;
    }

    public void setShowBtn(boolean showBtn) {
        this.showBtn = showBtn;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getBtnIcon() {
        return btnIcon;
    }

    public void setBtnIcon(String btnIcon) {
        this.btnIcon = btnIcon;
    }

    public boolean isShowOperations() {
        return showOperations;
    }

    public void setShowOperations(boolean showOperations) {
        this.showOperations = showOperations;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
