// London Bielicke
// Artificial Intelligence
// Connect Four!

//I have not given nor recieved unauthorized aid on this program

import java.util.*;

public class MiniMax {
  Board board; // current state
  Player p = Player.MAX; //which player is user
  boolean debug;
  //board data
  int rows;
  int cols;
  int numInARow;
  Scanner keyboard = new Scanner(System.in); //get user input
  int prunings = 0; // store num prunings
  HashMap<Board, MiniMaxInfo> table = new HashMap<Board, MiniMaxInfo>(); //transposition table

  // constructor
  public MiniMax(int r, int c, int n, int first, Scanner s, boolean debug) {
    rows = r;
    cols = c;
    numInARow = n;
    keyboard = s;
    this.debug = debug;
    board = new Board(r,c,n); //create board using input
    if (first == 2)
      p = Player.MIN; //if first player is computer, then MIN is user
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
    // accumulate points
    int points = 0;
    //start checking for more than 1 in a row
    for (int i = 2; i < b.getConsecNeeded(); i++) {
      // iterate through every boared entry
      for (int r = 0; r < b.getRows(); r++) {
          // if entry is 0, continue
          for (int c = 0; c < b.getCols(); c++) {
              if (b.getCurrPosVal(r,c) == 0) {
                  continue;
              }
              // if i-many pieces in a row
              if ((c <= b.getCols() - i && b.numInARow(r, c, i))
                      || (r <= b.getRows() - i && b.numInAColumn(r, c, i))
                      || (r <= b.getRows() - i && c <= b.getCols() - i && b.numInANorthEastDiagonal(r, c, i))
                      || (r <= b.getRows() - i && c - i >= -1 && b.numInANorthWestDiagonal(r, c, i))) {
                  // subtract from total if benefits min
                  if (b.getCurrPosVal(r,c) == -1) points -= (5*i);
                  // add to total if benefits max
                  else if (b.getCurrPosVal(r,c) == 1) points += (5*i);
              }
          }
      }
    }
    // return heuristic value
    return points;
  }

  // legal-moves: a move is legal if the column is not empty
  private static ArrayList<Integer> legalMoves(Board state) {
    ArrayList<Integer> legal = new ArrayList<Integer>(); // represent action as an int
    // move is legal if column is not full
    for(int i = 0; i < state.getCols(); i++) {
      if (!state.isColumnFull(i)) {
        legal.add(i);
      }
    }
    // return list of legal actions
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
    int util; // utility value of current state
    MiniMaxInfo info = new MiniMaxInfo(0, -1); // defualt tie and terminal state
    List<Integer> actions = legalMoves(state); // get list of legal actions

    // if we have already seen this state, we already know info
    if (table.containsKey(state))
      return table.get(state);

    // if state is terminal, create and return state util and action
    else if (isTerminal(state)) {
      util = utility(state);
      info = new MiniMaxInfo(util, -1);
      table.put(state, info);
      return info;
    }

    // if next player is MAX
    else if (state.getPlayerToMoveNext() == Player.MAX) {
      info.v = -99999; // we are trying to maximize
      info.a = -1; // haven't found best action yet

      // for every legal action, recurse down tree
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i));
        MiniMaxInfo childInfo = alphabeta(childState, alpha, beta, table);
        // if child node is a better move, replace info
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
      // only add to table if best move and not pruned
      table.put(state, info);
      return info;
    }

    // if next player is MIN
    else {
      info.v = 99999; // trying to minimize v
      info.a = -1; // no best action found yet

      // for every legal action, recurse down tree
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i));
        MiniMaxInfo childInfo = alphabeta(childState, alpha, beta, table);

        // if better action foundm replace info
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
      // only add to table if best move and not pruned
      table.put(state, info);
      return info;
    }
  }

  // same as alpha beta, but add cutoff that
  // acts as terminal state, but instead of
  // computing util, we evalute with a heuristic
  private MiniMaxInfo alphabetaWithHeuristic(Board state, int alpha, int beta, HashMap<Board,MiniMaxInfo> table, int depth) {
    int util; // utility value for current state
    MiniMaxInfo info = new MiniMaxInfo(0, -1); // assume tie and terminal state
    List<Integer> actions = legalMoves(state); // get list of legal moves

    // if we have already encountered this state we don't need to run alg
    if (table.containsKey(state))
      return table.get(state);

    // if state is terminal, then calculate info (leaf node)
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
    else if (depth == 0) {
      util = heuristic(state);
      info = new MiniMaxInfo(util, -1);
      table.put(state, info);
      return info;
    }


    // if the next player is MAX
    else if (state.getPlayerToMoveNext() == Player.MAX) {
      info.v = -99999; // trying to maximize v
      info.a = -1; // no best action found yet

      //for every legal action recurse down tree
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i));
        MiniMaxInfo childInfo = alphabetaWithHeuristic(childState, alpha, beta, table, depth-1);

        // if we find a better move, replace info
        if (childInfo.v > info.v) {
          info.v = childInfo.v;
          alpha = max(alpha, info.v);
          info.a = actions.get(i);
        }

        // if MIN would not pick this move, prune
        if (info.v >= beta) {
          prunings+=1;
          return info;
        }
      }
      // only add to table if best move and not pruned
      table.put(state, info);
      return info;
    }


    // if next player is MIN
    else {
      info.v = 99999; // trying to minimize v
      info.a = -1; // no best action found yet

      //for every legal action, recurse down tree
      for (int i = 0; i < actions.size(); i++) {
        Board childState = new Board(state, actions.get(i));
        MiniMaxInfo childInfo = alphabetaWithHeuristic(childState, alpha, beta, table, depth-1);

        //if better move found, replace info
        if (childInfo.v < info.v) {
          info.v = childInfo.v;
          beta = min(beta, info.v);
          info.a = actions.get(i);
        }

        // if max would not choose this play, prune
        if(info.v <= alpha) {
          prunings += 1;
          return info;
        }
      }
      //only add to table if best move and not pruned
      table.put(state, info);
      return info;
    }
  }

  // play against regular minimax AI
  public void playMinimax() {
    table = new HashMap<Board, MiniMaxInfo>(); //transposition table
    board = new Board(rows,cols,numInARow); // gameboard
    MiniMaxInfo info = minimax(board, table); // gametree info

    //print init move info
    System.out.println("transposition table has " + table.size() + " states ");
    if (info.v == 0) System.out.println("Neither player is guarunteed a win; game will end in tie with perfect play on both sides");
    else if (info.v > 0) System.out.println("First player guarunteed a win with perfect play.");
    else System.out.println("Second player is guarunteed a win with perfect play.");
    System.out.println(board.to2DString());
    System.out.println("MiniMax value for this state: " + info.v + ", optimal move: " + info.a);
    System.out.println("It's MAX\'s turn!"); // MAX always plays First

    // while game not over
    while (!isTerminal(board)) {
      int col; // next move

      // if player to move next is the user, let them pick an action
      if (board.getPlayerToMoveNext() == p) {
        System.out.print("Enter move: ");
        col = keyboard.nextInt();
        System.out.println();
      }
      // otherwise, computer makes next move using MiniMax table
      else {
        col = table.get(board).a;
        System.out.println("Computer chooses move: " + col + "\n");
      }

      // progress to next state
      board = board.makeMove(col);

      // if next move isn't terminal, print state info
      if (!isTerminal(board)) {
        System.out.println(board.to2DString());
        System.out.println("MiniMax value for this state: " + table.get(board).v + ", optimal move: " + table.get(board).a);
        System.out.println("It's " + board.getPlayerToMoveNext().toString() + "'s turn! \n");
      }
    }

    // Game over, print info
    System.out.println("Game Over!");
    System.out.println(board.to2DString());
    if (board.getGameState() == GameState.TIE)
      System.out.println("The result is a tie!");
    else
      System.out.println("This winner is " + board.getWinner().toString());
  }

    // play against regular alphabeta AI
    public void playAlphabeta() {
      prunings = 0; // store for printing info
      table = new HashMap<Board, MiniMaxInfo>(); //transposition table
      MiniMaxInfo info = alphabeta(board, -999999, 999999, table); // run initial alg
      board = new Board(rows,cols,numInARow); // create gameboard
      // print alg info
      System.out.println("transposition table has " + table.size() + " states and was pruned " + prunings + " times.");
      if (info.v == 0) System.out.println("Neither player is guarunteed a win; game will end in tie with perfect play on both sides");
      else if (info.v > 0) System.out.println("First player guarunteed a win with perfect play.");
      else System.out.println("Second player is guarunteed a win with perfect play.");
      System.out.println(board.to2DString());
      System.out.println("MiniMax value for this state: " + info.v + ", optimal move: " + info.a);
      System.out.println("It's MAX\'s turn!"); // MAX is always first

      //while game is not over, play game
      while (!isTerminal(board)) {
        int col; // next move

        //if next player is user, they pick next move
        if (board.getPlayerToMoveNext() == p) {
          System.out.print("Enter move: ");
          col = keyboard.nextInt();
          System.out.println();
        }
        // if next player is computer, choose move from alg
        else {
          // if we haven't seen this state, run alg again
          if (!table.containsKey(board)) {
            alphabeta(board, -999999, 999999, table);
          }
          col = table.get(board).a;
          System.out.println("Computer chooses move: " + col + "\n");
        }

        // progress game
        board = board.makeMove(col);

        // if new state is not terminal, print state info
        if (!isTerminal(board)) {
          if (!table.containsKey(board)) {
            alphabeta(board, -999999, 999999, table);
          }
          System.out.println(board.to2DString());
          System.out.println("MiniMax value for this state: " + table.get(board).v + ", optimal move: " + table.get(board).a);
          System.out.println("It's " + board.getPlayerToMoveNext().toString() + "'s turn! \n");
        }
      }

      // print ending-state info
      System.out.println("Game Over!");
      System.out.println(board.to2DString());
      if (board.getGameState() == GameState.TIE)
        System.out.println("The result is a tie!");
      else
        System.out.println("This winner is " + board.getWinner().toString());
    }

    // play against alphabeta AI with heuristic
    public void playAlphabetaWithHeuristic() {
      System.out.print("Number of moves to look ahead (depth): ");
      int depth = keyboard.nextInt(); // get depth from user
      prunings = 0; // store prunings for printing
      table = new HashMap<Board, MiniMaxInfo>(); //transposition table
      board = new Board(rows,cols,numInARow); // create game board
      MiniMaxInfo info;

      // while game is not over, play ga,e
      while (!isTerminal(board)) {
        int col; // next move
        table = new HashMap<Board, MiniMaxInfo>(); //transposition table
        //run alg with inputted depth
        info = alphabetaWithHeuristic(board, -99999, 99999, table, depth);

        // print state info
        System.out.println(board.to2DString());
        System.out.println("transposition table has " + table.size() + " states and was pruned " + prunings + " times.");
        System.out.println("Heuristic value for this state: " + info.v + ", optimal move: " + info.a);
        System.out.println("It's " + board.getPlayerToMoveNext().toString() + "'s turn! \n");

        // if next player is user, then user picks next move
        if (board.getPlayerToMoveNext() == p) {
          System.out.print("Enter move: ");
          col = keyboard.nextInt();
          System.out.println();
        }
        // if next player is computer, then use alg to pick action
        else {
          col = info.a;
          System.out.println("Computer chooses move: " + col + "\n");
        }

        //progress game
        board = board.makeMove(col);

      }

      // print ending-state info
      System.out.println("Game Over!");
      System.out.println(board.to2DString());
      if (board.getGameState() == GameState.TIE)
        System.out.println("The result is a tie!");
      else
        System.out.println("This winner is " + board.getWinner().toString());
    }

}
