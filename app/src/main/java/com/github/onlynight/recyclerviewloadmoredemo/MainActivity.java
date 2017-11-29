package com.github.onlynight.recyclerviewloadmoredemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TestRecyclerAdapter mAdapter;

    private int mLastVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TestRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                /**
                 * scrollState有三种状态，分别是SCROLL_STATE_IDLE、SCROLL_STATE_TOUCH_SCROLL、SCROLL_STATE_FLING
                 * SCROLL_STATE_IDLE是当屏幕停止滚动时
                 * SCROLL_STATE_TOUCH_SCROLL是当用户在以触屏方式滚动屏幕并且手指仍然还在屏幕上时
                 * SCROLL_STATE_FLING是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时
                 */
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItem + 1 == mAdapter.getItemCount()
                        && mAdapter.isShowFooter()) {

                    // 上拉加载更多
                    loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 给lastVisibleItem赋值
                // findLastVisibleItemPosition()是返回最后一个item的位置
                mLastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        loadMore();
    }

    private void loadMore() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addData(20);
                mAdapter.setShowFooter(true);
                mAdapter.notifyDataSetChanged();
            }
        }, 1000);
    }

}
