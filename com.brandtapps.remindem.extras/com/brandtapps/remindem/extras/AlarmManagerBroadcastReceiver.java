package com.brandtapps.remindem.extras;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	public static final String ACTION_RESPONSE = "com.brandtapps.remindem.SCHEDULE_QUERY";

	@Override
	public void onReceive(Context context, Intent intent) {

		// note, alarms are cleared on reboot, so we must reset alarm after boot
		// up.
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			SetAlarm(context);
			Log.v("TEST", "Service loaded at start");
		} else if (ACTION_RESPONSE.equals(intent.getAction())) {

			String errorMessage = intent.getStringExtra(CommunicationsIntentService.PARAM_OUT_ERROR_MESSAGE);

			if (errorMessage != null && !errorMessage.equals("")) {
				sendSMS(getMyPhoneNumber(context), "Failed to acquire Data.");
			} else {

				String data = intent.getStringExtra(CommunicationsIntentService.PARAM_OUT_JSON);
				data = CleanRtf.cleanRtf(data);

				
				HashSet<String> names = findNames(data);

				String sentNames = "";
				
				Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
				while (phones.moveToNext()) {
					// String
					// name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					// String phoneNumber =
					// phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

					// if ( names.contains(name)){
					// sendSMS(getMyPhoneNumber(context),
					// "Reminder: You are schedule to run sound this Sabbath.");
					//sentNames+= name + " ";
				//}
					
				}
				phones.close();

				sendSMS(getMyPhoneNumber(context), "Just send messages From RemindEm: " + sentNames);

			}

		} else {

			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MY_WAKE");

			// Acquire the lock
			wl.acquire();

			// You can do the processing here.
			// Bundle extras = intent.getExtras();
			StringBuilder msgStr = new StringBuilder();

			Format formatter = new SimpleDateFormat("hh:mm:ss a", Locale.US);
			msgStr.append(formatter.format(new Date()));

			Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

			if (isOnline(context.getApplicationContext())) {

				try {

					Calendar now = Calendar.getInstance();
					int year = now.get(Calendar.YEAR);
					String filename = "_" + Integer.toString(year) + ".rtf";
					if (Calendar.getInstance().get(Calendar.MONTH) % 2 == 0) { // Jan,
																				// Mar
																				// May...

						String currMonth = firstUpper(new SimpleDateFormat("MMM").format(now.getTime()));

						now.add(Calendar.MONTH, 1);
						String nextMonth = firstUpper(new SimpleDateFormat("MMM").format(now.getTime()));

						filename = currMonth + "_" + nextMonth + filename;
					} else {

						String currMonth = firstUpper(new SimpleDateFormat("MMM").format(now.getTime()));

						now.add(Calendar.MONTH, -1);
						String prevMonth = firstUpper(new SimpleDateFormat("MMM").format(now.getTime()));

						filename = prevMonth + "_" + currMonth + filename;

					}

					Intent commIntent = new Intent(context, CommunicationsIntentService.class);
					commIntent.putExtra(CommunicationsIntentService.PARAM_RECEIVER, ACTION_RESPONSE);
					commIntent.putExtra(CommunicationsIntentService.PARAM_ACTION, "paschedule/" + filename);
					context.startService(commIntent);

				} catch (Exception e) {
					sendSMS(getMyPhoneNumber(context), "Failed online connection.");
				}
			}

			// Release the lock
			wl.release();
		}
	}

	private HashSet<String> findNames(String str) {

		HashSet<String> names = new HashSet<String>();

		String[] plainTextArr = str.split("\n");

		Calendar now = Calendar.getInstance();
		int weekday = now.get(Calendar.DAY_OF_WEEK);
		if (weekday != Calendar.SATURDAY) {
			int days = (Calendar.SATURDAY - weekday) % 7;
			now.add(Calendar.DAY_OF_YEAR, days);
		}
		// now is the date you want
		Date date = now.getTime();

		String month = firstUpper(new SimpleDateFormat("MMM", Locale.US).format(date));
		String day = new SimpleDateFormat("MMM", Locale.US).format(date);

		Boolean found = false;
		for (String text : plainTextArr) {
			if (text.startsWith(month + " " + day)) {
				names.addAll(Arrays.asList(text.split("\t")));
				found = true;
			} else if (found) {
				names.addAll(Arrays.asList(text.split("\t")));
				break;
			}

		}
		return names;
	}

	private String firstUpper(String str) {
		return str.substring(0, 1).toUpperCase(Locale.US) + str.substring(1);
	}

	public String getMyPhoneNumber(Context mContext) {
		return ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
	}

	public static boolean isOnline(Context mContext) {
		ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	private void sendSMS(String phoneNumber, String message) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	}

	public void SetAlarm(Context context) {

		final int alarmInterval = 7; // 7;

		// cancel any existing alarm before setting a new one.
		CancelAlarm(context);

		
		final int targetDay = 6;// 5
		final int targetHour = 8;
		final int targetMinute = 52;
		final int targetSecond = 0;

		Calendar calendar = Calendar.getInstance();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		if (dayOfWeek == targetDay && hour >= targetHour && minute >= targetMinute && second >= targetSecond)
			calendar.add(Calendar.DATE, 7);
		else
			calendar.add(Calendar.DATE, (targetDay - dayOfWeek) % 7);

		calendar.set(Calendar.HOUR_OF_DAY, targetHour);
		calendar.set(Calendar.MINUTE, targetMinute);
		calendar.set(Calendar.SECOND, targetSecond);

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class).setAction("com.brandtapps.remindem.extras.AlarmManagerBroadcastReceiver.ALARM");
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		//am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, pi);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY * alarmInterval, pi);
		// am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi); //
		// AlarmManager.INTERVAL_DAY * 7
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class).setAction("com.brandtapps.remindem.extras.AlarmManagerBroadcastReceiver.ALARM");
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

}
