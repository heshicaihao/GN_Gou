package com.gionee.client.business.upgradeplus;

import java.text.DecimalFormat;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.business.upgradeplus.common.UpgradeUtils;
import com.gionee.client.business.util.AndroidUtils;
import com.gionee.client.business.util.LogUtils;
import com.gionee.client.model.Constants;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.widget.CustomProgressDialog;
import com.gionee.client.view.widget.FlipImageView;
import com.gionee.client.view.widget.GNCustomDialog;
import com.gionee.client.view.widget.GNUpgrateDialog;
import com.gionee.framework.operation.net.GNImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.upgrate.download.FileService;
import com.upgrate.manage.UpgrateDataManage;

/**
 * com.gionee.client.upgradeplus.AppUpgradeDialogFactory
 * 
 * @author hcy <br/>
 *         create at 2013-7-22 下午4:53:05
 */
public class UpgradeDialogFactory {

	protected static final String TAG = "AppUpgradeManager";

	// /**
	// * @param activity
	// * @param manager
	// * @return
	// */
	// public static Dialog createDownloadCompleteDialog(final Activity
	// activity,
	// final UpgradeManager manager) {
	// final GNCustomDialog dialog = new GNCustomDialog(activity);
	// dialog.setCancelable(false);
	// dialog.setTitle(R.string.upgrade_download_complete_title);
	// dialog.setMessage(R.string.upgrade_download_complete_message);
	// dialog.setNegativeButton(R.string.upgrade_donot_upgrade,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// manager.setStatus(Status.PENDING);
	// dialog.dismiss();
	// if (manager.isForceMode()) {
	// UpgradeUtils.closeApp(activity);
	// }
	// }
	//
	// });
	// dialog.setPositiveButton(R.string.upgrade_upgrade_now,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// manager.setStatus(Status.PENDING);
	// manager.startInstallApp();
	// }
	//
	// });
	// return dialog;
	// }

	// /**
	// * @param activity
	// * @param manager
	// * @return
	// */
	// public static Dialog createForceVersionDialog(final Activity activity,
	// final UpgradeManager manager) {
	// final GNCustomDialog dialog = new GNCustomDialog(activity);
	// dialog.setTitle(R.string.upgrade_title);
	// dialog.setMessage(R.string.upgrade_force);
	// dialog.setNegativeButton(R.string.upgrade_donot_download,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// UpgradeUtils.closeApp(activity);
	// }
	//
	// });
	// dialog.setPositiveButton(R.string.upgrade_download_now,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// manager.startDownload();
	// }
	//
	// });
	// dialog.setCancelable(false);
	// return dialog;
	//
	// }

	// /**
	// * @param activity
	// * @param manager
	// * @return
	// */
	// public static Dialog createHasNewVersionDialog(final Activity activity,
	// final UpgradeManager manager) {
	// final int CHECK_FALSE = 0;
	// final int CHECK_TRUE = 1;
	// final GNCustomDialog dialog = new GNCustomDialog(activity);
	// dialog.setTitle(R.string.upgrade_title);
	// LayoutInflater layoutInflater = LayoutInflater.from(activity);
	// View contentView = layoutInflater.inflate(R.layout.start_upgrate_tip,
	// null);
	// ScrollView scrollView = (ScrollView) contentView
	// .findViewById(R.id.start_tip);
	// scrollView.getLayoutParams().height = AndroidUtils
	// .dip2px(activity, 240);
	// scrollView.invalidate();
	// TextView message = (TextView) contentView
	// .findViewById(R.id.gn_dialog_message);
	// message.setText(buildNewVersionReleseNote(
	// manager.getAppNewVerionInfo(), activity, manager));
	//
	// dialog.setContentView(contentView);
	//
	// final ImageView checkbox = (ImageView) contentView
	// .findViewById(R.id.tip_check_box);
	// LinearLayout checkBoxLayout = (LinearLayout) contentView
	// .findViewById(R.id.check_box_layout);
	// final Drawable box = checkbox.getDrawable();
	// box.setLevel(manager.isDisplayThisVersion() ? CHECK_FALSE : CHECK_TRUE);
	// checkBoxLayout.setOnClickListener(new OnClickListener() {
	//
	// public void onClick(View v) {
	//
	// if (box.getLevel() == CHECK_TRUE) {
	// box.setLevel(CHECK_FALSE);
	// manager.setDisplayFlag(true, manager.getNewVersion());
	// } else {
	// manager.setDisplayFlag(false, manager.getNewVersion());
	// box.setLevel(CHECK_TRUE);
	// }
	// }
	//
	// });
	// dialog.setNegativeButton(R.string.upgrade_donot_download,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// if (manager.isForceMode()) {
	// manager.showUserFeedback(UserFeedback.MESSAGE_FORCE_MODE);
	// }
	// }
	//
	// });
	// dialog.setPositiveButton(R.string.upgrade_download_now,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// if (isNetworkAvailable(activity)) {
	// manager.startDownload();
	// } else {
	// manager.showUserFeedback(UserFeedback.MESSAGE_NO_NETWORK);
	// }
	// }
	//
	// });
	// if (manager.isForceMode()) {
	// dialog.setCancelable(false);
	// }
	// dialog.setOwnerActivity(activity);
	// return dialog;
	// }

	/**
	 * 
	 * @param activity
	 * @param data
	 * @param flag
	 *            0表示显示是否升级提示，1表示显示是否安装提示
	 * @param onClickListener
	 * @return
	 */
	public static GNUpgrateDialog createHasNewVersionDialog(
			final Activity activity, JSONObject data,
			final boolean fileIsExitAndComplete, OnClickListener onClickListener) {
		final int CHECK_FALSE = 0;
		final int CHECK_TRUE = 1;
		GNUpgrateDialog dialog = new GNUpgrateDialog(activity);
		dialog.setTitle(R.string.upgrade_title);
		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		final View contentView = layoutInflater.inflate(
				R.layout.check_upgrate_tip, null);
		final TextView message = (TextView) contentView
				.findViewById(R.id.gn_dialog_message);
		message.setText(Html.fromHtml(data
				.optString(HttpConstants.Data.Upgrate.DESCRIPTION_S)));
		message.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ScrollView scrollView = (ScrollView) contentView
						.findViewById(R.id.start_tip);
				int height = 0;
				if (message.getLineCount() > 3) {
					int perHeight = message.getHeight()
							/ message.getLineCount();
					height = (int) (perHeight * 3.5);
				} else {
					height = message.getHeight();
				}
				scrollView.getLayoutParams().height = height;
				scrollView.invalidate();
			}
		});
		ImageView ivTop = (ImageView) contentView.findViewById(R.id.iv_top);
		GNImageLoader.getInstance().loadBitmap(
				data.optString(HttpConstants.Data.Upgrate.BG_IMG_S), ivTop);
		JSONObject staffObject = data
				.optJSONObject(HttpConstants.Data.Upgrate.STAFF_JO);
		final FlipImageView icon = (FlipImageView) contentView
				.findViewById(R.id.user_head_img);
		icon.setInterpolator(new DecelerateInterpolator());
		loadEggImage(contentView, staffObject, icon);
		TextView say = (TextView) contentView.findViewById(R.id.title);
		say.setText(data.optString(HttpConstants.Data.Upgrate.SAY_S));
		TextView newVersion = (TextView) contentView
				.findViewById(R.id.new_version);
		newVersion.setText(activity.getString(R.string.new_version,
				data.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S),
				data.optString(HttpConstants.Data.Upgrate.VERSION_SIZE_S)));
		final ImageView checkbox = (ImageView) contentView
				.findViewById(R.id.tip_check_box);
		LinearLayout checkBoxLayout = (LinearLayout) contentView
				.findViewById(R.id.check_box_layout);
		final Drawable box = checkbox.getDrawable();
		boolean isTip = true;
		Button left = (Button) contentView.findViewById(R.id.bt_left);
		Button right = (Button) contentView.findViewById(R.id.bt_right);
		if (!fileIsExitAndComplete) {
			isTip = UpgrateDataManage.getDownLoadingIsTip(activity,
					AndroidUtils.getAppVersionName(activity));
			left.setText(activity.getString(R.string.upgrade_donot_download));
			right.setText(activity.getString(R.string.upgrade_download_now));
			right.setBackgroundResource(R.drawable.menu_item_bg);
			right.setTextColor(activity.getResources().getColor(
					R.color.menu_text));
		} else {
			isTip = UpgrateDataManage.getInstallIsTip(activity,
					AndroidUtils.getAppVersionName(activity));
			left.setText(activity.getString(R.string.upgrade_donot_upgrade));
			right.setText(activity.getString(R.string.install_at_once));
			right.setBackgroundResource(R.drawable.install_bg);
			right.setTextColor(activity.getResources().getColor(R.color.white));
		}
		left.setOnClickListener(onClickListener);
		right.setOnClickListener(onClickListener);
		box.setLevel(isTip ? CHECK_FALSE : CHECK_TRUE);
		checkBoxLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (box.getLevel() == CHECK_TRUE) {
					box.setLevel(CHECK_FALSE);
				} else {
					box.setLevel(CHECK_TRUE);
					if (fileIsExitAndComplete) {
						StatService.onEvent(activity, "upgrade_nonetips",
								"install");
					} else {
						StatService.onEvent(activity, "upgrade_nonetips",
								"download");
					}
				}
			}

		});
		dialog.setContentView(contentView);
		dialog.setOwnerActivity(activity);
		return dialog;
	}

	/**
	 * @param activity
	 * @param manager
	 * @return
	 */
	public static GNUpgrateDialog createUpgrateProgressDialog(
			final Activity activity, JSONObject data,
			OnClickListener onClickListener) {
		final GNUpgrateDialog dialog = new GNUpgrateDialog(activity);
		dialog.setTitle(R.string.upgrade_title);
		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		final View contentView = layoutInflater.inflate(
				R.layout.start_downloading_tip, null);
		final TextView message = (TextView) contentView
				.findViewById(R.id.gn_dialog_message);
		message.setText(Html.fromHtml(data
				.optString(HttpConstants.Data.Upgrate.DESCRIPTION_S)));
		message.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ScrollView scrollView = (ScrollView) contentView
						.findViewById(R.id.start_tip);
				int height = 0;
				if (message.getLineCount() > 3) {
					int perHeight = message.getHeight()
							/ message.getLineCount();
					height = (int) (perHeight * 3.5);
				} else {
					height = message.getHeight();
				}
				scrollView.getLayoutParams().height = height;
				scrollView.invalidate();
			}
		});
		ImageView ivTop = (ImageView) contentView.findViewById(R.id.iv_top);
		GNImageLoader.getInstance().loadBitmap(
				data.optString(HttpConstants.Data.Upgrate.BG_IMG_S), ivTop);
		JSONObject staffObject = data
				.optJSONObject(HttpConstants.Data.Upgrate.STAFF_JO);
		final FlipImageView icon = (FlipImageView) contentView
				.findViewById(R.id.user_head_img);
		icon.setInterpolator(new DecelerateInterpolator());
		loadEggImage(contentView, staffObject, icon);
		TextView say = (TextView) contentView.findViewById(R.id.title);
		say.setText(data.optString(HttpConstants.Data.Upgrate.SAY_S));
		TextView newVersion = (TextView) contentView
				.findViewById(R.id.new_version);
		String versionName = data
				.optString(HttpConstants.Data.Upgrate.VERSION_NAME_S);
		newVersion.setText(activity.getString(R.string.new_version,
				versionName,
				data.optString(HttpConstants.Data.Upgrate.VERSION_SIZE_S)));
		ProgressBar downloadbar = (ProgressBar) contentView
				.findViewById(R.id.progress_bar);
		downloadbar.setMax(UpgrateDataManage.getApkAllSize(activity,
				versionName));
		downloadbar.setProgress(UpgrateDataManage.getApkDownloadSize(activity,
				versionName));
		int p = 0;
		if (downloadbar.getMax() != 0) {
			float result = (float) downloadbar.getProgress()
					/ (float) downloadbar.getMax();
			p = (int) (result * 100);
		}
		TextView progressPercent = (TextView) contentView
				.findViewById(R.id.progress_percent);
		TextView progressNumber = (TextView) contentView
				.findViewById(R.id.progress_number);
		progressPercent.setText(p + "%");
		progressNumber.setText((p == 0 ? "0k" : UpgrateDataManage
				.kToM(downloadbar.getProgress()))
				+ "/"
				+ data.optString(HttpConstants.Data.Upgrate.VERSION_SIZE_S));

		Button left = (Button) contentView
				.findViewById(R.id.bt_cancel_download);
		Button right = (Button) contentView
				.findViewById(R.id.bt_continue_download);
		left.setOnClickListener(onClickListener);
		right.setOnClickListener(onClickListener);
		dialog.setContentView(contentView);
		dialog.setOwnerActivity(activity);
		return dialog;
	}

	private static void loadEggImage(View contentView, JSONObject staffObject,
			final FlipImageView icon) {
		String designerIcon = staffObject
				.optString(HttpConstants.Data.Upgrate.DESIGNER_ICON_S);
		GNImageLoader.getInstance().loadBitmap(designerIcon, icon,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						if (loadedImage != null) {
							Drawable drawable = new BitmapDrawable(loadedImage);
							drawable.setBounds(0, 0, loadedImage.getWidth(),
									loadedImage.getHeight());
							icon.setDrawable(drawable);
						}
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				});
		ImageView ivDefault = (ImageView) contentView
				.findViewById(R.id.iv_default);
		GNImageLoader.getInstance().loadBitmap(
				staffObject.optString(HttpConstants.Data.Upgrate.EGG_S),
				ivDefault, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						if (loadedImage != null) {
							Drawable drawable = new BitmapDrawable(loadedImage);
							drawable.setBounds(0, 0, loadedImage.getWidth(),
									loadedImage.getHeight());
							icon.setFlippedDrawable(drawable);
							icon.setCanAnimation(true);
						}
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				});
	}

	// /**
	// * @param info
	// * @param activity
	// * @param manager
	// * @return
	// */
	// private static String buildNewVersionReleseNote(AppNewVersionInfo info,
	// final Activity activity, UpgradeManager manager) {
	// StringBuilder sb = new StringBuilder();
	// float size = info.getSize();
	// DecimalFormat df = new DecimalFormat("0.00");
	// String fileSize = df.format(size / (1024 * 1024));
	// sb.append(activity.getString(R.string.upgrade_now_version))
	// .append(UpgradeUtils.getVersion(activity, "")).append("\n");
	// sb.append(
	// activity.getResources().getString(R.string.upgrade_new_version))
	// .append(info.getVersion())
	// .append(" (" + activity.getString(R.string.upgrade_file_size)
	// + " " + fileSize + "MB)").append("\n")
	// .append(activity.getString(R.string.upgrade_new_feature_desc))
	// .append("\n").append(info.getReleaseNote()).append("\n")
	// .append(activity.getString(R.string.upgrade_new_down_desc1))
	// .append("\n")
	// .append(activity.getString(R.string.upgrade_new_down_desc2))
	// .append("\n")
	// .append(activity.getString(R.string.upgrade_new_down_desc3))
	// .append("\n");
	// LogUtils.log(TAG, LogUtils.getThreadName() + "Info:" + sb.toString());
	// return sb.toString();
	// }

	private static boolean isNetworkAvailable(final Activity activity) {
		return Constants.NET_UNABLE != AndroidUtils.getNetworkType(activity);
	}

	/**
	 * @param activity
	 * @return
	 */
	public static ProgressDialog createProgressDialog(final Activity activity) {
		ProgressDialog progressDialog = new ProgressDialog(activity,
				R.style.custom_dialog_style);
		progressDialog.setMessage(activity
				.getString(R.string.update_version_ing));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		return progressDialog;
	}

	/**
	 * @param activity
	 * @return
	 */
	public static CustomProgressDialog createDownloadProgressDialog(
			final Activity activity) {
		CustomProgressDialog progressDialog = new CustomProgressDialog(activity);
		progressDialog.setTitle(activity
				.getString(R.string.upgrade_start_download));
		return progressDialog;
	}

	// /**
	// * @param activity
	// * @param manager
	// * @return a no network dialog
	// */
	// public static Dialog createNoNetworkDialog(final Activity activity,
	// final UpgradeManager manager) {
	// final GNCustomDialog dialog = new GNCustomDialog(activity);
	// dialog.setTitle(R.string.upgrade_tip);
	// dialog.setCancelable(true);
	// dialog.setMessage(R.string.upgrade_no_net);
	// dialog.setPositiveButton(R.string.upgrade_menu_preferences,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// Intent intent = new Intent(
	// android.provider.Settings.ACTION_SETTINGS);
	// activity.startActivity(intent);
	// }
	//
	// });
	// dialog.setNegativeButton(R.string.menu_quit,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// UpgradeUtils.closeApp(activity);
	// }
	//
	// });
	// return dialog;
	// }

	/**
	 * @param activity
	 * @param message
	 * @return
	 */
	public static Toast createToastMessage(final Activity activity,
			String message) {
		return Toast.makeText(activity, message, Toast.LENGTH_LONG);
	}
}
