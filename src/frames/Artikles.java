package frames;

import frames.dialog.DicCurrenc;
import common.UCom;
import common.eProp;
import frames.dialog.DicColor;
import dataset.Connect;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eArtikl;
import domain.eArtdet;
import domain.eColor;
import domain.eCurrenc;
import enums.TypeArt;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import frames.swing.comp.DefTableModel;
import frames.swing.comp.TableFieldFormat;
import domain.eGroups;
import domain.eSyssize;
import enums.TypeGrup;
import enums.UseUnit;
import frames.dialog.DicArtikl;
import frames.dialog.DicEnums;
import frames.dialog.DicGroups;
import frames.swing.comp.DefCellRendererBool;
import java.awt.CardLayout;
import java.awt.Window;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import common.listener.ListenerRecord;
import domain.eFurniture;
import domain.eFurnside1;
import frames.swing.comp.DefCellEditorNumb;
import frames.swing.comp.TableFieldFilter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import report.sup.ExecuteCmd;
import report.sup.RTable;
import startup.App;

//TODO ���� ��� �������� ���� ������� ����� � �������� ���. ���� �� ��� ��� �� ������� � �������. ���������
/**
 * ������������ ��������
 */
public class Artikles extends javax.swing.JFrame {

    private ListenerRecord listener = null;
    private Query qGroups = new Query(eGroups.values());
    private Query qSyssize = new Query(eSyssize.values());
    private Query qColor = new Query(eColor.values());
    private Query qCurrenc = new Query(eCurrenc.values());
    private Query qArtikl = new Query(eArtikl.values());
    private Query qArtdet = new Query(eArtdet.values());

    private TableFieldFormat rsvArtikl;
    private TableFieldFilter filterTable = null;
    private HashSet<JTextField> jtf = new HashSet<JTextField>();
    private DefaultMutableTreeNode nodeRoot = null;
    private Window owner = null;
    private ListenerRecord listenerSeries, listenerCateg, listenerColor, listenerUnit, listenerCurrenc1,
            listenerCurrenc2, listenerAnalog, listenerSyssize, listenerMarkup, listenerDiscount;

    public Artikles() {
        initComponents();
        initElements();
        listenerSet();
        loadingData();
        loadingModel();
        loadingTree();
    }

    public Artikles(java.awt.Window owner, ListenerRecord listener) {
        initComponents();
        initElements();
        this.listener = listener;
        btnChoice.setVisible(true);
        listenerSet();
        loadingData();
        loadingModel();
        loadingTree();
    }

    public Artikles(java.awt.Window owner, Record artiklRec, ListenerRecord listener) {
        initComponents();
        initElements();
        this.listener = listener;
        btnChoice.setVisible(true);
        listenerSet();
        loadingData();
        loadingModel();
        loadingTree();
        setSelectionPath(artiklRec);
    }

    public Artikles(java.awt.Window owner, Record artiklRec) {
        initComponents();
        initElements();
        listenerSet();
        loadingData();
        loadingModel();
        loadingTree();
        setSelectionPath(artiklRec);
    }

    public void loadingData() {
        qSyssize.sql(eSyssize.data(), eSyssize.up).sort(eSyssize.name);
        qGroups.sql(eGroups.data(), eGroups.up);
        qCurrenc.sql(eCurrenc.data(), eCurrenc.up).sort(eCurrenc.name);
        qColor.sql(eColor.data(), eColor.up).sort(eColor.name);
    }

    public void loadingModel() {

        new DefTableModel(tab1, qArtikl, eArtikl.code, eArtikl.name, eArtikl.groups1_id,
                eArtikl.groups2_id, eArtikl.groups3_id, eArtikl.groups4_id, eArtikl.depth, eArtikl.height, eArtikl.otx_norm, eArtikl.tech_code, eArtikl.analog_id) {
            @Override
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];

                if (field == eArtikl.groups1_id) {
                    Record artiklRec = qArtikl.get(row);
                    return qGroups.find(eGroups.data(), eGroups.id, artiklRec.getInt(eArtikl.groups1_id)).get(eGroups.name);

                } else if (field == eArtikl.groups2_id) {
                    Record artiklRec = qArtikl.get(row);
                    return qGroups.find(eGroups.data(), eGroups.id, artiklRec.getInt(eArtikl.groups2_id)).get(eGroups.name);

                } else if (field == eArtikl.groups3_id) {
                    Record artiklRec = qArtikl.get(row);
                    return qGroups.find(eGroups.data(), eGroups.id, artiklRec.getInt(eArtikl.groups3_id)).get(eGroups.name);

                } else if (field == eArtikl.groups4_id) {
                    Record artiklRec = qArtikl.get(row);
                    return qGroups.find(eGroups.data(), eGroups.id, artiklRec.getInt(eArtikl.groups4_id)).get(eGroups.name);

                } else if (field == eArtikl.analog_id) {
                    int analogId = qArtikl.get(row).getInt(eArtikl.analog_id);
                    if (analogId != -1) {
                        return eArtikl.find(analogId).get(eArtikl.code);
                    }
                }
                return val;
            }
        };
        new DefTableModel(tab2, qArtdet, eArtdet.color_fk, eArtdet.color_fk, eArtdet.mark_c1, eArtdet.cost_c1,
                eArtdet.mark_c2, eArtdet.cost_c2, eArtdet.mark_c3, eArtdet.cost_c3, eArtdet.cost_c4, eArtdet.cost_unit, eArtdet.coef) {

            @Override
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (field == eArtdet.color_fk && val != null) {
                    Integer color_fk = Integer.valueOf(String.valueOf(val));

                    if (color_fk >= 0) {
                        Record colorRec = qColor.find(eColor.data(), eColor.id, color_fk);

                        if (col == 0) {
                            Record colgrpRec = qGroups.find(eGroups.data(), eGroups.id, colorRec.getInt(eColor.groups_id));
                            return colgrpRec.getStr(eGroups.name);
                        } else {
                            return colorRec.getStr(eColor.name);
                        }

                    } else if (color_fk < 0) {
                        if (col == 0) {
                            Record colgrpRec = eGroups.data().stream().filter(rec -> rec.getInt(eGroups.id) == color_fk).findFirst().orElse(eGroups.up.newRecord(Query.SEL));
                            //Record colgrpRec = eGroups.data().stream().filter(rec -> rec.getInt(eGroups.id) == color_fk).findFirst().orElse(eGroups.up.newRecord(Query.SEL));
                            return colgrpRec.getStr(eGroups.name);
                        } else {
                            return "��� �������� ������";
                        }
                    }
                }
                return val;
            }
        };

        tab1.getColumnModel().getColumn(8).setCellEditor(new DefCellEditorNumb(3));
        tab2.getColumnModel().getColumn(2).setCellRenderer(new DefCellRendererBool());
        tab2.getColumnModel().getColumn(4).setCellRenderer(new DefCellRendererBool());
        tab2.getColumnModel().getColumn(6).setCellRenderer(new DefCellRendererBool());

        rsvArtikl = new TableFieldFormat(tab1) {

            public Set<JTextField> set = new HashSet<JTextField>();
            private DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(eProp.locale);

            public void setText(JTextField jtf, String val) {
                set.add(jtf);
                jtf.setText(val);
            }

            public void setText(JTextField jtf, String val, String pattern) {
                set.add(jtf);
                jtf.setText(UCom.format(val, pattern));
            }

            @Override
            public void load(Integer index) {
                super.load(index);
                update = false;
                Record artiklRec = qArtikl.get(UGui.getIndexRec(tab1));
                Record seriesRec = qGroups.find(eGroups.data(), eGroups.id, artiklRec.getInt(eArtikl.groups4_id));
                Record currenc1Rec = qCurrenc.find(eCurrenc.data(), eCurrenc.id, artiklRec.getInt(eArtikl.currenc1_id));
                Record currenc2Rec = qCurrenc.find(eCurrenc.data(), eCurrenc.id, artiklRec.getInt(eArtikl.currenc2_id));
                Record artgrp1Rec = qGroups.find(eGroups.data(), eGroups.id, artiklRec.getInt(eArtikl.groups1_id));
                Record artgrp2Rec = qGroups.find(eGroups.data(), eGroups.id, artiklRec.getInt(eArtikl.groups2_id));
                Record artgrp3Rec = qGroups.find(eGroups.data(), eGroups.id, artiklRec.getInt(eArtikl.groups3_id));
                Record syssizeRec = qSyssize.find(eSyssize.data(), eSyssize.id, artiklRec.getInt(eArtikl.syssize_id));

                setText(txt5, UseUnit.getName(artiklRec.getInt(eArtikl.unit)));
                setText(txt7, currenc1Rec.getStr(eCurrenc.name));
                setText(txt17, currenc2Rec.getStr(eCurrenc.name));
                setText(txt24, currenc2Rec.getStr(eCurrenc.name).replace('.', ','));
                setText(txt25, currenc1Rec.getStr(eCurrenc.name).replace('.', ','));
                setText(txt33, currenc2Rec.getStr(eCurrenc.name).replace('.', ','));
                setText(txt31, UseUnit.getName(artiklRec.getInt(eArtikl.unit)));
                setText(txt32, currenc1Rec.getStr(eCurrenc.name).replace('.', ','));
                setText(txt40, UseUnit.getName(artiklRec.getInt(eArtikl.unit)));

                if (artiklRec.getInt(eArtikl.analog_id) != -1) {
                    Record analogRec = qArtikl.find(eArtikl.data(), eArtikl.id, artiklRec.getInt(eArtikl.analog_id));
                    setText(txt11, analogRec.getStr(eArtikl.code));
                } else {
                    setText(txt11, null);
                }
                setText(txt10, seriesRec.getStr(eGroups.name));
                setText(txt18, syssizeRec.getStr(eSyssize.name));
                setText(txt19, artgrp1Rec.getStr(eGroups.val).replace('.', ','));
                setText(txt26, artgrp1Rec.getStr(eGroups.val).replace('.', ','));
                setText(txt20, artgrp2Rec.getStr(eGroups.val).replace('.', ','));
                setText(txt22, artgrp3Rec.getStr(eGroups.name));
                setText(txt27, artgrp2Rec.getStr(eGroups.val).replace('.', ','));
                setText(txt29, artgrp3Rec.getStr(eGroups.name));
                setText(txt30, seriesRec.getStr(eGroups.name));
                setText(txt34, artgrp1Rec.getStr(eGroups.val).replace('.', ','));
                setText(txt35, artgrp2Rec.getStr(eGroups.val).replace('.', ','));
                setText(txt37, artgrp3Rec.getStr(eGroups.name));
                setText(txt38, seriesRec.getStr(eGroups.name));
                setText(txt49, artgrp1Rec.getStr(eGroups.name));
                setText(txt50, artgrp2Rec.getStr(eGroups.name));
                setText(txt51, artgrp1Rec.getStr(eGroups.name));
                setText(txt52, artgrp2Rec.getStr(eGroups.name));
                setText(txt53, artgrp1Rec.getStr(eGroups.name));
                setText(txt54, artgrp2Rec.getStr(eGroups.name));
                update = true;
            }

            @Override
            public void clear() {
                super.clear();
                set.forEach(s -> s.setText(null));
            }
        };

        rsvArtikl.add(eArtikl.len_unit, txt1);
        rsvArtikl.add(eArtikl.height, txt2);
        rsvArtikl.add(eArtikl.depth, txt3);
        rsvArtikl.add(eArtikl.density, txt4);
        rsvArtikl.add(eArtikl.density, txt6);
        rsvArtikl.add(eArtikl.size_centr, txt8);
        rsvArtikl.add(eArtikl.size_furn, txt9);
        rsvArtikl.add(eArtikl.min_rad, txt12);
        rsvArtikl.add(eArtikl.tech_code, txt14);
        rsvArtikl.add(eArtikl.size_falz, txt15);
        rsvArtikl.add(eArtikl.size_tech, txt16);
        rsvArtikl.add(eArtikl.size_frez, txt21);
        rsvArtikl.add(eArtikl.size_frez, txt23);
        rsvArtikl.add(eArtikl.density, txt39);
        rsvArtikl.add(eArtikl.height, txt45);
        rsvArtikl.add(eArtikl.depth, txt46);
        rsvArtikl.add(eArtikl.len_unit, txt48);

        UGui.buttonCellEditor(tab2, 0).addActionListener(event -> {
            Record recordDet = qArtdet.get(UGui.getIndexRec(tab2));
            DicColor frame = new DicColor(this, listenerColor, recordDet, true, false);
        });

        UGui.buttonCellEditor(tab2, 1).addActionListener(event -> {
            Record recordDet = qArtdet.get(UGui.getIndexRec(tab2));
            DicColor frame = new DicColor(this, listenerColor, recordDet, true, false);
        });
    }

    public void loadingTree() {
        nodeRoot = new DefaultMutableTreeNode(TypeArt.ROOT);
        UTree.loadArtTree(nodeRoot);
        tree.setModel(new DefaultTreeModel(nodeRoot));
        scrTree.setViewportView(tree);
        tree.setSelectionRow(0);
    }

    public void selectionTree() {

        UGui.stopCellEditing(tab1, tab2);
        List.of(qArtikl, qArtdet).forEach(q -> q.execsql());
        rsvArtikl.clear();
        UGui.clearTable(tab1, tab2);
        UGui.stopCellEditing(tab1, tab2);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node != null) {
            TypeArt e = (TypeArt) node.getUserObject();
            String name = (e.id1 > 4) ? "pan8" : (e.id1 > 1) ? "pan7" : "pan2";
            ((CardLayout) pan6.getLayout()).show(pan6, name);

            if (e == TypeArt.ROOT) {
                qArtikl.sql(eArtikl.data(), eArtikl.up).sort(eArtikl.level1, eArtikl.code);
            } else if (node.isLeaf()) {
                qArtikl.sql(eArtikl.data(), eArtikl.level1, e.id1, eArtikl.level2, e.id2).sort(eArtikl.level1, eArtikl.code);
            } else {
                qArtikl.sql(eArtikl.data(), eArtikl.level1, e.id1).sort(eArtikl.level1, eArtikl.code);
            }
            DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) node.getParent();
            lab1.setText((node2 != null && node.getParent() != null) ? " ���: " + ((TypeArt) node2.getUserObject()).id1
                    + "  ������: " + ((TypeArt) node.getUserObject()).id2 : "");
            ((DefaultTableModel) tab1.getModel()).fireTableDataChanged();
        }
        UGui.setSelectedRow(tab1);

    }

    public void selectionTab1(ListSelectionEvent event) {

        UGui.stopCellEditing(tab2);
        List.of(qArtikl, qArtdet).forEach(q -> q.execsql());
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record record = qArtikl.get(index);

            String name = (record.getInt(eArtikl.level1) > 4) ? "pan8" : (record.getInt(eArtikl.level1) > 1) ? "pan7" : "pan2";
            ((CardLayout) pan6.getLayout()).show(pan6, name);

            int id = record.getInt(eArtikl.id);
            lab2.setText(" id: " + id);
            qArtdet.sql(eArtdet.data(), eArtdet.artikl_id, id);
            rsvArtikl.load();
            lab12.setText(TypeArt.find(record));
            checkBox1.setSelected((record.getInt(eArtikl.with_seal) == 1));
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
            UGui.setSelectedRow(tab2);
        }
    }

    public void setSelectionPath(Record artiklRec) {
        DefaultMutableTreeNode node = nodeRoot;
        node = node.getNextNode();
        do {
            TypeArt typeArt = (TypeArt) node.getUserObject();
            if (typeArt.id1 == artiklRec.getInt(eArtikl.level1)
                    && typeArt.id2 == artiklRec.getInt(eArtikl.level2)) {
                TreePath path = new TreePath(node.getPath());
                tree.setSelectionPath(path);
                tree.scrollPathToVisible(path);
            }
            node = node.getNextNode();
        } while (node != null);

        for (int index = 0; index < qArtikl.size(); ++index) {
            int id = qArtikl.getAs(index, eArtikl.id);
            if (id == artiklRec.getInt(eArtikl.id)) {
                int row = tab1.convertRowIndexToView(index);
                UGui.setSelectedIndex(tab1, index);
                UGui.scrollRectToRow(row, tab1);
            }
        }
    }

    public void listenerSet() {

        listenerSeries = (record) -> {
            int rowQuery = UGui.getIndexRec(tab1);
            if (qGroups.stream().noneMatch(rec -> rec.getInt(eGroups.id) == record.getInt(eGroups.id))) {
                qGroups.sql(eGroups.data(), eGroups.up);
            }
            if (rowQuery != -1) {
                Record artiklRec = qArtikl.get(rowQuery);
                artiklRec.set(eArtikl.groups4_id, record.get(eGroups.id));
                rsvArtikl.load();
            }
            UGui.stopCellEditing(tab1, tab2);
        };

        listenerAnalog = (record) -> {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record artiklRec = qArtikl.get(index);
                artiklRec.set(eArtikl.analog_id, record.get(eArtikl.id));
                rsvArtikl.load();
                UGui.stopCellEditing(tab1, tab2);
            }
        };

        listenerColor = (record) -> {
            if (tab2.getBorder() != null) {
                if (eGroups.values().length == record.size()) {
                    qArtdet.set(record.getInt(eGroups.id), UGui.getIndexRec(tab2), eArtdet.color_fk);
                    qArtdet.set(0, UGui.getIndexRec(tab2), eArtdet.mark_c1);
                    qArtdet.set(1, UGui.getIndexRec(tab2), eArtdet.mark_c2);
                    qArtdet.set(1, UGui.getIndexRec(tab2), eArtdet.mark_c3);
                    qArtdet.set(0, UGui.getIndexRec(tab2), eArtdet.cost_c1);
                    qArtdet.set(100, UGui.getIndexRec(tab2), eArtdet.cost_c2);
                    qArtdet.set(100, UGui.getIndexRec(tab2), eArtdet.cost_c3);

                } else if (eColor.values().length == record.size()) {
                    qArtdet.set(record.getInt(eColor.id), UGui.getIndexRec(tab2), eArtdet.color_fk);
                    qArtdet.set(1, UGui.getIndexRec(tab2), eArtdet.mark_c1);
                    qArtdet.set(0, UGui.getIndexRec(tab2), eArtdet.mark_c2);
                    qArtdet.set(0, UGui.getIndexRec(tab2), eArtdet.mark_c3);
                    qArtdet.set(100, UGui.getIndexRec(tab2), eArtdet.cost_c1);
                    qArtdet.set(0, UGui.getIndexRec(tab2), eArtdet.cost_c2);
                    qArtdet.set(0, UGui.getIndexRec(tab2), eArtdet.cost_c3);
                }
                ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();
                UGui.stopCellEditing(tab1, tab2);
            }
        };

        listenerUnit = (record) -> {
            UGui.cellParamEnum(record, tab1, eArtikl.unit, tab1, tab2);
        };

        listenerCurrenc1 = (record) -> {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record artiklRec = qArtikl.get(index);
                artiklRec.set(eArtikl.currenc1_id, record.get(eCurrenc.id));
                rsvArtikl.load();
            }
            UGui.stopCellEditing(tab1, tab2);
        };

        listenerCurrenc2 = (record) -> {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record artiklRec = qArtikl.get(index);
                artiklRec.set(eArtikl.currenc2_id, record.get(eCurrenc.id));
                rsvArtikl.load();
            }
            UGui.stopCellEditing(tab1, tab2);
        };

        listenerSyssize = (record) -> {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record artiklRec = qArtikl.get(index);
                artiklRec.set(eArtikl.syssize_id, record.get(eSyssize.id));
                artiklRec.set(eArtikl.size_falz, record.get(eSyssize.falz));
                rsvArtikl.load();
                UGui.stopCellEditing(tab1, tab2);
            }
        };

        listenerMarkup = (record) -> {
            UGui.stopCellEditing(tab1, tab2);
            if (qGroups.stream().noneMatch(rec -> rec.getInt(eGroups.id) == record.getInt(eGroups.id))) {
                qGroups.sql(eGroups.data(), eGroups.up);
            }
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record artiklRec = qArtikl.get(index);
                artiklRec.set(eArtikl.groups1_id, record.get(eGroups.id));
                rsvArtikl.load();
            }
        };

        listenerDiscount = (record) -> {
            UGui.stopCellEditing(tab1, tab2);
            if (qGroups.stream().noneMatch(rec -> rec.getInt(eGroups.id) == record.getInt(eGroups.id))) {
                qGroups.sql(eGroups.data(), eGroups.up);
            }
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record artiklRec = qArtikl.get(index);
                artiklRec.set(eArtikl.groups2_id, record.get(eGroups.id));
                rsvArtikl.load();
            }
        };

        listenerCateg = (record) -> {
            UGui.stopCellEditing(tab1, tab2);
            if (qGroups.stream().noneMatch(rec -> rec.getInt(eGroups.id) == record.getInt(eGroups.id))) {
                qGroups.sql(eGroups.data(), eGroups.up);
            }
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record artiklRec = qArtikl.get(index);
                artiklRec.set(eArtikl.groups3_id, record.get(eGroups.id));
                rsvArtikl.load();
            }
        };
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmTab1 = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        separator1 = new javax.swing.JPopupMenu.Separator();
        groups1_id = new javax.swing.JMenuItem();
        groups2_id = new javax.swing.JMenuItem();
        groups3_id = new javax.swing.JMenuItem();
        groups4_id = new javax.swing.JMenuItem();
        texcod = new javax.swing.JMenuItem();
        analog = new javax.swing.JMenuItem();
        separator2 = new javax.swing.JPopupMenu.Separator();
        height = new javax.swing.JMenuItem();
        depth = new javax.swing.JMenuItem();
        ppmTab2 = new javax.swing.JPopupMenu();
        mInsert2 = new javax.swing.JMenuItem();
        mDelit2 = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnMove = new javax.swing.JButton();
        btnTest = new javax.swing.JButton();
        btnClone = new javax.swing.JButton();
        btnChoice = new javax.swing.JButton();
        center = new javax.swing.JPanel();
        pan9 = new javax.swing.JPanel();
        pan4 = new javax.swing.JPanel();
        scrTree = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        pan5 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan6 = new javax.swing.JPanel();
        pan2 = new javax.swing.JPanel();
        pan106 = new javax.swing.JPanel();
        pan91 = new javax.swing.JPanel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab13 = new javax.swing.JLabel();
        txt1 = new javax.swing.JTextField();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(12, 32767));
        lab14 = new javax.swing.JLabel();
        txt2 = new javax.swing.JTextField();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(12, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(12, 32767));
        lab15 = new javax.swing.JLabel();
        txt3 = new javax.swing.JTextField();
        pan98 = new javax.swing.JPanel();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab30 = new javax.swing.JLabel();
        txt18 = new javax.swing.JTextField();
        btn18 = new javax.swing.JButton();
        pan99 = new javax.swing.JPanel();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab28 = new javax.swing.JLabel();
        txt15 = new javax.swing.JTextField();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab21 = new javax.swing.JLabel();
        txt9 = new javax.swing.JTextField();
        pan100 = new javax.swing.JPanel();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab29 = new javax.swing.JLabel();
        txt16 = new javax.swing.JTextField();
        filler23 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab33 = new javax.swing.JLabel();
        txt8 = new javax.swing.JTextField();
        pan102 = new javax.swing.JPanel();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab16 = new javax.swing.JLabel();
        txt4 = new javax.swing.JTextField();
        filler24 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        labl17 = new javax.swing.JLabel();
        txt5 = new javax.swing.JTextField();
        btn5 = new javax.swing.JButton();
        pan103 = new javax.swing.JPanel();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab27 = new javax.swing.JLabel();
        checkBox1 = new javax.swing.JCheckBox();
        filler25 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(22, 0), new java.awt.Dimension(12, 32767));
        lab20 = new javax.swing.JLabel();
        txt21 = new javax.swing.JTextField();
        pan101 = new javax.swing.JPanel();
        filler22 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab24 = new javax.swing.JLabel();
        txt12 = new javax.swing.JTextField();
        filler33 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab23 = new javax.swing.JLabel();
        txt11 = new javax.swing.JTextField();
        btn11 = new javax.swing.JButton();
        pan96 = new javax.swing.JPanel();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab26 = new javax.swing.JLabel();
        txt14 = new javax.swing.JTextField();
        pan94 = new javax.swing.JPanel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab34 = new javax.swing.JLabel();
        txt22 = new javax.swing.JTextField();
        btn22 = new javax.swing.JButton();
        pan95 = new javax.swing.JPanel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab22 = new javax.swing.JLabel();
        txt10 = new javax.swing.JTextField();
        btn37 = new javax.swing.JButton();
        pan92 = new javax.swing.JPanel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab19 = new javax.swing.JLabel();
        txt7 = new javax.swing.JTextField();
        btn7 = new javax.swing.JButton();
        filler21 = new javax.swing.Box.Filler(new java.awt.Dimension(12, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(12, 32767));
        txt17 = new javax.swing.JTextField();
        btn17 = new javax.swing.JButton();
        pan93 = new javax.swing.JPanel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab31 = new javax.swing.JLabel();
        txt19 = new javax.swing.JTextField();
        txt49 = new javax.swing.JTextField();
        btn19 = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(12, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(12, 32767));
        pan110 = new javax.swing.JPanel();
        filler32 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab32 = new javax.swing.JLabel();
        txt20 = new javax.swing.JTextField();
        txt50 = new javax.swing.JTextField();
        btn20 = new javax.swing.JButton();
        pan7 = new javax.swing.JPanel();
        pan107 = new javax.swing.JPanel();
        pan18 = new javax.swing.JPanel();
        filler31 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab18 = new javax.swing.JLabel();
        txt6 = new javax.swing.JTextField();
        filler38 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(32, 0), new java.awt.Dimension(12, 32767));
        lab42 = new javax.swing.JLabel();
        txt31 = new javax.swing.JTextField();
        btn6 = new javax.swing.JButton();
        pan13 = new javax.swing.JPanel();
        filler29 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab40 = new javax.swing.JLabel();
        txt29 = new javax.swing.JTextField();
        btn25 = new javax.swing.JButton();
        pan17 = new javax.swing.JPanel();
        filler30 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab41 = new javax.swing.JLabel();
        txt30 = new javax.swing.JTextField();
        btn10 = new javax.swing.JButton();
        pan11 = new javax.swing.JPanel();
        filler26 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab36 = new javax.swing.JLabel();
        txt25 = new javax.swing.JTextField();
        btn9 = new javax.swing.JButton();
        filler34 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(12, 32767));
        txt24 = new javax.swing.JTextField();
        btn21 = new javax.swing.JButton();
        pan12 = new javax.swing.JPanel();
        filler27 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab37 = new javax.swing.JLabel();
        txt26 = new javax.swing.JTextField();
        txt51 = new javax.swing.JTextField();
        btn23 = new javax.swing.JButton();
        pan109 = new javax.swing.JPanel();
        filler28 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab38 = new javax.swing.JLabel();
        txt27 = new javax.swing.JTextField();
        txt52 = new javax.swing.JTextField();
        btn24 = new javax.swing.JButton();
        pan8 = new javax.swing.JPanel();
        pan108 = new javax.swing.JPanel();
        pan20 = new javax.swing.JPanel();
        filler35 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab54 = new javax.swing.JLabel();
        txt48 = new javax.swing.JTextField();
        filler44 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(32, 0), new java.awt.Dimension(12, 32767));
        lab55 = new javax.swing.JLabel();
        txt45 = new javax.swing.JTextField();
        filler45 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(12, 32767));
        pan28 = new javax.swing.JPanel();
        filler43 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab56 = new javax.swing.JLabel();
        txt46 = new javax.swing.JTextField();
        filler49 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(32, 0), new java.awt.Dimension(12, 32767));
        lab49 = new javax.swing.JLabel();
        txt39 = new javax.swing.JTextField();
        pan26 = new javax.swing.JPanel();
        filler41 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab50 = new javax.swing.JLabel();
        txt40 = new javax.swing.JTextField();
        btn33 = new javax.swing.JButton();
        filler48 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(12, 32767));
        lab35 = new javax.swing.JLabel();
        txt23 = new javax.swing.JTextField();
        pan24 = new javax.swing.JPanel();
        filler39 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab47 = new javax.swing.JLabel();
        txt37 = new javax.swing.JTextField();
        btn30 = new javax.swing.JButton();
        pan25 = new javax.swing.JPanel();
        filler40 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab48 = new javax.swing.JLabel();
        txt38 = new javax.swing.JTextField();
        btn32 = new javax.swing.JButton();
        pan21 = new javax.swing.JPanel();
        filler36 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab43 = new javax.swing.JLabel();
        txt32 = new javax.swing.JTextField();
        btn26 = new javax.swing.JButton();
        filler46 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(12, 32767));
        txt33 = new javax.swing.JTextField();
        btn16 = new javax.swing.JButton();
        pan22 = new javax.swing.JPanel();
        filler37 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab44 = new javax.swing.JLabel();
        txt34 = new javax.swing.JTextField();
        txt53 = new javax.swing.JTextField();
        btn27 = new javax.swing.JButton();
        pqn109 = new javax.swing.JPanel();
        filler42 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(12, 32767));
        lab45 = new javax.swing.JLabel();
        txt35 = new javax.swing.JTextField();
        txt54 = new javax.swing.JTextField();
        btn28 = new javax.swing.JButton();
        pan3 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan10 = new javax.swing.JPanel();
        lab11 = new javax.swing.JLabel();
        lab12 = new javax.swing.JLabel();
        south = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        lab1 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        lab2 = new javax.swing.JLabel();

        mInsert.setFont(frames.UGui.getFont(1,0));
        mInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        mInsert.setText("��������");
        mInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmTab1.add(mInsert);

        mDelit.setFont(frames.UGui.getFont(1,0));
        mDelit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        mDelit.setText("�������");
        mDelit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmTab1.add(mDelit);
        ppmTab1.add(separator1);

        groups1_id.setFont(frames.UGui.getFont(1,0));
        groups1_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c085.gif"))); // NOI18N
        groups1_id.setText("�������");
        groups1_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmTab1.add(groups1_id);

        groups2_id.setFont(frames.UGui.getFont(1,0));
        groups2_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c085.gif"))); // NOI18N
        groups2_id.setText("������");
        groups2_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmTab1.add(groups2_id);

        groups3_id.setFont(frames.UGui.getFont(1,0));
        groups3_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c085.gif"))); // NOI18N
        groups3_id.setText("������");
        groups3_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmTab1.add(groups3_id);

        groups4_id.setFont(frames.UGui.getFont(1,0));
        groups4_id.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c085.gif"))); // NOI18N
        groups4_id.setText("�����");
        groups4_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmTab1.add(groups4_id);

        texcod.setFont(frames.UGui.getFont(1,0));
        texcod.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c085.gif"))); // NOI18N
        texcod.setText("���-�� ���");
        texcod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmTab1.add(texcod);

        analog.setFont(frames.UGui.getFont(1,0));
        analog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c085.gif"))); // NOI18N
        analog.setText("������");
        analog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmTab1.add(analog);
        ppmTab1.add(separator2);

        height.setFont(frames.UGui.getFont(1,0));
        height.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c087.gif"))); // NOI18N
        height.setText("������");
        height.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmTab1.add(height);

        depth.setFont(frames.UGui.getFont(1,0));
        depth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c087.gif"))); // NOI18N
        depth.setText("�������");
        depth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmClick(evt);
            }
        });
        ppmTab1.add(depth);

        mInsert2.setFont(frames.UGui.getFont(1,0));
        mInsert2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c033.gif"))); // NOI18N
        mInsert2.setText("��������");
        mInsert2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmTab2.add(mInsert2);

        mDelit2.setFont(frames.UGui.getFont(1,0));
        mDelit2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c034.gif"))); // NOI18N
        mDelit2.setText("�������");
        mDelit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmActionItems(evt);
            }
        });
        ppmTab2.add(mDelit2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("��������");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 520));
        setPreferredSize(new java.awt.Dimension(900, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Artikles.this.windowClosed(evt);
            }
        });

        north.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        north.setMaximumSize(new java.awt.Dimension(32767, 31));
        north.setPreferredSize(new java.awt.Dimension(900, 29));

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

        btnMove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c050.gif"))); // NOI18N
        btnMove.setToolTipText(bundle.getString("�����������")); // NOI18N
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
                .addComponent(btnMove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 681, Short.MAX_VALUE)
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, northLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        center.setMinimumSize(new java.awt.Dimension(0, 0));
        center.setPreferredSize(new java.awt.Dimension(900, 550));
        center.setLayout(new javax.swing.BoxLayout(center, javax.swing.BoxLayout.PAGE_AXIS));

        pan9.setMaximumSize(new java.awt.Dimension(2147483647, 900));
        pan9.setMinimumSize(new java.awt.Dimension(800, 34));
        pan9.setPreferredSize(new java.awt.Dimension(800, 900));
        pan9.setLayout(new java.awt.BorderLayout());

        pan4.setPreferredSize(new java.awt.Dimension(220, 500));
        pan4.setLayout(new java.awt.BorderLayout());

        scrTree.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "���� ���������", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));

        tree.setFont(frames.UGui.getFont(0,0));
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("���. ��������");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("�������");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("A���������");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("������");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        scrTree.setViewportView(tree);

        pan4.add(scrTree, java.awt.BorderLayout.CENTER);

        pan9.add(pan4, java.awt.BorderLayout.WEST);

        pan5.setPreferredSize(new java.awt.Dimension(280, 500));
        pan5.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0), "�������� ���������", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0,0)));

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "�������", "��������", "�������", "������", "������", "�����", "������", "�������", "����� %", "���. ���", "������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false, false, false, false, false, false, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab1.setFillsViewportHeight(true);
        tab1.setMinimumSize(new java.awt.Dimension(0, 0));
        tab1.setName("tab1"); // NOI18N
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tab1MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Artikles.this.mousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(0).setMaxWidth(800);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(300);
            tab1.getColumnModel().getColumn(1).setMaxWidth(1800);
            tab1.getColumnModel().getColumn(2).setMinWidth(0);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(2).setMaxWidth(0);
            tab1.getColumnModel().getColumn(3).setMinWidth(0);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(3).setMaxWidth(0);
            tab1.getColumnModel().getColumn(4).setMinWidth(0);
            tab1.getColumnModel().getColumn(4).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(4).setMaxWidth(0);
            tab1.getColumnModel().getColumn(5).setMinWidth(0);
            tab1.getColumnModel().getColumn(5).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(5).setMaxWidth(0);
            tab1.getColumnModel().getColumn(6).setMinWidth(0);
            tab1.getColumnModel().getColumn(6).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(6).setMaxWidth(0);
            tab1.getColumnModel().getColumn(7).setMinWidth(0);
            tab1.getColumnModel().getColumn(7).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(7).setMaxWidth(0);
            tab1.getColumnModel().getColumn(8).setPreferredWidth(26);
            tab1.getColumnModel().getColumn(8).setMaxWidth(120);
            tab1.getColumnModel().getColumn(9).setMinWidth(0);
            tab1.getColumnModel().getColumn(9).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(9).setMaxWidth(0);
            tab1.getColumnModel().getColumn(10).setMinWidth(0);
            tab1.getColumnModel().getColumn(10).setPreferredWidth(0);
            tab1.getColumnModel().getColumn(10).setMaxWidth(0);
            tab1.getColumnModel().getColumn(11).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(11).setMaxWidth(60);
        }

        pan5.add(scr1, java.awt.BorderLayout.CENTER);

        pan9.add(pan5, java.awt.BorderLayout.CENTER);

        pan6.setPreferredSize(new java.awt.Dimension(360, 500));
        pan6.setLayout(new java.awt.CardLayout());

        pan2.setPreferredSize(new java.awt.Dimension(360, 24));
        pan2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        pan106.setPreferredSize(new java.awt.Dimension(360, 12));

        javax.swing.GroupLayout pan106Layout = new javax.swing.GroupLayout(pan106);
        pan106.setLayout(pan106Layout);
        pan106Layout.setHorizontalGroup(
            pan106Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 360, Short.MAX_VALUE)
        );
        pan106Layout.setVerticalGroup(
            pan106Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );

        pan2.add(pan106);

        pan91.setPreferredSize(new java.awt.Dimension(360, 24));
        pan91.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan91.add(filler4);

        lab13.setFont(frames.UGui.getFont(0,0));
        lab13.setText("�����");
        lab13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab13.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab13.setMinimumSize(new java.awt.Dimension(34, 14));
        lab13.setPreferredSize(new java.awt.Dimension(48, 18));
        pan91.add(lab13);

        txt1.setFont(frames.UGui.getFont(0,0));
        txt1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txt1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt1.setName("{3}"); // NOI18N
        txt1.setPreferredSize(new java.awt.Dimension(44, 18));
        pan91.add(txt1);
        pan91.add(filler3);

        lab14.setFont(frames.UGui.getFont(0,0));
        lab14.setText("������");
        lab14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab14.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab14.setMinimumSize(new java.awt.Dimension(34, 14));
        lab14.setPreferredSize(new java.awt.Dimension(48, 18));
        pan91.add(lab14);

        txt2.setFont(frames.UGui.getFont(0,0));
        txt2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt2.setName("{3}"); // NOI18N
        txt2.setPreferredSize(new java.awt.Dimension(44, 18));
        pan91.add(txt2);
        pan91.add(filler5);

        lab15.setFont(frames.UGui.getFont(0,0));
        lab15.setText("�������");
        lab15.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab15.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab15.setMinimumSize(new java.awt.Dimension(34, 14));
        lab15.setPreferredSize(new java.awt.Dimension(54, 18));
        pan91.add(lab15);

        txt3.setFont(frames.UGui.getFont(0,0));
        txt3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt3.setName("{3}"); // NOI18N
        txt3.setPreferredSize(new java.awt.Dimension(44, 18));
        pan91.add(txt3);

        pan2.add(pan91);

        pan98.setPreferredSize(new java.awt.Dimension(360, 24));
        pan98.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan98.add(filler13);

        lab30.setFont(frames.UGui.getFont(0,0));
        lab30.setText("������� ��������");
        lab30.setToolTipText("");
        lab30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab30.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab30.setMinimumSize(new java.awt.Dimension(34, 14));
        lab30.setPreferredSize(new java.awt.Dimension(98, 18));
        pan98.add(lab30);

        txt18.setEditable(false);
        txt18.setFont(frames.UGui.getFont(0,0));
        txt18.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt18.setPreferredSize(new java.awt.Dimension(204, 18));
        pan98.add(txt18);

        btn18.setText("...");
        btn18.setToolTipText(bundle.getString("�������")); // NOI18N
        btn18.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn18.setMaximumSize(new java.awt.Dimension(21, 20));
        btn18.setMinimumSize(new java.awt.Dimension(21, 20));
        btn18.setPreferredSize(new java.awt.Dimension(21, 20));
        btn18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn18(evt);
            }
        });
        pan98.add(btn18);

        pan2.add(pan98);

        pan99.setPreferredSize(new java.awt.Dimension(360, 24));
        pan99.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan99.add(filler14);

        lab28.setFont(frames.UGui.getFont(0,0));
        lab28.setText("������, �����(N)");
        lab28.setToolTipText("");
        lab28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab28.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab28.setMinimumSize(new java.awt.Dimension(34, 14));
        lab28.setPreferredSize(new java.awt.Dimension(98, 18));
        pan99.add(lab28);

        txt15.setFont(frames.UGui.getFont(0,0));
        txt15.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt15.setName("{3}"); // NOI18N
        txt15.setPreferredSize(new java.awt.Dimension(40, 18));
        pan99.add(txt15);
        pan99.add(filler16);

        lab21.setFont(frames.UGui.getFont(0,0));
        lab21.setText("����. ��� (F)");
        lab21.setToolTipText("");
        lab21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab21.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab21.setMinimumSize(new java.awt.Dimension(34, 14));
        lab21.setPreferredSize(new java.awt.Dimension(104, 18));
        pan99.add(lab21);

        txt9.setFont(frames.UGui.getFont(0,0));
        txt9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt9.setName("{3}"); // NOI18N
        txt9.setPreferredSize(new java.awt.Dimension(44, 18));
        pan99.add(txt9);

        pan2.add(pan99);

        pan100.setPreferredSize(new java.awt.Dimension(360, 24));
        pan100.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan100.add(filler15);

        lab29.setFont(frames.UGui.getFont(0,0));
        lab29.setText("����. �������(T) ");
        lab29.setToolTipText("");
        lab29.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab29.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab29.setMinimumSize(new java.awt.Dimension(34, 14));
        lab29.setPreferredSize(new java.awt.Dimension(98, 18));
        pan100.add(lab29);

        txt16.setFont(frames.UGui.getFont(0,0));
        txt16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt16.setName("{3}"); // NOI18N
        txt16.setPreferredSize(new java.awt.Dimension(40, 18));
        pan100.add(txt16);
        pan100.add(filler23);

        lab33.setFont(frames.UGui.getFont(0,0));
        lab33.setText("�� ���� �� ��� (B)");
        lab33.setToolTipText("");
        lab33.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab33.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab33.setMinimumSize(new java.awt.Dimension(34, 14));
        lab33.setPreferredSize(new java.awt.Dimension(104, 18));
        pan100.add(lab33);

        txt8.setFont(frames.UGui.getFont(0,0));
        txt8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt8.setName("{3}"); // NOI18N
        txt8.setPreferredSize(new java.awt.Dimension(44, 18));
        pan100.add(txt8);

        pan2.add(pan100);

        pan102.setPreferredSize(new java.awt.Dimension(360, 24));
        pan102.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan102.add(filler17);

        lab16.setFont(frames.UGui.getFont(0,0));
        lab16.setText("��.��� ��/��");
        lab16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab16.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab16.setMinimumSize(new java.awt.Dimension(34, 14));
        lab16.setPreferredSize(new java.awt.Dimension(98, 18));
        pan102.add(lab16);

        txt4.setFont(frames.UGui.getFont(0,0));
        txt4.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt4.setName("{3}"); // NOI18N
        txt4.setPreferredSize(new java.awt.Dimension(40, 18));
        pan102.add(txt4);
        pan102.add(filler24);

        labl17.setFont(frames.UGui.getFont(0,0));
        labl17.setText("��.���������");
        labl17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        labl17.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        labl17.setMinimumSize(new java.awt.Dimension(34, 14));
        labl17.setPreferredSize(new java.awt.Dimension(104, 18));
        pan102.add(labl17);

        txt5.setEditable(false);
        txt5.setFont(frames.UGui.getFont(0,0));
        txt5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt5.setPreferredSize(new java.awt.Dimension(44, 18));
        pan102.add(txt5);

        btn5.setText("...");
        btn5.setToolTipText(bundle.getString("�������")); // NOI18N
        btn5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn5.setMaximumSize(new java.awt.Dimension(21, 20));
        btn5.setMinimumSize(new java.awt.Dimension(21, 20));
        btn5.setPreferredSize(new java.awt.Dimension(21, 20));
        btn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn5(evt);
            }
        });
        pan102.add(btn5);

        pan2.add(pan102);

        pan103.setPreferredSize(new java.awt.Dimension(360, 24));
        pan103.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan103.add(filler18);

        lab27.setFont(frames.UGui.getFont(0,0));
        lab27.setText("� �����������");
        lab27.setToolTipText("");
        lab27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab27.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab27.setMinimumSize(new java.awt.Dimension(34, 14));
        lab27.setPreferredSize(new java.awt.Dimension(98, 18));
        pan103.add(lab27);

        checkBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        checkBox1.setMaximumSize(new java.awt.Dimension(20, 20));
        checkBox1.setMinimumSize(new java.awt.Dimension(20, 20));
        checkBox1.setPreferredSize(new java.awt.Dimension(20, 20));
        checkBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox1Action(evt);
            }
        });
        pan103.add(checkBox1);
        pan103.add(filler25);

        lab20.setFont(frames.UGui.getFont(0,0));
        lab20.setText("������� �����");
        lab20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab20.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab20.setMinimumSize(new java.awt.Dimension(34, 14));
        lab20.setPreferredSize(new java.awt.Dimension(104, 18));
        pan103.add(lab20);

        txt21.setFont(frames.UGui.getFont(0,0));
        txt21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt21.setName("{3}"); // NOI18N
        txt21.setPreferredSize(new java.awt.Dimension(44, 18));
        pan103.add(txt21);

        pan2.add(pan103);

        pan101.setPreferredSize(new java.awt.Dimension(360, 24));
        pan101.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan101.add(filler22);

        lab24.setFont(frames.UGui.getFont(0,0));
        lab24.setText("���. ������ ����");
        lab24.setToolTipText("");
        lab24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab24.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab24.setMinimumSize(new java.awt.Dimension(34, 14));
        lab24.setPreferredSize(new java.awt.Dimension(98, 18));
        pan101.add(lab24);

        txt12.setFont(frames.UGui.getFont(0,0));
        txt12.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt12.setName("{3}"); // NOI18N
        txt12.setPreferredSize(new java.awt.Dimension(40, 18));
        pan101.add(txt12);
        pan101.add(filler33);

        lab23.setFont(frames.UGui.getFont(0,0));
        lab23.setText("������");
        lab23.setToolTipText("");
        lab23.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab23.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab23.setMinimumSize(new java.awt.Dimension(34, 14));
        lab23.setPreferredSize(new java.awt.Dimension(60, 18));
        pan101.add(lab23);

        txt11.setEditable(false);
        txt11.setFont(frames.UGui.getFont(0,0));
        txt11.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt11.setMinimumSize(new java.awt.Dimension(88, 18));
        txt11.setPreferredSize(new java.awt.Dimension(88, 18));
        pan101.add(txt11);

        btn11.setText("...");
        btn11.setToolTipText(bundle.getString("�������")); // NOI18N
        btn11.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn11.setMinimumSize(new java.awt.Dimension(21, 20));
        btn11.setPreferredSize(new java.awt.Dimension(21, 20));
        btn11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn11(evt);
            }
        });
        pan101.add(btn11);

        pan2.add(pan101);

        pan96.setPreferredSize(new java.awt.Dimension(360, 24));
        pan96.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan96.add(filler11);

        lab26.setFont(frames.UGui.getFont(0,0));
        lab26.setText("����-�� ���");
        lab26.setToolTipText("");
        lab26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab26.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab26.setMinimumSize(new java.awt.Dimension(34, 14));
        lab26.setPreferredSize(new java.awt.Dimension(98, 18));
        pan96.add(lab26);

        txt14.setFont(frames.UGui.getFont(0,0));
        txt14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt14.setPreferredSize(new java.awt.Dimension(230, 18));
        pan96.add(txt14);

        pan2.add(pan96);

        pan94.setPreferredSize(new java.awt.Dimension(360, 24));
        pan94.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan94.add(filler9);

        lab34.setFont(frames.UGui.getFont(0,0));
        lab34.setText("������");
        lab34.setToolTipText("");
        lab34.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab34.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab34.setMinimumSize(new java.awt.Dimension(34, 14));
        lab34.setPreferredSize(new java.awt.Dimension(98, 18));
        pan94.add(lab34);

        txt22.setEditable(false);
        txt22.setFont(frames.UGui.getFont(0,0));
        txt22.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt22.setPreferredSize(new java.awt.Dimension(204, 18));
        pan94.add(txt22);

        btn22.setText("...");
        btn22.setToolTipText(bundle.getString("�������")); // NOI18N
        btn22.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn22.setMaximumSize(new java.awt.Dimension(21, 20));
        btn22.setMinimumSize(new java.awt.Dimension(21, 20));
        btn22.setPreferredSize(new java.awt.Dimension(21, 20));
        btn22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn22(evt);
            }
        });
        pan94.add(btn22);

        pan2.add(pan94);

        pan95.setPreferredSize(new java.awt.Dimension(360, 24));
        pan95.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan95.add(filler10);

        lab22.setFont(frames.UGui.getFont(0,0));
        lab22.setText("�����");
        lab22.setToolTipText("");
        lab22.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab22.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab22.setMinimumSize(new java.awt.Dimension(34, 14));
        lab22.setPreferredSize(new java.awt.Dimension(98, 18));
        pan95.add(lab22);

        txt10.setEditable(false);
        txt10.setFont(frames.UGui.getFont(0,0));
        txt10.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt10.setPreferredSize(new java.awt.Dimension(204, 18));
        pan95.add(txt10);

        btn37.setText("...");
        btn37.setToolTipText(bundle.getString("�������")); // NOI18N
        btn37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn37.setMaximumSize(new java.awt.Dimension(21, 20));
        btn37.setMinimumSize(new java.awt.Dimension(21, 20));
        btn37.setPreferredSize(new java.awt.Dimension(21, 20));
        btn37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn37(evt);
            }
        });
        pan95.add(btn37);

        pan2.add(pan95);

        pan92.setPreferredSize(new java.awt.Dimension(360, 24));
        pan92.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan92.add(filler6);

        lab19.setFont(frames.UGui.getFont(0,0));
        lab19.setText("������");
        lab19.setToolTipText("");
        lab19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab19.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab19.setMinimumSize(new java.awt.Dimension(34, 14));
        lab19.setPreferredSize(new java.awt.Dimension(98, 18));
        pan92.add(lab19);

        txt7.setEditable(false);
        txt7.setFont(frames.UGui.getFont(0,0));
        txt7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt7.setPreferredSize(new java.awt.Dimension(80, 18));
        pan92.add(txt7);

        btn7.setText("...");
        btn7.setToolTipText(bundle.getString("�������")); // NOI18N
        btn7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn7.setMaximumSize(new java.awt.Dimension(21, 20));
        btn7.setMinimumSize(new java.awt.Dimension(21, 20));
        btn7.setName("btn7"); // NOI18N
        btn7.setPreferredSize(new java.awt.Dimension(21, 20));
        btn7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7(evt);
            }
        });
        pan92.add(btn7);
        pan92.add(filler21);

        txt17.setEditable(false);
        txt17.setFont(frames.UGui.getFont(0,0));
        txt17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt17.setPreferredSize(new java.awt.Dimension(80, 18));
        pan92.add(txt17);

        btn17.setText("...");
        btn17.setToolTipText(bundle.getString("�������")); // NOI18N
        btn17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn17.setMaximumSize(new java.awt.Dimension(21, 20));
        btn17.setMinimumSize(new java.awt.Dimension(21, 20));
        btn17.setName("btn17"); // NOI18N
        btn17.setPreferredSize(new java.awt.Dimension(21, 20));
        btn17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7(evt);
            }
        });
        pan92.add(btn17);

        pan2.add(pan92);

        pan93.setPreferredSize(new java.awt.Dimension(360, 24));
        pan93.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan93.add(filler7);

        lab31.setFont(frames.UGui.getFont(0,0));
        lab31.setText("������� (����)");
        lab31.setToolTipText("");
        lab31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab31.setMinimumSize(new java.awt.Dimension(34, 14));
        lab31.setPreferredSize(new java.awt.Dimension(98, 18));
        pan93.add(lab31);

        txt19.setEditable(false);
        txt19.setFont(frames.UGui.getFont(0,0));
        txt19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt19.setMinimumSize(new java.awt.Dimension(46, 18));
        txt19.setPreferredSize(new java.awt.Dimension(46, 18));
        pan93.add(txt19);

        txt49.setEditable(false);
        txt49.setFont(frames.UGui.getFont(0,0));
        txt49.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt49.setPreferredSize(new java.awt.Dimension(153, 18));
        pan93.add(txt49);

        btn19.setText("...");
        btn19.setToolTipText(bundle.getString("�������")); // NOI18N
        btn19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn19.setMaximumSize(new java.awt.Dimension(21, 20));
        btn19.setMinimumSize(new java.awt.Dimension(21, 20));
        btn19.setPreferredSize(new java.awt.Dimension(21, 20));
        btn19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn19(evt);
            }
        });
        pan93.add(btn19);
        pan93.add(filler8);

        pan2.add(pan93);

        pan110.setPreferredSize(new java.awt.Dimension(360, 24));
        pan110.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan110.add(filler32);

        lab32.setFont(frames.UGui.getFont(0,0));
        lab32.setText("������ (%)");
        lab32.setToolTipText("");
        lab32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab32.setMinimumSize(new java.awt.Dimension(34, 14));
        lab32.setPreferredSize(new java.awt.Dimension(98, 18));
        pan110.add(lab32);

        txt20.setEditable(false);
        txt20.setFont(frames.UGui.getFont(0,0));
        txt20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt20.setPreferredSize(new java.awt.Dimension(46, 18));
        pan110.add(txt20);

        txt50.setEditable(false);
        txt50.setFont(frames.UGui.getFont(0,0));
        txt50.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt50.setPreferredSize(new java.awt.Dimension(153, 18));
        pan110.add(txt50);

        btn20.setText("...");
        btn20.setToolTipText(bundle.getString("�������")); // NOI18N
        btn20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn20.setMaximumSize(new java.awt.Dimension(21, 20));
        btn20.setMinimumSize(new java.awt.Dimension(21, 20));
        btn20.setPreferredSize(new java.awt.Dimension(21, 20));
        btn20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn20(evt);
            }
        });
        pan110.add(btn20);

        pan2.add(pan110);

        pan6.add(pan2, "pan2");

        pan7.setPreferredSize(new java.awt.Dimension(360, 24));
        pan7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        pan107.setPreferredSize(new java.awt.Dimension(360, 12));

        javax.swing.GroupLayout pan107Layout = new javax.swing.GroupLayout(pan107);
        pan107.setLayout(pan107Layout);
        pan107Layout.setHorizontalGroup(
            pan107Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 360, Short.MAX_VALUE)
        );
        pan107Layout.setVerticalGroup(
            pan107Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );

        pan7.add(pan107);

        pan18.setPreferredSize(new java.awt.Dimension(360, 24));
        pan18.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan18.add(filler31);

        lab18.setFont(frames.UGui.getFont(0,0));
        lab18.setText("��.��� ��/��");
        lab18.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab18.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab18.setMinimumSize(new java.awt.Dimension(34, 14));
        lab18.setPreferredSize(new java.awt.Dimension(98, 18));
        pan18.add(lab18);

        txt6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt6.setName("{3}"); // NOI18N
        txt6.setPreferredSize(new java.awt.Dimension(44, 18));
        pan18.add(txt6);
        pan18.add(filler38);

        lab42.setFont(frames.UGui.getFont(0,0));
        lab42.setText("��.���.");
        lab42.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab42.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab42.setMinimumSize(new java.awt.Dimension(34, 14));
        lab42.setPreferredSize(new java.awt.Dimension(68, 18));
        pan18.add(lab42);

        txt31.setEditable(false);
        txt31.setFont(frames.UGui.getFont(0,0));
        txt31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt31.setPreferredSize(new java.awt.Dimension(44, 18));
        txt31.setRequestFocusEnabled(false);
        pan18.add(txt31);

        btn6.setText("...");
        btn6.setToolTipText(bundle.getString("�������")); // NOI18N
        btn6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn6.setMaximumSize(new java.awt.Dimension(21, 20));
        btn6.setMinimumSize(new java.awt.Dimension(21, 20));
        btn6.setPreferredSize(new java.awt.Dimension(21, 20));
        btn6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn5(evt);
            }
        });
        pan18.add(btn6);

        pan7.add(pan18);

        pan13.setPreferredSize(new java.awt.Dimension(360, 24));
        pan13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan13.add(filler29);

        lab40.setFont(frames.UGui.getFont(0,0));
        lab40.setText("������");
        lab40.setToolTipText("");
        lab40.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab40.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab40.setMinimumSize(new java.awt.Dimension(34, 14));
        lab40.setPreferredSize(new java.awt.Dimension(98, 18));
        pan13.add(lab40);

        txt29.setEditable(false);
        txt29.setFont(frames.UGui.getFont(0,0));
        txt29.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt29.setPreferredSize(new java.awt.Dimension(204, 18));
        pan13.add(txt29);

        btn25.setText("...");
        btn25.setToolTipText(bundle.getString("�������")); // NOI18N
        btn25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn25.setMaximumSize(new java.awt.Dimension(21, 20));
        btn25.setMinimumSize(new java.awt.Dimension(21, 20));
        btn25.setPreferredSize(new java.awt.Dimension(21, 20));
        btn25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn22(evt);
            }
        });
        pan13.add(btn25);

        pan7.add(pan13);

        pan17.setPreferredSize(new java.awt.Dimension(360, 24));
        pan17.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan17.add(filler30);

        lab41.setFont(frames.UGui.getFont(0,0));
        lab41.setText("�����");
        lab41.setToolTipText("");
        lab41.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab41.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab41.setMinimumSize(new java.awt.Dimension(34, 14));
        lab41.setPreferredSize(new java.awt.Dimension(98, 18));
        pan17.add(lab41);

        txt30.setEditable(false);
        txt30.setFont(frames.UGui.getFont(0,0));
        txt30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt30.setPreferredSize(new java.awt.Dimension(204, 18));
        pan17.add(txt30);

        btn10.setText("...");
        btn10.setToolTipText(bundle.getString("�������")); // NOI18N
        btn10.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn10.setMaximumSize(new java.awt.Dimension(21, 20));
        btn10.setMinimumSize(new java.awt.Dimension(21, 20));
        btn10.setPreferredSize(new java.awt.Dimension(21, 20));
        btn10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn37(evt);
            }
        });
        pan17.add(btn10);

        pan7.add(pan17);

        pan11.setPreferredSize(new java.awt.Dimension(360, 24));
        pan11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan11.add(filler26);

        lab36.setFont(frames.UGui.getFont(0,0));
        lab36.setText("������");
        lab36.setToolTipText("");
        lab36.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab36.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab36.setMinimumSize(new java.awt.Dimension(34, 14));
        lab36.setPreferredSize(new java.awt.Dimension(98, 18));
        pan11.add(lab36);

        txt25.setEditable(false);
        txt25.setFont(frames.UGui.getFont(0,0));
        txt25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt25.setPreferredSize(new java.awt.Dimension(80, 18));
        pan11.add(txt25);

        btn9.setText("...");
        btn9.setToolTipText(bundle.getString("�������")); // NOI18N
        btn9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn9.setMaximumSize(new java.awt.Dimension(21, 20));
        btn9.setMinimumSize(new java.awt.Dimension(21, 20));
        btn9.setName("btnField7"); // NOI18N
        btn9.setPreferredSize(new java.awt.Dimension(21, 20));
        btn9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7(evt);
            }
        });
        pan11.add(btn9);
        pan11.add(filler34);

        txt24.setEditable(false);
        txt24.setFont(frames.UGui.getFont(0,0));
        txt24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt24.setPreferredSize(new java.awt.Dimension(80, 18));
        pan11.add(txt24);

        btn21.setText("...");
        btn21.setToolTipText(bundle.getString("�������")); // NOI18N
        btn21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn21.setMaximumSize(new java.awt.Dimension(21, 20));
        btn21.setMinimumSize(new java.awt.Dimension(21, 20));
        btn21.setName("btnField17"); // NOI18N
        btn21.setPreferredSize(new java.awt.Dimension(21, 20));
        btn21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7(evt);
            }
        });
        pan11.add(btn21);

        pan7.add(pan11);

        pan12.setPreferredSize(new java.awt.Dimension(360, 24));
        pan12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan12.add(filler27);

        lab37.setFont(frames.UGui.getFont(0,0));
        lab37.setText("������� (����)");
        lab37.setToolTipText("");
        lab37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab37.setMinimumSize(new java.awt.Dimension(34, 14));
        lab37.setPreferredSize(new java.awt.Dimension(98, 18));
        pan12.add(lab37);

        txt26.setEditable(false);
        txt26.setFont(frames.UGui.getFont(0,0));
        txt26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt26.setPreferredSize(new java.awt.Dimension(40, 18));
        pan12.add(txt26);

        txt51.setEditable(false);
        txt51.setFont(frames.UGui.getFont(0,0));
        txt51.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt51.setPreferredSize(new java.awt.Dimension(159, 18));
        pan12.add(txt51);

        btn23.setText("...");
        btn23.setToolTipText(bundle.getString("�������")); // NOI18N
        btn23.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn23.setMaximumSize(new java.awt.Dimension(21, 20));
        btn23.setMinimumSize(new java.awt.Dimension(21, 20));
        btn23.setPreferredSize(new java.awt.Dimension(21, 20));
        btn23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn19(evt);
            }
        });
        pan12.add(btn23);

        pan7.add(pan12);

        pan109.setPreferredSize(new java.awt.Dimension(360, 24));
        pan109.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan109.add(filler28);

        lab38.setFont(frames.UGui.getFont(0,0));
        lab38.setText("������ (%)");
        lab38.setToolTipText("");
        lab38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab38.setMinimumSize(new java.awt.Dimension(34, 14));
        lab38.setPreferredSize(new java.awt.Dimension(98, 18));
        pan109.add(lab38);

        txt27.setEditable(false);
        txt27.setFont(frames.UGui.getFont(0,0));
        txt27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt27.setPreferredSize(new java.awt.Dimension(40, 18));
        pan109.add(txt27);

        txt52.setEditable(false);
        txt52.setFont(frames.UGui.getFont(0,0));
        txt52.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt52.setPreferredSize(new java.awt.Dimension(159, 18));
        pan109.add(txt52);

        btn24.setText("...");
        btn24.setToolTipText(bundle.getString("�������")); // NOI18N
        btn24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn24.setMaximumSize(new java.awt.Dimension(21, 20));
        btn24.setMinimumSize(new java.awt.Dimension(21, 20));
        btn24.setPreferredSize(new java.awt.Dimension(21, 20));
        btn24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn20(evt);
            }
        });
        pan109.add(btn24);

        pan7.add(pan109);

        pan6.add(pan7, "pan7");

        pan8.setPreferredSize(new java.awt.Dimension(360, 24));
        pan8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        pan108.setPreferredSize(new java.awt.Dimension(360, 12));

        javax.swing.GroupLayout pan108Layout = new javax.swing.GroupLayout(pan108);
        pan108.setLayout(pan108Layout);
        pan108Layout.setHorizontalGroup(
            pan108Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 360, Short.MAX_VALUE)
        );
        pan108Layout.setVerticalGroup(
            pan108Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 12, Short.MAX_VALUE)
        );

        pan8.add(pan108);

        pan20.setPreferredSize(new java.awt.Dimension(360, 24));
        pan20.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan20.add(filler35);

        lab54.setFont(frames.UGui.getFont(0,0));
        lab54.setText("�����");
        lab54.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab54.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab54.setMinimumSize(new java.awt.Dimension(34, 14));
        lab54.setPreferredSize(new java.awt.Dimension(98, 18));
        pan20.add(lab54);

        txt48.setFont(frames.UGui.getFont(0,0));
        txt48.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt48.setName("{3}"); // NOI18N
        txt48.setPreferredSize(new java.awt.Dimension(44, 18));
        pan20.add(txt48);
        pan20.add(filler44);

        lab55.setFont(frames.UGui.getFont(0,0));
        lab55.setText("������");
        lab55.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab55.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab55.setMinimumSize(new java.awt.Dimension(34, 14));
        lab55.setPreferredSize(new java.awt.Dimension(74, 18));
        pan20.add(lab55);

        txt45.setFont(frames.UGui.getFont(0,0));
        txt45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt45.setName("{3}"); // NOI18N
        txt45.setPreferredSize(new java.awt.Dimension(44, 18));
        pan20.add(txt45);
        pan20.add(filler45);

        pan8.add(pan20);

        pan28.setPreferredSize(new java.awt.Dimension(360, 24));
        pan28.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan28.add(filler43);

        lab56.setFont(frames.UGui.getFont(0,0));
        lab56.setText("�������");
        lab56.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab56.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab56.setMinimumSize(new java.awt.Dimension(34, 14));
        lab56.setPreferredSize(new java.awt.Dimension(98, 18));
        pan28.add(lab56);

        txt46.setFont(frames.UGui.getFont(0,0));
        txt46.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt46.setName("{3}"); // NOI18N
        txt46.setPreferredSize(new java.awt.Dimension(44, 18));
        pan28.add(txt46);
        pan28.add(filler49);

        lab49.setFont(frames.UGui.getFont(0,0));
        lab49.setText("��.��� ��/��");
        lab49.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab49.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab49.setMinimumSize(new java.awt.Dimension(34, 14));
        lab49.setPreferredSize(new java.awt.Dimension(74, 18));
        pan28.add(lab49);

        txt39.setFont(frames.UGui.getFont(0,0));
        txt39.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt39.setName("{3}"); // NOI18N
        txt39.setPreferredSize(new java.awt.Dimension(44, 18));
        pan28.add(txt39);

        pan8.add(pan28);

        pan26.setPreferredSize(new java.awt.Dimension(360, 24));
        pan26.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan26.add(filler41);

        lab50.setFont(frames.UGui.getFont(0,0));
        lab50.setText("��.���������");
        lab50.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab50.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab50.setMinimumSize(new java.awt.Dimension(34, 14));
        lab50.setPreferredSize(new java.awt.Dimension(98, 18));
        pan26.add(lab50);

        txt40.setEditable(false);
        txt40.setFont(frames.UGui.getFont(0,0));
        txt40.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt40.setPreferredSize(new java.awt.Dimension(44, 18));
        pan26.add(txt40);

        btn33.setText("...");
        btn33.setToolTipText(bundle.getString("�������")); // NOI18N
        btn33.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn33.setMaximumSize(new java.awt.Dimension(21, 20));
        btn33.setMinimumSize(new java.awt.Dimension(21, 20));
        btn33.setPreferredSize(new java.awt.Dimension(21, 20));
        btn33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn5(evt);
            }
        });
        pan26.add(btn33);
        pan26.add(filler48);

        lab35.setFont(frames.UGui.getFont(0,0));
        lab35.setText("������� �����");
        lab35.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab35.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab35.setMinimumSize(new java.awt.Dimension(34, 14));
        lab35.setPreferredSize(new java.awt.Dimension(98, 18));
        pan26.add(lab35);

        txt23.setFont(frames.UGui.getFont(0,0));
        txt23.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt23.setName("{3}"); // NOI18N
        txt23.setPreferredSize(new java.awt.Dimension(40, 18));
        pan26.add(txt23);

        pan8.add(pan26);

        pan24.setPreferredSize(new java.awt.Dimension(360, 24));
        pan24.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan24.add(filler39);

        lab47.setFont(frames.UGui.getFont(0,0));
        lab47.setText("������");
        lab47.setToolTipText("");
        lab47.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab47.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab47.setMinimumSize(new java.awt.Dimension(34, 14));
        lab47.setPreferredSize(new java.awt.Dimension(98, 18));
        pan24.add(lab47);

        txt37.setEditable(false);
        txt37.setFont(frames.UGui.getFont(0,0));
        txt37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt37.setPreferredSize(new java.awt.Dimension(204, 18));
        pan24.add(txt37);

        btn30.setText("...");
        btn30.setToolTipText(bundle.getString("�������")); // NOI18N
        btn30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn30.setMaximumSize(new java.awt.Dimension(21, 20));
        btn30.setMinimumSize(new java.awt.Dimension(21, 20));
        btn30.setPreferredSize(new java.awt.Dimension(21, 20));
        btn30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn22(evt);
            }
        });
        pan24.add(btn30);

        pan8.add(pan24);

        pan25.setPreferredSize(new java.awt.Dimension(360, 24));
        pan25.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan25.add(filler40);

        lab48.setFont(frames.UGui.getFont(0,0));
        lab48.setText("�����");
        lab48.setToolTipText("");
        lab48.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab48.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab48.setMinimumSize(new java.awt.Dimension(34, 14));
        lab48.setPreferredSize(new java.awt.Dimension(98, 18));
        pan25.add(lab48);

        txt38.setEditable(false);
        txt38.setFont(frames.UGui.getFont(0,0));
        txt38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt38.setPreferredSize(new java.awt.Dimension(204, 18));
        pan25.add(txt38);

        btn32.setText("...");
        btn32.setToolTipText(bundle.getString("�������")); // NOI18N
        btn32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn32.setMaximumSize(new java.awt.Dimension(21, 20));
        btn32.setMinimumSize(new java.awt.Dimension(21, 20));
        btn32.setPreferredSize(new java.awt.Dimension(21, 20));
        btn32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn37(evt);
            }
        });
        pan25.add(btn32);

        pan8.add(pan25);

        pan21.setPreferredSize(new java.awt.Dimension(360, 24));
        pan21.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan21.add(filler36);

        lab43.setFont(frames.UGui.getFont(0,0));
        lab43.setText("������");
        lab43.setToolTipText("");
        lab43.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab43.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab43.setMinimumSize(new java.awt.Dimension(34, 14));
        lab43.setPreferredSize(new java.awt.Dimension(98, 18));
        pan21.add(lab43);

        txt32.setEditable(false);
        txt32.setFont(frames.UGui.getFont(0,0));
        txt32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt32.setPreferredSize(new java.awt.Dimension(80, 18));
        pan21.add(txt32);

        btn26.setText("...");
        btn26.setToolTipText(bundle.getString("�������")); // NOI18N
        btn26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn26.setMaximumSize(new java.awt.Dimension(21, 20));
        btn26.setMinimumSize(new java.awt.Dimension(21, 20));
        btn26.setName("btnField17"); // NOI18N
        btn26.setPreferredSize(new java.awt.Dimension(21, 20));
        btn26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7(evt);
            }
        });
        pan21.add(btn26);
        pan21.add(filler46);

        txt33.setEditable(false);
        txt33.setFont(frames.UGui.getFont(0,0));
        txt33.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt33.setPreferredSize(new java.awt.Dimension(82, 18));
        pan21.add(txt33);

        btn16.setText("...");
        btn16.setToolTipText(bundle.getString("�������")); // NOI18N
        btn16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn16.setMaximumSize(new java.awt.Dimension(21, 20));
        btn16.setMinimumSize(new java.awt.Dimension(21, 20));
        btn16.setName("btnField7"); // NOI18N
        btn16.setPreferredSize(new java.awt.Dimension(21, 20));
        btn16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn7(evt);
            }
        });
        pan21.add(btn16);

        pan8.add(pan21);

        pan22.setPreferredSize(new java.awt.Dimension(360, 24));
        pan22.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pan22.add(filler37);

        lab44.setFont(frames.UGui.getFont(0,0));
        lab44.setText("������� (����)");
        lab44.setToolTipText("");
        lab44.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab44.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab44.setMinimumSize(new java.awt.Dimension(34, 14));
        lab44.setPreferredSize(new java.awt.Dimension(98, 18));
        pan22.add(lab44);

        txt34.setEditable(false);
        txt34.setFont(frames.UGui.getFont(0,0));
        txt34.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt34.setPreferredSize(new java.awt.Dimension(40, 18));
        pan22.add(txt34);

        txt53.setEditable(false);
        txt53.setFont(frames.UGui.getFont(0,0));
        txt53.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt53.setPreferredSize(new java.awt.Dimension(159, 18));
        pan22.add(txt53);

        btn27.setText("...");
        btn27.setToolTipText(bundle.getString("�������")); // NOI18N
        btn27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn27.setMaximumSize(new java.awt.Dimension(21, 20));
        btn27.setMinimumSize(new java.awt.Dimension(21, 20));
        btn27.setPreferredSize(new java.awt.Dimension(21, 20));
        btn27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn19(evt);
            }
        });
        pan22.add(btn27);

        pan8.add(pan22);

        pqn109.setPreferredSize(new java.awt.Dimension(360, 24));
        pqn109.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        pqn109.add(filler42);

        lab45.setFont(frames.UGui.getFont(0,0));
        lab45.setText("������ (%)");
        lab45.setToolTipText("");
        lab45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab45.setMinimumSize(new java.awt.Dimension(34, 14));
        lab45.setPreferredSize(new java.awt.Dimension(98, 18));
        pqn109.add(lab45);

        txt35.setEditable(false);
        txt35.setFont(frames.UGui.getFont(0,0));
        txt35.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt35.setPreferredSize(new java.awt.Dimension(40, 18));
        pqn109.add(txt35);

        txt54.setEditable(false);
        txt54.setFont(frames.UGui.getFont(0,0));
        txt54.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt54.setPreferredSize(new java.awt.Dimension(159, 18));
        pqn109.add(txt54);

        btn28.setText("...");
        btn28.setToolTipText(bundle.getString("�������")); // NOI18N
        btn28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn28.setMaximumSize(new java.awt.Dimension(21, 20));
        btn28.setMinimumSize(new java.awt.Dimension(21, 20));
        btn28.setPreferredSize(new java.awt.Dimension(21, 20));
        btn28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn20(evt);
            }
        });
        pqn109.add(btn28);

        pan8.add(pqn109);

        pan6.add(pan8, "pan8");

        pan9.add(pan6, java.awt.BorderLayout.EAST);

        center.add(pan9);

        pan3.setMaximumSize(new java.awt.Dimension(2147483647, 300));
        pan3.setPreferredSize(new java.awt.Dimension(800, 300));
        pan3.setLayout(new java.awt.BorderLayout());

        scr2.setBorder(null);

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "������", "��������", "���������", "��������", "���������", "����������", "���������", "�������", "�������������", "�� ��. ����", "����. ����.��������", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, true, true, true, true, true, false
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
                tab2MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Artikles.this.mousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        tab2.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(120);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(160);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(20);
            tab2.getColumnModel().getColumn(2).setMaxWidth(120);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(4).setPreferredWidth(20);
            tab2.getColumnModel().getColumn(4).setMaxWidth(120);
            tab2.getColumnModel().getColumn(5).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(6).setPreferredWidth(20);
            tab2.getColumnModel().getColumn(6).setMaxWidth(120);
            tab2.getColumnModel().getColumn(7).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(8).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(9).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(10).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(11).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(11).setMaxWidth(60);
        }

        pan3.add(scr2, java.awt.BorderLayout.CENTER);

        pan10.setPreferredSize(new java.awt.Dimension(981, 20));
        pan10.setLayout(new java.awt.BorderLayout());

        lab11.setFont(frames.UGui.getFont(0,0));
        lab11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lab11.setText("                                                              " + java.util.ResourceBundle.getBundle("resource/hints/okno", common.eProp.locale).getString("Title.�������� ���������")); // NOI18N
        lab11.setAlignmentX(200.0F);
        pan10.add(lab11, java.awt.BorderLayout.CENTER);

        lab12.setText("���. ��������/");
        lab12.setPreferredSize(pan4.getPreferredSize());
        pan10.add(lab12, java.awt.BorderLayout.WEST);

        pan3.add(pan10, java.awt.BorderLayout.NORTH);

        center.add(pan3);

        getContentPane().add(center, java.awt.BorderLayout.CENTER);

        south.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.setMaximumSize(new java.awt.Dimension(2902, 20));
        south.setMinimumSize(new java.awt.Dimension(100, 20));
        south.setPreferredSize(new java.awt.Dimension(900, 20));
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.LINE_AXIS));

        filler1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.add(filler1);

        lab1.setFont(frames.UGui.getFont(0,0));
        lab1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lab1.setText("___");
        lab1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        lab1.setMaximumSize(new java.awt.Dimension(110, 14));
        lab1.setPreferredSize(new java.awt.Dimension(110, 14));
        lab1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        south.add(lab1);

        filler2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        south.add(filler2);

        lab2.setFont(frames.UGui.getFont(0,0));
        lab2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lab2.setText("___");
        lab2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        lab2.setMaximumSize(new java.awt.Dimension(80, 14));
        lab2.setPreferredSize(new java.awt.Dimension(80, 14));
        lab2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        south.add(lab2);

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());
    }//GEN-LAST:event_windowClosed

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert
        if (tab1.getBorder() != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                TypeArt typeArtikl = (TypeArt) node.getUserObject();
                UGui.insertRecordCur(tab1, eArtikl.up, (record) -> {
                    record.setNo(eArtikl.level1, typeArtikl.id1);
                    record.setNo(eArtikl.level2, typeArtikl.id2);
                    record.setNo(eArtikl.otx_norm, 0);
                    if (record.getInt(eArtikl.level1) == 1 && List.of(1, 2, 3)
                            .contains(record.getInt(eArtikl.level2))) {
                        record.setNo(eArtikl.len_unit, 6500);
                    }

                    if (typeArtikl.id1 == 1 || typeArtikl.id1 == 3) {
                        record.setNo(eArtikl.unit, UseUnit.METR.id);
                    } else if (typeArtikl.id1 == 5) {
                        record.setNo(eArtikl.unit, UseUnit.METR2.id);
                    } else {
                        record.setNo(eArtikl.unit, UseUnit.PIE.id);
                    }
                });
                rsvArtikl.clear();
                rsvArtikl.load();
            } else {
                JOptionPane.showMessageDialog(this, "�������� ��� ��������", "��������!", 1);
            }

        } else if (tab2.getBorder() != null) {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {

                UGui.insertRecordCur(tab2, eArtdet.up, (record) -> {
                    Record artiklRec = qArtikl.get(index);
                    record.setNo(eArtdet.artikl_id, artiklRec.get(eArtikl.id));
                    record.setNo(eArtdet.mark_c1, 1);
                    if (artiklRec.getInt(eArtikl.level1) == 1 && List.of(1, 2, 3, 4, 5).contains(artiklRec.getInt(eArtikl.level2))) {
                        record.setNo(eArtdet.mark_c2, 1);
                        record.setNo(eArtdet.mark_c3, 1);
                    }
                    record.setNo(eArtdet.cost_c1, 100);
                    record.setNo(eArtdet.cost_c2, 0);
                    record.setNo(eArtdet.cost_c3, 0);
                    record.setNo(eArtdet.cost_c4, 0);
                    record.setNo(eArtdet.coef, 1);
                });
            }
        }
    }//GEN-LAST:event_btnInsert

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete
        if (tab1.getBorder() != null) {

            if (UGui.isDeleteRecord(tab1, this, tab2) == 0) {
                if (JOptionPane.showConfirmDialog(owner, "��������!\n  ���� ������� ������������ � ����������, "
                        + "\n �����������, ��������, �����������, ���������, \n �� ������ ����� �������� "
                        + "����� �������. \n �� �������, ��� ������ ������� ������� ?", "��������������",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                    UGui.deleteRecord(tab1);
                }
            }
        } else if (tab2.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2, this) == 0) {
                UGui.deleteRecord(tab2);
            }
        }
    }//GEN-LAST:event_btnDelete

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        RTable.load("������ ���������", tab1);
        ExecuteCmd.documentType(this);
    }//GEN-LAST:event_btnReport

    private void btnClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose
        this.dispose();
    }//GEN-LAST:event_btnClose

    private void btn7(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn7
        JButton btn = (JButton) evt.getSource();
        if ((btn == btn16 || btn == btn17 || btn == btn21)
                && (txt7.getText().isEmpty() && txt25.getText().isEmpty() && txt32.getText().isEmpty())) {
            DicCurrenc frame = new DicCurrenc(this, listenerCurrenc1, listenerCurrenc2);
        } else {
            ListenerRecord listener = (btn == btn7 || btn == btn9 || btn == btn26) ? listenerCurrenc1 : listenerCurrenc2;
            Field field = (listener == listenerCurrenc1) ? eArtikl.currenc1_id : eArtikl.currenc2_id;
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                int id = qArtikl.getAs(index, field);
                DicCurrenc frame = new DicCurrenc(this, listener, id);
            } else {
                DicCurrenc frame = new DicCurrenc(this, listener);
            }
        }
    }//GEN-LAST:event_btn7

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab1, tab2));
    }//GEN-LAST:event_mousePressed

    private void btn11(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn11
        new DicArtikl(this, listenerAnalog, true, 1);
    }//GEN-LAST:event_btn11

    private void checkBox1Action(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox1Action
        int index = UGui.getIndexRec(tab1);
        Record artiklRec = qArtikl.get(index);
        int with_seal = (checkBox1.isSelected()) ? 1 : 0;
        artiklRec.set(eArtikl.with_seal, with_seal);
    }//GEN-LAST:event_checkBox1Action

    private void btn5(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn5
        int index = UGui.getIndexRec(tab1);
        Record artiklRec = qArtikl.get(index);
        if (artiklRec.getInt(eArtikl.level1) == 1) {
            new DicEnums(this, listenerUnit, UseUnit.METR);

        } else if (artiklRec.getInt(eArtikl.level1) == 2) {
            if (artiklRec.getInt(eArtikl.level2) == 4) {
                new DicEnums(this, listenerUnit, UseUnit.PIE, UseUnit.ML, UseUnit.GRAM, UseUnit.KG, UseUnit.LITER, UseUnit.DOSE);
            } else {
                new DicEnums(this, listenerUnit, UseUnit.PIE, UseUnit.KIT, UseUnit.PAIR);
            }
        } else if (artiklRec.getInt(eArtikl.level1) == 3) {
            new DicEnums(this, listenerUnit, UseUnit.METR);

        } else if (artiklRec.getInt(eArtikl.level1) == 4) {
            if (artiklRec.getInt(eArtikl.level2) == 1) {
                new DicEnums(this, listenerUnit, UseUnit.PIE, UseUnit.KIT, UseUnit.PAIR, UseUnit.MONTH);
            } else {
                new DicEnums(this, listenerUnit, UseUnit.PIE, UseUnit.KIT, UseUnit.MONTH);
            }
        } else if (artiklRec.getInt(eArtikl.level1) == 5) {
            new DicEnums(this, listenerUnit, UseUnit.METR2);
        }
    }//GEN-LAST:event_btn5

    private void btn18(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn18
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record artiklRec = qArtikl.get(index);
            new Syssize(this, listenerSyssize, artiklRec.getInt(eArtikl.syssize_id));
        } else {
            new Syssize(this, listenerSyssize, -1);
        }
    }//GEN-LAST:event_btn18

    private void btn19(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn19
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            int id = qArtikl.getAs(index, eArtikl.groups1_id);
            new DicGroups(this, listenerMarkup, TypeGrup.PRICE_INC, id, true);
        }
    }//GEN-LAST:event_btn19

    private void btn20(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn20
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            int id = qArtikl.getAs(index, eArtikl.groups2_id);
            new DicGroups(this, listenerDiscount, TypeGrup.PRICE_DEC, id, true);
        }
    }//GEN-LAST:event_btn20

    private void btn22(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn22
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            int id = qArtikl.getAs(index, eArtikl.groups3_id);
            new DicGroups(this, listenerCateg, TypeGrup.CATEG_ELEM, id, true);
        }
    }//GEN-LAST:event_btn22

    private void btnMove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMove
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record artiklRec = qArtikl.get(index);
            List list = new LinkedList();
            for (TypeArt typeArt : TypeArt.values()) {
                String str = (typeArt.id2 == 0) ? typeArt.name + ":" : "      " + typeArt.name;
                list.add(str);
            }
            Object result = JOptionPane.showInputDialog(Artikles.this, artiklRec.getStr(eArtikl.name),
                    "��������� ���� ��������", JOptionPane.QUESTION_MESSAGE, null, list.toArray(), list.toArray()[0]);

            if (result != null) {
                for (TypeArt enam : TypeArt.values()) {
                    if (enam instanceof TypeArt && enam.name.equals(result.toString().trim())) {
                        artiklRec.setNo(eArtikl.level1, enam.id1);
                        artiklRec.setNo(eArtikl.level2, enam.id2);
                        ((DefTableModel) tab1.getModel()).getQuery().update(artiklRec);
                        selectionTree();
                    }
                }
            }
        }
    }//GEN-LAST:event_btnMove

    private void btn37(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn37
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            int id = qArtikl.getAs(index, eArtikl.groups4_id);
            new DicGroups(this, listenerSeries, TypeGrup.SERI_ELEM, id, true);
        }
    }//GEN-LAST:event_btn37

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest
//        tab1.getColumnModel().getColumn(0).setMinWidth(400);
//        tab1.getColumnModel().getColumn(0).setPreferredWidth(400);
//        tab1.getColumnModel().getColumn(0).setMaxWidth(400);
    }//GEN-LAST:event_btnTest

    private void ppmClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmClick
        JMenuItem ppm = (JMenuItem) evt.getSource();
        int index = 0;
        if (ppm == groups1_id) {
            index = 2;
        } else if (ppm == groups2_id) {
            index = 3;
        } else if (ppm == groups3_id) {
            index = 4;
        } else if (ppm == groups4_id) {
            index = 5;
        } else if (ppm == texcod) {
            index = 9;
        } else if (ppm == analog) {
            index = 10;
        } else if (ppm == height) {
            index = 6;
        } else if (ppm == depth) {
            index = 7;
        }
        TableColumn column = tab1.getColumnModel().getColumn(index);
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

    private void btnClone(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClone
        UIManager.put("OptionPane.noButtonText", "���");
        int result = JOptionPane.showConfirmDialog(this, "����������� ������ �������� ������?",
                "�������������", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result != JOptionPane.CANCEL_OPTION) {

            if (tab1.getBorder() != null) {
                List<Record> data = new ArrayList(qArtdet);
                Record masterClon = UGui.cloneMaster(qArtikl, tab1, eArtikl.up, (clon) -> {
                    clon.setNo(eArtikl.code, clon.getStr(eArtikl.code) + "-����");
                    clon.setNo(eArtikl.name, clon.getStr(eArtikl.name) + "-����");
                });
                if (result == JOptionPane.NO_OPTION) {
                    UGui.cloneSlave(qArtdet, tab2, eArtdet.up, data, (clon) -> {
                        clon.setNo(eArtdet.artikl_id, masterClon.getInt(eArtikl.id));
                    });
                }
            } else if (tab2.getBorder() != null) {
                Record masterClon = UGui.cloneMaster(qArtdet, tab2, eArtdet.up, null);
            }
        }
    }//GEN-LAST:event_btnClone

    private void tab1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab1MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab1, tab2).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab1, tab2).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmTab1.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tab1MouseClicked

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert || evt.getSource() == mInsert2) {
            btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
        } else if (evt.getSource() == mDelit || evt.getSource() == mDelit2) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void btnChoice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoice
        int index = UGui.getIndexRec(tab1);
        if (index != -1) {
            Record record = qArtikl.get(index);
            listener.action(record);
        }
        this.dispose();
    }//GEN-LAST:event_btnChoice

    private void tab2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tab2MouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab1, tab2).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab1, tab2).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmTab2.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tab2MouseClicked

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem analog;
    private javax.swing.JButton btn10;
    private javax.swing.JButton btn11;
    private javax.swing.JButton btn16;
    private javax.swing.JButton btn17;
    private javax.swing.JButton btn18;
    private javax.swing.JButton btn19;
    private javax.swing.JButton btn20;
    private javax.swing.JButton btn21;
    private javax.swing.JButton btn22;
    private javax.swing.JButton btn23;
    private javax.swing.JButton btn24;
    private javax.swing.JButton btn25;
    private javax.swing.JButton btn26;
    private javax.swing.JButton btn27;
    private javax.swing.JButton btn28;
    private javax.swing.JButton btn30;
    private javax.swing.JButton btn32;
    private javax.swing.JButton btn33;
    private javax.swing.JButton btn37;
    private javax.swing.JButton btn5;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn7;
    private javax.swing.JButton btn9;
    private javax.swing.JButton btnChoice;
    private javax.swing.JButton btnClone;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnMove;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnTest;
    private javax.swing.JPanel center;
    private javax.swing.JCheckBox checkBox1;
    private javax.swing.JMenuItem depth;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler18;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler21;
    private javax.swing.Box.Filler filler22;
    private javax.swing.Box.Filler filler23;
    private javax.swing.Box.Filler filler24;
    private javax.swing.Box.Filler filler25;
    private javax.swing.Box.Filler filler26;
    private javax.swing.Box.Filler filler27;
    private javax.swing.Box.Filler filler28;
    private javax.swing.Box.Filler filler29;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler30;
    private javax.swing.Box.Filler filler31;
    private javax.swing.Box.Filler filler32;
    private javax.swing.Box.Filler filler33;
    private javax.swing.Box.Filler filler34;
    private javax.swing.Box.Filler filler35;
    private javax.swing.Box.Filler filler36;
    private javax.swing.Box.Filler filler37;
    private javax.swing.Box.Filler filler38;
    private javax.swing.Box.Filler filler39;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler40;
    private javax.swing.Box.Filler filler41;
    private javax.swing.Box.Filler filler42;
    private javax.swing.Box.Filler filler43;
    private javax.swing.Box.Filler filler44;
    private javax.swing.Box.Filler filler45;
    private javax.swing.Box.Filler filler46;
    private javax.swing.Box.Filler filler48;
    private javax.swing.Box.Filler filler49;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JMenuItem groups1_id;
    private javax.swing.JMenuItem groups2_id;
    private javax.swing.JMenuItem groups3_id;
    private javax.swing.JMenuItem groups4_id;
    private javax.swing.JMenuItem height;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab11;
    private javax.swing.JLabel lab12;
    private javax.swing.JLabel lab13;
    private javax.swing.JLabel lab14;
    private javax.swing.JLabel lab15;
    private javax.swing.JLabel lab16;
    private javax.swing.JLabel lab18;
    private javax.swing.JLabel lab19;
    private javax.swing.JLabel lab2;
    private javax.swing.JLabel lab20;
    private javax.swing.JLabel lab21;
    private javax.swing.JLabel lab22;
    private javax.swing.JLabel lab23;
    private javax.swing.JLabel lab24;
    private javax.swing.JLabel lab26;
    private javax.swing.JLabel lab27;
    private javax.swing.JLabel lab28;
    private javax.swing.JLabel lab29;
    private javax.swing.JLabel lab30;
    private javax.swing.JLabel lab31;
    private javax.swing.JLabel lab32;
    private javax.swing.JLabel lab33;
    private javax.swing.JLabel lab34;
    private javax.swing.JLabel lab35;
    private javax.swing.JLabel lab36;
    private javax.swing.JLabel lab37;
    private javax.swing.JLabel lab38;
    private javax.swing.JLabel lab40;
    private javax.swing.JLabel lab41;
    private javax.swing.JLabel lab42;
    private javax.swing.JLabel lab43;
    private javax.swing.JLabel lab44;
    private javax.swing.JLabel lab45;
    private javax.swing.JLabel lab47;
    private javax.swing.JLabel lab48;
    private javax.swing.JLabel lab49;
    private javax.swing.JLabel lab50;
    private javax.swing.JLabel lab54;
    private javax.swing.JLabel lab55;
    private javax.swing.JLabel lab56;
    private javax.swing.JLabel labl17;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mDelit2;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JMenuItem mInsert2;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan10;
    private javax.swing.JPanel pan100;
    private javax.swing.JPanel pan101;
    private javax.swing.JPanel pan102;
    private javax.swing.JPanel pan103;
    private javax.swing.JPanel pan106;
    private javax.swing.JPanel pan107;
    private javax.swing.JPanel pan108;
    private javax.swing.JPanel pan109;
    private javax.swing.JPanel pan11;
    private javax.swing.JPanel pan110;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan13;
    private javax.swing.JPanel pan17;
    private javax.swing.JPanel pan18;
    private javax.swing.JPanel pan2;
    private javax.swing.JPanel pan20;
    private javax.swing.JPanel pan21;
    private javax.swing.JPanel pan22;
    private javax.swing.JPanel pan24;
    private javax.swing.JPanel pan25;
    private javax.swing.JPanel pan26;
    private javax.swing.JPanel pan28;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan4;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan7;
    private javax.swing.JPanel pan8;
    private javax.swing.JPanel pan9;
    private javax.swing.JPanel pan91;
    private javax.swing.JPanel pan92;
    private javax.swing.JPanel pan93;
    private javax.swing.JPanel pan94;
    private javax.swing.JPanel pan95;
    private javax.swing.JPanel pan96;
    private javax.swing.JPanel pan98;
    private javax.swing.JPanel pan99;
    private javax.swing.JPopupMenu ppmTab1;
    private javax.swing.JPopupMenu ppmTab2;
    private javax.swing.JPanel pqn109;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scrTree;
    private javax.swing.JPopupMenu.Separator separator1;
    private javax.swing.JPopupMenu.Separator separator2;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JMenuItem texcod;
    public javax.swing.JTree tree;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txt10;
    private javax.swing.JTextField txt11;
    private javax.swing.JTextField txt12;
    private javax.swing.JTextField txt14;
    private javax.swing.JTextField txt15;
    private javax.swing.JTextField txt16;
    private javax.swing.JTextField txt17;
    private javax.swing.JTextField txt18;
    private javax.swing.JTextField txt19;
    private javax.swing.JTextField txt2;
    private javax.swing.JTextField txt20;
    private javax.swing.JTextField txt21;
    private javax.swing.JTextField txt22;
    private javax.swing.JTextField txt23;
    private javax.swing.JTextField txt24;
    private javax.swing.JTextField txt25;
    private javax.swing.JTextField txt26;
    private javax.swing.JTextField txt27;
    private javax.swing.JTextField txt29;
    private javax.swing.JTextField txt3;
    private javax.swing.JTextField txt30;
    private javax.swing.JTextField txt31;
    private javax.swing.JTextField txt32;
    private javax.swing.JTextField txt33;
    private javax.swing.JTextField txt34;
    private javax.swing.JTextField txt35;
    private javax.swing.JTextField txt37;
    private javax.swing.JTextField txt38;
    private javax.swing.JTextField txt39;
    private javax.swing.JTextField txt4;
    private javax.swing.JTextField txt40;
    private javax.swing.JTextField txt45;
    private javax.swing.JTextField txt46;
    private javax.swing.JTextField txt48;
    private javax.swing.JTextField txt49;
    private javax.swing.JTextField txt5;
    private javax.swing.JTextField txt50;
    private javax.swing.JTextField txt51;
    private javax.swing.JTextField txt52;
    private javax.swing.JTextField txt53;
    private javax.swing.JTextField txt54;
    private javax.swing.JTextField txt6;
    private javax.swing.JTextField txt7;
    private javax.swing.JTextField txt8;
    private javax.swing.JTextField txt9;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    public void initElements() {

        btnTest.setVisible(eProp.devel.equals("99"));
        App.loadLocationWin(this, btnClose, (e) -> {
            App.saveLocationWin(this, btnClose, tab1, tab2);
        }, tab1, tab2);

        filterTable = new TableFieldFilter(1, tab1, tab2);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        btnChoice.setVisible(false);
        List.of(btnIns, btnDel).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab1, tab2)));
        DefaultTreeCellRenderer rnd = (DefaultTreeCellRenderer) tree.getCellRenderer();
        rnd.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b038.gif")));
        rnd.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b007.gif")));
        rnd.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b006.gif")));

        tree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree());
        tab1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab1(event);
                }
            }
        });
    }
}
