/**
 * @author yangxiong
 * V 1.0.0
 * Create at 2015-2-5 下午05:03:37
 */
package com.gionee.client.business.shareTool;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.gionee.client.R;
import com.gionee.client.activity.base.BaseFragmentActivity;
import com.gionee.client.business.util.DialogFactory;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.view.widget.GNCustomDialog;
import com.gionee.framework.operation.utills.BitmapUtills;

/**
 * com.gionee.client.business.shareTool.GNShareDialog
 * 
 * @author yangxiong <br/>
 * @date create at 2015-2-5 下午05:03:37
 * @description TODO
 */
public class GNShareDialog implements OnClickListener {
    private final String TAG = "GNShareDialog";
    private ShareModel mShareModel;
    protected GNCustomDialog mDialog;
    private BaseFragmentActivity mContext;
    private String mTypeId;

    /**
     * @param typeid
     *            分享计分类型id
     */
    public GNShareDialog(ShareModel shareModel, BaseFragmentActivity context, String typeid) {
        super();
        this.mShareModel = shareModel;
        this.mContext = context;
        this.mTypeId = typeid;
    }

    public GNShareDialog(BaseFragmentActivity context) {
        super();
        this.mContext = context;
    }

    public void setmTypeId(String mTypeId) {
        this.mTypeId = mTypeId;
    }

    public void setmShareModel(ShareModel mShareModel) {
        this.mShareModel = mShareModel;
    }

    public ShareModel getmShareModel() {
        return mShareModel;
    }

    /**
     * @param typeid
     *            分享计分类型id
     */
    public void showShareDialog() {
        try {
            if (mDialog == null) {
                mDialog = (GNCustomDialog) DialogFactory.createShareDialog(mContext);
            }
            if (mDialog != null) {
                mDialog.show();
                mDialog.setDismissBtnVisible();
                mDialog.setCanceledOnTouchOutside(true);
                mDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        LogUtils.log(TAG, LogUtils.getThreadName());
                        BitmapUtills.bitmapRecycle(mShareModel.getBitmap());
                        mShareModel.setBitmap(null);
                    }
                });
                mDialog.getContentView().findViewById(R.id.share_weixin).setOnClickListener(this);
                mDialog.getContentView().findViewById(R.id.share_friends).setOnClickListener(this);
                mDialog.getContentView().findViewById(R.id.share_weibo).setOnClickListener(this);
                mDialog.getContentView().findViewById(R.id.share_qq_friend).setOnClickListener(this);
                mDialog.getContentView().findViewById(R.id.share_qq_zone).setOnClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeShareDialog() {
        LogUtils.log(TAG, LogUtils.getThreadName());
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        LogUtils.log(TAG, LogUtils.getThreadName());
        String imageUrl = mShareModel.getImageUrl();
        switch (v.getId()) {
            case R.id.share_weixin:
                if (!TextUtils.isEmpty(imageUrl)) {
                    mContext.shareToWeixin(false, mShareModel.getTitle(), mShareModel.getText(),
                            mShareModel.getImageUrl(), mShareModel.getUrl());
                } else {
                    mContext.shareToWeixin(false, mShareModel.getTitle(), mShareModel.getText(),
                            mShareModel.getBitmap(), mShareModel.getUrl());
                }
//                StatService.onEvent(mContext, "b_share", "weixin");
                closeShareDialog();
                if (ShareTool.isWXInstalled(mContext)) {
                    mContext.cumulateScore(mTypeId);
                }
                break;
            case R.id.share_friends:
                if (!TextUtils.isEmpty(imageUrl)) {
                    mContext.shareToWeixin(true, mShareModel.getText(), mShareModel.getText(),
                            mShareModel.getImageUrl(), mShareModel.getUrl());
                } else {
                    mContext.shareToWeixin(true, mShareModel.getText(), mShareModel.getText(),
                            mShareModel.getBitmap(), mShareModel.getUrl());
                }
//                StatService.onEvent(mContext, "b_share", "friends");
                closeShareDialog();
                if (ShareTool.isWXInstalled(mContext)) {
                    mContext.cumulateScore(mTypeId);
                }
                break;
            case R.id.share_weibo:
                mContext.shareToWeibo(mShareModel.getTitle(), mShareModel.getText(), mShareModel.getBitmap(),
                        mShareModel.getUrl());
//                StatService.onEvent(mContext, "b_share", "weibo");
                closeShareDialog();
                if (mContext.isWeiboValid()) {
                    mContext.cumulateScore(mTypeId);
                }
                break;
            case R.id.share_qq_friend:
                mContext.shareToQq(ShareTool.PLATFORM_QQ_FRIEND, mShareModel.getTitle(),
                        mShareModel.getText(), mShareModel.getImageUrl(), mShareModel.getUrl());
                closeShareDialog();
                if (ShareTool.isQQValid(mContext)) {
                    mContext.cumulateScore(mTypeId);
                }
                break;
            case R.id.share_qq_zone:
                mContext.shareToQq(ShareTool.PLATFORM_QQ_ZONE, mShareModel.getTitle(), mShareModel.getText(),
                        mShareModel.getImageUrl(), mShareModel.getUrl());
                closeShareDialog();
                if (ShareTool.isQQValid(mContext)) {
                    mContext.cumulateScore(mTypeId);
                }
                break;
            default:
                break;
        }

    }

}
