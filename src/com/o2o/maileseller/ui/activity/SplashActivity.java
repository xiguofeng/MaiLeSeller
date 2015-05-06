package com.o2o.maileseller.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.o2o.maileseller.R;
import com.o2o.maileseller.network.logic.ConnectLogic;
import com.o2o.maileseller.util.AppInfoManager;

public class SplashActivity extends Activity {

	private final int SPLISH_DISPLAY_LENGTH = 3000; // 延迟3秒启动登陆界面

	private Context mContext;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case ConnectLogic.CONNECT_SUC: {
				AppInfoManager.setPushUrl(mContext, (String) msg.obj);

				break;
			}
			case ConnectLogic.CONNECT_FAIL: {
				Toast.makeText(mContext, R.string.reg_fail, Toast.LENGTH_SHORT)
						.show();
				break;
			}
			case ConnectLogic.CONNECT_EXCEPTION: {
				break;
			}
			case ConnectLogic.NET_ERROR: {
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		mContext = SplashActivity.this;

		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Intent intent = new Intent(SplashActivity.this,
				LoginActivity.class);
		startActivity(intent);
		SplashActivity.this.finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

		// ConnectLogic.connect(mContext, mHandler);
		// // 启动三秒后进度到登陆界面
		// new Handler().postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// Intent startLoginIntent = new Intent(SplashActivity.this,
		// LoginActivity.class);
		// SplashActivity.this.startActivity(startLoginIntent);
		// SplashActivity.this.finish();
		// }
		// }, SPLISH_DISPLAY_LENGTH);

	}
}
