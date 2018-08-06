
package main.java.com.resong.editor.model;

import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Abstract class to define traits of an Archive, from which
 * the Playlist class inherits
 * 
 * @author Rebecca Song
 */
public abstract class Archive {
    
    //////// FIELDS /////////
    protected String name;
    protected int size;
    protected ObservableList<Mp3Record> records;
    
    ///////// CONSTRUCTORS //////////
    protected Archive(String name) {
        this.name = name;
        this.records = FXCollections.observableArrayList();
        this.size = 0;
    }
    
    protected Archive(String name, ObservableList<Mp3Record> records) {
        this.name = name;
        this.records = records;
        this.size = records.size();
    }
    
    /////////// METHODS ///////////
    public abstract void add(Mp3Record record);
    public abstract void addAll(Collection<? extends Mp3Record> c);
    public abstract boolean delete(Mp3Record record);
    public abstract void deleteAll(Collection<? extends Mp3Record> c);
    public abstract Mp3Record delete(int index);
    public abstract Mp3Record get(int index);
    public abstract ObservableList<Mp3Record> getRecords();

}
