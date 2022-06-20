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
    Font f = new Font("����", Font.BOLD,18);
    public MainUI() throws Exception {
        properties = ReadConfig();                         //�����ļ�
        initComponents();
        this.stopWatch = StopWatch.createStarted();     //���������ʱ����
        stopWatch.stop();
    }

    //������������֤�������ݵ�һ����
    public static int iWidth;
    public static int iHeight;
    public StopWatch stopWatch;     //ʵ�ּ�ʱ����
    private Timer timer = null;
    //public static HashMap map;              //�����ļ�����Ϊ��ϣ��ṹ
    public static Properties properties = null;

    private JLabel n_timesLabel;        //ѡ������n���ǩ
    private static JComboBox<String> n_times_value;    //����n��ѡ��������

    private JMenuBar jMenuBar;      //�˵�
    public static JMenu jMenu1;           //��������
    private JRadioButtonMenuItem Man_AI;    //�˻���ս
    private JRadioButtonMenuItem Man_Man;   //���˶�ս
    private JRadioButtonMenuItem AI_AI;     //������ս
    public static JMenu jMenu2;           //�Ծ�����
    public static JMenu jMenu3;           //�������
    private JMenuItem VCFManul;          //VCF���׵�ѡ�˵���
    private JMenuItem historyManul;      //��ʷ���ײ˵���
    private JMenu jMenu4;           //����

    private Panel panel1;       //��ʾ�Ծ���Ϣ
    private JLabel playerA;
    private JLabel playerB;
    private JLabel showA;       //������ʾ�����Ϣ
    private JLabel showB;
    private JTextField userAName;   //�������A����
    private JTextField userBName;
    private JTextField userATime;   //�������A��B ��ʱ����
    private JTextField userBTime;
    private JButton submitBtn;      //���������Ϣ�ύ��ť
    private JButton backBtn;        //���������Ϣ���ذ�ť
    private JTextField jTextField1;
    private JTextField jTextField2;
    private JTextField jTextField3;
    private JTextField jTextField4;

    private GobangPanel gobangPanel;//�������
    private Panel panel2;       //�������
    private Panel rightPane;    //�ұ����
    public static JButton startBtn;   //��ʼ��Ϸ
    private JButton undoBtn;    //���尴ť
    private JCheckBox orderBtn; //��ʾ����˳��ť
    public static JButton saveGameBtn;//��������
    private JButton giveUpBtn;  //����
    private JButton gameOverBtn;//����
    private JButton VCFBtn; //VCF�ҽ�

    private JDialog settingTip = new JDialog(this, "��������", true);       //�������õ���
    private JDialog saveTip = new JDialog(this, "��������", true);       //�������õ���
    private JFrame settingUser = null;         //�������A��B�û�������
    private JLabel jLabel1;         //���Aѡ��ڰ����ӱ�ǩ
    private ButtonGroup grp_alg;    //ѡ�����ְ�ť��
    private JRadioButton A;
    private JRadioButton B;
    private JLabel jLabel2;         //����ѡ���ǩ
    private ButtonGroup VsMode_alg; //���޽��ְ�ť��
    public static JRadioButton Jinshou;   //�н��֣�ָ�����֣�
    private JRadioButton Free;      //�޽��֣����ɿ��֣�
    private Label jLabel3;              //ѡ�񿪾ֱ�ǩ
    private JButton setting_ok;
    private JButton save_ok;
    public static JButton chooseBtn;

    //�������׵�������
    private JRadioButton VCFRadio;  //VCF����
    private JRadioButton historyRadio;  //��ʷ�Ծ�
    private ButtonGroup manualBtnGroup;  //�����ϵİ�ť��
    private JButton save_okBtn; //ȷ����ť
    private JLabel selectManualLabel;   //��ǩ

//    //���������ļ�
//    public HashMap<String, String> ReadConfig() throws IOException{
//        File file = new File("src/resources/config.txt");
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        //ArrayList result = new ArrayList<>();
//        String str = null;
//        String[] result;
//        HashMap<String,String> map=new HashMap<>();     //��txt�ļ�������ӳ��Ϊ��ϣ��
//        while ((str=reader.readLine()) !=null){          //��ȡ�ļ��е�ÿһ��
//            if (str.charAt(0)!='#'){
//                 result = str.split("��");     //�ԣ�Ϊ�ָ�
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
    /*����properties�����ļ�*/
    public Properties ReadConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/resources/config.properties", StandardCharsets.UTF_8));        //���������ļ�
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
        //ҳ�������������
        jMenuBar = new JMenuBar();  //���ò˵����
        jMenu1 = new JMenu();
        jMenu2 = new JMenu();
        jMenu3 = new JMenu();
        jMenu4 = new JMenu();
        Man_AI = new JRadioButtonMenuItem();
        Man_AI.setSelected(true);               //Ĭ��ѡ���˻���ս
        Man_Man = new JRadioButtonMenuItem();
        AI_AI = new JRadioButtonMenuItem();

        VCFManul = new JMenuItem("VCF����");
        historyManul = new JMenuItem("��ʷ����");

        playerA = new JLabel("���A");
        playerA.setBorder(BorderFactory.createEtchedBorder());
        playerA.setFont(f);
        playerA.setForeground(Color.black);
        playerB = new JLabel("���B");
        playerB.setBorder(BorderFactory.createEtchedBorder());
        playerB.setFont(f);
        playerB.setForeground(Color.black);

        //��嶥�˴������������ִ�����
        showA = new JLabel(properties.getProperty("userAName"));
        ImageIcon image1 = new ImageIcon("src/image/black.png");
        image1.setImage(image1.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT ));      //������ǩǰ���Ӵ�С
        showA.setIcon(image1);
        showA.setFont(f);
        showB = new JLabel(properties.getProperty("userBName"));
        ImageIcon image2 = new ImageIcon("src/image/white.png");//ʵ����ImageIcon ����
        image2.setImage(image2.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT ));
        showB.setIcon(image2);
        showB.setFont(f);
        userATime = new JTextField("00:00",12);
        userBTime = new JTextField("00:00",12);
        userATime.setEditable(false);
        userBTime.setEditable(false);

        grp_alg = new ButtonGroup();    //ѡ���Ⱥ��ְ�ť��
        VsMode_alg = new ButtonGroup();     //���޽��ְ�ť��


        rightPane = new Panel();       //�����
        panel2 = new Panel();           //�������

        startBtn = new JButton();
        undoBtn = new JButton();
        orderBtn = new JCheckBox();     //���ø�ѡ����ʾ����˳��

        saveGameBtn = new JButton("����");
        giveUpBtn = new JButton("����");
        gameOverBtn = new JButton("����");
        VCFBtn = new JButton("VCF");
        n_timesLabel = new JLabel("ѡ������N�������");
        n_times_value = new JComboBox<>();

        //��ť���á���������
        jLabel2 = new JLabel("����ѡ��");
        Free = new JRadioButton("�޽��֣����ɿ��֣�");
        Jinshou = new JRadioButton("�н��֣�ָ�����֣�");
        jLabel3 =  new Label();

        //������õ���
        jLabel1 = new JLabel();
        A = new JRadioButton();     //���A��B��ѡ��ť���,ѡ��ִ�����
        B = new JRadioButton();
        submitBtn = new JButton("�ύ");
        backBtn = new JButton("����");


        //��Ϸ���õ���
        settingTip = new JDialog(this, "��Ϸ����", true);
        settingTip.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        settingTip.setSize(400, 200);
        settingTip.setLocationRelativeTo(gobangPanel);
        setting_ok = new JButton("ȷ��");
        chooseBtn = new JButton("ѡ��ʽ����");
        n_times_value.setModel(new DefaultComboBoxModel<>(new String[]{"2", "3", "4", "5"}));

        // �������׵�������
        saveTip = new JDialog(this, "��������", true);
        saveTip.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        saveTip.setLocationRelativeTo(gobangPanel);
        manualBtnGroup = new ButtonGroup();
        historyRadio = new JRadioButton("��ʷ���");
        historyRadio.setSelected(true);
        VCFRadio = new JRadioButton("VCF����");
        selectManualLabel = new JLabel("ѡ�񱣴����ͣ�");
        save_okBtn = new JButton("ȷ��");
        saveTip.setSize(400, 150);
        manualBtnGroup.add(historyRadio);
        manualBtnGroup.add(VCFRadio);


        //��ʾ�Ծ���Ϣ����,���������ļ����ó�ʼֵ
        panel1 = new Panel();
        jTextField1 = new JTextField(properties.getProperty("VSMode"));
        jTextField2 = new JTextField(properties.getProperty("Begin"));
        jTextField3 = new JTextField(properties.getProperty("Rule"));
        jTextField4 = new JTextField(properties.getProperty("n_time_value"));

        //ͨ����ȡ��Ļ�ֱ�������������С
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        iHeight = (screenSize.height*6)/8;  //�����С�Ŀ���
        iWidth =(screenSize.height*17)/18;
        //iWidth = (screenSize.width*3)/5;
        setPreferredSize(new Dimension(iWidth, iHeight));     //���ðٷֱȴ�С
        ImageIcon icon = new ImageIcon("src/image/logo.png");   //�������Ͻ���Ϸͼ��
        setIconImage(icon.getImage());
        setResizable(false);        //���ô����С�������ı�
        int h = screenSize.height/2- iHeight /2;
        int w = screenSize.width/2- iWidth /2;
        setBounds(w,h, iWidth, iHeight);
        //System.out.println(" �ߣ�"+iHeight+"   �� "+iWidth+"  "+w+" "+h);
        gobangPanel = new GobangPanel(this, settingTip);    //�������崰��

        //���ò��������ع���
        panel2.setBorder(BorderFactory.createTitledBorder("����"));
        //���ÿ�ѡ��ť ���ֵ��ԡ��ˣ�Ĭ������Ϊ����
        jLabel1.setText("ִ���壺");
        grp_alg.add(A);
        A.setText("���A");
        A.setSelected(true);
        grp_alg.add(B);
        B.setText("���B");
        VsMode_alg.add(Free);
        VsMode_alg.add(Jinshou);
        Jinshou.setSelected(true);      //Ĭ��ѡ�����

        //�������ɿ��֡�ָ������
        jLabel3 = new Label();
        jLabel3.setText("����ģʽ��");

        startBtn.setText("��ʼ");
        startBtn.setFont(f);
        undoBtn.setText("����");
        undoBtn.setFont(f);
        saveGameBtn.setText("����");
        saveGameBtn.setFont(f);
        orderBtn.setText("��ʾ����˳��");
        orderBtn.setFont(f);
        giveUpBtn.setText("����");
        giveUpBtn.setFont(f);
        gameOverBtn.setText("����");
        gameOverBtn.setFont(f);
        VCFBtn.setText("VCF");
        VCFBtn.setFont(f);

        //������
        startBtn.addActionListener(l);
        orderBtn.addActionListener(l);
        undoBtn.addActionListener(l);
        giveUpBtn.addActionListener(e -> giveUpBtn());
//        saveGameBtn.addActionListener(e -> saveBin());
        saveGameBtn.addActionListener(l);
        VCFBtn.addActionListener(e -> showVCF());
//        saveExcelBtn.addActionListener(e -> saveExcel());

        setting_ok.addActionListener(evt -> settingOk());   //��������֡�ָ�����ֵ��¼���Ӧ
        save_okBtn.addActionListener(evt -> saveGame());
        chooseBtn.addActionListener(e -> choose());
        gameOverBtn.addActionListener(evt -> gameOver());
        submitBtn.addActionListener(e -> submit());
        Free.addChangeListener(e -> {
            n_timesLabel.setVisible(Free.isSelected()?false:true);  //����ѡ���޽��֣����ɿ��֣�����ʾ����N��ѡ��ʽ����
            n_times_value.setVisible(Free.isSelected()?false:true);
            chooseBtn.setEnabled(Free.isSelected()?false:true);
        });

        //�˵���menu����
        jMenu1.setText("�Ծ�����");
        Man_AI.setText("�˻���ս");
        Man_AI.addActionListener(evt -> VsModeSelected(evt));
        jMenu1.add(Man_AI);
        Man_Man.setText("���˶�ս");
        Man_Man.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                VsModeSelected(evt);
            }

        });
        jMenu1.add(Man_Man);
        AI_AI.setText("������ս");
        AI_AI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                VsModeSelected(evt);
            }
        });
        jMenu1.add(AI_AI);
        jMenuBar.add(jMenu1);

        //��Ϸ���ò˵���
        jMenu2.setText("��������");
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

        //��������
        jMenu3.setText("��������");
        jMenu3.add(VCFManul);
        jMenu3.add(historyManul);
        VCFManul.addActionListener(showGames);
        historyManul.addActionListener(showGames);
        jMenuBar.add(jMenu3);

        //���ð����˵���
        jMenu4.setText("����");
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

        //����������岼��
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

        //���öԾ���Ϣ��岼��
        panel1.setBorder(BorderFactory.createTitledBorder("�Ծ���Ϣ"));
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

        //ʹ����netbeans�����ò�����岼�ֽṹ
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

        //��Ϸ���õ��򲼾ֽṹ
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

        // �������׵�����������
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

        //������岼������
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
        setTitle("��������������ϵͳ");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /*��������˵�����������pdf�ĵ�*/
    private  void showHelp() throws IOException {

        try {
            String path = properties.getProperty("Help");
            File pdfFile = new File(path);
            Desktop.getDesktop().open(pdfFile);         //Ĭ������򿪱����ļ�
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveGame() {
        if (historyRadio.isSelected()) {        //����Ծ�
            System.out.println(isGameOver);
            saveGameToHistory();
        } else {   //����VCF����
            saveBin();
        }
    }
    //���Ծ�д����ʷ�Ծ�
    private void saveGameToHistory() {
        String[] players=new String[2];
        players[0]=userAName==null?"���A":userAName.getText();
        players[1]=userBName==null?"���B":userBName.getText();
        int playerAColor=A.isSelected()?GobangPanel.BLACK:GobangPanel.WHITE;
        Stack<Chess> chessList=GobangPanel.history;
        int VSMode=GobangPanel.VSMode;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//�������ڸ�ʽ
        String time = df.format(new Date());
        String winner;
        if(!GobangPanel.isGameOver){
            winner="ƽ��";
        }
        else {
            winner=(playerAColor==chessList.peek().getColor())?players[0]:players[1]+"Ӯ";
        }

        GameHistory game=new GameHistory(players,time,winner,chessList,VSMode);


        String path = "src/resources/GameHistory/"
                + game.getFileName()
                + ".txt";//���ݴ洢·��

        try {
            FileOutputStream fos = new FileOutputStream(path, true);//�½�һ���ļ������
            ObjectOutputStream oos = new ObjectOutputStream(fos);//���ļ����������һ�����������
            oos.writeObject(game);
            oos.flush();
            oos.close();
            JOptionPane.showMessageDialog(this, "�ѱ��棡");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "��������", 0);
        }


    }

    //���������д��Excel��
    private void saveGameToExcel() {
        String path = "src/resources/VCF����.xlsx";
//        String path = "src/resources/�������ֱ�ʤ�߷�.xlsx";
        Stack<Chess> data = gobangPanel.history;
//        for (Chess c:data)
//            System.out.println(c.x);
        try {
            // ��ȡExcel�ĵ�
            File file = new File(path);
            System.out.println(file.getAbsolutePath());
            if (!file.exists()) {
                //����ļ������ڣ������հױ��
                XSSFWorkbook blankExcel = new XSSFWorkbook();
                FileOutputStream outBlank = new FileOutputStream(file);
                blankExcel.write(outBlank);
                outBlank.close();//�ر������
                System.out.println("�ļ������ڣ��Ѵ���");
            }
            // Excel2003�汾������2003����ǰʹ��HSSFWorkbook�࣬��չ��Ϊ.xls
            // Excel2007�汾������2007���Ժ�ʹ��XSSFWorkbook�࣬��չ��Ϊ.xlsx

            // ������������
            FileInputStream in = new FileInputStream(path);
            XSSFWorkbook workBook = new XSSFWorkbook(in);
            in.close();//�ر�������

            // sheet ��Ӧһ������ҳ
            XSSFSheet sheet = workBook.getSheet("���ݱ�ע");
            if (sheet == null) {            //sheetΪ���򴴽�
                sheet = workBook.createSheet("���ݱ�ע");
            }
            sheet.setDefaultColumnWidth(17);           //�����п�
            int rowNumber = sheet.getLastRowNum();    // ��һ�д�0��ʼ��
            XSSFRow lastRow = sheet.getRow(rowNumber);
            if (!isRowEmpty(lastRow)) {
                rowNumber++;
            }
            // �����У��±�����һ��+1��ʼ

            XSSFRow row = sheet.createRow(rowNumber);
            row.setHeightInPoints(24);//�����и�
            DataAnnotations dataAnnotations = new DataAnnotations();    //�����������ݱ�ע��
            int i = 0;
            for (Chess chess : gobangPanel.history) {
                //��ȡ����
                String X = String.valueOf((char) (64 + chess.getX()));
                String Y = String.valueOf((16 - chess.getY()));

                //��ȡ���ݱ�ע
                String note = "-";
                String strGong = dataAnnotations.ScanBoard(chess, true);//�ж��ò���Ĺ�����Ϊ
                String strFang = dataAnnotations.ScanBoard(chess, false);//�ж��ò���ķ�����Ϊ
                note += dataAnnotations.Check(strGong, strFang);//���ݹ���������������յ����ݱ�ע
                
                row.createCell(i++).setCellValue(i + "." + X + Y + note);
                System.out.println(X + Y + note);
            }


            // �����ļ��������׼��������ӱ����������У���������sheet�������κβ�����������Ч
            FileOutputStream out = new FileOutputStream(path);
            workBook.write(out);// �����ݵ�����Excel���
            out.close();
            workBook.close();
            JOptionPane.showMessageDialog(this, "���ݵ����ɹ���");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "��������", 0);
        } catch (EmptyFileException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "��������", 0);
        }
    }

    // �洢VCF���ף�����������ļ�
    private void saveBin() {
        gobangPanel.saveBin();
        saveGameToExcel();  //ͬʱ��Excel�ļ���Ҳ���������¼�����ڲ鿴
    }

    // �ж����Ƿ�Ϊ��
    public static boolean isRowEmpty(XSSFRow row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            XSSFCell cell = row.getCell(c);
            if (cell != null && (cell.getCellTypeEnum() != CellType.BLANK)) {//BLANK����հ�����
                return false;
            }
        }
        return true;
    }
    /*��ȡ�û�����ʾ��������*/
    private void submit(){
        if((!userAName.getText().equals("")) && (!userBName.getText().equals(""))){
            showA.setText(userAName.getText());
            showB.setText(userBName.getText());
            settingUser.setVisible(false);
        }
        else{
            JOptionPane.showMessageDialog(gobangPanel,"��������Ϣ", "����", JOptionPane.WARNING_MESSAGE);
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

    /*����������Ϸ����*/
    private void settingOk() {
        if(Free.isSelected()){
            jTextField4.setVisible(false);
//            settingTip.setVisible(false);
        }
        else {
            jTextField4.setText("����"+(String) n_times_value.getSelectedItem()+"��");
            jTextField4.setFont(new Font("����", Font.BOLD,iWidth/70));
            jTextField4.setBackground(new Color(0xF0F6BE06, true));
            jTextField4.setEditable(false);
            jTextField4.setVisible(true);
        }
        //���ֺ����ð�ť���ɴ���
        startBtn.setEnabled(false);
        jMenu1.setEnabled(false);
        jMenu2.setEnabled(false);
        jTextField2.setText(Jinshou.isSelected()?"ָ������":"���ɿ���");
        jTextField2.setFont(new Font("����", Font.BOLD,iWidth/70));
        jTextField2.setFont(new Font("����", Font.BOLD,iWidth/70));
        jTextField2.setBackground(new Color(0xF0F6BE06, true));
        jTextField2.setEditable(false);

        jTextField3.setText(Jinshou.isSelected()?"�н���":"�޽���");
        jTextField3.setFont(new Font("����", Font.BOLD,iWidth/70));
        jTextField3.setBackground(new Color(0xF0F6BE06, true));
        jTextField3.setEditable(false);


        int VSMode = -1, initUser = -1;
//            VsMode = Jinshou.isSelected() ? GobangPanel.ManAI : GobangPanel.ManMan;
        VSMode = Man_AI.isSelected() ? ManAI : GobangPanel.ManMan;
        initUser = B.isSelected() ? GobangPanel.HUMAN : COMPUTER;
        settingTip.dispose();
        if(Jinshou.isSelected()){               //��ָ�������������Ԫ��ѡ���н��ֵ����
            gobangPanel.setStandardList();          //��ȡ��ʽ�����б�
        }
        if (chooseBtn.getText().trim().substring(2,4).equals("��ʽ")) {
            gobangPanel.startGame(initUser, VSMode, false);
        }
        else {
            gobangPanel.startGame(initUser, VSMode, true);
        }
        restartCountTime();
        GobangPanel.isGameOver=false;
    }
    /*ʵ��26��ָ������*/
    private void choose(){
        try {
            new Choose(this,true);
            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //ʵ�ֶԾ����ã��ж����˻�/����/���˶�ս���û�������
    private void VsModeSelected(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        //System.out.println(evt);
        String mode = evt.getActionCommand();
        Man_AI.setSelected(Man_AI.getText().equals(mode));
        Man_Man.setSelected(Man_Man.getText().equals(mode));
        AI_AI.setSelected(AI_AI.getText().equals(mode));
        jTextField1.setText(mode);
        jTextField1.setFont(new Font("����", Font.BOLD,iWidth/70));
        jTextField1.setBackground(new Color(0xF0F6BE06, true));
        jTextField1.setEditable(false);
        settingUser = new  JFrame("�������");
        settingUser.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        settingUser.setLocationRelativeTo(gobangPanel);
        settingUser.setResizable(false);
        settingUser.setBounds(iWidth/2,iHeight/2,iWidth/2,iHeight/2);
        //��װ��¼�û���
        Box vBox = Box.createVerticalBox();
        if(userAName==null || userBName==null){
            userAName = new JTextField(properties.getProperty("userAName"));
            userBName = new JTextField(properties.getProperty("userBName"));
        }
        //ˮƽ��װ�û���
        Box ABox = Box.createHorizontalBox();
        ABox.add(Box.createHorizontalStrut(settingUser.getWidth()/6));
        ABox.add(playerA);
        ABox.add(Box.createHorizontalStrut(settingUser.getWidth()/8));    //��Ӽ������
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
        //��װ��ť
        Box  btnBox = Box.createHorizontalBox();
        btnBox.add(submitBtn);
        btnBox.add(Box.createHorizontalStrut(80));
        btnBox.add(backBtn);
        //��ֱ��װ
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

    /*�������*/
    private void gameOver(){
        int n = JOptionPane.showConfirmDialog(gobangPanel,"ȷ�����䣿","ע��",JOptionPane.WARNING_MESSAGE);
        if(n==0){
            timer.cancel();
            GobangPanel.isGameOver=true;
            startBtn.setEnabled(true);
            jMenu1.setEnabled(true);
            jMenu2.setEnabled(true);
        }
    }
    /*�������*/
    private void giveUpBtn(){
        int n = JOptionPane.showConfirmDialog(gobangPanel,"ȷ�����壿","ע��",JOptionPane.WARNING_MESSAGE);
        if(n==0){
            timer.cancel();
            GobangPanel.isGameOver=true;
            startBtn.setEnabled(true);
            jMenu1.setEnabled(true);
            jMenu2.setEnabled(true);
        }
//        BinManual binManual = new BinManual();
//        binManual.excelToBin("src/resources/�������ֱ�ʤ����.xlsx","src/resources/MustWinManual.bin");
    }

    private ActionListener l = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveGameBtn.setEnabled(true);
            Object source = e.getSource();
            if (source == startBtn)//��ʼ��Ϸ
            {
//                userATime.setText("00:00");
//                userBTime.setText("00:00");
                settingTip.setVisible(true);
            } else if (source == orderBtn) {   //��ʾ����˳��
                gobangPanel.toggleOrder();
            } else if (source == undoBtn) {    //����
                if (GobangPanel.VSMode == ManAI) {  //�˻�ģʽ����һ�λ�����
                    gobangPanel.undo();
                    gobangPanel.undo();
                } else {
                    gobangPanel.undo();   //����ģʽ����һ�λ�һ��
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
        long stopTime = stopWatch.getTime();            //ֹͣʱ�䣬���Ӻ��ʱ�䣬�Ժ���Ϊ��λ
        long stopMinutes = (stopTime / 1000) / 60;      //ת��Ϊ���Ӻ���
        long stopSeconds = (stopTime / 1000) % 60;
        String str1 = stopMinutes < 10 ? "0" + stopMinutes : stopMinutes + " ";
        String str2 = stopSeconds < 10 ? "0" + stopSeconds : stopSeconds + " ";
        stop.setText(str1 + "��" + str2 + "\n");
        stopWatch.reset();
        stopWatch.start();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);        //������ʱ��
        timer.schedule(new TimerTask() {        //����ָ�������ָ�����ӳٺ�ʼ�ظ��Ĺ̶��ӳ�delayִ��
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

    // ��ȡ����N�����Ŀ
    public static int getNum(){
        String x = n_times_value.getSelectedItem().toString();
        return  Integer.parseInt(x);
    }
    //VCF����
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

    //�Դ�����ʾ����У�Metal��������ɫ��ǳЩ����Nimbus
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

