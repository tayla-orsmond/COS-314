// Tayla Orsmond u21467456
// Loader class loads up the datasets from the local files for use and stores them in an arraylist
// Loader also reads the optima files and stores the data in a hashmap

import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Loader {
    // Hashmap for optimal solutions
    private HashMap<String, Double> optima = new HashMap<>();
    /** 
        * Load all the PIs (.txt files) belonging to a dataset (folder)
        * @param dSName the name of the dataset (folder)
        * @return an arraylist of strings containing the names of the PIs (files)
        * @throws IOException
     */
    public ArrayList<String> loadDataset(String dSName) throws IOException {
        // Create a new file object
        File dataset = new File(dSName);
        // Create a new array list
        ArrayList<String> filenames = new ArrayList<String>();
        // Loop through the files in the folder
        for (File f : dataset.listFiles()) {
            // Check if the file is a text file
            if (f.getName().endsWith(".txt")) {
                // Remove the .txt extension
                String name = f.getName().substring(0, f.getName().length() - 4);
                // Add the file to the array list
                filenames.add(name);
            }
        }
        // Return the array
        return filenames;
    }
    /** 
        * Load a PI's / Summary data from a file (.txt)
        * @param filename the name of the PI / File
        * @return an arraylist of strings containing the PI / File's data
        * @throws IOException
     */
    public ArrayList<String> readFile(String filename) throws IOException {
        // Create a new file object
        File file = new File(filename);
        // Create a new scanner object
        Scanner scanner = new Scanner(file);
        // Create a new array list
        ArrayList<String> data = new ArrayList<String>();

        // Loop through the file
        while (scanner.hasNextLine()) {
            // Add the line to the array list
            data.add(scanner.nextLine());
        }
        // Close the scanner
        scanner.close();
        // Return the array
        return data;
    }
    /** 
        * Load the optima from a file (.txt)
        * @param filename the name of the file
        * @return a hashmap containing the optimal solutions
        * @throws IOException
     */
    public HashMap<String, Double> loadOptima(String filename) throws IOException {
        // Create a new file object
        File file = new File(filename);
        // Create a new scanner object
        Scanner scanner = new Scanner(file);
        // Loop through the file
        while (scanner.hasNextLine()) {
            // Split the line into an array
            String[] line = scanner.nextLine().split(" ");
            // Add the data to the hashmap
            optima.put(line[0], Double.parseDouble(line[1]));
        }
        // Close the scanner
        scanner.close();
        // Return the hashmap
        return optima;
    }
}
