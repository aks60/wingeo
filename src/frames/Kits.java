package frames;

import frames.swing.ProgressBar;
import frames.swing.FrameToFile;
import builder.param.ParamList;
import common.eProp;
import common.listener.ListenerFrame;
import common.listener.ListenerRecord;
import dataset.Conn;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import domain.eGroups;
import domain.eJoinpar2;
import domain.eKitdet;
import domain.eKitpar2;
import domain.eKits;
import domain.eParams;
import enums.Enam;
import enums.TypeGrup;
import enums.UseColor;
import enums.UseUnit;
import frames.dialog.DicArtikl2;
import frames.dialog.DicColor;
import frames.dialog.ParName;
import frames.dialog.ParUserVal;
import frames.dialog.ParSysVal;
import frames.swing.DefCellEditorBtn;
import frames.swing.DefCellRendererBool;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import frames.swing.DefTableModel;
import frames.swing.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import startup.App;

public class Kits extends javax.swing.JFrame {

    private Query qGroups = new Query(eGroups.values());
    private Query qCateg = new Query(eGroups.values());
    private Query qKits = new Query(eKits.values());
    private Query qKitdet = new Query(eKitdet.values());
    private Query qKitpar2 = new Query(eKitpar2.values());
    private Query qParams = new Query(eParams.values());
    private ListenerRecord listenerArtikl, listenerColor1, listenerColor2, listenerColor3;

    public Kits() {
        initComponents();
        initElements();
        listenerSet();
        loadingData();
        loadingModel();
        listenerAdd();
    }

    public void loadingData() {
        eArtikl.query();
        qGroups.select(eGroups.up, "where", eGroups.grup, " in (" + TypeGrup.PARAM_USER.id, ",", TypeGrup.COLOR_MAP.id + ")");
        qCateg.select(eGroups.up, "where", eGroups.grup, "=", TypeGrup.CATEG_KIT.id, "order by", eGroups.name);
        qKits.select(eKits.up, "order by", eKits.groups_id, ",", eKits.name);
    }

    public void loadingModel() {
        new DefTableModel(tab1, qCateg, eGroups.name);
        new DefTableModel(tab2, qKits, eKits.name);
        new DefTableModel(tab3, qKitdet, eKitdet.artikl_id, eKitdet.artikl_id,
                eKitdet.color1_id, eKitdet.color2_id, eKitdet.color3_id, eKitdet.id, eKitdet.flag) {

            public Object getValueAt(int col, int row, Object val) {

                if (val != null && val.equals(0) && List.of(eKitdet.color1_id, eKitdet.color2_id, eKitdet.color3_id).contains(columns[col])) {
                    return UseColor.automatic[1];

                } else if (val != null && col == 0) {
                    return eArtikl.get((int) val).getStr(eArtikl.code);

                } else if (val != null && col == 1) {
                    return eArtikl.get((int) val).getStr(eArtikl.name);

                } else if (val != null && columns[col] == eKitdet.color1_id) {
                    return eColor.get((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == eKitdet.color2_id) {
                    return eColor.get((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == eKitdet.color3_id) {
                    return eColor.get((int) val).getStr(eColor.name);

                } else if (val != null && col == 5) { //columns[col] == eArtikl.unit) {
                    int id = qKitdet.getAs(row, eKitdet.artikl_id);
                    Record record = eArtikl.get(id);
                    return UseUnit.getName(record.getInt(eArtikl.unit));
                }
                return val;
            }
        };
        new DefTableModel(tab4, qKitpar2, eKitpar2.groups_id, eKitpar2.text) {

            public Object getValueAt(int col, int row, Object val) {
                if (val != null) {
                    Field field = columns[col];
                    if (field == eKitpar2.groups_id) {

                        if (Integer.valueOf(String.valueOf(val)) < 0) {
                            return qGroups.find(val, eGroups.id).getDev(eGroups.name, val);
                        } else {
                            Enam en = ParamList.find(val);
                            return Record.getDev(en.numb(), en.text());
                        }
                    }
                }
                return val;
            }
        };
        tab3.getColumnModel().getColumn(6).setCellRenderer(new DefCellRendererBool());
        UGui.setSelectedRow(tab1);
    }

    public void selectionTab1(ListSelectionEvent event) {
        List.of(qKits, qKitdet).forEach(q -> q.execsql());
        UGui.clearTable(tab2, tab3, tab4);
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record record = qCateg.get(index);
            Integer id = record.getInt(eGroups.id);
            qKits.select(eKits.up, "where", eKits.groups_id, "=", id, "order by", eKits.name);
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab2);
        }
    }

    public void selectionTab2(ListSelectionEvent event) {
        List.of(qKitdet).forEach(q -> q.execsql());
        UGui.clearTable(tab3, tab4);
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            Record record = qKits.get(index);
            Integer id = record.getInt(eKits.id);
            qKitdet.select(eKitdet.up, "where", eKitdet.kits_id, "=", id, "order by", eKitdet.id);
            ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab3);
        }
    }

    public void selectionTab3(ListSelectionEvent event) {
        List.of(qKitpar2).forEach(q -> q.execsql());
        UGui.clearTable(tab4);
        int index = UGui.getIndexRec(tab3);
        if (index != -1) {
            Record record = qKitdet.get(index);
            Integer id = record.getInt(eKitdet.id);
            //qKitpar2.select(eKitpar2.up, "where", eKitpar2.kitdet_id, "=", id, "order by", eKitpar2.text);
            qKitpar2.clear();
            qKitpar2.addAll(eKitpar2.find(id));
            ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab4);
        }
    }

    public void listenerAdd() {

        UGui.buttonCellEditor(tab3, 0).addActionListener(event -> {
            new DicArtikl2(this, listenerArtikl, 1, 2, 3, 4, 5);
        });

        UGui.buttonCellEditor(tab3, 1).addActionListener(event -> {
            new DicArtikl2(this, listenerArtikl, 1, 2, 3, 4, 5);
        });

        UGui.buttonCellEditor(tab3, 2).addActionListener(event -> {
            Record record = qKitdet.get(UGui.getIndexRec(tab3));
            int artikl_id = record.getInt(eKitdet.artikl_id);
            HashSet<Record> colorSet = UGui.artiklToColorSet(artikl_id, 1);
            new DicColor(this, listenerColor1, colorSet, true, true);
        });

        UGui.buttonCellEditor(tab3, 3).addActionListener(event -> {
            Record record = qKitdet.get(UGui.getIndexRec(tab3));
            int artikl_id = record.getInt(eKitdet.artikl_id);
            HashSet<Record> colorSet = UGui.artiklToColorSet(artikl_id, 2);
            new DicColor(this, listenerColor2, colorSet, true, true);
        });

        UGui.buttonCellEditor(tab3, 4).addActionListener(event -> {
            Record record = qKitdet.get(UGui.getIndexRec(tab3));
            int artikl_id = record.getInt(eKitdet.artikl_id);
            HashSet<Record> colorSet = UGui.artiklToColorSet(artikl_id, 3);
            new DicColor(this, listenerColor3, colorSet, true, false);
        });

        UGui.buttonCellEditor(tab4, 0).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab3);
            if (index != -1) {
                int param = -1;
                Record recordDet = qKitdet.get(index);
                int artikl_id = recordDet.getInt(eKitdet.artikl_id);
                Record recordArt = eArtikl.get(artikl_id);
                if (UseUnit.PIE.id == recordArt.getInt(eArtikl.unit)) {
                    param = 7000;
                } else if (UseUnit.METR.id == recordArt.getInt(eArtikl.unit)) {
                    param = 8000;
                } else if (UseUnit.METR2.id == recordArt.getInt(eArtikl.unit)) {
                    param = 9000;
                }

                if (qKitpar2.get(UGui.getIndexRec(tab4), eKitpar2.groups_id) == null) {
                    ParName frame = new ParName(this, (rec) -> {
                        UGui.cellParamNameOrValue(rec, tab4, eKitpar2.groups_id, eKitpar2.text);
                    }, eParams.kits, param);
                } else {
                    int groupsID = qKitpar2.getAs(UGui.getIndexRec(tab4), eJoinpar2.groups_id);
                    ParName frame = new ParName(this, groupsID, (rec) -> {
                        UGui.cellParamNameOrValue(rec, tab4, eKitpar2.groups_id, eKitpar2.text);
                    }, eParams.kits, param);
                }
            }
        });

        UGui.buttonCellEditor(tab4, 1, (component) -> { //слушатель редактирование типа и вида данных и вида ячейки таблицы
            return UGui.cellParamTypeOrVid(tab4, component, eKitpar2.groups_id);

        }).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            Record record = qKitpar2.get(UGui.getIndexRec(tab4));
            int grup = record.getInt(eKitpar2.groups_id);
            if (grup < 0) {
                ParUserVal frame = new ParUserVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab4, eKitpar2.groups_id, eKitpar2.text);
                }, eParams.kits, grup);
            } else {
                List list = ParamList.find(grup).dict();
                ParSysVal frame = new ParSysVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab4, eKitpar2.groups_id, eKitpar2.text);
                }, list);
            }
        });
    }

    public void listenerSet() {

        listenerArtikl = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            if (tab3.getBorder() != null) {
                int index = UGui.getIndexRec(tab3);
                qKitdet.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab3), eKitdet.artikl_id);
                UGui.fireTableRowUpdated(tab3);
            }
        };

        listenerColor1 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab3);
            Record kitdetRec = qKitdet.get(index);
            Integer ID = (record.get(eColor.id) == null) ? null : record.getInt(eColor.id);
            kitdetRec.set(eKitdet.color1_id, ID);
            UGui.fireTableRowUpdated(tab3);
        };

        listenerColor2 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab3);
            Record record2 = qKitdet.get(index);
            Integer ID = (record.get(eColor.id) == null) ? null : record.getInt(eColor.id);
            record2.set(eKitdet.color2_id, ID);
            UGui.fireTableRowUpdated(tab3);
        };

        listenerColor3 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab3);
            Record record2 = qKitdet.get(index);
            Integer ID = (record.get(eColor.id) == null) ? null : record.getInt(eColor.id);
            record2.set(eKitdet.color3_id, ID);
            UGui.fireTableRowUpdated(tab3);
        };
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnRef = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnFind = new javax.swing.JButton();
        west = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        centr = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        mInsert.setFont(frames.UGui.getFont(1,0));
        mInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        mInsert.setText("Добавить");
        mInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mInsert);

        mDelit.setFont(frames.UGui.getFont(1,0));
        mDelit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        mDelit.setText("Удалить");
        mDelit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mDelit);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Комплекты");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(900, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Kits.this.windowClosed(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(800, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
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

        btnRef.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c038.gif"))); // NOI18N
        btnRef.setToolTipText(bundle.getString("Обновить")); // NOI18N
        btnRef.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnRef.setFocusable(false);
        btnRef.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRef.setMaximumSize(new java.awt.Dimension(25, 25));
        btnRef.setMinimumSize(new java.awt.Dimension(25, 25));
        btnRef.setPreferredSize(new java.awt.Dimension(25, 25));
        btnRef.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnRef.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefresh(evt);
            }
        });

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        btnDel.setToolTipText(bundle.getString("Удалить")); // NOI18N
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
        btnIns.setToolTipText(bundle.getString("Добавить")); // NOI18N
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

        btnFind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c090.gif"))); // NOI18N
        btnFind.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btnFind.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFind.setFocusable(false);
        btnFind.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFind.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFind.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFind.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFind.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnFind.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFind(evt);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 670, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRef, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFind, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        west.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        west.setPreferredSize(new java.awt.Dimension(440, 10));
        west.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), " ", javax.swing.border.TitledBorder.RIGHT, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr1.setPreferredSize(new java.awt.Dimension(140, 404));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"wwwwwww"},
                {"ddddddddd"}
            },
            new String [] {
                "Категория"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setName("tab1"); // NOI18N
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Kits.this.mousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);

        west.add(scr1, java.awt.BorderLayout.WEST);

        scr2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Список комплектов", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"111111111111111",  new Integer(1)},
                {"222222222222222",  new Integer(2)}
            },
            new String [] {
                "Название комплектов", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class
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
        tab2.setFillsViewportHeight(true);
        tab2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Kits.this.mousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(200);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(1).setMaxWidth(60);
        }

        west.add(scr2, java.awt.BorderLayout.CENTER);

        getContentPane().add(west, java.awt.BorderLayout.WEST);

        centr.setPreferredSize(new java.awt.Dimension(600, 200));
        centr.setLayout(new java.awt.BorderLayout());

        scr3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Детализация комплектов", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "111", "1", "1", "1", null, null,  new Integer(1)},
                {"2", "222", "2", "2", "2", null, null,  new Integer(2)}
            },
            new String [] {
                "Артикул", "Название", "Основная текстура", "Внутренняя текстура", "Внешняя текстура", "Ед.измерения", "Основной элемент", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab3.setFillsViewportHeight(true);
        tab3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Kits.this.mousePressed(evt);
            }
        });
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(140);
            tab3.getColumnModel().getColumn(1).setPreferredWidth(260);
            tab3.getColumnModel().getColumn(2).setPreferredWidth(80);
            tab3.getColumnModel().getColumn(3).setPreferredWidth(80);
            tab3.getColumnModel().getColumn(4).setPreferredWidth(80);
            tab3.getColumnModel().getColumn(6).setPreferredWidth(60);
            tab3.getColumnModel().getColumn(7).setPreferredWidth(40);
            tab3.getColumnModel().getColumn(7).setMaxWidth(60);
        }

        centr.add(scr3, java.awt.BorderLayout.CENTER);

        scr4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Параметры", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr4.setPreferredSize(new java.awt.Dimension(0, 200));

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"111", "1"},
                {"222", "2"}
            },
            new String [] {
                "Параметр", "Значение"
            }
        ));
        tab4.setFillsViewportHeight(true);
        tab4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Kits.this.mousePressed(evt);
            }
        });
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(0).setPreferredWidth(220);
            tab4.getColumnModel().getColumn(1).setPreferredWidth(80);
        }

        centr.add(scr4, java.awt.BorderLayout.SOUTH);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(800, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnRefresh(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefresh
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(qCateg, qKits, qKitdet, qKitpar2).forEach(q -> q.execsql());
        int index1 = UGui.getIndexRec(tab1);
        int index2 = UGui.getIndexRec(tab2);
        int index3 = UGui.getIndexRec(tab3);
        int index4 = UGui.getIndexRec(tab4);
        loadingData();
        UGui.setSelectedIndex(tab1, index1);
        UGui.setSelectedIndex(tab2, index2);
        UGui.setSelectedIndex(tab3, index3);
        UGui.setSelectedIndex(tab4, index4);
    }//GEN-LAST:event_btnRefresh

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete
        if (tab1.getBorder() != null) {
            if (UGui.isDeleteRecord(tab1, this, tab2) == 0) {
                UGui.deleteRecord(tab1);
            }
        } else if (tab2.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2, this, tab3) == 0) {
                UGui.deleteRecord(tab2);
            }

        } else if (tab3.getBorder() != null) {
            if (UGui.isDeleteRecord(tab3, this, tab4) == 0) {
                UGui.deleteRecord(tab3);
            }

        } else if (tab4.getBorder() != null) {
            if (UGui.isDeleteRecord(tab4, this) == 0) {
                UGui.deleteRecord(tab4);
            }
        }
    }//GEN-LAST:event_btnDelete

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert
        if (tab1.getBorder() != null) {
            Object result = JOptionPane.showInputDialog(Kits.this, "Название", "Категория", JOptionPane.QUESTION_MESSAGE);
            if (result != null) {
                Record groupsRec = eGroups.up.newRecord(Query.INS);
                int id = Conn.genId(eGroups.up);
                groupsRec.setNo(eGroups.id, id);
                groupsRec.setNo(eGroups.grup, TypeGrup.CATEG_KIT.id);
                groupsRec.setNo(eGroups.name, result);
                qCateg.insert(groupsRec);
                loadingData();
                int index;
                for (index = 0; index < qCateg.size(); ++index) {
                    if (qCateg.get(index).getInt(eGroups.id) == id) {
                        UGui.setSelectedIndex(tab1, index - 1);
                        ((DefaultTableModel) tab1.getModel()).fireTableRowsInserted(index - 1, index - 1);
                        UGui.scrollRectToRow(index, tab1);
                        break;
                    }
                }
                ((DefaultTableModel) tab1.getModel()).fireTableRowsInserted(index, index);
                UGui.setSelectedIndex(tab1, index);
                UGui.scrollRectToIndex(index, tab1);
            }
        } else if (tab2.getBorder() != null) {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                UGui.insertRecordCur(tab2, eKits.up, (record) -> {
                    record.set(eKits.groups_id, qCateg.getAs(index, eGroups.id));
                });
            }

        } else if (tab3.getBorder() != null) {
            int index = UGui.getIndexRec(tab2, 0);
            UGui.insertRecordCur(tab3, eKitdet.up, (record) -> {
                record.set(eKitdet.kits_id, qKits.getAs(index, eKits.id));
            });

        } else if (tab4.getBorder() != null) {
            int index = UGui.getIndexRec(tab3, 0);
            UGui.insertRecordCur(tab4, eKitpar2.up, (record) -> {
                record.set(eKitpar2.kitdet_id, qKitdet.getAs(index, eKitdet.id));
            });
            DefCellEditorBtn defCellEditorBtn = (DefCellEditorBtn) tab4.getColumnModel().getColumn(0).getCellEditor();
            defCellEditorBtn.getButton().getActionListeners()[0].actionPerformed(null);
        }
    }//GEN-LAST:event_btnInsert

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(qKits, qKitdet, qKitpar2).forEach(q -> q.execsql());
    }//GEN-LAST:event_windowClosed

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        UGui.updateBorderAndSql((JTable) evt.getSource(), List.of(tab1, tab2, tab3, tab4));
    }//GEN-LAST:event_mousePressed

    private void btnFind(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind
        if (tab3.getBorder() != null) {
            Record record = ((DefTableModel) tab3.getModel()).getQuery().get(UGui.getIndexRec(tab3));
            if (record != null) {
                Record record2 = eArtikl.find(record.getInt(eKitdet.artikl_id), false);
                ProgressBar.create(this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        App.Artikles.createFrame(Kits.this, record2);
                    }
                });
            }
        }
    }//GEN-LAST:event_btnFind

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab1, tab2, tab3, tab4).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab1, tab2, tab3, tab4).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnRef;
    private javax.swing.JPanel centr;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel north;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JPanel west;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
    public void initElements() {
        new FrameToFile(this, btnClose);

        TableFieldFilter filterTable = new TableFieldFilter(0, tab2);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        btnIns.addActionListener(l -> UGui.stopCellEditing(tab1, tab2, tab3, tab4));
        btnDel.addActionListener(l -> UGui.stopCellEditing(tab1, tab2, tab3, tab4));
        btnRef.addActionListener(l -> UGui.stopCellEditing(tab1, tab2, tab3, tab4));

        FocusListener listenerFocus = new FocusListener() {

            javax.swing.border.Border border = javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255));

            public void focusGained(FocusEvent e) {
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                tab1.setBorder(null);
                tab2.setBorder(null);
                tab3.setBorder(null);
                tab4.setBorder(null);
                if (e.getSource() instanceof JTable) {
                    ((JTable) e.getSource()).setBorder(border);
                }
            }

            public void focusLost(FocusEvent e) {
            }
        };
        tab1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab1(event);
                }
            }
        });
        tab2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab2(event);
                }
            }
        });
        tab3.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                selectionTab3(event);
            }
        });
        tab1.addFocusListener(listenerFocus);
        tab2.addFocusListener(listenerFocus);
        tab3.addFocusListener(listenerFocus);
        tab4.addFocusListener(listenerFocus);
    }
}
