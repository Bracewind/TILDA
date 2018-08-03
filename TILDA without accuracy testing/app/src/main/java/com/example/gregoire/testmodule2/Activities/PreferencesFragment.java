package com.example.gregoire.testmodule2.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;
import com.example.gregoire.testmodule2.ExternalFileManager.DatasetManager;

public class PreferencesFragment extends PreferenceFragment  {

  public static String TAG  = "Preferences fragment";

  private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
      Log.i(TAG, "The key is : " + key);
      if (key.equals(getString(R.string.pGiven))) {
        Integer kChosen = Integer.valueOf(prefs.getString((String) getText(R.string.kGiven), "4"));
        DataHolder.getInstance().changeK(kChosen);
      }
      if (key.equals(getString(R.string.pGiven))) {
        Integer pChosen = Integer.valueOf(prefs.getString((String) getText(R.string.pGiven), "4"));
        DataHolder.getInstance().changeP(pChosen);
      }
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    prefs.registerOnSharedPreferenceChangeListener(listener);
  }

}
