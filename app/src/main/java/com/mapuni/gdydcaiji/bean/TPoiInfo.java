package com.mapuni.gdydcaiji.bean;

import com.google.gson.annotations.Expose;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * POI信息点数据采集
 * @author meiak
 *
 */
@Entity
public class TPoiInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    
    private String bm;

    private String csbs;

    private String qxbs;

    private String xzbs;

    private String gsjd;

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
    private String sixad;

    @Expose
    private String sevenad;

    private String lydz;

    @Expose
    private String fl;

    @Expose
    private String mjdj;

    private String sslc;

    @Expose
    private String sslymc;

    private Double x;

    private Double y;

    private Date gxsj;

    private String bz;

    @Expose
    private String optuser;

    @Expose
    private Double lat;

    @Expose
    private Double lng;

    @Expose
    private Date opttime;

    @Expose
    private String img;

    private int flag; //是否已上传状态（1-> 已上传、0->未上传）

    @Generated(hash = 1969789401)
    public TPoiInfo(Long id, String bm, String csbs, String qxbs, String xzbs,
            String gsjd, String name, String firstad, String secondad,
            String thirdad, String forthad, String fifad, String sixad,
            String sevenad, String lydz, String fl, String mjdj, String sslc,
            String sslymc, Double x, Double y, Date gxsj, String bz, String optuser,
            Double lat, Double lng, Date opttime, String img, int flag) {
        this.id = id;
        this.bm = bm;
        this.csbs = csbs;
        this.qxbs = qxbs;
        this.xzbs = xzbs;
        this.gsjd = gsjd;
        this.name = name;
        this.firstad = firstad;
        this.secondad = secondad;
        this.thirdad = thirdad;
        this.forthad = forthad;
        this.fifad = fifad;
        this.sixad = sixad;
        this.sevenad = sevenad;
        this.lydz = lydz;
        this.fl = fl;
        this.mjdj = mjdj;
        this.sslc = sslc;
        this.sslymc = sslymc;
        this.x = x;
        this.y = y;
        this.gxsj = gxsj;
        this.bz = bz;
        this.optuser = optuser;
        this.lat = lat;
        this.lng = lng;
        this.opttime = opttime;
        this.img = img;
        this.flag = flag;
    }

    @Generated(hash = 1842064909)
    public TPoiInfo() {
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

    public String getSevenad() {
        return sevenad;
    }

    public void setSevenad(String sevenad) {
        this.sevenad = sevenad == null ? null : sevenad.trim();
    }

    public String getLydz() {
        return lydz;
    }

    public void setLydz(String lydz) {
        this.lydz = lydz == null ? null : lydz.trim();
    }

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl == null ? null : fl.trim();
    }

    public String getMjdj() {
        return mjdj;
    }

    public void setMjdj(String mjdj) {
        this.mjdj = mjdj == null ? null : mjdj.trim();
    }

    public String getSslc() {
        return sslc;
    }

    public void setSslc(String sslc) {
        this.sslc = sslc == null ? null : sslc.trim();
    }

    public String getSslymc() {
        return sslymc;
    }

    public void setSslymc(String sslymc) {
        this.sslymc = sslymc == null ? null : sslymc.trim();
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

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz == null ? null : bz.trim();
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

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}