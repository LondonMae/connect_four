import java.util.Scanner;

public class ConnectFour {

  static void twoPlayer(Board board, Scanner keyboard) {
    while (board.getGameState() != GameState.MIN_WIN && board.getGameState() != GameState.MAX_WIN) {
      System.out.println(board.getGameState());
      System.out.println("Board so far (press key corresponding to desired column):");
      System.out.println(board.to2DString());
      int col = keyboard.nextInt();
      board = board.makeMove(col);
    }
    System.out.println("GAME OVER");
    return;
  }

  public static void main(String[] args) {
    Board board = new Board(6, 7, 4); // standard connect 4 size
    Scanner keyboard = new Scanner(System.in);
    twoPlayer(board,keyboard);
  }
}
