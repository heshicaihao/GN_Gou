/*
 * CustomFontTextView.java
 * classes : com.example.pullscrollviewtest.CustomFontTextView
 * @author wuhao
 * Create at 2015-4-28 下午2:53:50
 */
package com.gionee.client.view.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author wuhao
 * @date create at 2015-4-28 下午2:53:50
 * @description
 */
public class CustomFontTextView extends TextView {
  
    public CustomFontTextView(Context context) {
        super(context);
        init(context);
    }
    
    public CustomFontTextView(Context context,AttributeSet attributeSet) {
        super(context,attributeSet);
        init(context);
    }
    public CustomFontTextView(Context context,AttributeSet attributeSet,int defStyle) {
        super(context,attributeSet,defStyle);
        init(context);
    }
    /**
     * @param context
     */
    private void init(Context context) {
        AssetManager assetManager=context.getAssets();
        Typeface typeface=Typeface.createFromAsset(assetManager,"fonts/font_arial_round.ttf");
        setTypeface(typeface);
    }
}
