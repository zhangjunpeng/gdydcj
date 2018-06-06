package com.mapuni.gdydcaiji.adapter

import android.view.View
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.mapuni.gdydcaiji.R
import com.mapuni.gdydcaiji.bean.HomeArea
import java.util.ArrayList

/**
 * Created by yf on 2018/6/6.
 */
class HomeAreaAdapter : BaseQuickAdapter<HomeArea.HomeAreaBean, BaseViewHolder>(R.layout.item_download_interior_area, null) {
    private val areaNames = ArrayList<String>()
    override fun convert(helper: BaseViewHolder?, item: HomeArea.HomeAreaBean?) {
        helper?.setText(R.id.tv_fieid_name, item?.homearea)

        val checkBox = helper?.getView<CheckBox>(R.id.cb_fieid)
        checkBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!areaNames.contains(item?.homearea)) {
                    areaNames.add(item?.homearea!!)
                }
            } else {
                if (areaNames.contains(item?.homearea)) {
                    areaNames.remove(item?.homearea)
                }
            }
        }
    }

    fun getAreaNames(): List<String> {
        return areaNames
    }

}