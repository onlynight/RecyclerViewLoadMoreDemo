package com.github.onlynight.recyclerviewloadmoredemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhang on 2017/11/29.
 */

public class TestRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 设置底部布局
    private static final int VIEW_TYPE_FOOTER = 1;
    // 设置默认布局
    private static final int VIEW_TYPE_DEFAULT = 0;

    private boolean mShowFooter = false;
    private int mCount = 0;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View contentView;
        if (viewType == VIEW_TYPE_DEFAULT) {
            contentView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_test_recycler_view, parent, false);
            viewHolder = new ViewHolder(contentView);
        } else {
            contentView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.part_recycler_view_footer, parent, false);
            viewHolder = new FooterViewHolder(contentView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        // 判断是不是显示底部，是就返回1，不是返回0
        int begin = mShowFooter ? 1 : 0;
        // 没有数据的时候，直接返回begin
        if (mCount <= 0) {
            return begin;
        }
        // 因为底部布局要占一个位置，所以总数目要+1
        return mCount + begin;
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowFooter) {
            // 判断当前位置+1是不是等于数据总数（因为数组从0开始计数），是的就加载底部布局刷新，不是就加载默认布局
            if (position + 1 == getItemCount()) {
                return VIEW_TYPE_FOOTER;
            } else {
                return VIEW_TYPE_DEFAULT;
            }
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    public boolean isShowFooter() {
        return mShowFooter;
    }

    public void setShowFooter(boolean isShowFooter) {
        this.mShowFooter = isShowFooter;
    }

    public void addData(int i) {
        this.mCount += i;
    }

    public void clearData() {
        this.mCount = 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }

    }

}
