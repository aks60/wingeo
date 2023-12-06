package startup;

import frames.PathToDb;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import frames.UGui;
import frames.swing.ProgressBar;
import frames.swing.FrameToFile;
import common.eProp;
import dataset.Record;
import builder.Wincalc;
import domain.eSysprod;
import java.awt.Frame;
import java.util.HashMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import common.listener.ListenerFrame;
import common.eProfile;
import dataset.Conn;
import dataset.Field;
import dataset.Query;
import domain.eColor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * <p>
 * Технолог</p>
 */
public class Tex extends javax.swing.JFrame {

    private Wincalc winc = new Wincalc();
    private javax.swing.Timer timer = null;
    private ListenerFrame listenerMenu;
    private HashMap<String, JCheckBoxMenuItem> hmLookAndFill = new HashMap();

    public Tex() {

        initComponents();
        initElements();
    }

    private void winc_build() {
        int sysprodID = Integer.valueOf(eProp.sysprodID.read());
        Record sysprodRec = eSysprod.find(sysprodID);
        if (sysprodRec != null) {
            String script = sysprodRec.getStr(eSysprod.script);
            if (script != null && script.isEmpty() == false) {
                JsonElement script2 = new Gson().fromJson(script, JsonElement.class);
                script2.getAsJsonObject().addProperty("nuni", sysprodRec.getInt(eSysprod.systree_id)); //запишем nuni в script
                winc.parsing(script2.toString()); //калькуляция изделия                
            }
        }
    }

    private void mnLookAndFeel(java.awt.event.ActionEvent evt) {
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            if (((JCheckBoxMenuItem) evt.getSource()).getText().equals(laf.getName()) == true) {
                eProp.lookandfeel.write(laf.getName());
                eProp.save();
            }
        }
    }

    private void connectBaseNumb(String num_base) {
        List.of(App.values()).stream().filter(el -> el.frame != null && el != App.Top).forEach(el -> el.frame.dispose());
        Query.listOpenTable.forEach(q -> q.clear());
        PathToDb pathToDb = new PathToDb(this, num_base);
        FrameToFile.setFrameSize(pathToDb);
        pathToDb.setVisible(true);

        if (eProp.base_num.read().equals("1")) {
            btnT7.setSelected(true);
            mn631.setSelected(true);

        } else if (eProp.base_num.read().equals("2")) {
            btnT8.setSelected(true);
            mn632.setSelected(true);

        } else if (eProp.base_num.read().equals("3")) {
            btnT9.setSelected(true);
            mn633.setSelected(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonLookAndFeelGroup = new javax.swing.ButtonGroup();
        buttonBaseGroup1 = new javax.swing.ButtonGroup();
        buttonBaseGroup2 = new javax.swing.ButtonGroup();
        buttonMenuGroup = new javax.swing.ButtonGroup();
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
        btn52 = new javax.swing.JButton();
        btn51 = new javax.swing.JButton();
        btn53 = new javax.swing.JButton();
        tb2 = new javax.swing.JToolBar();
        btn23 = new javax.swing.JButton();
        btn24 = new javax.swing.JButton();
        btn25 = new javax.swing.JButton();
        btn26 = new javax.swing.JButton();
        btn27 = new javax.swing.JButton();
        btn21 = new javax.swing.JButton();
        tb8 = new javax.swing.JToolBar();
        btn17 = new javax.swing.JButton();
        btn3 = new javax.swing.JButton();
        btn5 = new javax.swing.JButton();
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
        mn91 = new javax.swing.JMenuItem();
        mn94 = new javax.swing.JMenuItem();
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
        mn07 = new javax.swing.JMenu();
        mn71 = new javax.swing.JMenuItem();
        mn73 = new javax.swing.JMenuItem();
        mn74 = new javax.swing.JPopupMenu.Separator();
        mn72 = new javax.swing.JMenuItem();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SA.OKNA   <АРМ Технолог>");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setPreferredSize(new java.awt.Dimension(800, 80));
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

        btn52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c012.gif"))); // NOI18N
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

        btn51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c021.gif"))); // NOI18N
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

        btn53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c028.gif"))); // NOI18N
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
        tb2.setMaximumSize(new java.awt.Dimension(172, 28));
        tb2.setMinimumSize(new java.awt.Dimension(172, 28));
        tb2.setPreferredSize(new java.awt.Dimension(172, 28));

        btn23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c023.gif"))); // NOI18N
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

        btn24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c024.gif"))); // NOI18N
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

        btn21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c016.gif"))); // NOI18N
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

        btn5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c083.gif"))); // NOI18N
        btn5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 255)));
        btn5.setFocusable(false);
        btn5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn5.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnDiler(evt);
            }
        });
        tb8.add(btn5);

        getContentPane().add(tb8);

        tb6.setRollover(true);
        tb6.setMaximumSize(new java.awt.Dimension(96, 28));
        tb6.setMinimumSize(new java.awt.Dimension(96, 28));
        tb6.setPreferredSize(new java.awt.Dimension(94, 28));

        buttonBaseGroup1.add(btnT7);
        btnT7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c057.gif"))); // NOI18N
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

        mn01.setActionCommand("*Учреждение");
        mn01.setFont(frames.UGui.getFont(1,1));
        mn01.setLabel("  Настройки  ");

        mn12.setFont(frames.UGui.getFont(0,1));
        mn12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        mn12.setText("Установки");
        mn12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSetting(evt);
            }
        });
        mn01.add(mn12);

        mn15.setFont(frames.UGui.getFont(0,1));
        mn15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        mn15.setText("Правила расч...");
        mn15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnRulecalc(evt);
            }
        });
        mn01.add(mn15);

        mn11.setFont(frames.UGui.getFont(0,1));
        mn11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b031.gif"))); // NOI18N
        mn11.setText("Ценовые коэф...");
        mn11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnGroup1(evt);
            }
        });
        mn01.add(mn11);
        mn01.add(mn13);

        mn14.setFont(frames.UGui.getFont(0,1));
        mn14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b009.gif"))); // NOI18N
        mn14.setText("Выход");
        mn14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnExit(evt);
            }
        });
        mn01.add(mn14);

        mn0.add(mn01);

        mn02.setFont(frames.UGui.getFont(1,1));
        mn02.setLabel("  Справочники  ");

        mn26.setFont(frames.UGui.getFont(0,1));
        mn26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        mn26.setText("Константы");
        mn26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSyssize(evt);
            }
        });
        mn02.add(mn26);

        mn21.setFont(frames.UGui.getFont(0,1));
        mn21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        mn21.setText("Текстуры");
        mn21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnColor(evt);
            }
        });
        mn02.add(mn21);

        mn23.setFont(frames.UGui.getFont(0,1));
        mn23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        mn23.setText("Параметры");
        mn23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnParametr(evt);
            }
        });
        mn02.add(mn23);
        mn02.add(mn24);

        mn22.setFont(frames.UGui.getFont(0,1));
        mn22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        mn22.setText("Справочники");
        mn22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnGroup2(evt);
            }
        });
        mn02.add(mn22);

        mn0.add(mn02);

        mn09.setFont(frames.UGui.getFont(1,1));
        mn09.setLabel("  Системы  ");

        mn91.setFont(frames.UGui.getFont(0,1));
        mn91.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b054.gif"))); // NOI18N
        mn91.setText("Модели");
        mn91.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBoxTypical(evt);
            }
        });
        mn09.add(mn91);

        mn94.setFont(frames.UGui.getFont(0,1));
        mn94.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b054.gif"))); // NOI18N
        mn94.setText("Артикулы");
        mn94.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn94(evt);
            }
        });
        mn09.add(mn94);
        mn09.add(mn93);

        mn92.setFont(frames.UGui.getFont(0,1));
        mn92.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b054.gif"))); // NOI18N
        mn92.setText("Системы");
        mn92.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn42(evt);
            }
        });
        mn09.add(mn92);

        mn0.add(mn09);

        mn03.setText("  Конструктив  ");
        mn03.setFont(frames.UGui.getFont(1,1));

        mn31.setFont(frames.UGui.getFont(0,1));
        mn31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn31.setText("Комплекты");
        mn31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnKits(evt);
            }
        });
        mn03.add(mn31);

        mn32.setFont(frames.UGui.getFont(0,1));
        mn32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn32.setText("Соединения");
        mn32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnJoining(evt);
            }
        });
        mn03.add(mn32);

        mn34.setFont(frames.UGui.getFont(0,1));
        mn34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn34.setText("Составы");
        mn34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnElement(evt);
            }
        });
        mn03.add(mn34);

        mn35.setFont(frames.UGui.getFont(0,1));
        mn35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn35.setText("Заполнения");
        mn35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnGlass(evt);
            }
        });
        mn03.add(mn35);

        mn36.setFont(frames.UGui.getFont(0,1));
        mn36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn36.setText("Фурнитура");
        mn36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnFurnityra(evt);
            }
        });
        mn03.add(mn36);
        mn03.add(mn38);

        mn37.setFont(frames.UGui.getFont(0,1));
        mn37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b056.gif"))); // NOI18N
        mn37.setText("Спецификация");
        mn37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnSpecif(evt);
            }
        });
        mn03.add(mn37);

        mn0.add(mn03);

        mn07.setText("  Заказы ");
        mn07.setActionCommand("  Заказы  ");
        mn07.setFont(frames.UGui.getFont(1,1));
        mn07.setPreferredSize(new java.awt.Dimension(67, 19));

        mn71.setFont(frames.UGui.getFont(0,1));
        mn71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        mn71.setText("Контрагенты");
        mn71.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnPartn(evt);
            }
        });
        mn07.add(mn71);

        mn73.setFont(frames.UGui.getFont(0,1));
        mn73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        mn73.setText("Дилер");
        mn73.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnDiler(evt);
            }
        });
        mn07.add(mn73);
        mn07.add(mn74);

        mn72.setFont(frames.UGui.getFont(0,1));
        mn72.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        mn72.setText("Заказы");
        mn72.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnOrder(evt);
            }
        });
        mn07.add(mn72);

        mn0.add(mn07);

        mn06.setFont(frames.UGui.getFont(1,1));
        mn06.setLabel("  Сервис  ");

        mn63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b028.gif"))); // NOI18N
        mn63.setText("Установка соединения");
        mn63.setFont(frames.UGui.getFont(0,1));

        buttonMenuGroup.add(mn631);
        mn631.setFont(frames.UGui.getFont(1,1));
        mn631.setSelected(true);
        mn631.setText("База 1");
        mn631.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBase(evt);
            }
        });
        mn63.add(mn631);

        buttonMenuGroup.add(mn632);
        mn632.setFont(frames.UGui.getFont(1,1));
        mn632.setText("База 2");
        mn632.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBase(evt);
            }
        });
        mn63.add(mn632);

        buttonMenuGroup.add(mn633);
        mn633.setFont(frames.UGui.getFont(1,1));
        mn633.setText("База 3");
        mn633.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnBase(evt);
            }
        });
        mn63.add(mn633);

        mn06.add(mn63);

        mn62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b061.gif"))); // NOI18N
        mn62.setText("Вид интерфейса");
        mn62.setFont(frames.UGui.getFont(0,1));
        mn06.add(mn62);

        mn0.add(mn06);

        mn05.setFont(frames.UGui.getFont(1,1));
        mn05.setLabel("  Справка  ");

        mn51.setFont(frames.UGui.getFont(0,1));
        mn51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        mn51.setText("Справка");
        mn51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mn51ActionPerformed(evt);
            }
        });
        mn05.add(mn51);

        mn52.setFont(frames.UGui.getFont(0,1));
        mn52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        mn52.setText("Часто задаваемые вопросы");
        mn52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mHowTo(evt);
            }
        });
        mn05.add(mn52);

        mn54.setFont(frames.UGui.getFont(0,1));
        mn54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        mn54.setText("О программе");
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
            App.Specification.createFrame(Tex.this);
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
        String num_base = (mn631.isSelected()) ? "1" : (mn632.isSelected()) ? "2" : "3";
        connectBaseNumb(num_base);
    }//GEN-LAST:event_mnBase

    private void btnBase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBase
        String num_base = (btnT7.isSelected()) ? "1" : (btnT8.isSelected()) ? "2" : "3";
        connectBaseNumb(num_base);
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
                App.PSCompare.createFrame(Tex.this);
            }
        });
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
                App.Order.createFrame(Tex.this);
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

    private void mnDiler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnDiler
        try {
            Desktop desktop = Desktop.getDesktop();
            URI url = new URI(eProp.url_src.read());
            desktop.browse(url);
        } catch (URISyntaxException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }//GEN-LAST:event_mnDiler

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
    private javax.swing.JButton btn23;
    private javax.swing.JButton btn24;
    private javax.swing.JButton btn25;
    private javax.swing.JButton btn26;
    private javax.swing.JButton btn27;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btn42;
    private javax.swing.JButton btn5;
    private javax.swing.JButton btn51;
    private javax.swing.JButton btn52;
    private javax.swing.JButton btn53;
    private javax.swing.JToggleButton btnT7;
    private javax.swing.JToggleButton btnT8;
    private javax.swing.JToggleButton btnT9;
    private javax.swing.JButton btnTest;
    private javax.swing.ButtonGroup buttonBaseGroup1;
    private javax.swing.ButtonGroup buttonBaseGroup2;
    private javax.swing.ButtonGroup buttonLookAndFeelGroup;
    private javax.swing.ButtonGroup buttonMenuGroup;
    private javax.swing.JMenuBar mn0;
    private javax.swing.JMenu mn01;
    private javax.swing.JMenu mn02;
    private javax.swing.JMenu mn03;
    private javax.swing.JMenu mn05;
    private javax.swing.JMenu mn06;
    private javax.swing.JMenu mn07;
    private javax.swing.JMenu mn09;
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
    private javax.swing.JMenuItem mn73;
    private javax.swing.JPopupMenu.Separator mn74;
    private javax.swing.JMenuItem mn91;
    private javax.swing.JMenuItem mn92;
    private javax.swing.JPopupMenu.Separator mn93;
    private javax.swing.JMenuItem mn94;
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
        setTitle(eProfile.profile.title + UGui.designTitle());
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
        if ("Nimbus".equals(lookAndFeel.getName())) {
            tb6.setPreferredSize(new Dimension(97, 28));
        }
        if (eProp.base_num.read().equals("1")) {
            mn631.setSelected(true);
            btnT7.setSelected(true);
        } else if (eProp.base_num.read().equals("2")) {
            mn632.setSelected(true);
            btnT8.setSelected(true);
        } else if (eProp.base_num.read().equals("3")) {
            mn633.setSelected(true);
            btnT9.setSelected(true);
        }
    }
}
