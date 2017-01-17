package edu.illinois.ugl.minrva.new_titles;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import edu.illinois.ugl.minrva.R;
import edu.illinois.ugl.minrva.core.MinrvaApp;
import edu.illinois.ugl.minrva.core.adapter.ItemView;
import edu.illinois.ugl.minrva.core.adapter.ListItem;
import edu.illinois.ugl.minrva.core.adapter.MinrvaAdapter;
import edu.illinois.ugl.minrva.core.connections.HTTP;
import edu.illinois.ugl.minrva.core.display.Display;
import edu.illinois.ugl.minrva.core.help.HelpActivity;
import edu.illinois.ugl.minrva.new_titles.models.DisplayLocation;
import edu.illinois.ugl.minrva.new_titles.models.NewTitlesBook;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;

public class ShowTitles extends GDActivity implements OnItemClickListener {

	private String url;
	private ListView lv;
	private MinrvaAdapter ma;
	private Activity activity;
	private ArrayList<ListItem> lis;
	ProgressDialog progressDialog;

	public void onCreate(Bundle savedInstanceState) {
		// Load
		activity = this;
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.show_titles);
		addActionBarItem(Type.Help);

		lis = new ArrayList<ListItem>();
		lv = (ListView) findViewById(R.id.show_titles_list);
		ma = new MinrvaAdapter(this, lis);
		lv.setAdapter(ma);
		lv.setOnItemClickListener(this);
		lv.setFastScrollEnabled(true);
		url = getIntent().getStringExtra("url");
		new DownloadBooks().execute();
	}

	private class DownloadBooks extends AsyncTask<Void, Void, NewTitlesBook[]> {

		@Override
		protected void onPreExecute() {
			if (HTTP.isNetworkAvailable()) {
				 progressDialog = ProgressDialog.show(activity, "","Loading...");
				// Clear current list before downloading more stuff
				lis.clear();
				ma.notifyDataSetChanged();

			} else {
				HTTP.alertFinish(activity);
			}
		}

		protected NewTitlesBook[] doInBackground(Void... params) {

			Log.d(null, "fetching data from "+url);
			NewTitlesBook[] ntBooks = HTTP.downloadObjects(url, NewTitlesBook[].class);
			return ntBooks;
		}

		protected void onPostExecute(NewTitlesBook[] ntBooks) {
			if (HTTP.isNetworkAvailable() && ntBooks != null) 
			{
				if (ntBooks.length == 0)
					lv.setVisibility(View.GONE);
				else
					lv.setVisibility(View.VISIBLE);
				
				TextView tv1 = (TextView)findViewById(R.id.show_titles_description);
				if (ntBooks.length < 100)
					tv1.setText("Number of results: "+ntBooks.length);
				else
					tv1.setText("Number of results: Displaying first 100 titles");

				bindData(ntBooks);
				ma.notifyDataSetChanged();
			}
			
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}

	public void bindData(NewTitlesBook[] ntBooks) {
		if (HTTP.isNetworkAvailable() && ntBooks != null) {

			// Log.d("books", "Book length = "+bsBooks.length);

			for (int i = 0; i < ntBooks.length; i++) {
				// Get book
				NewTitlesBook NewTitlesBook = ntBooks[i];

				// Get data from book
				String image = ntBooks[i].getImage();
				String author = ntBooks[i].getAuthor();
				String title = ntBooks[i].getTitle();
				String location = ntBooks[i].getLocation();

				// Build Spanned
	    		String html = "";
	    		if(!author.equalsIgnoreCase(""))
	    			html +="<b>Author:</b> " + author + "<br/>";
	    		if(!location.equalsIgnoreCase(""))
	    			html +="<b>Location:</b> " + location + "<br/>";
	    		Spanned info = Html.fromHtml(html);

				// Build List Item 1
				ItemView ivThumbnail = new ItemView(image, R.id.st_image);
				ItemView ivTitle = new ItemView(title, R.id.st_title);
				ItemView ivInfo = new ItemView(info, R.id.nt_info);
				

				ListItem li1 = new ListItem(NewTitlesBook, R.layout.show_titles_li, true);
				li1.add(ivThumbnail);
				li1.add(ivTitle);
				li1.add(ivInfo);

				// Build List Item 2
				ListItem li2 = new ListItem(null, R.layout.best_sellers_div, false);

				// Add List Items to list
				lis.add(li1);
				lis.add(li2);
			}

		} else {
			// nameArray.clear();
			Toast.makeText(getApplicationContext(), "Network Not Available", Toast.LENGTH_LONG).show();
		}
		


	}

	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) 
	{
		NewTitlesBook book = (NewTitlesBook) ma.getItem(pos);   	
		MinrvaApp.id = book.getBibid();
		new ConvertLocation().execute(book.getLocation());
	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) 
	{
		Intent intent = new Intent(this, HelpActivity.class);
		intent.putExtra("help_uri", getString(R.string.newtitles_help));
		intent.putExtra(GD_ACTION_BAR_TITLE, getIntent().getStringExtra(GD_ACTION_BAR_TITLE));
		intent.putExtra(GD_ACTION_BAR_ICON, getIntent().getIntExtra(GD_ACTION_BAR_ICON, R.drawable.stub));
	    startActivity(intent);
	    return true;	                         
    }
	
	
	
	private class ConvertLocation extends AsyncTask<String, Void, DisplayLocation> 
	{
		@Override
		protected void onPreExecute() 
		{
			
		}

		protected DisplayLocation doInBackground(String... params) 
		{   
			String id = params[0];
			id = id.replaceAll("(\\W)", "%20");
			String uri = getString(R.string.display_converter) + "?id=" + id;
			Log.d("","URI: " + uri);
			DisplayLocation location = HTTP.downloadObject(uri, DisplayLocation.class);
			return location;
		}

		protected void onPostExecute(DisplayLocation location) 
		{
			Intent intent = new Intent(activity, Display.class);
			if(location != null)
			{
				intent.putExtra("setLocLabel", location.getLabel());
				intent.putExtra("setLocCode", location.getCode());
			}
			
			intent.putExtra(GD_ACTION_BAR_TITLE, getIntent().getStringExtra(GD_ACTION_BAR_TITLE));
			intent.putExtra(GD_ACTION_BAR_ICON, getIntent().getIntExtra(GD_ACTION_BAR_ICON, R.drawable.stub));
			startActivity(intent);	
		}
	}

}