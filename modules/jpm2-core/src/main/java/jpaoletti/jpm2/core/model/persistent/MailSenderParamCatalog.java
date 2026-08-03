package jpaoletti.jpm2.core.model.persistent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jpaoletti.jpm2.core.entityparam.EntityParameterDef;

/**
 * Fixed parameter catalog (kind {@code "mail-sender"}) for {@link MailSender}: the SMTP connection and message
 * settings the sender reads (see {@code SMTPMailSender#getMailConfig}). It is FLAT — there is a single mail
 * backend (SMTP) and its parameters do not vary by type — so there is no per-type composition. The
 * {@code password} is a secret (encrypted at rest, masked); the rest are plain, with the defaults the sender
 * uses in code. Types deduced; adjust as needed.
 *
 * @author jpaoletti
 */
public final class MailSenderParamCatalog {

    public static final String KIND = "mail-sender";

    private static final List<EntityParameterDef<?>> GENERAL = buildGeneral();

    private MailSenderParamCatalog() {
    }

    public static List<EntityParameterDef<?>> general() {
        return GENERAL;
    }

    private static List<EntityParameterDef<?>> buildGeneral() {
        final List<EntityParameterDef<?>> d = new ArrayList<>();
        d.add(EntityParameterDef.secret(KIND, "password").group("credentials").build());
        d.add(EntityParameterDef.string(KIND, "host").group("connection").defRaw("smtp.gmail.com").build());
        d.add(EntityParameterDef.integer(KIND, "port").group("connection").defRaw("587").build());
        d.add(EntityParameterDef.string(KIND, "user").group("connection").build());
        d.add(EntityParameterDef.bool(KIND, "auth").group("connection").defRaw("true").build());
        d.add(EntityParameterDef.bool(KIND, "ssl").group("connection").defRaw("false").build());
        d.add(EntityParameterDef.bool(KIND, "tls").group("connection").defRaw("true").build());
        d.add(EntityParameterDef.bool(KIND, "debug").group("connection").defRaw("true").build());
        d.add(EntityParameterDef.string(KIND, "from").group("message").build());
        d.add(EntityParameterDef.string(KIND, "from-name").group("message").build());
        d.add(EntityParameterDef.string(KIND, "reply-to").group("message").build());
        d.add(EntityParameterDef.string(KIND, "subject-prefix").group("message").build());
        return Collections.unmodifiableList(d);
    }
}
