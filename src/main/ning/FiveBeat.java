package main.ning;

import main.lu.Chess;
import main.yang.GobangPanel;
import main.yang.MainUI;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class FiveBeat {

    public static boolean beat(){
        boolean Beat=false;
        int num= MainUI.getNum();
        if(GobangPanel.p) {
            if (GobangPanel.initUser == 3) {//人类先手
                if(GobangPanel.t<num){
                    GobangPanel.t++;
                    GobangPanel.togglePlayer();
                    Beat=true;
                }
                if(GobangPanel.t==num){
                    ArrayList<Chess> BeatList = new ArrayList<>();
                    JOptionPane.showMessageDialog(null, "白棋请选择留下的棋子");

                    for(int i=0;i<num;i++){
                        Chess chess=GobangPanel.history.pop();
                        GobangPanel.boardData[chess.getX()][chess.getY()]=0;
                        BeatList.add(chess);
                    }

                    int[][] boardscore=Level.valuation(GobangPanel.copyBoardData(),GobangPanel.history,1);

                    int minscore=10000000;
                    int goalx=-1;
                    int goaly=-1;
                    while (BeatList.size()>0){
                        Chess chess=BeatList.remove(BeatList.size() - 1);
                        int tempscore=boardscore[chess.getX()][chess.getY()];
                        if(minscore>tempscore){
                            minscore=tempscore;
                            goalx=chess.getX();
                            goaly=chess.getY();
                        }
                    }
                    GobangPanel.boardData[goalx][goaly]=1;
                    GobangPanel.history.push(new Chess(goalx,goaly,1));
                    System.out.println(GobangPanel.history.size());
                    GobangPanel.p=false;
                    GobangPanel.t=0;
                    GobangPanel.togglePlayer();
                    Beat=false;
                }

            }
            if(GobangPanel.initUser==4){//机器先手
                Chess[] sorted = Level.getSortedPoint(1,GobangPanel.copyBoardData(),GobangPanel.history);
                for (int i=0;i<num;i++){
                    Chess chess=sorted[i];
                    chess.setColor(1);
                    GobangPanel.history.push(chess);
                    GobangPanel.boardData[chess.getX()][chess.getY()]=1;
                }
                GobangPanel.p=false;
                GobangPanel.beat=true;
                JOptionPane.showMessageDialog(null, "白棋请选择留下的棋子");
                Beat=true;
            }
        }

        return Beat;
    }



    public static int  delatebeat(int x, int y, int color){
        int [][] score=AIGo.valuation(color,GobangPanel.boardData);
        //从空位置中找到得分最大的位置
        int goal=score[x][y];
        return goal;
    }


    public static ArrayList<Chess>  findbeat(int num, int color){
        int [][] score=AIGo.valuation(color,GobangPanel.boardData);
        //每次都初始化下score评分数组

        int goalX=-1;
        int goalY=-1;
        int maxScore=-1;
        int k=0;
        ArrayList<Chess> location = new ArrayList<>();

        //从空位置中找到得分最大的位置
        for(int t=0;t<num;t++){
            for(int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (GobangPanel.boardData[i + 1][j + 1] == 0 && score[i + 1][j + 1] > maxScore) {
                        if (color == 1) {
                            k = Forbid.forbid(new Chess(i + 1, j + 1, 1),GobangPanel.copyBoardData());
                            if (k == 0) {
                                goalX = i;
                                goalY = j;
                                maxScore = score[i + 1][j + 1];
                            }
                        }
                    }
                }
            }
            location.add(new Chess(goalX+1,goalY+1,1));
            score[goalX+1][goalY+1]=0;
            maxScore=0;

        }

        return location;

    }




    public static int tupleScore(int humanChessmanNum, int machineChessmanNum){
        //1.既有人类落子，又有机器落子，判分为0
        if(humanChessmanNum > 0 && machineChessmanNum > 0){
            return 0;
        }
        //2.全部为空，没有落子，判分为7
        if(humanChessmanNum == 0 && machineChessmanNum == 0){
            return 7;
        }
        //3.机器落1子，判分为35
        if(machineChessmanNum == 1){
            return 35;
        }
        //4.机器落2子，判分为800
        if(machineChessmanNum == 2){
            return 800;
        }
        //5.机器落3子，判分为15000
        if(machineChessmanNum == 3){
            return 15000;
        }
        //6.机器落4子，判分为800000
        if(machineChessmanNum == 4){
            return 800000;
        }
        //7.人类落1子，判分为15
        if(humanChessmanNum == 1){
            return 15;
        }
        //8.人类落2子，判分为400
        if(humanChessmanNum == 2){
            return 400;
        }
        //9.人类落3子，判分为1800
        if(humanChessmanNum == 3){
            return 1800;
        }
        //10.人类落4子，判分为100000
        if(humanChessmanNum == 4){
            return 100000;
        }
        return -1;//若是其他结果肯定出错了。这行代码根本不可能执行
    }
}

