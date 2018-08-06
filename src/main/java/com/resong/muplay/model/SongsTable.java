package main.java.com.resong.muplay.model;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * Class inheriting from TableView; customised to bind to fields of the
 * Mp3Record class.
 *
 * @author Reso
 */
public class SongsTable extends TableView {

  /////// FIELDS //////
  final static double TEXT_FLD_LENGTH = 5.0 / 24;
  final static double NUMERIC_FLD_LENGTH = 2.0 / 24;

  ObservableList<Mp3Record> songs;

  ///////// CONSTRUCTOR //////////
  public SongsTable(ObservableList<Mp3Record> list, int width, int height, boolean flag) {

    // set up table
    super();
    songs = list;
    setItems(songs);
    setMinWidth(width);
    setMaxHeight(height);

    // redistribute leftover space among all the columns
    setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

    // create table columns and bind them to appropriate fields
    TableColumn<Mp3Record, String> titleCol = new TableColumn<>("Title");
    TableColumn<Mp3Record, String> artistCol = new TableColumn<>("Artist");

    getColumns().addAll(titleCol, artistCol);
    titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
    artistCol.setCellValueFactory(cellData -> cellData.getValue().artistProperty());

    // if flag is true (i.e. requires the full table), include additional columns
    if (flag) {
      TableColumn<Mp3Record, String> albumCol = new TableColumn<>("Album");
      TableColumn<Mp3Record, String> genreCol = new TableColumn<>("Genre");
      TableColumn<Mp3Record, String> yearCol = new TableColumn<>("Year");
      TableColumn<Mp3Record, String> timeCol = new TableColumn<>("Time");

      getColumns().addAll(albumCol, genreCol, yearCol, timeCol);

      // custom cell to align the text to the right
      Callback alignCell = (Callback<TableColumn<Mp3Record, String>, TableCell<Mp3Record, String>>) (
          TableColumn<Mp3Record, String> p) -> {
        TableCell<Mp3Record, String> cell = new TableCell<Mp3Record, String>() {
          @Override
          public void updateItem(String item, boolean empty) {
            if (item == null | empty) {
              setText(null);
            } else {
              setText(item);
            }
          }
        };
        cell.setStyle("-fx-alignment: CENTER-RIGHT;");
        return cell;
      };

      titleCol.setMinWidth(TEXT_FLD_LENGTH * width);
      artistCol.setMinWidth(TEXT_FLD_LENGTH * width);
      albumCol.setMinWidth(TEXT_FLD_LENGTH * width);
      albumCol.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
      genreCol.setMinWidth(TEXT_FLD_LENGTH * width);
      genreCol.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
      yearCol.setMinWidth(NUMERIC_FLD_LENGTH * width);
      yearCol.setCellValueFactory(cellData -> cellData.getValue().yearProperty());
      yearCol.setCellFactory(alignCell);
      timeCol.setMinWidth(NUMERIC_FLD_LENGTH * width);
      timeCol.setCellValueFactory(cellData -> cellData.getValue().lengthProperty());
      timeCol.setCellFactory(alignCell);
    }
  }

  ///////// METHODS /////////
  /**
   * Method to clear the table of all elements if there are any elements in it
   */
  public void clear() {
    if (songs.size() > 0) {
      songs.clear();
    }
  }

  /**
   * Method to refresh the table with Mp3Records from the Playlist
   * 
   * @param p Playlist from which to get the records to fill the table with
   */
  public void refresh(Playlist p) {
    if (p != null) {
      clear();
      songs.addAll(p.getRecords());
    }
  }
}
