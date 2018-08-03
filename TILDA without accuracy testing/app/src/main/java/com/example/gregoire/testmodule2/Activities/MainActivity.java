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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;

import java.io.File;

import jp.wasabeef.blurry.Blurry;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

  public static String TAG = "Main Activity";

  private Dialog mDialog;
  private boolean isTraining;

  //to draw
  private Canvas mCanvas;
  private Paint mPaint = new Paint();
  private Bitmap mBitmap;
  private ImageView mImageView;
  private Rect mRect = new Rect();
  private Rect mBounds = new Rect();


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
    DataHolder.getInstance().initializeDataHolder(getAssets(), pathDataset, kChosen, pChosen, 2048);

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

  private Dialog createDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

    LayoutInflater inflater = getLayoutInflater();
    final View dialogView = inflater.inflate(R.layout.edittext_dialog, null);

    // Specify alert dialog is not cancelable/not ignorable
    builder.setCancelable(false);

    // Set the custom layout as alert dialog view
    builder.setView(dialogView);

    final Dialog d = builder.create();

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
        String nameClassOfPhotoTaken = ((EditText) dialogView.findViewById(R.id.class_name)).getText().toString();
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
