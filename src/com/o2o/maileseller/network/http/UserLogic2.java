package com.o2o.maileseller.network.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.o2o.maileseller.entity.User;
import com.o2o.maileseller.network.config.RequestUrl;

public class UserLogic2 {

	public static final int NET_ERROR = 0;

	public static final int REGIS_SUC = NET_ERROR + 1;

	public static final int REGIS_FAIL = REGIS_SUC + 1;

	public static final int REGIS_EXCEPTION = REGIS_FAIL + 1;

	public static final int LOGIN_SUC = REGIS_EXCEPTION + 1;

	public static final int LOGIN_FAIL = LOGIN_SUC + 1;

	public static final int LOGIN_EXCEPTION = LOGIN_FAIL + 1;

	public static final int MODIFY_PWD_SUC = LOGIN_EXCEPTION + 1;

	public static final int MODIFY_PWD_FAIL = MODIFY_PWD_SUC + 1;

	public static final int MODIFY_PWD_EXCEPTION = MODIFY_PWD_FAIL + 1;

	public static void register(final Context context, final Handler handler,
			final User user) {

		if (HttpUtils.checkNetWorkInfo(context)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					String url = RequestUrl.HOST_URL
							+ RequestUrl.account.register;

					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

					try {
						String susername = URLEncoder.encode(
								user.getUsername(), "UTF-8");
						susername = URLEncoder.encode(susername, "UTF-8");

						String password = URLEncoder.encode(user.getPassword(),
								"UTF-8");
						password = URLEncoder.encode(password, "UTF-8");

						String realname = URLEncoder.encode(user.getRealName(),
								"UTF-8");
						realname = URLEncoder.encode(realname, "UTF-8");

						String companyname = URLEncoder.encode(
								user.getCompanyname(), "UTF-8");
						companyname = URLEncoder.encode(companyname, "UTF-8");

						String province = URLEncoder.encode(user.getProvince(),
								"UTF-8");
						province = URLEncoder.encode(province, "UTF-8");

						String city = URLEncoder.encode(user.getCity(), "UTF-8");
						city = URLEncoder.encode(city, "UTF-8");

						params.add(new BasicNameValuePair("susername",
								susername));
						params.add(new BasicNameValuePair("password", password));
						params.add(new BasicNameValuePair("realname", realname));
						params.add(new BasicNameValuePair("companyname",
								companyname));
						params.add(new BasicNameValuePair("province", province));
						params.add(new BasicNameValuePair("city", city));
						String resultStr = HttpUtils
								.sendHttpRequestByHttpClientGet(url, params);

						Log.e("xxx", resultStr);
						if (!TextUtils.isEmpty(resultStr)) {
							JSONObject jsonObject = new JSONObject(resultStr);
							parseRegisterData(jsonObject, handler);
						} else {
							handler.sendEmptyMessage(REGIS_FAIL);
						}
					} catch (UnsupportedEncodingException e) {
						handler.sendEmptyMessage(REGIS_EXCEPTION);
					} catch (JSONException e) {
						handler.sendEmptyMessage(REGIS_EXCEPTION);
					}
				}

			}).start();
		} else {
			handler.sendEmptyMessage(NET_ERROR);
		}

	}

	private static void parseRegisterData(JSONObject response, Handler handler) {
		try {
			String sucResult = response.getString("success").trim();
			if (sucResult.equals("false")) {
				Message msg = new Message();
				msg.what = REGIS_FAIL;
				msg.obj = response.getString("msg").trim();
				handler.sendMessage(msg);
			} else {
				handler.sendEmptyMessage(REGIS_SUC);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(REGIS_EXCEPTION);
		}
	}

	public static void login(final Context context, final Handler handler,
			final User user) {

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String url = RequestUrl.HOST_URL + RequestUrl.account.login;

				try {
					String susername = URLEncoder.encode(user.getUsername(), "UTF-8");
					susername = URLEncoder.encode(susername, "UTF-8");

					String password = URLEncoder.encode(user.getPassword(), "UTF-8");
					password = URLEncoder.encode(password, "UTF-8");

					JSONObject reqJsonObject = new JSONObject();
					try {
						reqJsonObject.put("username", susername);
						reqJsonObject.put("password", password);
					} catch (JSONException e) {
						return;
					}

					if (null != reqJsonObject
							&& !TextUtils.isEmpty(reqJsonObject.toString())) {
						String resultStr = HttpUtils.sendHttpRequestByHttpClientPost(
								url, reqJsonObject);

						if (!TextUtils.isEmpty(resultStr)) {
							JSONObject jsonObject = new JSONObject(resultStr);
							parseLoginData(jsonObject, handler);
						} else {
							handler.sendEmptyMessage(LOGIN_FAIL);
						}
					}

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}

	private static void parseLoginData(JSONObject response, Handler handler) {
		try {
			String sucResult = response.getString("success").trim();
			if (sucResult.equals("false")) {
				handler.sendEmptyMessage(LOGIN_FAIL);
			} else {
				handler.sendEmptyMessage(LOGIN_SUC);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(LOGIN_EXCEPTION);
		}
	}

}
