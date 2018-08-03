package com.example.gregoire.testmodule2.ExternalFileManager;

import com.example.gregoire.testmodule2.Classifier.FeatureSubvector;
import com.example.gregoire.testmodule2.Classifier.FeatureVector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * This class represents the featureVectors in a JSON format
 */
public class JSONParser {

  private JSONArray jsonVectorRepresentation;

  /**
   * initialize the parser from the file
   *
   * @param stringRepresentation
   */
  public JSONParser(String stringRepresentation) throws IllegalArgumentException {
    if (stringRepresentation == null) {
      throw new IllegalArgumentException();
    }

    try {
      jsonVectorRepresentation = loadJSONVectorRepresentation(stringRepresentation);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * initialize the parser from the feature vector class
   *
   * @param featureVector
   */
  public JSONParser(FeatureVector featureVector) {
    try {
      jsonVectorRepresentation = loadJSONVectorRepresentation(featureVector);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * methods used to help initializing the json parser
   */

  private JSONArray loadJSONVectorRepresentation(String stringRepresentation) throws  JSONException {
    return new JSONArray(stringRepresentation);
  }

  private JSONObject loadJSONSubvectorRepresentation(int currentP, FeatureVector featureVector) throws JSONException {
    JSONObject subvectorRepresentation =  new JSONObject();

    Integer heavinessCoefficient = featureVector.heavinessCoefficient(currentP);
    List<Float> features = featureVector.retrieveFeatureVectorCopy(currentP);

    subvectorRepresentation.put("heavinessCoefficient", heavinessCoefficient);
    subvectorRepresentation.put("features", arrayToJSONArray(features));

    return subvectorRepresentation;
  }

  private JSONArray loadJSONVectorRepresentation(FeatureVector featureVector) throws  JSONException {

    JSONArray jsonVectorRepresentation = new JSONArray();
    for (int currentP = 0; currentP<featureVector.p(); currentP++) {
      JSONObject subvectorRepresentation = loadJSONSubvectorRepresentation(currentP, featureVector);
      jsonVectorRepresentation.put(subvectorRepresentation);
    }
    return jsonVectorRepresentation;
  }


  /**
   *
   *
   * @param currentP
   * @param featureVector
   * @throws JSONException
   */
  public void updateVector(int currentP, FeatureVector featureVector) throws JSONException {
    jsonVectorRepresentation.put(currentP, loadJSONSubvectorRepresentation(currentP, featureVector));
  }



  public JSONArray arrayToJSONArray(List<Float> array) throws JSONException{
    JSONArray jsonArray = new JSONArray();
    int len = array.size();
    for (int i=0;i<len;i++){
      jsonArray.put((double)array.get(i));
    }

    return jsonArray;
  }

  protected ArrayList<Float> jsonArrayToArray(JSONArray jsonArray) throws JSONException {
    ArrayList<Float> array = new ArrayList<>();
    int len = jsonArray.length();
    for (int i=0;i<len;i++){
      array.add((float) jsonArray.getDouble(i));
    }

    return array;
  }

  /**
   * return the Feature vector associated with the json representation {@link #jsonVectorRepresentation}
   * TODO: it would be good if we did not have to send p and size as parameter,
   * TODO: as it is included in the json representation
   *
   * @param p
   * @param size
   * @return
   * @throws JSONException
   */
  public FeatureVector loadVector(int p, int size) throws JSONException, IndexOutOfBoundsException {

    FeatureVector feature = new FeatureVector(p, size);
    for (int index=0;index<jsonVectorRepresentation.length();index++) {
      JSONObject subvector = (JSONObject) jsonVectorRepresentation.get(index);
      feature.changeSubvector(
              index,
              subvector.getInt("heavinessCoefficient"),
              jsonArrayToArray(subvector.getJSONArray("features"))
      );
    }

    return feature;
  }

  @Override
  public String toString() {
    return jsonVectorRepresentation.toString();
  }
}
