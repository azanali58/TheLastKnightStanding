package Boardgame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import Boardgame.Model.BoardGameModel;
import java.io.IOException;

public class MainSceneController {

    private BoardGameModel model = new BoardGameModel();
    @FXML
    private TextField BlackPlayer;

    @FXML
    private TextField WhitePlayer;

    @FXML
    private void switchScene(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/GameUI.fxml"));
        model.setWhitePlayerName(WhitePlayer.getText());
        model.setBlackPlayerName(BlackPlayer.getText());

        stage.setTitle("Javafx board game model");
        stage.setScene(new Scene(root));
        stage.show();
    }
}

