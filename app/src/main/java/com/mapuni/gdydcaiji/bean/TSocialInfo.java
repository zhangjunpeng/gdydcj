package com.mapuni.gdydcaiji.bean;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 小区、学校、医院专题数据采集     不全
 * @author meiak
 *
 */
@Entity
public class TSocialInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    private String bm;

    private String csbs;

    private String qxbs;

    private String xzbs;

    private String gsjd;

    private String xqid;

    @Expose
    private String name;

    private String firstad;

    private String secondad;

    private String thirdad;

    @Expose
    private String forthad;

    @Expose
    private String fifad;

    @Expose
    private String xqdz;

    private Double x;

    private Double y;

    private Date gxsj;

    @Expose
    private String wyxx;

    @Expose
    private String lxdh;

    private String glgx;

    private Double xqmj;

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

    private String lysl;

    private String zhs;

    private String lds;

    @Expose
    private String bj;

    @Expose
    private String img;

    private int flag; //是否已上传状态（1-> 已上传、0->未上传）

    @Generated(hash = 1485449167)
    public TSocialInfo(Long id, String bm, String csbs, String qxbs, String xzbs,
            String gsjd, String xqid, String name, String firstad, String secondad,
            String thirdad, String forthad, String fifad, String xqdz, Double x,
            Double y, Date gxsj, String wyxx, String lxdh, String glgx, Double xqmj,
            String optuser, Double lat, Double lng, Date opttime, String type,
            String lysl, String zhs, String lds, String bj, String img, int flag) {
        this.id = id;
        this.bm = bm;
        this.csbs = csbs;
        this.qxbs = qxbs;
        this.xzbs = xzbs;
        this.gsjd = gsjd;
        this.xqid = xqid;
        this.name = name;
        this.firstad = firstad;
        this.secondad = secondad;
        this.thirdad = thirdad;
        this.forthad = forthad;
        this.fifad = fifad;
        this.xqdz = xqdz;
        this.x = x;
        this.y = y;
        this.gxsj = gxsj;
        this.wyxx = wyxx;
        this.lxdh = lxdh;
        this.glgx = glgx;
        this.xqmj = xqmj;
        this.optuser = optuser;
        this.lat = lat;
        this.lng = lng;
        this.opttime = opttime;
        this.type = type;
        this.lysl = lysl;
        this.zhs = zhs;
        this.lds = lds;
        this.bj = bj;
        this.img = img;
        this.flag = flag;
    }

    @Generated(hash = 282476677)
    public TSocialInfo() {
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

    public String getXqid() {
        return xqid;
    }

    public void setXqid(String xqid) {
        this.xqid = xqid == null ? null : xqid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getFirstad() {
        return firstad;
    }

    public void setFirstad(String firstad) {
        this.firstad = firstad == null ? null : firstad.trim();
    }

    public String getSecondad() {
        return secondad;
    }

    public void setSecondad(String secondad) {
        this.secondad = secondad == null ? null : secondad.trim();
    }

    public String getThirdad() {
        return thirdad;
    }

    public void setThirdad(String thirdad) {
        this.thirdad = thirdad == null ? null : thirdad.trim();
    }

    public String getForthad() {
        return forthad;
    }

    public void setForthad(String forthad) {
        this.forthad = forthad == null ? null : forthad.trim();
    }

    public String getFifad() {
        return fifad;
    }

    public void setFifad(String fifad) {
        this.fifad = fifad == null ? null : fifad.trim();
    }

    public String getXqdz() {
        return xqdz;
    }

    public void setXqdz(String xqdz) {
        this.xqdz = xqdz == null ? null : xqdz.trim();
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

    public Date getGxsj() {
        return gxsj;
    }

    public void setGxsj(Date gxsj) {
        this.gxsj = gxsj;
    }

    public String getWyxx() {
        return wyxx;
    }

    public void setWyxx(String wyxx) {
        this.wyxx = wyxx == null ? null : wyxx.trim();
    }

    public String getLxdh() {
        return lxdh;
    }

    public void setLxdh(String lxdh) {
        this.lxdh = lxdh == null ? null : lxdh.trim();
    }

    public String getGlgx() {
        return glgx;
    }

    public void setGlgx(String glgx) {
        this.glgx = glgx == null ? null : glgx.trim();
    }

    public Double getXqmj() {
        return xqmj;
    }

    public void setXqmj(Double xqmj) {
        this.xqmj = xqmj;
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

    public String getLysl() {
        return lysl;
    }

    public void setLysl(String lysl) {
        this.lysl = lysl;
    }

    public String getZhs() {
        return zhs;
    }

    public void setZhs(String zhs) {
        this.zhs = zhs;
    }

    public String getLds() {
        return lds;
    }

    public void setLds(String lds) {
        this.lds = lds;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getBj() {
        return this.bj;
    }

    public void setBj(String bj) {
        this.bj = bj;
    }
}