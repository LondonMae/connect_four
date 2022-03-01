// London Bielicke
// Artificial Intelligence
// Connect Four!

import java.util.*;

public class ConnectFour {

  public static void main(String args[]) {
    //gather info about player's desired game
    Scanner s = new Scanner(System.in);
    System.out.print("Run Part A, B, or C? ");
    String alg = s.nextLine();
    System.out.print("Include debugging info? (y/n) ");
    String debug = s.nextLine();
    System.out.print("Enter rows: ");
    int r = Integer.parseInt(s.nextLine());
    System.out.print("Enter columns: ");
    int c = Integer.parseInt(s.nextLine());
    System.out.print("Enter number in a row to win: ");
    int n = Integer.parseInt(s.nextLine());
    System.out.print("Who plays first? 1=human, 2=computer: ");
    int first = Integer.parseInt(s.nextLine());

    MiniMax game = new MiniMax(r,c,n,first,s);

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
      String play = s.nextLine();
      if (!play.equals("y"))
        break;
    }

    s.close();
  }
}
