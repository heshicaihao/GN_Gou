package com.gionee.client.business.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.gionee.client.R;
import com.gionee.client.activity.hotorder.SubmitHotOrderActivity;
import com.gionee.client.activity.myfavorites.ImageGridAdapter;
import com.gionee.client.activity.myfavorites.MyFavoritesActivity;
import com.gionee.client.activity.myfavorites.MyFavoritesBaseFragment;
import com.gionee.client.activity.myfavorites.ZhiwuFavorFragment;
import com.gionee.client.activity.question.AskQuestionActivity;
import com.gionee.client.business.appDownload.GNDownloadListener;
import com.gionee.client.business.appDownload.InstallUtills;
import com.gionee.client.business.appDownload.ListDownloadManager;
import com.gionee.client.business.manage.GNActivityManager;
import com.gionee.client.model.BaiduStatConstants;
import com.gionee.client.model.GNDowanload;
import com.gionee.client.model.HttpConstants;
import com.gionee.client.view.adapter.AbstractListBaseAdapter;
import com.gionee.client.view.adapter.AbstractMyfavoriteBaseAdapter;
import com.gionee.client.view.widget.GNCustomDialog;
import com.gionee.framework.model.bean.MyBean;
import com.huewu.pla.MultiColumnListView;

public class DialogFactory {

	protected static final String TAG = "DialogFactory";

	public static Dialog createDeletePromptDialog(final Activity activity,
			int count) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Fragment fragment = ((MyFavoritesActivity) activity)
								.getCurrentFragment();
						if (fragment instanceof MyFavoritesBaseFragment) {
							AbstractListBaseAdapter adapter = ((MyFavoritesBaseFragment) fragment)
									.getMyFavoritesList().getListBaseAdapter();
							((AbstractMyfavoriteBaseAdapter) adapter)
									.batchRemoveFavorite();
						} else if (fragment instanceof ZhiwuFavorFragment) {
							MultiColumnListView multiColumnListView = ((ZhiwuFavorFragment) fragment)
									.getMultiColumnListView();
							ImageGridAdapter adapater = (ImageGridAdapter) multiColumnListView
									.getAdapter();
							adapater.deleteChooseFavor();
						}
					}
				});
		dialog.setNegativeButton(R.string.cancel, null);
		String infor = activity.getString(R.string.delete_message, count);
		SpannableStringBuilder style = new SpannableStringBuilder(infor);
		String countStr = Integer.toString(count);
		int index = infor.indexOf(countStr);
		style.setSpan(
				new ForegroundColorSpan(activity.getResources().getColor(
						R.color.tab_text_color_sel)), index,
				index + countStr.length() + 1,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		dialog.setMessage(style);
		return dialog;
	}

	public static Dialog createQustionContentMinDialog(final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setMessage(R.string.question_least_note);
		dialog.setPositiveButton(R.string.ok, null);
		return dialog;
	}

	public static Dialog createQustionContentMaxDialog(final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		View view = LayoutInflater.from(activity).inflate(
				R.layout.question_content_max, null);
		dialog.setContentView(view);
		dialog.setPositiveButton(R.string.ok, null);
		return dialog;
	}

	public static Dialog createAddDescriptionMinDialog(final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setMessage(R.string.add_description_less_note);
		dialog.setPositiveButton(R.string.ok, null);
		return dialog;
	}

	public static Dialog createAddDescriptionMaxDialog(final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setMessage(R.string.add_description_most_note);
		dialog.setPositiveButton(R.string.ok, null);
		return dialog;
	}

	public static Dialog createAppExitDialog(final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setMessage(R.string.is_exit);
		dialog.setNegativeButton(R.string.g_cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AndroidUtils.exitApp();
					}

				});
		dialog.setPositiveButton(R.string.exit_delayed,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}

				});
		return dialog;
	}

	public static Dialog createReloadDialog(final Activity activity,
			MyBean myBean) {
		final MyBean appInfo = myBean;
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.re_download);
		dialog.setMessage(R.string.is_certain_reload);
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeDownload(activity, appInfo);
						((GNDownloadListener) activity)
								.onStatusChanged(appInfo);
					}

					/**
					 * @param activity
					 * @param appInfo
					 * @author yuwei
					 * @description TODO
					 */
					private void removeDownload(final Activity activity,
							final MyBean appInfo) {
						long downloadId = appInfo
								.getLong(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L);
						InstallUtills.removeDownloadHistory(activity,
								downloadId);
						appInfo.put(
								HttpConstants.Data.AppRecommond.APP_DOWNLOAD_PERCENT_I,
								0);
						appInfo.put(
								HttpConstants.Data.AppRecommond.APP_STATUS_EM,
								GNDowanload.DownloadStatus.STATUS_INSTALL);
						appInfo.remove(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L);
					}
				});
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						long downloadId = appInfo
								.getLong(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L);
						InstallUtills.removeDownloadHistory(activity,
								downloadId);
						appInfo.put(
								HttpConstants.Data.AppRecommond.APP_DOWNLOAD_PERCENT_I,
								0);
						appInfo.put(
								HttpConstants.Data.AppRecommond.APP_STATUS_EM,
								GNDowanload.DownloadStatus.STATUS_INSTALL);
						appInfo.remove(HttpConstants.Data.AppRecommond.APP_DOWNLOAD_ID_L);
						((GNDownloadListener) activity)
								.onStatusChanged(appInfo);
						ListDownloadManager.getInstance(activity).download(
								activity, appInfo);
					}

				});
		return dialog;
	}

	public static Dialog createShareDialog(final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.share);
		View view = LayoutInflater.from(activity).inflate(
				R.layout.share_popwindow, null);
		dialog.setContentView(view);
		return dialog;
	}

	public static Dialog createShareDialogOnCommentDetailActivity(
			final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.share);
		View view = LayoutInflater.from(activity).inflate(
				R.layout.comment_detail_share, null);
		dialog.setContentView(view);
		return dialog;
	}

	public static Dialog createSelectProgramDialog(final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.select_program);
		View view = LayoutInflater.from(activity).inflate(
				R.layout.select_program, null);
		dialog.setContentView(view);
		return dialog;
	}

	public static Dialog createSaveDataDialog(final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setMessage(R.string.is_save_data);
		dialog.setNegativeButton(R.string.not_save,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
						AndroidUtils.exitActvityAnim(activity);
					}

				});
		dialog.setPositiveButton(R.string.save,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						((SubmitHotOrderActivity) activity).saveData();
						activity.finish();
						AndroidUtils.exitActvityAnim(activity);
					}

				});
		return dialog;
	}

	public static Dialog createSaveDataDialogWhenQuestionSubmit(
			final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setMessage(R.string.is_save_data);
		dialog.setNegativeButton(R.string.not_save,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						StatService.onEvent(activity,
								BaiduStatConstants.QUESTION_UNSAVE,
								BaiduStatConstants.QUESTION_UNSAVE);
						AndroidUtils.exitActvityAnim(activity);
					}

				});
		dialog.setPositiveButton(R.string.save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StatService.onEvent(activity,
								BaiduStatConstants.QUESTION_SAVE,
								BaiduStatConstants.QUESTION_UNSAVE);
						((AskQuestionActivity) activity).saveData();
						AndroidUtils.exitActvityAnim(activity);
					}
				});
		return dialog;
	}

	public static Dialog createAbandonEditDialog(final Activity activity) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setMessage(R.string.is_abandon_edit);
		dialog.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}

				});
		dialog.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
						AndroidUtils.exitActvityAnim(activity);
					}

				});
		return dialog;
	}

	public static Dialog createRemindTraffic(final Context activity,
			final OnClickListener onClick) {

		final int CHECK_FALSE = 0;
		final int CHECK_TRUE = 1;
		GNCustomDialog dialog = new GNCustomDialog(activity);
		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		final View contentView = layoutInflater.inflate(R.layout.start_tip,
				null);
		dialog.setContentView(contentView);
		dialog.setTitle(R.string.g_title);
		final ImageView checkbox = (ImageView) contentView
				.findViewById(R.id.tip_check_box);
		LinearLayout checkBoxLayout = (LinearLayout) contentView
				.findViewById(R.id.check_box_layout);
		final Drawable box = checkbox.getDrawable();
		box.setLevel(CHECK_TRUE);
		checkBoxLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (box.getLevel() == CHECK_TRUE) {
					box.setLevel(CHECK_FALSE);
				} else {
					box.setLevel(CHECK_TRUE);
				}
			}

		});
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						setTwoGInfo(activity, 0);
						GNActivityManager.getScreenManager().popAllActivity();
					}

				});
		dialog.setPositiveButton(R.string.ok,
				R.color.negative_button_normal_color,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						setTwoGInfo(activity, box.getLevel());
						onClick.onClick(contentView);
					}

				});
		dialog.setCancelable(false);
		return dialog;
	}

	public static Dialog createDeleteCommentDialog(final Activity activity,
			final View.OnLongClickListener onListener) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setMessage(R.string.delete_story_info);
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}

				});
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						onListener.onLongClick(null);
					}

				});
		return dialog;
	}

	//
	// private static String getMessage(Activity activity, int id) {
	// String msg = "";
	// if (activity != null) {
	// msg = activity.getResources().getString(id);
	// }
	// return msg;
	// }

	//
	// public static ProgressDialog createProgressDialog(final GnHomeActivity
	// activity) {
	// ProgressDialog progressDialog = new ProgressDialog(activity);
	// progressDialog.setMessage(getMessage(activity,
	// R.string.upgrade_check_message));
	// progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	// // progressDialog.setCancelable(false);
	// return progressDialog;
	// }

	// 设置点击"不再提示"
	private static void setTwoGInfo(Context context, int flag) {
		SharedPreferences.Editor mSPEditor = context.getSharedPreferences(
				"twoginfo", 0).edit();
		mSPEditor.putInt("twoginfo", flag);
		mSPEditor.commit();
	}

	// 设置点击“确定”
	// private static void setResumeInfo(Context context, int flag) {
	// SharedPreferences.Editor mSPEditor =
	// context.getSharedPreferences("resume", 0).edit();
	// mSPEditor.putInt("resume", flag);
	// mSPEditor.commit();
	// }

	public static Dialog ClearHistory(final Activity activity,
			final View.OnClickListener onListener, int type) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setMessage(type == 0 ? R.string.clear_history_goods_tip
				: R.string.clear_history_store_tip);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}

				});
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						onListener.onClick(null);
					}

				});
		return dialog;
	}

	public static Dialog createMsgDialog(final Activity activity,
			final View.OnClickListener onListener, int msgId) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setMessage(msgId);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						onListener.onClick(null);
					}

				});
		return dialog;
	}

	public static Dialog createMsgDialog(final Activity activity,
			final View.OnClickListener onListener, CharSequence msg) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setMessage(msg);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						onListener.onClick(null);
					}

				});
		return dialog;
	}

	public static Dialog closeCourseDialog(final Activity activity,
			CharSequence msg, final View.OnClickListener letf,
			final View.OnClickListener right) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setMessage(msg);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						letf.onClick(null);
						dialog.dismiss();
					}

				});
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						right.onClick(null);
					}

				});
		return dialog;
	}

	public static Dialog compareFullDialog(final Activity activity,
			final View.OnClickListener listener) {
		GNCustomDialog dialog = new GNCustomDialog(activity);
		dialog.setTitle(R.string.friendly_notify);
		dialog.setMessage(R.string.compare_list_is_full);
		dialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		dialog.setPositiveButton(R.string.goto_delete,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onClick(null);
					}

				});
		return dialog;
	}
}
