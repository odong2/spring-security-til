package io.security.basicsecurity.security.enums;

public enum SecurityMethodType {

    METHOD("method"),
    POINTCUT("pointcut");

    private String name;

    SecurityMethodType(String name) {
        this.name = name;
    }

    public String getValue() {
        return name;
    }
}
