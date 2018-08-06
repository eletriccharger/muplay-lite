package main.java.com.resong.editor.screen;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.java.com.resong.editor.controller.M3uHandler;
import main.java.com.resong.editor.interfaces.ChildInterface;
import main.java.com.resong.editor.model.Mp3Record;
import main.java.com.resong.editor.model.Playlist;
import main.java.com.resong.editor.model.SongsTable;
import main.java.com.resong.editor.utils.Mp3Parser;

/**
 * Edit screen that permits the user to add or delete songs from each of the
 * playlists loaded into the program. The user can add a song that is already
 * loaded into the program, or the user can add a new mp3 file.
 *
 * Class implements ChildInterface to receive the selected mp3 songs from the
 * "Add" screen, when adding a song already loaded into the program.
 *
 * @author Rebecca Song
 */
public class EditScreen implements ChildInterface {

    ////////// FIELDS ////////////
    private Stage primaryStage;
    private ObservableList<Playlist> playlistList;
    private ObservableList<Mp3Record> allSongs = FXCollections.observableArrayList();
    ObservableList<Mp3Record> comparer = FXCollections.observableArrayList();
    private SongsTable songs;
    private ComboBox<Playlist> comboBox;
    FileChooser fileChooser = new FileChooser();

    ////////////// CONSTRUCTOR /////////////
    public EditScreen(ChildInterface parent) {

        MainScreen mainWindow = (MainScreen) parent;

        // establishes modality and ownership of the current window 
        // in relation to the main window
        primaryStage = new Stage();
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(mainWindow.getStage());

        // set up the window, including the ComboBox and TableView
        primaryStage.setTitle("Edit Playlist");

        HBox panel = new HBox(50);

        comboBox = new ComboBox<>();
        songs = new SongsTable(FXCollections.observableArrayList(), 400, 250, false);

        comboBox.setPrefWidth(200);

        // obtain the list of playlists from the main window
        playlistList = FXCollections.observableArrayList(mainWindow.onChildRequest());

        // keep track of all songs (including duplicates) in a list
        for (Playlist playlist : playlistList) {
            comparer.addAll(playlist.getRecords());
            for (Mp3Record song : playlist.getRecords()) {
                if (!allSongs.contains(song)) {
                    allSongs.add(song);
                }
            }
        }

        comboBox.setItems(playlistList);

        // if there are playlists loaded into the program,
        // display them in the ComboBox and have the default set to the
        // first playlist in the list
        if (playlistList.size() > 0) {
            Playlist currentPlaylist = playlistList.get(0);
            comboBox.setValue(currentPlaylist);
            songs.refresh(currentPlaylist);
        }

        songs.setPlaceholder(new Label("No songs loaded"));
        songs.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // can select more than one item

        // create another converter for the ComboBox; because it's not
        // editable, only the toString method needs to be established
        comboBox.setConverter(new StringConverter<Playlist>() {
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
                return null;
            }
        });

        // add a listener that refreshes the TableView to display the songs from
        // the newly selected playlist in the ComboBox
        comboBox.valueProperty().addListener((observable, oldPlaylist, newPlaylist) -> {
            if (newPlaylist != null) {
                songs.refresh(newPlaylist);
            }
        });

        // set up the buttons
        VBox main = new VBox(10);
        Button add = new Button("Add");

        add.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // add button that opens a window displaying a list of the unique songs
        // currently loaded into the program that the user can select from
        add.setOnAction((ActionEvent event) -> {
            new AddMp3Screen(EditScreen.this);
        });

        Button addNew = new Button("Add New");

        addNew.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // add new button that allows the user to add an mp3 file to the program
        // that wasn't previously loaded
        addNew.setOnAction((ActionEvent event) -> {
            try {
                ExtensionFilter extFilter = new ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
                fileChooser.getExtensionFilters().add(extFilter);
                List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);

                if (files != null) {
                    fileChooser.setInitialDirectory(files.get(0).getParentFile());

                    Mp3Parser musicParser = new Mp3Parser();
                    for (File file : files.toArray(new File[files.size()])) {
                        String filePath = file.getAbsolutePath();

                        Mp3Record mp3 = new Mp3Record(filePath, "");
                        mp3 = musicParser.parse(mp3);
                        comboBox.getValue().add(mp3);
                        comparer.add(mp3);
                        if (!allSongs.contains(mp3)) {
                            allSongs.add(mp3);
                        }
                    }
                    songs.refresh(comboBox.getValue());
                }
            } catch (Exception ex) {
                Alert errorBox = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                errorBox.setTitle("Add New Song");
                errorBox.setHeaderText("Error");
                errorBox.showAndWait();
            }
        });

        Button delete = new Button("Delete");
        delete.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // delete button that will remove the selected songs from the
        // current playlist, and also remove them from the "Add" window
        // only if all copies of the song have been removed from the playlists
        // (i.e. as long as one playlist contains the song, it will appear in
        // the list of songs in the "Add" window)
        delete.setOnAction((ActionEvent event) -> {
            ObservableList<Integer> selectedIndices = songs.getSelectionModel().getSelectedIndices();
            if (selectedIndices.size() > 0) {
                Playlist currentlySelected = comboBox.getValue();

                ObservableList<Integer> orderedList = FXCollections.observableArrayList(selectedIndices);
                ObservableList<Mp3Record> selectedItems = FXCollections.observableArrayList();

                // order the list of selected indices in descending order
                // so that the indices do not change as the items are removed
                FXCollections.sort(orderedList);
                FXCollections.reverse(orderedList);

                for (Integer index : orderedList) {
                    selectedItems.add(currentlySelected.delete(index));
                }

                // remove the songs from the list that is used as a comparison
                // for the "Add" window list
                for (Mp3Record song : selectedItems) {
                    comparer.remove(song);
                }

                // must use an iterator to remove items while looping through 
                for (Iterator<Mp3Record> iter = allSongs.iterator(); iter.hasNext();) {
                    Mp3Record song = iter.next();
                    if (!comparer.contains(song)) {
                        iter.remove();
                    }
                }

                songs.refresh(currentlySelected);

            } else {
                String errorMsg = "No songs selected to delete from the current playlist.";
                Alert errorBox = new Alert(Alert.AlertType.ERROR, errorMsg, ButtonType.OK);
                errorBox.setTitle("Delete Songs");
                errorBox.setHeaderText("Error: No Songs Selected");
                errorBox.showAndWait();
            }
        });

        HBox buttons = new HBox(15);

        buttons.getChildren().addAll(add, addNew, delete);

        panel.getChildren().addAll(comboBox, buttons);

        HBox acceptPanel = new HBox(10);
        Button ok = new Button("OK");
        Button cancel = new Button("Cancel");

        // when "Ok" is pressed, the list of playlists is
        // sent back to the main window, and the files for
        // each playlist is overwritten with the newly edited playlists;
        // if an error occurs a message is displayed notifying the user
        ok.setOnAction((ActionEvent event) -> {
            parent.onChildUpdate(playlistList);
            M3uHandler handler = new M3uHandler();
            for (Playlist playlist : playlistList) {
                try {
                    handler.write(playlist);
                } catch (Exception ex) {
                    Alert errorBox = new Alert(Alert.AlertType.ERROR);
                    errorBox.setTitle("Updating Playlist");
                    errorBox.setHeaderText("Error");
                    errorBox.setContentText(ex.getMessage());
                    errorBox.showAndWait();
                }
            }
            primaryStage.close();   // close the window
        });

        cancel.setOnAction((ActionEvent event) -> {
            primaryStage.close();   // close the window upon cancel
        });

        acceptPanel.getChildren().addAll(ok, cancel);
        acceptPanel.setAlignment(Pos.CENTER);

        main.getChildren().addAll(panel, songs, acceptPanel);
        main.setPadding(new Insets(20));
        Scene scene = new Scene(main);

        primaryStage.setScene(scene);

        primaryStage.show();

    }

    //////////// METHODS ///////////
    /**
     * Getter to return the current stage
     *
     * @return
     */
    public Stage getStage() {
        return this.primaryStage;
    }

    /**
     * Implementation of ChildInterface method that returns the list of unique
     * songs currently loaded into the program
     *
     * @return ObservableList<Mp3Record> list of unique songs in program
     */
    @Override
    public ObservableList<Mp3Record> onChildRequest() {
        return allSongs;
    }

    /**
     * Implementation of ChildInterface method that adds the list of songs
     * received to the currently selected playlist
     *
     * @param list list of Mp3Records to add to the current playlist
     */
    @Override
    public void onChildUpdate(ObservableList<?> list) {
        ObservableList<Mp3Record> moreSongs = (ObservableList<Mp3Record>) list;
        Playlist selectedPlaylist = comboBox.getValue();
        selectedPlaylist.addAll(moreSongs);
        comparer.addAll(moreSongs);
        songs.refresh(selectedPlaylist);
    }

}
