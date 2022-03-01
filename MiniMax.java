// London Bielicke
// Artificial Intelligence
// Connect Four!

//I have not given nor recieved unauthorized aid on this program

import java.util.*;

public class MiniMax {
  Board board;
  Player p = Player.MAX;
  int rows;
  int cols;
  int numInARow;
  Scanner keyboard = new Scanner(System.in);
  int prunings = 0;
  HashMap<Board, MiniMaxInfo> table = new HashMap<Board, MiniMaxInfo>(); //transposition table

  // constructor
  public MiniMax(int r, int c, int n, int first, Scanner s) {
    rows = r;
    cols = c;
    numInARow = n;
    keyboard = s;
    board = new Board(r,c,n);
    if (first == 2)
      p = Player.MIN;
  }

  // is-terminal: short function determines if
  // there are any possible plays left
  private boolean isTerminal(Board b) {
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

    for (int i = 2; i < b.getConsecNeeded(); i++) {
      for (int r = 0; r < b.getRows(); r++) {
          for (int c = 0; c < b.getCols(); c++) {
              if (b.getCurrPosVal(r,c) == 0) {
                  continue;
              }

              if ((c <= b.getCols() - i && b.numInARow(r, c, i))
                      || (r <= b.getRows() - i && b.numInAColumn(r, c, i))
                      || (r <= b.getRows() - i && c <= b.getCols() - i && b.numInANorthEastDiagonal(r, c, i))
                      || (r <= b.getRows() - i && c - i >= -1 && b.numInANorthWestDiagonal(r, c, i))) {
                  if (b.getCurrPosVal(r,c) == -1) points -= (5*i);
                  else if (b.getCurrPosVal(r,c) == 1) points += (5*i);
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
  private MiniMaxInfo minimax(Board state, HashMap<Board,MiniMaxInfo> table) {
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
  private int max(int a, int b) {
    if (a > b)
      return a;
    else
      return b;
  }

  // min function returns min of 2 numbers
  private int min(int a, int b) {
    if (a < b)
      return a;
    else
      return b;
  }

  // same as minimax function, but with alphabeta pruning
  private MiniMaxInfo alphabeta(Board state, int alpha, int beta, HashMap<Board,MiniMaxInfo> table) {
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
          prunings+=1;
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
          prunings+=1;
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
  private MiniMaxInfo alphabetaWithHeuristic(Board state, int alpha, int beta, HashMap<Board,MiniMaxInfo> table, int depth) {
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

  // play against regular minimax AI
  public void playMinimax() {
    table = new HashMap<Board, MiniMaxInfo>(); //transposition table
    board = new Board(rows,cols,numInARow);
    MiniMaxInfo info = minimax(board, table);
    System.out.println("transposition table has " + table.size() + " states.");
    if (info.v == 0) System.out.println("Neither player is guarunteed a win; game will end in tie with perfect play on both sides");
    else if (info.v > 0) System.out.println("First player guarunteed a win with perfect play.");
    else System.out.println("Second player is guarunteed a win with perfect play.");
    System.out.println("guarunteed outcome is " + info.v);
    System.out.println(board.to2DString());
    System.out.println("MiniMax value for this state: " + info.v + ", optimal move: " + info.a);
    System.out.println("It's MAX\'s turn!");
    while (!isTerminal(board)) {
      int col;
      if (board.getPlayerToMoveNext() == p) {
        System.out.print("Enter move: ");
        col = keyboard.nextInt();
        System.out.println();
      }
      else {
        col = table.get(board).a;
        System.out.println("Computer chooses move: " + col + "\n");
      }
      board = board.makeMove(col);
      if (!isTerminal(board)) {
        System.out.println(board.to2DString());
        System.out.println("MiniMax value for this state: " + table.get(board).v + ", optimal move: " + table.get(board).a);
        System.out.println("It's " + board.getPlayerToMoveNext().toString() + "'s turn! \n");
      }
    }
    System.out.println("Game Over!");
    System.out.println(board.to2DString());
    if (board.getGameState() == GameState.TIE)
      System.out.println("The result is a tie!");
    else
      System.out.println("This winner is " + board.getWinner().toString());
  }

    // play against regular alphabeta AI
    public void playAlphabeta() {
      prunings = 0;
      table = new HashMap<Board, MiniMaxInfo>(); //transposition table
      MiniMaxInfo info = alphabeta(board, -999999, 999999, table);
      board = new Board(rows,cols,numInARow);
      System.out.println("transposition table has " + table.size() + " states and was pruned " + prunings + " times.");
      if (info.v == 0) System.out.println("Neither player is guarunteed a win; game will end in tie with perfect play on both sides");
      else if (info.v > 0) System.out.println("First player guarunteed a win with perfect play.");
      else System.out.println("Second player is guarunteed a win with perfect play.");
      System.out.println("guarunteed outcome is " + info.v);
      System.out.println(board.to2DString());
      System.out.println("MiniMax value for this state: " + info.v + ", optimal move: " + info.a);
      System.out.println("It's MAX\'s turn!");
      while (!isTerminal(board)) {
        int col;
        if (board.getPlayerToMoveNext() == p) {
          System.out.print("Enter move: ");
          col = keyboard.nextInt();
          System.out.println();
        }
        else {
          if (!table.containsKey(board)) {
            alphabeta(board, -999999, 999999, table);
          }
          col = table.get(board).a;
          System.out.println("Computer chooses move: " + col + "\n");
        }
        board = board.makeMove(col);
        if (!isTerminal(board)) {
          if (!table.containsKey(board)) {
            alphabeta(board, -999999, 999999, table);
          }
          System.out.println(board.to2DString());
          System.out.println("MiniMax value for this state: " + table.get(board).v + ", optimal move: " + table.get(board).a);
          System.out.println("It's " + board.getPlayerToMoveNext().toString() + "'s turn! \n");
        }
      }
      System.out.println("Game Over!");
      System.out.println(board.to2DString());
      if (board.getGameState() == GameState.TIE)
        System.out.println("The result is a tie!");
      else
        System.out.println("This winner is " + board.getWinner().toString());
    }

    // play against alphabeta AI with heuristic
    public void playAlphabetaWithHeuristic() {
      table = new HashMap<Board, MiniMaxInfo>(); //transposition table
      board = new Board(rows,cols,numInARow);
      MiniMaxInfo info;
      while (!isTerminal(board)) {
        int col;
        info = alphabetaWithHeuristic(board, -99999, 99999, table, 6);
        if (!isTerminal(board)) {
          System.out.println(board.to2DString());
          System.out.println("transposition table has " + table.size() + " states");
          System.out.println("Heuristic value for this state: " + info.v + ", optimal move: " + info.a);
          System.out.println("It's " + board.getPlayerToMoveNext().toString() + "'s turn! \n");
        }
        if (board.getPlayerToMoveNext() == p) {
          System.out.print("Enter move: ");
          col = keyboard.nextInt();
          System.out.println();
        }
        else {
          col = info.a;
          System.out.println("Computer chooses move: " + col + "\n");
        }
        board = board.makeMove(col);
      }
      System.out.println("Game Over!");
      System.out.println(board.to2DString());
      if (board.getGameState() == GameState.TIE)
        System.out.println("The result is a tie!");
      else
        System.out.println("This winner is " + board.getWinner().toString());
    }

}
