package com.mapuni.gdydcaiji.utils;

import com.mapuni.gdydcaiji.GdydApplication;
import com.mapuni.gdydcaiji.bean.TbLine;
import com.mapuni.gdydcaiji.bean.TbPoint;
import com.mapuni.gdydcaiji.bean.TbSurface;
import com.mapuni.gdydcaiji.database.greendao.DaoSession;
import com.mapuni.gdydcaiji.database.greendao.TbLineDao;
import com.mapuni.gdydcaiji.database.greendao.TbPointDao;
import com.mapuni.gdydcaiji.database.greendao.TbSurfaceDao;

import java.util.List;

/**
 * Created by yf on 2018/4/17.
 */

public class DbUtils {

    private TbPointDao tbPointDao;
    private TbLineDao tbLineDao;
    private TbSurfaceDao tbSurfaceDao;

    public DbUtils() {
        DaoSession daoSession = GdydApplication.getInstances().getDaoSession();
        tbPointDao = daoSession.getTbPointDao();
        tbLineDao = daoSession.getTbLineDao();
        tbSurfaceDao = daoSession.getTbSurfaceDao();
    }

    /**
     * 质检获取未上传数据数量
     * 在子线程调用
     */
    public int getNoUpdateNum() {
        int totalNum = 0;
        List<TbPoint> tbPointList = tbPointDao.queryBuilder()
                .where(TbPointDao.Properties.Flag.eq(2),  //未上传
                        TbPointDao.Properties.Id.isNotNull()).list();
        List<TbLine> tbLineList = tbLineDao.queryBuilder()
                .where(TbLineDao.Properties.Flag.eq(2),
                        TbLineDao.Properties.Id.isNotNull()).list();
        List<TbSurface> tbSurfaceList = tbSurfaceDao.queryBuilder()
                .where(TbSurfaceDao.Properties.Flag.eq(2),
                        TbSurfaceDao.Properties.Id.isNotNull()).list();
        totalNum = tbPointList.size() + tbLineList.size() + tbSurfaceList.size();
        return totalNum;

    }

    /**
     * 质检删除下载下来的数据
     */
    public void deleteData() {
        tbPointDao.deleteInTx(tbPointDao.queryBuilder()
                .where(TbPointDao.Properties.Id.isNotNull()).list());
        tbLineDao.deleteInTx(tbLineDao.queryBuilder()
                .where(TbLineDao.Properties.Id.isNotNull()).list());
        tbSurfaceDao.deleteInTx(tbSurfaceDao.queryBuilder()
                .where(TbSurfaceDao.Properties.Id.isNotNull()).list());

    }

    public String getFlagByUser() {
        String flag = "";
        String roleid = SPUtils.getInstance().getString("roleid");
        if (roleid.equals("6")) {
            //外业
            flag = "0";
        } else if (roleid.equals("2")) {
            //质检
            flag = "1";
        }
        return flag;
    }

}
