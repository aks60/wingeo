package frames;

import frames.swing.comp.ProgressBar;
import dataset.Query;
import dataset.Record;
import domain.eArtikl;
import domain.eFurndet;
import domain.eFurniture;
import domain.eFurnpar1;
import domain.eFurnpar2;
import domain.eFurnside1;
import domain.eColor;
import domain.eFurnside2;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import frames.swing.comp.DefTableModel;
import dataset.Field;
import domain.eGroups;
import frames.dialog.DicColvar;
import frames.dialog.DicEnums;
import frames.dialog.ParColor;
import frames.dialog.ParName;
import frames.dialog.ParSysVal;
import frames.dialog.ParUserVal;
import domain.eParams;
import enums.Enam;
import builder.param.ParamList;
import common.eProp;
import enums.LayoutFurn1;
import enums.UseFurn3;
import enums.LayoutFurn3;
import enums.TypeGrup;
import enums.UseColor;
import enums.UseFurn1;
import enums.UseFurn2;
import frames.swing.comp.DefCellRendererBool;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import startup.App;
import javax.swing.JOptionPane;
import common.listener.ListenerRecord;
import common.listener.ListenerFrame;
import dataset.Connect;
import domain.eArtdet;
import domain.eParmap;
import domain.eSysfurn;
import domain.eSystree;
import frames.dialog.DicArtikl2;
import frames.swing.comp.DefCellEditorBtn;
import frames.swing.comp.DefCellEditorNumb;
import frames.swing.comp.DefCellRendererNumb;
import frames.swing.comp.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableColumnModel;
import report.sup.ExecuteCmd;
import report.sup.RTable;

//TODO �������������� �������� ������������ �������
public class Furniturs extends javax.swing.JFrame {

    private Query qGroups = new Query(eGroups.values());
    private Query qColor = new Query(eColor.id, eColor.groups_id, eColor.name);
    private Query qFurnall = new Query(eFurniture.values());
    private Query qFurniture = new Query(eFurniture.values());
    private Query qArtikl = new Query(eArtikl.values());
    private Query qFurndet2a = new Query(eFurndet.values());
    private Query qFurndet2b = new Query(eFurndet.values());
    private Query qFurndet2c = new Query(eFurndet.values());
    private Query qFurnside1 = new Query(eFurnside1.values());
    private Query qFurnside2 = new Query(eFurnside2.values());
    private Query qFurnpar1 = new Query(eFurnpar1.values());
    private Query qFurnpar2 = new Query(eFurnpar2.values());
    private ListenerRecord listenerArtikl, listenerPar1, listenerPar2, listenerTypset, listenerColor,
            listenerColvar, listenerSide1, listenerSide2, listenerSide3, listenerSide4, listenerVariant1, listenerVariant2;
    private JTable tab2X = null; //�������� ������� ������������
    private int indexT1 = 0;

    public Furniturs() {
        initComponents();
        initElements();
        loadingData();
        loadingModel();
        listenerAdd();
        listenerSet();
    }

    public Furniturs(int recordID) {
        initComponents();
        initElements();
        loadingData();
        loadingModel();
        listenerAdd();
        listenerSet();
        if (recordID < 0) {
            variantFind(recordID);
        } else {
            deteilFind(recordID);
        }
    }

    public void loadingData() {
        tab2X = tab2a;
        qColor.sql(eColor.data(), eColor.up);
        qArtikl.sql(eArtikl.data(), eArtikl.up);
        qFurnall.sql(eFurniture.data(), eFurniture.up).sort(eFurniture.name);
        qGroups.sql(eGroups.data(), eGroups.grup, TypeGrup.PARAM_USER.id, TypeGrup.COLOR_MAP.id);
        int types = (btnTab1.isSelected()) ? 0 : (btnTab2.isSelected()) ? 1 : -1;
        qFurniture.sql(eFurniture.data(), eFurniture.types, types).sort(eFurniture.name);
    }

    public void loadingModel() {
        new DefTableModel(tab1, qFurniture, eFurniture.name, eFurniture.view_open, eFurniture.hand_side, eFurniture.hand_set1, eFurniture.hand_set2, eFurniture.hand_set3, eFurniture.max_p2,
                eFurniture.max_width, eFurniture.max_height, eFurniture.max_weight, eFurniture.ways_use, eFurniture.pars, eFurniture.coord_lim, eFurniture.id) {

            public Object getValueAt(int col, int row, Object val) {

                Field field = columns[col];
                if (val != null && eFurniture.view_open == field) {
                    int fk = Integer.valueOf(String.valueOf(val));
                    if (UseFurn1.P1.find(fk) != null) {
                        return UseFurn1.P1.find(fk).text();
                    }
                } else if (val != null && eFurniture.hand_side == field) {
                    int fk = Integer.valueOf(String.valueOf(val));
                    if (LayoutFurn1.BOTT.find(fk) != null) {
                        return LayoutFurn1.BOTT.find(fk).text();
                    }
                } else if (val != null && eFurniture.ways_use == field) {
                    int fk = Integer.valueOf(String.valueOf(val));
                    if (UseFurn2.P1.find(fk) != null) {
                        return UseFurn2.P1.find(fk).text();
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab2a, qFurndet2a, eFurndet.artikl_id, eFurndet.artikl_id, eFurndet.color_fk, eFurndet.color_us, eFurndet.id) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];

                //��������
                if (val != null && eFurndet.color_fk == field) {
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

                    //������ ��������
                } else if (val != null && eFurndet.color_us == field) {
                    int types = Integer.valueOf(String.valueOf(val));
                    types = types & 0x0000000f;
                    return UseColor.MANUAL.find(types).text();

                } else if (eFurndet.artikl_id == field) {

                    //�����
                    if (qFurndet2a.get(row, eFurndet.furniture_id2) != null) {
                        int furniture_id2 = qFurndet2a.getAs(row, eFurndet.furniture_id2);
                        String name = qFurnall.find(eFurniture.data(), eFurniture.id, furniture_id2).getStr(eFurniture.name);
                        return (col == 0) ? "�����" : name;

                        //�������
                    } else if (val != null) {
                        int artikl_id = Integer.valueOf(String.valueOf(val));
                        Record recordArt = qArtikl.find(eArtikl.data(), eArtikl.id, artikl_id);
                        return (col == 0) ? recordArt.getStr(eArtikl.code) : recordArt.getStr(eArtikl.name);
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab2b, qFurndet2b, eFurndet.artikl_id, eFurndet.artikl_id, eFurndet.color_fk, eFurndet.color_us, eFurndet.id) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];

                //��������
                if (val != null && eFurndet.color_fk == field) {
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

                    //������ ��������
                } else if (val != null && eFurndet.color_us == field) {
                    int types = Integer.valueOf(String.valueOf(val));
                    types = types & 0x0000000f;
                    return UseColor.MANUAL.find(types).text();

                } else if (eFurndet.artikl_id == field) {

                    //�����
                    if (qFurndet2b.get(row, eFurndet.furniture_id2) != null) {
                        int furniture_id2 = qFurndet2b.getAs(row, eFurndet.furniture_id2);
                        String name = qFurnall.find(eFurniture.data(), eFurniture.id, furniture_id2).getStr(eFurniture.name);
                        return (col == 0) ? "�����" : name;

                        //�������    
                    } else if (val != null) {
                        int artikl_id = Integer.valueOf(String.valueOf(val));
                        Record recordArt = qArtikl.find(eArtikl.data(), eArtikl.id, artikl_id);
                        return (col == 0) ? recordArt.getStr(eArtikl.code) : recordArt.getStr(eArtikl.name);
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab2c, qFurndet2c, eFurndet.artikl_id, eFurndet.artikl_id, eFurndet.color_fk, eFurndet.color_us, eFurndet.id) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];

                //��������
                if (val != null && eFurndet.color_fk == field) {
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

                    //������ ��������
                } else if (val != null && eFurndet.color_us == field) {
                    int types = Integer.valueOf(String.valueOf(val));
                    types = types & 0x0000000f;
                    return UseColor.MANUAL.find(types).text();

                    //�����
                } else if (eFurndet.artikl_id == field) {
                    if (qFurndet2c.get(row, eFurndet.furniture_id2) != null) {
                        int furniture_id2 = qFurndet2c.getAs(row, eFurndet.furniture_id2);
                        String name = qFurnall.find(eFurniture.data(), eFurniture.id, furniture_id2).getStr(eFurniture.name);
                        return (col == 0) ? "�����" : name;

                        //������� 
                    } else if (val != null) {
                        int artikl_id = Integer.valueOf(String.valueOf(val));
                        Record recordArt = qArtikl.find(eArtikl.data(), eArtikl.id, artikl_id);
                        return (col == 0) ? recordArt.getStr(eArtikl.code) : recordArt.getStr(eArtikl.name);
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab3, qFurnside1, eFurnside1.side_num, eFurnside1.side_use) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && eFurnside1.side_num == field) {
                    int v = Integer.valueOf(String.valueOf(val));
                    if (v > 0 && v < 5) {
                        return LayoutFurn1.values()[v - 1].name;
                    }
                } else if (val != null && eFurnside1.side_use == field) {
                    int v = Integer.valueOf(String.valueOf(val));
                    if (v > 0 && v < 4) {
                        return UseFurn3.values()[v - 1].name;
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab4, qFurnpar1, eFurnpar1.groups_id, eFurnpar1.text) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (field == eFurnpar1.groups_id && val != null) {

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
        new DefTableModel(tab5, qFurnside2, eFurnside2.side_num, eFurnside2.len_min, eFurnside2.len_max, eFurnside2.ang_min, eFurnside2.ang_max) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && eFurnside2.side_num == field) {
                    int v = Integer.valueOf(String.valueOf(val));
                    if (v > -3 && v < 5) {
                        return Stream.of(LayoutFurn3.values()).filter(en -> en.id == v).findFirst().get().name;  //orElse(null).name;
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab6, qFurnpar2, eFurnpar2.groups_id, eFurnpar2.text) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && field == eFurnpar2.groups_id) {

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

        List.of(3, 4, 5).forEach(i -> tab1.getColumnModel().getColumn(i).setCellRenderer(new DefCellRendererBool()));
        List.of(6, 7, 8).forEach(i -> tab1.getColumnModel().getColumn(i).setCellEditor(new DefCellEditorNumb(1)));
        tab1.getColumnModel().getColumn(12).setCellEditor(new DefCellEditorNumb("4"));

        List.of(1, 2, 3, 4).forEach(i -> tab5.getColumnModel().getColumn(i).setCellRenderer(new DefCellRendererNumb(3)));
        List.of(1, 2, 3, 4).forEach(i -> tab5.getColumnModel().getColumn(i).setCellEditor(new DefCellEditorNumb(3)));

        UGui.setSelectedRow(tab1);
    }

    public void selectionTab1(ListSelectionEvent event) {
        UGui.clearTable(tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            indexT1 = (btnTab1.isSelected()) ? index : indexT1;
            Record record = qFurniture.query(eFurniture.up).get(index);
            Integer id = record.getInt(eFurniture.id);

            qFurnside1.sql(eFurnside1.data(), eFurnside1.furniture_id, id).sort(eFurnside1.side_num);
            ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab3);
            qFurndet2a.sql(eFurndet.data(), eFurndet.furniture_id1, id, eFurndet.furndet_id, eFurndet.id);
            ((DefaultTableModel) tab2a.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab2a);
        }
    }

    public void selectionTab2a(ListSelectionEvent event) {
        UGui.clearTable(tab2b, tab2c, tab5, tab6);
        int index = UGui.getIndexRec(tab2a);
        if (index != -1) {
            Record record = qFurndet2a.get(index);
            int id = record.getInt(eFurndet.id);

            qFurndet2b.sq2(eFurndet.data(), eFurndet.furndet_id, id, eFurndet.id, eFurndet.furndet_id);
            ((DefaultTableModel) tab2b.getModel()).fireTableDataChanged();

            qFurnside2.sql(eFurnside2.data(), eFurnside2.furndet_id, id).sort(eFurnside2.side_num);
            ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab5);

            qFurnpar2.sql(eFurnpar2.data(), eFurnpar2.furndet_id, id).sort(eFurnpar2.id);
            ((DefaultTableModel) tab6.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab6);
        }
    }

    public void selectionTab2b(ListSelectionEvent event) {
        UGui.clearTable(tab2c, tab5, tab6);
        int index = UGui.getIndexRec(tab2b);
        if (index != -1) {
            Record record = qFurndet2b.get(index);
            Integer id = record.getInt(eFurndet.id);

            qFurndet2c.sql(eFurndet.data(), eFurndet.furndet_id, id);
            ((DefaultTableModel) tab2c.getModel()).fireTableDataChanged();

            qFurnside2.sql(eFurnside2.data(), eFurnside2.furndet_id, id).sort(eFurnside2.side_num);
            ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab5);

            qFurnpar2.sql(eFurnpar2.data(), eFurnpar2.furndet_id, id).sort(eFurnpar2.id);
            ((DefaultTableModel) tab6.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab6);
        }
    }

    public void selectionTab2c(ListSelectionEvent event) {
        UGui.clearTable(tab5, tab6);
        int index = UGui.getIndexRec(tab2c);
        if (index != -1) {
            Record record = qFurndet2c.get(index);
            Integer id = record.getInt(eFurndet.id);

            qFurnside2.sql(eFurnside2.data(), eFurnside2.furndet_id, id).sort(eFurnside2.side_num);
            ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab5);

            qFurnpar2.sql(eFurnpar2.data(), eFurnpar2.furndet_id, id).sort(eFurnpar2.id);
            ((DefaultTableModel) tab6.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab6);
        }
    }

    public void selectionTab3(ListSelectionEvent event) {
        UGui.clearTable(tab4);
        int index = UGui.getIndexRec(tab3);
        if (index != -1) {
            Record record = qFurnside1.query(eFurnside1.up).get(index);
            Integer id = record.getInt(eFurnside1.id);
            qFurnpar1.sql(eFurnpar1.data(), eFurnpar1.furnside_id, id).sort(eFurnpar1.id);
            ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab4);
        }
    }

    public void listenerAdd() {
        UGui.buttonCellEditor(tab1, 1).addActionListener(event -> {
            new DicEnums(this, listenerVariant1, UseFurn1.values());
        });

        UGui.buttonCellEditor(tab1, 2).addActionListener(event -> {
            new DicEnums(this, listenerSide4, LayoutFurn1.values());
        });

        UGui.buttonCellEditor(tab1, 10).addActionListener(event -> {
            new DicEnums(this, listenerVariant2, UseFurn2.values());
        });

        for (JTable tab2x : List.of(tab2a, tab2b, tab2c)) {
            UGui.buttonCellEditor(tab2x, 0).addActionListener(event -> {
                new DicArtikl2(this, listenerArtikl, 1, 2, 3, 4, 5, 6);
            });
        }
        for (JTable tab2x : List.of(tab2a, tab2b, tab2c)) {
            UGui.buttonCellEditor(tab2x, 1).addActionListener(event -> {
                new DicArtikl2(this, listenerArtikl, 1, 2, 3, 4, 5, 6);
            });
        }

        for (JTable tab2x : List.of(tab2a, tab2b, tab2c)) {
            Query query = (tab2x == tab2a) ? qFurndet2a : (tab2x == tab2b) ? qFurndet2b : qFurndet2c;
            UGui.buttonCellEditor(tab2x, 2).addActionListener(event -> {
                Record record = query.get(UGui.getIndexRec(tab2x));
                int artiklID = record.getInt(eFurndet.artikl_id);
                int colorFK = record.getInt(eFurndet.color_fk, -1);
                ParColor frame = new ParColor(this, listenerColor, eParmap.furn, artiklID, colorFK);
            });
        }
        for (JTable tab2x : List.of(tab2a, tab2b, tab2c)) {
            Query query = (tab2x == tab2a) ? qFurndet2a : (tab2x == tab2b) ? qFurndet2b : qFurndet2c;
            UGui.buttonCellEditor(tab2x, 3).addActionListener(event -> {
                Record record = query.get(UGui.getIndexRec(tab2x));
                int colorFk = record.getInt(eFurndet.color_fk);
                new DicColvar(this, listenerColvar, colorFk);
            });
        }

        UGui.buttonCellEditor(tab3, 0).addActionListener(event -> {
            new DicEnums(this, listenerSide1, LayoutFurn1.values());
        });

        UGui.buttonCellEditor(tab3, 1).addActionListener(event -> {
            new DicEnums(this, listenerSide2, UseFurn3.values());
        });

        UGui.buttonCellEditor(tab4, 0).addActionListener(event -> {
            new ParName(this, listenerPar1, eParams.joint, 21000);
        });

        UGui.buttonCellEditor(tab4, 1, (componentCell) -> { //��������� �������������� ���� � ���� ������ � ���� ������ �������
            return UGui.cellParamTypeOrVid(tab4, componentCell, eFurnpar1.groups_id);

        }).addActionListener(event -> {
            Record record = qFurnpar1.get(UGui.getIndexRec(tab4));
            int grup = record.getInt(eFurnpar1.groups_id);
            if (grup < 0) {
                ParUserVal frame = new ParUserVal(this, listenerPar1, eParams.furn, grup);
            } else {
                List list = ParamList.find(grup).dict();
                ParSysVal frame = new ParSysVal(this, listenerPar1, list);
            }
        });

        UGui.buttonCellEditor(tab5, 0).addActionListener(event -> {
            new DicEnums(this, listenerSide3, LayoutFurn3.values());
        });

        UGui.buttonCellEditor(tab6, 0).addActionListener(event -> {
            int index2 = UGui.getIndexRec(tab2X);
            if (index2 != -1) {
                Query query = ((DefTableModel) tab2X.getModel()).getQuery();
                Record furndetRec = query.get(index2);
                int artikl_id = furndetRec.getInt(eFurndet.artikl_id);
                Record recordArt = eArtikl.find(artikl_id, false);
                int level = (recordArt.getInt(eArtikl.level1) == -1) ? 0 : recordArt.getInt(eArtikl.level1);
                Integer[] part = {0, 25000, 24000, 25000, 24000, 0, 0};

                //Record furnpar2Rec = qFurnpar2.get(UGui.getIndexRec(tab6));
                if (furndetRec.get(eFurndet.furniture_id2) != null) {
                    new ParName(this, listenerPar2, eParams.furn, 24000);
                } else {
                    if (qFurnpar2.get(UGui.getIndexRec(tab6), eFurnpar2.groups_id) == null) {
                        new ParName(this, listenerPar2, eParams.furn, part[level]);
                    } else {
                        int groupsID = qFurnpar2.getAs(UGui.getIndexRec(tab6), eFurnpar2.groups_id);
                        new ParName(this, groupsID, listenerPar2, eParams.furn, part[level]);
                    }
                }
            }
        });

        UGui.buttonCellEditor(tab6, 1, (componentCell) -> { //��������� �������������� ���� � ���� ������ � ���� ������ �������
            return UGui.cellParamTypeOrVid(tab6, componentCell, eFurnpar2.groups_id);

        }).addActionListener(event -> {
            Record record = qFurnpar2.get(UGui.getIndexRec(tab6));
            int grup = record.getInt(eFurnpar2.groups_id);
            if (grup < 0) {
                ParUserVal frame = new ParUserVal(this, listenerPar2, eParams.furn, grup);
            } else {
                List list = ParamList.find(grup).dict();
                ParSysVal frame = new ParSysVal(this, listenerPar2, list);
            }
        });
    }

    public void listenerSet() {

        listenerArtikl = (record) -> {
            UGui.stopCellEditing(tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
            if (qArtikl.find(eArtikl.data(), eArtikl.id, record.getInt(eArtikl.id)).get(eArtikl.id) == null) {
                qArtikl.sql(eArtikl.data(), eArtikl.up);
            }
            JTable tab2x = (tab2a.getBorder() != null) ? tab2a : (tab2b.getBorder() != null) ? tab2b : tab2c;
            Query query = (tab2a.getBorder() != null) ? qFurndet2a : (tab2b.getBorder() != null) ? qFurndet2b : qFurndet2c;
            if (tab2x.getBorder() != null) {
                int index = UGui.getIndexRec(tab2x);
                int artiklID = record.getInt(eArtikl.id);
                List<Record> artdetList = eArtdet.filter2(artiklID);
                if (artdetList.size() == 1) {
                    if (artdetList.get(0).getInt(eArtdet.color_fk) > 0) {
                        Record colorRec = eColor.find(artdetList.get(0).getInt(eArtdet.color_fk));
                        listenerColor.action(colorRec);
                    }
                }
                query.set(artiklID, UGui.getIndexRec(tab2x), eFurndet.artikl_id);
                ((DefaultTableModel) tab2x.getModel()).fireTableDataChanged();
                UGui.setSelectedIndex(tab2x, index);
            }
        };

        listenerColor = (record) -> {
            JTable tab = (tab2a.getBorder() != null) ? tab2a : (tab2b.getBorder() != null) ? tab2b : tab2c;
            UGui.cellParamColor(record, tab, eFurndet.color_fk, eFurndet.color_us, tab1, tab2a, tab2b, tab2c, tab6, tab3, tab4);
        };

        listenerColvar = (record) -> {
            JTable tab = (tab2a.getBorder() != null) ? tab2a : (tab2b.getBorder() != null) ? tab2b : tab2c;
            Query query = (tab2a.getBorder() != null) ? qFurndet2a : (tab2b.getBorder() != null) ? qFurndet2b : qFurndet2c;
            UGui.stopCellEditing(tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
            int index = UGui.getIndexRec(tab);
            Record furndetRec = query.get(UGui.getIndexRec(tab));
            int types = (furndetRec.getInt(eFurndet.color_us) == -1) ? 0 : furndetRec.getInt(eFurndet.color_us);
            types = (types & 0xfffffff0) + record.getInt(0);
            furndetRec.set(eFurndet.color_us, types);
            ((DefaultTableModel) tab.getModel()).fireTableDataChanged();
            UGui.setSelectedIndex(tab, index);
        };

        listenerSide1 = (record) -> {
            UGui.cellParamEnum(record, tab3, eFurnside1.side_num, tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
        };

        listenerSide2 = (record) -> {
            UGui.cellParamEnum(record, tab3, eFurnside1.side_use, tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
        };

        listenerSide3 = (record) -> {
            UGui.cellParamEnum(record, tab5, eFurnside2.side_num, tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
        };

        listenerSide4 = (record) -> {
            UGui.cellParamEnum(record, tab1, eFurniture.hand_side, tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
        };

        listenerPar1 = (record) -> {
            UGui.stopCellEditing(tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
            UGui.cellParamNameOrValue(record, tab4, eFurnpar1.groups_id, eFurnpar1.text);
        };

        listenerPar2 = (record) -> {
            UGui.stopCellEditing(tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
            UGui.cellParamNameOrValue(record, tab6, eFurnpar2.groups_id, eFurnpar2.text);
        };

        listenerVariant1 = (record) -> {
            UGui.cellParamEnum(record, tab1, eFurniture.view_open, tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
        };

        listenerVariant2 = (record) -> {
            UGui.cellParamEnum(record, tab1, eFurniture.ways_use, tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
        };

//        listenerPpmCrud = (evt) -> {
//            if (evt.getSource() == mInsert) {
//                btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
//            } else if (evt.getSource() == mDelit) {
//                btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
//            }
//        };
    }

    public void selectionRows(Query qFurn, Query qDet2a, Query qDet2b, Query qDet2c, int iTabb, int iFurn, int iDet2a, int iDet2b, int iDet2c) {

        if (qFurn.get(iFurn).getInt(eFurniture.types) == 0) {
            btnTab1.setSelected(true);
        } else if (qFurn.get(iFurn).getInt(eFurniture.types) == 1) {
            btnTab2.setSelected(true);
        } else {
            btnTab3.setSelected(true);
        }
        tbtnAction(null);

        UGui.setSelectedIndex(tab1, iFurn);
        UGui.scrollRectToRow(iFurn, tab1);
        UGui.setSelectedIndex(tab2a, iDet2a);
        UGui.setSelectedIndex(tab2b, iDet2b);
        UGui.setSelectedIndex(tab2c, iDet2c);
        tabb1.setSelectedIndex(iTabb);
        if (iTabb == 0) {
            UGui.scrollRectToRow(iDet2a, tab2a);
        } else if (iTabb == 1) {
            UGui.scrollRectToRow(iDet2b, tab2b);
        } else {
            UGui.scrollRectToRow(iDet2c, tab2c);
        }
    }

    public void variantFind(int variantID) {
        variantID = -1 * variantID;
        JTable table = null;
        Query qFurn = new Query(eFurniture.values());
        for (int index0 : List.of(0, 1, -1)) {
            qFurn.sql(eFurniture.data(), eFurniture.types, index0).sort(eFurniture.name);
            for (int index = 0; index < qFurn.size(); index++) {
                int id = qFurn.get(index).getInt(eFurniture.id);
                if (id == variantID) {
                    if (index0 == 0) {
                        btnTab1.setSelected(true);
                    } else if (index0 == 1) {
                        btnTab2.setSelected(true);
                    } else if (index0 == -1) {
                        btnTab3.setSelected(true);
                    }
                    tbtnAction(null);
                    UGui.setSelectedIndex(tab1, index);
                    UGui.scrollRectToRow(index, tab1);
                }
            }
        }
    }

    public void deteilFind(int deteilID) {
        Query qFurn = new Query(eFurniture.values());
        Query qDet2a = new Query(eFurndet.values(), eArtikl.values());
        Query qDet2b = new Query(eFurndet.values(), eArtikl.values());
        Query qDet2c = new Query(eFurndet.values(), eArtikl.values());
        try {
            for (int index0 : List.of(0, 1, -1)) {
                qFurn.sql(eFurniture.data(), eFurniture.types, index0).sort(eFurniture.name);
                for (int index1 = 0; index1 < qFurn.size(); index1++) {

                    int id = qFurn.get(index1).getInt(eFurniture.id);
                    qDet2a.sql(eFurndet.data(), eFurndet.furniture_id1, id, eFurndet.furndet_id, eFurndet.id);
                    qDet2a.query(eArtikl.up).join(qDet2a, eArtikl.data(), eFurndet.artikl_id, eArtikl.id);
                    for (int index2 = 0; index2 < qDet2a.size(); index2++) {
                        if (qDet2a.get(index2).getInt(eFurndet.id) == deteilID) {
                            selectionRows(qFurn, qDet2a, qDet2b, qDet2c, 0, index1, index2, 0, 0);
                            return;
                        } else {
                            int pk = qDet2a.get(index2).getInt(eFurndet.id);
                            qDet2b.sql(eFurndet.data(), eFurndet.furndet_id, pk, eFurndet.furndet_id, eFurndet.id);
                            qDet2b.query(eArtikl.up).join(qDet2b, eArtikl.data(), eFurndet.artikl_id, eArtikl.id);
                            for (int index3 = 0; index3 < qDet2b.size(); index3++) {
                                if (qDet2b.get(index3).getInt(eFurndet.id) == deteilID) {
                                    selectionRows(qFurn, qDet2a, qDet2b, qDet2c, 1, index1, index2, index3, 0);
                                    return;
                                } else {
                                    pk = qDet2b.get(index3).getInt(eFurndet.id);
                                    qDet2c.sql(eFurndet.data(), eFurndet.furndet_id, pk);
                                    qDet2c.query(eArtikl.up).join(qDet2c, eArtikl.data(), eFurndet.artikl_id, eArtikl.id);
                                    for (int index4 = 0; index4 < qDet2c.size(); index4++) {
                                        if (qDet2c.get(index4).getInt(eFurndet.id) == deteilID) {
                                            selectionRows(qFurn, qDet2a, qDet2b, qDet2c, 2, index1, index2, index3, index4);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("������:Furniturs.deteilFind()");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        group1 = new javax.swing.ButtonGroup();
        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        pan6 = new javax.swing.JPanel();
        north = new javax.swing.JPanel();
        btnIns = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnFind1 = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnFind2 = new javax.swing.JButton();
        btnClone = new javax.swing.JButton();
        center = new javax.swing.JPanel();
        pan1 = new javax.swing.JPanel();
        pan4 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan12 = new javax.swing.JPanel();
        btnTab1 = new javax.swing.JToggleButton();
        btnTab2 = new javax.swing.JToggleButton();
        btnTab3 = new javax.swing.JToggleButton();
        btnSet = new javax.swing.JButton();
        pan5 = new javax.swing.JPanel();
        lab2 = new javax.swing.JLabel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        pan2 = new javax.swing.JPanel();
        pan9 = new javax.swing.JPanel();
        tabb1 = new javax.swing.JTabbedPane();
        scr2a = new javax.swing.JScrollPane();
        tab2a = new javax.swing.JTable();
        scr2c = new javax.swing.JScrollPane();
        tab2c = new javax.swing.JTable();
        scr2b = new javax.swing.JScrollPane();
        tab2b = new javax.swing.JTable();
        pan10 = new javax.swing.JPanel();
        lab1 = new javax.swing.JLabel();
        scr5 = new javax.swing.JScrollPane();
        tab5 = new javax.swing.JTable();
        scr6 = new javax.swing.JScrollPane();
        tab6 = new javax.swing.JTable();
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

        pan6.setPreferredSize(new java.awt.Dimension(800, 300));
        pan6.setLayout(new java.awt.BorderLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("���������");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(900, 640));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Furniturs.this.windowClosed(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(900, 29));

        btnIns.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
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

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
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

        btnFind2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c089.gif"))); // NOI18N
        btnFind2.setToolTipText(bundle.getString("����� ������")); // NOI18N
        btnFind2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnFind2.setFocusable(false);
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

        btnClone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c065.gif"))); // NOI18N
        btnClone.setToolTipText(bundle.getString("����������� ������")); // NOI18N
        btnClone.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnClone.setEnabled(false);
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
                .addGap(18, 18, 18)
                .addComponent(btnClone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 581, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFind1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnDel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnIns, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFind2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClone, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        center.setPreferredSize(new java.awt.Dimension(800, 500));
        center.setLayout(new javax.swing.BoxLayout(center, javax.swing.BoxLayout.PAGE_AXIS));

        pan1.setPreferredSize(new java.awt.Dimension(800, 200));
        pan1.setLayout(new javax.swing.BoxLayout(pan1, javax.swing.BoxLayout.LINE_AXIS));

        pan4.setMaximumSize(new java.awt.Dimension(2000, 800));
        pan4.setMinimumSize(new java.awt.Dimension(153, 112));
        pan4.setPreferredSize(new java.awt.Dimension(500, 200));
        pan4.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);
        scr1.setMinimumSize(new java.awt.Dimension(153, 77));
        scr1.setPreferredSize(new java.awt.Dimension(500, 400));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "��������", "���", "������� �����", "�� ��������", "�����������", "������������", "�/2 ������������", "������ ������������", "������ ������������", "��� ������������", "�������", "�������. ���������", "�����������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, true, true, true, true, true, true, true, false
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
                Furniturs.this.mousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(280);
            tab1.getColumnModel().getColumn(13).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(13).setMaxWidth(60);
        }

        pan4.add(scr1, java.awt.BorderLayout.CENTER);

        pan12.setPreferredSize(new java.awt.Dimension(500, 26));
        pan12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 0));

        group1.add(btnTab1);
        btnTab1.setFont(frames.UGui.getFont(0,0));
        btnTab1.setSelected(true);
        btnTab1.setText("�������� ���������");
        btnTab1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTab1.setMaximumSize(new java.awt.Dimension(180, 26));
        btnTab1.setMinimumSize(new java.awt.Dimension(180, 26));
        btnTab1.setPreferredSize(new java.awt.Dimension(180, 26));
        btnTab1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtnAction(evt);
            }
        });
        pan12.add(btnTab1);

        group1.add(btnTab2);
        btnTab2.setFont(frames.UGui.getFont(0,0));
        btnTab2.setText("�������������� ���������");
        btnTab2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTab2.setMaximumSize(new java.awt.Dimension(180, 26));
        btnTab2.setMinimumSize(new java.awt.Dimension(180, 26));
        btnTab2.setPreferredSize(new java.awt.Dimension(180, 26));
        btnTab2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtnAction(evt);
            }
        });
        pan12.add(btnTab2);

        group1.add(btnTab3);
        btnTab3.setFont(frames.UGui.getFont(0,0));
        btnTab3.setText("������ �������");
        btnTab3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnTab3.setMaximumSize(new java.awt.Dimension(180, 26));
        btnTab3.setMinimumSize(new java.awt.Dimension(180, 26));
        btnTab3.setPreferredSize(new java.awt.Dimension(180, 26));
        btnTab3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtnAction(evt);
            }
        });
        pan12.add(btnTab3);

        btnSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        btnSet.setToolTipText(bundle.getString("������� ������")); // NOI18N
        btnSet.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnSet.setEnabled(false);
        btnSet.setPreferredSize(new java.awt.Dimension(25, 25));
        btnSet.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInser2(evt);
            }
        });
        pan12.add(btnSet);

        pan4.add(pan12, java.awt.BorderLayout.NORTH);

        pan1.add(pan4);

        pan5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 0));
        pan5.setMaximumSize(new java.awt.Dimension(600, 800));
        pan5.setPreferredSize(new java.awt.Dimension(260, 200));
        pan5.setLayout(new javax.swing.BoxLayout(pan5, javax.swing.BoxLayout.PAGE_AXIS));

        lab2.setFont(frames.UGui.getFont(0,0));
        lab2.setText("                  �������� ������");
        lab2.setMaximumSize(new java.awt.Dimension(300, 46));
        lab2.setMinimumSize(new java.awt.Dimension(170, 26));
        lab2.setPreferredSize(new java.awt.Dimension(200, 26));
        pan5.add(lab2);

        scr3.setBorder(null);
        scr3.setPreferredSize(new java.awt.Dimension(0, 100));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������", "����������"
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
                Furniturs.this.mousePressed(evt);
            }
        });
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(80);
        }

        pan5.add(scr3);

        scr4.setBorder(null);
        scr4.setPreferredSize(new java.awt.Dimension(452, 100));

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "��������", "��������"
            }
        ));
        tab4.setFillsViewportHeight(true);
        tab4.setName("tab4"); // NOI18N
        tab4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Furniturs.this.mousePressed(evt);
            }
        });
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(0).setPreferredWidth(220);
            tab4.getColumnModel().getColumn(1).setPreferredWidth(80);
        }

        pan5.add(scr4);

        pan1.add(pan5);

        center.add(pan1);

        pan2.setMaximumSize(new java.awt.Dimension(2600, 800));
        pan2.setPreferredSize(new java.awt.Dimension(800, 300));
        pan2.setLayout(new javax.swing.BoxLayout(pan2, javax.swing.BoxLayout.LINE_AXIS));

        pan9.setMaximumSize(new java.awt.Dimension(2000, 900));
        pan9.setPreferredSize(new java.awt.Dimension(500, 300));
        pan9.setLayout(new javax.swing.BoxLayout(pan9, javax.swing.BoxLayout.PAGE_AXIS));

        tabb1.setFont(frames.UGui.getFont(0,0));
        tabb1.setPreferredSize(new java.awt.Dimension(500, 180));

        tab2a.setFont(frames.UGui.getFont(0,0));
        tab2a.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������", "��������", "��������", "������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
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
        tab2a.setFillsViewportHeight(true);
        tab2a.setName("tab2a"); // NOI18N
        tab2a.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2a.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Furniturs.this.mousePressed(evt);
            }
        });
        scr2a.setViewportView(tab2a);
        if (tab2a.getColumnModel().getColumnCount() > 0) {
            tab2a.getColumnModel().getColumn(0).setPreferredWidth(60);
            tab2a.getColumnModel().getColumn(1).setPreferredWidth(200);
            tab2a.getColumnModel().getColumn(4).setPreferredWidth(40);
            tab2a.getColumnModel().getColumn(4).setMaxWidth(60);
        }

        tabb1.addTab("����������� (1 �������)", scr2a);

        tab2c.setFont(frames.UGui.getFont(0,0));
        tab2c.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������", "��������", "��������", "������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
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
        tab2c.setFillsViewportHeight(true);
        tab2c.setName("tab2c"); // NOI18N
        tab2c.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2c.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Furniturs.this.mousePressed(evt);
            }
        });
        scr2c.setViewportView(tab2c);
        if (tab2c.getColumnModel().getColumnCount() > 0) {
            tab2c.getColumnModel().getColumn(0).setPreferredWidth(60);
            tab2c.getColumnModel().getColumn(1).setPreferredWidth(200);
            tab2c.getColumnModel().getColumn(4).setPreferredWidth(40);
            tab2c.getColumnModel().getColumn(4).setMaxWidth(60);
        }

        tabb1.addTab("����������� (3 �������)", scr2c);

        pan9.add(tabb1);

        scr2b.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "����������� (2 �������)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr2b.setPreferredSize(new java.awt.Dimension(500, 120));

        tab2b.setFont(frames.UGui.getFont(0,0));
        tab2b.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������", "��������", "��������", "������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
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
        tab2b.setFillsViewportHeight(true);
        tab2b.setName("tab2b"); // NOI18N
        tab2b.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab2b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Furniturs.this.mousePressed(evt);
            }
        });
        scr2b.setViewportView(tab2b);
        if (tab2b.getColumnModel().getColumnCount() > 0) {
            tab2b.getColumnModel().getColumn(0).setPreferredWidth(60);
            tab2b.getColumnModel().getColumn(1).setPreferredWidth(200);
            tab2b.getColumnModel().getColumn(4).setPreferredWidth(40);
            tab2b.getColumnModel().getColumn(4).setMaxWidth(60);
        }

        pan9.add(scr2b);

        pan2.add(pan9);

        pan10.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 0));
        pan10.setMaximumSize(new java.awt.Dimension(600, 900));
        pan10.setPreferredSize(new java.awt.Dimension(260, 300));
        pan10.setLayout(new javax.swing.BoxLayout(pan10, javax.swing.BoxLayout.PAGE_AXIS));

        lab1.setFont(frames.UGui.getFont(0,0));
        lab1.setText("                  ����������� ������");
        lab1.setMaximumSize(new java.awt.Dimension(300, 46));
        lab1.setMinimumSize(new java.awt.Dimension(170, 26));
        lab1.setPreferredSize(new java.awt.Dimension(200, 26));
        pan10.add(lab1);

        scr5.setBorder(null);
        scr5.setPreferredSize(new java.awt.Dimension(300, 108));

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������", "���. �����", "����. �����", "���. ����", "����. ����"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tab5.setFillsViewportHeight(true);
        tab5.setName("tab5"); // NOI18N
        tab5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Furniturs.this.mousePressed(evt);
            }
        });
        scr5.setViewportView(tab5);
        if (tab5.getColumnModel().getColumnCount() > 0) {
            tab5.getColumnModel().getColumn(0).setPreferredWidth(180);
        }

        pan10.add(scr5);

        scr6.setBorder(null);
        scr6.setPreferredSize(new java.awt.Dimension(300, 200));

        tab6.setFont(frames.UGui.getFont(0,0));
        tab6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "��������", "��������"
            }
        ));
        tab6.setFillsViewportHeight(true);
        tab6.setName("tab6"); // NOI18N
        tab6.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Furniturs.this.mousePressed(evt);
            }
        });
        scr6.setViewportView(tab6);
        if (tab6.getColumnModel().getColumnCount() > 0) {
            tab6.getColumnModel().getColumn(0).setPreferredWidth(220);
            tab6.getColumnModel().getColumn(1).setPreferredWidth(80);
        }

        pan10.add(scr6);

        pan2.add(pan10);

        center.add(pan2);

        getContentPane().add(center, java.awt.BorderLayout.CENTER);

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
            if (UGui.isDeleteRecord(tab1, this, tab2a, tab2b, tab2c, tab3) == 0) {
                UGui.deleteRecord(tab1);
            }
        } else if (tab2a.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2a, this, tab2b, tab2c, tab5, tab6) == 0) {
                UGui.deleteRecord(tab2a);
            }
        } else if (tab2b.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2b, this, tab2c, tab5, tab6) == 0) {
                UGui.deleteRecord(tab2b);
            }
        } else if (tab2c.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2c, this, tab5, tab6) == 0) {
                UGui.deleteRecord(tab2c);
            }
        } else if (tab3.getBorder() != null) {
            if (UGui.isDeleteRecord(tab3, this, tab4) == 0) {
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
        } else if (tab6.getBorder() != null) {
            if (UGui.isDeleteRecord(tab6, this) == 0) {
                UGui.deleteRecord(tab6);
            }
        }
    }//GEN-LAST:event_btnDelete

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert
        if (tab1.getBorder() != null) {
            UGui.insertRecordCur(tab1, eFurniture.up, (record) -> {
                int types = (btnTab1.isSelected()) ? 0 : (btnTab2.isSelected()) ? 1 : -1;
                record.setDev(eFurniture.name, (btnTab1.isSelected()) ? "���. ����." : (btnTab2.isSelected()) ? "���. ����." : "����� ����.");
                record.set(eFurniture.types, types);
                List.of(eFurniture.max_height, eFurniture.max_width, eFurniture.max_p2).forEach(field -> record.set(field, 3000));
                record.set(eFurniture.max_weight, 300);
                record.set(eFurniture.ways_use, UseFurn2.P2.id);
            });

        } else if (tab2a.getBorder() != null && tab5.getBorder() == null && tab6.getBorder() == null) {
            if (UGui.getIndexRec(tab1) != -1) {
                UGui.insertRecordCur(tab2a, eFurndet.up, (record) -> {
                    int id = qFurniture.getAs(UGui.getIndexRec(tab1), eFurniture.id);
                    record.set(eFurndet.furniture_id1, id);
                    record.set(eFurndet.furndet_id, record.getInt(eFurndet.id));
                });
            } else {
                JOptionPane.showMessageDialog(this, "������� ��������� �������� �������", "��������������", JOptionPane.NO_OPTION);
            }

        } else if (tab2b.getBorder() != null && tab5.getBorder() == null && tab6.getBorder() == null) {
            if (UGui.getIndexRec(tab1) != -1 && UGui.getIndexRec(tab2a) != -1) {
                UGui.insertRecordCur(tab2b, eFurndet.up, (record) -> {
                    int id = qFurniture.getAs(UGui.getIndexRec(tab1), eFurniture.id);
                    int pk = qFurndet2a.getAs(UGui.getIndexRec(tab2a), eFurndet.id);
                    record.set(eFurndet.furniture_id1, id);
                    record.set(eFurndet.furndet_id, pk);
                });
            } else {
                JOptionPane.showMessageDialog(this, "������� ��������� �������� �������", "��������������", JOptionPane.NO_OPTION);
            }

        } else if (tab2c.getBorder() != null && tab5.getBorder() == null && tab6.getBorder() == null) {
            if (UGui.getIndexRec(tab1) != -1 && UGui.getIndexRec(tab2b) != -1) {
                UGui.insertRecordCur(tab2c, eFurndet.up, (record) -> {
                    int id = qFurniture.getAs(UGui.getIndexRec(tab1), eFurniture.id);
                    int pk = qFurndet2b.getAs(UGui.getIndexRec(tab2b), eFurndet.id);
                    record.set(eFurndet.furniture_id1, id);
                    record.set(eFurndet.furndet_id, pk);
                });
            } else {
                JOptionPane.showMessageDialog(this, "������� ��������� �������� �������", "��������������", JOptionPane.NO_OPTION);
            }

        } else if (tab3.getBorder() != null) {
            UGui.insertRecordCur(tab3, eFurnside1.up, (record) -> {
                int id = qFurniture.getAs(UGui.getIndexRec(tab1), eFurniture.id);
                record.set(eFurnside1.furniture_id, id);
            });

        } else if (tab4.getBorder() != null) {
            UGui.insertRecordCur(tab4, eFurnpar1.up, (record) -> {
                int id = qFurnside1.getAs(UGui.getIndexRec(tab3), eFurnside1.id);
                record.set(eFurnpar1.furnside_id, id);
            });
            DefCellEditorBtn defCellEditorBtn = (DefCellEditorBtn) tab4.getColumnModel().getColumn(0).getCellEditor();
            defCellEditorBtn.getButton().getActionListeners()[0].actionPerformed(null);

        } else if (tab5.getBorder() != null) {
            JTable table = (UGui.getIndexRec(tab2c) != -1) ? tab2c
                    : (UGui.getIndexRec(tab2b) != -1) ? tab2b : tab2a; //�����! ����� ��������� ������ ����� �����.
            Query query = ((DefTableModel) table.getModel()).getQuery();
            if (UGui.getIndexRec(table) != -1) {
                UGui.insertRecordCur(tab5, eFurnside2.up, (record) -> {
                    int id = query.getAs(UGui.getIndexRec(table), eFurndet.id);
                    record.set(eFurnside2.furndet_id, id);
                    record.set(eFurnside2.ang_min, 0);
                    record.set(eFurnside2.ang_max, 360);
                });
            }
        } else if (tab6.getBorder() != null) {
            Object name = tab2X.getName();
            Query query = ((DefTableModel) tab2X.getModel()).getQuery();
            if (UGui.getIndexRec(tab2X) != -1) {
                UGui.insertRecordCur(tab6, eFurnpar2.up, (record) -> {
                    int id = query.getAs(UGui.getIndexRec(tab2X), eFurndet.id);
                    record.set(eFurnpar2.furndet_id, id);
                });
            }
            DefCellEditorBtn defCellEditorBtn = (DefCellEditorBtn) tab6.getColumnModel().getColumn(0).getCellEditor();
            defCellEditorBtn.getButton().getActionListeners()[0].actionPerformed(null);

        }
    }//GEN-LAST:event_btnInsert

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());
    }//GEN-LAST:event_windowClosed

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        RTable.load("���������", tab1);
        ExecuteCmd.documentType(this);
    }//GEN-LAST:event_btnReport

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        JTable table = (JTable) evt.getSource();
        //UGui.setSelectionBackground(table, tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6);
        UGui.updateBorderAndSql(table, List.of(tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6));
        btnClone.setEnabled(false);
        btnSet.setEnabled(false);
        if (table == tab1) {
            btnClone.setEnabled(true);
        } else if (table == tab2a) {
            btnClone.setEnabled(true);
            tab2X = tab2a;
            btnSet.setEnabled(true);
            selectionTab2a(null);
        } else if (table == tab2b) {
            tab2X = tab2b;
            btnSet.setEnabled(true);
            selectionTab2b(null);
        } else if (table == tab2c) {
            tab2X = tab2c;
            btnSet.setEnabled(true);
            selectionTab2c(null);
        }
    }//GEN-LAST:event_mousePressed

    private void btnFind1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind1
        JTable table = (UGui.getIndexRec(tab2c) != -1) ? tab2c
                : (UGui.getIndexRec(tab2b) != -1) ? tab2b : tab2a; //�����! ����� ��������� ������ ����� �����.
        Record record = ((DefTableModel) table.getModel()).getQuery().get(UGui.getIndexRec(table));
        Record record2 = qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == record.getInt(eFurndet.artikl_id)).findFirst().orElse(eFurndet.up.newRecord(Query.SEL));
        ProgressBar.create(this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                App.Artikles.createFrame(Furniturs.this, record2);
            }
        });
    }//GEN-LAST:event_btnFind1

    private void tbtnAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbtnAction
        JTable table = null;
        if (tab2a.getBorder() != null && tab5.getBorder() == null && tab6.getBorder() == null) {
            table = tab2a;
        } else if (tab2b.getBorder() != null && tab5.getBorder() == null && tab6.getBorder() == null) {
            table = tab2b;
        } else if (tab2c.getBorder() != null && tab5.getBorder() == null && tab6.getBorder() == null) {
            table = tab2c;
        }
        int index = (table == null) ? -1 : UGui.getIndexRec(table);
        Integer furnitureId = (index == -1) ? null : ((DefTableModel) table.getModel()).getQuery().getAs(index, eFurndet.furniture_id2);
        List.of(tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());

        loadingData();

        ((DefaultTableModel) tab1.getModel()).fireTableDataChanged();
        if (btnTab1.isSelected()) {
            UGui.setSelectedIndex(tab1, indexT1);
            Rectangle cellRect = tab1.getCellRect(indexT1, 0, false);
            tab1.scrollRectToVisible(cellRect);
        } else {
            UGui.setSelectedRow(tab1);
        }

        //���� ������� �� �����, ����� ���� ���
        if (btnTab3.isSelected() && furnitureId != null) {

            for (int index2 = 0; index2 < qFurniture.size(); ++index2) {
                Record record = qFurniture.get(index2);
                if (record.getInt(eFurniture.id) == furnitureId) {
                    UGui.setSelectedIndex(tab1, index2);
                    Rectangle cellRect = tab1.getCellRect(index2, 0, false);
                    tab1.scrollRectToVisible(cellRect);
                }
            }
        }
    }//GEN-LAST:event_tbtnAction

    private void btnInser2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInser2
        try {
            List list = new LinkedList();
            for (Record record : qFurnall) {
                if (record.getInt(eFurniture.types) == -1) {
                    list.add(record.getStr(eFurniture.name));
                }
            }
            Object result = JOptionPane.showInputDialog(Furniturs.this, "����� ������",
                    "������", JOptionPane.QUESTION_MESSAGE, null, list.toArray(), list.toArray()[0]);
            if (result != null) {
                for (Record record2 : qFurnall) {
                    if (result.equals(record2.get(eFurniture.name))) {

                        if (tab2a.getBorder() != null && tab5.getBorder() == null && tab6.getBorder() == null) {
                            UGui.insertRecordCur(tab2a, eFurndet.up, (record) -> {
                                int id = qFurniture.getAs(UGui.getIndexRec(tab1), eFurniture.id);
                                record.set(eFurndet.furniture_id1, id);
                                record.set(eFurndet.furniture_id2, record2.getInt(eFurniture.id));
                                record.set(eFurndet.furndet_id, record.getInt(eFurndet.id));
                                record.set(eFurndet.color_fk, null);
                                record.set(eFurndet.color_us, null);
                            });

                        } else if (tab2b.getBorder() != null && tab5.getBorder() == null && tab6.getBorder() == null) {
                            UGui.insertRecordCur(tab2b, eFurndet.up, (record) -> {
                                int furnitureID = qFurniture.getAs(UGui.getIndexRec(tab1), eFurniture.id);
                                int furndet2aPK = qFurndet2a.getAs(UGui.getIndexRec(tab2a), eFurndet.id);
                                record.set(eFurndet.furniture_id1, furnitureID);
                                record.set(eFurndet.furniture_id2, record2.getInt(eFurniture.id));
                                record.set(eFurndet.furndet_id, furndet2aPK);
                                record.set(eFurndet.color_fk, null);
                                record.set(eFurndet.color_us, null);
                            });
                        } else {
                            JOptionPane.showMessageDialog(null, "������� ��������� �������� �������", "��������������", JOptionPane.NO_OPTION);
                        }
                    }
                }

            }
        } catch (Exception e) {
            System.err.println("������:Furniturs.btnInser2()");
        }
    }//GEN-LAST:event_btnInser2

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest
    }//GEN-LAST:event_btnTest

    private void btnFind2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind2
        List<String> pathList = new ArrayList<String>();
        List<Integer> keyList = new ArrayList<Integer>();
        StringBuffer bufferPath = new StringBuffer();

        int index = UGui.getIndexRec(tab1);
        Record furnitureRec = qFurniture.get(index);
        List<Record> sysfurnList1 = eSysfurn.data().stream().filter(rec -> furnitureRec.getInt(eFurniture.id) == rec.getInt(eSysfurn.furniture_id)).collect(Collectors.toList());
        Set<Integer> systreeList = new HashSet<Integer>();
        sysfurnList1.forEach(rec -> systreeList.add(rec.getInt(eSysfurn.systree_id)));

        for (Record rec : eSystree.data()) {
            if (systreeList.contains(rec.get(eSystree.id))) {
                bufferPath = bufferPath.append(rec.getStr(eSystree.name));
                findPathSystree(rec, bufferPath);
                pathList.add(bufferPath.toString());
                keyList.add(rec.getInt(eSystree.id));
                bufferPath.delete(0, bufferPath.length());
            }
        }
        Object[] pathArr = pathList.stream().sorted().toArray();
        if (pathList.size() != 0) {
            for (int i = pathList.size(); i < 21; ++i) {
                pathList.add(null);
            }
            Object result = JOptionPane.showInputDialog(Furniturs.this, "��������� � ������ ������ ��������", "����� ������ ��������",
                    JOptionPane.QUESTION_MESSAGE, new ImageIcon(getClass().getResource("/resource/img24/c066.gif")), pathArr, pathArr[0]);

            if (result != null || result instanceof Integer) {
                for (int i = 0; i < pathList.size(); ++i) {
                    if (result.equals(pathList.get(i))) {
                        Object id = keyList.get(i);
                        ProgressBar.create(Furniturs.this, new ListenerFrame() {
                            public void actionRequest(Object obj) {
                                App.Systree.createFrame(Furniturs.this, id, 2);
                            }
                        });
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(Furniturs.this, "� ������� �������� ��������� �� �������", "���������", JOptionPane.NO_OPTION);
        }
    }//GEN-LAST:event_btnFind2

    private void cloneRecord(Map<Record, Integer> furnside2Map, Map<Record, Integer> furnpar2Map) {
        for (Map.Entry<Record, Integer> it : furnside2Map.entrySet()) {
            Record furnside2Rec = it.getKey();
            Record furnside2Clon = (Record) furnside2Rec.clone();
            furnside2Clon.setNo(eFurnside2.up, Query.INS);
            furnside2Clon.setNo(eFurnside2.id, Connect.genId(eFurnside2.up));
            furnside2Clon.setNo(eFurnside2.furndet_id, it.getValue());
            qFurnside2.add(furnside2Clon);
        }
        for (Map.Entry<Record, Integer> it : furnpar2Map.entrySet()) {
            Record furnpar2Rec = it.getKey();
            Record furnpar2Clon = (Record) furnpar2Rec.clone();
            furnpar2Clon.setNo(eFurnpar2.up, Query.INS);
            furnpar2Clon.setNo(eFurnpar2.id, Connect.genId(eFurnpar2.up));
            furnpar2Clon.setNo(eFurnpar2.furndet_id, it.getValue());
            qFurnpar2.add(furnpar2Clon);
        }
    }

    private void btnClone(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClone
        UIManager.put("OptionPane.noButtonText", "���");
        int result = JOptionPane.showConfirmDialog(this, "����������� ������ �������� ������?",
                "�������������", JOptionPane.YES_NO_CANCEL_OPTION);

        //UIManager.put("OptionPane.cancelButtonText", "nope");
        //UIManager.put("OptionPane.okButtonText", "yup");
        if (result != JOptionPane.CANCEL_OPTION) {

            if (tab1.getBorder() != null) {
                int types = (btnTab1.isSelected()) ? 0 : (btnTab2.isSelected()) ? 1 : -1;
                List<Record> dataDet2a = new ArrayList(qFurndet2a);
                List<Record> dataSide1 = new ArrayList(qFurnside1);

                Record masterClon = UGui.cloneMaster(qFurniture, tab1, eFurniture.up, (clon) -> {
                    clon.set(eFurniture.name, clon.getStr(eFurniture.name) + "-clon");
                    clon.set(eFurniture.types, types);
                });
                if (result == JOptionPane.NO_OPTION) {
                    //UGui.cloneSlave(qFurndet2a, tab3, eFurndet.up, dataDet2a, (clon) -> {
                    //    clon.setNo(eFurndet.furniture_id1, masterClon.getInt(eFurniture.id));
                    //    clon.setNo(eFurndet.furndet_id, clon.getInt(eFurndet.id));
                    //});
                    UGui.cloneSlave(qFurnside1, tab3, eFurnside1.up, dataSide1, (clon) -> {
                        clon.setNo(eFurnside1.furniture_id, masterClon.getInt(eFurniture.id));
                    });
                }
            } else if (tab2a.getBorder() != null) {
                List<Record> dataDet2b = new ArrayList(qFurndet2b);
                List<Record> dataSide2 = new ArrayList(qFurnside2);
                List<Record> dataPar2 = new ArrayList(qFurnpar2);

                Record masterClon = qFurniture.get(UGui.getIndexRec(tab1));
                Record masterClonA = UGui.cloneMaster(qFurndet2a, tab2a, eFurndet.up, (clon) -> {
                    clon.setNo(eFurndet.furndet_id, clon.getInt(eFurndet.id));
                });
                if (result == JOptionPane.NO_OPTION) {
                    UGui.cloneSlave(qFurndet2b, tab2b, eFurndet.up, dataDet2b, (clon) -> {
                        clon.setNo(eFurndet.furniture_id1, masterClon.getInt(eFurniture.id));
                        clon.setNo(eFurndet.furndet_id, masterClonA.getInt(eFurndet.id));
                    });
                    UGui.cloneSlave(qFurnside2, tab5, eFurnside2.up, dataSide2, (clon) -> {
                        clon.setNo(eFurnside2.furndet_id, masterClonA.getInt(eFurndet.id));
                    });
                    UGui.cloneSlave(qFurnpar2, tab6, eFurnpar2.up, dataPar2, (clon) -> {
                        clon.setNo(eFurnpar2.furndet_id, masterClonA.getInt(eFurndet.id));
                    });
                }
            } else if (tab2b.getBorder() != null) {
                List<Record> dataDet2c = new ArrayList(qFurndet2c);
                List<Record> dataSide2 = new ArrayList(qFurnside2);
                List<Record> dataPar2 = new ArrayList(qFurnpar2);

                Record masterClon = qFurniture.get(UGui.getIndexRec(tab1));
                Record masterClonA = qFurndet2a.get(UGui.getIndexRec(tab2a));
                Record masterClonB = UGui.cloneMaster(qFurndet2b, tab2b, eFurndet.up, (clon) -> {
                    clon.setNo(eFurndet.furniture_id1, masterClon.getInt(eFurniture.id));
                    clon.setNo(eFurndet.furndet_id, masterClonA.getInt(eFurndet.id));
                });
                if (result == JOptionPane.NO_OPTION) {
                    UGui.cloneSlave(qFurndet2c, tab2c, eFurndet.up, dataDet2c, (clon) -> {
                        clon.setNo(eFurndet.furniture_id1, masterClon.getInt(eFurniture.id));
                        clon.setNo(eFurndet.furndet_id, masterClonB.getInt(eFurndet.id));
                    });
                    UGui.cloneSlave(qFurnside2, tab5, eFurnside2.up, dataSide2, (clon) -> {
                        clon.setNo(eFurnside2.furndet_id, masterClonB.getInt(eFurndet.id));
                    });
                    UGui.cloneSlave(qFurnpar2, tab6, eFurnpar2.up, dataPar2, (clon) -> {
                        clon.setNo(eFurnpar2.furndet_id, masterClonB.getInt(eFurndet.id));
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
            JTable table = List.of(tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());

        } else if (evt.getButton() == MouseEvent.BUTTON1) {
            List.of(btnFind1, btnFind2, btnClone).forEach(btn -> btn.setEnabled(false));
            if (tab1.getBorder() != null) {
                List.of(btnFind2, btnClone).forEach(btn -> btn.setEnabled(true));
            } else if (tab2a.getBorder() != null
                    || tab2b.getBorder() != null || tab2c.getBorder() != null) {
                List.of(btnFind1, btnClone).forEach(btn -> btn.setEnabled(true));
            }
        }
    }//GEN-LAST:event_tabMouseClicked

    private void findPathSystree(Record record, StringBuffer path) {
        for (Record rec : eSystree.data()) {
            if (record.getInt(eSystree.parent_id) == rec.getInt(eSystree.id)) {
                path.insert(0, rec.getStr(eSystree.name) + "->");
                if (rec.getInt(eSystree.id) != rec.getInt(eSystree.parent_id)) {
                    findPathSystree(rec, path);
                }
            }
        }
    }
// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClone;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnFind1;
    private javax.swing.JButton btnFind2;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSet;
    private javax.swing.JToggleButton btnTab1;
    private javax.swing.JToggleButton btnTab2;
    private javax.swing.JToggleButton btnTab3;
    private javax.swing.JButton btnTest;
    private javax.swing.JPanel center;
    private javax.swing.ButtonGroup group1;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab2;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan10;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan9;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2a;
    private javax.swing.JScrollPane scr2b;
    private javax.swing.JScrollPane scr2c;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JScrollPane scr6;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2a;
    private javax.swing.JTable tab2b;
    private javax.swing.JTable tab2c;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JTable tab5;
    private javax.swing.JTable tab6;
    private javax.swing.JTabbedPane tabb1;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 
    private void initElements() {

        btnTest.setVisible(eProp.devel.equals("99"));
        App.loadLocationWin(this, btnClose, (e) -> {
            App.saveLocationWin(this, btnClose);
        });

        TableFieldFilter filterTable = new TableFieldFilter(0, tab1, tab2a, tab2b, tab2c, tab4, tab6);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        List.of(btnIns, btnDel).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab1, tab2a, tab2b, tab2c, tab3, tab4, tab5, tab6)));
        DefaultTableColumnModel columnModel = (DefaultTableColumnModel) tab1.getColumnModel();
        for (int i = 1; i < 6; ++i) {
            columnModel.getColumn(i).setMinWidth(0);
            columnModel.getColumn(i).setMaxWidth(0);
        }
        tab1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab1(event);
                }
            }
        });
        tab2a.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab2a(event);
                }
            }
        });
        tab2b.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab2b(event);
                }
            }
        });
        tab2c.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab2c(event);
                }
            }
        });
        tab3.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab3(event);
                }
            }
        });

        if ("Nimbus".equals(eProp.lookandfeel.getProp())) {
            lab1.setPreferredSize(new java.awt.Dimension(200, 34));
        }
    }
}
