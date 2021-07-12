package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class TakeOrUploadPictureConverter extends BaseEditFileConverter {

    private String cropperAspectRatio = "Number.Nan";
    private String confirmBtnLabel = "Change";
    private String cancelBtnLabel = "Cancel";

    @Override
    protected String getPage(ContextualEntity contextualEntity, Field field, Object object, String instanceId) {
        return "@page:takeOrUploadPicture-converter.jsp"
                + "?instanceId=" + instanceId
                + "&entityId=" + contextualEntity.toString()
                + "&cropperAspectRatio=" + cropperAspectRatio
                + "&confirmBtnLabel=" + confirmBtnLabel
                + "&cancelBtnLabel=" + cancelBtnLabel
                + "&fieldId=" + field.getId() + "&";
    }

    public String getCropperAspectRatio() {
        return cropperAspectRatio;
    }

    public void setCropperAspectRatio(String cropperAspectRatio) {
        this.cropperAspectRatio = cropperAspectRatio;
    }

    public String getConfirmBtnLabel() {
        return confirmBtnLabel;
    }

    public void setConfirmBtnLabel(String confirmBtnLabel) {
        this.confirmBtnLabel = confirmBtnLabel;
    }

    public String getCancelBtnLabel() {
        return cancelBtnLabel;
    }

    public void setCancelBtnLabel(String cancelBtnLabel) {
        this.cancelBtnLabel = cancelBtnLabel;
    }

}
