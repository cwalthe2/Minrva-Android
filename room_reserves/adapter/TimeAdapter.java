package edu.illinois.ugl.minrva.room_reserves.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.illinois.ugl.minrva.R;
import edu.illinois.ugl.minrva.room_reserves.models.Times;

/**
 * 
 * @version 2.0
 * @author Collin Walther cbwalther1@gmail.com
 */
public class TimeAdapter extends ArrayAdapter<Times>
{
	// Image loader and context
    private Context context;
    private Times[] times;
    
	public TimeAdapter(Context context, int textViewResourceId, Times[] room) 
	{
		super(context, textViewResourceId, room);
		this.context = context;
        this.times = room;
	}
	       
    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) 
    {

    	 LayoutInflater inflater = LayoutInflater.from(context);
    	 
    	 // Get row
         View row=inflater.inflate(R.layout.roomreserves_spinner_open, parent, false);
         
         // Set values
         TextView title = (TextView) row.findViewById(R.id.b_title);
         title.setText(times[position].getRoomid());

         TextView desc= (TextView) row.findViewById(R.id.b_description);
         desc.setText(times[position].getRoomid());
         

         
         
         return row;
    	
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	position++;
    	LayoutInflater inflater = LayoutInflater.from(context);
    	
    	// Get row
    	View row = inflater.inflate(R.layout.roomreserves_spinner_closed, parent, false);
    	
    	// Set values
    	TextView title = (TextView) row.findViewById(R.id.b_title);
    	title.setText(times[position].getRoomid());
    	    	

         
 		return row;
    }  
}