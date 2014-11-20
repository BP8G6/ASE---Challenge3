package com.hackathon.myhome;

import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.data.IBMDataObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CurrentStatus_v1 extends Activity {

	protected static final String CLASS_NAME = "CurrentStatus";


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_currentstatus);
		
		
//		Button add = (Button)findViewById(R.id.submit);
//		
//		add.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				createItem(v);
//			}
//		});
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
//						listItems();
//						updateOtherDevices();
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
	
}
