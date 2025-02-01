package pdp.model;

public enum Piece {
  PAWN {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public String getStringRepresentation(boolean white) {
      // TODO
      throw new UnsupportedOperationException();
    }
  },
  ROOK {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public String getStringRepresentation(boolean white) {
      // TODO
      throw new UnsupportedOperationException();
    }
  },
  BISHOP {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public String getStringRepresentation(boolean white) {
      // TODO
      throw new UnsupportedOperationException();
    }
  },
  KNIGHT {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public String getStringRepresentation(boolean white) {
      // TODO
      throw new UnsupportedOperationException();
    }
  },
  QUEEN {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public String getStringRepresentation(boolean white) {
      // TODO
      throw new UnsupportedOperationException();
    }
  },
  KING {
    @Override
    public Bitboard getMoveMask(Bitboard pos, Board board) {
      // TODO
      throw new UnsupportedOperationException();
    }

    @Override
    public String getStringRepresentation(boolean white) {
      // TODO
      throw new UnsupportedOperationException();
    }
  };

  public abstract Bitboard getMoveMask(Bitboard pos, Board board);

  public abstract String getStringRepresentation(boolean white);
}
