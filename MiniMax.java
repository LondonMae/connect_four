import java.util.*;

public class MiniMax {

  public static void main(String args[]) {
    Board board = new Board(4, 4, 4); // standard connect 4 size
    HashMap<Board, MiniMaxInfo> table = new HashMap<Board, MiniMaxInfo>();
    // MiniMaxInfo info = minimax(board, table);
    // Scanner keyboard = new Scanner(System.in);
    //
    // while (!isTerminal(board)) {
    //   int col;
    //   if (board.getPlayerToMoveNext() == Player.MIN) {
    //     System.out.println("it is your turn: ");
    //     col = keyboard.nextInt();
    //     System.out.println("board after your turn: ");
    //   }
    //   else {
    //     col = table.get(board).a;
    //     System.out.println("board after AI turn: ");
    //   }
    //   board = board.makeMove(col);
    //   System.out.println(board.to2DString());
    // }
    // System.out.println("Game Over");

    table = new HashMap<Board, MiniMaxInfo>();
    MiniMaxInfo info = alphabeta(board, -9999, 9999, table);


    Scanner keyboard = new Scanner(System.in);

    while (!isTerminal(board)) {
      int col;
      if (board.getPlayerToMoveNext() == Player.MIN) {
        System.out.println("it is your turn: ");
        col = keyboard.nextInt();
        System.out.println("board after your turn: ");
      }
      else {
        if (!table.containsKey(board)) {
          alphabeta(board, -9999, 9999, table);
        }
        col = table.get(board).a;
        System.out.println("board after AI turn: ");
      }
      board = board.makeMove(col);
      System.out.println(board.to2DString());
    }
    System.out.println("Game Over");

  }


  private static boolean isTerminal(Board b) {
    if (b.getGameState() == GameState.IN_PROGRESS)
      return false;
    return true;
  }

  private static int utility(Board b) {
    if (b.getGameState() == GameState.MAX_WIN)
      return (int) (10000.0 * b.getRows() * b.getCols() / b.getNumberOfMoves());
    else if (b.getGameState() == GameState.MIN_WIN)
      return (int) -(10000.0 * b.getRows() * b.getCols() / b.getNumberOfMoves());
    return 0;
  }

  private static ArrayList<Integer> legalMoves(Board state) {
    ArrayList<Integer> legal = new ArrayList<Integer>();
    for(int i = 0; i < state.getCols(); i++) {
      if (!state.isColumnFull(i)) {
        legal.add(i);
      }
    }
    return legal;
  }

  static MiniMaxInfo minimax(Board state, HashMap<Board,MiniMaxInfo> table) {
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
        MiniMaxInfo childInfo = minimax(childState, table);
        if (childInfo.v > info.v) {
          info.v = childInfo.v;
          info.a = actions.get(i);
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
        MiniMaxInfo childInfo = minimax(childState, table);
        if (childInfo.v < info.v) {
          info.v = childInfo.v;
          info.a = actions.get(i);
        }
      }
      table.put(state, info);
      return info;
    }
  }

  static private int max(int a, int b) {
    if (a > b)
      return a;
    else
      return b;
  }

  static private int min(int a, int b) {
    if (a < b)
      return a;
    else
      return b;
  }

  static MiniMaxInfo alphabeta(Board state, int alpha, int beta, HashMap<Board,MiniMaxInfo> table) {
    System.out.println("running again");
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
        MiniMaxInfo childInfo = alphabeta(childState, alpha, beta, table);
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

  static MiniMaxInfo alphabetaWithHeuristic(Board state, int alpha, int beta, HashMap<Board,MiniMaxInfo> table) {
    System.out.println("running again");
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
        MiniMaxInfo childInfo = alphabeta(childState, alpha, beta, table);
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
