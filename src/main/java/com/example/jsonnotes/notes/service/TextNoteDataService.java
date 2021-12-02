package com.example.jsonnotes.notes.service;

import com.example.jsonnotes.notes.Note;

/**
 * A data service for {@link Note Notes} that stores all data in individual text files that only contain the content of the {@link Note}.
 */
public class TextNoteDataService extends FileNoteDataService {
    /**
     * Single instance of {@link TextNoteDataService}
     */
    private static TextNoteDataService _instance;

    /**
     * Create a new single instance of {@link TextNoteDataService}
     * @param dataPath Absolute path to the directory where {@link Note notes} are stored
     * @return {@link TextNoteDataService}
     * @throws IllegalArgumentException Thrown when no dataPath is specified and no {@link TextNoteDataService#_instance} is available
     */
    public static TextNoteDataService getInstance(String dataPath) throws IllegalArgumentException {
        if (dataPath != null) {
            //When a dataPath is provided, generate a new instance
            _instance = new TextNoteDataService(dataPath);
        }
        if (_instance == null) {
            //When no dataPath is specified and there is no current instance, we cannot continue.
            throw new IllegalArgumentException("No instance available and no dataPath specified. Cannot proceed.");
        }
        return _instance;
    }

    /**
     * Create a new instance of a {@link TextNoteDataService} pointing at a specific path
     */
    public TextNoteDataService(String dataPath) {
        super(dataPath);
    }

    @Override
    protected Note getNoteFromFileData(String fileData) {
        return new Note(fileData);
    }

    @Override
    protected String getFileDataFromNote(Note note) {
        return note.getContent();
    }
}
