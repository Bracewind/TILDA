package com.example.gregoire.datasetbuilder.ExternalFileManager;

import android.graphics.Bitmap;

import com.example.gregoire.datasetbuilder.helpers.VectorOperation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatasetManager extends FileManager {

  private static String TAG = "DatasetManager";

  private ArrayList<File> allPathLabels;
  private ArrayList<File[]> allDataPath;
  protected File mDatasetFolder;

  public DatasetManager(File folder) {
    super();
    createDirectory(folder);
    mDatasetFolder = folder;
    try {
      retrievePastMemory();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void initializeNewClass(String className, int k, String vectorRepresentationReference) throws IOException {
    File fileToSaveVector = new File(mDatasetFolder, className);
    createDirectory(new File(mDatasetFolder, className + "/"));
    ArrayList<String> initVector = new ArrayList<String>();
    for (int index=0; index< k;index++) {
      initVector.add(vectorRepresentationReference);
    }

    saveStringArray(className, initVector);
  }

  public File saveImage(String className, String imageName, Bitmap image) throws IOException {
    File fileToSaveImage = new File(mDatasetFolder, className + File.separator + imageName + ".jpg");
    createDirectory(new File(mDatasetFolder, className));
    saveImage(image, fileToSaveImage);
    return fileToSaveImage;
  }

  public File saveVector(String className, String nameImage, ArrayList<Float> features) throws IOException {
    File fileToSaveVector = new File(mDatasetFolder, className + "/" + nameImage + ".txt");
    createDirectory(new File(mDatasetFolder, className + "/"));
    saveFloatlist(features, fileToSaveVector);
    return fileToSaveVector;
  }

  public File saveVector(String className, String nameImage, float[] features) throws IOException {
    File fileToSaveVector = new File(mDatasetFolder, className + "/" + nameImage + ".txt");
    createDirectory(new File(mDatasetFolder, className + "/"));
    saveFloatlist(features, fileToSaveVector);
    return fileToSaveVector;
  }

  public void saveStringArray(String className, List<String> vectorsRepresentation) throws IOException {
    for (int index=0; index< vectorsRepresentation.size();index++) {
      File fileToSaveVector = new File(mDatasetFolder, className + "/array"+ Integer.toString(index+1) +".txt");
      saveString(className, vectorsRepresentation.get(index), index);
    }
  }

  public void saveString(String className, String vectorRepresentation, int indexFileToSave) throws IOException {
    File fileToSaveVector = new File(mDatasetFolder, className + "/array"+ Integer.toString(indexFileToSave+1) +".txt");
    FileOutputStream stream = new FileOutputStream(fileToSaveVector);
    try {
      stream.write(vectorRepresentation.getBytes());
    } finally {
      stream.close();
    }
  }

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

  public void retrievePastMemory() {
    allPathLabels = VectorOperation.collectionToArrayList(mDatasetFolder.listFiles());
    allDataPath = new ArrayList<>();

    for (int i = 0; i< allPathLabels.size(); i++) {
      allDataPath.add(allPathLabels.get(i).listFiles());
    }
  }

  public ArrayList<String> getLabels() {
    ArrayList<String> allLabels = new ArrayList<>();
    int i = 0;
    for (File label : allPathLabels) {
      allLabels.add(label.getName());
      i+=1;
    }

    return allLabels;
  }

  //SIMPLIFIABLE
  public ArrayList<ArrayList<String>> getAllData() {
    ArrayList<ArrayList<String>> allVector = new ArrayList<>();
    for (int idClass=0;idClass<allPathLabels.size();idClass++) {
      allVector.add(new ArrayList<String>());
      for (int idVector=0;idVector<allDataPath.get(idClass).length;idVector++) {
        allVector.get(idClass).add(loadString(allDataPath.get(idClass)[idVector]));
      }
    }

    return allVector;
  }
}
