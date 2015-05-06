package com.o2o.maileseller.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.o2o.maileseller.R;
import com.o2o.maileseller.ui.fragment.OrderAgreedFragment;
import com.o2o.maileseller.ui.fragment.OrderUndeliveredFragment;
import com.o2o.maileseller.ui.view.pageindicator.TabPageIndicator;

public class OrderListActivity extends FragmentActivity implements
		OnClickListener {

	private static final String[] ORDERLISTTYPETITLE = new String[] { "1", "2" };

	private ImageView mBackIv;

	private ViewPager mPager;// 页卡内容

	private TabPageIndicator mIndicator;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_list);
		findViewById();
		initView();
	}

	private void findViewById() {
		mPager = (ViewPager) findViewById(R.id.orderlist_vPager);
		// mBackIv = (ImageView) findViewById(R.id.trans_query_back_iv);
		mIndicator = (TabPageIndicator) findViewById(R.id.orderlist_indicator);
	}

	private void initView() {
		InitViewPager();
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		FragmentPagerAdapter adapter = new TabAdapter(
				getSupportFragmentManager());
		mPager.setAdapter(adapter);
		mIndicator.setViewPager(mPager);
	}

	class TabAdapter extends FragmentPagerAdapter {
		public TabAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			Fragment nowFragment = new OrderUndeliveredFragment();
			switch (position) {
			case 0: {
				nowFragment = new OrderUndeliveredFragment();
				break;
			}
			case 1: {
				nowFragment = new OrderAgreedFragment();
				break;
			}
			default:
				break;
			}

			return nowFragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String title = getResources().getString(R.string.undelivered_order);
			switch (position % ORDERLISTTYPETITLE.length) {
			case 0: {
				title = getResources().getString(R.string.undelivered_order);
				break;
			}
			case 1: {
				title = getResources().getString(R.string.finish_order);
				break;
			}
			}
			return title;
		}

		@Override
		public int getCount() {
			return ORDERLISTTYPETITLE.length;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.trans_query_back_iv: {
		// finish();
		// overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
		// break;
		// }
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
