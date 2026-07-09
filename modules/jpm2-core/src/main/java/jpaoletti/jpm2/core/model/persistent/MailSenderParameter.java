package jpaoletti.jpm2.core.model.persistent;

import jpaoletti.jpm2.core.model.Duplicable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A single name/value parameter belonging to a {@link MailSender}.
 *
 * @author jpaoletti
 */
@Entity
@Table(name = "mail_senders_parameteres")
public class MailSenderParameter extends JPMPersistentObject implements Duplicable {

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String value;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender")
    private MailSender sender;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MailSender getSender() {
        return sender;
    }

    public void setSender(MailSender sender) {
        this.sender = sender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected boolean isValidClass(Object obj) {
        return obj instanceof MailSenderParameter;
    }

    @Override
    public Duplicable duplicate() {
        final MailSenderParameter res = new MailSenderParameter();
        res.setName(name);
        res.setValue(value);
        return res;
    }

}
