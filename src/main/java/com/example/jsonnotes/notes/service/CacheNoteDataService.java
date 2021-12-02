package com.example.jsonnotes.notes.service;

import com.example.jsonnotes.notes.Note;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A data service for {@link Note Notes} that stores all data in a collection in memory.
 */
public class CacheNoteDataService implements INoteDataService {

    /**
     * Single instance of {@link CacheNoteDataService}
     */
    private static CacheNoteDataService _instance = new CacheNoteDataService();

    /**
     * Get the single instance of {@link CacheNoteDataService}
     * @return {@link CacheNoteDataService}
     */
    public static CacheNoteDataService getInstance() {
        if (_instance == null) {
            _instance = new CacheNoteDataService();
        }
        return _instance;
    }

    /**
     * Collection of {@link Note Notes}
     */
    private Map<String, Note> _cache;

    /**
     * Create a new instance of a {@link CacheNoteDataService}
     */
    public CacheNoteDataService() {
        _cache = new HashMap<>();
    }

    @Override
    public String createNote() {
        //Create new note
        var note = new Note();
        //Store note in cache
        _cache.put(note.getId(), note);
        //Return note ID
        return note.getId();
    }

    @Override
    public String createNote(String content) {
        //Create new note
        var note = new Note(content);
        //Store note in cache
        _cache.put(note.getId(), note);
        //Return note ID
        return note.getId();
    }

    @Override
    public String createNote(Note note) {
        //Create new note
        var trueNote = new Note();
        trueNote.copyValues(note);
        //Store note in cache
        _cache.put(trueNote.getId(), trueNote);
        //Return note ID
        return trueNote.getId();
    }

    @Override
    public List<Note> getAllNotes() {
        return _cache.values().stream().toList();
    }

    @Override
    public Note getNoteById(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        if (!_cache.containsKey(id)) {
            throw new NoSuchElementException(String.format("Note with id %s not found", id));
        }
        return _cache.get(id);
    }

    @Override
    public int getNoteCount() {
        if (_cache == null) {
            return 0;
        }
        return _cache.size();
    }

    @Override
    public Note updateNote(Note upNote) {
        if (upNote == null) {
            throw new IllegalArgumentException("upNote cannot be null");
        }
        var id = upNote.getId();
        var curNote = getNoteById(id);
        curNote.copyValues(upNote);
        //Save the updated note to the cache
        _cache.put(id, curNote);
        return curNote;
    }

    @Override
    public boolean deleteNoteById(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("id cannot be blank");
        }
        _cache.remove(id);
        return true;
    }

    @Override
    public void deleteAllNotes() {
        _cache = new HashMap<>();
    }
}
