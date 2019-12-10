package com.gionee.client.business.filter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.gionee.client.business.appDownload.InstallUtills;
import com.gionee.client.model.Constants;

/**
 * WeiXinClientHandler
 * 
 * @author heshicaihao <br/>
 * @date create at 2015-11-16
 * @description TODO处理微信客户端url
 */
public class WeiXinClientHandler extends IUrlHandler {

	private static final int LAST_USABLE_WEIXIN_VERSION = 6;

	@Override
	public boolean handleRequest(Activity context, String url) {
		for (UrlFilterConfig.WeiXinClientEnum scheme : UrlFilterConfig.WeiXinClientEnum
				.values()) {
			if (url.contains(scheme.getValue())) {
				if (isWeiXinClientUseable(context)) {
					gotoWeiXin(context, url);
					return true;
				}

			}
		}

		return mSuccessor.handleRequest(context, url);
	}

	private boolean isWeiXinClientUseable(Activity context) {
		final String packageName = Constants.WEIXIN_PACKAGENAME;
		PackageInfo info = InstallUtills.getPackgeInfo(context, packageName);
		if (info != null && !(info.versionCode < LAST_USABLE_WEIXIN_VERSION)) {
			return true;
		}
		return true;
	}

	/**
	 * @param context
	 *            TODO
	 * @param url
	 * @author heshicaihao
	 * @description TODO
	 */
	public static void gotoWeiXin(Activity context, String url) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			intent.addCategory(Intent.CATEGORY_BROWSABLE);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
