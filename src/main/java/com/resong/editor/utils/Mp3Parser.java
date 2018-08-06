package main.java.com.resong.editor.utils;

import java.io.IOException;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import main.java.com.resong.editor.model.Mp3Record;

/**
 * Class that parses an MP3 file to obtain the appropriate title, artist, etc.
 * fields from its tag by determining whether it has an ID3v1 tag or ID3v2 tag.
 *
 * @author Rebecca Song
 */
public class Mp3Parser {

    final static String EXT = ".mp3";
    
    /**
     * Method to parse Mp3Record and obtain the corresponding metadata.
     *
     * @param record Mp3Record to be parsed
     * @return updated Mp3Record with fields filled in from the MP3 file tag
     * @throws IOException error reading bytes of the MP3 file
     * @throws UnsupportedTagException Tag version not supported
     * @throws InvalidDataException Frames not found
     */
    public Mp3Record parse(Mp3Record record) throws IOException, UnsupportedTagException, InvalidDataException {

        String filePath = record.getFilePath();
        Mp3File mp3File = new Mp3File(filePath);
        if (mp3File.hasId3v2Tag()) {
            ID3v2 tag = (ID3v2) mp3File.getId3v2Tag();
            record.setTitle(tag.getTitle());
            record.setArtist(tag.getArtist());
            record.setAlbum(tag.getAlbum());
            record.setGenre(tag.getGenreDescription());
            record.setYear(tag.getYear());
        } else {
            ID3v1 tag = mp3File.getId3v1Tag();
            record.setTitle(tag.getTitle());
            record.setArtist(tag.getArtist());
            record.setAlbum(tag.getAlbum());
            record.setGenre(tag.getGenreDescription());
            record.setYear(tag.getYear());
        }

        if (record.getTitle() == null || record.getTitle().isEmpty()) {
            String name = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length() - EXT.length());
            record.setTitle(name);
        }

        int length = (int) Math.ceil(mp3File.getLengthInSeconds());

        record.setDuration(length);

        return record;
    }

}
