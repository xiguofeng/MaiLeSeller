package com.o2o.maileseller.ui.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

import com.o2o.maileseller.R;

public class RobOrderResultActivity extends Activity implements OnClickListener {

	private TextView mHistoryOrderTv;

	private TextView mTimeTv;

	private TextView mPhoneTv;

	private TextView mBillNumTv;

	private RatingBar mSpeedRb;

	private RatingBar mQualityRb;

	private RatingBar mServiceRb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rob_order_result);
		initView();
		initData();
	}

	private void initView() {
		mHistoryOrderTv = (TextView) findViewById(R.id.result_history_order_tv);
		mHistoryOrderTv.setOnClickListener(this);
		mTimeTv = (TextView) findViewById(R.id.result_time_tv);
		mPhoneTv = (TextView) findViewById(R.id.result_phone_tv);
		mBillNumTv = (TextView) findViewById(R.id.result_bill_count_tv);

		mSpeedRb = (RatingBar) findViewById(R.id.result_speed_rb);
		mQualityRb = (RatingBar) findViewById(R.id.result_quality_rb);
		mServiceRb = (RatingBar) findViewById(R.id.result_service_rb);
	}

	private void initData() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"MM-dd HH:mm:ss");
		String timeStr = simpleDateFormat.format(date);
		mTimeTv.setText(timeStr);
		// mTimeTv.setText(TimeUtils.TimeStamp2Date(
		// String.valueOf(System.currentTimeMillis()),
		// TimeUtils.FORMAT_PATTERN_DATE));
		mPhoneTv.setText("18688998899");
		mBillNumTv.setText("1980");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.result_history_order_tv: {
			Intent intent = new Intent(RobOrderResultActivity.this,
					OrderListActivity.class);
			startActivity(intent);
			RobOrderResultActivity.this.finish();
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		}
		default:
			break;
		}

	}

}
