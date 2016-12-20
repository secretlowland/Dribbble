package com.andy.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.andy.base.beans.ShotInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2016/12/11.
 */
public class ShotDetailAct extends BaseActivity {

    public static final String SHOT_INFO = "shot_info";

    private CardView mDetailContainer;
    private CardView mCommentsContainer;
    private ShotInfo mShotInfo;

    public static Intent getIntent(Context context, ShotInfo shotInfo) {
        Intent intent = new Intent(context, ShotDetailAct.class);
        intent.putExtra(SHOT_INFO, shotInfo);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_shot_detail);
        initData(savedInstanceState);
        initView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SHOT_INFO, mShotInfo);
    }

    private void initView() {
        mDetailContainer = (CardView) findViewById(R.id.detail_container);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.detail_container, ShotDetailFrag.getInstance(mShotInfo));
    }

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mShotInfo = (ShotInfo) getIntent().getSerializableExtra(SHOT_INFO);
        } else {
            mShotInfo = (ShotInfo) savedInstanceState.get(SHOT_INFO);
        }
    }

    List<String> getData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            data.add("item"+(i+1));
        }
        return data;
    }
}
