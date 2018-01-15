package com.routee.routeetoast;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ToastService extends Service {

	private RouteeToast rocketToast;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("火箭服务打开了");
		rocketToast = new RouteeToast(this);
		
		rocketToast.showRocketToast();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("火箭服务关闭了");
		rocketToast.hideRocketToast();
	}

}
