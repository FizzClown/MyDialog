package com.lq.mylibrary.bean;

/**
 * Created by Administrator on 2018/3/12.
 */

public class ListRadioBean {
    private boolean isChecked;
    private String content;

    public ListRadioBean(boolean isChecked, String content) {
        this.isChecked = isChecked;
        this.content = content;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
