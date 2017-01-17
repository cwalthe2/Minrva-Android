package edu.illinois.ugl.minrva.room_reserves.models;

/**
 * 
 * @version 2.0
 * @author Collin Walther cbwalther1@gmail.com
 */
public class Buildings {
	private String picurl;
	private String description;
	private int id;
	private String name;

	/**
	 * 
	 */
	public Buildings() {
		super();
		this.picurl = "";
		this.description = "";
		this.id = 0;
		this.name = "";

	}

	/**
	 * 
	 * @param picurl
	 * @param desc
	 * @param id
	 * @param name
	 */
	public Buildings(String picurl, String desc, int id, String name) {
		super();
		this.picurl = picurl;
		this.description = desc;
		this.id = id;
		this.name = name;
	}

	public String getPic() {
		return picurl;
	}

	public void setPic(String picurl) {
		this.picurl = picurl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}