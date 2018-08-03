package com.example.gregoire.datasetbuilder.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VectorOperation {

  float[] mVector;

  public VectorOperation(float[] vector)
  {
    mVector = vector;
  }

  public float euclideanDistance(float[] vector)
  {
    float distance = 0;
    for (int i = 0; i<mVector.length; i++)
    {
      distance += (mVector[i] - vector[i])*(mVector[i] - vector[i]);
    }
    return distance;
  }

  public float euclideanDistance(List<Float> vector) throws IllegalArgumentException
  {
    if (mVector.length != vector.size())
    {
      throw new IllegalArgumentException("the size of the vector must be the same than the size of mVector");
    }

    float distance = 0;
    for (int i = 0; i<mVector.length; i++)
    {
      distance += (mVector[i] - vector.get(i))*(mVector[i] - vector.get(i));
    }
    return distance;
  }

  public static float euclideanDistance(List<Float> vector1, List<Float> vector2) throws IllegalArgumentException
  {
    if (vector1.size() != vector2.size())
    {
      throw new IllegalArgumentException("the size of the vector must be the same than the size of mVector");
    }

    float distance = 0;
    for (int i = 0; i<vector1.size(); i++)
    {
      distance += (vector1.get(i) - vector2.get(i))*(vector1.get(i) - vector2.get(i));
    }
    return distance;
  }

  public static ArrayList<Float> deepCopy(List<Float> array) {
    ArrayList<Float> newArray = new ArrayList<Float>();
    for (float elem : array) {
      newArray.add(new Float(elem));
    }

    return newArray;
  }

  public static ArrayList<String> collectionToArrayList(String[] array) {
    ArrayList<String> newArray = new ArrayList<>();
    for (String elem : array) {
      newArray.add(elem);
    }

    return newArray;
  }

  public static ArrayList<File> collectionToArrayList(File[] array) {
    ArrayList<File> newArray = new ArrayList<>();
    for (File elem : array) {
      newArray.add(elem);
    }

    return newArray;
  }

  public static ArrayList<Float> collectionToArrayList(float[] array) {
    ArrayList<Float> newArray = new ArrayList<>();
    for (float elem : array) {
      newArray.add(elem);
    }

    return newArray;
  }
}
