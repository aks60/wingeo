package frames;

import frames.swing.ProgressBar;
import frames.swing.FrameToFile;
import builder.making.UColor;
import builder.model1.ElemJoining;
import frames.dialog.ParName;
import frames.dialog.ParSysVal;
import frames.dialog.ParColor;
import frames.dialog.ParUserVal;
import frames.dialog.DicArtikl;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eArtikl;
import domain.eColor;
import domain.eGroups;
import domain.eJoindet;
import domain.eJoining;
import domain.eJoinpar1;
import domain.eJoinpar2;
import domain.eParams;
import enums.Enam;
import builder.param.ParamList;
import common.eProp;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import frames.swing.DefTableModel;
import frames.dialog.DicJoinvar;
import domain.eJoinvar;
import enums.TypeGrup;
import frames.swing.DefCellRendererBool;
import frames.dialog.DicColvar;
import enums.UseColor;
import java.awt.Component;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import startup.App;
import common.listener.ListenerRecord;
import common.listener.ListenerFrame;
import dataset.Conn;
import domain.eArtdet;
import domain.eGlaspar2;
import domain.eParmap;
import domain.ePrjkit;
import frames.dialog.DicArtikl2;
import frames.swing.DefCellEditorBtn;
import frames.swing.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import report.ExecuteCmd;
import report.HtmlOfTable;

//Варианты соединений
public class Joinings extends javax.swing.JFrame {

    private Query qGroups = new Query(eGroups.values());
    private Query qColor = new Query(eColor.id, eColor.groups_id, eColor.name);
    private Query qArtikl = new Query(eArtikl.values());
    private Query qJoining = new Query(eJoining.values());
    private Query qJoinvar = new Query(eJoinvar.values());
    private Query qJoindet = new Query(eJoindet.values());
    private Query qJoinpar1 = new Query(eJoinpar1.values());
    private Query qJoinpar2 = new Query(eJoinpar2.values());
    private String subsql = "(-1)";
    private ListenerRecord listenerArtikl, listenerJoinvar, listenerColor, listenerColvar1, listenerColvar2, listenerColvar3;

    //Запуск из Tex (главное меню)
    public Joinings() {
        this.subsql = null;
        initComponents();
        initElements();
        loadingData();
        loadingModel();
        listenerSet();
        listenerAdd();
    }

    //Запуск из Specific
    public Joinings(ElemJoining join) {
        this.subsql = "(" + join.joiningRec.getStr(1) + ")";
        initComponents();
        initElements();
        loadingData();
        loadingModel();
        listenerSet();
        listenerAdd();
        for (int index = 0; index < qJoinvar.size() - 1; ++index) {
            if (qJoinvar.get(index).getInt(1) == join.joinvarRec.getInt(1)) {
                UGui.setSelectedIndex(tab2, index);
            }
        }
    }

    //Запуск из Systree
    public Joinings(int deteilID) {
        initComponents();
        initElements();
        loadingData();
        loadingModel();
        listenerSet();
        listenerAdd();
        deteilFind(deteilID);
    }

    public void loadingData() {

        tab1.setToolTipText("");
        qGroups.select(eGroups.up, "where", eGroups.grup, "in (", TypeGrup.PARAM_USER.id, ",", TypeGrup.COLOR_MAP.id, ") order by", eGroups.npp, ",", eGroups.name);
        qColor.select(eColor.up);
        qArtikl.select(eArtikl.up);
        if (subsql == null) {
            qJoining.select(eJoining.up, "order by", eJoining.name);
        } else {
            qJoining.select(eJoining.up, "where", eJoining.id, "in", subsql, "order by", eJoining.name);
        }
    }

    public void loadingModel() {
        new DefTableModel(tab1, qJoining, eJoining.artikl_id1, eJoining.artikl_id2, eJoining.name, eJoining.is_main, eJoining.analog) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (eJoining.artikl_id1 == field) {
                    return qArtikl.stream().filter(rec -> rec.get(eArtikl.id).equals(val)).findFirst().orElse(eArtikl.up.newRecord()).get(eArtikl.code);

                } else if (eJoining.artikl_id2 == field) {
                    return qArtikl.stream().filter(rec -> rec.get(eArtikl.id).equals(val)).findFirst().orElse(eArtikl.up.newRecord()).get(eArtikl.code);
                }
                return val;
            }
        };
        new DefTableModel(tab2, qJoinvar, eJoinvar.prio, eJoinvar.name, eJoinvar.mirr);
        new DefTableModel(tab3, qJoinpar1, eJoinpar1.groups_id, eJoinpar1.text) {

            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (columnIndex == 1) {
                    setValueAt(aValue, rowIndex, eJoinpar1.text);
                }
            }

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && eJoinpar1.groups_id == field) {

                    if (Integer.valueOf(String.valueOf(val)) < 0) {
                        return qGroups.find(val, eGroups.id).getDev(eGroups.name, val);
                    } else {
                        Enam en = ParamList.find(val);
                        return Record.getDev(en.numb(), en.text());
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab4, qJoindet, eJoindet.artikl_id, eJoindet.artikl_id, eJoindet.color_fk, eJoindet.color_us, eJoindet.color_us, eJoindet.color_us) {

            public Object getValueAt(int col, int row, Object val) {
                if (val != null) {
                    Field field = columns[col];
                    if (eJoindet.artikl_id == field) {
                        int id = Integer.valueOf(val.toString());
                        Record recordArt = qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == id).findFirst().orElse(eArtikl.up.newRecord());
                        if (col == 0) {
                            return recordArt.getStr(eArtikl.code);
                        } else if (col == 1) {
                            return recordArt.getStr(eArtikl.name);
                        }
                    } else if (eJoindet.color_fk == field) {
                        int colorFk = Integer.valueOf(val.toString());

                        if (UseColor.automatic[0].equals(colorFk)) {
                            return UseColor.automatic[1];

                        } else if (UseColor.precision[0].equals(colorFk)) {
                            return UseColor.precision[1];
                        }
                        if (colorFk > 0) {
                            return qColor.stream().filter(rec -> rec.getInt(eColor.id) == colorFk).findFirst().orElse(eColor.up.newRecord()).get(eColor.name);
                        } else {
                            return "# " + qGroups.stream().filter(rec -> rec.getInt(eGroups.id) == -1 * colorFk).findFirst().orElse(eGroups.up.newRecord()).get(eGroups.name);
                        }
                    } else if (eJoindet.color_us == field) {
                        int types = Integer.valueOf(val.toString());
                        types = (col == 3) ? types & 0x0000000f : (col == 4) ? (types & 0x000000f0) >> 4 : (types & 0x00000f00) >> 8;
                        return UseColor.MANUAL.find(types).text();
                    }
                    return val;
                }
                return val;
            }
        };
        new DefTableModel(tab5, qJoinpar2, eJoinpar2.groups_id, eJoinpar2.text) {

            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (columnIndex == 1) {
                    setValueAt(aValue, rowIndex, eJoinpar2.text);
                }
            }

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && eJoinpar2.groups_id == field) {

                    if (Integer.valueOf(String.valueOf(val)) < 0) {
                        return qGroups.find(val, eGroups.id).getDev(eGroups.name, val);
                    } else {
                        Enam en = ParamList.find(val);
                        return Record.getDev(en.numb(), en.text());
                    }
                }
                return val;
            }
        };

        tab1.getColumnModel().getColumn(3).setCellRenderer(new DefCellRendererBool());
        tab2.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 0) {
                    int types = qJoinvar.get(row).getInt(eJoinvar.types);
                    JLabel label = (JLabel) comp;
                    label.setIcon(UColor.iconFromTypeJoin(types));
                }
                return comp;
            }
        });
        tab2.getColumnModel().getColumn(2).setCellRenderer(new DefCellRendererBool());

        UGui.setSelectedRow(tab1);
    }

    public void selectionTab1(ListSelectionEvent event) {
        UGui.clearTable(tab2, tab3, tab4, tab5);
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record record = qJoining.table(eJoining.up).get(index);
            Integer id = record.getInt(eJoining.id);
            qJoinvar.select(eJoinvar.up, "where", eJoinvar.joining_id, "=", id, "order by", eJoinvar.prio);
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab2);
        }
    }

    public void selectionTab2(ListSelectionEvent event) {
        UGui.clearTable(tab3, tab4, tab5);
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            Record record = qJoinvar.table(eJoinvar.up).get(index);
            Integer id = record.getInt(eJoinvar.id);
            qJoindet.select(eJoindet.up, "where", eJoindet.joinvar_id, "=", id, "order by", eJoindet.artikl_id);
            qJoinpar1.select(eJoinpar1.up, "where", eJoinpar1.joinvar_id, "=", id, "order by", eJoinpar1.id);
            ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
            ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab3);
            UGui.setSelectedRow(tab4);
        }
    }

    public void selectionTab4(ListSelectionEvent event) {
        int index = UGui.getIndexRec(tab4);
        if (index != -1) {
            Record record = qJoindet.table(eJoindet.up).get(index);
            Integer id = record.getInt(eJoindet.id);
            qJoinpar2.select(eJoinpar2.up, "where", eJoinpar2.joindet_id, "=", id, "order by", eJoinpar2.id);
            ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab5);
        }
    }

    public void listenerAdd() {
        UGui.buttonCellEditor(tab1, 0).addActionListener(event -> {
            new DicArtikl(this, listenerArtikl, false, 1);
        });

        UGui.buttonCellEditor(tab1, 1).addActionListener(event -> {
            new DicArtikl(this, listenerArtikl, false, 1);
        });

        UGui.buttonCellEditor(tab2, 1).addActionListener(event -> {
            new DicJoinvar(this, listenerJoinvar);
        });

        UGui.buttonCellEditor(tab3, 0).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab2);
            if (index != -1) {
                Record record = qJoinvar.get(index);
                int joinVar = record.getInt(eJoinvar.types) / 10;

                if (qJoinpar1.get(UGui.getIndexRec(tab3), eJoinpar1.groups_id) == null) {
                    new ParName(this, (rec) -> {
                        UGui.cellParamNameOrValue(rec, tab3, eJoinpar1.groups_id, eJoinpar1.text);
                    }, eParams.joint, joinVar * 1000);
                } else {
                    int groupsID = qJoinpar1.getAs(UGui.getIndexRec(tab3), eJoinpar1.groups_id);
                    new ParName(this, groupsID, (rec) -> {
                        UGui.cellParamNameOrValue(rec, tab3, eJoinpar1.groups_id, eJoinpar1.text);
                    }, eParams.joint, joinVar * 1000);
                }
            }
        });

        UGui.buttonCellEditor(tab3, 1, (componentCell) -> { //слушатель редактирование типа, вида данных и вида ячейки таблицы
            return UGui.cellParamTypeOrVid(tab3, componentCell, eJoinpar1.groups_id);

        }).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            Record record = qJoinpar1.get(UGui.getIndexRec(tab3));
            int grup = record.getInt(eJoinpar1.groups_id);
            if (grup < 0) {
                new ParUserVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab3, eJoinpar1.groups_id, eJoinpar1.text);
                }, eParams.joint, grup);
            } else {
                List list = ParamList.find(grup).dict();
                new ParSysVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab3, eJoinpar1.groups_id, eJoinpar1.text);
                }, list);
            }
        });

        UGui.buttonCellEditor(tab4, 0).addActionListener(event -> {
            Record joindetRec = qJoindet.get(UGui.getIndexRec(tab4));
            int id = joindetRec.getInt(eJoindet.artikl_id, -1);
            new DicArtikl2(this, id, listenerArtikl, 1, 2, 3, 4);
        });

        UGui.buttonCellEditor(tab4, 1).addActionListener(event -> {
            Record joindetRec = qJoindet.get(UGui.getIndexRec(tab4));
            int id = joindetRec.getInt(eJoindet.artikl_id, -1);
            new DicArtikl2(this, id, listenerArtikl, 1, 2, 3, 4);
        });

        UGui.buttonCellEditor(tab4, 2).addActionListener(event -> {
            Record record = qJoindet.get(UGui.getIndexRec(tab4));
            int artiklID = record.getInt(eJoindet.artikl_id);
            int colorFK = record.getInt(eJoindet.color_fk, -1);
            new ParColor(this, listenerColor, eParmap.joint, artiklID, colorFK);
        });

        UGui.buttonCellEditor(tab4, 3).addActionListener(event -> {
            Record record = qJoindet.get(UGui.getIndexRec(tab4));
            int colorFk = record.getInt(eJoindet.color_fk);
            new DicColvar(this, listenerColvar1, colorFk);
        });

        UGui.buttonCellEditor(tab4, 4).addActionListener(event -> {
            Record record = qJoindet.get(UGui.getIndexRec(tab4));
            int colorFk = record.getInt(eJoindet.color_fk);
            new DicColvar(this, listenerColvar2, colorFk);
        });

        UGui.buttonCellEditor(tab4, 5).addActionListener(event -> {
            Record record = qJoindet.get(UGui.getIndexRec(tab4));
            int colorFk = record.getInt(eJoindet.color_fk);
            new DicColvar(this, listenerColvar3, colorFk);
        });

        UGui.buttonCellEditor(tab5, 0).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab4);
            if (index != -1) {
                Record recordJoin = qJoindet.get(index);
                int artikl_id = recordJoin.getInt(eJoindet.artikl_id);
                Record recordArt = eArtikl.find(artikl_id, false);
                int level = recordArt.getInt(eArtikl.level1);
                Integer[] part = {0, 12000, 11000, 12000, 11000, 0};

                if (qJoinpar2.get(UGui.getIndexRec(tab5), eJoinpar2.groups_id) == null) {
                    new ParName(this, (record) -> {
                        UGui.cellParamNameOrValue(record, tab5, eJoinpar2.groups_id, eJoinpar2.text);
                    }, eParams.joint, part[level]);
                } else {
                    int groupsID = qJoinpar2.getAs(UGui.getIndexRec(tab5), eJoinpar2.groups_id);
                    new ParName(this, groupsID, (record) -> {
                        UGui.cellParamNameOrValue(record, tab5, eJoinpar2.groups_id, eJoinpar2.text);
                    }, eParams.joint, part[level]);
                }
            }
        });

        UGui.buttonCellEditor(tab5, 1, (componentCell) -> { //слушатель редактирование типа, вида данных и вида ячейки таблицы
            return UGui.cellParamTypeOrVid(tab5, componentCell, eJoinpar2.groups_id);

        }).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            Record record = qJoinpar2.get(UGui.getIndexRec(tab5));
            int grup = record.getInt(eJoinpar2.groups_id);
            if (grup < 0) {
                new ParUserVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab5, eJoinpar2.groups_id, eJoinpar2.text);
                }, eParams.joint, grup);
            } else {
                List list = ParamList.find(grup).dict();
                new ParSysVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab5, eJoinpar2.groups_id, eJoinpar2.text);
                }, list);
            }
        });
    }

    public void listenerSet() {

        listenerArtikl = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            if (qArtikl.find(record.get(eArtikl.id), eArtikl.id).get(eArtikl.id) == null) {
                qArtikl.select(eArtikl.up);
            }
            if (tab1.getBorder() != null) {
                Record joiningRec = qJoining.get(UGui.getIndexRec(tab1));
                if (tab1.getSelectedColumn() == 0) {
                    joiningRec.set(eJoining.artikl_id1, record.getInt(eArtikl.id));
                } else if (tab1.getSelectedColumn() == 1) {
                    joiningRec.set(eJoining.artikl_id2, record.getInt(eArtikl.id));
                }

            } else if (tab4.getBorder() != null) {
                int index = UGui.getIndexRec(tab4);
                Record joindetRec = qJoindet.get(UGui.getIndexRec(tab4));
                joindetRec.set(eJoindet.artikl_id, record.getInt(eArtikl.id));
                joindetRec.set(eJoindet.color_fk, null);
                int artiklID = record.getInt(eArtikl.id);
                List<Record> artdetList = eArtdet.filter2(artiklID);
                
                if (artdetList.size() == 1) {
                    if (artdetList.get(0).getInt(eArtdet.color_fk) > 0) {
                        Record colorRec = eColor.find(artdetList.get(0).getInt(eArtdet.color_fk));
                        listenerColor.action(colorRec);
                    }
                }
                ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
                UGui.setSelectedIndex(tab4, index);
            }
        };
        
        listenerColor = (record) -> {
            UGui.cellParamColor(record, tab4, eJoindet.color_fk, eJoindet.color_us, tab1, tab2, tab3, tab4, tab5);
        };

        listenerColvar1 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab4);
            Record joindetRec = qJoindet.get(UGui.getIndexRec(tab4));
            int types = (joindetRec.getInt(eJoindet.color_us) == -1) ? 0 : joindetRec.getInt(eJoindet.color_us);
            types = (types & 0xfffffff0) + record.getInt(0);
            joindetRec.set(eJoindet.color_us, types);
            ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab4, index);
        };

        listenerColvar2 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab4);
            Record joindetRec = qJoindet.get(UGui.getIndexRec(tab4));
            int types = (joindetRec.getInt(eJoindet.color_us) == -1) ? 0 : joindetRec.getInt(eJoindet.color_us);
            types = (types & 0xffffff0f) + (record.getInt(0) << 4);
            joindetRec.set(eJoindet.color_us, types);
            ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab4, index);
        };

        listenerColvar3 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab4);
            Record joindetRec = qJoindet.get(UGui.getIndexRec(tab4));
            int types = (joindetRec.getInt(eJoindet.color_us) == -1) ? 0 : joindetRec.getInt(eJoindet.color_us);
            types = (types & 0xfffff0ff) + (record.getInt(0) << 8);
            joindetRec.set(eJoindet.color_us, types);
            ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab4, index);
        };

        listenerJoinvar = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab2);
            Record joinvarRec = qJoinvar.get(UGui.getIndexRec(tab2));
            joinvarRec.set(eJoinvar.name, record.getStr(0));
            joinvarRec.set(eJoinvar.types, record.getInt(1));
            if (joinvarRec.get(eJoinvar.prio) == null) {
                int max = 0;
                for (Record rec : qJoinvar) {
                    max = (max < rec.getInt(eJoinvar.prio)) ? rec.getInt(eJoinvar.prio) : max;
                }
                joinvarRec.set(eJoinvar.prio, ++max);
            }
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab2, index);
        };
    }

    public void deteilFind(int deteilID) {
        try {
            Query qVar = new Query(eJoinvar.values());
            Query qDet = new Query(eJoindet.values(), eArtikl.values());
            for (int index = 0; index < qJoining.size(); index++) {
                int joining_id = qJoining.get(index).getInt(eJoining.id);
                qVar.select(eJoinvar.up, "where", eJoinvar.joining_id, "=", joining_id, "order by", eJoinvar.prio);
                for (int index2 = 0; index2 < qVar.size(); index2++) {
                    int joinvar_id = qVar.get(index2).getInt(eJoining.id);
                    qDet.select(eJoindet.up, "left join", eArtikl.up, "on", eArtikl.id, "=", eJoindet.artikl_id, "where", eJoindet.joinvar_id, "=", joinvar_id, "order by", eJoindet.artikl_id);
                    for (int index3 = 0; index3 < qDet.size(); index3++) {
                        if (qDet.get(index3).getInt(eJoindet.id) == deteilID) {

                            UGui.setSelectedIndex(tab1, index);
                            UGui.scrollRectToIndex(index, tab1);
                            UGui.setSelectedIndex(tab2, index2);
                            UGui.scrollRectToIndex(index2, tab2);
                            UGui.setSelectedIndex(tab4, index3);
                            UGui.scrollRectToIndex(index3, tab3);
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Joinings.deteilFind() " + e);
        }
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
        btnReport = new javax.swing.JButton();
        btnConstructiv = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnClone = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        pan2 = new javax.swing.JPanel();
        pan4 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan1 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        pan5 = new javax.swing.JPanel();
        pan3 = new javax.swing.JPanel();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
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
        setTitle("Соединения");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Joinings.this.windowClosed(evt);
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

        btnConstructiv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c090.gif"))); // NOI18N
        btnConstructiv.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
        btnConstructiv.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnConstructiv.setFocusable(false);
        btnConstructiv.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConstructiv.setMaximumSize(new java.awt.Dimension(25, 25));
        btnConstructiv.setMinimumSize(new java.awt.Dimension(25, 25));
        btnConstructiv.setPreferredSize(new java.awt.Dimension(25, 25));
        btnConstructiv.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnConstructiv.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConstructiv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConstructiv(evt);
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

        btnClone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c065.gif"))); // NOI18N
        btnClone.setToolTipText(bundle.getString("Клонировать запись")); // NOI18N
        btnClone.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClone.setFocusable(false);
        btnClone.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClone.setMaximumSize(new java.awt.Dimension(25, 25));
        btnClone.setMinimumSize(new java.awt.Dimension(25, 25));
        btnClone.setPreferredSize(new java.awt.Dimension(25, 25));
        btnClone.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnClone.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClone(evt);
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
                .addComponent(btnClone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConstructiv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 511, Short.MAX_VALUE)
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
                    .addGroup(northLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnClose, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRef, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConstructiv, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnClone, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(800, 500));
        centr.setLayout(new javax.swing.BoxLayout(centr, javax.swing.BoxLayout.PAGE_AXIS));

        pan2.setMaximumSize(new java.awt.Dimension(2000, 800));
        pan2.setPreferredSize(new java.awt.Dimension(800, 400));
        pan2.setLayout(new javax.swing.BoxLayout(pan2, javax.swing.BoxLayout.LINE_AXIS));

        pan4.setMaximumSize(new java.awt.Dimension(2000, 800));
        pan4.setPreferredSize(new java.awt.Dimension(600, 300));
        pan4.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Списки соединений", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"11", "11", "1111", null, null, null},
                {"22", "22", "2222", null, null, null}
            },
            new String [] {
                "Артикул 1", "Артикул 2", "Название", "Основной", "Аналог", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Integer.class
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
        tab1.setFillsViewportHeight(true);
        tab1.setName("tab1"); // NOI18N
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Joinings.this.mousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(120);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(120);
            tab1.getColumnModel().getColumn(2).setMinWidth(100);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(360);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(68);
            tab1.getColumnModel().getColumn(4).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(5).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(5).setMaxWidth(60);
        }

        pan4.add(scr1, java.awt.BorderLayout.CENTER);

        pan2.add(pan4);

        pan1.setMaximumSize(new java.awt.Dimension(600, 800));
        pan1.setPreferredSize(new java.awt.Dimension(260, 300));
        pan1.setLayout(new javax.swing.BoxLayout(pan1, javax.swing.BoxLayout.PAGE_AXIS));

        scr2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Варианты соединений", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr2.setPreferredSize(new java.awt.Dimension(300, 234));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "Мммммммммм", null, null},
                {"2", "Ррррррррррр", null, null}
            },
            new String [] {
                "Приоритет", "Название", "Зеркальность", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Integer.class
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
        tab2.setFillsViewportHeight(true);
        tab2.setName("tab2"); // NOI18N
        tab2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Joinings.this.mousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(52);
            tab2.getColumnModel().getColumn(0).setMaxWidth(80);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(2).setMaxWidth(40);
            tab2.getColumnModel().getColumn(3).setMaxWidth(40);
        }

        pan1.add(scr2);

        scr3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Параметры", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr3.setPreferredSize(new java.awt.Dimension(300, 234));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1111", "1"},
                {"2222", "2"}
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Joinings.this.mousePressed(evt);
            }
        });
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(1).setPreferredWidth(80);
            tab3.getColumnModel().getColumn(1).setMaxWidth(160);
        }

        pan1.add(scr3);

        pan2.add(pan1);

        centr.add(pan2);

        pan5.setMaximumSize(new java.awt.Dimension(2000, 300));
        pan5.setPreferredSize(new java.awt.Dimension(800, 200));
        pan5.setLayout(new java.awt.BorderLayout());

        pan3.setPreferredSize(new java.awt.Dimension(654, 234));
        pan3.setLayout(new javax.swing.BoxLayout(pan3, javax.swing.BoxLayout.LINE_AXIS));

        scr4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Детализация соединений", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr4.setMaximumSize(new java.awt.Dimension(2000, 800));
        scr4.setPreferredSize(new java.awt.Dimension(600, 300));

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"yyyyyyyy", "fffffffffffffff", "44", "7", null, null, null},
                {"rrrrrrrrrrr", "llllllllllllllllllllllllllll", "77", "2", null, null, null}
            },
            new String [] {
                "Артикул", "Название", "Текстура", "Основная", "Внутренняя", "Внешняя", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, false
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Joinings.this.mousePressed(evt);
            }
        });
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(0).setPreferredWidth(120);
            tab4.getColumnModel().getColumn(1).setPreferredWidth(200);
            tab4.getColumnModel().getColumn(2).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(3).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(4).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(5).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(6).setPreferredWidth(40);
            tab4.getColumnModel().getColumn(6).setMaxWidth(60);
        }

        pan3.add(scr4);

        scr5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Параметры", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr5.setMaximumSize(new java.awt.Dimension(600, 800));
        scr5.setPreferredSize(new java.awt.Dimension(260, 300));

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"eeeeeeeeee", "22"},
                {"mmmmmmm", "44"}
            },
            new String [] {
                "Параметр", "Значение"
            }
        ));
        tab5.setFillsViewportHeight(true);
        tab5.setName("tab5"); // NOI18N
        tab5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Joinings.this.mousePressed(evt);
            }
        });
        scr5.setViewportView(tab5);
        if (tab5.getColumnModel().getColumnCount() > 0) {
            tab5.getColumnModel().getColumn(1).setPreferredWidth(80);
            tab5.getColumnModel().getColumn(1).setMaxWidth(160);
        }

        pan3.add(scr5);

        pan5.add(pan3, java.awt.BorderLayout.CENTER);

        centr.add(pan5);

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
        UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
        List.of(tab1, tab2, tab3, tab4, tab5).forEach(tab -> UGui.getQuery(tab).execsql());
        int indexTab1 = UGui.getIndexRec(tab1);
        int indexTab2 = UGui.getIndexRec(tab2);
        int indexTab4 = UGui.getIndexRec(tab4);
        loadingData();
        ((DefaultTableModel) tab1.getModel()).fireTableDataChanged();
        ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
        ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
        UGui.setSelectedIndex(tab1, indexTab1);
        UGui.setSelectedIndex(tab2, indexTab2);
        UGui.setSelectedIndex(tab4, indexTab4);
    }//GEN-LAST:event_btnRefresh

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete

        if (tab1.getBorder() != null) {
            if (UGui.isDeleteRecord(tab1, this, tab2) == 0) {
                UGui.deleteRecord(tab1);
            }
        } else if (tab2.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2, this, tab3, tab4) == 0) {
                UGui.deleteRecord(tab2);
            }
        } else if (tab3.getBorder() != null) {
            if (UGui.isDeleteRecord(tab3, this) == 0) {
                UGui.deleteRecord(tab3);
            }
        } else if (tab4.getBorder() != null) {
            if (UGui.isDeleteRecord(tab4, this, tab5) == 0) {
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
            UGui.insertRecordCur(tab1, eJoining.up, (record) -> {
                //record.setDev(eJoining.name, "Соединение-");
            });

        } else if (tab2.getBorder() != null) {
            UGui.insertRecordCur(tab2, eJoinvar.up, (record) -> {
                int id = qJoining.getAs(UGui.getIndexRec(tab1), eJoining.id);
                record.set(eJoinvar.joining_id, id);
            });

        } else if (tab3.getBorder() != null) {
            UGui.insertRecordCur(tab3, eJoinpar1.up, (record) -> {
                int id = qJoinvar.getAs(UGui.getIndexRec(tab2), eJoinvar.id);
                record.set(eJoinpar1.joinvar_id, id);
            });
            DefCellEditorBtn defCellEditorBtn = (DefCellEditorBtn) tab3.getColumnModel().getColumn(0).getCellEditor();
            defCellEditorBtn.getButton().getActionListeners()[0].actionPerformed(null);

        } else if (tab4.getBorder() != null) {
            UGui.insertRecordCur(tab4, eJoindet.up, (record) -> {
                int id = qJoinvar.getAs(UGui.getIndexRec(tab2), eJoinvar.id);
                record.set(eJoindet.joinvar_id, id);
            });

        } else if (tab5.getBorder() != null) {
            UGui.insertRecordCur(tab5, eJoinpar2.up, (record) -> {
                int id = qJoindet.getAs(UGui.getIndexRec(tab4), eJoindet.id);
                record.set(eJoinpar2.joindet_id, id);
            });
            DefCellEditorBtn defCellEditorBtn = (DefCellEditorBtn) tab5.getColumnModel().getColumn(0).getCellEditor();
            defCellEditorBtn.getButton().getActionListeners()[0].actionPerformed(null);
        }
    }//GEN-LAST:event_btnInsert

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
        List.of(tab1, tab2, tab3, tab4, tab5).forEach(tab -> UGui.getQuery(tab).execsql());
    }//GEN-LAST:event_windowClosed

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        HtmlOfTable.load("Соединения", tab1);
        ExecuteCmd.documentType(this);
    }//GEN-LAST:event_btnReport

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab1, tab2, tab3, tab4, tab5));
    }//GEN-LAST:event_mousePressed

    private void btnConstructiv(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConstructiv
        if (tab4.getBorder() != null) {
            Record record = ((DefTableModel) tab4.getModel()).getQuery().get(UGui.getIndexRec(tab4));
            Record record2 = qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == record.getInt(eJoindet.artikl_id)).findFirst().orElse(eJoindet.up.newRecord());
            ProgressBar.create(this, new ListenerFrame() {
                public void actionRequest(Object obj) {
                    App.Artikles.createFrame(Joinings.this, record2);
                }
            });
        }
    }//GEN-LAST:event_btnConstructiv

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest

    }//GEN-LAST:event_btnTest

    private void btnClone(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClone
        int index = UGui.getIndexRec(tab1);
        if (index != -1 && JOptionPane.showConfirmDialog(this, "Вы действительно хотите клонировать текущую запись?",
                "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {

            List<Record> joinvarList = new ArrayList<>();
            List<Record> joindetList = new ArrayList<>();
            Map<Record, Integer> joindetMap = new HashMap<>();
            Map<Record, Integer> joinpar1Map = new HashMap<>();
            Map<Record, Integer> joinpar2Map = new HashMap<>();
            qJoinvar.forEach(rec -> joinvarList.add(rec));

            Record joiningClon = (Record) qJoining.get(index).clone();
            joiningClon.setNo(eJoining.up, Query.INS);
            joiningClon.setNo(eJoining.id, Conn.genId(eJoining.up));
            joiningClon.setNo(eJoining.name, joiningClon.getStr(eJoining.name) + "-клон");
            qJoining.add(index, joiningClon);
            qJoining.insert(joiningClon);

            for (Record joinvarRec : joinvarList) {
                qJoinpar1.select(eJoinpar1.up, "where", eJoinpar1.joinvar_id, "=", joinvarRec.get(eJoinvar.id), "order by", eJoinpar1.id);
                qJoindet.select(eJoindet.up, "where", eJoindet.joinvar_id, "=", joinvarRec.get(eJoinvar.id), "order by", eJoindet.id);
                Record joinvarClon = (Record) joinvarRec.clone();
                joinvarClon.setNo(eJoinvar.up, Query.INS);
                joinvarClon.setNo(eJoinvar.id, Conn.genId(eJoinvar.up));
                joinvarClon.setNo(eJoinvar.joining_id, joiningClon.getInt(eJoining.id));
                qJoinpar1.forEach(rec -> joinpar2Map.put(rec, joinvarClon.getInt(eJoinvar.id)));
                qJoindet.forEach(rec -> joindetMap.put(rec, joinvarClon.getInt(eJoinvar.id)));
                qJoinvar.add(joinvarClon);
            }
            for (Map.Entry<Record, Integer> it : joinpar2Map.entrySet()) {
                Record joinpar1Rec = it.getKey();
                Record joinpar1Clon = (Record) joinpar1Rec.clone();
                joinpar1Clon.setNo(eJoinpar1.up, Query.INS);
                joinpar1Clon.setNo(eJoinpar1.id, Conn.genId(eJoinpar1.up));
                joinpar1Clon.setNo(eJoinpar1.joinvar_id, it.getValue());
                qJoinpar1.add(joinpar1Clon);
            }
            for (Map.Entry<Record, Integer> it : joindetMap.entrySet()) {
                Record joindetRec = it.getKey();
                qJoinpar2.select(eJoinpar2.up, "where", eJoinpar2.joindet_id, "=", joindetRec.get(eJoindet.id), "order by", eJoinpar2.id);
                Record joindetClon = (Record) joindetRec.clone();
                joindetClon.setNo(eJoindet.up, Query.INS);
                joindetClon.setNo(eJoindet.id, Conn.genId(eJoindet.up));
                joindetClon.setNo(eJoindet.joinvar_id, it.getValue());
                qJoinpar2.forEach(rec -> joinpar2Map.put(rec, joindetClon.getInt(eJoindet.id)));
                qJoindet.add(joindetClon);
            }
            for (Map.Entry<Record, Integer> it : joinpar2Map.entrySet()) {
                Record joinpar2Rec = it.getKey();
                Record joinpar2Clon = (Record) joinpar2Rec.clone();
                joinpar2Clon.setNo(eJoinpar2.up, Query.INS);
                joinpar2Clon.setNo(eJoinpar2.id, Conn.genId(eJoinpar2.up));
                joinpar2Clon.setNo(eJoinpar2.joindet_id, it.getValue());
                qJoinpar2.add(joinpar2Clon);
            }
            List.of(qJoinvar, qJoindet, qJoinpar1, qJoinpar2).forEach(q -> q.execsql());
            ((DefaultTableModel) tab1.getModel()).fireTableRowsInserted(index, index);
            UGui.setSelectedIndex(tab1, index);
            UGui.scrollRectToIndex(index, tab1);
            UGui.setSelectedRow(tab2);
        }
    }//GEN-LAST:event_btnClone

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
            List.of(tab1, tab2, tab3, tab4, tab5).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked

// <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClone;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnConstructiv;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnRef;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnTest;
    private javax.swing.JPanel centr;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
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
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    private void initElements() {

        new FrameToFile(this, btnClose);
        new UColor();

        TableFieldFilter filterTable = new TableFieldFilter(0, tab1, tab2, tab3, tab4, tab5);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        List.of(btnIns, btnDel, btnRef).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5)));
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
        tab4.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab4(event);
                }
            }
        });
    }
}
