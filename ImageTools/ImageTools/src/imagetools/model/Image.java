package imagetools.model;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;

/**
 * This class houses an a file name and 3-D array integer that represents an image. This class has
 * two main functions. If a file path already exists, it renders the 3-D int[][][] array of the
 * existing image that will be used for processing.
 */
public class Image {


  /**
   * Image array.
   */
  private int[][][] imageArray;
  private String path;

  /**
   * This constructor creates an image that already exists. It will find the imageArray data so it
   * can be used for processing.
   *
   * @param path A valid file path.
   * @throws FileNotFoundException if file does not exist.
   */
  public Image(String path) throws IOException {
    if (!(new File(path).exists())) {
      throw new FileNotFoundException("Cannot instantiate, file path not found");

    }
    try {
      this.imageArray = new ImageUtill().readImage(path);
      this.path = path;
    } catch (IOException e) {
      throw new FileNotFoundException("Could not process file");
    }
  }

  /**
   * This constructor creates an image based on a new file path and new int[][][] data. It will
   * write the image to the file in the constructor. This can be the result of a newly generated
   * image that can now be used for processing.
   *
   * @param path A valid file path.
   * @throws FileNotFoundException if file does not exist.
   */
  public Image(String path, int[][][] imageArray) throws IOException {
    this.imageArray = imageArray;
    try {
      new ImageUtill().writeImage(imageArray, imageArray[0].length, imageArray.length, path);
    } catch (IOException e) {
      throw new IOException("Could not write to file");
    }

  }

  public int[][][] getImageArray() {
    return imageArray.clone();
  }

  public String getPath() {
    return path;
  }

  public int getLength() {
    return imageArray.length;
  }

}
