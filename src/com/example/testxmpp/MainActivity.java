package com.example.testxmpp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pheonec.service.chat.ProtocolChat;
import com.pheonec.service.chat.OnProtocolChatListener;

public class MainActivity extends Activity implements OnProtocolChatListener{
	
	private ProtocolChat chat;
	
	private LinearLayout layout_chat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		
		chat = ProtocolChat.getInstance();
		chat.setOnProtocolChatListener(this);
		chat.connect("192.168.1.119", 5222, null, "a", "a");
		
		layout_chat = (LinearLayout)findViewById(R.id.layout_message);
		
		Button btTest = (Button)findViewById(R.id.bt_test);
		btTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				chat.sendMessage(System.currentTimeMillis(), "mojiiz@pheonec-pc", "Hello Mojiizu");
			}
		});
		
		Button btSendfile = (Button)findViewById(R.id.bt_send_file);
		btSendfile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				chooseImageIntent(MENU_CAMERA_GALLERY);
			}
		});
	}

	@Override
	public void onReceiveMessage(String from, String message) {
		final LinearLayout chatMessage = new LinearLayout(MainActivity.this);
		chatMessage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		chatMessage.setOrientation(LinearLayout.VERTICAL);
		TextView tFrom = new TextView(MainActivity.this);
		TextView tMessage = new TextView(MainActivity.this);
		tFrom.setText(from);
		tMessage.setText(message);
		chatMessage.addView(tFrom);
		chatMessage.addView(tMessage);
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				layout_chat.addView(chatMessage);
			}
		});
	}

	@Override
	public void onConnectionFail(String message) {
		Log.w("test", message);
	}

	@Override
	public void onConnected() {
//		chat.getFriends();
		Log.w("test", "connected");
	}

	@Override
	public void onSendMessageFail(long messageID, String message) {
		Log.w("test", messageID + ":" + message);
	}

	@Override
	public void onSendMessageSuccess(long messageID) {
		Log.w("test", messageID + "");
	}
	
	
	int MENU_CAMERA_GALLERY = 0;
	int REQUEST_CODE_CAMERA_GALLERY = 1;
	
	private void chooseImageIntent(int viewID){
//		if(viewID == MENU_CAMERA_CAPTURE){
//			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//			File imageFile = new File(Environment.getExternalStorageDirectory() ,"/joyspaces.jpg");
//			imageUri = imageFile.getPath();
//			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
//			startActivityForResult(cameraIntent, ActivityUtils.REQUEST_CODE_CAMERA_CAPTURE); 
//		}else 
		if(viewID == MENU_CAMERA_GALLERY){
			 Intent intent = new Intent();
             intent.setType("image/*");
             intent.setAction(Intent.ACTION_GET_CONTENT);
             startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_CODE_CAMERA_GALLERY);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			if(requestCode == REQUEST_CODE_CAMERA_GALLERY){
				try{
					Uri select = data.getData();
	    			String path = getRealPathFromURI(this, select);
	    			chat.sendFile("", path);
	    			
//	    			if(softBmp != null)BitmapUtils.destroy(softBmp.get());

//	    			setOrientation(path);
	    			
//	    			if(softBmp != null && softBmp.get() != null){
//						joyerView.destroy();
//						joyerView.addButtonController(MENU_SEND, "Submit", R.drawable.style_joyer_button);
//						joyerView.addButtonController(MENU_CANCEL, "Cancel", R.drawable.style_joyer_button);
//						joyerView.addButtonController(MENU_RE_GALLERY, "Reselect", R.drawable.style_joyer_button);
//						joyerView.setOnMenuSelectedListener(this);
//						joyerView.setContentAnimation(AnimationUtils.fade(500, 0.0f, +1.0f));
//						joyerView.setContent(softBmp.get());
//					}else{
//						dialogUtils.showToast(getResources().getString(R.string.error_cannot_detect_your_image), Toast.LENGTH_LONG);
//						finish();
//					}
				}catch (Exception e) {
					Log.w("test", e.toString());
//					dialogUtils.showToast(getResources().getString(R.string.error_cannot_encode_your_image), Toast.LENGTH_LONG);
//					finish();
				}
			}
		}else if(resultCode == RESULT_CANCELED){
//			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public static String getRealPathFromURI(Context context, Uri contentUri) {
//		freeGC();
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = ((Activity) context).managedQuery(contentUri, proj, // Which
																			// columns
																			// to
																			// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

}