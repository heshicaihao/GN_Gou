package com.gionee.client.business.sina;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.framework.operation.utills.BitmapUtills;
import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.utils.Utility;

public class WeiBoShare {
    private static final int MIN_THUMB_SIZE = 150;
    private static final float MIN_DENSITY = 2;
    private static final int MAX_LENGTH = 180 * 1024;
    private static final int THUMB_SIZE = 200;
    private static final String TAG = "WeiBoShare";

    private IWeiboShareAPI mWeiboShareAPI;
    private Context mContext;

    public WeiBoShare(IWeiboShareAPI api, Context context) {
        mWeiboShareAPI = api;
        mContext = context;
    }

    public void sendShareMsg(String title, String description, Bitmap bitmap, String url) {
        LogUtils.log(TAG, "title=" + title + " description=" + description + "  bitmap=" + bitmap + "  url="
                + url);

        if (checkParameters(title, url, description) || bitmap == null) {
            Toast.makeText(mContext, R.string.share_faild, Toast.LENGTH_SHORT).show();
            return;
        }
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(description, title);
        bitmap = BitmapUtills.compressFromBitmap(bitmap, getThumSize(), getThumSize(), MAX_LENGTH);
        weiboMessage.imageObject = getImageObj(bitmap);
        weiboMessage.mediaObject = getWebpageObj(title, "", bitmap, url);
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(request);
    }

    private int getThumSize() {
        LogUtils.log(TAG, LogUtils.getFunctionName());
        int size = THUMB_SIZE;
        if (AndroidUtils.getDensity(mContext) < MIN_DENSITY) {
            size = MIN_THUMB_SIZE;
        }
        return size;
    }

    private boolean checkParameters(String title, String url, String description) {
        return TextUtils.isEmpty(title) || url.equals("file:///android_asset/share_unnetwork.html")
                || url.equals("file:///android_asset/unnetwork.html");
    }

    private BaseMediaObject getWebpageObj(String title, String description, Bitmap bitmap, String url) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = url;
        mediaObject.defaultText = "Webpage 默认文案";
        return mediaObject;
    }

    private TextObject getTextObj(String description, String title) {
        TextObject textObject = new TextObject();
        if (TextUtils.isEmpty(description) || description.startsWith("http")) {
            description = "分享推荐:" + title;
        }
        textObject.text = description;
        return textObject;
    }

    private ImageObject getImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

}
