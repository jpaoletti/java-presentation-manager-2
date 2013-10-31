package jpaoletti.jpm2.web;

import jpaoletti.jpm2.core.model.EntityPath;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author jpaoletti
 */
public class EntityPathToStringConverter implements Converter<EntityPath, String> {

    @Override
    public String convert(EntityPath s) {
        return s.toString();
    }
}
