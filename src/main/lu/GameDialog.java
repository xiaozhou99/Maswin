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

// ��ʷ�����б�Ի������ʾ�࣬��ʷ���׼���resourcesĿ¼�µĺ������ֱ�ʤ�߷�.xlsx
public class GameDialog extends JDialog {
    // ���캯��������parent��ʾ����MainUI��JFrame����ʾ
    public GameDialog(JFrame parent, boolean modal, boolean showGame) throws Exception {
        super(parent, modal);
        this.showGame = showGame;
        initComponents();
    }

    // �������������ʼ��
    private void initComponents() throws Exception {
        // ������ʼ��
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
            for (int i = 0; i < lineNum; i++) {  // ��ʼ�������ʾ���ݣ�����ʾ��ʷ�����ļ���
                tableData[i] = new Object[]{gameHistories.get(i).getTip()};
            }

        } else {
            binManual = new BinManual();
            manual = binManual.readBinFile("src/resources/VCF.bin");
            lineNum = manual.size();
            tableData = new Object[lineNum][];
            for (int i = 0; i < lineNum; i++) {  // ��ʼ�������ʾ���ݣ�����ʾ��ʷ�����ļ���
                tableData[i] = new Object[]{i+1};
            }
        }

        // ���ñ�����ݾ�����ʾ
        r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class,r);
        table.getTableHeader().setDefaultRenderer(r);

        // ���ڡ���ť��������
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //�������Ͻǹر���Ի�����ʧ
        setTitle("��ʷ�����б�");  //���öԻ������
        setLocation(400,200);  //���öԻ����ڵ�����Ļ�ϵ���ʾλ�ã�������Ϊ�Ի������Ͻ�����
        slowShowManualBtn.setText("��ʱ����");
        showManualBtn.setText("���ٸ���");  //���ð�ť��ʾ����
        backBtn.setText("����");

        // ����ť����¼���������
        slowShowManualBtn.addActionListener(l);
        showManualBtn.addActionListener(l);
        backBtn.addActionListener(l);

        // ���ñ���ʽ
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,  //�������
                new String [] {  //��ͷ����
                        showGame?"��ʷ���":"VCF����"
                }
        ) {
            Class[] types = new Class [] {  //�����������
                    java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {  //���ñ�񲻿ɱ༭
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

        // ���ý����ϸ��������
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

    // �¼���������
    private ActionListener l = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();  //��ȡ�¼�Դ
//            //��������������򱣴水ť���ɵ������
//            if(showManualBtn.isSelected() || slowShowManualBtn.isSelected()){
//                MainUI.saveGameBtn.setEnabled(false);
//            }

            if (source == backBtn) {  // ���ذ�ť
                setUnseen(); // ���öԻ�����ʧ
            }
            else{
                int index = table.getSelectedRow();  // ��ȡ�û�ѡ�е�������ţ�index��0��ʼ
                if (index == -1){  // -1��ʾδѡ��
                    showMessage();  // ��ʾ��ѡ���¼����ʾ��Ϣ
                }
                else {
                    if(showGame){//������ʷ�Ծ�
                        panel.showGame(source == showManualBtn,gameHistories.get(index).getChessList());
                    }
                    else {
                        try {
                            manual = binManual.readBinFile("src/resources/VCF.bin");  //��ȡ�������ļ�
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        int chosenLine = (int) table.getValueAt(index,0);  // ��ȡѡ�е���������
                        ArrayList<String> chosenManual = manual.get(chosenLine - 1);
                        panel.drawManual(chosenManual, source == showManualBtn);
                    }
                    setUnseen();  // ���öԻ�����ʧ
                    MainUI.saveGameBtn.setEnabled(false);
                    }
                }
        }
    };


    //��ȡexcel�ļ���������
    private int getLineNum(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("�ļ������ڣ�");
        }

        InputStream in = new FileInputStream(file);

        XSSFWorkbook sheets = new XSSFWorkbook(in);  //��ȡ�ļ�
        XSSFSheet sheet = sheets.getSheetAt(0); //��ȡ��һ��������
        return sheet.getPhysicalNumberOfRows();
    }

    private Vector<GameHistory> getGames(){
        File file = new File("src/resources/GameHistory"); //��Ҫ��ȡ���ļ���·��
        File[] filePathLists = file.listFiles(); //�洢�ļ�·����String����
        System.out.println(filePathLists.length);
        Vector<GameHistory> gameHistories=new Vector<>();
        for (File path:filePathLists) {
            if(path.isFile()){
                try {//��ȡָ���ļ�·���µ��ļ�����
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
        //��ʱ������
        Collections.sort(gameHistories, new Comparator<GameHistory>() {
            @Override
            public int compare(GameHistory o1, GameHistory o2) {
                return -o1.getTime().compareTo(o2.getTime());
            }
        });

        return gameHistories;
    }

    // ��ʾ��ʾ��ѡ���¼����ʾ��Ϣ
    private void showMessage() {
        JOptionPane.showMessageDialog(this,"��ѡ��һ����¼��");
    }

    // ���öԻ�����ʧ
    public void setUnseen() {
        this.setVisible(false);
    }

    // ��������
    private JButton backBtn;  // ���ذ�ť
    private JScrollPane jScrollPane;  // �������
    private JButton showManualBtn;          // ������ʾ����
    private JButton slowShowManualBtn;      //һ������ʾ����
    private JTable table;   // ��ʾ��ʷ���׵ı��
    private int lineNum;  //excel�ļ�������
    private Object [][]tableData;   // ���ڼ��ص������ʾ�еı������
    private DefaultTableCellRenderer r;  // ���ñ�����ݸ�ʽ��ʾ
    private GobangPanel panel;  // ������

    private JRadioButtonMenuItem Man_Man;
    private ArrayList<ArrayList<String>> manual;
    private BinManual binManual;
    private boolean showGame;//��ʾ��ʷ�Ծֻ��Ǳ�ʤ���ף�trueΪ��ʷ�Ծ�
    private Vector<GameHistory> gameHistories;
}
