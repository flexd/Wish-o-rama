package hioa.s180212.mappe2;

import android.net.Uri;
import android.provider.BaseColumns;

public final class BirthdaysContract {

	public BirthdaysContract() {}
	
	public static final String CONTENT_AUTHORITY = "hioa.s180212.mappe2.birthdaysauthority";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_BIRTHDAYS = "birthdays";

    interface BirthdaysColumns {
		String BIRTHDAY_ID 			= "birthday_id";
		String BIRTHDAY_NAME		= "birthday_name";
		String BIRTHDAY_DATE   = "birthday_date";
    }
	public static class Birthdays implements BirthdaysColumns, BaseColumns {
		
		public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BIRTHDAYS).build();
				
		public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.mappe2.birthday";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.mappe2.birthday";
        
        public static String getBirthdayId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
	}
}
