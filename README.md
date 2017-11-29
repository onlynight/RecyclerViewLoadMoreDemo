# RecyclerView Load More Demo

日常开发中使用RecyclerView时会经常用到上拉加载的功能，但是RecyclerView本身又没有提供便捷的使用方法，只提供了一个统一的下拉加载功能。这里我们演示一下如何为RecyclerView添加上拉加载功能。

完整demo你可以再github上看到：![RecyclerViewLoadMoreDemo](https://github.com/onlynight/RecyclerViewLoadMoreDemo)

### 原理

使用RecyclerView提供的ScrollListener监听RecyclerView滑动事件，当滑动到底部时，如果需要显示loading则显示loading布局。基本原理就是这样，下面我们来看下代码：

#### 1. 首先定义布局

一个是RecyclerView的item布局；另外一个是loading布局。

item布局如下(布局视使用情况而定，这里只展示demo)：

```xml
<!-- item_test_recycler_view.xml-->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="60dp"
    android:gravity="bottom|center"
    android:orientation="vertical">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:gravity="center"
        android:text="Debug" />

    <View
        android:layout_width="match_parent"
        android:layout_height="1px"
        android:layout_gravity="bottom"
        android:background="#333333" />

</LinearLayout>
```

loading布局如下(布局视使用情况而定，这里只展示demo)：

```xml
<!-- part_recycler_view_footer.xml -->
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <ProgressBar
        android:layout_width="50dp"
        android:layout_height="50dp"
        android:layout_gravity="center" />

</FrameLayout>
```

#### 2. 修改Adapter中的部分代码

如果只显示内容布局，我们在getCount方法中返回内容的数量即可，如果要显示loading则需要在内容的基础上再加1用于显示loading布局。同时我们还需要一个标识，标识是否显示loading。具体代码如下：

```java
// TestRecyclerAdapter.java

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

    // 是否显示loading footer
    private boolean mShowFooter = false;

    // item的数量，这里只做demo展示用。
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
        // 如果需要显示footer则加1，否则不加1。
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

        // 判断返回的viewholder类性的时候，需要首先判断是否需要显示footer，不显示的话直接返回固定类性即可。
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
```

#### 3. 为RecyclerView添加ScrollListener

我们需要监听滑动到底部的事件，这时候我们就需要监听RecyclerView的滚动事件了。同时再滚动到底部的时候触发加载更多事件即可。代码如下：

```java
// MainActivity.java 代码片段

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
```


