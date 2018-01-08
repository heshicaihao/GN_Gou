package com.gionee.client.activity.imageScan;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.model.Constants;
import com.gionee.client.model.ImageBean;
import com.gionee.framework.operation.business.LocalBuisiness;
import com.gionee.framework.operation.net.GNImageLoader;
import com.gionee.framework.operation.page.PageCacheManager;

/**
 * @blog http://blog.csdn.net/xiaanming
 * 
 * @author xiaanming
 * 
 * 
 */
public class ImageFolderScanActivity extends BaseFragmentActivity {
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private List<ImageBean> mImageList = new ArrayList<ImageBean>();
    private static final int SCAN_OK = 1;
    private GroupAdapter mGroupAdapter;
    private GridView mGroupGridView;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:

                    mGroupAdapter = new GroupAdapter(ImageFolderScanActivity.this,
                            mImageList = subGroupOfImage(mGruopMap));
                    mGroupGridView.setAdapter(mGroupAdapter);
                    break;
                default:
                    break;
            }
        }

    };

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            GNImageLoader.getInstance().getImageLoader().getMemoryCache().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    /* (non-Javadoc)
     * @see com.gionee.client.activity.base.BaseFragmentActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PageCacheManager.LookupPageData(ImageScanAndSelectActivity.class.getName()).clear();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        GNImageLoader.getInstance().init(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_folder);
        mGroupGridView = (GridView) findViewById(R.id.main_grid);
        getImages();
        mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> childList = mGruopMap.get(mImageList.get(position).getFolderName());

                Intent mIntent = new Intent(ImageFolderScanActivity.this, ImageScanAndSelectActivity.class);
                mIntent.putStringArrayListExtra("data", (ArrayList<String>) childList);
                startActivityForResult(mIntent, Constants.ActivityResultCode.RESULT_CODE_IMAGE_SELECT);

            }
        });

    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int reqeustCode, int resultCode, Intent arg2) {
        super.onActivityResult(reqeustCode, resultCode, arg2);
        if (resultCode == Constants.ActivityResultCode.RESULT_CODE_IMAGE_SELECT) {
            finish();
        }
    }

    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.sd_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        LocalBuisiness.getInstance().postRunable(queryLocalImageRunnable);

    }

//    /* (non-Javadoc)
//     * @see android.app.Activity#onTrimMemory(int)
//     */
////    @Override
////    public void onTrimMemory(int level) {
////        super.onTrimMemory(level);
////        getImages();
////    }

    private Runnable queryLocalImageRunnable = new Runnable() {

        @Override
        public void run() {

            try {
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] {"_id", "_data"}, null, null, null);
                while (cursor.moveToNext()) {
                    String path = cursor.getString(1);
                    String lastIndex = path.substring(path.lastIndexOf(".") + 1, path.length());
                    if (lastIndex.equals("jpg") || lastIndex.equals("png") || lastIndex.equals("jpeg")) {
                        String parentName = new File(path).getParentFile().getName();
                        if (parentName.equals("hotorderimgcache")) {
                            continue;
                        }
                        if (!mGruopMap.containsKey(parentName)) {
                            List<String> chileList = new ArrayList<String>();
                            chileList.add(path);
                            mGruopMap.put(parentName, chileList);
                        } else {
                            mGruopMap.get(parentName).add(path);
                        }
                    }
                }
                cursor.close();
                mHandler.sendEmptyMessage(SCAN_OK);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
        if (mGruopMap.size() == 0) {
            return null;
        }
        List<ImageBean> list = new ArrayList<ImageBean>();
        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));
            File file = new File(value.get(0)).getParentFile();
            mImageBean.setmLastModifiyTime(file.lastModified());
            list.add(mImageBean);
        }
        Collections.sort(list);
        return list;

    }
}
