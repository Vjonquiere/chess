package pdp.model;

import java.util.List;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.MoveParsingException;
import pdp.utils.Logging;
import pdp.utils.Position;

public class Move {
  private static final Logger LOGGER = Logger.getLogger(Move.class.getName());
  Position source;
  Position dest;
  Piece piece;
  boolean isTake = false;
  boolean isCheck = false;
  boolean isCheckMate = false;

  public Move(Position source, Position dest) {
    Logging.configureLogging(LOGGER);
    this.source = source;
    this.dest = dest;
  }

  public Move(Position source, Position dest, Piece piece, boolean isTake) {
    Logging.configureLogging(LOGGER);
    this.source = source;
    this.dest = dest;
    this.piece = piece;
    this.isTake = isTake;
  }

  // fromString("h2 h4") -> Move move (source(7,1), dest= (7,3))
  public static Move fromString(String stringMove) {

    String[] parts = stringMove.split("-");
    if (parts.length != 2) {
      throw new MoveParsingException("Invalid format : " + stringMove);
    }
    Move move = new Move(stringToPosition(parts[0]), stringToPosition(parts[1]));
    return move;
  }

  // stringToPosition("e4") -> Position (4,3)
  public static Position stringToPosition(String move) {

    if (move.length() != 2) {
      throw new MoveParsingException("Invalid position : " + move);
    }

    char colLetter = Character.toLowerCase(move.charAt(0));
    int rowNumber = Character.getNumericValue(move.charAt(1));

    if (colLetter < 'a' || colLetter > 'h' || rowNumber < 1 || rowNumber > 8) {
      throw new IllegalMoveException("Invalid position: " + move);
    }

    int x = colLetter - 'a';
    int y = rowNumber - 1;

    return new Position(y, x);
  }

  // positionToString((4,3)) -> "e4"
  public String positionToString(Position position) {
    char col = (char) ('a' + position.getX());
    int row = position.getY() + 1;

    return "" + col + row;
  }

  public Move isMoveClassical(List<Move> availableMoves) throws IllegalMoveException {
    for (Move move : availableMoves) {
      if (move.equals(this)) {
        return move;
      }
    }
    throw new IllegalMoveException("Illegal move: " + this.toString());
  }

  public Position getSource() {
    return source;
  }

  public Position getDest() {
    return dest;
  }

  public Piece getPiece() {
    return piece;
  }

  public boolean isTake() {
    return isTake;
  }

  public boolean isCheck() {
    return isCheck;
  }

  public boolean isCheckMate() {
    return isCheckMate;
  }

  @Override
  public String toString() {
    String sourceStr = positionToString(this.source);
    String destinationStr = positionToString(this.dest);
    String separator = this.isTake ? "x" : "-";

    return sourceStr + separator + destinationStr;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Move move = (Move) obj;
    return source.equals(move.source) && dest.equals(move.dest);
  }

  @Override
  public int hashCode() {
    return 31 * source.hashCode() + dest.hashCode();
  }
}
