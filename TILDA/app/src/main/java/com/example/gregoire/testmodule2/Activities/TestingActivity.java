package com.example.gregoire.testmodule2.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Pair;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gregoire.testmodule2.Classifier.ClassifierCalculator;
import com.example.gregoire.testmodule2.Classifier.ClassifierCalculatorForTesting;
import com.example.gregoire.testmodule2.Classifier.ClassifierCallback;
import com.example.gregoire.testmodule2.Classifier.ClassifierFromFeature;
import com.example.gregoire.testmodule2.Classifier.KNearestNeighbour;
import com.example.gregoire.testmodule2.Classifier.TILDA;
import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;
import com.example.gregoire.testmodule2.ExternalFileManager.JSONParser;
import com.example.gregoire.testmodule2.ExternalFileManager.TestingAssetManager;
import com.example.gregoire.testmodule2.Testing.NoImageRemainingException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

public class TestingActivity extends Activity {

  private TestingAssetManager testingAssetManager;

  public static String TAG = "TestingActivity";

  private int SIZE_LAST_LAYER = 2048;

  private String currentMethod;
  private boolean isTraining;

  private Bitmap mCurrentImage;
  private String mCurrentLabel;

  private long timeBeforeExec;
  private long timeAfterExec;
  private int meanTime;

  //stats for testing
  private ClassifierFromFeature classifierFromFeature1;
  private ClassifierFromFeature classifierFromFeature2;
  private float mAccuracyClassifier1;
  private float mAccuracyClassifier2;
  private int mNbClassified;

  //information for displaying progress bar
  private int mNbTotalImage;
  private int nbImageAlreadySeen;
  private ProgressBar mBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_testing);

    currentMethod = "TILDA";
    nbImageAlreadySeen = 0;

    mBar = findViewById(R.id.determinateBar);
    mBar.setProgress(0);

    DataHolder dataHolder = DataHolder.getInstance();
    classifierFromFeature1 = new TILDA(
            dataHolder.kChosen(),
            dataHolder.pChosen(),
            SIZE_LAST_LAYER,
            new ArrayList<String>(),
            new ArrayList<ArrayList<JSONParser>>()
    );

    classifierFromFeature2 = new KNearestNeighbour(new ArrayList<String>(), new ArrayList<ArrayList<ArrayList<Float>>>());

    try {
      testingAssetManager = new TestingAssetManager(getAssets());
      mNbTotalImage = testingAssetManager.nbImageInDataset();
      Pair<String, Bitmap> firstData = testingAssetManager.firstImage();
      mCurrentLabel = firstData.first;
      mCurrentImage = firstData.second;
      isTraining = testingAssetManager.trainingDataRemains();

      timeBeforeExec = System.currentTimeMillis();

      launchClassifier();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void launchClassifier() {
    ClassifierCalculatorForTesting classifierCalculator = new ClassifierCalculatorForTesting(
            DataHolder.getInstance().retrieveFeatureExtractor(),
            classifierFromFeature1,
            classifierFromFeature2,
            this,
            mCurrentImage,
            isTraining,
            mCurrentLabel
    );

    classifierCalculator.execute();

    nbImageAlreadySeen += 1;

    mBar.setProgress(1000*nbImageAlreadySeen/mNbTotalImage);
  }

  private void retrieveData() throws NoImageRemainingException {
    try {
      Pair<String, Bitmap> nextTrainingData = testingAssetManager.nextImage();
      mCurrentLabel = nextTrainingData.first;
      mCurrentImage = nextTrainingData.second;
      isTraining = testingAssetManager.trainingDataRemains();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void onClassificationFinished(String labelFoundFor1, String labelFoundFor2) {
    try {
      int successClassifier1 = 0;
      int successClassifier2 = 0;
      if (labelFoundFor1.equals(mCurrentLabel)) {
        successClassifier1 = 1;
      }
      if (labelFoundFor2.equals(mCurrentLabel)) {
        successClassifier2 = 1;
      }

      Log.i(TAG, "success : " + successClassifier1 + " label found : " + labelFoundFor1 + " real label : " + mCurrentLabel);

      mAccuracyClassifier1 = (mNbClassified * mAccuracyClassifier1 + successClassifier1) / (mNbClassified + 1);
      mAccuracyClassifier2 = (mNbClassified * mAccuracyClassifier2 + successClassifier2) / (mNbClassified + 1);
      mNbClassified += 1;

      Log.i(TAG, "label : " + mCurrentLabel + " file : see above");

      retrieveData();

      //calculate mean time
      timeRemaining();

      launchClassifier();


    } catch (NoImageRemainingException e) {
      TextView accuracyDisplayFor1 = findViewById(R.id.display_accuracy_for_1);
      TextView accuracyDisplayFor2 = findViewById(R.id.display_accuracy_for_2);
      accuracyDisplayFor1.setText("the accuracy of the tilda network is " + mAccuracyClassifier1);
      accuracyDisplayFor2.setText("the accuracy of the knn network is " + mAccuracyClassifier2);
    }

  }


  public void onTrainFinished() {
    try {
      retrieveData();
      if (!classifierFromFeature1.hasLearned(mCurrentLabel)) {
        classifierFromFeature1.addNewClass(mCurrentLabel);
      }

      if (!classifierFromFeature2.hasLearned(mCurrentLabel)) {
        classifierFromFeature2.addNewClass(mCurrentLabel);
      }

      timeRemaining();

      launchClassifier();

      Log.i(TAG, "IN ON TRAIN !!!");

    } catch (NoImageRemainingException e) {
      e.printStackTrace();
    }

  }

  public void timeRemaining() {
    timeAfterExec = System.currentTimeMillis();
    long delay = timeBeforeExec - timeAfterExec;

    meanTime = (int) abs((mNbClassified*meanTime+delay)/(mNbClassified+1));
    int timeRemaining = meanTime*(mNbTotalImage-nbImageAlreadySeen);

    String timeRemainingMessage = String.format("%02d min, %02d sec",
            TimeUnit.MILLISECONDS.toMinutes(timeRemaining),
            TimeUnit.MILLISECONDS.toSeconds(timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeRemaining))
    );


    TextView textView = findViewById(R.id.total_time_approximated);
    textView.setText("time until end : " + timeRemainingMessage);
    timeBeforeExec = System.currentTimeMillis();
  }
}
