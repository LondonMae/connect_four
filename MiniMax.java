public class MiniMax {

  public static void main(String args[]) {
    Board board = new Board(6, 7, 4); // standard connect 4 size
    minimax(board);
  }

  static MiniMaxInfo minimax(Board b) {
    Map<State, MiniMaxInfo> t = new HashMap<State, MiniMaxInfo>();
    minimaxHelper(b, t);
  }

  private boolean isTerminal(Board b) {
    if (b.getGameState() == GameState.IN_PROGRESS)
      return false;
    return true;
  }

  private int utility(Board b) {
    return (int) (10000.0 * b.getRows() * b.getCols() / b.getNumberOfMoves());
  }

  private List<int> legalMoves(Board state) {
    List<int> legal = new List<int>();
    for(int i = 0; i < state.getCols; i++) {
      if (!isColumnFull(i)) {
        legal.add(i);
      }
    }
    return legal;
  }

  static MiniMaxInfo minimaxHelper(Board state, Map table) {
    int util;
    MiniMaxInfo info;
    List<int> actions = legalMoves(state);
    if (table.containsKey(state))
      return table[state];
    else if (isTerminal(state)) {
      util = utility(state);
      info = new MiniMaxInfo(util, -1);
      table[state] = info;
      return info;
    }
    else if (state.getPlayerToMoveNext() == Player.MAX) {
      info.v = -99999;
      info.a = -1;
      for (int i = 0; i < actions.length; i++) {
        Board childState = new Board(state, i);
        MiniMaxInfo childInfo = minimaxHelper(childState, table);
        if (childInfo.v > info.v) {
          info.v = childInfo.v;
          info.a = a;
        }
      }
      table[state] = info;
      return info;
    }
    else {
      info.v = 99999;
      info.a = -1;
      for (int i = 0; i < actions.length; i++) {
        Board childState = new Board(state, i);
        MiniMaxInfo childInfo = minimaxHelper(childState, table);
        if (childInfo.v < info.v) {
          info.v = childInfo.v;
          info.a = a;
        }
      }
      table[state] = info;
      return info;
    }
  }
}
