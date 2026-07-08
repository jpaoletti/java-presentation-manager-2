package jpaoletti.jpm2.core.model.persistent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * System customization (logos, favicon, styles and DB-backed i18n messages).
 *
 * To enable it in an app add to spring-jpm: &lt;import resource="classpath:entities/customization.xml"/&gt;
 * and register the annotatedClass jpaoletti.jpm2.core.model.persistent.Customization in the sessionFactory.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "customization")
public class Customization extends JPMPersistentObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 5000, name = "login_style") // TODO change database name
    private String style;

    @Column(name = "login_page") // TODO change database name
    private String loginPage;

    @Lob
    @Column(columnDefinition = "LONGBLOB", name = "login_logo")
    private byte[] loginLogo;

    @Lob
    @Column(columnDefinition = "LONGBLOB", name = "logo")
    private byte[] logo;

    @Lob
    @Column(columnDefinition = "LONGBLOB", name = "favicon")
    private byte[] favicon;

    @Column(columnDefinition = "LONGTEXT")
    private String messages;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public byte[] getLoginLogo() {
        return loginLogo;
    }

    public void setLoginLogo(byte[] loginLogo) {
        this.loginLogo = loginLogo;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public byte[] getFavicon() {
        return favicon;
    }

    public void setFavicon(byte[] favicon) {
        this.favicon = favicon;
    }

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Personalizaci&oacute;n del sistema";
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof Customization;
    }

}
