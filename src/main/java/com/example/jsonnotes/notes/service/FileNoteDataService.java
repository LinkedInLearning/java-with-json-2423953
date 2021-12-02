package com.example.jsonnotes.notes.service;

import com.example.jsonnotes.notes.Note;
import com.example.jsonnotes.util.FileHelper;

import java.io.File;
import java.util.*;

/**
 * A data service for {@link Note Notes} that stores all data in individual text files - one file per {@link Note}.
 */
public abstract class FileNoteDataService implements INoteDataService {

    /**
     * The file extension used by files that contain {@link Note notes}
     */
    protected final static String _FILE_EXTENSION = ".txt";
    /**
     * The absolute path to the directory containing {@link Note notes}
     */
    protected final String _dataPath;
    /**
     * A collection of IDs for the {@link Note notes} managed by this service
     */
    protected Set<String> _noteIds = new HashSet<>();

    /**
     * Create a new instance of a {@link FileNoteDataService} pointing at a specific path
     */
    FileNoteDataService(String dataPath) {
        _dataPath = dataPath;
        _loadNoteIds();
    }

    @Override
    public String createNote() {
        return createNote("");
    }

    @Override
    public String createNote(String content) {
        //Create new note
        var note = new Note(content);
        //Store note ID in cache
        _noteIds.add(note.getId());
        //Write note to file
        if (_writeNoteToFile(note)) {
            //Success - return the note ID
            return note.getId();
        } else {
            //Failed - return null
            return null;
        }
    }

    @Override
    public String createNote(Note note) {
        //Create new note
        var trueNote = new Note();
        trueNote.copyValues(note);
        //Store note in cache
        _noteIds.add(trueNote.getId());
        //Return note ID
        if (_writeNoteToFile(trueNote)) {
            //Success - return the note ID
            return trueNote.getId();
        } else {
            //Failed - return null
            return null;
        }
    }

    @Override
    public List<Note> getAllNotes() {
        var notes = new ArrayList<Note>();
        //Scan the list of known notes and get all the data from the corresponding files
        for (String noteId : _noteIds) {
            var note = _readNoteFromFile(noteId);
            if (note != null) {
                notes.add(note);
            }
        }
        return notes;
    }

    @Override
    public Note getNoteById(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        if (!_noteIds.contains(id)) {
            throw new NoSuchElementException(String.format("Note with id %s not found", id));
        }
        return _readNoteFromFile(id);
    }

    @Override
    public int getNoteCount() {
        if (_noteIds == null) {
            return 0;
        }
        return _noteIds.size();
    }

    @Override
    public Note updateNote(Note upNote) {
        if (upNote == null) {
            throw new IllegalArgumentException("upNote cannot be null");
        }
        var id = upNote.getId();
        var curNote = getNoteById(id);
        curNote.copyValues(upNote);
        if (!_writeNoteToFile(curNote)) {
            System.out.printf("Failed to update note %s", id);
            return null;
        }
        return curNote;
    }

    @Override
    public boolean deleteNoteById(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        var noteFile = _getNoteFile(id);
        if (noteFile.delete()) {
            //The file was successfully deleted
            _noteIds.remove(id);
            return true;
        }
        //The file was not deleted
        return false;
    }

    @Override
    public void deleteAllNotes() {
        var upNoteIdList = new HashSet<>(_noteIds);
        for (var id : upNoteIdList) {
            if (!deleteNoteById(id)) {
                System.out.printf("Error while deleting note %s. It may still be in memory.", id);
            }
        }
    }

    /**
     * Get a {@link Note} from a String of file data
     * @param fileData A string representing the data for a {@link Note}
     * @return A {@link Note}
     */
    protected abstract Note getNoteFromFileData(String fileData);

    /**
     * Get a String of data to write to file for a {@link Note}
     * @param note A {@link Note} to convert to a String
     * @return A String representing the {@link Note} passed in
     */
    protected abstract String getFileDataFromNote(Note note);

    /**
     * Load all {@link Note} IDs from the configured data directory. This scans the directory for text files and
     *  uses the file names as the IDs.
     */
    private void _loadNoteIds() {
        var noteIds = new HashSet<String>();
        var dataDir = new File(_dataPath);
        if (dataDir.isDirectory()) {
            var files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.getAbsolutePath().contains(_FILE_EXTENSION)) {
                        continue;
                    }
                    noteIds.add(_getIdFromFileName(file));
                }
            }
        }
        _noteIds = noteIds;
    }

    /**
     * Get a {@link File} for a {@link Note} with a specific ID
     * @param id The ID of the {@link Note} to get
     * @return A {@link File}
     */
    private File _getNoteFile(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        var fileName = String.format("%s%s", id, _FILE_EXTENSION);
        var filePath = String.format("%s/%s", _dataPath, fileName);
        return new File(filePath);
    }

    /**
     * Read a {@link Note} from a {@link File}
     * @param id The ID of the {@link Note} to get
     * @return A {@link Note}. NULL if there was a problem reading the data from file
     */
    private Note _readNoteFromFile(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        var noteFile = _getNoteFile(id);
        var fileData = FileHelper.getFileAsString(noteFile);
        if (fileData == null) {
            //Received null from file content - there was an error while reading the file
            return null;
        }
        var noteFromFile = getNoteFromFileData(fileData);
        if (noteFromFile == null) {
            //Received a null note - there was an error processing the file data
            return null;
        }
        //Construct the note
        var returnNote = new Note(UUID.fromString(id), "");
        returnNote.copyValues(noteFromFile);
        return returnNote;
    }

    /**
     * Write a {@link Note} to a {@link File}
     * @param note The {@link Note} to write
     * @return TRUE if the file was written, FALSE if it wasn't
     */
    private boolean _writeNoteToFile(Note note) {
        var fileData = getFileDataFromNote(note);
        return FileHelper.replaceFileData(_getNoteFile(note.getId()), fileData);
    }

    /**
     * Get the ID of a {@link Note} from a {@link File} path
     * @param noteFile The {@link File} to get the ID from
     * @return The ID of the {@link Note}
     */
    private static String _getIdFromFileName(File noteFile) {
        if (noteFile == null) {
            throw new IllegalArgumentException("noteFile cannot be null");
        }
        return noteFile.getName().replace(_FILE_EXTENSION, "");
    }
}
