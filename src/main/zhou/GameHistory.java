package main.zhou;

import main.lu.Chess;

import java.io.Serializable;
import java.util.Stack;

public class GameHistory implements Serializable {
    private static final long serialVersionUID = 3328095044966575509L;
    private String[] players; //���A�����B
    private String time;//��սʱ��
    private String winner; //ʤ��
    private Stack<Chess> chessList; //��������������
    private int VSMode;//��սģʽ�����ˣ��˻�������

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


    public String getTip() {//��ȡչʾ�б�����
        return time + "   " +players[0]+"vs"+players[1]+"   "+winner;
    }
    public String getFileName()//��ȡ�����ļ���
    {
        return players[0]+"vs"+players[1]+System.currentTimeMillis();
    }

}
