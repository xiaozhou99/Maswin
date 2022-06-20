package main.ning;

import main.lu.Chess;
import main.yang.GobangPanel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Forbid {
    public static enum forbid {
        //重复冲四
        GO_4_S(0, new String[]{"(0|1|2)(0|2)11011(0|2)(0|1|2)|(0|1|2)(0|2)10111(0|2)(0|2)|(0|1|2)(2)11110(0|2)(0|1|2)"}, 1),
        //重复活三
        ALIVE_3_S(1, new String[]{"(0|1|2)(0|2|3)011100(0|2)(0|1|2)"}, 1),

        LONG(2, new String[]{"111111"}, 1),
        ALIVE_4(3, new String[]{"(0|2)011110(0|2)"}, 1),
        GO_4(4, new String[]{"(0|2)11011(0|2)|(0|2)10111(0|2)|(10|2)11110(0|2)"}, 1),
        ALIVE_3(5, new String[]{"(0|2)011100(0|2)|(0|2)010110(0|2)"}, 1);

        private int index;
        private String[] regex;// 正则表达式
        private int forbid_num;


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

        public int getforbid_num() {
            return forbid_num;
        }

        public void setforbid_num(int forbid_num) {
            this.forbid_num = forbid_num;
        }


        // 构造方法
        forbid(int index, String[] regex, int forbid_num) {
            this.forbid_num = forbid_num;
            this.index = index;
            this.regex = regex;

        }
    }

    public static int forbid(Chess chess, int[][] panel) {
        int x = chess.getX();
        int y = chess.getY();
       //1为三三禁手，2为四四禁手，3为长连禁手
        String str1 = new String();
        String str2 = new String();
        String str3 = new String();
        String str4 = new String();
        ArrayList<String> list = new ArrayList<>();
        //水平方向
        for (int i = -5; i <= 5; i++) {
            if (x + i >= 1 && x + i <= GobangPanel.BOARD_SIZE && y >= 1 && y <= GobangPanel.BOARD_SIZE) {
                String num = String.valueOf(GobangPanel.boardData[x + i][y]);
                str1 = str1 + num;
            } else {
                str1 = str1 + "2";
            }
        }
        list.add(str1);
        //垂直方向
        for (int i = -5; i <= 5; i++) {
            if (x >= 1 && x <= GobangPanel.BOARD_SIZE && y + i >= 1 && y + i <= GobangPanel.BOARD_SIZE) {
                String num = String.valueOf(GobangPanel.boardData[x][y + i]);
                str2 = str2 + num;
            } else {
                str2 = str2 + "2";
            }
        }
        list.add(str2);
        //右斜方向
        for (int i = -5; i <= 5; i++) {
            if (x + i >= 1 && x + i <= GobangPanel.BOARD_SIZE && y + i >= 1 && y + i <= GobangPanel.BOARD_SIZE) {
                String num = String.valueOf(GobangPanel.boardData[x + i][y + i]);
                str3 = str3 + num;
            } else {
                str3 = str3 + "2";
            }
        }
        list.add(str3);
        //左斜方向
        for (int i = -5; i <= 5; i++) {
            if (x - i >= 1 && x - i <= GobangPanel.BOARD_SIZE && y + i >= 1 && y + i <= GobangPanel.BOARD_SIZE) {
                String num = String.valueOf(GobangPanel.boardData[x - i][y + i]);
                str4 = str4 + num;
            } else {
                str4 = str4 + "2";
            }
        }
        list.add(str4);

        //长连判断
        if(L_forbidtype(list)){
            return 3;
        }
        //四四判断
        if(FF_forbidtype(list)){
            return 2;
        }
        //三三判断
        if(TT_forbidtype(list)){
            return 1;
        }



        return 0;
    }

    //判断长连禁手
    public static boolean L_forbidtype(ArrayList<String> numlist) {
        for (int i = 0; i < 4; i++) {
            String str = numlist.get(i);
            //判断长连禁手
            if(getforbid(str,2)){
                return true;
            }
        }
        return false;
    }

    //判断四四禁手
    public static boolean FF_forbidtype(ArrayList<String> numlist){
        int t=0;
        for (int i = 0; i < 4; i++) {
            String str = numlist.get(i);
            //判断长连禁手
            if(getforbid(str,0)){
                t++;
            }
            else {
                for (int j = 0; j < 4; j++) {
                    String str1 = str.substring(j, 8 + j);
                    if(getforbid(str1,3)||getforbid(str1,4)){
                        t++;
                    }
                }
            }
            if(t>1){
                return true;
            }
        }
        return false;
    }

    //判断三三禁手
    public static boolean TT_forbidtype(ArrayList<String> numlist){
        int t=0;
        for (int i = 0; i < 4; i++) {
            String str = numlist.get(i);
            //判断长连禁手
            if(getforbid(str,1)){
                t++;
            }
            else {
                for (int j = 0; j < 4; j++) {
                    String str1 = str.substring(j, 8 + j);
                    if(getforbid(str1,5)){
                        t++;
                    }
                }
            }
            if(t>1){
                return true;
            }
        }
        return false;
    }



    public static boolean getforbid(String chessnum,int i) {
        for (forbid forbid : forbid.values()) {
            if(forbid.index==i) {
                Pattern pat = Pattern.compile(forbid.regex[0]);
                String rseq = new StringBuilder(chessnum).reverse().toString();
                Matcher mat = pat.matcher(chessnum);
                boolean rs1 = mat.find();
                mat = pat.matcher(rseq);
                boolean rs2 = mat.find();
                if (rs1 || rs2) {
                    return true;
                }
            }
        }
        return false;
    }


}

