package com.example.gregoire.testmodule2.Classifier;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

/**
 * This interface allows you to create your own network(or method) for finding feature
 * To do that, you just need to initialize in {@link com.example.gregoire.testmodule2.ExternalFileManager.DataHolder}
 * an instance of your new class :
 * @see com.example.gregoire.testmodule2.ExternalFileManager.DataHolder#initializeDataHolder(AssetManager, int, int)
 */
public interface FeatureExtractor {

  /**
   * use this function to retrieve the features found by the (neural network)/method
   *
   * @param image taken by the camera and normalized
   * @return an array containing the features
   */
  float[] retrieveFeature(Bitmap image);

  /**
   * @return the size of the vector returned by the algorithm
   */
  long sizeLastOutput();

  void enableStatLogging(boolean logStats);

  String getStatString();

  void close();


}
