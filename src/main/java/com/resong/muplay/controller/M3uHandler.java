package main.java.com.resong.muplay.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import main.java.com.resong.muplay.interfaces.PlaylistFileHandler;
import main.java.com.resong.muplay.model.Mp3Record;
import main.java.com.resong.muplay.model.Playlist;
import main.java.com.resong.muplay.utils.Mp3Parser;

/**
 * Class to handle the editing of the actual .m3u files on the hard disk.
 * Implements the PlaylistFileHandler interface, with methods to read, write and
 * rename .m3u files.
 *
 * @author Rebecca Song
 */
public class M3uHandler implements PlaylistFileHandler {

  ////////////// FIELDS /////////////
  private static final String HEADER = "#EXTM3U";
  private static final String MARKER = "#EXTINF";
  private static final String PLAYLIST_EXT = ".m3u";
  private static final String SONG_EXT = ".mp3";

  ///////////// METHODS ////////////
  /**
   * Read a m3u file; if the file does not exist, display error message notifying
   * user. Parse the file line by line, and if the file location of the mp3 file
   * is incorrect/corrupt, display an error message asking if the user would like
   * to find the file and fix the error; otherwise remove the mp3 file from the
   * playlist
   * 
   * @param file file path of the playlist
   * @return Playlist playlist parsed from the file
   * @throws Exception exceptions arising from corruption or missing files
   */
  @Override
  public Playlist read(String file) throws Exception {

    Playlist playlist = null;

    String playlistName = file.substring(0, file.length() - PLAYLIST_EXT.length());
    playlistName = playlistName.substring(playlistName.lastIndexOf("\\") + 1);

    try (Scanner reader = new Scanner(new File(file))) {

      // check if the data in the file is not corrupt
      if (reader.hasNextLine() && (reader.nextLine()).equals(HEADER)) {

        playlist = new Playlist(playlistName, file);
        Mp3Parser musicParser = new Mp3Parser();
        boolean corrupt = false;

        while (reader.hasNextLine()) {
          String mp3File = null;
          Mp3Record mp3 = new Mp3Record();
          try {
            reader.nextLine(); // skip over first line; obtain this information from the mp3 file itself
            mp3File = reader.nextLine();
            mp3.setFilePath(mp3File);
            mp3 = musicParser.parse(mp3);
            playlist.add(mp3);
          } catch (FileNotFoundException ex) {
            corrupt = true;
            String errorMsg = mp3File + " not found. Would you like to find it?";
            Alert errorDialog = new Alert(Alert.AlertType.CONFIRMATION, errorMsg);
            errorDialog.setTitle("Load Song");
            errorDialog.setHeaderText("Error: Missing File");
            errorDialog.showAndWait();

            if (errorDialog.getResult() == ButtonType.OK) {
              FileChooser fileChooser = new FileChooser();
              FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)",
                  "*" + SONG_EXT);
              fileChooser.getExtensionFilters().add(extFilter);
              File newFile = fileChooser.showOpenDialog(null);

              if (newFile != null) {
                mp3.setFilePath(newFile.getAbsolutePath());
                mp3 = musicParser.parse(mp3);
                playlist.add(mp3);
              } else {
                showCorruptErrorMsg(mp3File);
              }
            } else {
              showCorruptErrorMsg(mp3File);
            }
          }
        }

        // if the playlist was corrupt, rewrite the file
        if (corrupt) {
          write(playlist);
        }

      } else {
        throw new NoSuchElementException("Playlist file " + playlistName + " is corrupted.");
      }

    } catch (FileNotFoundException ex) {
      throw new FileNotFoundException("Playlist file " + playlistName + " not found.");
    }
    return playlist;
  }

  /**
   * Implementation of method to write the playlist passed in to the file
   *
   * @param playlist Playlist to be saved to the file
   * @throws FileNotFoundException file path of playlist cannot be found
   */
  @Override
  public void write(Playlist playlist) throws FileNotFoundException {

    try (PrintWriter writer = new PrintWriter(new File(playlist.getFilePath()))) {

      int length = playlist.getSize();

      writer.println(HEADER);

      for (int i = 0; i < length; i++) {
        Mp3Record mp3 = (Mp3Record) playlist.get(i);
        String firstLine = "%s:%d,%s";
        writer.printf(firstLine, MARKER, mp3.getDuration(), mp3.getTitle());
        writer.println("");

        if (i != length - 1) {
          writer.println(mp3.getFilePath());
        } else {
          writer.print(mp3.getFilePath());
        }
      }

    } catch (FileNotFoundException ex) {
      throw new FileNotFoundException("Playlist file " + playlist.getName() + " not found.");
    }
  }

  /**
   * Implementation of method to rename the playlist file; first confirms if
   * another file in the same directory has the same name and asks user if they
   * want to overwrite that file; if they do not, then display an error message
   *
   * @param filePath file path of the Playlist to be renamed
   * @param newName  new name of the Playlist
   * @throws IOException thrown when a playlist already exists in the same
   *                     directory with the same name
   */
  @Override
  public void rename(String filePath, String newName) throws IOException {
    String directory = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
    File srcFile = new File(filePath);
    Path source = srcFile.toPath();

    File destFile = new File(directory + newName + PLAYLIST_EXT);
    if (destFile.exists()) {
      String content = "There already exists a playlist with that name. Would you like to overwrite it?";
      Alert confirmBox = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO,
          ButtonType.CANCEL);
      confirmBox.setTitle("Confirmation");
      confirmBox.setHeaderText("Rename Playlist");
      confirmBox.showAndWait();

      if (confirmBox.getResult() == ButtonType.YES) {
        Files.move(source, source.resolveSibling(newName + PLAYLIST_EXT),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      } else if (confirmBox.getResult() == ButtonType.NO) {
        throw new IOException("Error: A playlist already exists with that name.");
      }
    } else {
      Files.move(source, source.resolveSibling(newName + PLAYLIST_EXT));
    }
  }

  /**
   * Helper method to display error dialog message notifying the user of a corrupt
   * file
   *
   * @param fileName file that is corrupted
   */
  private static void showCorruptErrorMsg(String fileName) {
    String message = fileName + " was removed from the playlist.";
    Alert notAddedDlg = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
    notAddedDlg.setTitle("Add Song");
    notAddedDlg.setHeaderText("Error: Corrupt File Path");
    notAddedDlg.showAndWait();
  }
}
