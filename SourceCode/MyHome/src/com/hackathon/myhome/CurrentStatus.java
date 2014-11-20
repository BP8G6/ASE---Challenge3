package com.hackathon.myhome;

import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class CurrentStatus extends Activity implements OnClickListener {

	protected static final String CLASS_NAME = "CurrentStatus";

	private MyHomeApplication myApp;
	private MyService myService;
	private IntentFilter filter;
	private Intent bindIntent;
	
	ImageView door;
	ImageView window;
	ImageView light;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();

		setContentView(R.layout.layout_currentstatus);
		Log.d(CLASS_NAME, "Current status OnCreate invoked");
		myApp = (MyHomeApplication)getApplication();
		
		window = (ImageView) findViewById(R.id.window);
		door = (ImageView) findViewById(R.id.door);
		light = (ImageView) findViewById(R.id.light);

		window.setOnClickListener(this);
		door.setOnClickListener(this);
		light.setOnClickListener(this);

//		listItems();
		
		filter = new IntentFilter(MyHomeApplication.MYOWNACTIONFILTER);
		//registerReceiver(myReceiver, filter);
//
//		bindIntent = new Intent(this, MyService.class);
//		bindService(bindIntent, serviceConncetion, Context.BIND_AUTO_CREATE);
		Log.d(CLASS_NAME, "Current status OnCreate completed");
	}

	@Override
	public void onPause() {
		super.onPause();

		unregisterReceiver(myReceiver);
//		unbindService(serviceConncetion);
		// Toast.makeText(getApplicationContext(), "i am pausing",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		// Toast.makeText(getApplicationContext(), "i am resuming",
		// Toast.LENGTH_SHORT).show();

		System.out.println("OnResume: Registering for brodcasted message");
		
		setDoorStatus();
		setWindowStatus();
		setLightStatus();
		
//		bindService(bindIntent, serviceConncetion, Context.BIND_AUTO_CREATE);
		registerReceiver(myReceiver, filter);
	}

	private void setLightStatus() {
		List<SensorInfo> lightList = myApp.getLightsSensorList();
		boolean islightOpened = false;
		for(SensorInfo light: lightList)
		{
			if(light.getStatus().equalsIgnoreCase("1"))
				islightOpened = true;
		}
		
		if(islightOpened)
			light.setImageResource(R.drawable.light_on);
		else
			light.setImageResource(R.drawable.light_off);
	}

	private void setWindowStatus() {
		List<SensorInfo> windowList = myApp.getWindowsSensorList();
		boolean iswindowOpened = false;
		for(SensorInfo window: windowList)
		{
			if(window.getStatus().equalsIgnoreCase("1"))
				iswindowOpened = true;
		}
		
		if(iswindowOpened)
			window.setImageResource(R.drawable.opened_window);
		else
			window.setImageResource(R.drawable.closed_window);
	}

	private void setDoorStatus() {
		List<SensorInfo> doorsList = myApp.getDoorsSensorList();
		boolean isDoorOpened = false;
		for(SensorInfo door: doorsList)
		{
			if(door.getStatus().equalsIgnoreCase("1"))
				isDoorOpened = true;
		}
		
		if(isDoorOpened)
			door.setImageResource(R.drawable.open_door);
		else
			door.setImageResource(R.drawable.closed_door);
	}

	// service Connection
	private ServiceConnection serviceConncetion = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			// Toast.makeText(getApplicationContext() , "service connected",
			// Toast.LENGTH_SHORT).show();
			myService = ((MyService.MyBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			// Toast.makeText(getApplicationContext() , "service disconnected",
			// Toast.LENGTH_SHORT).show();
			myService = null;
		}
	};

	// receiver for the activity
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get Bundles
			Bundle extras = intent.getExtras();
			System.out.println("Broadcast message received+++: " + extras.getString("key"));
//			myService.setData();
		}
	};

	public void listItems() {
		try {
			final List<SensorInfo> itemList = new ArrayList<SensorInfo>();
			System.out.println("CS: getting list items +++++++");
			IBMQuery<SensorInfo> query = IBMQuery.queryForClass(SensorInfo.class);
			System.out.println("CS: check1");
			/**
			 * IBMQueryResult is used to receive array of objects from server.
			 * 
			 * onResult is called when it successfully retrieves the objects associated with the 
			 * query, and will reorder these items based on creation time.
			 * 
			 * onError is called when an error occurs during the query.
			 */
			query.find().continueWith(new Continuation<List<SensorInfo>, Void>() {

                @Override
                public Void then(Task<List<SensorInfo>> task) throws Exception {
                    // Log error message, if the save task is cancelled.
                    if (task.isCancelled()) {
                        Log.e(CLASS_NAME, "CS: Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the save task fails.
                    if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "CS: Exception : " + task.getError().getMessage());
                    }

                    // If the result succeeds, load the list.
                    else {
                        final List<SensorInfo> objects = task.getResult();
                        // Clear local itemList, as we'll be reordering & repopulating from DataService.
                        itemList.clear();
                        System.out.println("CS: ItemList before size: " + itemList.size());
                        for (IBMDataObject item : objects) {
                            itemList.add((SensorInfo) item);
                        }
                        System.out.println("CS: ItemList size: " + itemList.size());
//                        sortItems(itemList);
//                        lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }
            },Task.UI_THREAD_EXECUTOR);
			System.out.println("CS: completed getlist");
			
		}  catch (IBMDataException error) {
			Log.e(CLASS_NAME, "Exception : " + error.getMessage());
		}
	}
	public void createItem(View v) {
		v.setClickable(false);
		EditText sensorName = (EditText) findViewById(R.id.name);
		EditText sensorStatu = (EditText) findViewById(R.id.status);
		String nameToAdd = sensorName.getText().toString();
		String statusToAdd = sensorStatu.getText().toString();
		SensorInfo sensor = new SensorInfo();
		if (!nameToAdd.equals("") && !statusToAdd.equals("")) {
			sensor.setName(nameToAdd);
			sensor.setStatus(statusToAdd);

			sensor.save().continueWith(new Continuation<IBMDataObject, Void>() {

				@Override
				public Void then(Task<IBMDataObject> task) throws Exception {
					// Log error message, if the save task is cancelled.
					if (task.isCancelled()) {
						Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
					}
					// Log error message, if the save task fails.
					else if (task.isFaulted()) {
						Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
					}

					// If the result succeeds, load the list.
					else {
						System.out.println("sensor data added succesfully");
						// listItems();
						// updateOtherDevices();
					}
					return null;
				}

			});

			// Set text field back to empty after item added.
			sensorName.setText("");
			sensorStatu.setText("");
			v.setClickable(true);
		}
	}

	@Override
	public void onClick(View v) {

		System.out.println("onclicked invoked");
		int sensorType = 1;
		switch (v.getId()) {
		case R.id.door:
			System.out.println("Door image clicked");
			sensorType = 1;
			break;
		case R.id.window:
			System.out.println("Window image clicked");
			sensorType = 2;
			break;
		case R.id.light:
			System.out.println("Light image clicked");
			sensorType = 3;
			break;
		default:
			System.out.println("Nothing clicked");
			break;
		}

		Intent allSensors = new Intent(getApplicationContext(), SensorsList.class);
		allSensors.putExtra("type", sensorType);
		// startActivityForResult(allSensors, 111);
		startActivity(allSensors);
	}

	/*@Override
	public void onBackPressed() {
		System.out.println("");
		Toast.makeText(getApplicationContext(), "Back disabled", Toast.LENGTH_SHORT).show();
	}*/
}
