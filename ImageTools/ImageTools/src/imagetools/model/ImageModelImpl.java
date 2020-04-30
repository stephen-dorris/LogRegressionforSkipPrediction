package imagetools.model;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Deque;


/**
 * This class represents a model that can perform picture filtering, color transformation and
 * rainbow and checkerboard image generation. The model allows for processing on its Image
 * attribute, but also creates Images from scratch. This model also can keep track of versions
 * history, while also loading and saving local file images.
 */
public class ImageModelImpl implements ImageModel {

  /**
   * his is the current image being worked on represented as a 3-D array. Once an image is loaded
   * or passed in as constructor, the 3-D array will be a copy of the currentImage specs, so no
   * overwriting happens.
   */
  private int[][][] currentImage;


  /**
   * The top of this stack always holds the models "current image". If an undo operation is
   * performed, then undo stack is popped and the array is send to the redo stack to hold if there
   * is a redo done.
   */
  private Deque<int[][][]> undoStack;

  /**
   * The top of this stack always holds the most recent "undone" action, with any older versions
   * being held near the bottom of the stack. If this stack is empty, that means that the
   * currentImage is the most recent version. If a redo operation is performed, then redo stack is
   * popped, and the array is sent to the undo stack.
   */
  private Deque<int[][][]> redoStack;


  /**
   * Constructor for the ImageModel tool. This tool will be able to process the image that is set as
   * the argument to constructor.
   *
   * @param image Image passed in can be processed by the Image Model.
   */
  public ImageModelImpl(Image image) {
    int[][][] arr = image.getImageArray();
    this.currentImage = arr;
    this.undoStack = new ArrayDeque<>();
    this.redoStack = new ArrayDeque<>();
    undoStack.push(arr);
  }

  /**
   * Constructor for an Image tool that will only generate images. Calling methods that process
   * images using this model will cause Exceptions.
   */
  public ImageModelImpl() {
    this.currentImage = null;
    this.undoStack = new ArrayDeque<>();
    this.redoStack = new ArrayDeque<>();
  }

  /**
   * This appropriately updates the model state, by clearing the redo stack, and updating the
   * current image with the most recent version.When an operation is performed, then the the ability
   * to "redo" is cleared. The only purpose for a redo stack is to hold previous states that you
   * have "undone".
   */
  private void updateImageState(int[][][] currentImage) {
    this.redoStack.clear();
    this.undoStack.push(currentImage);
    System.out.println(undoStack.size());
    this.currentImage = currentImage;
  }


  @Override
  public int[][][] getImage() {
    return this.currentImage.clone();
  }

  @Override
  public void save(String s) throws IOException {
    try {
      new Image(s, this.currentImage);
    } catch (IOException e) {
      throw new IOException("Could not save currentImage\n");
    }
  }

  @Override
  public void loadImage(String s) throws IOException {
    try {
      updateImageState(new Image(s).getImageArray());

    } catch (IOException e) {
      throw new IOException("Could not load an currentImage with file name give\n");
    }
  }


  @Override
  public void undo() throws IllegalStateException {
    if (this.undoStack.isEmpty()) {
      throw new IllegalStateException("Cannot undo, either no operations done, or all operations" +
              "have been undone\n");
    } else {
      // Save undone image to redo stack to hold if a redo option is performed.
      int[][][] undoneImage = undoStack.pop();
      System.out.println("popping from stack, size of stack now is: "+undoStack.size());
      redoStack.push(undoneImage);

      // Current image is top of undo stack (when not empty);
      if (!undoStack.isEmpty()) {
        this.currentImage = undoStack.peek();
      }
      // Set current image to be empty when undo stack is empty (means that redo stack is full).
      else {
        this.currentImage = new int[][][]{{{0,0,0}}}; // empty image.
      }
    }


  }

  @Override
  public void redo() throws IllegalStateException {
    if (this.redoStack.isEmpty()) {
      throw new IllegalStateException("Cannot redo once a new operation is performed, " +
              "can only redo" + "what has been undone.");
    } else {
      int[][][] redoneImage = redoStack.pop();
      undoStack.push(redoneImage);
      updateImageState(undoStack.peek());

    }
  }


  @Override
  public void filter(FilterOp op) throws IllegalArgumentException {
    if (this.currentImage == null) {
      throw new IllegalArgumentException("No currentImage object to process");
    }
    if (op == FilterOp.Blur) {
      updateImageState(filterApply(Kernel.blur));
    } else {
      updateImageState(filterApply(Kernel.sharpen));
    }

  }

  //Applies filter for RGB values of every pixel.
  private int[][][] filterApply(double[][] kernel) throws IllegalArgumentException {
    if (this.currentImage == null) {
      throw new IllegalArgumentException("No currentImage object to process");
    }

    int[][][] imageCpy = currentImage.clone();

    int[][][] filteredImage = new int[imageCpy.length][imageCpy[0].length][3];

    int row;
    int column;
    int color;

    for (row = 0; row < imageCpy.length; row++) {
      for (column = 0; column < imageCpy[0].length; column++) {
        for (color = 0; color < 3; color++) {
          filteredImage[row][column][color] = kernelApply(row, column, color, kernel, imageCpy);

        }
      }
    }
    return clamp(filteredImage);

  }

  //The algorithm of applying kernel on RGB values of a pixel.
  private int kernelApply(int rowArg, int columnArg, int color, double[][] kernel, int[][][] arr) {
    double sum = 0;
    int i;
    int j;
    i = 0;
    for (int row = rowArg - (kernel.length / 2); i < kernel.length; row++, i++) {
      j = 0;
      for (int column = columnArg - (kernel.length / 2); j < kernel.length; column++, j++) {
        if (inBounds(row, column, arr)) {
          sum += kernel[i][j] * arr[row][column][color];
        }
      }
    }
    return (int) sum;
  }

  //Clamps RGB values, if the value is more than 255 it stays at 255, if the value is negative it
  //stays at 0.
  private int[][][] clamp(int[][][] image) {
    for (int i = 0; i < image.length; i++) {
      for (int j = 0; j < image[0].length; j++) {
        for (int k = 0; k < 3; k++) {
          if (image[i][j][k] > 255) {
            image[i][j][k] = 255;
          } else if (image[i][j][k] < 0) {
            image[i][j][k] = 0;
          }
        }
      }
    }

    return image;
  }

  //Checks if the pixel is in bounds, determines edge cases.
  private boolean inBounds(int row, int column, int[][][] arr) {
    return (row < arr.length - 1 && row >= 0)
            && (column < arr[0].length - 1 && column >= 0);
  }


  @Override
  public void colorTransform(ColorOp op) throws IllegalArgumentException {
    if (this.currentImage == null) {
      throw new IllegalArgumentException("No currentImage to process\n");
    }

    if (op == ColorOp.GrayScale) {
      updateImageState(applyColor(new double[][]{
              {0.2126, 0.7152, 0.0722},
              {0.2126, 0.7152, 0.0722},
              {0.2126, 0.7152, 0.0722}}));

    } else {
      updateImageState(applyColor(new double[][]{
              {0.393, 0.769, 0.189},
              {0.349, 0.686, 0.168},
              {0.272, 0.534, 0.131},
      }));
    }
  }

  //Iterator. Applies color transformation on every pixel.
  private int[][][] applyColor(double[][] transformation) {
    int[][][] imageCpy = currentImage.clone();
    int[][][] transformedImage = new int[imageCpy.length][imageCpy[0].length][3];
    for (int row = 0; row < currentImage.length - 1; row++) {
      for (int column = 0; column < imageCpy[0].length - 1; column++) {
        transformedImage[row][column] = applyTransform(imageCpy[row][column], transformation);
      }
    }
    return clamp(transformedImage);

  }

  //The algorithms of transforming RGB values of every pixel.
  private int[] applyTransform(int[] colors, double[][] transformation) {
    int[] temp = new int[3];

    for (int i = 0; i < 3; i++) {
      temp[i] = (int) Math.round(colors[0] * transformation[i][0] + colors[1] *
              transformation[i][1] +
              colors[2] * transformation[i][2]);
    }
    return temp;
  }

  @Override
  public void generateRainbow(int width, int height, RainbowPosn position) {

    int[] red = new int[]{255, 0, 0};
    int[] orange = new int[]{255, 127, 0};
    int[] yellow = new int[]{255, 255, 0};
    int[] green = new int[]{0, 255, 0};
    int[] blue = new int[]{0, 0, 250};
    int[] indigo = new int[]{75, 0, 130};
    int[] violet = new int[]{148, 0, 211};

    int[][] colors = new int[7][3];
    colors[0] = red;
    colors[1] = orange;
    colors[2] = yellow;
    colors[3] = green;
    colors[4] = blue;
    colors[5] = indigo;
    colors[6] = violet;

    int[][][] rainbow = new int[height][width][3];
    int thickness;

    if (position.equals(RainbowPosn.Vertical)) {
      if (width % 7 != 0) {
        thickness = (int) Math.ceil((double) width / 7.0);
      } else {
        thickness = width / 7;
      }
      updateImageState(drawRainbowV(rainbow, thickness, colors));
    } else {
      if (height % 7 != 0) {
        thickness = (int) Math.ceil((double) height / 7.0);
      } else {
        thickness = height / 7;
      }
      updateImageState(drawRainbowH(rainbow, thickness, colors));

    }
  }


  //generates rainbow with vertical stripes.
  private int[][][] drawRainbowV(int[][][] rainbow, int thickness, int[][] colors) {
    int currentColor = 0;

    for (int i = 0; i < rainbow.length; i++) {
      currentColor = 0;
      for (int j = 0; j < rainbow[0].length; j++) {
        if (j % thickness == 0 && j > 0) {
          currentColor++;
        }
        rainbow[i][j] = colors[currentColor];
      }
    }
    return rainbow;
  }

  //generates rainbow with horizontal stripes.
  private int[][][] drawRainbowH(int[][][] rainbow, int thickness, int[][] colors) {
    int currentColor = 0;

    for (int i = 0; i < rainbow.length; i++) {
      if (i % thickness == 0 && i > 0) {
        currentColor++;
      }
      for (int j = 0; j < rainbow[0].length; j++) {
        rainbow[i][j] = colors[currentColor];
      }
    }
    return rainbow;

  }


  @Override
  public void generateCheckerboard(int squareSize) throws IllegalArgumentException {
    if (squareSize <= 10 || squareSize >= 400) {
      throw new IllegalArgumentException("Square size should be more or equals to 10 and " +
              "less or equals to 400 pixels");
    }

    int[][][] board = new int[squareSize * 8][squareSize * 8][3];
    int[] red = new int[]{212, 27, 44};
    int[] black = new int[]{0, 0, 0};
    int currentColor = 0;

    for (int i = 0; i < board.length; i++) {
      //change current "start" color of each row.
      if (i % squareSize == 0) {
        if (currentColor == 0) {
          currentColor = 1;
        } else {
          currentColor = 0;
        }
      }

      for (int j = 0; j < board[0].length; j++) {
        if (j % squareSize == 0) {
          if (currentColor == 0) {
            currentColor = 1;
          } else {
            currentColor = 0;
          }
        }
        if (currentColor == 0) {
          board[i][j] = black;
        } else {
          board[i][j] = red;
        }
      }
    }

    updateImageState(board);

  }


  @Override
  public void dither() {
    int rows_count = currentImage.length;
    int columns_count = currentImage[0].length;

    int old_color;
    int new_color;
    int error;

    int[][][] dithered = new int[rows_count][columns_count][3];

    this.colorTransform(ColorOp.GrayScale); // this adds to the undostack. remove it from undostack.
    this.undoStack.pop();

    for (int i = 0; i < rows_count; i++) {
      for (int j = 0; j < columns_count; j++) {
        old_color = this.currentImage[i][j][0];

        if (old_color <= 127) {
          new_color = 0;
        } else {
          new_color = 255;
        }

        error = old_color - new_color;

        dithered[i][j] = new int[]{new_color, new_color, new_color};
        addError(i, j, error, this.currentImage);
      }
    }
    updateImageState(dithered);
  }

  private void addError(int row, int column, int error, int[][][] grayscaleArr) {
    int added_err;
    int temp_row;
    int temp_col;

    temp_row = row;
    temp_col = column + 1;
    if (this.inBounds(temp_row, temp_col, grayscaleArr)) {
      added_err = (int) ((7.0 / 16.0) * error);
      grayscaleArr[temp_row][temp_col] = addErrorAdded(grayscaleArr[temp_row][temp_col], added_err);
    }

    temp_row = row + 1;
    temp_col = column - 1;
    if (this.inBounds(temp_row, temp_col, grayscaleArr)) {
      added_err = (int) ((3.0 / 16.0) * error);
      grayscaleArr[temp_row][temp_col] = addErrorAdded(grayscaleArr[temp_row][temp_col], added_err);
    }

    temp_row = row + 1;
    temp_col = column;
    if (this.inBounds(temp_row, temp_col, grayscaleArr)) {
      added_err = (int) ((5.0 / 16.0) * error);
      grayscaleArr[temp_row][temp_col] = addErrorAdded(grayscaleArr[temp_row][temp_col], added_err);
    }

    temp_row = row + 1;
    temp_col = column + 1;
    if (this.inBounds(temp_row, temp_col, grayscaleArr)) {
      added_err = (int) ((1.0 / 16.0) * error);

      grayscaleArr[temp_row][temp_col] = addErrorAdded(grayscaleArr[temp_row][temp_col], added_err);
    }
  }

  private int[] addErrorAdded(int[] rgb, int addedError) {
    return new int[]{rgb[0] + addedError, rgb[1] + addedError, rgb[2] + addedError};
  }


  @Override
  public void mosaic(int seedCt) throws IllegalArgumentException {

    if (currentImage.length * currentImage[0].length < seedCt) {
      throw new IllegalArgumentException("Cannot have seed count greater than pixel count\n");
    }
    // pseudo-code//

    // generate seed-positions that are MUTEX.
    int[][] seeds = getSeeds(seedCt);

    // figure out cluster for each individual pixel. cluster # corresponds to the seed index in
    // seed array.
    int[][][] clusters = clusterize(seeds);


    Map<Integer, Integer[]> averageRGB = averageRGB(clusters, seeds);


    // find average rgb
    // return new currentImage with updated int [][][]

    updateImageState(makeMosaic(clusters, averageRGB));

  }

  private int[][][] makeMosaic(int[][][] clusters, Map<Integer, Integer[]> averageRGB) {
    int[][][] mosaic = new int[currentImage.length][currentImage[0].length][3];
    for (int row = 0; row < currentImage.length; row++) {
      for (int col = 0; col < currentImage[0].length; col++) {
        int cluster = clusters[row][col][0];
        Integer[] rgb = averageRGB.get(cluster);
        mosaic[row][col] = new int[]{rgb[0], rgb[1], rgb[2]};
      }
    }
    return mosaic;

  }


  ///finds average rgb for each cluster.
  private Map<Integer, Integer[]> averageRGB(int[][][] clusters, int[][] seeds) {
    Map<Integer, Integer[]> averageRBG = new HashMap<>();
    int[][] clusterSpecs;  // [cluster number (index by seeds)][number in cluster][total r, g,b]
    clusterSpecs = new int[seeds.length][3];

    for (int seed = 0; seed < seeds.length; seed++) {
      int numInCluster = 0;
      for (int row = 0; row < currentImage.length; row++) {
        for (int col = 0; col < currentImage[0].length; col++) {
          // if we've found the seed at pixel (row,col) then add info to cluster specs.

          if (clusters[row][col][0] == seed) {
            clusterSpecs[seed][0] += currentImage[row][col][0];
            clusterSpecs[seed][1] += currentImage[row][col][1];
            clusterSpecs[seed][2] += currentImage[row][col][2];
            numInCluster++;

          }
        }
      }
      Integer averageR = clusterSpecs[seed][0] / numInCluster;
      Integer averageG = clusterSpecs[seed][1] / numInCluster;
      Integer averageB = clusterSpecs[seed][2] / numInCluster;
      averageRBG.put(seed, new Integer[]{averageR, averageG, averageB});
    }
    return averageRBG;
  }


  /**
   * Takes in seeds provided (in the form [indexOfseed][x,y]) and for each pixel in currentImage
   * find seed it belongs to. Element one will be cluster, element 2 is x and y tuple.
   *
   * @param seeds seeds provided (in the orm [indexOfseed][x of seed ,y of seed])
   * @return clusters in the form of [row][column][cluster #, corresponding to seed index.]
   */
  private int[][][] clusterize(int[][] seeds) {
    int[][][] arr = this.currentImage;
    int[][][] clusters = new int[currentImage.length][currentImage[0].length][1];
    for (int row = 0; row < arr.length; row++) {
      for (int col = 0; col < arr[0].length; col++) {
        // set current x y as  single array of length 2.
        Integer[] position = new Integer[]{row, col};

        // assume best cluster is 0;
        int cluster = 0;

        // assume smallest distance (which determines best cluster) is from seed 0;
        double minDist = euclidDistance(position, seeds[0]);

        // find seed with smallest distance
        for (int seedInd = 1; seedInd < seeds.length; seedInd++) {
          double testDistance = euclidDistance(position, seeds[seedInd]);
          if (testDistance < minDist) {
            minDist = testDistance;
            cluster = seedInd;
          }
        }
        clusters[row][col][0] = cluster;

      }

    }
    return clusters;
  }


  /**
   * Computes euclidian distance between two (x,y) coordinates on currentImage for pixel
   * clustering.
   *
   * @param key      Integer (x,y) the pixel that is being currently clustered.
   * @param testSeed int (x,y) for a single seed of  which the distance is being queried.
   * @return double representing distance between key and test seed.
   */
  public double euclidDistance(Integer[] key, int[] testSeed) {
    double xDist = Math.pow(key[0] - testSeed[0], 2);
    double yDist = Math.pow(key[1] - testSeed[1], 2);
    return Math.sqrt(xDist + yDist);
  }


  // Get a randomized and distinct array of seeds.
  private int[][] getSeeds(int seedCt) {
    Random rn = new Random();
    // generate seed-positions
    int xTemp;
    int yTemp;
    int[][] seeds = new int[seedCt][2];
    int numFound = 0;
    while (numFound < seedCt) {
      xTemp = Math.abs(rn.nextInt() % currentImage.length);
      yTemp = Math.abs(rn.nextInt() % currentImage[0].length);

      if (!taken(xTemp, yTemp, seeds, numFound)) {
        seeds[numFound] = new int[]{xTemp, yTemp};
        numFound++;
      }

    }
    return seeds;
  }


  /**
   * Goes through array up to number of seeds found, and checks to make sure x/y combo doesnt
   * exist.
   *
   * @return true when xy combo has been taken as seed.
   */
  private boolean taken(int x, int y, int[][] seeds, int numFound) {
    for (int i = 0; i < numFound; i++) {
      if (x == seeds[i][0] && y == seeds[i][1]) {
        return true;
      }
    }
    return false;

  }

  @Override
  public void exit() {
    System.exit(0);
  }


}






