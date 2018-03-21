package com.mapuni.gdydcaiji.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;


import com.mapuni.gdydcaiji.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by xhg on 2017/4/14.
 */

public class BaseSaveInfoActivity extends Activity {


    /***
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /***
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    //得到裁切后的图片
    public static final int GET_PIC = 3;

    /***
     * 从Intent获取图片路径的KEY
     */
    public static final String KEY_PHOTO_PATH = "photo_path";

    /**
     * 获取到的图片路径
     */
    private String picPath;
    private com.nostra13.universalimageloader.core.ImageLoader imageLoader = ImageLoader.getInstance();

    public Uri photoUri;

//    public void movePicToTargetPath(String picpath, ImageView imageView) {
//        File file = new File(picpath);
//        String targetPath = CollectionActivity.imagePath + "/" + file.getName();
//        copyFile(picpath, targetPath);
//        imageLoader.displayImage("file://" + targetPath, imageView);
//        imageView.setTag(file.getName());
//    }


    /**
     * 拍照获取图片
     */
    public void takePhoto() {
        //执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
            /***
             * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
             * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
             * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
             */
            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            /**-----------------*/
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * 从相册中取图片
     */
    public void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_PICK);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
//        startActivityForResult(intent, 1);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }


    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_takepic);
        dialog.findViewById(R.id.takephoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.selectpic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    /* 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public String getPhotoJson(List<String> photoList) {
        JSONArray jsonArray = new JSONArray();
        for (String photo : photoList) {
            jsonArray.put(photo);
        }
        return jsonArray.toString();
    }

    AlertDialog alertDialog;

    public void showDeleteDialog(Context context, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("删除此条数据？").setPositiveButton("确定", listener).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.show();
    }
}
