package edu.illinois.ugl.minrva.new_titles.adapter;

import java.util.ArrayList;

import edu.illinois.ugl.minrva.core.adapter.ItemView;
import edu.illinois.ugl.minrva.core.adapter.ListItem;
import edu.illinois.ugl.minrva.new_titles.NewTitles;
import android.app.Activity;
import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * 
 * @version 2.0
 * @author Collin Walther & Rohit Saigal
 */
public class LibsAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater inflater;
	private ArrayList<ListItem> data;

	/**
	 * 
	 * @param a
	 * @param d
	 */
	public LibsAdapter(Activity a, ArrayList<ListItem> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * 
	 */
	public int getCount() {
		return data.size();
	}

	/**
	 * 
	 */
	public Object getItem(int position) {
		return data.get(position).data;
	}

	/**
	 * 
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 
	 */
	public boolean areAllItemsEnabled() {
		return false;
	}

	/**
	 * 
	 */
	public boolean isEnabled(int position) {
		return data.get(position).enabled;
	}

	/**
	 * 
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {
		//Log.i("Get View", "position = "+position);
		// Get list item data
		ListItem li = data.get(position);

		// Inflate list item XML if need be
		View v = convertView;
		if (convertView == null || convertView.getId() != li.id)
			v = inflater.inflate(li.id, null);

		// Change values in views in list item
		for (ItemView view : li) {
			View item = v.findViewById(view.getId());
			Object value = view.getValue();			
			
			
			if (item instanceof CheckBox) {
				CheckBox cb = (CheckBox) item;
				if (NewTitles.libs.get(position / 2).isChecked()) {
					cb.setChecked(true);
				} else
					cb.setChecked(false);

			} else if (item instanceof TextView) {
				TextView tv = (TextView) item;

				if (value instanceof String)
					tv.setText((String) value);
				else if (value instanceof Spanned)
					tv.setText((Spanned) value, TextView.BufferType.SPANNABLE);
			}

		}

		return v;
	}
}