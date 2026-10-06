package oit.is.z3541.kaizi.janken.model;

public class Janken {

  private final String userHand;
  private final String cpuHand = "グー";
  private final String result;

  // CPUの手はグーに固定し、ユーザが選んだ手で勝敗を判定する。
  public Janken(String hand) {
    if ("Gu".equals(hand)) {
      this.userHand = "グー";
      this.result = "あいこ";
    } else if ("Choki".equals(hand)) {
      this.userHand = "チョキ";
      this.result = "負け";
    } else if ("Pa".equals(hand)) {
      this.userHand = "パー";
      this.result = "勝ち";
    } else {
      throw new IllegalArgumentException("手はGu、Choki、Paのいずれかを指定してください。");
    }
  }

  public String getUserHand() {
    return userHand;
  }

  public String getCpuHand() {
    return cpuHand;
  }

  public String getResult() {
    return result;
  }
}
