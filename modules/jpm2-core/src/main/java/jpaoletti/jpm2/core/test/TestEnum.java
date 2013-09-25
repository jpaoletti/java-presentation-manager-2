package jpaoletti.jpm2.core.test;

/**
 *
 * @author jpaoletti
 */
public enum TestEnum {

    CASE1("This is case1"), CASE2("This is case2"), CASE3("This is case3");
    private String description;

    private TestEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
