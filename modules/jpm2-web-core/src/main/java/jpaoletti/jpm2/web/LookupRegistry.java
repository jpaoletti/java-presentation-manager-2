package jpaoletti.jpm2.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jpaoletti.jpm2.core.PresentationManager;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.EntityContext;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.FieldConfig;
import jpaoletti.jpm2.core.model.IdentifiableListFilter;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Index of the lookups (combos / autocompletes) declared in the entities
 * configuration. A lookup is any field converter or searcher that exposes an
 * "entity" property (the target Entity) and a "textField" property, like
 * WebEditObject, WebEditCollection, ObjectSearcher or CollectionSearcher2.
 *
 * It is used to authorize the generic json endpoints: a request that matches a
 * declared lookup is allowed when the user can see one of its source fields.
 *
 * @author jpaoletti
 */
@Component
public class LookupRegistry {

    @Autowired
    private PresentationManager jpm;

    private volatile Map<String, List<Lookup>> lookups;

    /**
     * Declared lookups whose target is the given entity id.
     */
    public List<Lookup> getLookups(String targetEntityId) {
        final List<Lookup> res = getIndex().get(targetEntityId);
        return res == null ? Collections.emptyList() : res;
    }

    /**
     * Declared lookups matching the given target, text field and filter.
     */
    public List<Lookup> find(String targetEntityId, String textField, String filter) {
        final List<Lookup> res = new ArrayList<>();
        for (Lookup lookup : getLookups(targetEntityId)) {
            if (lookup.getTextField().equals(normalize(textField)) && lookup.getFilter().equals(normalize(filter))) {
                res.add(lookup);
            }
        }
        return res;
    }

    /**
     * True if the filter bean is declared by any lookup of the target entity.
     */
    public boolean isDeclaredFilter(String targetEntityId, String filter) {
        final String f = normalize(filter);
        for (Lookup lookup : getLookups(targetEntityId)) {
            if (lookup.getFilter().equals(f)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if the text field is declared by any lookup of the target entity.
     */
    public boolean isDeclaredTextField(String targetEntityId, String textField) {
        final String t = normalize(textField);
        for (Lookup lookup : getLookups(targetEntityId)) {
            if (lookup.getTextField().equals(t)) {
                return true;
            }
        }
        return false;
    }

    public void setJpm(PresentationManager jpm) {
        this.jpm = jpm;
        this.lookups = null;
    }

    protected Map<String, List<Lookup>> getIndex() {
        Map<String, List<Lookup>> res = lookups;
        if (res == null) {
            synchronized (this) {
                res = lookups;
                if (res == null) {
                    res = build();
                    lookups = res;
                }
            }
        }
        return res;
    }

    protected Map<String, List<Lookup>> build() {
        final Map<String, List<Lookup>> res = new LinkedHashMap<>();
        for (Entity entity : jpm.getEntities().values()) {
            register(res, entity, null, entity.getAllFields(null));
            if (entity.getContexts() != null) {
                for (EntityContext ctx : entity.getContexts()) {
                    register(res, entity, ctx.getId(), entity.getAllFields(ctx.getId()));
                }
            }
        }
        int count = 0;
        for (List<Lookup> list : res.values()) {
            count += list.size();
        }
        JPMUtils.getLogger().debug("LookupRegistry built targets={} lookups={}", res.size(), count);
        return res;
    }

    private void register(Map<String, List<Lookup>> res, Entity entity, String context, List<Field> fields) {
        for (Field field : fields) {
            try {
                for (FieldConfig config : field.getConfigs()) {
                    register(res, entity, context, field, config.getConverter());
                }
                register(res, entity, context, field, field.getSearcher());
            } catch (Exception e) {
                JPMUtils.getLogger().warn("LookupRegistry could not inspect field {}.{}: {}", entity.getId(), field.getId(), e.getMessage());
            }
        }
    }

    private void register(Map<String, List<Lookup>> res, Entity entity, String context, Field field, Object candidate) {
        if (candidate == null) {
            return;
        }
        final BeanWrapper bw = new BeanWrapperImpl(candidate);
        if (!bw.isReadableProperty("entity") || !bw.isReadableProperty("textField")) {
            return;
        }
        final Object target = bw.getPropertyValue("entity");
        if (!(target instanceof Entity)) {
            return;
        }
        String filter = null;
        if (bw.isReadableProperty("filter")) {
            final Object f = bw.getPropertyValue("filter");
            if (f instanceof IdentifiableListFilter) {
                filter = ((IdentifiableListFilter) f).getId();
            } else if (f instanceof String) {
                filter = (String) f;
            }
        }
        final Object textField = bw.getPropertyValue("textField");
        final Lookup lookup = new Lookup(entity, context, field, normalize(textField == null ? null : textField.toString()), normalize(filter));
        final String targetId = ((Entity) target).getId();
        final List<Lookup> list = res.computeIfAbsent(targetId, k -> new ArrayList<>());
        if (!list.contains(lookup)) {
            list.add(lookup);
        }
    }

    /**
     * Converters render null properties as the "null" string in the page
     * parameters, so null, "" and "null" are all the same value.
     */
    public static String normalize(String value) {
        return (value == null || "null".equals(value)) ? "" : value;
    }

    public static class Lookup {

        private final Entity source;
        private final String sourceContext;
        private final Field sourceField;
        private final String textField;
        private final String filter;

        public Lookup(Entity source, String sourceContext, Field sourceField, String textField, String filter) {
            this.source = source;
            this.sourceContext = sourceContext;
            this.sourceField = sourceField;
            this.textField = textField;
            this.filter = filter;
        }

        public Entity getSource() {
            return source;
        }

        public String getSourceContext() {
            return sourceContext;
        }

        public Field getSourceField() {
            return sourceField;
        }

        public String getTextField() {
            return textField;
        }

        public String getFilter() {
            return filter;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Lookup)) {
                return false;
            }
            final Lookup o = (Lookup) obj;
            return source == o.source && sourceField == o.sourceField
                    && String.valueOf(sourceContext).equals(String.valueOf(o.sourceContext))
                    && textField.equals(o.textField) && filter.equals(o.filter);
        }

        @Override
        public int hashCode() {
            return (source.getId() + "|" + sourceContext + "|" + sourceField.getId() + "|" + textField + "|" + filter).hashCode();
        }

        @Override
        public String toString() {
            return source.getId() + (sourceContext == null ? "" : "!" + sourceContext) + "." + sourceField.getId() + " textField=" + textField + " filter=" + filter;
        }
    }
}
