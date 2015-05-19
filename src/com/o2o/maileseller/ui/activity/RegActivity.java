package com.o2o.maileseller.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.o2o.maileseller.R;
import com.o2o.maileseller.entity.User;
import com.o2o.maileseller.network.logic.UserLogic;
import com.o2o.maileseller.ui.view.CustomProgressDialog;
import com.o2o.maileseller.util.UserInfoManager;

/**
 * 注册用户Activity
 * 
 */
public class RegActivity extends Activity {

	private EditText mUserNameEt;
	private EditText mPassWordEt;
	private EditText mPassWordRepeatEt;
	private EditText mMailEt;
	private EditText mPhoneEt;

	private Button mRegNewUserBtn;
	private Button mReturnToLoginBtn;

	private CustomProgressDialog mCustomProgressDialog;

	private String mEmail;
	private String mUserName;
	private String mUserPassword;
	private String mPasswordRepet;
	private String mPhone;

	private User mUser = new User();

	private Context mContext = RegActivity.this;

	Handler regHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case UserLogic.REGIS_SUC: {
				UserInfoManager.saveUserInfo(RegActivity.this,
						mUser.getUsername(), mUser.getPassword(), mUser);
				UserInfoManager.setUserInfo(RegActivity.this);
				UserInfoManager.setLoginIn(RegActivity.this, true);

				Intent intent = new Intent(RegActivity.this,
						LoginActivity.class);
				intent.setAction(LoginActivity.ORIGIN_FROM_REG_KEY);
				startActivity(intent);
				RegActivity.this.finish();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

				break;
			}
			case UserLogic.REGIS_FAIL: {
				String message = (String) msg.obj;
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				break;
			}
			case UserLogic.REGIS_EXCEPTION: {
				break;
			}
			case UserLogic.NET_ERROR: {
				break;
			}

			default:
				break;
			}

			if (null != mCustomProgressDialog) {
				mCustomProgressDialog.dismiss();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initView();
		initData();
	}

	private void initView() {
		// 初始化控件
		mUserNameEt = (EditText) findViewById(R.id.reg_username_et);
		mPassWordEt = (EditText) findViewById(R.id.reg_password_et);
		mPassWordRepeatEt = (EditText) findViewById(R.id.reg_confirm_password_et);
		mMailEt = (EditText) findViewById(R.id.reg_email_et);
		mPhoneEt = (EditText) findViewById(R.id.reg_phone_et);

		mRegNewUserBtn = (Button) findViewById(R.id.btn_regform_regnewuser);
		// mReturnToLoginBtn = (Button)
		// findViewById(R.id.btn_noreg_return_login);
	}

	private void initData() {
		mRegNewUserBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 对表单数据进行初始化
				mUserName = mUserNameEt.getText().toString().trim();
				mUserPassword = mPassWordEt.getText().toString().trim();
				mPasswordRepet = mPassWordRepeatEt.getText().toString().trim();
				mEmail = mMailEt.getText().toString().trim();
				mPhone = mPhoneEt.getText().toString().trim();

				if ("".equals(mUserName) || "".equals(mUserPassword)
						|| "".equals(mPasswordRepet) || "".equals(mEmail)
						|| "".equals(mPhone)) {
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.reg_error_empty_form_message),
							Toast.LENGTH_LONG).show();
				} else if (!mUserPassword.equals(mPasswordRepet)) {
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.reg_error_psw_not_same),
							Toast.LENGTH_LONG).show();
				} else {
					mUser.setUsername(mUserName);
					mUser.setPassword(mUserPassword);
					mUser.setEmail(mEmail);
					mUser.setTelephone(mPhone);

					mCustomProgressDialog = new CustomProgressDialog(mContext);
					mCustomProgressDialog.show();
					UserLogic.register(mContext, regHandler, mUser);
				}

			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent(RegActivity.this, LoginActivity.class);
			startActivity(intent);
			RegActivity.this.finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
