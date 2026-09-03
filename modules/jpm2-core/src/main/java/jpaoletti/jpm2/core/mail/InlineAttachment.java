package jpaoletti.jpm2.core.mail;

import java.util.Arrays;

/**
 * Binary resource embedded in an HTML mail and referenced through a CID URL.
 *
 * @author jpaoletti
 */
public final class InlineAttachment {

    private final String contentId;
    private final String filename;
    private final String contentType;
    private final byte[] content;

    public InlineAttachment(String contentId, String filename, String contentType, byte[] content) {
        this.contentId = requireHeaderValue(contentId, "contentId", true);
        this.filename = requireHeaderValue(filename, "filename", false);
        this.contentType = requireHeaderValue(contentType, "contentType", false);
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("content must not be empty");
        }
        this.content = Arrays.copyOf(content, content.length);
    }

    private static String requireHeaderValue(String value, String name, boolean contentId) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        if (value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0
                || contentId && (value.indexOf('<') >= 0 || value.indexOf('>') >= 0
                || value.chars().anyMatch(Character::isWhitespace))) {
            throw new IllegalArgumentException("Invalid " + name);
        }
        return value;
    }

    public String getContentId() {
        return contentId;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }
}
