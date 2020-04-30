package imagetools.controller;


import java.io.IOException;

/**
 *
 * This interface represents a imagetools.controller for picture filtering and generation.
 * the controller must be able to either start functionality based on a current view object (GUI),
 * or will only process a text file with batch commands.
 *
 */
public interface IController {


  /**
   * Given a text parses it and performs specified commands.
   *
   * @param script text to parse.
   */
  void parse(String script);

  /**
   * Performs a filter or image generation operation in the model.
   *
   * @param command command string with arguments.
   * @throws IOException if command is not found.
   */
  void action(String command) throws IOException;
}
