import javax.swing.*;
import java.awt.*;
import java.io.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Scanner;

/*
 * THIS CLASS CREATES A GUI OUTPUT FOR THE DIGITS THAT THE AI IS IDENTIFYING
 * uses Java library graphics classes to create GUI
 * Author: Kai
 */


public class Dawndow extends JPanel
{
  public String theFont = "Ariel";

  public int[] pixels = new int[784];
  public int label;
  public boolean correct;
  public double[] confidence;
  public String[] confidenceStrings;

  public ImageGraphic imagePanel;
  public ConfidenceGraphic eastSide;
  public LabelGraphic northSide;
  public JPanel southSide;

  public JButton findNew;
  public JButton findWrong;



	public static void main (String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	  frame.getContentPane().add(new Dawndow());
		frame.pack();
		frame.setVisible(true);
	}
	
	
  //constructor
	public Dawndow()
	{
    resetPixels();
    
    imagePanel = new ImageGraphic();
    eastSide = new ConfidenceGraphic();
    northSide = new LabelGraphic();
    southSide = new JPanel();
    eastSide.setPreferredSize(new Dimension(250,600));
    northSide.setPreferredSize(new Dimension(600, 100));
    southSide.setBackground(Color.BLACK);

    findNew = new JButton("NEW");
    findNew.setBackground(Color.BLUE);
    findNew.setForeground(Color.BLACK);
    findNew.addActionListener(new NewListener());

    findWrong = new JButton("WRONG");
    findWrong.setBackground(Color.YELLOW);
    findWrong.setForeground(Color.BLACK);
    findWrong.addActionListener(new WrongListener());

    southSide.add(findNew);
    southSide.add(findWrong);

		int initWidth = 800;
		int initHeight = 700;
		setPreferredSize(new Dimension(initWidth, initHeight));
		this.setDoubleBuffered(true);
    this.setLayout(new BorderLayout());

    this.add(imagePanel, BorderLayout.CENTER);
    this.add(northSide, BorderLayout.NORTH);
    this.add(eastSide, BorderLayout.EAST);
    this.add(southSide, BorderLayout.SOUTH);
	}

  private void resetPixels() {
    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = 0;
    }
    confidence = new double[10];
    confidenceStrings = new String[10];
    for (int i = 0; i < confidence.length; i++) {
      confidence[i] = 0;
      confidenceStrings[i] = "NULL";
    }
    label = -1;
    correct = false;
  }

  private void setConfidence(double[] list) {
    for (int i = 0; i < list.length; i++) {
      confidence[i] = Math.round(list[i] * 10000) / 100.0;
    }

    int[] indices = new int[confidence.length];

    for (int i = 0; i < confidence.length; i++) {
      indices[i] = i;
    }

    for (int i = 0; i < confidence.length; i++) {
      for (int j = i + 1; j < confidence.length; j++) {
        if (confidence[j] >= confidence[i]) {
          double temp = confidence[i];
          confidence[i] = confidence[j];
          confidence[j] = temp;

          int tempIndex = indices[i];
          indices[i] = indices[j];
          indices[j] = tempIndex;
        }
      }
    }

    for(int i = 0; i < confidence.length; i++) {
      if(confidence[i] < 10) {     
        confidenceStrings[i] = indices[i] + ": 0" + confidence[i] + "%";
      } else {
        confidenceStrings[i] = indices[i] + ": " + confidence[i] + "%";
      }
    }

  }

  private class ImageGraphic extends JPanel
  {
    public void paintComponent(Graphics g)
	  {
      int width = getWidth(); 
      int height = getHeight(); 

      int imageWidth = (width > height) ? height : width;
      imageWidth = imageWidth/28 * 28;
      int squareWidth = imageWidth/28;
      
      g.fillRect(0, 0, width, height);

      int pixelIndex = 0;

      for(int i = 0; i < imageWidth; i+=squareWidth) {
        for(int j = 0; j < imageWidth; j+=squareWidth) {
          int pixelValue = pixels[pixelIndex];
          g.setColor(new Color(pixelValue,pixelValue,pixelValue));
          g.fillRect(j, i, squareWidth, squareWidth);
          pixelIndex++;
        }
      }
      Toolkit.getDefaultToolkit().sync();
	  }
  }

  private class ConfidenceGraphic extends JPanel {

    public void paintComponent(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(Color.WHITE);
      Font stringFont = new Font(theFont, Font.PLAIN, 50);
      g.setFont(stringFont);

      int y = 70;
      int yOffset = 50;
      for(int i = 0; i < 10; i++) {
        g.drawString(confidenceStrings[i], 0, y);
        y+=yOffset;
      }
      

    }
  }

  private class LabelGraphic extends JPanel {
    public void paintComponent(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(Color.WHITE);

      Font stringFont = new Font(theFont, Font.BOLD, 50);
      g.setFont(stringFont);

      g.drawString("Label: " + label, 20, 60);

      String grade;
      if(correct) {
        grade = "RIGHT";
        g.setColor(Color.GREEN);
      } else {
        grade = "WRONG";
        g.setColor(Color.RED);
      }
      g.drawString(grade, 300, 60);
    }
  }

  private class WrongListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

      Random rand = new Random();
      int picDex = rand.nextInt(5000);

      try 
        {
            File file = new File("mnist_train.csv");
            Scanner fileScan = new Scanner(file);

            for(int i = 0; i < picDex; i++) {
                fileScan.nextLine();
            }

            Dawn brain = new Dawn();
            boolean unfound = true;
            while(unfound) {
                Scanner lineScan = new Scanner(fileScan.nextLine());
                lineScan.useDelimiter(",");
                
                //initialize variables
                label = lineScan.nextInt();
                for(int j = 0; j < 784; j++) {
                    pixels[j] = lineScan.nextInt();
                }

                int guess = brain.wildGuess(pixels);
                if(guess != label) {
                  unfound = false;
                  
                  correct = false;
                  setConfidence(brain.getConfidence(brain.layers[brain.layers.length - 1].outputs));
                }
            }
            fileScan.close();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        northSide.repaint();
        eastSide.repaint();
        imagePanel.repaint();
    }

  }

  private class NewListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {

      Random rand = new Random();
      int picDex = rand.nextInt(5000);

      try 
        {
            File file = new File("mnist_train.csv");
            Scanner fileScan = new Scanner(file);

            for(int i = 0; i < picDex; i++) {
                fileScan.nextLine();
            }

            Dawn brain = new Dawn();
           
            Scanner lineScan = new Scanner(fileScan.nextLine());
            lineScan.useDelimiter(",");

            //initialize variables
            label = lineScan.nextInt();
            for(int j = 0; j < 784; j++) {
                pixels[j] = lineScan.nextInt();
            }

            int guess = brain.wildGuess(pixels);
            correct = (guess == label) ? true : false;

            setConfidence(brain.getConfidence(brain.layers[brain.layers.length - 1].outputs));
            
            fileScan.close();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        northSide.repaint();
        eastSide.repaint();
        imagePanel.repaint();
    }
  }
}
