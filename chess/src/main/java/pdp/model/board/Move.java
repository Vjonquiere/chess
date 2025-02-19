package pdp.model.board;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPositionException;
import pdp.exceptions.MoveParsingException;
import pdp.model.Game;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

public class Move {
  private static final Logger LOGGER = Logger.getLogger(Move.class.getName());
  public Position source;
  public Position dest;
  public ColoredPiece piece;
  public ColoredPiece takenPiece;
  public boolean isTake = false;
  public boolean isCheck = false;
  public boolean isCheckMate = false;

  /**
   * Constructs a new Move object with the specified source and destination positions.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   */
  public Move(Position source, Position dest) {
    Logging.configureLogging(LOGGER);
    this.source = source;
    this.dest = dest;
  }

  /**
   * Constructs a new Move object with the specified source and destination positions.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param isTake A boolean indicating whether the move is a capture (true if it's a capture, false
   *     otherwise).
   */
  public Move(Position source, Position dest, ColoredPiece piece, boolean isTake) {
    Logging.configureLogging(LOGGER);
    this.source = source;
    this.dest = dest;
    this.piece = piece;
    this.isTake = isTake;
  }

  /**
   * Constructs a new {@code Move} object with the specified source and destination positions, the
   * piece being moved, a flag indicating whether the move is a capture, and the captured piece (if
   * any).
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param piece The ColoredPiece being moved.
   * @param isTake A boolean indicating whether the move is a capture (true if it's a capture, false
   *     otherwise).
   * @param takenPiece The ColoredPiece that was captured, or null if no piece was captured.
   */
  public Move(
      Position source, Position dest, ColoredPiece piece, boolean isTake, ColoredPiece takenPiece) {
    Logging.configureLogging(LOGGER);
    this.source = source;
    this.dest = dest;
    this.piece = piece;
    this.isTake = isTake;
    this.takenPiece = takenPiece;
  }

  /**
   * Constructs a new Move object with the specified source and destination positions, the piece
   * being moved, a flag indicating whether the move is a capture, the captured piece (if any), and
   * flags for check and checkmate.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param piece The ColoredPiece being moved.
   * @param isTake A boolean indicating whether the move is a capture (true if it's a capture, false
   *     otherwise).
   * @param takenPiece The ColoredPiece that was captured, or null if no piece was captured.
   * @param isCheck A boolean indicating whether the move results in a check (true if it's a check,
   *     false otherwise).
   * @param isCheckMate A boolean indicating whether the move results in a checkmate (true if it's a
   *     checkmate, false otherwise).
   */
  public Move(
      Position source,
      Position dest,
      ColoredPiece piece,
      boolean isTake,
      ColoredPiece takenPiece,
      boolean isCheck,
      boolean isCheckMate) {
    Logging.configureLogging(LOGGER);
    this.source = source;
    this.dest = dest;
    this.piece = piece;
    this.isTake = isTake;
    this.takenPiece = takenPiece;
    this.isCheck = isCheck;
    this.isCheckMate = isCheckMate;
  }

  /**
   * Parses a string representation of a move and converts it into a {@code Move} object
   *
   * @param stringMove The move in string format ("e2-e4")
   * @return A {@code Move} object representing the parsed move
   * @throws MoveParsingException If the string format is invalid
   */
  public static Move fromString(String stringMove) throws MoveParsingException {

    if (stringMove.equalsIgnoreCase("o-o-o")) {
      if (Game.getInstance().getGameState().isWhiteTurn()) {
        stringMove = "e1-c1";
      } else {
        stringMove = "e8-c8";
      }
    } else if (stringMove.equalsIgnoreCase("o-o")) {
      if (Game.getInstance().getGameState().isWhiteTurn()) {
        stringMove = "e1-g1";
      } else {
        stringMove = "e8-g8";
      }
    }

    String[] parts = stringMove.split("-");
    if (parts.length != 2) {
      throw new MoveParsingException(stringMove);
    }

    String[] promoteParts = parts[1].split("=");
    if (promoteParts.length == 2) {
      return new PromoteMove(
          stringToPosition(parts[0]),
          stringToPosition(promoteParts[0]),
          stringToPiece(promoteParts[1]));
    }
    return new Move(stringToPosition(parts[0]), stringToPosition(parts[1]));
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

    return new Position(x, y);
  }

  /**
   * Converts a string representation of a chess piece to the corresponding Piece enum. The input
   * string should be a single character representing the piece (case-insensitive): - "p" for Pawn -
   * "n" for Knight - "b" for Bishop - "r" for Rook - "q" for Queen - "k" for King
   *
   * @param pieceStr A string representing a chess piece (e.g., "p", "n", "b", "r", "q", "k").
   * @return The corresponding Piece enum value.
   * @throws IllegalArgumentException If the input string does not correspond to a valid piece.
   */
  public static Piece stringToPiece(String pieceStr) {
    switch (pieceStr.toLowerCase()) {
      case "p":
        return Piece.PAWN;
      case "n":
        return Piece.KNIGHT;
      case "b":
        return Piece.BISHOP;
      case "r":
        return Piece.ROOK;
      case "q":
        return Piece.QUEEN;
      case "k":
        return Piece.KING;
      default:
        throw new IllegalArgumentException("Invalid piece string: " + pieceStr);
    }
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
  public Optional<Move> isMoveClassical(List<Move> availableMoves) throws IllegalMoveException {
    for (Move move : availableMoves) {
      if (move.equals(this)) {
        if (this instanceof PromoteMove) {
          return Optional.of(
              new PromoteMove(
                  move.source,
                  move.dest,
                  ((PromoteMove) this).getPromPiece(),
                  move.piece,
                  move.isTake,
                  move.takenPiece,
                  move.isCheck,
                  move.isCheckMate));
        }
        return Optional.of(move);
      }
    }
    return Optional.empty();
  }

  /**
   * Retrieves the source position of the move.
   *
   * @return The Position representing the source of the move.
   */
  public Position getSource() {
    return source;
  }

  /**
   * Retrieves the destination position of the move.
   *
   * @return The Position representing the destination of the move.
   */
  public Position getDest() {
    return dest;
  }

  /**
   * Retrieves the piece involved in the move.
   *
   * @return The ColoredPiece being moved.
   */
  public ColoredPiece getPiece() {
    return piece;
  }

  /**
   * Checks if the move is a capture.
   *
   * @return true if the move is a capture,f alse otherwise.
   */
  public boolean isTake() {
    return isTake;
  }

  /**
   * Sets whether the move is a capture.
   *
   * @param isTake A boolean indicating whether the move is a capture.
   */
  public void setTake(boolean isTake) {
    this.isTake = isTake;
  }

  /**
   * Checks if the move results in a check.
   *
   * @return true if the move results in a check, false otherwise.
   */
  public boolean isCheck() {
    return isCheck;
  }

  /**
   * Checks if the move results in a checkmate.
   *
   * @return true if the move results in a checkmate, false otherwise.
   */
  public boolean isCheckMate() {
    return isCheckMate;
  }

  /**
   * Converts the move to its algebraic notation string representation.
   *
   * <p>The format includes the piece type (except for pawns), the source position, a separator ('x'
   * for captures, '-' otherwise), the destination position, and an optional annotation for check
   * ('+') or checkmate ('#').
   *
   * @return The algebraic notation string representing the move.
   */
  public String toAlgebraicString() {
    String piece = "";
    if (this.piece != null && this.piece.piece != Piece.PAWN) {
      piece = String.valueOf(this.piece.piece.getCharRepresentation(true));
    }
    String sourceStr = positionToString(this.source);
    String destinationStr = positionToString(this.dest);
    String separator = this.isTake ? "x" : "-";
    String annotation = this.isCheckMate ? "#" : (this.isCheck ? "+" : "");

    return piece + sourceStr + separator + destinationStr + annotation;
  }

  /**
   * Converts the move to a string representation.
   *
   * <p>The format is the source position, a separator ('x' for captures, '-' otherwise), and the
   * destination position.
   *
   * @return The string representation of the move.
   */
  @Override
  public String toString() {
    String sourceStr = positionToString(this.source);
    String destinationStr = positionToString(this.dest);
    String separator = this.isTake ? "x" : "-";

    return sourceStr + separator + destinationStr;
  }

  /**
   * Compares this Move object with another object for equality. Two Move objects are considered
   * equal if they have the same source and destination positions.
   *
   * @param obj The object to compare this Move with.
   * @return true if the two Move objects are equal, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || !(obj instanceof Move)) return false;
    Move move = (Move) obj;
    return source.equals(move.source) && dest.equals(move.dest);
  }

  /**
   * Generates a hash code for this Move object based on the source and destination positions. The
   * hash code is calculated as 31 times the hash code of the source position, plus the hash code of
   * the destination position.
   *
   * @return The hash code of this Move object.
   */
  @Override
  public int hashCode() {
    return 31 * source.hashCode() + dest.hashCode();
  }
}
