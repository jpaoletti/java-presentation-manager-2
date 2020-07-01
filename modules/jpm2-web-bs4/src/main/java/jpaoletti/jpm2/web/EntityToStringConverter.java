package jpaoletti.jpm2.web;

import jpaoletti.jpm2.core.model.Entity;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author jpaoletti
 */
public class EntityToStringConverter implements Converter<Entity, String> {

    @Override
    public String convert(Entity s) {
        return s.getId();
    }
}
