package main.java.com.resong.editor.model;

import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class representing an instance of a Playlist; extends Archive class
 * and implements its abstract methods
 * 
 * @author Rebecca Song
 */
public class Playlist extends Archive {

    /////////// FIELDS //////////
    private String filePath;
    
    ////////// CONSTRUCTORS /////////
    public Playlist() {
        this("", "");
    }

    public Playlist(String name, String location) {
        this(name, FXCollections.observableArrayList(), location);
    }

    public Playlist(String name, ObservableList<Mp3Record> records, String location) {
        super(name, records);
        this.filePath = location;
    }

    ////////// METHODS /////////
    /**
     * Adds a record to the playlist and increases its size
     * @param record Mp3Record to be added
     */
    @Override
    public void add(Mp3Record record) {
        records.add(record);
        size++;
    }

    /**
     * Adds a collection of Mp3Records and updates the size
     * @param c collection of Mp3Records
     */
    @Override
    public void addAll(Collection<? extends Mp3Record> c) {
        records.addAll(c);
        size += c.size();
    }

    /**
     * Deletes a record from the playlist and updates its size
     * @param record Mp3Record to be deleted
     * @return true if record was deleted, false otherwise
     */
    @Override
    public boolean delete(Mp3Record record) {
        boolean value = records.remove(record);
        if (value) {
            size--;
        }
        return value;
    }

    /**
     * Deletes a collection of Mp3Records from the playlist and updates its size
     * @param c collection of Mp3Records to be deleted
     */
    @Override
    public void deleteAll(Collection<? extends Mp3Record> c) {
        records.removeAll(c);
        size -= c.size();
    }

    /**
     * Deletes the record at the specified index and updates the playlist size
     * @param index index of record to be deleted
     * @return Mp3Record deleted
     */
    @Override
    public Mp3Record delete(int index) {

        Mp3Record record = records.remove(index);
        size--;

        return record;
    }
    
    // getters and setters
    @Override
    public Mp3Record get(int index) {
        return records.get(index);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String newLocation) {
        filePath = newLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public int getSize() {
        return size;
    }

    @Override
    public ObservableList<Mp3Record> getRecords() {
        return records;
    }

    /**
     * Equals method where two playlists are equal if their file paths are the same
     * @param obj Playlist to be compared
     * @return true if file paths are the same, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Playlist) {
            if (this.getFilePath().equals(((Playlist) obj).getFilePath())) {
                return true;
            }
        }
        return false;
    }

}
