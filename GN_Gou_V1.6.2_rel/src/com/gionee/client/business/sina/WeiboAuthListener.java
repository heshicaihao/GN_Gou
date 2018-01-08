package com.gionee.client.business.sina;

import android.os.Bundle;

public interface WeiboAuthListener {

    public void onCancel();

    public void onComplete(Bundle values);

    void onWeiboException(Exception e);
}
