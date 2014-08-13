package com.brandtapps.remindem;

import com.brandtapps.remindem.R;
import com.brandtapps.remindem.extras.AlarmManagerBroadcastReceiver;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private TextView alarmTextView = null;
		
		private AlarmManagerBroadcastReceiver alarm;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);

			alarmTextView = (TextView) rootView.findViewById(R.id.textView_alarm);
			
			alarm = new AlarmManagerBroadcastReceiver();

			
			final Button button = (Button) rootView.findViewById(R.id.button_set_alarm);
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					isAlarmSet(getActivity().getApplicationContext());
					
					Context context = getActivity().getApplicationContext();
					if (alarm != null) {
						alarm.SetAlarm(context);
					} else {
						Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
					}
					isAlarmSet(getActivity().getApplicationContext());
				}
			});

			final Button button2 = (Button) rootView.findViewById(R.id.button_cancel_alarm);
			button2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					isAlarmSet(getActivity().getApplicationContext());
					
					Context context = getActivity().getApplicationContext();
					if (alarm != null) {
						alarm.CancelAlarm(context);
					} else {
						Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
					}
					isAlarmSet(getActivity().getApplicationContext());
				}
			});
			
			return rootView;
		}

		@Override
		public void onResume(){
			super.onResume();
			
			isAlarmSet(getActivity().getApplicationContext());
		}
		
		private void isAlarmSet(Context context){
			PendingIntent intent = PendingIntent.getBroadcast(context, 0, 
			        new Intent(context,AlarmManagerBroadcastReceiver.class).setAction("com.brandtapps.remindem.extras.AlarmManagerBroadcastReceiver.ALARM"), 
			        PendingIntent.FLAG_NO_CREATE);
			
			boolean alarmUp = (PendingIntent.getBroadcast(context, 0, 
			        new Intent(context,AlarmManagerBroadcastReceiver.class).setAction("com.brandtapps.remindem.extras.AlarmManagerBroadcastReceiver.ALARM"), 
			        PendingIntent.FLAG_NO_CREATE) != null);

			if (alarmUp)
			{
				alarmTextView.setText("Alarm Set");
			}
			else
				alarmTextView.setText("Alarm NOT Set");
		}
		
	}
}
