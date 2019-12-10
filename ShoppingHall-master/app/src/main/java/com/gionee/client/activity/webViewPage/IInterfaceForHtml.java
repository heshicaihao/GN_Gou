/*
 * IInterfaceForHtml.java
 * classes : com.gionee.client.activity.webViewPage.IInterfaceForHtml
 * @author yuwei
 * 
 * Create at 2015-2-9 下午2:29:09
 */
package com.gionee.client.activity.webViewPage;


/**
 * com.gionee.client.activity.webViewPage.IInterfaceForHtml
 * @author yuwei <br/>
 * create at 2015-2-9 下午2:29:09
 *@description 供H5调用的接口
 */
public interface IInterfaceForHtml {

    // h5调用，调起分享对话框，用于分享网页
    public abstract void showWebShareDialog();

    //H5调用接口: 退出webview。
    public abstract void exitWebView();

    //h5调用接口 获取apk版本名称
    public abstract String getVersionName();

    //h5调用接口 设置分享内容
    public abstract void setmDescription(String description);

    // h5调用接口 设置分享标题
    public abstract void setShareTitle(String mTitle);

    // h5调用接口 设置分享缩略图
    public abstract void setmThumbBitmap(String fileName);
    
    //h5调用接口 设置分享url
    public abstract void setShareUrl(String url);

    // h5调用接口 分享到微信
    public abstract void shareToWeixin(boolean isShareToFriends);

    // h5调用接口 分享到微博
    public abstract void shareToWeibo();

    //h5调用接口 分享到"QQ好友":2, "QQ空间":3
    public abstract void share(int platform);

    // h5调用接口, 调起分享对话框，分享购物大厅这个应用的相关链接
    public abstract void shareApp();

    //h5跳转到砍价页面
    public abstract void gotoCutPriceInterface();

    //h5跳转到同款列表
    public abstract void showSameStyleList(String data);
    
    //H5跳转到晒单页面 orderId:砍价订单id;  hotOrderId: 晒单id, 即实际为其对应知物ID号
    public abstract void gotoHotOrderInterface(String orderId, String hotOrderId, String nickname);
    
    //H5跳转到活动商品列表
    public abstract void gotoHuodongProductList();
}