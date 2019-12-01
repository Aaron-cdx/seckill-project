package org.seckill.dto;

/**
 * @author caoduanxi
 * @2019/12/1 9:56
 */
// 获取结果数据
public class SeckillExecutionResult<T> {
    // 是否成功
    private boolean success;
    // 数据
    private T data;
    // 失败的原因
    private String error;

    public SeckillExecutionResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillExecutionResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
