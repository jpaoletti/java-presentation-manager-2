package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;

/**
 *
 * @author jpaoletti
 */
public class EditFileConverter extends BaseEditFileConverter {

    @Override
    protected String getPage(ContextualEntity contextualEntity, Field field, Object object, String instanceId) {
        return "@page:file-converter.jsp?postAction=uploadFileConverter&";
    }
}
