package startup;

import builder.script.GsonScript;
import common.ePref;
import common.eProfile;
import common.ePref;
import frames.PSConvert;
import dataset.Conn;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import dataset.eExcep;
import domain.eSysuser;
import frames.PathToDb;
import frames.UGui;
import frames.swing.FileFilter;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.LookAndFeel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Adm extends javax.swing.JFrame {

    private Thread thread = null;
    private char card = 'I';
    private Queue<Object[]> listQue = new ConcurrentLinkedQueue<Object[]>();
    private Query qSysuser = new Query(eSysuser.values()).sql(eSysuser.data(), eSysuser.up);
    private HashMap<String, JCheckBoxMenuItem> hmLookAndFill = new HashMap<String, JCheckBoxMenuItem>();
    javax.swing.Timer timer = new Timer(100, new ActionListener() {

        public void actionPerformed(ActionEvent ev) {
            if (listQue.isEmpty()) {
                Thread.yield();
            } else {
                clearListQue();
            }
        }
    });

    public Adm() {
        initComponents();
        initElements();
    }

    private void loadingPath() {

        if (ePref.base_num.getProp().equals("1")) {
            labPath2.setText(ePref.server1.getProp() + "/" + ePref.port1.getProp() + "\\" + ePref.base1.getProp());

        } else if (ePref.base_num.getProp().equals("2")) {
            //edPath.setText("D:\\Okna\\Database\\ps3\\sial3.fdb");
            labPath2.setText(ePref.server2.getProp() + "/" + ePref.port2.getProp() + "\\" + ePref.base2.getProp());

        } else if (ePref.base_num.getProp().equals("3")) {
            //edPath.setText("D:\\Okna\\Database\\ps4\\krauss.fdb");
            //edPath.setText("D:\\Okna\\Database\\ps4\\vidnal.fdb");
            labPath2.setText(ePref.server3.getProp() + "/" + ePref.port3.getProp() + "\\" + ePref.base3.getProp());

        }
        edPath.setText(GsonScript.filePath());
        if (ePref.dev == true) {
            edPort.setText((ePref.base_num.getProp().equals("2") || ePref.base_num.getProp().equals("3")) ? "3055" : "3050");
        } else {
            edPort.setText("3050");
        }
        edServer.setText("localhost");
        edUser.setText("sysdba");
        edPass.setText("masterkey");
    }

    private void loadingTab2() {

        DefaultTableModel dm = (DefaultTableModel) tab2.getModel();
        dm.getDataVector().clear();
        int npp = 0;
        for (Field up : App.db) {
            List rec = List.of(++npp, up.tname(), up.meta().descr());
            Vector vec = new Vector(rec);
            dm.getDataVector().add(vec);
        }
        ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
        UGui.setSelectedRow(tab2);
    }

    private void loadingTab3() {
        try {
            int row = tab2.getSelectedRow();
            Field fieldUp = App.db[row];
            Query qTable = new Query(fieldUp.fields()).select(fieldUp);

            String[] columnArr = new String[fieldUp.fields().length - 1];
            for (int k = 1; k < fieldUp.fields().length; k++) {
                columnArr[k - 1] = fieldUp.fields()[k].name();
            }
            Object dataArr[][] = new Object[qTable.size()][fieldUp.fields().length - 1];
            for (int i = 0; i < qTable.size(); ++i) {
                for (int k = 1; k < fieldUp.fields().length; ++k) {
                    dataArr[i][k - 1] = qTable.get(i).get(k);
                }
            }
            tab3.setModel(new DefaultTableModel(dataArr, columnArr) {
                public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                    super.setValueAt(aValue, rowIndex, columnIndex);
                    qTable.get(rowIndex).set(columnIndex + 1, aValue);
                    qTable.update(qTable.get(rowIndex));
                }
            });

        } catch (Exception e) {
            System.err.println("Adm.loadingTab3() " + e);
        }
    }

    private void loadingTab4() {
        try {
            DefaultTableModel dm = (DefaultTableModel) tab4.getModel();
            dm.getDataVector().clear();
            String sql = "SELECT DISTINCT a.rdb$role_name , b.rdb$user, c.fio, c.phone, c.email FROM rdb$roles a left join "
                    + " rdb$user_privileges b on a.rdb$role_name = b.rdb$relation_name AND "
                    + " b.rdb$user != 'SYSDBA' AND NOT EXISTS (SELECT * FROM rdb$roles c WHERE c.rdb$role_name = b.rdb$user) "
                    + " left join sysuser c on b.rdb$user = c.login where b.rdb$user is not null ORDER BY 1";
            Statement statement = Conn.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = statement.executeQuery(sql);
            Query userList = new Query(eSysuser.values());
            int npp = 0;
            while (rs.next()) {
                String role = rs.getString(1).trim();
                boolean br = List.of("TEXNOLOG_RW", "MANAGER_RW").contains(role);
                String permis = (br) ? "чтение-запись" : "только чтение";
                String login = rs.getString(2).trim().toUpperCase();
                Record sysuserRec = qSysuser.stream().filter(rec -> login.equalsIgnoreCase(rec.getStr(eSysuser.login)) == true)
                        .findFirst().orElse(eSysuser.up.newRecord(Query.INS));
                if (sysuserRec.get(eSysuser.id) == null) {
                    sysuserRec.setNo(eSysuser.role, role);
                    sysuserRec.setNo(eSysuser.login, login);
                    userList.add(sysuserRec);
                }
                Object fio = (sysuserRec.get(eSysuser.fio) == null) ? "" : sysuserRec.get(eSysuser.fio);
                Object phone = (sysuserRec.get(eSysuser.phone) == null) ? "" : rs.getObject("phone");
                Object email = (sysuserRec.get(eSysuser.email) == null) ? "" : rs.getObject("email");
                List rec = List.of(++npp, login, permis, role, fio, phone, email);
                Vector vec = new Vector(rec);
                dm.getDataVector().add(vec);
            }
            userList.forEach(rec -> rec.setNo(eSysuser.id, Conn.genId(eSysuser.up)));
            userList.execsql();
            qSysuser.addAll(userList);
            ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab4);

        } catch (Exception e) {
            System.err.println("Ошибка: Adm.loadingTab4() " + e);
        }
    }

    private void clearListQue() {

        if (listQue.isEmpty() == false) {
            for (int i = 0; i < listQue.size(); ++i) {
                Object obj[] = listQue.poll();
                if (obj.length == 2) {
                    if (obj[0] instanceof Color && obj[1] instanceof String) {
                        appendToPane(obj[1].toString() + "\n", (Color) obj[0]);
                    }
                } else {
                    if (obj[0] instanceof Color && obj[1] instanceof String && obj[2] instanceof Color && obj[3] instanceof String) {
                        appendToPane(obj[1].toString(), (Color) obj[0]);
                        appendToPane(obj[3].toString() + "\n", (Color) obj[2]);
                    }
                }
            }
        }
    }

    private void appendToPane(String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = txtPane.getDocument().getLength();
        txtPane.setCaretPosition(len);
        txtPane.setCharacterAttributes(aset, false);
        txtPane.replaceSelection(msg);
    }

    private void connectBaseNumb(String num_base) {
        PathToDb frame = new PathToDb(this, num_base);
        frame.setVisible(true);

        if (ePref.base_num.getProp().equals("1")) {
            btnT7.setSelected(true);
            mn631.setSelected(true);

        } else if (ePref.base_num.getProp().equals("2")) {
            btnT8.setSelected(true);
            mn632.setSelected(true);

        } else if (ePref.base_num.getProp().equals("3")) {
            btnT9.setSelected(true);
            mn633.setSelected(true);
        }
        loadingPath();
    }

    private void mnLookAndFeel(ActionEvent evt) {
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            if (((JCheckBoxMenuItem) evt.getSource()).getText().equals(laf.getName()) == true) {
                ePref.lookandfeel.putProp(laf.getName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonLookAndFiilGroup = new javax.swing.ButtonGroup();
        buttonBaseGroup1 = new javax.swing.ButtonGroup();
        buttonBaseGroup2 = new javax.swing.ButtonGroup();
        ppmMain = new javax.swing.JPopupMenu();
        mn20 = new javax.swing.JMenuItem();
        mn10 = new javax.swing.JMenuItem();
        mn50 = new javax.swing.JMenuItem();
        mn40 = new javax.swing.JMenuItem();
        sep2 = new javax.swing.JPopupMenu.Separator();
        mn63 = new javax.swing.JMenu();
        mn631 = new javax.swing.JCheckBoxMenuItem();
        mn632 = new javax.swing.JCheckBoxMenuItem();
        mn633 = new javax.swing.JCheckBoxMenuItem();
        mn62 = new javax.swing.JMenu();
        sep1 = new javax.swing.JPopupMenu.Separator();
        mn30 = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnMenu = new javax.swing.JButton();
        toolBar1 = new javax.swing.JToolBar();
        btnConv = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        btnBaseEdit = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 32767));
        btnLogin = new javax.swing.JButton();
        toolBar2 = new javax.swing.JToolBar();
        btnT7 = new javax.swing.JToggleButton();
        btnT8 = new javax.swing.JToggleButton();
        btnT9 = new javax.swing.JToggleButton();
        center = new javax.swing.JPanel();
        pan8 = new javax.swing.JPanel();
        pan5 = new javax.swing.JPanel();
        pan6 = new javax.swing.JPanel();
        lab1 = new javax.swing.JLabel();
        lab2 = new javax.swing.JLabel();
        lab3 = new javax.swing.JLabel();
        lab4 = new javax.swing.JLabel();
        edServer = new javax.swing.JTextField();
        edPath = new javax.swing.JTextField();
        edUser = new javax.swing.JTextField();
        lab5 = new javax.swing.JLabel();
        edPort = new javax.swing.JTextField();
        edPass = new javax.swing.JTextField();
        labPath2 = new javax.swing.JLabel();
        lab6 = new javax.swing.JLabel();
        btn10 = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnStart = new javax.swing.JButton();
        pn7 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        pan4 = new javax.swing.JPanel();
        txtPane = new javax.swing.JTextPane();
        pan2 = new javax.swing.JPanel();
        pan9 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan10 = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        pan3 = new javax.swing.JPanel();
        pan11 = new javax.swing.JPanel();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        pan14 = new javax.swing.JPanel();
        btnIns = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnSysdba = new javax.swing.JButton();
        pan13 = new javax.swing.JPanel();
        pan12 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        box1 = new javax.swing.JComboBox<>();
        txt1 = new javax.swing.JTextField();
        box2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        txt2 = new javax.swing.JPasswordField();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt5 = new javax.swing.JTextField();
        txt6 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt7 = new javax.swing.JTextField();
        pan15 = new javax.swing.JPanel();
        south = new javax.swing.JPanel();

        ppmMain.setFont(frames.UGui.getFont(1,1));

        mn20.setFont(frames.UGui.getFont(1,1));
        mn20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b059.gif"))); // NOI18N
        mn20.setActionCommand("sa-okna <= ПрофСтрой(3,4)");
        mn20.setLabel("БД <= ПрофСтрой(3,4)");
        mn20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnCard(evt);
            }
        });
        ppmMain.add(mn20);

        mn10.setFont(frames.UGui.getFont(1,1));
        mn10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b051.gif"))); // NOI18N
        mn10.setText("Правка БД");
        mn10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnCard(evt);
            }
        });
        ppmMain.add(mn10);

        mn50.setFont(frames.UGui.getFont(1,1));
        mn50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b041.gif"))); // NOI18N
        mn50.setText("Выполнить скрипт");
        mn50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnCard(evt);
            }
        });
        ppmMain.add(mn50);

        mn40.setFont(frames.UGui.getFont(1,1));
        mn40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b032.gif"))); // NOI18N
        mn40.setText("Пользователи");
        mn40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnCard(evt);
            }
        });
        ppmMain.add(mn40);
        ppmMain.add(sep2);

        mn63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b028.gif"))); // NOI18N
        mn63.setText("База данных");
        mn63.setFont(frames.UGui.getFont(1,1));

        buttonBaseGroup1.add(mn631);
        mn631.setFont(frames.UGui.getFont(1,1));
        mn631.setText("База 1");
        mn631.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBase(evt);
            }
        });
        mn63.add(mn631);

        buttonBaseGroup1.add(mn632);
        mn632.setFont(frames.UGui.getFont(1,1));
        mn632.setText("База 2");
        mn632.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBase(evt);
            }
        });
        mn63.add(mn632);

        buttonBaseGroup1.add(mn633);
        mn633.setFont(frames.UGui.getFont(1,1));
        mn633.setText("База 3");
        mn633.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBase(evt);
            }
        });
        mn63.add(mn633);

        ppmMain.add(mn63);

        mn62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b061.gif"))); // NOI18N
        mn62.setText("Вид интерфейса");
        mn62.setFont(frames.UGui.getFont(1,1));
        ppmMain.add(mn62);
        ppmMain.add(sep1);

        mn30.setFont(frames.UGui.getFont(1,1));
        mn30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b009.gif"))); // NOI18N
        mn30.setText("Выход");
        mn30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn30mnExit(evt);
            }
        });
        ppmMain.add(mn30);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setPreferredSize(new java.awt.Dimension(900, 503));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Adm.this.windowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                Adm.this.windowClosing(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(900, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.ePref.locale); // NOI18N
        btnClose.setToolTipText(bundle.getString("Закрыть")); // NOI18N
        btnClose.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setMaximumSize(new java.awt.Dimension(25, 25));
        btnClose.setMinimumSize(new java.awt.Dimension(25, 25));
        btnClose.setPreferredSize(new java.awt.Dimension(25, 25));
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnExit(evt);
            }
        });

        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c053.gif"))); // NOI18N
        btnReport.setToolTipText(bundle.getString("Печать")); // NOI18N
        btnReport.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnReport.setFocusable(false);
        btnReport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReport.setMaximumSize(new java.awt.Dimension(25, 25));
        btnReport.setMinimumSize(new java.awt.Dimension(25, 25));
        btnReport.setPreferredSize(new java.awt.Dimension(25, 25));
        btnReport.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReport(evt);
            }
        });

        btnMenu.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c032.gif"))); // NOI18N
        btnMenu.setText("Гл. меню");
        btnMenu.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMenu.setFocusable(false);
        btnMenu.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMenu.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMenu.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMenu.setPreferredSize(new java.awt.Dimension(96, 25));
        btnMenu.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenu(evt);
            }
        });

        toolBar1.setRollover(true);
        toolBar1.setPreferredSize(new java.awt.Dimension(112, 28));

        btnConv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c070.gif"))); // NOI18N
        btnConv.setToolTipText(bundle.getString("Конвертор базы данных")); // NOI18N
        btnConv.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnConv.setFocusable(false);
        btnConv.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConv.setMaximumSize(new java.awt.Dimension(25, 25));
        btnConv.setMinimumSize(new java.awt.Dimension(25, 25));
        btnConv.setPreferredSize(new java.awt.Dimension(25, 25));
        btnConv.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnConv.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCard(evt);
            }
        });
        toolBar1.add(btnConv);
        toolBar1.add(filler1);

        btnBaseEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c068.gif"))); // NOI18N
        btnBaseEdit.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnBaseEdit.setFocusable(false);
        btnBaseEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBaseEdit.setMaximumSize(new java.awt.Dimension(25, 25));
        btnBaseEdit.setMinimumSize(new java.awt.Dimension(25, 25));
        btnBaseEdit.setPreferredSize(new java.awt.Dimension(25, 25));
        btnBaseEdit.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnBaseEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBaseEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCard(evt);
            }
        });
        toolBar1.add(btnBaseEdit);
        toolBar1.add(filler2);

        btnLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c069.gif"))); // NOI18N
        btnLogin.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnLogin.setFocusable(false);
        btnLogin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLogin.setMaximumSize(new java.awt.Dimension(25, 25));
        btnLogin.setMinimumSize(new java.awt.Dimension(25, 25));
        btnLogin.setPreferredSize(new java.awt.Dimension(25, 25));
        btnLogin.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnLogin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCard(evt);
            }
        });
        toolBar1.add(btnLogin);

        toolBar2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        toolBar2.setRollover(true);
        toolBar2.setPreferredSize(new java.awt.Dimension(96, 28));

        buttonBaseGroup2.add(btnT7);
        btnT7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c057.gif"))); // NOI18N
        btnT7.setSelected(true);
        btnT7.setFocusable(false);
        btnT7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnT7.setMaximumSize(new java.awt.Dimension(28, 25));
        btnT7.setMinimumSize(new java.awt.Dimension(28, 25));
        btnT7.setPreferredSize(new java.awt.Dimension(28, 25));
        btnT7.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c061.gif"))); // NOI18N
        btnT7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnT7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBase(evt);
            }
        });
        toolBar2.add(btnT7);

        buttonBaseGroup2.add(btnT8);
        btnT8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c058.gif"))); // NOI18N
        btnT8.setFocusable(false);
        btnT8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnT8.setMaximumSize(new java.awt.Dimension(28, 25));
        btnT8.setMinimumSize(new java.awt.Dimension(28, 25));
        btnT8.setPreferredSize(new java.awt.Dimension(28, 25));
        btnT8.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c062.gif"))); // NOI18N
        btnT8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnT8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBase(evt);
            }
        });
        toolBar2.add(btnT8);

        buttonBaseGroup2.add(btnT9);
        btnT9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c059.gif"))); // NOI18N
        btnT9.setFocusable(false);
        btnT9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnT9.setMaximumSize(new java.awt.Dimension(28, 25));
        btnT9.setMinimumSize(new java.awt.Dimension(28, 25));
        btnT9.setPreferredSize(new java.awt.Dimension(28, 25));
        btnT9.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c063.gif"))); // NOI18N
        btnT9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnT9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBase(evt);
            }
        });
        toolBar2.add(btnT9);

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(toolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(toolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(156, 156, 156)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 326, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(toolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(toolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        center.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        center.setPreferredSize(new java.awt.Dimension(865, 500));
        center.setLayout(new java.awt.CardLayout());

        pan8.setPreferredSize(new java.awt.Dimension(861, 500));

        javax.swing.GroupLayout pan8Layout = new javax.swing.GroupLayout(pan8);
        pan8.setLayout(pan8Layout);
        pan8Layout.setHorizontalGroup(
            pan8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 896, Short.MAX_VALUE)
        );
        pan8Layout.setVerticalGroup(
            pan8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );

        center.add(pan8, "pan8");

        pan5.setPreferredSize(new java.awt.Dimension(500, 500));
        pan5.setLayout(new java.awt.BorderLayout());

        pan6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pan6.setPreferredSize(new java.awt.Dimension(500, 72));

        lab1.setFont(frames.UGui.getFont(0,0));
        lab1.setText("Cервер (host)");
        lab1.setAlignmentX(0.5F);
        lab1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab1.setMaximumSize(new java.awt.Dimension(100, 18));
        lab1.setMinimumSize(new java.awt.Dimension(0, 18));
        lab1.setPreferredSize(new java.awt.Dimension(80, 19));

        lab2.setFont(frames.UGui.getFont(0,0));
        lab2.setText("База источник");
        lab2.setAlignmentX(0.5F);
        lab2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab2.setMinimumSize(new java.awt.Dimension(100, 18));
        lab2.setPreferredSize(new java.awt.Dimension(84, 19));

        lab3.setFont(frames.UGui.getFont(0,0));
        lab3.setText("Пользователь");
        lab3.setAlignmentX(0.5F);
        lab3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab3.setMaximumSize(new java.awt.Dimension(100, 18));
        lab3.setMinimumSize(new java.awt.Dimension(0, 18));
        lab3.setPreferredSize(new java.awt.Dimension(80, 19));

        lab4.setFont(frames.UGui.getFont(0,0));
        lab4.setText("Пароль");
        lab4.setAlignmentX(0.5F);
        lab4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab4.setPreferredSize(new java.awt.Dimension(46, 19));

        edServer.setFont(frames.UGui.getFont(0,0));
        edServer.setText("localhost");
        edServer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        edServer.setMinimumSize(new java.awt.Dimension(0, 0));
        edServer.setPreferredSize(new java.awt.Dimension(72, 18));
        edServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edServerActionPerformed(evt);
            }
        });

        edPath.setFont(frames.UGui.getFont(0,0));
        edPath.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        edPath.setMinimumSize(new java.awt.Dimension(0, 0));
        edPath.setPreferredSize(new java.awt.Dimension(200, 18));

        edUser.setFont(frames.UGui.getFont(0,0));
        edUser.setText("sysdba");
        edUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        edUser.setFocusable(false);
        edUser.setMinimumSize(new java.awt.Dimension(0, 0));
        edUser.setPreferredSize(new java.awt.Dimension(72, 18));

        lab5.setFont(frames.UGui.getFont(0,0));
        lab5.setText("Порт");
        lab5.setAlignmentX(0.5F);
        lab5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab5.setMaximumSize(new java.awt.Dimension(40, 18));
        lab5.setMinimumSize(new java.awt.Dimension(40, 18));
        lab5.setPreferredSize(new java.awt.Dimension(46, 19));

        edPort.setFont(frames.UGui.getFont(0,0));
        edPort.setText("3050");
        edPort.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        edPort.setMinimumSize(new java.awt.Dimension(0, 0));
        edPort.setPreferredSize(new java.awt.Dimension(72, 18));

        edPass.setFont(frames.UGui.getFont(0,0));
        edPass.setText("masterkey");
        edPass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        edPass.setMinimumSize(new java.awt.Dimension(0, 0));
        edPass.setPreferredSize(new java.awt.Dimension(72, 18));

        labPath2.setBackground(new java.awt.Color(255, 255, 255));
        labPath2.setFont(frames.UGui.getFont(0,0));
        labPath2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        labPath2.setFocusable(false);
        labPath2.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        labPath2.setOpaque(true);
        labPath2.setPreferredSize(new java.awt.Dimension(200, 18));

        lab6.setFont(frames.UGui.getFont(0,0));
        lab6.setText("База приемник");
        lab6.setAlignmentX(0.5F);
        lab6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab6.setPreferredSize(new java.awt.Dimension(84, 19));

        btn10.setText("...");
        btn10.setToolTipText(bundle.getString("Печать")); // NOI18N
        btn10.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn10.setName("btnField17"); // NOI18N
        btn10.setPreferredSize(new java.awt.Dimension(18, 19));
        btn10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn10btnAction(evt);
            }
        });

        btnTest.setFont(frames.UGui.getFont(0,0));
        btnTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b028.gif"))); // NOI18N
        btnTest.setText("Тест");
        btnTest.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTest.setMargin(new java.awt.Insets(0, 14, 2, 14));
        btnTest.setMaximumSize(new java.awt.Dimension(21, 21));
        btnTest.setMinimumSize(new java.awt.Dimension(0, 0));
        btnTest.setPreferredSize(new java.awt.Dimension(54, 25));
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestBtnStartClick(evt);
            }
        });

        btnStart.setFont(frames.UGui.getFont(0,0));
        btnStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b059.gif"))); // NOI18N
        btnStart.setText("Конвертировать");
        btnStart.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnStart.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStart.setMargin(new java.awt.Insets(0, 14, 2, 14));
        btnStart.setMaximumSize(new java.awt.Dimension(120, 25));
        btnStart.setMinimumSize(new java.awt.Dimension(0, 0));
        btnStart.setPreferredSize(new java.awt.Dimension(80, 25));
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStart(evt);
            }
        });

        javax.swing.GroupLayout pan6Layout = new javax.swing.GroupLayout(pan6);
        pan6.setLayout(pan6Layout);
        pan6Layout.setHorizontalGroup(
            pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lab1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(edServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addComponent(lab5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lab2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edPath, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan6Layout.createSequentialGroup()
                        .addComponent(lab4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lab6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labPath2, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pan6Layout.setVerticalGroup(
            pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan6Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))
                    .addGroup(pan6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(edUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lab4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(edPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lab6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(labPath2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pan5.add(pan6, java.awt.BorderLayout.PAGE_START);

        pn7.setPreferredSize(new java.awt.Dimension(20, 20));
        pn7.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);

        pan4.setLayout(new java.awt.BorderLayout());

        txtPane.setBackground(new java.awt.Color(232, 233, 236));
        txtPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 6, 1, 1));
        pan4.add(txtPane, java.awt.BorderLayout.CENTER);

        scr1.setViewportView(pan4);

        pn7.add(scr1, java.awt.BorderLayout.CENTER);

        pan5.add(pn7, java.awt.BorderLayout.CENTER);

        center.add(pan5, "pan5");

        pan2.setPreferredSize(new java.awt.Dimension(654, 504));
        pan2.setLayout(new java.awt.BorderLayout());

        pan9.setPreferredSize(new java.awt.Dimension(360, 500));
        pan9.setLayout(new java.awt.BorderLayout());

        scr2.setPreferredSize(new java.awt.Dimension(360, 404));

        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "№пп", "Таблица", "Описание"
            }
        ));
        tab2.setFillsViewportHeight(true);
        tab2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setMaxWidth(40);
            tab2.getColumnModel().getColumn(1).setMaxWidth(80);
        }

        pan9.add(scr2, java.awt.BorderLayout.CENTER);

        pan2.add(pan9, java.awt.BorderLayout.WEST);

        pan10.setPreferredSize(new java.awt.Dimension(400, 500));
        pan10.setLayout(new java.awt.BorderLayout());

        scr3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tab3.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tab3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scr3.setViewportView(tab3);

        pan10.add(scr3, java.awt.BorderLayout.CENTER);

        pan2.add(pan10, java.awt.BorderLayout.CENTER);

        center.add(pan2, "pan2");

        pan3.setLayout(new java.awt.CardLayout());

        pan11.setPreferredSize(new java.awt.Dimension(348, 496));
        pan11.setRequestFocusEnabled(false);
        pan11.setLayout(new java.awt.BorderLayout());

        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "№пп", "Пользователь", "Права доступа", "Профиль", "ФИО", "Телефон", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab4.setFillsViewportHeight(true);
        tab4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(0).setPreferredWidth(40);
            tab4.getColumnModel().getColumn(0).setMaxWidth(40);
        }

        pan11.add(scr4, java.awt.BorderLayout.CENTER);

        pan14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pan14.setPreferredSize(new java.awt.Dimension(548, 29));

        btnIns.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b042.gif"))); // NOI18N
        btnIns.setToolTipText(bundle.getString("Добавить пользователя")); // NOI18N
        btnIns.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnIns.setFocusable(false);
        btnIns.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnIns.setMaximumSize(new java.awt.Dimension(25, 25));
        btnIns.setMinimumSize(new java.awt.Dimension(25, 25));
        btnIns.setPreferredSize(new java.awt.Dimension(25, 25));
        btnIns.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnIns.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnIns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userAdded(evt);
            }
        });

        btnUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b044.gif"))); // NOI18N
        btnUp.setToolTipText(bundle.getString("Изменить пароль пользователя")); // NOI18N
        btnUp.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnUp.setFocusable(false);
        btnUp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUp.setMaximumSize(new java.awt.Dimension(25, 25));
        btnUp.setMinimumSize(new java.awt.Dimension(25, 25));
        btnUp.setPreferredSize(new java.awt.Dimension(25, 25));
        btnUp.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnUp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userUpdate(evt);
            }
        });

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b043.gif"))); // NOI18N
        btnDel.setToolTipText(bundle.getString("Удалить пользователя")); // NOI18N
        btnDel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnDel.setFocusable(false);
        btnDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDel.setMaximumSize(new java.awt.Dimension(25, 25));
        btnDel.setMinimumSize(new java.awt.Dimension(25, 25));
        btnDel.setPreferredSize(new java.awt.Dimension(25, 25));
        btnDel.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnDel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userDelete(evt);
            }
        });

        btnSysdba.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c071.gif"))); // NOI18N
        btnSysdba.setToolTipText(bundle.getString("Изменить пароль администратора")); // NOI18N
        btnSysdba.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnSysdba.setFocusable(false);
        btnSysdba.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSysdba.setMaximumSize(new java.awt.Dimension(25, 25));
        btnSysdba.setMinimumSize(new java.awt.Dimension(25, 25));
        btnSysdba.setPreferredSize(new java.awt.Dimension(25, 25));
        btnSysdba.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnSysdba.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSysdba.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSysdba(evt);
            }
        });

        javax.swing.GroupLayout pan14Layout = new javax.swing.GroupLayout(pan14);
        pan14.setLayout(pan14Layout);
        pan14Layout.setHorizontalGroup(
            pan14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(btnSysdba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(722, Short.MAX_VALUE))
        );
        pan14Layout.setVerticalGroup(
            pan14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnIns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnDel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnSysdba, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pan11.add(pan14, java.awt.BorderLayout.PAGE_START);

        pan3.add(pan11, "pan11");

        pan13.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Профиль");
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel1.setPreferredSize(new java.awt.Dimension(160, 18));

        jLabel2.setText("Пользователь  (english)");
        jLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel2.setPreferredSize(new java.awt.Dimension(160, 18));

        jLabel3.setText("Права");
        jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel3.setPreferredSize(new java.awt.Dimension(160, 18));

        jLabel4.setText("Пароль  (english)");
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel4.setPreferredSize(new java.awt.Dimension(160, 18));

        box1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Технолог", "Менеджер" }));
        box1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        box1.setPreferredSize(new java.awt.Dimension(140, 20));

        txt1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt1.setPreferredSize(new java.awt.Dimension(140, 18));

        box2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "чтение-запись", "только чтение", " " }));
        box2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        box2.setPreferredSize(new java.awt.Dimension(140, 20));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b044.gif"))); // NOI18N
        jButton1.setText("OK");
        jButton1.setPreferredSize(new java.awt.Dimension(120, 23));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminOk(evt);
            }
        });

        txt2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt2.setPreferredSize(new java.awt.Dimension(140, 20));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b044.gif"))); // NOI18N
        jButton2.setText("Отмена");
        jButton2.setPreferredSize(new java.awt.Dimension(120, 23));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                admCancel(evt);
            }
        });

        jLabel5.setText("Телефон");
        jLabel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel5.setPreferredSize(new java.awt.Dimension(60, 18));

        jLabel6.setText("ФИО");
        jLabel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel6.setPreferredSize(new java.awt.Dimension(60, 18));

        txt5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt5.setPreferredSize(new java.awt.Dimension(96, 18));

        txt6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt6.setPreferredSize(new java.awt.Dimension(120, 18));

        jLabel7.setText("E-mail");
        jLabel7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel7.setPreferredSize(new java.awt.Dimension(60, 18));

        txt7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt7.setPreferredSize(new java.awt.Dimension(140, 18));

        javax.swing.GroupLayout pan12Layout = new javax.swing.GroupLayout(pan12);
        pan12.setLayout(pan12Layout);
        pan12Layout.setHorizontalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan12Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(box2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan12Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan12Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(box1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan12Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pan12Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(40, 40, 40)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(304, Short.MAX_VALUE))
        );
        pan12Layout.setVerticalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan12Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(box1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(box2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(261, Short.MAX_VALUE))
        );

        pan13.add(pan12, java.awt.BorderLayout.CENTER);

        pan3.add(pan13, "pan13");

        center.add(pan3, "pan3");

        javax.swing.GroupLayout pan15Layout = new javax.swing.GroupLayout(pan15);
        pan15.setLayout(pan15Layout);
        pan15Layout.setHorizontalGroup(
            pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 896, Short.MAX_VALUE)
        );
        pan15Layout.setVerticalGroup(
            pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );

        center.add(pan15, "pan6");

        getContentPane().add(center, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(900, 20));

        javax.swing.GroupLayout southLayout = new javax.swing.GroupLayout(south);
        south.setLayout(southLayout);
        southLayout.setHorizontalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 896, Short.MAX_VALUE)
        );
        southLayout.setVerticalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnExit(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnExit
        List.of(App.values()).stream().filter(el -> el.frame != null).forEach(el -> el.frame.dispose());
    }//GEN-LAST:event_mnExit

    private void userDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userDelete
        if (JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить текущего пользователя?", "Предупреждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
            int row = tab4.getSelectedRow();
            if (row != -1) {
                try {
                    String login = String.valueOf(tab4.getValueAt(row, 1));
                    Conn.deleteUser2(login);

                    Record rec = qSysuser.stream().filter(rec2 -> login.equals(rec2.get(eSysuser.login)) == true)
                            .findFirst().orElse(eSysuser.up.newRecord(Query.INS));
                    if (rec.get(eSysuser.id) != null) {
                        qSysuser.delete(rec);
                        qSysuser.remove(rec);
                    }
                    loadingTab4();

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Ошибка удаления пользователя", "ВНИМАНИЕ!", 1);
                    System.err.println("Ошибка:Adm.userDelete() " + e);
                }
            }
        }
    }//GEN-LAST:event_userDelete

    private void userUpdate(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userUpdate
        int row = tab4.getSelectedRow();
        card = 'U';
        if (row != -1) {
            box1.setEnabled(false);
            box2.setEnabled(false);
            txt1.setEnabled(false);

            String role = tab4.getValueAt(row, 3).toString().substring(0, 8);
            int index1 = ("TEXNOLOG".equals(role)) ? 1 : 2;
            box1.setSelectedIndex(index1);
            
            int index2 = ("чтение-запись".equals(tab4.getValueAt(row, 2))) ? 0 : 1;
            box2.setSelectedIndex(index2);
            txt1.setText(String.valueOf(tab4.getValueAt(row, 1)));
            txt2.setText(String.valueOf(ePref.password));
            txt7.setText(String.valueOf(tab4.getValueAt(row, 6)));
            txt5.setText(String.valueOf(tab4.getValueAt(row, 5)));
            txt6.setText(String.valueOf(tab4.getValueAt(row, 4)));
            ((CardLayout) pan3.getLayout()).show(pan3, "pan13");
        }
    }//GEN-LAST:event_userUpdate

    private void userAdded(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userAdded
        card = 'I';
        box1.setEnabled(true);
        box1.setSelectedIndex(0);
        box2.setEnabled(true);
        box2.setSelectedIndex(0);
        txt1.setEnabled(true);
        txt1.setText("");
        ((CardLayout) pan3.getLayout()).show(pan3, "pan13");
    }//GEN-LAST:event_userAdded

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
    }//GEN-LAST:event_btnReport

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        try {
            timer.stop();
            if (thread != null) {
                thread.join();
            }
        } catch (InterruptedException e) {
            System.out.println("Ошибка:Adm.windowClosed()");
        }
    }//GEN-LAST:event_windowClosed

    private void windowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosing
        mnExit(null);
    }//GEN-LAST:event_windowClosing

    private void btnBase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBase
        String num_base = (btnT7.isSelected()) ? "1" : (btnT8.isSelected()) ? "2" : "3";
        connectBaseNumb(num_base);
    }//GEN-LAST:event_btnBase

    private void btnMenu(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenu
        ppmMain.show(btnMenu, center.getX(), center.getY());
    }//GEN-LAST:event_btnMenu

    private void btnCard(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCard
        JButton button = (JButton) evt.getSource();
        if (button == btnConv) {
            ((CardLayout) center.getLayout()).show(center, "pan5");
            timer.start();
            loadingPath();

        } else if (button == btnBaseEdit) {
            ((CardLayout) center.getLayout()).show(center, "pan2");
            loadingTab2();

        } else if (button == btnLogin) {
            ((CardLayout) center.getLayout()).show(center, "pan3");
            loadingTab4();
        }
    }//GEN-LAST:event_btnCard

    private void btn10btnAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn10btnAction
        FileFilter filter = new FileFilter();
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileFilter(filter);
        int result = chooser.showDialog(this, "Выбрать");
        if (result == JFileChooser.APPROVE_OPTION) {
            edPath.setText(chooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_btn10btnAction

    private void btnStart(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStart
        try {
            Query.listOpenTable.clear();
            ePref.user.putProp("sysdba");
            ePref.password = String.valueOf("masterkey");
            String num_base = ePref.base_num.getProp();
            Conn.connection(ePref.getServer(num_base), ePref.getPort(num_base), ePref.getBase(num_base), ePref.user.getProp(), ePref.password.toCharArray(), null);
            Connection c2 = Conn.getConnection();

            Conn con1 = new Conn();
            con1.connection(edServer.getText().trim(), edPort.getText().trim(), edPath.getText().trim(), edUser.getText().trim(), edPass.getText().toCharArray(), null);
            Connection c1 = con1.getConnection();

            txtPane.setText("");
            thread = new Thread(new Runnable() {
                public void run() {
                    PSConvert.exec(listQue, c1, c2);
                }

            });
            thread.start();

        } catch (Exception e) {
            System.err.println(e);
        }
    }//GEN-LAST:event_btnStart

    private void btnTestBtnStartClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestBtnStartClick
        Conn Src = new Conn();
        eExcep excep = Src.connection(edServer.getText().trim(), edPort.getText().trim(),
                edPath.getText().trim(), edUser.getText().trim(), edPass.getText().toCharArray(), null);
        JOptionPane.showMessageDialog(this, edPath.getText().trim() + "  \n" + excep.mes, "Сообщение", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnTestBtnStartClick

    private void mn30mnExit(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mn30mnExit
        List.of(App.values()).stream().filter(el -> el.frame != null).forEach(el -> el.frame.dispose());
    }//GEN-LAST:event_mn30mnExit

    private void mnBase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnBase
        String num_base = (mn631.isSelected()) ? "1" : (mn632.isSelected()) ? "2" : "3";
        connectBaseNumb(num_base);
    }//GEN-LAST:event_mnBase

    private void mnCard(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnCard
        JMenuItem button = (JMenuItem) evt.getSource();
        if (button == mn20) {
            ((CardLayout) center.getLayout()).show(center, "pan5");
            timer.start();
            loadingPath();

        } else if (button == mn10) {
            ((CardLayout) center.getLayout()).show(center, "pan2");
            loadingTab2();

        } else if (button == mn40) {
            ((CardLayout) center.getLayout()).show(center, "pan3");

        } else if (button == mn50) {
            ((CardLayout) center.getLayout()).show(center, "pan6");
            loadingTab4();
        }
    }//GEN-LAST:event_mnCard

    private void adminOk(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminOk

        String login = txt1.getText().trim().toUpperCase();
        String fio = txt6.getText().trim();
        String phone = txt5.getText().trim();
        String email = txt7.getText().trim();
        Record sysuserRec = qSysuser.stream().filter(rec2
                -> login.equalsIgnoreCase(rec2.getStr(eSysuser.login)) == true)
                .findFirst().orElse(eSysuser.up.newRecord(Query.INS));
        if (card == 'I') {
            if (validation() == true) {
                String role = (box1.getSelectedIndex() == 1) ? "TEXNOLOG" : "MANAGER";
                String role2 = (box2.getSelectedIndex() == 0) ? role + "_RW" : role + "_RO";
                Conn.addUser(login, txt2.getPassword(), role2);
                if (sysuserRec.get(eSysuser.id) == null) {
                    sysuserRec.set(eSysuser.id, Conn.genId(eSysuser.up));
                    sysuserRec.set(eSysuser.login, login);
                    sysuserRec.set(eSysuser.role, role);
                    sysuserRec.set(eSysuser.fio, fio);
                    sysuserRec.set(eSysuser.phone, phone);
                    sysuserRec.set(eSysuser.email, email);
                    qSysuser.insert(sysuserRec);
                    qSysuser.add(sysuserRec);
                }

                loadingTab4();
                ((CardLayout) pan3.getLayout()).show(pan3, "pan11");
            }
        } else if (card == 'U') {
            if (validation() == true) {
                sysuserRec.set(eSysuser.fio, fio);
                sysuserRec.set(eSysuser.phone, phone);
                sysuserRec.set(eSysuser.email, email);
                qSysuser.execsql();
                Conn.modifyPassword(login, txt2.getPassword());
                loadingTab4();
                ((CardLayout) pan3.getLayout()).show(pan3, "pan11");
            }
        }
    }//GEN-LAST:event_adminOk

    private boolean validation() {
        if (box1.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Укажите профиль пользователя", "Предупреждение", JOptionPane.NO_OPTION);
            return false;
        } else if (txt1.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Укажите имя пользователя", "Предупреждение", JOptionPane.NO_OPTION);
            return false;
        } else if (txt2.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Укажите пароль пользователя", "Предупреждение", JOptionPane.NO_OPTION);
            return false;
        } else {
            for (char c : txt1.getText().toCharArray()) {
                if (((c >= 'а') && (c <= 'я')) || ((c >= 'А') && (c <= 'Я'))) {
                    JOptionPane.showMessageDialog(this, "В имени пользователя есть символы  принадлежащие русскому алфавиту", "Предупреждение", JOptionPane.NO_OPTION);
                    return false;
                }
            }
            for (char c : txt2.getPassword()) {
                if (((c >= 'а') && (c <= 'я')) || ((c >= 'А') && (c <= 'Я'))) {
                    JOptionPane.showMessageDialog(this, "В пароле есть символы принадлежащие русскому алфавиту", "Предупреждение", JOptionPane.NO_OPTION);
                    return false;
                }
            }
        }
        return true;
    }

    private void admCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_admCancel
        txt1.setText("");
        txt5.setText("");
        txt6.setText("");
        ((CardLayout) pan3.getLayout()).show(pan3, "pan11");
    }//GEN-LAST:event_admCancel

    private void btnSysdba(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSysdba

        JPasswordField pass1 = new JPasswordField();
        JPasswordField pass2 = new JPasswordField();
        Object[] ob = {"Новый пароль SYSDBA", pass1, "Подтвердите новый пароль", pass2};
        int result = JOptionPane.showConfirmDialog(null, ob, "Изменение пароля", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (String.valueOf(pass1.getPassword()).equals(String.valueOf(pass2.getPassword()))) {

                Conn.modifyPassword("sysdba", pass2.getPassword());
                JOptionPane.showMessageDialog(this, "Операция выполнена успешно!", "Изменение пароля SYSDBA", JOptionPane.NO_OPTION);
            } else {
                JOptionPane.showMessageDialog(this, "Имена не совпали", "Неудача", JOptionPane.NO_OPTION);
            }
        }

    }//GEN-LAST:event_btnSysdba

    private void edServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edServerActionPerformed
    }//GEN-LAST:event_edServerActionPerformed

// <editor-fold defaultstate="collapsed" desc="Generated Code">    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> box1;
    private javax.swing.JComboBox<String> box2;
    private javax.swing.JButton btn10;
    private javax.swing.JButton btnBaseEdit;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnConv;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnMenu;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnSysdba;
    private javax.swing.JToggleButton btnT7;
    private javax.swing.JToggleButton btnT8;
    private javax.swing.JToggleButton btnT9;
    private javax.swing.JButton btnTest;
    private javax.swing.JButton btnUp;
    private javax.swing.ButtonGroup buttonBaseGroup1;
    private javax.swing.ButtonGroup buttonBaseGroup2;
    private javax.swing.ButtonGroup buttonLookAndFiilGroup;
    private javax.swing.JPanel center;
    private javax.swing.JTextField edPass;
    private javax.swing.JTextField edPath;
    private javax.swing.JTextField edPort;
    private javax.swing.JTextField edServer;
    private javax.swing.JTextField edUser;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab2;
    private javax.swing.JLabel lab3;
    private javax.swing.JLabel lab4;
    private javax.swing.JLabel lab5;
    private javax.swing.JLabel lab6;
    private javax.swing.JLabel labPath2;
    private javax.swing.JMenuItem mn10;
    private javax.swing.JMenuItem mn20;
    private javax.swing.JMenuItem mn30;
    private javax.swing.JMenuItem mn40;
    private javax.swing.JMenuItem mn50;
    private javax.swing.JMenu mn62;
    private javax.swing.JMenu mn63;
    private javax.swing.JCheckBoxMenuItem mn631;
    private javax.swing.JCheckBoxMenuItem mn632;
    private javax.swing.JCheckBoxMenuItem mn633;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan10;
    private javax.swing.JPanel pan11;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan13;
    private javax.swing.JPanel pan14;
    private javax.swing.JPanel pan15;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan8;
    private javax.swing.JPanel pan9;
    private javax.swing.JPanel pn7;
    private javax.swing.JPopupMenu ppmMain;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JPopupMenu.Separator sep1;
    private javax.swing.JPopupMenu.Separator sep2;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JToolBar toolBar1;
    private javax.swing.JToolBar toolBar2;
    private javax.swing.JTextField txt1;
    private javax.swing.JPasswordField txt2;
    private javax.swing.JTextField txt5;
    private javax.swing.JTextField txt6;
    private javax.swing.JTextField txt7;
    private javax.swing.JTextPane txtPane;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    private void initElements() {
        
        ePref.getWin(this, btnClose, (e) -> {
            ePref.putWin(this, btnClose);
        }); 
        appendToPane("\n", Color.GRAY);
        appendToPane("    Внимание!!! Перенос данных из ПрофСтрой-3 должен\n", Color.GRAY);
        appendToPane("    выполняться под управлением Firebird 2.1 НЕ ВЫШЕ.\n", Color.GRAY);
        appendToPane("    Если версия выше чем 2.1 переустановите Firebird.\n", Color.GRAY);
        appendToPane("\n", Color.GRAY);
        appendToPane("    PS. У Вас установлена версия Firebird " + Conn.version() + "\n", Color.GRAY);

        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            JCheckBoxMenuItem mnIt = new javax.swing.JCheckBoxMenuItem();
            buttonLookAndFiilGroup.add(mnIt);
            hmLookAndFill.put(laf.getName(), mnIt);
            mn62.add(mnIt);
            mnIt.setFont(frames.UGui.getFont(1, 1));
            mnIt.setText(laf.getName());
            mnIt.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mnLookAndFeel(evt);
                }
            });
            if (lookAndFeel.getName().equals(laf.getName())) {
                mnIt.setSelected(true);
            }
        }
        if (ePref.base_num.getProp().equals("1")) {
            btnT7.setSelected(true);
            mn631.setSelected(true);

        } else if (ePref.base_num.getProp().equals("2")) {
            btnT8.setSelected(true);
            mn632.setSelected(true);

        } else if (ePref.base_num.getProp().equals("3")) {
            btnT9.setSelected(true);
            mn633.setSelected(true);
        }
        tab2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    loadingTab3();
                }
            }
        });
    }
}
