package jpaoletti.jpm2.core.mail;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class MailSenderTypeTest {

    @Test
    public void dummyBuildsDummyMailSender() {
        assertInstanceOf(DummyMailSender.class, MailSenderType.DUMMY.build(Map.of()));
    }
}
