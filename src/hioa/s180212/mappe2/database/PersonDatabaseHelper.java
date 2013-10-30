package hioa.s180212.mappe2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * This class handles creating and upgrading the table and defines the database file name.
 */
public class PersonDatabaseHelper extends SQLiteOpenHelper {

	  private static final String DATABASE_NAME = "persontable.db";
	  private static final int DATABASE_VERSION = 4;

	  public PersonDatabaseHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  // Method is called during creation of the database
	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    PersonTable.onCreate(database);
	  }

	  // Method is called during an upgrade of the database,
	  // e.g. if you increase the database version
	  @Override
	  public void onUpgrade(SQLiteDatabase database, int oldVersion,
	      int newVersion) {
	    PersonTable.onUpgrade(database, oldVersion, newVersion);
	  }


}
