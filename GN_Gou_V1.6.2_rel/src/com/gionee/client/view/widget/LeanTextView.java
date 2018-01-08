// Gionee <yangxiong><2013-12-28> add for CR00850885 begin
/*
 * @author yangxiong
 * V 1.0.0
 * Create at 2013-12-28 上午10:51:54
 */
package com.gionee.client.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * com.gionee.client.view.widget.LeanTextView
 * 
 * @author yangxiong <br/>
 * @date create at 2013-12-28 上午10:51:54
 * @description TODO 文字倾斜TextView
 */
public class LeanTextView extends TextView {
/*    private static final String TAG = "LeanTextView";

    private static final String NAMESPACE = "http://www.gionee.com/";

    private static final String ATTR_LEAN = "lean";

    // 默认倾斜0度
    private static final int DEFAULTVALUE_DEGREES = 0;

    private int degrees;*/

    public LeanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        degrees = attrs.getAttributeIntValue(NAMESPACE, ATTR_LEAN, DEFAULTVALUE_DEGREES);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 旋转：绕getMeasuredWidth()/2,getMeasuredHeight()/2点，旋转degrees度。
        canvas.rotate(-45, getMeasuredWidth() * 11 / (float) 20, getMeasuredHeight() * 1 / (float) 4);
        super.onDraw(canvas);
    }

}
