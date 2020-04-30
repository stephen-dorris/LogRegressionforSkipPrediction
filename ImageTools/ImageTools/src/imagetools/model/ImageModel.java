package imagetools.model;

import java.io.IOException;

/**
 * This is an Interface for a Model that performs different operations on a image, which is
 * represented as a 3-D integer array. Each operation returns mutates this current image array being
 * processed. To save an image, there is a save method.
 */
public interface ImageModel {

  public int[][][] getImage();

  /**
   * Saves an the current image array (that has been processed by model) to the file path
   * specified.
   *
   * @throws IOException if file writing cannot happen.
   */
  void save(String s) throws IOException;

  /**
   * Loads a new picture to the model.
   *
   * @param s path to a picture to load
   * @throws IOException iff file path cannot be found.
   */
  void loadImage(String s) throws IOException;

  /**
   * Perform a filtering operation on an image, uses kernel matricies.
   *
   * @param op Specifies the type of filtering needed.
   * @return Image that is the result of filtering on the image stored in the model
   * @throws IllegalArgumentException Model has no image to process.
   */
  void filter(FilterOp op) throws IllegalArgumentException;

  /**
   * Perform a color transformation operation on an image, using a linear combination of rgb
   * values.
   *
   * @param op Specifies the new "tone" of the image.
   * @return Image that is the result of filtering on the image stored in the model.
   * @throws IllegalArgumentException Model has no image to process.
   */
  void colorTransform(ColorOp op) throws IllegalArgumentException;


  /**
   * Performs a dithering operation on an image, using Floyd-Steinberg algorithm.
   *
   * @return a newly created image as result of greyscale and then dithering.
   */
  void dither() throws IllegalArgumentException;

  /**
   * Performs mosaic clustering operation on an image.
   *
   * @param seedCt the amount of "mosaic" clusters in image (the more, the clearer);
   * @return newly created mosaic image.
   * @throws IllegalArgumentException when size of seedCt is greater than number of pixels in
   *                                  image.
   */
  void mosaic(int seedCt) throws IllegalArgumentException;

  /**
   * Generate from scratch a 8x8 checkerboard pattern image where the squares are a certain size, in
   * pixels.
   *
   * @param squareSize The integer size (in pixels) of one square.
   * @return a new Checkerboard Image.
   * @throws IllegalArgumentException pif square size &lt; 20 pixels and &gt; than 400 pixels.
   */
  void generateCheckerboard(int squareSize) throws IllegalArgumentException;

  /**
   * Generate from scratch a Column Rainbow pattern image with all 7 standard rainbow colors. The
   * size of the columns in pixels are an argument.
   *
   * @param width    represents the width of the picture.
   * @param height   represents the height of the picture.
   * @param position represents the position of rainbow stripes on a picture.
   * @return a new Rainbow pattern image.
   * @throws IllegalArgumentException with and/or height is &lt; 160 pixels is &gt; than 3200 *
   *                                  pixels.
   */
  void generateRainbow(int width, int height, RainbowPosn position) throws IllegalArgumentException;

  /**
   * Tries to the most recent non-save operation done on an image (if exists) in model command
   * history. If successful, then the model's current image is set to  the previous state.
   *
   * @throws IllegalStateException if there is no more commands to undo, (ie. there is no image
   *                               housed in model.
   */
  void undo() throws IllegalStateException;

  /**
   * Tries to redo morst recent undone non-save operation that has been previously undone. If
   * successful, the model's current image is set to the redone state.
   *
   * @throws IllegalStateException if the state is at the most recent version.
   */
  void redo() throws IllegalStateException;



  /**
   * Exits the model, saves current processed image.l
   */
  void exit();


}
