package main.lu;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

// ��ʤ�߷��Զ������ļ��洢����ȡ�������ļ��Ĳ�����
public class BinManual {
    // ����ʤ�߷��������֣�д��������ļ��У�����Ϊ��ʤ�߷��Ķ�ά������ʽ,�ļ�·��
    public void saveBinManual(ArrayList<ArrayList<String>> list,String path) throws FileNotFoundException {
        File file = new File(path); //�������ļ�·��
        try {
            if (!(file.exists())) {
                file.createNewFile();
            }
            OutputStream os = new FileOutputStream(file);   //�ļ���������壬�˴�Ϊ����д��s
            for (ArrayList<String> line : list) {   //line����ά�����е�һ�У���һ�������ı�ʤ�߷����
                byte[] temp = new byte[line.size() + 1];    //�����Ӧ�ֽ�������ʱ����������+1��ԭ��������ȡ����β�����0
                                                            // ��ʾ���н���
                for (int i = 0; i < line.size(); i++) {     //line.get(i)����ȡ�����еĵ�i���߷�����
                    temp[i] = merge2BytesTo1Byte(line.get(i));  //merge2BytesTo1ByteΪ��������ת��Ϊ��Ӧ���ֽ���ʽ
                }
                temp[line.size()] = 0;  //��β��0��ʾ

                os.write(temp);  //�����ֽ�����д���ļ�
            }
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // ���²����ĵ��ֱ�ʤ�߷�׷��д��������ļ��У�����Ϊ��ֵ���ʷ��¼
    public void saveSingleGameToBin(Stack<Chess> history,String path) throws Exception {
        File binFile = new File(path); //�������ļ�·��
        try {
            if (!(binFile.exists())) {
                binFile.createNewFile();
            }
            OutputStream os = new FileOutputStream(binFile,true);   //�ļ����������
            byte[] temp = new byte[history.size() + 1];    //�����Ӧ�ֽ�������ʱ����
            // ����+1��ԭ��������ȡ����β�����-0����ʾ���н���
            for (int i = 0; i < history.size(); i++) {
                Chess chess = history.get(i);
                String X = String.valueOf((char) (64 + chess.x)).trim();
                String Y = String.valueOf((16 - chess.y)).trim();
                temp[i] = merge2BytesTo1Byte(X + Y);  //merge2BytesTo1ByteΪ��������ת��Ϊ��Ӧ���ֽ���ʽ
                // ���崦������ڸú���������
            }
            temp[history.size()] = 0;  //��β��0��ʾ
            os.write(temp);  //�����ֽ�����д���ļ�
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
                System.out.println("�ļ�������!");
            }
            InputStream in = null;
            in = new FileInputStream(file);
            XSSFWorkbook sheets = null;  //��ȡ�ļ�
            sheets = new XSSFWorkbook(in);
            XSSFSheet sheet = sheets.getSheetAt(0); //��ȡ��һ��������
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { //���ж�ȡ
                XSSFRow row = sheet.getRow(i);
                ArrayList<String> l = new ArrayList<>(); //l��ʾһ�У���һ�����������

                for (int index = 0; index < row.getPhysicalNumberOfCells(); index++) {
                    XSSFCell cell = row.getCell(index);
                    String t = getString(cell);
                    if (!(t.equals(""))){ //��ȡ��Ԫ������
                        int index1 = t.indexOf("."),index2=t.indexOf("-");
                        l.add(t.substring(index1+1,index2));
                    }

                }
                if (!(l.size() == 0))
                    list.add(l); //��������ּ��������б�
            }
            saveBinManual(list,toPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //������-1����ʾ���ļ�ĩβ����ȡ������read(temp,i,1)��ʾ����ȡ�ļ��г���Ϊ1���ֽ����ݣ����浽temp�ֽ��������±�Ϊi��λ�ã��������ֶ�ȡ��ʽ��ԭ���ǣ�����read���ص����޷��ŵ�byte��ֵ���ܻᷢ���仯��read(temp,i,1)ʱ���൱�ڰ��ļ��е�i���ֽڶ��뵽��b[i]�У�������̲�û�н���ת��Ϊ�޷�����ֵ�Ĺ���
    // ��ȡ�������ļ�����ȡ��ʤ�߷���������ʽ�Ƕ�ά������ʽ
    public ArrayList<ArrayList<String>> readBinFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file); //�ļ�������
        ArrayList<ArrayList<String>> result = new ArrayList<>(); //���صĽ������
        while (true) {  // ��ȡÿһ�б�ʤ���
            int i = 0; //��ȡ����i������
            byte[] temp = new byte[225];  //��ʼ��һ���ֽ�������ʱ���������ڱ���һ��������֣���С225�������225��
            ArrayList<String> list = new ArrayList<>(); //һ��
            boolean endFlag = false; //�ж��Ƿ��ȡ���ļ�ĩβ��flag
            while (true) {  //��ȡһ�ֱ�ʤ����е�ÿ���߷�����
                if((fis.read(temp,i,1)) == -1){
                    endFlag = true;
                    break;
                }
                if (temp[i]!= 0) {
                    list.add(binToStringCoo(temp[i]));  //binToStringCoo�ǽ�byte���͵�����ת����String���͵����꣬�硰H8��
                    i++;
                }
                else {  //���������ļ��ж�ȡ��0����ʾ���ж�ȡ����
                    result.add(list); //����������ּ����ʤ�����б�
                    break; //����ѭ��������һ��������ֶ�ȡ
                }
            }
            if (endFlag) //����flagΪ�棬��ʾ�ļ���ȡ�����������ļ���ȡ
                break;
        }
        return result;
    }

    //��������ת��Ϊ��Ӧ���ֽ���ʽ����"H8"�У���ĸ����Ҳ��������������������A-O��Ӧ1-15�����ֲ�������
    //���������硰A1�������ơ�11����ת�ɶ����Ƽ���00010001����ͬ��H8������10001000�� ������byte���ͣ�����8λ
    public byte merge2BytesTo1Byte(String str){ //str��������
        byte b;
        char s;
        s = str.charAt(0); //s���������ĸ����
        String e = str.substring(1);  //e����������ֲ���
        b = (byte)(((s-64)<<4) + Integer.parseInt(e)); //s-64����ƫ��������H��8��������λ�������ڸ���λ��ʾ����
                                                        // Integer.parseInt(e)�������ֲ���ת��Ϊ��������������Ӻ�ת����byte��ʽ
        return b;
    }

    //��byte���͵�����ת����String���͵����꣬��8bitת�ɡ�H8������ʽ
    public String binToStringCoo (byte i){
        StringBuilder s = new StringBuilder();
        for (int j = 7; j >= 0; j--) { //������byte�����Բ���ķ�ʽ�洢���ʽ�ÿλ��1������ܵõ�����������Ҫ�Ķ����ƴ���ʽ
                                        // �硰H8���Ķ����ƴ�Ϊ��01000100������������Ҫ��
            if (((1 << j) & i) != 0)
                s.append("1");
            else
                s.append("0");
        }

        String sx = s.substring(0,4),sy = s.substring(4),result; //����λ���룬����λ��ʾx���꣬����λ��ʾy����
        char x;
        String y;
        //��������ʮ����ֵ��-48�ǽ��ַ��͵�"1/0"ת�������ֵ�"1/0"
        int xvalue = (sx.charAt(0)-48) * 8 + (sx.charAt(1)-48) * 4 + (sx.charAt(2)-48) * 2 + (sx.charAt(3)-48);
        x = (char)(64 + xvalue); //����64����ֵ�õ���Ӧ��д��ĸ��ASC���룬ǿ��ת����char���ͼ���ȡ��x����

        int yvalue = (sy.charAt(0)-48) * 8 + (sy.charAt(1)-48) * 4 + (sy.charAt(2)-48) * 2 + (sy.charAt(3)-48);//��������ʮ����ֵ
        y = Integer.toString(yvalue); //�������ֵ����y����

        result = x + y; //����xy����
        return result;
    }

    //����Ԫ�����ݲ�����
    public static String getString(XSSFCell cell) {
        if (cell == null) { //�����򷵻ؿ��ַ���
            return "";
        }
        return cell.getStringCellValue().trim(); //�����ַ�����ʽ���ص�Ԫ������
    }

}
