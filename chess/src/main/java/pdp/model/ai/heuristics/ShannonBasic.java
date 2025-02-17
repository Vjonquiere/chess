package pdp.model.ai.heuristics;

/**
 * Basic Heuristic from Shannon (XXII. Programming a Computer for Playing Chess) f(P) = 200(K-K') +
 * 9(Q-Q') + 5(R-R') + 3(B-B'+N-N') + (P-P') - 0.5(D-D'+S-S'+I-I') + 0.1(M-M') + ... in which: -
 *
 * <ul>
 *   <li>(1)K,Q,R,B,B,P are the number of White kings, queens, rooks, bishops, knights and pawns on
 *       the board.
 *   <li>(2)D,S,I are doubled, backward and isolated White pawns.
 *   <li>(3)M= White mobility (measured, say, as the number of legal moves available to White).
 * </ul>
 *
 * <ul>
 *   <li>(1) --> Material
 *   <li>(2) --> BadPawnsHeuristic
 *   <li>(3) --> Mobility
 * </ul>
 */
public class ShannonBasic extends AbstractHeuristic {

  public ShannonBasic() {
    super.addHeuristic(new MobilityHeuristic());
    super.addHeuristic(new MaterialHeuristic());
    super.addHeuristic(new BadPawnsHeuristic());
  }
}
