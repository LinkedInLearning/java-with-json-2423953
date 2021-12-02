package com.example.jsonnotes.notes;

import java.util.UUID;

/**
 * A short set of data used to remind someone of something - like a "sticky note"
 */
public class Note {
    /**
     * The unique ID of the {@link Note}
     */
    private final String id;
    /**
     * The content of the {@link Note}
     */
    private String content = "";

    /**
     * Create a new instance of a {@link Note} with a random ID and no content
     */
    public Note() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Create a new instance of a {@link Note} with a random ID and some content
     * @param content {@link Note#content Content} of the new {@link Note}
     */
    public Note(String content) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
    }

    /**
     * Create a new instance of a {@link Note} with a specific {@link Note#id} and {@link Note#content}. New {@link Note Notes} should not be
     *  created like this. This is used generate an instance of pre-existing {@link Note} data
     * @param id The {@link Note#id} of the {@link Note}
     * @param content {@link Note#content Content} of the {@link Note}
     */
    public Note(UUID id, String content) {
        this.id = id.toString();
        this.content = content;
    }

    /**
     * Get the {@link Note#id} of the {@link Note}
     * @return The {@link Note#id} as a String
     */
    public String getId() { return this.id; }

    /**
     * Get the {@link Note#content} of the {@link Note}
     * @return The {@link Note#content} as a String
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the {@link Note#content} of the {@link Note}
     * @param content The new {@link Note#content}
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Copy all the data from another {@link Note} into this {@link Note}
     * @param note The {@link Note} to copy
     */
    public void copyValues(Note note) {
        this.content = note.content;
    }

    /**
     * Create a copy of this {@link Note}
     * @return a {@link Note} that is an exact copy of this one
     */
    public Note duplicate() {
        var note = new Note(UUID.fromString(this.id), this.content);
        note.copyValues(this);
        return note;
    }

    @Override
    public String toString() {
        return this.getContent();
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
