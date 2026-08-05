package jpaoletti.jpm2.core.entityparam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Builds a jstree JSON model of a single {@link ParameterizedEntity}'s parameters, grouped by their catalog
 * {@code group} (one folder per group, one leaf per parameter). Each leaf carries the metadata the tree UI
 * needs to open an inline "set value" popup: the parameter id, its resolved type/secrecy, allowed values,
 * catalog default and current (masked for secrets) value. Analogous to {@code SysparamService.treeJson()} but
 * scoped to one owner and catalog {@code kind}.
 *
 * @author jpaoletti
 */
public final class EntityParameterTree {

    private static final String MASK = "******";

    private EntityParameterTree() {
    }

    public static String json(ParameterizedEntity<? extends EntityParameter> owner) {
        final JSONArray root = new JSONArray();
        if (owner == null || owner.getParameters() == null) {
            return root.toString();
        }
        final Map<String, List<EntityParameter>> byGroup = new TreeMap<>();
        for (EntityParameter param : owner.getParameters()) {
            if (param.getName() == null) {
                continue;
            }
            final EntityParameterDef<?> def = EntityParameterResolver.defFor(owner, param.getName());
            final String group = (def != null && StringUtils.isNotBlank(def.getGroup())) ? def.getGroup() : "general";
            byGroup.computeIfAbsent(group, k -> new ArrayList<>()).add(param);
        }

        for (Map.Entry<String, List<EntityParameter>> entry : byGroup.entrySet()) {
            final List<EntityParameter> params = entry.getValue();
            params.sort((a, b) -> safe(a.getName()).compareToIgnoreCase(safe(b.getName())));
            final JSONArray children = new JSONArray();
            for (EntityParameter param : params) {
                final EntityParameterDef<?> def = EntityParameterResolver.defFor(owner, param.getName());
                final boolean secret = EntityParameterResolver.isSecret(owner, param.getName());
                final String type = (def != null) ? def.getType().name()
                        : (param.getType() != null ? param.getType().name() : "STRING");
                final String display = secret ? MASK : truncate(param.getValue(), 60);

                final JSONArray allowed = new JSONArray();
                if (def != null) {
                    for (String value : def.getAllowedValues()) {
                        allowed.put(value);
                    }
                }
                final JSONObject data = new JSONObject();
                data.put("pid", param.getId());
                data.put("name", param.getName());
                data.put("secret", secret);
                data.put("type", type);
                data.put("allowed", allowed);
                data.put("def", def != null ? def.getDefaultRaw() : JSONObject.NULL);
                data.put("cur", secret ? "" : safe(param.getValue()));

                final JSONObject leaf = new JSONObject();
                leaf.put("id", "p:" + param.getId());
                leaf.put("text", escapeHtml(param.getName())
                        + " <span class=\"text-muted\">= " + escapeHtml(display) + "</span>");
                leaf.put("icon", secret ? "fas fa-key" : "fas fa-tag");
                leaf.put("data", data);
                children.put(leaf);
            }
            final JSONObject groupNode = new JSONObject();
            groupNode.put("id", "g:" + entry.getKey());
            groupNode.put("text", "<i class=\"fas fa-folder\"></i> " + escapeHtml(entry.getKey())
                    + " <span class=\"text-muted\">(" + params.size() + ")</span>");
            groupNode.put("icon", false);
            groupNode.put("children", children);
            groupNode.put("state", new JSONObject().put("opened", true));
            root.put(groupNode);
        }
        return root.toString();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return "";
        }
        return value.length() <= max ? value : value.substring(0, max) + "…";
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
