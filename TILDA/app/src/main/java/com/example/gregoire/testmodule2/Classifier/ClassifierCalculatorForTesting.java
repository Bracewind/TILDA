package com.example.gregoire.testmodule2.Classifier;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gregoire.testmodule2.Activities.TestingActivity;
import com.example.gregoire.testmodule2.helpers.VectorOperation;

import java.util.ArrayList;

/**
 * Contrary to {@link ClassifierCalculator}, it uses multiple {@link ClassifierFromFeature}
 * and return all their results
 */
public class ClassifierCalculatorForTesting extends AsyncTask<Void , Void, Void> {
  private static String TAG = "ClassifierCalculator";

  private Bitmap imageToAnalyse;
  private FeatureExtractor featureExtractor;
  private ClassifierFromFeature classifierFromFeature1;
  private ClassifierFromFeature classifierFromFeature2;
  private TestingActivity classifierCallback;
  private boolean isTraining;

  private String className;
  private String resultClassifier1;
  private String resultClassifier2;

  /**
   * @param classifierCallback the activity that will give the result to the user
   * @param image
   * @param isTraining
   * @param className
   */
  public ClassifierCalculatorForTesting(FeatureExtractor featureExtractor, ClassifierFromFeature classifierFromFeature1, ClassifierFromFeature classifierFromFeature2, TestingActivity classifierCallback, Bitmap image, boolean isTraining,
                              String className) {
    this.imageToAnalyse = image;
    this.classifierCallback = classifierCallback;
    this.isTraining = isTraining;
    this.className = className;

    //initialize tensorflow network
    this.featureExtractor = featureExtractor;
    this.classifierFromFeature1 = classifierFromFeature1;
    this.classifierFromFeature2 = classifierFromFeature2;
  }

  /**
   *
   * @param arg0
   * @return
   */
  @Override
  public Void doInBackground(Void... arg0) {

    ArrayList<Float> outputs = VectorOperation.collectionToArrayList(featureExtractor.retrieveFeature(imageToAnalyse));
    if (isTraining) {
      classifierFromFeature1.trainAlgorithm(className, outputs);
      classifierFromFeature2.trainAlgorithm(className, outputs);
    }
    else {
      try {
        resultClassifier1 = classifierFromFeature1.recognizeImage(outputs);
        resultClassifier2 = classifierFromFeature2.recognizeImage(outputs);
      } catch (NoTrainingFoundException e) {
        e.printStackTrace();
      }
      Log.i(TAG, "label found is " + resultClassifier1);
    }
    return null;
  }

  /**
   * inform the activity that the classification has been done
   *
   * @param result1
   */
  @Override
  public void onPostExecute(Void result1) {
    if (isTraining) {
      classifierCallback.onTrainFinished();
    } else {
      classifierCallback.onClassificationFinished(resultClassifier1, resultClassifier2);
    }
  }

  /**
   *
   */
  public void saveTraining() {
    classifierFromFeature1.saveTraining(className);
  }

}
