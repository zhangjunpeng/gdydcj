package com.mapuni.gdydcaiji.bean;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 行政村、自然村点采集
 *
 * @author meiak
 */
@Entity
public class TVillageInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    private String bm;

    private String csbs;

    private String qxbs;

    private String xzbs;

    private String gsjd;

    private String gsxzc;

    @Expose
    private String name;

    @Expose
    private String dz;

    private Double x;

    private Double y;

    @Expose
    private String zrcbj;

    private Date gxsj;

    private Double czmj;

    @Expose
    private String optuser;

    @Expose
    private Double lat;

    @Expose
    private Double lng;

    @Expose
    private Date opttime;

    @Expose
    private String type;

    @Expose
    private String img;

    private int flag; //是否已上传状态（1-> 已上传、0->未上传）

    @Generated(hash = 586932351)
    public TVillageInfo(Long id, String bm, String csbs, String qxbs, String xzbs,
            String gsjd, String gsxzc, String name, String dz, Double x, Double y,
            String zrcbj, Date gxsj, Double czmj, String optuser, Double lat,
            Double lng, Date opttime, String type, String img, int flag) {
        this.id = id;
        this.bm = bm;
        this.csbs = csbs;
        this.qxbs = qxbs;
        this.xzbs = xzbs;
        this.gsjd = gsjd;
        this.gsxzc = gsxzc;
        this.name = name;
        this.dz = dz;
        this.x = x;
        this.y = y;
        this.zrcbj = zrcbj;
        this.gxsj = gxsj;
        this.czmj = czmj;
        this.optuser = optuser;
        this.lat = lat;
        this.lng = lng;
        this.opttime = opttime;
        this.type = type;
        this.img = img;
        this.flag = flag;
    }

    @Generated(hash = 439369900)
    public TVillageInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBm() {
        return this.bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public String getCsbs() {
        return this.csbs;
    }

    public void setCsbs(String csbs) {
        this.csbs = csbs;
    }

    public String getQxbs() {
        return this.qxbs;
    }

    public void setQxbs(String qxbs) {
        this.qxbs = qxbs;
    }

    public String getXzbs() {
        return this.xzbs;
    }

    public void setXzbs(String xzbs) {
        this.xzbs = xzbs;
    }

    public String getGsjd() {
        return this.gsjd;
    }

    public void setGsjd(String gsjd) {
        this.gsjd = gsjd;
    }

    public String getGsxzc() {
        return this.gsxzc;
    }

    public void setGsxzc(String gsxzc) {
        this.gsxzc = gsxzc;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDz() {
        return this.dz;
    }

    public void setDz(String dz) {
        this.dz = dz;
    }

    public Double getX() {
        return this.x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return this.y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String getZrcbj() {
        return this.zrcbj;
    }

    public void setZrcbj(String zrcbj) {
        this.zrcbj = zrcbj;
    }

    public Date getGxsj() {
        return this.gxsj;
    }

    public void setGxsj(Date gxsj) {
        this.gxsj = gxsj;
    }

    public Double getCzmj() {
        return this.czmj;
    }

    public void setCzmj(Double czmj) {
        this.czmj = czmj;
    }

    public String getOptuser() {
        return this.optuser;
    }

    public void setOptuser(String optuser) {
        this.optuser = optuser;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return this.lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Date getOpttime() {
        return this.opttime;
    }

    public void setOpttime(Date opttime) {
        this.opttime = opttime;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

}