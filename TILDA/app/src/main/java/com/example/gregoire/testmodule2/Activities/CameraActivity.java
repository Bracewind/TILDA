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
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gregoire.testmodule2.Classifier.FeatureSubvector;
import com.example.gregoire.testmodule2.Classifier.FeatureVector;
import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;
import com.example.gregoire.testmodule2.ExternalFileManager.DatasetManager;
import com.example.gregoire.testmodule2.ExternalFileManager.JSONParser;
import com.example.gregoire.testmodule2.env.ImageUtils;
import com.example.gregoire.testmodule2.helpers.PermissionUtils;
import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.CameraViewImpl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.github.silvaren.easyrs.tools.Nv21Image;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * This activity is used to take a photography of the object
 */
public class CameraActivity extends AppCompatActivity {

  public static String TAG = "CameraActivity";

  private static final int PERMISSION_CODE_STORAGE = 3001;
  private static final int PERMISSION_CODE_CAMERA = 3002;

  private Toast warnPictureTaken;

  /**
   * The {@link CameraView} used in the library cameraviewplus for using the camera
   */
  CameraView cameraView;

  /**
   * definition of the buttons
   */
  View shutterEffect;
  View captureButton;
  View turnButton;

  private RenderScript rs;

  private boolean frameIsProcessing = false;

  /**
   * a bunch of property used for the tensorflow classifier
   */
  private Size mSizePreview;
  //represent the rotation of the phone
  private int mRotation;
  private static final int INPUT_NETWORK__SIZE = 224;
  private static final boolean MAINTAIN_ASPECT = true;

  /**
   *
   */
  private boolean isTraining;
  private String nameClass;

  /**
   * create the acitvity
   * @param savedInstanceState
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_camera);

    //retrieve the information given by the user
    Intent intent = getIntent();
    isTraining = intent.getBooleanExtra("isTraining", false);
    if (isTraining) {
      nameClass = intent.getStringExtra("class_name");
    }

    //initialize the listener for the buttons
    cameraView = findViewById(R.id.camera_view);
    shutterEffect = findViewById(R.id.shutter_effect);
    captureButton = findViewById(R.id.shutter);
    turnButton = findViewById(R.id.turn);
    Button backButton = findViewById(R.id.select_mode);

    captureButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          warnPictureTaken = Toast.makeText(getApplicationContext(), getString(R.string.camera_take_picture), Toast.LENGTH_LONG);
          warnPictureTaken.show();
          cameraView.takePicture();
        }
    });

    turnButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraView.switchCamera();
        }
    });

    backButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(CameraActivity.this, MainActivity.class));
      }
    });

    rs = RenderScript.create(this);
  }

  /**
   * resume the application and check for permissions
   */
  @Override
  protected void onResume() {
      super.onResume();
      if (PermissionUtils.isStorageGranted(this) && PermissionUtils.isCameraGranted(this)) {
          cameraView.start();
          setupCameraCallbacks();
      } else {
          if (!PermissionUtils.isCameraGranted(this)) {
              PermissionUtils.checkPermission(this, Manifest.permission.CAMERA,
                      PERMISSION_CODE_CAMERA);
          } else {
              PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                      PERMISSION_CODE_STORAGE);
          }
      }
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
          case PERMISSION_CODE_STORAGE:
          case PERMISSION_CODE_CAMERA:
              if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                  Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                  finish();
              }
              break;
      }
      if (requestCode != PERMISSION_CODE_STORAGE && requestCode != PERMISSION_CODE_CAMERA) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }
  }

  @Override
  protected void onPause() {
      cameraView.stop();
      super.onPause();
  }

  /**
   * Create the listeners for using easily the cameraviewplus library.
   */
  private void setupCameraCallbacks() {
    //most important listener, what to do after taken a picture
    cameraView.setOnPictureTakenListener(new CameraViewImpl.OnPictureTakenListener() {
        @Override
        public void onPictureTaken(Bitmap bitmap, int rotationDegrees) {

          CameraActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              warnPictureTaken.cancel();
              warnPictureTaken = Toast.makeText(CameraActivity.this, getString(R.string.camera_picture_taken), Toast.LENGTH_SHORT);
              warnPictureTaken.show();
            }
          });

          //transform the image to be able to feed it into the neural network
          mSizePreview = new Size(bitmap.getWidth(), bitmap.getHeight());
          mRotation = -rotationDegrees;
          bitmap = normalizeImage(bitmap);

          //it's hard to put a picture inside an intent and time consuming to save it, so we use Dataholder to send it
          DataHolder.getInstance().savePictureTaken(bitmap);

          Intent intent = new Intent(CameraActivity.this, ValidationPhotoActivity.class);
          intent.putExtra("isTraining", isTraining);
          Log.i(TAG, "value of isTraining : " + isTraining);
          intent.putExtra("photo_taken", true);
          if (isTraining) {
            intent.putExtra("class_name", nameClass);
          }
          startActivity(intent);

        }
    });
    //Other listeners
    cameraView.setOnFocusLockedListener(new CameraViewImpl.OnFocusLockedListener() {
        @Override
        public void onFocusLocked() {
            playShutterAnimation();
        }
    });
    cameraView.setOnTurnCameraFailListener(new CameraViewImpl.OnTurnCameraFailListener() {
        @Override
        public void onTurnCameraFail(Exception e) {
            Toast.makeText(CameraActivity.this, "Switch Camera Failed. Does you device has a front camera?",
                    Toast.LENGTH_SHORT).show();
        }
    });
    cameraView.setOnCameraErrorListener(new CameraViewImpl.OnCameraErrorListener() {
        @Override
        public void onCameraError(Exception e) {
            Toast.makeText(CameraActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
    cameraView.setOnFrameListener(new CameraViewImpl.OnFrameListener() {
        @Override
        public void onFrame(final byte[] data, final int width, final int height, int rotationDegrees) {
            if (frameIsProcessing) return;
            frameIsProcessing = true;
            Observable.fromCallable(new Callable<Bitmap>() {
                @Override
                public Bitmap call() {
                    return Nv21Image.nv21ToBitmap(rs, data, width, height);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Bitmap frameBitmap) {
                            if (frameBitmap != null) {
                                Log.i("onFrame", frameBitmap.getWidth() + ", " + frameBitmap.getHeight());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            frameIsProcessing = false;
                        }
                    });
        }
    });
  }

  /**
   * Add an animation when taking pictures.
   */
  private void playShutterAnimation() {
      shutterEffect.setVisibility(View.VISIBLE);
      shutterEffect.animate().alpha(0f).setDuration(300).setListener(
              new AnimatorListenerAdapter() {
                  @Override
                  public void onAnimationEnd(Animator animation) {
                      shutterEffect.setVisibility(View.GONE);
                      shutterEffect.setAlpha(0.8f);
                  }
              });
  }

  /**
   * Method used to transform the image taken by the photo into
   * a photo that can be feed inside the neural network.
   * It also take into account the orientation so that the picture is
   * in the right direction when taking a photo in landscape mode.
   *
   * @param image the image to normalize
   * @return the image normalized
   */
  protected Bitmap normalizeImage(Bitmap image) {

      int previewWidth = mSizePreview.getWidth();
      int previewHeight = mSizePreview.getHeight();

      final Display display = getWindowManager().getDefaultDisplay();
      final int screenOrientation = display.getRotation();

      Integer sensorOrientation = mRotation + screenOrientation;

      Bitmap croppedBitmap = Bitmap.createBitmap(INPUT_NETWORK__SIZE, INPUT_NETWORK__SIZE, Bitmap.Config.ARGB_8888);

      Matrix frameToCropTransform =
              ImageUtils.getTransformationMatrix(
                      previewWidth, previewHeight,
                      INPUT_NETWORK__SIZE, INPUT_NETWORK__SIZE,
                      sensorOrientation, MAINTAIN_ASPECT);

      Matrix cropToFrameTransform = new Matrix();
      frameToCropTransform.invert(cropToFrameTransform);

      final Canvas canvas = new Canvas(croppedBitmap);
      canvas.drawBitmap(image, frameToCropTransform, null);

      return croppedBitmap;
  }

}
