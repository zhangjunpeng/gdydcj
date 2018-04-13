package com.mapuni.gdydcaiji.adapter;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by yf on 2018/4/13.
 */

public class Level1Item implements MultiItemEntity {
    public String title;

    public Level1Item(String title) {
        this.title = title;
    }

    @Override
    public int getItemType() {
        return FieidPersonListAdapter.TYPE_LEVEL_1;
    }

    public String getTitle() {
        return title;
    }
}
