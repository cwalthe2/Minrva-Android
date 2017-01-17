package edu.illinois.ugl.minrva.new_titles.models;

public class Lib {
	private String lib_name;
	private boolean isChecked;
	private int id;
	public Lib(){
		super();
		this.lib_name="";
		this.isChecked = false;
		this.id=0;
	}
	
	public Lib(String lib_name, boolean isChecked,int id){
		super();
		this.lib_name = lib_name;
		this.isChecked = isChecked;
		this.id=id;
	}

	public String getName() {
		return lib_name;
	}

	public void setName(String lib_name) {
		this.lib_name = lib_name;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

}
