package com.lq.mylibrary.bean;

/**
 * Created by Administrator on 2018/3/12.
 */

public class ListRadioResult {
    private String str;
    private int position;

    public ListRadioResult(String str, int position) {
        this.str = str;
        this.position = position;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
