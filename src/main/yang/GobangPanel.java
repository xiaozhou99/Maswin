package main.yang;

import main.lu.BinManual;
import main.lu.Chess;
import main.lu.VCF;
import main.ning.*;
import org.apache.poi.xssf.usermodel.XSSFCell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;

public class GobangPanel extends JPanel  {
    private static final long serialVersionUID = 667503661521167626L;
    private static final int OFFSETY = MainUI.iHeight/10;;// ����ƫ��
    private static final int OFFSETX = MainUI.iWidth/12;// ����ƫ��y 90
    private static int CELL_WIDTH = MainUI.iWidth/24;// �����
    public static final int BOARD_SIZE = 15;// ���̸���
    public static final int BT = BOARD_SIZE + 2;
    public static final int CENTER = BOARD_SIZE / 2 + 1;// ���ĵ�
    public static final int BLACK = 1;//����
    public static final int WHITE = 2;//����
    public static final int BORDER = -1;//�߽�
    public static final int EMPTY = 0;//����������

    public static final int ManMan = 0;// ˫��ģʽ
    public static final int ManAI = 1;// �˻�ģʽ

    public static final int HUMAN = 3;//������
    public static final int COMPUTER = 4;//���������



    public static boolean p = false;//����N��flag
    public static int t = 0;//����N��ѭ��

    public static boolean beat=false;
    protected static int[] standardList = new int[4];  //ѡ����ʽ���ֵ�ǰ���������еĵ�2��3���������꣨��һ��������Ԫ���ʲ��棩

    // VCF���ñ���
    private VCF vcf;  //VCF����
    private static ArrayList<Chess> chessSolve;   //���ҵ�VCF�⣬�ñ�����VCF��
    private static boolean vcfFlag;   //��ʾ�Ƿ��ҵ�vcf��
    private static int solveIndex;      //���ҵ�vcf�⣬��ʾ��ǰ�ߵ���ĵڼ���

    /**
     * history�洢�������������� ������ ����ʱ��
     * boardStatus�洢��ǰ���  �ж�ĳλ���Ƿ�����  �������� ��������ʱ��  ���������������ʣ�
     */
    public static Stack<Chess> history;// ������ʷ��¼��������������������
    public static int[][] boardData;//��ǰ�����̾��棬EMPTY��ʾ���ӣ�BLACK��ʾ���ӣ�WHITE��ʾ���ӣ�BORDER��ʾ�߽�
    private int[] lastStep;// ��һ�����ӵ㣬����Ϊ2�����飬��¼��һ�����ӵ������

    public static int currentPlayer = 0;// ��ǰ���ִ����ɫ��Ĭ��Ϊ����
    private static int computerSide = BLACK;// Ĭ�ϻ����ֺ�
    private static int humanSide = WHITE;
    static boolean isShowOrder = false;// ��ʾ����˳��
    public static boolean isGameOver = true;
    public static int initUser;// ����
    public static int VSMode;   //��սģʽ,ManMan=0 ����˫�˶�ս��ManAI=1�����˻���ս

    private JDialog settingTip;
    private MainUI mainUI = null;

    static boolean isShowManual = false; //�Ƿ��ǻ�����
    private Chess AIGoChess;//��������ӵ�

    private int minx, maxx, miny, maxy; // ��ǰ������������ӵ���Сx�����x����Сy�����y��������С�������ӵ�ķ�Χ

    private int cx = CENTER, cy = CENTER;

    public GobangPanel(){};
    Level level=new Level();
    public GobangPanel(MainUI mainUI, JDialog settingTip) throws Exception {
        boardData = new int[BT][BT];//1-15�洢����״̬��0 16Ϊ�߽�
        for (int i = 0; i < BT; i++) {
            for (int j = 0; j < BT; j++) {
                boardData[i][j] = EMPTY;
                if (i == 0 || i == BT - 1 || j == 0 || j == BT - 1)
                    boardData[i][j] = BORDER;// �߽�
            }
        }

        history = new Stack<>();
        lastStep = new int[2];
        vcf = new VCF();
        vcfFlag = false;
        chessSolve = new ArrayList<>();
        solveIndex = 0;
        this.settingTip = settingTip;
        this.mainUI=mainUI;
        addMouseMotionListener(mouseMotionListener);
        addMouseListener(mouseListener);
        setPreferredSize(new Dimension(getWidth(), getHeight()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    repaint();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // �Ƿ���ʾ����˳��
    public void toggleOrder() {
        isShowOrder = !isShowOrder;
    }

    // ����
    public void undo() {
        if (!history.isEmpty()) {
            if (history.size() == 1 && initUser == COMPUTER) {
                return;
            }
            Chess p1 = history.pop();
            boardData[p1.getX()][p1.getY()] = EMPTY;
            if (!history.isEmpty()) {
                Chess chess = history.peek();
                lastStep[0] = chess.getX();
                lastStep[1] = chess.getY();
            }
            togglePlayer();
        } else {
            lastStep[0] = BORDER;
            lastStep[1] = BORDER;
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font("April", Font.BOLD, 12));

        // ������ͼ
        ImageIcon icon = new ImageIcon("src/image/background.jpg");
        g2d.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);
        // ������
        drawBoard(g2d);
        // ����Ԫ����
        drawStar(g2d, CENTER, CENTER);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) * 3 / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) * 3 / 4);

        // �����ֺ���ĸ
        drawNumAndLetter(g2d);
        if (!isShowManual) {   //���Ǹ������ף�����������
            // ����ʾ��
            drawCell(g2d, cx, cy);
            // ����������
            for (Chess chess : history) {
                drawChess(g2d, chess.getX(), chess.getY(), chess.getColor());
            }
            // ��˳��
            if (isShowOrder) {
                drawOrder(g2d);
            } else {    // ����һ�����ӵ������ʾ
                if (lastStep[0] > 0 && lastStep[1] > 0) {
                    g2d.setColor(Color.RED);
                    g2d.fillRect((lastStep[0] - 1) * CELL_WIDTH + OFFSETX
                                    - CELL_WIDTH / 10, (lastStep[1] - 1) * CELL_WIDTH
                                    + OFFSETY - CELL_WIDTH / 10, CELL_WIDTH / 5,
                            CELL_WIDTH / 5);

                }
            }
        } else {   //����
            // ����������
            for (Chess chess : history) {
                drawChess(g2d, chess.getX(), chess.getY(), chess.getColor());
            }
            drawOrder(g2d);
        }

    }

    // ������
    private void drawBoard(Graphics g2d) {
        for (int x = 0; x < BOARD_SIZE; ++x) {   // ������
            g2d.drawLine(x * CELL_WIDTH + OFFSETX, OFFSETY, x * CELL_WIDTH
                    + OFFSETX, (BOARD_SIZE - 1) * CELL_WIDTH + OFFSETY);

        }
        for (int y = 0; y < BOARD_SIZE; ++y) {   // ������
            g2d.drawLine(OFFSETX, y * CELL_WIDTH + OFFSETY,
                    (BOARD_SIZE - 1) * CELL_WIDTH + OFFSETX, y
                            * CELL_WIDTH + OFFSETY);

        }
    }

    // ����Ԫ����
    private void drawStar(Graphics g2d, int cx, int cy) {
        g2d.fillOval((cx - 1) * CELL_WIDTH + OFFSETX - 4, (cy - 1) * CELL_WIDTH
                + OFFSETY - 4, 8, 8);
    }

    // �����ֺ���ĸ
    private void drawNumAndLetter(Graphics g2d) {
        FontMetrics fm = g2d.getFontMetrics();
        int stringWidth, stringAscent;
        stringAscent = fm.getAscent();
        for (int i = 1; i <= BOARD_SIZE; i++) {
            String num = String.valueOf(BOARD_SIZE - i + 1);
            stringWidth = fm.stringWidth(num);
            g2d.drawString(String.valueOf(BOARD_SIZE - i + 1), OFFSETX / 2    // ������
                    - stringWidth / 2, OFFSETY + (CELL_WIDTH * (i - 1))
                    + stringAscent / 2);

            String letter = String.valueOf((char) (64 + i));
            stringWidth = fm.stringWidth(letter);
            g2d.drawString(String.valueOf((char) (64 + i)), OFFSETX    //����ĸ
                    + (CELL_WIDTH * (i - 1)) - stringWidth / 2, OFFSETY * 3 / 4
                    + (OFFSETY -CELL_WIDTH/2) + CELL_WIDTH * (BOARD_SIZE - 1)
                    + stringAscent / 2);
        }
    }

    // ������
    private void drawChess(Graphics g2d, int cx, int cy, int color) {
        if (color == 0)
            return;
        int size = CELL_WIDTH * 5 / 6;
        if (color == BLACK) {
            ImageIcon icon1 = new ImageIcon("src/image/black.png");
            Image image1 = icon1.getImage();
            g2d.drawImage(image1, (cx - 1) * CELL_WIDTH + OFFSETX - size / 2, (cy - 1) * CELL_WIDTH - size / 2 + OFFSETY, size, size, null);
        }
        else if(color == WHITE){
            ImageIcon icon2 = new ImageIcon("src/image/white.png");
            Image image2 = icon2.getImage();
            g2d.drawImage(image2, (cx - 1) * CELL_WIDTH + OFFSETX - size / 2, (cy - 1) * CELL_WIDTH - size / 2 + OFFSETY, size, size,this);
        }
    }

    // ��Ԥѡ��
    private void drawCell(Graphics g2d, int x, int y) {
        int length = CELL_WIDTH / 4;
        int xx = (x - 1) * CELL_WIDTH + OFFSETX;     //��궨λֵ����Ԥѡ��λ��
        int yy = (y - 1) * CELL_WIDTH + OFFSETY;
        //����Ԥѡ��ֻ����������ʾ
        if((xx>= OFFSETX && xx<=(BOARD_SIZE+2)*CELL_WIDTH)&&(yy>= OFFSETY && yy <=(BOARD_SIZE+1)*CELL_WIDTH)) {
            int x1, y1, x2, y2, x3, y3, x4, y4;
            x1 = x4 = xx - CELL_WIDTH / 2;
            x2 = x3 = xx + CELL_WIDTH / 2;
            y1 = y2 = yy - CELL_WIDTH / 2;
            y3 = y4 = yy + CELL_WIDTH / 2;
            g2d.setColor(Color.RED);
            g2d.drawLine(x1, y1, x1 + length, y1);
            g2d.drawLine(x1, y1, x1, y1 + length);
            g2d.drawLine(x2, y2, x2 - length, y2);
            g2d.drawLine(x2, y2, x2, y2 + length);
            g2d.drawLine(x3, y3, x3 - length, y3);
            g2d.drawLine(x3, y3, x3, y3 - length);
            g2d.drawLine(x4, y4, x4 + length, y4);
            g2d.drawLine(x4, y4, x4, y4 - length);
        }
    }

    // ������˳��
    private void drawOrder(Graphics g2d) {
        if (history.size() > 0) {
            g2d.setColor(Color.RED);
            int i = 0;
            for (Chess chess : history) {
                int x = chess.getX();
                int y = chess.getY();
                String text = String.valueOf(i + 1);
                // ����
                FontMetrics fm = g2d.getFontMetrics();
                int stringWidth = fm.stringWidth(text);
                int stringAscent = fm.getAscent();
                g2d.drawString(text, (x - 1) * CELL_WIDTH + OFFSETX - stringWidth / 2,
                        (y - 1) * CELL_WIDTH + OFFSETY + stringAscent / 2);
                i++;
            }
        }
    }


    // ��ʼ��Ϸ
    public void startGame(int initUser, int VSMode, boolean isStandard) {
        this.initUser = initUser;
        this.VSMode = VSMode;
        reset();

        isGameOver = false;
        if (isShowManual) {
            isShowOrder = false;
        }
        isShowManual = false;
        lastStep[0] = lastStep[1] = 0;
        currentPlayer = Chess.BLACK;// Ĭ�Ϻ�������

        if (VSMode == ManAI)//�˻�
        {
            if (initUser == COMPUTER) {//��������
                putChess(8,8);// Ĭ�ϵ�һ����������
                minx = maxx = miny = maxy = 8;
            }
        }
        beat=false;
        p=false;
        t=0;
        vcfFlag = false;
        if (isStandard) {  // ���ѡ����ĳһ��ʽ���֣���ѵ�2��3������Ҳ����
            putChess(8,8);
            putChess(standardList[0], standardList[1]);
            putChess(standardList[2], standardList[3]);
        }
        standardList[0] = standardList[1] = standardList[2] = standardList[3] = 0;  //�����ʽ���������б�
    }


    // ����ƶ�
    private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
        public void mouseMoved(MouseEvent e) {
            int tx = Math.round((e.getX() - OFFSETX) * 1.0f / CELL_WIDTH) + 1;       //Math.round������Ĭ�ϼ���0.5֮������ȡ����
            int ty = Math.round((e.getY() - OFFSETY) * 1.0f / CELL_WIDTH) + 1;
            if (tx != cx || ty != cy) {
                if (tx >= 1 && tx <= (BOARD_SIZE+ OFFSETX) && ty >= 1
                        && ty <= (BOARD_SIZE+ OFFSETY)) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));      //Cursor�Ƿ�װ������λͼ��ʾ��ʽ����,HAND��״�������
                } else
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                cx = tx;
                cy = ty;
            }
        }
    };



    private MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (isGameOver) {       //��Ϸ������������
                //settingTip.setVisible(true);
                return;
            }
            int x = Math.round((e.getX() - OFFSETX) * 1.0f / CELL_WIDTH) + 1;
            int y = Math.round((e.getY() - OFFSETY) * 1.0f / CELL_WIDTH) + 1;

            if (cx >= 1 && cx <= BOARD_SIZE && cy >= 1 && cy <= BOARD_SIZE) {
                int mods = e.getModifiers();
                if ((mods & InputEvent.BUTTON1_MASK) != 0) {// ������
                    if (putChess(x, y)) {
                       if (VSMode == ManAI) {
                           if (!FiveBeat.beat()) {
                               if (vcfFlag) {  //�����ҵ�VCF�ⷨ�������ҵ��Ľⷨ��
                                   //��Ҫ��Ѱ���߷�����δ�����ⷨ�б�ߴ磬�Ұ׵��߷����Žⷨ�б��߷��ߣ�����Ӹ�����Ѱ���Ľ����
                                   if ((++solveIndex < chessSolve.size() && (x == chessSolve.get(solveIndex - 1).getX() && y == chessSolve.get(solveIndex - 1).getY()))) {
                                       putChess(chessSolve.get(solveIndex).getX(), chessSolve.get(solveIndex).getY());
                                       solveIndex++;
                                   } else {    // �������������������ʱ�õ��Ľ������ֱ������
                                       Chess goalchess = level.getonechess(currentPlayer, copyBoardData(), history);
                                       putChess(goalchess.getX(), goalchess.getY());
                                   }
                               } else {
                                   chessSolve = vcf.find_solution(currentPlayer); //��ʤ����û�ҵ�����ʼVCF����
                                   if (chessSolve.size() != 0) {
                                       vcfFlag = true;
                                       putChess(chessSolve.get(solveIndex).getX(), chessSolve.get(solveIndex).getY());
                                       solveIndex++;
                                   } else {
                                       Chess goalchess = level.getonechess(currentPlayer, copyBoardData(), history);
                                       putChess(goalchess.getX(), goalchess.getY());
                                   }
                               }
                           }
                       }
                    }

                }
            }
        }
    };

    /**
     * ���Ӻ���
     *
     * @param x
     * @param y
     * @return
     */

     boolean putChess(int x, int y) {
         if(MainUI.Jinshou.isSelected()) {

                 if ( beat) {//����N��Ĵ�������Ϊ����ѡ�����µĵ�λ
                     while (history.size() > 4) {
                         Chess chess = history.pop();
                         boardData[chess.getX()][chess.getY()] = 0;
                     }
                     history.push(new Chess(x, y, 1));
                     boardData[x][y] = 1;
                     currentPlayer = 2;
                     beat = false;
                     p=false;
                 }

         }


        if (boardData[x][y] == EMPTY) {
            // ����������Χ����
            minx = Math.min(minx, x);
            maxx = Math.max(maxx, x);
            miny = Math.min(miny, y);
            maxy = Math.max(maxy, y);
            boardData[x][y] = currentPlayer;
            history.push(new Chess(x, y, currentPlayer));

            String str = currentPlayer == WHITE ? "���壺" : "���壺";
            System.out.println(str + " ��" + (char) (64 + x) + (16 - y) + "��");


            lastStep[0] = x;// ������һ�����ӵ�
            lastStep[1] = y;

            togglePlayer();
            mainUI.restartCountTime();

            int winSide = isGameOver(boardData[x][y]);// �ж��վ�
            if (winSide > 0) {
                isGameOver = true;
                //ֹͣ��ʱ
                mainUI.stopWatch.stop();
                MainUI.startBtn.setEnabled(true);
                if (winSide == WHITE) {
                    JOptionPane.showMessageDialog(GobangPanel.this, "�׷�Ӯ�ˣ�");
                } else if (winSide == BLACK) {
                    JOptionPane.showMessageDialog(GobangPanel.this, "�ڷ�Ӯ�ˣ�");
                } else if(winSide != 777) {
                    JOptionPane.showMessageDialog(GobangPanel.this, "˫��ƽ��");
                }
                return false;
            }
            if(MainUI.Jinshou.isSelected()) {
                if (VSMode == ManAI) {
                    if (history.size() == 4) {
                        JOptionPane.showMessageDialog(GobangPanel.this, "�����������N��");
                        repaint();
                        p = true;
                    }
                }


                //���ֽ���
                if (VSMode == ManAI) {
                    if (history.size() == 3) {//��������Ϊ3ʱ�����н���ѡ��
                        int op = JOptionPane.showConfirmDialog(GobangPanel.this, "��ѡ���Ƿ����ֽ���", "���ֽ���", JOptionPane.YES_NO_OPTION);
                        //�����������
                        if (initUser == 4) {
                            if (op == 0) {//������������,������������������
                                mainUI.changeUserName();  //��������������ʾ�����û���
                                Chess chess = level.getonechess(currentPlayer, copyBoardData(), history);
                                history.push(new Chess(chess.getX(), chess.getY(), currentPlayer));
                                boardData[chess.getX()][chess.getY()] = currentPlayer;
                                togglePlayer();
                                lastStep[0] = chess.getX();// ������һ�����ӵ�
                                lastStep[1] = chess.getY();

                                repaint();
                                JOptionPane.showMessageDialog(GobangPanel.this, "�����������N��");
                                p = true;//��ʾ������N��ѭ��
                                initUser = 3;//�����������Ϊ����
                                return false;
                            }
                        }
                        //��������
                        if (initUser == 3) {
                            if (op == 0) {
                                initUser = 4;
                                return false;
                            }
                        }

                    }
                }
            }
            return true;
        }

        return false;
    }

    // �������ӽ�ɫ
    public static void togglePlayer() {
        currentPlayer = 3 - currentPlayer;
    }

    //��������״̬
    public void reset() {
        for (int i = 1; i < BT - 1; i++)
            for (int j = 1; j < BT - 1; j++) {
                boardData[i][j] = EMPTY;
            }
        history.clear();

        //mainUI.stopWatch.start();
    }

    /**
     * �ж�����Ƿ����
     *
     * @return 0�������У�1������Ӯ��2������Ӯ��3��ƽ��
     */

    public int isGameOver(int color) {
        if (!history.isEmpty()) {

            Chess lastStep = history.peek();
            int x = lastStep.getX();
            int y = lastStep.getY();

            //��������Ƿ��н���
            if(MainUI.Jinshou.isSelected()) {
                Chess firstStep = history.get(0);
                Chess step = history.get(history.size() - 1);
                if (step.getColor() == firstStep.getColor()) {
                    int i = Forbid.forbid(step,boardData);//�жϽ��ֵ�����
                    if (i == 1) {
                        JOptionPane.showMessageDialog(GobangPanel.this, "�����������֣�����ʧ��!");
                        return WHITE;
                    }
                    if (i == 2) {
                        JOptionPane.showMessageDialog(GobangPanel.this, "�������Ľ��֣�����ʧ��!");
                        return WHITE;
                    }
                    if (i == 3) {
                        JOptionPane.showMessageDialog(GobangPanel.this, "���ֳ������֣�����ʧ��!");
                        return WHITE;//��������
                    }

                }
            }

            // �ĸ�����
            if (check(x, y, 1, 0, color) + check(x, y, -1, 0, color) >= 4 ||
                    check(x, y, 0, 1, color) + check(x, y, 0, -1, color) >= 4 ||
                    check(x, y, 1, 1, color) + check(x, y, -1, -1, color) >= 4 ||
                    check(x, y, 1, -1, color) + check(x, y, -1, 1, color) >= 4) {
                return color;
            }

        }

        // ������
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j)
                if (boardData[i][j] == EMPTY) {
                    return 0;
                }
        }
        return 3;
    }

    // ������ͬ��ɫ���ӵ�����
    private int check(int x, int y, int dx, int dy, int color) {
        int sum = 0;
        for (int i = 0; i < 4; ++i) {
            x += dx;
            y += dy;
            if (x < 1 || x > BOARD_SIZE || y < 1 || y > BOARD_SIZE) {
                break;
            }
            if (boardData[x][y] == color) {
                sum++;
            } else {
                break;
            }
        }
        return sum;
    }

    // ��������
    public void drawManual(ArrayList<String> list, boolean isFast) {
        // ��һ�����������в�����
        int[][] records = new int[230][3]; //���������ڵ����Ӽ�¼��ÿ����¼�� ����˳�� x y ��ʽ�洢
        int x,y;

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            String s1 = s.substring(0,1), s2 = s.substring(1);

            records[i][0] = (i % 2 == 0) ? 1 : 2;
            x = s1.charAt(0) - 64;  //����ȡ����x����ת������ĸת��Ϊ����
            y = 16 - Integer.parseInt(s2);  //��ȡy����
            records[i][1] = x;
            records[i][2] = y;
        }

        //�ڶ����������������
        reset();
        //�������
        repaint();

        //��������������������ݣ�ˢ�½���
        for (int j = 0; j < list.size(); j++) {  // ��������������������boradData�����ݣ���Ϊ���̻����ӵĸ�����boardData������
            x = records[j][1];
            y = records[j][2];
            boardData[x][y] = records[j][0];
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(isFast)
        {
            setHistory(records,list.size()); // ����������������history����
        }
        else{
            slowShow(records,list.size());
        }
        isShowManual = true;   // ������ʾ���ױ�ǣ���������ˢ�½���ʱ����Ŀ��ѡ�������׶�������������
        isShowOrder = true;    // ��ʾ����˳��
    }


    // ���ݴ����array�������������¸�ֵ���Ӽ�¼historyջ����������ʱ��
    public void slowShow(int[][] array, int length) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                if (i < length) {
                    int x = array[i][1], y = array[i][2], c = array[i][0];
                    boardData[x][y] = c;
                    history.push(new Chess(x, y, c));
//                    history.push(new Chess(x, y, boardData[x][y]));
                    i++;
                } else {
                    timer.cancel();
                    System.gc();
                }
            }
        }, 0, 1 * 1000);
    }

    //����Ԫ�����ݲ�����
    public static String getString(XSSFCell cell) {
        if (cell == null) { //�����򷵻ؿ��ַ���
            return "";
        }
        return cell.getStringCellValue().trim(); //�����ַ�����ʽ���ص�Ԫ������
    }

    // ���ݴ����array�������������¸�ֵ���Ӽ�¼historyջ����������ʱ��
    public void setHistory(int[][] array,int length) {
        Chess p;
        for (int i = 0; i < length; i++) {
            int x = array[i][1],y = array[i][2];
            p = new Chess(x,y,boardData[x][y]);
            history.push(p);
        }
    }

    public void addHistory(ArrayList<Chess> list) {
        for (Chess chess : list) {
            history.push(chess);
        }
    }

    public void showVCF() {
        ArrayList<Chess> result;
        if (history.size() % 2 == 0)
            result = vcf.find_solution(1);
        else
            result = vcf.find_solution(2);
        int[][] temp = new int[result.size()][3];
        for (int i = 0; i < result.size(); i++) {
            temp[i][0] = result.get(i).getColor();
            temp[i][1] = result.get(i).getX();
            temp[i][2] = result.get(i).getY();
        }
        if (result.size() == 0) {
            JOptionPane.showMessageDialog(this, "VCF�޽�");
        } else {
//            for (Chess chess : result) {
//                boardData[chess.getX()][chess.getY()] = chess.getColor();
//            }
//            addHistory(result);
//            isShowManual = true;
//            isShowOrder = true;
//
//            repaint();
            slowShow(temp,temp.length);
        }
        isGameOver = false;
        currentPlayer = ((history.size() + temp.length) % 2 == 0) ? 1 : 2;  // history������Ϊ��ʱ��ԭ����ʱȡ����ֵ��δ���£�

    }

    public void saveBin() {
        BinManual binManual = new BinManual();
        try {
            binManual.saveSingleGameToBin(history,"src/resources/VCF.bin");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGame(boolean isFast, Stack<Chess> chessList) {
        reset();
        isShowManual = true;   // ������ʾ���ױ�ǣ���������ˢ�½���ʱ����Ŀ��ѡ�������׶�������������
        isShowOrder = true;    // ��ʾ����˳��
        if(isFast)
        {
            for (Chess chess:chessList) {
                history.push(chess);
                boardData[chess.getX()][chess.getY()]=chess.getColor();
            }
        }
        else{
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int i = 0;
                @Override
                public void run() {
                    if (i < chessList.size()) {
                        Chess chess=chessList.get(i);
                        history.push(chess);
                        boardData[chess.getX()][chess.getY()]=chess.getColor();
                        i++;
                    } else {
                        timer.cancel();
                        System.gc();
                    }
                }
            }, 0, 1 * 1000);
        }
    }

    public void setStandardList() {
        String str[] = {"���Ǿ�","Ϫ�¾�","���Ǿ�", "���¾�","���¾�","���¾�","���Ǿ�","���¾�","���¾�","���¾�","���Ǿ�","ɽ�¾�","���Ǿ�","���Ǿ�","Ͽ�¾�","���Ǿ�","ˮ�¾�","���Ǿ�","���¾�","���¾�","��¾�","���¾�","���Ǿ�","б�¾�","���¾�","���Ǿ�"};
        String chosenTitle = MainUI.chooseBtn.getText().trim().substring(2,4);

        int chosenIndex = -1;
        for (int i = 0; i < str.length; i++) {
            if (chosenTitle.equals(str[i].substring(0,2))) {
                chosenIndex = i;
            }
        }
        if (chosenIndex == -1)
            return;
        int[][] coList = {
                {8,7,8,6}, {8,7,9,6}, {8,7,10,6}, {8,7,9,7}, {8,7,10,7}, {8,7,9,8}, {8,7,10,8},
                {8,7,8,9}, {8,7,9,9}, {8,7,10,9}, {8,7,8,10}, {8,7,9,10}, {8,7,6,10}, {9,7,10,6},
                {9,7,10,7}, {9,7,10,8}, {9,7,10,9}, {9,7,10,10}, {9,7,9,8}, {9,7,9,9}, {9,7,9,10},
                {9,7,8,9}, {9,7,8,10}, {9,7,7,9}, {9,7,7,10}, {9,7,6,10}
        };

        standardList = coList[chosenIndex];
    }
    public static int[][] copyBoardData() {
        int[][] copydata=new int[BT][BT];
        for (int i = 0; i < BT; i++) {
            for (int j = 0; j < BT; j++) {
                copydata[i][j] =boardData[i][j];
            }
        }
        return copydata;
    }
}
