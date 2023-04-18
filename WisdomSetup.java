import java.io.*;
import java.util.Random;

 /*
  * RUNNNING THIS FILE RANDOMIZES ALL WEIGHTS AND BIASES
  * ALL TRAINING WILL BE LOST UNLESS SAVED ELSEWHERE
  */

public class WisdomSetup {

	//structure determines the number of layers and number of nodes in each
	//this must be equivalent to the structure set in Dawn.java
	public static int structure[] = {784,80,40,10};

	public static void main (String[]args) {

		try
		{
			System.out.println();
            for(int k = 0; k < structure.length - 1; k++)
            {
                FileWriter writer = new FileWriter("Wisdom"+ k + ".txt");
                BufferedWriter bw = new BufferedWriter(writer);
				Random rand = new Random();
                
                //printing new weights
                for (int i = 0; i < structure[k]; i++)
				{
					for (int j = 0; j < structure[k+1]; j++)
					{
						String newWeight = (rand.nextInt(3) - 1) + "";
						bw.write(newWeight);
						//adding commas to separate values
						if (j != structure[k+1] - 1) {
							bw.write(",");
						}
					}
					bw.write("\n");
				}
				bw.write("\n");
				//printing new biases
				for(int i = 0; i < structure[k+1]; i++)
				{
					String newBias = (rand.nextInt(3) - 1) + "";
					bw.write(newBias);
					//adding commas to separate values
					if (i != structure[k+1] - 1){
						bw.write(",");
					}
				}
				
				bw.close();
	
				System.out.println("---- WEIGHTS/BIASES RANDOMIZED ----");
            }
			System.out.println();
        } catch (IOException e)
		{
            e.printStackTrace();
        }
	}
}
