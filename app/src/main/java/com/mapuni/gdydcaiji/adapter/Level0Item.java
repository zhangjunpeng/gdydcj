package com.mapuni.gdydcaiji.adapter;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by yf on 2018/4/13.
 */

public class Level0Item extends AbstractExpandableItem implements MultiItemEntity {
    public String title;
    public String id;

    public Level0Item(String title,String id) {
        this.title = title;
        this.id = id;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return FieidPersonListAdapter.TYPE_LEVEL_0;
    }
    
    public String getId(){return this.id;}

    public String getTitle() {
        return title;
    }
}
