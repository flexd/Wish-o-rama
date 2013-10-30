package hioa.s180212.mappe2;

import hioa.s180212.mappe2.contentprovider.PersonContentProvider;
import hioa.s180212.mappe2.database.PersonTable;
import hioa.s180212.mappe2.preferences.SetPreferencesActivity;
import hioa.s180212.mappe2.scheduling.MyScheduleReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

// This class represents the main activity listview where your contacts are listed.
// Strongly influenced by Vogella.com tutorial, some parts direct re-usage.
public class PeopleOverviewActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter adapter;
	private static final int DELETE_ID = Menu.FIRST + 1;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // Progress Indicator top right corner.
		
		setContentView(R.layout.person_list);
		this.getListView().setDividerHeight(2);
		registerForContextMenu(getListView());
		fillData();
		
		MyScheduleReceiver.setAlarm(this); // Set up the service if it has not already been set up.
	
	}
	
	
	
	// Handle actionbar/options clicks
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent i = new Intent(this, PersonDetailActivity.class);
			startActivity(i);
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this, SetPreferencesActivity.class));
			return true;
		}
		return false;
	}
	
	// Handle contextMenu click (long press on item).
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			Uri uri = Uri.parse(PersonContentProvider.CONTENT_URI + "/" + info.id);
			getContentResolver().delete(uri, null, null);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	  }
	
	// Fill the list with data.
	
	private void fillData() {

	    // Fields from the database (projection)
	    // Must include the _id column for the adapter to work
	    String[] from = new String[] { PersonTable.COLUMN_NAME, PersonTable.COLUMN_PHONE, PersonTable.COLUMN_BIRTHDATE };
	    // Fields on the UI to which we map
	    int[] to = new int[] { R.id.row_name, R.id.row_phone, R.id.row_birthdate };

	    
	    getLoaderManager().initLoader(0, null, this);
	    adapter = new SimpleCursorAdapter(this, R.layout.person_row, null, from,
	        to, 0);

	    setListAdapter(adapter);
	    
		adapter.setViewBinder(new ViewBinder() {

		    @Override
			public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

		        if (aColumnIndex == 3) {
//		        		Log.d("ViewBinder", "The content of column 3 is: " + aCursor.getString(aColumnIndex));
		        		long createDate = aCursor.getLong(aColumnIndex);
		                TextView textView = (TextView) aView;
		                Date time = new Date(createDate);
		     			textView.setText(sdf.format(time));
		                return true;
		         }

		         return false;
		    }
		});
	  }
	// Handle list item clicks.

	 @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    super.onListItemClick(l, v, position, id);
	    Intent i = new Intent(this, PersonDetailActivity.class);
	    Uri personUri = Uri.parse(PersonContentProvider.CONTENT_URI + "/" + id);
	    i.putExtra(PersonContentProvider.CONTENT_ITEM_TYPE, personUri);

	    startActivity(i);
	  }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Loader stuff
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { PersonTable.COLUMN_ID, PersonTable.COLUMN_NAME, PersonTable.COLUMN_PHONE, PersonTable.COLUMN_BIRTHDATE };
		CursorLoader cursorLoader = new CursorLoader(this, PersonContentProvider.CONTENT_URI, projection, null, null, null);
		setProgressBarIndeterminateVisibility(true);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
		setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
		setProgressBarIndeterminateVisibility(true);
	}

}
