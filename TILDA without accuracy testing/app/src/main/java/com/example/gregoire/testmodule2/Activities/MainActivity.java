/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.gregoire.testmodule2.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;
import com.example.gregoire.testmodule2.PersonnalizedView.ItemClickSupport;
import com.example.gregoire.testmodule2.PersonnalizedView.MyAdapter;
import com.example.gregoire.testmodule2.helpers.PermissionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

import jp.wasabeef.blurry.Blurry;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

  public static String TAG = "Main Activity";
  public static boolean FROM_ASSETS = false;
  private static final String neuralNetworkPath = "testInception.pb";
  private static final String labelPath = "testInception.txt";

  private Dialog mDialog;
  private boolean isTraining;

  EditText class_given;

  //to draw
  private Canvas mCanvas;
  private Paint mPaint = new Paint();
  private Bitmap mBitmap;
  private ImageView mImageView;
  private Rect mRect = new Rect();
  private Rect mBounds = new Rect();

  //to display list of existing class
  RecyclerView mRecyclerView;
  MyAdapter mAdapter;
  LinearLayoutManager mLayoutManager;

  private static final int PERMISSION_CODE_READ_STORAGE = 3003;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    File pathDataset = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.incremental_method_folder));
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    Integer kChosen = Integer.valueOf(prefs.getString((String) getText(R.string.kGiven), "4"));
    Integer pChosen = Integer.valueOf(prefs.getString((String) getText(R.string.pGiven), "4"));

    //the tensorflow network is initialized here
    //WARNING : we need access to the assets folder to load the weights
    if (FROM_ASSETS) {
      DataHolder.getInstance().initializeDataHolder(getAssets(), pathDataset, kChosen, pChosen, 2048);
    }
    else {
      //get neural network and label stream
      InputStream neuralNetworkis = new InputStream() {
        @Override
        public int read() throws IOException {
          return 0;
        }
      };
      InputStream labelis = new InputStream() {
        @Override
        public int read() throws IOException {
          return 0;
        }
      };
      if (PermissionUtils.isReadStorageGranted(this)) {
        try {
          neuralNetworkis = readFile(neuralNetworkPath);
          labelis = readFile(labelPath);
        }
        catch (FileNotFoundException e) {

        }
      } else {
        PermissionUtils.checkPermission(this, Manifest.permission.CAMERA,
                PERMISSION_CODE_READ_STORAGE);
      }
      DataHolder.getInstance().initializeDataHolder(neuralNetworkis, labelis, pathDataset, kChosen, pChosen, 2048);
    }

    Button test = findViewById(R.id.button_test);

    test.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        isTraining = false;

        Intent intent = sendInfoToNextActivity();
        startActivity(intent);
      }
    });

    Button buttonTrain = findViewById(R.id.button_train);

    buttonTrain.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        isTraining = true;

        mDialog = createDialog();
        mDialog.show();
      }


    });

    //Alert dialog to be sure the user wants to erase all the training
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            this);

    alertDialogBuilder.setTitle("Reinitialize training");
    alertDialogBuilder
            .setMessage("Are you sure you want to erase all the past experience of the method ?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                DataHolder.getInstance().getDataset().reinitializeTraining();
                dialog.cancel();
              }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
              }
            });
    // create alert dialog
    final AlertDialog alertDialog = alertDialogBuilder.create();

    View reinitializeButton = findViewById(R.id.button_reinitialize_training);
    reinitializeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        alertDialog.show();
      }
    });

    Button setting = findViewById(R.id.button_settings);
    setting.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, Preferences.class);
        startActivity(intent);
      }
    });

    Log.i(TAG, "k value is : " + prefs.getString((String) getText(R.string.kGiven), "null"));

  }

  protected InputStream readFile(String path) throws FileNotFoundException {
    if (isExternalStorageReadable()) {
      try {
        File neuralNetwork = new File(Environment.getExternalStorageDirectory(), path);
        Log.i(TAG, "path : " + Environment.getExternalStorageDirectory());
        FileInputStream neuralNetworkStream = new FileInputStream(neuralNetwork);
        return neuralNetworkStream;
      }
      catch (FileNotFoundException e) {
        e.printStackTrace();
        throw e;
      }
    }
    throw new FileNotFoundException("file not readable");
  }

  protected boolean isExternalStorageReadable() {
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.i(TAG, "k value : " + DataHolder.getInstance().kChosen() +
                " p value : " + DataHolder.getInstance().pChosen());
  }

  private void drawNodes(ImageView imageView) {
    //to draw
    mPaint.setColor(Color.BLUE);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(10);

    mBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    imageView.setImageBitmap(mBitmap);
    mCanvas = new Canvas(mBitmap);
    mCanvas.drawCircle(50, 50, 40, mPaint);
  }

  /**
   * Execute the code in agreement with the will of the user.
   *
   * @param requestCode
   * @param permissions
   * @param grantResults represents the will of the user
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_CODE_READ_STORAGE:
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
          finish();
        }
        break;
    }
    if (requestCode != PERMISSION_CODE_READ_STORAGE) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  private Dialog createSelecterClassDialog() {

    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

    LayoutInflater inflater = getLayoutInflater();
    final View dialogView = inflater.inflate(R.layout.select_class_dialog, null);

    mRecyclerView = dialogView.findViewById(R.id.list_known_class_name);

    // Specify alert dialog is not cancelable/not ignorable
    builder.setCancelable(false);

    // Set the custom layout as alert dialog view
    builder.setView(dialogView);

    final Dialog d2 = builder.create();

    // use this setting to improve performance if you know that changes
    // in content do not change the layout size of the RecyclerView
    mRecyclerView.setHasFixedSize(true);

    // use a linear layout manager
    mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);

    // specify an adapter (see also next example)
    String[] allKnownLabels = DataHolder.getInstance().getDataset().getLabels();
    if (allKnownLabels.length == 0) {
      allKnownLabels = new String[1];
      allKnownLabels[0] = "no existing class found";
    }
    mAdapter = new MyAdapter(allKnownLabels);
    mRecyclerView.setAdapter(mAdapter);
    ItemClickSupport.addTo(mRecyclerView)
            .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
              @Override
              public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                String labelSelected = ((TextView) v).getText().toString();

                class_given.setText(labelSelected);
                d2.dismiss();
              }
            });

    return d2;

  }

  private Dialog createDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

    LayoutInflater inflater = getLayoutInflater();
    final View dialogView = inflater.inflate(R.layout.edittext_dialog, null);

    // Specify alert dialog is not cancelable/not ignorable
    builder.setCancelable(false);

    // Set the custom layout as alert dialog view
    builder.setView(dialogView);

    final Dialog d = builder.create();

    class_given = dialogView.findViewById(R.id.class_name);

    Button selectExistingClass = dialogView.findViewById(R.id.display_existing_class);
    selectExistingClass.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createSelecterClassDialog().show();
      }
    });

    Button cancel = dialogView.findViewById(R.id.dialog_negative_btn);
    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        d.dismiss();
      }
    });

    Button confirm = dialogView.findViewById(R.id.dialog_positive_btn);
    confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String nameClassOfPhotoTaken = class_given.getText().toString();
        d.dismiss();

        Intent intent = sendInfoToNextActivity();
        intent.putExtra("class_name", nameClassOfPhotoTaken);
        startActivity(intent);

      }
    });

    return d;
  }

  protected Intent sendInfoToNextActivity() {
    Intent intent = new Intent(MainActivity.this, CameraActivity.class);

    Context context = getApplicationContext();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    Integer kChosen = Integer.valueOf(prefs.getString((String) getText(R.string.kGiven), "4"));
    Integer pChosen = Integer.valueOf(prefs.getString((String) getText(R.string.pGiven), "4"));


    intent.putExtra("isTraining", isTraining);
    intent.putExtra("kChosen", kChosen);
    intent.putExtra("pChosen", pChosen);
    return intent;
  }


}
