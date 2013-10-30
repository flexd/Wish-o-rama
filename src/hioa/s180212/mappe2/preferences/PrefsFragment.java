package hioa.s180212.mappe2.preferences;

import hioa.s180212.mappe2.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

/*
 * This class is the preference fragment for showing our preferences/settings window.
 */
public class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	private EditTextPreference mEditTextPreference;
	
	public static final String KEY_MESSAGE_PREFERENCE = "birthdayMessage";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		
		mEditTextPreference = (EditTextPreference) getPreferenceScreen()
                .findPreference(KEY_MESSAGE_PREFERENCE);
	}
	
	// To set the summary of the message preference to the current value.
	// Source: http://stackoverflow.com/a/531927/1364614
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	        String key) {
	    // Let's do something when a preference value changes
	    if (key.equals(KEY_MESSAGE_PREFERENCE)) {
	    	mEditTextPreference.setSummary(sharedPreferences.getString(key, ""));
	    }
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		 // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Set the summary of the message edittext box to the current message.
		mEditTextPreference.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_MESSAGE_PREFERENCE, ""));
		
		// Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
	}
	
	
}
