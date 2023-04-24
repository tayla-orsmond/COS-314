import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    private static String dataset = "Knapsack Instances";
    public static void main(String[] args) {
        System.out.println("========== COS 314 - Assignment 2 - Knapsack Problem ==========");
        Loader loader = new Loader();
        System.out.println("========== Running GA... ==========");
        runGA(loader);
        System.out.println("========== Running ACO... ==========");
        runACO(loader);
        
        System.out.println("The results for every instance have been written to the Solutions folder, as well as a summary for each dataset.");
        
        //Allow user to press enter to exit
        System.out.println("\nPress enter to exit...");
        try {
            System.in.read();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }        
    }

    public static void runGA(Loader loader) {
        GA ga = new GA();
        try{
            HashMap<String, Double> optima = loader.loadOptima("Optima.txt");
            ArrayList<String> instances = loader.loadDataset(dataset);
            for (String instance : instances) {
                ArrayList<String> data = loader.readFile(dataset + "/" + instance + ".txt");
                ga.clear();
                ga.setInstanceName(instance);
                ga.setOptimal(optima.get(instance));
                ga.setItems(data);

                ga.solve();
                ga.writeResults("Solutions/GA/" + instance + "_SOL.txt");
            }
        } catch (Exception e) {
            System.out.println("[GA] Error: " + e);
        }

        ga.summarize("Solutions/GA/GA_Summary.txt", "GA");
    }

    public static void runACO(Loader loader){
        // ACO aco = new ACO();
        // try{
        //     HashMap<String, Double> optima = loader.loadOptima("Optima.txt");
        //     ArrayList<String> instances = loader.loadDataset(dataset);
        //     for (String instance : instances) {
        //         ArrayList<String> data = loader.readFile(dataset + "/" + instance + ".txt");
        //         aco.clear();
        //         aco.setInstanceName(instance);
        //         aco.setOptimal(optima.get(instance));
        //         aco.setItems(data);

        //         aco.solve();
        //         aco.writeResults("Solutions/ACO/" + instance + "_SOL.txt");
        //     }
        // } catch (Exception e) {
        //     System.out.println("[ACO] Error: " + e);
        // }

        // aco.summarize("Solutions/ACO/ACO_Summary.txt", "ACO");
    }

}
