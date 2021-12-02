package com.example.jsonnotes.notes;

import java.util.ArrayList;
import java.util.List;

public class JsonNotesFile {

    private List<Note> notes = new ArrayList<>();

    public JsonNotesFile() {}

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
