package jpaoletti.jpm2.web;

import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author jpaoletti
 */
public class StringToEntityConverter implements Converter<String, Entity> {

    @Autowired
    private PresentationManager jpm;

    @Override
    public Entity convert(String s) {
        return getJpm().getEntity(s);

    }

    public PresentationManager getJpm() {
        return jpm;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
    }
}
