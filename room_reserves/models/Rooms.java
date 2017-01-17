package edu.illinois.ugl.minrva.room_reserves.models;

/**
 * 
 * @version 2.0
 * @author Collin Walther	cbwalther1@gmail.com
 */
public class Rooms {
	private String picurl;
	private String description;
	private String mapurl;
	private int maxOccupancy;
	private String name;
	private int buildingID;
	private int roomID;

	/**
	 * 
	 */
	public Rooms() {
		super();
		this.picurl = "";
		this.description = "";
		this.mapurl = "";
		this.maxOccupancy = 0;
		this.name = "";
		this.buildingID = 0;
		this.roomID = 0;

	}

	/**
	 * 
	 * @param picurl
	 * @param desc
	 * @param mapurl
	 * @param maxOccupancy
	 * @param name
	 * @param buildingID
	 * @param roomID
	 */
	public Rooms(String picurl, String desc, String mapurl, int maxOccupancy, String name, int buildingID, int roomID) {
		super();
		this.picurl = picurl;
		this.description = desc;
		this.mapurl = mapurl;
		this.maxOccupancy = maxOccupancy;
		this.name = name;
		this.buildingID = buildingID;
		this.roomID = roomID;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMapurl() {
		return mapurl;
	}

	public void setMapurl(String mapurl) {
		this.mapurl = mapurl;
	}

	public int getMaxOccupancy() {
		return maxOccupancy;
	}

	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBuildingID() {
		return buildingID;
	}

	public void setBuildingID(int buildingID) {
		this.buildingID = buildingID;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	


}