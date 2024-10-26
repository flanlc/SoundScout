package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.soundscoutfx.Artist;

public class ArtistProfileController {

    @FXML
    private Label artistNameLabel;
    @FXML
    private Label artistGenreLabel;
    @FXML
    private Label artistCityLabel;

    public void setArtistDetails(Artist artist) {
        artistNameLabel.setText(artist.getStageName());
        artistGenreLabel.setText(artist.getProfile().getGenre());
        artistCityLabel.setText(artist.getCity());
    }
}
