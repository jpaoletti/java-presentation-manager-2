package jpaoletti.jpm2.core.mail;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MailManagerTest {

    private static final byte[] LOGO = {1, 2, 3, 4};

    @TempDir
    Path tempDir;

    @Test
    public void inlineAttachmentBuildsRelatedMessage() throws Exception {
        final Mail mail = new Mail(
                "Subject", "<html><img src=\"cid:logoEmpresa\"></html>", "to@example.com");
        mail.addInlineAttachment("logoEmpresa", "logo.jpg", "image/jpeg", LOGO);

        final MimeMessage message = buildMessage(mail);
        final Multipart related = multipart(message);

        assertTrue(message.isMimeType("multipart/related"));
        assertEquals(2, related.getCount());
        assertTrue(related.getBodyPart(0).isMimeType("text/html"));

        final BodyPart image = related.getBodyPart(1);
        assertTrue(image.isMimeType("image/jpeg"));
        assertEquals(Part.INLINE, image.getDisposition());
        assertEquals("logo.jpg", image.getFileName());
        assertEquals("<logoEmpresa>", image.getHeader("Content-ID")[0]);
        assertArrayEquals(LOGO, image.getInputStream().readAllBytes());
    }

    @Test
    public void textInlineAndFileAttachmentBuildNestedMultiparts() throws Exception {
        final Path attachment = tempDir.resolve("invoice.pdf");
        final byte[] pdf = {5, 6, 7};
        Files.write(attachment, pdf);

        final Mail mail = new Mail(
                "Subject", "<html><img src=\"cid:logoEmpresa\"></html>", "to@example.com");
        mail.setTextbody("Plain text");
        mail.addInlineAttachment("logoEmpresa", "logo.jpg", "image/jpeg", LOGO);
        mail.setAttachs(attachment.toFile());

        final MimeMessage message = buildMessage(mail);
        final Multipart mixed = multipart(message);

        assertTrue(message.isMimeType("multipart/mixed"));
        assertEquals(2, mixed.getCount());

        final Multipart alternative = multipart(mixed.getBodyPart(0));
        assertTrue(mixed.getBodyPart(0).isMimeType("multipart/alternative"));
        assertEquals(2, alternative.getCount());
        assertTrue(alternative.getBodyPart(0).isMimeType("text/plain"));

        final Multipart related = multipart(alternative.getBodyPart(1));
        assertTrue(alternative.getBodyPart(1).isMimeType("multipart/related"));
        assertEquals(2, related.getCount());
        assertTrue(related.getBodyPart(0).isMimeType("text/html"));
        assertEquals("<logoEmpresa>", related.getBodyPart(1).getHeader("Content-ID")[0]);

        final BodyPart filePart = mixed.getBodyPart(1);
        assertEquals(Part.ATTACHMENT, filePart.getDisposition());
        assertEquals("invoice.pdf", filePart.getFileName());
        assertArrayEquals(pdf, filePart.getInputStream().readAllBytes());
    }

    @Test
    public void regularAttachmentUsesMixedInsteadOfAlternative() throws Exception {
        final Path attachment = tempDir.resolve("document.txt");
        Files.writeString(attachment, "content");
        final Mail mail = new Mail("Subject", "<html>Body</html>", "to@example.com");
        mail.setAttachs(attachment.toFile());

        final MimeMessage message = buildMessage(mail);
        final Multipart mixed = multipart(message);

        assertTrue(message.isMimeType("multipart/mixed"));
        assertEquals(2, mixed.getCount());
        assertTrue(mixed.getBodyPart(0).isMimeType("text/html"));
        assertEquals(Part.ATTACHMENT, mixed.getBodyPart(1).getDisposition());
    }

    @Test
    public void duplicateContentIdIsRejectedIgnoringCase() {
        final Mail mail = new Mail("Subject", "Body", "to@example.com");
        mail.addInlineAttachment("logoEmpresa", "logo.jpg", "image/jpeg", LOGO);

        assertThrows(IllegalArgumentException.class, ()
                -> mail.addInlineAttachment("LOGOEMPRESA", "other.jpg", "image/jpeg", LOGO));
    }

    @Test
    public void inlineAttachmentRejectsUnsafeContentIdAndCopiesContent() {
        assertThrows(IllegalArgumentException.class, ()
                -> new InlineAttachment("logo\r\nHeader", "logo.jpg", "image/jpeg", LOGO));

        final byte[] content = {10, 20};
        final InlineAttachment attachment = new InlineAttachment(
                "logoEmpresa", "logo.jpg", "image/jpeg", content);
        content[0] = 99;
        final byte[] returnedContent = attachment.getContent();
        returnedContent[1] = 99;

        assertArrayEquals(new byte[]{10, 20}, attachment.getContent());
    }

    private MimeMessage buildMessage(Mail mail) throws Exception {
        final MailConfig config = new MailConfig();
        config.setFrom("from@example.com");
        config.setFromName("Sender");
        final MailManager manager = new MailManager(null, config);
        final Session session = Session.getInstance(new Properties());
        final MimeMessage message = manager.buildMessage(session, mail);
        message.saveChanges();
        assertEquals("to@example.com", ((jakarta.mail.internet.InternetAddress)
                message.getRecipients(Message.RecipientType.TO)[0]).getAddress());
        return message;
    }

    private Multipart multipart(Part part) throws Exception {
        return (Multipart) part.getContent();
    }
}
