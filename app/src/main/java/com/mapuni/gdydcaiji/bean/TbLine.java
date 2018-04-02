package com.mapuni.gdydcaiji.bean;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TbLine implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private Long id;

    @Id
    private Long bm;

    @Expose
    private String pathname;

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
    private String img;

    private int flag; //是否已上传状态（1-> 已上传、0->未上传）

    @Generated(hash = 23816848)
    public TbLine(Long id, Long bm, String pathname, String sfz, String zdz,
            String polyarrays, String oprator, Date opttime, String deleteflag,
            Date createtime, String note, String img, int flag) {
        this.id = id;
        this.bm = bm;
        this.pathname = pathname;
        this.sfz = sfz;
        this.zdz = zdz;
        this.polyarrays = polyarrays;
        this.oprator = oprator;
        this.opttime = opttime;
        this.deleteflag = deleteflag;
        this.createtime = createtime;
        this.note = note;
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
    

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname == null ? null : pathname.trim();
    }

    public String getSfz() {
        return sfz;
    }

    public void setSfz(String sfz) {
        this.sfz = sfz == null ? null : sfz.trim();
    }

    public String getZdz() {
        return zdz;
    }

    public void setZdz(String zdz) {
        this.zdz = zdz == null ? null : zdz.trim();
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