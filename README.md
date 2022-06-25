# WTSightEdit
![GitHub All Releases](https://img.shields.io/github/downloads/SMILEY4/WTSightEdit/total)

A WYSIWYG-Editor for custom sights for the game "War Thunder" 

*Note: This project is no longer actively developed.*


## How to run

This application was written with Java 8 and requires JavaFX. JavaFX is no longer provided with versions 9 or higher or with the OpenJDK. 

**Workaround - Adding JavaFX manually when running**

1. Download JavaFX: https://gluonhq.com/products/javafx/

2. Run the following command, replace "YOUR_PATH_TO_JAVAFX" and "YOUR_PATH_TO_WTSIGHTEDIT" with the correct paths (This command should work with any Java version)

   ```
   java --module-path YOUR_PATH_TO_JAVAFX/lib --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web,javafx.swt -jar YOUR_PATH_TO_WTSIGHTEDIT/data/wtedit_application.jar
   ```
   
   Replace "pathToJavaFx" with the path to the downloaded and extracted javafx-directory and "pathToWTSightEdit" with the path to the directory with of the "./WTSightEdit.jar".
   




![alt text](https://i.imgur.com/rnyTPeB.png)
https://www.youtube.com/watch?v=bRAGADRql1g
