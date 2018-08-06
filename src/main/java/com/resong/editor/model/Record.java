
package main.java.com.resong.editor.model;

/**
 * Abstract class representing an instance of a Record.
 * 
 * @author Rebecca Song
 */
public abstract class Record {
    
    //////// FIELDS /////////
    protected String filePath;
    
    ///////// CONSTRUCTORS ////////
    protected Record() {
        this("");
    }
    
    protected Record(String location) {
        this.filePath = location;
    }
    
    ///////// METHODS ////////
    
    // getters and setters
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String path) {
        this.filePath = path;
    }
    
}
