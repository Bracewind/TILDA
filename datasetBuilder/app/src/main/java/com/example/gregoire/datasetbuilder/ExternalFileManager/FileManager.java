package com.example.gregoire.datasetbuilder.ExternalFileManager;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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

public class FileManager {

  private static String TAG = "FileManager";

  private String pathToTempDirectory;

  public FileManager()
  {

  }

  protected void createDirectory(File file) {
    if (!file.exists()) {
      file.mkdirs();
    }
  }

    public String getNewFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "/JPEG_" + timeStamp + "_";

        return imageFileName;
    }

    //return the file in which the bitmap has been saved
    public File saveImage(Bitmap bitmap, File fileToSave) throws IOException {

      if (fileToSave.exists()) fileToSave.delete();
      FileOutputStream out = new FileOutputStream(fileToSave);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
      out.flush();
      out.close();

      return fileToSave;
    }

    public File saveFloatlist(float[] output, File fileToWrite) throws IOException {

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
      return null;
    }

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

  public Bitmap loadImage(File pathImage) throws FileNotFoundException {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      return BitmapFactory.decodeStream(new FileInputStream(pathImage), null, options);
  }

  public Bitmap loadImage(String pathImage) throws FileNotFoundException {
    return loadImage(new File(pathImage));
  }

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

  void deleteAllContentInFolder(File fileOrDirectory) {
    if (fileOrDirectory.isDirectory())
      for (File child : fileOrDirectory.listFiles())
        deleteAllContentInFolder(child);

    fileOrDirectory.delete();
  }

}
