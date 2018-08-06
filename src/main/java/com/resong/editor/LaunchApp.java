
package main.java.com.resong.editor;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.com.resong.editor.screen.MainScreen;

/**
 **************************** Java II April 22, 2015 **************************
 ******************************* FINAL PROJECT ********************************
 *
 *                        Submitted by Rebecca Song
 *
 * Honour: I have completed this assignment on my own. In researching the
 * assignment, I got help/ideas from class, the textbook, javadoc tutorials. 
 *
 * File name: TestApp.java
 *
 * Description: Main program that launches the app, "MuPlay Lite".
 *
 * @author Rebecca Song
 */
public class LaunchApp extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Initiates main screen of the application; method that is called on
     * by the launch method in the main program.
     * @param stage Stage to be shown in main GUI
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        new MainScreen(stage);
    }

}
