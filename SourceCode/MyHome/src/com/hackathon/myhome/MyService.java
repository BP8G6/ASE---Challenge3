package com.hackathon.myhome;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service implements Runnable {

	private static final String deviceAlias = "TargetDevice";		
	private static final String consumerID = "MyHome-Ramesh";	
	private static final String APP_ID = "applicationID";
	private static final String APP_SECRET = "applicationSecret";
	private static final String APP_ROUTE = "applicationRoute";
	private static final String PROPS_FILE = "bluelist.properties";
	
	private final IBinder binder = new MyBinder();
	private Handler handler = new Handler();
	public static final String MYOWNACTIONFILTER = "com.hackathon.myhome";
	public static final String CLASS_NAME = "MyService";

	List<SensorInfo> itemList;
	
	// Instance of the Service is now in MyBinder
	public class MyBinder extends Binder {
		MyService getService() {
			return MyService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("MyService onCreate");
		// Service is just created
		// but does nothing now
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
		Log.i(CLASS_NAME, "MyService: Application ID is: " + props.getProperty(APP_ID));
		
		// Initialize the IBM core backend-as-a-service.
		IBMBluemix.initialize(this, props.getProperty(APP_ID), props.getProperty(APP_SECRET), props.getProperty(APP_ROUTE));
		// Initialize the IBM Data Service.
		IBMData.initializeService();
		// Register Item Specialization here.
		SensorInfo.registerSpecialization(SensorInfo.class);
		// Initialize IBM Push service.
		IBMPush.initializeService();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		System.out.println("MyService: onStartCommand invoked");
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startid) {
		// I call the Runnable using Handler instance
		// handler.removeCallbacks(this);
		// if(i==0){
		// Toast.makeText(getApplicationContext(), "started",
		// Toast.LENGTH_LONG).show();
		System.out.println("MyService onStart: " + intent.getPackage());
		handler.postDelayed(this, 2000); // after 2 sec it will call the run()
										// block
		// }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void setData() {
		System.out.println("Invoked Service method from Client");
	}

	public boolean onUnbind(Intent Intent) {
		handler.removeCallbacks(this);
		System.out.println("MyService onUnbind");
		// Toast.makeText(getApplicationContext(), "test",
		// Toast.LENGTH_SHORT).show();

		return true;
	}

	@Override
	public void run() {
		System.out.println("My Service running");

		Intent intent = new Intent();
		// Bundle the counter value with Intent
		intent.putExtra("key", "value");
		intent.setAction(MYOWNACTIONFILTER); // Define intent-filter
		sendBroadcast(intent);
		
		System.out.println("MyService: broadcasted message");
		listItems();
		//handler.postDelayed(this, 2000);
	}

	/**
	 * Refreshes itemList from data service.
	 * 
	 * An IBMQuery is used to find all the list items.
	 */
	public void listItems() {
		try {
			System.out.println("MyService: getting list items +++++++");
			IBMQuery<SensorInfo> query = IBMQuery.queryForClass(SensorInfo.class);
			System.out.println("MyService: check1");
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
                        Log.e(CLASS_NAME, "MyService: Exception : Task " + task.toString() + " was cancelled.");
                    }
                    // Log error message, if the save task fails.
                    if (task.isFaulted()) {
                        Log.e(CLASS_NAME, "MyService: Exception : " + task.getError().getMessage());
                    }

                    // If the result succeeds, load the list.
                    else {
                        final List<SensorInfo> objects = task.getResult();
                        // Clear local itemList, as we'll be reordering & repopulating from DataService.
                        itemList.clear();
                        System.out.println("MyService: ItemList before size: " + itemList.size());
                        for (IBMDataObject item : objects) {
                            itemList.add((SensorInfo) item);
                        }
                        System.out.println("MyService: ItemList size: " + itemList.size());
//                        sortItems(itemList);
//                        lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }
            },Task.UI_THREAD_EXECUTOR);
			System.out.println("MyService: completed getlist");
			
		}  catch (IBMDataException error) {
			Log.e(CLASS_NAME, "Exception : " + error.getMessage());
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("MyService onBind");
		handler.postDelayed(this, 2000);
		return binder;
	}

}
