package com.gionee.client.view.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

public class CustomGuideDialog extends AlertDialog {

//    private Button mImmediatelyExperience;

    public CustomGuideDialog(Context context) {
        super(context);
    }

    public CustomGuideDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomGuideDialog(Context context, int theme, boolean fromRight) {
        super(context, theme);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.boot_guide_dialog);
//        
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
////        lp.width = (int)(display.getWidth()); //设置宽度
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        getWindow().setAttributes(lp);
//        
//        mImmediatelyExperience = (Button) this.findViewById(R.id.immediately_experience);
//        mImmediatelyExperience.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                CustomGuideDialog.this.dismiss();
//            }
//        });
    }

}
