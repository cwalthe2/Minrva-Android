package edu.illinois.ugl.minrva.new_titles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import edu.illinois.ugl.minrva.R;
import edu.illinois.ugl.minrva.core.adapter.ItemView;
import edu.illinois.ugl.minrva.core.adapter.ListItem;
import edu.illinois.ugl.minrva.core.help.HelpActivity;
import edu.illinois.ugl.minrva.new_titles.adapter.LibsAdapter;
import edu.illinois.ugl.minrva.new_titles.models.Lib;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;

public class NewTitles extends GDActivity implements OnItemClickListener,
		OnItemSelectedListener {
	private Spinner chooseDate;
	private ListView libList;
	private ArrayList<ListItem> lis;
	private ArrayAdapter<String> adapter;
	public static LibsAdapter ma;
	private ArrayList<String> dates;
	public static ArrayList<Lib> libs;
	private String url = "";
	private String dRange = "Today";
	public static int numChecked = 0;

	public void onCreate(Bundle savedInstanceState) {
		// Load
		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.new_titles_main);
		addActionBarItem(Type.Help);

		// List of libs
		libs = new ArrayList<Lib>();
		getCheckList();
		lis = new ArrayList<ListItem>();
		ma = new LibsAdapter(this, lis);
		libList = (ListView) findViewById(R.id.libsList);
		libList.setAdapter(ma);
		libList.setOnItemClickListener(this);
		bindData(libs);
		ma.notifyDataSetChanged();

		// Spinner of dates
		chooseDate = (Spinner) findViewById(R.id.pastxdays);
		dates = new ArrayList<String>();
		dates.add("Today");
		dates.add("Past 7 Days");
		dates.add("Past 14 Days");
		dates.add("Past 30 Days");
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, dates);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		chooseDate.setOnItemSelectedListener(this);
		chooseDate.setAdapter(adapter);

	}

	/**
	 * Hard-coded list of Lib's. Adds these items to global variable libs
	 */
	private void getCheckList() {

		libs.add(new Lib("Government Documents", false, 33));
		libs.add(new Lib("Maps & Geography", false, 44));
		libs.add(new Lib("Main Stacks", false, 56));
		libs.add(new Lib("Rare Book & Manuscript", false, 53));
		libs.add(new Lib("Reference", false, 54));
		libs.add(new Lib("Residence Halls", false, 65));
		libs.add(new Lib("Undergraduate", false, 58));
		libs.add(new Lib("University High School", false, 59));
		libs.add(new Lib("Agriculture (Funk ACES)", false, 22));
		libs.add(new Lib("Biology", false, 26));
		libs.add(new Lib("Chemistry", false, 28));
		libs.add(new Lib("Engineering (Grainger)", false, 36));
		libs.add(new Lib("Geology", false, 37));
		libs.add(new Lib("Mathematics", false, 46));
		libs.add(new Lib("Prairie Research Institute", false, 49));
		libs.add(new Lib("Veterinary Medicine", false, 60));
		libs.add(new Lib("International and Area Studies Library", false, 76));
		libs.add(new Lib("Architecture & Art (Ricker)", false, 19));
		libs.add(new Lib("Center for Children's Books", false, 27));
		libs.add(new Lib("Classics", false, 29));
		libs.add(new Lib("English", false, 35));
		libs.add(new Lib("History, Philosophy, & Newspaper", false, 38));
		libs.add(new Lib("Illinois Historical Survey", false, 39));
		libs.add(new Lib("Literature and Languages Library", false, 74));
		libs.add(new Lib("Modern Languages & Linguistics", false, 45));
		libs.add(new Lib("Music", false, 47));
		libs.add(new Lib("Business & Economics", false, 30));
		libs.add(new Lib("Communications", false, 31));
		libs.add(new Lib("Social Sciences, Health, and Education Library",
				false, 75));
		libs.add(new Lib("Law", false, 42));

		// Sort Alphabetically
		Collections.sort(libs, new Comparator<Lib>() {
			public int compare(final Lib object1, final Lib object2) {
				return object1.getName().compareTo(object2.getName());
			}
		});
		libs.add(0, new Lib("All Libraries", false, -1));
	}

	// Build listview
	public void bindData(ArrayList<Lib> libraries) {
		if (libraries != null) {

			for (int i = 0; i < libraries.size(); i++) {
				// Get lib
				Lib library = libraries.get(i);

				// Get data from lib
				String title = library.getName();
				boolean cb = library.isChecked();
				Log.d("", "name " + i + " = " + title + " - " + cb);

				// Build List Item 1 - Checkbox and lib name
				ItemView name = new ItemView(title, R.id.libName);
				ItemView check = new ItemView(cb, R.id.checkBox1);
				ListItem li1 = new ListItem(library, R.layout.new_titles_li,
						true);
				li1.add(name);
				li1.add(check);

				// Build List Item 2 - Divider
				ListItem li2 = new ListItem(null, R.layout.best_sellers_div,
						false);

				// Add List Items to list
				lis.add(li1);

				if ((i + 1) != libraries.size())
					lis.add(li2);
			}

		}

	}

	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		pos /= 2;
		CheckBox c = (CheckBox) v.findViewById(R.id.checkBox1);
		c.setChecked(!c.isChecked());
		numChecked = c.isChecked() ? numChecked + 1 : numChecked - 1;

		if (pos == 0) {
			Log.d("", "Change all libraries");

			if (c.isChecked()) {
				for (int i = 0; i < libs.size(); i++) {
					libs.get(i).setChecked(true);
				}
				numChecked = libs.size();

			} else {
				for (int i = 0; i < libs.size(); i++) {
					libs.get(i).setChecked(false);
				}
				numChecked = 0;
			}
		} else {

			libs.get(pos).setChecked(c.isChecked());
		}

		Log.d("", "numChecked = " + numChecked);
		Log.d("", "libs size = " + libs.size());

		if (numChecked == libs.size() - 1) {
			if (libs.get(0).isChecked()) {
				libs.get(0).setChecked(false);
				Log.d("", "setting first to false");
				numChecked--;
			} else {
				libs.get(0).setChecked(true);
				Log.d("", "setting first to true");
				numChecked++;

			}
		}

		ma.notifyDataSetChanged();

	}

	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

		switch (pos) {
		case 0:
			dRange = "today";
			break;
		case 1:
			dRange = "week";
			break;
		case 2:
			dRange = "2weeks";
			break;
		case 3:
			dRange = "month";
			break;
		}
	}

	public void clearLibs(View v) {
		for (int i = 0; i < libs.size(); i++) {
			libs.get(i).setChecked(false);

		}
		ma.notifyDataSetChanged();

	}

	public void submitLibs(View v) {
		url = getString(R.string.new_titles)
				+ "?nomissingimgs=false&classic=false&maxfeed=100&removevolumes=false&filter=units=";

		boolean noneSelected = true;

		for (int i = 1; i < libs.size(); i++) {
			if (noneSelected) {
				if (libs.get(i).isChecked()) {
					url += libs.get(i).getId();
					noneSelected = false;
				}
			} else if (libs.get(i).isChecked())
				url += "," + libs.get(i).getId();
		}

		url += "~sort=title";
		url += "~dateFixed=" + dRange;

		Log.d(null, url);

		if (!noneSelected) {
			Intent intent = new Intent(this, ShowTitles.class);
			intent.putExtra(GD_ACTION_BAR_TITLE,
					getIntent().getStringExtra(GD_ACTION_BAR_TITLE));
			intent.putExtra(GD_ACTION_BAR_ICON,
					getIntent()
							.getIntExtra(GD_ACTION_BAR_ICON, R.drawable.stub));
			intent.putExtra("url", url);
			startActivity(intent);
		} else 
		{
			Toast.makeText(getApplicationContext(), "Please select a library.", Toast.LENGTH_LONG).show();
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

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
}
