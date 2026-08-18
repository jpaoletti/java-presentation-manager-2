package jpaoletti.jpm2.core.service;

import java.lang.reflect.Field;
import java.util.List;
import jpaoletti.jpm2.core.dao.DefaultJPADAO;
import jpaoletti.jpm2.core.mail.DummyMailSender;
import jpaoletti.jpm2.core.mail.MailSenderType;
import jpaoletti.jpm2.core.model.persistent.MailSender;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MailSenderServiceTest {

    @Test
    public void initialLoadOnlyMakesEnabledSendersAvailable() throws Exception {
        final MailSender enabled = sender("enabled", true);
        final MailSender disabled = sender("disabled", false);
        final DefaultJPADAO dao = mock(DefaultJPADAO.class);
        final SessionFactory sessionFactory = mock(SessionFactory.class);
        final Session session = mock(Session.class);
        when(dao.list(null)).thenReturn(List.of(enabled, disabled));
        when(sessionFactory.openSession()).thenReturn(session);

        final MailSenderService service = new MailSenderService();
        setField(service, "mailSenderDAO", dao);
        service.setSessionFactory(sessionFactory);
        service.init();

        assertInstanceOf(DummyMailSender.class, service.getCache("enabled"));
        assertNull(service.getCache("disabled"));
        verify(session).close();
    }

    @Test
    public void reloadRemovesSenderWhenItBecomesDisabled() throws Exception {
        final MailSender sender = sender("sender", true);
        final MailSenderService service = new MailSenderService();
        setField(service, "senders", new java.util.LinkedHashMap<>());

        service.reload(sender);
        assertInstanceOf(DummyMailSender.class, service.getCache("sender"));

        sender.setEnabled(false);
        service.reload(sender);
        assertNull(service.getCache("sender"));
    }

    private MailSender sender(String name, boolean enabled) {
        final MailSender sender = new MailSender();
        sender.setName(name);
        sender.setDescription(name);
        sender.setSenderType(MailSenderType.DUMMY);
        sender.setEnabled(enabled);
        return sender;
    }

    private void setField(Object target, String name, Object value) throws Exception {
        final Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
