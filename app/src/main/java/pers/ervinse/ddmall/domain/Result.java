package pers.ervinse.ddmall.domain;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Result<T> implements Response {
    private Integer code;
    private String message;
    private T data;

    Result() {
    }

    Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 将上述的Json串转化为对象
     */
    public static <V> Result<V> parseResultValue(String json) {
        return JSONObject.parseObject(json, new TypeReference<Result<V>>() {
        });
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
    //应当以如下代码进行嵌套数据传输
    //  Result<List<Medicine>> medicineResponse = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Medicine>>>() {
    //                    });

}
