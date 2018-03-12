package com.lq.mylibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lq.mylibrary.adapter.ListRadioAdapter;
import com.lq.mylibrary.bean.ListRadioBean;
import com.lq.mylibrary.bean.ListRadioResult;
import com.lq.mylibrary.listener.OnDeleteEnsure;
import com.lq.mylibrary.listener.OnGetListRadioResult;
import com.lq.mylibrary.listener.OnNameChanged;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lq on 2018/3/10
 */
@SuppressLint("InflateParams")
public class DialogUtil {
    //列表后面带选择按钮
    public static Dialog dialogWithListRadio(final Activity activity, String tip, List<String> contents, final OnGetListRadioResult result) {
        final List<ListRadioBean> list = new ArrayList<>();
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_choose_reason, null);
        if(contents==null){
            throw new NullPointerException("List不能为空");
        }
        for (String content : contents) {
            if ("".equals(content) || null == content) {
                throw new NullPointerException("每个List的条目内容不能为空");
            } else {
                ListRadioBean bean = new ListRadioBean(false, content);
                list.add(bean);
            }
        }
        TextView tvTip = view.findViewById(R.id.tv_dialog_tip);
        tvTip.setText(tip);
        final Dialog dialogWithList = new Dialog(activity, R.style.dialogTransparent);
        final ListRadioAdapter adapter = new ListRadioAdapter(R.layout.adapter_list_radio, list);
        RecyclerView recyclerView = view.findViewById(R.id.dialog_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                list.get(position).setChecked(!list.get(position).isChecked());
                adapter.notifyDataSetChanged();
            }
        });
        view.findViewById(R.id.tv_dialog_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ListRadioResult> results = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isChecked()) {
                        ListRadioResult resultItem = new ListRadioResult(list.get(i).getContent(), i);
                        results.add(resultItem);
                    }
                }
                if(results.size()==0){
                    Toast.makeText(activity, "请至少选择一项", Toast.LENGTH_SHORT).show();
                    return;
                }
                result.getResult(results);
            }
        });
        view.findViewById(R.id.tv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogWithList.dismiss();
            }
        });
        dialogWithList.setContentView(view);
        if (dialogWithList.getWindow() != null)
            dialogWithList.getWindow().setGravity(Gravity.CENTER);
        dialogWithList.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        dialogWithList.show();
        return dialogWithList;
    }

    //取消发表
    public static Dialog dialogDelete(final Activity activity, String tip, String tipContent, final OnDeleteEnsure delete) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_delete, null);
        final Dialog dialogDelete = new Dialog(activity, R.style.dialogTransparent);
        TextView tvTip = view.findViewById(R.id.tv_tip);
        tvTip.setText(tip);
        TextView tvTipContent = view.findViewById(R.id.tv_tip_content);
        tvTipContent.setText(tipContent);
        dialogDelete.setContentView(view);
        if (dialogDelete.getWindow() != null)
            dialogDelete.getWindow().setGravity(Gravity.CENTER);
        dialogDelete.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        view.findViewById(R.id.bt_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDelete.dismiss();
                delete.onDelete();
            }
        });
        view.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDelete.dismiss();
            }
        });
        dialogDelete.setCancelable(true);
        dialogDelete.show();
        return dialogDelete;
    }

    //更改名字
    public static Dialog dialogChangeName(final Activity activity, String tip, final OnNameChanged nameChanged) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_change_name, null);
        final Dialog dialogChangeName = new Dialog(activity, R.style.dialogTransparent);
        TextView tvTip = view.findViewById(R.id.tv_dialog_tip);
        tvTip.setText(tip);
        final EditText etNewName = view.findViewById(R.id.et_new_name);
        view.findViewById(R.id.bt_ensure_new_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNewName.getText().toString().equals("")) {
                    Toast.makeText(activity, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                nameChanged.getName(etNewName.getText().toString());
            }
        });
        dialogChangeName.setContentView(view);
        if (dialogChangeName.getWindow() != null)
            dialogChangeName.getWindow().setGravity(Gravity.CENTER);
        dialogChangeName.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        dialogChangeName.show();
        return dialogChangeName;
    }

    //查看大图
    public static Dialog dialogWatchBigImg(final Activity activity, final String path) {
        View popupView = LayoutInflater.from(activity).inflate(R.layout.dialog_watch_big_img, null);
        ImageView iv = popupView.findViewById(R.id.iv_img);
        Glide.with(activity).load(path).into(iv);
        final Dialog dialogWatchBigImg = new Dialog(activity, R.style.dialogTransparent);
        dialogWatchBigImg.setContentView(popupView);
        if (dialogWatchBigImg.getWindow() != null)
            dialogWatchBigImg.getWindow().setGravity(Gravity.CENTER);
        int paddingL = 0 - 10;
        Window window = dialogWatchBigImg.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        dialogWatchBigImg.getWindow().getDecorView().setPadding(paddingL, 0, paddingL, 0);
        dialogWatchBigImg.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        dialogWatchBigImg.setCancelable(true);
        dialogWatchBigImg.show();
        return dialogWatchBigImg;
    }
}
