package com.mapuni.gdydcaiji.bean;

/**
 * Created by yf on 2018/3/23.
 */

public class UploadBean {

    /**
     * status : true
     * result : 处理成功
     */

    private boolean status;
    private String result;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
