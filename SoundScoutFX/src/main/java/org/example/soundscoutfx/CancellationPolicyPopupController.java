package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class CancellationPolicyPopupController {
    @FXML
    private Label policyHeader;
    @FXML
    private TextArea policyTextArea;
    @FXML
    private Label updatedAtLabel;

    public void setPolicyData(String policy, String updatedAt) {
        policyTextArea.setText(policy);
        updatedAtLabel.setText("Last updated: " + updatedAt);
    }

    @FXML
    private void closePopup() {
        Stage stage = (Stage) policyTextArea.getScene().getWindow();
        stage.close();
    }
}
