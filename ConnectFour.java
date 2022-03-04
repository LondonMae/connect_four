// London Bielicke
// Artificial Intelligence
// Connect Four!

import java.util.*;

public class ConnectFour {

  public static void main(String args[]) {
    //gather info about player's desired game
    Scanner s = new Scanner(System.in);
    //which algorithm
    System.out.print("Run Part A, B, or C? ");
    String alg = s.nextLine().toUpperCase();
    // for debugging
    System.out.print("Include debugging info? (y/n) ");
    String debug = s.nextLine();
    boolean d = false;
    if (debug.equals("y")) d = true;
    // rows for game board
    System.out.print("Enter rows: ");
    int r = Integer.parseInt(s.nextLine());
    // columns for game board
    System.out.print("Enter columns: ");
    int c = Integer.parseInt(s.nextLine());
    // number in a row needed to win
    System.out.print("Enter number in a row to win: ");
    int n = Integer.parseInt(s.nextLine());
    // who plays first
    System.out.print("Who plays first? 1=human, 2=computer: ");
    int first = Integer.parseInt(s.nextLine());

    // create game board
    MiniMax game = new MiniMax(r,c,n,first,s, d);

    // keep playing until user terminates
    while (true) {
      if (alg.equals("A"))
        game.playMinimax();
      if (alg.equals("B"))
        game.playAlphabeta();
      if (alg.equals("C"))
        game.playAlphabetaWithHeuristic();

      // allows player to replay/close game
      System.out.print("do you want to continue playing? (y/n) ");
      s.nextLine();
      String play = s.nextLine().toLowerCase();
      if (!play.equals("y"))
        break;
    }

    s.close();
  }
}
