package hioa.s180212.mappe2.preferences;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

/*
 * This activity shows the preference/settings fragment.
 */
public class SetPreferencesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();
	}
	
}
