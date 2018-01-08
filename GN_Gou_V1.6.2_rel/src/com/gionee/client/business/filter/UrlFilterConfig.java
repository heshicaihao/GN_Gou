// Gionee <yuwei><2013-12-19> add for CR00821559 begin
/*
 * UrlFilter.java
 * classes : com.gionee.client.model.UrlFilter
 * @author yuwei
 * V 1.0.0
 * Create at 2013-12-19 下午12:03:55
 */
package com.gionee.client.business.filter;

import com.gionee.client.model.Constants;

/**
 * UrlFilter
 * 
 * @author yuwei <br/>
 * @date create at 2013-12-19 下午12:03:55
 * @description TODO 配置需要过滤处理的url
 */
public class UrlFilterConfig {

    public static enum HttpUrlEnum {
        HTTP_URL("http://"), HTTPS_URL("https://");
        private String mValue;

        HttpUrlEnum(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return this.mValue;
        }
    }

    public static enum TaobaoClientEnum {
        TAOBAO_CLIENT_URL("taobao://");
//        TABAO_WEBVIEW_URL("taobaowebview://"),
//        TAOBAO_H5_URL("http://h5.m.taobao.com/awp/core/detail.htm");
        private String mValue;

        TaobaoClientEnum(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return this.mValue;
        }
    }
    
    public static enum WeiXinClientEnum {
    	WEIXIN_CLIENT_URL(Constants.WEIXIN_PAY);
        private String mValue;

        WeiXinClientEnum(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return this.mValue;
        }
    }

    public static enum BlackListUrlEnum {
        APPLE_APP_URL("http://itunes.apple.com"), TMALL_CLIENT_URL("tmall://"), ITMALL_MOBILE_URL("itmall://"), ALIPAY_CLIENT_URL(
                "alipays://"), JHS_CLIENT_URL("jhs://"), WANGTALK_CLIENT_URL("wangtalk://"), LAIWANG_CLIENT_URL(
                "laiwang://"), T_APP_CLIENT_URL("taoapp://"), T_YITAO_URL("etao://"), T_COUPON_CLIENT_URL(
                "taobaocoupon://"), T_TRAVEL_CLIETN_URL("taobaotravel://");
        private String mValue;

        BlackListUrlEnum(String value) {
            this.mValue = value;
        }

        public String getValue() {
            return this.mValue;
        }
    }

}
//Gionee <yuwei><2013-12-19> add for CR00821559 end