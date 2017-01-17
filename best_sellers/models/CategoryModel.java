package edu.illinois.ugl.minrva.best_sellers.models;

import java.util.ArrayList;

public class CategoryModel
{	
	private ArrayList<String> groups;

	
	
	public CategoryModel(ArrayList<String> groups) {
		super();
		this.groups = groups;
	}

	public CategoryModel() {
		super();
		this.groups = new ArrayList<String>();
	}
	
	public ArrayList<String> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}
	

	
}