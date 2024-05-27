package Boardgame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import Boardgame.Model.BoardGameModel;
import Boardgame.Model.KnightDirection;
import Boardgame.Model.PieceType;
import Boardgame.Model.Position;
import Boardgame.ResultModel.JsonResultManager;
import Boardgame.ResultModel.Result;
import Boardgame.ResultModel.ResultManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.tinylog.Logger;

public class GameController {


    ResultManager manager = new JsonResultManager(Path.of("results.json"));
    @FXML
    private void switchScene(ActionEvent event) throws IOException {
        if (selectablePositions.isEmpty()) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/results.fxml"));
            stage.setTitle("Result of the game");
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            Logger.info("Game is not over");
        }
    }



    private List<Position> selectablePositions = new ArrayList<>();



    private HashSet<Position> visitedPositions = new HashSet<>();


    private int orderOfMove = 1;

    private Position selected;

    private BoardGameModel model = new BoardGameModel();

    @FXML
    private TextField WhiteMoves;

    @FXML
    private TextField BlackMoves;

    @FXML
    private TextField PlayerTurn;



    @FXML
    private GridPane Board;

    @FXML
    private void initialize() {


        createBoard();
        createPieces();
        setSelectablePositions();
        showSelectablePositions();
        BlackMoves.textProperty().bind(model.numberOfMovesBlackProperty().asString());
        WhiteMoves.textProperty().bind(model.numberOfMovesWhiteProperty().asString());
        PlayerTurn.textProperty().bind(model.nameofWhitePlayerProperty().concat(" turn"));
        model.setTime((LocalDateTime.now()));

    }

    private void createBoard() {
        for (int i = 0; i < Board.getRowCount(); i++) {
            for (int j = 0; j < Board.getColumnCount(); j++) {
                var square = createSquare(i,j);
                Board.add(square, i, j);
            }
        }
    }


    private StackPane createSquare(int row, int col) {
        var square = new StackPane();
        square.getStyleClass().add("square");
        square.getStyleClass().add((row + col) % 2 == 0 ? "light": "dark");


        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    private void createPieces() {

        for (int i = 0; i < model.getPieceCount(); i++) {
            model.positionProperty(i).addListener(this::piecePositionChange);
            var piece = createPiece(i);
            getSquare(model.getPiecePosition(i)).getChildren().add(piece);
        }
    }

    private ImageView createPiece(int piece_no) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(40);
        imageView.setFitWidth(35);
        imageView.setSmooth(true);
        ReadOnlyObjectProperty<PieceType> pice_type =  new ReadOnlyObjectWrapper<>(PieceType.valueOf(model.getPieceType(piece_no).name()));
        imageView.imageProperty().bind(createSquareBinding(pice_type));
        return imageView;
    }


    private ObjectBinding<Image> createSquareBinding(ReadOnlyObjectProperty<PieceType> squareProperty) {
        return new ObjectBinding<>() {
            {
                super.bind(squareProperty);
            }

            @Override
            protected Image computeValue() {
                switch (squareProperty.get()) {
                    case BLACK:
                        return new Image("/knight01.png"); // Set the path to black image
                    case WHITE:
                        return new Image("/knight02.png"); // Set the path to white image
                    default:
                        return null;
                }
            }
        };
    }


    private enum SelectionPhase {
        SELECT_FROM,
        SELECT_TO;

        public SelectionPhase alter() {
            return switch (this) {
                case SELECT_FROM -> SELECT_TO;
                case SELECT_TO -> SELECT_FROM;
            };
        }
    }

    private SelectionPhase selectionPhase = SelectionPhase.SELECT_FROM;

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        var position = new Position(row, col);
        Logger.debug("Click on square {}", position);
        if (orderOfMove == 1) {
            handleClickOnSquare1(position);

        }
        if (orderOfMove == 0) {
            handleClickOnSquare0(position);

        }

    }
    private void handleClickOnSquare1(Position position) {


        switch (selectionPhase) {
            case SELECT_FROM -> {
                if (selectablePositions.get(1).equals(position)) {
                    selectPosition(position);
                    alterSelectionPhase();
                    orderOfMove = 0;

                }
            }
            case SELECT_TO -> {
                if (selectablePositions.contains(position)) {
                    var pieceNumber = model.getPieceNumber(selected);
                    var direction = KnightDirection.of(position.row() - selected.row(), position.col() - selected.col());
                    Logger.debug("Moving piece {} {}", pieceNumber, direction);
                    model.move(pieceNumber, direction); //pieceNumber is
                    deselectSelectedPosition();
                    alterSelectionPhase();
                    model.increaseMoves("black");
                    PlayerTurn.textProperty().bind(model.nameofWhitePlayerProperty().concat(" turn"));

                }
            }
        }
    }


    private void handleClickOnSquare0(Position position) {

        switch (selectionPhase) {
            case SELECT_FROM -> {
                if (selectablePositions.get(0).equals(position)) {
                    selectPosition(position);
                    alterSelectionPhase();
                    orderOfMove = 1;

                }
            }
            case SELECT_TO -> {
                if (selectablePositions.contains(position)) {
                    var pieceNumber = model.getPieceNumber(selected);
                    var direction = KnightDirection.of(position.row() - selected.row(), position.col() - selected.col());
                    Logger.debug("Moving piece  {} {}", pieceNumber, direction);
                    model.move(pieceNumber, direction);
                    deselectSelectedPosition();
                    alterSelectionPhase();
                    model.increaseMoves("white");
                    PlayerTurn.textProperty().bind(model.nameofBlackPlayerProperty().concat(" turn"));

                }
            }
        }
    }



    private void alterSelectionPhase() {
        selectionPhase = selectionPhase.alter();
        hideSelectablePositions();
        setSelectablePositions();
        showSelectablePositions();
    }

    private void selectPosition(Position position) {
        selected = position;
        showSelectedPosition();
    }

    private void showSelectedPosition() {
        var square = getSquare(selected);
        square.getStyleClass().add("selected");
    }

    private void deselectSelectedPosition() {
        hideSelectedPosition();
        selected = null;
    }
    private void hideSelectedPosition() {
        var square = getSquare(selected);
        square.getStyleClass().remove("selected");
    }

    private void setSelectablePositions() {


        selectablePositions.clear();
        visitedPositions.addAll(model.getPiecePositions());

        switch (selectionPhase) {
            case SELECT_FROM -> {
                selectablePositions.addAll(model.getPiecePositions());
            }
            case SELECT_TO -> {
                var pieceNumber = model.getPieceNumber(selected);
                for (var direction : model.getValidMoves(pieceNumber)) {
                    selectablePositions.add(selected.moveTo(direction));
                }
                selectablePositions.removeAll(visitedPositions);
                if (selectablePositions.isEmpty()) {
                    gameOver();
                }
            }
        }
    }

    private void showSelectablePositions() {
        for (var selectablePosition : selectablePositions) {
            var square = getSquare(selectablePosition);
            square.getStyleClass().add("selectable");
        }
    }

    private void hideSelectablePositions() {
        for (var selectablePosition : selectablePositions) {
            var square = getSquare(selectablePosition);
            square.getStyleClass().remove("selectable");
        }
    }




    private StackPane getSquare(Position position) {
        for (var child : Board.getChildren()) {
            if (GridPane.getRowIndex(child) == position.row() && GridPane.getColumnIndex(child) == position.col()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }


    private void piecePositionChange(ObservableValue<? extends Position> observable, Position oldPosition, Position newPosition) {
        Logger.debug("Move: {} -> {}", oldPosition, newPosition);
        StackPane oldSquare = getSquare(oldPosition);
        StackPane newSquare = getSquare(newPosition);
        newSquare.getChildren().addAll(oldSquare.getChildren());
        oldSquare.getChildren().clear();
    }

    private void gameOver()  {
        Logger.info("game is over");

        if (model.getNumberOfMovesBlack() < model.getNumberOfMovesWhite()) {
            model.setWinnerName(model.getNameOfWhitePlayer());
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Game is over");
            alert.setContentText(model.getNameOfWhitePlayer() + " wins with " + model.getNumberOfMovesWhite() + " moves");
            alert.showAndWait();
        } else {
            model.setWinnerName(model.getNameOfBlackPlayer());
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Game is over");
            alert.setContentText(model.getNameOfBlackPlayer() + " wins with " + model.getNumberOfMovesBlack() + " moves");
            alert.showAndWait();
        }

        try {
            manager.add(createGameResult());
        } catch (IOException e) {
             throw new RuntimeException(e);
        }
    }



    private  Result createGameResult() {
        return Result.builder()
                .blackPlayer(model.getNameOfBlackPlayer())
                .whitePlayer(model.getNameOfWhitePlayer())
                .numberOfMovesBlack(model.getNumberOfMovesBlack())
                .numberOfMovesWhite(model.getNumberOfMovesWhite())
                .winner(model.getWinnerName())
                .startDateTime(model.getTime())
                .build();
    }
}


