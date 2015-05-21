package com.o2o.maileseller.network.logic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.o2o.maileseller.BaseApplication;
import com.o2o.maileseller.entity.OrderInfo;
import com.o2o.maileseller.network.config.MsgResult;
import com.o2o.maileseller.network.config.RequestUrl;
import com.o2o.maileseller.network.netty.codec.JsonUtil;
import com.o2o.maileseller.network.netty.codec.JsonUtil.JsonException;
import com.o2o.maileseller.network.utils.HttpUtils;
import com.o2o.maileseller.network.volley.Request.Method;
import com.o2o.maileseller.network.volley.Response.Listener;
import com.o2o.maileseller.network.volley.toolbox.JsonObjectRequest;

public class OrderLogic {

	public static final int NET_ERROR = 0;

	public static final int ORDER_ROB_SUC = NET_ERROR + 1;

	public static final int ORDER_ROB_FAIL = ORDER_ROB_SUC + 1;

	public static final int ORDER_ROB_EXCEPTION = ORDER_ROB_FAIL + 1;

	public static final int ORDERLIST_GET_SUC = ORDER_ROB_EXCEPTION + 1;

	public static final int ORDERLIST_GET_FAIL = ORDERLIST_GET_SUC + 1;

	public static final int ORDERLIST_GET_EXCEPTION = ORDERLIST_GET_FAIL + 1;

	public static void robOrder(final Context context, final Handler handler,
			final String orderID, final String userID, final String telphone,
			final String message) {

		String url = RequestUrl.HOST_URL + RequestUrl.order.sellerRobOrder;

		JSONObject requestJson = new JSONObject();
		try {
			requestJson.put("orderID", URLEncoder.encode(orderID, "UTF-8"));
			requestJson.put("probablyWaitTime", Integer.parseInt("123"));
			requestJson.put("message", URLEncoder.encode(message, "UTF-8"));
			requestJson.put("username", URLEncoder.encode(userID, "UTF-8"));
			requestJson.put("telphone", URLEncoder.encode(telphone, "UTF-8"));

			BaseApplication.getInstanceRequestQueue().add(
					new JsonObjectRequest(Method.POST, url, requestJson,
							new Listener<JSONObject>() {
								@Override
								public void onResponse(JSONObject response) {
									if (null != response) {
										Log.e("xxx_robOrder",
												response.toString());
										parseRobOrderData(response, handler);
									}

								}
							}, null));
			BaseApplication.getInstanceRequestQueue().start();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	// {"message":"订单已被其他买家抢走，继续努力哦!","result":false}
	private static void parseRobOrderData(JSONObject response, Handler handler) {
		try {
			String sSuc = response.getString(MsgResult.RESULT_TAG).trim();
			String sMsg = response.getString(MsgResult.RESULT_MSG_TAG).trim();

			Message message = new Message();
			if (!TextUtils.isEmpty(sSuc)
					&& MsgResult.RESULT_SUCCESS.equals(sSuc)) {
				message.what = ORDER_ROB_SUC;
				message.obj = sMsg;
			} else {
				message.what = ORDER_ROB_FAIL;
				message.obj = sMsg;
			}
			handler.sendMessage(message);

		} catch (JSONException e) {
			handler.sendEmptyMessage(ORDER_ROB_EXCEPTION);
		}
	}

	// {"limit":25,"skip":0,"states":["success","timeout"],"sellerName":"12369"}
	public static void getMyOrderList(final Context context,
			final Handler handler, final String userName, final int skip,
			final int limit, ArrayList<String> statesList) {
		if (HttpUtils.checkNetWorkInfo(context)) {
			String url = RequestUrl.HOST_URL
					+ RequestUrl.order.querySellerOrder;

			JSONObject requestJson = new JSONObject();
			JSONArray statesJsonArray = new JSONArray(statesList);
			try {
				requestJson.put("sellerName",
						URLEncoder.encode(userName, "UTF-8"));
				requestJson.put("skip", skip);
				requestJson.put("limit", limit);
				requestJson.put("states", statesJsonArray);

				Log.e("xxx_OrderLogic", requestJson.toString());
				Log.e("xxx_OrderLogic_url", url);

				BaseApplication.getInstanceRequestQueue().add(
						new JsonObjectRequest(Method.POST, url, requestJson,
								new Listener<JSONObject>() {
									@Override
									public void onResponse(JSONObject response) {
										if (null != response) {
											Log.e("xxx_orderlist",
													response.toString());
											parseMyOrderListData(response,
													handler);
										}

									}
								}, null));
				BaseApplication.getInstanceRequestQueue().start();
			} catch (UnsupportedEncodingException e) {
				handler.sendEmptyMessage(ORDERLIST_GET_EXCEPTION);
			} catch (JSONException e) {
				handler.sendEmptyMessage(ORDERLIST_GET_EXCEPTION);
			}
		} else {
			handler.sendEmptyMessage(NET_ERROR);
		}
	}

	// {"orderInfos":[{"state":"challenged","goods":["HX0001"],"buyer":"12369","createTime":1429607961419,"buyerComment":"","seller":"12369","sellerComment":""},
	// {"state":"challenged","goods":["HX0001"],"buyer":"12369","createTime":1429685379927,"buyerComment":"","seller":"12369","sellerComment":""},
	// List<OrderInfo> orderInfos = new ArrayList<>();

	private static void parseMyOrderListData(JSONObject response,
			Handler handler) {
		try {
			JSONArray orderListArray = response.getJSONArray("orderInfos");
			int size = orderListArray.length();
			
			ArrayList<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
			JSONObject orderJsonObject = new JSONObject();

			for (int i = 0; i < size; i++) {
				OrderInfo order = new OrderInfo();
				orderJsonObject = orderListArray.getJSONObject(i);
				order = JsonUtil.jsonToBean(orderJsonObject.toString(),
						OrderInfo.class);
				orderInfos.add(order);
			}
			Message message = new Message();
			message.what = ORDERLIST_GET_SUC;
			message.obj = orderInfos;
			handler.sendMessage(message);

			// String sGoodsName =
			// orderJsonObject.getString("goods").trim();

			// JSONArray goodsNameListArray = orderJsonObject
			// .getJSONArray("goods");
			// int size2 = goodsNameListArray.length();
			// String sGoodsName;
			// for (int j = 0; j < size2; j++) {
			// sGoodsName =
			// }
			// String buyer = orderJsonObject.getString("buyer").trim();
			// String seller = orderJsonObject.getString("seller").trim();
			// String sellerComment = orderJsonObject.getString(
			// "sellerComment").trim();
			// String buyerComment =
			// orderJsonObject.getString("buyerComment")
			// .trim();
		} catch (JSONException e) {
			handler.sendEmptyMessage(ORDERLIST_GET_EXCEPTION);
		} catch (JsonException e) {
			handler.sendEmptyMessage(ORDERLIST_GET_EXCEPTION);
		}

	}
}
