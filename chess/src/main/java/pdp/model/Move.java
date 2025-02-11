package pdp.model;

import java.util.List;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPositionException;
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

  /**
   * Parses a string representation of a move and converts it into a {@code Move} object
   *
   * @param stringMove The move in string format ("e2-e4")
   * @return A {@code Move} object representing the parsed move
   * @throws MoveParsingException If the string format is invalid
   */
  public static Move fromString(String stringMove) {

    String[] parts = stringMove.split("-");
    if (parts.length != 2) {
      throw new MoveParsingException(stringMove);
    }
    Move move = new Move(stringToPosition(parts[0]), stringToPosition(parts[1]));
    return move;
  }

  /**
   * Converts a string representation of a chess position into a {@code Position} object
   *
   * @param move The position in string format ("e4")
   * @return A {@code Position} object representing the parsed position
   * @throws InvalidPositionException If the input string is not a valid chess position
   */
  public static Position stringToPosition(String move) {

    if (move.length() != 2) {
      throw new InvalidPositionException(move);
    }

    char colLetter = Character.toLowerCase(move.charAt(0));
    int rowNumber = Character.getNumericValue(move.charAt(1));

    if (colLetter < 'a' || colLetter > 'h' || rowNumber < 1 || rowNumber > 8) {
      throw new InvalidPositionException(move);
    }

    int x = colLetter - 'a';
    int y = rowNumber - 1;

    return new Position(y, x);
  }

  /**
   * Converts a {@code Position} object into its string representation in chess notation
   *
   * @param position The {@code Position} object to convert
   * @return A string representing the position ("e4")
   */
  public String positionToString(Position position) {
    char col = (char) ('a' + position.getX());
    int row = position.getY() + 1;

    return "" + col + row;
  }

  /**
   * Checks if the current move is a classical move by comparing it with a list of available moves
   *
   * @param availableMoves The list of legal moves to check
   * @return The matching move if found
   * @throws IllegalMoveException If the move is not found in the list of available moves
   */
  public Move isMoveClassical(List<Move> availableMoves) throws IllegalMoveException {
    for (Move move : availableMoves) {
      if (move.equals(this)) {
        return move;
      }
    }
    throw new IllegalMoveException("It's not a classicalMove " + this.toString());
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
