package com.mapuni.gdydcaiji.utils;

import android.location.Address;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yf on 2018/4/9.
 * 用于保存和回显名称和地址字段
 */

public class ShowDataUtils {


    public static void saveAddressOrName(String key,String value) {

        if (TextUtils.isEmpty(value)) {
            return;
        }

        List<String> mAddArray = getAddressOrNameArray(key);
        if (getAddressOrName(key).contains(value)) {
            mAddArray.remove(value);
        } else if (mAddArray.size() >= 10) {
            mAddArray.remove(9);
        }
        mAddArray.add(0, value);

        String spAddress = "";
        for (int i = 0; i < mAddArray.size(); i++) {
            spAddress += mAddArray.get(i) + ";";
        }

        SPUtils.getInstance().put(key, spAddress.substring(0, spAddress.length() - 1));
    }

    public static List<String> getAddressOrNameArray(String key) {
        String address = getAddressOrName(key);
        if (TextUtils.isEmpty(address)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(address.split(";")));
    }

    public static String getAddressOrName(String key) {
        return SPUtils.getInstance().getString(key);
    }
}
