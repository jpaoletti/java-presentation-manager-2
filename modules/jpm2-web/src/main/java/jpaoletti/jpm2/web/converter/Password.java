package jpaoletti.jpm2.web.converter;

import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.converter.ConverterException;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.security.BCrypt;

/**
 *
 * @author jpaoletti
 */
public class Password extends Converter {

    @Override
    public Object visualize(Field field, Object object, String instanceId) throws ConverterException {
       return "@page:password-converter.jsp";
    }

    @Override
    public Object build(Field field, Object object, Object newValue) throws ConverterException {
        return BCrypt.hashpw((String) newValue, BCrypt.gensalt());
    }
}
