package ncu.cc.activedirectory.models;

public enum ApiResultCodeEnum {
    SUCCESS(0, "Success"),
    USER_NOT_FOUND(10, "User not found"),
    PASSWORD_TOO_SHORT(20, "Password too short"),
    EXCEPTION(90, "Exception");


    private final int code;
    private final String message;

    ApiResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
