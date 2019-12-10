package com.gionee.framework.event;

import java.io.InputStream;
import android.content.Context;
import android.util.Log;

import com.gionee.framework.model.bean.MyBean;
import com.gionee.framework.model.config.ControlKey;
import com.gionee.framework.operation.net.NetUtil;

public class AnologDataConnector implements INetConnector {

    @Override
    public String openUrl(MyBean params, Context context) throws Exception {
        try {
            String url = params.getString(ControlKey.request.control.__url_s);
            String port = url.substring(url.lastIndexOf("/") + 1);
            InputStream in = context.getAssets().open("AnologData/" + port + ".txt");
            String data_json = NetUtil.read(in);
            return data_json;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "";
    }

}
