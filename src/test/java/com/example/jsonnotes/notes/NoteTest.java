package com.example.jsonnotes.notes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Note tests")
public class NoteTest {

    @Order(1)
    @Nested
    @DisplayName("Create Note")
    public class NoteCreation {

        @DisplayName("empty and with a random ID")
        @Test
        void createEmptyNote() {
            assertDoesNotThrow(() -> {
                var note = new Note();
                assertNotNull(note.getId());
            });
        }

        @DisplayName("with some text and a random ID")
        @Test
        void createNoteWithData() {
            final var noteContent = "test";
            assertDoesNotThrow(() -> {
                var note = new Note(noteContent);
                assertNotNull(note.getId());
                assertEquals(noteContent, note.getContent());
            });
        }

        @DisplayName("as a copy of another Note")
        @Test
        void createNoteCopy() {
            final var noteContent = "test";
            assertDoesNotThrow(() -> {
                var note = new Note(noteContent);
                assertNotNull(note.getId());
                var copyNote = note.duplicate();
                assertEquals(note.getId(), copyNote.getId());
                assertEquals(note.getContent(), copyNote.getContent());
            });
        }
    }

    @Order(2)
    @DisplayName("Get note ID")
    @Test
    void getId() {
        System.out.println("Testing getId()");
        var testId = UUID.randomUUID().toString();
        var note = new Note(UUID.fromString(testId), "");
        System.out.printf("Checking for ID %s\n", testId);
        assertEquals(testId, note.getId());
    }

    @Order(3)
    @DisplayName("Get note content")
    @Test
    void getContent() {
        System.out.println("Testing getContent()");
        var testContent = "test";
        var note = new Note(testContent);
        System.out.printf("Checking for content %s\n", testContent);
        assertEquals(testContent, note.getContent());
    }

    @Order(4)
    @DisplayName("Set note content")
    @Test
    void setContent() {
        System.out.println("Testing setContent()");
        var testContent = "test";
        System.out.println("Creating empty Note");
        var note = new Note();
        System.out.printf("Setting content to %s\n", testContent);
        note.setContent(testContent);
        System.out.printf("Checking for content %s\n", testContent);
        assertEquals(testContent, note.getContent());
    }

    @DisplayName("Copy note values")
    @Test
    void copyValues() {
        System.out.println("Testing copyValues()");
        var testId = UUID.randomUUID().toString();
        var testContent = "test";
        System.out.println("Creating test Note");
        var origNote = new Note(UUID.fromString(testId), testContent);
        System.out.println("Creating empty Note");
        var copyNote = new Note();
        copyNote.copyValues(origNote);
        System.out.printf("Checking for content %s\n", testContent);
        assertEquals(testContent, copyNote.getContent());
        System.out.printf("Checking to make sure ID does not equal %s\n", testId);
        assertNotEquals(testId, copyNote.getId());
        System.out.println("Checking to make sure toString() matches");
        assertEquals(origNote.toString(), copyNote.toString());
    }

    @DisplayName("Duplicate note")
    @Test
    void duplicate() {
        System.out.println("Testing duplicate()");
        var testContent = "test";
        System.out.println("Creating test Note");
        var note = new Note(testContent);
        assertNotNull(note.getId());
        System.out.println("Creating copy Note");
        var copyNote = note.duplicate();
        System.out.println("Checking to make sure notes match");
        assertEquals(note.getId(), copyNote.getId());
        assertEquals(note.getContent(), copyNote.getContent());
    }
}