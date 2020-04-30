package imagetools.controller;

/**
 * Helps controller determine if commands are coming from GUI or from bash commands.
 */
public enum ControllerStatus {
  BatchCommands, ViewCommands
}
