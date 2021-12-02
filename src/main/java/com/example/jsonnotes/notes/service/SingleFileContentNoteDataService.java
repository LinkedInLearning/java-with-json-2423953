package com.example.jsonnotes.notes.service;

import com.example.jsonnotes.notes.Note;
import com.example.jsonnotes.util.NoteHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SingleFileContentNoteDataService extends SingleFileNoteDataService {

    /**
     * Single instance of {@link SingleFileContentNoteDataService}
     */
    private static SingleFileContentNoteDataService _instance;

    /**
     * Create a new single instance of {@link SingleFileContentNoteDataService}
     * @param dataPath Absolute path to the directory where the {@link SingleFileNoteDataService#_notesFile} will be located
     * @return {@link SingleFileContentNoteDataService}
     * @throws IllegalArgumentException Thrown when no dataPath is specified and no {@link SingleFileContentNoteDataService#_instance} is available
     */
    public static SingleFileContentNoteDataService getInstance(String dataPath) throws IllegalArgumentException {
        if (dataPath != null) {
            //When a dataPath is provided, generate a new instance
            _instance = new SingleFileContentNoteDataService(dataPath);
        }
        if (_instance == null) {
            //When no dataPath is specified and there is no current instance, we cannot continue.
            throw new IllegalArgumentException("No instance available and no dataPath specified. Cannot proceed.");
        }
        return _instance;
    }

    /**
     * Create a new instance of a {@link SingleFileContentNoteDataService} pointing at a specific path
     */
    public SingleFileContentNoteDataService(String dataPath) throws RuntimeException {
        super(dataPath);
    }

    @Override
    protected List<Note> getNotesFromFileData(String fileData) {
        //Initialize a new list of Notes
        var notes = new ArrayList<Note>();
        //Check if there is any data to convert
        if (Objects.equals(fileData, "")) {
            //Return an empty list if there is nothing to convert - save us the work
            return notes;
        }
        //Convert the string to a list of Notes
        return NoteHelper.getNotesFromDataString(fileData);
    }

    @Override
    protected String getFileDataFromNotes(List<Note> notes) {
        //Initialize an empty data string
        var noteString = "";
        //Check if there is any data to convert
        if (notes == null || notes.size() == 0) {
            //Return the empty data string if there is nothing to convert - save us the work
            return noteString;
        }
        //Convert the list of Notes to a data string that can be saved to file
        return NoteHelper.getNotesDataStringFromList(notes);
    }
}
