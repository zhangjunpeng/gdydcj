package com.mapuni.gdydcaiji.bean;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TbSurface implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private Long id;

    @Id
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
    private String lds;

    @Expose
    private String img;

    private int flag; //是否已上传状态（1-> 已上传、0->未上传）

    @Generated(hash = 1646036237)
    public TbSurface(Long id, Long bm, String name, String xqdz, String fl,
            String wyxx, String lxdh, String polyarrays, String oprator,
            Date opttime, String deleteflag, Date createtime, String note,
            String lds, String img, int flag) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getXqdz() {
        return xqdz;
    }

    public void setXqdz(String xqdz) {
        this.xqdz = xqdz == null ? null : xqdz.trim();
    }

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl == null ? null : fl.trim();
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

    public String getPolyarrays() {
        return polyarrays;
    }

    public void setPolyarrays(String polyarrays) {
        this.polyarrays = polyarrays == null ? null : polyarrays.trim();
    }

    public String getOprator() {
        return oprator;
    }

    public void setOprator(String oprator) {
        this.oprator = oprator == null ? null : oprator.trim();
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
        this.deleteflag = deleteflag == null ? null : deleteflag.trim();
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
        this.note = note == null ? null : note.trim();
    }

    public String getLds() {
        return lds;
    }

    public void setLds(String lds) {
        this.lds = lds == null ? null : lds.trim();
    }

    public Long getBm() {
        return this.bm;
    }

    public void setBm(Long bm) {
        this.bm = bm;
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