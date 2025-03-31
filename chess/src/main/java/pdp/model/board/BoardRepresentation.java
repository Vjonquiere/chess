package pdp.model.board;

import java.util.List;
import pdp.model.piece.Color;
import pdp.model.piece.ColoredPiece;
import pdp.model.piece.Piece;
import pdp.utils.Position;

/** Interface containing all needed methods to implement a custom board representation. */
public interface BoardRepresentation {
  /**
   * Retrieves the position of the pawns of the given player.
   *
   * @param white true if the player is white, false otherwise.
   * @return List of positions of pawns
   */
  List<Position> getPawns(boolean white);

  /**
   * Retrieves the position of the rooks of the given player.
   *
   * @param white true if the player is white, false otherwise.
   * @return List of positions of rooks
   */
  List<Position> getRooks(boolean white);

  /**
   * Retrieves the position of the bishops of the given player.
   *
   * @param white true if the player is white, false otherwise.
   * @return List of positions of bishops
   */
  List<Position> getBishops(boolean white);

  /**
   * Retrieves the position of the knights of the given player.
   *
   * @param white true if the player is white, false otherwise.
   * @return List of positions of knights
   */
  List<Position> getKnights(boolean white);

  /**
   * Retrieves the position of the queens of the given player.
   *
   * @param white true if the player is white, false otherwise.
   * @return List of positions of queens
   */
  List<Position> getQueens(boolean white);

  /**
   * Retrieves the position of the king of the given player.
   *
   * @param white true if the player is white, false otherwise.
   * @return List of positions of king
   */
  List<Position> getKing(boolean white);

  /**
   * Retrieves the piece at the given (x,y) position.
   *
   * @param x x-coordinate of the piece.
   * @param y y-coordinate of the piece.
   * @return Piece at (x,y)
   */
  ColoredPiece getPieceAt(int x, int y);

  /**
   * Retrieves the number of columns in the board.
   *
   * @return number of columns.
   */
  int getNbCols();

  /**
   * Retrieves the number of rows in the board.
   *
   * @return number of rows.
   */
  int getNbRows();

  /**
   * Move the piece at the position from to the position to.
   *
   * @param from old position of the piece.
   * @param to new position of the piece.
   */
  void movePiece(Position from, Position to);

  /**
   * Deletes the piece at position (x,y).
   *
   * @param x x-coordinate of the piece.
   * @param y y-coordinate of the piece.
   */
  void deletePieceAt(int x, int y);

  /**
   * Boolean to indicate whether there are queens on the board or not.
   *
   * @return true if queens are not on the board, false otherwise.
   */
  boolean queensOffTheBoard();

  /**
   * Boolean to indicate if the kings are active or not.
   *
   * @return true if the king are active, false otherwise.
   */
  boolean areKingsActive();

  /**
   * Boolean to indicate if the pawns have progressed or not.
   *
   * @return true if the pawns have progressed, false otherwise.
   */
  boolean pawnsHaveProgressed(boolean isWhite);

  /**
   * Retrieves the number of pieces still on the board.
   *
   * @return number of pieces on the board.
   */
  int nbPiecesRemaining();

  /**
   * Retrieves a list corresponding to the moves of the king of the given color.
   *
   * @param white true if the piece is white, false otherwise.
   * @return List of moves for the given king.
   */
  List<Move> retrieveKingMoves(boolean white);

  /**
   * Retrieves a list corresponding to the moves of the bishops of the given color.
   *
   * @param white true if the piece is white, false otherwise.
   * @return List of moves for the given bishop.
   */
  List<Move> retrieveBishopMoves(boolean white);

  /**
   * Retrieves a list corresponding to the positions on the board of the white player.
   *
   * @return List of positions for the white player.
   */
  List<List<Position>> retrieveWhitePiecesPos();

  /**
   * Retrieves a list corresponding to the positions on the board of the black player.
   *
   * @return List of positions for the black player.
   */
  List<List<Position>> retrieveBlackPiecesPos();

  /**
   * Retrieves a list corresponding to the initial positions on the board of the black player.
   *
   * @return List of initial positions for the black player.
   */
  List<List<Position>> retrieveInitialWhitePiecesPos();

  /**
   * Retrieves a list corresponding to the initial positions on the board of the black player.
   *
   * @return List of initial positions for the black player.
   */
  List<List<Position>> retrieveInitialBlackPiecesPos();

  /**
   * Retrieves a copy of the board representation.
   *
   * @return copy of the board
   */
  BoardRepresentation getCopy();

  /**
   * Retrieves a list corresponding to the legal moves from the given position.
   *
   * @param pos Position to get the moves from.
   * @return List of available moves from pos.
   */
  List<Move> getAvailableMoves(Position pos);

  /**
   * Retrieves a list corresponding to the legal moves from the given (x,y) position.
   *
   * @param x x-coordinate of the position
   * @param y y-coordinate of the position
   * @param kingReachable true if the king is reachable, false otherwise.
   * @return List of available moves from pos.
   */
  List<Move> getAvailableMoves(int x, int y, boolean kingReachable);

  /**
   * Retrieves all available moves for a given player.
   *
   * @param isWhite true if the player is white, false otherwise.
   * @return list of move for a player.
   */
  List<Move> getAllAvailableMoves(boolean isWhite);

  /**
   * Boolean to indicate whether the square at (x,y) is attacked by the player of the given color.
   *
   * @param x x-coordinate of the piece.
   * @param y y-coordinate of the piece.
   * @param by white if the current player is black, Black otherwise.
   * @return true if (x,y) is attacked by color by.
   */
  boolean isAttacked(int x, int y, Color by);

  /**
   * Boolean to indicate whether the king of the given color is in check.
   *
   * @param color white or black
   * @return true if the king of the given color is in check
   */
  boolean isCheck(Color color);

  /**
   * Checks if a given move would put the king of the given color in check.
   *
   * @param color The color of the player.
   * @param move The move to evaluate.
   * @return true if the move results in check, false otherwise.
   */
  boolean isCheckAfterMove(Color color, Move move);

  /**
   * Boolean to indicate whether the king of the given color is checkmate.
   *
   * @param color white or black
   * @return true if the king of the given color is checkmate
   */
  boolean isCheckMate(Color color);

  /**
   * Determines if the game is in a stalemate state for the given player.
   *
   * @param color The color of the player.
   * @param colorTurnToPlay The color of the player whose turn it is.
   * @return true if the game is in stalemate, false otherwise.
   */
  boolean isStaleMate(Color color, Color colorTurnToPlay);

  /**
   * Boolean to indicate if the game ends in a draw because there is not enough material to mate.
   *
   * @return true if the material is insufficient to mate, false otherwise.
   */
  boolean isDrawByInsufficientMaterial();

  /**
   * Determines if a pawn at the given position can be promoted.
   *
   * @param x x-coordinate of the pawn.
   * @param y y-coordinate of the pawn.
   * @param white true if the pawn is white, false if black.
   * @return true if the pawn is at a promotion rank, false otherwise.
   */
  boolean isPawnPromoting(int x, int y, boolean white);

  /**
   * Determines if the given moves leads to a pawn promotion.
   *
   * @param xSource x-coordinate of the pawn.
   * @param ySource y-coordinate of the pawn.
   * @param xDest x-coordinate of the pawn after the move.
   * @param yDest y-coordinate of the pawn after the move.
   * @param isWhite true if the pawn is white, false if black.
   * @return true if the move is a pawn promotion, false otherwise
   */
  boolean isPromotionMove(int xSource, int ySource, int xDest, int yDest, boolean isWhite);

  /**
   * Promotes a pawn at the given position to a new piece if it is possible.
   *
   * @param x x-coordinate of the pawn.
   * @param y y-coordinate of the pawn.
   * @param white true if the pawn is white, false if black.
   * @param newPiece The piece to promote to.
   */
  void promotePawn(int x, int y, boolean white, Piece newPiece);

  /**
   * Checks if a double pawn push is possible for the given move.
   *
   * @param move The move to check.
   * @param white true if checking for a white pawn, false for a black pawn.
   * @return true if the double push is possible, false otherwise.
   */
  boolean isDoublePushPossible(Move move, boolean white);

  /**
   * Checks if an en passant capture is possible at the given position.
   *
   * @param x x-coordinate of the target position.
   * @param y y-coordinate of the target position.
   * @param move The move being evaluated.
   * @param white true if checking for a white pawn, false for a black pawn.
   * @return true if the move is an en passant capture, false otherwise.
   */
  boolean isEnPassant(int x, int y, Move move, boolean white);

  /**
   * Determines whether a player has enough material to mate the opponent.
   *
   * @param white true if checking for the white player, false for black.
   * @return true if the player has enough material to mate, false otherwise.
   */
  boolean hasEnoughMaterialToMate(boolean white);

  /**
   * Applies a short castle (kingside castling) for the given player.
   *
   * @param color The color of the player performing castling.
   */
  void applyShortCastle(Color color);

  /**
   * Applies a long castle (queenside castling) for the given player.
   *
   * @param color The color of the player performing castling.
   */
  void applyLongCastle(Color color);

  /**
   * Checks if castling is possible for the given player.
   *
   * @param color The color of the player.
   * @param shortCastle true for short castling, false for long castling.
   * @return true if the player can castle, false otherwise.
   */
  boolean canCastle(Color color, boolean shortCastle);

  /**
   * Checks whether a given move is a castling move.
   *
   * @param coloredPiece The piece being moved.
   * @param source The starting position of the piece.
   * @param dest The destination position of the piece.
   * @return true if the move is a castling move, false otherwise.
   */
  boolean isCastleMove(ColoredPiece coloredPiece, Position source, Position dest);

  /**
   * Determines if the game is in the endgame phase based on the turn count.
   *
   * @param fullTurn The number of full turns that have passed.
   * @param white true if checking for the white player, false for black.
   * @return true if the game is in the endgame phase, false otherwise.
   */
  boolean isEndGamePhase(int fullTurn, boolean white);

  /**
   * Boolean to indicate whether the specified piece belongs to the current player.
   *
   * @param white true if the player is white, false for black.
   * @param sourcePosition The position of the piece being validated.
   * @return true if the piece belongs to the player, false otherwise.
   */
  boolean validatePieceOwnership(boolean white, Position sourcePosition);

  /**
   * Gets the current player.
   *
   * @return true if it is white's turn, false if it is black's turn.
   */
  boolean getPlayer();

  /**
   * Sets the current player.
   *
   * @param isWhite true if setting the player to white, false for black.
   */
  void setPlayer(boolean isWhite);

  /**
   * Retrieves the position of the en passant target square.
   *
   * @return The position of the en passant target, or null if none.
   */
  Position getEnPassantPos();

  /**
   * Sets the position of the en passant target square.
   *
   * @param enPassantPos The new en passant target position.
   */
  void setEnPassantPos(Position enPassantPos);

  /**
   * Checks if the last move was a double pawn push.
   *
   * @return true if the last move was a double push, false otherwise.
   */
  boolean isLastMoveDoublePush();

  /**
   * Sets whether the last move was a double pawn push.
   *
   * @param lastMoveDoublePush true if the last move was a double push, false otherwise.
   */
  void setLastMoveDoublePush(boolean lastMoveDoublePush);

  /**
   * Checks if white still has the right to castle kingside.
   *
   * @return true if white can castle kingside, false otherwise.
   */
  boolean isWhiteShortCastle();

  /**
   * Sets whether white still has the right to castle kingside.
   *
   * @param whiteShortCastle true if white can castle kingside, false otherwise.
   */
  void setWhiteShortCastle(boolean whiteShortCastle);

  /**
   * Checks if black still has the right to castle kingside.
   *
   * @return true if black can castle kingside, false otherwise.
   */
  boolean isBlackShortCastle();

  /**
   * Sets whether black still has the right to castle kingside.
   *
   * @param blackShortCastle true if black can castle kingside, false otherwise.
   */
  void setBlackShortCastle(boolean blackShortCastle);

  /**
   * Checks if white still has the right to castle queenside.
   *
   * @return true if white can castle queenside, false otherwise.
   */
  boolean isWhiteLongCastle();

  /**
   * Sets whether white still has the right to castle queenside.
   *
   * @param whiteLongCastle true if white can castle queenside, false otherwise.
   */
  void setWhiteLongCastle(boolean whiteLongCastle);

  /**
   * Checks if black still has the right to castle queenside.
   *
   * @return true if black can castle queenside, false otherwise.
   */
  boolean isBlackLongCastle();

  /**
   * Sets whether black still has the right to castle queenside.
   *
   * @param blackLongCastle true if black can castle queenside, false otherwise.
   */
  void setBlackLongCastle(boolean blackLongCastle);

  /**
   * Checks if the last move was an en passant capture.
   *
   * @return true if the last move was en passant, false otherwise.
   */
  boolean isEnPassantTake();

  /**
   * Sets whether the last move was an en passant capture.
   *
   * @param enPassantTake true if the last move was en passant, false otherwise.
   */
  void setEnPassantTake(boolean enPassantTake);

  /**
   * Retrieves the number of moves made without a pawn move or a capture.
   *
   * @return The count of half-moves since the last pawn move or capture.
   */
  int getNbMovesWithNoCaptureOrPawn();

  /**
   * Retrieves the number of full moves made without a pawn move or a capture.
   *
   * @return The count of full moves since the last pawn move or capture.
   */
  int getNbFullMovesWithNoCaptureOrPawn();

  /**
   * Sets the number of half-moves made without a pawn move or a capture.
   *
   * @param newVal The new value to set.
   */
  void setNbMovesWithNoCaptureOrPawn(int newVal);

  /**
   * Applies a move to the board.
   *
   * @param move The move to apply.
   */
  void makeMove(Move move);

  /**
   * Retrieves an ASCII representation of the board.
   *
   * @return A 2D character array representing the board state.
   */
  char[][] getAsciiRepresentation();

  /**
   * Applies a castling move for the given player.
   *
   * @param color The color of the player.
   * @param shortCastle true for kingside castling, false for queenside castling.
   */
  void applyCastle(Color color, boolean shortCastle);

  /**
   * Retrieves the castling rights for both players.
   *
   * @return A boolean array where: - Index 0: White kingside castling right. - Index 1: White
   *     queenside castling right. - Index 2: Black kingside castling right. - Index 3: Black
   *     queenside castling right.
   */
  boolean[] getCastlingRights();
}
