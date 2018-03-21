package com.mapuni.gdydcaiji.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 行政村、自然村点采集
 * @author meiak
 *
 */
@Entity
public class TVillageInfo {
    @Id
    private Long id;
    
    private String bm;

    private String csbs;

    private String qxbs;

    private String xzbs;

    private String gsjd;

    private String gsxzc;

    private String name;

    private String dz;

    private Double x;

    private Double y;

    private Double zrcbj;

    private Date gxsj;

    private Double czmj;

    private String optuser;

    private Double lat;

    private Double lng;

    private Date opttime;

    private String type;

    private String img;

    @Generated(hash = 1434740945)
    public TVillageInfo(Long id, String bm, String csbs, String qxbs, String xzbs,
            String gsjd, String gsxzc, String name, String dz, Double x, Double y,
            Double zrcbj, Date gxsj, Double czmj, String optuser, Double lat,
            Double lng, Date opttime, String type, String img) {
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
    }

    @Generated(hash = 439369900)
    public TVillageInfo() {
    }

    public String getBm() {
        return bm;
    }

    public void setBm(String bm) {
        this.bm = bm == null ? null : bm.trim();
    }

    public String getCsbs() {
        return csbs;
    }

    public void setCsbs(String csbs) {
        this.csbs = csbs == null ? null : csbs.trim();
    }

    public String getQxbs() {
        return qxbs;
    }

    public void setQxbs(String qxbs) {
        this.qxbs = qxbs == null ? null : qxbs.trim();
    }

    public String getXzbs() {
        return xzbs;
    }

    public void setXzbs(String xzbs) {
        this.xzbs = xzbs == null ? null : xzbs.trim();
    }

    public String getGsjd() {
        return gsjd;
    }

    public void setGsjd(String gsjd) {
        this.gsjd = gsjd == null ? null : gsjd.trim();
    }

    public String getGsxzc() {
        return gsxzc;
    }

    public void setGsxzc(String gsxzc) {
        this.gsxzc = gsxzc == null ? null : gsxzc.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDz() {
        return dz;
    }

    public void setDz(String dz) {
        this.dz = dz == null ? null : dz.trim();
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZrcbj() {
        return zrcbj;
    }

    public void setZrcbj(Double zrcbj) {
        this.zrcbj = zrcbj;
    }

    public Date getGxsj() {
        return gxsj;
    }

    public void setGxsj(Date gxsj) {
        this.gxsj = gxsj;
    }

    public Double getCzmj() {
        return czmj;
    }

    public void setCzmj(Double czmj) {
        this.czmj = czmj;
    }

    public String getOptuser() {
        return optuser;
    }

    public void setOptuser(String optuser) {
        this.optuser = optuser == null ? null : optuser.trim();
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Date getOpttime() {
        return opttime;
    }

    public void setOpttime(Date opttime) {
        this.opttime = opttime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}