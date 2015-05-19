package com.o2o.maileseller;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import com.o2o.maileseller.network.volley.RequestQueue;
import com.o2o.maileseller.network.volley.toolbox.Volley;
import com.o2o.maileseller.service.PushService;
import com.o2o.maileseller.util.file.FileUtil;

public class BaseApplication extends Application {

	private static Context context;

	private static RequestQueue sQueue;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		sQueue = Volley.newRequestQueue(getApplicationContext());
		FileUtil.createLogDir();
		startPushService();
		// startNotifyService();
		// 自动恢复连接服务
		// Intent reConnectService = new Intent(context,
		// ReConnectService.class);
		// context.startService(reConnectService);
		// ImageLoaderConfig.initImageLoader(this, Constants.BASE_IMAGE_CACHE);
		// PrefUtils.putBoolean(PrefUtils.IS_REFRESHING, false); // init
	}

	private void startPushService() {
		Intent intent = new Intent(this, PushService.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(intent);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static RequestQueue getInstanceRequestQueue() {
		if (null == sQueue) {
			sQueue = Volley.newRequestQueue(getContext());
		}
		return sQueue;
	}

	public static Context getContext() {
		return context;
	}

}
