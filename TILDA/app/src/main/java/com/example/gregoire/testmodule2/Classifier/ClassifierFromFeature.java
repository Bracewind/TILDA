package com.example.gregoire.testmodule2.Classifier;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * This interface allows you to create your own network(or method) for getting the label
 * from the feature given by the {@link FeatureExtractor}.
 * To do that, you just need to initialize in {@link ClassifierCalculator} an instance of your new class :
 * @see ClassifierCalculator#ClassifierCalculator(FeatureExtractor, ClassifierFromFeature, ClassifierCallback, Bitmap, boolean, String)
 * you might need more information from the user.
 */
public interface ClassifierFromFeature {

  /**
   * @param className the label
   * @return whether or not the {@link ClassifierFromFeature} know the label
   */
  boolean hasLearned(String className);

  /**
   * @param nameClass the name of the class that the {@link ClassifierFromFeature}
   *                  should be able to recognize from now on
   */
  void addNewClass(String nameClass);

  /**
   * Function called to classify the image.
   *
   * @param features the feature recognized by the {@link FeatureExtractor}.
   * @return the label found by the method
   */
  String recognizeImage(ArrayList<Float> features) throws NoTrainingFoundException;

  /**
   * Train the method or network to classify the object.
   *
   * @param nameClass name of the class for the training
   * @param featureVectorLabelized features found by the {@link FeatureExtractor}
   */
  void trainAlgorithm(String nameClass, ArrayList<Float> featureVectorLabelized);

  /**
   *
   * @param nameClass
   */
  void saveTraining(String nameClass);
}
