package main.java.com.resong.muplay.model;

import javafx.beans.property.*;

/**
 * Class representing an MP3 file in the program; child class of Record. Methods
 * include those that establish its properties so as to allow JavaFX binding,
 * particularly when binding fields to specific columns in the TableView of the
 * windows.
 *
 * @author Rebecca Song
 */
public class Mp3Record extends Record {

  /////////// FIELDS ////////////
  private final static int MINUTES_IN_HOUR = 60;
  private final static int SECONDS_IN_MINUTE = 60;

  // both length and duration keep track of the duration of the
  // mp3 track; but duration maintains it in seconds, while length
  // is formatted as hours:minutes:seconds
  private SimpleStringProperty artist, title, album, genre, year, length;
  private SimpleIntegerProperty duration;

  /////////// CONSTRUCTORS //////////
  public Mp3Record() {
    this("", "");
  }

  public Mp3Record(String location, String title) {
    this(location, title, "", "", "", "", 0);
  }

  public Mp3Record(String location, String title, String artist, String album, String genre, String year,
      int duration) {
    super(location);
    setArtist(artist);
    setTitle(title);
    setAlbum(album);
    setGenre(genre);
    setYear(year);
    setDuration(duration);
    setLength(duration);
  }

  public Mp3Record(String location, String title, String artist, String album, String genre, String year,
      String length) {
    super(location);
    setArtist(artist);
    setTitle(title);
    setAlbum(album);
    setGenre(genre);
    setYear(year);
    setTime(length);
  }

  ///////////////// METHODS ////////////////////
  /**
   * Method to format time in seconds to hours:minutes:seconds
   * 
   * @param songLength duration of track in seconds
   * @return String of format hh:mm:ss
   */
  public static String formatTime(int songLength) {
    int seconds = songLength % SECONDS_IN_MINUTE;
    int totalMinutes = songLength / SECONDS_IN_MINUTE;
    int minutes = totalMinutes % MINUTES_IN_HOUR;
    int hours = totalMinutes / MINUTES_IN_HOUR;

    if (hours == 0) {
      String time = String.format("%d:%2d", minutes, seconds);
      return time.replaceAll(" ", "0");
    } else {
      String time = String.format("%d:%2d:%2d", hours, minutes, seconds);
      return time.replaceAll(" ", "0");
    }

  }

  // getters and setters
  public final String getArtist() {
    return artist.get();
  }

  public final void setArtist(String artist) {
    artistProperty().set(artist);
  }

  public final String getTitle() {
    return title.get();
  }

  public final void setTitle(String title) {
    titleProperty().set(title);
  }

  public final String getAlbum() {
    return album.get();
  }

  public final void setAlbum(String album) {
    albumProperty().set(album);
  }

  public final String getGenre() {
    return genre.get();
  }

  public final void setGenre(String genre) {
    genreProperty().set(genre);
  }

  public final int getDuration() {
    return duration.get();
  }

  public final void setDuration(int duration) {
    durationProperty().set(duration);
    lengthProperty().set(formatTime(duration));
  }

  public final String getYear() {
    return year.get();
  }

  public final void setYear(String year) {
    yearProperty().set(year);
  }

  public final String getLength() {
    return length.get();
  }

  public final void setLength(int length) {
    lengthProperty().set(formatTime(length));
  }

  public final void setTime(String length) {
    try {
      lengthProperty().set(formatTime(Integer.parseInt(length)));
      durationProperty().set(Integer.parseInt(length));
    } catch (Exception ex) {
      lengthProperty().set("");
      durationProperty().set(0);
    }
  }

  // methods establishing fields as JavaFX properties
  public SimpleStringProperty titleProperty() {
    if (title == null) {
      title = new SimpleStringProperty();
    }
    return title;
  }

  public SimpleStringProperty artistProperty() {
    if (artist == null) {
      artist = new SimpleStringProperty();
    }
    return artist;
  }

  public SimpleStringProperty albumProperty() {
    if (album == null) {
      album = new SimpleStringProperty();
    }
    return album;
  }

  public SimpleStringProperty genreProperty() {
    if (genre == null) {
      genre = new SimpleStringProperty();
    }
    return genre;
  }

  public SimpleStringProperty yearProperty() {
    if (year == null) {
      year = new SimpleStringProperty();
    }
    return year;
  }

  public SimpleIntegerProperty durationProperty() {
    if (duration == null) {
      duration = new SimpleIntegerProperty();
    }
    return duration;
  }

  public SimpleStringProperty lengthProperty() {
    if (length == null) {
      length = new SimpleStringProperty();
    }
    return length;
  }

  /**
   * Equals method - two Mp3Records are the same if they have the same file path
   * 
   * @param obj Mp3Record
   * @return true if file path is equal, false otherwise
   */
  @Override
  public boolean equals(Object obj) {

    if (obj instanceof Mp3Record) {
      if ((this.getFilePath()).equals(((Mp3Record) obj).getFilePath())) {
        return true;
      }
    }
    return false;
  }

}
