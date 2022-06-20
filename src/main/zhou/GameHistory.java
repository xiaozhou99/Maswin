package main.zhou;

import main.lu.Chess;

import java.io.Serializable;
import java.util.Stack;

public class GameHistory implements Serializable {
    private static final long serialVersionUID = 3328095044966575509L;
    private String[] players; //玩家A和玩家B
    private String time;//对战时间
    private String winner; //胜者
    private Stack<Chess> chessList; //棋盘上所有棋子
    private int VSMode;//对战模式：人人，人机，机机

    public GameHistory(String[] players, String time, String winner, Stack<Chess> chessList, int VSMode) {
        this.players = players;
        this.time = time;
        this.winner = winner;
        this.chessList = chessList;
        this.VSMode = VSMode;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Stack<Chess> getChessList() {
        return chessList;
    }

    public void setChessList(Stack<Chess> chessList) {
        this.chessList = chessList;
    }

    public int getVSMode() {
        return VSMode;
    }

    public void setVSMode(int VSMode) {
        this.VSMode = VSMode;
    }


    public String getTip() {//获取展示列表名称
        return time + "   " +players[0]+"vs"+players[1]+"   "+winner;
    }
    public String getFileName()//获取保存文件名
    {
        return players[0]+"vs"+players[1]+System.currentTimeMillis();
    }

}
