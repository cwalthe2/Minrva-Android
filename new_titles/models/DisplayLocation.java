package edu.illinois.ugl.minrva.new_titles.models;

public class DisplayLocation 
{
	public String label;
	public String code;
	
	
	public DisplayLocation(String label, String code) {
		super();
		this.label = label;
		this.code = code;
	}
	public DisplayLocation() {
		super();
		this.label = "";
		this.code = "";
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
}