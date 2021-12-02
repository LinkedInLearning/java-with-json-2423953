package com.example.jsonnotes.notes;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonNotesFile {

    private Map<String, Note> notes = new HashMap<>();

    private Map<String, String> noteJsonStrings = new HashMap<>();

    public JsonNotesFile() {}

    public List<Note> getNotes() {
        return notes.values().stream().toList();
    }

    public void setNotes(List<Note> notes) {
        var gson = new Gson();
        var noteJsonMap = new HashMap<String, String>();
        var noteMap = new HashMap<String, Note>();
        for (var note : notes) {
            noteMap.put(note.getId(), note);
            noteJsonMap.put(note.getId(), gson.toJson(note));
        }
        this.noteJsonStrings = noteJsonMap;
        this.notes = noteMap;
    }
}
