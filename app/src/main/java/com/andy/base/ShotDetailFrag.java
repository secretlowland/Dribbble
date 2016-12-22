package com.andy.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andy.base.beans.ShotInfo;

/**
 * Created by andy on 2016/12/20.
 */
public class ShotDetailFrag extends BaseFragment {

    private ShotInfo mShotInfo;

    public static ShotDetailFrag getInstance(ShotInfo shotInfo) {
        ShotDetailFrag frag = new ShotDetailFrag();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShotDetailAct.SHOT_INFO, shotInfo);
        frag.setArguments(bundle);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_shot_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView(view);
    }

    private void initView(View view) {
        TextView updateTime = (TextView) view.findViewById(R.id.update_time);
        TextView likesCount = (TextView) view.findViewById(R.id.likes_count);
        TextView commentsCount = (TextView) view.findViewById(R.id.comments_count);
        TextView viewsCount = (TextView) view.findViewById(R.id.views_count);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView userName = (TextView) view.findViewById(R.id.user_name);

        if (mShotInfo != null) {
            updateTime.setText(mShotInfo.getUpdateTime());
            likesCount.setText(getFormatString(R.string.likes_count, mShotInfo.getLikesCount()));
            commentsCount.setText(getFormatString(R.string.comments_count, mShotInfo.getCommentsCount()));
            viewsCount.setText(getFormatString(R.string.views_count, mShotInfo.getViewsCount()));
            title.setText(mShotInfo.getTitle());
            if (mShotInfo.getDescription() != null) {
                description.setText(Html.fromHtml(mShotInfo.getDescription()));
            }
            name.setText(mShotInfo.getUserInfo().getName());
            userName.setText(mShotInfo.getUserInfo().getUserName());
        }
    }

    private String getFormatString(int resId, Object obj) {
        return String.format(getContext().getResources().getString(resId), obj);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mShotInfo = (ShotInfo) bundle.getSerializable(ShotDetailAct.SHOT_INFO);
        }
        Log.d("TAG","shot info>>"+mShotInfo);
    }
}
