package com.o2o.maileseller.ui.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.o2o.maileseller.R;
import com.o2o.maileseller.ui.fragment.SelectableTreeFragment;

public class SingleFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_single_fragment);
		Class<?> fragmentClass = (Class<?>) SelectableTreeFragment.class;
		Fragment f = Fragment.instantiate(this, fragmentClass.getName());
		getFragmentManager().beginTransaction()
				.replace(R.id.fragment, f, fragmentClass.getName()).commit();
	}
}
