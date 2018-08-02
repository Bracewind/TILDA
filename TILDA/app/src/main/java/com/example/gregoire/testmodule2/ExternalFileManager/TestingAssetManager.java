package com.example.gregoire.testmodule2.ExternalFileManager;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import com.example.gregoire.testmodule2.Testing.NoImageRemainingException;
import com.example.gregoire.testmodule2.helpers.VectorOperation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestingAssetManager {

  private static String TAG = "TestingAssetManager";

  private AssetManager mAssetManager;

  //attribute that allows the class to know what images the method has not already seen
  private String trainOrTestDirectory;
  private String[] allLabels;
  private int indexCurrentLabel;
  private String[] allImagesInCurrentLabel;
  private int indexCurrentImage;

  public TestingAssetManager(AssetManager assetManager) throws IOException {
    mAssetManager = assetManager;

    trainOrTestDirectory = "Train";
    indexCurrentLabel = 0;
    indexCurrentImage = 0;

    allLabels = mAssetManager.list(trainOrTestDirectory);
    if (allLabels != null) {
      allImagesInCurrentLabel = mAssetManager.list(
              trainOrTestDirectory+File.separator+allLabels[indexCurrentLabel]);
    }
    else {
      trainOrTestDirectory = "Test";
      allLabels = mAssetManager.list(trainOrTestDirectory);
      allImagesInCurrentLabel = mAssetManager.list(
              trainOrTestDirectory+File.separator+allLabels[indexCurrentLabel]);
    }

  }

  public int nbImageInDataset() throws IOException {
    int nbImage = 0;
    String[] labels = mAssetManager.list("Train");
    for (String label : labels) {
      nbImage += mAssetManager.list("Train"+File.separator+label).length;
    }
    labels = mAssetManager.list("Test");
    for (String label : labels) {
      nbImage += mAssetManager.list("Test"+File.separator+label).length;
    }
    return nbImage;
  }

  public Pair<String, Bitmap> firstImage() throws IOException {

    Log.i(TAG, "label is " + allLabels[indexCurrentLabel]+ " files are " + indexCurrentImage);

    return new Pair<>(
            allLabels[indexCurrentLabel],
            loadBitmap(
                    trainOrTestDirectory+File.separator+
                            allLabels[indexCurrentLabel]+File.separator+
                            allImagesInCurrentLabel[indexCurrentImage]
            )
    );
  }

  public Bitmap loadBitmap(String filePath) throws IOException {

    InputStream istr;
    Bitmap bitmap;
    istr = mAssetManager.open(filePath);
    bitmap = BitmapFactory.decodeStream(istr);

    return bitmap;
  }
  /**
   *
   *
   * @return
   */
  public Pair<String, Bitmap> nextImage() throws IOException, NoImageRemainingException {
    indexCurrentImage += 1;
    if (indexCurrentImage >= allImagesInCurrentLabel.length) {
      indexCurrentImage = 0;
      indexCurrentLabel += 1;
      if (indexCurrentLabel >= allLabels.length) {
        indexCurrentLabel = 0;
        if (trainOrTestDirectory.equals("Train")) {
          trainOrTestDirectory = "Test";
          allLabels = mAssetManager.list(trainOrTestDirectory);
        }
        else {
          throw new NoImageRemainingException();
        }
      }

      allImagesInCurrentLabel = mAssetManager.list(
                trainOrTestDirectory+File.separator+allLabels[indexCurrentLabel]);

    }

    Log.i(TAG, "label is " + allLabels[indexCurrentLabel]+ " file is " + allImagesInCurrentLabel[indexCurrentImage]);
    return new Pair<>(
            allLabels[indexCurrentLabel],
            loadBitmap(
                    trainOrTestDirectory+File.separator+
                    allLabels[indexCurrentLabel]+File.separator+
                    allImagesInCurrentLabel[indexCurrentImage]
            )
    );
  }

  public boolean trainingDataRemains()
  {
    return trainOrTestDirectory.equals("Train");
  }

}
