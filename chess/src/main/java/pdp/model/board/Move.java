package pdp.model.board;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import pdp.exceptions.IllegalMoveException;
import pdp.exceptions.InvalidPositionException;
import pdp.exceptions.MoveParsingException;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Logging;
import pdp.utils.Position;

/** Move representation for all move types. */
public class Move {
  /** Logger of the class. */
  private static final Logger LOGGER = Logger.getLogger(Move.class.getName());

  /** Positon of the source of the move. */
  private final Position source;

  /** Positon of the destination of the move. */
  private final Position dest;

  /** Positon of the piece taken. */
  private Position takeDest;

  /** Piece making the move. */
  private ColoredPiece piece;

  /** Piece taken during the move. */
  private ColoredPiece takenPiece;

  /** Boolean to indicate if the move is a capture. */
  private boolean isTake;

  /** Boolean to indicate if the move result in a check. */
  private final boolean isCheck;

  /** Boolean to indicate if the move result in a checkmate. */
  private final boolean isCheckMate;

  /** Boolean to indicate if the move is a castling move. */
  private final boolean isCastle;

  static {
    Logging.configureLogging(LOGGER);
  }

  /**
   * Constructs a new Move object with the specified source and destination positions.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   */
  public Move(final Position source, final Position dest) {
    this(source, dest, null, false, null, dest, false, false, false);
  }

  /**
   * Constructs a new Move object with the specified source and destination positions.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param isTake A boolean indicating whether the move is a capture (true if it's a capture, false
   *     otherwise).
   */
  public Move(
      final Position source, final Position dest, final ColoredPiece piece, final boolean isTake) {
    this(source, dest, piece, isTake, null, dest, false, false, false);
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
      final Position source,
      final Position dest,
      final ColoredPiece piece,
      final boolean isTake,
      final ColoredPiece takenPiece) {
    this(source, dest, piece, isTake, takenPiece, dest, false, false, false);
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
      final Position source,
      final Position dest,
      final ColoredPiece piece,
      final boolean isTake,
      final ColoredPiece takenPiece,
      final boolean isCastle) {
    this(source, dest, piece, isTake, takenPiece, dest, false, false, isCastle);
  }

  /**
   * Construct a new move from a specified source, dest with full piece take support.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param piece The being moved.
   * @param isTake A boolean indicating whether the move is a capture (true if it's a capture, false
   *     otherwise).
   * @param takenPiece The ColoredPiece that was captured, or null if no piece was captured.
   * @param takeDest The position of the taken piece.
   */
  public Move(
      final Position source,
      final Position dest,
      final ColoredPiece piece,
      final boolean isTake,
      final ColoredPiece takenPiece,
      final Position takeDest) {
    this(source, dest, piece, isTake, takenPiece, takeDest, false, false, false);
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
      final Position source,
      final Position dest,
      final ColoredPiece piece,
      final boolean isTake,
      final ColoredPiece takenPiece,
      final boolean isCheck,
      final boolean isCheckMate) {

    this(source, dest, piece, isTake, takenPiece, dest, isCheck, isCheckMate, false);
  }

  /**
   * Constructs a new Move object with the specified source and destination positions, the piece
   * being moved, a flag indicating whether the move is a capture, the captured piece (if any), the
   * take position and flags for check and checkmate.
   *
   * @param source The starting Position of the move.
   * @param dest The destination Position of the move.
   * @param piece The ColoredPiece being moved.
   * @param isTake A boolean indicating whether the move is a capture (true if it's a capture, false
   *     otherwise).
   * @param takenPiece The ColoredPiece that was captured, or null if no piece was captured.
   * @param takeDest The position of the taken piece.
   * @param isCheck A boolean indicating whether the move results in a check (true if it's a check,
   *     false otherwise).
   * @param isCheckMate A boolean indicating whether the move results in a checkmate (true if it's a
   *     checkmate, false otherwise).
   * @param isCastle A boolean indicating whether the move is a castle.
   */
  private Move(
      final Position source,
      final Position dest,
      final ColoredPiece piece,
      final boolean isTake,
      final ColoredPiece takenPiece,
      final Position takeDest,
      final boolean isCheck,
      final boolean isCheckMate,
      final boolean isCastle) {
    this.source = source;
    this.dest = dest;
    this.piece = piece;
    this.isTake = isTake;
    this.takenPiece = takenPiece;
    this.takeDest = takeDest;
    this.isCheck = isCheck;
    this.isCheckMate = isCheckMate;
    this.isCastle = isCastle;
  }

  /**
   * Parses a string representation of a move and converts it into a {@code Move} object.
   *
   * @param stringMove The move in string format ("e2-e4")
   * @return A {@code Move} object representing the parsed move
   * @throws MoveParsingException If the string format is invalid
   */
  public static Move fromString(final String stringMove, final boolean isWhiteTurn)
      throws MoveParsingException {
    String move = stringMove;

    if (move.equalsIgnoreCase("o-o-o")) {
      if (isWhiteTurn) {
        move = "e1-c1";
      } else {
        move = "e8-c8";
      }
    } else if (move.equalsIgnoreCase("o-o")) {
      if (isWhiteTurn) {
        move = "e1-g1";
      } else {
        move = "e8-g8";
      }
    }

    return fromString(move);
  }

  /**
   * Parses a string representation of a move and converts it into a {@code Move} object Warning:
   * Can't handle castling as o-o-o or o-o.
   *
   * @param stringMove The move in string format ("e2-e4")
   * @return A {@code Move} object representing the parsed move
   * @throws MoveParsingException If the string format is invalid
   */
  public static Move fromString(final String stringMove) throws MoveParsingException {

    if (stringMove.equalsIgnoreCase("o-o-o") || stringMove.equalsIgnoreCase("o-o")) {
      throw new MoveParsingException(
          "Castling notation ('o-o' or 'o-o-o') is not supported in this method.");
    }

    final String[] parts = stringMove.split("-");
    if (parts.length != 2) {
      throw new MoveParsingException(stringMove);
    }

    final String[] promoteParts = parts[1].split("=");
    if (promoteParts.length == 2) {
      return new PromoteMove(
          stringToPosition(parts[0]),
          stringToPosition(promoteParts[0]),
          stringToPiece(promoteParts[1]));
    }
    return new Move(stringToPosition(parts[0]), stringToPosition(parts[1]));
  }

  /**
   * Parses a string representation of a move and converts it into a {@code Move} object.
   *
   * @param stringMove The move in string format ("e2-e4")
   * @return A {@code Move} object representing the parsed move
   * @throws MoveParsingException If the string format is invalid
   */
  public static Move fromUciString(final String stringMove) throws MoveParsingException {
    if (stringMove.length() == 5) {
      return new PromoteMove(
          stringToPosition(stringMove.substring(0, 2)),
          stringToPosition(stringMove.substring(2, 4)),
          stringToPiece(stringMove.substring(4, 5)));
    } else if (stringMove.length() == 4) {
      return new Move(
          stringToPosition(stringMove.substring(0, 2)),
          stringToPosition(stringMove.substring(2, 4)));
    }
    throw new MoveParsingException(stringMove);
  }

  /**
   * Converts a string representation of a chess position into a {@code Position} object.
   *
   * @param move The position in string format ("e4")
   * @return A {@code Position} object representing the parsed position
   * @throws InvalidPositionException If the input string is not a valid chess position
   */
  public static Position stringToPosition(final String move) {

    if (move.length() != 2) {
      throw new InvalidPositionException(move);
    }

    final char colLetter = Character.toLowerCase(move.charAt(0));
    final int rowNumber = Character.getNumericValue(move.charAt(1));

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
   * "n" for Knight - "b" for Bishop - "r" for Rook - "q" for Queen - "k" for King.
   *
   * @param pieceStr A string representing a chess piece (e.g., "p", "n", "b", "r", "q", "k").
   * @return The corresponding Piece enum value.
   * @throws IllegalArgumentException If the input string does not correspond to a valid piece.
   */
  public static Piece stringToPiece(final String pieceStr) {
    return switch (pieceStr.toLowerCase()) {
      case "p" -> Piece.PAWN;
      case "n" -> Piece.KNIGHT;
      case "b" -> Piece.BISHOP;
      case "r" -> Piece.ROOK;
      case "q" -> Piece.QUEEN;
      case "k" -> Piece.KING;
      default -> throw new IllegalArgumentException("Invalid piece string: " + pieceStr);
    };
  }

  /**
   * Converts a {@code Position} object into its string representation in chess notation.
   *
   * @param position The {@code Position} object to convert
   * @return A string representing the position ("e4")
   */
  public static String positionToString(final Position position) {
    final char col = (char) ('a' + position.x());
    final int row = position.y() + 1;

    return "" + col + row;
  }

  /**
   * Checks if the current move is a classical move by comparing it with a list of available moves.
   *
   * @param availableMoves The list of legal moves to check
   * @return The matching move if found
   * @throws IllegalMoveException If the move is not found in the list of available moves
   */
  public Optional<Move> isMoveClassical(final List<Move> availableMoves)
      throws IllegalMoveException {
    for (final Move move : availableMoves) {
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
   * Retrieves the position of the piece taken during this move.
   *
   * @return Position of the taken piece
   */
  public Position getTakeDest() {
    return this.takeDest;
  }

  /**
   * Retrieves a boolean to indicate if the move is a castling move.
   *
   * @return true if the move is a castling, false if not.
   */
  public boolean isCastle() {
    return this.isCastle;
  }

  public void setTakeDest(final Position takeDest) {
    this.takeDest = takeDest;
  }

  /**
   * Retrieves the piece involved in the move.
   *
   * @return The ColoredPiece being moved.
   */
  public ColoredPiece getPiece() {
    return piece;
  }

  public void setPiece(final ColoredPiece piece) {
    this.piece = piece;
  }

  /**
   * Retrieves the piece taken.
   *
   * @return The ColoredPiece taken.
   */
  public ColoredPiece getPieceTaken() {
    return takenPiece;
  }

  /**
   * Sets the piece taken during the move to the given piece.
   *
   * @param pieceTaken value to put in the field pieceTaken
   */
  public void setPieceTaken(final ColoredPiece pieceTaken) {
    this.takenPiece = pieceTaken;
  }

  /**
   * Checks if the move is a capture.
   *
   * @return true if the move is a capture,false otherwise.
   */
  public boolean isTake() {
    return isTake;
  }

  /**
   * Sets whether the move is a capture.
   *
   * @param isTake A boolean indicating whether the move is a capture.
   */
  public void setTake(final boolean isTake) {
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
    if (this.piece != null && this.piece.getPiece() != Piece.PAWN) {
      piece = String.valueOf(this.piece.getPiece().getCharRepresentation(true));
    }
    final String sourceStr = positionToString(this.source);
    final String destinationStr = positionToString(this.dest);
    final String separator = this.isTake ? "x" : "-";
    final String annotation = this.isCheckMate ? "#" : (this.isCheck ? "+" : "");

    return piece + sourceStr + separator + destinationStr + annotation;
  }

  /**
   * Convert a move to the UCI format.
   *
   * @return The string representing the move at UCI format
   */
  public String toUciString() {
    final String sourceStr = positionToString(this.source);
    final String destinationStr = positionToString(this.dest);
    return sourceStr + destinationStr;
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
    final String sourceStr = positionToString(this.source);
    final String destinationStr = positionToString(this.dest);
    final String separator = this.isTake ? "x" : "-";

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
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Move move)) {
      return false;
    }
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
