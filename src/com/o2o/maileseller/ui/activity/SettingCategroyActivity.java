package com.o2o.maileseller.ui.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.o2o.maileseller.R;
import com.o2o.maileseller.entity.Categroy;
import com.o2o.maileseller.network.logic.GoodsLogic;
import com.o2o.maileseller.network.logic.SettingLogic;
import com.o2o.maileseller.ui.adapter.CategroyAdapter;
import com.o2o.maileseller.ui.view.CustomProgressDialog;
import com.o2o.maileseller.util.UserInfoManager;

public class SettingCategroyActivity extends Activity implements
		OnClickListener {

	private Context mContext;

	private ListView mCategroyLv;

	private CategroyAdapter mCategroyAdapter;

	private Button mSelectOkBtn, mAllCategroyBtn, mCancelBtn;

	private TextView mLevelTv;

	public static ArrayList<String> sCategoryIDs = new ArrayList<String>();

	private ArrayList<Categroy> mCategroyList = new ArrayList<Categroy>();

	private ArrayList<Categroy> mTempCategroyList = new ArrayList<Categroy>();

	private HashMap<Integer, ArrayList<Categroy>> mCategroyListHistory = new HashMap<Integer, ArrayList<Categroy>>();

	private int mNowLevel = 0;
	private CustomProgressDialog mCustomProgressDialog;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case GoodsLogic.CATEGROY_LIST_GET_SUC: {
				if (null != msg.obj) {
					mTempCategroyList.clear();
					mTempCategroyList
							.addAll((Collection<? extends Categroy>) msg.obj);
					if (mTempCategroyList.size() > 0) {
						ArrayList<Categroy> categroyList = new ArrayList<Categroy>();
						categroyList.addAll(mTempCategroyList);
						mCategroyListHistory.put(mNowLevel, categroyList);
						mNowLevel++;
						mCategroyList.clear();
						mCategroyList.addAll(mTempCategroyList);
						mCategroyAdapter.notifyDataSetChanged();
						mLevelTv.setText("" + mNowLevel
								+ getString(R.string.categroy_level));
					} else {
						Toast.makeText(mContext,
								getString(R.string.already_min_categroy),
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			case GoodsLogic.CATEGROY_LIST_GET_FAIL: {
				break;
			}
			case GoodsLogic.CATEGROY_LIST_GET_EXCEPTION: {
				break;
			}
			case SettingLogic.CATEGROY_SET_SUC: {
				Toast.makeText(mContext, getString(R.string.setting_suc),
						Toast.LENGTH_SHORT).show();
				SettingCategroyActivity.this.finish();
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
				break;
			}
			case SettingLogic.CATEGROY_SET_FAIL: {
				break;
			}
			case SettingLogic.CATEGROY_SET_EXCEPTION: {
				break;
			}
			case SettingLogic.NET_ERROR: {
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
		setContentView(R.layout.setting_categroy);
		mContext = SettingCategroyActivity.this;
		initView();
		initData();
	}

	private void initView() {
		mLevelTv = (TextView) findViewById(R.id.setcate_level_tv);
		mCategroyLv = (ListView) findViewById(R.id.setcate_categroy_lv);

		mSelectOkBtn = (Button) findViewById(R.id.setcate_ok_btn);
		mAllCategroyBtn = (Button) findViewById(R.id.setcate_all_categroy_btn);
		mCancelBtn = (Button) findViewById(R.id.setcate_cancel_btn);

		mSelectOkBtn.setOnClickListener(this);
		mAllCategroyBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
	}

	private void initData() {
		mCategroyAdapter = new CategroyAdapter(mContext, mCategroyList);
		mCategroyLv.setAdapter(mCategroyAdapter);
		mCategroyLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO
				// if (mCategroyList.size() > 0) {
				// mCustomProgressDialog = new CustomProgressDialog(mContext);
				// mCustomProgressDialog.show();
				// GoodsLogic.getCategroyList(mContext, mHandler,
				// mCategroyList.get(position).getCategoryID());
				// }
			}
		});
		mCustomProgressDialog = new CustomProgressDialog(mContext);
		mCustomProgressDialog.show();
		GoodsLogic.getCategroyList(mContext, mHandler, "");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setcate_ok_btn: {
			mCustomProgressDialog = new CustomProgressDialog(this);
			mCustomProgressDialog.show();
			SettingLogic.setCategroy(mContext, mHandler, sCategoryIDs,
					UserInfoManager.userInfo.getUsername());
			mHandler.sendEmptyMessage(SettingLogic.CATEGROY_SET_SUC);
			break;
		}
		case R.id.setcate_all_categroy_btn: {

			break;
		}
		case R.id.setcate_cancel_btn: {

			break;
		}
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mNowLevel > 0) {
				mLevelTv.setText("" + mNowLevel
						+ getString(R.string.categroy_level));
				mNowLevel--;
				mCategroyList.clear();
				mCategroyList.addAll(mCategroyListHistory.get(mNowLevel));
				mCategroyAdapter.notifyDataSetChanged();
			} else {
				SettingCategroyActivity.this.finish();
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
