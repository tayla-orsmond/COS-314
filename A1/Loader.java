// Tayla Orsmond u21467456
// Loader class loads up the datasets from the local files for use

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class Loader {
    /** 
        * @brief load all the PIs (files) belonging to a dataset (folder)
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
                // Add the file to the array list
                filenames.add(f.getName());
            }
        }
        // Convert the array list to an array
        //String[] filenamesArray = filenames.toArray(new String[filenames.size()]);
        // Return the array
        return filenames;
    }
    /** 
        * @brief load a PI's / Summary data from a file
        * @param filename the name of the PI / File
        * @return an arraylist of strings containing the PI's / File's data
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
        // Convert the array list to an array
        //String[] dataArray = data.toArray(new String[data.size()]);
        // Return the array
        return data;
    }
}
