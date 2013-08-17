package jpaoletti.jpm2.controller;

/**
 *
 * @author jpaoletti
 */
public class Login extends BaseAction {

    private String error;

    public String isError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
