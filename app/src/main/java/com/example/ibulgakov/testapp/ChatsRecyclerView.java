package com.example.ibulgakov.testapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class ChatsRecyclerView extends RecyclerView {
    private int oldHeight;

    public ChatsRecyclerView(Context context) {
        super(context);
    }

    public ChatsRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatsRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int delta = b - t - this.oldHeight;
        this.oldHeight = b - t;
        if (delta < 0) {
            this.scrollBy(0, -delta);
        }
    }
}