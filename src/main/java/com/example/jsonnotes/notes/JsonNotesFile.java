package com.example.jsonnotes.notes;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.*;

public class JsonNotesFile {

    private Map<String, Note> notes = new HashMap<>();

    private Map<String, String> noteJsonStrings = new HashMap<>();

    public JsonNotesFile() {}

    public List<Note> getNotes() {
        var gson = new Gson();
        var noteMap = new HashMap<String, Note>();
        noteJsonStrings.forEach((key, value) -> {
            try {
                noteMap.put(key, gson.fromJson(value, Note.class));
            } catch (JsonSyntaxException exception) {
                System.out.printf("Error while deserializing the note with ID of %s/n", key);
                noteMap.put(key, new Note(UUID.fromString(key), value));
            }
        });
        this.notes = noteMap;
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
