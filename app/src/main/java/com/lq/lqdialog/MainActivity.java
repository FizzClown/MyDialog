package com.lq.lqdialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lq.mylibrary.DialogUtil;
import com.lq.mylibrary.bean.ListRadioResult;
import com.lq.mylibrary.listener.OnGetListRadioResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 6; i++) {
            titles.add("AAA" + i);
        }
    }

    public void show(View view) {
        DialogUtil.dialogWithListRadio(this, "请选择", titles, new OnGetListRadioResult() {
            @Override
            public void getResult(List<ListRadioResult> resultList) {
                List<ListRadioResult> results = resultList;
            }
        });

//        DialogUtil.dialogChangeName(this, "请填写新名字", new OnNameChanged() {
//            @Override
//            public void getName(String name) {
//                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
//            }
//        });

//        DialogUtil.dialogDelete(this, "提示", "确定取消？", new OnDeleteEnsure() {
//            @Override
//            public void onDelete() {
//                Toast.makeText(MainActivity.this, "delete", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
