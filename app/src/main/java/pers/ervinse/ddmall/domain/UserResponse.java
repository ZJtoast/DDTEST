package pers.ervinse.ddmall.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserResponse<T> implements Response{
    private Integer code;
    private String message;
    private T data;
    UserResponse()
    {}
    UserResponse(Integer code, String message,T data)
    {
        this.code=code;
        this.message=message;
        this.data=data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
