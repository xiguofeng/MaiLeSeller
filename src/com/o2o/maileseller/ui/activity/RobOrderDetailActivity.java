package com.o2o.maileseller.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.o2o.maileseller.R;
import com.o2o.maileseller.entity.OrderClient;
import com.o2o.maileseller.network.logic.OrderLogic;
import com.o2o.maileseller.ui.view.CustomProgressDialog;
import com.o2o.maileseller.util.UserInfoManager;

public class RobOrderDetailActivity extends Activity implements OnClickListener {

	public static final String ORDER_KEY = "order_key";

	private Button mRobBtn, mCancelBtn;

	private TextView mGoodsNameTv;

	private TextView mBuyPriceTv;

	private TextView mBuyNumTv;

	private TextView mBuyAddressTv;

	private TextView mBuyPhoneTv;

	private Context mContext;

	private OrderClient mOrder;

	private CustomProgressDialog mCustomProgressDialog;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case OrderLogic.ORDER_ROB_SUC: {
				Intent intent = new Intent(RobOrderDetailActivity.this,
						RobOrderResultActivity.class);
				startActivity(intent);
				RobOrderDetailActivity.this.finish();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				break;
			}
			case OrderLogic.ORDER_ROB_FAIL: {
				Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT)
						.show();
				finish();
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
				break;
			}
			case OrderLogic.ORDER_ROB_EXCEPTION: {
				break;
			}
			case OrderLogic.NET_ERROR: {
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
		setContentView(R.layout.rob_order_detail);
		mContext = RobOrderDetailActivity.this;
		initView();
		initData();
	}

	private void initView() {
		mRobBtn = (Button) findViewById(R.id.order_rob_btn);
		mCancelBtn = (Button) findViewById(R.id.order_giveup_btn);

		mGoodsNameTv = (TextView) findViewById(R.id.order_name_tv);
		mBuyPriceTv = (TextView) findViewById(R.id.order_price_tv);
		mBuyNumTv = (TextView) findViewById(R.id.order_num_tv);
		mBuyAddressTv = (TextView) findViewById(R.id.order_address_tv);
		mBuyPhoneTv = (TextView) findViewById(R.id.order_phone_tv);

		mRobBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
	}

	private void initData() {
		mOrder = (OrderClient) getIntent().getSerializableExtra(ORDER_KEY);

		mGoodsNameTv.setText(mOrder.getGoodsName());
		mBuyPriceTv.setText(mOrder.getTotalPrice());
		mBuyNumTv.setText(mOrder.getBuyNum());
		mBuyAddressTv.setText(mOrder.getBuyAddress());
		mBuyPhoneTv.setText(mOrder.getBuyPhone());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.order_rob_btn: {
			mCustomProgressDialog = new CustomProgressDialog(this);
			mCustomProgressDialog.show();
			OrderLogic.robOrder(mContext, mHandler, mOrder.getId(),
					UserInfoManager.userInfo.getUsername(),
					mOrder.getBuyPhone(), mOrder.getBuyAddress());
			break;
		}
		case R.id.order_giveup_btn: {
			RobOrderDetailActivity.this.finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			break;
		}
		default:
			break;
		}
	}

}
