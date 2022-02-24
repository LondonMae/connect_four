import java.util.*;

public class MiniMax {

  public static void main(String args[]) {
    Board board = new Board(3, 3, 3); // standard connect 4 size
    MiniMaxInfo info = minimax(board);
    System.out.println(info.v);
  }

  static MiniMaxInfo minimax(Board b) {
    HashMap<Board, MiniMaxInfo> t = new HashMap<Board, MiniMaxInfo>();
    return minimaxHelper(b, t);
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

  static MiniMaxInfo minimaxHelper(Board state, HashMap<Board,MiniMaxInfo> table) {
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
        MiniMaxInfo childInfo = minimaxHelper(childState, table);
        if (childInfo.v > info.v) {
          info.v = childInfo.v;
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
        MiniMaxInfo childInfo = minimaxHelper(childState, table);
        if (childInfo.v < info.v) {
          info.v = childInfo.v;
        }
      }
      table.put(state, info);
      return info;
    }
  }
}
