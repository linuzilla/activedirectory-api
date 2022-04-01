package ncu.cc.activedirectory.models;

public enum ApiLogEventEnum {
    NA, CREATE, PASSWORD, SUSPEND, RESUME, DELETE;

    private final String value;

    ApiLogEventEnum() {
        this.value = this.name().toLowerCase();
    }

    public String getValue() {
        return value;
    }
}
