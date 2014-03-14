package com.pheonec.service.chat;

import java.io.File;
import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.provider.BytestreamsProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;

import android.util.Log;

public class ProtocolChat implements RosterListener, PacketListener {

	private static ProtocolChat instance;
	private ConnectionConfiguration connection;
	private XMPPConnection xmpp;
	private Roster roster;

	private OnProtocolChatListener callback;

	private ProtocolChat() {}

	/**
	 * 
	 * @return A singleton of ChatProtocol instance
	 */
	public static ProtocolChat getInstance() {
		if (instance == null)
			instance = new ProtocolChat();
		return instance;
	}

	public void setOnProtocolChatListener(OnProtocolChatListener callback) {
		this.callback = callback;
	}

	/**
	 * Connect to your xmpp server
	 * 
	 * @param host
	 * @param port
	 * @param serviceName
	 * @param username
	 * @param password
	 */
	public void connect(String host, int port, String serviceName, String username, String password) {
		try {
			disconnect();

			if (serviceName != null)
				connection = new ConnectionConfiguration(host, port,serviceName);
			else
				connection = new ConnectionConfiguration(host, port);

			xmpp = new XMPPConnection(connection);
			xmpp.connect();
			xmpp.login(username, password);
			roster = xmpp.getRoster();
			roster.addRosterListener(this);

			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			xmpp.addPacketListener(this, filter);

			if (callback != null)
				callback.onConnected();
		} catch (Exception e) {
			if (callback != null)
				callback.onConnectionFail(e.toString());
		}
	}

	/**
	 *
	 * 
	 * @param messageID : Let you put your unique message id
	 * @param to
	 * @param mesage
	 */
	public void sendMessage(long messageID, String to, String mesage) {
		Message msg = new Message(to, Message.Type.normal);
		msg.setBody(mesage);
		
		try{
			xmpp.sendPacket(msg);
			if (callback != null)
				callback.onSendMessageSuccess(messageID);
		}catch(Exception e){
			if (callback != null)
				callback.onSendMessageFail(messageID, e.toString());
		}
//		Chat chat = xmpp.getChatManager().createChat(to, new MessageListener() {
//			
//			@Override
//			public void processMessage(Chat chat, Message message) {
////				Log.w("test", message.getBody());
//				if(callback != null){
//					callback.onReceiveMessage(message.getFrom(), message.getBody());
//				}
//			}
//		});
//		
//		Message msg = new Message(to, Message.Type.normal);
//		msg.setBody(mesage);
//		try {
//			chat.sendMessage(msg);
//		} catch (XMPPException e) {
//			if (callback != null)
//				callback.onConnectionFail(e.toString());
//		}
	}
	
	public void sendFile(String to, String filePath){
		try{
			ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
			ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
			ProviderManager.getInstance().addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
			
			ServiceDiscoveryManager discoveryManager = new ServiceDiscoveryManager(xmpp);
			discoveryManager.addFeature("http://jabber.org/protocol/disco#info");
			discoveryManager.addFeature("jabber:iq:privacy");
			
			FileTransferManager manager = new FileTransferManager(xmpp);
//			OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer("mojiiz@pheonec-pc/Smack");
			OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(to);
			File file = new File(filePath);
			try {
			   transfer.sendFile(file, "test_file");
			} catch (XMPPException e) {
			   e.printStackTrace();
			}
			while(!transfer.isDone()) {
			   if(transfer.getStatus().equals(Status.error)) {
			      System.out.println("ERROR!!! " + transfer.getError());
			   } else if (transfer.getStatus().equals(Status.cancelled)
			                    || transfer.getStatus().equals(Status.refused)) {
			      System.out.println("Cancelled!!! " + transfer.getError());
			   }
			   try {
			      Thread.sleep(1000L);
			   } catch (InterruptedException e) {
			      e.printStackTrace();
			   }
			}
			if(transfer.getStatus().equals(Status.refused) || transfer.getStatus().equals(Status.error)
			 || transfer.getStatus().equals(Status.cancelled)){
			   System.out.println("refused cancelled error " + transfer.getError());
			   Log.w("test", transfer.getError() + "" + transfer.getStatus());
			   Log.w("test", transfer.getError() + "" + transfer.getStatus());
			} else {
			   System.out.println("Success");
			}
		}catch(Exception e){
			Log.w("test", e.toString());
		}
	}

	public void disconnect() {
		if (xmpp != null) {
			xmpp.disconnect();
			xmpp = null;
		}
	}
	
	@Override
	public void processPacket(Packet packet) {
		if(callback != null){
			Message message = (Message) packet;
			callback.onReceiveMessage(message.getFrom(), message.getBody());
		}
	}

	@Override
	public void entriesAdded(Collection<String> arg0) {}

	@Override
	public void entriesDeleted(Collection<String> arg0) {}

	@Override
	public void entriesUpdated(Collection<String> arg0) {}

	@Override
	public void presenceChanged(Presence presence) {}
}