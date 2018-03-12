package com.lq.mylibrary.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lq.mylibrary.R;
import com.lq.mylibrary.bean.ListRadioBean;

import java.util.List;

/**
 * Created by Administrator on 2018/3/12.
 */

public class ListRadioAdapter extends BaseQuickAdapter<ListRadioBean, BaseViewHolder> {

    public ListRadioAdapter(@LayoutRes int layoutResId, @Nullable List<ListRadioBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ListRadioBean item) {
        helper.setText(R.id.tv_list_item,item.getContent());
        ImageView choose = helper.getView(R.id.iv_list_choose_item);
        if (item.isChecked()) {
            choose.setImageResource(R.drawable.choose_circle_red);
        } else {
            choose.setImageResource(R.drawable.unchoose_circle);
        }
    }
}
