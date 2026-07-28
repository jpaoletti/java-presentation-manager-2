package jpaoletti.jpm2.core.sysparam;

/**
 * Declaration of an open family of sysparams sharing a key prefix (e.g. {@code terminal-}).
 * Members are not individually declared: any key matching the prefix is a legitimate,
 * administrable free parameter. Gives structure (group, member type, cache/secret policy)
 * to otherwise loose keys, and lets the whole family be read at once and passed through
 * verbatim.
 *
 * @author jpaoletti
 */
public final class SysparamFamily {

    private final String prefix;
    private final SysparamType memberType;
    private final String group;
    private final boolean cached;
    private final String descriptionKey;

    private SysparamFamily(Builder b) {
        this.prefix = b.prefix;
        this.memberType = b.memberType;
        this.group = b.group;
        this.cached = b.cached;
        this.descriptionKey = b.descriptionKey;
    }

    public String getPrefix() {
        return prefix;
    }

    public SysparamType getMemberType() {
        return memberType;
    }

    public String getGroup() {
        return group;
    }

    /** @return true if members are secret, i.e. the member type is {@link SysparamType#SECRET}. */
    public boolean isSecret() {
        return memberType == SysparamType.SECRET;
    }

    public boolean isCached() {
        return cached;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public boolean matches(String key) {
        return key != null && key.startsWith(prefix);
    }

    public static Builder of(String prefix) {
        return new Builder(prefix);
    }

    /**
     * Fluent builder for {@link SysparamFamily}.
     */
    public static final class Builder {

        private final String prefix;
        private SysparamType memberType = SysparamType.STRING;
        private String group;
        private boolean cached = true;
        private String descriptionKey;

        private Builder(String prefix) {
            this.prefix = prefix;
            this.group = prefix;
        }

        public Builder memberType(SysparamType memberType) {
            this.memberType = memberType;
            return this;
        }

        public Builder group(String group) {
            this.group = group;
            return this;
        }

        public Builder cached(boolean cached) {
            this.cached = cached;
            return this;
        }

        public Builder description(String descriptionKey) {
            this.descriptionKey = descriptionKey;
            return this;
        }

        public SysparamFamily build() {
            return new SysparamFamily(this);
        }
    }
}
