// Tayla Orsmond u21467456
// A class that represents a preprocessor for the data
// The preprocessor is used to process the data before it is used to train the neural network
// The preprocessor splits the data into training and testing data
// The preprocessor uses hot-one encoding to encode the data


/*
 * Attribute Information:
   1. Class: no-recurrence-events, recurrence-events
   2. age: 10-19, 20-29, 30-39, 40-49, 50-59, 60-69, 70-79, 80-89, 90-99.
   3. menopause: lt40, ge40, premeno.
   4. tumor-size: 0-4, 5-9, 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-44,
                  45-49, 50-54, 55-59.
   5. inv-nodes: 0-2, 3-5, 6-8, 9-11, 12-14, 15-17, 18-20, 21-23, 24-26,
                 27-29, 30-32, 33-35, 36-39.
   6. node-caps: yes, no.
   7. deg-malig: 1, 2, 3.
   8. breast: left, right.
   9. breast-quad: left-up, left-low, right-up,	right-low, central.
  10. irradiat:	yes, no.
 */

import java.util.ArrayList;
import java.util.Random;

public class Preprocessor {
  ArrayList<String> data; // The data to be processed
  ArrayList<double[]> encodedData; // The encoded data
  ArrayList<double[]> trainingSet; // The encoded training set
  ArrayList<double[]> testingSet; // The encoded test set

  /**
   * Constructor for the preprocessor
   * @param data the data to be processed
   */
  public Preprocessor(ArrayList<String> data) {
    this.data = data;
    encodedData = new ArrayList<double[]>();
    trainingSet = new ArrayList<double[]>();
    testingSet = new ArrayList<double[]>();
  }

  // Getters
  public ArrayList<double[]> getEncodedData() {
    return encodedData;
  }

  public ArrayList<double[]> getTrainingSet() {
    return trainingSet;
  }

  public ArrayList<double[]> getTestingSet() {
    return testingSet;
  }

  /**
   * Method to split the data into a training set and a test set
   * The data is randomly split into a training set and a test set to ensure that the neural network is not overfitting
   * @param trainingSetSize the size of the training set
   * @param seed the seed for the random number generator
   */
  public void splitData(int trainingSetSize, Random rng) {
    // Loop through the data
    for (int i = 0; i < encodedData.size(); i++) {
      // Get a random index 
      int randomIndex = rng.nextInt(encodedData.size());
      // Get the data at the random index
      double[] randomData = encodedData.get(randomIndex);
      // If the training set is not full
      if (trainingSet.size() < trainingSetSize) {
        // Add the data to the training set
        trainingSet.add(randomData);
      } else {
        // Add the data to the test set
        testingSet.add(randomData);
      }
    }
  }

  /**
   * Method to encode the data
   * The data is encoded using hot-one encoding
   */
  public void encodeData() {
    // Loop through the data
    for (int i = 0; i < data.size(); i++) {
      // Get the data at the index
      String dataLine = data.get(i);
      // Split the data into an array
      String[] dataLineArray = dataLine.split(",");
      // Create a new array to store the encoded data
      double[] encodedDataLine = new double[52];
      // Encode each attribute
      // Encode the class
      switch(dataLineArray[0]) {
        case "no-recurrence-events":
          encodedDataLine[0] = 0.0;
          break;
        case "recurrence-events":
          encodedDataLine[0] = 1.0;
          break;
      }
      // Encode the age
      for(int j = 1; j < 10; j++){
        if(dataLineArray[1].compareTo(j + "0-" + j + "9") == 0){
          encodedDataLine[j] = 1.0;
        } else {
          encodedDataLine[j] = 0.0;
        }
      }
      // Encode the menopause
      switch(dataLineArray[2]) {
        case "lt40":
          encodedDataLine[10] = 1.0;
          encodedDataLine[11] = 0.0;
          encodedDataLine[12] = 0.0;
          break;
        case "ge40":
          encodedDataLine[10] = 0.0;
          encodedDataLine[11] = 1.0;
          encodedDataLine[12] = 0.0;
          break;
        case "premeno":
          encodedDataLine[10] = 0.0;
          encodedDataLine[11] = 0.0;
          encodedDataLine[12] = 1.0;
          break;
        default:
          encodedDataLine[10] = 0.0;
          encodedDataLine[11] = 0.0;
          encodedDataLine[12] = 0.0;
      }
      // Encode the tumor-size
      int k = 0;
      for(int j = 0; j < 12; j++){
        if(dataLineArray[3].compareTo(k + "-" + (k + 4)) == 0){
          encodedDataLine[j + 13] = 1.0;
        } else {
          encodedDataLine[j + 13] = 0.0;
        }
        k += 5;
      }
      // Encode the inv-nodes
      k = 0;
      for(int j = 0; j < 13; j++){
        if(dataLineArray[4].compareTo(j + "-" + (j + 2)) == 0){
          encodedDataLine[j + 25] = 1.0;
        } else {
          encodedDataLine[j + 25] = 0.0;
        }
        k += 3;
      }
      // Encode the node-caps
      switch(dataLineArray[5]) {
        case "yes":
          encodedDataLine[38] = 1.0;
          encodedDataLine[39] = 0.0;
          break;
        case "no":
          encodedDataLine[38] = 0.0;
          encodedDataLine[39] = 1.0;
          break;
        default:
          encodedDataLine[38] = 0.0;
          encodedDataLine[39] = 0.0;
      }
      // Encode the deg-malig
      switch(dataLineArray[6]) {
        case "1":
          encodedDataLine[40] = 1.0;
          encodedDataLine[41] = 0.0;
          encodedDataLine[42] = 0.0;
          break;
        case "2":
          encodedDataLine[40] = 0.0;
          encodedDataLine[41] = 1.0;
          encodedDataLine[42] = 0.0;
          break;
        case "3":
          encodedDataLine[40] = 0.0;
          encodedDataLine[41] = 0.0;
          encodedDataLine[42] = 1.0;
          break;
        default:
          encodedDataLine[40] = 0.0;
          encodedDataLine[41] = 0.0;
          encodedDataLine[42] = 0.0;
      }
      // Encode the breast
      switch(dataLineArray[7]) {
        case "left":
          encodedDataLine[43] = 1.0;
          encodedDataLine[44] = 0.0;
          break;
        case "right":
          encodedDataLine[43] = 0.0;
          encodedDataLine[44] = 1.0;
          break;
        default:
          encodedDataLine[43] = 0.0;
          encodedDataLine[44] = 0.0;
      }
      // Encode the breast-quad
      switch(dataLineArray[8]) {
        case "left_up":
          encodedDataLine[45] = 1.0;
          encodedDataLine[46] = 0.0;
          encodedDataLine[47] = 0.0;
          encodedDataLine[48] = 0.0;
          encodedDataLine[49] = 0.0;
          break;
        case "left_low":
          encodedDataLine[45] = 0.0;
          encodedDataLine[46] = 1.0;
          encodedDataLine[47] = 0.0;
          encodedDataLine[48] = 0.0;
          encodedDataLine[49] = 0.0;
          break;
        case "right_up":
          encodedDataLine[45] = 0.0;
          encodedDataLine[46] = 0.0;
          encodedDataLine[47] = 1.0;
          encodedDataLine[48] = 0.0;
          encodedDataLine[49] = 0.0;
          break;
        case "right_low":
          encodedDataLine[45] = 0.0;
          encodedDataLine[46] = 0.0;
          encodedDataLine[47] = 0.0;
          encodedDataLine[48] = 1.0;
          encodedDataLine[49] = 0.0;
          break;
        case "central":
          encodedDataLine[45] = 0.0;
          encodedDataLine[46] = 0.0;
          encodedDataLine[47] = 0.0;
          encodedDataLine[48] = 0.0;
          encodedDataLine[49] = 1.0;
          break;
        default:
          encodedDataLine[45] = 0.0;
          encodedDataLine[46] = 0.0;
          encodedDataLine[47] = 0.0;
          encodedDataLine[48] = 0.0;
          encodedDataLine[49] = 0.0;
      }
      // Encode the irradiat
      switch(dataLineArray[9]) {
        case "yes":
          encodedDataLine[50] = 1.0;
          encodedDataLine[51] = 0.0;
          break;
        case "no":
          encodedDataLine[50] = 0.0;
          encodedDataLine[51] = 1.0;
          break;
        default:
          encodedDataLine[50] = 0.0;
          encodedDataLine[51] = 0.0;
      }
      encodedData.add(encodedDataLine);
    }
  }
}
