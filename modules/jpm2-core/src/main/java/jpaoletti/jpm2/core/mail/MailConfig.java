package jpaoletti.jpm2.core.mail;

/**
 * Transient model of a mail configuration.
 *
 * @author jpaoletti
 */
public class MailConfig {

    private boolean debug = false;
    private boolean auth = false;
    private boolean ssl = false;
    private String user;
    private String replyTo;
    private String password;
    private String host = "127.0.0.1";
    private String port = "25";
    private String tlsEnabled = "false";
    private String tlsVersion;
    private String from;
    private String fromName;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getTlsEnabled() {
        return tlsEnabled;
    }

    public void setTlsEnabled(String tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String getTlsVersion() {
        return tlsVersion;
    }

    public void setTlsVersion(String tlsVersion) {
        this.tlsVersion = tlsVersion;
    }
}
