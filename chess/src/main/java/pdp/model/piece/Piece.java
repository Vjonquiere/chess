package pdp.model.piece;

/** Enum that represent chess pieces. */
public enum Piece {
  PAWN {
    @Override
    public char getCharRepresentation(final boolean white) {
      return white ? 'P' : 'p';
    }
  },
  ROOK {
    @Override
    public char getCharRepresentation(final boolean white) {
      return white ? 'R' : 'r';
    }
  },
  BISHOP {
    @Override
    public char getCharRepresentation(final boolean white) {
      return white ? 'B' : 'b';
    }
  },
  KNIGHT {
    @Override
    public char getCharRepresentation(final boolean white) {
      return white ? 'N' : 'n';
    }
  },
  QUEEN {
    @Override
    public char getCharRepresentation(final boolean white) {
      return white ? 'Q' : 'q';
    }
  },
  KING {
    @Override
    public char getCharRepresentation(final boolean white) {
      return white ? 'K' : 'k';
    }
  },
  EMPTY {
    @Override
    public char getCharRepresentation(final boolean white) {
      return '_';
    }
  };

  /**
   * Get the character representation of the piece.
   *
   * @param white if true -> white piece, if false -> black piece
   * @return The character representation of the piece
   */
  public abstract char getCharRepresentation(final boolean white);
}
