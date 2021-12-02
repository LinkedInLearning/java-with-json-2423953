package com.example.jsonnotes.notes;

import com.example.jsonnotes.util.NoteHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

@DisplayName("Reflection tests")
public class NoteReflectionTest {

    @DisplayName("Read all Note fields using reflection")
    @Test
    void readNoteFields() {
        //Generate test Note
        var testNote = new Note("test");
        System.out.println("Inspecting Note class using reflection");
        //Look at all Note class fields
        Field[] fields = Note.class.getDeclaredFields();
        for (Field field : fields) {
            //Make it accessible so we can grab the value
            field.setAccessible(true);
            //Print out the field info
            System.out.printf("Note has field '%s' of type '%s' with a value of '%s'%n",
                    field.getName(),
                    field.getGenericType().getTypeName(),
                    NoteHelper.getNoteFieldValue(field, testNote));
        }
    }
}
