package imagetools.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class represents a mock model class that makes sure the imagetools.controller calls the
 * right batch commands in a file, or by the view events. It writes to a mock.txt file in the order
 * that the command called the model, writing a specifically formatted string based on the function
 * called, and the arguments. This allows the user to test a certain batch file, and compare the
 * expected Controller-> Model calls based on the mock.txt file.
 */
public class ImageMockModel implements ImageModel {

  FileWriter mockFileWriter;

  /**
   * Instantiate Mock Model Object, try to open a mock.txt file.
   *
   * @throws IOException if mock.txt cant be accessed.
   */
  public ImageMockModel() throws IOException {
    File f = new File("mock.txt");
    f.delete();

    try {
      mockFileWriter = new FileWriter("mock.txt");
      mockFileWriter.flush(); // Flush stream to make sure nothing is there
    } catch (IOException e) {
      throw new IOException("Cannot write to mock.txt");
    }


  }

  @Override
  public int[][][] getImage() {
    return new int[0][][];
  }

  @Override
  public void save(String s) throws IOException {
    try {
      mockFileWriter.append("saves " + s + "\n");
      mockFileWriter.flush();
    } catch (IOException e) {
      //
      System.out.println("exception");
    }
  }

  @Override
  public void loadImage(String s) {
    try {
      mockFileWriter.append("loaded " + s + "\n");
      mockFileWriter.flush();
    } catch (IOException e) {
      //

      System.out.println("exception");
    }
  }

  @Override
  public void filter(FilterOp op) {

    try {
      mockFileWriter.append("filter-" + op + "\n");
      mockFileWriter.flush();
    } catch (IOException e) {
      System.out.println("exception");
    }
  }

  @Override
  public void colorTransform(ColorOp op) {
    try {

      mockFileWriter.append("color-" + op + "\n");
      mockFileWriter.flush();
    } catch (IOException e) {
      System.out.println("exception");
    }

  }

  @Override
  public void dither() {
    try {
      mockFileWriter.append("dither-" + "\n");
      mockFileWriter.flush();

    } catch (IOException e) {
      System.out.println("exception");
    }

  }

  @Override
  public void mosaic(int seedCt) throws IllegalArgumentException {
    try {
      mockFileWriter.append("mosaic-" + seedCt + "\n");
      mockFileWriter.flush();

    } catch (IOException e) {
      System.out.println("exception");

    }
  }

  @Override
  public void generateCheckerboard(int squareSize) throws IllegalArgumentException {
    try {
      mockFileWriter.append("checkerboard-" + squareSize + "-" + "\n");
      mockFileWriter.flush();

    } catch (IOException e) {
      System.out.println("exception");

    }
  }

  @Override
  public void generateRainbow(int width, int height, RainbowPosn position)
          throws IllegalArgumentException {
    try {
      mockFileWriter.append("rainbow-" + width + "-" + height + "-" + position + "\n");
      mockFileWriter.flush();
    } catch (IOException e) {
      System.out.println("exception");
    }
  }

  @Override
  public void undo() throws IllegalStateException {

  }

  @Override
  public void redo() throws IllegalStateException {

  }

  @Override
  public void exit() {
    try {
      mockFileWriter.close();
    } catch (IOException e) {
      //
    }

  }

}
