package main.zhou;

import main.lu.Chess;
import main.yang.GobangPanel;

//���ݱ����
public class DataAnnotations {

    private int[][] data;//����ģ�����̵����ӹ��̲�����ɨ��

    private boolean Huo_Three = false;//����
    private boolean Huo_Four = false;//����
    private boolean Chong_Four = false;//����
    private boolean GongFive = false;//����

    public DataAnnotations() {
        //һ��һ����������״̬����ɨ�������ϵ�����
        this.data = new int[GobangPanel.BT][GobangPanel.BT];
        for (int i = 0; i < GobangPanel.BT; i++) {
            for (int j = 0; j < GobangPanel.BT; j++) {
                data[i][j] = GobangPanel.EMPTY;
                if (i == 0 || i == GobangPanel.BT - 1 || j == 0 || j == GobangPanel.BT - 1)
                    data[i][j] = GobangPanel.BORDER;// �߽�
            }
        }
    }


    /**
     * @param isAttack �ж϶Ե�ǰ����chess�ǹ����ж����Ƿ����ж�,trueΪ�����ж�
     */
    public String ScanBoard(Chess chess, boolean isAttack) {

        reset();//����������Ϊ״̬

        if (!isAttack)//����Ƿ����ж���1Ϊ���ӣ�2Ϊ����
        {
//            chess.color = 3 - chess.color;//ת��Ϊ�Է����ӣ��жϻ��ʲô����
            chess.setColor(3 - chess.getColor());

        }
        data[chess.getX()][chess.getY()] = chess.getColor();

        for (int dir = 1; dir <= 4; dir++) {//dir��1��4���ֱ�����ĸ�ɨ�跽��
            int chessCount = 1;  // �͵�ǰλ��������ͬɫ�������� ###
            int spaceCount1 = 0;//ͬɫ�����ұ�һ�˿�λ��###000(r)
            int spaceCount2 = 0;//ͬɫ�������һ�˿�λ��(l)000###
            int chessRight = 0;//�ұ߸���һ����λ������ͬɫ�������� ### ###(r)
            int chessLeft = 0;  // ��߱߸���һ����λ������ͬɫ�������� (l)### ###
            int chessRightSpace = 0;//��chessRight֮��������λ��### ###(r)000
            int chessLeftSpace = 0;  // ��chessLeft֮��������λ�� 000(l)### ###

            int k, n;

            switch (dir) {
                case 1: //ˮƽ����
                    //���Ҳ�����ͬ��ɫ����������
                    for (k = chess.getX() + 1; k < GobangPanel.BT - 1; k++) {
                        if (data[k][chess.getY()] == chess.getColor()) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //��ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k < GobangPanel.BT - 1) && (data[k][chess.getY()] == 0)) {
                        spaceCount1++;
                        k++;
                    }
                    if (spaceCount1 == 1) {//����һ����λ�ж���ͬɫ����
                        while ((k < GobangPanel.BT - 1) && (data[k][chess.getY()] == chess.getColor())) {
                            chessRight++;
                            k++;
                        }
                        while ((k < GobangPanel.BT - 1) && (data[k][chess.getY()] == 0)) {//��chessRight֮���ж��ٿ�λ
                            chessRightSpace++;
                            k++;
                        }
                    }

                    //���෴���������ͬ��ɫ����������
                    for (k = chess.getX() - 1; k >= 1; k--) {
                        if (data[k][chess.getY()] == chess.getColor()) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //�෴���򡪡���ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k >= 1) && (data[k][chess.getY()] == 0)) {
                        spaceCount2++;
                        k--;
                    }
                    if (spaceCount2 == 1) {//�෴���򡪡�����һ����λ�ж���ͬɫ����
                        while ((k >= 1) && (data[k][chess.getY()] == chess.getColor())) {
                            chessLeft++;
                            k--;
                        }
                        while ((k >= 1) && (data[k][chess.getY()] == 0)) {//��chessLeft֮���ж��ٿ�λ
                            chessLeftSpace++;
                            k--;
                        }
                    }
                    break;

                case 2:  //  ��ֱ����
                    //�����ӵķ��������ͬ��ɫ����������
                    for (k = chess.getY() + 1; k < GobangPanel.BT - 1; k++) {
                        if (data[chess.getX()][k] == chess.getColor()) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //��ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k < GobangPanel.BT - 1) && (data[chess.getX()][k] == 0)) {
                        spaceCount1++;
                        k++;
                    }
                    if (spaceCount1 == 1) {//����һ����λ�ж���ͬɫ����
                        while ((k < GobangPanel.BT - 1) && (data[chess.getX()][k] == chess.getColor())) {
                            chessRight++;
                            k++;
                        }
                        while ((k < GobangPanel.BT - 1) && (data[chess.getX()][k] == 0)) {//��chessRight֮���ж��ٿ�λ
                            chessRightSpace++;
                            k++;
                        }
                    }

                    //���෴���������ͬ��ɫ����������
                    for (k = chess.getY() - 1; k >= 1; k--) {
                        if (data[chess.getX()][k] == chess.getColor()) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //�෴���򡪡���ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k >= 1) && (data[chess.getX()][k] == 0)) {
                        spaceCount2++;
                        k--;
                    }
                    if (spaceCount2 == 1) {//�෴���򡪡�����һ����λ�ж���ͬɫ����
                        while ((k >= 1) && (data[chess.getX()][k] == chess.getColor())) {
                            chessLeft++;
                            k--;
                        }
                        while ((k >= 1) && (data[chess.getX()][k] == 0)) {//��chessLeft֮���ж��ٿ�λ
                            chessLeftSpace++;
                            k--;
                        }
                    }
                    break;
                case 3:  //  ���ϵ�����
                    //�����ӵķ��������ͬ��ɫ����������
                    for (k = chess.getX() + 1, n = chess.getY() + 1; (k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1); k++, n++) {
                        if (data[k][n] == chess.getColor()) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //��ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {
                        spaceCount1++;
                        k++;
                        n++;
                    }
                    if (spaceCount1 == 1) {//����һ����λ�ж���ͬɫ����
                        while ((k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1) && (data[k][n] == chess.getColor())) {
                            chessRight++;
                            k++;
                            n++;
                        }
                        while ((k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {//��chessRight֮���ж��ٿ�λ
                            chessRightSpace++;
                            k++;
                            n++;
                        }
                    }

                    //���෴���������ͬ��ɫ����������
                    for (k = chess.getX() - 1, n = chess.getY() - 1; (k >= 1) && (n >= 1); k--, n--) {
                        if (data[k][n] == chess.getColor()) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //�෴���򡪡���ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k >= 1) && (n >= 1) && (data[k][n] == 0)) {
                        spaceCount2++;
                        k--;
                        n--;
                    }
                    if (spaceCount2 == 1) {//�෴���򡪡�����һ����λ�ж���ͬɫ����
                        while ((k >= 1) && (n >= 1) && (data[k][n] == chess.getColor())) {
                            chessLeft++;
                            k--;
                            n--;
                        }
                        while ((k >= 1) && (n >= 1) && (data[k][n] == 0)) {//��chessLeft֮���ж��ٿ�λ
                            chessLeftSpace++;
                            k--;
                            n--;
                        }
                    }
                    break;

                case 4:  //  ���ϵ�����
                    for (k = chess.getX() + 1, n = chess.getY() - 1; k < GobangPanel.BT - 1 && n >= 1; k++, n--) {  //����������ͬɫ����
                        if (data[k][n] == chess.getColor()) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //��ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k < GobangPanel.BT - 1) && (n >= 1) && (data[k][n] == 0)) {
                        spaceCount1++;
                        k++;
                        n--;
                    }
                    if (spaceCount1 == 1) {//����һ����λ�ж���ͬɫ����
                        while ((k < GobangPanel.BT - 1) && (n >= 1) && (data[k][n] == chess.getColor())) {
                            chessRight++;
                            k++;
                            n--;
                        }
                        while ((k < GobangPanel.BT - 1) && (n >= 1) && (data[k][n] == 0)) {//��chessRight֮���ж��ٿ�λ
                            chessRightSpace++;
                            k++;
                            n--;
                        }
                    }

                    //���෴���������ͬ��ɫ����������
                    for (k = chess.getX() - 1, n = chess.getY() + 1; k >= 1 && n < GobangPanel.BT - 1; k--, n++) {  //����������ͬɫ����
                        if (data[k][n] == chess.getColor()) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //�෴���򡪡���ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k >= 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {
                        spaceCount2++;
                        k--;
                        n++;
                    }
                    if (spaceCount2 == 1) {//�෴���򡪡�����һ����λ�ж���ͬɫ����
                        while ((k >= 1) && (n < GobangPanel.BT - 1) && (data[k][n] == chess.getColor())) {
                            chessLeft++;
                            k--;
                            n++;
                        }
                        while ((k >= 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {//��chessLeft֮���ж��ٿ�λ
                            chessLeftSpace++;
                            k--;
                            n++;
                        }
                    }
            }

            switch (chessCount) {
                case 5://����ͬɫ����
                    GongFive = true;
                    break;

                case 4://����ͬɫ����
                    if ((spaceCount1 > 0) && (spaceCount2 > 0)) { //���� 011110

                        //  0111101  1011110
                        if ((spaceCount1 == 1 && chessRight > 0) || (spaceCount2 == 1 && chessLeft > 0)) {
                            Chong_Four = true;
                        } else {
                            Huo_Four = true;
                        }

                    } else if ((spaceCount1 > 0 && spaceCount2 == 0)
                            || (spaceCount1 == 0 && spaceCount2 > 0)) {//����  #11110 01111#
                        Chong_Four = true;

                    }
                    break;
                case 3://����ͬɫ���� 111
                    // 1011101
                    if (((spaceCount1 == 1) && (chessRight == 1)) && ((spaceCount2 == 1) && (chessLeft == 1))) {
                        Huo_Four = true;
                    }
                    // 11101  10111
                    else if (((spaceCount1 == 1) && (chessRight == 1)) || ((spaceCount2 == 1) && (chessLeft == 1))) {
                        Chong_Four = true;
                    }
                    //  011100  001110
                    else if (((spaceCount1 > 1) && (spaceCount2 > 0)) || ((spaceCount1 > 0) && (spaceCount2 > 1))) {
                        Huo_Three = true;
                    }
                    break;
                case 2://����ͬɫ���� 11
                    //  11011011
                    if ((spaceCount1 == 1) && (chessRight == 2) && (spaceCount2 == 1) && (chessLeft == 2)) {
                        Huo_Four = true;
                    }
                    //  11011
                    else if (((spaceCount1 == 1) && (chessRight == 2)) || ((spaceCount2 == 1) && (chessLeft == 2))) {
                        Chong_Four = true;
                    }
                    //  011010  010110
                    else if (((spaceCount1 == 1) && (chessRight == 1) && (chessRightSpace > 0) && (spaceCount2 > 0))
                            || ((spaceCount2 == 1) && (chessLeft == 1)) && (chessLeftSpace > 0) && (spaceCount1 > 0)) {
                        Huo_Three = true;
                    }
                    break;

                case 1:
                    //  10111  11101
                    if (((spaceCount1 == 1) && (chessRight == 3)) || (spaceCount2 == 1) && (chessLeft == 3)) {
                        Chong_Four = true;
                    }
                    //  010110   011010
                    else if (((spaceCount1 == 1) && (chessRight == 2) && (chessRightSpace >= 1) && (spaceCount2 >= 1))
                            || ((spaceCount2 == 1) && (chessLeft == 2) && (chessLeftSpace >= 1) && (spaceCount1 >= 1))) {
                        Huo_Three = true;
                    }
                    break;

                default:
                    System.out.println("ƥ��ʧ��");
            }
        }

        if (!isAttack) {
//            chess.color = 3 - chess.color;//�ָ�������ɫ
            chess.setColor(3 - chess.getColor());
            data[chess.getX()][chess.getY()] = chess.getColor();
        }

        //���ظò�������ݱ�ע
        if (isAttack)//�����ж�
        {
            if (GongFive) {
                return "����";
            } else if (Huo_Four) {
                return "����";
            } else if (Chong_Four) {
                if (Huo_Three) {
                    return "���Ļ���";
                } else {
                    return "����";
                }
            } else if (Huo_Three) {
                return "����";
            }
        } else {//�����ж�
            if (GongFive) {
                return "������";
            } else if (Huo_Four) {
                return "������";
            } else if (Chong_Four) {
                if (Huo_Three) {
                    return "�����Ļ���";
                }

            }
        }
        return "##";//##����δƥ��ɹ�

    }

    /**
     * �ȽϹ�����Ϊ�ͷ�����Ϊ���Ըò���������ı�ע
     */
    public String Check(String strGong, String strFang) {
        if (strGong.equals("##") && strFang.equals("##")) {//�޹��޷�
            return "��ʽ";
        } else if (strGong.equals("##") || strFang.equals("##")) {
            if (strFang.equals("##"))//�й��޷�
            {
                return strGong;
            }
            if (strGong.equals("##"))//�з��޹�
            {
                return strFang;
            }
        } else {//�й��з�
            if (strFang.equals("������")) {
                if (strGong.equals("����")) {
                    return "ǿ����";
                } else if (strGong.equals("����")) {
                    return "ǿ����";
                }

            } else {
                return strFang+"��"+strGong;
            }
        }

        return "��ʽ";
    }


    //����������Ϊ״̬
    public void reset() {
        GongFive = false;
        Huo_Four = false;
        Huo_Three = false;
        Chong_Four = false;
    }


}
