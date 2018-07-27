package com.mapuni.gdydcaiji.bean;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TbSurface implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Id
    @Expose
    private Long bm;

    @Expose
    private String name;

    @Expose
    private String xqdz;

    @Expose
    private String fl;

    @Expose
    private String wyxx;

    @Expose
    private String lxdh;

    @Expose
    private String polyarrays;

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
    private String lds;

    @Expose
    private String img;

    private int flag; //是否已上传状态（1-> 已上传、0->新增未上传,2->修改未上传）

    @Generated(hash = 414289962)
    public TbSurface(Long id, Long bm, String name, String xqdz, String fl,
                     String wyxx, String lxdh, String polyarrays, String oprator,
                     Date opttime, String deleteflag, Date createtime, String note,
                     String authcontent, String authflag, String lds, String img, int flag) {
        this.id = id;
        this.bm = bm;
        this.name = name;
        this.xqdz = xqdz;
        this.fl = fl;
        this.wyxx = wyxx;
        this.lxdh = lxdh;
        this.polyarrays = polyarrays;
        this.oprator = oprator;
        this.opttime = opttime;
        this.deleteflag = deleteflag;
        this.createtime = createtime;
        this.note = note;
        this.authcontent = authcontent;
        this.authflag = authflag;
        this.lds = lds;
        this.img = img;
        this.flag = flag;
    }

    @Generated(hash = 806237974)
    public TbSurface() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXqdz() {
        return xqdz;
    }

    public void setXqdz(String xqdz) {
        this.xqdz = xqdz;
    }

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl;
    }

    public String getWyxx() {
        return wyxx;
    }

    public void setWyxx(String wyxx) {
        this.wyxx = wyxx;
    }

    public String getLxdh() {
        return lxdh;
    }

    public void setLxdh(String lxdh) {
        this.lxdh = lxdh;
    }

    public String getPolyarrays() {
        return polyarrays;
    }

    public void setPolyarrays(String polyarrays) {
        this.polyarrays = polyarrays;
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

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
        return "TbSurface{" +
                "id=" + id +
                ", bm=" + bm +
                ", name='" + name + '\'' +
                ", xqdz='" + xqdz + '\'' +
                ", fl='" + fl + '\'' +
                ", wyxx='" + wyxx + '\'' +
                ", lxdh='" + lxdh + '\'' +
                ", polyarrays='" + polyarrays + '\'' +
                ", oprator='" + oprator + '\'' +
                ", opttime=" + opttime +
                ", deleteflag='" + deleteflag + '\'' +
                ", createtime=" + createtime +
                ", note='" + note + '\'' +
                ", authcontent='" + authcontent + '\'' +
                ", authflag='" + authflag + '\'' +
                ", lds='" + lds + '\'' +
                ", img='" + img + '\'' +
                ", flag=" + flag +
                '}';
    }
}