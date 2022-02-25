// London Bielicke
// Artificial Intelligence
// Connect Four!

import java.util.*;

public class MiniMax {

  // play against regular minimax AI
  static void playMinimax(Board board, Player p, Scanner keyboard) {
    HashMap<Board, MiniMaxInfo> table = new HashMap<Board, MiniMaxInfo>(); //transposition table
    MiniMaxInfo info = minimax(board, table);

    while (!isTerminal(board)) {
      int col;
      if (board.getPlayerToMoveNext() == p) {
        System.out.println("it is your turn: ");
        col = keyboard.nextInt();
        System.out.println("board after your turn: ");
      }
      else {
        col = table.get(board).a;
        System.out.println("board after AI turn: ");
      }
      board = board.makeMove(col);
      System.out.println(board.to2DString());
    }
    System.out.println("Game Over");
  }

    // play against regular alphabeta AI
    static void playAlphabeta(Board board, Player p, Scanner keyboard) {
        HashMap<Board, MiniMaxInfo> table = new HashMap<Board, MiniMaxInfo>(); //transposition table
        MiniMaxInfo info = alphabeta(board, -999999, 999999, table);

        while (!isTerminal(board)) {
          int col;
          if (board.getPlayerToMoveNext() == p) {
            System.out.println("it is your turn: ");
            col = keyboard.nextInt();
            System.out.println("board after your turn: ");
          }
          else {
            if (!table.containsKey(board)) {
              alphabeta(board, -999999, 999999, table);
            }
            col = table.get(board).a;
            System.out.println("board after AI turn: ");
          }
          board = board.makeMove(col);
          System.out.println(board.to2DString());
        }
        System.out.println("Game Over");
    }

    // play against alphabeta AI with heuristic
    static void playAlphabetaWithHeuristic(Board board, Player p, Scanner keyboard) {
        HashMap<Board, MiniMaxInfo> table = new HashMap<Board, MiniMaxInfo>(); //transposition table

        while (!isTerminal(board)) {
          int col;
          if (board.getPlayerToMoveNext() == p) {
            System.out.println("it is your turn: ");
            col = keyboard.nextInt();
            System.out.println("board after your turn: ");
          }
          else {
            col = alphabetaWithHeuristic(board, -99999, 99999, new HashMap<Board, MiniMaxInfo>(), 3).a;
            System.out.println("board after AI turn: ");
          }
          board = board.makeMove(col);
          System.out.println(board.to2DString());
        }
        System.out.println("Game Over");
    }

  // driver function
  public static void main(String args[]) {
    Scanner s = new Scanner(System.in);
    System.out.print("Run Part A, B, or C? ");
    String alg = s.nextLine();
    System.out.print("Include debugging info? (y/n) ");
    String debug = s.nextLine();
    System.out.print("Enter rows: ");
    int r = s.nextInt();
    System.out.print("Enter columns: ");
    int c = s.nextInt();
    System.out.print("Enter number in a row to win: ");
    int n = s.nextInt();
    System.out.print("Who plays first? 1=human, 2=computer: ");
    int first = s.nextInt();
    Board board = new Board(r, c, n); // standard connect 4 size

    Player p = Player.MAX;
    if (first == 2)
      p = Player.MIN;

    if (alg.equals("A"))
      playMinimax(board, p, s);
    if (alg.equals("B"))
      playAlphabeta(board, p, s);
    if (alg.equals("C"))
      playAlphabetaWithHeuristic(board, p, s);
    System.out.print("Play again? (y/n)");
    String again = s.nextLine();
    if (again.equals("n")) {
      play = false;
    }
    s.close();
  }

  // is-terminal: short function determines if
  // there are any possible plays left
  private static boolean isTerminal(Board b) {
    // anything other than in-progress is terminal
    if (b.getGameState() == GameState.IN_PROGRESS)
      return false;
    return true;
  }

  // utility: compute utility of given state
  private static int utility(Board b) {
    int util = (int) (10000.0 * b.getRows() * b.getCols() / b.getNumberOfMoves());
    if (b.getGameState() == GameState.MAX_WIN)
      return util;
    else if (b.getGameState() == GameState.MIN_WIN)
      return util * -1;
    return 0;
  }

  // heuristic: guesses utility without reaching terminal node
  private static int heuristic(Board b) {
    int v = b.getPlayerToMoveNext().getNumber();
    int points = 0;

    for (int i = 2; i < b.getConsecNeeded() - 1; i++) {
      for (int r = 0; r < b.getRows(); r++) {
          for (int c = 0; c < b.getCols(); c++) {
              if (b.getCurrPosVal(r,c) == 0) {
                  continue;
              }

              if ((c <= b.getCols() - i && b.numInARow(r, c, i))
                      || (r <= b.getRows() - i && b.numInAColumn(r, c, i))
                      || (r <= b.getRows() - i && c <= b.getCols() - i && b.numInANorthEastDiagonal(r, c, i))
                      || (r <= b.getRows() - i && c - i >= -1 && b.numInANorthWestDiagonal(r, c, i))) {
                  if (b.getCurrPosVal(r,c) == v) {
                      points += (100/i);
                  } else {
                      points -= (100/i);
                  }
              }
          }
      }
    }

    return points;
  }

  // legal-moves: a move is legal if the column is not empty
  private static ArrayList<Integer> legalMoves(Board state) {
    ArrayList<Integer> legal = new ArrayList<Integer>(); // represent action as an int
    for(int i = 0; i < state.getCols(); i++) {
      if (!state.isColumnFull(i)) {
        legal.add(i);
      }
    }
    return legal;
  }

  /* minimax algorithm: takes state and empty table
   * returns Minimax info (recursive function)
   * runs entire minimax algorithm on given state without pruning
  */
  static MiniMaxInfo minimax(Board state, HashMap<Board,MiniMaxInfo> table) {
    int util; // utility value of given state
    MiniMaxInfo info = new MiniMaxInfo(0, -1); // defualt tie and terminal state
    List<Integer> actions = legalMoves(state);

    // if we have already seen state, return stored info
    if (table.containsKey(state))
      return table.get(state);

    // if state is terminal, return node value and add to table
    else if (isTerminal(state)) {
      util = utility(state);
      info = new MiniMaxInfo(util, -1);
      table.put(state, info);
      return info;
    }

    // if next player is max, iterate through each possible action
    else if (state.getPlayerToMoveNext() == Player.MAX) {
      info.v = -99999; // since we are trying to maximize
      info.a = -1; // defual null action
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i)); // new board represents child state
        MiniMaxInfo childInfo = minimax(childState, table); // recurse down to child state
        // if child utility is better than current utility,
        // then current action is better
        if (childInfo.v > info.v) {
          info.v = childInfo.v;
          info.a = actions.get(i);
        }
      }
      // add given state with best action to table
      table.put(state, info);
      return info;
    }
    // if player is min, iterate through each possible action
    else {
      info.v = 99999; // since we are trying to minimize
      info.a = -1; //defualt null action
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i)); // new board represents child state
        MiniMaxInfo childInfo = minimax(childState, table); // recurse down to child state
        // if child utility is better than current utility,
        // then current action is better
        if (childInfo.v < info.v) {
          info.v = childInfo.v;
          info.a = actions.get(i);
        }
      }
      // add given state with best action to table
      table.put(state, info);
      return info;
    }
  }

  // max function returns max of 2 numbers
  static private int max(int a, int b) {
    if (a > b)
      return a;
    else
      return b;
  }

  // min function returns min of 2 numbers
  static private int min(int a, int b) {
    if (a < b)
      return a;
    else
      return b;
  }

  // same as minimax function, but with alphabeta pruning
  static MiniMaxInfo alphabeta(Board state, int alpha, int beta, HashMap<Board,MiniMaxInfo> table) {
    int util;
    MiniMaxInfo info = new MiniMaxInfo(0, -1);
    List<Integer> actions = legalMoves(state);

    if (table.containsKey(state))
      return table.get(state);

    else if (isTerminal(state)) {
      util = utility(state);
      info = new MiniMaxInfo(util, -1);
      table.put(state, info);
      return info;
    }
    else if (state.getPlayerToMoveNext() == Player.MAX) {
      info.v = -99999;
      info.a = -1;
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i));
        MiniMaxInfo childInfo = alphabeta(childState, alpha, beta, table);
        if (childInfo.v > info.v) {
          info.v = childInfo.v;
          alpha = max(alpha, info.v); // set alpha to highest number found so far
          info.a = actions.get(i);
        }
        // if the info of max is more than the smallest value
        // of another node, then we know MIN will not choose this
        // action, so we can prune the rest of the tree
        if (info.v >= beta) {
          return info;
        }
      }
      table.put(state, info);
      return info;
    }

    else {
      info.v = 99999;
      info.a = -1;
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i));
        MiniMaxInfo childInfo = alphabeta(childState, alpha, beta, table);
        if (childInfo.v < info.v) {
          info.v = childInfo.v;
          beta = min(beta, info.v);
          info.a = actions.get(i);
        }
        // if the info of min is less than the largest value
        // of another child node, then we know MAX will not choose this
        // action, so we can prune the rest of the tree
        if(info.v <= alpha) {
          return info;
        }
      }
      table.put(state, info);
      return info;
    }
  }

  // same as alpha beta, but add cutoff that
  // acts as terminal state, but instead of
  // computing util, we evalute with a heuristic
  static MiniMaxInfo alphabetaWithHeuristic(Board state, int alpha, int beta, HashMap<Board,MiniMaxInfo> table, int depth) {
    int util;
    MiniMaxInfo info = new MiniMaxInfo(0, -1);
    List<Integer> actions = legalMoves(state);

    if (table.containsKey(state))
      return table.get(state);

    else if (isTerminal(state)) {
      util = utility(state);
      info = new MiniMaxInfo(util, -1);
      table.put(state, info);
      return info;
    }

    // where the code differs:
    // if we have reached cutoff, then
    // evalute heuristic of current state
    // and return the value of it
    else if (depth <= 0) {
      util = heuristic(state);
      info = new MiniMaxInfo(util, -1);
      return info;
    }

    else if (state.getPlayerToMoveNext() == Player.MAX) {
      info.v = -99999;
      info.a = -1;
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i));
        MiniMaxInfo childInfo = alphabetaWithHeuristic(childState, alpha, beta, table, depth-1);
        if (childInfo.v > info.v) {
          info.v = childInfo.v;
          alpha = max(alpha, info.v);
          info.a = actions.get(i);
        }
        if (info.v >= beta) {
          table.put(state, info);
          return info;
        }
      }
      table.put(state, info);
      return info;
    }

    else {
      info.v = 99999;
      info.a = -1;
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i));
        MiniMaxInfo childInfo = alphabetaWithHeuristic(childState, alpha, beta, table, depth-1);
        if (childInfo.v < info.v) {
          info.v = childInfo.v;
          beta = min(beta, info.v);
          info.a = actions.get(i);
        }
        if(info.v < alpha) {
          table.put(state, info);
          return info;
        }
      }
      table.put(state, info);
      return info;
    }
  }

}
