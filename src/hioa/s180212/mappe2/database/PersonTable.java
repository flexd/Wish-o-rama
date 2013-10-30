package hioa.s180212.mappe2.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
 * This class defines the 'people' table and it's contents.
 */
public class PersonTable {
	private static final String TAG = "PersonTable";

	public static final String TABLE_PERSON = "people";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_PHONE = "phone";
	public static final String COLUMN_BIRTHDATE = "birthdate";
	public static final String COLUMN_LAST_YEAR_NOTIFIED = "last_year_notified";

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PERSON + " (" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_LAST_YEAR_NOTIFIED + " integer default 1900, " // By default we have never notified. 1900 works.
			+ COLUMN_NAME + " text not null unique, " 
			+ COLUMN_PHONE + " text not null, "
			+ COLUMN_BIRTHDATE + " timestamp not null);";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON);
		onCreate(db);
	}
}