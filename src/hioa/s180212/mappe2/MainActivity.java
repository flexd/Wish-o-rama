package hioa.s180212.mappe2;

import hioa.s180212.mappe2.BirthdaysContract.Birthdays;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected Date lastPickedDate = null;
    EditText vFullName, vPhoneNumber;
    TextView vBirthDate;
    PeopleDBAdapter db;
    
    ListView vList;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar = Calendar.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 db = new PeopleDBAdapter(this);
		 db.open();
		
		vFullName    =  (EditText) findViewById(R.id.editFullName);
		vPhoneNumber =  (EditText) findViewById(R.id.editPhoneNumber);
		vBirthDate   =  (TextView) findViewById(R.id.textDateView);
		vList		 =  (ListView) findViewById(R.id.listPeople);
		
		vBirthDate.setText("Not picked");
		
		Cursor c = db.all();
		
		String[] columns = new String[] {
				PeopleDBAdapter.KEY_NAME,
				PeopleDBAdapter.KEY_PHONE,
				PeopleDBAdapter.KEY_DATE
		};
		
		// Reverse order from columns.
		int[] to = new int[] {
				R.id.textListBirthdate,
				R.id.textListPhoneNumber,
				R.id.textListFullName
		};
		SimpleCursorAdapter a = new SimpleCursorAdapter(this, R.layout.people_info, c, columns, to, 0);
		vList.setAdapter(a);
		
		a.setViewBinder(new ViewBinder() {

		    public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

		        if (aColumnIndex == 2) {
		                long createDate = Long.parseLong(aCursor.getString(aColumnIndex));
		                TextView textView = (TextView) aView;
		                Date time = new Date(createDate);
		     			textView.setText(time.toString());
					
		                return true;
		         }

		         return false;
		    }
		});
		
		vList.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> listView, View view, 
			     int position, long id) {
			   // Get the cursor, positioned to the corresponding row in the result set
			   Cursor cursor = (Cursor) listView.getItemAtPosition(position);
			 
			   // Get the state's capital from this row in the database.
			   String rowID = 
			    cursor.getString(cursor.getColumnIndexOrThrow("name"));
			   Toast.makeText(getApplicationContext(),
			     rowID, Toast.LENGTH_SHORT).show();
			 
			   }
			  });
	}
	
	public void showDatePickerDialog(View v) {
		
		new DatePickerDialog(MainActivity.this,
                mDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
	}
	
	public void logTheStuff(View v) {
		StringBuilder s = new StringBuilder();
		Log.d("Database stuff!", "Starting to fetch all the shit");
		Cursor c = db.all();
		
		if (c.moveToFirst()) {
			do {
				s.append(c.getString(1) + " " + c.getString(2) + " " + c.getLong(3) + "\n");
			} while (c.moveToNext());
			Log.d("Database stuff!", s.toString());
		}
	}
	
	public void addPersonButton(View v) {
		
		String name, phoneNumber;
		name = vFullName.getText().toString();
		phoneNumber = vPhoneNumber.getText().toString();
		Pattern pPhoneNumber = Pattern.compile("\\d{8}");
		Pattern pFullName = Pattern.compile("\\D+?");
		Matcher mPhoneNumber = pPhoneNumber.matcher(phoneNumber);
		Matcher mFullName = pFullName.matcher(name);
		
		
		
		if (lastPickedDate != null && mFullName.find() && mPhoneNumber.find()) {
			// Obviously can't go wrong now? >:{D
			try {
				db.add(vFullName.getText().toString(), vPhoneNumber.getText().toString(), lastPickedDate);
				Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
			}
			catch (SQLException e) {
				Toast.makeText(this, "Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
				
		}
		else {
			if (!mPhoneNumber.find()){
				Toast.makeText(this, "The phone number is not valid.", Toast.LENGTH_SHORT).show();
	        }
			else if (!mFullName.find()){
				Toast.makeText(this, "The name is not valid.", Toast.LENGTH_SHORT).show();
	        }
			else {
				Toast.makeText(this, "You're missing somethin' or it's wrong, dummy!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	// the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {
 
        public void onDateSet(DatePicker view, int year, 
                int month, int day) {
        	Calendar c = Calendar.getInstance();
        	
        	c.set(year, month, day);
        	lastPickedDate = c.getTime();
        	
        	vBirthDate.setText(lastPickedDate.toString());
            //displayDate();
        }
    };
    private void displayDate() {
		Toast.makeText(this, lastPickedDate.toString(), Toast.LENGTH_SHORT).show();
	}
	private void DisplayPerson(Cursor c) {
		Toast.makeText(this, c.getString(1), Toast.LENGTH_SHORT).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
