package pdp.model;

import pdp.exceptions.IllegalMoveException;
import pdp.utils.Position;

public class Move {
  Position source;
  Position dest;
  Piece piece;
  boolean isTake;
  boolean isCheck;
  boolean isCheckMate;


  // The constructor takes a string of a movement played, example: "h2 h4"
  public Move(String move){
    String[] parts = move.split(" ");
    this.source = fromString(parts[0]);
    this.dest = fromString(parts[1]);
    /* this.piece = getPieceAt(source.getX(),source.getY()); */
    this.isTake = false;
    this.isCheck = false ;
    this.isCheckMate = false;
  }


  // fromString("e4") -> Position (4,3)
  public static Position fromString(String move) {
    
    char colLetter = Character.toLowerCase(move.charAt(0));
    int rowNumber = Character.getNumericValue(move.charAt(1));

    if (colLetter < 'a' || colLetter > 'h' || rowNumber < 1 || rowNumber > 8) {
      throw new IllegalMoveException("Invalid move: " + move);
    }
    
    int x = colLetter - 'a';
    int y = rowNumber - 1;

    return new Position(y,x);
  }

  // fromPosition(4,3) -> "e4"
  public static String fromPosition(Position move) {
    
    char colLetter = (char) ('a' + move.getX());
    int rowNumber = move.getY() - 1;

    return "" + colLetter + rowNumber;
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
    // TODO
    throw new UnsupportedOperationException(
        "Method not implemented in " + this.getClass().getName());
  }
}
