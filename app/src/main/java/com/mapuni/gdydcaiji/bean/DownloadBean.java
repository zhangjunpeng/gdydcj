package com.mapuni.gdydcaiji.bean;

import java.util.List;

/**
 * Created by yf on 2018/4/12.
 */

public class DownloadBean {


    private List<TbPoint> tb_point;
    private List<TbLine> tb_line;
    private List<TbSurface> tb_surface;

    public List<TbPoint> getTb_point() {
        return tb_point;
    }

    public void setTb_point(List<TbPoint> tb_point) {
        this.tb_point = tb_point;
    }

    public List<TbLine> getTb_line() {
        return tb_line;
    }

    public void setTb_line(List<TbLine> tb_line) {
        this.tb_line = tb_line;
    }

    public List<TbSurface> getTb_surface() {
        return tb_surface;
    }

    public void setTb_surface(List<TbSurface> tb_surface) {
        this.tb_surface = tb_surface;
    }
    
}
