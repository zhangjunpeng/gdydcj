package com.mapuni.gdydcaiji.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mapuni.gdydcaiji.R;
import com.mapuni.gdydcaiji.utils.PathConstant;

import java.util.List;

/**
 * Created by yf on 2018/4/16.
 */

public class PhotoAdapter extends android.widget.BaseAdapter {

    private Context mContext;
    private List<String> mList;
    private LayoutInflater inflater;
    private final int maxlenth = 3;

    public PhotoAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        //return mList.size() + 1;//因为最后多了一个添加图片的ImageView 
        int count = mList == null ? 1 : mList.size() + 1;
        if (count > maxlenth) {
            return mList.size();
        } else {
            return count;
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_photo, parent, false);
        final ImageView iv = convertView.findViewById(R.id.iv_photo);
        if (position < mList.size()) {
            Glide
                    .with(mContext)
                    .load(PathConstant.ROOT_PATH + mList.get(position))
                    .apply(new RequestOptions()
                            .error(R.drawable.not_have_image)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .timeout(1000))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            iv.setBackground(resource);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            iv.setImageDrawable(errorDrawable);
                        }
                    });
        } else {
            iv.setImageResource(R.drawable.selector_camera);//最后一个显示加号图片
        }
        return convertView;
    }
}  
