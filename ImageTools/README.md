# ImageTool
(All picture belong to Stephen Dorris)

imagetool is a package that has model, view and controller packages that allow a user to process and generate new images based on the user specifications. 

## Model

This package includes the interface (*Image Model*), its implementation (*ImageModelImpl*) and a mock version of the model (*ImageMockModel*) and their supporting classes. 

**This package has the following classes:**

### class Image

This class stores 3D array of an image or creates a new one with the new values in a 3D array. 
Has the following constructors: 

```java 

//used when an image already exist under a certain name.
public Image(String path);

//used to create a new image under a new name and with a new 3D array.
public Image(String path, int[][][] imageArray);
```



### class ImageModelImpl

***
**CHANGES MADE TO THE MODEL:** 
* We have updated previous functionality to the model. You can load the picture using load(String path) method instead of having to 
construct a new model object every time you want to start processing a new image. This design change makes more sense when using the controller, and is consistent with the command 
```java load(String path)``` . This functionality does not change the use of the batch command file, but makes it easier to implement and allows for less potential for bugs. 
* in mosaic() method signature switched (String path, int seeds) to (int seeds, String path) as it makes it more consistent with other functions which first do operations and have path as a last argument(under what name to save a new image). 
***

***
CHANGES MADE IN HW10:
- model now has undo and redo stacks that keep track of 
operations performed on the image.
- model can now save and load images, can be initialized as empty.
***
Implementation of ImageModel interface.
 
Has the following constructors: 

```java
class ImageModelImpl {
/* 
Takes in a path to a picture to apply filters and 
color transformations on.
*/
public ImageModelImp(String path);

/*
The following constructor initializes the model. 
Can be used to generate images (e.g rainbow, chekerboard)
or to load picture later.
*/
public ImageModelImpl();
}
```


The following are the functionalities of the ImageModel: 

```java
class ImageModelImpl {

//loads the picture in the model. 
void loadPic(String s);

//loads the picture in the model.
void loadPic(String path);

//based on requested filter operation filters image 
//and saves it using the name specified by the client 
//(can Blur and Sharpen an Image).
Image filter(FilterOp op, String path);

//based on requested color operation transforms the color of the picture
//and saves it using the name specified by the client
Image colorTransform(ColorOp op, String path);

//dithers the image and saves under the name specified in the path.
Image dither(String path);

//changes the picture to mosaic based on the number of seeds
//saves under the path name.
Image mosaic(int seedCt, String path); 

//generates checkerboard with specified square sizes 
//under path name.
Image generateCheckerboard(int squareSize, String path);

//generates vertical or horizontal rainbow image with specified 
//width and height parameters, saves under the path name.
Image generateRainbow(int width, int height, RainbowPosn position,
                        String path);
//exits the program.
void exit();
}
```
### class ImageMockModel

The following class has the same functionalities as ImageModeImpl
but doesn't actually do anything except writes to a new file to check that the controller uses the right functions in the right order given the commands in a text file.


### class Kernel 

The following class can be used to create a new 2D array with values 
to be applied on an image. Has two static kernels to blur and sharpen an image.

### Enums

model package has 3 enum classes used in different function inside the model. 

ColorOp enum: has Sepia and GreyScale as options for color transformation operation.

FilterOp enum: has Blur and Sharpen options for picture filtering.

RainbowPosn enum: has Horizontal and Vertical options for rainbow generation.

## Controller

This package contains the interface and implementation of the controller used to generate and filter images based on a text file that contains bash commands or interactions with the model coming from GUI.

### class Controller

Implementation of a controller. The controller has only one functionality right now, and that is 
to process a batch command instruction set. when constructing the controller, you must put in a text
file extension to a batch command, 

The follow Has the following constructor: 

```java
class Controller {
//takes in a model and the batch command file to read from.
public Controller(ImageModel model, String batchCommandFile);
}
```

The following represent the functionality of the controller. 


```java 

void start();

//takes in a commands with arguments as a string, parses it and 
//goes to the right method of ImageModelImpl.

void action(String line);

```
###View
This package includes a GUI implementation of filter and image generating 
operations.

##class Frame
Implementation of the view. It can display a window where user can 
perform filter operations with an image and generate new images. The
user can press buttons to change and view the image.

```java
class Frame {
  //Initializes the view
  public Frame();
}

```

The following is the functionality of the view class.

```java 

//draws the image and updates it that is sent by the controller.
public void setImg(int[][][] img);

//sets the image visible
void display();


//exits out of program.
void exit();

//sets listeners for buttons and the menu.
void setListener(IController listener);

//displays the error message on the screen
void errorOption(String message);

```


## Batch commands syntax:

to load a picture 

```text
format: command followed by the name of the file to load: 

load cat.png
```

to save a picture

```text

# command plus name of the new image.
save cat-dithered.PGN

```

to filter (sharpen, blur) uses the file loaded.
``` text
// filter operations:

sharpen
blur 
```

to apply color
```text
 
greyscale 
sepia

```

to dither 
```text

dither 

```

to generate a mosaic picture 
```text 

mosaic 1000

```

to generate rainbow
```text 
#command followed by the height and the width of a new rainbow picture 
and the position of stripes 

rainbow 256 256 horizontal 
```

to generate chekerboard
```text 
#command followed by the size of the squares in pixels
checkerboard 60
```