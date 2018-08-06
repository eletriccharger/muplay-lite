
package main.java.com.resong.editor.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;

import main.java.com.resong.editor.model.Playlist;

/**
 * Playlist file handler interface with abstract methods to read, write and rename
 * files. Implemented by M3uHandler.
 * 
 * @author Rebecca Song
 */
public interface PlaylistFileHandler {
    
    public abstract Playlist read(String file) throws Exception;//throws FileNotFoundException, NoSuchElementException, IOException;
    public abstract void write(Playlist playlist) throws FileNotFoundException;
    public abstract void rename(String oldName, String newName) throws IOException;

}
