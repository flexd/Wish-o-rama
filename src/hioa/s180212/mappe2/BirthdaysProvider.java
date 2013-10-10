package hioa.s180212.mappe2;

import java.util.Arrays;

import hioa.s180212.mappe2.BirthdaysContract.Birthdays;
import hioa.s180212.mappe2.BirthdaysDatabase.Tables;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class BirthdaysProvider extends ContentProvider {
	private static final String TAG = "BirthdaysProvider";
    private static final boolean LOGV = Log.isLoggable(TAG, Log.VERBOSE);

    private BirthdaysDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    
    private static final int BIRTHDAYS 	 = 100;
    private static final int BIRTHDAYS_ID = 101;
    
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BirthdaysContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "birthdays", BIRTHDAYS);
        matcher.addURI(authority, "birthdays/*", BIRTHDAYS_ID);
     
        return matcher;
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mOpenHelper = new BirthdaysDatabase(context);
        return true;
    }
    
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BIRTHDAYS:
                return Birthdays.CONTENT_TYPE;
            case BIRTHDAYS_ID:
                return Birthdays.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        if (LOGV) Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cur = null;
		if (sUriMatcher.match(uri) == BIRTHDAYS_ID) {
			cur = db.query(Tables.BIRTHDAYS, projection, "_id = " + Birthdays.getBirthdayId(uri), selectionArgs, null, null,
					sortOrder);
			return cur;
		} else {
			cur = db.query(Tables.BIRTHDAYS, null, null, null, null, null, null);
			return cur;
		}
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (LOGV) Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        db.insert(Tables.BIRTHDAYS, null, values);

		Cursor c = db.query(Tables.BIRTHDAYS, null, null, null, null, null, null);

		c.moveToLast();
		long minid = c.getLong(0);
		getContext().getContentResolver().notifyChange(uri, null);

		return ContentUris.withAppendedId(uri, minid);
    }
    
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (LOGV) Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int retVal = 0;
        if (sUriMatcher.match(uri) == BIRTHDAYS_ID) {
			db.update(Tables.BIRTHDAYS, values, "_id = " + Birthdays.getBirthdayId(uri), null);
			retVal = BIRTHDAYS_ID;
		}
		if (sUriMatcher.match(uri) == BIRTHDAYS) {
			db.update(Tables.BIRTHDAYS, null, null, null);
			retVal = BIRTHDAYS;
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return retVal;
       
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (LOGV) Log.v(TAG, "delete(uri=" + uri + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int retVal = 0;
        if (sUriMatcher.match(uri) == BIRTHDAYS_ID) {
			db.delete(Tables.BIRTHDAYS,"_id = " + Birthdays.getBirthdayId(uri), selectionArgs);
			retVal = BIRTHDAYS_ID;
		}
		if (sUriMatcher.match(uri) == BIRTHDAYS) {
			db.delete(Tables.BIRTHDAYS, null, null);
			retVal = BIRTHDAYS;
		}
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

}
