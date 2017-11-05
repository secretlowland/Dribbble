package com.andy.dribbble.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andy.dribbble.R;
import com.andy.dribbble.beans.ShotInfo;
import com.andy.dribbble.common_utils.DateTimeUtil;
import com.andy.dribbble.common_utils.ScreenUtil;
import com.andy.dribbble.view.IconText;
import com.bumptech.glide.Glide;

/**
 * Created by andy on 2016/12/11.
 */
public class ShotsListAdapter extends BaseListAdapter<ShotInfo, ShotsListAdapter.Holder> {

    private Fragment mFrag;
    private ActionListener mListener;

    public ShotsListAdapter(Fragment frag) {
        super(frag.getContext());
        this.mFrag = frag;
    }

    public void setActionListener(ActionListener listener) {
        this.mListener = listener;
    }

    @Override
    protected Holder onCreateItemHolder(ViewGroup parent) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shots_list, parent, false));
    }

    @Override
    protected void onBindItemHolder(Holder holder, int position) {
        super.onBindItemHolder(holder, position);
        final ShotInfo info = mData.get(position);
        if (info == null) {
            return;
        }

        holder.updateTime.setText(DateTimeUtil.formatDate(info.getUpdateTime()));
        holder.likesCount.setText(String.valueOf(info.getLikesCount()));
        holder.commentsCount.setText(String.valueOf(info.getCommentsCount()));
        holder.viewsCount.setText(String.valueOf(info.getViewsCount()));

        // 设置最后一个item底部的margin
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        int bottomMargin = lp.bottomMargin;
        if (position == getItemCount() - 1) {
            bottomMargin = ScreenUtil.dip2px(mFrag.getContext(), 10);
        }
        lp.bottomMargin = bottomMargin;
        holder.itemView.setLayoutParams(lp);

        // 加载图片
        Glide.with(mFrag).load(info.getImages().getNormal()).into(holder.image);

        if (mListener != null) {
            holder.likesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onLikeClick(view, info.getId());
                }
            });
        }

    }

    private String getFormatString(int resId, Object obj) {
        return String.format(mFrag.getContext().getResources().getString(resId), obj);
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView image;
        IconText updateTime, likesCount, commentsCount, viewsCount;
        public Holder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            updateTime = (IconText) itemView.findViewById(R.id.update_time);
            likesCount = (IconText) itemView.findViewById(R.id.likes_count);
            commentsCount = (IconText) itemView.findViewById(R.id.comments_count);
            viewsCount = (IconText) itemView.findViewById(R.id.views_count);
        }
    }

    public interface ActionListener {
        void onLikeClick(View view, int shotId);
    }
}
