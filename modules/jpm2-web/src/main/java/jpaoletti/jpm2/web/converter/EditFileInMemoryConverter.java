package jpaoletti.jpm2.web.converter;

import javax.servlet.http.HttpSession;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.FieldNotFoundException;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This file converter do not rely in a disk file but in a session attribute.
 *
 * @author jpaoletti
 */
public class EditFileInMemoryConverter extends BaseEditFileConverter {

    @Autowired
    private HttpSession session;

    @Override
    protected String getPage(ContextualEntity contextualEntity, Field field, Object object, String instanceId) {
        return "@page:file-converter.jsp?postAction=uploadFileInMemoryConverter&";
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue != null && newValue.equals("@current:")) {
            throw new IgnoreConvertionException();
        }
        if (newValue == null || newValue.equals("")) {
            return null;
        }
        byte[] bFile = (byte[]) session.getAttribute(newValue.toString());

        if (bFile == null) {
            JPMUtils.getLogger().error("File not exists in file converter");
            throw new ConverterException("jpm.error.uploading.file");
        }
        try {
            if (getFilenameField() != null) {
                try {
                    final Field f = contextualEntity.getEntity().getFieldById(getFilenameField(), contextualEntity.getContext());
                    JPMUtils.set(object, f.getProperty(), session.getAttribute(newValue + "originalName"));
                    session.removeAttribute(newValue + "originalName");
                } catch (FieldNotFoundException ex) {
                    JPMUtils.getLogger().error("Filename field not exists");
                    throw new ConverterException("jpm.error.uploading.file");
                }
            }
        } finally {
            session.removeAttribute(newValue.toString());
        }
        return bFile;
    }

}