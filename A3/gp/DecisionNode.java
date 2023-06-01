package gp;
// Tayla Orsmond u21467456
// A class that represents a decision node in the decision tree
// The decision tree will be used to classify the breast cancer data
// The terminal nodes will be no-recurrence-events and recurrence-events
// The decision nodes will be the attributes of the data


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

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

public class DecisionNode {
    private final String[] functionalSet = {
        "age", "menopause", "tumor-size", "inv-nodes", "node-caps", "deg-malig", "breast", "breast-quad", "irradiat"
    };
    private final String[] terminalSet = {
        "no-recurrence-events", "recurrence-events"
    };
    private final String[] age = {
        "10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79", "80-89", "90-99"
    };
    private final String[] menopause = {
        "lt40", "ge40", "premeno"
    };
    private final String[] tumorSize = {
        "0-4", "5-9", "10-14", "15-19", "20-24", "25-29", 
        "30-34", "35-39", "40-44", "45-49", "50-54", "55-59"
    };
    private final String[] invNodes = {
        "0-2", "3-5", "6-8", "9-11", "12-14", "15-17", 
        "18-20", "21-23", "24-26", "27-29", "30-32", "33-35", "36-39"
    };
    private final String[] nodeCaps = {
        "yes", "no"
    };
    private final String[] degMalig = {
        "1", "2", "3"
    };
    private final String[] breast = {
        "left", "right"
    };
    private final String[] breastQuad = {
        "left_up", "left_low", "right_up", "right_low", "central"
    };
    private final String[] irradiat = {
        "yes", "no"
    };

    private final String[][] allAttributes = {
        age, menopause, tumorSize, invNodes, nodeCaps, degMalig, breast, breastQuad, irradiat
    };
    
    private String value; // the value of the node (either a terminal or functional value)
    private ArrayList<DecisionNode> children;
    private int numChildren;
    private boolean isTerminal;
    private double fitness;

    /**
     * Constructor for the DecisionNode class
     * This selects a random attribute from the functional / terminal set and creates a node with that value as well as the correct number of children
     * @param isTerminal a boolean that indicates whether the node should be a terminal node or not
     * @param rng a random number generator
     */
    public DecisionNode(boolean isTerminal, Random rng) {
        this.isTerminal = isTerminal;
        if (isTerminal) {
            value = terminalSet[rng.nextInt(terminalSet.length)];
            numChildren = 0;
            children = null;
        } else {
            value = functionalSet[rng.nextInt(functionalSet.length)];
            numChildren = allAttributes[Arrays.asList(functionalSet).indexOf(value)].length;
            // scoped to the number of children
            children = new ArrayList<>(numChildren);
        }
    }

    public DecisionNode(boolean isTerminal, Random rng, ArrayList<String> takenValues) {
        this.isTerminal = isTerminal;
        if(isTerminal) {
            value = terminalSet[rng.nextInt(terminalSet.length)];
            numChildren = 0;
            children = null;
        } else {
            // select a random attribute from the functional set that is not already in the takenValues array
            do {
                value = functionalSet[rng.nextInt(functionalSet.length)];
            } while (takenValues.contains(value));
            numChildren = allAttributes[Arrays.asList(functionalSet).indexOf(value)].length;
            // scoped to the number of children
            children = new ArrayList<>(numChildren);
        }
    }
    
    // Getters
    public String getValue() {
        return value;
    }

    public ArrayList<DecisionNode> getChildren() {
        return children;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public DecisionNode getChild(int index) {
        if(!this.isTerminal && index < numChildren && index >= 0) {
            return children.get(index);
        }
        throw new IndexOutOfBoundsException("Index out of bounds for child of node");
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public double getFitness() {
        return fitness;
    }

    // Setters
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    /**
     * Change a node from a functional node to a terminal node
     * This is used in the shrink mutation method (see GP.java) 
     * @return a boolean that indicates whether the node was changed to a terminal node or not
     */
    public boolean setTerminal(Random rng) {
        if(!this.isTerminal){ // if the node is not a terminal node, then change it to a terminal node
            this.isTerminal = true;
            this.value = terminalSet[rng.nextInt(terminalSet.length)];
            this.numChildren = 0;
            this.children = null;
            return true;
        } 
        return false; // if the node is already a terminal node, then return false
    }

    /*
     * Change a node from a terminal node to a functional node
     * This is used in the grow mutation method (see GP.java)
     * @param rng a random number generator
     * @return a boolean that indicates whether the node was changed to a functional node or not
     */
    public boolean setFunctional(Random rng) {
        if(this.isTerminal){ // if the node is a terminal node, then change it to a functional node
            this.isTerminal = false;
            this.value = functionalSet[rng.nextInt(functionalSet.length)];
            this.numChildren = allAttributes[Arrays.asList(functionalSet).indexOf(value)].length;
            this.children = new ArrayList<>(numChildren);
            return true;
        }
        return false; // if the node is already a functional node, then return false
    }    

    /**
     * A method that uses the decision tree to classify the data
     * @param data the data to classify
     * @return the classification of the data
     */
    public String classify(String[] data) {
        // if the node is a terminal node, then return the value
        if (isTerminal) {
            return value;
        }
        // if the node is not a terminal node, then find the index of the node's attribute in the functional set
        int index = Arrays.asList(functionalSet).indexOf(this.value);
        // find the index of the attribute value in the attribute array
        if(data[index].equals("?")){ // if the attribute value is unknown, then return the classification of the first child node (i.e., data is made 0)
            return children.get(0).classify(data);
        }
        int attributeIndex = Arrays.asList(allAttributes[index]).indexOf(data[index]);
        // get the child node at the index of the attribute value       
        // recursively call the classify method on the child node
        return children.get(attributeIndex).classify(data);
    }

    // Helpers
    /**
     * A method that adds a child to the children array
     * @param child the child to add
     * @throws IndexOutOfBoundsException if the number of children is equal to the maximum number of children
     */
    public void addChild(DecisionNode child) throws IndexOutOfBoundsException {
        if(!isTerminal) {
            if(children == null) {
                children = new ArrayList<>();
            }
            if (children.size() == numChildren) {
                throw new IndexOutOfBoundsException("Cannot add more children to this node, maximum number of children: " + numChildren + " reached");
            }
            children.add(child);
        }
    }

    /**
     * A method that adds a child to the children array at a specific index (replacing a previous child / null)
     * @param child the child to add
     * @param index the index to add the child to
     * @throws IndexOutOfBoundsException if the number of children is equal to the maximum number of children
     */
    public void addChild(DecisionNode child, int index) throws IndexOutOfBoundsException {
        if(!isTerminal && index < numChildren) {
            if(children == null) {
                children = new ArrayList<>();
            }
            children.add(index, child);
        } else {
            throw new IndexOutOfBoundsException("Cannot add children to this node at index: " + index + " and maximum number of children: " + numChildren);
        }
    }

    /**
     * A method that removes a child from the children array
     * @param index the index of the child to remove
     * @throws IndexOutOfBoundsException if the number of children is equal to the maximum number of children
     */
    public void removeChild(int index) throws IndexOutOfBoundsException {
        if(!isTerminal && children != null && !children.isEmpty() && index < numChildren) {
            children.remove(index);
        } else {
            throw new IndexOutOfBoundsException("Cannot remove child from this node at index: " + index + " and maximum number of children: " + numChildren);
        }
    }

    /**
     * A method that sets and gets the depth of the node & tree
     * @param depth the depth of the starting node
     * @return the depth of the tree
     */
    public int getTreeDepth(int depth){
        if(isTerminal){
            return depth;
        }
        int maxDepth = 0;
        for(DecisionNode child : children){
            maxDepth = Math.max(maxDepth, child.getTreeDepth(depth + 1));
        }
        return maxDepth;
    }

    /**
     * A method to prune the tree
     * @param depth the depth of the tree to prune
     */
    public void prune(int depth, Random rng) {
        if(isTerminal) { // i.e., nothing to prune
            return;
        }
        if(depth == 0) {
            setTerminal(rng);
        } else {
            for(DecisionNode child : children) {
                child.prune(depth - 1, rng);
            }
        }
    }

    /**
     * A method to print the tree
     * @param depth the depth of the tree to print
     */
    public void printTree(int depth) {
        if(isTerminal) {
            System.out.println("  ".repeat(depth) + value);
        } else {
            System.out.println("  ".repeat(depth) + value);
            for(DecisionNode child : children) {
                child.printTree(depth + 1);
            }
        }
    }

}
