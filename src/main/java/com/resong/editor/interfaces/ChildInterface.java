package main.java.com.resong.editor.interfaces;

import javafx.collections.ObservableList;

/**
 * Interface with two methods to allow the passing of data from one window
 * to the other (implemented by the windows)
 * 
 * @author Rebecca Song
 */
public interface ChildInterface {

    public ObservableList<?> onChildRequest();  // returns data upon request from the child class
    public void onChildUpdate(ObservableList<?> list); // passes data upon the child class updating

}
