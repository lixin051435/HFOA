package com.hfoa.common;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 自定义的响应结构
 * Created by xyc on 2018/4/11.
 * 结合JJWT使用
 */
public class AnccResult implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;

    private Object map;

    public static AnccResult build(Integer status, String msg, Object data) {
        return new AnccResult(status, msg, data);
    }

    public static AnccResult ok(Object data) {
        return new AnccResult(data);
    }

    public static AnccResult ok() {
        return new AnccResult(null);
    }

    public AnccResult() {

    }

    public static AnccResult build(Integer status, String msg) {
        return new AnccResult(status, msg, null);
    }

    public AnccResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public AnccResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }
    public AnccResult(Object data,Object map) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
        this.map = map;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Object是集合转化
     *
     * @param jsonData json数据
     * @param clazz 集合中的类型
     * @return
     */
    public static AnccResult formatToList(String jsonData, Class<?> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isArray() && data.size() > 0) {
                obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }
}
