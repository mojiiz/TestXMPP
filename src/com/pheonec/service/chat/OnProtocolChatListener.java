package com.pheonec.service.chat;

public interface OnProtocolChatListener {
	void onReceiveMessage(String from, String message);
	void onConnectionFail(String messageError);
	void onSendMessageSuccess(long messageID);
	void onSendMessageFail(long messageID, String messageError);
	void onConnected();
}