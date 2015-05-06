package com.o2o.maileseller.notify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.o2o.maileseller.network.push.message.PushOrder2SellerRequest;
import com.o2o.maileseller.service.PushService;

public class NewOrderNotify implements Notify {

	@Override
	public void sendMsg(Context context, Object object) {
		PushOrder2SellerRequest order = (PushOrder2SellerRequest) object;

		Bundle localBundle = new Bundle();
		localBundle.putString("buyerAddress", order.getBuyerAddress());
		localBundle.putString("buyerPhone", order.getBuyerPhone());
		localBundle.putString("buyNum", String.valueOf(order.getBuyNum()));
		localBundle
				.putString("buyerName", String.valueOf(order.getBuyerName()));
		localBundle.putString("goodsName", order.getGoodsName());
		localBundle.putString("orderID", order.getOrderID());
		localBundle.putString("totalPrice", order.getTotalPrice());
		localBundle.putString("goodsBrief", order.getGoodsBrief());
		Intent localIntent = new Intent(PushService.NOTIFY_NEW_ORDER);
		localIntent.putExtras(localBundle);
		context.sendBroadcast(localIntent);
	}

}
