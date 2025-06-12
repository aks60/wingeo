package frames;

import frames.swing.comp.ProgressBar;
import frames.dialog.DicArtikl;
import dataset.Connect;
import enums.Enam;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import frames.dialog.DicColvar;
import frames.dialog.DicGroups;
import frames.dialog.DicTypset;
import frames.dialog.ParColor;
import frames.dialog.ParName;
import frames.dialog.ParSysVal;
import frames.dialog.ParUserVal;
import domain.eArtikl;
import domain.eColor;
import domain.eParams;
import domain.eElemdet;
import domain.eElement;
import domain.eElempar1;
import domain.eElempar2;
import domain.eGroups;
import domain.eJoindet;
import builder.param.ParamList;
import common.eProp;
import enums.TypeGrup;
import enums.TypeSet;
import enums.UseColor;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import frames.swing.comp.DefCellRendererBool;
import frames.swing.comp.DefTableModel;
import java.util.Set;
import java.util.stream.Collectors;
import startup.App;
import common.listener.ListenerRecord;
import common.listener.ListenerFrame;
import domain.eArtdet;
import static domain.eArtikl.groups1_id;
import static domain.eArtikl.groups2_id;
import domain.eParmap;
import domain.eSysprof;
import domain.eSystree;
import frames.dialog.DicArtikl2;
import frames.swing.comp.DefCellEditorBtn;
import frames.swing.comp.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import report.sup.ExecuteCmd;
import report.sup.RTable;

public class Elements extends javax.swing.JFrame {

    private Query qGroups = new Query(eGroups.values());
    private Query qGrCateg = new Query(eGroups.values());
    private Query qColor = new Query(eColor.id, eColor.groups_id, eColor.name);
    private Query qElement = new Query(eElement.values(), eArtikl.values());
    private Query qElemdet = new Query(eElemdet.values(), eArtikl.values());
    private Query qElempar1 = new Query(eElempar1.values());
    private Query qElempar2 = new Query(eElempar2.values());
    private ListenerRecord listenerArtikl, listenerTypset, listenerSeries, listenerGroups, listenerColor, listenerColvar1, listenerColvar2, listenerColvar3;

    public Elements() {
        initComponents();
        initElements();
        listenerSet();
        loadingData();
        loadingModel();
        listenerAdd();
    }

    public Elements(int deteilID) {
        initComponents();
        initElements();
        listenerSet();
        loadingData();
        loadingModel();
        listenerAdd();
        deteilFind(deteilID);
    }

    public void loadingData() {

        qColor.sql(eColor.data(), eColor.up);
        qGrCateg.sql(eGroups.data(), eGroups.grup, TypeGrup.CATEG_VST.id).sort(eGroups.npp, eGroups.name);
        qGroups.sql(eGroups.data(), eGroups.grup, TypeGrup.CATEG_VST.id, TypeGrup.SERI_ELEM.id,
                 TypeGrup.PARAM_USER.id, TypeGrup.COLOR_MAP.id, TypeGrup.GROUP_VST.id).sort(eGroups.npp, eGroups.name);

        Record groups1Rec = eGroups.up.newRecord(Query.SEL);
        groups1Rec.setNo(eGroups.id, -1);
        groups1Rec.setNo(eGroups.npp, 1);
        groups1Rec.setNo(eGroups.name, "<html><font size='3' color='red'>&nbsp;&nbsp;&nbsp;ПРОФИЛИ</font>");
        qGrCateg.add(0, groups1Rec); //ПРОФИЛИ
        //Цыкл по категориям
        for (int i = 0; i < qGrCateg.size(); ++i) {
            Record groups2Rec = qGrCateg.get(i);
            if (groups2Rec.getInt(eGroups.npp) == 5) {
                Record groups3Rec = eGroups.up.newRecord(Query.SEL);
                groups3Rec.setNo(eGroups.id, -5);
                groups3Rec.setNo(eGroups.npp, 5);
                groups3Rec.setNo(eGroups.name, "<html><font size='3' color='red'>&nbsp;&nbsp;ЗАПОЛНЕНИЯ</font>");
                qGrCateg.add(i, groups3Rec); //ЗАПОЛНЕНИЯ
                break;
            }
        }
    }

    public void loadingModel() {

        tab1.getTableHeader().setEnabled(false);
        new DefTableModel(tab1, qGrCateg, eGroups.name);
        new DefTableModel(tab2, qElement, eArtikl.code, eArtikl.name, eElement.name, eElement.typset,
                 eElement.signset, eElement.groups1_id, eElement.groups3_id, eElement.todef, eElement.toset, eElement.markup) {

            public Object getValueAt(int col, int row, Object val) {

                if (val != null && columns[col] == eElement.typset) {
                    int typset = Integer.valueOf(val.toString());
                    return List.of(TypeSet.values()).stream().filter(el -> el.id == typset).findFirst().orElse(TypeSet.P1).name;

                } else if (val != null && columns[col] == eElement.groups1_id) {
                    return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).get(eGroups.name);

                } else if (val != null && columns[col] == eElement.groups3_id) {
                    return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).get(eGroups.name);
                }
                return val;
            }
        };
        new DefTableModel(tab3, qElemdet, eArtikl.code, eArtikl.name, eElemdet.color_fk, eElemdet.color_us, eElemdet.color_us, eElemdet.color_us) {

            public Object getValueAt(int col, int row, Object val) {

                if (val != null) {
                    Field field = columns[col];
                    if (eElemdet.color_fk == field) {
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

                    } else if (eElemdet.color_us == field) {
                        int types = Integer.valueOf(String.valueOf(val));
                        types = (col == 3) ? types & 0x0000000f : (col == 4) ? (types & 0x000000f0) >> 4 : (types & 0x00000f00) >> 8;
                        return UseColor.MANUAL.find(types).text();
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab4, qElempar1, eElempar1.groups_id, eElempar1.text) {

            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && eElempar1.groups_id == field) {

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
        new DefTableModel(tab5, qElempar2, eElempar2.groups_id, eElempar2.text) {

            public Object getValueAt(int col, int row, Object val) {
                if (val != null) {
                    Field field = columns[col];
                    if (field == eElempar2.groups_id) {

                        if (Integer.valueOf(String.valueOf(val)) < 0) {
                            return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).getDev(eGroups.name, val);
                        } else {
                            Enam en = ParamList.find(val);
                            return Record.getDev(en.numb(), en.text());
                        }
                    }
                }
                return val;
            }
        };
        List.of(7, 8).forEach(index -> tab2.getColumnModel().getColumn(index).setCellRenderer(new DefCellRendererBool()));
        UGui.setSelectedRow(tab1);
    }

    public void selectionTab1(ListSelectionEvent event) {
        try {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            List.of(tab1, tab2, tab3, tab4, tab5).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
            UGui.clearTable(tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record record = qGrCateg.get(index);
                int id = record.getInt(eGroups.id);

                if (id == -1 || id == -5) { //(-1) - профили, (-5) - заполнения
                    eElement.sql(qElement, qElement.query(eArtikl.up), id);
                    //qElement.sql(eElement.data(), eElement.groups2_id, id).sort(eElement.name);
                    //qElement.table(eArtikl.up).join(qElement, eArtikl.data(), eElement.artikl_id, eArtikl.id);               
                } else { //категории
                    qElement.sql(eElement.data(), eElement.groups2_id, id).sort(eElement.name);
                    qElement.query(eArtikl.up).join(qElement, eArtikl.data(), eElement.artikl_id, eArtikl.id);
                }
                ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
                UGui.setSelectedRow(tab2);
            }
        } catch (Exception e) {
            System.out.println("Ошиибка:Elements.selectionTab1() " + e);
        }
    }

    public void selectionTab2(ListSelectionEvent event) {
        try {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            List.of(tab1, tab2, tab3, tab4, tab5).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
            UGui.clearTable(tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab2);
            if (index != -1) {
                Record record = qElement.query(eElement.up).get(index);
                Integer ID = record.getInt(eElement.id);
                qElemdet.sql(eElemdet.data(), eElemdet.element_id, ID);
                qElemdet.query(eArtikl.up).join(qElemdet, eArtikl.data(), eElemdet.artikl_id, eArtikl.id);
                qElempar1.sql(eElempar1.data(), eElempar1.element_id, ID);
                ((DefaultTableModel) tab3.getModel()).fireTableDataChanged();
                ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
                UGui.setSelectedRow(tab3);
                UGui.setSelectedRow(tab4);
            }
        } catch (Exception e) {
            System.out.println("Ошиибка:Elements.selectionTab2() " + e);
        }
    }

    public void selectionTab3(ListSelectionEvent event) {
        UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
        List.of(tab1, tab2, tab3, tab4, tab5).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        int index = UGui.getIndexRec(tab3);
        if (index != -1) {
            //Util.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            List.of(qElempar2).forEach(q -> q.execsql());
            Record record = qElemdet.query(eElemdet.up).get(index);
            Integer p1 = record.getInt(eElemdet.id);
            qElempar2.sql(eElempar2.data(), eElempar2.elemdet_id, p1);
            ((DefaultTableModel) tab5.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab5);
        }
    }

    public void listenerAdd() {
        UGui.buttonCellEditor(tab2, 0).addActionListener(event -> {
            int level = qGrCateg.getAs(UGui.getIndexRec(tab1), eGroups.npp);
            new DicArtikl(this, listenerArtikl, false, level);
        });

        UGui.buttonCellEditor(tab2, 1).addActionListener(event -> {
            int level = qGrCateg.getAs(UGui.getIndexRec(tab1), eGroups.npp);
            new DicArtikl(this, listenerArtikl, false, level);
        });

        UGui.buttonCellEditor(tab2, 3).addActionListener(event -> {
            DicTypset frame = new DicTypset(this, listenerTypset);
        });

        UGui.buttonCellEditor(tab2, 5).addActionListener(event -> {
            int index = UGui.getIndexRec(tab2);
            if (index != -1) {
                int id = qElement.getAs(index, eElement.groups1_id);
                new DicGroups(this, listenerSeries, TypeGrup.SERI_ELEM, id, true);
            }
        });

        UGui.buttonCellEditor(tab2, 6).addActionListener(event -> {
            int index = UGui.getIndexRec(tab2);
            if (index != -1) {
                int id = qElement.getAs(index, eElement.groups3_id);
                new DicGroups(this, listenerGroups, TypeGrup.GROUP_VST, id, true);
            }
        });

        UGui.buttonCellEditor(tab3, 0).addActionListener(event -> {
            new DicArtikl2(this, listenerArtikl, 1, 2, 3, 4, 5);
        });

        UGui.buttonCellEditor(tab3, 1).addActionListener(event -> {
            new DicArtikl2(this, listenerArtikl, 1, 2, 3, 4, 5);
        });

        UGui.buttonCellEditor(tab3, 2).addActionListener(event -> {
            Record record = qElemdet.get(UGui.getIndexRec(tab3));
            int artikID = record.getInt(eElemdet.artikl_id);
            int colorFK = record.getInt(eElemdet.color_fk, -1);
            new ParColor(this, listenerColor, eParmap.elem, artikID, colorFK);
        });

        UGui.buttonCellEditor(tab3, 3).addActionListener(event -> {
            Record record = qElemdet.get(UGui.getIndexRec(tab3));
            int colorFk = record.getInt(eElemdet.color_fk);
            new DicColvar(this, listenerColvar1, colorFk);
        });

        UGui.buttonCellEditor(tab3, 4).addActionListener(event -> {
            Record record = qElemdet.get(UGui.getIndexRec(tab3));
            int colorFk = record.getInt(eElemdet.color_fk);
            new DicColvar(this, listenerColvar2, colorFk);
        });

        UGui.buttonCellEditor(tab3, 5).addActionListener(event -> {
            Record record = qElemdet.get(UGui.getIndexRec(tab3));
            int colorFk = record.getInt(eElemdet.color_fk);
            new DicColvar(this, listenerColvar3, colorFk);
        });

        UGui.buttonCellEditor(tab4, 0).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            Record record = qGrCateg.get(UGui.getIndexRec(tab1));
            int paramPart = record.getInt(eGroups.npp);
            paramPart = (paramPart == 1) ? 31000 : 37000;
            if (qElempar1.get(UGui.getIndexRec(tab4), eElempar1.groups_id) == null) {
                new ParName(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab4, eElempar1.groups_id, eElempar1.text);
                }, eParams.elem, paramPart);
            } else {
                int groupsID = qElempar1.getAs(UGui.getIndexRec(tab4), eElempar1.groups_id);
                new ParName(this, groupsID, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab4, eElempar1.groups_id, eElempar1.text);
                }, eParams.elem, paramPart);
            }
        });

        UGui.buttonCellEditor(tab4, 1, (componentCell) -> { //первый слушатель кнопки, редактирование типа данных и вида ячейки
            return UGui.cellParamTypeOrVid(tab4, componentCell, eElempar1.groups_id);

        }).addActionListener(event -> { //второй слушатель кнопки, выбор из справочника значения
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            Record record = qElempar1.get(UGui.getIndexRec(tab4));
            int grup = record.getInt(eElempar1.groups_id);

            if (grup < 0) {
                new ParUserVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab4, eElempar1.groups_id, eElempar1.text);
                }, eParams.elem, grup);
            } else {
                List list = ParamList.find(grup).dict();
                new ParSysVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab4, eElempar1.groups_id, eElempar1.text);
                }, list);
            }
        });

        UGui.buttonCellEditor(tab5, 0).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            int index = UGui.getIndexRec(tab3);
            if (index != -1) {
                int levelGrp = qGrCateg.getAs(UGui.getIndexRec(tab1), eGroups.npp);
                Record recordDet = qElemdet.get(index);
                int artikl_id = recordDet.getInt(eJoindet.artikl_id);
                Record recordArt = eArtikl.find(artikl_id, false);
                int level = recordArt.getInt(eArtikl.level1);
                Integer[] part1 = {0, 34000, 33000, 34000, 33000, 40000, 0};
                Integer[] part2 = {0, 39000, 38000, 39000, 38000, 40000, 0};
                int grup = (levelGrp == 1) ? part1[level] : part2[level];
                if (qElempar2.get(UGui.getIndexRec(tab5), eElempar2.groups_id) == null) {
                    new ParName(this, (rec) -> {
                        UGui.cellParamNameOrValue(rec, tab5, eElempar2.groups_id, eElempar2.text);
                    }, eParams.elem, grup);
                } else {
                    int groupsID = qElempar2.getAs(UGui.getIndexRec(tab5), eElempar2.groups_id);
                    new ParName(this, groupsID, (rec) -> {
                        UGui.cellParamNameOrValue(rec, tab5, eElempar2.groups_id, eElempar2.text);
                    }, eParams.elem, grup);
                }
            }
        });

        UGui.buttonCellEditor(tab5, 1, (componentCell) -> { //слушатель редактирование типа и вида данных и вида ячейки таблицы
            return UGui.cellParamTypeOrVid(tab5, componentCell, eElempar2.groups_id);

        }).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            Record record = qElempar2.get(UGui.getIndexRec(tab5));
            int grup = record.getInt(eElempar2.groups_id);
            if (grup < 0) {
                ParUserVal frame = new ParUserVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab5, eElempar2.groups_id, eElempar2.text);
                }, eParams.elem, grup);
            } else {
                List list = ParamList.find(grup).dict();
                ParSysVal frame = new ParSysVal(this, (rec) -> {
                    UGui.cellParamNameOrValue(rec, tab5, eElempar2.groups_id, eElempar2.text);
                }, list);
            }
        });
    }

    public void listenerSet() {

        listenerTypset = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            if (tab2.getBorder() != null) {
                qElement.set(record.getInt(0), UGui.getIndexRec(tab2), eElement.typset);
                UGui.fireTableRowUpdated(tab2);
            }
        };

        listenerArtikl = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            if (tab2.getBorder() != null) {
                qElement.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab2), eElement.artikl_id);
                qElement.query(eArtikl.up).set(record.get(eArtikl.name), UGui.getIndexRec(tab2), eArtikl.name);
                qElement.query(eArtikl.up).set(record.get(eArtikl.code), UGui.getIndexRec(tab2), eArtikl.code);
                UGui.fireTableRowUpdated(tab2);

            } else if (tab3.getBorder() != null) {
                qElemdet.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab3), eElemdet.artikl_id);
                qElemdet.query(eArtikl.up).set(record.get(eArtikl.name), UGui.getIndexRec(tab3), eArtikl.name);
                qElemdet.query(eArtikl.up).set(record.get(eArtikl.code), UGui.getIndexRec(tab3), eArtikl.code);
                int artiklID = record.getInt(eArtikl.id);
                List<Record> artdetList = eArtdet.filter2(artiklID);

                if (artdetList.size() == 1) {
                    if (artdetList.get(0).getInt(eArtdet.color_fk) > 0) {
                        Record colorRec = eColor.find(artdetList.get(0).getInt(eArtdet.color_fk));
                        listenerColor.action(colorRec);
                    }
                }
                UGui.fireTableRowUpdated(tab3);
            }
        };

        listenerSeries = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            if (tab2.getBorder() != null) {
                int series_id = record.getInt(eGroups.id);
                qElement.set(series_id, UGui.getIndexRec(tab2), eElement.groups1_id);
                UGui.fireTableRowUpdated(tab2);
            }
        };

        listenerGroups = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            if (tab2.getBorder() != null) {
                int groups_id = record.getInt(eGroups.id);
                qElement.set(groups_id, UGui.getIndexRec(tab2), eElement.groups3_id);
                UGui.fireTableRowUpdated(tab2);
            }
        };

        listenerColor = (record) -> {
            UGui.cellParamColor(record, tab3, eElemdet.color_fk, eElemdet.color_us, tab1, tab2, tab3, tab4, tab5);
        };

        listenerColvar1 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            Record elemdetRec = qElemdet.get(UGui.getIndexRec(tab3));
            int types = (elemdetRec.getInt(eElemdet.color_us) == -1) ? 0 : elemdetRec.getInt(eElemdet.color_us);
            types = (types & 0xfffffff0) + record.getInt(0);
            elemdetRec.set(eElemdet.color_us, types);
            UGui.fireTableRowUpdated(tab3);
        };

        listenerColvar2 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            Record elemdetRec = qElemdet.get(UGui.getIndexRec(tab3));
            int types = (elemdetRec.getInt(eElemdet.color_us) == -1) ? 0 : elemdetRec.getInt(eElemdet.color_us);
            types = (types & 0xffffff0f) + (record.getInt(0) << 4);
            elemdetRec.set(eElemdet.color_us, types);
            UGui.fireTableRowUpdated(tab3);
        };

        listenerColvar3 = (record) -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4, tab5);
            Record elemdetRec = qElemdet.get(UGui.getIndexRec(tab3));
            int types = (elemdetRec.getInt(eElemdet.color_us) == -1) ? 0 : elemdetRec.getInt(eElemdet.color_us);
            types = (types & 0xfffff0ff) + (record.getInt(0) << 8);
            elemdetRec.set(eElemdet.color_us, types);
            UGui.fireTableRowUpdated(tab3);
        };
    }

    public void deteilFind(int deteilID) {
        Query qDet = new Query(eElemdet.values(), eArtikl.values());
        for (int index = 0; index < qElement.size(); index++) {
            int element_id = qElement.get(index).getInt(eElement.id);
            qDet.sql(eElemdet.data(), eElemdet.element_id, element_id);
            for (int index2 = 0; index2 < qDet.size(); index2++) {
                if (qDet.get(index2).getInt(eElemdet.id) == deteilID) {
                    UGui.setSelectedIndex(tab2, index);
                    UGui.scrollRectToRow(index, tab2);
                    UGui.setSelectedIndex(tab3, index2);
                    UGui.scrollRectToRow(index2, tab3);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmCateg = new javax.swing.JPopupMenu();
        itCateg1 = new javax.swing.JMenuItem();
        itCateg2 = new javax.swing.JMenuItem();
        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        separator1 = new javax.swing.JPopupMenu.Separator();
        mDefault = new javax.swing.JMenuItem();
        mType = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnFind1 = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnFind2 = new javax.swing.JButton();
        btnClone = new javax.swing.JButton();
        btnMove = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        pan1 = new javax.swing.JPanel();
        pan4 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan5 = new javax.swing.JPanel();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        pan2 = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        scr5 = new javax.swing.JScrollPane();
        tab5 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        itCateg1.setText("ПРОФИЛИ");
        itCateg1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmCategAction(evt);
            }
        });
        ppmCateg.add(itCateg1);

        itCateg2.setText("ЗАПОЛНЕНИЯ");
        itCateg2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmCategAction(evt);
            }
        });
        ppmCateg.add(itCateg2);

        mInsert.setFont(frames.UGui.getFont(1,0));
        mInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale); // NOI18N
        mInsert.setText(bundle.getString("Добавить")); // NOI18N
        mInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mInsert);

        mDelit.setFont(frames.UGui.getFont(1,0));
        mDelit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        mDelit.setText(bundle.getString("Удалить")); // NOI18N
        mDelit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmCrud.add(mDelit);
        ppmCrud.add(separator1);

        mDefault.setFont(frames.UGui.getFont(1,0));
        mDefault.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c085.gif"))); // NOI18N
        mDefault.setText("По умолчанию");
        mDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmCrud.add(mDefault);

        mType.setFont(frames.UGui.getFont(1,0));
        mType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c085.gif"))); // NOI18N
        mType.setText("Тип состава");
        mType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmCrud.add(mType);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Составы");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(900, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Elements.this.windowClosed(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(900, 29));

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c009.gif"))); // NOI18N
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

        btnFind1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c088.gif"))); // NOI18N
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

        btnFind2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c089.gif"))); // NOI18N
        btnFind2.setToolTipText(bundle.getString("Поиск записи")); // NOI18N
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

        btnMove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c050.gif"))); // NOI18N
        btnMove.setToolTipText(bundle.getString("Переместить")); // NOI18N
        btnMove.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnMove.setFocusable(false);
        btnMove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMove.setMaximumSize(new java.awt.Dimension(25, 25));
        btnMove.setMinimumSize(new java.awt.Dimension(25, 25));
        btnMove.setPreferredSize(new java.awt.Dimension(25, 25));
        btnMove.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnMove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMove(evt);
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
                .addComponent(btnMove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 557, Short.MAX_VALUE)
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
                    .addComponent(btnFind2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnClone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMove, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        centr.setPreferredSize(new java.awt.Dimension(758, 460));
        centr.setLayout(new java.awt.BorderLayout());

        pan1.setPreferredSize(new java.awt.Dimension(800, 360));
        pan1.setLayout(new javax.swing.BoxLayout(pan1, javax.swing.BoxLayout.LINE_AXIS));

        pan4.setMaximumSize(new java.awt.Dimension(2000, 800));
        pan4.setPreferredSize(new java.awt.Dimension(500, 400));
        pan4.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), " "));
        scr1.setPreferredSize(new java.awt.Dimension(120, 404));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Elements.this.mousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);

        pan4.add(scr1, java.awt.BorderLayout.WEST);

        scr2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Списки составов", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr2.setPreferredSize(new java.awt.Dimension(454, 320));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Артикул", "Название", "Наименование составов", "Тип состава", "Признак состава", "Серия", "Группа составов", "Умолчание", "Обязательно", "Наценка%", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, true, true, true, true, false
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
                Elements.this.mousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(120);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(200);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(200);
            tab2.getColumnModel().getColumn(3).setMinWidth(0);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(0);
            tab2.getColumnModel().getColumn(3).setMaxWidth(0);
            tab2.getColumnModel().getColumn(4).setPreferredWidth(32);
            tab2.getColumnModel().getColumn(5).setPreferredWidth(60);
            tab2.getColumnModel().getColumn(6).setPreferredWidth(60);
            tab2.getColumnModel().getColumn(7).setMinWidth(0);
            tab2.getColumnModel().getColumn(7).setPreferredWidth(0);
            tab2.getColumnModel().getColumn(7).setMaxWidth(0);
            tab2.getColumnModel().getColumn(8).setPreferredWidth(32);
            tab2.getColumnModel().getColumn(9).setPreferredWidth(32);
            tab2.getColumnModel().getColumn(10).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(10).setMaxWidth(60);
        }

        pan4.add(scr2, java.awt.BorderLayout.CENTER);

        pan1.add(pan4);

        pan5.setMaximumSize(new java.awt.Dimension(600, 800));
        pan5.setPreferredSize(new java.awt.Dimension(240, 400));
        pan5.setLayout(new java.awt.BorderLayout());

        scr4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Параметры", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr4.setPreferredSize(new java.awt.Dimension(260, 320));

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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Elements.this.mousePressed(evt);
            }
        });
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(0).setPreferredWidth(220);
            tab4.getColumnModel().getColumn(1).setPreferredWidth(80);
        }

        pan5.add(scr4, java.awt.BorderLayout.CENTER);

        pan1.add(pan5);

        centr.add(pan1, java.awt.BorderLayout.CENTER);

        pan2.setPreferredSize(new java.awt.Dimension(800, 200));
        pan2.setLayout(new javax.swing.BoxLayout(pan2, javax.swing.BoxLayout.LINE_AXIS));

        scr3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Детализация составов", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr3.setMaximumSize(new java.awt.Dimension(2000, 800));
        scr3.setPreferredSize(new java.awt.Dimension(500, 2044));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

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
        tab3.setFillsViewportHeight(true);
        tab3.setName("tab3"); // NOI18N
        tab3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Elements.this.mousePressed(evt);
            }
        });
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(96);
            tab3.getColumnModel().getColumn(1).setPreferredWidth(180);
            tab3.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab3.getColumnModel().getColumn(3).setPreferredWidth(60);
            tab3.getColumnModel().getColumn(4).setPreferredWidth(60);
            tab3.getColumnModel().getColumn(5).setPreferredWidth(60);
            tab3.getColumnModel().getColumn(6).setPreferredWidth(40);
            tab3.getColumnModel().getColumn(6).setMaxWidth(60);
        }

        pan2.add(scr3);

        scr5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "Параметры", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));
        scr5.setMaximumSize(new java.awt.Dimension(600, 800));
        scr5.setPreferredSize(new java.awt.Dimension(240, 200));

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Параметр", "Значение"
            }
        ));
        tab5.setFillsViewportHeight(true);
        tab5.setMinimumSize(new java.awt.Dimension(6, 64));
        tab5.setName("tab5"); // NOI18N
        tab5.setPreferredSize(new java.awt.Dimension(0, 64));
        tab5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Elements.this.mousePressed(evt);
            }
        });
        scr5.setViewportView(tab5);
        if (tab5.getColumnModel().getColumnCount() > 0) {
            tab5.getColumnModel().getColumn(0).setPreferredWidth(220);
            tab5.getColumnModel().getColumn(1).setPreferredWidth(80);
        }

        pan2.add(scr5);

        centr.add(pan2, java.awt.BorderLayout.SOUTH);

        getContentPane().add(centr, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(900, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());
    }//GEN-LAST:event_windowClosed

    private void ppmCategAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmCategAction

        JMenuItem ppm = (JMenuItem) evt.getSource();
        int level1 = (ppm == itCateg1) ? 1 : 5;
        String result = JOptionPane.showInputDialog(Elements.this, "Название", "Категория", JOptionPane.QUESTION_MESSAGE);
        int id = Connect.genId(eGroups.up);
        if (result.isEmpty() && eProp.devel.equals("99")) {
            result = (ppm == itCateg1) ? "Катег.проф-" + id : "Катег.зап-" + id;
        }
        if (result.isEmpty() == false) {
            Record elemgrpRec = eGroups.up.newRecord(Query.INS);
            elemgrpRec.setNo(eGroups.id, id);
            elemgrpRec.setNo(eGroups.grup, TypeGrup.CATEG_VST.id);
            elemgrpRec.setNo(eGroups.npp, level1); //-1 -РџР РћР¤Р?Р›Р?, -5 -Р—РђРџРћР›РќР•РќР?РЇ
            elemgrpRec.setNo(eGroups.name, result);
            qGrCateg.insert(elemgrpRec);
            eGroups.up.query().add(elemgrpRec);
            loadingData();
            for (int i = 0; i < qGrCateg.size(); ++i) {
                if (qGrCateg.get(i).getInt(eGroups.id) == id) {
                    UGui.setSelectedIndex(tab1, i - 1);
                    ((DefaultTableModel) tab1.getModel()).fireTableRowsInserted(i - 1, i - 1);
                    UGui.scrollRectToRow(i, tab1);
                    break;
                }
            }
        }
    }//GEN-LAST:event_ppmCategAction

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        UGui.updateBorderAndSql((JTable) evt.getSource(), List.of(tab1, tab2, tab3, tab4, tab5));
        List.of(btnClone, btnFind1, btnFind2).forEach(btn -> btn.setEnabled(false));
    }//GEN-LAST:event_mousePressed

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (tab2.getBorder() != null) {
                List.of(btnClone, btnFind1, btnFind2).forEach(btn -> btn.setEnabled(true));
            } else if (tab3.getBorder() != null) {
                List.of(btnClone, btnFind1).forEach(btn -> btn.setEnabled(true));
            }
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab2, tab3, tab4, tab5).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab2, tab3, tab4, tab5).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked

    private void btnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMove
        try {
            int index = UGui.getIndexRec(tab2);
            if (index != -1) {
                Record elementRec = qElement.get(index);
                List list = new LinkedList();
                qGrCateg.forEach(rec -> list.add(rec.getStr(eGroups.name)));
                Object result = JOptionPane.showInputDialog(Elements.this, "Вставка: " + elementRec.getStr(eElement.name),
                        "Изменение категории элемента втавки", JOptionPane.QUESTION_MESSAGE, null, list.toArray(), list.toArray()[0]);
                if (result != null) {
                    for (Record groupsRec : qGrCateg) {
                        if (result.equals(groupsRec.getStr(eGroups.name))) {
                            elementRec.setNo(eElement.groups2_id, groupsRec.getInt(eGroups.id));
                            qElement.update(elementRec);
                            selectionTab1(null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Elements.btnMove()");
        }
    }//GEN-LAST:event_btnMove

    private void btnClone(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClone
        UIManager.put("OptionPane.noButtonText", "Все");
        int result = JOptionPane.showConfirmDialog(this, "Клонировать только основную запись?",
                "Подтверждение", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result != JOptionPane.CANCEL_OPTION) {

            if (tab2.getBorder() != null) {
                List<Record> dataPar1 = new ArrayList(qElempar1);
                List<Record> dataDet = new ArrayList(qElemdet);

                Record masterClon = UGui.cloneMaster(qElement, tab2, eElement.up, (clon) -> {
                    clon.set(eElement.name, clon.getStr(eElement.name) + "-клон");
                });
                if (result == JOptionPane.NO_OPTION) {
                    UGui.cloneSlave(qElemdet, tab3, eElemdet.up, dataDet, (clon) -> {
                        clon.setNo(eElemdet.element_id, masterClon.getStr(eElement.id));
                        Record tail = eArtikl.data().stream().filter(rec -> rec.getInt(eArtikl.id)
                                == clon.getInt(eElemdet.artikl_id)).findFirst().get();
                        qElemdet.query(eArtikl.up).add(tail);
                    });
                    UGui.cloneSlave(qElempar1, tab4, eElempar1.up, dataPar1, (clon) -> {
                        clon.setNo(eElempar1.element_id, masterClon.getStr(eElement.id));
                    });
                }
            } else if (tab3.getBorder() != null) {
                List<Record> dataPar2 = new ArrayList(qElempar2);
                Record masterClon = UGui.cloneMaster(qElemdet, tab3, eElemdet.up, null);
                if (result == JOptionPane.NO_OPTION) {
                    UGui.cloneSlave(qElempar2, tab5, eElempar2.up, dataPar2, (clon) -> {
                        clon.setNo(eElempar2.elemdet_id, masterClon.getStr(eElemdet.id));
                    });
                }
            }
        }
    }//GEN-LAST:event_btnClone

    private void btnFind2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind2
        int index = UGui.getIndexRec(tab2);
        Record record = qElement.get(index);
        List<Record> sysprofList1 = eSysprof.data().stream().filter(rec -> record.getInt(eElement.artikl_id) == rec.getInt(eSysprof.artikl_id)).collect(Collectors.toList());
        Set<Integer> sysprofList2 = new HashSet<Integer>();
        sysprofList1.forEach(rec -> sysprofList2.add(rec.getInt(eSysprof.systree_id)));
        List<String> pathList = new ArrayList<String>();
        List<Integer> keyList = new ArrayList<Integer>();
        StringBuffer path = new StringBuffer();
        for (Record rec : eSystree.data()) {
            if (sysprofList2.contains(rec.get(eSystree.id))) {
                path = path.append(rec.getStr(eSystree.name));
                findPathSystree(rec, path);
                pathList.add(path.toString());
                keyList.add(rec.getInt(eSystree.id));
                path.delete(0, path.length());
            }
        }
        if (pathList.size() != 0) {
            for (int i = pathList.size(); i < 21; ++i) {
                pathList.add(null);
            }
            Object result = JOptionPane.showInputDialog(Elements.this, "Артикул в системе профилей", "Сообщение", JOptionPane.QUESTION_MESSAGE,
                    new ImageIcon(getClass().getResource("/resource/img24/c066.gif")), pathList.toArray(), pathList.toArray()[0]);
            if (result != null || result instanceof Integer) {
                for (int i = 0; i < pathList.size(); ++i) {
                    if (result.equals(pathList.get(i))) {
                        Object id = keyList.get(i);
                        ProgressBar.create(Elements.this, new ListenerFrame() {
                            public void actionRequest(Object obj) {
                                App.Systree.createFrame(Elements.this, id, 1);
                            }
                        });
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(Elements.this, "В системе профилей артикул не найден", "Сообщение", JOptionPane.NO_OPTION);
        }
    }//GEN-LAST:event_btnFind2

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest
    }//GEN-LAST:event_btnTest

    private void btnFind1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind1
        if (tab2.getBorder() != null) {
            Record record = ((DefTableModel) tab2.getModel()).getQuery().get(UGui.getIndexRec(tab2));
            if (record != null) {
                Record record2 = eArtikl.find(record.getInt(eElement.artikl_id), false);
                ProgressBar.create(this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        App.Artikles.createFrame(Elements.this, record2);
                    }
                });
            }
        } else if (tab3.getBorder() != null) {
            Record record = ((DefTableModel) tab3.getModel()).getQuery().get(UGui.getIndexRec(tab3));
            if (record != null) {
                Record record2 = eArtikl.find(record.getInt(eElemdet.artikl_id), false);
                ProgressBar.create(this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        App.Artikles.createFrame(Elements.this, record2);
                    }
                });
            }
        }
    }//GEN-LAST:event_btnFind1

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        RTable.load("Составы", tab2);
        ExecuteCmd.documentType(this);
    }//GEN-LAST:event_btnReport

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert

        if (tab1.getBorder() != null) {
            ppmCateg.show(north, btnIns.getX(), btnIns.getY() + 18);

        } else if (tab2.getBorder() != null) {
            if (UGui.getIndexRec(tab1) != -1) {
                Record groupsRec = qGrCateg.get(UGui.getIndexRec(tab1));
                int id = groupsRec.getInt(eGroups.id);
                if ((id == -1 || id == -5) == false) {
                    UGui.insertRecordCur(tab2, eElement.up, (record) -> {
                        record.set(eElement.groups2_id, id);
                        record.set(eElement.todef, 1);
                        record.set(eElement.markup, 0);
                        int index = UGui.getIndexFind(tab2, eElement.id, record.get(eElement.id));
                        qElement.query(eArtikl.up).add(index, eArtikl.up.newRecord(Query.SEL));
                    });
                }
            }
        } else if (tab3.getBorder() != null) {
            if (UGui.getIndexRec(tab2) != -1) {
                UGui.insertRecordCur(tab3, eElemdet.up, (record) -> {
                    record.set(eElemdet.element_id, qElement.get(UGui.getIndexRec(tab2), eElement.id));
                    int index = UGui.getIndexFind(tab3, eElemdet.id, record.get(eElemdet.id));
                    qElemdet.query(eArtikl.up).add(index, eArtikl.up.newRecord(Query.SEL));
                });
            }
        } else if (tab4.getBorder() != null) {
            UGui.insertRecordCur(tab4, eElempar1.up, (record) -> {
                int id = qElement.getAs(UGui.getIndexRec(tab2), eElement.id);
                record.set(eElempar1.element_id, id);
            });
            DefCellEditorBtn defCellEditorBtn = (DefCellEditorBtn) tab4.getColumnModel().getColumn(0).getCellEditor();
            defCellEditorBtn.getButton().getActionListeners()[0].actionPerformed(null);

        } else if (tab5.getBorder() != null) {
            UGui.insertRecordCur(tab5, eElempar2.up, (record) -> {
                int id = qElemdet.getAs(UGui.getIndexRec(tab3), eElemdet.id);
                record.set(eElempar2.elemdet_id, id);
            });
            DefCellEditorBtn defCellEditorBtn = (DefCellEditorBtn) tab5.getColumnModel().getColumn(0).getCellEditor();
            defCellEditorBtn.getButton().getActionListeners()[0].actionPerformed(null);
        }
    }//GEN-LAST:event_btnInsert

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
            if (UGui.isDeleteRecord(tab3, this, tab5) == 0) {
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

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void ppmClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmClick
        JMenuItem ppm = (JMenuItem) evt.getSource();
        int index = 0;
        if (ppm == mType) {
            index = 3;
        } else if (ppm == mDefault) {
            index = 7;
        }
        TableColumn column = tab2.getColumnModel().getColumn(index);
        if (column.getMaxWidth() == 0) {
            column.setPreferredWidth(80);
            column.setMaxWidth(220);
            column.setMinWidth(60);
        } else {
            column.setMinWidth(0);
            column.setPreferredWidth(0);
            column.setMaxWidth(0);
        }
    }//GEN-LAST:event_ppmClick

    private void findPathSystree(Record record, StringBuffer path) {
        for (Record rec : eSystree.data()) {
            if (record.getInt(eSystree.parent_id) == rec.getInt(eSystree.id)) {
                path.insert(0, rec.getStr(eSystree.name) + "->");
                if (rec.getInt(eSystree.id) != rec.getInt(eSystree.parent_id)) {
                    findPathSystree(rec, path); //рекурсия
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
    private javax.swing.JButton btnMove;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnTest;
    private javax.swing.JPanel centr;
    private javax.swing.JMenuItem itCateg1;
    private javax.swing.JMenuItem itCateg2;
    private javax.swing.JMenuItem mDefault;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JMenuItem mType;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
    private javax.swing.JPopupMenu ppmCateg;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JSeparator separator1;
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
            App.saveLocationWin(this, btnClose);
        });

        TableFieldFilter filterTable = new TableFieldFilter(0, tab2, tab3, tab4, tab5);
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
        tab3.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                selectionTab3(event);
                if (event.getValueIsAdjusting() == false) {
                    selectionTab3(event);
                }
            }
        });
    }
}
