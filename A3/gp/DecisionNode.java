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
    private String[] functionalSet = {
        "age", "menopause", "tumor-size", "inv-nodes", "node-caps", "deg-malig", "breast", "breast-quad", "irradiat"
    };
    private String[] terminalSet = {
        "no-recurrence-events", "recurrence-events"
    };
    private String[] age = {
        "10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79", "80-89", "90-99"
    };
    private String[] menopause = {
        "lt40", "ge40", "premeno"
    };
    private String[] tumorSize = {
        "0-4", "5-9", "10-14", "15-19", "20-24", "25-29", 
        "30-34", "35-39", "40-44", "45-49", "50-54", "55-59"
    };
    private String[] invNodes = {
        "0-2", "3-5", "6-8", "9-11", "12-14", "15-17", 
        "18-20", "21-23", "24-26", "27-29", "30-32", "33-35", "36-39"
    };
    private String[] nodeCaps = {
        "yes", "no"
    };
    private String[] degMalig = {
        "1", "2", "3"
    };
    private String[] breast = {
        "left", "right"
    };
    private String[] breastQuad = {
        "left_up", "left_low", "right_up", "right_low", "central"
    };
    private String[] irradiat = {
        "yes", "no"
    };

    private String[][] allAttributes = {
        age, menopause, tumorSize, invNodes, nodeCaps, degMalig, breast, breastQuad, irradiat
    };
    
    private String value; // the value of the attribute / terminal node
    private ArrayList<DecisionNode> children;
    private int numChildren;
    private boolean isTerminal;
    private double fitness;

    /**
     * Constructor for the DecisionNode class
     * @param value the value of the attribute / terminal node
     */
    public DecisionNode(String value, boolean isTerminal) {
        this.value = value;
        this.isTerminal = isTerminal;
        this.fitness = 0;
        if(isTerminal) {
            this.children = null;
            this.numChildren = 0;
        } else {
            this.children = new ArrayList<DecisionNode>();
            this.numChildren = allAttributes[Arrays.asList(functionalSet).indexOf(value)].length;
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

    public boolean isTerminal() {
        return isTerminal;
    }

    public double getFitness() {
        return fitness;
    }

    // Setters
    public void setChildren(ArrayList<DecisionNode> children) {
        this.children = children;
    }

    public void setTerminal(boolean isTerminal) {
        this.isTerminal = isTerminal;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
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
        // if the node is not a terminal node, then find the index of the attribute in the functional set
        int index = Arrays.asList(functionalSet).indexOf(value);
        // find the index of the attribute value in the attribute array
        if(data[index].equals("?")){
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
     */
    public void addChild(DecisionNode child) {
        if(!isTerminal){
            if(children == null) {
                children = new ArrayList<>();
            }
            children.add(child);
        }
    }

    /**
     * A method that adds a child to the children array at a specific index (replacing a previous child / null)
     * @param child the child to add
     * @param index the index to add the child to
     */
    public void addChild(DecisionNode child, int index) {
        if(!isTerminal && index < numChildren) {
            if(children == null) {
                children = new ArrayList<>();
            }
            children.add(index, child);
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
        // if the node is a terminal node, then return
        if (isTerminal) {
            return;
        }
        // if the depth is 0, then set the node to a terminal node
        if (depth == 1) {
            isTerminal = true;
            value = rng.nextInt(2) == 0 ? terminalSet[0] : terminalSet[1];
            children = null;
            numChildren = 0;
            return;
        }
        // traverse down the tree to the depth
        for (DecisionNode child : children) {
            child.prune(depth - 1, rng);
        }
    }

    /**
     * A method that returns the index of the attribute in the functional set
     * @param attribute the attribute to find the index of
     * @return the index of the attribute in the functional set
     */
    public int getAttributeIndex(String attribute) {
        return Arrays.asList(functionalSet).indexOf(attribute);
    }

    /**
     * A method that returns the index of the terminal node in the terminal set
     * @param terminal the terminal node to find the index of
     * @return the index of the terminal node in the terminal set
     */
    public int getTerminalIndex(String terminal) {
        return Arrays.asList(terminalSet).indexOf(terminal);
    }

    /**
     * A method that returns the index of the attribute value in the attribute set
     * @param attribute the attribute to find the index of
     * @param value the value of the attribute to find the index of
     * @return the index of the attribute value in the attribute set
     */
    public int getAttributeValueIndex(String attribute, String value) {
        int index = getAttributeIndex(attribute);
        return Arrays.asList(allAttributes[index]).indexOf(value);
    }

    /**
     * A method that returns the attribute at the given index
     * @param index the index of the attribute to return
     * @return the attribute at the given index
     */
    public String getAttribute(int index) {
        return functionalSet[index];
    }

    /**
     * A method that returns the terminal node at the given index
     * @param index the index of the terminal node to return
     * @return the terminal node at the given index
     */
    public String getTerminal(int index) {
        return terminalSet[index];
    }

    /**
     * A method that returns the attribute value at the given index
     * @param attribute the attribute to find the index of
     * @param index the index of the attribute value to return
     * @return the attribute value at the given index
     */
    public String getAttributeValue(String attribute, int index) {
        int attributeIndex = getAttributeIndex(attribute);
        return allAttributes[attributeIndex][index];
    }

    /**
     * A method that returns the tree as a string
     * @return the tree as a string
     */
    public String toString() {
        String result = "";
        if(isTerminal) {
            result += "[" + value + "] ";
        } else {
            result += value + " children: {";
            for (DecisionNode child : children) {
                result += child.toString() + ", ";
            }
            result += "}";
        }
        return result;
    }

    public DecisionNode getChild(int nextInt) {
        return children.get(nextInt);
    }

}
