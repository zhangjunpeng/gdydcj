package com.mapuni.gdydcaiji.bean;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TbLine implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Id
    @Expose
    private Long bm;

    @Expose
    private String name;

    @Expose
    private String sfz;

    @Expose
    private String zdz;

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
    private String img;

    private int flag; //是否已上传状态（1-> 已上传、0->新增未上传,2->修改未上传）

    @Generated(hash = 1320395952)
    public TbLine(Long id, Long bm, String name, String sfz, String zdz,
                  String polyarrays, String oprator, Date opttime, String deleteflag,
                  Date createtime, String note, String authcontent, String authflag,
                  String img, int flag) {
        this.id = id;
        this.bm = bm;
        this.name = name;
        this.sfz = sfz;
        this.zdz = zdz;
        this.polyarrays = polyarrays;
        this.oprator = oprator;
        this.opttime = opttime;
        this.deleteflag = deleteflag;
        this.createtime = createtime;
        this.note = note;
        this.authcontent = authcontent;
        this.authflag = authflag;
        this.img = img;
        this.flag = flag;
    }

    @Generated(hash = 2053601648)
    public TbLine() {
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

    public String getSfz() {
        return sfz;
    }

    public void setSfz(String sfz) {
        this.sfz = sfz;
    }

    public String getZdz() {
        return zdz;
    }

    public void setZdz(String zdz) {
        this.zdz = zdz;
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
}