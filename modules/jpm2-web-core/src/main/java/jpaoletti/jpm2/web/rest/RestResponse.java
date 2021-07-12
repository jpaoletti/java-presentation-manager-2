package jpaoletti.jpm2.web.rest;

/**
 *
 * @author jpaoletti
 */
public class RestResponse {

    private final boolean ok;
    private final String message;

    public RestResponse(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }
}
