package com.example.gregoire.testmodule2.Classifier;

/**
 * This interface allows you to create a new activity to display the result of the classification
 */
public interface ClassifierCallback {
  /**
   * Used to inform the activity has recognized the label, and to transfer it to
   * the activity.
   * Called by {@link ClassifierCalculator#onPostExecute(Void)}
   *
   * @param label the label found by the classifier
   */
  void onClassificationFinished(String label);

  /**
   * Used to inform the activity that the classifier has been trained.
   * Called by {@link ClassifierCalculator#onPostExecute(Void)}
   */
  void onTrainFinished();

  void onNoTrainingFound();
}
