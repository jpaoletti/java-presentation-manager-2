package jpaoletti.jpm2.core;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import jpaoletti.jpm2.core.converter.ClassConverter;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.PMSession;
import jpaoletti.jpm2.util.JPMUtils;

/**
 *
 * @author jpaoletti
 */
public class PresentationManager extends Observable {

    private static Long sessionIdSeed = 0L;
    protected String title;
    protected String subtitle;
    protected String appversion;
    protected String contact;
    protected String cssMode;
    //
    private CustomLoader customLoader;
    private List<Entity> entities;
    private List<Converter> converters;
    //private Map<Object, Monitor> monitors;
    private List<ClassConverter> classConverters;
    private final Map<String, PMSession> sessions = new HashMap<>();

    public PresentationManager() {
        this.title = "JPM2";
        this.subtitle = "Java Presentation Manager 2";
    }

    /**
     * Creates a new session with the given id. If null is used, an automatic
     * session id will be generated
     *
     * @param sessionId The new session id. Must be unique.
     * @throws PMException on already defined session
     * @return New session
     */
    public PMSession registerSession(String sessionId) {
        synchronized (sessions) {
            if (sessionId != null) {
                if (!sessions.containsKey(sessionId)) {
                    sessions.put(sessionId, new PMSession(sessionId));
                }
                return getSession(sessionId);
            } else {
                return registerSession(newSessionId());
            }
        }
    }

    public static synchronized String newSessionId() {
        sessionIdSeed++;
        final MD5 md5 = new MD5();
        return md5.calcMD5(sessionIdSeed.toString());
    }

    /**
     * Return the session for the given id
     *
     * @param sessionId The id of the wanted session
     * @return The session
     */
    public PMSession getSession(String sessionId) {
        final PMSession s = sessions.get(sessionId);
        if (s != null) {
            s.setLastAccess(new Date());
        }
        return s;
    }

    /**
     * Getter for the session map.
     *
     * @return Sessions
     */
    public Map<String, PMSession> getSessions() {
        return sessions;
    }

    /**
     * Removes the given id session
     */
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    protected void customLoad() throws Exception {
        if (getCustomLoader() != null) {
            getCustomLoader().execute(this);
        }
    }

    /**
     * Formatting helper for startup
     *
     * @param evt The event
     * @param s1 Text
     * @param s2 Extra description
     * @param symbol Status symbol
     */
    public void logItem(String s1, String s2, String symbol) {
        JPMUtils.getLogger().info(String.format("(%s) %-25s %s", symbol, s1, (s2 != null) ? s2 : ""));
    }

    private void error(Object error) {
        JPMUtils.getLogger().error(error);
    }

    /**
     * GETTERS & SETTERS
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCssMode() {
        return cssMode;
    }

    public void setCssMode(String cssMode) {
        this.cssMode = cssMode;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public List<Converter> getConverters() {
        return converters;
    }

    public void setConverters(List<Converter> converters) {
        this.converters = converters;
    }

    public CustomLoader getCustomLoader() {
        return customLoader;
    }

    public void setCustomLoader(CustomLoader customLoader) {
        this.customLoader = customLoader;
    }

    public List<ClassConverter> getClassConverters() {
        return classConverters;
    }

    public void setClassConverters(List<ClassConverter> classConverters) {
        this.classConverters = classConverters;
    }

    public Converter getClassConverter(String operation, String className) {
        for (ClassConverter classConverter : classConverters) {
            if (classConverter.getOperations().contains(operation) && classConverter.getClassName().equals(className)) {
                return classConverter.getConverter();
            }
        }
        return null;
    }
}
