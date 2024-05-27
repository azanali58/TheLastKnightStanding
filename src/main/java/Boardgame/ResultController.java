package Boardgame;


import Boardgame.ResultModel.JsonResultManager;
import Boardgame.ResultModel.Result;
import Boardgame.ResultModel.Stats;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ResultController {

        @FXML
        private TableColumn<Stats, String> player;

        @FXML
        private TableView<Stats> Table;

        @FXML
        private TableColumn<Stats, Integer> winCount;

        @FXML
        void handleExit(ActionEvent event) {
            Platform.exit();

        }
        @FXML
        void handleRestart(ActionEvent event) throws IOException {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/mainScene.fxml"));
            stage.setTitle("Javafx Main Scene");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }

        @FXML
        public void initialize() throws IOException {
            player.setCellValueFactory(new PropertyValueFactory<>("name"));
            winCount.setCellValueFactory(new PropertyValueFactory<>("WinCount"));
            ObservableList<Stats> observableResult = FXCollections.observableArrayList();
            observableResult.addAll(new JsonResultManager(Path.of("results.json")).getBestPlayers(10));
            Table.setItems(observableResult);
        }
}

