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
        Chess AI=new Chess(0,0);//�������ص��������
        XSSFWorkbook xssfWorkbook= new XSSFWorkbook("src/resources/�������ֱ�ʤ�߷�.xlsx");//��ȡ������
        XSSFSheet sheet=xssfWorkbook.getSheetAt(0);//��ȡ������
        int lastRowNum=sheet.getLastRowNum();//��ȡ�д�С
        int row=0;
        while(row<=lastRowNum){//����ÿһ������
            XSSFRow Row=sheet.getRow(row);//��ȡ������
            int column=0;//��ʼ����
            if(Row!=null) {//�ж��Ƿ�Ϊ��
                int lastcolumNum=Row.getLastCellNum();//��ȡ��ǰ�е�����
                while(column<lastcolumNum) {
                    int y = 0;//��ʼ��y����
                    String value = Row.getCell(column).getStringCellValue().trim();//��ȡ��ǰ�еĵ�Ԫ������
                    if (value != null) {
                        var strs = value.split("\\.");
                        char s = strs[1].substring(0).charAt(0);//x����
                        int x = Integer.parseInt(String.valueOf((s - 64)));//ת����int����
                        var posStr = strs[1].split("-")[0];
                        if (posStr.length() == 2) {//y����С��10��ʱ��ת��
                            y = Integer.parseInt(String.valueOf(posStr.charAt(1)));
                        } else {                  //y�������10��ʱ������ת��
                            y = Integer.parseInt(posStr.substring(1, 3));
                        }

                        if (GobangPanel.history.get(column).getX() == x && GobangPanel.history.get(column).getY() == 16 - y) {
                            if (GobangPanel.history.size() == column + 1) {
                                String Nextchess = Row.getCell(column + 1).getStringCellValue().trim();
                                var Strs = Nextchess.split("\\.");
                                char S = Strs[1].substring(0).charAt(0);//x����
                                int X = Integer.parseInt(String.valueOf((S - 64)));//ת����int����
                                var PosStr = Strs[1].split("-")[0];
                                int Y;
                                if (PosStr.length() == 2) {//y����С��10��ʱ��ת��
                                    Y = Integer.parseInt(String.valueOf(PosStr.charAt(1)));
                                } else {                  //y�������10��ʱ������ת��
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
        //ÿ�ζ���ʼ����score��������
        for(int i = 0; i  < 16; i++){
            for(int j = 0; j < 16; j++){
                score[i][j] = 0;
            }
        }

        //ÿ�λ�����Ѱ����λ�ã����ֶ�������һ�飨��Ȼ���˺ܶ����ģ���Ϊ�ϴ�����ʱ����Ĵ�඼û�䣩
        //�ȶ���һЩ����
        int humanChessmanNum = 0;//��Ԫ���еĺ�������
        int machineChessmanNum = 0;//��Ԫ���еİ�������
        int tupleScoreTmp = 0;//��Ԫ��÷���ʱ����



        //1.ɨ������15����

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
                //Ϊ����Ԫ���ÿ��λ����ӷ���
                for(k = j; k < j + 5; k++){
                    score[i+1][k+1] += tupleScoreTmp;
                }
                //����
                humanChessmanNum = 0;//��Ԫ���еĺ�������
                machineChessmanNum = 0;//��Ԫ���еİ�������
                tupleScoreTmp = 0;//��Ԫ��÷���ʱ����
            }
        }

        //2.ɨ������15��
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
                //Ϊ����Ԫ���ÿ��λ����ӷ���
                for(k = j; k < j + 5; k++){
                    score[k+1][i+1] += tupleScoreTmp;
                }
                //����
                humanChessmanNum = 0;//��Ԫ���еĺ�������
                machineChessmanNum = 0;//��Ԫ���еİ�������
                tupleScoreTmp = 0;//��Ԫ��÷���ʱ����
            }
        }

        //3.ɨ�����Ͻǰ�������
        for(int i = 14; i >= 4; i--){
            for(int k = i, j = 0; j < 15 && k >= 0; j++, k--){
                int m = k;
                int n = j;
                while(m > k - 5 && k - 4 >= 0){//������Ԫ�飬��ֻɨ���������
                    if(panel[m+1][n+1] == 1) machineChessmanNum++;
                    else if(panel[m+1][n+1] == 2)humanChessmanNum++;

                    m--;
                    n++;
                }
                //ע��б���жϵ�ʱ�򣬿��ܹ�������Ԫ�飨�����ĸ����䣩�������������Ҫ���Ե�
                if(m == k-5){
                    if(color==1){
                        tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                    }
                    if(color==2){
                        tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                    }
                    //Ϊ����Ԫ���ÿ��λ����ӷ���
                    for(m = k, n = j; m > k - 5 ; m--, n++){
                        score[m+1][n+1] += tupleScoreTmp;
                    }
                }

                //����
                humanChessmanNum = 0;//��Ԫ���еĺ�������
                machineChessmanNum = 0;//��Ԫ���еİ�������
                tupleScoreTmp = 0;//��Ԫ��÷���ʱ����

            }
        }

        //4.ɨ�����½ǲ���������
        for(int i = 1; i < 15; i++){
            for(int k = i, j = 14; j >= 0 && k < 15; j--, k++){
                int m = k;
                int n = j;
                while(m < k + 5 && k + 5 <= 15){//������Ԫ�飬��ֻɨ���������
                    if(panel[n+1][m+1] == 1) machineChessmanNum++;
                    else if(panel[n+1][m+1] ==2)humanChessmanNum++;

                    m++;
                    n--;
                }
                //ע��б���жϵ�ʱ�򣬿��ܹ�������Ԫ�飨�����ĸ����䣩�������������Ҫ���Ե�
                if(m == k+5){
                    if(color==1){
                        tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                    }
                    if(color==2){
                        tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                    }
                    //Ϊ����Ԫ���ÿ��λ����ӷ���
                    for(m = k, n = j; m < k + 5; m++, n--){
                        score[n+1][m+1] += tupleScoreTmp;
                    }
                }
                //����
                humanChessmanNum = 0;//��Ԫ���еĺ�������
                machineChessmanNum = 0;//��Ԫ���еİ�������
                tupleScoreTmp = 0;//��Ԫ��÷���ʱ����

            }
        }

        //5.ɨ�����½ǰ�������
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
                //ע��б���жϵ�ʱ�򣬿��ܹ�������Ԫ�飨�����ĸ����䣩�������������Ҫ���Ե�
                if(m == k + 5){
                    if(color==1){
                        tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                    }
                    if(color==2){
                        tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                    }
                    //Ϊ����Ԫ���ÿ��λ����ӷ���
                    for(m = k, n = j; m < k + 5; m++, n++){
                        score[m+1][n+1] += tupleScoreTmp;
                    }
                }

                //����
                humanChessmanNum = 0;//��Ԫ���еĺ�������
                machineChessmanNum = 0;//��Ԫ���еİ�������
                tupleScoreTmp = 0;//��Ԫ��÷���ʱ����

            }
        }

        //6.ɨ�����Ͻǲ���������
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
                //ע��б���жϵ�ʱ�򣬿��ܹ�������Ԫ�飨�����ĸ����䣩�������������Ҫ���Ե�
                if(m == k + 5){
                    if(color==1){
                        tupleScoreTmp = Fivebeat.tupleScore(humanChessmanNum, machineChessmanNum);
                    }
                    if(color==2){
                        tupleScoreTmp = Fivebeat.tupleScore(machineChessmanNum, humanChessmanNum);
                    }
                    //Ϊ����Ԫ���ÿ��λ����ӷ���
                    for(m = k, n = j; m < k + 5; m++, n++){
                        score[n+1][m+1] += tupleScoreTmp;
                    }
                }

                //����
                humanChessmanNum = 0;//��Ԫ���еĺ�������
                machineChessmanNum = 0;//��Ԫ���еİ�������
                tupleScoreTmp = 0;//��Ԫ��÷���ʱ����

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
        int goalX = -1;//Ŀ��λ��x����
        int goalY = -1;//Ŀ��λ��y����
        int maxScore = -1;//������
        int k=0;
        //�ӿ�λ�����ҵ��÷�����λ��
        for(int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
                if(GobangPanel.boardData[i+1][j+1] == 0 && score[i+1][j+1] > maxScore) {
                    if (color == 1) {
                        GobangPanel.boardData[i+1][j+1]=1;
                        k = Forbid.forbid(new Chess(i + 1, j + 1, 1),GobangPanel.copyBoardData());//����÷�����ߵĵ��ǽ��ֵ㣬�򽫷�������
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

        //û�ҵ�����˵��ƽ����
        return new Chess(-1, -1, -1);
    }

    // ����ֵ��Ŀհ׵㰴�����ִӴ�С����
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
