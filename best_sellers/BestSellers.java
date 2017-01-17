package edu.illinois.ugl.minrva.best_sellers;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.illinois.ugl.minrva.R;
import edu.illinois.ugl.minrva.best_sellers.models.BestSellersBook;
import edu.illinois.ugl.minrva.best_sellers.models.CategoryModel;
import edu.illinois.ugl.minrva.core.MinrvaApp;
import edu.illinois.ugl.minrva.core.adapter.ItemView;
import edu.illinois.ugl.minrva.core.adapter.ListItem;
import edu.illinois.ugl.minrva.core.adapter.MinrvaAdapter;
import edu.illinois.ugl.minrva.core.connections.HTTP;
import edu.illinois.ugl.minrva.core.display.Display;
import edu.illinois.ugl.minrva.core.help.HelpActivity;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;

/**
 * The best sellers module displays popular books based on a daily, weekly, or
 * monthly updated list.
 * 
 * @version 2.1.1
 * @author Collin Walther cbwalther1@gmail.com
 */
public class BestSellers extends GDActivity implements OnItemClickListener,
		OnItemSelectedListener, OnCheckedChangeListener {
	ProgressDialog progressDialog;
	private boolean itemClickRun = true;

	// Radio
	private RadioGroup rgStyle;
	private boolean all = true;
	private boolean weekly = false;
	private boolean monthly = false;

	// Activity
	private Activity activity;

	// List
	private ListView lv;
	private MinrvaAdapter ma;
	private ArrayList<ListItem> lis; // Books
	private ArrayList<String> nameArray = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private ArrayList<String> list; // Categories
	private String header = "ALL CATEGORIES";
	int counter = 0;

	// TextView
	private TextView noBooks;

	// Spinner
	private Spinner sItems;

	/**
	 * Adds title bar and creates list view + data adapter
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Load
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.best_sellers_main);
		addActionBarItem(Type.Help);

		// Get reference
		activity = this;

		// Radio
		rgStyle = (RadioGroup) findViewById(R.id.bs_radio);
		rgStyle.setOnCheckedChangeListener(this);

		// Initialize Spinner
		list = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sItems = (Spinner) findViewById(R.id.bs_category);
		sItems.setAdapter(adapter);
		sItems.setOnItemSelectedListener(this);
		// Download search drop down items

		// List
		lis = new ArrayList<ListItem>();
		ma = new MinrvaAdapter(this, lis);
		lv = (ListView) findViewById(R.id.best_sellers_list);
		lv.setAdapter(ma);
		lv.setOnItemClickListener(this);
		lv.setFastScrollEnabled(true);

		noBooks = (TextView) findViewById(R.id.noBS);

		new DownloadSearchList().execute();
		// Download items
	}

	private class DownloadSearchList extends
			AsyncTask<Void, Void, CategoryModel> {

		@Override
		protected void onPreExecute() {
			if (HTTP.isNetworkAvailable()) {
				progressDialog = ProgressDialog
						.show(activity, "", "Loading...");
			} else {
				Log.d("", "NETWORK NOT AVAILABLE");
				HTTP.alertFinish(activity);
			}
		}

		protected CategoryModel doInBackground(Void... params) {
			String cat_uri;
			// Download data

			if (rgStyle.getCheckedRadioButtonId() == R.id.week)
				cat_uri = getString(R.string.bs_categories_weekly);
			else if (rgStyle.getCheckedRadioButtonId() == R.id.month)
				cat_uri = getString(R.string.bs_categories_monthly);
			else
				cat_uri = getString(R.string.bs_categories_all);
			// Log.d("","cat_uri= " + cat_uri);
			CategoryModel categories = HTTP.downloadObject(cat_uri,
					CategoryModel.class);
			// ArrayList<String> category = categories.getGroups();
			// Log.d("", "category size = "+category.size());

			return categories;
		}

		protected void onPostExecute(CategoryModel categories) {
			
			if (HTTP.isNetworkAvailable() && categories != null) 
			{
				ArrayList<String> category = categories.getGroups();
				if (category.size() == 0) {
					lv.setVisibility(View.GONE);
					noBooks.setVisibility(View.VISIBLE);
				} else {
					lv.setVisibility(View.VISIBLE);
					noBooks.setVisibility(View.GONE);
					bindSpinnerInfo(category);
					itemClickRun = false;
					sItems.setSelection(0);

				}

			}

			if (progressDialog != null) {
				// progressDialog.dismiss();
			}

			new DownloadBooks().execute();
		}

		private void bindSpinnerInfo(ArrayList<String> category) {
			list.clear();

			for (int i = 0; i < category.size(); i++) {
				String newName = category.get(i);
				list.add(newName);
			}

			adapter.notifyDataSetChanged();

		}
	}

	/**
	 * Download.
	 */
	private class DownloadBooks extends
			AsyncTask<Void, Void, BestSellersBook[]> {

		@Override
		protected void onPreExecute() {
			if (HTTP.isNetworkAvailable()) {
				// progressDialog = ProgressDialog.show(activity, "",
				// "Loading...");
				// Clear current list before downloading more stuff
				lis.clear();
				ma.notifyDataSetChanged();
			} else {
				HTTP.alertFinish(activity);
			}
		}

		protected BestSellersBook[] doInBackground(Void... params) {
			itemClickRun = true;
			Log.d("header1", "header = " + header);
			String bs_uri;
			// Download data

			if (header.compareTo("ALL CATEGORIES") == 0
					|| header.compareTo("ALL%20CATEGORIES") == 0)
				bs_uri = getString(R.string.bestsellers_all);
			else if (header.compareTo("ALL WEEKLY CATEGORIES") == 0
					|| header.compareTo("ALL%20WEEKLY%20CATEGORIES") == 0)
				bs_uri = getString(R.string.bestsellers_weekly);
			else if (header.compareTo("ALL MONTHLY CATEGORIES") == 0
					|| header.compareTo("ALL%20MONTHLY%20CATEGORIES") == 0)
				bs_uri = getString(R.string.bestsellers_monthly);
			else {
				header = Uri.encode(header);
				bs_uri = getString(R.string.bestsellers_list) + "group="
						+ header;
			}

			Log.d("header", "bs_uri = " + bs_uri);
			Log.d("header", "header = " + header);

			BestSellersBook[] bsBooks = HTTP.downloadObjects(bs_uri,
					BestSellersBook[].class);
			return bsBooks;
		}

		protected void onPostExecute(BestSellersBook[] bsBooks) {
			if (HTTP.isNetworkAvailable() && bsBooks != null) {
				if (bsBooks.length == 0)
					lv.setVisibility(View.GONE);
				else
					lv.setVisibility(View.VISIBLE);

			}

			bindData(bsBooks);
			ma.notifyDataSetChanged();
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}

	/**
	 * Add data to listview.
	 * 
	 * @param bsBooks
	 * @return
	 */
	public void bindData(BestSellersBook[] bsBooks) {
		if (HTTP.isNetworkAvailable() && bsBooks != null) {

			// Log.d("books", "Book length = "+bsBooks.length);

			for (int i = 0; i < bsBooks.length; i++) {
				// Get book
				BestSellersBook BestSellersBook = bsBooks[i];

				// Get data from book
				String title = bsBooks[i].getTitle();
				// Log.d("", "book["+i+"] = "+title);
				String thumbnail = bsBooks[i].getThumbnail();
				String author = bsBooks[i].getAuthor();
				String pubYear = bsBooks[i].getPubYear();
				String location = bsBooks[i].getLocation();
				String format = bsBooks[i].getFormat();

				// Build Spanned
				String html = "";
				if (!location.equalsIgnoreCase(""))
					html += "<b>Location:</b> " + location + "<br/>";
				if (!author.equalsIgnoreCase(""))
					html += "<b>Author:</b> " + author + "<br/>";
				if (!pubYear.equalsIgnoreCase(""))
					html += "<b>Pub. Date:</b> " + pubYear + "<br/>";
				if (!format.equalsIgnoreCase(""))
					html += "<b>Format:</b> " + format + "<br/>";
				Spanned info = Html.fromHtml(html);

				// Build List Item 1
				ItemView ivThumbnail = new ItemView(thumbnail,
						R.id.bs_thumbnail);
				ItemView ivTitle = new ItemView(title, R.id.bs_title);
				ItemView ivInfo = new ItemView(info, R.id.bs_info);

				ListItem li1 = new ListItem(BestSellersBook,
						R.layout.best_sellers_li, true);
				li1.add(ivThumbnail);
				li1.add(ivTitle);
				li1.add(ivInfo);

				// Build List Item 2
				ListItem li2 = new ListItem(null, R.layout.best_sellers_div,
						false);

				// Add List Items to list
				lis.add(li1);
				lis.add(li2);
			}

		} else {
			nameArray.clear();
			Toast.makeText(getApplicationContext(), "Network Not Available",
					Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Open up item in new tab.
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BestSellersBook book = (BestSellersBook) ma.getItem(position);
		MinrvaApp.id = "uiu_".concat(book.getBibId());

		// -------------------------------------------------------------------------------------------------------

		Intent intent = new Intent(this, Display.class);
		intent.putExtra("setLocLabel", "University of Illinois at Urbana-Champaign");
		intent.putExtra("setLocCode", "uiu");
		intent.putExtra(GD_ACTION_BAR_TITLE,
				getIntent().getStringExtra(GD_ACTION_BAR_TITLE));
		intent.putExtra(GD_ACTION_BAR_ICON,
				getIntent().getIntExtra(GD_ACTION_BAR_ICON, R.drawable.stub));
		startActivity(intent);

	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d("position", "position = " + position);
		if (position == 0)
			itemClickRun = true;
		if (itemClickRun) {
			// new DownloadSearchList().execute();
			header = list.get(position);
			if (counter != 0)
				new DownloadBooks().execute();

			counter++;
		} else
			itemClickRun = true;
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d("", "ITEM SELECTED");
		all = rgStyle.getCheckedRadioButtonId() == R.id.all;
		weekly = rgStyle.getCheckedRadioButtonId() == R.id.week;
		monthly = rgStyle.getCheckedRadioButtonId() == R.id.month;
		new DownloadSearchList().execute();
		if (all)
			header = "ALL CATEGORIES";
		if (weekly)
			header = "ALL WEEKLY CATEGORIES";
		if (monthly)
			header = "ALL MONTHLY CATEGORIES";

	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) 
	{
		Intent intent = new Intent(this, HelpActivity.class);
		intent.putExtra("help_uri", getString(R.string.bestsellers_help));
		intent.putExtra(GD_ACTION_BAR_TITLE, getIntent().getStringExtra(GD_ACTION_BAR_TITLE));
		intent.putExtra(GD_ACTION_BAR_ICON, getIntent().getIntExtra(GD_ACTION_BAR_ICON, R.drawable.stub));
	    startActivity(intent);
	    return true;	                         
    }

}
