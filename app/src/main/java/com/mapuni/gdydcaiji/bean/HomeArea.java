package com.mapuni.gdydcaiji.bean;

import java.util.List;

/**
 * Created by yf on 2018/6/6.
 */

public class HomeArea {

    private List<HomeAreaBean> data;

    public List<HomeAreaBean> getData() {
        return data;
    }

    public void setData(List<HomeAreaBean> data) {
        this.data = data;
    }

    public static class HomeAreaBean {
        /**
         * homearea : 归属区2
         */

        private String homearea;

        public String getHomearea() {
            return homearea;
        }

        public void setHomearea(String homearea) {
            this.homearea = homearea;
        }
    }
}
