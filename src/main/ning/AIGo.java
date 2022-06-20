package main.ning;

import main.lu.Chess;
import main.yang.GobangPanel;
import main.yang.MainUI;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.Arrays;

public class AIGo {
    public static Chess aigo() throws IOException {
        Chess AI=new Chess(0,0);//创建返回的坐标参数
        XSSFWorkbook xssfWorkbook= new XSSFWorkbook("src/resources/黑棋先手必胜走法.xlsx");//获取工作薄
        XSSFSheet sheet=xssfWorkbook.getSheetAt(0);//获取工作表
        int lastRowNum=sheet.getLastRowNum();//获取行大小
        int row=0;
        while(row<=lastRowNum){//遍历每一行数据
            XSSFRow Row=sheet.getRow(row);//获取行数据
            int column=0;//初始化列
            if(Row!=null) {//判断是否为空
                int lastcolumNum=Row.getLastCellNum();//获取当前行的列数
                while(column<lastcolumNum) {
                    int y = 0;//初始化y坐标
                    String value = Row.getCell(column).getStringCellValue().trim();//获取当前列的单元格数据
                    if (value != null) {
                        var strs = value.split("\\.");
                        char s = strs[1].substring(0).charAt(0);//x坐标
                        int x = Integer.parseInt(String.valueOf((s - 64)));//转换成int类型
                        var posStr = strs[1].split("-")[0];
                        if (posStr.length() == 2) {//y坐标小于10的时候转换
                            y = Integer.parseInt(String.valueOf(posStr.charAt(1)));
                        } else {                  //y坐标大于10的时候数据转换
                            y = Integer.parseInt(posStr.substring(1, 3));
                        }

                        if (GobangPanel.history.get(column).getX() == x && GobangPanel.history.get(column).getY() == 16 - y) {
                            if (GobangPanel.history.size() == column + 1) {
                                String Nextchess = Row.getCell(column + 1).getStringCellValue().trim();
                                var Strs = Nextchess.split("\\.");
                                char S = Strs[1].substring(0).charAt(0);//x坐标
                                int X = Integer.parseInt(String.valueOf((S - 64)));//转换成int类型
                                var PosStr = Strs[1].split("-")[0];
                                int Y;
                                if (PosStr.length() == 2) {//y坐标小于10的时候转换
                                    Y = Integer.parseInt(String.valueOf(PosStr.charAt(1)));
                                } else {                  //y坐标大于10的时候数据转换
                                    Y = Integer.parseInt(PosStr.substring(1, 3));
                                }
                                AI.setX(X);
                                AI.setY(16 - Y);
                                break;
                            } else {
                                column++;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            if(AI.getX()!=0){
                break;
            }
            else {
                row++;
                AI.setX(0);
                AI.setY(0);
            }
        }
        return AI;

    }


    public static int [][] valuation(int color,int[][] panel){
        int [][] score=new int[16][16];
        //每次都初始化下score评分数组
        for(int i = 0; i  < 16; i++){
            for(int j = 0; j < 16; j++){
                score[i][j] = 0;
            }
        }

        //每次机器找寻落子位置，评分都重新算一遍（虽然算了很多多余的，因为上次落子时候算的大多都没变）
        //先定义一些变量
        int humanChessmanNum = 0;//五元组中的黑棋数量
        int machineChessmanNum = 0;//五元组中的白棋数量
        int tupleScoreTmp = 0;//五元组得分临时变量



        //1.扫描横向的15个行

        FiveBeat Fivebeat = new FiveBeat();
        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 11; j++){
                int k = j;
                while(k < j + 5){
                    if(panel[i+1][k+1] == 1) machineChessmanNum++;
                    else if(panel[i+1][k+1] == 2)humanChessmanNum++;
                    k++;
                }
                if(color==1){
                    tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                }
                if(color==2){
                    tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                }
                //为该五元组的每个位置添加分数
                for(k = j; k < j + 5; k++){
                    score[i+1][k+1] += tupleScoreTmp;
                }
                //置零
                humanChessmanNum = 0;//五元组中的黑棋数量
                machineChessmanNum = 0;//五元组中的白棋数量
                tupleScoreTmp = 0;//五元组得分临时变量
            }
        }

        //2.扫描纵向15行
        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 11; j++){
                int k = j;
                while(k < j + 5){
                    if(panel[k+1][i+1] == 1) machineChessmanNum++;
                    else if(panel[k+1][i+1] == 2)humanChessmanNum++;
                    k++;
                }
                if(color==1){
                    tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                }
                if(color==2){
                    tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                }
                //为该五元组的每个位置添加分数
                for(k = j; k < j + 5; k++){
                    score[k+1][i+1] += tupleScoreTmp;
                }
                //置零
                humanChessmanNum = 0;//五元组中的黑棋数量
                machineChessmanNum = 0;//五元组中的白棋数量
                tupleScoreTmp = 0;//五元组得分临时变量
            }
        }

        //3.扫描左上角包括中线
        for(int i = 14; i >= 4; i--){
            for(int k = i, j = 0; j < 15 && k >= 0; j++, k--){
                int m = k;
                int n = j;
                while(m > k - 5 && k - 4 >= 0){//存在五元组，且只扫描五个棋子
                    if(panel[m+1][n+1] == 1) machineChessmanNum++;
                    else if(panel[m+1][n+1] == 2)humanChessmanNum++;

                    m--;
                    n++;
                }
                //注意斜向判断的时候，可能构不成五元组（靠近四个角落），遇到这种情况要忽略掉
                if(m == k-5){
                    if(color==1){
                        tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                    }
                    if(color==2){
                        tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                    }
                    //为该五元组的每个位置添加分数
                    for(m = k, n = j; m > k - 5 ; m--, n++){
                        score[m+1][n+1] += tupleScoreTmp;
                    }
                }

                //置零
                humanChessmanNum = 0;//五元组中的黑棋数量
                machineChessmanNum = 0;//五元组中的白棋数量
                tupleScoreTmp = 0;//五元组得分临时变量

            }
        }

        //4.扫描右下角不包括中线
        for(int i = 1; i < 15; i++){
            for(int k = i, j = 14; j >= 0 && k < 15; j--, k++){
                int m = k;
                int n = j;
                while(m < k + 5 && k + 5 <= 15){//存在五元组，且只扫描五个棋子
                    if(panel[n+1][m+1] == 1) machineChessmanNum++;
                    else if(panel[n+1][m+1] ==2)humanChessmanNum++;

                    m++;
                    n--;
                }
                //注意斜向判断的时候，可能构不成五元组（靠近四个角落），遇到这种情况要忽略掉
                if(m == k+5){
                    if(color==1){
                        tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                    }
                    if(color==2){
                        tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                    }
                    //为该五元组的每个位置添加分数
                    for(m = k, n = j; m < k + 5; m++, n--){
                        score[n+1][m+1] += tupleScoreTmp;
                    }
                }
                //置零
                humanChessmanNum = 0;//五元组中的黑棋数量
                machineChessmanNum = 0;//五元组中的白棋数量
                tupleScoreTmp = 0;//五元组得分临时变量

            }
        }

        //5.扫描左下角包括中线
        for(int i = 0; i < 11; i++){
            for(int k = i, j = 0; j < 15 && k < 15; j++, k++){
                int m = k;
                int n = j;
                while(m < k + 5 && k + 5 <= 15){
                    if(panel[m+1][n+1] == 1) machineChessmanNum++;
                    else if(panel[m+1][n+1] == 2)humanChessmanNum++;

                    m++;
                    n++;
                }
                //注意斜向判断的时候，可能构不成五元组（靠近四个角落），遇到这种情况要忽略掉
                if(m == k + 5){
                    if(color==1){
                        tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                    }
                    if(color==2){
                        tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                    }
                    //为该五元组的每个位置添加分数
                    for(m = k, n = j; m < k + 5; m++, n++){
                        score[m+1][n+1] += tupleScoreTmp;
                    }
                }

                //置零
                humanChessmanNum = 0;//五元组中的黑棋数量
                machineChessmanNum = 0;//五元组中的白棋数量
                tupleScoreTmp = 0;//五元组得分临时变量

            }
        }

        //6.扫描右上角不包括中线
        for(int i = 1; i < 11; i++){
            for(int k = i, j = 0; j < 15 && k < 15; j++, k++){
                int m = k;
                int n = j;
                while(m < k + 5 && k + 5 <= 15){
                    if(panel[n+1][m+1] == 1) machineChessmanNum++;
                    else if(panel[n+1][m+1] == 2)humanChessmanNum++;

                    m++;
                    n++;
                }
                //注意斜向判断的时候，可能构不成五元组（靠近四个角落），遇到这种情况要忽略掉
                if(m == k + 5){
                    if(color==1){
                        tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                    }
                    if(color==2){
                        tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                    }
                    //为该五元组的每个位置添加分数
                    for(m = k, n = j; m < k + 5; m++, n++){
                        score[n+1][m+1] += tupleScoreTmp;
                    }
                }

                //置零
                humanChessmanNum = 0;//五元组中的黑棋数量
                machineChessmanNum = 0;//五元组中的白棋数量
                tupleScoreTmp = 0;//五元组得分临时变量

            }
        }
        if(MainUI.Jinshou.isSelected()) {
            if (color == 1) {
                int k = 0;
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 15; j++) {
                        if (panel[i + 1][j + 1] == 0) {
                            k = Forbid.forbid(new Chess(i + 1, j + 1, 1),GobangPanel.copyBoardData());
                            if (k > 0) {
                                score[i + 1][j + 1] = 0;
                            }
                        } else {
                            score[i + 1][j + 1] = 0;
                        }
                    }
                }
            }
        }
        return score;
    }

    public static Chess searchLocation(int color){
        int [][] score=valuation(color,GobangPanel.boardData);
        int goalX = -1;//目标位置x坐标
        int goalY = -1;//目标位置y坐标
        int maxScore = -1;//最大分数
        int k=0;
        //从空位置中找到得分最大的位置
        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if(GobangPanel.boardData[i+1][j+1] == 0 && score[i+1][j+1] > maxScore) {
                    if (color == 1) {
                        GobangPanel.boardData[i+1][j+1]=1;
                        k = Forbid.forbid(new Chess(i + 1, j + 1, 1),GobangPanel.copyBoardData());//如果该分数最高的点是禁手点，则将分数置零
                        if (k == 0) {
                            goalX = i;
                            goalY = j;
                            maxScore = score[i + 1][j + 1];
                        }
                        GobangPanel.boardData[i+1][j+1]=0;
                    }
                    if (color==2){
                        goalX = i;
                        goalY = j;
                        maxScore = score[i + 1][j + 1];
                    }
                }
            }
        }

        if(goalX != -1 && goalY != -1){
            return new Chess(1+goalX, 1+goalY, color);
        }

        //没找到坐标说明平局了
        return new Chess(-1, -1, -1);
    }

    // 将估值后的空白点按评估分从大到小返回
    public Chess[] getSortedPoint(int color,int[][] panel) {
        int[][] score = valuation(color,panel);
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
}
