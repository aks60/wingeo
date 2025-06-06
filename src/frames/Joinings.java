package frames;

import frames.swing.comp.ProgressBar;
import builder.making.UColor;
import builder.model.ElemJoining;
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
import frames.swing.comp.DefTableModel;
import frames.dialog.DicJoinvar;
import domain.eJoinvar;
import enums.TypeGrup;
import frames.swing.comp.DefCellRendererBool;
import frames.dialog.DicColvar;
import enums.UseColor;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import startup.App;
import common.listener.ListenerRecord;
import common.listener.ListenerFrame;
import dataset.Connect;
import domain.eArtdet;
import domain.eParmap;
import frames.dialog.DicArtikl2;
import frames.swing.comp.DefCellEditorBtn;
import frames.swing.comp.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import report.sup.ExecuteCmd;
import report.sup.RTable;

//�������� ����������
public class Joinings extends javax.swing.JFrame {

    private Query qGroups = new Query(eGroups.values());
    private Query qColor = new Query(eColor.id, eColor.groups_id, eColor.name);
    private Query qArtikl = new Query(eArtikl.values());
    private Query qJoining = new Query(eJoining.values());
    private Query qJoinvar = new Query(eJoinvar.values());
    private Query qJoindet = new Query(eJoindet.values());
    private Query qJoinpar1 = new Query(eJoinpar1.values());
    private Query qJoinpar2 = new Query(eJoinpar2.values());
    private int joinID = -1;
    private ListenerRecord listenerArtikl, listenerJoinvar, listenerColor, listenerColvar1, listenerColvar2, listenerColvar3;

    //������ �� Tex (������� ����)
    public Joinings() {
        initComponents();
        initElements();
        loadingData();
        loadingModel();
        listenerSet();
        listenerAdd();
    }

    //������ �� Systree
    public Joinings(ElemJoining join) {
        this.joinID = join.joiningRec.getInt(1);
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

    //������ �� Specific
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
        qGroups.sql(eGroups.data(), eGroups.grup, TypeGrup.PARAM_USER.id, TypeGrup.COLOR_MAP.id).sort(eGroups.npp, eGroups.name);
        qColor.sql(eColor.data(), eColor.up);
        qArtikl.sql(eArtikl.data(), eArtikl.up);
        if (joinID == -1) {
            qJoining.sql(eJoining.data(), eJoining.up).sort(eJoining.name);
        } else {
            qJoining.sql(eJoining.data(), eJoining.id, joinID).sort(eJoining.name);
        }
    }

    public void loadingModel() {

        new DefTableModel(tab1, qJoining, eJoining.artikl_id1, eJoining.artikl_id2, eJoining.name, eJoining.is_main, eJoining.analog) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (eJoining.artikl_id1 == field) {
                    return qArtikl.stream().filter(rec -> rec.get(eArtikl.id).equals(val)).findFirst().orElse(eArtikl.up.newRecord(Query.SEL)).get(eArtikl.code);

                } else if (eJoining.artikl_id2 == field) {
                    return qArtikl.stream().filter(rec -> rec.get(eArtikl.id).equals(val)).findFirst().orElse(eArtikl.up.newRecord(Query.SEL)).get(eArtikl.code);
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
                        return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).getDev(eGroups.name, val);
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
                        int id = Integer.valueOf(String.valueOf(val));
                        Record recordArt = qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == id).findFirst().orElse(eArtikl.up.newRecord(Query.SEL));
                        if (col == 0) {
                            return recordArt.getStr(eArtikl.code);
                        } else if (col == 1) {
                            return recordArt.getStr(eArtikl.name);
                        }
                    } else if (eJoindet.color_fk == field) {
                        int colorFk = Integer.valueOf(String.valueOf(val));

                        if (UseColor.automatic[0].equals(colorFk)) {
                            return UseColor.automatic[1];

                        } else if (UseColor.precision[0].equals(colorFk)) {
                            return UseColor.precision[1];
                        }
                        if (colorFk > 0) {
                            return qColor.stream().filter(rec -> rec.getInt(eColor.id) == colorFk).findFirst().orElse(eColor.up.newRecord(Query.SEL)).get(eColor.name);
                        } else {
                            return "# " + qGroups.stream().filter(rec -> rec.getInt(eGroups.id) == -1 * colorFk).findFirst().orElse(eGroups.up.newRecord(Query.SEL)).get(eGroups.name);
                        }
                    } else if (eJoindet.color_us == field) {
                        int types = Integer.valueOf(String.valueOf(val));
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
                        return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).getDev(eGroups.name, val);
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
        UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
        List.of(tab1, tab2, tab3, tab4, tab5).forEach(tab -> UGui.getQuery(tab).execsql());
        UGui.clearTable(tab2, tab3, tab4, tab5);
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record record = qJoining.query(eJoining.up).get(index);
            Integer id = record.getInt(eJoining.id);
            qJoinvar.sql(eJoinvar.data(), eJoinvar.joining_id, id).sort(eJoinvar.prio);
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab2);
        }
    }

    public void selectionTab2(ListSelectionEvent event) {
        UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
        List.of(tab1, tab2, tab3, tab4, tab5).forEach(tab -> UGui.getQuery(tab).execsql());
        UGui.clearTable(tab3, tab4, tab5);
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            Record record = qJoinvar.query(eJoinvar.up).get(index);
            Integer id = record.getInt(eJoinvar.id);
            qJoindet.sql(eJoindet.data(), eJoindet.joinvar_id, id).sort(eJoindet.artikl_id);
            qJoinpar1.sql(eJoinpar1.data(), eJoinpar1.joinvar_id, id).sort(eJoinpar1.id);
            ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
            ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab3);
            UGui.setSelectedRow(tab4);
        }
    }

    public void selectionTab4(ListSelectionEvent event) {
        UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
        List.of(tab1, tab2, tab3, tab4, tab5).forEach(tab -> UGui.getQuery(tab).execsql());
        int index = UGui.getIndexRec(tab4);
        if (index != -1) {
            Record record = qJoindet.query(eJoindet.up).get(index);
            Integer id = record.getInt(eJoindet.id);
            qJoinpar2.sql(eJoinpar2.data(), eJoinpar2.joindet_id, id).sort(eJoinpar2.id);
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

        UGui.buttonCellEditor(tab3, 1, (componentCell) -> { //��������� �������������� ����, ���� ������ � ���� ������ �������
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

        UGui.buttonCellEditor(tab5, 1, (componentCell) -> { //��������� �������������� ����, ���� ������ � ���� ������ �������
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
            if (qArtikl.find(eArtikl.id, record.get(eArtikl.id)).get(eArtikl.id) == null) {
                qArtikl.sql(eArtikl.data(), eArtikl.up);
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

            //���� �� �����������
            for (int index = 0; index < qJoining.size(); index++) {
                int joining_id = qJoining.get(index).getInt(eJoining.id);
                qVar.sql(eJoinvar.data(), eJoinvar.joining_id, joining_id).sort(eJoinvar.prio);

                //���� �� ��������� �����������
                for (int index2 = 0; index2 < qVar.size(); index2++) {
                    int joinvar_id = qVar.get(index2).getInt(eJoining.id);
                    //qDet.selects(eJoindet.up, "left join", eArtikl.up, "on", eArtikl.id, "=", eJoindet.artikl_id, "where", eJoindet.joinvar_id, "=", joinvar_id, "order by", eJoindet.artikl_id);
                    qDet.sql(eJoindet.data(), eJoindet.joinvar_id, joinvar_id).sort(eJoindet.artikl_id);
                    qDet.query(eArtikl.up).join(qDet, eArtikl.data(), eJoindet.artikl_id, eArtikl.id);
                    //���� �� ����������� ����������
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
            System.err.println("������:Joinings.deteilFind() " + e);
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
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnFind1 = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnClone = new javax.swing.JButton();
        btnFind2 = new javax.swing.JButton();
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
        setTitle("����������");
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

        btnFind1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c088.gif"))); // NOI18N
        btnFind1.setToolTipText(bundle.getString("����� ������")); // NOI18N
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

        btnClone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c065.gif"))); // NOI18N
        btnClone.setToolTipText(bundle.getString("����������� ������")); // NOI18N
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

        btnFind2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c089.gif"))); // NOI18N
        btnFind2.setToolTipText(bundle.getString("����� ������")); // NOI18N
        btnFind2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFind2.setFocusable(false);
        btnFind2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFind2.setMaximumSize(new java.awt.Dimension(25, 25));
        btnFind2.setMinimumSize(new java.awt.Dimension(25, 25));
        btnFind2.setPreferredSize(new java.awt.Dimension(25, 25));
        btnFind2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnFind2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFind2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFind2(evt);
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
                .addComponent(btnClone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(btnReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFind1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnClone, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFind2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        scr1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "������ ����������", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "������� 1", "������� 2", "��������", "��������", "������", "ID"
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

        scr2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "�������� ����������", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr2.setPreferredSize(new java.awt.Dimension(300, 234));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "���������", "��������", "������������", "ID"
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

        scr3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "���������", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr3.setPreferredSize(new java.awt.Dimension(300, 234));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "��������", "��������"
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

        scr4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "����������� ����������", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr4.setMaximumSize(new java.awt.Dimension(2000, 800));
        scr4.setPreferredSize(new java.awt.Dimension(600, 300));

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������", "��������", "��������", "��������", "����������", "�������", "ID"
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

        scr5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "���������", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr5.setMaximumSize(new java.awt.Dimension(600, 800));
        scr5.setPreferredSize(new java.awt.Dimension(260, 300));

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "��������", "��������"
            }
        ));
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
                //record.setDev(eJoining.name, "����������-");
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
        UGui.stopCellEditingAndExecSql(getRootPane());
    }//GEN-LAST:event_windowClosed

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        RTable.load("����������", tab1);
        ExecuteCmd.documentType(this);
    }//GEN-LAST:event_btnReport

    private void btnFind1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind1
        if (tab1.getBorder() != null) {
            Record record = ((DefTableModel) tab1.getModel()).getQuery().get(UGui.getIndexRec(tab1));
            Record record2 = qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == record.getInt(eJoining.artikl_id1)).findFirst().orElse(eJoining.up.newRecord(Query.SEL));
            ProgressBar.create(this, new ListenerFrame() {
                public void actionRequest(Object obj) {
                    App.Artikles.createFrame(Joinings.this, record2);
                }
            });
        } else if (tab4.getBorder() != null) {
            Record record = ((DefTableModel) tab4.getModel()).getQuery().get(UGui.getIndexRec(tab4));
            Record record2 = qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == record.getInt(eJoindet.artikl_id)).findFirst().orElse(eJoindet.up.newRecord(Query.SEL));
            ProgressBar.create(this, new ListenerFrame() {
                public void actionRequest(Object obj) {
                    App.Artikles.createFrame(Joinings.this, record2);
                }
            });
        }
    }//GEN-LAST:event_btnFind1

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest
    }//GEN-LAST:event_btnTest

    private void btnClone(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClone
        UIManager.put("OptionPane.noButtonText", "���");
        int result = JOptionPane.showConfirmDialog(this, "����������� ������ �������� ������?",
                "�������������", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result != JOptionPane.CANCEL_OPTION) {

            if (tab1.getBorder() != null) {
                List<Record> dataVar = new ArrayList(qJoinvar);
                Record masterClon = UGui.cloneMaster(qJoining, tab1, eJoining.up, (clon) -> {
                    clon.set(eJoining.name, clon.getStr(eJoining.name) + "-����");
                });
                if (result == JOptionPane.NO_OPTION) {
                    UGui.cloneSlave(qJoinvar, tab2, eJoinvar.up, dataVar, (clon) -> {
                        clon.setNo(eJoinvar.joining_id, masterClon.getStr(eJoining.id));
                    });
                }
            } else if (tab4.getBorder() != null) {
                List<Record> dataPar2 = new ArrayList(qJoinpar2);
                Record masterClon = UGui.cloneMaster(qJoindet, tab4, eJoindet.up, null);
                if (result == JOptionPane.NO_OPTION) {
                    UGui.cloneSlave(qJoinpar2, tab5, eJoinpar2.up, dataPar2, (clon) -> {
                        clon.setNo(eJoinpar2.joindet_id, masterClon.getStr(eJoindet.id));
                    });
                }
            }
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

        } else if (evt.getButton() == MouseEvent.BUTTON1) {
            JTable table = (JTable) evt.getSource();
            UGui.updateBorderAndSql(table, List.of(tab1, tab2, tab3, tab4, tab5));
            List.of(btnClone, btnFind1, btnFind2).forEach(btn -> btn.setEnabled(false));
            if (tab1.getBorder() != null) {
                List.of(btnClone, btnFind1, btnFind2).forEach(btn -> btn.setEnabled(true));
            } else if (tab4.getBorder() != null) {
                btnFind1.setEnabled(true);
                btnClone.setEnabled(true);
            }
        }
    }//GEN-LAST:event_tabMouseClicked

    private void btnFind2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind2
        if (tab1.getBorder() != null) {
            Record record = ((DefTableModel) tab1.getModel()).getQuery().get(UGui.getIndexRec(tab1));
            Record record2 = qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == record.getInt(eJoining.artikl_id2)).findFirst().orElse(eJoining.up.newRecord(Query.SEL));
            ProgressBar.create(this, new ListenerFrame() {
                public void actionRequest(Object obj) {
                    App.Artikles.createFrame(Joinings.this, record2);
                }
            });
        }
    }//GEN-LAST:event_btnFind2

// <editor-fold defaultstate="collapsed" desc="Generated Code">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClone;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnFind1;
    private javax.swing.JButton btnFind2;
    private javax.swing.JButton btnIns;
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

        btnTest.setVisible(eProp.devel.equals("99"));
        App.loadLocationWin(this, btnClose, (e) -> {
            App.saveLocationWin(this, btnClose, tab1, tab2, tab3, tab4, tab5);
        }, tab1, tab2, tab3, tab4, tab5);
        new UColor();

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
        tab4.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab4(event);
                }
            }
        });
    }
}
