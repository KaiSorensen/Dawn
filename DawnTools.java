import java.io.*;
import java.util.Scanner;
import java.lang.Math;

/*
 * PROVIDES A TRAIN METHOD AND A TEST METHOD FOR DAWN
 * Author: Kai
 */

public class DawnTools {
    static double learnRate = 0.0001; //DECREASE THE LEARN-RATE AS ACCURACY IMPOVES
    static int batchSize = 80;

    //train by setting the indexes of the range of data points to train on
    //test by running test on the indexes of the range to test on
    public static void main(String[] args) {
        int index1 = 21002;
        int index2 = 30000;
        train(index1,index2);
        test(10,2000);
        // while(true) {
        //     if(index2 >= 60000) {
        //         index1 = 01002; index2 = 10000;
        //     } else {
        //         index1 += 10000; index2 += 10000;
        //     }
        //     train(index1,index2);
        //     test(1,1001);
        // }
        
    }
    
    //runs the neural network on a list of images between the two idexes and prints the numebr it gets correct
    static void test(int index1, int index2) {
        int totalCorrect = 0;
        float percentCorrect = 0;
        try 
        {
            File file = new File("mnist_train.csv");
            Scanner fileScan = new Scanner(file);

            for(int i = 0; i < index1; i++) {
                fileScan.nextLine();
            }

            Dawn brain = new Dawn();
            System.out.println();
            for(int i = 0; i < index2-index1; i++) {
                Scanner lineScan = new Scanner(fileScan.nextLine());
                lineScan.useDelimiter(",");

                int label;
                int[] pixels = new int[784];
                
                //initialize variables
                label = lineScan.nextInt();
                for(int j = 0; j < 784; j++) {
                    pixels[j] = lineScan.nextInt();
                }
                int result = brain.wildGuess(pixels);

                if(i % 30 == 0) {
                    if (result == label) {
                        System.out.println("RIGHT (" + label +") " + brain.layers[brain.layers.length - 1].cost(label));
                    } else {
                        System.out.println("WRONG (" + label +") " + brain.layers[brain.layers.length - 1].cost(label));
                    }
                }

                if(result == label) {
                    totalCorrect++;
                }
            }
            fileScan.close();

            percentCorrect = (float) totalCorrect / (index2 - index1);
            percentCorrect = (float) (Math.round(percentCorrect * 1000.0) / 10.0);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("\nCorrect: " + totalCorrect + "/" + (index2-index1));
        System.out.println("Percentage: " + percentCorrect + "%\n");
    }

    //runs the neural network on a list of images between the two indexes and learns for next time
    static void train(int index1, int index2) {
        try {
            
            File file = new File("mnist_train.csv");
            Scanner fileScan = new Scanner(file);
            // Random rand = new Random();
            
            for(int i = 0; i < index1; i++) {
                fileScan.nextLine();
            }
            Dawn brain = new Dawn();

            int batchIndex = 1;
            for(int i = 0; i < index2-index1; i++) {
                Scanner lineScan = new Scanner(fileScan.nextLine());
                lineScan.useDelimiter(",");

                int label;
                int[] pixels = new int[784];
                //initialize new training data
                label = lineScan.nextInt();
                for(int j = 0; j < 784; j++) {
                    pixels[j] = lineScan.nextInt();
                }
                //here we go
                boolean reachedBatchSize = false;
                if(batchIndex == batchSize) {
                    reachedBatchSize = true;
                }

                brain.wildGuess(pixels);
                brain.fullBackprop(label, learnRate, reachedBatchSize, batchSize);
            
                if(batchIndex == batchSize) {
                    batchIndex = 1;
                }
                reachedBatchSize = false;
                batchIndex++;

                if(i == (index2-index1) / 4 * 3) {
                    System.out.print("...75%...");
                } else if (i == (index2-index1) / 2) {
                    System.out.print("...50%...");
                } else if (i == (index2-index1) / 4) {
                    System.out.print("...25%...");
                } 
            }
            fileScan.close();
            System.out.println("TRAINING COMPLETE");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
