package com.mapuni.gdydcaiji.utils;

import android.content.Context;

import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.bean.TbLine;
import com.mapuni.gdydcaiji.bean.TbPoint;
import com.mapuni.gdydcaiji.bean.TbSurface;

import java.io.File;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


/**
 * Created by Summer on 2017/4/11.
 */

public class SaveToExcelUtil {
    private WritableWorkbook wwb;
    private String excelPath;
    private File excelFile;
    private Context mContext;

    public SaveToExcelUtil(Context mContext, String excelPath) {
        this.excelPath = excelPath;
        this.mContext = mContext;

        excelFile = new File(excelPath);
        createExcel();
    }

    // 创建excel表.
    public void createExcel() {

        try {
            if (excelFile.exists()) {
                excelFile.delete();
            }

            wwb = Workbook.createWorkbook(excelFile);

            WritableSheet sheet0 = wwb.createSheet("点数据", 0);
            WritableSheet sheet1 = wwb.createSheet("线数据", 1);
            WritableSheet sheet2 = wwb.createSheet("面数据", 2);

            String[] pois = mContext.getResources().getStringArray(R.array.poi_item);
            String[] lines = mContext.getResources().getStringArray(R.array.line_item);
            String[] surfaces = mContext.getResources().getStringArray(R.array.surface_item);
            // 在指定单元格插入数据
            for (int i = 0; i < pois.length; i++) {
                Label label = new Label(i, 0, pois[i]);
                sheet0.addCell(label);
            }
            for (int i = 0; i < lines.length; i++) {
                Label label = new Label(i, 0, lines[i]);
                sheet1.addCell(label);
            }
            for (int i = 0; i < surfaces.length; i++) {
                Label label = new Label(i, 0, surfaces[i]);
                sheet2.addCell(label);
            }

            // 从内存中写入文件中
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //将数据存入到Excel表中
    public void writeToExcel(String... args) {

        try {
            Workbook oldWwb = Workbook.getWorkbook(excelFile);
            wwb = Workbook.createWorkbook(excelFile, oldWwb);
            WritableSheet sheet = wwb.getSheet(0);
            // 当前行数
            int row = sheet.getRows();
            for (int i = 0; i < args.length; i++) {
                Label label = new Label(i, row, args[i] + "");
                sheet.addCell(label);
            }

            // 从内存中写入文件中,只能刷一次.
            wwb.write();
            wwb.close();
            //Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeToExcel(List<TbPoint> points, List<TbLine> lines, List<TbSurface> surfaces) {

        try {
            Workbook oldWwb = Workbook.getWorkbook(excelFile);
            wwb = Workbook.createWorkbook(excelFile, oldWwb);
            WritableSheet sheet0 = wwb.getSheet(0);
            WritableSheet sheet1 = wwb.getSheet(1);
            WritableSheet sheet2 = wwb.getSheet(2);

            //行，应从第二行开始
            for (int i = 1; i < points.size() + 1; i++) {
                TbPoint tbPoint = points.get(i - 1);
                //列
                for (int j = 0; j < mContext.getResources().getStringArray(R.array.poi_item).length; j++) {
                    Label label = null;
                    if (j == 0) {
<<<<<<< HEAD
//                        label = new Label(j, i, tbPoint.getId().toString());
                        label = new Label(j, i, "");
=======
                        label = new Label(j, i, tbPoint.getId().toString());
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075
                    } else if (j == 1) {
                        label = new Label(j, i, tbPoint.getLytype());
                    } else if (j == 2) {
                        label = new Label(j, i, tbPoint.getLyxz());
                    } else if (j == 3) {
                        label = new Label(j, i, tbPoint.getName());
                    } else if (j == 4) {
                        label = new Label(j, i, tbPoint.getFl());
                    } else if (j == 5) {
                        label = new Label(j, i, tbPoint.getDz());
                    } else if (j == 6) {
                        label = new Label(j, i, tbPoint.getDy());
                    } else if (j == 7) {
                        label = new Label(j, i, tbPoint.getLxdh());
                    } else if (j == 8) {
                        label = new Label(j, i, tbPoint.getDj());
                    } else if (j == 9) {
                        label = new Label(j, i, tbPoint.getLycs());
                    } else if (j == 10) {
                        label = new Label(j, i, tbPoint.getLyzhs());
                    } else if (j == 11) {
                        label = new Label(j, i, tbPoint.getNote());
                    } else if (j == 12) {
                        label = new Label(j, i, tbPoint.getAuthcontent());
                    }
                    sheet0.addCell(label);
                }
            }

            //行，应从第二行开始
            for (int i = 1; i < lines.size() + 1; i++) {
                TbLine tbLine = lines.get(i - 1);
                //列
                for (int j = 0; j < mContext.getResources().getStringArray(R.array.line_item).length; j++) {
                    Label label = null;
                    if (j == 0) {
<<<<<<< HEAD
//                        label = new Label(j, i, tbLine.getId().toString());
                        label = new Label(j, i, "");
=======
                        label = new Label(j, i, tbLine.getId().toString());
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075
                    } else if (j == 1) {
                        label = new Label(j, i, tbLine.getName());
                    } else if (j == 2) {
                        label = new Label(j, i, tbLine.getSfz());
                    } else if (j == 3) {
                        label = new Label(j, i, tbLine.getZdz());
                    } else if (j == 4) {
                        label = new Label(j, i, tbLine.getNote());
                    } else if (j == 5) {
                        label = new Label(j, i, tbLine.getAuthcontent());
                    }
                    sheet1.addCell(label);
                }
            }

            //行，应从第二行开始
            for (int i = 1; i < surfaces.size() + 1; i++) {
                TbSurface tbSurface = surfaces.get(i - 1);
                //列
                for (int j = 0; j < mContext.getResources().getStringArray(R.array.surface_item).length; j++) {
                    Label label = null;
                    if (j == 0) {
<<<<<<< HEAD
//                        label = new Label(j, i, tbSurface.getId().toString());
                        label = new Label(j, i, "");
=======
                        label = new Label(j, i, tbSurface.getId().toString());
>>>>>>> 746d730cb42a41f26875c11a1735a2e36e6a7075
                    } else if (j == 1) {
                        label = new Label(j, i, tbSurface.getName());
                    } else if (j == 2) {
                        label = new Label(j, i, tbSurface.getXqdz());
                    } else if (j == 3) {
                        label = new Label(j, i, mContext.getResources().getStringArray(R.array.social_type)[Integer.parseInt(tbSurface.getFl())]);
                    } else if (j == 4) {
                        label = new Label(j, i, tbSurface.getWyxx());
                    } else if (j == 5) {
                        label = new Label(j, i, tbSurface.getLxdh());
                    } else if (j == 6) {
                        label = new Label(j, i, tbSurface.getLds());
                    } else if (j == 7) {
                        label = new Label(j, i, tbSurface.getNote());
                    } else if (j == 8) {
                        label = new Label(j, i, tbSurface.getAuthcontent());
                    }
                    sheet2.addCell(label);
                }
            }

            // 从内存中写入文件中,只能刷一次.
            wwb.write();
            wwb.close();
            ThreadUtils.executeMainThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShort("导出完成");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            ThreadUtils.executeMainThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShort("导出失败");
                    if (excelFile.exists())
                        excelFile.delete();
                }
            });
        }

    }
}