package ncu.cc.activedirectory.models;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ApiResult {
    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object result;

    public ApiResult(ApiResultCodeEnum apiResultCodeEnum) {
        this.code = apiResultCodeEnum.getCode();
        this.message = apiResultCodeEnum.getMessage();
    }

    public ApiResult(ApiResultCodeEnum apiResultCodeEnum, String message) {
        this.code = apiResultCodeEnum.getCode();
        this.message = message;
    }

    public ApiResult(Throwable e) {
        this.code = ApiResultCodeEnum.EXCEPTION.getCode();
        this.message = e.getMessage();
    }

    public ApiResult() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
