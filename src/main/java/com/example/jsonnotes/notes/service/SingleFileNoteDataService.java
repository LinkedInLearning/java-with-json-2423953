package com.example.jsonnotes.notes.service;

import com.example.jsonnotes.notes.Note;
import com.example.jsonnotes.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * A data service for {@link Note Notes} that stores all data in a single {@link File}
 */
public abstract class SingleFileNoteDataService implements INoteDataService {

    /**
     * The name of the file that contains {@link Note notes}
     */
    protected final static String _NOTES_FILE_NAME = "notes.txt";
    /**
     * The file that contains the {@link Note notes}
     */
    protected final File _notesFile;
    /**
     * A collection of the notes managed by this service. {@link Note} data is supplied from this collection when read.
     * Updates to the {@link Note notes} in this collection will cause the {@link SingleFileNoteDataService#_notesFile} to be rewritten.
     */
    protected Map<String, Note> _notes = new HashMap<>();

    /**
     * Create a new instance of a {@link SingleFileNoteDataService} pointing at a specific path
     * @exception RuntimeException thrown when the SingleFileNoteDataService cannot be created
     */
    SingleFileNoteDataService(String dataPath) throws RuntimeException {
        _notesFile = Path.of(dataPath, _NOTES_FILE_NAME).toFile();
        try {
            if (!_notesFile.createNewFile()) {
                //file already exists so we should try to load notes
                _readNotesFromFile();
            } else {
                //file was created - initialize an empty collection of notes
                _notes = new HashMap<>();
            }
        } catch (IOException | SecurityException exception) {
            exception.printStackTrace();
            System.out.printf("Error while creating/accessing new instance of SingleFileNoteDataService. %s is not accessible./n", _notesFile.getAbsolutePath());
            throw new RuntimeException(String.format("Cannot access file %s/n", _notesFile.getAbsolutePath()));
        }
    }

    @Override
    public String createNote() {
        return createNote("");
    }

    @Override
    public String createNote(String content) {
        //Create new note
        var note = new Note(content);
        //Store note in cache
        _notes.put(note.getId(), note);
        //Rewrite notes file
        _writeNotesToFile();
        //Return ID of new note
        return note.getId();
    }

    @Override
    public String createNote(Note note) {
        //Create new note
        var trueNote = new Note();
        trueNote.copyValues(note);
        //Store note in cache
        _notes.put(trueNote.getId(), trueNote);
        //Rewrite notes file
        _writeNotesToFile();
        //Return ID of new note
        return trueNote.getId();
    }

    @Override
    public List<Note> getAllNotes() {
        //There is no need to check the file. The cache SHOULD be accurate for reads
        return _notes.values().stream().toList();
    }

    @Override
    public Note getNoteById(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        //There is no need to check the file. The cache SHOULD be accurate for reads
        if (!_notes.containsKey(id)) {
            throw new NoSuchElementException(String.format("Note with id %s not found", id));
        }
        return _notes.get(id);
    }

    @Override
    public int getNoteCount() {
        //There is no need to check the file. The cache SHOULD be accurate for reads
        if (_notes == null) {
            return 0;
        }
        return _notes.size();
    }

    @Override
    public Note updateNote(Note upNote) {
        if (upNote == null) {
            throw new IllegalArgumentException("upNote cannot be null");
        }
        //Get the currently saved note
        var curNote = getNoteById(upNote.getId());
        //copy the values from the updated note into the current note
        curNote.copyValues(upNote);
        //Save the current note to cache
        _notes.put(upNote.getId(), curNote);
        //Rewrite notes file
        _writeNotesToFile();
        return curNote;
    }

    @Override
    public boolean deleteNoteById(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        //Remove the note from the cache
        _notes.remove(id);
        //Rewrite the notes file
        return _writeNotesToFile();
    }

    @Override
    public void deleteAllNotes() {
        //The easiest way to delete all the notes saved  to file is to replace the file data with nothing
        if (!FileHelper.replaceFileData(_notesFile, "")) {
            System.out.println("Error while deleting notes. They may still be in memory.");
        }
        //Clear the cache
        _notes = new HashMap<>();
    }

    /**
     * Load all {@link Note Notes} from the configured {@link SingleFileNoteDataService#_notesFile}
     */
    private void _readNotesFromFile() {
        var noteMap = new HashMap<String, Note>();
        var notes = getNotesFromFileData(FileHelper.getFileAsString(_notesFile));
        for (var note : notes) {
            noteMap.put(note.getId(), note);
        }
        _notes = noteMap;
    }

    /**
     * Convert String data from the {@link SingleFileNoteDataService#_notesFile} to a collection of {@link Note notes}
     * @param fileData A String describing a {@link List} of {@link Note notes}
     * @return a {@link List} of {@link Note notes}
     */
    protected abstract List<Note> getNotesFromFileData(String fileData);

    /**
     * Convert all {@link Note notes} to a String to be saved to a {@link File}
     * @param notes the {@link Note notes} to save
     * @return a String representing the {@link Note notes}
     */
    protected abstract String getFileDataFromNotes(List<Note> notes);

    /**
     * Write all notes to the {@link SingleFileNoteDataService#_notesFile}.
     * @return TRUE if the file was written successfully. FALSE if it was not
     */
    private boolean _writeNotesToFile() {
        var fileData = getFileDataFromNotes(_notes.values().stream().toList());
        if (!FileHelper.replaceFileData(_notesFile, fileData)) {
            System.out.println("Error while rewriting notes file. WARNING - The file may no longer match the cache.");
            return false;
        }
        return true;
    }
}
