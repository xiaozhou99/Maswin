package main.ning;

import main.lu.Chess;
import main.yang.GobangPanel;
import main.yang.MainUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Level {

    public static enum level {
        //会重复积分冲四的情况
        GO_4_s("重复冲四情况", 0, new String[] { "(0|1|2|3)(0|2|3)11011(0|2|3)(0|1|2|3)|(0|1|2|3)(0|2|3)10111(0|2|3)(0|1|2|3)|(0|1|2|3)(2|3)11110(0|2|3)(0|1|2|3)", "(0|1|2|3)22202(0|1|2|3)|(0|1|2|3)22022(0|1|2|3)" }, 400,400,1,1),
        //会重复积分的活三情况
        ALIVE_3_s("重复活三", 1, new String[] { "(0|1|2|3)(0|2|3)011100(0|2|3)(0|1|2|3)", "002220(0|1|2|3)" }, 8000,6000,1,1),

        CON_5("五连", 2, new String[] { "(0|2|3)11111(0|2|3)", "22222" }, 1000000,1000000,2,2),
        ALIVE_4("活四", 3, new String[] { "(0|2|3)011110(0|2|3)", "022220" }, 100000,100000,2,2),
        GO_4("冲四", 4, new String[] { "(0|2|3)11011(0|2|3)|(0|2|3)10111(0|2|3)|(10|2|3)11110(0|2|3)", "22202|22022|(1|3)22220" }, 400,200,1,1),
        ALIVE_3("活三", 5, new String[] { "(0|2|3)011100(0|2|3)|(0|2|3)010110(0|2|3)", "002220|020220" }, 5000,4000,1,1),
        SLEEP_3("眠三", 6, new String[] {"(10|2|3)01110(2|3|01)|(2|3)11100(0|2|3)|(10|2|3)10110(0|2|3)|(10|2|3)11010(0|2|3)|(0|2|3)10011(0|2|3)", "02202(1|3)|20202|20022|(1|3)22200|02022(1|3)|(1|3)02220(1|3)" }, 300,300,0,0),
        ALIVE_2("活二", 7, new String[] { "(0|2|3)001100(0|2|3)|(0|2|3)010100(0|2|3)|(0|2|3)011000(0|2|3)|(0|2|3)010010(0|2|3)", "002200|000220|002020|020020" }, 1200,1000,0,0),
        SLEEP_2("眠二", 8, new String[] {"(10|2|3)00110(2|3)|(10|2|3)01010(2|3|01)|(10|2|3)10010(0|2|3)|(2|3)10100(0|2|3)|(0|2|3)00011(0|2|3)|(0|2|3)10001(0|2|3)", "(1|3)02020|00202(1|3)|02002(1|3)|02200(1|3)|20002|(1|3)02200|(1|3)22000" }, 100,100,0,0),
        NULL("null", 10, new String[] { "", "" }, 0,0,0,0),
        BLACK("null", 11, new String[] { "", "" }, 0,0,0,0),
        WHITE ("null", 12, new String[] { "", "" }, 0,0,0,0),
        TOTAL ("null", 12, new String[] { "", "" }, 0,0,0,0);;

        private String name;
        private int index;
        private String[] regex;// 正则表达式
        int threat;
        int BLACK_score;
        int WHITR_score;
        int attack;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String[] getRegex() {
            return regex;
        }

        public void setRegex(String[] regex) {
            this.regex = regex;
        }

        public int getThreat() {
            return threat;
        }

        public void setThreat(int threat) {
            this.threat = threat;
        }

        public int getBLACK_score() {
            return BLACK_score;
        }

        public void setBLACK_score(int BLACK_score) {
            this.BLACK_score = BLACK_score;
        }

        public int getWHITR_score() {
            return WHITR_score;
        }

        public void setWHITR_score(int WHITR_score) {
            this.WHITR_score = WHITR_score;
        }

        public int getAttack() {
            return attack;
        }

        public void setAttack(int attack) {
            this.attack = attack;
        }

        // 构造方法
        level(String name, int index, String[] regex, int BLACK_score, int WHITR_score, int attack, int threat) {
            this.name = name;
            this.index = index;
            this.regex = regex;
            this.BLACK_score = BLACK_score;
            this.WHITR_score = WHITR_score;
            this.threat = threat;
            this.attack = attack;
        }

        public void setRegex(String s, String s1) {
        }
    }

    //获取该点四个方向的数据
    public static ArrayList<String> getnumString(Chess chess, int[][] board) {
        int x = chess.getX();
        int y = chess.getY();
        String str1 = new String();
        String str2 = new String();
        String str3 = new String();
        String str4 = new String();
        ArrayList<String> list = new ArrayList<>();
        //水平方向
        for (int i = -5; i <= 5; i++) {
            if (x + i >= 1 && x + i <= 15 && y >= 1 && y <= 15) {
                String num = String.valueOf(board[x + i][y]);
                str1 = str1 + num;
            } else {
                str1 = str1 + "3";
            }
        }
        list.add(str1);

        //垂直方向
        for (int i = -5; i <= 5; i++) {
            if (x >= 1 && x <= 15 && y + i >= 1 && y + i <= 15) {
                String num = String.valueOf(board[x][y + i]);
                str2 = str2 + num;
            } else {
                str2 = str2 + "3";
            }
        }
        list.add(str2);
        //右斜方向
        for (int i = -5; i <= 5; i++) {
            if (x + i >= 1 && x + i <= 15 && y + i >= 1 && y + i <= 15) {
                String num = String.valueOf(board[x + i][y + i]);
                str3 = str3 + num;
            } else {
                str3 = str3 + "3";
            }
        }
        list.add(str3);
        //左斜方向
        for (int i = -5; i <= 5; i++) {
            if (x - i >= 1 && x - i <= 15 && y + i >= 1 && y + i <= 15) {
                String num = String.valueOf(board[x - i][y + i]);
                str4 = str4 + num;
            } else {
                str4 = str4 + "3";
            }
        }
        list.add(str4);
        return list;
    }

    public static void init(level level){
        level.setWHITR_score(0);
        level.setThreat(0);
        level.setAttack(0);
        level.setBLACK_score(0);
        level.setRegex("","");
    }

    //棋盘估值
    public static int[][] valuation(int[][] boarddata, Stack<Chess> history,int color){
        int[][] boardscore=new int[17][17];//棋盘的分数
        int[][] board=new int[17][17];//拷贝模拟的棋盘
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                boardscore[i][j] =boarddata[i][j];
                board[i][j] =boarddata[i][j];
            }
        }

        for(int i=1;i<16;i++){
            for (int j=1;j<16;j++){

                if(board[i][j]==0){
                        //模拟黑子
                        board[i][j] = 1;

                        ArrayList<String> BLACKlist = getnumString(new Chess(i, j), board);
                        getlevel2(1, BLACKlist);
                        if(level.TOTAL.attack<2){
                            level.BLACK.setBLACK_score(level.TOTAL.getBLACK_score());
                        }
                        else {
                            level.BLACK.setBLACK_score(level.TOTAL.getBLACK_score()+100000);
                        }
                    if(MainUI.Jinshou.isSelected()) {//如果是禁手模式
                        int m=Forbid.forbid(new Chess(i,j),board);
                        if(m>0){
                            level.BLACK.setBLACK_score(0);
                        }
                    }


                        init(level.TOTAL);//初始化

                        //模拟白子
                        board[i][j] = 2;
                        ArrayList<String> WHITElist = getnumString(new Chess(i, j), board);
                        getlevel2(2, WHITElist);
                        if(level.TOTAL.threat<2){
                            level.WHITE.setWHITR_score(level.TOTAL.getWHITR_score());
                        }
                        else  {
                            level.WHITE.setWHITR_score(level.TOTAL.getWHITR_score()+100000);
                        }

                    init(level.TOTAL);//初始化
                    board[i][j] = 0;
                    if(color==1) {
                        boardscore[i][j] = (int) (level.BLACK.BLACK_score + level.WHITE.WHITR_score * 0.9);
                    }
                    if(color==2){
                        boardscore[i][j] = (int) (level.BLACK.BLACK_score*0.7+ level.WHITE.WHITR_score);
                    }
                    init(level.BLACK);//初始化
                    init(level.WHITE);//初始化
                }
                if(board[i][j]!=0){
                    boardscore[i][j]=0;
                }
            }
        }

        return boardscore;
    }

//获取最高分的棋子
    public static Chess getonechess(int color, int[][] panel, Stack<Chess> history){

        int[][] ascore=valuation(panel, history,color);
        int[][] score=new int[17][17];
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                score[i][j] =0;
            }
        }

        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                score[i][j] =ascore[i][j];
            }
        }

        int temp=0;
        int x=0;
        int y=0;
        for(int i=1;i<16;i++){
            for (int j=0;j<16;j++){
                if(score[i][j]>temp){
                    temp=score[i][j];
                    x=i;
                    y=j;
                }
            }
        }
        return new Chess(x,y);
    }

    //获取排序后的chess类型数组
    public static Chess[] getSortedPoint(int color, int[][] panel, Stack<Chess> history)

    {
        int[][] score = valuation(panel, history,color);
        Chess[] result = new Chess[225];
        int count=0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                result[count] = new Chess(i+1,j+1);
                result[count].setScore(score[i+1][j+1]);
                count++;
            }
        }
        Arrays.sort(result);
        return result;
    }





    //获取总的分数
    public static void getlevel2(int color,ArrayList<String> numlist){
        init(level.TOTAL);//初始化
        int score=0;
        int tempt=0;
        boolean repeat=false;
        for(int i=0;i<4;i++){
            String str=numlist.get(i);
            //若模拟为黑子，获取其黑棋分数以及攻击分
            if(color==1) {
                for (level level : level.values()) {
                    if (level.index < 3) {//首先查询是否存在重复积分的情况
                        repeat=false;
                        Pattern pat = Pattern.compile(level.regex[color-1]);
                        String rseq = new StringBuilder(str).reverse().toString();
                        Matcher mat = pat.matcher(str);
                        boolean rs1 = mat.find();
                        mat = pat.matcher(rseq);
                        boolean rs2 = mat.find();
                        if (rs1 || rs2) {
                            score += level.getBLACK_score();
                            tempt += level.getAttack();
                            level.TOTAL.setBLACK_score(score);
                            level.TOTAL.setAttack(tempt);
                            repeat=true;
                            break;
                        }
                    }
                    else break;
                }
                if (!repeat) {//未匹配到重复积分情况，开始正常匹配
                    for (int j = 0; j < 4; j++) {
                        String str1 = str.substring(j, 8 + j);
                        level level = getLevel(color, str1);

                        score += level.getBLACK_score();
                        tempt += level.getAttack();
                    }

                    level.TOTAL.setBLACK_score(score);
                    level.TOTAL.setAttack(tempt);
                }
            }
            //若模拟为白子，获取其白子分数以及威胁分
            if(color==2) {
                for (level level : level.values()) {
                    if (level.index < 2) {//首先查询是否存在重复积分的情况
                        repeat=false;
                        Pattern pat = Pattern.compile(level.regex[color-1]);
                        String rseq = new StringBuilder(str).reverse().toString();
                        Matcher mat = pat.matcher(str);
                        boolean rs1 = mat.find();
                        mat = pat.matcher(rseq);
                        boolean rs2 = mat.find();
                        if (rs1 || rs2) {
                            score += level.getWHITR_score();
                            tempt += level.getThreat();
                            level.TOTAL.setWHITR_score(score);
                            level.TOTAL.setThreat(tempt);
                            repeat=true;
                            break;
                        }
                    }
                    else break;
                }
                if (!repeat) {//未匹配到重复积分情况，开始正常匹配
                    for (int j = 0; j < 4; j++) {
                        String str2 = str.substring(j + 1, 7 + j);
                        level level = getLevel(color, str2);

                        score += level.getWHITR_score();
                        tempt += level.getThreat();
                    }

                    level.TOTAL.setWHITR_score(score);
                    level.TOTAL.setThreat(tempt);
                }
            }

        }

    }


//获取模拟落子后的level
    public static level getLevel(int color, String chessnum) {
            for (level level : level.values()) {
                Pattern pat = Pattern.compile(level.regex[color-1]);
                String rseq = new StringBuilder(chessnum).reverse().toString();
                Matcher mat = pat.matcher(chessnum);
                boolean rs1 = mat.find();
                mat = pat.matcher(rseq);
                boolean rs2 = mat.find();
                if (rs1 || rs2) {
                    return level;
                }
            }
       return level.NULL;
    }

}

