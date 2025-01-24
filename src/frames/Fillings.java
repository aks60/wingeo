package frames;

import frames.swing.ProgressBar;
import dataset.Query;
import dataset.Record;
import domain.eArtikl;
import domain.eGlasdet;
import domain.eGlasgrp;
import domain.eGlaspar1;
import domain.eGlaspar2;
import domain.eGlasprof;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import frames.swing.DefTableModel;
import dataset.Field;
import frames.dialog.DicArtikl;
import frames.dialog.DicColvar;
import frames.dialog.ParColor;
import frames.dialog.ParName;
import frames.dialog.ParSysVal;
import frames.dialog.ParUserVal;
import domain.eColor;
import domain.eElemdet;
import domain.eElement;
import domain.eElempar1;
import domain.eGroups;
import domain.eParams;
import enums.Enam;
import builder.param.ParamList;
import common.ePref;
import enums.TypeGrup;
import enums.UseColor;
import frames.dialog.DicName;
import java.util.List;
import frames.swing.DefCellRendererBool;
import startup.App;
import common.listener.ListenerRecord;
import common.listener.ListenerFrame;
import domain.eArtdet;
import domain.eParmap;
import frames.dialog.DicArtikl2;
import frames.swing.DefCellEditorBtn;
import frames.swing.DefCellEditorNumb;
import frames.swing.TableFieldFilter;
import java.awt.event.MouseEvent;
import report.sup.ExecuteCmd;
import report.sup.RTable;

public class Fillings extends javax.swing.JFrame {

    private Query qGroups = new Query(eGroups.values());
    private Query qColor = new Query(eColor.id, eColor.groups_id, eColor.name);
    private Query qParams = new Query(eParams.values());
    private Query qGlasgrp = new Query(eGlasgrp.values());
    private Query qGlasdet = new Query(eGlasdet.values(), eArtikl.values());
    private Query qGlasprof = new Query(eGlasprof.values(), eArtikl.values());
    private Query qGlaspar1 = new Query(eGlaspar1.values());
    private Query qGlaspar2 = new Query(eGlaspar2.values());
    private ListenerRecord listenerArtikl, listenerPar1, listenerPar2, listenerColor,
            listenerColvar1, listenerColvar2, listenerColvar3, listenerTypset, listenerThicknes;

    public Fillings() {
        initComponents();
        initElements();
        listenerSet();
        loadingData();
        loadingModel();
        listenerAdd();
    }

    public Fillings(int... ids) {
        initComponents();
        initElements();
        loadingData();
        listenerSet();
        loadingModel();
        listenerAdd();
        if (ids.length == 1) {
            deteilFind(ids[0]);
        } else if (ids.length > 1) {
            profileFind(ids);
        }
    }

    public void loadingData() {
        qGroups.sql(eGroups.data(), eGroups.grup, TypeGrup.COLOR_MAP.id, TypeGrup.PARAM_USER.id);
        qColor.sql(eColor.data(), eColor.up);
        qParams.sql(eParams.data(), eParams.up);
        qGlasgrp.sql(eGlasgrp.data(), eGlasgrp.up).sort(eGlasgrp.name);

    }

    public void loadingModel() {
        new DefTableModel(tab1, qGlasgrp, eGlasgrp.name, eGlasgrp.gap, eGlasgrp.depth);
        new DefTableModel(tab2, qGlasdet, eGlasdet.depth, eArtikl.code, eArtikl.name, eGlasdet.color_fk, eGlasdet.color_us, eGlasdet.color_us, eGlasdet.color_us) {

            public Object getValueAt(int col, int row, Object val) {
                if (val != null) {
                    Field field = columns[col];
                    if (eGlasdet.color_fk == field) {
                        int colorFk = Integer.valueOf(String.valueOf(val));

                        if (UseColor.automatic[0].equals(colorFk)) {
                            return UseColor.automatic[1];

                        } else if (UseColor.precision[0].equals(colorFk)) {
                            return UseColor.precision[1];
                        }
                        if (colorFk > 0) {
                            return qColor.find(eColor.data(), eColor.id, colorFk).get(eColor.name);
                        } else {
                            return "# " + qGroups.find(eGroups.data(), eGroups.id, colorFk).get(eGroups.name);
                        }
                    } else if (eGlasdet.color_us == field) {
                        int types = Integer.valueOf(String.valueOf(val));
                        types = (col == 4) ? types & 0x0000000f : (col == 5) ? (types & 0x000000f0) >> 4 : (types & 0x00000f00) >> 8;
                        return UseColor.MANUAL.find(types).text();
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab3, qGlaspar1, eGlaspar1.groups_id, eGlaspar1.text) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && eGlaspar1.groups_id == field) {

                    if (Integer.valueOf(String.valueOf(val)) < 0) {
                        return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).getDev(eGroups.name, val);
                    } else {
                        Enam en = ParamList.find(val);
                        return Record.getDev(en.numb(), en.text());
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab4, qGlaspar2, eGlaspar2.groups_id, eGlaspar2.text) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && field == eGlaspar2.groups_id) {

                    if (Integer.valueOf(String.valueOf(val)) < 0) {
                        return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).getDev(eGroups.name, val);
                    } else {
                        Enam en = ParamList.find(val);
                        return Record.getDev(en.numb(), en.text());
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab5, qGlasprof, eArtikl.code, eArtikl.name, eGlasprof.gsize, eGlasprof.inside, eGlasprof.outside);
        tab1.getColumnModel().getColumn(1).setCellEditor(new DefCellEditorNumb(2));
        tab1.getColumnModel().getColumn(2).setCellEditor(new DefCellEditorNumb("2"));
        List.of(3, 4).forEach(index -> tab5.getColumnModel().getColumn(index).setCellRenderer(new DefCellRendererBool()));
        UGui.setSelectedRow(tab1);
    }

    public void selectionTab1(ListSelectionEvent event) {
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            List.of(qGlasdet, qGlaspar1, qGlaspar2, qGlasprof).forEach(q -> q.execsql());
            UGui.clearTable(tab2, tab3, tab4, tab5);
            Record record = qGlasgrp.table(eGlasgrp.up).get(index);
            Integer id = record.getInt(eGlasgrp.id);
            qGlasdet.sql(eGlasdet.data(), eGlasdet.glasgrp_id, id).sort(eGlasdet.depth);
            qGlasdet.table(eArtikl.up).join(qGlasdet, eArtikl.data(), eGlasdet.artikl_id, eArtikl.id);
            qGlasprof.sql(eGlasprof.data(), eGlasprof.glasgrp_id, id);
            qGlasprof.table(eArtikl.up).join(qGlasprof, eArtikl.data(), eGlasprof.artikl_id, eArtikl.id);
            qGlaspar1.sql(eGlaspar1.data(), eGlaspar1.glasgrp_id, id);
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
            ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab2);
            UGui.setSelectedRow(tab3);
            UGui.setSelectedRow(tab5);
        }
    }

    public void selectionTab2(ListSelectionEvent event) {
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            List.of(qGlaspar2, qGlasprof).forEach(q -> q.execsql());
            UGui.clearTable(tab4);
            Record record = qGlasdet.table(eGlasdet.up).get(index);
            Integer id = record.getInt(eGlasdet.id);
            qGlaspar2.sql(eGlaspar2.data(), eGlaspar2.glasdet_id, id);
            ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab4);
        }
    }

    public void listenerAdd() {
        UGui.buttonCellEditor(tab2, 0).addActionListener(event -> {
            Query qArtikl = new Query(eArtikl.values());
            eArtikl.sql(qArtikl, eArtikl.level1, 5).sort(eArtikl.depth);
            DicName frame = new DicName(this, listenerThicknes, qArtikl, eArtikl.depth);
        });

        UGui.buttonCellEditor(tab2, 1).addActionListener(event -> {
            new DicArtikl2(this, listenerArtikl, 1, 2, 3, 4);
        });

        UGui.buttonCellEditor(tab2, 2).addActionListener(event -> {
            new DicArtikl2(this, listenerArtikl, 1, 2, 3, 4);
        });

        UGui.buttonCellEditor(tab2, 3).addActionListener(event -> {
            Record record = qGlasdet.get(UGui.getIndexRec(tab2));
            int artiklID = record.getInt(eElemdet.artikl_id);
            int colorID = record.getInt(eGlasdet.color_fk, -1);
            new ParColor(this, listenerColor, eParmap.glas, artiklID, colorID);
        });

        UGui.buttonCellEditor(tab2, 4).addActionListener(event -> {
            Record record = qGlasdet.get(UGui.getIndexRec(tab2));
            int colorFk = record.getInt(eGlasdet.color_fk);
            DicColvar frame = new DicColvar(this, listenerColvar1, colorFk);
        });

        UGui.buttonCellEditor(tab2, 5).addActionListener(event -> {
            Record record = qGlasdet.get(UGui.getIndexRec(tab2));
            int colorFk = record.getInt(eGlasdet.color_fk);
            DicColvar frame = new DicColvar(this, listenerColvar2, colorFk);
        });

        UGui.buttonCellEditor(tab2, 6).addActionListener(event -> {
            Record record = qGlasdet.get(UGui.getIndexRec(tab2));
            int colorFk = record.getInt(eGlasdet.color_fk);
            DicColvar frame = new DicColvar(this, listenerColvar3, colorFk);
        });

        UGui.buttonCellEditor(tab3, 0).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                if (qGlaspar1.get(UGui.getIndexRec(tab3), eGlaspar1.groups_id) == null) {
                    new ParName(this, listenerPar1, eParams.glas, 13000);
                } else {
                    int groupsID = qGlaspar1.getAs(UGui.getIndexRec(tab3), eGlaspar1.groups_id);
                    new ParName(this, groupsID, listenerPar1, eParams.glas, 13000);
                }
            }
        });

        UGui.buttonCellEditor(tab3, 1, (componentCell) -> { //слушатель редактирование типа, вида данных и вида ячейки таблицы
            return UGui.cellParamTypeOrVid(tab3, componentCell, eGlaspar1.groups_id);

        }).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int grup = qGlaspar1.getAs(UGui.getIndexRec(tab3), eGlaspar1.groups_id);
            if (grup < 0) {
                ParUserVal frame = new ParUserVal(this, listenerPar1, eParams.glas, grup);
            } else {
                List list = ParamList.find(grup).dict();
                ParSysVal frame = new ParSysVal(this, listenerPar1, list);
            }
        });

        UGui.buttonCellEditor(tab4, 0).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab2);
            if (index != -1) {
                Record record = qGlasdet.table(eArtikl.up).get(index);
                int paramPart = record.getInt(eArtikl.level1);
                paramPart = (paramPart == 1 || paramPart == 3) ? 15000 : 14000;

                if (qGlaspar2.get(UGui.getIndexRec(tab4), eGlaspar2.groups_id) == null) {
                    new ParName(this, listenerPar2, eParams.glas, paramPart);
                } else {
                    int groupsID = qGlaspar2.getAs(UGui.getIndexRec(tab4), eGlaspar2.groups_id);
                    new ParName(this, groupsID, listenerPar2, eParams.glas, paramPart);
                }
            }
        });

        UGui.buttonCellEditor(tab4, 1, (componentCell) -> { //слушатель редактирование типа, вида данных и вида ячейки таблицы
            return UGui.cellParamTypeOrVid(tab4, componentCell, eGlaspar2.groups_id);

        }).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            Record record = qGlaspar2.get(UGui.getIndexRec(tab4));
            int grup = record.getInt(eGlaspar1.groups_id);
            if (grup < 0) {
                ParUserVal frame = new ParUserVal(this, listenerPar2, eParams.glas, grup);
            } else {
                List list = ParamList.find(grup).dict();
                ParSysVal frame = new ParSysVal(this, listenerPar2, list);
            }
        });

        UGui.buttonCellEditor(tab5, 0).addActionListener(event -> {
            new DicArtikl(this, listenerArtikl, true, 1);
        });

        UGui.buttonCellEditor(tab5, 1).addActionListener(event -> {
            new DicArtikl(this, listenerArtikl, true, 1);
        });
    }

    public void listenerSet() {

        listenerArtikl = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            if (tab2.getBorder() != null) {
                int index = UGui.getIndexRec(tab2);
                int artiklID = record.getInt(eArtikl.id);
                qGlasdet.set(artiklID, UGui.getIndexRec(tab2), eGlasdet.artikl_id);
                qGlasdet.table(eArtikl.up).set(record.get(eArtikl.code), UGui.getIndexRec(tab2), eArtikl.code);
                qGlasdet.table(eArtikl.up).set(record.get(eArtikl.name), UGui.getIndexRec(tab2), eArtikl.name);
                List<Record> artdetList = eArtdet.filter2(artiklID);

                if (artdetList.size() == 1) {
                    if (artdetList.get(0).getInt(eArtdet.color_fk) > 0) {
                        Record colorRec = eColor.find(artdetList.get(0).getInt(eArtdet.color_fk));
                        listenerColor.action(colorRec);
                    }
                }
                ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
                UGui.setSelectedIndex(tab2, index);

            } else if (tab5.getBorder() != null) {
                int index = UGui.getIndexRec(tab5);
                qGlasprof.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab5), eGlasprof.artikl_id);
                qGlasprof.table(eArtikl.up).set(record.get(eArtikl.code), UGui.getIndexRec(tab5), eArtikl.code);
                qGlasprof.table(eArtikl.up).set(record.get(eArtikl.name), UGui.getIndexRec(tab5), eArtikl.name);
                ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
                UGui.setSelectedIndex(tab5, index);
            }
        };

        listenerColor = (record) -> {
            UGui.cellParamColor(record, tab2, eGlasdet.color_fk, eGlasdet.color_us, tab1, tab2, tab3, tab4, tab5);
        };

        listenerColvar1 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab2);
            Record glasdetRec = qGlasdet.get(UGui.getIndexRec(tab2));
            int types = (glasdetRec.getInt(eGlasdet.color_us) == -1) ? 0 : glasdetRec.getInt(eGlasdet.color_us);
            types = (types & 0xfffffff0) + record.getInt(0);
            glasdetRec.set(eGlasdet.color_us, types);
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab2, index);
        };

        listenerColvar2 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab2);
            Record glasdetRec = qGlasdet.get(UGui.getIndexRec(tab2));
            int types = (glasdetRec.getInt(eGlasdet.color_us) == -1) ? 0 : glasdetRec.getInt(eGlasdet.color_us);
            types = (types & 0xffffff0f) + (record.getInt(0) << 4);
            glasdetRec.set(eGlasdet.color_us, types);
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab2, index);
        };

        listenerColvar3 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab2);
            Record glasdetRec = qGlasdet.get(UGui.getIndexRec(tab2));
            int types = (glasdetRec.getInt(eGlasdet.color_us) == -1) ? 0 : glasdetRec.getInt(eGlasdet.color_us);
            types = (types & 0xfffff0ff) + (record.getInt(0) << 8);
            glasdetRec.set(eGlasdet.color_us, types);
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab2, index);
        };

        listenerTypset = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            if (tab2.getBorder() != null) {
                int index = UGui.getIndexRec(tab2);
                qGlasdet.set(record.getInt(0), UGui.getIndexRec(tab2), eElement.typset);
                ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
                UGui.setSelectedIndex(tab2, index);
            }
        };

        listenerThicknes = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            if (tab2.getBorder() != null) {
                int index = UGui.getIndexRec(tab2);
                Object depth = record.get(eArtikl.depth);
                qGlasdet.set(depth, UGui.getIndexRec(tab2), eGlasdet.depth);
                ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
                UGui.setSelectedIndex(tab2, index);
            }
        };

        listenerPar1 = (record) -> {
            UGui.cellParamNameOrValue(record, tab3, eElempar1.groups_id, eElempar1.text);
        };

        listenerPar2 = (record) -> {
            UGui.cellParamNameOrValue(record, tab4, eGlaspar2.groups_id, eGlaspar2.text);
        };
    }

    public void deteilFind(int deteilID) {
        Query qGlasdet2 = new Query(eGlasdet.values(), eArtikl.values());
        for (int index = 0; index < qGlasgrp.size(); index++) {
            int glasgrpID = qGlasgrp.get(index).getInt(eGlasgrp.id);
            qGlasdet2.sql(eGlasdet.data(), eGlasdet.glasgrp_id, glasgrpID).sort(eGlasdet.depth);
            qGlasdet2.table(eArtikl.up).join(qGlasdet2, eArtikl.data(), eGlasdet.artikl_id, eArtikl.id);
            for (int index2 = 0; index2 < qGlasdet2.size(); index2++) {
                if (qGlasdet2.get(index2).getInt(eGlasdet.id) == deteilID) {
                    UGui.setSelectedIndex(tab1, index);
                    UGui.scrollRectToRow(index, tab1);
                    UGui.setSelectedIndex(tab2, index2);
                    UGui.scrollRectToRow(index2, tab2);
                    break;
                }
            }
        }
    }

    public void profileFind(int... deteilID) {

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
        btnFind1 = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        tabb1 = new javax.swing.JTabbedPane();
        pan4 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        pan3 = new javax.swing.JPanel();
        scr5 = new javax.swing.JScrollPane();
        tab5 = new javax.swing.JTable();
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
        setTitle("Заполнения");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(900, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Fillings.this.windowClosed(evt);
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
                btnClose(evt);
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

        btnFind1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c090.gif"))); // NOI18N
        btnFind1.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btnFind1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFind1.setFocusable(false);
        btnFind1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFind1.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFind1.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFind1.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFind1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnFind1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFind1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFind1(evt);
            }
        });

        btnTest.setText("Test");
        btnTest.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTest.setMaximumSize(new java.awt.Dimension(25, 25));
        btnTest.setMinimumSize(new java.awt.Dimension(25, 25));
        btnTest.setPreferredSize(new java.awt.Dimension(25, 25));
        btnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTest(evt);
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
                .addComponent(btnFind1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 577, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, northLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnClose, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFind1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(900, 500));
        centr.setLayout(new javax.swing.BoxLayout(centr, javax.swing.BoxLayout.PAGE_AXIS));

        pan1.setMaximumSize(new java.awt.Dimension(2000, 800));
        pan1.setPreferredSize(new java.awt.Dimension(1000, 300));
        pan1.setLayout(new javax.swing.BoxLayout(pan1, javax.swing.BoxLayout.LINE_AXIS));

        scr1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Список групп заполнений", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr1.setMaximumSize(new java.awt.Dimension(2000, 32767));
        scr1.setPreferredSize(new java.awt.Dimension(600, 200));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Название", "Зазор между фальцем и стеклопакетом ", "Толщины доступные", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, false
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
            tab1.getColumnModel().getColumn(0).setPreferredWidth(700);
            tab1.getColumnModel().getColumn(0).setMaxWidth(1600);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(1).setMaxWidth(260);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(120);
            tab1.getColumnModel().getColumn(2).setMaxWidth(340);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(3).setMaxWidth(60);
        }

        pan1.add(scr1);

        scr3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Параметры", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr3.setMaximumSize(new java.awt.Dimension(600, 32767));
        scr3.setPreferredSize(new java.awt.Dimension(260, 200));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Параметр", "Значение"
            }
        ));
        tab3.setFillsViewportHeight(true);
        tab3.setName("tab3"); // NOI18N
        tab3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(220);
            tab3.getColumnModel().getColumn(1).setPreferredWidth(80);
        }

        pan1.add(scr3);

        centr.add(pan1);

        tabb1.setToolTipText("");
        tabb1.setFont(frames.UGui.getFont(1,0));
        tabb1.setMaximumSize(new java.awt.Dimension(2000, 800));
        tabb1.setPreferredSize(new java.awt.Dimension(900, 300));

        pan4.setPreferredSize(new java.awt.Dimension(850, 300));
        pan4.setLayout(new javax.swing.BoxLayout(pan4, javax.swing.BoxLayout.LINE_AXIS));

        scr2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Детализация групп заполнений", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr2.setMaximumSize(new java.awt.Dimension(2000, 32767));
        scr2.setPreferredSize(new java.awt.Dimension(600, 300));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Толщина", "Артикул", "Название", "Текстура", "Основная", "Внутренняя", "Внешняя", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
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
            tab2.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab2.getColumnModel().getColumn(0).setMaxWidth(180);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(130);
            tab2.getColumnModel().getColumn(1).setMaxWidth(600);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(320);
            tab2.getColumnModel().getColumn(2).setMaxWidth(1600);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(120);
            tab2.getColumnModel().getColumn(3).setMaxWidth(300);
            tab2.getColumnModel().getColumn(4).setPreferredWidth(110);
            tab2.getColumnModel().getColumn(4).setMaxWidth(300);
            tab2.getColumnModel().getColumn(5).setPreferredWidth(110);
            tab2.getColumnModel().getColumn(5).setMaxWidth(300);
            tab2.getColumnModel().getColumn(6).setPreferredWidth(110);
            tab2.getColumnModel().getColumn(6).setMaxWidth(300);
            tab2.getColumnModel().getColumn(7).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(7).setMaxWidth(60);
        }

        pan4.add(scr2);

        scr4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Параметры", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr4.setMaximumSize(new java.awt.Dimension(600, 32767));
        scr4.setPreferredSize(new java.awt.Dimension(260, 300));

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Параметр", "Значение"
            }
        ));
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
            tab4.getColumnModel().getColumn(0).setPreferredWidth(220);
            tab4.getColumnModel().getColumn(1).setPreferredWidth(80);
        }

        pan4.add(scr4);

        tabb1.addTab("Детализация", pan4);

        pan3.setPreferredSize(new java.awt.Dimension(860, 304));
        pan3.setLayout(new java.awt.BorderLayout());

        scr5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Профили в группе заполнения", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr5.setPreferredSize(new java.awt.Dimension(454, 304));

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Артикул", "Название", "Размер от оси до стеклопакета", "Внутреннее", "Внешнее", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, false
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
        tab5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr5.setViewportView(tab5);
        if (tab5.getColumnModel().getColumnCount() > 0) {
            tab5.getColumnModel().getColumn(0).setPreferredWidth(160);
            tab5.getColumnModel().getColumn(0).setMaxWidth(400);
            tab5.getColumnModel().getColumn(1).setPreferredWidth(300);
            tab5.getColumnModel().getColumn(1).setMaxWidth(1600);
            tab5.getColumnModel().getColumn(2).setPreferredWidth(80);
            tab5.getColumnModel().getColumn(2).setMaxWidth(210);
            tab5.getColumnModel().getColumn(3).setPreferredWidth(80);
            tab5.getColumnModel().getColumn(3).setMaxWidth(120);
            tab5.getColumnModel().getColumn(4).setPreferredWidth(80);
            tab5.getColumnModel().getColumn(4).setMaxWidth(120);
            tab5.getColumnModel().getColumn(5).setPreferredWidth(40);
            tab5.getColumnModel().getColumn(5).setMaxWidth(80);
        }

        pan3.add(scr5, java.awt.BorderLayout.CENTER);

        tabb1.addTab("Профили в группе", pan3);

        centr.add(tabb1);
        tabb1.getAccessibleContext().setAccessibleName("");

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(900, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete

        if (tab1.getBorder() != null) {
            if (UGui.isDeleteRecord(tab1, this, tab2, tab3, tab5) == 0) {
                UGui.deleteRecord(tab1);
            }
        } else if (tab2.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2, this, tab4) == 0) {
                UGui.deleteRecord(tab2);
            }
        } else if (tab3.getBorder() != null) {
            if (UGui.isDeleteRecord(tab3, this) == 0) {
                UGui.deleteRecord(tab3);
            }
        } else if (tab4.getBorder() != null) {
            if (UGui.isDeleteRecord(tab4, this) == 0) {
                UGui.deleteRecord(tab4);
            }
        } else if (tab5.getBorder() != null) {
            if (UGui.isDeleteRecord(tab5, this) == 0) {
                UGui.deleteRecord(tab5);
            }
        }
    }//GEN-LAST:event_btnDelete

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert

        if (tab1.getBorder() != null) {
            UGui.insertRecordCur(tab1, eGlasgrp.up, (record) -> {
                record.set(eGlasgrp.gap, 0);
            });

        } else if (tab2.getBorder() != null) {
            UGui.insertRecordCur(tab2, eGlasdet.up, (record) -> {
                int id = qGlasgrp.getAs(UGui.getIndexRec(tab1), eGlasgrp.id);
                record.set(eGlasdet.depth, 0);
                record.set(eGlasdet.glasgrp_id, id);
                int index = UGui.getIndexFind(tab2, eGlasdet.id, record.get(eGlasdet.id));
                qGlasdet.table(eArtikl.up).add(index, eArtikl.up.newRecord(Query.SEL));
            });

        } else if (tab3.getBorder() != null) {
            UGui.insertRecordCur(tab3, eGlaspar1.up, (record) -> {
                int id = qGlasgrp.getAs(UGui.getIndexRec(tab1), eGlasgrp.id);
                record.set(eGlaspar1.glasgrp_id, id);
            });
            DefCellEditorBtn defCellEditorBtn = (DefCellEditorBtn) tab3.getColumnModel().getColumn(0).getCellEditor();
            defCellEditorBtn.getButton().getActionListeners()[0].actionPerformed(null);

        } else if (tab4.getBorder() != null) {
            UGui.insertRecordCur(tab4, eGlaspar2.up, (record) -> {
                int id = qGlasdet.getAs(UGui.getIndexRec(tab2), eGlasdet.id);
                record.set(eGlaspar2.glasdet_id, id);
            });
            DefCellEditorBtn defCellEditorBtn = (DefCellEditorBtn) tab4.getColumnModel().getColumn(0).getCellEditor();
            defCellEditorBtn.getButton().getActionListeners()[0].actionPerformed(null);

        } else if (tab5.getBorder() != null) {
            UGui.insertRecordCur(tab5, eGlasprof.up, (record) -> {
                int id = qGlasgrp.getAs(UGui.getIndexRec(tab1), eGlasgrp.id);
                record.set(eGlasprof.glasgrp_id, id);
                record.set(eGlasprof.gsize, .0);
                record.set(eGlasprof.inside, 1);
                record.set(eGlasprof.outside, 1);
                int index = UGui.getIndexFind(tab5, eGlasprof.id, record.get(eGlasprof.id));
                qGlasprof.table(eArtikl.up).add(index, eArtikl.up.newRecord(Query.SEL));
            });
        }
    }//GEN-LAST:event_btnInsert

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());  
    }//GEN-LAST:event_windowClosed

    private void btnFind1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind1
        if (tab2.getBorder() != null) {
            Record record = ((DefTableModel) tab2.getModel()).getQuery().get(UGui.getIndexRec(tab2));
            Record record2 = eArtikl.find(record.getInt(eElemdet.artikl_id), false);
            ProgressBar.create(this, new ListenerFrame() {
                public void actionRequest(Object obj) {
                    App.Artikles.createFrame(Fillings.this, record2);
                }
            });
        } else if (tab5.getBorder() != null) {
            Record record = ((DefTableModel) tab5.getModel()).getQuery().get(UGui.getIndexRec(tab5));
            Record record2 = eArtikl.find(record.getInt(eGlasprof.artikl_id), false);
            ProgressBar.create(this, new ListenerFrame() {
                public void actionRequest(Object obj) {
                    App.Artikles.createFrame(Fillings.this, record2);
                }
            });
        }
    }//GEN-LAST:event_btnFind1

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest
        deteilFind(1657);
    }//GEN-LAST:event_btnTest

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        RTable.load("Р—Р°РїРѕР»РЅРµРЅРёСЏ", tab1);
        ExecuteCmd.documentType(this);
    }//GEN-LAST:event_btnReport

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab1, tab2, tab3, tab4, tab5).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab1, tab2, tab3, tab4).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());

        } else if (evt.getButton() == MouseEvent.BUTTON1) {
            JTable table = (JTable) evt.getSource();
            UGui.updateBorderAndSql(table, List.of(tab1, tab2, tab3, tab4, tab5));

            btnFind1.setEnabled(false);
            if (tab1.getBorder() != null) {
                btnFind1.setEnabled(true);
            } else if (tab2.getBorder() != null) {
                btnFind1.setEnabled(true);
            } else if (tab5.getBorder() != null) {
                btnFind1.setEnabled(true);
            }
        }

    }//GEN-LAST:event_tabMouseClicked
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnFind1;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnTest;
    private javax.swing.JPanel centr;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JTable tab5;
    private javax.swing.JTabbedPane tabb1;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    private void initElements() {

        ePref.get(this, btnClose, (e) -> {
            ePref.put(this, btnClose);
        });        

        TableFieldFilter filterTable = new TableFieldFilter(0, tab1, tab2, tab3, tab4, tab5);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        List.of(btnIns, btnDel).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5)));
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
        tabb1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                if (tabb1.getSelectedIndex() == 0) {
                    UGui.updateBorderAndSql(tab2, List.of(tab1, tab2, tab3, tab4, tab5));
                } else if (tabb1.getSelectedIndex() == 1) {
                    UGui.updateBorderAndSql(tab5, List.of(tab1, tab2, tab3, tab4, tab5));
                }
            }
        });
    }
}
