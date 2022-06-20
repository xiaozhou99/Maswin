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
    private static final int OFFSETY = MainUI.iHeight/10;;// 棋盘偏移
    private static final int OFFSETX = MainUI.iWidth/12;// 棋盘偏移y 90
    private static int CELL_WIDTH = MainUI.iWidth/24;// 棋格宽度
    public static final int BOARD_SIZE = 15;// 棋盘格数
    public static final int BT = BOARD_SIZE + 2;
    public static final int CENTER = BOARD_SIZE / 2 + 1;// 中心点
    public static final int BLACK = 1;//黑棋
    public static final int WHITE = 2;//白棋
    public static final int BORDER = -1;//边界
    public static final int EMPTY = 0;//棋盘上无子

    public static final int ManMan = 0;// 双人模式
    public static final int ManAI = 1;// 人机模式

    public static final int HUMAN = 3;//人先手
    public static final int COMPUTER = 4;//计算机先手



    public static boolean p = false;//五手N打flag
    public static int t = 0;//五手N打循环

    public static boolean beat=false;
    protected static int[] standardList = new int[4];  //选定定式开局的前三颗棋子中的第2、3步棋子坐标（第一步总在天元，故不存）

    // VCF所用变量
    private VCF vcf;  //VCF对象
    private static ArrayList<Chess> chessSolve;   //若找到VCF解，该变量存VCF解
    private static boolean vcfFlag;   //表示是否找到vcf解
    private static int solveIndex;      //若找到vcf解，表示当前走到解的第几步

    /**
     * history存储棋盘上所有棋子 画棋子 悔棋时用
     * boardStatus存储当前棋局  判定某位置是否有棋  五子连珠 局面评估时用  （数组可以随机访问）
     */
    public static Stack<Chess> history;// 落子历史记录，储存棋盘上所有棋子
    public static int[][] boardData;//当前的棋盘局面，EMPTY表示无子，BLACK表示黑子，WHITE表示白子，BORDER表示边界
    private int[] lastStep;// 上一个落子点，长度为2的数组，记录上一个落子点的坐标

    public static int currentPlayer = 0;// 当前玩家执棋颜色，默认为无子
    private static int computerSide = BLACK;// 默认机器持黑
    private static int humanSide = WHITE;
    static boolean isShowOrder = false;// 显示落子顺序
    public static boolean isGameOver = true;
    public static int initUser;// 先手
    public static int VSMode;   //对战模式,ManMan=0 代表双人对战，ManAI=1代表人机对战

    private JDialog settingTip;
    private MainUI mainUI = null;

    static boolean isShowManual = false; //是否是画棋谱
    private Chess AIGoChess;//计算机落子点

    private int minx, maxx, miny, maxy; // 当前棋局下所有棋子的最小x，最大x，最小y，最大y，用于缩小搜索落子点的范围

    private int cx = CENTER, cy = CENTER;

    public GobangPanel(){};
    Level level=new Level();
    public GobangPanel(MainUI mainUI, JDialog settingTip) throws Exception {
        boardData = new int[BT][BT];//1-15存储棋子状态，0 16为边界
        for (int i = 0; i < BT; i++) {
            for (int j = 0; j < BT; j++) {
                boardData[i][j] = EMPTY;
                if (i == 0 || i == BT - 1 || j == 0 || j == BT - 1)
                    boardData[i][j] = BORDER;// 边界
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

    // 是否显示落子顺序
    public void toggleOrder() {
        isShowOrder = !isShowOrder;
    }

    // 悔棋
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

        // 画背景图
        ImageIcon icon = new ImageIcon("src/image/background.jpg");
        g2d.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);
        // 画棋盘
        drawBoard(g2d);
        // 画天元和星
        drawStar(g2d, CENTER, CENTER);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) * 3 / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) * 3 / 4);

        // 画数字和字母
        drawNumAndLetter(g2d);
        if (!isShowManual) {   //不是复现棋谱，即正常下棋
            // 画提示框
            drawCell(g2d, cx, cy);
            // 画所有棋子
            for (Chess chess : history) {
                drawChess(g2d, chess.getX(), chess.getY(), chess.getColor());
            }
            // 画顺序
            if (isShowOrder) {
                drawOrder(g2d);
            } else {    // 将上一个落子点标红点显示
                if (lastStep[0] > 0 && lastStep[1] > 0) {
                    g2d.setColor(Color.RED);
                    g2d.fillRect((lastStep[0] - 1) * CELL_WIDTH + OFFSETX
                                    - CELL_WIDTH / 10, (lastStep[1] - 1) * CELL_WIDTH
                                    + OFFSETY - CELL_WIDTH / 10, CELL_WIDTH / 5,
                            CELL_WIDTH / 5);

                }
            }
        } else {   //复现
            // 画所有棋子
            for (Chess chess : history) {
                drawChess(g2d, chess.getX(), chess.getY(), chess.getColor());
            }
            drawOrder(g2d);
        }

    }

    // 画棋盘
    private void drawBoard(Graphics g2d) {
        for (int x = 0; x < BOARD_SIZE; ++x) {   // 画竖线
            g2d.drawLine(x * CELL_WIDTH + OFFSETX, OFFSETY, x * CELL_WIDTH
                    + OFFSETX, (BOARD_SIZE - 1) * CELL_WIDTH + OFFSETY);

        }
        for (int y = 0; y < BOARD_SIZE; ++y) {   // 画横线
            g2d.drawLine(OFFSETX, y * CELL_WIDTH + OFFSETY,
                    (BOARD_SIZE - 1) * CELL_WIDTH + OFFSETX, y
                            * CELL_WIDTH + OFFSETY);

        }
    }

    // 画天元和星
    private void drawStar(Graphics g2d, int cx, int cy) {
        g2d.fillOval((cx - 1) * CELL_WIDTH + OFFSETX - 4, (cy - 1) * CELL_WIDTH
                + OFFSETY - 4, 8, 8);
    }

    // 画数字和字母
    private void drawNumAndLetter(Graphics g2d) {
        FontMetrics fm = g2d.getFontMetrics();
        int stringWidth, stringAscent;
        stringAscent = fm.getAscent();
        for (int i = 1; i <= BOARD_SIZE; i++) {
            String num = String.valueOf(BOARD_SIZE - i + 1);
            stringWidth = fm.stringWidth(num);
            g2d.drawString(String.valueOf(BOARD_SIZE - i + 1), OFFSETX / 2    // 画数字
                    - stringWidth / 2, OFFSETY + (CELL_WIDTH * (i - 1))
                    + stringAscent / 2);

            String letter = String.valueOf((char) (64 + i));
            stringWidth = fm.stringWidth(letter);
            g2d.drawString(String.valueOf((char) (64 + i)), OFFSETX    //画字母
                    + (CELL_WIDTH * (i - 1)) - stringWidth / 2, OFFSETY * 3 / 4
                    + (OFFSETY -CELL_WIDTH/2) + CELL_WIDTH * (BOARD_SIZE - 1)
                    + stringAscent / 2);
        }
    }

    // 画棋子
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

    // 画预选框
    private void drawCell(Graphics g2d, int x, int y) {
        int length = CELL_WIDTH / 4;
        int xx = (x - 1) * CELL_WIDTH + OFFSETX;     //鼠标定位值设置预选框位置
        int yy = (y - 1) * CELL_WIDTH + OFFSETY;
        //设置预选框只在棋盘上显示
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

    // 画落子顺序
    private void drawOrder(Graphics g2d) {
        if (history.size() > 0) {
            g2d.setColor(Color.RED);
            int i = 0;
            for (Chess chess : history) {
                int x = chess.getX();
                int y = chess.getY();
                String text = String.valueOf(i + 1);
                // 居中
                FontMetrics fm = g2d.getFontMetrics();
                int stringWidth = fm.stringWidth(text);
                int stringAscent = fm.getAscent();
                g2d.drawString(text, (x - 1) * CELL_WIDTH + OFFSETX - stringWidth / 2,
                        (y - 1) * CELL_WIDTH + OFFSETY + stringAscent / 2);
                i++;
            }
        }
    }


    // 开始游戏
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
        currentPlayer = Chess.BLACK;// 默认黑子先行

        if (VSMode == ManAI)//人机
        {
            if (initUser == COMPUTER) {//电脑先手
                putChess(8,8);// 默认第一步落在中心
                minx = maxx = miny = maxy = 8;
            }
        }
        beat=false;
        p=false;
        t=0;
        vcfFlag = false;
        if (isStandard) {  // 如果选择了某一定式开局，则把第2、3步落子也下了
            putChess(8,8);
            putChess(standardList[0], standardList[1]);
            putChess(standardList[2], standardList[3]);
        }
        standardList[0] = standardList[1] = standardList[2] = standardList[3] = 0;  //清除定式开局棋子列表
    }


    // 鼠标移动
    private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
        public void mouseMoved(MouseEvent e) {
            int tx = Math.round((e.getX() - OFFSETX) * 1.0f / CELL_WIDTH) + 1;       //Math.round函数是默认加上0.5之后，向下取整。
            int ty = Math.round((e.getY() - OFFSETY) * 1.0f / CELL_WIDTH) + 1;
            if (tx != cx || ty != cy) {
                if (tx >= 1 && tx <= (BOARD_SIZE+ OFFSETX) && ty >= 1
                        && ty <= (BOARD_SIZE+ OFFSETY)) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));      //Cursor是封装鼠标光标的位图表示形式的类,HAND手状光标种类
                } else
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                cx = tx;
                cy = ty;
            }
        }
    };



    private MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (isGameOver) {       //游戏结束则不能落子
                //settingTip.setVisible(true);
                return;
            }
            int x = Math.round((e.getX() - OFFSETX) * 1.0f / CELL_WIDTH) + 1;
            int y = Math.round((e.getY() - OFFSETY) * 1.0f / CELL_WIDTH) + 1;

            if (cx >= 1 && cx <= BOARD_SIZE && cy >= 1 && cy <= BOARD_SIZE) {
                int mods = e.getModifiers();
                if ((mods & InputEvent.BUTTON1_MASK) != 0) {// 鼠标左键
                    if (putChess(x, y)) {
                       if (VSMode == ManAI) {
                           if (!FiveBeat.beat()) {
                               if (vcfFlag) {  //若已找到VCF解法，则按着找到的解法走
                                   //所要搜寻的走法索引未超过解法列表尺寸，且白的走法按着解法列表走法走，则黑子根据搜寻到的结果走
                                   if ((++solveIndex < chessSolve.size() && (x == chessSolve.get(solveIndex - 1).getX() && y == chessSolve.get(solveIndex - 1).getY()))) {
                                       putChess(chessSolve.get(solveIndex).getX(), chessSolve.get(solveIndex).getY());
                                       solveIndex++;
                                   } else {    // 否则调用评估函数，此时得到的结果往往直接连五
                                       Chess goalchess = level.getonechess(currentPlayer, copyBoardData(), history);
                                       putChess(goalchess.getX(), goalchess.getY());
                                   }
                               } else {
                                   chessSolve = vcf.find_solution(currentPlayer); //必胜棋谱没找到，开始VCF搜索
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
     * 落子函数
     *
     * @param x
     * @param y
     * @return
     */

     boolean putChess(int x, int y) {
         if(MainUI.Jinshou.isSelected()) {

                 if ( beat) {//五手N打的处理，人作为后手选择留下的点位
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
            // 棋盘搜索范围限制
            minx = Math.min(minx, x);
            maxx = Math.max(maxx, x);
            miny = Math.min(miny, y);
            maxy = Math.max(maxy, y);
            boardData[x][y] = currentPlayer;
            history.push(new Chess(x, y, currentPlayer));

            String str = currentPlayer == WHITE ? "白棋：" : "黑棋：";
            System.out.println(str + " 【" + (char) (64 + x) + (16 - y) + "】");


            lastStep[0] = x;// 保存上一步落子点
            lastStep[1] = y;

            togglePlayer();
            mainUI.restartCountTime();

            int winSide = isGameOver(boardData[x][y]);// 判断终局
            if (winSide > 0) {
                isGameOver = true;
                //停止计时
                mainUI.stopWatch.stop();
                MainUI.startBtn.setEnabled(true);
                if (winSide == WHITE) {
                    JOptionPane.showMessageDialog(GobangPanel.this, "白方赢了！");
                } else if (winSide == BLACK) {
                    JOptionPane.showMessageDialog(GobangPanel.this, "黑方赢了！");
                } else if(winSide != 777) {
                    JOptionPane.showMessageDialog(GobangPanel.this, "双方平手");
                }
                return false;
            }
            if(MainUI.Jinshou.isSelected()) {
                if (VSMode == ManAI) {
                    if (history.size() == 4) {
                        JOptionPane.showMessageDialog(GobangPanel.this, "黑棋进行五手N打");
                        repaint();
                        p = true;
                    }
                }


                //三手交换
                if (VSMode == ManAI) {
                    if (history.size() == 3) {//棋子数量为3时，进行交换选择
                        int op = JOptionPane.showConfirmDialog(GobangPanel.this, "请选择是否三手交换", "三手交换", JOptionPane.YES_NO_OPTION);
                        //机器先手情况
                        if (initUser == 4) {
                            if (op == 0) {//点击交换的情况,若不交换，正常运行
                                mainUI.changeUserName();  //若交换，界面显示交换用户名
                                Chess chess = level.getonechess(currentPlayer, copyBoardData(), history);
                                history.push(new Chess(chess.getX(), chess.getY(), currentPlayer));
                                boardData[chess.getX()][chess.getY()] = currentPlayer;
                                togglePlayer();
                                lastStep[0] = chess.getX();// 保存上一步落子点
                                lastStep[1] = chess.getY();

                                repaint();
                                JOptionPane.showMessageDialog(GobangPanel.this, "黑棋进行五手N打");
                                p = true;//表示进入了N打循环
                                initUser = 3;//交换后人类变为先手
                                return false;
                            }
                        }
                        //人类先手
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

    // 交换落子角色
    public static void togglePlayer() {
        currentPlayer = 3 - currentPlayer;
    }

    //重置棋盘状态
    public void reset() {
        for (int i = 1; i < BT - 1; i++)
            for (int j = 1; j < BT - 1; j++) {
                boardData[i][j] = EMPTY;
            }
        history.clear();

        //mainUI.stopWatch.start();
    }

    /**
     * 判断棋局是否结束
     *
     * @return 0：进行中，1：黑棋赢，2：白棋赢，3：平手
     */

    public int isGameOver(int color) {
        if (!history.isEmpty()) {

            Chess lastStep = history.peek();
            int x = lastStep.getX();
            int y = lastStep.getY();

            //检查先手是否有禁手
            if(MainUI.Jinshou.isSelected()) {
                Chess firstStep = history.get(0);
                Chess step = history.get(history.size() - 1);
                if (step.getColor() == firstStep.getColor()) {
                    int i = Forbid.forbid(step,boardData);//判断禁手的类型
                    if (i == 1) {
                        JOptionPane.showMessageDialog(GobangPanel.this, "出现三三禁手，黑子失败!");
                        return WHITE;
                    }
                    if (i == 2) {
                        JOptionPane.showMessageDialog(GobangPanel.this, "出现四四禁手，黑子失败!");
                        return WHITE;
                    }
                    if (i == 3) {
                        JOptionPane.showMessageDialog(GobangPanel.this, "出现长连禁手，黑子失败!");
                        return WHITE;//结束程序
                    }

                }
            }

            // 四个方向
            if (check(x, y, 1, 0, color) + check(x, y, -1, 0, color) >= 4 ||
                    check(x, y, 0, 1, color) + check(x, y, 0, -1, color) >= 4 ||
                    check(x, y, 1, 1, color) + check(x, y, -1, -1, color) >= 4 ||
                    check(x, y, 1, -1, color) + check(x, y, -1, 1, color) >= 4) {
                return color;
            }

        }

        // 进行中
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j)
                if (boardData[i][j] == EMPTY) {
                    return 0;
                }
        }
        return 3;
    }

    // 计算相同颜色连子的数量
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

    // 复现棋谱
    public void drawManual(ArrayList<String> list, boolean isFast) {
        // 第一步，从棋谱中并保存
        int[][] records = new int[230][3]; //保存棋谱内的落子记录，每条记录以 落子顺序 x y 形式存储
        int x,y;

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            String s1 = s.substring(0,1), s2 = s.substring(1);

            records[i][0] = (i % 2 == 0) ? 1 : 2;
            x = s1.charAt(0) - 64;  //将获取到的x坐标转换由字母转换为数字
            y = 16 - Integer.parseInt(s2);  //获取y坐标
            records[i][1] = x;
            records[i][2] = y;
        }

        //第二步，清空棋盘内容
        reset();
        //清空棋盘
        repaint();

        //第三步，重设各变量数据，刷新界面
        for (int j = 0; j < list.size(); j++) {  // 根据棋谱内容重新设置boradData的数据，因为棋盘画棋子的根据是boardData的数据
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
            setHistory(records,list.size()); // 根据棋谱重新设置history内容
        }
        else{
            slowShow(records,list.size());
        }
        isShowManual = true;   // 设置显示棋谱标记，用于区分刷新界面时，项目会选择复现棋谱而不是正常下棋
        isShowOrder = true;    // 显示落子顺序
    }


    // 根据传入的array即棋谱内容重新赋值落子记录history栈，复现棋谱时用
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

    //处理单元格内容并返回
    public static String getString(XSSFCell cell) {
        if (cell == null) { //若空则返回空字符串
            return "";
        }
        return cell.getStringCellValue().trim(); //否则按字符串形式返回单元格内容
    }

    // 根据传入的array即棋谱内容重新赋值落子记录history栈，复现棋谱时用
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
            JOptionPane.showMessageDialog(this, "VCF无解");
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
        currentPlayer = ((history.size() + temp.length) % 2 == 0) ? 1 : 2;  // history可能因为延时的原因获此时取到的值都未更新？

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
        isShowManual = true;   // 设置显示棋谱标记，用于区分刷新界面时，项目会选择复现棋谱而不是正常下棋
        isShowOrder = true;    // 显示落子顺序
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
        String str[] = {"寒星局","溪月局","疏星局", "花月局","残月局","雨月局","金星局","松月局","丘月局","新月局","瑞星局","山月局","游星局","长星局","峡月局","恒星局","水月局","流星局","云月局","浦月局","岚月局","银月局","明星局","斜月局","名月局","彗星局"};
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
