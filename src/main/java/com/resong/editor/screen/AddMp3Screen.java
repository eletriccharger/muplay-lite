package main.java.com.resong.editor.screen;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.com.resong.editor.interfaces.ChildInterface;
import main.java.com.resong.editor.model.Mp3Record;

/**
 * Add screen that permits the user to select multiple songs already loaded
 * into the program to add to the currently selected playlist
 * 
 * @author Rebecca Song
 */
public class AddMp3Screen {

    ///////// FIELDS /////////
    private ObservableList<Mp3Record> songs;

    ////////// CONSTRUCTOR ///////////
    public AddMp3Screen(ChildInterface parent) {

        EditScreen editScreen = (EditScreen) parent;

        Stage primaryStage = new Stage();

        // establishes modality and ownership of the current window 
        // in relation to the "Edit" window
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.initOwner(editScreen.getStage());

        // obtain the list of unique songs from the "Edit" screen
        songs = FXCollections.observableArrayList(editScreen.onChildRequest());

        primaryStage.setTitle("Add Songs");
        VBox main = new VBox(10);

        ListView<Mp3Record> songList = new ListView<>();
        
        // customise how the Mp3Records are displayed in the ListView;
        // if the artist is null or empty, display them as "Unknown"
        songList.setCellFactory(new Callback<ListView<Mp3Record>, ListCell<Mp3Record>>() {
            @Override
            public ListCell<Mp3Record> call(ListView<Mp3Record> list) {
                ListCell<Mp3Record> cell = new ListCell<Mp3Record>() {
                    @Override
                    protected void updateItem(Mp3Record record, boolean val) {
                        super.updateItem(record, val);
                        if (record != null) {
                            String artist = record.getArtist();
                            if (artist == null || artist.trim().isEmpty()) {
                                artist = "Unknown";
                            }
                            setText(record.getTitle() + " by " + artist);
                        }
                    }
                };
                return cell;
            }
        }
        );
        songList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        songList.setItems(songs);
        songList.setMaxHeight(300);

        HBox btnPanel = new HBox(10);
        Button add = new Button("Add");
        
        // add a listener to the "Add" button: if the user selected one or more songs,
        // add them to the currently selected playlist and close the window;
        // otherwise display an error message
        add.setOnAction((ActionEvent event) -> {
            ObservableList<Mp3Record> selectedItems = songList.getSelectionModel().getSelectedItems();
            if (selectedItems != null && selectedItems.size() > 0) {
                editScreen.onChildUpdate(selectedItems);
                primaryStage.close();
            } else {
                String errorMsg = "No songs selected to add to the current playlist.";
                Alert errorBox = new Alert(Alert.AlertType.ERROR, errorMsg, ButtonType.OK);
                errorBox.setTitle("Add Songs");
                errorBox.setHeaderText("Error: No Songs Selected");
                errorBox.showAndWait();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction((ActionEvent event) -> {
            primaryStage.close();
        });

        btnPanel.getChildren().addAll(add, cancel);
        btnPanel.setAlignment(Pos.CENTER);

        main.getChildren().addAll(songList, btnPanel);
        main.setPadding(new Insets(20));
        Scene scene = new Scene(main);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
