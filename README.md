# Instagram Filters Using Computer Vision
 
Creating Instagram-like filters using Java and OpenCV (With the help of the tutorial: https://www.youtube.com/watch?v=R6wgJ6epakU made by [Gabriela Solano](https://github.com/GabySol))
 
 
### Previous requirements
In order to run this project it is necessary to install OpenCV version 4.6.0 in the IDE in which you want to run it.
 
### Introduction
* Goal: The objective of this project is to try to understand how the filters used by the Instagram social network work, such as those in which personal items are added to the user's face (glasses, hats, etc.) as well as provide some functionalities that are not present in the OpenCV 4.6 package for Java.

* Theoretical framework:
The Haar-Like features receive their name due to the similarity they have with the Haar wavelets introduced in [1], this feature considers two adjacent rectangular regions in an image, in these regions the difference between the sum of all the pixels is calculated of each one. Adjacent regions are the same size and shape. Characteristics of 3 rectangles and 4 rectangles [2] are also considered, in the first of these the sum of two outer rectangles is calculated and from this value the sum of the pixels within a central rectangle is subtracted, in that of 4 rectangles the difference between diagonal pairs of rectangles is performed. The mentioned regions are shown in the following figure.

    ![Fugure 1](https://github.com/Samvel24/Filtros-Instagram/blob/master/ImagenesEjemplo/Figura1.png)
    **Figure 1. Haar features of 2, 3 and 4 rectangles respectively**

    In [2] these characteristics are used to detect faces and the integral image is used to calculate them quickly. An example of using these features is shown in figure 2, in this example, the difference in intensity between the eye region and the nose region is measured. Accordingly, these characteristics allow to categorize small sections of an image and, in the case of figure 2, to classify some features of the face.

    ![Figure 2](https://github.com/Samvel24/Filtros-Instagram/blob/master/ImagenesEjemplo/Figura2.png)
    **Figure 2. Haar features used in the classification of facial features**

    The above is important because it will allow us to detect the eyes and the face that come from the camera image and that we can implement through OpenCV using the CascadeClassifier class and an xml file that contains a previously trained Haar classifier.

### Contributions added to this project
* Use of the StretchIcon class so that the camera image is displayed completely inside the JLabel (and according to the size of the JLabel) and in this way simulate the use of the flags:
    - WINDOW_KEEPRATIO
    - WND_PROP_ASPECT_RATIO
    
    described in https://docs.opencv.org/4.x/d0/d90/group__highgui__window__flags.html.
    
    Note: At this time, these flags are not available in the OpenCV 4.6 package for Java.
    
    To complement what was previously described, a sequence of processes similar to the one provided by HighGui.imshow() and HighGui.waitKey() was used to be able to resize the image inside the JLabel object and that will allow us to visualize the camera image with the best possible quality.

* The functionality of adding lenses in the area of the detected eyes with the help of the CascadeClassifier class has been implemented.

### Screenshots

* Presentation video: https://linkedin.com

### References
* [1] Haar, A., Zur theorie der orthogonalen funktionensysteme. Mathematische Annalen, 1910.
* [2] Viola, P., Jones, M., Rapid object detection using a boosted cascade of simple features, IEEE Conf. on Computer Vision and Pattern Recognition, 2001.

***

2022 [Samuel Ramirez](https://github.com/Samvel24/)