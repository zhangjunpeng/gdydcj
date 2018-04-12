package com.mapuni.gdydcaiji.bean;

import com.google.gson.annotations.Expose;
import com.mapuni.gdydcaiji.utils.DateUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TbPoint implements Serializable{
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
    private String authcontent;//质检内容

    @Expose
    private String authflag;//0->未质检

    @Expose
    private String lyzhs;

    @Expose
    private String img;

    private int flag; //是否已上传状态（1-> 已上传、0->未上传）

    @Generated(hash = 1558216315)
    public TbPoint(Long id, Long bm, String lytype, String lyxz, String name,
            String fl, String dz, String dy, String lxdh, String dj, String lycs,
            Double lng, Double lat, String oprator, Date opttime, String deleteflag,
            Date createtime, String note, String authcontent, String authflag,
            String lyzhs, String img, int flag) {
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
    }

    @Generated(hash = 1467713832)
    public TbPoint() {
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBm() {
        return bm;
    }

    public void setBm(Long bm) {
        this.bm = bm;
    }

    public String getLytype() {
        return lytype;
    }

    public void setLytype(String lytype) {
        this.lytype = lytype;
    }

    public String getLyxz() {
        return lyxz;
    }

    public void setLyxz(String lyxz) {
        this.lyxz = lyxz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl;
    }

    public String getDz() {
        return dz;
    }

    public void setDz(String dz) {
        this.dz = dz;
    }

    public String getDy() {
        return dy;
    }

    public void setDy(String dy) {
        this.dy = dy;
    }

    public String getLxdh() {
        return lxdh;
    }

    public void setLxdh(String lxdh) {
        this.lxdh = lxdh;
    }

    public String getDj() {
        return dj;
    }

    public void setDj(String dj) {
        this.dj = dj;
    }

    public String getLycs() {
        return lycs;
    }

    public void setLycs(String lycs) {
        this.lycs = lycs;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getOprator() {
        return oprator;
    }

    public void setOprator(String oprator) {
        this.oprator = oprator;
    }

    public Date getOpttime() {
        return opttime;
    }

    public void setOpttime(Date opttime) {
        this.opttime = opttime;
    }

    public String getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(String deleteflag) {
        this.deleteflag = deleteflag;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = DateUtil.getDateByFormat(createtime,DateUtil.YMDHMS);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLyzhs() {
        return lyzhs;
    }

    public void setLyzhs(String lyzhs) {
        this.lyzhs = lyzhs;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
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

    @Override
    public String toString() {
        return "TbPoint{" +
                "id=" + id +
                ", bm=" + bm +
                ", lytype='" + lytype + '\'' +
                ", lyxz='" + lyxz + '\'' +
                ", name='" + name + '\'' +
                ", fl='" + fl + '\'' +
                ", dz='" + dz + '\'' +
                ", dy='" + dy + '\'' +
                ", lxdh='" + lxdh + '\'' +
                ", dj='" + dj + '\'' +
                ", lycs='" + lycs + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", oprator='" + oprator + '\'' +
                ", opttime=" + opttime +
                ", deleteflag='" + deleteflag + '\'' +
                ", createtime=" + createtime +
                ", note='" + note + '\'' +
                ", authcontent='" + authcontent + '\'' +
                ", authflag='" + authflag + '\'' +
                ", lyzhs='" + lyzhs + '\'' +
                ", img='" + img + '\'' +
                ", flag=" + flag +
                '}';
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}