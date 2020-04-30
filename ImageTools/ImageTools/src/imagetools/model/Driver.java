package imagetools.model;

import java.io.IOException;

import imagetools.controller.Controller;
import imagetools.view.Frame;
import imagetools.view.IView;

/**
 * This class represents a driver of the model with main method.
 */
public class Driver {
  /**
   * Main function processes,filters, and images and  stores images.
   */
  public static void main(String[] args) {

      ImageModel model = new ImageModelImpl();
      IView view = new Frame();
      Controller controller;

      if(args[3].equals("-script")) {
       controller =  new Controller(model);
       controller.parse(args[4]);
      }
      else {
        controller =  new Controller(model, view);
      }
  }
}
