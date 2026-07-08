package jpaoletti.jpm2.core.i18n;

import jpaoletti.jpm2.core.service.CustomizationService;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MessageSource that looks up Customization (DB) first and, if not found,
 * delegates to the parentMessageSource (e.g. messages.properties files).
 */
public class DbMessageSource extends AbstractMessageSource implements HierarchicalMessageSource, InitializingBean {

    private static final String DEFAULT_PREFIX = "**";

    private MessageSource parentMessageSource;
    private CustomizationService customizationService;

    /**
     * cache per Locale
     */
    private final Map<Locale, Properties> cacheByLocale = new ConcurrentHashMap<>();
    private int cacheSeconds = -1; // <=0: never expires
    private final AtomicLong lastReload = new AtomicLong(0);

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
    }

    @Override
    public void setParentMessageSource(MessageSource parent) {
        this.parentMessageSource = parent;
    }

    @Override
    public MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String msg = resolveFromDb(code, locale);
        if (msg != null) {
            return createMessageFormat(msg, locale);
        }
        // delegate to parent
        return (parentMessageSource != null) ? getMessageFormatFromParent(code, locale) : null;
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        String msg = resolveFromDb(code, locale);
        if (msg != null) {
            return msg;
        }
        // delegate to parent
        try {
            return (parentMessageSource != null)
                    ? parentMessageSource.getMessage(code, null, locale)
                    : null;
        } catch (Exception ignore) {
            return null;
        }
    }

    private MessageFormat getMessageFormatFromParent(String code, Locale locale) {
        try {
            String pattern = parentMessageSource.getMessage(code, null, null, locale);
            return (pattern != null) ? createMessageFormat(pattern, locale) : null;
        } catch (Exception ignore) {
            return null;
        }
    }

    private String resolveFromDb(String code, Locale locale) {
        maybeReload();
        // Strategy: exact locale -> language-only -> default locale (optional)
        List<Locale> candidates = candidatesFor(locale);
        for (Locale cand : candidates) {
            Properties p = cacheByLocale.get(cand);
            if (p != null && p.containsKey(code)) {
                return p.getProperty(code);
            }
        }
        // alternative: if you store everything in a single "messages" without locales,
        // you can also check cacheByLocale.get(Locale.ROOT)
        Properties root = cacheByLocale.get(Locale.ROOT);
        if (root != null && root.containsKey(code)) {
            return root.getProperty(code);
        }
        return null;
    }

    private void maybeReload() {
        if (cacheSeconds <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        long last = lastReload.get();
        if (now - last >= cacheSeconds * 1000L) {
            if (lastReload.compareAndSet(last, now)) {
                cacheByLocale.clear();
                loadAllLocales();
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        loadAllLocales();
    }

    private void loadAllLocales() {
        // 1) If you have a per-Locale record in Customization:
        // Map<Locale, String> map = customizationService.fetchMessagesByLocale();
        // for (Map.Entry<Locale, String> e : map.entrySet()) putProps(e.getKey(), e.getValue());

        // 2) If you have a single "messages" text (mix of keys per language):
        String all = customizationService.getCustomization().getMessages();
        if (StringUtils.hasText(all)) {
            // Option A: if it is split into per-locale blocks, parse them here.
            // Option B: if they come as standard properties including a .es, .en, etc. suffix,
            //          you can distribute them into different Properties per locale:
            Map<Locale, Properties> distributed = distributeByLocale(all);
            cacheByLocale.putAll(distributed);
        }
    }

    private void putProps(Locale locale, String propsText) {
        try {
            Properties p = new Properties();
            p.load(new StringReader(propsText));
            cacheByLocale.put(locale, p);
        } catch (Exception e) {
            // log it if you want
        }
    }

    /**
     * Accepts keys with a locale prefix:
     * es.jpm.field.customization.style=Estilo
     * es_AR.jpm.field.customization.style=Estilo AR
     * en.jpm.field.customization.style=Style
     * **.jpm.field.customization.style=Default (DB fallback). It also supports
     * keys without a prefix (they go to ROOT): jpm.field.customization.color=Color
     */
    private Map<Locale, Properties> distributeByLocale(String text) {
        Properties raw = new Properties();
        try {
            raw.load(new StringReader(text));
        } catch (Exception ignore) {
        }

        Map<Locale, Properties> map = new HashMap<>();
        Properties root = new Properties();

        for (String fullKey : raw.stringPropertyNames()) {
            String val = raw.getProperty(fullKey);

            // No dot means no prefix -> ROOT
            int firstDot = fullKey.indexOf('.');
            if (firstDot <= 0) {
                root.put(fullKey, val);
                continue;
            }

            String maybePrefix = fullKey.substring(0, firstDot).trim();   // e.g.: "es", "es_AR", "**"
            String baseKey = fullKey.substring(firstDot + 1).trim();  // e.g.: "jpm.field.customization.style"

            // Explicit default case: **.key...
            if (DEFAULT_PREFIX.equals(maybePrefix)) {
                root.put(baseKey, val);
                continue;
            }

            // Is it a valid locale? (e.g.: es, en, pt, es_AR, zh_CN, etc.)
            Locale loc = tryParseLocalePrefix(maybePrefix);
            if (loc != null) {
                map.computeIfAbsent(loc, k -> new Properties()).put(baseKey, val);
            } else {
                // If the prefix is not a valid locale, we treat the whole key as "without prefix"
                root.put(fullKey, val);
            }
        }

        if (!root.isEmpty()) {
            map.put(Locale.ROOT, root);
        }
        return map;
    }

    /**
     * Returns a Locale if the prefix has a valid form (lang, lang_COUNTRY,
     * lang_COUNTRY_VARIANT), or null if it is not.
     */
    private static Locale tryParseLocalePrefix(String prefix) {
        // Accepts hyphen or underscore as separator
        String[] parts = prefix.split("[-_]");
        if (parts.length == 0) {
            return null;
        }

        String lang = parts[0];
        if (lang.isEmpty() || !lang.matches("^[a-zA-Z]{2,8}$")) {
            return null;
        }
        String country = (parts.length >= 2) ? parts[1] : "";
        String variant = (parts.length >= 3) ? parts[2] : "";

        // Normalize: language lowercase, country uppercase (Java standard)
        lang = lang.toLowerCase(Locale.ROOT);
        if (!country.isEmpty()) {
            country = country.toUpperCase(Locale.ROOT);
        }

        switch (parts.length) {
            case 1:
                return new Locale(lang);
            case 2:
                return new Locale(lang, country);
            default:
                return new Locale(lang, country, variant);
        }
    }

    private static Locale toLocale(String s) {
        String[] parts = s.split("[-_]");
        if (parts.length == 1) {
            return new Locale(parts[0]);
        }
        if (parts.length == 2) {
            return new Locale(parts[0], parts[1]);
        }
        return new Locale(parts[0], parts[1], parts[2]);
    }

    private static List<Locale> candidatesFor(Locale locale) {
        if (locale == null) {
            return Collections.singletonList(Locale.ROOT);
        }
        List<Locale> list = new ArrayList<>();
        // exact
        list.add(locale);
        // language only
        if (StringUtils.hasText(locale.getLanguage())) {
            list.add(new Locale(locale.getLanguage()));
        }
        // root (optional, if you loaded something like that)
        list.add(Locale.ROOT);
        return list;
    }

    public CustomizationService getCustomizationService() {
        return customizationService;
    }

    public void setCustomizationService(CustomizationService customizationService) {
        this.customizationService = customizationService;
    }
}
