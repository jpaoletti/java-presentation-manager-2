package jpaoletti.jpm2.web.search;

import java.util.Map;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.search.SearcherHelper;

/**
 * EnumSearcher que soporta propiedades anidadas (e.g. source.participantType).
 * Agrega los aliases de Hibernate necesarios para el join.
 */
public class NestedEnumSearcher extends EnumSearcher {

    private String enumClassName;

    @Override
    public DescribedCriterion build(Entity entity, Field field, Map<String, String[]> parameters) {
        DescribedCriterion dc = super.build(entity, field, parameters);
        SearcherHelper.addAliases(dc, field);
        return dc;
    }

    @Override
    protected Class getEnumClass(Entity entity, Field field) {
        if (enumClassName != null) {
            try {
                return Class.forName(enumClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Enum class not found: " + enumClassName, e);
            }
        }
        return super.getEnumClass(entity, field);
    }

    public String getEnumClassName() {
        return enumClassName;
    }

    public void setEnumClassName(String enumClassName) {
        this.enumClassName = enumClassName;
    }
}
