package com.mapuni.gdydcaiji.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.mapuni.gdydcaiji.R;

import java.util.ArrayList;
import java.util.List;

public class FieidPersonListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    private List<String> fieidIds = new ArrayList<>();

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public FieidPersonListAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_download_level0);
        addItemType(TYPE_LEVEL_1, R.layout.item_download_fieidperson);
    }

    public List<String> getFieidIds() {
        return fieidIds;
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (item.getItemType()) {
            case TYPE_LEVEL_0:
                final Level0Item level0Item = (Level0Item) item;
                helper.setText(R.id.tv_fieid_name, level0Item.getTitle());
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = helper.getAdapterPosition();
                        if (level0Item.isExpanded()) {
                            collapse(position);
                        } else {
                            expand(position);
                        }
                    }
                });
                
                break;
            case TYPE_LEVEL_1:
                final Level1Item level1Item = (Level1Item) item;

                helper.setText(R.id.tv_fieid_name, level1Item.getTitle());

                CheckBox checkBox2 = helper.getView(R.id.cb_fieid);
                checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            if (!fieidIds.contains(level1Item.getTitle()))
                                fieidIds.add(level1Item.getTitle());
                        } else {
                            if (fieidIds.contains(level1Item.getTitle()))
                                fieidIds.remove(level1Item.getTitle());
                        }
                        
                    }
                });
                break;
        }
    }
}