package com.example.gregoire.testmodule2.Classifier;

import android.util.Log;

import com.example.gregoire.testmodule2.ExternalFileManager.DataHolder;
import com.example.gregoire.testmodule2.ExternalFileManager.DatasetManager;
import com.example.gregoire.testmodule2.ExternalFileManager.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is the one recognizing the object
 */
public class TILDA implements ClassifierFromFeature {

  private static String TAG = "Tilda";

  /**
   * allFeatures.get(i) contains feature vectors of label labels.get(i)
   */
  ArrayList<ArrayList<FeatureVector>> allFeatures;
  ArrayList<String> labels;

  //contains the loaded feature representation in JSON
  ArrayList<ArrayList<JSONParser>> featureJSONRepresentation;


  int mSize;

  protected int mK;
  protected int mP;

  public TILDA(
          int k,
          int p,
          int size,
          ArrayList<String> labels,
          ArrayList<ArrayList<JSONParser>> featureJSONRepresentation
          ) throws IndexOutOfBoundsException
  {
    mK = k;
    mP = p;
    mSize = size;
    allFeatures = new ArrayList<>();

    this.labels = labels;
    this.featureJSONRepresentation = featureJSONRepresentation;
    try {
      for (int idClass = 0; idClass < labels.size(); idClass++) {
        allFeatures.add(new ArrayList<FeatureVector>());
        for (int currentK = 0; currentK < mK; currentK++) {
          JSONParser vectorJSONRepresentation = featureJSONRepresentation.get(idClass).get(currentK);


          allFeatures.get(idClass).add(vectorJSONRepresentation.loadVector(p, size));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  /**
   * find the index of the string label in {@link #labels}
   *
   * @param nameClass
   * @return the id of the class in {@link #labels}
   * @throws LabelNotFoundException
   */
  public int idLabel(String nameClass) {
    for (int index = 0; index < labels.size(); index++) {
      if (nameClass.equals(labels.get(index))) {
        return index;
      }
    }
    return -1;
  }

  /**
   * Retrieve the pth subvector in constant time.
   * Use {@link ArrayList#subList(int, int)} for constant time "copy".
   *
   * @param subvectorP
   * @param featureVectorLabelized
   * @return
   */
  public List<Float> retrieveSubvector(int subvectorP, ArrayList<Float> featureVectorLabelized) {
    int currentFirstIndex = 0;

    int sizeSubvector = 0;
    int sizeRemaining = featureVectorLabelized.size();
    for (int currentP = 0; currentP<subvectorP ;currentP++) {
      sizeSubvector = sizeRemaining/(mP-currentP);
      currentFirstIndex += sizeSubvector;
      sizeRemaining -= sizeSubvector;
    }

    List<Float> currentFeatureLabelizedSubvector = featureVectorLabelized.subList(
            currentFirstIndex, sizeRemaining/(mP-subvectorP) + currentFirstIndex
    );

    return currentFeatureLabelizedSubvector;
  }

  /**
   * calculate the new vectors by adding the new image of the object into the dataset
   *
   * @param nameClass label of the new image given by the user
   * @param featureVectorLabelized features of the new image found by the {@link FeatureExtractor}
   * @return the new vectors in a JSON format
   */
  public ArrayList<JSONParser> calculateNewVectorsTraining(String nameClass, ArrayList<Float> featureVectorLabelized) {
    int idLabel = idLabel(nameClass);
    ArrayList<FeatureVector> featuresVector = allFeatures.get(idLabel);
    ArrayList<JSONParser> featuresRepresentation = featureJSONRepresentation.get(idLabel);

    for (int currentP = 0; currentP < mP; currentP++) {

      //retrieve the subvector associated with currentP
      List<Float> currentFeatureLabelizedSubvector = retrieveSubvector(currentP, featureVectorLabelized);

      //find the featurevector that minimize the cost for this currentP
      FeatureVector minFeatureVector = featuresVector.get(0);
      int indexMinFeatureVector = 0;
      float currentCostMin = minFeatureVector.costTrainingFunction(currentP, currentFeatureLabelizedSubvector);
      for (int currentK = 0; currentK < mK; currentK++) {
        FeatureVector featureVector = featuresVector.get(currentK);
        float cost = featureVector.costTrainingFunction(currentP, currentFeatureLabelizedSubvector);
        if (cost < currentCostMin) {
          currentCostMin = cost;
          minFeatureVector = featureVector;
          indexMinFeatureVector = currentK;
        }
      }

      //update the feature vector found with the new weights
      minFeatureVector.updateFeatureVector(currentP, currentFeatureLabelizedSubvector);
      try {
        //update the json representation
        featuresRepresentation.get(indexMinFeatureVector).updateVector(currentP, minFeatureVector);
      } catch (JSONException e) {
        e.printStackTrace();
      }

    }
    return featuresRepresentation;
  }

  /**
   * @see #calculateNewVectorsTraining(String, ArrayList)
   * Do almost the same thing, but save the json into the phone.
   *
   * @param nameClass name of the class for the training
   * @param featureVectorLabelized features found by the {@link FeatureExtractor}
   */
  @Override
  public void trainAlgorithm(String nameClass, ArrayList<Float> featureVectorLabelized) {

    //TODO create an other class to deal with external storage
    if (!hasLearned(nameClass))
    {
      addNewClass(nameClass);
    }

    calculateNewVectorsTraining(nameClass, featureVectorLabelized);

  }

  @Override
  public void saveTraining(String nameClass) {
    int idLabel = idLabel(nameClass);
    ArrayList<JSONParser> featuresRepresentation = featureJSONRepresentation.get(idLabel);

    //transform json to string
    ArrayList<String> vectorStringRepresentation = new ArrayList<>();
    for (JSONParser vector : featuresRepresentation) {
      vectorStringRepresentation.add(vector.toString());
    }

    //save the strings
    DatasetManager datasetManager = DataHolder.getInstance().getDataset();

    try {
      datasetManager.saveStringArray(nameClass, vectorStringRepresentation);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * find the label from the feature vector given in input
   *
   * @param featureVectorLabelized features found by the {@link FeatureExtractor}
   * @return the label the method has recognized
   */
  @Override
  public String recognizeImage(ArrayList<Float> featureVectorLabelized)
          throws NoTrainingFoundException
  {
    if (labels == null || labels.size() == 0) {
      throw new NoTrainingFoundException();
    }

    HashMap<String, Integer> labelsRecognized = new HashMap<>();
    for (int currentP = 0; currentP<mP;currentP++) {

      //retrieve subvector and cost of the first subvector with the feature vector found
      List<Float> currentFeatureLabelizedSubvector = retrieveSubvector(currentP, featureVectorLabelized);
      float currentCostMin = allFeatures.get(0).get(0).costEvaluationFunction(currentP, currentFeatureLabelizedSubvector);

      //put function to retrieve label of the minimum cost
      int minIdLabel = 0;
      for (int idClass = 0; idClass<labels.size(); idClass++) {

        ArrayList<FeatureVector> featuresVector = allFeatures.get(idClass);
        for (int currentK = 0; currentK < mK; currentK++) {
          FeatureVector featureVector = featuresVector.get(currentK);
          float cost = featureVector.costEvaluationFunction(currentP, currentFeatureLabelizedSubvector);
          if (cost < currentCostMin) {
            currentCostMin = cost;
            minIdLabel = idClass;
          }
        }
      }

      //change the hashmap to take into account that we have found the label one more time
      String label = labels.get(minIdLabel);

      //retrieve number of times the label has been seen and add one
      int newValue = 1;
      if (labelsRecognized.containsKey(label)) {
        newValue += labelsRecognized.get(label);
      }

      labelsRecognized.put(label, newValue);
    }

    //find the label that appeared most of the time
    int maxValue = 0;
    String labelImage = null;
    for (HashMap.Entry labelFound : labelsRecognized.entrySet()) {
      int nbAppearance = (Integer) labelFound.getValue();
      if (nbAppearance > maxValue) {
        labelImage = (String) labelFound.getKey();
        maxValue = nbAppearance;
      }
    }

    return labelImage;
  }

  @Override
  public void addNewClass(String nameClass)
  {
    labels.add(nameClass);

    ArrayList<JSONParser> newJSONRepresentationArray = new ArrayList<>();
    ArrayList<FeatureVector> newFeatureVectorArray = new ArrayList<>();
    for (int currentK = 0; currentK < mK; currentK++)
    {
      FeatureVector featureVector = new FeatureVector(mP, mSize);
      newFeatureVectorArray.add(featureVector);
      newJSONRepresentationArray.add(new JSONParser(featureVector));
    }
    allFeatures.add(newFeatureVectorArray);
    featureJSONRepresentation.add(newJSONRepresentationArray);
  }

  @Override
  public boolean hasLearned(String className)
  {
    return labels.contains(className);
  }
}
