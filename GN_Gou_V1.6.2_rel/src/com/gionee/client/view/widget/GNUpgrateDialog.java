package com.gionee.client.view.widget;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gionee.client.R;
import com.gionee.client.business.util.AndroidUtils;

public class GNUpgrateDialog extends Dialog {

	private Resources mResources;
	protected Activity mActivity;

	protected Button mPositiveButton;
	protected Button mNegativeButton;

	private final GameDialogParams mDialogParams;

	public GNUpgrateDialog(Context context) {
		super(context, R.style.UpgrateDialog);
		mActivity = (Activity) context;
		mDialogParams = new GameDialogParams(this);
		mResources = context.getResources();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.upgrate_dialog);
		setGravity(Gravity.BOTTOM);
		mDialogParams.apply();
	}

	public void setGravity(int gravity) {
		Window window = getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.gravity = gravity;
		wlp.width = AndroidUtils.getDisplayWidth(mActivity);
		window.setAttributes(wlp);
	}

	public void setDismissBtnVisible() {
		if (mDialogParams.mDismissBtn != null) {
			mDialogParams.mDismissBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void show() {
		if (!mActivity.isFinishing()) {
			super.show();
		}
	}

	public void setTitle(CharSequence title) {
		mDialogParams.mTitleText = title;
	}

	public void setTitle(int titleId) {
		setTitle(getContext().getString(titleId));
	}

	@Override
	public void setContentView(View contentView) {
		mDialogParams.mContentView = contentView;
	}

	public View getContentView() {
		return mDialogParams.mContentView;
	}

	@Override
	public void setContentView(int layoutResID) {
		LayoutInflater inflater = getLayoutInflater();
		View contentView = inflater.inflate(layoutResID, null);
		setContentView(contentView);
	}

	public void setContentViewAlign(int contentAlign) {
		mDialogParams.mContentAlign = contentAlign;
	}

	public void setMessage(int messageId) {
		setMessage(getContext().getString(messageId));
	}

	public void setMessage(CharSequence message) {
		mDialogParams.mMessageText = message;
	}

	public void setPositiveButton(int textId,
			DialogInterface.OnClickListener onClickListener) {
		setButton(mResources.getString(textId), onClickListener,
				DialogInterface.BUTTON_POSITIVE);
	}

	public void setPositiveButton(int textId, int textColor,
			DialogInterface.OnClickListener onClickListener) {
		setButton(mResources.getString(textId), mResources.getColor(textColor),
				onClickListener, DialogInterface.BUTTON_POSITIVE);
	}

	public void setPositiveButton(CharSequence text,
			DialogInterface.OnClickListener onClickListener) {
		setButton(text, onClickListener, DialogInterface.BUTTON_POSITIVE);
	}

	public void setNegativeButton(int textId,
			DialogInterface.OnClickListener onClickListener) {
		setButton(mResources.getString(textId), onClickListener,
				DialogInterface.BUTTON_NEGATIVE);
	}

	public void setNegativeButton(CharSequence text,
			DialogInterface.OnClickListener onClickListener) {
		setButton(text, onClickListener, DialogInterface.BUTTON_NEGATIVE);
	}

	private void setButton(CharSequence text,
			final OnClickListener onClickListener, final int which) {
		mDialogParams.mButtonParams.add(new SetButtonParam(text,
				onClickListener, which));
	}

	private void setButton(CharSequence text, final int textColor,
			OnClickListener onClickListener, final int which) {
		mDialogParams.mButtonParams.add(new SetButtonParam(text, textColor,
				onClickListener, which));

	}

	public class GameDialogParams {

		public ArrayList<SetButtonParam> mButtonParams = new ArrayList<SetButtonParam>();
		public CharSequence mTitleText;
		public CharSequence mMessageText;
		public View mContentView;
		public ImageView mDismissBtn;
		public int mContentAlign = RelativeLayout.CENTER_HORIZONTAL;

		private GNUpgrateDialog mDialog;

		public GameDialogParams(GNUpgrateDialog dialog) {
			mDialog = dialog;
		}

		private void apply() {
			if (mTitleText != null) {
				RelativeLayout mTitleLayout = (RelativeLayout) mDialog
						.findViewById(R.id.title_rl);
				TextView title = (TextView) mDialog
						.findViewById(R.id.dialog_title);
				title.setText(mTitleText);
				mTitleLayout.setVisibility(View.GONE);
				LinearLayout mLl_bg = (LinearLayout) mDialog
						.findViewById(R.id.ll_bg);
				mLl_bg.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
					}
				});
				LinearLayout mDialoglayout = (LinearLayout) mDialog
						.findViewById(R.id.dialog_parent_layout);
				mDialoglayout.setOnClickListener(null);
			}

			if (mMessageText != null) {
				TextView message = (TextView) mDialog
						.findViewById(R.id.dialog_message);
				message.setVisibility(View.VISIBLE);
				message.setText(mMessageText);
			}
			mDismissBtn = (ImageView) mDialog.findViewById(R.id.dismiss_dialog);
			mDismissBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mDialog.dismiss();
				}

			});
			FrameLayout contentLayout = (FrameLayout) mDialog
					.findViewById(R.id.dialog_content_layout);
			if (mContentAlign != RelativeLayout.CENTER_HORIZONTAL) {
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentLayout
						.getLayoutParams();
				layoutParams.addRule(mContentAlign);
				contentLayout.setLayoutParams(layoutParams);
			}
			if (mContentView != null) {
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				contentLayout.addView(mContentView, layoutParams);
			}

			if (hasButton()) {
				mPositiveButton = (Button) mDialog
						.findViewById(R.id.positive_button);
				mNegativeButton = (Button) mDialog
						.findViewById(R.id.negative_button);
				LinearLayout buttonBarLayout = (LinearLayout) mDialog
						.findViewById(R.id.button_layout);
				if (buttonBarLayout.getVisibility() != View.VISIBLE) {
					buttonBarLayout.setVisibility(View.VISIBLE);
				}

				setButtons();
			}
		}

		private boolean hasButton() {
			return !mButtonParams.isEmpty();
		}

		private void setButtons() {
			if (mButtonParams.size() > 1) {
				setButtonDividerVisible();
			}
			for (SetButtonParam param : mButtonParams) {
				setButton(param.text, param.textColor, param.onClickListener,
						param.which);
			}
			mButtonParams.clear();
		}

		private void setButtonDividerVisible() {
			View divider = mDialog.findViewById(R.id.button_divider);
			divider.setVisibility(View.VISIBLE);
		}

		private void setButton(CharSequence text, final int textColor,
				final OnClickListener onClickListener, final int which) {
			Button targetBtn = getTargetBtn(which);
			targetBtn.setVisibility(View.VISIBLE);
			targetBtn.setText(text);
			if (textColor != 0) {
				targetBtn.setTextColor(textColor);
			}
			targetBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
					if (onClickListener != null) {
						onClickListener.onClick(GNUpgrateDialog.this, which);
					}
				}
			});
		}

		private Button getTargetBtn(int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				return mPositiveButton;
			case DialogInterface.BUTTON_NEGATIVE:
				return mNegativeButton;
			default:
				return null;
			}
		}
	}

	public static class SetButtonParam {
		public CharSequence text;
		public OnClickListener onClickListener;
		public int which;
		public int textColor;

		public SetButtonParam(CharSequence text,
				OnClickListener onClickListener, int which) {
			this.text = text;
			this.onClickListener = onClickListener;
			this.which = which;
		}

		public SetButtonParam(CharSequence text, int textColor,
				OnClickListener onClickListener, int which) {
			this.text = text;
			this.onClickListener = onClickListener;
			this.which = which;
			this.textColor = textColor;
		}
	}

}
