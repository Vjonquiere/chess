package pdp.model;

public enum Piece {
  PAWN {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public char getCharRepresentation(boolean white) {
      return white ? 'P' : 'p';
    }
  },
  ROOK {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public char getCharRepresentation(boolean white) {
      return white ? 'R' : 'r';
    }
  },
  BISHOP {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public char getCharRepresentation(boolean white) {
      return white ? 'B' : 'b';
    }
  },
  KNIGHT {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public char getCharRepresentation(boolean white) {
      return white ? 'N' : 'n';
    }
  },
  QUEEN {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public char getCharRepresentation(boolean white) {
      return white ? 'Q' : 'q';
    }
  },
  KING {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public char getCharRepresentation(boolean white) {
      return white ? 'K' : 'k';
    }
  },
  EMPTY {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public char getCharRepresentation(boolean white) {
      throw new UnsupportedOperationException();
    }
  };

  public abstract Bitboard getMoveMask(Bitboard pos, Board board);

  public abstract char getCharRepresentation(boolean white);
}
