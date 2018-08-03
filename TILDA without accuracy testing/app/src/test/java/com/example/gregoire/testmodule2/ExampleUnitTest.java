package com.example.gregoire.testmodule2;

import com.example.gregoire.testmodule2.Classifier.FeatureExtractor;
import com.example.gregoire.testmodule2.Classifier.FeatureSubvector;
import com.example.gregoire.testmodule2.Classifier.FeatureVector;
import com.example.gregoire.testmodule2.Classifier.NoTrainingFoundException;
import com.example.gregoire.testmodule2.Classifier.TILDA;
import com.example.gregoire.testmodule2.ExternalFileManager.JSONParser;

import org.json.JSONException;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
  @Test
  public void addition_isCorrect() {
    assertEquals(4, 2 + 2);
  }

  protected void assertArrayEquals(List<Float> arrayList1, List<Float> arrayList2) {
    assertEquals("different sizes", arrayList1.size(), arrayList2.size());
    for (int index=0; index<arrayList1.size();index++) {
      assertEquals(arrayList1.get(index), arrayList2.get(index), 0.00001);
    }
  }

  @Test
  public void ParseBasicJSON() {
    String basicJSON = "[" +
            "{\"heavinessCoefficient\":3," +
            "\"features\":[0.2,0.4,0.8,0.9]" +
            "}," +
            "{\"heavinessCoefficient\":6," +
            "\"features\":[0.5,0.1,0.2,0.7785]" +
            "}" +
            "]";

    JSONParser jsonParser = new JSONParser(basicJSON);
    try {
      FeatureVector featureVector = jsonParser.loadVector(2, 8);
      assertEquals(3, featureVector.heavinessCoefficient(0));
      assertEquals(6, featureVector.heavinessCoefficient(1));

      List<Float> verifList= new ArrayList<>();
      verifList.add((float) 0.2);
      verifList.add((float) 0.4);
      verifList.add((float) 0.8);
      verifList.add((float) 0.9);
      assertArrayEquals(verifList ,featureVector.retrieveFeatureVectorCopy(0));

      List<Float> verifList2= new ArrayList<>();
      verifList2.add((float) 0.5);
      verifList2.add((float) 0.1);
      verifList2.add((float) 0.2);
      verifList2.add((float) 0.7785);
      assertArrayEquals(verifList2 ,featureVector.retrieveFeatureVectorCopy(1));

      assertEquals(basicJSON, jsonParser.toString());
    } catch (JSONException e) {

    }

  }

  @Test
  public void testFeatureSubvector() {
    ArrayList<Float> arrayList = new ArrayList<>();
    arrayList.add((float) 0.2);
    arrayList.add((float) 0.4);
    arrayList.add((float) 0.8);
    arrayList.add((float) 0.9);

    ArrayList<Float> identity = new ArrayList<>();
    identity.add((float) 1);
    identity.add((float) 1);
    identity.add((float) 1);
    identity.add((float) 1);

    FeatureSubvector feature = new FeatureSubvector(4, arrayList);
    assertEquals(1.05, feature.costEvaluationFunction(identity), 0.00001);
    assertEquals(4.20, feature.costTrainingFunction(identity), 0.00001);


    feature.updateSubvector(identity);

    ArrayList<Float> verifArray = new ArrayList<>();
    verifArray.add((float) 0.36);
    verifArray.add((float) 0.52);
    verifArray.add((float) 0.84);
    verifArray.add((float) 0.92);
    assertArrayEquals(verifArray, feature.retrieveFeatureVectorCopy());

  }

  @Test
  public void testFeatureVector() {
    ArrayList<Float> arrayList = new ArrayList<>();
    arrayList.add((float) 0.2);
    arrayList.add((float) 0.4);
    arrayList.add((float) 0.8);
    arrayList.add((float) 0.9);

    ArrayList<Float> identity = new ArrayList<>();
    identity.add((float) 1);
    identity.add((float) 1);
    identity.add((float) 1);
    identity.add((float) 1);

    ArrayList<Float> nullArray = new ArrayList<>();
    nullArray.add((float) 0);
    nullArray.add((float) 0);
    nullArray.add((float) 0);
    nullArray.add((float) 0);

    FeatureVector feature = new FeatureVector(2, 4);

    assertArrayEquals(nullArray.subList(0, 2), feature.retrieveFeatureVectorCopy(0));
    assertArrayEquals(nullArray.subList(2, nullArray.size()), feature.retrieveFeatureVectorCopy(1));
    assertEquals(0, feature.heavinessCoefficient(0));

    feature.changeSubvector(0, 4, identity);
    assertEquals(4, feature.heavinessCoefficient(0));
    assertArrayEquals(identity, feature.retrieveFeatureVectorCopy(0));


  }

  @Test
  public void testJSONParser() {
    FeatureVector featureVectorReference = new FeatureVector(2, 4);
    JSONParser jsonParserReference = new JSONParser(featureVectorReference);
    jsonParserReference.toString();
    assertEquals("[{" +
            "\"heavinessCoefficient\":0," +
            "\"features\":[0,0]" +
            "}," +
            "{" +
            "\"heavinessCoefficient\":0," +
            "\"features\":[0,0]" +
            "}]",
            jsonParserReference.toString());
  }

  @Test
  public void testTILDA() {

    ArrayList<String> labels = new ArrayList<>();
    labels.add("Clavier");

    JSONParser features = new JSONParser("[{" +
            "\"heavinessCoefficient\":0," +
            "\"features\":[0,0]" +
            "}," +
            "{" +
            "\"heavinessCoefficient\":0," +
            "\"features\":[0,0]" +
            "}]");

    JSONParser features2 = new JSONParser("[{" +
            "\"heavinessCoefficient\":1," +
            "\"features\":[0,1]" +
            "}," +
            "{" +
            "\"heavinessCoefficient\":1," +
            "\"features\":[1,0]" +
            "}]");
    ArrayList<ArrayList<JSONParser>> featureJSONRepresentation = new ArrayList<>();
    ArrayList<JSONParser> t = new ArrayList<>();
    t.add(features);
    t.add(features2);
    featureJSONRepresentation.add(t);

    ArrayList<Float> identity = new ArrayList<>();
    identity.add((float) 1);
    identity.add((float) 1);
    identity.add((float) 1);
    identity.add((float) 1);

    ArrayList<Float> coefftest1 = new ArrayList<>();
    coefftest1.add((float) 1);
    coefftest1.add((float) 1);
    coefftest1.add((float) 0);
    coefftest1.add((float) 0);

    TILDA test = new TILDA(2, 2, 4, labels, featureJSONRepresentation);


    //retrieve subvector test
    List<Float> subvectorObtained = test.retrieveSubvector(0, identity);

    ArrayList<Float> result = new ArrayList<>();
    result.add((float)1);
    result.add((float)1);
    assertArrayEquals(result, subvectorObtained);

    try {
      ArrayList<JSONParser> test2 = test.calculateNewVectorsTraining("Clavier", identity);
      test.calculateNewVectorsTraining("Clavier", coefftest1);
    } catch (Exception e) {
      e.printStackTrace();
    }


    //test recognition

    ArrayList<String> labelsRecognition = new ArrayList<>();
    labelsRecognition.add("Clavier");
    labelsRecognition.add("Souris");

    JSONParser featuresRecognition = new JSONParser("[{" +
            "\"heavinessCoefficient\":0," +
            "\"features\":[0,0]" +
            "}," +
            "{" +
            "\"heavinessCoefficient\":0," +
            "\"features\":[0,0]" +
            "}," +
            "{" +
            "\"heavinessCoefficient\":0," +
            "\"features\":[1,0]" +
            "}]");

    JSONParser featuresRecognition2 = new JSONParser("[{" +
            "\"heavinessCoefficient\":1," +
            "\"features\":[0,1]" +
            "}," +
            "{" +
            "\"heavinessCoefficient\":1," +
            "\"features\":[1,0]" +
            "}," +
            "{" +
            "\"heavinessCoefficient\":0," +
            "\"features\":[0,0]" +
            "}]");
    ArrayList<ArrayList<JSONParser>> featureJSONRepresentationRecognition = new ArrayList<>();
    ArrayList<JSONParser> tRecognitionClavier = new ArrayList<>();
    ArrayList<JSONParser> tRecognitionSouris = new ArrayList<>();
    tRecognitionClavier.add(featuresRecognition);
    tRecognitionSouris.add(featuresRecognition2);
    featureJSONRepresentationRecognition.add(tRecognitionClavier);
    featureJSONRepresentationRecognition.add(tRecognitionSouris);

    ArrayList<Float> coefftestRecognition = new ArrayList<>();
    coefftestRecognition.add((float) 0);
    coefftestRecognition.add((float) 1);
    coefftestRecognition.add((float) 1);
    coefftestRecognition.add((float) 0);
    coefftestRecognition.add((float) 0);
    coefftestRecognition.add((float) 0);

    ArrayList<Float> nullVector = new ArrayList<>();
    nullVector.add((float) 0);
    nullVector.add((float) 0);
    nullVector.add((float) 0);
    nullVector.add((float) 0);
    nullVector.add((float) 0);
    nullVector.add((float) 0);

    TILDA testRecognition = new TILDA(1, 3, 6, labelsRecognition, featureJSONRepresentationRecognition);

    List<Float> subvectorObtained2 = testRecognition.retrieveSubvector(0, coefftestRecognition);
    try {
      String label = testRecognition.recognizeImage(coefftestRecognition);

      String label2 = testRecognition.recognizeImage(nullVector);
    } catch (NoTrainingFoundException e) {
      e.printStackTrace();
    }

    //test with other k value
    TILDA testRecognition2 = new TILDA(1, 3, 2048, labelsRecognition, featureJSONRepresentationRecognition);

    FeatureVector featureVector = new FeatureVector(3, 2048);
    ArrayList<Float> featureArray = new ArrayList<>();
    for (int index=0; index < 2048; index++) {
      featureArray.add((float) 0);
    }


    for (int index=0; index<3; index++) {
      List<Float> featureSubvector = testRecognition2.retrieveSubvector(index, featureArray);
      featureVector.p();
    }
  }


  @Test
  public void testAssetManager() {
    File file = new File("allo/souris");
    String coucou = file.getName();
  }

}