package pdp.view.gui.board;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import pdp.controller.BagOfCommands;
import pdp.controller.commands.PlayMoveCommand;
import pdp.model.board.PromoteMove;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;
import pdp.utils.TextGetter;
import pdp.view.GuiView;

/** A popup to choose the piece to add on the board when promoting. */
public class PromotionPieceSelectionPopUp extends VBox {
  /**
   * Build a popup from the given information.
   *
   * @param stage The stage where you want to display the popup.
   * @param from The source position of the move.
   * @param to The destination position of the move.
   */
  public PromotionPieceSelectionPopUp(Stage stage, Position from, Position to) {
    Stage popupStage = new Stage();
    // Remove possibility to close
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.initOwner(stage);
    popupStage.setOnCloseRequest(WindowEvent::consume);
    popupStage.setResizable(false);
    popupStage.initStyle(StageStyle.UNDECORATED);

    VBox queenButton =
        pieceImage(new ColoredPiece(Piece.QUEEN, Color.WHITE), TextGetter.getText("queen"));
    queenButton.setOnMouseClicked(
        e -> {
          BagOfCommands.getInstance()
              .addCommand(new PlayMoveCommand(new PromoteMove(from, to, Piece.QUEEN).toString()));
          popupStage.close();
        });
    queenButton.setId("queenButton");

    VBox knightButton =
        pieceImage(new ColoredPiece(Piece.KNIGHT, Color.WHITE), TextGetter.getText("knight"));
    knightButton.setOnMouseClicked(
        e -> {
          BagOfCommands.getInstance()
              .addCommand(new PlayMoveCommand(new PromoteMove(from, to, Piece.KNIGHT).toString()));
          popupStage.close();
        });
    knightButton.setId("knightButton");

    VBox bishopButton =
        pieceImage(new ColoredPiece(Piece.BISHOP, Color.WHITE), TextGetter.getText("bishop"));
    bishopButton.setOnMouseClicked(
        e -> {
          BagOfCommands.getInstance()
              .addCommand(new PlayMoveCommand(new PromoteMove(from, to, Piece.BISHOP).toString()));
          popupStage.close();
        });
    bishopButton.setId("bishopButton");

    VBox rookButton =
        pieceImage(new ColoredPiece(Piece.ROOK, Color.WHITE), TextGetter.getText("rook"));
    rookButton.setOnMouseClicked(
        e -> {
          BagOfCommands.getInstance()
              .addCommand(new PlayMoveCommand(new PromoteMove(from, to, Piece.ROOK).toString()));
          popupStage.close();
        });
    rookButton.setId("rookButton");

    HBox layout = new HBox(15, queenButton, knightButton, bishopButton, rookButton);
    layout.setAlignment(Pos.CENTER);
    layout.setStyle(
        "-fx-padding: 15; -fx-border-color: "
            + GuiView.getTheme().getPrimary()
            + "; -fx-border-width: 5; -fx-background-radius: 10;-fx-background-color: "
            + GuiView.getTheme().getBackground()
            + ";");
    Scene scene = new Scene(layout, 500, 150);
    popupStage.setScene(scene);
    popupStage.setTitle(TextGetter.getText("promotion"));
    popupStage.showAndWait();
  }

  private VBox pieceImage(ColoredPiece piece, String text) {
    ImageView imageView = new PieceImage(piece);
    VBox result = new VBox(imageView, new Label(text));
    result.setAlignment(Pos.CENTER);
    result.setStyle(
        "-fx-padding: 15; -fx-background-color: "
            + GuiView.getTheme().getSecondary()
            + "; -fx-border-color: "
            + GuiView.getTheme().getPrimary()
            + "; -fx-border-radius: 5; -fx-background-radius: 10; -fx-background-insets: 0, 1;");
    result.setOnMouseEntered(
        e ->
            result.setStyle(
                "-fx-padding: 15; -fx-background-color: "
                    + GuiView.getTheme().getPrimary()
                    + "; -fx-border-color: "
                    + GuiView.getTheme().getSecondary()
                    + "; -fx-border-radius: 5; -fx-background-radius: 10;"
                    + " -fx-background-insets: 0, 1;"));
    result.setOnMouseExited(
        e ->
            result.setStyle(
                "-fx-padding: 15; -fx-background-color: "
                    + GuiView.getTheme().getSecondary()
                    + "; -fx-border-color: "
                    + GuiView.getTheme().getPrimary()
                    + "; -fx-border-radius: 5; -fx-background-radius: 10;"
                    + " -fx-background-insets: 0, 1;"));
    return result;
  }
}
