package main.lu;
import main.yang.GobangPanel;
import main.yang.MainUI;
import main.zhou.GameHistory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

// 历史棋谱列表对话框的显示类，历史棋谱即在resources目录下的黑棋先手必胜走法.xlsx
public class GameDialog extends JDialog {
    // 构造函数，参数parent表示其在MainUI的JFrame上显示
    public GameDialog(JFrame parent, boolean modal, boolean showGame) throws Exception {
        super(parent, modal);
        this.showGame = showGame;
        initComponents();
    }

    // 各组件及变量初始化
    private void initComponents() throws Exception {
        // 变量初始化
        jScrollPane = new JScrollPane();
        table = new JTable();
        showManualBtn = new JButton();
        slowShowManualBtn = new JButton();
        backBtn = new JButton();
        panel = new GobangPanel();

        if (showGame) {
            gameHistories = getGames();
            lineNum=gameHistories.size();
            tableData=new Object[lineNum][];
            for (int i = 0; i < lineNum; i++) {  // 初始化表格显示内容，即显示历史棋谱文件名
                tableData[i] = new Object[]{gameHistories.get(i).getTip()};
            }

        } else {
            binManual = new BinManual();
            manual = binManual.readBinFile("src/resources/VCF.bin");
            lineNum = manual.size();
            tableData = new Object[lineNum][];
            for (int i = 0; i < lineNum; i++) {  // 初始化表格显示内容，即显示历史棋谱文件名
                tableData[i] = new Object[]{i+1};
            }
        }

        // 设置表格内容居中显示
        r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class,r);
        table.getTableHeader().setDefaultRenderer(r);

        // 窗口、按钮属性设置
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //设置右上角关闭则对话框消失
        setTitle("历史棋谱列表");  //设置对话框标题
        setLocation(400,200);  //设置对话框在电脑屏幕上的显示位置，该坐标为对话框左上角坐标
        slowShowManualBtn.setText("定时复盘");
        showManualBtn.setText("快速复盘");  //设置按钮显示文字
        backBtn.setText("返回");

        // 给按钮添加事件监听对象
        slowShowManualBtn.addActionListener(l);
        showManualBtn.addActionListener(l);
        backBtn.addActionListener(l);

        // 设置表格格式
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,  //表格内容
                new String [] {  //表头内容
                        showGame?"历史棋局":"VCF棋谱"
                }
        ) {
            Class[] types = new Class [] {  //表格数据类型
                    java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {  //设置表格不可编辑
                    false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setResizable(false);
        }

        // 设置界面上各组件布局
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(55, 55, 55)
                                .addComponent(slowShowManualBtn)
                                .addGap(50, 50, 50)
                                .addComponent(showManualBtn)
                                .addGap(55, 55, 55)
                                .addComponent(backBtn, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jScrollPane, GroupLayout.Alignment.TRAILING)
        );
        layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[]{backBtn, showManualBtn});
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(backBtn)
                                        .addComponent(showManualBtn)
                                        .addComponent(slowShowManualBtn))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }

    // 事件监听处理
    private ActionListener l = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();  //获取事件源
//            //若点击复盘设置则保存按钮不可点击触发
//            if(showManualBtn.isSelected() || slowShowManualBtn.isSelected()){
//                MainUI.saveGameBtn.setEnabled(false);
//            }

            if (source == backBtn) {  // 返回按钮
                setUnseen(); // 设置对话框消失
            }
            else{
                int index = table.getSelectedRow();  // 获取用户选中的棋谱序号，index从0开始
                if (index == -1){  // -1表示未选中
                    showMessage();  // 显示需选择记录的提示信息
                }
                else {
                    if(showGame){//复现历史对局
                        panel.showGame(source == showManualBtn,gameHistories.get(index).getChessList());
                    }
                    else {
                        try {
                            manual = binManual.readBinFile("src/resources/VCF.bin");  //读取二进制文件
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        int chosenLine = (int) table.getValueAt(index,0);  // 获取选中的棋谱索引
                        ArrayList<String> chosenManual = manual.get(chosenLine - 1);
                        panel.drawManual(chosenManual, source == showManualBtn);
                    }
                    setUnseen();  // 设置对话框消失
                    MainUI.saveGameBtn.setEnabled(false);
                    }
                }
        }
    };


    //获取excel文件中总行数
    private int getLineNum(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("文件不存在！");
        }

        InputStream in = new FileInputStream(file);

        XSSFWorkbook sheets = new XSSFWorkbook(in);  //获取文件
        XSSFSheet sheet = sheets.getSheetAt(0); //获取第一个工作簿
        return sheet.getPhysicalNumberOfRows();
    }

    private Vector<GameHistory> getGames(){
        File file = new File("src/resources/GameHistory"); //需要获取的文件的路径
        File[] filePathLists = file.listFiles(); //存储文件路径的String数组
        System.out.println(filePathLists.length);
        Vector<GameHistory> gameHistories=new Vector<>();
        for (File path:filePathLists) {
            if(path.isFile()){
                try {//读取指定文件路径下的文件内容
                    FileInputStream fis = new FileInputStream(path);
                    ObjectInputStream ooi = new ObjectInputStream(fis);
                    GameHistory game= (GameHistory) ooi.readObject();
                    gameHistories.add(game);
                } catch (IOException | ClassNotFoundException e) {
                    //e.printStackTrace();
                }
            }
        }
        //System.out.println(gameHistories.size());
        //按时间排序
        Collections.sort(gameHistories, new Comparator<GameHistory>() {
            @Override
            public int compare(GameHistory o1, GameHistory o2) {
                return -o1.getTime().compareTo(o2.getTime());
            }
        });

        return gameHistories;
    }

    // 显示显示需选择记录的提示信息
    private void showMessage() {
        JOptionPane.showMessageDialog(this,"请选择一条记录！");
    }

    // 设置对话框消失
    public void setUnseen() {
        this.setVisible(false);
    }

    // 变量定义
    private JButton backBtn;  // 返回按钮
    private JScrollPane jScrollPane;  // 滚动面板
    private JButton showManualBtn;          // 快速显示棋谱
    private JButton slowShowManualBtn;      //一步步显示棋谱
    private JTable table;   // 显示历史棋谱的表格
    private int lineNum;  //excel文件总行数
    private Object [][]tableData;   // 用于加载到表格显示中的表格数据
    private DefaultTableCellRenderer r;  // 设置表格内容格式显示
    private GobangPanel panel;  // 面板对象

    private JRadioButtonMenuItem Man_Man;
    private ArrayList<ArrayList<String>> manual;
    private BinManual binManual;
    private boolean showGame;//显示历史对局还是必胜棋谱，true为历史对局
    private Vector<GameHistory> gameHistories;
}
