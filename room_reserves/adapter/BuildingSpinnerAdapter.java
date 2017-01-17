package edu.illinois.ugl.minrva.room_reserves.adapter;

import java.util.ArrayList;

import edu.illinois.ugl.minrva.core.adapter.ItemView;
import edu.illinois.ugl.minrva.core.adapter.ListItem;
import edu.illinois.ugl.minrva.core.cache.ImageLoader;
import edu.illinois.ugl.minrva.R;
import android.app.Activity;
import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @version 2.0
 * @author Nathaniel Ryckman &lt;nathanielryckman@gmail.com&gt;
 */
public class BuildingSpinnerAdapter extends BaseAdapter 
{    
    private Activity activity;
    private LayoutInflater inflater;
    private ImageLoader imageLoader; 
    private ArrayList<ListItem> data;

    /**
     * 
     * @param a
     * @param d
     */
    public BuildingSpinnerAdapter(Activity a, ArrayList<ListItem> d) 
    {
    	activity = a;
    	data = d;
    	inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity);
    }
    
    /**
     * 
     */
    public int getCount() 
    {
        return data.size();
    }
    
    /**
     * 
     */
    public Object getItem(int position) 
    {
        return data.get(position).data;
    }

    /**
     * 
     */
    public long getItemId(int position) 
    {
        return position;
    }
    
    /**
     * 
     */
    public boolean areAllItemsEnabled() 
    {
        return false;
    }

    /**
     * 
     */
    public boolean isEnabled(int position) 
    {
    	return data.get(position).enabled;
    }
    
    /**
     * 
     */
    public View getView(int position, View convertView,ViewGroup parent) {
    	// Get list item data
    	ListItem li = data.get(position);
    	
    	// Inflate list item XML if need be
    	View v = convertView;    
    	v = inflater.inflate(R.layout.roomreserves_spinner_closed, null);
        //if (convertView == null || convertView.getId() != li.id) 
        	//v = inflater.inflate(li.id, null);
        
        // Change values in views in list item
        for(ItemView view:li)
        {
        	View item = v.findViewById(view.getId());
        	Object value = view.getValue();
        		
        	if(item instanceof ImageView)
        	{
        		ImageView iv = (ImageView) item;
        		imageLoader.DisplayImage((String) value, iv);
        	}
        	else if(item instanceof TextView)
        	{
        		TextView tv = (TextView) item;
        		
        		if(value instanceof String)
        			tv.setText((String) value);
        		else if(value instanceof Spanned)
        			tv.setText((Spanned) value, TextView.BufferType.SPANNABLE);		
        	}
        }
        
        return v;
    }
    public View getDropDownView(int position, View convertView, ViewGroup parent) 
    {
    	// Get list item data
    	ListItem li = data.get(position);
    	
    	// Inflate list item XML if need be
    	View v = convertView;      
        if (convertView == null || convertView.getId() != li.id) 
        	v = inflater.inflate(li.id, null);
        
        // Change values in views in list item
        for(ItemView view:li)
        {
        	View item = v.findViewById(view.getId());
        	Object value = view.getValue();
        		
        	if(item instanceof ImageView)
        	{
        		ImageView iv = (ImageView) item;
        		imageLoader.DisplayImage((String) value, iv);
        	}
        	else if(item instanceof TextView)
        	{
        		TextView tv = (TextView) item;
        		
        		if(value instanceof String)
        			tv.setText((String) value);
        		else if(value instanceof Spanned)
        			tv.setText((Spanned) value, TextView.BufferType.SPANNABLE);		
        	}
        }
        
        return v;
    }
}