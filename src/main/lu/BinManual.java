package main.lu;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

// 必胜走法以二进制文件存储、读取二进制文件的操作类
public class BinManual {
    // 将必胜走法（多个棋局）写入二进制文件中，参数为必胜走法的二维数组形式,文件路径
    public void saveBinManual(ArrayList<ArrayList<String>> list,String path) throws FileNotFoundException {
        File file = new File(path); //二进制文件路径
        try {
            if (!(file.exists())) {
                file.createNewFile();
            }
            OutputStream os = new FileOutputStream(file);   //文件输出流定义，此处为覆盖写入s
            for (ArrayList<String> line : list) {   //line即二维数组中的一行，即一局完整的必胜走法棋局
                byte[] temp = new byte[line.size() + 1];    //定义对应字节数组临时变量，容量+1的原因是若读取到行尾则存入0
                                                            // 表示该行结束
                for (int i = 0; i < line.size(); i++) {     //line.get(i)即获取到该行的第i步走法坐标
                    temp[i] = merge2BytesTo1Byte(line.get(i));  //merge2BytesTo1Byte为将该坐标转换为对应的字节形式
                }
                temp[line.size()] = 0;  //行尾用0表示

                os.write(temp);  //将该字节数组写入文件
            }
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // 将新产生的单局必胜走法追加写入二进制文件中，参数为棋局的历史记录
    public void saveSingleGameToBin(Stack<Chess> history,String path) throws Exception {
        File binFile = new File(path); //二进制文件路径
        try {
            if (!(binFile.exists())) {
                binFile.createNewFile();
            }
            OutputStream os = new FileOutputStream(binFile,true);   //文件输出流定义
            byte[] temp = new byte[history.size() + 1];    //定义对应字节数组临时变量
            // 容量+1的原因是若读取到行尾则存入-0，表示该行结束
            for (int i = 0; i < history.size(); i++) {
                Chess chess = history.get(i);
                String X = String.valueOf((char) (64 + chess.x)).trim();
                String Y = String.valueOf((16 - chess.y)).trim();
                temp[i] = merge2BytesTo1Byte(X + Y);  //merge2BytesTo1Byte为将该坐标转换为对应的字节形式
                // 具体处理过程在该函数定义中
            }
            temp[history.size()] = 0;  //行尾用0表示
            os.write(temp);  //将该字节数组写入文件
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void excelToBin(String path,String toPath) {
        File file = new File(path);
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        try {
            if (!file.exists()) {
                System.out.println("文件不存在!");
            }
            InputStream in = null;
            in = new FileInputStream(file);
            XSSFWorkbook sheets = null;  //获取文件
            sheets = new XSSFWorkbook(in);
            XSSFSheet sheet = sheets.getSheetAt(0); //获取第一个工作簿
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { //按行读取
                XSSFRow row = sheet.getRow(i);
                ArrayList<String> l = new ArrayList<>(); //l表示一行，即一个完整的棋局

                for (int index = 0; index < row.getPhysicalNumberOfCells(); index++) {
                    XSSFCell cell = row.getCell(index);
                    String t = getString(cell);
                    if (!(t.equals(""))){ //读取单元格内容
                        int index1 = t.indexOf("."),index2=t.indexOf("-");
                        l.add(t.substring(index1+1,index2));
                    }

                }
                if (!(l.size() == 0))
                    list.add(l); //将完整棋局加入棋谱列表
            }
            saveBinManual(list,toPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //若返回-1，表示到文件末尾，读取结束，read(temp,i,1)表示将读取文件中长度为1的字节内容，保存到temp字节数组中下表为i的位置，采用这种读取方式的原因是，正常read返回的是无符号的byte，值可能会发生变化，read(temp,i,1)时，相当于把文件中第i个字节读入到了b[i]中，这个过程并没有将其转化为无符号数值的过程
    // 读取二进制文件，获取必胜走法，返回形式是二维数组形式
    public ArrayList<ArrayList<String>> readBinFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file); //文件输入流
        ArrayList<ArrayList<String>> result = new ArrayList<>(); //返回的结果变量
        while (true) {  // 读取每一行必胜棋局
            int i = 0; //读取到第i个坐标
            byte[] temp = new byte[225];  //初始化一个字节数组临时变量，用于保存一个完整棋局，大小225是最多有225步
            ArrayList<String> list = new ArrayList<>(); //一行
            boolean endFlag = false; //判断是否读取到文件末尾的flag
            while (true) {  //读取一局必胜棋局中的每个走法坐标
                if((fis.read(temp,i,1)) == -1){
                    endFlag = true;
                    break;
                }
                if (temp[i]!= 0) {
                    list.add(binToStringCoo(temp[i]));  //binToStringCoo是将byte类型的坐标转换成String类型的坐标，如“H8”
                    i++;
                }
                else {  //若二进制文件中读取到0，表示该行读取结束
                    result.add(list); //将该完整棋局加入必胜棋谱列表
                    break; //跳出循环进入下一个完整棋局读取
                }
            }
            if (endFlag) //若该flag为真，表示文件读取结束，跳出文件读取
                break;
        }
        return result;
    }

    //将该坐标转换为对应的字节形式，如"H8"中，字母部分也采用类似数字来处理，如A-O对应1-15，数字部分类似
    //完整处理如“A1”，类似“11”，转成二进制即“00010001”，同理“H8”即“10001000” 。返回byte类型，即这8位
    public byte merge2BytesTo1Byte(String str){ //str即该坐标
        byte b;
        char s;
        s = str.charAt(0); //s即坐标的字母部分
        String e = str.substring(1);  //e即坐标的数字部分
        b = (byte)(((s-64)<<4) + Integer.parseInt(e)); //s-64即算偏移量，如H是8，左移四位即将其在高四位表示出来
                                                        // Integer.parseInt(e)即将数字部分转换为整数。两部分相加后转换成byte形式
        return b;
    }

    //将byte类型的坐标转换成String类型的坐标，即8bit转成“H8”的形式
    public String binToStringCoo (byte i){
        StringBuilder s = new StringBuilder();
        for (int j = 7; j >= 0; j--) { //由于在byte中是以补码的方式存储，故将每位和1相与才能得到上述我们想要的二进制串形式
                                        // 如“H8”的二进制串为“01000100”才是我们想要的
            if (((1 << j) & i) != 0)
                s.append("1");
            else
                s.append("0");
        }

        String sx = s.substring(0,4),sy = s.substring(4),result; //将八位分离，高四位表示x坐标，低四位表示y坐标
        char x;
        String y;
        //算其代表的十进制值，-48是将字符型的"1/0"转换成数字的"1/0"
        int xvalue = (sx.charAt(0)-48) * 8 + (sx.charAt(1)-48) * 4 + (sx.charAt(2)-48) * 2 + (sx.charAt(3)-48);
        x = (char)(64 + xvalue); //加上64基本值得到对应大写字母的ASCⅡ码，强制转换成char类型即获取到x坐标

        int yvalue = (sy.charAt(0)-48) * 8 + (sy.charAt(1)-48) * 4 + (sy.charAt(2)-48) * 2 + (sy.charAt(3)-48);//算其代表的十进制值
        y = Integer.toString(yvalue); //算出来的值即是y坐标

        result = x + y; //返回xy坐标
        return result;
    }

    //处理单元格内容并返回
    public static String getString(XSSFCell cell) {
        if (cell == null) { //若空则返回空字符串
            return "";
        }
        return cell.getStringCellValue().trim(); //否则按字符串形式返回单元格内容
    }

}
