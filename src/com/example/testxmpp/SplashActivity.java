package com.example.testxmpp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		
		Button bt_go_to_chat = (Button)findViewById(R.id.bt_go_to_chat);
		bt_go_to_chat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}
}