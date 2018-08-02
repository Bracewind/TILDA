package com.example.gregoire.testmodule2.Classifier;

import com.example.gregoire.testmodule2.helpers.VectorOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of the subvectors for the {@link TILDA} method
 */
public class FeatureSubvector {
  /**
   * represents the value of a part of the features found by the {@link FeatureExtractor}
   */
  protected ArrayList<Float> mVector;

  /**
   * represents the number of times this feature subvector has been updated
   */
  protected int mHeavinessCoefficient;

  /**
   * initialize with 0 the subvector
   *
   * @param size
   */
  public FeatureSubvector(int size) {
    mHeavinessCoefficient = 0;
    mVector = new ArrayList<>();
    for (int index=0;index<size;index++) {
      mVector.add(new Float(0));
    }
  }

  /**
   * Create a feature subvector from the heaviness coefficient and the vector
   * Mainly used for retrieving the saved features
   *
   * @param heavinessCoefficient
   * @param vector
   */
  public FeatureSubvector(int heavinessCoefficient, List<Float> vector) {
    mHeavinessCoefficient = heavinessCoefficient;
    mVector = VectorOperation.deepCopy(vector);
  }

  /**
   *
   * @return the heaviness coefficient of the subvector
   */
  public int heavinessCoefficient() {
    return mHeavinessCoefficient;
  }

  /**
   * The function used for determining the distance between two feature subvector
   * when it should find the label of the object
   *
   * @param vector a subvector representing a part of the features
   * @return the distance between the subvector and the subvector given in parameters
   */
  public float costEvaluationFunction(List<Float> vector) {
    if (mVector.size() != vector.size()) {
      throw new IllegalArgumentException("the subvector must have the same length than the feature subvector\n" +
                    "the size of the attribute is " +mVector.size() + " while the argument size is " + vector.size());
    }
    float distance = VectorOperation.euclideanDistance(mVector, vector);
    return distance;
  }

  /**
   * The function used for determining the distance between two feature subvector
   * when it should train the {@link TILDA} method
   *
   * @param vector a subvector representing a part of the features
   * @return the distance between the subvector and the subvector given in parameters
   */
  public float costTrainingFunction(List<Float> vector) {
    if (mVector.size() != vector.size()) {
      throw new IllegalArgumentException("the subvector must have the same length than the feature subvector\n" +
      "the size of the attribute is " +mVector.size() + " while the argument size is " + vector.size());
    }
    float distance = VectorOperation.euclideanDistance(mVector, vector);
    return distance * mHeavinessCoefficient;
  }

  /**
   * do the mean between all the previous subvector found for this instance and the new
   * subvector found for this image.
   * Only used for training the method.
   *
   * @param refSubvector
   */
  public void updateSubvector(List<Float> refSubvector) {
    for (int index=0; index<mVector.size(); index++) {
      Float newValue = (mVector.get(index)*mHeavinessCoefficient
              + refSubvector.get(index)) /(mHeavinessCoefficient+1);
      mVector.set(index, newValue);
    }
    mHeavinessCoefficient += 1;
  }

  /**
   *
   * @return the number of features managed by this subvector
   */
  public int size() {
    return mVector.size();
  }

  /**
   * To avoid a modification of the subvector without using {@link #updateSubvector(List)}
   * this function do a deep copy of the vector
   *
   * @return a deep copy of the feature subvector
   */
  public List<Float> retrieveFeatureVectorCopy() {
    return VectorOperation.deepCopy(mVector);
  }
}
