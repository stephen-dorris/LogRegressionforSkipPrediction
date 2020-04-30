package imagetools.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import imagetools.model.ColorOp;
import imagetools.model.FilterOp;

import imagetools.model.ImageModel;
import imagetools.model.RainbowPosn;
import imagetools.view.IView;

/**
 * This class represents a imagetools.controller that can filter and generate images based on a
 * batchCommandFile with script commands.
 */
public class Controller implements IController {


  /**
   * Status that tells the imagetools.controller, on "start", to parse file or, to perform waiting
   * on view for processing,
   */
  private ControllerStatus status;

  /**
   * Controller has  access to the model and can call certain image processing functions, and can
   * control the version of the image worked on.
   */
  private ImageModel model;

  /**
   * Controller has  acess to the view and can let the view know if there is an error, or if not,
   * display a certain image.
   */
  private IView view;

  /**
   * Used to see if there has been image loaded in GUI, if not, and processing method is called,
   * then send view error.
   */
  private static boolean imageLoaded;

  /**
   * This String is message sent to view if an image processing method is called with imageLoaded
   * being false.
   */
  private final String LoadWarningMessage = "Cannot perform operation on image that has " +
          "not been loaded yet. Please load image.";


  /**
   * Constructor for a imagetools.controller that will process batch commands from file.
   *
   * @param model image model that stores a picture and performs operations on it.
   */
  public Controller(ImageModel model) {
    this.model = model;
    this.status = ControllerStatus.BatchCommands;
  }


  /**
   * Constructor for a controller that works with the model and the view.
   *
   * @param model model that holds an image and performs operations on it.
   * @param view GUI that displays the image and command buttons.
   */
  public Controller(ImageModel model, IView view) {
    imageLoaded = false;
    this.model = model;
    this.view = view;
    this.status = ControllerStatus.ViewCommands;
    view.setListener(this);
    view.display();
  }


  //gets an image from the model.
  private int[][][] getImage() {
    return this.model.getImage();
  }


  @Override
  public void parse(String script) {
    try {
      File f = new File(script);
      Scanner s;

      if (f.isFile()) {
        s = new Scanner(new FileInputStream(script));
      } else {
        s = new Scanner(script);
      }

      String line;
      while (s.hasNext()) {
        line = s.nextLine();
        action(line);
      }
    } catch (IOException e) {

      if (this.status == ControllerStatus.ViewCommands) {
        view.errorOption(e.getMessage());
      } else {
        throw new IllegalArgumentException("Illegal command, batchCommandFile not found\n");
      }
    }
  }

  @Override
  public void action(String command) throws IOException {
    Scanner line = new Scanner(command);
    String c = line.next();

    try {
      // try to choose and perform operation from the line.
      // If model throws exception, get error message from model and send to view
      switch (c) {
        case "load":
          imageLoaded = true;
          model.loadImage(line.next());
          break;
        case "save":
          if (!imageLoaded) {
            view.errorOption(LoadWarningMessage);
          } else {
            model.save(line.next());
          }
          break;

        case "blur":
          if (!imageLoaded) {
            view.errorOption(LoadWarningMessage);
          } else {
            model.filter(FilterOp.Blur);
          }
          break;
        case "sharpen":
          if (!imageLoaded) {
            view.errorOption(LoadWarningMessage);
          } else {
            model.filter(FilterOp.Sharpen);
          }
          break;
        case "greyscale":
          if (!imageLoaded) {
            view.errorOption(LoadWarningMessage);
          } else {
            model.colorTransform(ColorOp.GrayScale);
          }
          break;

        case "sepia":
          if (!imageLoaded) {
            view.errorOption(LoadWarningMessage);
          } else {
            model.colorTransform(ColorOp.Sepia);
          }
          break;

        case "rainbow":
          imageLoaded = true;
          int width = Integer.parseInt(line.next());
          int height = Integer.parseInt(line.next());
          RainbowPosn p;
          if (line.next().equals("horizontal")) {
            p = RainbowPosn.Horizontal;
          } else {
            p = RainbowPosn.Vertical;
          }
          model.generateRainbow(width, height, p);
          break;

        case "checkerboard":
          imageLoaded = true;
          model.generateCheckerboard(Integer.parseInt(line.next()));
          break;
        case "dither":
          if (!imageLoaded) {
            view.errorOption(LoadWarningMessage);
          } else {
            model.dither();
          }
          break;
        case "mosaic":
          if (!imageLoaded) {
            view.errorOption(LoadWarningMessage);
          } else {
            model.mosaic(Integer.parseInt(line.next()));
          }
          break;
        case "exit":
          model.exit();
          break;

        case "undo":
          model.undo();
          break;
        case "redo":
          model.redo();
          break;

        default:
          throw new IllegalArgumentException("Batch command not found");
      }

      if (status == ControllerStatus.ViewCommands && imageLoaded) {
        view.setImg(getImage());
      }
    } catch (IllegalArgumentException eArg) {
      if (status == ControllerStatus.ViewCommands) {
        view.errorOption(eArg.getMessage());
      } else {
        // Controller throws exception if no// view to display error
        throw new IllegalArgumentException(eArg.getMessage());
      }

    } catch (IllegalStateException eState) {
      if (status == ControllerStatus.ViewCommands) {
        view.errorOption(eState.getMessage());
      } else {

        // Controller throws exception if no view to display error
        throw new IllegalStateException(eState.getMessage());
      }
    }
  }
}
