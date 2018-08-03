package com.example.gregoire.testmodule2.Classifier;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of the vectors for the {@link TILDA} method
 */
public class FeatureVector {
  /**
   * The list of all the features with their heaviness coefficient.
   * @see FeatureSubvector
   */
  protected ArrayList<FeatureSubvector> mFeatures;

  /**
   * The number of subvector in mFeatures.
   */
  protected int mP;

  /**
   * Create a new default FeatureVector.
   * All featureSubvector sizes are almost all the same (1 additional feature at most)
   *
   * @param p number of subvector wanted
   * @param size size optimal for each subvector
   */
  public FeatureVector(int p, int size) {
    if (p <= 0) {
      throw new IllegalArgumentException("p must be strictly positive");
    }
    mP = p;
    mFeatures = new ArrayList<>();
    int sizeRemaining = size;
    //nbSubvector*sizeSubvector is the index of the beginning of the subvector nbSubvector
    for (int nbSubvector = 0; nbSubvector<p; nbSubvector++) {
      int sizeSubvector = sizeRemaining/(p-nbSubvector);
      mFeatures.add(new FeatureSubvector(sizeSubvector));
      sizeRemaining -= sizeSubvector;
    }
  }

  /**
   * Initialize the subvector from a given {@link FeatureSubvector}
   * Used to retrieve from external storage the feature vector :
   * @see TILDA#TILDA(int, int, int, ArrayList, ArrayList)
   *
   * @param index
   * @param featureSubvector
   */
  public void changeSubvector(int index, FeatureSubvector featureSubvector) throws IndexOutOfBoundsException {
    mFeatures.set(index, featureSubvector);
  }

  /**
   * Operator overloading of {@link #changeSubvector(int, FeatureSubvector)}
   * to ease the use of this method
   *
   * @param index reffering to the index of mFeatures
   * @param heavinessCoefficient
   * @param vector feature subvector
   */
  public void changeSubvector(int index, int heavinessCoefficient, List<Float> vector) throws IndexOutOfBoundsException {
    changeSubvector(index, new FeatureSubvector(heavinessCoefficient, vector));
  }

  //getter
  public int p()
  {
    return mP;
  }

  /**
   * Call to all the method first defined in {@link FeatureSubvector}
   */

  public float costTrainingFunction(int currentP, List<Float> subvector) {
    return mFeatures.get(currentP).costTrainingFunction(subvector);
  }

  public float costEvaluationFunction(int currentP, List<Float> subvector) {
    return mFeatures.get(currentP).costEvaluationFunction(subvector);
  }

  public void updateFeatureVector(int currentP, List<Float> refSubvector) {
    mFeatures.get(currentP).updateSubvector(refSubvector);
  }

  public int heavinessCoefficient(int currentP) {
    return mFeatures.get(currentP).heavinessCoefficient();
  }

  public List<Float> retrieveFeatureVectorCopy(int currentP) {
    return mFeatures.get(currentP).retrieveFeatureVectorCopy();
  }

}
