package com.example.gregoire.testmodule2.ExternalFileManager;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class defines the important operation used while managing the external storage
 */
public class FileManager {

  private static String TAG = "FileManager";

  public FileManager() { }

  /**
   * Create all the directory that appear in {@code file}
   * if they don't already exist.
   *
   * @param file
   */
  protected void createDirectory(File file) {
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  /**
   * Generate a name from the current timestamp.
   *
   * @return the name generated
   */
  public String getNewFileName() {
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      String imageFileName = "/JPEG_" + timeStamp + "_";

      return imageFileName;
  }

  /**
   * save an image inside the external storage
   *
   * @param bitmap the image to save
   * @param fileToSave the file in which the image will be saved
   * @throws IOException
   */
  public void saveImage(Bitmap bitmap, File fileToSave) throws IOException {

    if (fileToSave.exists()) fileToSave.delete();
    FileOutputStream out = new FileOutputStream(fileToSave);
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
    out.flush();
    out.close();
  }

  /**
   * save the float array {@code output} inside the file {@code fileToWrite}
   *
   * @param output the float array to save
   * @param fileToWrite the file in which the array will be saved
   * @throws IOException
   */
  public void saveFloatlist(float[] output, File fileToWrite) throws IOException {

    FileWriter outputStream = new FileWriter(fileToWrite);
    Log.i(TAG, "log saved in :" + fileToWrite.getAbsolutePath());
    Log.w(TAG, "value output :" + output[0]);
    Log.w(TAG, "value string output :" + Float.toString(output[0]));
    for (float value : output) {
        outputStream.append(Float.toString(value));
        outputStream.append("\n");
    }
    outputStream.flush();
    outputStream.close();
  }

  /**
   * @see #saveFloatlist(float[], File)
   * override this method to work with {@link ArrayList}
   *
   * @param output the float array to save
   * @param fileToWrite the file in which the array will be saved
   * @throws IOException
   */
  public File saveFloatlist(ArrayList<Float> output, File fileToWrite) throws IOException {

    FileWriter outputStream = new FileWriter(fileToWrite);
    Log.i(TAG, "log saved ina :" + fileToWrite.getAbsolutePath());
    Log.w(TAG, "value output :" + output.get(0));
    Log.w(TAG, "value string output :" + Float.toString(output.get(0)));
    for (float value : output) {
      outputStream.append(Float.toString(value));
      outputStream.append("\n");
    }
    outputStream.flush();
    outputStream.close();
    return null;
  }

  /**
   * transform a file containing a list of float into an array list
   *
   * @param file containing the data
   * @return an {@link ArrayList} representing the list
   * @throws IOException
   */
  public ArrayList<Float> loadFloatList(File file) throws IOException {
    ArrayList<Float> floatList = new ArrayList<Float>();

    FileInputStream fis = new FileInputStream(file);
    DataInputStream in = new DataInputStream(fis);
    BufferedReader br =
            new BufferedReader(new InputStreamReader(in));
    String strLine;
    while ((strLine = br.readLine()) != null) {
      floatList.add(Float.valueOf(strLine));
    }
    in.close();

    return floatList;
  }

  public Bitmap loadAssetImage(Context context, String nameFile) {
      AssetManager assetManager = context.getAssets();

      InputStream istr;
      Bitmap bitmap = null;
      try {
          istr = assetManager.open(nameFile);
          bitmap = BitmapFactory.decodeStream(istr);
      } catch (IOException e) {
          // handle exception
          e.printStackTrace();
      }

      return bitmap;

  }

  /**
   * return an image from a file
   *
   * @param pathImage the file containing the image
   * @return the {@link Bitmap} representation of the image
   * @throws FileNotFoundException
   */
  public Bitmap loadImage(File pathImage) throws FileNotFoundException {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      return BitmapFactory.decodeStream(new FileInputStream(pathImage), null, options);
  }

  public Bitmap loadImage(String pathImage) throws FileNotFoundException {
    return loadImage(new File(pathImage));
  }

  /**
   * return a String from a file
   *
   * @param path file containing the string
   * @return the string content
   */
  public String loadString(File path) {
    String textToLoad;
    try {
      FileInputStream is = new FileInputStream(path);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      textToLoad = new String(buffer, "UTF-8");
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
    return textToLoad;
  }

  /**
   * @param fileOrDirectory the directory (or file) that will be deleted
   */
  public void deleteAllContentInFolder(File fileOrDirectory) {
    if (fileOrDirectory.isDirectory())
      for (File child : fileOrDirectory.listFiles())
        deleteAllContentInFolder(child);

    fileOrDirectory.delete();
  }

}
