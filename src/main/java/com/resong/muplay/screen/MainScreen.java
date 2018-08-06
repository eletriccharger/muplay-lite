package main.java.com.resong.muplay.screen;

import java.io.File;
import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.java.com.resong.muplay.controller.M3uHandler;
import main.java.com.resong.muplay.interfaces.ChildInterface;
import main.java.com.resong.muplay.model.Playlist;
import main.java.com.resong.muplay.model.SongsTable;

/**
 * Main GUI window that will greet the user upon application launch. Contains a
 * ListView for the playlists loaded and a customised TableView to display the
 * corresponding songs for each playlist.
 *
 * The user can load a playlist, create a new playlist, delete a playlist (from
 * both the program and the hard disk if they wish), and edit the playlists
 * loaded into the program. They may rename the playlist by double-clicking on
 * it in the ListView, and pressing "Enter" to commit any changes made.
 *
 * Hitting the "Edit" button will cause a new window to pop up where the user
 * can actually edit the records the playlist holds.
 *
 * Class implements ChildInterface so that it can receive information from this
 * window.
 *
 * @author Rebecca Song
 */
public class MainScreen implements ChildInterface {

  //////////// FIELDS ////////////
  static final String EXT = ".m3u";

  private Stage stage;

  private ListView<Playlist> playlists;
  private SongsTable songs;

  ///////////// CONSTRUCTOR ////////////
  public MainScreen(Stage stage) {
    this.stage = stage;

    BorderPane mainPane = new BorderPane();

    Label header = new Label("--- MuPlay Lite ---");
    header.setStyle("-fx-font-size:20pt;");
    header.setPadding(new Insets(10, 5, 0, 5));

    BorderPane.setAlignment(header, Pos.CENTER);
    mainPane.setTop(header);
    mainPane.setLeft(new PlaylistPane());
    mainPane.setRight(new SongsPane());

    // add a listener so that whenever a playlist is selected in the ListView,
    // the TableView will refresh to display the list of songs associated with the
    // playlist
    playlists.getSelectionModel().selectedItemProperty()
        .addListener((ObservableValue<? extends Playlist> observable, Playlist oldValue, Playlist newValue) -> {
          if (playlists.getSelectionModel().getSelectedIndex() != -1) {
            songs.refresh(playlists.getSelectionModel().getSelectedItem());
          }
        });

    Scene scene = new Scene(mainPane);

    this.stage.setTitle("Final Project");
    this.stage.setScene(scene);

    this.stage.setResizable(false);
    this.stage.show();
  }

  ///////////// METHODS /////////////
  /**
   * Getter to get the stage
   * 
   * @return stage
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * Implementation of interface method that returns a list of the playlists
   * currently loaded into the program
   * 
   * @return ObservableList<Playlist> list of playlists in the program
   */
  @Override
  public ObservableList<Playlist> onChildRequest() {
    return playlists.getItems();
  }

  /**
   * Implementation of interface method that sets the program's list of playlists
   * to the received list, which will be coming from the Edit window
   * 
   * @param list updated list of the playlists and their songs
   */
  @Override
  public void onChildUpdate(ObservableList<?> list) {
    playlists.getItems().setAll((ObservableList<Playlist>) list);
    int index = playlists.getSelectionModel().getSelectedIndex();
    if (index != -1) {
      songs.refresh(playlists.getSelectionModel().getSelectedItem());
    }
    playlists.getSelectionModel().clearAndSelect(0);
  }

  ///////////// INNER CLASSES //////////////

  /**
   * Inner class to set up the Playlist controls and list in the main window (on
   * the left)
   */
  class PlaylistPane extends VBox {

    ///////// FIELD //////////
    FileChooser fileChooser = new FileChooser(); // field (to establish initial directory
                                                 // whenever a dialog box opens up

    ////////// CONSTRUCTOR /////////
    public PlaylistPane() {

      VBox playlistBox = new VBox();

      // set up the ListView for the playlists
      playlists = new ListView<>();

      playlistBox.getChildren().add(playlists);

      setSpacing(15);

      playlists.setMaxWidth(150);
      playlists.setMinWidth(150);
      playlists.setMaxHeight(260);

      playlists.setItems(FXCollections.observableArrayList());

      playlists.setPlaceholder(new Label("Add a playlist"));
      playlists.setEditable(true);

      // since the ListView for the playlists is editable, a converter
      // must be used to update the Playlist with the String entered by
      // the user, and to display the Playlist in the ListView appropriately
      // by its name
      StringConverter<Playlist> converter = new StringConverter<Playlist>() {
        @Override
        public String toString(Playlist p) {
          if (p != null) {
            return p.getName();
          } else {
            return "";
          }
        }

        @Override
        public Playlist fromString(String string) {
          if (string != null && !string.trim().isEmpty()) {
            return new Playlist(string, playlists.getSelectionModel().getSelectedItem().getFilePath());
          } else if (string != null && string.trim().isEmpty()) {
            return playlists.getSelectionModel().getSelectedItem();
          } else {
            return null;
          }
        }
      };

      // add the converter to the ListView
      playlists.setCellFactory(TextFieldListCell.forListView(converter));

      // when the user attempts to rename the playlist, update the playlist name
      // and the file name & path to reflect the change (if the user enters
      // whitespace only or there is already a file in the same directory
      // with that name, an error message will be shown and the changes will not
      // persist)
      playlists.setOnEditCommit((ListView.EditEvent<Playlist> event) -> {
        try {
          Playlist temp = event.getNewValue();
          String newName = temp.getName();
          File directory = new File(temp.getFilePath()).getParentFile();
          Playlist updatedPlaylist = playlists.getItems().get(event.getIndex());

          temp.setFilePath(directory + newName + EXT);

          boolean duplicate = playlists.getItems().contains(temp);

          if (!newName.equals(updatedPlaylist.getName()) && !duplicate) {
            new M3uHandler().rename(updatedPlaylist.getFilePath(), newName);
            updatedPlaylist.setName(newName);
            updatedPlaylist.setFilePath(directory + newName + EXT);
          } else if (!newName.equals(updatedPlaylist.getName()) && duplicate) {
            throw new IOException(
                "The current playlist cannot be renamed while the overridden playlist is loaded into the program.");
          }
        } catch (Exception ex) {
          Alert confirmBox = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
          confirmBox.setTitle("Playlist Rename");
          confirmBox.setHeaderText("Error: Unsuccessful Rename");
          confirmBox.showAndWait();
        }
      });

      // set up the buttons
      VBox btns = new VBox();
      Button btnLoad = new Button("Load");
      double width = 100;

      btnLoad.setMaxWidth(width);
      Button btnEdit = new Button("Edit");

      btnEdit.setMaxWidth(width);
      Button btnCreate = new Button("Create New");

      btnCreate.setMaxWidth(width);
      Button btnDlt = new Button("Delete");

      btnDlt.setMaxWidth(width);

      btns.setSpacing(5);
      btns.setAlignment(Pos.CENTER);

      // if a playlist is selected to be deleted, a dialog box will pop up
      // asking if the user would like to remove the playlist from the hard disk
      // as well, and will be notified whether the operation was successful or not;
      // otherwise an error message will pop up notifying the user no playlists were
      // selected or none were loaded into the program
      btnDlt.setOnAction((ActionEvent event) -> {
        int index = playlists.getSelectionModel().getSelectedIndex();

        if (index == -1) {

          Alert errorBox = new Alert(Alert.AlertType.ERROR);
          errorBox.setTitle("Playlist Deletion");

          if (playlists.getItems().size() == 0) {
            errorBox.setHeaderText("Error: No Playlists Detected");
            errorBox.setContentText(
                "No playlists loaded into the program." + "\nPlease add a playlist to the program before deleting.");
          } else {
            errorBox.setHeaderText("Error: No Playlist Selected");
            errorBox.setContentText("No playlist to remove." + "\nPlease select a playlist to delete.");
          }

          errorBox.showAndWait();

        } else {
          Playlist playlist = playlists.getSelectionModel().getSelectedItem();
          File file = new File(playlist.getFilePath());
          String content = "Delete \"" + playlist.getName() + "\" from the hard disk as well?";
          Alert confirmBox = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO,
              ButtonType.CANCEL);
          confirmBox.setTitle("Confirm Deletion");
          confirmBox.setHeaderText("Delete Playlist");
          confirmBox.showAndWait();

          if (confirmBox.getResult() != ButtonType.CANCEL) {

            Alert notifyBox = new Alert(Alert.AlertType.INFORMATION);
            playlists.getItems().remove(index);

            String playlistName = playlist.getName();
            String result;

            if (confirmBox.getResult() == ButtonType.YES) {
              boolean deleted = file.delete();
              if (deleted) {
                result = "Playlist \"" + playlistName + "\" was successfully removed from the hard disk.";
              } else {
                result = "Playlist \"" + playlistName + "\" was not removed from the hard disk.";
              }
            } else {
              result = "Playlist \"" + playlistName + "\" was successfully removed from the program.";
            }
            if (index != -1) {
              songs.clear();
            }
            notifyBox.setContentText(result);
            notifyBox.showAndWait();
          }
        }
      });

      // button listener to create a new playlist; any errors that occur
      // will result in a dialog box notifying the user
      btnCreate.setOnAction((ActionEvent event) -> {
        ExtensionFilter extFilter = new ExtensionFilter("M3U files (*.m3u)", "*" + EXT);
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
          try {
            String filePath = file.getAbsolutePath();
            String playlistName = filePath.substring(0, filePath.length() - EXT.length());
            playlistName = playlistName.substring(playlistName.lastIndexOf("\\") + 1);

            M3uHandler fileHandler = new M3uHandler();
            Playlist newPlaylist = new Playlist(playlistName, filePath);
            fileHandler.write(newPlaylist);
            playlists.getItems().add(newPlaylist);
            playlists.getSelectionModel().selectLast();
          } catch (Exception ex) {
            Alert errorBox = new Alert(Alert.AlertType.ERROR);
            errorBox.setTitle("Loading Playlist");
            errorBox.setHeaderText("Error");
            errorBox.setContentText(ex.getMessage());
            errorBox.showAndWait();
          }
        }
      });

      // user can load an existing playlist of the M3U extension into the app;
      // if the playlist is already loaded, the user will be notified with an error
      // message;
      // any exceptions occurring will also be displayed in a dialog box
      btnLoad.setOnAction((ActionEvent event) -> {
        ExtensionFilter extFilter = new ExtensionFilter("M3U files (*.m3u)", "*" + EXT);
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
          try {
            fileChooser.setInitialDirectory(file.getParentFile());
            String filePath = file.getAbsolutePath();
            M3uHandler handler = new M3uHandler();
            Playlist p = handler.read(filePath);
            if (playlists.getItems().contains(p)) {
              String message = "Playlist is already opened in the program.";
              Alert messageBox = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
              messageBox.setTitle("Loading Playlist");
              messageBox.setHeaderText("Error: Duplicate Playlist");
              messageBox.showAndWait();
            } else {
              playlists.getItems().add(p);
              playlists.getSelectionModel().selectLast();
            }
          } catch (Exception ex) {
            Alert errorBox = new Alert(Alert.AlertType.ERROR);
            errorBox.setTitle("Loading Playlist");
            errorBox.setHeaderText("Error");
            errorBox.setContentText(ex.getMessage());
            errorBox.showAndWait();
          }

        }
      });

      // opens up a new window to edit the individual playlists, if any are
      // loaded into the program
      btnEdit.setOnAction((ActionEvent event) -> {
        if (playlists.getItems().size() > 0) {
          new EditScreen(MainScreen.this);
        } else {
          String message = "There are no playlists to edit.";
          Alert errorBox = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
          errorBox.setTitle("Playlist Edit");
          errorBox.setHeaderText("Error: No Playlists Loaded");
          errorBox.showAndWait();
        }
      });

      // add each component to the pane and establish spacing
      setPadding(new Insets(10, 10, 20, 20));

      btns.getChildren().add(btnLoad);
      btns.getChildren().add(btnEdit);
      btns.getChildren().add(btnCreate);
      btns.getChildren().add(btnDlt);

      getChildren().add(playlistBox);
      getChildren().add(btns);

      setAlignment(Pos.CENTER);

    }
  }

  /**
   * Inner class to establish the TableView for the songs as well as set up the
   * spacing and alignment
   */
  class SongsPane extends StackPane {

    ///////// FIELDS //////////
    final int SONGS_PANE_WIDTH = 700;
    final int SONGS_PANE_HEIGHT = 396;

    /////////// CONSTRUCTORS //////////
    public SongsPane() {

      songs = new SongsTable(FXCollections.observableArrayList(), SONGS_PANE_WIDTH, SONGS_PANE_HEIGHT, true);
      setPadding(new Insets(10, 20, 20, 10));
      setAlignment(Pos.CENTER);
      songs.setPlaceholder(new Label("No songs loaded"));

      getChildren().add(songs);

    }
  }
}
