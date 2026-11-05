package frames;

import dataset.Query;
import dataset.Record;
import domain.eSysmodel;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import builder.Wincalc;
import builder.making.TJoining;
import builder.model.Com5t;
import builder.script.GsonElem;
import builder.script.GsonRoot;
import builder.script.GsonScript;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import common.eProp;
import common.listener.ListenerAction;
import frames.swing.comp.Canvas;
import common.listener.ListenerRecord;
import common.listener.ListenerFrame;
import dataset.Connect;
import frames.swing.comp.DefTableModel;
import frames.swing.comp.Scene;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import common.listener.ListenerReload;
import frames.swing.comp.DefMutableTreeNode;
import static java.util.stream.Collectors.toList;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import org.locationtech.jts.geom.Envelope;
import startup.App;

//TODO Не работает кнопка отменить
public final class Models extends javax.swing.JFrame implements ListenerFrame<Object, Object>, ListenerReload, ListenerAction {

    private Icon ico = new javax.swing.ImageIcon(getClass().getResource("/img16/b053.gif"));

    private ListenerRecord listenet = null;
    private Canvas canvas = new Canvas();
    private Scene scene = null;
    private Query qSysmodel = new Query(eSysmodel.values());
    private DefMutableTreeNode winNode = null;

    public Models() {
        initComponents();
        scene = new Scene(canvas, this, this);
        initElements();
        loadingModel();
        btnChoice.setVisible(false);
        loadingTab1(tab1, 2001);
    }

    public Models(ListenerRecord listener) {
        initComponents();
        scene = new Scene(canvas, this);
        initElements();
        loadingModel();
        this.listenet = listener;
        loadingTab1(tab1, 2001);
    }

    public void loadingModel() {
        new DefTableModel(tab1, qSysmodel, eSysmodel.npp, eSysmodel.name, eSysmodel.id);
        tab1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 2) {
                    Object v = qSysmodel.get(row).get(eSysmodel.values().length);
                    if (v instanceof Wincalc) {
                        label.setIcon(((Wincalc) v).imageIcon);
                    }
                } else {
                    label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
                    label.setIcon(null);
                }
                return label;
            }
        });
        canvas.setVisible(true);
    }

    public void loadingTab1(JTable tab, int form) {
        qSysmodel.sq2(eSysmodel.data(), eSysmodel.form, form, eSysmodel.form, 2000, eSysmodel.id, 0).sort(eSysmodel.npp);
        DefaultTableModel dm = (DefaultTableModel) tab.getModel();
        dm.getDataVector().removeAllElements();
        for (Record record : qSysmodel.table(eSysmodel.up)) {
            try {
                String script = record.getStr(eSysmodel.script);
                Wincalc iwin2 = new Wincalc(script);
                TJoining joining = new TJoining(iwin2, true);//заполним соединения из конструктива
                joining.join();
                iwin2.imageIcon = Canvas.createIcon(iwin2, 68);
                record.add(iwin2);

            } catch (Exception e) {
                System.err.println("Error: Models.loadingTab() " + e);
            }
        }
        ((DefaultTableModel) tab.getModel()).fireTableDataChanged();
    }

    public void loadingTree(Wincalc winc) {
        try {
            DefMutableTreeNode root = UTree.loadModTree(winc);
            winTree.setModel(new DefaultTreeModel(root));
            winTree.setRootVisible(false);

        } catch (Exception e) {
            System.err.println("Error: Models.loadingWinTree() " + e);
        }
    }

    //Выбор другой конструкции    
    public void selectionTab1() {

        Wincalc win = wincalc();
        if (win != null) { //прорисовка окна               

            loadingTree(win);
            winTree.setSelectionRow(1);

            scene.init(win);
            canvas.draw();
        }
    }

    //При выборе элемента конструкции public  
    public void selectionTree() {
        try {
            //Выделенный элемент
            Object selNode = winTree.getLastSelectedPathComponent();
            if (selNode instanceof DefMutableTreeNode) {
                winNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
                Com5t com5t = winNode.com5t();
                Wincalc winc = wincalc();

                //Таймер цвета
                if (enums.Type.contains(com5t, enums.Type.PARAM, enums.Type.FRAME, enums.Type.JOINING) == false) {
                    if (winc.canvas != null) {
                        com5t.timer.start();
                        winc.canvas.repaint();
                    }
                }

                UTree.changeModPpmTree(winTree, ppmTree, com5t);
            }
        } catch (Exception e) {
            System.err.println("Error: Models.selectionTree() " + e);
        }
    }

    //Изменить скрипт в базе, пересчитать и перерисовать
    public void changeAndRedraw() {
        try {
            //Сохраним скрипт в базе
            Wincalc winc = wincalc();
            String script = winc.gson.toJson();
            Record sysmodelRec = qSysmodel.get(UGui.getIndexRec(tab1));
            sysmodelRec.set(eSysmodel.script, script);

            //Экземпляр нового скрипта
            winc.build(script);
            winc.imageIcon = Canvas.createIcon(winc, 68);

            //Запомним курсор
            DefMutableTreeNode selectNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
            double id = (selectNode != null) ? selectNode.com5t().id : -1;

            //Перегрузим winTree
            loadingTree(winc);

            //Установим курсор
            UTree.selectionPathWin(id, winTree);

            //Перерисуем конструкцию
            canvas.init(winc);
            canvas.draw();

            //Обновим поля форм
            selectionTree();

        } catch (Exception e) {
            System.err.println("Error: Models.updateScript() " + e);
        }
    }

    @Override
    public Query reload(boolean b) {
        try {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Wincalc winc = wincalc();
                String script = winc.gson.toJson();
                winc.build(script);
                winc.imageIcon = Canvas.createIcon(winc, 68);
                Record sysmodelRec = qSysmodel.get(index);
                sysmodelRec.set(eSysmodel.script, script);
                sysmodelRec.set(eSysmodel.values().length, winc);
                ((DefaultTableModel) tab1.getModel()).fireTableDataChanged();
                UGui.setSelectedIndex(tab1, index);
            }
        } catch (Exception e) {
            System.err.println("Error: Models.reload() " + e);
        }
        return null;
    }

    @Override
    public void action() {
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            btnToggl(null);
            UGui.setSelectedIndex(tab1, index);
        }
    }

    private Wincalc wincalc() {
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record sysmodelRec = qSysmodel.table(eSysmodel.up).get(index);
            Object v = sysmodelRec.get(eSysmodel.values().length);
            if (v instanceof Wincalc) {
                return (Wincalc) v;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        ppmTree = new javax.swing.JPopupMenu();
        addImpost = new javax.swing.JMenu();
        addImpostHor = new javax.swing.JMenuItem();
        addImpostVer = new javax.swing.JMenuItem();
        removeImpost = new javax.swing.JMenuItem();
        addStvorka = new javax.swing.JMenuItem();
        removeStvorka = new javax.swing.JMenuItem();
        removeMosquit = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnChoice = new javax.swing.JButton();
        btnT1 = new javax.swing.JToggleButton();
        btnT2 = new javax.swing.JToggleButton();
        btnT3 = new javax.swing.JToggleButton();
        btnT4 = new javax.swing.JToggleButton();
        btnMoveU = new javax.swing.JButton();
        btnMoveD = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnIns1 = new javax.swing.JButton();
        west = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pan13 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan12 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        winTree = new javax.swing.JTree();
        centr = new javax.swing.JPanel();
        pan17 = new javax.swing.JPanel();
        panDesign = new javax.swing.JPanel();
        south = new javax.swing.JPanel();

        addImpost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b055.gif"))); // NOI18N
        addImpost.setText("Добавить импост");

        addImpostHor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b055.gif"))); // NOI18N
        addImpostHor.setText("горизонтальный");
        addImpostHor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImpostHorAction(evt);
            }
        });
        addImpost.add(addImpostHor);

        addImpostVer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b055.gif"))); // NOI18N
        addImpostVer.setText("вертикальный");
        addImpostVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImpostVerAction(evt);
            }
        });
        addImpost.add(addImpostVer);

        ppmTree.add(addImpost);

        removeImpost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b055.gif"))); // NOI18N
        removeImpost.setText("Удалить импост");
        removeImpost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeImpostAction(evt);
            }
        });
        ppmTree.add(removeImpost);

        addStvorka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b055.gif"))); // NOI18N
        addStvorka.setText("Добавить сворку");
        addStvorka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStvorkaAction(evt);
            }
        });
        ppmTree.add(addStvorka);

        removeStvorka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b055.gif"))); // NOI18N
        removeStvorka.setText("Удалить створку");
        removeStvorka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeStvorkaAction(evt);
            }
        });
        ppmTree.add(removeStvorka);

        removeMosquit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b055.gif"))); // NOI18N
        removeMosquit.setText("Удалить москитку");
        removeMosquit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMosquitAction(evt);
            }
        });
        ppmTree.add(removeMosquit);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Модели виртуальных профилей");
        setFont(frames.UGui.getFont(0,1));
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Models.this.windowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                Models.this.windowOpened(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(800, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("hints/okno", common.eProp.locale); // NOI18N
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
                btnClose(evt);
            }
        });

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c034.gif"))); // NOI18N
        btnDel.setToolTipText(bundle.getString("Удалить")); // NOI18N
        btnDel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnDel.setFocusable(false);
        btnDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDel.setMaximumSize(new java.awt.Dimension(25, 25));
        btnDel.setMinimumSize(new java.awt.Dimension(25, 25));
        btnDel.setPreferredSize(new java.awt.Dimension(25, 25));
        btnDel.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c001.gif"))); // NOI18N
        btnDel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete(evt);
            }
        });

        btnIns.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c097.gif"))); // NOI18N
        btnIns.setToolTipText(bundle.getString("Добавить")); // NOI18N
        btnIns.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnIns.setFocusable(false);
        btnIns.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnIns.setMaximumSize(new java.awt.Dimension(25, 25));
        btnIns.setMinimumSize(new java.awt.Dimension(25, 25));
        btnIns.setPreferredSize(new java.awt.Dimension(25, 25));
        btnIns.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c001.gif"))); // NOI18N
        btnIns.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnIns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenScript(evt);
            }
        });

        btnChoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c044.gif"))); // NOI18N
        btnChoice.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btnChoice.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnChoice.setFocusable(false);
        btnChoice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnChoice.setMaximumSize(new java.awt.Dimension(25, 25));
        btnChoice.setMinimumSize(new java.awt.Dimension(25, 25));
        btnChoice.setPreferredSize(new java.awt.Dimension(25, 25));
        btnChoice.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c001.gif"))); // NOI18N
        btnChoice.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoice(evt);
            }
        });

        buttonGroup.add(btnT1);
        btnT1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c003.gif"))); // NOI18N
        btnT1.setSelected(true);
        btnT1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnT1.setMaximumSize(new java.awt.Dimension(25, 25));
        btnT1.setMinimumSize(new java.awt.Dimension(25, 25));
        btnT1.setPreferredSize(new java.awt.Dimension(25, 25));
        btnT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggl(evt);
            }
        });

        buttonGroup.add(btnT2);
        btnT2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c004.gif"))); // NOI18N
        btnT2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnT2.setMaximumSize(new java.awt.Dimension(25, 25));
        btnT2.setMinimumSize(new java.awt.Dimension(25, 25));
        btnT2.setPreferredSize(new java.awt.Dimension(25, 25));
        btnT2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggl(evt);
            }
        });

        buttonGroup.add(btnT3);
        btnT3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c005.gif"))); // NOI18N
        btnT3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnT3.setMaximumSize(new java.awt.Dimension(25, 25));
        btnT3.setMinimumSize(new java.awt.Dimension(25, 25));
        btnT3.setPreferredSize(new java.awt.Dimension(25, 25));
        btnT3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggl(evt);
            }
        });

        buttonGroup.add(btnT4);
        btnT4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c079.gif"))); // NOI18N
        btnT4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnT4.setMaximumSize(new java.awt.Dimension(25, 25));
        btnT4.setMinimumSize(new java.awt.Dimension(25, 25));
        btnT4.setPreferredSize(new java.awt.Dimension(25, 25));
        btnT4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggl(evt);
            }
        });

        btnMoveU.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c051.gif"))); // NOI18N
        btnMoveU.setToolTipText(bundle.getString("Переместить вверх")); // NOI18N
        btnMoveU.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMoveU.setFocusable(false);
        btnMoveU.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveU.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMoveU.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMoveU.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMoveU.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c001.gif"))); // NOI18N
        btnMoveU.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });

        btnMoveD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c052.gif"))); // NOI18N
        btnMoveD.setToolTipText(bundle.getString("Переместить вниз")); // NOI18N
        btnMoveD.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMoveD.setFocusable(false);
        btnMoveD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveD.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMoveD.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMoveD.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMoveD.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c001.gif"))); // NOI18N
        btnMoveD.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });

        btnTest.setText("Test");
        btnTest.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTest.setMaximumSize(new java.awt.Dimension(25, 25));
        btnTest.setMinimumSize(new java.awt.Dimension(25, 25));
        btnTest.setPreferredSize(new java.awt.Dimension(25, 25));
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestActionPerformed(evt);
            }
        });

        btnIns1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c065.gif"))); // NOI18N
        btnIns1.setToolTipText(bundle.getString("Добавить")); // NOI18N
        btnIns1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnIns1.setFocusable(false);
        btnIns1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnIns1.setMaximumSize(new java.awt.Dimension(25, 25));
        btnIns1.setMinimumSize(new java.awt.Dimension(25, 25));
        btnIns1.setPreferredSize(new java.awt.Dimension(25, 25));
        btnIns1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/img24/c001.gif"))); // NOI18N
        btnIns1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnIns1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsert(evt);
            }
        });

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnIns1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMoveU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMoveD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(btnT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnT2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnT4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 310, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnChoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMoveU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMoveD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTest, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnIns1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnT2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnT4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        west.setPreferredSize(new java.awt.Dimension(360, 560));
        west.setLayout(new java.awt.BorderLayout());

        pan13.setName(""); // NOI18N
        pan13.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "№", "Наименование", "Рисунок"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setRowHeight(68);
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(260);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(68);
            tab1.getColumnModel().getColumn(2).setMaxWidth(68);
        }

        pan13.add(scr1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("      Список конструкций      ", pan13);

        pan12.setLayout(new java.awt.BorderLayout());

        scr2.setBorder(null);

        winTree.setBorder(null);
        winTree.setFont(frames.UGui.getFont(0,0));
        scr2.setViewportView(winTree);

        pan12.add(scr2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("  Конструктор компонента    ", pan12);

        west.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(west, java.awt.BorderLayout.WEST);

        centr.setPreferredSize(new java.awt.Dimension(600, 560));
        centr.setLayout(new java.awt.CardLayout());

        pan17.setPreferredSize(new java.awt.Dimension(600, 500));
        pan17.setLayout(new java.awt.BorderLayout());

        panDesign.setLayout(new java.awt.BorderLayout());
        pan17.add(panDesign, java.awt.BorderLayout.CENTER);

        centr.add(pan17, "pan17");

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(800, 20));

        javax.swing.GroupLayout southLayout = new javax.swing.GroupLayout(south);
        south.setLayout(southLayout);
        southLayout.setHorizontalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 820, Short.MAX_VALUE)
        );
        southLayout.setVerticalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete
        if (UGui.isDeleteRecord(tab1, this) == 0) {
            UGui.deleteRecord(tab1);
        }
    }//GEN-LAST:event_btnDelete

    private void btnOpenScript(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenScript
        try {
            String json = null;
            Record record = eSysmodel.up.newRecord(Query.INS);
            record.set(eSysmodel.id, Connect.genId(eSysmodel.up));
            record.set(eSysmodel.npp, record.get(eSysmodel.id));

            //Для загрузки скрипта из программы  
            if (eProp.typuse.equals("99") == true) {
                Object prj = JOptionPane.showInputDialog(Models.this, "Номер проекта", "Проект", JOptionPane.QUESTION_MESSAGE);
                if (prj != null) {
                    json = GsonScript.modelScript(Integer.valueOf(String.valueOf(prj)));
                    GsonRoot gsonRoot = new Gson().fromJson(json, GsonRoot.class);
                    record.set(eSysmodel.name, "<html> Kod:" + prj + "* " + gsonRoot.name);
                    record.set(eSysmodel.script, json);
                }
            } else {
                //Для загрузки скрипта с диска              
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Json (формат json)", "json");
                chooser.setFileFilter(filter);
                if (chooser.showDialog(this, "Выбрать") != JFileChooser.CANCEL_OPTION) {
                    String path = chooser.getSelectedFile().getPath();
                    JsonReader reader = new JsonReader(new FileReader(path));
                    GsonRoot gsonRoot = new Gson().fromJson(reader, GsonRoot.class);
                    json = gsonRoot.toJson();
                    reader.close();
                    record.set(eSysmodel.name, "<html>" + gsonRoot.name);
                    record.set(eSysmodel.script, json);
                }
            }
            if (json != null) {
                if (btnT1.isSelected()) {
                    record.set(eSysmodel.form, 2001);
                    qSysmodel.insert(record);
                    loadingTab1(tab1, 2001);

                } else if (btnT2.isSelected()) {
                    record.set(eSysmodel.form, 2004);
                    qSysmodel.insert(record);
                    loadingTab1(tab1, 2004);

                } else if (btnT3.isSelected()) {
                    record.set(eSysmodel.form, 2002);
                    qSysmodel.insert(record);
                    loadingTab1(tab1, 2002);

                } else if (btnT4.isSelected()) {
                    record.set(eSysmodel.form, 2007);
                    qSysmodel.insert(record);
                    loadingTab1(tab1, 2007);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: Models.btnInsert()");
        }
    }//GEN-LAST:event_btnOpenScript

    private void btnChoice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoice
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record record = new Record();
            record.add(qSysmodel.get(index, eSysmodel.id));
            record.add(qSysmodel.get(index, eSysmodel.name));
            record.add(qSysmodel.get(index, eSysmodel.script));
            listenet.action(record);
        }
        this.dispose();
    }//GEN-LAST:event_btnChoice

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());
    }//GEN-LAST:event_windowClosed

    private void btnToggl(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggl

        if (btnT1.isSelected()) {
            loadingTab1(tab1, 2001);
        } else if (btnT2.isSelected()) {
            loadingTab1(tab1, 2004);
        } else if (btnT3.isSelected()) {
            loadingTab1(tab1, 2002);
        } else if (btnT4.isSelected()) {
            loadingTab1(tab1, 2007);
        }
        UGui.updateBorderAndSql(tab1, List.of(tab1));
        UGui.setSelectedRow(tab1);
    }//GEN-LAST:event_btnToggl

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (listenet != null && evt.getClickCount() == 2) {
            btnChoice(null);
        }
    }//GEN-LAST:event_tabMouseClicked

    private void btnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMove
        JTable table = UGui.tableFromBorder(tab1);
        int index = UGui.getIndexRec(table), index2 = index;
        JButton btn = (JButton) evt.getSource();
        if (index != -1) {
            if (table.getBorder() != null) {
                if (btn == btnMoveD && table.getSelectedRow() < table.getRowCount() - 1) {
                    Collections.swap(qSysmodel, index, ++index2);

                } else if (btn == btnMoveU && table.getSelectedRow() > 0) {
                    Collections.swap(qSysmodel, index, --index2);
                }
                IntStream.range(0, qSysmodel.size()).forEach(i -> qSysmodel.set(i + 1, i, eSysmodel.npp));
                ((DefaultTableModel) table.getModel()).fireTableDataChanged();
                UGui.setSelectedIndex(table, index2);
                qSysmodel.execsql();
            }
        }
    }//GEN-LAST:event_btnMove

    private void btnTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestActionPerformed
    }//GEN-LAST:event_btnTestActionPerformed

    private void windowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowOpened
        UGui.setSelectedRow(tab1);
    }//GEN-LAST:event_windowOpened

    private void addImpostHorAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addImpostHorAction
        Com5t glass = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
        for (int i = 0; i < glass.owner.gson.childs.size(); ++i) {
            if (glass.owner.gson.childs.get(i).id == glass.id) {
                Envelope env = glass.owner.area.getEnvelopeInternal();
                double x1 = env.getMinX(), x2 = env.getMaxX(), y = env.getMinY() + env.getHeight() / 2;
                glass.owner.gson.childs.set(i, new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS)));
                glass.owner.gson.childs.add(i, new GsonElem(enums.Type.IMPOST, x1, y, x2, y));
                glass.owner.gson.childs.add(i, new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS)));
            }
        }
        changeAndRedraw();
    }//GEN-LAST:event_addImpostHorAction

    private void addImpostVerAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addImpostVerAction
        Com5t glass = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
        for (int i = 0; i < glass.owner.gson.childs.size(); ++i) {
            if (glass.owner.gson.childs.get(i).id == glass.id) {
                Envelope env = glass.owner.area.getEnvelopeInternal();
                double y1 = env.getMinY(), y2 = env.getMaxY(), x = env.getMinX() + env.getWidth() / 2;
                glass.owner.gson.childs.set(i, new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS)));
                glass.owner.gson.childs.add(i, new GsonElem(enums.Type.IMPOST, x, y1, x, y2));
                glass.owner.gson.childs.add(i, new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS)));
            }
        }
        changeAndRedraw();
    }//GEN-LAST:event_addImpostVerAction

    private void removeImpostAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeImpostAction
        Com5t owner = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t().owner;
        owner.gson.childs = owner.gson.childs.stream().filter(e -> e.type == enums.Type.BOX_SIDE).collect(toList());
        owner.gson.addElem(new GsonElem(enums.Type.GLASS));
        changeAndRedraw();
    }//GEN-LAST:event_removeImpostAction

    private void addStvorkaAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStvorkaAction
        try {
            GsonElem gsonGlass = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t().gson;
            GsonElem gsonStvorka = new GsonElem(enums.Type.STVORKA);
            gsonStvorka.addArea(new GsonElem(enums.Type.GLASS));
            gsonStvorka.owner = gsonGlass.owner;

            if (gsonGlass.owner instanceof GsonRoot) {
                for (int i = 0; i < gsonGlass.owner.childs.size(); ++i) {
                    if (gsonGlass.owner.childs.get(i).id == gsonGlass.id) {
                        gsonGlass.owner.childs.set(i, gsonStvorka);
                    }
                }
            } else {
                for (int i = 0; i < gsonGlass.owner.owner.childs.size(); ++i) {
                    if (gsonGlass.owner.owner.childs.get(i).id == gsonGlass.owner.id) {
                        gsonGlass.owner.owner.childs.set(i, gsonStvorka);
                    }
                }
            }
            changeAndRedraw();

        } catch (Exception e) {
            System.err.println("Error: Models.addStvorkaAction()");
        }
    }//GEN-LAST:event_addStvorkaAction

    private void removeStvorkaAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeStvorkaAction
        Com5t stv = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
        for (int i = 0; i < stv.owner.gson.childs.size(); ++i) {
            if (stv.owner.gson.childs.get(i).id == stv.id) {
                if (stv.owner.gson instanceof GsonRoot) { //первый уровень

                    if (wincalc().listElem.stream().anyMatch(e -> e.type == enums.Type.IMPOST)) {
                        GsonElem glass = new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS));
                        stv.owner.gson.childs.set(i, glass);
                    } else {
                        GsonElem glass = new GsonElem(enums.Type.GLASS);
                        stv.owner.gson.childs.set(i, glass);
                    }
                } else { //второй, третий... уровни
                    GsonElem glass = new GsonElem(enums.Type.AREA).addElem(new GsonElem(enums.Type.GLASS));
                    stv.owner.gson.childs.set(i, glass);
                }
            }
        }
        changeAndRedraw();
    }//GEN-LAST:event_removeStvorkaAction

    private void removeMosquitAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeMosquitAction
        Com5t mosq = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
        for (int i = 0; i < mosq.owner.gson.childs.size(); ++i) {
            if (mosq.owner.gson.childs.get(i).id == mosq.id) {
                mosq.owner.gson.childs.remove(i);
            }
        }
        changeAndRedraw();
    }//GEN-LAST:event_removeMosquitAction

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert
        UGui.stopCellEditing(tab1);
        int result = JOptionPane.showConfirmDialog(this, "Вы действительно хотите клонировать текущую запись?",
                "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.NO_OPTION) {
            int index = UGui.getIndexRec(tab1);
            Record sysmodelRec = qSysmodel.get(index);
            String name = sysmodelRec.getStr(eSysmodel.name);
            String script = sysmodelRec.getStr(eSysmodel.script);

            UGui.insertRecordEnd(tab1, eSysmodel.up, (record) -> {
                String ID = record.getStr(eSysmodel.id);
                int max = qSysmodel.stream().mapToInt(rec -> rec.getInt(eSysmodel.npp)).max().orElse(0);
                int form = -1;
                if (btnT1.isSelected()) {
                    form = 2001;
                } else if (btnT2.isSelected()) {
                    form = 2004;
                } else if (btnT3.isSelected()) {
                    form = 2002;
                } else if (btnT4.isSelected()) {
                    form = 2007;
                }
                record.set(eSysmodel.npp, ++max);
                record.set(eSysmodel.form, form);
                record.set(eSysmodel.name, "Kod:" + ID + " " + name);
                record.set(eSysmodel.script, script);
                Wincalc iwin = new Wincalc(script);
                iwin.imageIcon = Canvas.createIcon(iwin, 68);
                record.add(iwin);
            });
        }
    }//GEN-LAST:event_btnInsert

// <editor-fold defaultstate="collapsed" desc="Generated Code">     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu addImpost;
    private javax.swing.JMenuItem addImpostHor;
    private javax.swing.JMenuItem addImpostVer;
    private javax.swing.JMenuItem addStvorka;
    private javax.swing.JButton btnChoice;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnIns1;
    private javax.swing.JButton btnMoveD;
    private javax.swing.JButton btnMoveU;
    private javax.swing.JToggleButton btnT1;
    private javax.swing.JToggleButton btnT2;
    private javax.swing.JToggleButton btnT3;
    private javax.swing.JToggleButton btnT4;
    private javax.swing.JButton btnTest;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel centr;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan13;
    private javax.swing.JPanel pan17;
    private javax.swing.JPanel panDesign;
    private javax.swing.JPopupMenu ppmTree;
    private javax.swing.JMenuItem removeImpost;
    private javax.swing.JMenuItem removeMosquit;
    private javax.swing.JMenuItem removeStvorka;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JPanel west;
    private javax.swing.JTree winTree;
    // End of variables declaration//GEN-END:variables
// </editor-fold>
    private void initElements() {

        btnTest.setVisible(eProp.typuse.equals("99"));
        App.loadLocationWin(this, btnClose, (e) -> {
            App.saveLocationWin(this, btnClose);
        });
        panDesign.add(scene, java.awt.BorderLayout.CENTER);
        tab1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab1();
                }
            }
        });
        winTree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree());
        DefaultTreeCellRenderer rnd2 = (DefaultTreeCellRenderer) winTree.getCellRenderer();
        rnd2.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b077.gif")));
        rnd2.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b007.gif")));
        rnd2.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/img16/b006.gif")));
    }
}
