# DAWN
 * Author: Kai Sorensen
 * Language: Java

## Overview
  Dawn is a ground-up implementation of a basic neural network trained
  to recognize single handwritten digits from the standard Mnist dataset.
  It can correctly identify about 95% of data within the dataset that it 
  hasn't seen before. This project also inludes a GUI to see exactly what
  digit it's identifying, and how confident it is.

## Reflection
  I'm writing this about 4 months after I last touched this project.
  Dawn was my second serious personal project, which I took on during
  my last winter break from school. It took me about a week to complete,
  essentially working full time on it. As it was my first introduction
  to the engineering of a neural network, it took some effort to wrap
  my head around backpropogation. I personally enjoyed seeing the presence
  of calculus in training phase. In retrospect, I believe I gained an
  intuitive understanding of the nature of neural networks. The more we
  understand the machanics behind magic, the more magical the world becomes.

  There's more that I'd like to add to this project. For example, I'd like
  to add functionality to draw my own digits for it to identify. I'd also
  like to add more variation to the training data. Maybe I'll get to this
  in the future, but for now I'm at a stopping point until my priorities
  shift back to this.

## Included Files
  Dawn.java
  Layer.java
  DawnTools.java
  Dawndow.java
  WisdomSetup.java

  Wisdom.txt
  Wisdom0.txt
  Wisdom1.txt
  Wisdom2.txt
  Wisdom3.txt
  Wisdom4.txt

  mnist_train.csv
  train.csv
  
## How to Run

  First, compile all files
    from command line: 
    ```
    $ javac *.java
    ```

  For GUI experience, run Dawndow
    from command line: 
    ```
    $ java Dawndow
    ```

  To train and test, run DawnTools.java with hardcoded data range
    from command line: set data range: 
    ```
    vim DawnTools.java
    ```
    from command line: recompile: 
    ```
    javac DawnTools.java
    ```
    from command line: 
    ```
    java DawnTools
    ```
    
  TO ERASE AND RANDOMIZE WEIGHTS AND BIASES
    run WisdomSetup.java
    ```
    java WisdomSetup
    ```
