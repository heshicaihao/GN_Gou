package com.gionee.client.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListViewForScrollView extends ListView {
    public ListViewForScrollView(Context context) {
        super(context);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, getHightMeasureSpec());

    }

    private int getHightMeasureSpec() {
        return MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    }
}
