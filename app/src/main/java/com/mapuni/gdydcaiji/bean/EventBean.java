package com.mapuni.gdydcaiji.bean;

/**
 * Created by oldJin on 2017/12/15.
 */

public class EventBean {
    public String beanStr;
    public int position;

    public EventBean(String beanStr) {
        this.beanStr = beanStr;
    }

    public EventBean(String beanStr, int position) {
        this.beanStr = beanStr;
        this.position = position;
    }
}
