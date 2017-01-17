package edu.illinois.ugl.minrva.new_titles.models;

public class NewTitlesBook {
	private String image, author, title, bib_id, location;

	public NewTitlesBook() {

		image = "";
		author = "";
		title = "";
		bib_id = "";
		location = "";
	}

	public NewTitlesBook(String image, String author, String title, String bib_id, String location) {
		this.image = image;
		this.author = author;
		this.title = title;
		this.bib_id = bib_id;
		this.location = location;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBibid() {
		return bib_id;
	}

	public void setBibid(String bibid) {
		this.bib_id = bibid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
