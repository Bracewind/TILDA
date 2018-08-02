package com.example.gregoire.testmodule2.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gregoire.testmodule2.Camera.CameraException;
import com.example.gregoire.testmodule2.Classifier.ClassifierCalculator;
import com.example.gregoire.testmodule2.Classifier.ClassifierFromFeature;
import com.example.gregoire.testmodule2.Classifier.FeatureVector;
import com.example.gregoire.testmodule2.Classifier.TILDA;
import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;
import com.example.gregoire.testmodule2.ExternalFileManager.DatasetManager;
import com.example.gregoire.testmodule2.ExternalFileManager.JSONParser;

import java.io.IOException;

/**
 *
 */
public class ValidationPhotoActivity extends Activity {

  private boolean isTraining;
  private String className;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_validate_image);

    Bitmap image = DataHolder.getInstance().retrievePictureTaken();

    Intent intent = getIntent();
    isTraining = intent.getBooleanExtra("isTraining", false);
    if (isTraining) {
      className = intent.getStringExtra("class_name");
    }

    ImageView mImg;
    mImg = findViewById(R.id.display_image_analysed);
    mImg.setImageBitmap(image);

    try {
      if (getIntent().getBooleanExtra("photo_taken", false)) {

      } else {
        throw new CameraException.PhotoNotAvailableException("photo not in data holder class");
      }
    } catch (CameraException.PhotoNotAvailableException e) {
      e.printStackTrace();
    }

    Button confirmPicture = findViewById(R.id.confirm_picture_button);
    confirmPicture.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(ValidationPhotoActivity.this, ClassifierActivity.class);
        intent.putExtra("isTraining", isTraining);
        if (isTraining) {
          intent.putExtra("class_name", className);
        }

        startActivity(intent);
      }
    });

    Button backToPhoto = findViewById(R.id.back_to_photo);
    backToPhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(ValidationPhotoActivity.this, CameraActivity.class);
        intent.putExtra("isTraining", isTraining);
        if (isTraining) {
          intent.putExtra("class_name", className);
        }

        startActivity(intent);
      }
    });

    Button backToMainMenu = findViewById(R.id.back_to_start);
    backToMainMenu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(ValidationPhotoActivity.this, MainActivity.class));
      }
    });
  }
}
