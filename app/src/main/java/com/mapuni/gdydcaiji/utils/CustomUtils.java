package com.mapuni.gdydcaiji.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by oldJin on 2017/12/15.
 */

public class CustomUtils {
    public static String parseFilePath(Uri uri, Context context) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    /**
     * 多个TextView设置必填的效果
     *
     * @param textViews
     */
    public static void setLabelRequired(TextView... textViews) {
        for (TextView textView : textViews) {
            String text = textView.getText().toString().trim();
            textView.setText(Html.fromHtml(text + "<font color='#e53935'>&nbsp;*</font>"));
        }
    }

}
