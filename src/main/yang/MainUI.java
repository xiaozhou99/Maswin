package main.yang;

import main.lu.Chess;
import main.lu.GameDialog;
import main.zhou.DataAnnotations;
import main.zhou.GameHistory;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.lang3.time.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

import static main.yang.GobangPanel.*;

public class MainUI<str> extends JFrame {
    Font f = new Font("楷体", Font.BOLD,18);
    public MainUI() throws Exception {
        properties = ReadConfig();                         //配置文件
        initComponents();
        this.stopWatch = StopWatch.createStarted();     //设置下棋计时功能
        stopWatch.stop();
    }

    //声明变量，保证访问数据的一致性
    public static int iWidth;
    public static int iHeight;
    public StopWatch stopWatch;     //实现计时功能
    private Timer timer = null;
    //public static HashMap map;              //配置文件接收为哈希表结构
    public static Properties properties = null;

    private JLabel n_timesLabel;        //选择五手n打标签
    private static JComboBox<String> n_times_value;    //五手n打选择下拉框

    private JMenuBar jMenuBar;      //菜单
    public static JMenu jMenu1;           //开局设置
    private JRadioButtonMenuItem Man_AI;    //人机对战
    private JRadioButtonMenuItem Man_Man;   //人人对战
    private JRadioButtonMenuItem AI_AI;     //机机对战
    public static JMenu jMenu2;           //对局设置
    public static JMenu jMenu3;           //复盘棋局
    private JMenuItem VCFManul;          //VCF棋谱单选菜单栏
    private JMenuItem historyManul;      //历史棋谱菜单栏
    private JMenu jMenu4;           //规则

    private Panel panel1;       //显示对局信息
    private JLabel playerA;
    private JLabel playerB;
    private JLabel showA;       //顶端显示玩家信息
    private JLabel showB;
    private JTextField userAName;   //设置玩家A姓名
    private JTextField userBName;
    private JTextField userATime;   //顶端玩家A、B 计时设置
    private JTextField userBTime;
    private JButton submitBtn;      //设置玩家信息提交按钮
    private JButton backBtn;        //设置玩家信息返回按钮
    private JTextField jTextField1;
    private JTextField jTextField2;
    private JTextField jTextField3;
    private JTextField jTextField4;

    private GobangPanel gobangPanel;//棋盘面板
    private Panel panel2;       //操作面板
    private Panel rightPane;    //右边面板
    public static JButton startBtn;   //开始游戏
    private JButton undoBtn;    //悔棋按钮
    private JCheckBox orderBtn; //显示落子顺序按钮
    public static JButton saveGameBtn;//保存棋谱
    private JButton giveUpBtn;  //和棋
    private JButton gameOverBtn;//认输
    private JButton VCFBtn; //VCF找解

    private JDialog settingTip = new JDialog(this, "规则设置", true);       //规则设置弹窗
    private JDialog saveTip = new JDialog(this, "保存设置", true);       //规则设置弹窗
    private JFrame settingUser = null;         //设置玩家A、B用户名弹框
    private JLabel jLabel1;         //玩家A选择黑白棋子标签
    private ButtonGroup grp_alg;    //选择先手按钮组
    private JRadioButton A;
    private JRadioButton B;
    private JLabel jLabel2;         //禁手选择标签
    private ButtonGroup VsMode_alg; //有无禁手按钮组
    public static JRadioButton Jinshou;   //有禁手（指定开局）
    private JRadioButton Free;      //无禁手（自由开局）
    private Label jLabel3;              //选择开局标签
    private JButton setting_ok;
    private JButton save_ok;
    public static JButton chooseBtn;

    //保存棋谱弹窗设置
    private JRadioButton VCFRadio;  //VCF棋谱
    private JRadioButton historyRadio;  //历史对局
    private ButtonGroup manualBtnGroup;  //弹窗上的按钮组
    private JButton save_okBtn; //确定按钮
    private JLabel selectManualLabel;   //标签

//    //加载配置文件
//    public HashMap<String, String> ReadConfig() throws IOException{
//        File file = new File("src/resources/config.txt");
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        //ArrayList result = new ArrayList<>();
//        String str = null;
//        String[] result;
//        HashMap<String,String> map=new HashMap<>();     //将txt文件的内容映射为哈希表
//        while ((str=reader.readLine()) !=null){          //读取文件中的每一行
//            if (str.charAt(0)!='#'){
//                 result = str.split("：");     //以：为分割
//                map.put(result[0],result[1]);
//            }
//        }
//        Set keys = map.keySet();
//        for (Object obj : keys) {
//            String key = (String) obj;
//            String value = map.get(key);
//            //System.out.println(key+":"+value);
//        }
//        return map;
//    }
    /*加载properties配置文件*/
    public Properties ReadConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/resources/config.properties", StandardCharsets.UTF_8));        //加载配置文件
        Set keys = properties.keySet();

        for (Object obj : keys) {
            String key = (String) obj;
            String value = properties.getProperty(key);
            System.out.println(key + "=" + value);
        }
        return properties;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() throws Exception {
        //页面区域变量声明
        jMenuBar = new JMenuBar();  //设置菜单组件
        jMenu1 = new JMenu();
        jMenu2 = new JMenu();
        jMenu3 = new JMenu();
        jMenu4 = new JMenu();
        Man_AI = new JRadioButtonMenuItem();
        Man_AI.setSelected(true);               //默认选择人机对战
        Man_Man = new JRadioButtonMenuItem();
        AI_AI = new JRadioButtonMenuItem();

        VCFManul = new JMenuItem("VCF棋谱");
        historyManul = new JMenuItem("历史棋谱");

        playerA = new JLabel("玩家A");
        playerA.setBorder(BorderFactory.createEtchedBorder());
        playerA.setFont(f);
        playerA.setForeground(Color.black);
        playerB = new JLabel("玩家B");
        playerB.setBorder(BorderFactory.createEtchedBorder());
        playerB.setFont(f);
        playerB.setForeground(Color.black);

        //面板顶端传入玩家姓名、执棋情况
        showA = new JLabel(properties.getProperty("userAName"));
        ImageIcon image1 = new ImageIcon("src/image/black.png");
        image1.setImage(image1.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT ));      //调整标签前棋子大小
        showA.setIcon(image1);
        showA.setFont(f);
        showB = new JLabel(properties.getProperty("userBName"));
        ImageIcon image2 = new ImageIcon("src/image/white.png");//实例化ImageIcon 对象
        image2.setImage(image2.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT ));
        showB.setIcon(image2);
        showB.setFont(f);
        userATime = new JTextField("00:00",12);
        userBTime = new JTextField("00:00",12);
        userATime.setEditable(false);
        userBTime.setEditable(false);

        grp_alg = new ButtonGroup();    //选择先后手按钮组
        VsMode_alg = new ButtonGroup();     //有无禁手按钮组


        rightPane = new Panel();       //右面板
        panel2 = new Panel();           //操作面板

        startBtn = new JButton();
        undoBtn = new JButton();
        orderBtn = new JCheckBox();     //设置复选框，显示下棋顺序

        saveGameBtn = new JButton("保存");
        giveUpBtn = new JButton("和棋");
        gameOverBtn = new JButton("认输");
        VCFBtn = new JButton("VCF");
        n_timesLabel = new JLabel("选择五手N打次数：");
        n_times_value = new JComboBox<>();

        //按钮设置、弹框设置
        jLabel2 = new JLabel("开局选择：");
        Free = new JRadioButton("无禁手（自由开局）");
        Jinshou = new JRadioButton("有禁手（指定开局）");
        jLabel3 =  new Label();

        //玩家设置弹窗
        jLabel1 = new JLabel();
        A = new JRadioButton();     //玩家A、B单选按钮组件,选择执棋情况
        B = new JRadioButton();
        submitBtn = new JButton("提交");
        backBtn = new JButton("返回");


        //游戏设置弹窗
        settingTip = new JDialog(this, "游戏设置", true);
        settingTip.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        settingTip.setSize(400, 200);
        settingTip.setLocationRelativeTo(gobangPanel);
        setting_ok = new JButton("确定");
        chooseBtn = new JButton("选择定式开局");
        n_times_value.setModel(new DefaultComboBoxModel<>(new String[]{"2", "3", "4", "5"}));

        // 保存棋谱弹窗设置
        saveTip = new JDialog(this, "保存设置", true);
        saveTip.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        saveTip.setLocationRelativeTo(gobangPanel);
        manualBtnGroup = new ButtonGroup();
        historyRadio = new JRadioButton("历史棋局");
        historyRadio.setSelected(true);
        VCFRadio = new JRadioButton("VCF棋谱");
        selectManualLabel = new JLabel("选择保存类型：");
        save_okBtn = new JButton("确定");
        saveTip.setSize(400, 150);
        manualBtnGroup.add(historyRadio);
        manualBtnGroup.add(VCFRadio);


        //显示对局信息区域,按照配置文件设置初始值
        panel1 = new Panel();
        jTextField1 = new JTextField(properties.getProperty("VSMode"));
        jTextField2 = new JTextField(properties.getProperty("Begin"));
        jTextField3 = new JTextField(properties.getProperty("Rule"));
        jTextField4 = new JTextField(properties.getProperty("n_time_value"));

        //通过获取屏幕分辨率来设置面板大小
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        iHeight = (screenSize.height*6)/8;  //窗体大小的宽、高
        iWidth =(screenSize.height*17)/18;
        //iWidth = (screenSize.width*3)/5;
        setPreferredSize(new Dimension(iWidth, iHeight));     //设置百分比大小
        ImageIcon icon = new ImageIcon("src/image/logo.png");   //设置左上角游戏图标
        setIconImage(icon.getImage());
        setResizable(false);        //设置窗体大小不能随便改变
        int h = screenSize.height/2- iHeight /2;
        int w = screenSize.width/2- iWidth /2;
        setBounds(w,h, iWidth, iHeight);
        //System.out.println(" 高："+iHeight+"   宽： "+iWidth+"  "+w+" "+h);
        gobangPanel = new GobangPanel(this, settingTip);    //设置主体窗口

        //设置操作面板相关功能
        panel2.setBorder(BorderFactory.createTitledBorder("操作"));
        //设置可选框按钮 先手电脑、人，默认先手为黑子
        jLabel1.setText("执黑棋：");
        grp_alg.add(A);
        A.setText("玩家A");
        A.setSelected(true);
        grp_alg.add(B);
        B.setText("玩家B");
        VsMode_alg.add(Free);
        VsMode_alg.add(Jinshou);
        Jinshou.setSelected(true);      //默认选择禁手

        //设置自由开局、指定开局
        jLabel3 = new Label();
        jLabel3.setText("开局模式：");

        startBtn.setText("开始");
        startBtn.setFont(f);
        undoBtn.setText("悔棋");
        undoBtn.setFont(f);
        saveGameBtn.setText("保存");
        saveGameBtn.setFont(f);
        orderBtn.setText("显示落子顺序");
        orderBtn.setFont(f);
        giveUpBtn.setText("和棋");
        giveUpBtn.setFont(f);
        gameOverBtn.setText("认输");
        gameOverBtn.setFont(f);
        VCFBtn.setText("VCF");
        VCFBtn.setFont(f);

        //鼠标监听
        startBtn.addActionListener(l);
        orderBtn.addActionListener(l);
        undoBtn.addActionListener(l);
        giveUpBtn.addActionListener(e -> giveUpBtn());
//        saveGameBtn.addActionListener(e -> saveBin());
        saveGameBtn.addActionListener(l);
        VCFBtn.addActionListener(e -> showVCF());
//        saveExcelBtn.addActionListener(e -> saveExcel());

        setting_ok.addActionListener(evt -> settingOk());   //设置完禁手、指定开局的事件相应
        save_okBtn.addActionListener(evt -> saveGame());
        chooseBtn.addActionListener(e -> choose());
        gameOverBtn.addActionListener(evt -> gameOver());
        submitBtn.addActionListener(e -> submit());
        Free.addChangeListener(e -> {
            n_timesLabel.setVisible(Free.isSelected()?false:true);  //开局选择无禁手（自由开局）则不显示五手N打、选择定式开局
            n_times_value.setVisible(Free.isSelected()?false:true);
            chooseBtn.setEnabled(Free.isSelected()?false:true);
        });

        //菜单栏menu设置
        jMenu1.setText("对局设置");
        Man_AI.setText("人机对战");
        Man_AI.addActionListener(evt -> VsModeSelected(evt));
        jMenu1.add(Man_AI);
        Man_Man.setText("人人对战");
        Man_Man.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                VsModeSelected(evt);
            }

        });
        jMenu1.add(Man_Man);
        AI_AI.setText("机机对战");
        AI_AI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                VsModeSelected(evt);
            }
        });
        jMenu1.add(AI_AI);
        jMenuBar.add(jMenu1);

        //游戏设置菜单项
        jMenu2.setText("规则设置");
        jMenu2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if(gobangPanel.isGameOver)
                {
                    jMenu2MouseClicked(evt);
                }
            }
            private void jMenu2MouseClicked(MouseEvent evt) {
                settingTip.setVisible(true);
            }
        });
        jMenuBar.add(jMenu2);

        //复盘设置
        jMenu3.setText("复盘设置");
        jMenu3.add(VCFManul);
        jMenu3.add(historyManul);
        VCFManul.addActionListener(showGames);
        historyManul.addActionListener(showGames);
        jMenuBar.add(jMenu3);

        //设置帮助菜单项
        jMenu4.setText("帮助");
        jMenu4.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    showHelp();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        jMenuBar.add(jMenu4);
        setJMenuBar(jMenuBar);

        //设置棋盘面板布局
        GroupLayout panelLayout = new GroupLayout(gobangPanel);
        gobangPanel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addGap(iWidth/10,iWidth/10,iWidth/10)
                                .addComponent(showA)
                                .addGap(iWidth/15,iWidth/15,iWidth/15)
                                .addComponent(userATime, GroupLayout.PREFERRED_SIZE,60, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, iWidth/20, Short.MAX_VALUE)
                                .addComponent(showB)
                                .addGap(iWidth/15,iWidth/15,iWidth/15)
                                .addComponent(userBTime, GroupLayout.PREFERRED_SIZE,60, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(100, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(showA, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(userATime, GroupLayout.PREFERRED_SIZE,50, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(showB, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(userBTime, GroupLayout.PREFERRED_SIZE,50, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(320, Short.MAX_VALUE))
        );

        //设置对局信息面板布局
        panel1.setBorder(BorderFactory.createTitledBorder("对局信息"));
        GroupLayout jPanel1Layout = new GroupLayout(panel1);
        panel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(15,15,15)
                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, iWidth/12,GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, iWidth/12,GroupLayout.PREFERRED_SIZE))
                                                .addGap(iWidth/30,iWidth/30,iWidth/30)
                                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, iWidth/12, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, iWidth/12, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(10,Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, iWidth/25, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, iWidth/25, GroupLayout.PREFERRED_SIZE))
                                .addGap(iWidth/35,iWidth/35,iWidth/35)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField3, GroupLayout.PREFERRED_SIZE,iWidth/25 , GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, iWidth/25, GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                        )
        );
        pack();

        //使用用netbeans，设置操作面板布局结构
        GroupLayout panel2Layout = new GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(startBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(undoBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(saveGameBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(giveUpBtn,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(gameOverBtn,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(VCFBtn,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(orderBtn)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(iHeight/30,iHeight/30,iHeight/30)
                                .addComponent(startBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                //.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                                .addComponent(undoBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(saveGameBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(giveUpBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(gameOverBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(VCFBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(orderBtn)
                                .addGap(iHeight/30,iHeight/30,iHeight/30))
        );
        GroupLayout rightPaneLayout = new GroupLayout(rightPane);
        rightPane.setLayout(rightPaneLayout);
        rightPaneLayout.setHorizontalGroup(
                rightPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, rightPaneLayout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(panel2, iWidth/4,iWidth/4, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(rightPaneLayout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(panel1, iWidth/4,iWidth/4, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        rightPaneLayout.setVerticalGroup(
                rightPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(rightPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel1, iHeight/4, iHeight/4, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel2, iHeight*3/4, iHeight*3/4, GroupLayout.PREFERRED_SIZE))
        );

        //游戏设置弹框布局结构
        GroupLayout settingTipLayout = new GroupLayout(settingTip.getContentPane());
        settingTip.getContentPane().setLayout(settingTipLayout);
        settingTipLayout.setHorizontalGroup(
                settingTipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(settingTipLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(settingTipLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel2)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Jinshou)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Free)
                                                .addGap(0, Short.MAX_VALUE, Short.MAX_VALUE))
                                        .addGroup(settingTipLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(n_timesLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(n_times_value)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(settingTipLayout.createSequentialGroup()
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGap(100, 100, 100)
                                                .addComponent(chooseBtn)
                                                .addGap(30, 30, 30)
                                                .addComponent(setting_ok)))
                                .addContainerGap())
        );
        settingTipLayout.setVerticalGroup(
                settingTipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(settingTipLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(Jinshou)
                                        .addComponent(Free))
                                .addGap(18, 18, 18)
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(n_timesLabel)
                                        .addComponent(n_times_value))
                                .addGap(18, 18, 18)
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(chooseBtn)
                                        .addComponent(setting_ok))
                                .addGap(18, 18, 18))
        );

        // 保存棋谱弹窗布局设置
        GroupLayout saveTiplayout = new GroupLayout(saveTip.getContentPane());
        saveTip.getContentPane().setLayout(saveTiplayout);
        saveTiplayout.setHorizontalGroup(
                saveTiplayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(saveTiplayout.createSequentialGroup()
                                .addGroup(saveTiplayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(saveTiplayout.createSequentialGroup()
                                                .addGap(48, 48, 48)
                                                .addComponent(selectManualLabel)
                                                .addGap(18, 18, 18)
                                                .addComponent(historyRadio)
                                                .addGap(32, 32, 32)
                                                .addComponent(VCFRadio))
                                        .addGroup(saveTiplayout.createSequentialGroup()
                                                .addGap(126, 126, 126)
                                                .addComponent(save_okBtn, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(61, Short.MAX_VALUE))
        );
        saveTiplayout.setVerticalGroup(
                saveTiplayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(saveTiplayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(saveTiplayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(selectManualLabel)
                                        .addComponent(historyRadio)
                                        .addComponent(VCFRadio))
                                .addGap(18, 18, 18)
                                .addComponent(save_okBtn)
                                .addContainerGap(20, Short.MAX_VALUE))
        );

        //整个面板布局设置
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(gobangPanel,  GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(rightPane,  GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        //.addComponent(rightPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(rightPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(gobangPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("五子棋计算机博弈系统");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /*点击帮助菜单栏弹出规则pdf文档*/
    private  void showHelp() throws IOException {

        try {
            String path = properties.getProperty("Help");
            File pdfFile = new File(path);
            Desktop.getDesktop().open(pdfFile);         //默认软件打开本地文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveGame() {
        if (historyRadio.isSelected()) {        //保存对局
            System.out.println(isGameOver);
            saveGameToHistory();
        } else {   //存入VCF棋谱
            saveBin();
        }
    }
    //将对局写入历史对局
    private void saveGameToHistory() {
        String[] players=new String[2];
        players[0]=userAName==null?"玩家A":userAName.getText();
        players[1]=userBName==null?"玩家B":userBName.getText();
        int playerAColor=A.isSelected()?GobangPanel.BLACK:GobangPanel.WHITE;
        Stack<Chess> chessList=GobangPanel.history;
        int VSMode=GobangPanel.VSMode;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        String time = df.format(new Date());
        String winner;
        if(!GobangPanel.isGameOver){
            winner="平局";
        }
        else {
            winner=(playerAColor==chessList.peek().getColor())?players[0]:players[1]+"赢";
        }

        GameHistory game=new GameHistory(players,time,winner,chessList,VSMode);


        String path = "src/resources/GameHistory/"
                + game.getFileName()
                + ".txt";//数据存储路径

        try {
            FileOutputStream fos = new FileOutputStream(path, true);//新建一个文件输出流
            ObjectOutputStream oos = new ObjectOutputStream(fos);//在文件输出流上套一个对象输出流
            oos.writeObject(game);
            oos.flush();
            oos.close();
            JOptionPane.showMessageDialog(this, "已保存！");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "出错啦！", 0);
        }


    }

    //将落棋过程写入Excel表
    private void saveGameToExcel() {
        String path = "src/resources/VCF棋谱.xlsx";
//        String path = "src/resources/黑棋先手必胜走法.xlsx";
        Stack<Chess> data = gobangPanel.history;
//        for (Chess c:data)
//            System.out.println(c.x);
        try {
            // 读取Excel文档
            File file = new File(path);
            System.out.println(file.getAbsolutePath());
            if (!file.exists()) {
                //表格文件不存在，创建空白表格
                XSSFWorkbook blankExcel = new XSSFWorkbook();
                FileOutputStream outBlank = new FileOutputStream(file);
                blankExcel.write(outBlank);
                outBlank.close();//关闭输出流
                System.out.println("文件不存在，已创建");
            }
            // Excel2003版本（包含2003）以前使用HSSFWorkbook类，扩展名为.xls
            // Excel2007版本（包含2007）以后使用XSSFWorkbook类，扩展名为.xlsx

            // 创建工作簿类
            FileInputStream in = new FileInputStream(path);
            XSSFWorkbook workBook = new XSSFWorkbook(in);
            in.close();//关闭输入流

            // sheet 对应一个工作页
            XSSFSheet sheet = workBook.getSheet("数据标注");
            if (sheet == null) {            //sheet为空则创建
                sheet = workBook.createSheet("数据标注");
            }
            sheet.setDefaultColumnWidth(17);           //设置列宽
            int rowNumber = sheet.getLastRowNum();    // 第一行从0开始算
            XSSFRow lastRow = sheet.getRow(rowNumber);
            if (!isRowEmpty(lastRow)) {
                rowNumber++;
            }
            // 创建行，下标从最后一行+1开始

            XSSFRow row = sheet.createRow(rowNumber);
            row.setHeightInPoints(24);//设置行高
            DataAnnotations dataAnnotations = new DataAnnotations();    //生成棋子数据标注类
            int i = 0;
            for (Chess chess : gobangPanel.history) {
                //获取坐标
                String X = String.valueOf((char) (64 + chess.getX()));
                String Y = String.valueOf((16 - chess.getY()));

                //获取数据标注
                String note = "-";
                String strGong = dataAnnotations.ScanBoard(chess, true);//判定该步棋的攻击行为
                String strFang = dataAnnotations.ScanBoard(chess, false);//判定该步棋的防守行为
                note += dataAnnotations.Check(strGong, strFang);//根据攻守情况，返回最终的数据标注
                
                row.createCell(i++).setCellValue(i + "." + X + Y + note);
                System.out.println(X + Y + note);
            }


            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            FileOutputStream out = new FileOutputStream(path);
            workBook.write(out);// 将数据导出到Excel表格
            out.close();
            workBook.close();
            JOptionPane.showMessageDialog(this, "数据导出成功！");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "出错啦！", 0);
        } catch (EmptyFileException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "出错啦！", 0);
        }
    }

    // 存储VCF棋谱，存入二进制文件
    private void saveBin() {
        gobangPanel.saveBin();
        saveGameToExcel();  //同时在Excel文件中也加入该条记录，便于查看
    }

    // 判断行是否为空
    public static boolean isRowEmpty(XSSFRow row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            XSSFCell cell = row.getCell(c);
            if (cell != null && (cell.getCellTypeEnum() != CellType.BLANK)) {//BLANK代表空白类型
                return false;
            }
        }
        return true;
    }
    /*获取用户名显示在棋盘上*/
    private void submit(){
        if((!userAName.getText().equals("")) && (!userBName.getText().equals(""))){
            showA.setText(userAName.getText());
            showB.setText(userBName.getText());
            settingUser.setVisible(false);
        }
        else{
            JOptionPane.showMessageDialog(gobangPanel,"请完善信息", "警告", JOptionPane.WARNING_MESSAGE);
            settingUser.setVisible(true);
        }

        showA.setText(A.isSelected()?userAName.getText():userBName.getText());
        showB.setText(A.isSelected()?userBName.getText():userAName.getText());
    }

    void changeUserName() {
        String AText = showA.getText(), BText = showB.getText();
        showA.setText(BText);
        showB.setText(AText);
    }

    /*设置整个游戏规则*/
    private void settingOk() {
        if(Free.isSelected()){
            jTextField4.setVisible(false);
//            settingTip.setVisible(false);
        }
        else {
            jTextField4.setText("五手"+(String) n_times_value.getSelectedItem()+"打");
            jTextField4.setFont(new Font("楷体", Font.BOLD,iWidth/70));
            jTextField4.setBackground(new Color(0xF0F6BE06, true));
            jTextField4.setEditable(false);
            jTextField4.setVisible(true);
        }
        //开局后设置按钮不可触发
        startBtn.setEnabled(false);
        jMenu1.setEnabled(false);
        jMenu2.setEnabled(false);
        jTextField2.setText(Jinshou.isSelected()?"指定开局":"自由开局");
        jTextField2.setFont(new Font("楷体", Font.BOLD,iWidth/70));
        jTextField2.setFont(new Font("楷体", Font.BOLD,iWidth/70));
        jTextField2.setBackground(new Color(0xF0F6BE06, true));
        jTextField2.setEditable(false);

        jTextField3.setText(Jinshou.isSelected()?"有禁手":"无禁手");
        jTextField3.setFont(new Font("楷体", Font.BOLD,iWidth/70));
        jTextField3.setBackground(new Color(0xF0F6BE06, true));
        jTextField3.setEditable(false);


        int VSMode = -1, initUser = -1;
//            VsMode = Jinshou.isSelected() ? GobangPanel.ManAI : GobangPanel.ManMan;
        VSMode = Man_AI.isSelected() ? ManAI : GobangPanel.ManMan;
        initUser = B.isSelected() ? GobangPanel.HUMAN : COMPUTER;
        settingTip.dispose();
        if(Jinshou.isSelected()){               //若指定开局则必下天元，选择有禁手的情况
            gobangPanel.setStandardList();          //获取定式开局列表
        }
        if (chooseBtn.getText().trim().substring(2,4).equals("定式")) {
            gobangPanel.startGame(initUser, VSMode, false);
        }
        else {
            gobangPanel.startGame(initUser, VSMode, true);
        }
        restartCountTime();
        GobangPanel.isGameOver=false;
    }
    /*实现26种指定开局*/
    private void choose(){
        try {
            new Choose(this,true);
            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //实现对局设置，判断是人机/机机/人人对战，用户名输入
    private void VsModeSelected(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        //System.out.println(evt);
        String mode = evt.getActionCommand();
        Man_AI.setSelected(Man_AI.getText().equals(mode));
        Man_Man.setSelected(Man_Man.getText().equals(mode));
        AI_AI.setSelected(AI_AI.getText().equals(mode));
        jTextField1.setText(mode);
        jTextField1.setFont(new Font("楷体", Font.BOLD,iWidth/70));
        jTextField1.setBackground(new Color(0xF0F6BE06, true));
        jTextField1.setEditable(false);
        settingUser = new  JFrame("玩家设置");
        settingUser.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        settingUser.setLocationRelativeTo(gobangPanel);
        settingUser.setResizable(false);
        settingUser.setBounds(iWidth/2,iHeight/2,iWidth/2,iHeight/2);
        //组装登录用户名
        Box vBox = Box.createVerticalBox();
        if(userAName==null || userBName==null){
            userAName = new JTextField(properties.getProperty("userAName"));
            userBName = new JTextField(properties.getProperty("userBName"));
        }
        //水平组装用户名
        Box ABox = Box.createHorizontalBox();
        ABox.add(Box.createHorizontalStrut(settingUser.getWidth()/6));
        ABox.add(playerA);
        ABox.add(Box.createHorizontalStrut(settingUser.getWidth()/8));    //添加间隔距离
        ABox.add(userAName);
        ABox.add(Box.createHorizontalStrut(settingUser.getWidth()/8));

        Box BBox = Box.createHorizontalBox();
        BBox.add(Box.createHorizontalStrut(settingUser.getWidth()/6));
        BBox.add(playerB);
        BBox.add(Box.createHorizontalStrut(settingUser.getWidth()/8));
        BBox.add(userBName);
        BBox.add(Box.createHorizontalStrut(settingUser.getWidth()/8));

        Box CBox = Box.createHorizontalBox();
        CBox.add(jLabel1);
        CBox.add(Box.createHorizontalStrut(settingUser.getWidth()/10));
        CBox.add(A);
        CBox.add(Box.createHorizontalStrut(settingUser.getWidth()/10));
        CBox.add(B);
        //组装按钮
        Box  btnBox = Box.createHorizontalBox();
        btnBox.add(submitBtn);
        btnBox.add(Box.createHorizontalStrut(80));
        btnBox.add(backBtn);
        //垂直组装
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(ABox);
        vBox.add(Box.createVerticalStrut(30));
        vBox.add(BBox);
        vBox.add(Box.createVerticalStrut(30));
        vBox.add(CBox);
        vBox.add(Box.createVerticalStrut(30));
        vBox.add(btnBox);
        vBox.add(Box.createVerticalStrut(30));
        settingUser.add(vBox);
        settingUser.pack();
        settingUser.setVisible(true);

        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                settingUser.setVisible(false);
            }
        });
    }

    /*点击认输*/
    private void gameOver(){
        int n = JOptionPane.showConfirmDialog(gobangPanel,"确定认输？","注意",JOptionPane.WARNING_MESSAGE);
        if(n==0){
            timer.cancel();
            GobangPanel.isGameOver=true;
            startBtn.setEnabled(true);
            jMenu1.setEnabled(true);
            jMenu2.setEnabled(true);
        }
    }
    /*点击和棋*/
    private void giveUpBtn(){
        int n = JOptionPane.showConfirmDialog(gobangPanel,"确定和棋？","注意",JOptionPane.WARNING_MESSAGE);
        if(n==0){
            timer.cancel();
            GobangPanel.isGameOver=true;
            startBtn.setEnabled(true);
            jMenu1.setEnabled(true);
            jMenu2.setEnabled(true);
        }
//        BinManual binManual = new BinManual();
//        binManual.excelToBin("src/resources/黑棋先手必胜棋谱.xlsx","src/resources/MustWinManual.bin");
    }

    private ActionListener l = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveGameBtn.setEnabled(true);
            Object source = e.getSource();
            if (source == startBtn)//开始游戏
            {
//                userATime.setText("00:00");
//                userBTime.setText("00:00");
                settingTip.setVisible(true);
            } else if (source == orderBtn) {   //显示落子顺序
                gobangPanel.toggleOrder();
            } else if (source == undoBtn) {    //悔棋
                if (GobangPanel.VSMode == ManAI) {  //人机模式悔棋一次悔两步
                    gobangPanel.undo();
                    gobangPanel.undo();
                } else {
                    gobangPanel.undo();   //人人模式悔棋一次悔一步
                }

            } else if (source == saveGameBtn) {
                saveTip.setVisible(true);
            }
        }
    };

    private ActionListener showGames = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if(source==VCFManul){
                try {
                    showMustWin();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if(source==historyManul)
            {
                try {
                    showHistoryGame();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };

    public void restartCountTime() {
        int playerA_color = A.isSelected()?1:2;
        JTextField start =  playerA_color==gobangPanel.currentPlayer ?userATime : userBTime;
        JTextField stop = playerA_color==gobangPanel.currentPlayer ? userBTime : userATime;
        long stopTime = stopWatch.getTime();            //停止时间，落子后的时间，以毫秒为单位
        long stopMinutes = (stopTime / 1000) / 60;      //转换为分钟和秒
        long stopSeconds = (stopTime / 1000) % 60;
        String str1 = stopMinutes < 10 ? "0" + stopMinutes : stopMinutes + " ";
        String str2 = stopSeconds < 10 ? "0" + stopSeconds : stopSeconds + " ";
        stop.setText(str1 + "：" + str2 + "\n");
        stopWatch.reset();
        stopWatch.start();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);        //创建计时器
        timer.schedule(new TimerTask() {        //安排指定任务从指定的延迟后开始重复的固定延迟delay执行
            @Override
            public void run() {
                long startTime = stopWatch.getTime();
                long startMinutes = (startTime / 1000) / 60;
                long startSeconds = (startTime / 1000) % 60;
                String str1 = startMinutes < 10 ? "0" + startMinutes : startMinutes + " ";
                String str2 = startSeconds < 10 ? "0" + startSeconds : startSeconds + " ";
                start.setText(str1 + ":" + str2 + "\n");
            }
        },0, 1 * 1000);
    }

    // 获取五手N打的数目
    public static int getNum(){
        String x = n_times_value.getSelectedItem().toString();
        return  Integer.parseInt(x);
    }
    //VCF查找
    private void showVCF() {
        gobangPanel.showVCF();
        startBtn.setEnabled(true);
    }
    public void showMustWin() throws Exception {
        new GameDialog(this, true,false).setVisible(true);
    }
    public void showHistoryGame() throws Exception {
        new GameDialog(this, true,true).setVisible(true);
    }

    //自带的显示风格有：Metal（背景颜色较浅些）和Nimbus
    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainUI mainUI = null;
                try {
                    mainUI = new MainUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mainUI.setVisible(true);
            }
        });
    }
}

