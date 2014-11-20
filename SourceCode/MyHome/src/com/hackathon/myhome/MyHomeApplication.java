package com.hackathon.myhome;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;
import com.ibm.mobile.services.push.IBMPush;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MyHomeApplication extends Application {

	private Activity mActivity;
	private static final String deviceAlias = "TargetDevice";		
	private static final String consumerID = "MyHome-Ramesh";	
	private static final String CLASS_NAME = MyHomeApplication.class.getSimpleName();
	private static final String APP_ID = "applicationID";
	private static final String APP_SECRET = "applicationSecret";
	private static final String APP_ROUTE = "applicationRoute";
	private static final String PROPS_FILE = "bluelist.properties";

	final List<SensorInfo> itemList = new ArrayList<SensorInfo>();
	
	public static final String MYOWNACTIONFILTER = "com.hackathon.myhome";
	
	private Handler handler = new Handler();
	
	public MyHomeApplication() {
		
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			
			@Override
			public void onActivityStopped(Activity activity) {
			}
			
			@Override
			public void onActivityStarted(Activity activity) {
			}
			
			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			}
			
			@Override
			public void onActivityResumed(Activity activity) {
				Log.d(CLASS_NAME, "OnActivityResumed");
				handler.postDelayed(readData, 2000);
			}
			
			@Override
			public void onActivityPaused(Activity activity) {
				Log.d(CLASS_NAME, "OnActivityPaused");
				handler.removeCallbacks(readData);
			}
			
			@Override
			public void onActivityDestroyed(Activity activity) {
			}
			
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				Log.d(CLASS_NAME, "OnActivityCreated");
				handler.postDelayed(readData, 2000);
			}
		});
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		Properties props = new java.util.Properties();
		Context context = getApplicationContext();
		try {
			AssetManager assetManager = context.getAssets();					
			props.load(assetManager.open(PROPS_FILE));
			Log.i(CLASS_NAME, "Found configuration file: " + PROPS_FILE);
		} catch (FileNotFoundException e) {
			Log.e(CLASS_NAME, "The bluelist.properties file was not found.", e);
		} catch (IOException e) {
			Log.e(CLASS_NAME, "The bluelist.properties file could not be read properly.", e);
		}
		Log.i(CLASS_NAME, "Application ID is: " + props.getProperty(APP_ID));
		
		// Initialize the IBM core backend-as-a-service.
		IBMBluemix.initialize(this, props.getProperty(APP_ID), props.getProperty(APP_SECRET), props.getProperty(APP_ROUTE));
		// Initialize the IBM Data Service.
		IBMData.initializeService();
		// Register Item Specialization here.
		SensorInfo.registerSpecialization(SensorInfo.class);
		// Initialize IBM Push service.
		IBMPush.initializeService();
		
		listItems();
	}

	Runnable readData = new Runnable() {
		@Override
		public void run() {
			Log.d(CLASS_NAME, "Executing run");
			listItems();
//			handler.postDelayed(readData, 5*5000);
		}
	};
	
	public List<SensorInfo> getDoorsSensorList()
	{
		List<SensorInfo> doors = new ArrayList<SensorInfo>();
		for (SensorInfo item : itemList) {
			if(item.getType().equals("1"))
				doors.add(item);
        }
		Log.d(CLASS_NAME, "size of doors list: " + doors.size());
		return doors;
	}
	
	public List<SensorInfo> getWindowsSensorList()
	{
		List<SensorInfo> windows = new ArrayList<SensorInfo>();
		for (SensorInfo item : itemList) {
			if(item.getType().equals("2"))
				windows.add(item);
		}
		Log.d(CLASS_NAME, "size of windows list: " + windows.size());
		return windows;
	}
	
	public List<SensorInfo> getLightsSensorList()
	{
		List<SensorInfo> lights = new ArrayList<SensorInfo>();
		for (SensorInfo item : itemList) {
			if(item.getType().equals("3"))
				lights.add(item);
		}
		Log.d(CLASS_NAME, "size of lights list: " + lights.size());
		return lights;
	}
	
	public List<SensorInfo> getSensorList()
	{
		return itemList;
	}
	
	public void listItems() {
		try {
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

				List<SensorInfo> currentData = new ArrayList<SensorInfo>();
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
//                        itemList.clear();
                        Log.d(CLASS_NAME, "My: ItemList before size: " + itemList.size());
                        for (IBMDataObject item : objects) {
                        	currentData.add((SensorInfo) item);
                        }
                        Log.d(CLASS_NAME, "ItemList size: " + itemList.size());
//                        sortItems(itemList);
//                        lvArrayAdapter.notifyDataSetChanged();
                        
                        // if there is some change
                    	Intent intent = new Intent();
                		// Bundle the counter value with Intent
                		intent.putExtra("key", "value");
                		intent.setAction(MYOWNACTIONFILTER); // Define intent-filter
                		sendBroadcast(intent);
                		Log.d(CLASS_NAME, "broadcasted message");
                    }
                    return null;
                }
            },Task.UI_THREAD_EXECUTOR);
			Log.d(CLASS_NAME, " completed getlist");
			
		}  catch (IBMDataException error) {
			Log.e(CLASS_NAME, "Exception : " + error.getMessage());
		}
	}
}
