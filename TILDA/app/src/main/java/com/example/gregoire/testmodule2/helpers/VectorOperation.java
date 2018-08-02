package com.example.gregoire.testmodule2.helpers;

import com.example.gregoire.testmodule2.ExternalFileManager.JSONParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A bunch of method used to ease the use of ArrayList as mathematical vector.
 * It also contains ways to convert a simple array into an ArrayList.
 */
final public class VectorOperation {

  private VectorOperation() {}

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

  public static ArrayList<String> collectionToArrayList(String[] array) {
    ArrayList<String> newArray = new ArrayList<>();
    for (String elem : array) {
      newArray.add(elem);
    }

    return newArray;
  }
}
