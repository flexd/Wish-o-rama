


package hioa.s180212.mappe2;

import hioa.s180212.mappe2.contentprovider.PersonContentProvider;
import hioa.s180212.mappe2.database.PersonTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * PersonDetailActivity to view an existing person or create a new one.
 */
public class PersonDetailActivity extends Activity {
	private EditText mName;
	private EditText mPhoneNumber;
	private TextView mBirthdate;

	private Uri personUri;

	protected Time lastPickedDate = null;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	protected boolean bCanceled = true; // Default to cancel = true, so no changes are saved if you press the back button.

	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.edit_person);

		mName = (EditText) findViewById(R.id.editFullName);
		mPhoneNumber = (EditText) findViewById(R.id.editPhoneNumber);
		mBirthdate = (TextView) findViewById(R.id.textDateView);
		Button confirmButton = (Button) findViewById(R.id.buttonConfirmSave);
		Button cancelButton = (Button) findViewById(R.id.buttonCancelPerson);
		
		Bundle extras = getIntent().getExtras();

		// check from the saved Instance
		personUri = (bundle == null) ? null : (Uri) bundle
				.getParcelable(PersonContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			personUri = extras
					.getParcelable(PersonContentProvider.CONTENT_ITEM_TYPE);

			fillData(personUri);
		}

		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TextUtils.isEmpty(mName.getText().toString()) || TextUtils.isEmpty(mPhoneNumber.getText().toString()) || TextUtils.isEmpty(mBirthdate.getText().toString())) {
					makeToast();
				} else {
					bCanceled = false; // We want to save the data!
					setResult(RESULT_OK);
					finish();
				}
			}

		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					bCanceled  = true;
					setResult(RESULT_OK);
					finish();
			}

		});
	}

	// Fills our form/view with data from the database, if we are editing a person.
	private void fillData(Uri uri) {
		String[] projection = { PersonTable.COLUMN_NAME,
				PersonTable.COLUMN_PHONE, PersonTable.COLUMN_BIRTHDATE };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();

			mName.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(PersonTable.COLUMN_NAME)));
			mPhoneNumber.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(PersonTable.COLUMN_PHONE)));
			
			long date = cursor.getLong(cursor
					.getColumnIndexOrThrow(PersonTable.COLUMN_BIRTHDATE));
			lastPickedDate = new Time();
			lastPickedDate.set(date);
			mBirthdate.setText(lastPickedDate.format("%D"));
	
			// always close the cursor
			cursor.close();
		}
	}

	// Saves our state (what person we are editing).
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(PersonContentProvider.CONTENT_ITEM_TYPE, personUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	// Saves the state of the form/view, this is used for saving the form to the database.
	private void saveState() {
		String name = mName.getText().toString();
		String phone = mPhoneNumber.getText().toString();
		long birthdate = -1;
		if (lastPickedDate != null) {
			birthdate = lastPickedDate.toMillis(true);
		}
		
		if (bCanceled) {
			Toast.makeText(this, "No changes saved", Toast.LENGTH_SHORT).show();
			return;
		}
		

		Pattern pPhoneNumber = Pattern.compile("\\d{8}");
		Pattern pFullName = Pattern.compile("\\D+?");
		Matcher mPhoneNumber = pPhoneNumber.matcher(phone);
		Matcher mFullName = pFullName.matcher(name);
		
		// only save if all fields are filled/match the regex.

		if (!mPhoneNumber.find()){
				Toast.makeText(this, "The phone number is not valid.", Toast.LENGTH_SHORT).show();
				return;
        }
		else if (!mFullName.find()){
			Toast.makeText(this, "The name is not valid.", Toast.LENGTH_SHORT).show();
			return;
        }
		else if (birthdate == -1){
			Toast.makeText(this, "You need to set the date!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ContentValues values = new ContentValues();
		values.put(PersonTable.COLUMN_NAME, name);
		values.put(PersonTable.COLUMN_PHONE, phone);
		values.put(PersonTable.COLUMN_BIRTHDATE, birthdate);

		if (personUri == null) {
			// New person
			personUri = getContentResolver().insert(PersonContentProvider.CONTENT_URI, values);
		} else {
			// Update person
			getContentResolver().update(personUri, values, null, null);
		}
		Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void makeToast() {
		Toast.makeText(PersonDetailActivity.this, "Please fill out all the fields!",
				Toast.LENGTH_LONG).show();
	}
	
	// This shows the datepicker dialog window to select the birthdate.
	public void showDatePickerDialog(View v) {
		
		int day = 0;
		int month = 0;
		int year = 0;
		if (lastPickedDate != null) {
			day = lastPickedDate.monthDay;
			month = lastPickedDate.month;
			year = lastPickedDate.year;
		}
		else {
			Calendar calendar = Calendar.getInstance();
			day   = calendar.get(Calendar.DAY_OF_MONTH);
			month = calendar.get(Calendar.MONTH);
			year  = calendar.get(Calendar.YEAR);		            
		}
		new DatePickerDialog(PersonDetailActivity.this, mDateSetListener, year, month,day).show();
	}
		
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener =
	    new DatePickerDialog.OnDateSetListener() {
	
	    @Override
		public void onDateSet(DatePicker view, int year, 
	            int month, int day) {
	    	Calendar c = Calendar.getInstance();
	    	
	    	c.set(year, month, day);
	    	if (lastPickedDate == null) {
	    		lastPickedDate = new Time();
	    	}
	    	lastPickedDate.set(c.getTimeInMillis());
	    	
	    	mBirthdate.setText(lastPickedDate.format("%D")); //TODO: Format this!
	        //displayDate();
	    }
	};

} 
