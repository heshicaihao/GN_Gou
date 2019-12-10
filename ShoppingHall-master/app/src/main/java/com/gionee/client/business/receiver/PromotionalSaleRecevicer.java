/*
 * SalesMsgRecevicer.java
 * classes : com.gionee.client.business.push.SalesMsgRecevicer
 * @author wuhao
 * Create at 2015-1-28 上午10:56:26
 */
package com.gionee.client.business.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import com.gionee.client.service.PromotionalSaleService;

/**
 * @author wuhao
 * @date create at 2015-2-2 下午2:01:37
 * @description
 */
public class PromotionalSaleRecevicer extends BroadcastReceiver {

    private long mTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals(PromotionalSaleService.PROMOTIONAL_SALE_RECEVICE)
                    || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                // 防止service被杀掉
                startServer(context);
            }
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (State.CONNECTED == connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                        || State.CONNECTED == connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                                .getState()) {
                    if (mTime == 0) {
                        mTime = System.currentTimeMillis();
                    } else {
                        if (System.currentTimeMillis() - mTime < 10*1000) {
                            mTime = System.currentTimeMillis();
                            return;
                        }
                        mTime = System.currentTimeMillis();
                    }
                    context.sendBroadcast(new Intent(PromotionalSaleService.NETWORK_SALE_SERVICE));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param context
     */
    private void startServer(Context context) {
        Intent intent = new Intent(PromotionalSaleService.PROMOTIONAL_SALE_SERVICE);
        context.startService(intent);
    }

}
