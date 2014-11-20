package com.hackathon.myhome;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SensorsList extends Activity {

	protected static final String CLASS_NAME = "SensorsList";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();

		int type = getIntent().getIntExtra("type", 1);
		
		setContentView(R.layout.layout_sensor_data);
		
		TextView header = (TextView) findViewById(R.id.header);
		String headerText = "Everything is Fine";
		if(type == 1)
		{
			headerText = "Opened Doors";
		}
		else if(type == 2)
		{
			headerText = "Opened Windows";
		}
		else if(type == 3)
		{
			headerText = "Running lights";
		}
		
		header.setText(headerText);
		ListView list = (ListView)findViewById(R.id.sensors);
		ArrayList<String> sensors = new ArrayList<String>();
		sensors.add("Front Door");
		sensors.add("Garage Door");
		sensors.add("Light");
		sensors.add("Door");
		sensors.add("Window");
		sensors.add("Window1");
		list.setAdapter(new ItemListArrayAdapter(getApplicationContext(), sensors));
	}
	
	public class ItemListArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final ArrayList<String> values;

		public ItemListArrayAdapter(Context context, ArrayList<String> values) {
			super(context, R.layout.sensor_list_item, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.sensor_list_item, parent,false);
			TextView name = (TextView)rowView.findViewById(R.id.name);
			name.setText(values.get(position));
			name.setTextColor(Color.BLACK);

			TextView status = (TextView)rowView.findViewById(R.id.status);
			status.setText("closed");
			status.setTextColor(Color.BLACK);
			
			/*
			 * TextView titleView = (TextView) rowView
			 * .findViewById(R.id.itemtitle); TextView priceView = (TextView)
			 * rowView.findViewById(R.id.price); ImageView itemImage =
			 * (ImageView) rowView.findViewById(R.id.itemimage); if
			 * (values.get(position).getName().equalsIgnoreCase("NOITEM")) {
			 * itemImage.setVisibility(View.GONE);
			 * priceView.setVisibility(View.GONE);
			 * titleView.setText("Item Not Found");
			 * titleView.setTextColor(Color.RED); } else {
			 * titleView.setText(values.get(position).getName());
			 * titleView.setTextColor(Color.BLACK); priceView.setText("$" +
			 * values.get(position).getPricing()); new
			 * DownloadAndSetItemImage().execute(itemImage,
			 * values.get(position).getImageLocation());
			 */
			return rowView;
		}
	}
	
}
