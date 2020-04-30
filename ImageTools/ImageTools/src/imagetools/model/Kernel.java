package imagetools.model;

/**
 * This class represents a kernel that can be applied on an image for filtering with static values
 * of existing kernels.
 */
public class Kernel {

  /**
   * Kernel for bluring pictures.
   */
  static final double[][] blur = new double[][]{
          {1.0 / 16.0, 1.0 / 8.0, 1.0 / 16.0},
          {1.0 / 8.0, 1.0 / 4.0, 1.0 / 8.0},
          {1.0 / 16.0, 1.0 / 8.0, 1.0 / 16.0}
  };

  /**
   * Kernel for sharpening a a picture.
   */
  static final double[][] sharpen = new double[][]{
          {-1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0},
          {-1.0 / 8.0, 1.0 / 4.0, 1.0 / 4.0, 1.0 / 4.0, -1.0 / 8.0},
          {-1.0 / 8.0, 1.0 / 4.0, 1.0, 1.0 / 4.0, -1.0 / 8.0},
          {-1.0 / 8.0, 1.0 / 4.0, 1.0 / 4.0, 1.0 / 4.0, -1.0 / 8.0},
          {-1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0},
  };

  private double[][] kernel;


  /**
   * Constructor for a new kernel.
   *
   * @param kernel 2D array of values to be applied on an image.
   * @throws IllegalArgumentException if kernel does not have an odd number of rows.
   */
  public Kernel(double[][] kernel) throws IllegalArgumentException {
    if (!validKernel(kernel)) {
      throw new IllegalArgumentException("Kernel must be a 2-D ZxZ matrix,  "
              + "where Z is and odd number");
    }
    this.kernel = kernel;
  }

  //checks that the kernel has an odd number of rows.
  private boolean validKernel(double[][] kernel) {
    int initLength = kernel[0].length;
    if (initLength % 2 != 1 || kernel.length != initLength) {
      return false;
    }
    for (int i = 1; i < kernel.length; i++) {
      if (kernel[i].length != initLength) {
        return false;
      }
    }
    return true;
  }

  /**
   * Gets kernel values to be applied on a picture.
   *
   * @return 2D array of a kernel.
   */
  public double[][] getKernel() {
    double[][] copy = this.kernel.clone();
    return copy;
  }
}
