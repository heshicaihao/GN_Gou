package com.gionee.client.activity.imageScan;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.activity.imageScan.ImageScanAdapter.ISelectImageListener;
import com.gionee.client.model.Constants;
import com.gionee.framework.operation.net.GNImageLoader;

public class ImageScanAndSelectActivity extends BaseFragmentActivity implements ISelectImageListener,
        OnClickListener {
    private GridView mGridView;
    private List<String> mImageList;
    private ImageScanAdapter mImageScanAdapter;
    private Button mCertainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_activity);
        mCertainBtn = (Button) findViewById(R.id.certain_btn);
        mGridView = (GridView) findViewById(R.id.child_grid);
        mImageList = getIntent().getStringArrayListExtra("data");
        mImageScanAdapter = new ImageScanAdapter(this, mImageList);
        mGridView.setAdapter(mImageScanAdapter);
        mImageScanAdapter.setmSelectImageListener(this);
    }

    @Override
    public void onSelectImageChange(List<String> mImageList) {
        if (mImageList == null || mImageList.size() == 0) {
            mCertainBtn.setText(R.string.ok);
            mCertainBtn.setEnabled(false);
            return;
        }
        mCertainBtn.setEnabled(true);
        mCertainBtn.setText(String.format(getString(R.string.select_image_btn), mImageList.size()));
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            GNImageLoader.getInstance().getImageLoader().getMemoryCache().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    /* (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        GNImageLoader.getInstance().init(this);
    }

    @Override
    public void onClick(View arg0) {
        mSelfData.put(Constants.SELECT_IMAGE_LIST, mImageScanAdapter.getSelectItems());
        setResult(Constants.ActivityResultCode.RESULT_CODE_IMAGE_SELECT);
        finish();
    }

    /* (non-Javadoc)
     * @see com.gionee.client.activity.base.BaseFragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mImageScanAdapter.getSelectItems().clear();
        mSelfData.clear();
    }
}
