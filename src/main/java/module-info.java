module com.example.jsonnotes {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;


    opens com.example.jsonnotes to javafx.fxml;
    exports com.example.jsonnotes;
    exports com.example.jsonnotes.notes;
    opens com.example.jsonnotes.notes to javafx.fxml;
    exports com.example.jsonnotes.notes.service;
    opens com.example.jsonnotes.notes.service to javafx.fxml;
}