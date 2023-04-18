import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * This file must be accompanied by a file with its saved weights and biases.
 * If that file does not exist, WidsomSetup.java can be ran to create the file.
 * However, new weights files are randomized, so it must be trained using DawnTools.java.
 * Author: Kai
 */

public class Dawn {

    public int structure[] = {784,80,40,10};
    public Layer layers[] = new Layer[structure.length - 1]; //one less layer than numbers in structure[]

    //basic constructor, also calls readInWeightsBiases
    public Dawn() {
        for(int i = 0; i < layers.length; i++) {
            layers[i] = new Layer(structure[i], structure[i+1]);
        }
        readInWeightsBiases();
    }
    
    //reads in weights and biases from associated files
    public void readInWeightsBiases() {
        //since we're connecting to a file
        try {
            for(int i = 0; i < layers.length; i++)
            {
                File file = new File("Wisdom" + i + ".txt");
                Scanner fileScan = new Scanner(file);
                
                //reading in weights
                for(int j = 0; j < layers[i].weights.length; j++)
                {
                    Scanner lineScan = new Scanner(fileScan.nextLine());
                    lineScan.useDelimiter(",");
                    for(int k = 0; k < layers[i].weights[j].length; k++)
                    {
                        layers[i].weights[j][k] = (Double) (lineScan.nextDouble());
                    }
                    lineScan.close();
                }
                fileScan.nextLine();
                //reading in biases
                Scanner lineScan = new Scanner(fileScan.nextLine());
                lineScan.useDelimiter(",");
                for (int j = 0; j < layers[i].biases.length; j++)
                {
                    layers[i].biases[j] = lineScan.nextDouble();
                }
                lineScan.close();
                fileScan.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
 
    //returns the INDEX of the highest value in a float list
    public int highestValIndex(double[] list) {
        int ret = 0;
        double highestVal = 0;
        for(int i = 0; i < list.length; i++) {
            if (list[i] > highestVal) {
                highestVal = list[i];
                ret = i;
            }
        }
        return ret;
    }

    //takes a list of integers and returns a list of activations for the pixels
    public double[] convertPixelsToActivations(int[] pixels) {
        double activations[] = new double[pixels.length];
        for(int i = 0; i < pixels.length; i++) {
            double doublePixel = pixels[i];
            activations[i] = doublePixel /= 255.0;
        }
        return activations;
    }

    //value / sum of values
    public double[] getConfidence(double[] list) {
        double confidence[] = new double[list.length];
        double total = totalOfList(list);
        
        for(int i = 0; i < list.length; i++) {
            confidence[i] = list[i] / total;
        }
        return confidence;
    }

    //adds all values of a list together
    public double totalOfList(double[] list) {
        double total = 0;
        for(double x : list) {
            total += x;
        }
        return total;
    }

    //Given a list of pixels, this method runs the neural network
    //It also sets its confidence
    public int wildGuess(int[] pixels) {
        for(int i = 0; i < layers.length; i++)
        {
            if (i == 0) {
                layers[i].outputs = layers[i].calculateOutputs(convertPixelsToActivations(pixels));
            } else {
                layers[i].outputs = layers[i].calculateOutputs(layers[i-1].outputs);
            }
        }
        return highestValIndex(layers[layers.length - 1].outputs);
    }

    public void fullBackprop(int label, double learnRate, boolean reachedBatchSize, int batchSize) {
        double[] expectations = new double[structure[structure.length - 1]];
        for(int i = 0; i < expectations.length; i++) {
            expectations[i] = (i == label) ? 1 : 0;
        }

        for(int i = layers.length - 1; i >= 0; i--) {
            boolean finalLayer = (i == layers.length - 1) ? true : false;
            if(finalLayer) {
                layers[i].backpropagate(expectations, learnRate);
                if(reachedBatchSize) {
                    layers[i].saveAdjustments(i, batchSize);
                }
            } else {
                layers[i].backpropagate(layers[i+1].inputs, learnRate);
                if(reachedBatchSize) {
                    layers[i].saveAdjustments(i, batchSize);
                }
            }
        }
    }
}