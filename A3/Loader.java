// Tayla Orsmond u21467456
// Loader class loads up the datasets from the local files for use and stores them in an arraylist

import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;

public class Loader {
    /** 
        * Load the data from a file (.data file)
        * @param filename the name of the file
        * @return an arraylist of strings containing the file's data
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
}
