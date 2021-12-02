package com.example.jsonnotes.notes.service;

import com.example.jsonnotes.notes.Note;
import com.google.gson.Gson;

/**
 * A data service for {@link Note Notes} that stores all data in individual text files that only contain the content of the {@link Note}.
 */
public class JsonNoteDataService extends FileNoteDataService {
    /**
     * Single instance of {@link JsonNoteDataService}
     */
    private static JsonNoteDataService _instance;

    /**
     * Create a new single instance of {@link JsonNoteDataService}
     * @param dataPath Absolute path to the directory where {@link Note notes} are stored
     * @return {@link JsonNoteDataService}
     * @throws IllegalArgumentException Thrown when no dataPath is specified and no {@link JsonNoteDataService#_instance} is available
     */
    public static JsonNoteDataService getInstance(String dataPath) throws IllegalArgumentException {
        if (dataPath != null) {
            //When a dataPath is provided, generate a new instance
            _instance = new JsonNoteDataService(dataPath);
        }
        if (_instance == null) {
            //When no dataPath is specified and there is no current instance, we cannot continue.
            throw new IllegalArgumentException("No instance available and no dataPath specified. Cannot proceed.");
        }
        return _instance;
    }

    /**
     * Create a new instance of a {@link JsonNoteDataService} pointing at a specific path
     */
    public JsonNoteDataService(String dataPath) {
        super(dataPath);
    }

    @Override
    protected Note getNoteFromFileData(String fileData) {
        var gson = new Gson();
        return gson.fromJson(fileData, Note.class);
    }

    @Override
    protected String getFileDataFromNote(Note note) {
        var gson = new Gson();
        return gson.toJson(note);
    }
}
