package com.pheonec.service.chat;

import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONObject;

class JSONFactory {

	public static JSONObject receiveMessage(String from, String message){
		try{
			JSONObject json = new JSONObject();
			json.put("from", from);
			json.put("message", message);
			json.put("timestamp", System.currentTimeMillis());
			return json;
		}catch(Exception e){}
		
		return null;
	}
	
	public static JSONObject retrieveFriendsList(Roster roster, Collection<RosterEntry> entries){
		try{
			JSONObject json = new JSONObject();
			JSONArray jArr = new JSONArray();
			for (RosterEntry entry : entries) {
				Presence entryPresence = roster.getPresence(entry.getUser());
				JSONObject jFriend = new JSONObject();
				jFriend.put("name", entryPresence.getFrom());
				jArr.put(jFriend);
			}
			json.put("data", jArr);
			return json;
		}catch(Exception e){}
		return null;
	}
}