package com.o2o.maileseller.network.logic;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.o2o.maileseller.BaseApplication;
import com.o2o.maileseller.network.config.MsgResult;
import com.o2o.maileseller.network.config.RequestUrl;
import com.o2o.maileseller.network.volley.Request.Method;
import com.o2o.maileseller.network.volley.Response.Listener;
import com.o2o.maileseller.network.volley.toolbox.JsonObjectRequest;

public class ConnectLogic {

	public static final int NET_ERROR = 0;

	public static final int CONNECT_SUC = NET_ERROR + 1;

	public static final int CONNECT_FAIL = CONNECT_SUC + 1;

	public static final int CONNECT_EXCEPTION = CONNECT_FAIL + 1;

	public static void connect(final Context context, final Handler handler) {

		String url = RequestUrl.HOST_URL + RequestUrl.connect.connect;
		JSONObject requestJson = new JSONObject();
		try {
			requestJson.put("clientVersion", 1);
			BaseApplication.getInstanceRequestQueue().add(
					new JsonObjectRequest(Method.POST, url, requestJson,
							new Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									if (null != response) {
										Log.e("xxx_Connect",
												response.toString());
										parseConnectData(response, handler);
									} else {
										handler.sendEmptyMessage(CONNECT_FAIL);
									}
								}
							}, null));
			BaseApplication.getInstanceRequestQueue().start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void parseConnectData(JSONObject response, Handler handler) {
		try {
			String address = response.getString(
					MsgResult.RESULT_PUSH_ADDRESS_TAG).trim();
			Message msg = new Message();
			if (!TextUtils.isEmpty(address)) {
				msg.what = CONNECT_SUC;
				msg.obj = address;
			} else {
				msg.what = CONNECT_FAIL;
				// msg.obj = response.getString(MsgResult.RESULT_ERROR_MSG_TAG)
				// .trim();
			}
			handler.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(CONNECT_EXCEPTION);
		}
	}
}
