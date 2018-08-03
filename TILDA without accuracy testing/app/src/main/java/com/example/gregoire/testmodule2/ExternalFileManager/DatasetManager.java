package com.example.gregoire.testmodule2.ExternalFileManager;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.example.gregoire.testmodule2.helpers.VectorOperation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manage the external storage used by the
 * {@link com.example.gregoire.testmodule2.Classifier.ClassifierFromFeature}
 */
public class DatasetManager extends FileManager {

  private static String TAG = "DatasetManager";

  protected File mDatasetFolder;

  public DatasetManager(File folder) {
    super();
    createDirectory(folder);
    mDatasetFolder = folder;
  }

  /**
   * Used to initialize a new class when it does not already exist.
   * Create a new directory and add k feature vector having the same value than the
   * reference inside this new directory
   *
   * @param className new label fiven by the user
   * @param k number of feature vector
   * @param vectorRepresentationReference
   * @throws IOException
   */
  public void initializeNewClass(String className, int k, String vectorRepresentationReference) throws IOException {
    createDirectory(new File(mDatasetFolder, className + "/"));
    ArrayList<String> initVector = new ArrayList<String>();
    for (int index=0; index< k;index++) {
      initVector.add(vectorRepresentationReference);
    }

    saveStringArray(className, initVector);
  }

  public File saveImage(String className, String imageName, Bitmap image) throws IOException{
    File fileToSaveImage = new File(mDatasetFolder, className + "/image/" + imageName + ".jpg");
    createDirectory(new File(mDatasetFolder, className + "/image/"));
    saveImage(image, fileToSaveImage);
    return fileToSaveImage;
  }

  public File saveVector(String className, String nameImage, ArrayList<Float> features) throws IOException{
    File fileToSaveVector = new File(mDatasetFolder, className + "/" + nameImage + ".txt");
    createDirectory(new File(mDatasetFolder, className + "/"));
    saveFloatlist(features, fileToSaveVector);
    return fileToSaveVector;
  }

  public File saveVector(String className, String nameImage, float[] features) throws IOException{
    File fileToSaveVector = new File(mDatasetFolder, className + "/" + nameImage + ".txt");
    createDirectory(new File(mDatasetFolder, className + "/"));
    saveFloatlist(features, fileToSaveVector);
    return fileToSaveVector;
  }

  /**
   * save the JSONrepresentations of the vectors (given as a string)
   * inside the directory referring to the label
   *
   * @param className name of the label
   * @param vectorsRepresentation list of string to save
   * @throws IOException
   */
  public void saveStringArray(String className, List<String> vectorsRepresentation) throws IOException {
    for (int index=0; index< vectorsRepresentation.size();index++) {
      File fileToSaveVector = new File(mDatasetFolder, className);
      createDirectory(fileToSaveVector);
      saveString(className, vectorsRepresentation.get(index), index);
    }
  }

  /**
   * save the string representation of the vector inside the
   *
   * @param className the label given by the user
   * @param vectorRepresentation the string representation of the feature vector
   * @param indexFileToSave the index of the array that will be replaced
   * @throws IOException
   */
  public void saveString(String className, String vectorRepresentation, int indexFileToSave) throws IOException {
    File fileToSaveVector = new File(mDatasetFolder, className + "/array"+ Integer.toString(indexFileToSave+1) +".txt");
    FileOutputStream stream = new FileOutputStream(fileToSaveVector);
    try {
      stream.write(vectorRepresentation.getBytes());
    } finally {
      stream.close();
    }
  }

  /**
   * reinitialize by deleting all the feature vectors
   */
  public void reinitializeTraining() {
    deleteAllContentInFolder(mDatasetFolder);
  }

  protected ArrayList<ArrayList<Float>> allImagesWithLabel(File folderLabel) throws IOException {

    ArrayList<ArrayList<Float>> vectorImagesLabeled = new ArrayList<ArrayList<Float>>();
    createDirectory(folderLabel);
    File[] filesLabeled = folderLabel.listFiles();

    for (File file : filesLabeled) {
      vectorImagesLabeled.add(loadFloatList(file));
    }

    return vectorImagesLabeled;
  }

  /**
   * It gives all the feature vector as follows:
   * allVector.get(i) contains all the feature vectors of labels.get(i)
   *
   * @return a pair containing :
   * 1. all the labels
   * 2. all the feature vectors of all the labels
   */
  public Pair<ArrayList<String>, ArrayList<ArrayList<JSONParser>>> getAllDataAndLabelInJSON() {
    //retrieve all labels folder
    File[] allPathLabels = mDatasetFolder.listFiles();

    ArrayList<String> allLabels = new ArrayList<>();
    ArrayList<ArrayList<JSONParser>> allVector = new ArrayList<>();
    try {
      for (int idClass = 0; idClass < allPathLabels.length; idClass++) {
        //add label in array
        allLabels.add(allPathLabels[idClass].getName());

        //retrieve all the json representation from this folder
        allVector.add(new ArrayList<JSONParser>());
        File[] dataPathForLabel = allPathLabels[idClass].listFiles();
        for (int idVector = 0; idVector < dataPathForLabel.length; idVector++) {
          allVector.get(idClass).add(new JSONParser(loadString(dataPathForLabel[idVector])));
        }
      }
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      deleteAllContentInFolder(mDatasetFolder);
    }

    return new Pair<>(allLabels, allVector);
  }
}
