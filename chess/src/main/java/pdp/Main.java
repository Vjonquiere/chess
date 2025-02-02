package pdp;

import pdp.Model.Bitboard;

public class Main {

  public static String returnsA() {
    return "A";
  }

  public static void main(String[] args) {

    System.out.println("Hello world!");
    Bitboard bitboard = new Bitboard();
    bitboard.setBit(0);
    bitboard.setBit(8);
    System.out.println(bitboard);
    System.out.println(bitboard.moveUp().moveUp().moveUp().moveUp().moveUp());
  }
}
