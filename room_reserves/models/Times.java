package edu.illinois.ugl.minrva.room_reserves.models;

/**
 * 
 * @version 2.0
 * @author Collin Walther	cbwalther1@gmail.com
 */
import java.util.ArrayList;

public class Times
{	
	
	private String roomid;
	private ArrayList<String> starttimes;
	
	
	
	public Times(String roomid, ArrayList<String> starttimes) {
		super();
		this.roomid = roomid;
		this.starttimes = starttimes;
	}
	
	public Times() {
		super();
		this.roomid = "";
		this.starttimes = new ArrayList<String>();
	}
	
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public ArrayList<String> getStartTimes() {
		return starttimes;
	}
	public void setStarttimes(ArrayList<String> startTimes) {
		this.starttimes = startTimes;
	}
	
}