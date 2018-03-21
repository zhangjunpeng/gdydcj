package com.mapuni.gdydcaiji.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
/**
 * 楼宇信息点采集不全
 * @author meiak
 *
 */
@Entity
public class TBuildingInfo {
    @Id
    private Long id;
    
    private String bm;

    private String csbs;

    private String qxbs;

    private String xzbs;

    private String gsjd;

    private String lygs;

    private String lytype;

    private String lyxz;

    private String lyfl;

    private String xqid;

    private String xqmc;

    private String name;

    private String firstad;

    private String secondad;

    private String thirdad;

    private String forthad;

    private String fifad;

    private String sixad;

    private String lydz;

    private String lycs;

    private String lydscs;

    private Double x;

    private Double y;

    private Double lydbx;

    private Date gxsj;

    private String bz;

    private Double lymj;

    private String optuser;

    private Double lat;

    private Double lng;

    private Date opttime;

    private String lygd;

    private String lyzhs;

    private String img;

    @Generated(hash = 1655551627)
    public TBuildingInfo(Long id, String bm, String csbs, String qxbs, String xzbs,
            String gsjd, String lygs, String lytype, String lyxz, String lyfl,
            String xqid, String xqmc, String name, String firstad, String secondad,
            String thirdad, String forthad, String fifad, String sixad, String lydz,
            String lycs, String lydscs, Double x, Double y, Double lydbx, Date gxsj,
            String bz, Double lymj, String optuser, Double lat, Double lng,
            Date opttime, String lygd, String lyzhs, String img) {
        this.id = id;
        this.bm = bm;
        this.csbs = csbs;
        this.qxbs = qxbs;
        this.xzbs = xzbs;
        this.gsjd = gsjd;
        this.lygs = lygs;
        this.lytype = lytype;
        this.lyxz = lyxz;
        this.lyfl = lyfl;
        this.xqid = xqid;
        this.xqmc = xqmc;
        this.name = name;
        this.firstad = firstad;
        this.secondad = secondad;
        this.thirdad = thirdad;
        this.forthad = forthad;
        this.fifad = fifad;
        this.sixad = sixad;
        this.lydz = lydz;
        this.lycs = lycs;
        this.lydscs = lydscs;
        this.x = x;
        this.y = y;
        this.lydbx = lydbx;
        this.gxsj = gxsj;
        this.bz = bz;
        this.lymj = lymj;
        this.optuser = optuser;
        this.lat = lat;
        this.lng = lng;
        this.opttime = opttime;
        this.lygd = lygd;
        this.lyzhs = lyzhs;
        this.img = img;
    }

    @Generated(hash = 1921453099)
    public TBuildingInfo() {
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

    public String getLygs() {
        return lygs;
    }

    public void setLygs(String lygs) {
        this.lygs = lygs == null ? null : lygs.trim();
    }

    public String getLytype() {
        return lytype;
    }

    public void setLytype(String lytype) {
        this.lytype = lytype == null ? null : lytype.trim();
    }

    public String getLyxz() {
        return lyxz;
    }

    public void setLyxz(String lyxz) {
        this.lyxz = lyxz == null ? null : lyxz.trim();
    }

    public String getLyfl() {
        return lyfl;
    }

    public void setLyfl(String lyfl) {
        this.lyfl = lyfl == null ? null : lyfl.trim();
    }

    public String getXqid() {
        return xqid;
    }

    public void setXqid(String xqid) {
        this.xqid = xqid == null ? null : xqid.trim();
    }

    public String getXqmc() {
        return xqmc;
    }

    public void setXqmc(String xqmc) {
        this.xqmc = xqmc == null ? null : xqmc.trim();
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

    public String getSixad() {
        return sixad;
    }

    public void setSixad(String sixad) {
        this.sixad = sixad == null ? null : sixad.trim();
    }

    public String getLydz() {
        return lydz;
    }

    public void setLydz(String lydz) {
        this.lydz = lydz == null ? null : lydz.trim();
    }

    public String getLycs() {
        return lycs;
    }

    public void setLycs(String lycs) {
        this.lycs = lycs == null ? null : lycs.trim();
    }

    public String getLydscs() {
        return lydscs;
    }

    public void setLydscs(String lydscs) {
        this.lydscs = lydscs == null ? null : lydscs.trim();
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

    public Double getLydbx() {
        return lydbx;
    }

    public void setLydbx(Double lydbx) {
        this.lydbx = lydbx;
    }

    public Date getGxsj() {
        return gxsj;
    }

    public void setGxsj(Date gxsj) {
        this.gxsj = gxsj;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz == null ? null : bz.trim();
    }

    public Double getLymj() {
        return lymj;
    }

    public void setLymj(Double lymj) {
        this.lymj = lymj;
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

    public String getLygd() {
        return lygd;
    }

    public void setLygd(String lygd) {
        this.lygd = lygd;
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}