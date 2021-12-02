package com.example.jsonnotes.notes;

import com.example.jsonnotes.notes.service.CacheNoteDataService;
import com.example.jsonnotes.notes.service.INoteDataService;
import com.example.jsonnotes.notes.service.TextNoteDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("INoteDataService tests")
class NoteDataServiceTest {

    @Order(1)
    @DisplayName("Create new data service")
    @ParameterizedTest(name = "{index} ==> A new instance of {0} is created")
    @MethodSource("dataServiceProvider")
    void create(INoteDataService dataService) {
        assertNotNull(dataService);
    }

    @DisplayName("Create empty Note - INoteDataService.createNote()")
    @ParameterizedTest(name = "{index} ==> {0} can create an empty note")
    @MethodSource("dataServiceProvider")
    void createEmptyNote(INoteDataService dataService) {
        System.out.println("Creating note");
        var noteId = dataService.createNote();
        assertNotNull(noteId);
        System.out.println("Checking to make sure there is 1 note in the data service.");
        var numNotes = dataService.getNoteCount();
        assertEquals(1, numNotes);
        System.out.printf("Found %d note(s)", numNotes);
        //Cleanup
        dataService.deleteAllNotes();
    }

    @DisplayName("Create Note with data - INoteDataService.createNote(String)")
    @ParameterizedTest(name = "{index} ==> {0} can create a note with data")
    @MethodSource("dataServiceProvider")
    void createNoteWithData(INoteDataService dataService) {
        System.out.println("Creating note");
        var testContent = "test";
        var noteId = dataService.createNote(testContent);
        assertNotNull(noteId);
        System.out.println("Checking to make sure there is 1 note in the data service.");
        var numNotes = dataService.getNoteCount();
        assertEquals(1, numNotes);
        System.out.printf("Found %d note(s)", numNotes);
        System.out.println("Checking to make sure the content of the new note is accurate.");
        var note = dataService.getNoteById(noteId);
        assertEquals(testContent, note.getContent());
        //Cleanup
        dataService.deleteAllNotes();
    }

    @DisplayName("Get all Notes - INoteDataService.getAllNotes()")
    @ParameterizedTest(name = "{index} ==> {0} returns all notes")
    @MethodSource("dataServiceProvider")
    void getAllNotes(INoteDataService dataService) {
        //Create notes
        var numNotes = 4;
        System.out.printf("Creating %d note(s)%n", numNotes);
        for (int i = 0; i < numNotes; i++) {
            dataService.createNote();
        }
        System.out.printf("Checking to make sure the data service returns %d note(s)%n", numNotes);
        //Get notes
        var notes = dataService.getAllNotes();
        //Compare data
        assertEquals(numNotes, notes.size());
        //Cleanup
        dataService.deleteAllNotes();
    }

    @DisplayName("Get Note by ID - INoteDataService.getNoteById(String)")
    @ParameterizedTest(name = "{index} ==> {0} returns a note with the requested ID")
    @MethodSource("dataServiceProvider")
    void getNoteById(INoteDataService dataService) {
        System.out.println("Creating note");
        var testContent = "test";
        var noteId = dataService.createNote(testContent);
        assertNotNull(noteId);
        System.out.println("Checking to make sure the content of the note is accurate.");
        var note = dataService.getNoteById(noteId);
        assertEquals(noteId, note.getId());
        assertEquals(testContent, note.getContent());
        //Cleanup
        dataService.deleteAllNotes();
    }

    @DisplayName("Get number of Notes - INoteDataService.getNoteCount()")
    @ParameterizedTest(name = "{index} ==> {0} returns the number of notes it contains")
    @MethodSource("dataServiceProvider")
    void getNoteCount(INoteDataService dataService) {
        //Create notes
        var numNotes = 4;
        System.out.printf("Creating %d note(s)%n", numNotes);
        for (int i = 0; i < numNotes; i++) {
            dataService.createNote();
        }
        System.out.printf("Checking to make sure the data service returns %d note(s)%n", numNotes);
        //Compare data
        assertEquals(numNotes, dataService.getNoteCount());
        //Cleanup
        dataService.deleteAllNotes();
    }

    @DisplayName("Update a Note - INoteDataService.updateNote(Note)")
    @ParameterizedTest(name = "{index} ==> {0} updates a note's data")
    @MethodSource("dataServiceProvider")
    void updateNote(INoteDataService dataService) {
        System.out.println("Creating note");
        var testContent = "test";
        var noteId = dataService.createNote(testContent);
        assertNotNull(noteId);
        var replaceContent = "replace";
        System.out.println("Updating note");
        var upNote = new Note(UUID.fromString(noteId), replaceContent);
        var retNote = dataService.updateNote(upNote);
        assertNotNull(retNote);
        assertEquals(noteId, retNote.getId());
        assertEquals(replaceContent, retNote.getContent());
        //Cleanup
        dataService.deleteAllNotes();
    }

    @DisplayName("Delete a Note - INoteDataService.deleteNoteById(String)")
    @ParameterizedTest(name = "{index} ==> {0} deletes a specific note")
    @MethodSource("dataServiceProvider")
    void deleteNoteById(INoteDataService dataService) {
        System.out.println("Creating note");
        var testContent = "test";
        var noteId = dataService.createNote(testContent);
        assertNotNull(noteId);
        System.out.println("Deleting note");
        assertTrue(dataService.deleteNoteById(noteId));
        System.out.println("Checking to make sure the note is no longer there");
        assertThrows(NoSuchElementException.class, () -> dataService.getNoteById(noteId));
        //Cleanup
        dataService.deleteAllNotes();
    }

    @DisplayName("Delete all Note - INoteDataService.deleteAllNotes()")
    @ParameterizedTest(name = "{index} ==> {0} deletes all notes")
    @MethodSource("dataServiceProvider")
    void deleteAllNotes(INoteDataService dataService) {
        //Create notes
        var numNotes = 4;
        System.out.printf("Creating %d note(s)%n", numNotes);
        for (int i = 0; i < numNotes; i++) {
            dataService.createNote();
        }
        System.out.printf("Checking to make sure the data service has %d note(s)%n", numNotes);
        assertEquals(numNotes, dataService.getNoteCount());
        System.out.println("Deleting notes");
        assertDoesNotThrow(dataService::deleteAllNotes);
        System.out.println("Checking to make sure the data service has 0 notes");
        assertEquals(0, dataService.getNoteCount());
    }

    /**
     * Test data provider. Supplies a list of {@link INoteDataService} classes for testing
     * @return A stream of {@link INoteDataService} arguments
     */
    static Stream<Arguments> dataServiceProvider() {
        return Stream.of(
                Arguments.arguments(new CacheNoteDataService()),
                Arguments.arguments(new TextNoteDataService(_getTestDataPath()))
        );
    }

    /**
     * Get the path to the data used for testing
     * @return A string representing a fully qualified path to test data
     */
    private static String _getTestDataPath() {
        //All test data is stored in a directory called "test-data"
        var currentRelativePath = Paths.get("").toAbsolutePath().resolve("test-data").normalize();
        return currentRelativePath.toString();
    }
}