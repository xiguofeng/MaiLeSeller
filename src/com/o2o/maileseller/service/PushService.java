package com.o2o.maileseller.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.o2o.maileseller.R;
import com.o2o.maileseller.entity.NotifyInfo;
import com.o2o.maileseller.network.logic.ConnectLogic;
import com.o2o.maileseller.network.netty.ConnectException;
import com.o2o.maileseller.network.netty.NettyMessageType;
import com.o2o.maileseller.network.netty.client.ClientConnectStateMonitor;
import com.o2o.maileseller.network.netty.handler.AbstractNettyMessageHandler;
import com.o2o.maileseller.network.netty.handler.NettyMessageHandler;
import com.o2o.maileseller.network.netty.handler.NettyMessageHandlerFactory;
import com.o2o.maileseller.network.push.client.PushClient;
import com.o2o.maileseller.network.push.client.PushClientImpl;
import com.o2o.maileseller.network.push.message.PushOrder2SellerRequest;
import com.o2o.maileseller.notify.Notify;
import com.o2o.maileseller.notify.NotifyFactory;
import com.o2o.maileseller.ui.activity.RobOrderActivity;
import com.o2o.maileseller.util.AppInfoManager;

/**
 * 
 * Push 服务.
 * 
 */
public class PushService extends Service {
	public static final String NOTIFY_DATA_UPDATE = "action.notify.data.update";

	public static final String NOTIFY_NEW_ORDER = "action.notify.data.order";;

	private Context mContext;
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mBuilder;

	public static HashMap<Integer, NotifyInfo> sNotifyHistory = new HashMap<Integer, NotifyInfo>();
	HashMap<Integer, Notification> mNotifications = new HashMap<Integer, Notification>();
	private static int sNotifyId = 0;

	public static PushClient sCli;

	public static ArrayList<PushOrder2SellerRequest> sPushOrder2SellerList = new ArrayList<PushOrder2SellerRequest>();

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case ConnectLogic.CONNECT_SUC: {
				AppInfoManager.setPushUrl(mContext, (String) msg.obj);
				pushConnection();
				break;
			}
			case ConnectLogic.CONNECT_FAIL: {
				Toast.makeText(mContext, R.string.reg_fail, Toast.LENGTH_SHORT)
						.show();
				break;
			}
			case ConnectLogic.CONNECT_EXCEPTION: {
				break;
			}
			case ConnectLogic.NET_ERROR: {
				break;
			}

			default:
				break;
			}

		}

	};

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		// initNotify();
		ConnectLogic.connect(mContext, mHandler);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void pushConnection() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String pushUrl = AppInfoManager.getPushUrl(mContext);
				if (TextUtils.isEmpty(pushUrl)) {
					return;
				}

				String[] divisionUrl = pushUrl.split(":");
				if (divisionUrl.length < 2) {
					return;
				}
				String host = divisionUrl[0];
				int port = Integer.valueOf(divisionUrl[1]);
				Log.e("xxx_pushurl", host + port);

				// [1] 启动客户端
				sCli = new PushClientImpl(host, port);
				Log.e("xxx_push", "gogogo");
				// PushOrder2SellerRequest pushOrder2SellerRequest = new
				// PushOrder2SellerRequest();
				// pushOrder2SellerRequest.setBuyerAddress("buyerAddress");
				// pushOrder2SellerRequest.setBuyerPhone("setBuyerPhone");
				// pushOrder2SellerRequest.setBuyNum(1);
				// pushOrder2SellerRequest.setGoodsName("goodsName");
				// pushOrder2SellerRequest.setOrderID("orderID");
				// pushOrder2SellerRequest.setTotalPrice("TotalPrice");
				// sendMsg(pushOrder2SellerRequest);
				try {
					sCli.start();

					// [3] 定义推送消息处理类
					NettyMessageHandlerFactory factory = new NettyMessageHandlerFactory() {

						@Override
						public List<Integer> getNettyMessageTypes() {

							return Arrays
									.asList(NettyMessageType.PushSellOrderInfo
											.getType());
						}

						@Override
						public NettyMessageHandler createNettyMessageHandler(
								Integer messageType) {

							NettyMessageHandler h = new AbstractNettyMessageHandler<PushOrder2SellerRequest>() {

								@Override
								protected void handle(
										ChannelHandlerContext ctx,
										PushOrder2SellerRequest request) {
									Log.e("xxx_push_end", request.toString());
									sendMsg(request);
									System.out.println(request.toString());
								}

								@Override
								protected Class<PushOrder2SellerRequest> getRequestType() {
									return PushOrder2SellerRequest.class;
								}

							};
							return h;
						}
					};

					sCli.addMessageHandlerFactory(factory);

					ClientConnectStateMonitor monitor = new ClientConnectStateMonitor() {

						@Override
						protected void disconnect(Channel channel) {
							// TODO 添加提示消息

						}

						@Override
						protected void connected(Channel channel) {

						}

						@Override
						protected void connectFailed(Channel channel) {
						}

					};
					sCli.addClientConnectStateMonitor(monitor);

					String clientID = "";
					while (TextUtils.isEmpty(clientID)) {
						Thread.sleep(2000);
						clientID = sCli.clientID();
						AppInfoManager.setClinetID(getApplicationContext(),
								clientID);
						Log.e("xxx_clientID", clientID);
					}

				} catch (ConnectException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	// {"goodsName":"HX0001","orderID":"26369587708415921321169590958","buyerPhone":"HX0001","buyerName":"111",
	// "buyNum":1111,"buyerAddress":"%E6%B1%9F%E8%8B%8F%E7%9C%81%E5%8D%97%E4%BA%AC%E5%B8%82%E9%9B%A8%E8%8A%B1%E5%8F%B0%E5%8C%BA%E5%B0%8F%E8%A1%8C%E8%B7%AF130%E5%8F%B7%E9%99%84%E8%BF%91",
	// "totalPrice":"123321","goodsBrief":"HX0001"}
	private void sendMsg(PushOrder2SellerRequest newOrder) {

		if (null != newOrder) {
			sPushOrder2SellerList.add(newOrder);
			Log.e("xxx_sPushOrder2SellerListadd", "");
			Notify newOrderNotify = NotifyFactory
					.create(NotifyFactory.NotifyType.NewOrder);
			newOrderNotify.sendMsg(mContext, newOrder);

			NotifyInfo notifyInfo = new NotifyInfo();
			notifyInfo.setNotifyType(NotifyFactory.NotifyType.NewOrder);
			notifyInfo.setPhone(newOrder.getBuyerPhone());
			notifyInfo.setDate(newOrder.getTotalPrice());
			notifyInfo.setTitle(getResources().getString(R.string.new_order));
			notifyInfo.setContent(newOrder.getBuyerPhone() + "购买物品:"
					+ newOrder.getGoodsName());
			showIntentActivityNotify(notifyInfo);
		}

	}

	private void showNotification(NotifyInfo notifyInfo) {

		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		// notification.flags |= Notification.FLAG_ONGOING_EVENT;// 表示正处于活动中
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;

		Intent intent = new Intent(this, RobOrderActivity.class);
		// 这里要加入此Flags，作用：当你通过点击通知栏来唤起Activity时，对应的Activity启动模式要为android:launchMode="singleTop"
		// 于此Flag一起使用，可以保证你要启动的Activity不会重新启动，在一个堆栈只有一个对应的实例对象
		// 使用这个标志，如果正在启动的Activity的Task已经在运行的话，那么，新的Activity将不会启动；代替的，当前Task会简单的移入前台。
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent pendingIntent = PendingIntent.getActivity(this,
				sNotifyId + 1, intent, 0);
		notification.contentIntent = pendingIntent;

		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notification);
		contentView.setImageViewResource(R.id.nf_icon, R.drawable.ic_launcher);
		notification.contentView = contentView;

		long time = SystemClock.elapsedRealtime();

		// notification.contentView.setTextViewText(R.id.nf_content,
		// notifyInfo.getTitle());
		notification.contentView.setTextViewText(R.id.nf_content,
				notifyInfo.getTitle() + ":" + notifyInfo.getContent());

		// 使用服务来启动通知栏，这样的好处时，
		// 1.当应用程序在后台运行时，startForeground可以使本应用优先级提高，不至于被系统杀掉
		// 2.当应用被异常挂掉时，可以保证通知栏对应的图标被系统移除
		startForeground(sNotifyId + 1, notification);
		// mNotificationManager.notify(sNotifyId + 1, notification);
		sNotifyHistory.put(sNotifyId, notifyInfo);
		mNotifications.put(sNotifyId, notification);
		sNotifyId++;

	}

	/** 初始化通知栏 */
	private void initNotify() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(getApplicationContext());
	}

	/** 显示通知栏点击跳转到指定Activity */
	private void showIntentActivityNotify(NotifyInfo notifyInfo) {
		// Notification.FLAG_ONGOING_EVENT --设置常驻
		// Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
		// notification.flags = Notification.FLAG_AUTO_CANCEL;
		// //在通知栏上点击此通知后自动清除此通知
		mBuilder.setAutoCancel(true)
				// 点击后让通知将消失
				.setContentTitle(notifyInfo.getTitle())
				.setContentText(notifyInfo.getContent()).setTicker("点我")
				.setSmallIcon(R.drawable.ic_launcher)
				.setDefaults(Notification.DEFAULT_VIBRATE);
		// 点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(this, RobOrderActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		Log.e("xxx_mNotificationManager", "mNotificationManager");
		mNotificationManager.notify(100, mBuilder.build());
	}
}
