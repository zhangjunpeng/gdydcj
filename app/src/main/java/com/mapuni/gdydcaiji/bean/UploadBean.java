package com.mapuni.gdydcaiji.bean;

/**
 * Created by yf on 2018/3/23.
 */

public class UploadBean {

    /**
     * result : true
     */

    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "UploadBean{" +
                "result=" + result +
                '}';
    }
}
