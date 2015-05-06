package com.o2o.maileseller.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.o2o.maileseller.R;
import com.o2o.maileseller.entity.User;
import com.o2o.maileseller.network.logic.UserLogic;
import com.o2o.maileseller.network.push.message.RegistUserInfoRequest.UserType;
import com.o2o.maileseller.service.PushService;
import com.o2o.maileseller.ui.view.CustomProgressDialog;
import com.o2o.maileseller.util.UserInfoManager;

/**
 * 登录界面
 */
public class LoginActivity extends Activity implements OnClickListener {
	public static final String ORIGIN_FROM_REG_KEY = "com.reg";

	private EditText mUserNameEt;
	private EditText mPassWordEt;
	private TextView mRegTv;
	private CheckBox mRemberpswCb;
	private CheckBox mLoginAutoCb;
	// private LinearLayout layoutProcess;
	private Button mLoginBtn;
	private Button mRegNewUserBtn;

	private CustomProgressDialog mCustomProgressDialog;

	private String mUserName;
	private String mPassWord;

	private User mUser = new User();

	private boolean isLoginSuccess = false;// 默认登陆未成功
	private Context mContext = LoginActivity.this;

	Handler mLoginHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case UserLogic.LOGIN_SUC: {
				UserInfoManager.saveUserInfo(LoginActivity.this,
						mUser.getUsername(), mUser.getPassword(), mUser);
				UserInfoManager.setUserInfo(LoginActivity.this);
				UserInfoManager.setLoginIn(LoginActivity.this, true);

				// [2] 注册用户信息
				PushService.sCli.registUserInfo(UserType.seller,
						UserInfoManager.userInfo.getUsername(),
						UserInfoManager.userInfo.getPassword());

				Intent intent = new Intent(LoginActivity.this,
						RobOrderActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);

				break;
			}
			case UserLogic.LOGIN_FAIL: {
				Toast.makeText(mContext, R.string.login_fail,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case UserLogic.LOGIN_EXCEPTION: {
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
		setContentView(R.layout.login);
		initView();
		initData();

	}

	private void initView() {
		/* 初始化控件 */
		mUserNameEt = (EditText) findViewById(R.id.login_username_et);
		mPassWordEt = (EditText) findViewById(R.id.login_password_et);
		mRemberpswCb = (CheckBox) findViewById(R.id.login_remember_cb);
		mLoginAutoCb = (CheckBox) findViewById(R.id.login_auto_cb);

		mLoginBtn = (Button) findViewById(R.id.login_login_btn);
		mRegTv = (TextView) findViewById(R.id.login_reg_tv);

		mLoginBtn.setOnClickListener(this);
		mRegTv.setOnClickListener(this);
		// mRegNewUserBtn = (Button) findViewById(R.id.register_btn);
	}

	private void initData() {
		if (!TextUtils.isEmpty(getIntent().getAction())
				&& getIntent().getAction().equals(ORIGIN_FROM_REG_KEY)) {
			mUserNameEt.setText(UserInfoManager.userInfo.getUsername());
			mPassWordEt.setText(UserInfoManager.userInfo.getPassword());
			login();
		} else {
			if (UserInfoManager.getRememberPwd(mContext)) {
				UserInfoManager.setUserInfo(LoginActivity.this);
				mUserNameEt.setText(UserInfoManager.userInfo.getUsername());
				mPassWordEt.setText(UserInfoManager.userInfo.getPassword());
				mRemberpswCb.setChecked(true);

				if (UserInfoManager.getLoginInAuto(mContext)) {
					login();
				}
			}
			// // 检测是否存在SD卡，存在SD卡的情况下进行判断文件是否存在
			// if (AndroidTools.isHasSD()) {
			// // 检测是否存在文件，不存在，则创建xml文件
			// if (AndroidTools.isFileExists(Fileconfig.xmlinfopath)) {
			// // 存在xml,读取内容，放置到表单中国
			//
			// String xmlpath = Fileconfig.sdrootpath
			// + Fileconfig.xmlinfopath;
			// mUserNameEt.setText(XMLHelper.readXMLByNodeName(
			// UserXmlParseConst.USERNAME, xmlpath));
			// mPassWordEt.setText(XMLHelper.readXMLByNodeName(
			// UserXmlParseConst.PASSWORD, xmlpath));
			// mRemberpswCb.setChecked(true);
			//
			// }
			// } else {
			// // 没有内存卡，不需要执行操作
			// }
		}

		mRemberpswCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 如果点选记住密码,检测是否存在SD卡，如果不存在，提示，则不能进行写文件操作
				if (isChecked) {
					UserInfoManager.setRememberPwd(mContext, true);

					// if (!AndroidTools.isHasSD()) {
					// Toast.makeText(LoginActivity.this, "没有SD卡",
					// Toast.LENGTH_SHORT).show();
					// }
				} else {
					UserInfoManager.setRememberPwd(mContext, false);
					// if (!AndroidTools.isHasSD()) {
					// Toast.makeText(LoginActivity.this, "没有SD卡",
					// Toast.LENGTH_SHORT).show();
					// }
				}

			}
		});

		mLoginAutoCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 如果点选记住密码,检测是否存在SD卡，如果不存在，提示，则不能进行写文件操作
				if (isChecked) {
					mRemberpswCb.setChecked(true);
					UserInfoManager.setLoginInAuto(mContext, true);
					// if (!AndroidTools.isHasSD()) {
					// Toast.makeText(LoginActivity.this, "没有SD卡",
					// Toast.LENGTH_SHORT).show();
					// }
				} else {
					UserInfoManager.setLoginInAuto(mContext, false);
					// if (!AndroidTools.isHasSD()) {
					// Toast.makeText(LoginActivity.this, "没有SD卡",
					// Toast.LENGTH_SHORT).show();
					// }
				}

			}
		});
	}

	private void login() {
		mCustomProgressDialog = new CustomProgressDialog(this);
		mCustomProgressDialog.show();
		// 获取用户的登录信息，连接服务器，获取登录状态
		mUserName = mUserNameEt.getText().toString().trim();
		mPassWord = mPassWordEt.getText().toString().trim();

		if ("".equals(mUserName) || "".equals(mPassWord)) {
			// layoutProcess.setVisibility(View.GONE);
			Toast.makeText(LoginActivity.this,
					mContext.getString(R.string.login_emptyname_or_emptypwd),
					Toast.LENGTH_SHORT).show();
		} else {
			mUser.setUsername(mUserName);
			mUser.setPassword(mPassWord);
			// LoginLogic.login(LoginActivity.this, "xkm", "sss");
			UserLogic.login(mContext, mLoginHandler, mUser);

			// TODO
			// mLoginHandler.sendEmptyMessage(UserLogic.LOGIN_SUC);
		}
	}

	private void toReg() {
		Intent intent = new Intent(LoginActivity.this, RegActivity.class);
		startActivity(intent);
		LoginActivity.this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_login_btn: {
			login();
			break;
		}
		case R.id.login_reg_tv: {
			toReg();
			break;
		}

		default:
			break;
		}
	}

}
