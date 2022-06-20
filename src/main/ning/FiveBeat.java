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
            if (GobangPanel.initUser == 3) {//��������
                if(GobangPanel.t<num){
                    GobangPanel.t++;
                    GobangPanel.togglePlayer();
                    Beat=true;
                }
                if(GobangPanel.t==num){
                    ArrayList<Chess> BeatList = new ArrayList<>();
                    JOptionPane.showMessageDialog(null, "������ѡ�����µ�����");

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
            if(GobangPanel.initUser==4){//��������
                Chess[] sorted = Level.getSortedPoint(1,GobangPanel.copyBoardData(),GobangPanel.history);
                for (int i=0;i<num;i++){
                    Chess chess=sorted[i];
                    chess.setColor(1);
                    GobangPanel.history.push(chess);
                    GobangPanel.boardData[chess.getX()][chess.getY()]=1;
                }
                GobangPanel.p=false;
                GobangPanel.beat=true;
                JOptionPane.showMessageDialog(null, "������ѡ�����µ�����");
                Beat=true;
            }
        }

        return Beat;
    }



    public static int  delatebeat(int x, int y, int color){
        int [][] score=AIGo.valuation(color,GobangPanel.boardData);
        //�ӿ�λ�����ҵ��÷�����λ��
        int goal=score[x][y];
        return goal;
    }


    public static ArrayList<Chess>  findbeat(int num, int color){
        int [][] score=AIGo.valuation(color,GobangPanel.boardData);
        //ÿ�ζ���ʼ����score��������

        int goalX=-1;
        int goalY=-1;
        int maxScore=-1;
        int k=0;
        ArrayList<Chess> location = new ArrayList<>();

        //�ӿ�λ�����ҵ��÷�����λ��
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
        //1.�����������ӣ����л������ӣ��з�Ϊ0
        if(humanChessmanNum > 0 && machineChessmanNum > 0){
            return 0;
        }
        //2.ȫ��Ϊ�գ�û�����ӣ��з�Ϊ7
        if(humanChessmanNum == 0 && machineChessmanNum == 0){
            return 7;
        }
        //3.������1�ӣ��з�Ϊ35
        if(machineChessmanNum == 1){
            return 35;
        }
        //4.������2�ӣ��з�Ϊ800
        if(machineChessmanNum == 2){
            return 800;
        }
        //5.������3�ӣ��з�Ϊ15000
        if(machineChessmanNum == 3){
            return 15000;
        }
        //6.������4�ӣ��з�Ϊ800000
        if(machineChessmanNum == 4){
            return 800000;
        }
        //7.������1�ӣ��з�Ϊ15
        if(humanChessmanNum == 1){
            return 15;
        }
        //8.������2�ӣ��з�Ϊ400
        if(humanChessmanNum == 2){
            return 400;
        }
        //9.������3�ӣ��з�Ϊ1800
        if(humanChessmanNum == 3){
            return 1800;
        }
        //10.������4�ӣ��з�Ϊ100000
        if(humanChessmanNum == 4){
            return 100000;
        }
        return -1;//������������϶������ˡ����д������������ִ��
    }
}

