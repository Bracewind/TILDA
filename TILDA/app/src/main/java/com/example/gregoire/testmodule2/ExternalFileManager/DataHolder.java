package com.example.gregoire.testmodule2.ExternalFileManager;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.example.gregoire.testmodule2.Classifier.ClassifierFromFeature;
import com.example.gregoire.testmodule2.Classifier.FeatureExtractor;
import com.example.gregoire.testmodule2.Classifier.TILDA;
import com.example.gregoire.testmodule2.Classifier.TensorFlowImageClassifier;

import java.io.File;
import java.util.ArrayList;


/**
 * this class allows the application to use the objects defined here inside all the activities
 * It contains :
 * -the preference parameter k and p
 * -the classifier from feature and the feature extractor (in order to initialize them only once)
 * -the picture taken by the phone (to avoid saving it inside the external storage and deleted it 10 seconds after)
 * -the dataset object that manage the external storage of the {@link com.example.gregoire.testmodule2.Classifier.ClassifierFromFeature}
 */


public class DataHolder {

  public static String TAG = "DataHolder";

  private DatasetManager dataset;
  private static final DataHolder holder = new DataHolder();
  private Bitmap mPictureTaken;

  //return true if already initialized
  private boolean initialized = false;

  //tensorflow classifier
  private static final int INPUT_SIZE = 299;
  private static final int IMAGE_MEAN = 128;
  private static final float IMAGE_STD = 128.0f;
  private static final String INPUT_NAME = "module_apply_default/hub_input/Mul";
  private static final int NB_OUTPUT_LAST_LAYER = 2048;
  private static final String OUTPUT_NAME_SPATIAL_SQUEEZE = "module_apply_default/hub_output/feature_vector/SpatialSqueeze";

  private static final String MODEL_FILE = "file:///android_asset/testInception.pb";
  private static final String LABEL_FILE = "file:///android_asset/testInception.txt";

  FeatureExtractor mFeatureExtractor;

  //info for Classifier from feature
  int mK;
  int mP;
  ClassifierFromFeature mClassifierFromFeature;

  public void initializeDataHolder(AssetManager assetManager, File pathDatasetFolder, int k, int p, int sizeLastLayer) {
    if (!initialized) {
      mFeatureExtractor = TensorFlowImageClassifier.create(
              assetManager,
              MODEL_FILE,
              LABEL_FILE,
              INPUT_SIZE,
              IMAGE_MEAN,
              IMAGE_STD,
              INPUT_NAME,
              OUTPUT_NAME_SPATIAL_SQUEEZE);

      dataset = new DatasetManager(pathDatasetFolder);
      Pair<ArrayList<String>, ArrayList<ArrayList<JSONParser>>> data = dataset.getAllDataAndLabelInJSON();
      Log.i(TAG, data.first.toString());
      try {
        mClassifierFromFeature = new TILDA(k, p, sizeLastLayer, data.first, data.second);
      } catch (IndexOutOfBoundsException e) {
        e.printStackTrace();
        dataset.reinitializeTraining();
      }
      initialized = true;
    }
    mK = k;
    mP = p;
  }

  public static DataHolder getInstance() {
    return holder;
  }

  public DatasetManager getDataset() {
    return dataset;
  }

  /**
   * Initialize the {@link #dataset}.
   *
   * @param pathFolder
   */
  public void setPath(File pathFolder) {
    dataset = new DatasetManager(pathFolder);
  }

  public void savePictureTaken(Bitmap bitmap) {
    mPictureTaken = bitmap;
  }

  public Bitmap retrievePictureTaken() {
    return mPictureTaken;
  }

  public FeatureExtractor retrieveFeatureExtractor() { return mFeatureExtractor;}

  public ClassifierFromFeature retrieveClassifierFromFeature() { return mClassifierFromFeature; }

  public int kChosen() {return mK;}
  public int pChosen() {return mP;}

  public void changeK(int newK) {
    dataset.reinitializeTraining();
    mK = newK;
  }

  public void changeP(int newP) {
    dataset.reinitializeTraining();
    mP = newP;
  }

}