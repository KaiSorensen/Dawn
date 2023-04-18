import java.lang.Math;
import java.io.*;

/*
 * A LAYER OBJECT IS USED IN Dawn.java TO CONSTRUCT THE NEURAL NETWORK
 * Author: Kai
 */

public class Layer {

    public double[] inputs;
    public double[] outputs;

    public double[] preSigOutputs;

    public double[][] weights;
    public double[] biases;

    public double[][] weightAdjustments;
    public double[] biasAdjustments;
    public double[] inputAdjustments;

    public int batchIndex;
    
    public Layer(int numInputs, int numOutputs)
    {
        this.inputs = new double[numInputs];
        this.outputs = new double[numOutputs];
        this.preSigOutputs = new double[numOutputs];
        this.weights = new double[numInputs][numOutputs];
        this.biases = new double[numOutputs];
        this.weightAdjustments = new double[numInputs][numOutputs];
        this.biasAdjustments = new double[numOutputs];
        this.inputAdjustments = new double[numInputs];
        resetAdjustments();
    }

    //sets "adjustments" instance variables to 0;
    public void resetAdjustments() 
    {
        for(int i = 0; i < outputs.length; i++) {
            for(int j = 0; j < inputs.length; j++) {
                weightAdjustments[j][i] = 0;
            }
            biasAdjustments[i] = 0;
           
        }
        this.batchIndex = 0;
    }


    public double[] calculateOutputs(double[] newinputs) 
    {
        for(int i = 0; i < newinputs.length; i++) {
            inputs[i] = newinputs[i];
        }
        // this.inputs = inputs;
        for(int i = 0; i < outputs.length; i++)
        {
            outputs[i] = 0;
            for(int j = 0; j < inputs.length; j++)
            {
                outputs[i] += inputs[j] * weights[j][i];
            }
            outputs[i] += biases[i];
        }
        for(int i = 0; i < outputs.length; i++) {
            preSigOutputs[i] = outputs[i];
        }
        outputs = sigmoid(outputs);
        return outputs;
    }

    //applies the sigmoid function 1 / (1 + e^-x) to every element in the list and returns a new list
    public double[] sigmoid(double list[]) 
    {
        double newList[] = list;
        for(int i = 0; i < list.length; i++) 
        {
            newList[i] = 1 / (1 + Math.pow(Math.E, -list[i]));
        }
        return newList;
    }

    //returns the cost - function is (activation - expected)^2 added with every other activation's cost
    public double cost(int label)
    {
        double retVal = 0;
        for(int i = 0; i < outputs.length; i++) 
        {
            double expectedActivation = (i == label) ? 1.0 : 0.0;
            retVal += (outputs[i] - expectedActivation) * (outputs[i] - expectedActivation);
        }
        return retVal;
    }

    //value / sum of values
    public double[] setConfidence(double[] list) {
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

    // (e^-x) / (1 + e^-x)^2
    public double derivSig(double x) 
    {
        return (Math.pow(Math.E, -x)) / (Math.pow(1 + Math.pow(Math.E, -x), 2));
    }

    // 2(x - e)
    public double derivCost(double x, double expectation) 
    {
        return 2 * (x - expectation);
    }

    // // value / sum of values
    // public double derivConf(double x) {

    //     return 0;
    // }

    public double[] calculateError(double[] expectations) {
        double[] errors = new double[expectations.length];
        for(int i = 0; i < expectations.length; i++) {
            errors[i] = expectations[i] - outputs[i];
        }
        return errors;
    }

    //calculates all the adjustments
    public void backpropagate(double[] passover, double learnRate) {
        double[] inputsCopy = new double[inputs.length];
        for(int i = 0; i < inputs.length; i++) {
            inputsCopy[i] = inputs[i];
            inputs[i] = 0;
        }
        if(outputs.length == 10) {
            for(int i = 0; i < outputs.length; i++) {
                double derivBias = 1;
                double derivSig = derivSig(preSigOutputs[i]);
                double derivCost = derivCost(outputs[i], passover[i]);
                double finalDeriv = derivBias * derivSig * derivCost;
                biasAdjustments[i] -= finalDeriv * learnRate;
                for(int j = 0; j < inputs.length; j++) {
                    double derivWeight = inputsCopy[j];
                    finalDeriv = derivWeight * derivSig * derivCost;
                    weightAdjustments[j][i] -= finalDeriv * learnRate;

                    inputs[j] += derivSig * derivCost * weights[j][i];
                }
            }
        } else {
            for(int i = 0; i < outputs.length; i++) {
                double derivBias = 1;
                double derivSig = derivSig(preSigOutputs[i]);
                double derivPassed = passover[i];
                double finalDeriv = derivBias * derivSig * derivPassed;
                biasAdjustments[i] -= finalDeriv * learnRate;

                for(int j = 0; j < inputs.length; j++) {
                    double derivWeight = inputsCopy[j];
                    finalDeriv = derivWeight * derivSig * derivPassed;
                    weightAdjustments[j][i] -= finalDeriv * learnRate;
                    
                    inputs[j] += weights[j][i] * derivSig * passover[i];
                }
            }
        }
    }

    //saves weights and biases to a file for the layer
    public void saveAdjustments(int layerIndex, int batchSize) {
        try
		{
            FileWriter writer = new FileWriter("Wisdom"+ layerIndex + ".txt");
            BufferedWriter bw = new BufferedWriter(writer);
            
            //weights
            for (int i = 0; i < weights.length; i++)
            {
                for (int j = 0; j < weights[i].length; j++)
                {
                    weights[i][j] += (weightAdjustments[i][j] / batchSize);
                    String newWeight = weights[i][j] + "";
                    bw.write(newWeight);
                    //adding commas to separate values
                    if (j != weights[i].length - 1) {
                        bw.write(",");
                    }
                }
                bw.write("\n");
            }
            bw.write("\n");
            //biases
            for(int i = 0; i < biases.length; i++)
            {
                biases[i] += (biasAdjustments[i] / batchSize);
                String newBias = biases[i] + "";
                bw.write(newBias);
                //adding commas to separate values
                if (i != biases.length - 1){
                    bw.write(",");
                }
            }
            bw.write("\n");
            bw.close();
        
        } catch (IOException e)
		{
            e.printStackTrace();
        }
        resetAdjustments();
	} 
}