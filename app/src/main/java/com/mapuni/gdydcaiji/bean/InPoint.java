package com.mapuni.gdydcaiji.bean;

import com.google.gson.annotations.Expose;
import com.mapuni.gdydcaiji.utils.DateUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Entity
public class InPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Id
    @Expose
    private Long bm;

    @Expose
    private String lytype;

    @Expose
    private String lyxz;

    @Expose
    private String name;

    @Expose
    private String fl;

    @Expose
    private String dz;

    @Expose
    private String dy;

    @Expose
    private String lxdh;

    @Expose
    private String dj;

    @Expose
    private String lycs;

    @Expose
    private Double lng;

    @Expose
    private Double lat;

    @Expose
    private String oprator;

    @Expose
    private Date opttime;

    private String deleteflag;

    private Date createtime;

    @Expose
    private String note;

    @Expose
    private String authcontent = "";//质检内容

    @Expose
    private String authflag = "0";//0->未质检

    @Expose
    private String lyzhs;

    @Expose
    private String img;

    private int flag; //是否已上传状态（1-> 已上传、0->新增未上传,2->修改未上传）

    @Expose
    private String homearea;

    @Generated(hash = 1917959841)
    public InPoint(Long id, Long bm, String lytype, String lyxz, String name,
            String fl, String dz, String dy, String lxdh, String dj, String lycs,
            Double lng, Double lat, String oprator, Date opttime, String deleteflag,
            Date createtime, String note, String authcontent, String authflag,
            String lyzhs, String img, int flag, String homearea) {
        this.id = id;
        this.bm = bm;
        this.lytype = lytype;
        this.lyxz = lyxz;
        this.name = name;
        this.fl = fl;
        this.dz = dz;
        this.dy = dy;
        this.lxdh = lxdh;
        this.dj = dj;
        this.lycs = lycs;
        this.lng = lng;
        this.lat = lat;
        this.oprator = oprator;
        this.opttime = opttime;
        this.deleteflag = deleteflag;
        this.createtime = createtime;
        this.note = note;
        this.authcontent = authcontent;
        this.authflag = authflag;
        this.lyzhs = lyzhs;
        this.img = img;
        this.flag = flag;
        this.homearea = homearea;
    }

    @Generated(hash = 228514815)
    public InPoint() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBm() {
        return this.bm;
    }

    public void setBm(Long bm) {
        this.bm = bm;
    }

    public String getLytype() {
        return this.lytype;
    }

    public void setLytype(String lytype) {
        this.lytype = lytype;
    }

    public String getLyxz() {
        return this.lyxz;
    }

    public void setLyxz(String lyxz) {
        this.lyxz = lyxz;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFl() {
        return this.fl;
    }

    public void setFl(String fl) {
        this.fl = fl;
    }

    public String getDz() {
        return this.dz;
    }

    public void setDz(String dz) {
        this.dz = dz;
    }

    public String getDy() {
        return this.dy;
    }

    public void setDy(String dy) {
        this.dy = dy;
    }

    public String getLxdh() {
        return this.lxdh;
    }

    public void setLxdh(String lxdh) {
        this.lxdh = lxdh;
    }

    public String getDj() {
        return this.dj;
    }

    public void setDj(String dj) {
        this.dj = dj;
    }

    public String getLycs() {
        return this.lycs;
    }

    public void setLycs(String lycs) {
        this.lycs = lycs;
    }

    public Double getLng() {
        return this.lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getOprator() {
        return this.oprator;
    }

    public void setOprator(String oprator) {
        this.oprator = oprator;
    }

    public Date getOpttime() {
        return this.opttime;
    }

    public void setOpttime(Date opttime) {
        this.opttime = opttime;
    }

    public String getDeleteflag() {
        return this.deleteflag;
    }

    public void setDeleteflag(String deleteflag) {
        this.deleteflag = deleteflag;
    }

    public Date getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAuthcontent() {
        return this.authcontent;
    }

    public void setAuthcontent(String authcontent) {
        this.authcontent = authcontent;
    }

    public String getAuthflag() {
        return this.authflag;
    }

    public void setAuthflag(String authflag) {
        this.authflag = authflag;
    }

    public String getLyzhs() {
        return this.lyzhs;
    }

    public void setLyzhs(String lyzhs) {
        this.lyzhs = lyzhs;
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

    public String getHomearea() {
        return this.homearea;
    }

    public void setHomearea(String homearea) {
        this.homearea = homearea;
    }
    
}