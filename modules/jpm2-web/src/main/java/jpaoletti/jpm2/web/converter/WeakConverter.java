package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;

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
    private boolean showOperations; //if true, weak list show items operations

    public WeakConverter() {
        this.showBtn = true;
        this.showList = true;
        this.btnIcon = "glyphicon-th-list";
        this.showOperations = false;
    }

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException {

        return "@page:weak-converter.jsp?showList=" + isShowList() + "&showBtn=" + isShowBtn() + "&btnText=" + getBtnText() + "&btnIcon=" + getBtnIcon()
                + "&weakId=" + getEntity().getId() + "&ownerId=" + instanceId + "&showOperations=" + isShowOperations();
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
}
