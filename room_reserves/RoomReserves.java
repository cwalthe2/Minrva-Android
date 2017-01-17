package edu.illinois.ugl.minrva.room_reserves;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import edu.illinois.ugl.minrva.R;
import edu.illinois.ugl.minrva.core.adapter.ItemView;
import edu.illinois.ugl.minrva.core.adapter.ListItem;
import edu.illinois.ugl.minrva.core.adapter.MinrvaAdapter;
import edu.illinois.ugl.minrva.core.connections.HTTP;
import edu.illinois.ugl.minrva.core.help.HelpActivity;
import edu.illinois.ugl.minrva.room_reserves.adapter.BuildingSpinnerAdapter;
import edu.illinois.ugl.minrva.room_reserves.models.Buildings;
import edu.illinois.ugl.minrva.room_reserves.models.Rooms;
import edu.illinois.ugl.minrva.room_reserves.models.Times;
import edu.illinois.ugl.minrva.room_reserves.WebViewer;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;

/**
 * The room reserves module allows the user to choose a location, room number,
 * date, and time to reserve a study room. A custom calendar is used since we
 * need specific date ranges. Implementing a DatePicker with certain ranges
 * exceeds our desired API level
 * 
 * @version 2.1.1
 * @author Collin Walther cbwalther1@gmail.com
 */

public class RoomReserves extends GDActivity implements OnItemSelectedListener,
		OnItemClickListener, OnClickListener {
	ProgressDialog progressDialog;
	private Dialog dialog;
	private int MAXBUTTONS;
	private String[] strings;
	private int[] buttonID;
	private Button buttons[];

	private Activity activity;

	private String weekDay;
	private SimpleDateFormat dayFormat;
	private SimpleDateFormat dateFormat;
	private String[] dates;
	private TextView days[];
	private int[] dayID;
	private String month;
	private int year;
	private int chosenDay;
	private int numDays;
	private boolean isLeapYear;
	private boolean isOneMonth;
	private String[] daysOfWeek = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri",
			"Sat" };
	private String[] monthsOfYear = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	private Calendar calendar;
	private String chosenDate; // ex. November 2, 2015
	private String selectedDate; // ex. 11-2-2015
	private String selectedTime;
	private Spinner d_spp;
	private MyAdapter sa;

	// Building
	private ArrayList<ListItem> b_lis;
	private Spinner b_spp;
	private BuildingSpinnerAdapter b_ba;
	int current_building_ID;

	// Room
	private ArrayList<ListItem> r_lis;
	private Spinner r_spp;
	private BuildingSpinnerAdapter r_ba;
	int current_room_ID;
	String current_room_name;

	// Time
	private ListView t_lv;
	private MinrvaAdapter t_ma;
	private ArrayList<ListItem> t_lis;

	// View row1 = null;
	// View row2 = null;

	private int time_flag = 0;

	private String firstSelected = "";
	private String lastSelected = "";
	private double num_selected = 0;
	private int last_pos;
	private int first_pos;

	private TextView rrMessage;
	private View goldLine;

	protected static String d_url;

	private LinearLayout.LayoutParams d_spp_params;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setActionBarContentView(R.layout.roomreserves_main);
		addActionBarItem(Type.Help);

		// 14 buttons = 2 weeks
		MAXBUTTONS = 14;

		// Calendar Buttons
		buttons = new Button[MAXBUTTONS];
		buttonID = new int[MAXBUTTONS];

		// Days of week
		days = new TextView[7];
		dayID = new int[7];

		// Dates
		dates = new String[MAXBUTTONS];

		// Get current day of week (Sun, Mon, Tue, etc.)
		dayFormat = new SimpleDateFormat("EEE", Locale.US);
		calendar = Calendar.getInstance();
		weekDay = dayFormat.format(calendar.getTime());
		Log.d("day of week", weekDay);

		// Get current day of month (1, 2, 3, etc.)
		dateFormat = new SimpleDateFormat("d", Locale.US);
		dates[0] = dateFormat.format(calendar.getTime());
		chosenDay = Integer.parseInt(dates[0]);

		// Get current month (Jan, Feb, Mar, etc.)
		dateFormat = new SimpleDateFormat("LLL", Locale.US);
		month = dateFormat.format(calendar.getTime());
		Log.d("Month", month);

		// Get current year (needed for leap years)
		dateFormat = new SimpleDateFormat("yyyy", Locale.US);
		year = Integer.parseInt(dateFormat.format(calendar.getTime()));
		isLeapYear = (year % 4 == 0) ? true : false;

		// Get current date (mm-dd-yyyy)
		dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
		selectedDate = dateFormat.format(calendar.getTime());

		isOneMonth = true;
		numDays = getNumDaysOfMonth();
		initDates(dates);

		dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
		strings = new String[2];
		strings[0] = "";
		strings[1] = chosenDate = dateFormat.format(calendar.getTime());

		activity = this;

		// Date Spinner
		d_spp = (Spinner) findViewById(R.id.rr_date_spinner);
		d_spp_params = (LinearLayout.LayoutParams) d_spp.getLayoutParams();
		d_spp_params.setMargins(5, 5, 5, 85);
		d_spp.setLayoutParams(d_spp_params);
		String header = getCalHeader();
		Log.d("HEADER", header);
		d_spp.setPrompt(header);

		sa = new MyAdapter(RoomReserves.this, R.layout.roomreserves_days,
				strings);
		d_spp.setAdapter(sa);
		// OnItemSelecter doesn't work appropriately for Date Spinner. Only
		// calls on user's first selection, nothing more.
		// See MyAdapater -> getView for the workaround. Uses global variable -
		// time_flag.

		// Building Spinner
		b_lis = new ArrayList<ListItem>();
		b_ba = new BuildingSpinnerAdapter(this, b_lis);
		b_spp = (Spinner) findViewById(R.id.rr_building_spinner);
		b_spp.setAdapter(b_ba);
		b_spp.setOnItemSelectedListener(this);

		// Room Spinner
		r_lis = new ArrayList<ListItem>();
		r_ba = new BuildingSpinnerAdapter(this, r_lis);
		r_spp = (Spinner) findViewById(R.id.rr_room_spinner);
		r_spp.setAdapter(r_ba);
		r_spp.setOnItemSelectedListener(this);

		// List
		t_lis = new ArrayList<ListItem>();
		t_ma = new MinrvaAdapter(this, t_lis);
		// t_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		t_lv = (ListView) findViewById(R.id.rr_times);
		t_lv.setAdapter(t_ma);
		t_lv.setOnItemClickListener(this);

		rrMessage = (TextView) findViewById(R.id.rr_messageBox);
		goldLine = (View) findViewById(R.id.rr_div3);

		//Set up Dialog
        dialog = new Dialog(activity, R.style.CustomDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
  		dialog.setContentView(R.layout.core_dialog);
  		dialog.setCancelable(true);

    	// Add listeners to buttons
    	Button btnYes = (Button) dialog.findViewById(R.id.yes_button);
    	btnYes.setOnClickListener(this);
    	Button btnNo = (Button) dialog.findViewById(R.id.no_button);
    	btnNo.setOnClickListener(this);
    	
		// Let's begin
		new DownloadBuildings().execute();

	}

	private class DownloadBuildings extends AsyncTask<Void, Void, Buildings[]> {

		@Override
		protected void onPreExecute() {
			if (HTTP.isNetworkAvailable()) {
				progressDialog = ProgressDialog.show(activity, "",
						"Retrieving available buildings...");
				// Clear current list before downloading more stuff
				b_lis.clear();
				b_ba.notifyDataSetChanged();

			} else {
				HTTP.alertFinish(activity);
			}
		}

		protected Buildings[] doInBackground(Void... params) {
			String bs_uri;
			// Download data

			bs_uri = getString(R.string.roomreserves_buildings);

			Log.d("header", "bs_uri = " + bs_uri);

			Buildings[] bItems = HTTP
					.downloadObjects(bs_uri, Buildings[].class);
			return bItems;
		}

		protected void onPostExecute(Buildings[] rItems) {
			if (HTTP.isNetworkAvailable() && rItems != null) {
				if (rItems.length == 0)
					b_spp.setVisibility(View.GONE);
				else
					b_spp.setVisibility(View.VISIBLE);

			}

			bindBuildingData(rItems);
			b_ba.notifyDataSetChanged();
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}

	/**
	 * Add data to listview.
	 * 
	 * @param rItems
	 * @return
	 */

	public void bindBuildingData(Buildings[] bItems) {
		if (HTTP.isNetworkAvailable() && bItems != null) {

			for (int i = 0; i < bItems.length; i++) {
				// Get room
				Buildings room = bItems[i];

				// Get data from room
				String title = bItems[i].getName();
				if (title.compareTo("Branch 0") != 0) {
					String thumbnail = bItems[i].getPic();
					String desc = bItems[i].getDescription();
					Log.d("", "building[" + i + "] = " + title);

					// Build List Item 1
					ItemView ivTitle;
					ItemView ivThumbnail;
					ItemView ivInfo;
					ListItem li1;

					li1 = new ListItem(room,
							R.layout.roomreserves_spinner_open, true);
					ivTitle = new ItemView(title, R.id.b_title);
					ivThumbnail = new ItemView(thumbnail, R.id.b_thumbnail);
					ivInfo = new ItemView(desc, R.id.b_description);
					li1.add(ivTitle);
					li1.add(ivThumbnail);
					li1.add(ivInfo);

					// Build List Item 2
					ListItem li2 = new ListItem(null,
							R.layout.roomreserves_div, false);

					// Add List Items to list
					b_lis.add(li1);
					b_lis.add(li2);
				}
			}
		} else { // nameArray.clear();
			Toast.makeText(getApplicationContext(), "Network Not Available",
					Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Download rooms
	 */

	private class DownloadRooms extends AsyncTask<Void, Void, Rooms[]> {

		@Override
		protected void onPreExecute() {
			if (HTTP.isNetworkAvailable()) {
				progressDialog = ProgressDialog.show(activity, "",
						"Retrieving available rooms...");
				// Clear current list before downloading more stuff
				r_lis.clear();
				r_ba.notifyDataSetChanged();

			} else {
				HTTP.alertFinish(activity);
			}
		}

		protected Rooms[] doInBackground(Void... params) {
			;
			String r_uri;

			// Download data
			r_uri = getString(R.string.roomreserves_rooms);

			Rooms[] rItems = HTTP.downloadObjects(r_uri, Rooms[].class);
			return rItems;
		}

		protected void onPostExecute(Rooms[] rItems) {
			if (HTTP.isNetworkAvailable() && rItems != null) {
				if (rItems.length == 0)
					r_spp.setVisibility(View.GONE);
				else
					r_spp.setVisibility(View.VISIBLE);

			}

			bindRoomData(rItems);
			r_ba.notifyDataSetChanged();
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}

	/**
	 * Add data to listview.
	 * 
	 * @param rItems
	 * @return
	 */

	public void bindRoomData(Rooms[] rItems) {
		if (HTTP.isNetworkAvailable() && rItems != null) {

			// Get starting point of list
			int start, end;
			for (start = 0; start < rItems.length; start++) {
				if (rItems[start].getBuildingID() == current_building_ID)
					break;
			}

			// Traverse until the building ID changes or until end of list
			for (end = start; end < rItems.length; end++) {
				if (rItems[end].getBuildingID() != current_building_ID)
					break;
			}
			Log.d("START", "" + start);
			Log.d("END", "" + end);

			for (int i = start; i < end; i++) {
				// Get room
				Rooms room = rItems[i];

				// Get data from room
				String title = rItems[i].getName();
				String thumbnail = rItems[i].getPicurl();
				String desc = rItems[i].getDescription();

				// Build List Item 1
				ItemView ivTitle;
				ItemView ivThumbnail;
				ItemView ivInfo;
				ListItem li1;

				li1 = new ListItem(room, R.layout.roomreserves_spinner_open,
						true);
				ivTitle = new ItemView(title, R.id.b_title);
				ivThumbnail = new ItemView(thumbnail, R.id.b_thumbnail);
				ivInfo = new ItemView(desc, R.id.b_description);
				li1.add(ivTitle);
				li1.add(ivThumbnail);
				li1.add(ivInfo);

				// Build List Item 2
				ListItem li2 = new ListItem(null, R.layout.roomreserves_div,
						false);

				// Add List Items to list
				r_lis.add(li1);
				r_lis.add(li2);

			}
		} else { // nameArray.clear();
			Toast.makeText(getApplicationContext(), "Network Not Available",
					Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Download available times
	 */

	private class DownloadTimes extends AsyncTask<Void, Void, Times[]> {
		String t_uri;
		private String time_block_size = "0.5"; // 30 min increments

		@Override
		protected void onPreExecute() {
			if (d_spp_params.bottomMargin == 85) {
				d_spp_params.setMargins(5, 5, 5, 5);
				d_spp.setLayoutParams(d_spp_params);
			}

			if (HTTP.isNetworkAvailable()) {
				progressDialog = ProgressDialog
						.show(activity, "",
								"Retrieving available times...");
				// Clear current list before downloading more stuff
				t_lis.clear();
				t_ma.notifyDataSetChanged();

				t_uri = getString(R.string.roomreserves_times);
				t_uri += "?hours=" + time_block_size;
				int index = 1 + indexOfString(month, monthsOfYear);
				t_uri += "&date=" + index + "-" + chosenDay + "-" + year;
				t_uri += "&roomid=" + current_room_ID;
				t_uri += "&buildid=" + ""; // parameter allowed to be empty,
											// really serves no purpose
				Log.d("T_URI", t_uri);
			} else {
				HTTP.alertFinish(activity);
			}
		}

		protected Times[] doInBackground(Void... params) {

			// Download data
			Times[] startTimes = HTTP.downloadObjects(t_uri, Times[].class);
			return startTimes;

		}

		protected void onPostExecute(Times[] startTimes) {

			ArrayList<String> times = null;
					
			if(startTimes != null && startTimes.length > 0)
			{
				times = startTimes[0].getStartTimes();
			}

			// Log.d("", "category size = "+category.size());
			if (HTTP.isNetworkAvailable() && times != null && times.size() > 0) 
			{
				t_lv.setVisibility(View.VISIBLE);
				bindTimeData(times);
				t_ma.notifyDataSetChanged();

				ListView list = (ListView) findViewById(R.id.rr_times);
				setHeightBasedOnChildren(list);

			} else if (times != null && times.size() == 0)
			{
				rrMessage.setVisibility(View.GONE);
				goldLine.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "No Available Times",
						Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "A network error occured",
						Toast.LENGTH_LONG).show();
			}

			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}

	/**
	 * Add data to listview.
	 * 
	 * @param rItems
	 * @return
	 */

	public void bindTimeData(ArrayList<String> startTimes) {
		
		if (HTTP.isNetworkAvailable() && startTimes != null) {
			int size = 0;
			int reserved_last = 1;
			int current_time = unparseTime("12:00 AM");
			getCurrentTime();

			// For Testing ------
			// c_time = unparseTime("5:39 PM");
			// ------------------

			int last_time = current_time;
			Log.d("C_TIME", "" + current_time);
			Log.d("START_TIME_SIZE", "" + startTimes.size());
			for (int i = 0; i < startTimes.size(); i++) {
				String newTime = startTimes.get(i);
				Log.d("", "time = " + newTime);
				int newTime_int = Integer.parseInt(newTime);

				if (newTime_int >= current_time) {
					if (newTime_int > last_time + 1) {
						if (reserved_last == 0) {
							ItemView time = new ItemView("RESERVED",
									R.id.time_reserved);
							ListItem li1 = new ListItem(null,
									R.layout.roomreserves_time_reserved, false);
							ListItem li2 = new ListItem(null,
									R.layout.roomreserves_div, false);
							li1.add(time);
							t_lis.add(li1);
							t_lis.add(li2);
							reserved_last = 1;
						}
						newTime = parseTime(newTime);
						ItemView time = new ItemView(newTime,
								R.id.time_available);
						ListItem li1 = new ListItem(null,
								R.layout.roomreserves_time_available, true);
						ListItem li2 = new ListItem(null,
								R.layout.roomreserves_div, false);
						li1.add(time);
						t_lis.add(li1);
						if (i + 1 != startTimes.size())
							t_lis.add(li2);
						reserved_last = 0;
						size++;
					} else {
						newTime = parseTime(newTime);
						ItemView time = new ItemView(newTime,
								R.id.time_available);
						ListItem li1 = new ListItem(null,
								R.layout.roomreserves_time_available, true);
						ListItem li2 = new ListItem(null,
								R.layout.roomreserves_div, false);
						li1.add(time);
						t_lis.add(li1);
						if (i + 1 != startTimes.size())
							t_lis.add(li2);
						reserved_last = 0;
						size++;
					}
				} else {
					// Skip
				}
				last_time = newTime_int;
			}
			Log.d("SIZE", " = " + size);
			if (size == 0)
			{
				rrMessage.setVisibility(View.GONE);
				goldLine.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "No Available Times",
						Toast.LENGTH_LONG).show();
			}
			else
			{
				rrMessage.setVisibility(View.VISIBLE);
				goldLine.setVisibility(View.VISIBLE);
			}
		} else { // nameArray.clear();
			Toast.makeText(getApplicationContext(), "Network Not Available",
					Toast.LENGTH_LONG).show();
		}

	}

	// Custom adapter for date spinner (Displays a 2-week Calendar)
	public class MyAdapter extends ArrayAdapter<String> {

		private Context context;

		public MyAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			this.context = context;

		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			
			Log.d("CALLING", "getDropDownView " + position);
			// strings[1] = chosenDate;
			LayoutInflater inflater = LayoutInflater.from(context);
			View row1 = inflater.inflate(R.layout.roomreserves_days, parent,
					false);

			if (position == 0) {

				initDayIDs(dayID);
				initButtonIDs(buttonID);

				for (int i = 0; i < MAXBUTTONS; i++) {
					buttons[i] = (Button) row1.findViewById(buttonID[i]);
				}

				for (int i = 0; i < 7; i++) {
					days[i] = (TextView) row1.findViewById(dayID[i]);
				}

				int dayIndex = indexOfString(weekDay, daysOfWeek);
				for (int i = 0; i < 7; i++) {
					if (dayIndex == 7)
						dayIndex = 0;
					days[i].setText(daysOfWeek[dayIndex]);
					dayIndex++;
				}

				for (int i = 0; i < MAXBUTTONS; i++) {
					buttons[i].setText(dates[i]);

				}

			} else {
				View row2 = inflater.inflate(R.layout.roomreserves_done,
						parent, false);
				// label = (TextView) row2.findViewById(R.id.spinner_done);
				// label.setText("Finished");
				return row2;
			}
			return row1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d("CALLING", "getView " + position);
			LayoutInflater inflater = LayoutInflater.from(context);
			View row2 = inflater.inflate(R.layout.roomreserves_done, parent,
					false);
			
			TextView label = (TextView) row2.findViewById(R.id.spinner_done);
			
			
			label.setBackgroundColor(Color.TRANSPARENT);
			
			label.setText(chosenDate);
			label.setTextColor(getResources().getColor(R.color.med_red));
			label.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.medium_font));
			label.setTypeface(Typeface.DEFAULT_BOLD);
			
			
			
			sa.notifyDataSetChanged();

			// Don't mess with this
			if (position == 1) {
				if (time_flag != 0) {
					time_flag = 0;
				} else {
					time_flag = 1;
					num_selected = 0;
					firstSelected = "";

					if (t_lv != null) {
						t_lis.clear();
						t_ma.notifyDataSetChanged();
						t_lv.setVisibility(View.GONE);
						rrMessage.setVisibility(View.GONE);
						goldLine.setVisibility(View.GONE);

					}
				}
			}

			return row2;
		}

	}

	public void update(View v) {
		RelativeLayout rl = (RelativeLayout) v.getParent();
		Button btn = (Button) v;

		String chosen_day = (String) btn.getText();
		chosenDay = Integer.parseInt(chosen_day);
		// If one month is displayed, then chosenDate is just (currentMonth
		// chosenDay, currentYear)
		if (isOneMonth) {
			chosenDate = month + " " + chosen_day + ", " + year;
		}
		// If two months are displayed...
		else {
			// If (int)chosenDate is >= MAXBUTTONS, then we use currentMonth and
			// currentYear
			if (Integer.parseInt(chosen_day) >= MAXBUTTONS) {
				chosenDate = month + " " + chosen_day + ", " + year;
			}
			// If (int)chosenDate is < MAXBUTTONS, then we use currentMonth + 1
			// because we are in the next month
			else {
				int index = indexOfString(month, monthsOfYear);
				// If currentMonth + 1 is Janurary, then we use currentYear + 1.
				if (index == 11) {
					month = "Jan";
					year++;
					chosenDate = month + " " + chosen_day + ", " + year;
				} else {
					month = monthsOfYear[index + 1];
					chosenDate = month + " " + chosen_day + ", " + year;
				}
			}
		}
		Log.d("CHOSEN DATE", chosenDate);
		selectedDate = chosenDate;
		int m = indexOfString(month, monthsOfYear) + 1;
		selectedDate = "" + m + "-" + chosen_day + "-" + year;
		Log.d("SELECTED DATE", selectedDate);

		int idNumber = btn.getId();
		for (int i = 0; i < MAXBUTTONS; i++) {
			// Log.d("", "Button ID: " + buttonID[i]);

			if (buttonID[i] == idNumber) {
				Button clickedBtn = (Button) rl.findViewById(buttonID[i]);
				clickedBtn.setBackgroundResource(R.color.goldenrod_10);
			} else {
				Button clickedBtn = (Button) rl.findViewById(buttonID[i]);
				clickedBtn.setBackgroundResource(R.color.goldenrod_1);
			}
		}
	}

	/**
	 * Parses an string with value (int) [0-47] and returns the corresponding
	 * time of day. e.g. 0 would return 12:00 AM - 12:30 AM 24 would return
	 * 12:00 PM - 12:30 PM
	 */
	String parseTime(String time) {
		int num = Integer.parseInt(time);
		String start_m = "00";
		String end_m = "00";
		String start_meridiem = "AM";
		String end_meridiem = "AM";

		int start_h = num;
		int end_h;

		if (num % 2 == 0) {
			end_m = "30";
			start_h /= 2;
			start_h %= 12;
			if (start_h == 0)
				start_h = 12;
			end_h = start_h;
		} else {
			start_m = "30";
			start_h = (start_h - 1) / 2;
			start_h %= 12;
			if (start_h == 0) {
				start_h = 12;
				end_h = 1;
			} else
				end_h = start_h + 1;
		}

		if (num >= 24) {
			start_meridiem = "PM";
		}
		if (num >= 23)
			end_meridiem = "PM";

		return Integer.toString(start_h) + ":" + start_m + " " + start_meridiem
				+ " - " + Integer.toString(end_h) + ":" + end_m + " "
				+ end_meridiem;
	}

	/**
	 * "Unparse" a time with format (hh:mm AM/PM - hh:mm AM/PM) by returning the
	 * corresponding integer [0-47] e.g. 12:00 AM - 12:30 AM returns 0 12:00 PM
	 * - 12:30 PM returns 24
	 */
	int unparseTime(String time) {
		// time = "5:35 PM"; // For Testing
		Log.d("CHOSEN TIME", time);
		int hour, min;
		String meridian;
		if (time.charAt(2) == ':') {
			hour = Integer.parseInt(time.substring(0, 2));
			min = Integer.parseInt(time.substring(3, 5));
			meridian = time.substring(6, 8);
		} else {
			Log.d("", "FLAGGGGG");
			hour = Integer.parseInt(time.substring(0, 1));
			min = Integer.parseInt(time.substring(2, 4));
			meridian = time.substring(5, 7);
		}

		Log.d("hour", "" + hour);
		Log.d("min", "" + min);
		Log.d("meridian", meridian);

		if (hour == 12)
			hour = 0;

		int ret = hour * 2;

		if (meridian.compareTo("PM") == 0)
			ret += 24;

		if (min < 30)
			min = 0;
		else
			min = 30;

		if (min == 30)
			ret++;

		Log.i("RET", "" + ret);
		return ret;
	}

	public void initButtonIDs(int[] buttonID) {
		int x = R.id.button1;
		for (int i = 0; i < MAXBUTTONS; i++) {
			buttonID[i] = x;
			x++;
		}
	}

	public void initDayIDs(int[] dayID) {
		int x = R.id.day1;
		for (int i = 0; i < 7; i++) {
			dayID[i] = x;
			x++;
		}

	}

	public String getCalHeader() {
		if (isOneMonth) {
			dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
			return dateFormat.format(calendar.getTime()) + " - " + month + " "
					+ dates[MAXBUTTONS - 1] + ", " + year;
		} else {
			int index = indexOfString(month, monthsOfYear);
			Log.d("INDEX", "" + index);
			if (index == 11)
				return ("Dec " + dates[0] + ", " + year + " - Jan "
						+ dates[MAXBUTTONS - 1] + ", " + (year + 1));
			else
				return ("" + month + " " + dates[0] + ", " + year + " - "
						+ monthsOfYear[index + 1] + " " + dates[MAXBUTTONS - 1]
						+ ", " + year);
		}

	}

	/*
	 * Sets the DATES of the month for the calendar starting from the current
	 * date Depends on how many days are in the current month (and also the
	 * current year because of leap years)
	 */
	private int getNumDaysOfMonth() {
		if (month.equals("Apr") || month.equals("Jun") || month.equals("Sep")
				|| month.equals("Nov")) {
			return 30;
		} else if (month.equals("Feb")) {
			return (isLeapYear ? 29 : 28);
		} else
			return 31;

	}

	private void initDates(String[] dates) {
		int x = Integer.parseInt(dates[0]);
		for (int i = 0; i < MAXBUTTONS; i++) {
			// Should only occur a max of 1 time
			if (x == numDays + 1) {
				isOneMonth = false;
				x = 1;
			}
			dates[i] = Integer.toString(x);
			x++;
		}
	}

	public int indexOfString(String searchString, String[] domain) {
		for (int i = 0; i < domain.length; i++)
			if (searchString.equals(domain[i]))
				return i;
		return -1;
	}

	public void getCurrentTime() {
		calendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
	}

	/**
	 * onItemClick for the list of times. Allows the user to choose no more than
	 * 4 adjacent times
	 */

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TextView tv = (TextView) view.findViewById(R.id.time_available);
		String text = tv.getText().toString();
		Log.d("", "num_selected = " + num_selected);
		Log.i("pos", "" + position);

		Log.d("ITEM CLICK", "Time = " + text);

		if (num_selected == 0) {
			view.setBackgroundResource(R.drawable.li_gradient);
			firstSelected = text;
			lastSelected = text;
			num_selected = .5;
			first_pos = position;
			last_pos = position;
			String newTime = text.substring(0, text.indexOf(" - "));
			selectedTime = newTime.substring(0, newTime.indexOf(" "))
					+ newTime.substring(newTime.indexOf(" ") + 1);
			Log.d("ITEM PARSE", "Time = " + selectedTime);
			Log.d("", "prev = null");
		} else {

			// Selected an end point
			if (text.compareTo(firstSelected) == 0
					|| text.compareTo(lastSelected) == 0) {
				view.setBackgroundResource(0);
				num_selected -= .5;
				if (text.compareTo(firstSelected) == 0) {
					Log.d("", "selected = first_pos");
					View v = (View) parent.getChildAt(position + 2);
					TextView tv2 = null;
					if (v != null)
						tv2 = (TextView) v.findViewById(R.id.time_available);
					if (tv2 != null && v != null) {
						Drawable x = v.getBackground();
						if (x == null) {
							clearTimes(firstSelected, lastSelected, first_pos,
									last_pos);
						} else {
							ConstantState y = getResources().getDrawable(
									R.drawable.li_gradient).getConstantState();
							if (x.getConstantState() == y) {
								firstSelected = tv2.getText().toString();
								first_pos += 2;
							} else {
								clearTimes(firstSelected, lastSelected,
										first_pos, last_pos);
							}
						}
					} else {
						clearTimes(firstSelected, lastSelected, first_pos,
								last_pos);
					}

				} else {
					Log.d("", "selected = last_pos");
					View v = (View) parent.getChildAt(position - 2);
					TextView tv2 = null;
					if (v != null)
						tv2 = (TextView) v.findViewById(R.id.time_available);
					if (tv2 != null && v != null) {
						Drawable x = v.getBackground();
						if (x == null) {
							clearTimes(firstSelected, lastSelected, first_pos,
									last_pos);
						} else {
							ConstantState y = getResources().getDrawable(
									R.drawable.li_gradient).getConstantState();
							if (x.getConstantState() == y) {
								lastSelected = tv2.getText().toString();
								last_pos -= 2;
							} else {
								clearTimes(lastSelected, firstSelected,
										last_pos, first_pos);
							}
						}
					} else {
						clearTimes(lastSelected, firstSelected, last_pos,
								first_pos);
					}

				}
			}
			// Invalid selections
			else {
				if (position == last_pos + 2 && num_selected != 2) {
					view.setBackgroundResource(R.drawable.li_gradient);
					lastSelected = text;
					num_selected += .5;
					last_pos = position;
					Log.d("", "selected = prev+1");
				} else if (position == first_pos - 2 && num_selected != 2) {
					view.setBackgroundResource(R.drawable.li_gradient);
					firstSelected = text;
					num_selected += .5;
					first_pos = position;
					Log.d("", "selected = first-1");
					String newTime = text.substring(0, text.indexOf(" - "));
					selectedTime = newTime.substring(0, newTime.indexOf(" "))
							+ newTime.substring(newTime.indexOf(" ") + 1);
					Log.d("ITEM PARSE", "Time = " + selectedTime);
				} else {
					if (position > first_pos && position < last_pos) {

						Toast.makeText(
								getApplicationContext(),
								"Must choose consecutive times.\nSelections cleared.",
								Toast.LENGTH_SHORT).show();

					} else {
						if (num_selected == 2)
							Toast.makeText(
									getApplicationContext(),
									"Maximum reserve time reached (2 hours)\nSelections cleared.",
									Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(
									getApplicationContext(),
									"Must choose consecutive times.\nSelections cleared.",
									Toast.LENGTH_SHORT).show();

					}
					for (int i = first_pos; i <= last_pos; i += 2) {
						View v = (View) parent.getChildAt(i);
						v.setBackgroundResource(0);
						Log.d("" + i, "clearing selection");
					}
					firstSelected = "";
					lastSelected = "";
					first_pos = -3;
					last_pos = -3;
					num_selected = 0;
				}
			}
		}
	}

	void clearTimes(String selection1, String selection2, int pos1, int pos2) {
		if (pos1 == pos2) {
			pos2 = -3;
			selection2 = "";
		}
		selection1 = "";
		pos1 = -3;
		num_selected = 0;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (t_lv != null) {
			t_lis.clear();
			t_ma.notifyDataSetChanged();
			t_lv.setVisibility(View.GONE);
			rrMessage.setVisibility(View.GONE);
			goldLine.setVisibility(View.GONE);
			d_spp_params.setMargins(5, 5, 5, 85);
			d_spp.setLayoutParams(d_spp_params);
		}
		// Spinner spinner = (Spinner) parent;
		int currId = parent.getId();
		if (currId == R.id.rr_building_spinner) {
			Buildings build = (Buildings) b_ba.getItem(position);
			current_building_ID = build.getId();
			new DownloadRooms().execute();
		} else if (currId == R.id.rr_room_spinner) {
			Rooms room = (Rooms) r_ba.getItem(position);
			current_room_ID = room.getRoomID();
			current_room_name = room.getName();
			num_selected = 0;
			firstSelected = "";
			time_flag = 1;
		}

	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}

	public static void setHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
						LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/*
	 * Download times when Search Button is clicked
	 */
	public void searchTimes(View v) {
		new DownloadTimes().execute();
	}

	public void makeReservation(View v) {
		if (num_selected == 0) {
			Toast.makeText(getApplicationContext(),
					"Click the times you want to reserve first",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String range_beg = firstSelected.substring(0,
				firstSelected.indexOf("-"));
		String range_end = lastSelected.substring(
				lastSelected.indexOf("-") + 2, lastSelected.length());

		setDialog("<u>Please Verify Your Selection</u> <br><br>" + current_room_name + "<br>" + chosenDate + "<br>" + range_beg + " - " + range_end, "Reserve", "Cancel");
		
	}
	
	public void setDialog(String dialogDisplayText, String positiveButton, String negativeButton)
  	{
		Button dialogButtonPositive = (Button) dialog.findViewById(R.id.yes_button);
		dialogButtonPositive.setText(positiveButton);
	
		Button dialogButtonNegative = (Button) dialog.findViewById(R.id.no_button);
		dialogButtonNegative.setText(negativeButton);
  		
    	// Modify form elements
    	TextView tv = (TextView) dialog.findViewById(R.id.text);
    	
    	Spanned title = Html.fromHtml(dialogDisplayText);
    	tv.setText(title);
    	
    	// Show Dialog
    	dialog.show();   	
  	}
	
	@Override
    public boolean onHandleActionBarItemClick(ActionBarItem item, int position) 
	{
		Intent intent = new Intent(this, HelpActivity.class);
		intent.putExtra("help_uri", getString(R.string.roomreserve_help));
		intent.putExtra(GD_ACTION_BAR_TITLE, getIntent().getStringExtra(GD_ACTION_BAR_TITLE));
		intent.putExtra(GD_ACTION_BAR_ICON, getIntent().getIntExtra(GD_ACTION_BAR_ICON, R.drawable.stub));
	    startActivity(intent);
	    return true;	                         
    }

	public void onClick(View v) 
	{
		if(v.getId() == R.id.yes_button)
		{

			d_url = getString(R.string.roomreserves_dibs);
			d_url += "?SelectedTime=" + num_selected;
			d_url += "&SelectedRoomSize=";
			d_url += "&SelectedBuildingID="
					+ current_building_ID;
			d_url += "&SelectedRoomID=" + current_room_ID;
			d_url += "&SelectedSearchDate=" + selectedDate;
			d_url += "&SelectedStartTime=" + selectedDate
					+ "%20" + selectedTime;
			Log.d("DIBS", d_url);
	
			dialog.dismiss();
			Intent intent = new Intent(RoomReserves.this,
					WebViewer.class);
			intent.putExtra(
					GD_ACTION_BAR_TITLE,
					getIntent().getStringExtra(
							GD_ACTION_BAR_TITLE));
			intent.putExtra(
					GD_ACTION_BAR_ICON,
					getIntent().getIntExtra(
							GD_ACTION_BAR_ICON,
							R.drawable.stub));
			startActivity(intent);
		}
		else
		{
			dialog.dismiss();
		}
		
	}

}
