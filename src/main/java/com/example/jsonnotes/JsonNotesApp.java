package com.example.jsonnotes;

import com.example.jsonnotes.notes.service.*;
import com.example.jsonnotes.notes.Note;
import com.example.jsonnotes.util.NoteHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.awt.Desktop;

/**
 * Main app object for the JsonNotes application. This app enables users to manage simple {@link Note} objects stored
 *  in a directory on a computer. Users can create, read, update, and delete {@link Note Notes}.
 * @author Jon-Luke West
 */
public class JsonNotesApp extends Application {

    /**
     * The main UI stage for the app
     */
    private Stage mainStage;
    /**
     * The JavaFX UI controller for the stage
     */
    private NotesViewController controller;
    /**
     * The current loaded path for {@link Note notes}. Defaults to the current runtime directory
     */
    private File targetDirectory = new File(Paths.get("").toAbsolutePath().toString());
    /**
     * A data service used to interact with {@link Note Notes}
     */
    private INoteDataService noteDataService;

    /**
     * Main application entry-point
     */
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        //Load the JavaFX XML
        var fxmlLoader = new FXMLLoader(JsonNotesApp.class.getResource("notes-view.fxml"));
        var viewWithMenu = new VBox(getMenuBar(), fxmlLoader.load());
        var scene = new Scene(viewWithMenu, 640, 480);
        stage.setTitle("JSON Notes");
        stage.setScene(scene);
        controller = fxmlLoader.getController();
        mainStage = stage;
        setAppIcon();
        configureEventHandlers();
        configureTable();
        //Load initial data values
        updateDirectoryText();
        setNoteDirectory();
        //Show view
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        //Remove all event handlers when the app UI stops
        controller.btnSelectDirectory.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnSelect_Clicked);
        controller.btnRefresh.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnRefresh_Clicked);
        controller.btnCreateNote.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnCreateNote_Clicked);
        controller.btnEditNote.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnEditNote_Clicked);
        controller.btnDeleteNote.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnDeleteNote_Clicked);
    }

    /**
     * Configure all event handlers for the UI
     */
    private void configureEventHandlers() {
        controller.btnSelectDirectory.addEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnSelect_Clicked);
        controller.btnRefresh.addEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnRefresh_Clicked);
        controller.btnCreateNote.addEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnCreateNote_Clicked);
        controller.btnEditNote.addEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnEditNote_Clicked);
        controller.btnDeleteNote.addEventHandler(MouseEvent.MOUSE_CLICKED, handler_btnDeleteNote_Clicked);
    }

    /**
     * Configure the data table in the UI. Sets the columns in the table to represent the fields in the {@link Note} class
     */
    private void configureTable() {
        //Construct the table based on the Note class fields
        ObservableList<TableColumn<Note, ?>> tableColumns = controller.tableNotes.getColumns();
        //Build table columns - 1 column per Note field
        ArrayList<TableColumn<Note, ?>> columns = new ArrayList<>();
        TableColumn<Note, ?> column;
        var noteFields = NoteHelper.getNoteFieldNames();
        for (String fieldName : noteFields) {
            column = new TableColumn<>(NoteHelper.getPrettyName(fieldName));
            column.setCellValueFactory(new PropertyValueFactory<>(fieldName));
            columns.add(column);
        }
        //Add the columns to the table
        tableColumns.addAll(columns);
        //Configure double-click on a row so it opens the edit dialog
        controller.tableNotes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        controller.tableNotes.setRowFactory(tv -> {
            TableRow<Note> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Note selectedNote = row.getItem();
                    showEditNoteDialog(selectedNote);
                }
            });
            return row;
        });
    }

    /**
     * Get the {@link MenuBar} used by the app
     * @return A {@link MenuBar}
     */
    private MenuBar getMenuBar() {
        //Create File menu
        var fileMenu = new Menu("File");
        //New note item
        var newNoteItem = new MenuItem("New Note...");
        newNoteItem.setOnAction(event -> showCreateNoteDialog());
        //Set Note Directory item
        var setNoteDirItem = new MenuItem("Select Note Directory...");
        setNoteDirItem.setOnAction(event -> showSelectDirectoryDialog());
        //Open Note Directory item
        var openNoteDirItem = new MenuItem("Open Note Directory");
        openNoteDirItem.setOnAction(event -> {
            try {
                Desktop.getDesktop().open(targetDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error opening the platform specific file explorer app to view the target Note Directory.");
            }
        });
        //Exit item
        var exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> Platform.exit());
        fileMenu.getItems().addAll(newNoteItem, new SeparatorMenuItem(), setNoteDirItem, openNoteDirItem, new SeparatorMenuItem(), exitItem);
        //Create Edit menu
        var editMenu = new Menu("Edit");
        //Edit note item
        var editNoteItem = new MenuItem("Edit Selected Note...");
        editNoteItem.setOnAction(event -> tryEditSelectedNote());
        //Delete note item
        var deleteNoteItem = new MenuItem("Delete Selected Note");
        deleteNoteItem.setOnAction(event -> tryDeleteSelectedNote());
        //Delete all notes item
        var deleteAllItem = new MenuItem("Delete All Notes");
        deleteAllItem.setOnAction(event -> {
            noteDataService.deleteAllNotes();
            refreshTableData();
        });
        editMenu.getItems().addAll(editNoteItem, deleteNoteItem, new SeparatorMenuItem(), deleteAllItem);
        //Create menu bar
        var mb = new MenuBar();
        mb.getMenus().addAll(fileMenu, editMenu);
        return mb;
    }

    /**
     * Updates the displayed directory text so it shows the current target directory
     */
    private void updateDirectoryText() {
        controller.txtNoteDirectory.setText(targetDirectory.toString());
    }

    /**
     * Set the configured directory to the selected target directory and then refreshes the data in the table
     */
    private void setNoteDirectory() {
        try {
            noteDataService = JsonFileNotesDataService.getInstance(targetDirectory.getPath());
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("There was an error while trying to create a note data service. Reverting to cache only.");
            System.out.println("WARNING - nothing is being saved. When the application is closed, all data will be lost.");
            noteDataService = CacheNoteDataService.getInstance();
        }
        refreshTableData();
    }

    /**
     * Refresh the data displayed in the table
     */
    private void refreshTableData() {
        var notes = noteDataService.getAllNotes();
        controller.tableNotes.getItems().clear();
        controller.tableNotes.getItems().addAll(notes);
    }

    /**
     * Get the ID of the {@link Note} selected in the table
     * @return The ID of the selected {@link Note} or null if no {@link Note} is selected
     */
    private String getSelectedNoteId() {
        var selectedNote = controller.tableNotes.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            //No selected note
            return null;
        }
        return selectedNote.getId();
    }

    private void showCreateNoteDialog() {
        var dialog = createNoteDialog("Create Note",
                "Create a new note",
                "Create Note",
                null);
        //Display the dialog and wait for a result
        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(content -> {
            //The create button was pressed
            //Create a new note using the supplied content
            var noteToCreate = new Note(content[0], content[1]);
            noteDataService.createNote(noteToCreate);
            refreshTableData();
        });
    }

    /**
     * Show the "edit note" dialog so the user can edit a specific {@link Note}
     * @param note The {@link Note} to edit
     */
    private void showEditNoteDialog(Note note) {
        var dialog = createNoteDialog("Edit Note",
                String.format("Edit note %s", note.getId()),
                "Save Note",
                note);
        //Display the dialog and wait for a result
        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(content -> {
            //Update the note
            note.setContent(content[0]);
            note.setSummary(content[1]);
            noteDataService.updateNote(note);
            refreshTableData();
        });
    }

    /**
     * Create a {@link Dialog} for creating/editing {@link Note notes}
     * @param title The title of the {@link Dialog} window
     * @param headerText The text displayed at the top of the {@link Dialog} window
     * @param positiveButtonText The text displayed on the positive button
     * @param note The {@link Note} the {@link Dialog} is for
     * @return An array of Strings containing the captured {@link Note} data.
     */
    private Dialog<String[]> createNoteDialog(String title, String headerText, String positiveButtonText, Note note) {
        //Create a new dialog
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        //Define positive button
        var positiveButton = new ButtonType(positiveButtonText, ButtonBar.ButtonData.OK_DONE);
        //Add positive button and "Cancel" buttons to dialog
        dialog.getDialogPane().getButtonTypes().addAll(positiveButton, ButtonType.CANCEL);
        //Configure a grid to display text fields
        var grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(8);
        //Add a field for Note content
        var noteSummary = new TextField();
        noteSummary.setPromptText("Briefly summarize the note");
        grid.add(new Label("Summary:"), 0, 0);
        grid.add(noteSummary, 1, 0);
        var noteContent = new TextArea();
        noteContent.setPromptText("Write something in the note...");
        grid.add(new Label("Content:"), 0, 1);
        grid.add(noteContent, 1, 1);
        //Fill values if there is a note available
        if (note != null) {
            noteContent.setText(note.getContent());
            noteSummary.setText(note.getSummary());
        }
        //Set the dialog view content
        dialog.getDialogPane().setContent(grid);
        //Configure the dialog to set focus to the content field when the dialog is displayed
        Platform.runLater(noteSummary::requestFocus);
        //Configure the result converter
        dialog.setResultConverter(button -> {
            if (button == positiveButton) {
                return new String[] {
                        noteContent.getText(),
                        noteSummary.getText()
                };
            }
            return null;
        });

        return dialog;
    }

    /**
     * Displays a directory chooser to select a directory
     */
    private void showSelectDirectoryDialog() {
        if (mainStage == null || controller == null) {
            //Cannot display the directory chooser without a handle to the main stage
            return;
        }
        //Open directory chooser
        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Note Directory");
        dirChooser.setInitialDirectory(targetDirectory);
        File selectedDir = dirChooser.showDialog(mainStage);
        if (selectedDir != null) {
            //A directory was chosen - refresh everything for the selected directory
            targetDirectory = selectedDir;
            updateDirectoryText();
            setNoteDirectory();
        }
    }

    /**
     * Try to show the edit note dialog for the currently selected {@link Note note}.
     * @return TRUE if a {@link Note note} was selected and the dialog was displayed. FALSE otherwise
     */
    private boolean tryEditSelectedNote() {
        var selectedNoteId = getSelectedNoteId();
        if (selectedNoteId == null) {
            //No selected note
            return false;
        }
        var selectedNote = noteDataService.getNoteById(selectedNoteId);
        if (selectedNote == null) {
            //Cannot find a note with that specific ID
            System.out.printf("Failed to find Note with ID %s%n", selectedNoteId);
            return false;
        }
        showEditNoteDialog(selectedNote);
        return true;
    }

    /**
     * Try to delete the seleted {@link Note}. Nothing will happen if no {@link Note} is selected.
     * @return TRUE if the {@link Note} was found and deleted. FALSE otherwise
     */
    private boolean tryDeleteSelectedNote() {
        var selectedNoteId = getSelectedNoteId();
        if (selectedNoteId == null) {
            //No selected note
            return false;
        }
        //Try to delete the selected note
        if (noteDataService.deleteNoteById(selectedNoteId)) {
            //Could also show some sort of confirmation pop-up
            System.out.println("Successfully deleted selected note");
        }
        else {
            //Could show an alert to indicate the failure to the user
            System.out.println("Failed to delete selected note");
        }
        //Refresh the data table
        setNoteDirectory();
        return true;
    }

    /**
     * Sets the application icon to ic_launcher
     */
    private void setAppIcon() {
        var iconStream = getClass().getResourceAsStream("ic_launcher.png");
        if (iconStream == null) {
            return;
        }
        var appIcon = new Image(iconStream);
        mainStage.getIcons().add(appIcon);
    }

    /**
     * Handler for when the select directory button is clicked
     *
     * Displays a directory chooser to select a directory
     */
    private final EventHandler<MouseEvent> handler_btnSelect_Clicked = event -> showSelectDirectoryDialog();

    /**
     * Handler for when the refresh button is clicked
     *
     * Re-loads all the {@link Note notes} in the target directory
     */
    private final EventHandler<MouseEvent> handler_btnRefresh_Clicked = event -> setNoteDirectory();

    /**
     * Handler for when the "create note" button is clicked
     *
     * Displays a dialog so the user can type in content for the {@link Note}
     */
    private final EventHandler<MouseEvent> handler_btnCreateNote_Clicked = event -> showCreateNoteDialog();

    /**
     * Handler for when the "delete note" button is clicked
     *
     * Deletes the selected {@link Note}
     */
    private final EventHandler<MouseEvent> handler_btnDeleteNote_Clicked = event -> {
        if (!tryDeleteSelectedNote()) {
            System.out.println("No Note selected. Cannot find a Note to delete.");
        }
    };

    /**
     * Handler for when the "edit note" button is clicked
     *
     * Displays a dialog so the user can edit the content of the selected {@link Note}
     */
    private final EventHandler<MouseEvent> handler_btnEditNote_Clicked = event -> {
        if (!tryEditSelectedNote()) {
            System.out.println("No Note selected. Cannot find a Note to edit.");
        }
    };
}