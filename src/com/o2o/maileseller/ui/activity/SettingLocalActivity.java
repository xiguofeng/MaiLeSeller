package com.o2o.maileseller.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.o2o.maileseller.R;
import com.o2o.maileseller.network.logic.SettingLogic;
import com.o2o.maileseller.ui.view.CustomProgressDialog;
import com.o2o.maileseller.util.LocationUtilsV5;
import com.o2o.maileseller.util.LocationUtilsV5.LocationCallback;
import com.o2o.maileseller.util.UserInfoManager;

public class SettingLocalActivity extends Activity implements OnClickListener {

	private Context mContext;

	private Button mSelectOkBtn, mCancelBtn;

	private CustomProgressDialog mCustomProgressDialog;

	private double mLocalRange;

	private double mLatitude, mLongitude = 0.0;

	private EditText mRangeEt;

	private int mLocFlag = 0;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case SettingLogic.LOCAL_SET_SUC: {
				if (!TextUtils.isEmpty(mRangeEt.getText().toString().trim())) {
					mLocalRange = Double.parseDouble(mRangeEt.getText()
							.toString().trim());
					SettingLogic.setLocalRange(mContext, mHandler, mLocalRange,
							UserInfoManager.userInfo.getUsername());
				}
				break;
			}
			case SettingLogic.LOCAL_SET_FAIL: {
				if (null != mCustomProgressDialog) {
					mCustomProgressDialog.dismiss();
				}
				break;
			}
			case SettingLogic.LOCAL_SET_EXCEPTION: {
				if (null != mCustomProgressDialog) {
					mCustomProgressDialog.dismiss();
				}
				break;
			}
			case SettingLogic.LOCALRANGE_SET_SUC: {
				if (null != mCustomProgressDialog) {
					mCustomProgressDialog.dismiss();
				}
				Toast.makeText(mContext, R.string.setting_suc,
						Toast.LENGTH_SHORT).show();
				SettingLocalActivity.this.finish();
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
				break;
			}
			case SettingLogic.LOCALRANGE_SET_FAIL: {
				Toast.makeText(mContext, R.string.setting_fail,
						Toast.LENGTH_SHORT).show();
				if (null != mCustomProgressDialog) {
					mCustomProgressDialog.dismiss();
				}
				break;
			}
			case SettingLogic.LOCALRANGE_SET_EXCEPTION: {
				if (null != mCustomProgressDialog) {
					mCustomProgressDialog.dismiss();
				}
				break;
			}
			case SettingLogic.NET_ERROR: {
				if (null != mCustomProgressDialog) {
					mCustomProgressDialog.dismiss();
				}
				break;
			}

			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_local);
		mContext = SettingLocalActivity.this;
		initView();
		initData();
	}

	private void initView() {

		mRangeEt = (EditText) findViewById(R.id.setloc_loc_range_et);

		mSelectOkBtn = (Button) findViewById(R.id.setloc_ok_btn);
		mCancelBtn = (Button) findViewById(R.id.setloc_cancel_btn);

		mSelectOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
	}

	private void initData() {
		getLoc();
	}

	private void getLoc() {
		LocationUtilsV5.getLocation(getApplicationContext(),
				new LocationCallback() {
					@Override
					public void onGetLocation(BDLocation location) {
						Log.e("xxx_latitude", "" + location.getLatitude());
						Log.e("xxx_longitude", "" + location.getLongitude());
						mLatitude = location.getLatitude();
						mLongitude = location.getLongitude();

						if (mLocFlag == 1) {
							if (!TextUtils.isEmpty(mRangeEt.getText()
									.toString().trim())) {
								mLocFlag = 0;
								mCustomProgressDialog = new CustomProgressDialog(
										mContext);
								mCustomProgressDialog.show();
								SettingLogic.setLocal(mContext, mHandler,
										mLatitude, mLongitude,
										UserInfoManager.userInfo.getUsername());
							}
						}
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setloc_ok_btn: {
			if (mLatitude != 0.0 && mLongitude != 0.0) {
				if (!TextUtils.isEmpty(mRangeEt.getText().toString().trim())) {
					mCustomProgressDialog = new CustomProgressDialog(mContext);
					mCustomProgressDialog.show();
					SettingLogic.setLocal(mContext, mHandler, mLatitude,
							mLongitude, UserInfoManager.userInfo.getUsername());
				}
			} else {
				mLocFlag = 1;
				getLoc();
			}

			break;
		}
		case R.id.setloc_cancel_btn: {

			break;
		}
		default:
			break;
		}
	}
}
