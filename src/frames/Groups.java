package frames;

import common.UCom;
import common.eProp;
import common.listener.ListenerRecord;
import dataset.Query;
import dataset.Record;
import domain.eCurrenc;
import domain.eGroups;
import enums.TypeGrup;
import frames.swing.cmp.DefCellEditorBtn;
import frames.swing.cmp.DefCellEditorNumb;
import frames.swing.cmp.DefCellRendererNumb;
import frames.swing.cmp.DefTableModel;
import frames.swing.cmp.TableFieldFilter;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import static frames.UGui.getIndexRec;

public class Groups extends javax.swing.JFrame {

    private ListenerRecord listener = null;
    private Query qCurrenc = new Query(eCurrenc.values());
    private Query qDecInc = new Query(eGroups.values());
    private Query qArtSeri = new Query(eGroups.values());
    private Query qArtIncr = new Query(eGroups.values());
    private Query qArtDecr = new Query(eGroups.values());
    private Query qCategProf = new Query(eGroups.values());
    private Query qColGrp = new Query(eGroups.values());
    private Query qColMap = new Query(eGroups.values());
    private Query qCategVst = new Query(eGroups.values());
    private Query qCategKit = new Query(eGroups.values());
    private int mode = 0;

    public Groups(int mode) {
        this.mode = mode;
        initComponents();
        initElements();
        btnChoice.setVisible(false);
        loadingData();
        loadingModel();
        List<Component> list = (mode == 1) ? List.of(pan1, pan2, pan5, pan6, pan7) : List.of(pan3, pan4);
        list.forEach(comp -> tabb.remove(comp));
        setTitle((mode == 1) ? "����������� ��" : "������� ������������");
    }

    public Groups(int mode, ListenerRecord listener) {
        this.mode = mode;
        initComponents();
        initElements();
        this.listener = listener;
        btnChoice.setVisible(true);
        loadingData();
        loadingModel();
        List<Component> list = (mode == 1) ? List.of(pan1, pan2, pan5, pan6, pan7) : List.of(pan3, pan4);
        list.forEach(comp -> tabb.remove(comp));
        setTitle((mode == 1) ? "�����������" : "������������");
    }

    private void loadingData() {
        qCurrenc.sql(eCurrenc.data(), eCurrenc.up).sort(eCurrenc.npp, eCurrenc.name);
        qColGrp.sql(eGroups.data(), eGroups.grup, 2).sort(eGroups.npp, eGroups.name);
        qColMap.sql(eGroups.data(), eGroups.grup, 7).sort(eGroups.npp, eGroups.name);
        qArtSeri.sql(eGroups.data(), eGroups.grup, 3).sort(eGroups.npp, eGroups.name);
        qArtIncr.sql(eGroups.data(), eGroups.grup, 4).sort(eGroups.npp, eGroups.name);
        qArtDecr.sql(eGroups.data(), eGroups.grup, 5).sort(eGroups.npp, eGroups.name);
        qCategProf.sql(eGroups.data(), eGroups.grup, 6).sort(eGroups.npp, eGroups.name);
        qCategVst.sql(eGroups.data(), eGroups.grup, 8).sort(eGroups.npp, eGroups.name);
        qCategKit.sql(eGroups.data(), eGroups.grup, 10).sort(eGroups.npp, eGroups.name);
        qDecInc.sql(eGroups.data(), eGroups.grup, 9, eGroups.id, 2101, 2104, 2073, 2007, 2027).sort(eGroups.npp, eGroups.name);
    }

    public void loadingModel() {
        new DefTableModel(tab1, qArtIncr, eGroups.name, eGroups.val);
        new DefTableModel(tab2, qArtDecr, eGroups.name, eGroups.val);
        new DefTableModel(tab3, qArtSeri, eGroups.name);
        new DefTableModel(tab4, qCategProf, eGroups.name);
        new DefTableModel(tab5, qColGrp, eGroups.name, eGroups.val);
        new DefTableModel(tab6, qCurrenc, eCurrenc.name, eCurrenc.par_case1, eCurrenc.par_case2, eCurrenc.cross_cour);
        new DefTableModel(tab7, qDecInc, eGroups.name, eGroups.val);

        tab6.getColumnModel().getColumn(3).setCellEditor(new DefCellEditorNumb(4));
        tab6.getColumnModel().getColumn(3).setCellRenderer(new DefCellRendererNumb(4));
        List.of(tab1, tab2, tab5).forEach(tab -> tab.getColumnModel().getColumn(1).setCellEditor(new DefCellEditorNumb(3)));
        tab7.getColumnModel().getColumn(1).setCellEditor(new DefCellEditorNumb(3));
        tab7.getColumnModel().getColumn(1).setCellRenderer(new DefCellRendererNumb(3) {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (column == 1) {
                    int id = qDecInc.getAs(row, eGroups.id);
                    if (id == 2073) {
                        if ((double) value == 3) {
                            value = "1 ��";
                        } else if ((double) value == 2) {
                            value = "0,5 ��";
                        } else if ((double) value == 1) {
                            value = "0,1 ��";
                        } else if ((double) value == 0) {
                            value = "������";
                        }
                    } else if (id != -1 && (id == 2055 || id == 2056 || id == 2058 || id == 2101 || id == 2104)) {
                        value = UCom.format(value, scale) + "%";
                    }
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        List.of(tab1, tab2, tab3, tab4, tab5, tab6, tab7).forEach(tab -> UGui.setSelectedRow(tab));

        UGui.buttonCellEditor(tab7, 1, (component) -> {

            if (component instanceof DefCellEditorBtn) { //��������� ��� � ��� ������
                DefCellEditorBtn editor = (DefCellEditorBtn) component;
                int groupsID = qDecInc.getAs(getIndexRec(tab7), eGroups.id);
                if (groupsID == 2073) {
                    editor.getButton().setVisible(true);
                    editor.getTextField().setEnabled(false);
                } else { //�������� �������������
                    editor.getButton().setVisible(false);
                    editor.getTextField().setEnabled(true);
                    editor.getTextField().setEditable(true);
                }
            } else {  //�������� �� ����������� �����
                String txt = (String) component;
                return ("0123456789,".indexOf(txt) != -1);
            }
            return true;

        }).addActionListener(event -> {
            String arrStr[] = {"������", "0,1 ��", "0,5 ��", "1,0 ��"};
            double arrDbl[] = {0, 1, 2, 3};
            Object result = JOptionPane.showInputDialog(Groups.this, "�������� �������� ��������",
                    "���������� ����� ��������", JOptionPane.QUESTION_MESSAGE, null, arrStr, arrStr[0]);
            for (int i = 0; i < arrStr.length; ++i) {
                if (arrStr[i].equals(result)) {
                    UGui.stopCellEditing(tab7);
                    qDecInc.set(arrDbl[i], UGui.getIndexRec(tab7), eGroups.val);
                    ((DefTableModel) tab7.getModel()).fireTableRowsUpdated(tab7.getSelectedRow(), tab7.getSelectedRow());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnMoveD = new javax.swing.JButton();
        btnMoveU = new javax.swing.JButton();
        btnChoice = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        tabb = new javax.swing.JTabbedPane();
        pan6 = new javax.swing.JPanel();
        scr6 = new javax.swing.JScrollPane();
        tab6 = new javax.swing.JTable();
        pan7 = new javax.swing.JPanel();
        scr7 = new javax.swing.JScrollPane();
        tab7 = new javax.swing.JTable();
        pan2 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan5 = new javax.swing.JPanel();
        scr5 = new javax.swing.JScrollPane();
        tab5 = new javax.swing.JTable();
        pan3 = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        pan4 = new javax.swing.JPanel();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        mInsert.setFont(frames.UGui.getFont(1,0));
        mInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        mInsert.setText("��������");
        mInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mInsert);

        mDelit.setFont(frames.UGui.getFont(1,0));
        mDelit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        mDelit.setText("�������");
        mDelit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mDelit);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("������� ������������");
        setMinimumSize(new java.awt.Dimension(700, 400));
        setPreferredSize(new java.awt.Dimension(600, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Groups.this.windowClosed(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(10, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        btnClose.setToolTipText(bundle.getString("�������")); // NOI18N
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

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        btnDel.setToolTipText(bundle.getString("�������")); // NOI18N
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
                btnDelete(evt);
            }
        });

        btnIns.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        btnIns.setToolTipText(bundle.getString("��������")); // NOI18N
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
                btnInsert(evt);
            }
        });

        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c053.gif"))); // NOI18N
        btnReport.setToolTipText(bundle.getString("������")); // NOI18N
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

        btnMoveD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c052.gif"))); // NOI18N
        btnMoveD.setToolTipText(bundle.getString("����������� ����")); // NOI18N
        btnMoveD.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMoveD.setFocusable(false);
        btnMoveD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveD.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMoveD.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMoveD.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMoveD.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMoveD.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });

        btnMoveU.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c051.gif"))); // NOI18N
        btnMoveU.setToolTipText(bundle.getString("����������� �����")); // NOI18N
        btnMoveU.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMoveU.setFocusable(false);
        btnMoveU.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveU.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMoveU.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMoveU.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMoveU.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMoveU.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMoveU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
            }
        });

        btnChoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c044.gif"))); // NOI18N
        btnChoice.setToolTipText(bundle.getString("�������")); // NOI18N
        btnChoice.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnChoice.setFocusable(false);
        btnChoice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnChoice.setMaximumSize(new java.awt.Dimension(25, 25));
        btnChoice.setMinimumSize(new java.awt.Dimension(25, 25));
        btnChoice.setPreferredSize(new java.awt.Dimension(25, 25));
        btnChoice.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoice(evt);
            }
        });

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMoveU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMoveD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 701, Short.MAX_VALUE)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
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
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnMoveU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnMoveD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(600, 459));
        centr.setLayout(new java.awt.BorderLayout());

        tabb.setFont(frames.UGui.getFont(0,0));
        tabb.setPreferredSize(new java.awt.Dimension(600, 459));
        tabb.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbStateChanged(evt);
            }
        });
        tabb.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });

        pan6.setLayout(new java.awt.BorderLayout());

        scr6.setBorder(null);

        tab6.setFont(frames.UGui.getFont(0,0));
        tab6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "��������", "��. �����", "��. �����", "����", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab6.setFillsViewportHeight(true);
        tab6.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr6.setViewportView(tab6);
        if (tab6.getColumnModel().getColumnCount() > 0) {
            tab6.getColumnModel().getColumn(4).setPreferredWidth(40);
            tab6.getColumnModel().getColumn(4).setMaxWidth(60);
        }

        pan6.add(scr6, java.awt.BorderLayout.CENTER);

        tabb.addTab("����� �����", pan6);

        pan7.setName("pan7"); // NOI18N
        pan7.setLayout(new java.awt.BorderLayout());

        scr7.setBorder(null);

        tab7.setFont(frames.UGui.getFont(0,0));
        tab7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "��������", "��������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab7.setFillsViewportHeight(true);
        scr7.setViewportView(tab7);
        if (tab7.getColumnModel().getColumnCount() > 0) {
            tab7.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab7.getColumnModel().getColumn(1).setMaxWidth(80);
            tab7.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab7.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        pan7.add(scr7, java.awt.BorderLayout.CENTER);

        tabb.addTab("���������� ���������", pan7);

        pan2.setName("pan2"); // NOI18N
        pan2.setLayout(new java.awt.BorderLayout());

        scr2.setBorder(null);

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������� ������", "������ %", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab2.setFillsViewportHeight(true);
        tab2.setName("tab2"); // NOI18N
        tab2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab2.getColumnModel().getColumn(1).setMaxWidth(80);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        pan2.add(scr2, java.awt.BorderLayout.CENTER);

        tabb.addTab("������ ����� ��", pan2);

        pan1.setName("pan1"); // NOI18N
        pan1.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������� ������", "������� (����)", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setName("tab1"); // NOI18N
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(1).setMaxWidth(120);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        pan1.add(scr1, java.awt.BorderLayout.CENTER);

        tabb.addTab("������� ����� ��", pan1);

        pan5.setName("pan5"); // NOI18N
        pan5.setLayout(new java.awt.BorderLayout());

        scr5.setBorder(null);

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "������������ �����", "�����������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab5.setFillsViewportHeight(true);
        tab5.setName("tab5"); // NOI18N
        tab5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr5.setViewportView(tab5);
        if (tab5.getColumnModel().getColumnCount() > 0) {
            tab5.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab5.getColumnModel().getColumn(1).setMaxWidth(80);
            tab5.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab5.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        pan5.add(scr5, java.awt.BorderLayout.CENTER);

        tabb.addTab("����. ����� �������", pan5);

        pan3.setName("pan3"); // NOI18N
        pan3.setLayout(new java.awt.BorderLayout());

        scr3.setBorder(null);

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "������������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab3.setFillsViewportHeight(true);
        tab3.setName("tab3"); // NOI18N
        tab3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(1).setPreferredWidth(40);
            tab3.getColumnModel().getColumn(1).setMaxWidth(60);
        }

        pan3.add(scr3, java.awt.BorderLayout.CENTER);

        tabb.addTab("       ����� ��      ", pan3);

        pan4.setName("pan4"); // NOI18N
        pan4.setLayout(new java.awt.BorderLayout());

        scr4.setBorder(null);

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null}
            },
            new String [] {
                "������������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab4.setFillsViewportHeight(true);
        tab4.setName("tab4"); // NOI18N
        tab4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(1).setPreferredWidth(40);
            tab4.getColumnModel().getColumn(1).setMaxWidth(60);
        }

        pan4.add(scr4, java.awt.BorderLayout.CENTER);

        tabb.addTab("     ������  ��    ", pan4);

        centr.add(tabb, java.awt.BorderLayout.CENTER);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(10, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete
        JTable table = UGui.tableFromBorder(tab1, tab2, tab3, tab4, tab4, tab6, tab7);
        if (UGui.isDeleteRecord(table, this) == 0) {
            UGui.deleteRecord(table);
        }
    }//GEN-LAST:event_btnDelete

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert

        if (tab1.getBorder() != null) {
            UGui.insertRecordCur(tab1, eGroups.up, (record) -> {
                record.set(eGroups.val, 1);
                //record.setDev(eGroups.name, "�������");
                record.set(eGroups.grup, TypeGrup.PRICE_INC.id);
            });

        } else if (tab2.getBorder() != null) {
            UGui.insertRecordCur(tab2, eGroups.up, (record) -> {
                record.set(eGroups.val, 0);
                //record.setDev(eGroups.name, "������");
                record.set(eGroups.grup, TypeGrup.PRICE_DEC.id);
            });

        } else if (tab3.getBorder() != null) {
            UGui.insertRecordCur(tab3, eGroups.up, (record) -> {
                record.set(eGroups.grup, TypeGrup.SERI_ELEM.id);
                //record.setDev(eGroups.name, "�����");
            });

        } else if (tab4.getBorder() != null) {
            UGui.insertRecordCur(tab4, eGroups.up, (record) -> {
                record.set(eGroups.grup, TypeGrup.CATEG_ELEM.id);
                //record.setDev(eGroups.name, "�����.");
            });

        } else if (tab5.getBorder() != null) {
            UGui.insertRecordCur(tab5, eGroups.up, (record) -> {
                record.set(eGroups.val, 1);
                record.set(eGroups.grup, TypeGrup.COLOR_GRP.id);
                //record.setDev(eGroups.name, "����.");
            });

        } else if (tab6.getBorder() != null) {
            UGui.insertRecordCur(tab6, eCurrenc.up, (record) -> {
                //record.setDev(eCurrenc.name, "����");
            });

        }
        eGroups.data().execsql();
    }//GEN-LAST:event_btnInsert

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
         UGui.findComponents(getRootPane(), JTable.class).stream().forEach(System.out::println);    
    }//GEN-LAST:event_btnReport

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());        
    }//GEN-LAST:event_windowClosed

    private void btnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMove
        JTable table = UGui.tableFromBorder(tab1, tab2, tab3, tab4, tab5, tab6, tab7);
        int index = UGui.getIndexRec(table);
        int index2 = index;
        if (index != -1 && table != null) {
            JButton btn = (JButton) evt.getSource();
            Query query = ((DefTableModel) table.getModel()).getQuery();

            if (btn == btnMoveD && table.getSelectedRow() < table.getRowCount() - 1) {
                Collections.swap(query, index, ++index2);

            } else if (btn == btnMoveU && table.getSelectedRow() > 0) {
                Collections.swap(query, index, --index2);
            }
            for (int i = 0; i < query.size(); i++) {
                query.set(i + 1, i, eGroups.npp);
            }
            eGroups.data().execsql();

            ((DefaultTableModel) table.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(table, index2);
        }
    }//GEN-LAST:event_btnMove

    private void tabbStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbStateChanged
        UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5, tab6, tab7);
        List.of(btnIns, btnDel).forEach(q -> q.setEnabled(true));
        JTable table = null;
        if (mode == 0) {
            if (tabb.getSelectedIndex() == 0) {
                table = tab6;
            } else if (tabb.getSelectedIndex() == 1) {
                table = tab7;
                btnIns.setEnabled(false);
                btnDel.setEnabled(false);
            } else if (tabb.getSelectedIndex() == 2) {
                table = tab2;
            } else if (tabb.getSelectedIndex() == 3) {
                table = tab1;
            } else if (tabb.getSelectedIndex() == 4) {
                table = tab5;
                btnIns.setEnabled(false);
                btnDel.setEnabled(false);                
            }
        } else {
            if (tabb.getSelectedIndex() == 0) {
                table = tab3;
            } else if (tabb.getSelectedIndex() == 1) {
                table = tab4;
            }
        }
        UGui.updateBorderAndSql(table, List.of(tab1, tab2, tab3, tab4, tab5, tab6, tab7));
    }//GEN-LAST:event_tabbStateChanged

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab1, tab2, tab3, tab4, tab6).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab1, tab2, tab3, tab4, tab6).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked

    private void btnChoice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoice
        eGroups.data().execsql(); //���� ������� � ����� ������
        JTable table = UGui.tableFromBorder(tab1, tab2, tab3, tab4, tab4, tab6, tab7);
        int index = UGui.getIndexRec(table);
        if (index != -1) {
            Record record = ((DefTableModel) table.getModel()).getQuery().get(index);
            listener.action(record);
        }
        this.dispose();
    }//GEN-LAST:event_btnChoice

    // <editor-fold defaultstate="collapsed" desc="Generated Code">     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChoice;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnMoveD;
    private javax.swing.JButton btnMoveU;
    private javax.swing.JButton btnReport;
    private javax.swing.JPanel centr;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan7;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JScrollPane scr6;
    private javax.swing.JScrollPane scr7;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JTable tab5;
    private javax.swing.JTable tab6;
    private javax.swing.JTable tab7;
    private javax.swing.JTabbedPane tabb;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    public void initElements() {

        eProp.getWin(this, btnClose, (e) -> {
            eProp.putWin(this, btnClose);
        }); 

        TableFieldFilter filterTable = new TableFieldFilter(0, tab1, tab2, tab3, tab4, tab5, tab6, tab7);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        List.of(btnIns, btnDel).forEach(btn -> btn.addActionListener(l -> UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5, tab6, tab7)));
    }
}
