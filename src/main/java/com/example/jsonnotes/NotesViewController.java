package com.example.jsonnotes;

import com.example.jsonnotes.notes.Note;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class NotesViewController {
    @FXML
    TextField txtNoteDirectory;
    @FXML
    TableView<Note> tableNotes;
    @FXML
    Button btnCreateNote;
    @FXML
    Button btnDeleteNote;
    @FXML
    Button btnSelectDirectory;
    @FXML
    Button btnRefresh;
    @FXML
    Button btnEditNote;


}