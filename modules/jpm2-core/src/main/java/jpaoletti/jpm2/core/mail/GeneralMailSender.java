package jpaoletti.jpm2.core.mail;

import java.util.Map;

/**
 * Base class for a configurable mail sender built from a parameter map.
 *
 * @author jpaoletti
 */
public abstract class GeneralMailSender extends BaseWithParameters {

    public GeneralMailSender(Map<String, String> parameters) {
        super(parameters);
    }

    public abstract void send(Mail mail);

    /**
     * Sends a mail synchronously, propagating any failure to the caller (unlike
     * {@link #send(Mail)}, which is asynchronous and only logs errors). Meant for
     * interactive operations that must report a real success/failure to the user.
     */
    public abstract void sendSync(Mail mail) throws Exception;

}
