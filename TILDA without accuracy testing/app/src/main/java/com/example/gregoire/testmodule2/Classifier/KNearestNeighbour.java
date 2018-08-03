package com.example.gregoire.testmodule2.Classifier;

import com.example.gregoire.testmodule2.helpers.VectorOperation;

import java.util.ArrayList;

public class KNearestNeighbour implements ClassifierFromFeature {

  private ArrayList<String> labels;
  //vectorImage.get(i) contains feature vector for label labels.get(i)
  private ArrayList<ArrayList<ArrayList<Float>>> vectorsImage;

  public KNearestNeighbour(
          ArrayList<String> labels,
          ArrayList<ArrayList<ArrayList<Float>>> vectorsImagePreprocessed
          ) {
    this.labels = labels;
    this.vectorsImage = vectorsImagePreprocessed;

  }

  private int idLabel(String className) {
    for (int index = 0; index < labels.size(); index++) {
      if (className.equals(labels.get(index))) {
        return index;
      }
    }
    return -1;
  }

  @Override
  public void trainAlgorithm(String className, ArrayList<Float> arrayList) {
    if (!hasLearned(className))
    {
      addNewClass(className);
    }
    vectorsImage.get(idLabel(className)).add(arrayList);
  }

  @Override
  public String recognizeImage(ArrayList<Float> features)
  {
    String labelMostProbable = null;
    try {
      labelMostProbable = labels.get(0);
      float currentMin = VectorOperation.euclideanDistance(features, vectorsImage.get(0).get(0));
      for (int indexLabel = 0; indexLabel < labels.size(); indexLabel++) {
        for (int j = 0; j < vectorsImage.get(indexLabel).size(); j++) {
          Float currentDistance = VectorOperation.euclideanDistance(features, vectorsImage.get(indexLabel).get(j));
          if (currentDistance < currentMin) {
            currentMin = currentDistance;
            labelMostProbable = labels.get(indexLabel);
          }
        }
      }
    } catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }

    return labelMostProbable;
  }

  @Override
  public void addNewClass(String nameClass) {
    labels.add(nameClass);
    vectorsImage.add(new ArrayList<ArrayList<Float>>());
  }

  @Override
  public boolean hasLearned(String nameClass) {
    return false;
  }

  @Override
  public void saveTraining(String nameClass) {

  }
}
