package com.example.jsonnotes.util;

import com.example.jsonnotes.notes.Note;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Utility class used to make interacting with {@link Note Notes} easier.
 */
public class NoteHelper {

    /**
     * Get the name of all fields declared in the {@link Note} class using reflection
     * @return a List of Strings representing the names of all the fields in the {@link Note} class
     */
    public static List<String> getNoteFieldNames() {
        List<String> noteFields = new ArrayList<>();
        //Get all the declared fields from the Note class
        var fields = Note.class.getDeclaredFields();
        for (Field field : fields) {
            //Add each field name to the list
            noteFields.add(field.getName());
        }
        return noteFields;
    }

    /**
     * Get the value of a specific field declared in the {@link Note} class from an instance of a {@link Note} using reflection
     * @param field The field to get the value of
     * @param note An instance of a {@link Note}
     * @return The value of the Note's field
     */
    public static Object getNoteFieldValue(Field field, Note note) {
        //Get the Note's class
        Class<? extends Note> noteClass = note.getClass();
        //Look through each declared method for a "get" that corresponds to the field
        for (Method method : noteClass.getMethods()) {
            var methodName = method.getName();
            //Check if the method starts with the word "get"
            if (!methodName.startsWith("get")) {
                continue;
            }
            //Check if the remainder (with the leading "get") of the method name matches the field name
            methodName = methodName.substring(3);
            if (!methodName.toLowerCase().equals(field.getName())) {
                continue;
            }
            try {
                //Invoke the "get" method for the value
                return method.invoke(note);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
                System.out.printf("Error while retrieving the value the of a Note field - %s%n", field.getName());
            }
        }
        //We could not retrieve the value of the field - return null
        return null;
    }

    /**
     * Get the name of a field formatted for display. This will capitalize the first letter of every word and separates
     *  each word with a space. This method assumes that the input name is formatted using camel-case.
     * @param name The name to format. Name must be in camel-case format IE "thisIsAVariable" = "This Is A Variable"
     * @return The name formatted for display. IE "thisIsAVariable" = "This Is A Variable"
     */
    public static String getPrettyName(String name) {
        //Assuming name is a camel-case variable name we can convert the first character to upper-case
        // and add a space before any subsequent upper-case characters
        var prettyNameBuilder = new StringBuilder(name.substring(0,1).toUpperCase());
        for (int i = 1; i < name.length(); i++) {
            char targetChar = name.charAt(i);
            //If the character is a capital letter then we can add a space before the letter
            if (Character.isUpperCase(targetChar)) {
                prettyNameBuilder.append(' ');
            }
            prettyNameBuilder.append(targetChar);
        }
        return prettyNameBuilder.toString();
    }

    /**
     * Get an ID\u2043CONTENT string for a given {@link Note}. \u2043 is the unicode character for "hyphen bullet"
     * @param note The {@link Note} to convert to a String
     * @return A String containing the {@link Note} ID and Content in the format of "ID\u2043CONTENT"
     * @throws IllegalArgumentException Thrown when a null note is specified
     */
    public static String getNoteIdContentPair(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("note cannot be null");
        }
        return String.format("%s\\u2043%s", note.getId(), note.getContent());
    }

    /**
     * Get a {@link Note} instance from an ID\u2043CONTENT string generated from {@link NoteHelper#getNoteIdContentPair(Note)}.
     * @param idContentPair A string representing the ID and Content of a {@link Note} separated by \u2043.
     *                      \u2043 is the unicode character for "hyphen bullet"
     * @return A {@link Note}
     * @throws IllegalArgumentException Thrown when idContentPair is incorrectly formatted or null
     */
    public static Note getNoteFromIdContentPair(String idContentPair) {
        if (Objects.equals(idContentPair, "")) {
            throw new IllegalArgumentException("idContentPair cannot be blank");
        }
        var noteProps = idContentPair.split("\\u2043");
        if (noteProps.length == 1) {
            throw new IllegalArgumentException("idContentPair does not contain the appropriate splitter character \\u2043 and may be an invalid ID\\u2043CONTENT string");
        }
        return new Note(UUID.fromString(noteProps[0]), noteProps[1]);
    }

    /**
     * Gets a String representing a {@link List} of {@link Note Notes} encoded as ID\u2043CONTENT pairs separated by the \u221E (Infinity) character.
     * @param notes A {@link List} of {@link Note Notes}
     * @return A String of ID\u2043CONTENT pairs separated by the \u221E (Infinity) character
     * @see NoteHelper#getNoteIdContentPair(Note)
     * @see NoteHelper#getNotesFromDataString(String)
     */
    public static String getNotesDataStringFromList(List<Note> notes) {
        //Run getNoteIdContentPair on each Note in the list and store the results in a new list
        List<String> noteStrings = notes.stream().map(NoteHelper::getNoteIdContentPair).toList();
        //Join the items in the list with the \\u221E character
        return String.join("\\u221E", noteStrings);
    }

    /**
     * Gets a {@link List} of {@link Note Notes} from a String of ID\u2043CONTENT pairs separated by the \u221E (Infinity) character
     * @param notesDataString A String of ID\u2043CONTENT pairs separated by the \u221E (Infinity) character
     * @return A {@link List} of {@link Note Notes}
     * @see NoteHelper#getNoteFromIdContentPair(String)
     * @see NoteHelper#getNotesDataStringFromList(List)
     */
    public static List<Note> getNotesFromDataString(String notesDataString) {
        var notes = new ArrayList<Note>();
        //Split the string at the \\u221E characters to divide the Notes from one another
        var splitNoteStrings = notesDataString.split("\\u221E");
        for (String splitNoteString : splitNoteStrings) {
            //Split each Note ID-CONTENT pair and generate a new Note from it then store that in the notes list
            notes.add(getNoteFromIdContentPair(splitNoteString));
        }
        return notes;
    }
}
