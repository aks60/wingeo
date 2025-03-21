package startup;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import frames.swing.cmp.ProgressBar;
import common.eProp;
import dataset.Record;
import builder.Wincalc;
import common.listener.ListenerAction;
import domain.eSysprod;
import java.awt.Frame;
import java.util.HashMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import common.listener.ListenerFrame;
import dataset.Query;
import frames.PathToDb;
import frames.UGui;
import frames.swing.cmp.MainMenu;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JToggleButton;

/**
 * <p>
 * Технолог</p>
 */
public class Tex extends javax.swing.JFrame {

    private Wincalc winc = new Wincalc();
    private javax.swing.Timer timer = null;
    private ListenerFrame listenerMenu;
    private HashMap<String, JCheckBoxMenuItem> hmLookAndFill = new HashMap<String, JCheckBoxMenuItem>();

    public Tex() {

        initComponents();
        initElements();
    }

    private void winc_build() {
        int sysprodID = Integer.valueOf(eProp.sysprodID.getProp());
        Record sysprodRec = eSysprod.find(sysprodID);
        if (sysprodRec != null) {
            String script = sysprodRec.getStr(eSysprod.script);
            if (script != null && script.isEmpty() == false) {
                JsonElement script2 = new Gson().fromJson(script, JsonElement.class);
                script2.getAsJsonObject().addProperty("nuni", sysprodRec.getInt(eSysprod.systree_id)); //запишем nuni в script
                winc.build(script2.toString()); //калькуляция изделия                
            }
        }
    }

    private void mnLookAndFeel(java.awt.event.ActionEvent evt) {
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            if (((JCheckBoxMenuItem) evt.getSource()).getText().equals(laf.getName()) == true) {
                eProp.lookandfeel.putProp(laf.getName());
            }
        }
    }

    private void prepareConnectBaseNumb(String new_num_base) {

        String old_num_base = eProp.base_num.getProp();
        List.of(App.values()).stream().filter(el -> el.frame != null && el != App.Top).forEach(el
                -> UGui.findComponents(el.frame.getRootPane(), JTable.class)
                        .forEach(c -> UGui.stopCellEditing(c)));
        List.of(App.values()).stream().filter(el -> el.frame != null && el != App.Top).forEach(el -> el.frame.dispose());
        Query.listOpenTable.forEach(q -> q.execsql());
        Query.listOpenTable.forEach(q -> q.clear());

        PathToDb pathToDb = new PathToDb(App.Top.frame, new_num_base);
        pathToDb.setVisible(true);

        if (eProp.base_num.getProp().equals("1")) {
            btnT7.setSelected(true);
            mn631.setSelected(true);

        } else if (eProp.base_num.getProp().equals("2")) {
            btnT8.setSelected(true);
            mn632.setSelected(true);

        } else if (eProp.base_num.getProp().equals("3")) {
            btnT9.setSelected(true);
            mn633.setSelected(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonLookAndFeelGroup = new javax.swing.ButtonGroup();
        buttonBaseGroup1 = new javax.swing.ButtonGroup();
        buttonMenuGroup = new javax.swing.ButtonGroup();
        ppmSpecif = new javax.swing.JPopupMenu();
        spcSystem = new javax.swing.JMenuItem();
        spcProduct = new javax.swing.JMenuItem();
        tb7 = new javax.swing.JToolBar();
        btn4 = new javax.swing.JButton();
        btn15 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        tb1 = new javax.swing.JToolBar();
        btn14 = new javax.swing.JButton();
        btn12 = new javax.swing.JButton();
        btn11 = new javax.swing.JButton();
        btn16 = new javax.swing.JButton();
        td5 = new javax.swing.JToolBar();
        btn51 = new javax.swing.JButton();
        btn52 = new javax.swing.JButton();
        btn53 = new javax.swing.JButton();
        tb2 = new javax.swing.JToolBar();
        btn24 = new javax.swing.JButton();
        btn25 = new javax.swing.JButton();
        btn26 = new javax.swing.JButton();
        btn27 = new javax.swing.JButton();
        btn23 = new javax.swing.JButton();
        btn21 = new javax.swing.JButton();
        tb8 = new javax.swing.JToolBar();
        btn17 = new javax.swing.JButton();
        btn3 = new javax.swing.JButton();
        btn22 = new javax.swing.JButton();
        tb6 = new javax.swing.JToolBar();
        btnT7 = new javax.swing.JToggleButton();
        btnT8 = new javax.swing.JToggleButton();
        btnT9 = new javax.swing.JToggleButton();
        tb4 = new javax.swing.JToolBar();
        btnTest = new javax.swing.JButton();
        btn42 = new javax.swing.JButton();
        mn0 = new javax.swing.JMenuBar();
        mn01 = new javax.swing.JMenu();
        mn12 = new javax.swing.JMenuItem();
        mn15 = new javax.swing.JMenuItem();
        mn11 = new javax.swing.JMenuItem();
        mn13 = new javax.swing.JSeparator();
        mn14 = new javax.swing.JMenuItem();
        mn02 = new javax.swing.JMenu();
        mn26 = new javax.swing.JMenuItem();
        mn21 = new javax.swing.JMenuItem();
        mn23 = new javax.swing.JMenuItem();
        mn24 = new javax.swing.JPopupMenu.Separator();
        mn22 = new javax.swing.JMenuItem();
        mn09 = new javax.swing.JMenu();
        mn94 = new javax.swing.JMenuItem();
        mn91 = new javax.swing.JMenuItem();
        mn93 = new javax.swing.JPopupMenu.Separator();
        mn92 = new javax.swing.JMenuItem();
        mn03 = new javax.swing.JMenu();
        mn31 = new javax.swing.JMenuItem();
        mn32 = new javax.swing.JMenuItem();
        mn34 = new javax.swing.JMenuItem();
        mn35 = new javax.swing.JMenuItem();
        mn36 = new javax.swing.JMenuItem();
        mn38 = new javax.swing.JPopupMenu.Separator();
        mn37 = new javax.swing.JMenuItem();
        mn10 = new javax.swing.JMenu();
        mn71 = new javax.swing.JMenuItem();
        mn72 = new javax.swing.JMenuItem();
        mn74 = new javax.swing.JPopupMenu.Separator();
        mn75 = new javax.swing.JMenuItem();
        mn08 = new javax.swing.JMenu();
        mn06 = new javax.swing.JMenu();
        mn63 = new javax.swing.JMenu();
        mn631 = new javax.swing.JCheckBoxMenuItem();
        mn632 = new javax.swing.JCheckBoxMenuItem();
        mn633 = new javax.swing.JCheckBoxMenuItem();
        mn62 = new javax.swing.JMenu();
        mn05 = new javax.swing.JMenu();
        mn51 = new javax.swing.JMenuItem();
        mn52 = new javax.swing.JMenuItem();
        mn54 = new javax.swing.JMenuItem();

        spcSystem.setFont(frames.UGui.getFont(0,1));
        spcSystem.setText("Спецификация ситемы");
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        spcSystem.setToolTipText(bundle.getString("Пересчитать")); // NOI18N
        spcSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spcSystem(evt);
            }
        });
        ppmSpecif.add(spcSystem);

        spcProduct.setFont(frames.UGui.getFont(0,1));
        spcProduct.setText("Спецификация продукции");
        spcProduct.setToolTipText(bundle.getString("Пересчитать")); // NOI18N
        spcProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spcProductActionPerformed(evt);
            }
        });
        ppmSpecif.add(spcProduct);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SA.OKNA   <АРМ Технолог>");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Tex.this.windowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                Tex.this.windowClosing(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                Tex.this.windowDeiconified(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                Tex.this.windowIconified(evt);
            }
        });
        getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 1));

        tb7.setRollover(true);
        tb7.setMaximumSize(new java.awt.Dimension(94, 28));
        tb7.setMinimumSize(new java.awt.Dimension(94, 28));
        tb7.setPreferredSize(new java.awt.Dimension(94, 28));

        btn4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c015.gif"))); // NOI18N
        btn4.setToolTipText(bundle.getString("Меню.Установки")); // NOI18N
        btn4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 255)));
        btn4.setFocusable(false);
        btn4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn4.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSetting(evt);
            }
        });
        tb7.add(btn4);

        btn15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c018.gif"))); // NOI18N
        btn15.setToolTipText(bundle.getString("Меню.Правила расч...")); // NOI18N
        btn15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 255)));
        btn15.setFocusable(false);
        btn15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn15.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnRulecalc(evt);
            }
        });
        tb7.add(btn15);

        btn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c060.gif"))); // NOI18N
        btn2.setToolTipText(bundle.getString("Меню.Ценовые коэф...")); // NOI18N
        btn2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 255)));
        btn2.setFocusable(false);
        btn2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnGroup1(evt);
            }
        });
        tb7.add(btn2);

        getContentPane().add(tb7);

        tb1.setRollover(true);
        tb1.setMaximumSize(new java.awt.Dimension(150, 28));
        tb1.setMinimumSize(new java.awt.Dimension(150, 28));
        tb1.setPreferredSize(new java.awt.Dimension(120, 28));

        btn14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c008.gif"))); // NOI18N
        btn14.setToolTipText(bundle.getString("Меню.Константы")); // NOI18N
        btn14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        btn14.setFocusable(false);
        btn14.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn14.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSyssize(evt);
            }
        });
        tb1.add(btn14);

        btn12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c010.gif"))); // NOI18N
        btn12.setToolTipText(bundle.getString("Меню.Текстуры")); // NOI18N
        btn12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        btn12.setFocusable(false);
        btn12.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnColor(evt);
            }
        });
        tb1.add(btn12);

        btn11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c022.gif"))); // NOI18N
        btn11.setToolTipText(bundle.getString("Меню.Параметры")); // NOI18N
        btn11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        btn11.setFocusable(false);
        btn11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn11.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnParametr(evt);
            }
        });
        tb1.add(btn11);

        btn16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c017.gif"))); // NOI18N
        btn16.setToolTipText(bundle.getString("Меню.Справочники")); // NOI18N
        btn16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
        btn16.setFocusable(false);
        btn16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn16.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnGroup2(evt);
            }
        });
        tb1.add(btn16);

        getContentPane().add(tb1);

        td5.setRollover(true);
        td5.setMaximumSize(new java.awt.Dimension(96, 28));
        td5.setMinimumSize(new java.awt.Dimension(96, 28));
        td5.setPreferredSize(new java.awt.Dimension(94, 28));

        btn51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c021.gif"))); // NOI18N
        btn51.setToolTipText(bundle.getString("Меню.Артикулы")); // NOI18N
        btn51.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));
        btn51.setFocusable(false);
        btn51.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn51.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn94(evt);
            }
        });
        td5.add(btn51);

        btn52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c012.gif"))); // NOI18N
        btn52.setToolTipText(bundle.getString("Меню.Модели")); // NOI18N
        btn52.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));
        btn52.setFocusable(false);
        btn52.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn52.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBoxTypical(evt);
            }
        });
        td5.add(btn52);

        btn53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c028.gif"))); // NOI18N
        btn53.setToolTipText(bundle.getString("Меню.Системы")); // NOI18N
        btn53.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));
        btn53.setFocusable(false);
        btn53.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn53.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn53.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn42(evt);
            }
        });
        td5.add(btn53);

        getContentPane().add(td5);

        tb2.setRollover(true);
        tb2.setMaximumSize(new java.awt.Dimension(200, 28));
        tb2.setMinimumSize(new java.awt.Dimension(178, 28));
        tb2.setPreferredSize(new java.awt.Dimension(172, 28));

        btn24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c024.gif"))); // NOI18N
        btn24.setToolTipText(bundle.getString("Меню.Соединения")); // NOI18N
        btn24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn24.setFocusable(false);
        btn24.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn24.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnJoining(evt);
            }
        });
        tb2.add(btn24);

        btn25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c025.gif"))); // NOI18N
        btn25.setToolTipText(bundle.getString("Меню.Вставки")); // NOI18N
        btn25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn25.setFocusable(false);
        btn25.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn25.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnElement(evt);
            }
        });
        tb2.add(btn25);

        btn26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c026.gif"))); // NOI18N
        btn26.setToolTipText(bundle.getString("Меню.Заполнения")); // NOI18N
        btn26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn26.setFocusable(false);
        btn26.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn26.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnGlass(evt);
            }
        });
        tb2.add(btn26);

        btn27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c027.gif"))); // NOI18N
        btn27.setToolTipText(bundle.getString("Меню.Фурнитура")); // NOI18N
        btn27.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn27.setFocusable(false);
        btn27.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn27.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnFurnityra(evt);
            }
        });
        tb2.add(btn27);

        btn23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c023.gif"))); // NOI18N
        btn23.setToolTipText(bundle.getString("Меню.Комплекты")); // NOI18N
        btn23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn23.setFocusable(false);
        btn23.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn23.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnKits(evt);
            }
        });
        tb2.add(btn23);

        btn21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c016.gif"))); // NOI18N
        btn21.setToolTipText(bundle.getString("Меню.Спецификация")); // NOI18N
        btn21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 0)));
        btn21.setFocusable(false);
        btn21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn21.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSpecif(evt);
            }
        });
        tb2.add(btn21);

        getContentPane().add(tb2);

        tb8.setRollover(true);
        tb8.setPreferredSize(new java.awt.Dimension(94, 28));

        btn17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c080.gif"))); // NOI18N
        btn17.setToolTipText(bundle.getString("Меню.Контрагенты")); // NOI18N
        btn17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 255)));
        btn17.setFocusable(false);
        btn17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn17.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnPartn(evt);
            }
        });
        tb8.add(btn17);

        btn3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c081.gif"))); // NOI18N
        btn3.setToolTipText(bundle.getString("Меню.Заказ")); // NOI18N
        btn3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 255)));
        btn3.setFocusable(false);
        btn3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn3.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnOrder(evt);
            }
        });
        tb8.add(btn3);

        btn22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c016.gif"))); // NOI18N
        btn22.setToolTipText(bundle.getString("Меню.Спецификация")); // NOI18N
        btn22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 255)));
        btn22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn22.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn2Specif(evt);
            }
        });
        tb8.add(btn22);

        getContentPane().add(tb8);

        tb6.setRollover(true);
        tb6.setMaximumSize(new java.awt.Dimension(96, 28));
        tb6.setMinimumSize(new java.awt.Dimension(96, 28));
        tb6.setPreferredSize(new java.awt.Dimension(94, 28));

        buttonBaseGroup1.add(btnT7);
        btnT7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c057.gif"))); // NOI18N
        btnT7.setToolTipText(bundle.getString("Меню.Соединение с БД1")); // NOI18N
        btnT7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        btnT7.setFocusable(false);
        btnT7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnT7.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c061.gif"))); // NOI18N
        btnT7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBase(evt);
            }
        });
        tb6.add(btnT7);

        buttonBaseGroup1.add(btnT8);
        btnT8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c058.gif"))); // NOI18N
        btnT8.setToolTipText(bundle.getString("Меню.Соединение с БД2")); // NOI18N
        btnT8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        btnT8.setFocusable(false);
        btnT8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnT8.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c062.gif"))); // NOI18N
        btnT8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBase(evt);
            }
        });
        tb6.add(btnT8);

        buttonBaseGroup1.add(btnT9);
        btnT9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c059.gif"))); // NOI18N
        btnT9.setToolTipText(bundle.getString("Меню.Соединение с БД3")); // NOI18N
        btnT9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 204)));
        btnT9.setFocusable(false);
        btnT9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnT9.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c063.gif"))); // NOI18N
        btnT9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBase(evt);
            }
        });
        tb6.add(btnT9);

        getContentPane().add(tb6);

        tb4.setRollover(true);
        tb4.setMaximumSize(new java.awt.Dimension(70, 28));
        tb4.setMinimumSize(new java.awt.Dimension(70, 28));
        tb4.setPreferredSize(new java.awt.Dimension(68, 28));

        btnTest.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        btnTest.setText("Test");
        btnTest.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        btnTest.setMaximumSize(new java.awt.Dimension(26, 28));
        btnTest.setMinimumSize(new java.awt.Dimension(26, 28));
        btnTest.setPreferredSize(new java.awt.Dimension(26, 28));
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTest(evt);
            }
        });
        tb4.add(btnTest);

        btn42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        btn42.setToolTipText(bundle.getString("Меню.Выход")); // NOI18N
        btn42.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        btn42.setFocusable(false);
        btn42.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn42.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnExit(evt);
            }
        });
        tb4.add(btn42);

        getContentPane().add(tb4);

        mn0.setPreferredSize(new java.awt.Dimension(800, 25));

        mn01.setText(bundle.getString("Меню.Настройки")); // NOI18N
        mn01.setFont(frames.UGui.getFont(1,1));

        mn12.setFont(frames.UGui.getFont(0,1));
        mn12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        mn12.setText(bundle.getString("Меню.Установки")); // NOI18N
        mn12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSetting(evt);
            }
        });
        mn01.add(mn12);

        mn15.setFont(frames.UGui.getFont(0,1));
        mn15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        mn15.setText(bundle.getString("Меню.Правила расч...")); // NOI18N
        mn15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnRulecalc(evt);
            }
        });
        mn01.add(mn15);

        mn11.setFont(frames.UGui.getFont(0,1));
        mn11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        mn11.setText(bundle.getString("Меню.Ценовые коэф...")); // NOI18N
        mn11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnGroup1(evt);
            }
        });
        mn01.add(mn11);
        mn01.add(mn13);

        mn14.setFont(frames.UGui.getFont(0,1));
        mn14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b009.gif"))); // NOI18N
        mn14.setText(bundle.getString("Меню.Выход")); // NOI18N
        mn14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnExit(evt);
            }
        });
        mn01.add(mn14);

        mn0.add(mn01);

        mn02.setText(bundle.getString("Меню.Справочники")); // NOI18N
        mn02.setFont(frames.UGui.getFont(1,1));

        mn26.setFont(frames.UGui.getFont(0,1));
        mn26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        mn26.setText(bundle.getString("Меню.Константы")); // NOI18N
        mn26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSyssize(evt);
            }
        });
        mn02.add(mn26);

        mn21.setFont(frames.UGui.getFont(0,1));
        mn21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        mn21.setText(bundle.getString("Меню.Текстуры")); // NOI18N
        mn21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnColor(evt);
            }
        });
        mn02.add(mn21);

        mn23.setFont(frames.UGui.getFont(0,1));
        mn23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        mn23.setText(bundle.getString("Меню.Параметры")); // NOI18N
        mn23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnParametr(evt);
            }
        });
        mn02.add(mn23);
        mn02.add(mn24);

        mn22.setFont(frames.UGui.getFont(0,1));
        mn22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        mn22.setText(bundle.getString("Меню.Справочники")); // NOI18N
        mn22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnGroup2(evt);
            }
        });
        mn02.add(mn22);

        mn0.add(mn02);

        mn09.setText(bundle.getString("Меню.Модели")); // NOI18N
        mn09.setFont(frames.UGui.getFont(1,1));

        mn94.setFont(frames.UGui.getFont(0,1));
        mn94.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b054.gif"))); // NOI18N
        mn94.setText(bundle.getString("Меню.Артикулы")); // NOI18N
        mn94.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn94(evt);
            }
        });
        mn09.add(mn94);

        mn91.setFont(frames.UGui.getFont(0,1));
        mn91.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b054.gif"))); // NOI18N
        mn91.setText(bundle.getString("Меню.Модели")); // NOI18N
        mn91.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBoxTypical(evt);
            }
        });
        mn09.add(mn91);
        mn09.add(mn93);

        mn92.setFont(frames.UGui.getFont(0,1));
        mn92.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b054.gif"))); // NOI18N
        mn92.setText(bundle.getString("Меню.Системы")); // NOI18N
        mn92.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn42(evt);
            }
        });
        mn09.add(mn92);

        mn0.add(mn09);

        mn03.setText(bundle.getString("Меню.Составы")); // NOI18N
        mn03.setFont(frames.UGui.getFont(1,1));

        mn31.setFont(frames.UGui.getFont(0,1));
        mn31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn31.setText(bundle.getString("Меню.Комплекты")); // NOI18N
        mn31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnKits(evt);
            }
        });
        mn03.add(mn31);

        mn32.setFont(frames.UGui.getFont(0,1));
        mn32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn32.setText(bundle.getString("Меню.Соединения")); // NOI18N
        mn32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnJoining(evt);
            }
        });
        mn03.add(mn32);

        mn34.setFont(frames.UGui.getFont(0,1));
        mn34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn34.setText(bundle.getString("Меню.Вставки")); // NOI18N
        mn34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnElement(evt);
            }
        });
        mn03.add(mn34);

        mn35.setFont(frames.UGui.getFont(0,1));
        mn35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn35.setText(bundle.getString("Меню.Заполнения")); // NOI18N
        mn35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnGlass(evt);
            }
        });
        mn03.add(mn35);

        mn36.setFont(frames.UGui.getFont(0,1));
        mn36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn36.setText(bundle.getString("Меню.Фурнитура")); // NOI18N
        mn36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnFurnityra(evt);
            }
        });
        mn03.add(mn36);
        mn03.add(mn38);

        mn37.setFont(frames.UGui.getFont(0,1));
        mn37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn37.setText(bundle.getString("Меню.Спецификация")); // NOI18N
        mn37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSpecif(evt);
            }
        });
        mn03.add(mn37);

        mn0.add(mn03);

        mn10.setText(bundle.getString("Меню.Заказ")); // NOI18N
        mn10.setFont(frames.UGui.getFont(1,1));

        mn71.setFont(frames.UGui.getFont(0,1));
        mn71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        mn71.setText("Конт-ты");
        mn71.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnPartn(evt);
            }
        });
        mn10.add(mn71);

        mn72.setFont(frames.UGui.getFont(0,1));
        mn72.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        mn72.setText(bundle.getString("Меню.Заказ")); // NOI18N
        mn72.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnOrder(evt);
            }
        });
        mn10.add(mn72);
        mn10.add(mn74);

        mn75.setFont(frames.UGui.getFont(0,1));
        mn75.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        mn75.setText(bundle.getString("Меню.Спецификация")); // NOI18N
        mn75.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn2Specif(evt);
            }
        });
        mn10.add(mn75);

        mn0.add(mn10);

        mn08.setText(bundle.getString("Меню.Отчёты")); // NOI18N
        mn08.setFont(frames.UGui.getFont(1,1));
        mn0.add(mn08);

        mn06.setText(bundle.getString("Меню.Сервис")); // NOI18N
        mn06.setFont(frames.UGui.getFont(1,1));

        mn63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b028.gif"))); // NOI18N
        mn63.setText(bundle.getString("Меню.Установка соединения")); // NOI18N
        mn63.setFont(frames.UGui.getFont(0,1));

        buttonMenuGroup.add(mn631);
        mn631.setFont(frames.UGui.getFont(1,1));
        mn631.setSelected(true);
        mn631.setText(bundle.getString("Меню.База 1")); // NOI18N
        mn631.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBase(evt);
            }
        });
        mn63.add(mn631);

        buttonMenuGroup.add(mn632);
        mn632.setFont(frames.UGui.getFont(1,1));
        mn632.setText(bundle.getString("Меню.База 2")); // NOI18N
        mn632.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBase(evt);
            }
        });
        mn63.add(mn632);

        buttonMenuGroup.add(mn633);
        mn633.setFont(frames.UGui.getFont(1,1));
        mn633.setText(bundle.getString("Меню.База 3")); // NOI18N
        mn633.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBase(evt);
            }
        });
        mn63.add(mn633);

        mn06.add(mn63);

        mn62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b061.gif"))); // NOI18N
        mn62.setText(bundle.getString("Меню.Вид интерфейса")); // NOI18N
        mn62.setFont(frames.UGui.getFont(0,1));
        mn06.add(mn62);

        mn0.add(mn06);

        mn05.setText(bundle.getString("Меню.Справка")); // NOI18N
        mn05.setFont(frames.UGui.getFont(1,1));

        mn51.setFont(frames.UGui.getFont(0,1));
        mn51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        mn51.setText(bundle.getString("Меню.Справка")); // NOI18N
        mn51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn51ActionPerformed(evt);
            }
        });
        mn05.add(mn51);

        mn52.setFont(frames.UGui.getFont(0,1));
        mn52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        mn52.setText(bundle.getString("Меню.Часто задаваемые вопросы")); // NOI18N
        mn52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mHowTo(evt);
            }
        });
        mn05.add(mn52);

        mn54.setFont(frames.UGui.getFont(0,1));
        mn54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        mn54.setText(bundle.getString("Меню.О программе")); // NOI18N
        mn54.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnAboutBox(evt);
            }
        });
        mn05.add(mn54);

        mn0.add(mn05);

        setJMenuBar(mn0);

        getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnColor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnColor

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Colors.createFrame(Tex.this);
            }
        });
}//GEN-LAST:event_mnColor

    private void mn42(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mn42

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Systree.createFrame(Tex.this);
            }
        });
}//GEN-LAST:event_mn42

    private void mnExit(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnExit
        List.of(App.values()).stream().filter(el -> el.frame != null).forEach(el -> el.frame.dispose());
}//GEN-LAST:event_mnExit

private void mnAboutBox(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnAboutBox

    App.AboutBox.createFrame(Tex.this);
}//GEN-LAST:event_mnAboutBox

private void mn51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mn51ActionPerformed
}//GEN-LAST:event_mn51ActionPerformed

private void mnSpecif(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnSpecif
    ProgressBar.create(Tex.this, new ListenerFrame() {
        public void actionRequest(Object obj) {
            App.Specification.createFrame(Tex.this, false);
        }
    });
}//GEN-LAST:event_mnSpecif

private void mn94(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mn94

    ProgressBar.create(Tex.this, new ListenerFrame() {
        public void actionRequest(Object obj) {
            App.Artikles.createFrame(Tex.this);
        }
    });
}//GEN-LAST:event_mn94

    private void mHowTo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mHowTo

    }//GEN-LAST:event_mHowTo

    private void mnElement(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnElement

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Element.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnElement

    private void mnFurnityra(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnFurnityra

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Furniture.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnFurnityra

    private void mnParametr(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnParametr

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Param.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnParametr

    private void mnJoining(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnJoining

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Joining.createFrame(Tex.this); //все соединения
            }
        });
    }//GEN-LAST:event_mnJoining

    private void mnGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnGlass

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Filling.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnGlass

    private void mnKits(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnKits

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Kits.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnKits

    private void mnSyssize(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnSyssize

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Syssize.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnSyssize

    private void mnBoxTypical(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnBoxTypical

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Models.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnBoxTypical

    private void windowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosing
        mnExit(null);
    }//GEN-LAST:event_windowClosing

    private void mnRulecalc(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnRulecalc
        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.RuleCalc.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnRulecalc

    private void windowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowIconified
        List.of(App.values()).stream().filter(el -> el.frame != null).forEach(el -> el.frame.setState(Frame.ICONIFIED));
    }//GEN-LAST:event_windowIconified

    private void windowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowDeiconified
        List.of(App.values()).stream().filter(el -> el.frame != null).forEach(el -> el.frame.setState(Frame.NORMAL));
    }//GEN-LAST:event_windowDeiconified

    private void mnBase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnBase
        String new_num_base = (mn631.isSelected()) ? "1" : (mn632.isSelected()) ? "2" : "3";
        prepareConnectBaseNumb(new_num_base);
    }//GEN-LAST:event_mnBase

    private void btnBase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBase
        String new_num_base = (btnT7.isSelected()) ? "1" : (btnT8.isSelected()) ? "2" : "3";
        prepareConnectBaseNumb(new_num_base);
    }//GEN-LAST:event_btnBase

    private void mnGroup2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnGroup2
        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Groups.createFrame(Tex.this, 1);
            }
        });
    }//GEN-LAST:event_mnGroup2

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed

    }//GEN-LAST:event_windowClosed

    private void mnGroup1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnGroup1
        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Groups.createFrame(Tex.this, 0);
            }
        });
    }//GEN-LAST:event_mnGroup1

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest
        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.PSFrame.createFrame(Tex.this);
            }
        });
//        this.setResizable(true);
//        this.setPreferredSize(new java.awt.Dimension(800, 80));
//        this.setMinimumSize(new java.awt.Dimension(800, 80));

        // this.setPreferredSize(new java.awt.Dimension(800, 80));
    }//GEN-LAST:event_btnTest

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        /*List.of(App.values()).forEach(act -> {
            if (act.frame != null && act != App.Top && act.frame.isVisible() == false) {
                act.frame.setVisible(true);
            }
        });*/
    }//GEN-LAST:event_formWindowActivated

    private void mnPartn(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnPartn

        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Partner.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnPartn

    private void mnOrder(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnOrder
        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Order.createFrame(Tex.this, false);
            }
        });
    }//GEN-LAST:event_mnOrder

    private void mnSetting(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnSetting
        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Setting.createFrame(Tex.this);
            }
        });
    }//GEN-LAST:event_mnSetting

    private void spcSystem(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spcSystem

    }//GEN-LAST:event_spcSystem

    private void spcProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spcProductActionPerformed

    }//GEN-LAST:event_spcProductActionPerformed

    private void mn2Specif(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mn2Specif
        ProgressBar.create(Tex.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Specification.createFrame(Tex.this, true);
            }
        });
    }//GEN-LAST:event_mn2Specif

// <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn11;
    private javax.swing.JButton btn12;
    private javax.swing.JButton btn14;
    private javax.swing.JButton btn15;
    private javax.swing.JButton btn16;
    private javax.swing.JButton btn17;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn21;
    private javax.swing.JButton btn22;
    private javax.swing.JButton btn23;
    private javax.swing.JButton btn24;
    private javax.swing.JButton btn25;
    private javax.swing.JButton btn26;
    private javax.swing.JButton btn27;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn42;
    private javax.swing.JButton btn51;
    private javax.swing.JButton btn52;
    private javax.swing.JButton btn53;
    private javax.swing.JToggleButton btnT7;
    private javax.swing.JToggleButton btnT8;
    private javax.swing.JToggleButton btnT9;
    private javax.swing.JButton btnTest;
    private javax.swing.ButtonGroup buttonBaseGroup1;
    private javax.swing.ButtonGroup buttonLookAndFeelGroup;
    private javax.swing.ButtonGroup buttonMenuGroup;
    private javax.swing.JMenuBar mn0;
    private javax.swing.JMenu mn01;
    private javax.swing.JMenu mn02;
    private javax.swing.JMenu mn03;
    private javax.swing.JMenu mn05;
    private javax.swing.JMenu mn06;
    private javax.swing.JMenu mn08;
    private javax.swing.JMenu mn09;
    private javax.swing.JMenu mn10;
    private javax.swing.JMenuItem mn11;
    private javax.swing.JMenuItem mn12;
    private javax.swing.JSeparator mn13;
    private javax.swing.JMenuItem mn14;
    private javax.swing.JMenuItem mn15;
    private javax.swing.JMenuItem mn21;
    private javax.swing.JMenuItem mn22;
    private javax.swing.JMenuItem mn23;
    private javax.swing.JPopupMenu.Separator mn24;
    private javax.swing.JMenuItem mn26;
    private javax.swing.JMenuItem mn31;
    private javax.swing.JMenuItem mn32;
    private javax.swing.JMenuItem mn34;
    private javax.swing.JMenuItem mn35;
    private javax.swing.JMenuItem mn36;
    private javax.swing.JMenuItem mn37;
    private javax.swing.JPopupMenu.Separator mn38;
    private javax.swing.JMenuItem mn51;
    private javax.swing.JMenuItem mn52;
    private javax.swing.JMenuItem mn54;
    private javax.swing.JMenu mn62;
    private javax.swing.JMenu mn63;
    private javax.swing.JCheckBoxMenuItem mn631;
    private javax.swing.JCheckBoxMenuItem mn632;
    private javax.swing.JCheckBoxMenuItem mn633;
    private javax.swing.JMenuItem mn71;
    private javax.swing.JMenuItem mn72;
    private javax.swing.JPopupMenu.Separator mn74;
    private javax.swing.JMenuItem mn75;
    private javax.swing.JMenuItem mn91;
    private javax.swing.JMenuItem mn92;
    private javax.swing.JPopupMenu.Separator mn93;
    private javax.swing.JMenuItem mn94;
    private javax.swing.JPopupMenu ppmSpecif;
    private javax.swing.JMenuItem spcProduct;
    private javax.swing.JMenuItem spcSystem;
    private javax.swing.JToolBar tb1;
    private javax.swing.JToolBar tb2;
    private javax.swing.JToolBar tb4;
    private javax.swing.JToolBar tb6;
    private javax.swing.JToolBar tb7;
    private javax.swing.JToolBar tb8;
    private javax.swing.JToolBar td5;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    private void initElements() {

        btnTest.setVisible(eProp.dev);
        btnT9.setEnabled(!eProp.demo);
        
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            JCheckBoxMenuItem mnIt = new javax.swing.JCheckBoxMenuItem();
            buttonLookAndFeelGroup.add(mnIt);
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

        MainMenu.init(mn08, this, common.eProp.locale);

        if ("Nimbus".equals(lookAndFeel.getName())) {
            tb6.setPreferredSize(new Dimension(97, 28));
        }
        if (eProp.base_num.getProp().equals("1")) {
            mn631.setSelected(true);
            btnT7.setSelected(true);
        } else if (eProp.base_num.getProp().equals("2")) {
            mn632.setSelected(true);
            btnT8.setSelected(true);
        } else if (eProp.base_num.getProp().equals("3")) {
            mn633.setSelected(true);
            btnT9.setSelected(true);
        }
        List.of(btnT7, btnT8, btnT9).forEach(btn
                -> btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        JToggleButton b = (btn == btnT7) ? btnT7 : (btn == btnT8) ? btnT8 : btnT9;
                        eProp p = (btn == btnT7) ? eProp.base1 : (btn == btnT8) ? eProp.base2 : eProp.base3;
                        eProp s = (btn == btnT7) ? eProp.server1 : (btn == btnT8) ? eProp.server2 : eProp.server3;
                        b.setToolTipText("Connect:  \"" + s.getProp() + ":" + p.getProp() + "\"");
                    }
                }));
    }
}
