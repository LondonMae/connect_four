private class State {
  int winner;
  int util;
  State parent;
}

private class MiniMaxInfo {
  int value;
  String action;
}

public class GameGraph {
  Map<State, MiniMaxInfo> table = new HashMap<State, MiniMaxInfo>();
  public GameGraph() {

  }
}
