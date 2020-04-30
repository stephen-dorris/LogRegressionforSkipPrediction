package imagetools.view;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import imagetools.controller.IController;

public class Frame extends JFrame implements IView {

  //image
  private BufferedImage image;

  //menu
  private JMenu menu;

  //menu options
  private JMenuItem load;
  private JMenuItem save;
  private JMenuItem undo;
  private JMenuItem redo;
  private JMenuItem sharpenMenu;
  private JMenuItem blurMenu;
  private JMenuItem ditherMenu;
  private JMenuItem mosaicMenu;
  private JMenuItem sepiaMenu;
  private JMenuItem greyscaleMenu;
  private JMenuItem rainbowMenu;
  private JMenuItem checkerboardMenu;


  //filter buttons
  private JButton sharpen;
  private JButton blur;
  private JButton sepia;
  private JButton greyscale;
  private JButton dither;

  //for batch
  private JButton runBatch;
  private JTextArea batchScript;

  //for mosaic filtering.
  private JButton mosaic;
  private JTextField seeds;

  //for rainbow generation.
  private JButton rainbow;
  private JTextField heightR;
  private JTextField widthR;
  private JTextField horzOrVer;

  //for checkerboard generation.
  private JButton checkerboard;
  private JTextField squareSize;

  /**
   * Initializes the GUI view.
   */
  public Frame() {
    init();
  }

  //sets up the frame
  private void init() {
    //setting frame
    setSize(1000, 1000);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    getContentPane().setBackground(Color.GRAY);

    //name of the frame
    this.add(new JLabel("Image Editor"));
    this.setLayout(new BorderLayout());

    //setting image panel
    JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
      }
    };

    int dimensions = 600;
    panel.setPreferredSize(new Dimension(1500, 1500));
    panel.setBackground(Color.GRAY);
    JScrollPane scrolling = new JScrollPane(panel);
    scrolling.setPreferredSize(new Dimension(dimensions, dimensions));
    getContentPane().add(scrolling, BorderLayout.CENTER);


    //set up panels with buttons, script shell and menu items.
    addMenuItems();
    addButtons();
    addBatchCommand();

    this.setResizable(true);
    pack();
  }

  //adds menu items to the menu.
  private void addMenuItems() {
    //put buttons on the screen
    //menu
    JMenuBar bar = new JMenuBar();
    this.add(bar, BorderLayout.PAGE_START);
    menu = new JMenu("Menu");
    menu.setActionCommand("menu");
    bar.add(menu);


    load = new JMenuItem("Load");
    load.setActionCommand("load");

    save = new JMenuItem("Save");
    save.setActionCommand("save");

    menu.add(load);
    menu.add(save);

    undo = new JMenuItem("Undo");
    undo.setActionCommand("undo");
    menu.add(undo);

    redo = new JMenuItem("Redo");
    redo.setActionCommand("redo");
    menu.add(redo);

    JMenu filterMenu = new JMenu("Filters");
    menu.add(filterMenu);

    sharpenMenu = new JMenuItem("Sharpen");
    sharpenMenu.setActionCommand("sharpen");
    filterMenu.add(sharpenMenu);

    blurMenu = new JMenuItem("Blur");
    blurMenu.setActionCommand("blur");
    filterMenu.add(blurMenu);

    ditherMenu = new JMenuItem("Dither");
    ditherMenu.setActionCommand("dither");
    filterMenu.add(ditherMenu);

    mosaicMenu = new JMenuItem("Mosaic");
    filterMenu.add(mosaicMenu);

    JMenu colorTransformMenu = new JMenu("Color Transformation");
    menu.add(colorTransformMenu);

    sepiaMenu = new JMenuItem("Sepia");
    sepiaMenu.setActionCommand("sepia");
    colorTransformMenu.add(sepiaMenu);

    greyscaleMenu = new JMenuItem("Greyscale");
    greyscaleMenu.setActionCommand("greyscale");
    colorTransformMenu.add(greyscaleMenu);

    JMenu generatorsMenu = new JMenu("Generators");
    menu.add(generatorsMenu);

    rainbowMenu = new JMenuItem("Rainbow");
    generatorsMenu.add(rainbowMenu);

    checkerboardMenu = new JMenuItem("Checkerboard");
    generatorsMenu.add(checkerboardMenu);

  }


  //add batch script box
  private void addBatchCommand() {
    JPanel batch = new JPanel();
    this.add(batch, BorderLayout.LINE_END);
    batch.setLayout(new BoxLayout(batch, BoxLayout.Y_AXIS));

    JLabel l = new JLabel("Batch Script");
    l.setAlignmentX(Component.CENTER_ALIGNMENT);
    batch.add(l);


    batchScript = new JTextArea(24, 15);
    JScrollPane scrollPane = new JScrollPane(batchScript);
    batch.add(scrollPane);

    runBatch = new JButton("Run");
    runBatch.setAlignmentX(Component.CENTER_ALIGNMENT);
    batch.add(runBatch);

  }

  //sets up JPanel with filter buttons.
  private void addButtons() {
    int space = 20;
    JPanel buttons = new JPanel();
    this.add(buttons, BorderLayout.LINE_START);
    buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

    buttons.add(Box.createRigidArea(new Dimension(0, space)));

    //filters -----------------------------------------------------
    JPanel filters = new JPanel();
    filters.add(new JLabel("Filters"));
    filters.setForeground(Color.DARK_GRAY);

    filters.setLayout(new BoxLayout(filters, BoxLayout.Y_AXIS));
    buttons.add(filters);

    //set up filter and generator buttons
    sharpen = new JButton("Sharpen");
    sharpen.setActionCommand("sharpen");
    filters.add(sharpen);

    blur = new JButton("Blur");
    blur.setActionCommand("blur");
    filters.add(blur);

    dither = new JButton("Dither");
    dither.setActionCommand("dither");
    filters.add(dither);

    //filter mosaic -------------------------------------
    mosaic = new JButton("Mosaic");
    mosaic.setActionCommand("mosaic");
    filters.add(mosaic);

    seeds = new JTextField(null);
    addText(seeds, filters);
    addLabel(new JLabel("Num of seeds:"), filters);
    filters.add(Box.createRigidArea(new Dimension(0, 10)));

    //-----------------------------------------------------------------

    buttons.add(Box.createRigidArea(new Dimension(0, space)));

    //Color Transformations -------------------------------------------
    JPanel colorTransform = new JPanel();
    colorTransform.add(new JLabel("Color Transform"));
    colorTransform.setForeground(Color.DARK_GRAY);
    colorTransform.setLayout(new BoxLayout(colorTransform, BoxLayout.Y_AXIS));
    buttons.add(colorTransform);

    buttons.add(Box.createRigidArea(new Dimension(0, 10)));

    sepia = new JButton("Sepia");
    sepia.setActionCommand("sepia");
    colorTransform.add(sepia);


    greyscale = new JButton("Grey Scale");
    greyscale.setActionCommand("greyscale");
    colorTransform.add(greyscale);
    //--------------------------------------------------------------

    buttons.add(Box.createRigidArea(new Dimension(0, space)));

    //generate rainbow -----------------------------
    JPanel generators = new JPanel();
    generators.add(new JLabel("Generators"));
    generators.setForeground(Color.DARK_GRAY);
    generators.setLayout(new BoxLayout(generators, BoxLayout.Y_AXIS));
    buttons.add(generators);

    buttons.add(Box.createRigidArea(new Dimension(0, 10)));

    rainbow = new JButton("Rainbow");
    generators.add(rainbow);

    widthR = new JTextField();
    addText(widthR, generators);
    addLabel(new JLabel("width"), generators);

    heightR = new JTextField();
    addText(heightR, generators);
    addLabel(new JLabel("height"), generators);

    horzOrVer = new JTextField();
    addText(horzOrVer, generators);
    addLabel(new JLabel("horizontal or vertical?"), generators);
    //---------------------------------------------

    buttons.add(Box.createRigidArea(new Dimension(0, space)));

    //generate checkerboard-----------------------
    checkerboard = new JButton("Checkerboard");
    generators.add(checkerboard);

    squareSize = new JTextField();
    addText(squareSize, generators);
    addLabel(new JLabel("square size"), generators);
    generators.add(Box.createRigidArea(new Dimension(0, space + 10)));

    //--------------------------------------------
  }

  //add text field on a jPanel
  private void addText(JTextField field, JPanel p) {
    field.setMaximumSize(new Dimension(160, 20));
    p.add(field);
  }

  //add jLabel on a panel
  private void addLabel(JLabel label, JPanel p) {
    label.setFont(label.getFont().deriveFont(10.0f));
    label.setForeground(Color.GRAY);
    p.add(label);
  }

  @Override
  public void setImg(int[][][] img) {
    image = new BufferedImage(
            img[0].length,
            img.length,
            BufferedImage.TYPE_INT_RGB);

    for (int i = 0; i < img.length; i++) {
      for (int j = 0; j < img[0].length; j++) {
        int r = img[i][j][0];
        int g = img[i][j][1];
        int b = img[i][j][2];

        int color = (r << 16) + (g << 8) + b;
        image.setRGB(j, i, color);
      }
    }
    repaint();
  }

  @Override
  public void setListener(IController controllerListener) {
    setListerForButtons(controllerListener);
    setListenerForMenu(controllerListener);
  }


  //sets listener for cuttons
  private void setListerForButtons(IController controllerListener) {
    ActionListener listener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          controllerListener.action(e.getActionCommand());
        } catch (IOException ex) {
          //
        }
      }
    };

    sharpen.addActionListener(listener);
    blur.addActionListener(listener);
    greyscale.addActionListener(listener);
    sepia.addActionListener(listener);
    dither.addActionListener(listener);

    setMosaicListenerButton(controllerListener);
    setRainbowListenerButton(controllerListener);
    setCheckerboardListenerButton(controllerListener);
    setBatchListenerButton(controllerListener);
  }

  //setting listener for mosaic
  private void setMosaicListenerButton(IController mosaicListener) {
    mosaic.addActionListener((ActionEvent e) -> {
      try {
        if (!seeds.getText().isEmpty()) {
          mosaicListener.action("mosaic " + seeds.getText());
          seeds.setText("");
        }
      } catch (IOException ex) {
      }
    });
  }

  //setting listener for rainbow
  private void setRainbowListenerButton(IController rainbowListener) {
    rainbow.addActionListener((ActionEvent e) -> {
      try {
        if (!heightR.getText().isEmpty() && !widthR.getText().isEmpty()
                && !horzOrVer.getText().isEmpty()) {
          rainbowListener.action("rainbow " + widthR.getText() + " " + heightR.getText() + " "
                  + horzOrVer.getText());
          widthR.setText("");
          heightR.setText("");
          horzOrVer.setText("");
        }
      } catch (IOException ex) {
        //
      }
    });
  }

  //setting listener for checkerboard
  private void setCheckerboardListenerButton(IController checkerboardListener) {
    checkerboard.addActionListener((ActionEvent e) -> {
      try {
        if (!squareSize.getText().isEmpty()) {
          checkerboardListener.action("checkerboard " + squareSize.getText());
          squareSize.setText("");
        }
      } catch (IOException ex) {
        //
      }
    });
  }

  //setting listener for batch command
  private void setBatchListenerButton(IController runListener) {
    runBatch.addActionListener((ActionEvent e) -> {
      if (!batchScript.getText().isEmpty()) {
        runListener.parse(batchScript.getText());
        batchScript.setText("");
      }
    });
  }

  //set listeners for menu items
  private void setListenerForMenu(IController controllerListener) {
    setListenerLoad(controllerListener);
    setListenerSave(controllerListener);

    ActionListener listener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          System.out.println(e.getActionCommand());
          controllerListener.action(e.getActionCommand());
        } catch (IOException ex) {
          //
        }
      }
    };

    redo.addActionListener(listener);
    undo.addActionListener(listener);
    sharpenMenu.addActionListener(listener);
    blurMenu.addActionListener(listener);
    ditherMenu.addActionListener(listener);
    sepiaMenu.addActionListener(listener);
    greyscaleMenu.addActionListener(listener);

    setListenerMosaicMenu(controllerListener);
    setListenerRainbowMenu(controllerListener);
    setListenerCheckerboardMenu(controllerListener);

  }


  //set listener for loading a file
  private void setListenerLoad(IController loadListener) {
    load.addActionListener((ActionEvent e) -> {
      String filepath;
      // Open file chooser
      JFileChooser jfc = new JFileChooser();

      // Only allow for image selection
      FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
              "Image files", ImageIO.getReaderFileSuffixes());
      jfc.setFileFilter(imageFilter);
      jfc.setAcceptAllFileFilterUsed(false);

      // Load the image selected
      int return_val = jfc.showOpenDialog(this.load);
      if (return_val == JFileChooser.APPROVE_OPTION) {
        filepath = jfc.getSelectedFile().getAbsolutePath();
        try {
          loadListener.action("load " + filepath);
        } catch (IOException ex) {
          //
        }
      }
    });
  }


  //set listener for saving the file
  private void setListenerSave(IController saveListener) {
    save.addActionListener((ActionEvent e) -> {
      String currentPath = System.getProperty("user.dir");
      int response = JOptionPane.showConfirmDialog(this,
              "Do you want to save your new image in the current directory?\n" +
                      "The directory is: " + currentPath +
                      "\n", "Save Prompt", JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE);
      // If user wants to save in current directory
      if (response == JOptionPane.YES_OPTION) {
        String fileName = filenamePane();
        try {
          saveListener.action("save " + currentPath + "/" + fileName);
        } catch (IOException ex) {
          //
        }
      }
      // If user wants to save NOT in current directory, then prompt for directory to save.
      else {
        String path;
        // Open file chooser
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Select a directory to save your new image. You cannot select an" +
                " existing file\n");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Load the image selected
        int return_val = jfc.showOpenDialog(this);
        if (return_val == JFileChooser.APPROVE_OPTION) {
          path = jfc.getCurrentDirectory().getAbsolutePath();
          String fileName = filenamePane();
          System.out.println("save " + path + "/" + fileName);
          try {
            saveListener.action("save" + path + "/" + fileName);
          } catch (IOException ex) {
            //
          }
        }
      }
    });
  }

  private void setListenerMosaicMenu(IController mosaicListener) {
    mosaicMenu.addActionListener((ActionEvent e) -> {

      String message = JOptionPane.showInputDialog(null, "How many seeds? " +
              "\n Please enter an integer between 10 and 10,000");
      JOptionPane.showMessageDialog(null, message);
      try {
        mosaicListener.action("mosaic " + message);
      } catch (IOException ex) {
        //
      }
    });
  }

  private void setListenerRainbowMenu(IController rainbowListener) {
    rainbowMenu.addActionListener((ActionEvent e) -> {

      String message = JOptionPane.showInputDialog(null, "" +
              "Please input the following format: width height (vertical/horizontal)");
      JOptionPane.showMessageDialog(null, message);
      try {
        rainbowListener.action("rainbow " + message);
      } catch (IOException ex) {
        //
      }
    });
  }

  private void setListenerCheckerboardMenu(IController checkerboardListener) {
    checkerboardMenu.addActionListener((ActionEvent e) -> {
      String message = JOptionPane.showInputDialog(null, "Enter the size " +
              " of the squares on a checkerboard");
      JOptionPane.showMessageDialog(null, message);
      try {
        checkerboardListener.action("checkerboard " + message);
      } catch (IOException ex) {
        //
      }
    });
  }


  private String filenamePane() {
    String fileName = JOptionPane.showInputDialog(this,
            "Select file name to save", "File Selection", JOptionPane.PLAIN_MESSAGE);
    return fileName;
  }

  @Override
  public void display() {
    setVisible(true);
  }

  @Override
  public void errorOption(String message) {
    JOptionPane.showMessageDialog(this, message,
            "Error", JOptionPane.ERROR_MESSAGE);
    if(batchScript.getText() != null) {
      batchScript.setText("");
    }
  }

  @Override
  public void exit() {
    System.exit(0);
  }
}