package hioa.s180212.mappe2.smsservice;

import hioa.s180212.mappe2.R;
import hioa.s180212.mappe2.contentprovider.PersonContentProvider;
import hioa.s180212.mappe2.database.PersonTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

/*
 * The SMService is responsible for checking if it's anyone's birthday today.
 * If it's someones birthday it sends a SMS message to them with the birthday message defined in preferences/settings.
 * It also shows a notification with a list of all people with a birthday this day.
 */
public class SmsService extends Service {

	Calendar today = Calendar.getInstance();
	
	/*
	 * Shows a notification of all the people with birthdays today.
	 */
	private void showNotification(ArrayList<String> birthdays) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		StringBuilder message = new StringBuilder();
		for (Iterator<String> iterator = birthdays.iterator(); iterator.hasNext();) {
			String s = iterator.next();
			message.append(s);
			if (iterator.hasNext()) {
				message.append(", ");
			}	
		}
		Notification noti = new NotificationCompat.Builder(this)
				.setContentTitle(getResources().getString(R.string.birthday_notification_title_template))
				.setContentText(String.format(getResources().getString(R.string.birthday_notification_placeholder_text_template), message.toString()))
				.setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(true)
				.setNumber(birthdays.size())
				.build();

		notificationManager.notify(0, noti);
	}
	
	/* 
	 * Checks the database if it's someones birthday today and reacts accordingly as described in the class documentation.
	 * (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override	
	public	int	onStartCommand(Intent	intent,	int	ßags,	int	startId)	{	
			Log.d("SMSSERVICE", "Start");
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
			
			// Fields from the database (projection)
		    String[] from = new String[] { PersonTable.COLUMN_ID, PersonTable.COLUMN_NAME, PersonTable.COLUMN_PHONE, PersonTable.COLUMN_BIRTHDATE, PersonTable.COLUMN_LAST_YEAR_NOTIFIED };


		    ContentResolver cr = getContentResolver();
		    Cursor c = cr.query(PersonContentProvider.CONTENT_URI, from, null, null, null);
		    
		    ArrayList<String> birthdays = new ArrayList<String>();
		    while (c.moveToNext()) {
		    	Calendar birthdate = Calendar.getInstance();
		    	birthdate.setTimeInMillis(c.getLong(c.getColumnIndexOrThrow(PersonTable.COLUMN_BIRTHDATE)));
		    	int birthday = birthdate.get(Calendar.DAY_OF_MONTH);
		    	int birthmonth = birthdate.get(Calendar.MONTH);
		    	
		    	int thisDay = today.get(Calendar.DAY_OF_MONTH);
		    	int thisMonth = today.get(Calendar.MONTH);
		    	
		    	Log.d("SMSSERVICE", "Today is: " + thisDay + "/" + thisMonth + " the birthdate is: " + birthday + "/" + birthmonth);
		    	// Crude but it works :-)
		    	int lastNotifiedYear = c.getInt(c.getColumnIndex(PersonTable.COLUMN_LAST_YEAR_NOTIFIED));
		    	Log.d("SMSSERVICE", "lastNotifiedYear is: " + lastNotifiedYear);
		    	// If the following statement is true, it is someone's birthday today and we have not congratulated them this year.
		    	if (birthday == thisDay && birthmonth == thisMonth && (lastNotifiedYear != -1 && lastNotifiedYear != today.get(Calendar.YEAR))) {
		    		String name = c.getString(c.getColumnIndexOrThrow(PersonTable.COLUMN_NAME));
		    		int id = c.getInt(c.getColumnIndexOrThrow(PersonTable.COLUMN_ID));
		    		
		    		Log.d("SMSSERVICE", "It's " + name + "'s birthday today!");
		    		
		    		
		    		if (settings.getBoolean("bSendSMS", false)) { // Fetch the preference, default to false.
		    			// Send the SMS.
			    		SmsManager smsmanager = SmsManager.getDefault();
			    		smsmanager.sendTextMessage(c.getString(c.getColumnIndexOrThrow(PersonTable.COLUMN_PHONE)),null, settings.getString("birthdayMessage", "Happy birthday!"), null, null);
		    		}
		    		
		    		
		    		birthdays.add(name); // Add their name to the list for the notification.
		    		
		    		Uri personUri = Uri.parse(PersonContentProvider.CONTENT_URI + "/" + id);
		    		ContentValues values = new ContentValues();
		    		values.put(PersonTable.COLUMN_LAST_YEAR_NOTIFIED, today.get(Calendar.YEAR));
		    		getContentResolver().update(personUri, values, null, null); // Save the last year we sent a SMS.
		    		
		    	}
		    	
		    	// If birthdays contains any names, at least one person has his/hers birthday today so we show the notification.
		    	if (birthdays.size() > 0) {
		    		showNotification(birthdays); 
		    	}
		    }
		    c.close(); // Close the cursor.
		    
		    Log.d("SMSSERVICE", "End");
			return	Service.START_NOT_STICKY;	
	}	

	
	// Not used since this is not a local service we need to bind to.
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
