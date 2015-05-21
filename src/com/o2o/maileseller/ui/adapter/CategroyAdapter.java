package com.o2o.maileseller.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.o2o.maileseller.R;
import com.o2o.maileseller.entity.Categroy;
import com.o2o.maileseller.ui.activity.SettingCategroyActivity;

public class CategroyAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<Categroy> mDatas;

	private LayoutInflater mInflater;

	public CategroyAdapter(Context context, ArrayList<Categroy> datas) {
		this.mContext = context;
		this.mDatas = datas;
		mInflater = LayoutInflater.from(mContext);

	}

	@Override
	public int getCount() {
		if (mDatas != null) {
			return mDatas.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.categroy_item, null);

			holder = new ViewHolder();
			holder.mName = (TextView) convertView
					.findViewById(R.id.categroy_item_name_tv);
			holder.mBrief = (TextView) convertView
					.findViewById(R.id.categroy_item_brief_tv);
			holder.mCb = (CheckBox) convertView
					.findViewById(R.id.categroy_item_cb);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.mName.setText(mDatas.get(position).getCategoryName());
		holder.mBrief.setText(mDatas.get(position).getCategoryID());

		final int tempPosition = position;
		holder.mCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (!SettingCategroyActivity.sCategoryIDs.contains(mDatas
							.get(tempPosition).getCategoryID())) {
						SettingCategroyActivity.sCategoryIDs.add(mDatas.get(
								tempPosition).getCategoryID());
					} else {
						if (SettingCategroyActivity.sCategoryIDs
								.contains(mDatas.get(tempPosition)
										.getCategoryID())) {
							SettingCategroyActivity.sCategoryIDs.remove(mDatas
									.get(tempPosition).getCategoryID());
						}
					}

				}
			}
		});
		return convertView;
	}

	static class ViewHolder {

		public TextView mName;

		public TextView mBrief;

		public CheckBox mCb;

	}
}
