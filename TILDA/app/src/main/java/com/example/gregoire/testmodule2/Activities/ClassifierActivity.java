package com.example.gregoire.testmodule2.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gregoire.testmodule2.Camera.CameraException;
import com.example.gregoire.testmodule2.Classifier.ClassifierCalculator;
import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;
import com.example.gregoire.testmodule2.Classifier.ClassifierCallback;

import java.io.File;

/**
 * This activity is used to display the result of the classification
 */
public class ClassifierActivity extends Activity implements ClassifierCallback {

  private ClassifierCalculator classifierCalculator;

  public static String TAG = "ClassifierActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_display_classifier);

    System.out.println("Submitting Task ...");

    //retrieve data
    Intent intent = getIntent();
    final boolean isTraining = intent.getBooleanExtra("isTraining", false);
    final String className = intent.getStringExtra("class_name");
    final int k = DataHolder.getInstance().kChosen();
    final int p = DataHolder.getInstance().pChosen();

    //do the classification

    Bitmap image = DataHolder.getInstance().retrievePictureTaken();
    classifierCalculator = new ClassifierCalculator(
            DataHolder.getInstance().retrieveFeatureExtractor(),
            DataHolder.getInstance().retrieveClassifierFromFeature(),
            this,
            image,
            isTraining,
            className
    );

    classifierCalculator.execute();

    //initialize listeners
    Button backToPhoto = findViewById(R.id.back_to_photo);
    backToPhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(ClassifierActivity.this, CameraActivity.class);
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
        startActivity(new Intent(ClassifierActivity.this, MainActivity.class));
      }
    });

  }

  /**
   * display the result found by the classifier
   *
   * @param result the label found by the classifier
   */
  @Override
  public void onClassificationFinished(String result) {

    System.out.println("Task is completed, let's check result");
    try {
      TextView recog = findViewById(R.id.label_found);
      recog.setVisibility(View.VISIBLE);
      recog.setText(result);

      ProgressBar progressBar = findViewById(R.id.progress_bar);
      progressBar.setVisibility(View.GONE);
    } catch (Exception e) {
      e.printStackTrace();
    }

    Log.i(TAG, "result found");

  }

  /**
   * save the training
   * inform the user that the training has been done
   */
  public void onTrainFinished() {

    classifierCalculator.saveTraining();

    TextView recog = findViewById(R.id.label_found);
    recog.setText("the training has been done");
    recog.setVisibility(View.VISIBLE);

    ProgressBar progressBar = findViewById(R.id.progress_bar);
    progressBar.setVisibility(View.GONE);
    Log.i(TAG, "saved finished");

  }

  /**
   * inform the user he has not trained the classifier yet
   */
  public void onNoTrainingFound() {
    TextView recog = findViewById(R.id.label_found);
    recog.setText("Error : no training dataset found, please train the network at least once");
    recog.setVisibility(View.VISIBLE);

    ProgressBar progressBar = findViewById(R.id.progress_bar);
    progressBar.setVisibility(View.GONE);
  }

}
