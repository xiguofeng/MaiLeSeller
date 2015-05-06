package com.o2o.maileseller.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.o2o.maileseller.R;
import com.o2o.maileseller.entity.OrderClient;
import com.o2o.maileseller.service.PushService;
import com.o2o.maileseller.ui.adapter.OrderAdapter;
import com.o2o.maileseller.ui.view.CustomProgressDialog;

public class RobOrderActivity extends Activity implements OnClickListener {

	private Button mSettingCateBtn, mSettingLocalBtn, mHasOrderBtn,
			mRefreshOrderBtn;

	private ListView mOrderLv;

	private OrderAdapter mOrderAdapter;

	private CustomProgressDialog mCustomProgressDialog;

	private Context mContext;

	private ArrayList<OrderClient> mRoList = new ArrayList<OrderClient>();

	private long mExitTime = 0;

	private BroadcastReceiver mNotifyBr = new BroadcastReceiver() {

		public void onReceive(Context paramContext, Intent paramIntent) {
			if (paramIntent.getAction().equals(PushService.NOTIFY_NEW_ORDER)) {
				// TODO
				newOrder(paramIntent);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roborder);
		mContext = RobOrderActivity.this;
		initView();
		initData();
		initReceiver();
		if (null != mCustomProgressDialog) {
			mCustomProgressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNotifyBr);
	}

	private void initView() {
		mCustomProgressDialog = new CustomProgressDialog(mContext);
		mCustomProgressDialog.show();

		mSettingCateBtn = (Button) findViewById(R.id.ro_setting_cate_btn);
		mSettingLocalBtn = (Button) findViewById(R.id.ro_setting_local_btn);
		mHasOrderBtn = (Button) findViewById(R.id.ro_has_order_btn);
		mRefreshOrderBtn = (Button) findViewById(R.id.ro_refresh_order_btn);
		mOrderLv = (ListView) findViewById(R.id.ro_order_lv);

		mSettingCateBtn.setOnClickListener(this);
		mSettingLocalBtn.setOnClickListener(this);
		mHasOrderBtn.setOnClickListener(this);
		mRefreshOrderBtn.setOnClickListener(this);
	}

	private void initData() {
		for (int i = 0; i < 3; i++) {
			OrderClient order = new OrderClient();
			order.setId("11111111111111" + i);
			order.setBuyNum("" + i);
			order.setGoodsName("商品" + i);
			order.setBuyPrice("" + i);
			order.setBuyAddress("距您的距离越" + i + "公里");
			order.setBuyPhone("1891234000" + i);
			mRoList.add(order);
		}
		if (PushService.sPushOrder2SellerList.size() > 0) {
			getNewOrder();
		}
		mOrderAdapter = new OrderAdapter(mContext, mRoList);
		mOrderLv.setAdapter(mOrderAdapter);
		mOrderLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mContext,
						RobOrderDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(RobOrderDetailActivity.ORDER_KEY,
						mRoList.get(position));
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
	}

	private void newOrder(Intent intent) {
		Log.e("xxx_sPushOrder2SellerListadd", "newOrder");
		if (PushService.sPushOrder2SellerList.size() > 0) {
			getNewOrder();
		} else {
			Bundle bundle = intent.getExtras();
			String buyerAddress = bundle.getString("buyerAddress");
			try {
				buyerAddress = URLDecoder.decode(buyerAddress, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String buyerPhone = bundle.getString("buyerPhone");
			String buyNum = bundle.getString("buyNum");
			String buyerName = bundle.getString("buyerName");
			String goodsName = bundle.getString("goodsName");
			String orderID = bundle.getString("orderID");
			String totalPrice = bundle.getString("totalPrice");
			String goodsBrief = bundle.getString("goodsBrief");

			OrderClient order = new OrderClient();
			order.setId(orderID);
			order.setBuyNum(buyNum);
			order.setGoodsName(goodsName);
			order.setTotalPrice(totalPrice);
			order.setBuyAddress(buyerAddress);
			order.setBuyPhone(buyerPhone);
			order.setBuyUserName(buyerName);
			order.setGoodsBrief(goodsBrief);
			mRoList.add(0, order);
			mOrderAdapter.notifyDataSetChanged();
		}
	}

	private void getNewOrder() {
		Log.e("xxx_sPushOrder2SellerListadd", "size："
				+ PushService.sPushOrder2SellerList.size());
		mRoList.clear();
		for (int i = 0; i < PushService.sPushOrder2SellerList.size(); i++) {
			try {
				OrderClient order = new OrderClient();
				order.setId(PushService.sPushOrder2SellerList.get(i)
						.getOrderID());
				order.setBuyNum(String
						.valueOf(PushService.sPushOrder2SellerList.get(i)
								.getBuyNum()));
				order.setGoodsName(PushService.sPushOrder2SellerList.get(i)
						.getGoodsName());
				order.setTotalPrice(PushService.sPushOrder2SellerList.get(i)
						.getTotalPrice());
				order.setBuyAddress(URLDecoder.decode(
						PushService.sPushOrder2SellerList.get(i)
								.getBuyerAddress(), "UTF-8"));
				order.setBuyPhone(PushService.sPushOrder2SellerList.get(i)
						.getBuyerPhone());
				order.setBuyUserName(PushService.sPushOrder2SellerList.get(i)
						.getBuyerName());
				order.setGoodsBrief(PushService.sPushOrder2SellerList.get(i)
						.getGoodsBrief());
				mRoList.add(0, order);
				mOrderAdapter.notifyDataSetChanged();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	private void initReceiver() {
		IntentFilter localIntentFilter = new IntentFilter();
		localIntentFilter.addAction(PushService.NOTIFY_NEW_ORDER);
		localIntentFilter.addAction(PushService.NOTIFY_DATA_UPDATE);
		registerReceiver(mNotifyBr, localIntentFilter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ro_setting_cate_btn: {
			Intent intent = new Intent(mContext, SettingCategroyActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		}
		case R.id.ro_setting_local_btn: {
			Intent intent = new Intent(mContext, SettingLocalActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		}
		case R.id.ro_has_order_btn: {
			Intent intent = new Intent(mContext, OrderListActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(getApplicationContext(), R.string.exit,
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
