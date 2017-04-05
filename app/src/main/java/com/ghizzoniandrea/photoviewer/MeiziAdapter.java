package com.ghizzoniandrea.photoviewer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghizzoniandrea on 2017/3/15.
 */
public class MeiziAdapter extends RecyclerView.Adapter<MeiziAdapter.MeiziViewHolder> {

    private Context mContext;

    private List<String> datas;

    private List<Integer> mHeights;

    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnRecyclerViewItemClickListener = listener;
    }

    public MeiziAdapter(Context context, List<String> datas) {
        this.mContext = context;
        this.datas = datas;
        mHeights = new ArrayList<>();
    }

    @Override
    public MeiziViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_meizi, parent, false);
        MeiziViewHolder viewHolder = new MeiziViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MeiziViewHolder holder, int position) {
        // 随机高度, 模拟瀑布效果.
        if (mHeights.size() <= position) {
            mHeights.add((int) (100 + Math.random() * 300));
        }

        ViewGroup.LayoutParams lp = holder.getIvMeizi().getLayoutParams();
        lp.height = mHeights.get(position);

        holder.getIvMeizi().setLayoutParams(lp);
        Glide.with(mContext).load(datas.get(position)).into(holder.getIvMeizi());
        holder.getIvMeizi().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnRecyclerViewItemClickListener) {
                    mOnRecyclerViewItemClickListener.onItemClickListener(view, holder.getAdapterPosition());
                }
            }
        });
        holder.getIvMeizi().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != mOnRecyclerViewItemClickListener) {
                    mOnRecyclerViewItemClickListener.onItemLongClickListener(view, holder.getAdapterPosition());
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MeiziViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivMeizi;

        public MeiziViewHolder(View itemView) {
            super(itemView);
            ivMeizi = (ImageView) itemView.findViewById(R.id.iv_meizi);
        }

        public ImageView getIvMeizi() {
            return ivMeizi;
        }

    }
}
