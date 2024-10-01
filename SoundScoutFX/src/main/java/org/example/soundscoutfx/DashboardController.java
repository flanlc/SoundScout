package org.example.soundscoutfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaView;
import javafx.scene.input.MouseEvent;

public class DashboardController {
    SoundScoutSQLHelper sql;

    @FXML
    public void initialize() {
        // Establish SQL connection
        sql = new SoundScoutSQLHelper();
        sql.establishConnection();
        sql.testConnection();

        // Populate observable list with artist list data
        ObservableList<Artist> artistList = FXCollections.observableArrayList(sql.GetDBArtistsProfiles());
        listView.setItems(artistList);
    }

    @FXML
    private ListView<Artist> listView;

    @FXML
    private TextField nameField;

    @FXML
    private TextField joinDateField;

    @FXML
    private TextField genreField;

    @FXML
    private ImageView imgView;

    @FXML
    private MediaView mediaView;

    /** Handles list selection when user clicks an artist; all other fields become populated with the artist's information. */
    @FXML
    public void handleMouseClick(MouseEvent event) {
        ObservableList<Artist> items = listView.getItems();
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            Artist selectedArtist = items.get(selectedIndex);
            nameField.setText(selectedArtist.getStageName());
            joinDateField.setText(selectedArtist.getJoinDate());
            // genreField.setText(selectedArtist.getGenre());
        }
    }
}
