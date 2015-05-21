package com.o2o.maileseller.network.push.message;

import com.o2o.maileseller.network.netty.NettyMessageType;
import com.o2o.maileseller.network.netty.codec.JsonUtil;
import com.o2o.maileseller.network.netty.codec.JsonUtil.JsonException;
import com.o2o.maileseller.network.netty.handler.NettyMessage;
import com.o2o.maileseller.network.netty.handler.NettyMessageImpl;

public class PushOrder2SellerRequest {

	public static NettyMessage encode(PushOrder2SellerRequest request) {
		String messageBody;
		try {
			messageBody = JsonUtil.beanToJson(request);
			NettyMessage nettyMessage = new NettyMessageImpl(
					NettyMessageType.PushSellOrderInfo.getType(), messageBody);
			return nettyMessage;
		} catch (JsonException e) {
			return null;
		}
	}

	String orderID;

	String goodsName;

	String goodsBrief;

	String totalPrice;

	int buyNum;

	String buyerAddress;

	String buyerPhone;

	String buyerName;

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsBrief() {
		return goodsBrief;
	}

	public void setGoodsBrief(String goodsBrief) {
		this.goodsBrief = goodsBrief;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getBuyNum() {
		return buyNum;
	}

	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}

	public String getBuyerAddress() {
		return buyerAddress;
	}

	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}

	public String getBuyerPhone() {
		return buyerPhone;
	}

	public void setBuyerPhone(String buyerPhone) {
		this.buyerPhone = buyerPhone;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

}
