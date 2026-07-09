package jpaoletti.jpm2.core.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import org.apache.james.jdkim.api.Headers;

/**
 * An adapter to let DKIMSigner read headers from MimeMessage
 */
final class MimeMessageHeaders /*implements Headers */{
/*
    private final Map<String, List<String>> headers;
    private final List<String> fields;

    @SuppressWarnings("unchecked")
    public MimeMessageHeaders(MimeMessage message)
            throws MessagingException {
        headers = new HashMap<>();
        fields = new LinkedList<>();
        for (Enumeration<String> e = message.getAllHeaderLines(); e
                .hasMoreElements();) {
            String head = e.nextElement();
            int p = head.indexOf(':');
            if (p <= 0) {
                throw new MessagingException("Bad header line: " + head);
            }
            String headerName = head.substring(0, p).trim();
            String headerNameLC = headerName.toLowerCase();
            fields.add(headerName);
            List<String> strings = headers.get(headerNameLC);
            if (strings == null) {
                strings = new LinkedList<>();
                headers.put(headerNameLC, strings);
            }
            strings.add(head);
        }
    }

    @Override
    public List<String> getFields() {
        return fields;
    }

    @Override
    public List<String> getFields(String name) {
        return headers.get(name.toLowerCase());
    }*/
}
