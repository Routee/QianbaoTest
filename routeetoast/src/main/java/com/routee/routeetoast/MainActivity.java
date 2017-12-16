package com.routee.routeetoast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void start(View v){
		//开启火箭服务
		startService(new Intent(MainActivity.this,RocketService.class));
		finish();
	}
	public void stop(View v){
		//关闭火箭服务
		stopService(new Intent(MainActivity.this,RocketService.class));
	}
	
}
