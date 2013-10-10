package hioa.s180212.mappe2;

import hioa.s180212.mappe2.BirthdaysContract.BirthdaysColumns;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


public class BirthdaysDatabase extends SQLiteOpenHelper {

	private static final String TAG = "BirthdayDatabase";

    private static final String DATABASE_NAME = "birthdays.db";
    
    private static final int DATABASE_VERSION = 1;
    
    interface Tables {
    	String BIRTHDAYS = "birthdays";
    }
    public BirthdaysDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.BIRTHDAYS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BirthdaysColumns.BIRTHDAY_ID   + " TEXT NOT NULL,"
                + BirthdaysColumns.BIRTHDAY_NAME + " TEXT NOT NULL,"
                + BirthdaysColumns.BIRTHDAY_DATE + " INTEGER NOT NULL,"
                + "UNIQUE (" + BirthdaysColumns.BIRTHDAY_ID + ") ON CONFLICT REPLACE)");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);

        int version = oldVersion;

        Log.d(TAG, "after upgrade logic, at version " + version);
        if (version != DATABASE_VERSION) {
            Log.w(TAG, "Destroying old data during upgrade");

            db.execSQL("DROP TABLE IF EXISTS " + Tables.BIRTHDAYS);
   
            onCreate(db);
        }
    }
}
