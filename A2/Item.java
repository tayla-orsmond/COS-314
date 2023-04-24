//Tayla Orsmond u21467456
//Gene class to store genes in a chromosome / individual for the knapsack problem
//This represents one item in the knapsack problem

public class Item {
    private double weight;
    private double value;

    public Item(double value, double weight) {
        this.weight = weight;
        this.value = value;
    }

    //Getters
    public double getWeight() {
        return weight;
    }

    public double getValue() {
        return value;
    }
}
