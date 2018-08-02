package com.example.gregoire.testmodule2.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;
import com.example.gregoire.testmodule2.ExternalFileManager.DatasetManager;

/**
 * allow the user to choose the k and p of the TILDA method
 */
public class Preferences extends Activity {

  public static String TAG = "Preferences";

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Display the fragment as the main content.
    getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new PreferencesFragment())
            .commit();
  }


}
