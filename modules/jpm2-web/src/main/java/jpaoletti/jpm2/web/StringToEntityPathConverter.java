package jpaoletti.jpm2.web;

import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.exception.EntityNotFoundException;
import jpaoletti.jpm2.core.model.EntityPath;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author jpaoletti
 */
public class StringToEntityPathConverter implements Converter<String, EntityPath> {

    @Autowired
    private PresentationManager jpm;

    @Override
    public EntityPath convert(String s) {
        try {
            return new EntityPath(getJpm(), s);
        } catch (EntityNotFoundException ex) {
            JPMUtils.getLogger().error(ex.getMessage());
            return null;
        }
    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }
}
