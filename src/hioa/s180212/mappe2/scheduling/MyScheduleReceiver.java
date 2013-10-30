package hioa.s180212.mappe2.scheduling;

import hioa.s180212.mappe2.smsservice.SmsService;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/*
 * This class is responsible for 'catching' the RECEIVE_BOOT_COMPLETED broadcast and start our SMSService.
 */
public class MyScheduleReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		setAlarm(context); // Set up the service.
	}

	/*
	 * Fetches the startTime from preferences and schedules our service to run at the specified time.
	 */
	public static void setAlarm(Context context) {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		long startTime = settings.getLong("startTime", 0);
		Calendar time = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		time.setTimeInMillis(startTime);
		today.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
		today.set(Calendar.MINUTE, time.get(Calendar.MINUTE));

		Log.d("ScheduleReceiver",
				"Scheduled startTime is: " + today.get(Calendar.HOUR_OF_DAY) + ":"
						+ today.get(Calendar.MINUTE));
		AlarmManager service = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, SmsService.class);

		PendingIntent pending = PendingIntent.getService(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		//
		// Run every 24 hours
		service.setRepeating(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pending);
	}
}