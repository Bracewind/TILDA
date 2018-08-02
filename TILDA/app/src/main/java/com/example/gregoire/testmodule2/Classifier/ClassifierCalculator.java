package com.example.gregoire.testmodule2.Classifier;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;
import com.example.gregoire.testmodule2.ExternalFileManager.DatasetManager;
import com.example.gregoire.testmodule2.ExternalFileManager.JSONParser;
import com.example.gregoire.testmodule2.helpers.VectorOperation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to execute the classifier in an other thread that the main thread
 */
public class ClassifierCalculator extends AsyncTask<Void , Void, Void> {

  private static String TAG = "ClassifierCalculator";

  private Bitmap imageToAnalyse;
  private FeatureExtractor featureExtractor;
  private ClassifierFromFeature classifierFromFeature;
  private ClassifierCallback classifierCallback;
  private boolean isTraining;

  private String className;
  private String result;

  /**
   * @param classifierCallback the activity that will give the result to the user
   * @param image
   * @param isTraining
   * @param className
   */
  public ClassifierCalculator(FeatureExtractor featureExtractor, ClassifierFromFeature classifierFromFeature, ClassifierCallback classifierCallback, Bitmap image, boolean isTraining,
                              String className) {
    this.imageToAnalyse = image;
    this.classifierCallback = classifierCallback;
    this.isTraining = isTraining;
    this.className = className;

    //initialize tensorflow network
    this.featureExtractor = featureExtractor;
    this.classifierFromFeature = classifierFromFeature;
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
        classifierFromFeature.trainAlgorithm(className, outputs);

      }
      else {
        try {
          result = classifierFromFeature.recognizeImage(outputs);
          Log.i(TAG, "label found is " + result);
        } catch (NoTrainingFoundException e) {
          classifierCallback.onNoTrainingFound();
        }
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
      classifierCallback.onClassificationFinished(result);
    }
  }

  /**
   *
   */
  public void saveTraining() {
    classifierFromFeature.saveTraining(className);
  }

}
