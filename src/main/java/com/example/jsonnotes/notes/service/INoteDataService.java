package com.example.jsonnotes.notes.service;

import com.example.jsonnotes.notes.Note;

import java.util.List;

/**
 * An interface for managing {@link Note Notes}
 */
public interface INoteDataService {

    /**
     * Create a new {@link Note}
     *
     * @return The ID of the new {@link Note} or NULL if it could not be created.
     */
    String createNote();

    /**
     * Create a new {@link Note}
     * @param content The content of the new {@link Note}
     * @return The ID of the new {@link Note}
     */
    String createNote(String content);

    /**
     * Create a new {@link Note}. The ID of the supplied {@link Note} is not used and will be ignored.
     * @param note The {@link Note} data to save. The {@link Note} ID is ignored.
     * @return The ID of the new {@link Note} according to the data service. This ID should be considered the correct one.
     */
    String createNote(Note note);

    /**
     * Get a {@link List} of all {@link Note Notes}
     * @return A {@link List} of {@link Note Notes}
     */
    List<Note> getAllNotes();

    /**
     * Get the number of {@link Note Notes} stored in the {@link INoteDataService}
     * @return The number of {@link Note Notes} as an integer
     */
    int getNoteCount();

    /**
     * Get the {@link Note} with the specific ID
     * @param id The ID of the {@link Note} to get
     * @return A {@link Note}
     */
    Note getNoteById(String id);

    /**
     * Update a {@link Note}
     * @param note The {@link Note} to update
     * @return The updated {@link Note}
     */
    Note updateNote(Note note);

    /**
     * Delete the {@link Note} with the specific ID
     * @param id The ID of the {@link Note} to delete
     * @return TRUE if the note was deleted, FALSE if it wasn't
     */
    boolean deleteNoteById(String id);

    /**
     * Delete all {@link Note Notes}
     */
    void deleteAllNotes();
}
