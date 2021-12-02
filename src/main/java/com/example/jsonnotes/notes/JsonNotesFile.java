package com.example.jsonnotes.notes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonNotesFile {

    private Map<String, Note> notes = new HashMap<>();

    public JsonNotesFile() {}

    public List<Note> getNotes() {
        return notes.values().stream().toList();
    }

    public void setNotes(List<Note> notes) {
        var noteMap = new HashMap<String, Note>();
        for (var note : notes) {
            noteMap.put(note.getId(), note);
        }
        this.notes = noteMap;
    }
}
