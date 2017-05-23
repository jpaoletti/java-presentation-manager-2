package jpaoletti.jpm2.core.model;

/**
 * Operation scopes
 *
 * @author jpaoletti
 */
public enum OperationScope {

    GENERAL("general"),
    ITEM("item"),
    SELECTED("selected"), // a group of selected instances executed individually
    GROUPED("grouped"); //a group of selected instances executed together
    private String name;

    private OperationScope(String name) {
        this.name = name;
    }

    public boolean is(String name) {
        return getName().equals(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
