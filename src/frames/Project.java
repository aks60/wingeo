package frames;

import frames.swing.comp.ProgressBar;
import builder.model.Com5t;
import builder.Wincalc;
import builder.making.TFurniture;
import builder.making.TTariffic;
import builder.making.UColor;
import builder.model.AreaStvorka;
import builder.model.UPar;
import builder.script.GsonElem;
import builder.script.GsonRoot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import common.UCom;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eProject;
import domain.ePrjpart;
import frames.dialog.DicDate;
import javax.swing.JTable;
import frames.swing.comp.DefTableModel;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import javax.swing.table.DefaultTableModel;
import common.eProfile;
import common.eProp;
import common.listener.ListenerAction;
import common.listener.ListenerFrame;
import common.listener.ListenerGet;
import dataset.Connect;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eGroups;
import domain.eParams;
import domain.ePrjkit;
import domain.ePrjprod;
import domain.eSysfurn;
import domain.eSyspar1;
import domain.eSysprod;
import domain.eSystree;
import enums.Layout;
import enums.PKjson;
import enums.TypeGrup;
import frames.dialog.DicArtikl;
import frames.dialog.DicArtikl2;
import frames.dialog.DicColor;
import frames.dialog.DicKits;
import frames.dialog.DicPrjprod;
import frames.dialog.DicSyspod;
import frames.dialog.ParDefVal;
import frames.swing.comp.Canvas;
import frames.swing.comp.DefMutableTreeNode;
import frames.swing.comp.Scene;
import frames.swing.comp.TableFieldFilter;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import startup.App;
import common.listener.ListenerReload;
import static dataset.Query.INS;
import domain.eSysprof;
import frames.swing.comp.CardPanel;
import frames.swing.comp.MainMenu;
import java.util.ArrayList;
import static java.util.stream.Collectors.toList;
import org.locationtech.jts.geom.Envelope;

public class Project extends javax.swing.JFrame implements ListenerReload, ListenerAction {

    private ListenerGet<Wincalc> listenerWincalc = null;
    private ListenerAction listenerCangeAndRedraw = null;
    public boolean norm_otx = !(eGroups.find(2007).getInt(eGroups.val) == 0); //учитывать норму отхода в себестоимости
    private ImageIcon icon = new ImageIcon(getClass().getResource("/resource/img16/b031.gif"));
    private Query qGroups = new Query(eGroups.values());
    private Query qParams = new Query(eParams.values());
    private Query qCurrenc = new Query(eCurrenc.values());
    private Query qProjectAll = new Query(eProject.values());
    private Query qProject = new Query(eProject.values());
    private Query qPrjpart = new Query(ePrjpart.values());
    private Query qPrjprod = new Query(ePrjprod.values());
    private Query qSysprof = new Query(eSysprof.values(), eArtikl.values());
    private Query qPrjkit = new Query(ePrjkit.values(), eArtikl.values());
    private Query qSyspar1 = new Query(eSyspar1.values());
    private DefMutableTreeNode winNode = null;
    private Canvas canvas = new Canvas();
    private Scene scene = null;
    private CardPanel cardPanel = null;
    Object[] column = new String[]{"", "Скидка (%)", "Без скидок", "Со скидкой"};
    private Gson gson = new GsonBuilder().create();
    DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            value = (value instanceof Double) ? UCom.format(value, -9) : value;
            JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            lab.setBackground(new java.awt.Color(212, 208, 200));
            return lab;
        }
    };

    public Project(boolean menureport) {
        initComponents();
        scene = new Scene(canvas, this, this);
        //btnReport.setVisible(menureport);
        initElements();
        cardPanel = new CardPanel(listenerWincalc, listenerCangeAndRedraw, winTree, ppmTree, qGroups, qSysprof, pan7);
        loadingData();
        loadingModel();
        listenerAdd();
        loadingTab1();
    }

    private void loadingData() {
        qGroups.sq2(eGroups.data(), eGroups.grup, TypeGrup.PARAM_USER.id, eGroups.grup, TypeGrup.COLOR_MAP.id);
        qParams.sql(eParams.data(), eParams.up);
        qCurrenc.sql(eCurrenc.data(), eCurrenc.up).sort(eCurrenc.name);
        qProjectAll.sql(eProject.data(), eProject.up).sort(eProject.date4);
        qPrjpart.sql(ePrjpart.data(), ePrjpart.up);
    }

    public void loadingModel() {

        new DefTableModel(tab1, qProject, eProject.num_ord, eProject.num_acc, eProject.date4, eProject.date5,
                eProject.date6, eProject.prjpart_id, eProject.vendor_id, eProject.manager) {
            @Override
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (field == eProject.prjpart_id) {
                    Record record = qPrjpart.stream().filter(rec -> rec.get(ePrjpart.id).equals(val)).findFirst().orElse(ePrjpart.up.newRecord(Query.SEL));
                    return record.get(ePrjpart.partner);
                } else if (field == eProject.vendor_id) {
                    Record record = qPrjpart.stream().filter(rec -> rec.get(ePrjpart.id).equals(val)).findFirst().orElse(ePrjpart.up.newRecord(Query.SEL));
                    return record.get(ePrjpart.partner);
                }
                return val;
            }
        };
        new DefTableModel(tab2, qPrjprod, ePrjprod.name, ePrjprod.num, ePrjprod.id);
        new DefTableModel(tab3, qSyspar1, eSyspar1.groups_id, eSyspar1.text) {
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (val != null && field == eSyspar1.groups_id) {
                    return qGroups.find(eGroups.data(), eGroups.id, Integer.valueOf(String.valueOf(val))).getDev(eGroups.name, val);
                }
                return val;
            }
        };
        new DefTableModel(tab4, qPrjkit, ePrjkit.prjprod_id, eArtikl.code, eArtikl.name, ePrjkit.color1_id, ePrjkit.color2_id,
                ePrjkit.color3_id, ePrjkit.width, ePrjkit.height, ePrjkit.numb, ePrjkit.angl1, ePrjkit.angl2) {

            public Object getValueAt(int col, int row, Object val) {

                if (columns[col] == ePrjkit.prjprod_id) {
                    if (val != null) {
                        return qPrjprod.stream().filter(rec -> val.equals(rec.get(ePrjprod.id)))
                                .findFirst().orElse(ePrjprod.up.newRecord(Query.SEL)).getStr(ePrjprod.name);
                    } else {
                        return "Заказ № " + qProject.get(UGui.getIndexRec(tab1)).get(eProject.num_ord);
                    }
                } else if (val != null && columns[col] == ePrjkit.color1_id) {
                    return eColor.find((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == ePrjkit.color2_id) {
                    return eColor.find((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == ePrjkit.color3_id) {
                    return eColor.find((int) val).getStr(eColor.name);

                }
                return val;
            }
        };

        DefaultTableCellRenderer defaultTableDateRenderer = new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                Field field = ((DefTableModel) table.getModel()).columns[column];
                if (field.meta().type() == Field.TYPE.DATE) {
                    value = UGui.convert2Date(value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        tab1.getColumnModel().getColumn(2).setCellRenderer(defaultTableDateRenderer);
        tab1.getColumnModel().getColumn(3).setCellRenderer(defaultTableDateRenderer);
        tab1.getColumnModel().getColumn(4).setCellRenderer(defaultTableDateRenderer);
        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 2) {
                    Record rec = qPrjprod.get(row);
                    if (rec.size() > ePrjprod.values().length) {
                        Object v = rec.get(ePrjprod.values().length);
                        if (v instanceof Wincalc) {
                            label.setIcon(((Wincalc) v).imageIcon);
                        }
                    }
                } else {
                    label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
                    label.setIcon(null);
                }
                return label;
            }
        };
        tab2.setDefaultRenderer(Object.class, defaultTableCellRenderer);
    }

    public void loadingTab1() {
        qProject.clear();
        int first = (btnF1.isSelected()) ? qProjectAll.size() - 10 : (btnF2.isSelected()) ? qProjectAll.size() - 30 : 0;
        first = (first < 0) ? 0 : first;
        for (int i = first; i < qProjectAll.size(); ++i) {
            qProject.add(qProjectAll.get(i));
        }
        //Выделяем заказ если сохранён в Property
        int orderID = Integer.valueOf(eProp.orderID.getProp());
        ((DefTableModel) tab1.getModel()).fireTableDataChanged();
        int index = -1;
        for (int index2 = 0; index2 < qProject.size(); ++index2) {
            if (qProject.get(index2).getInt(ePrjprod.id) == orderID) {
                index = index2;
            }
        }
        if (index != -1) {
            UGui.setSelectedIndex(tab1, index);
            UGui.scrollRectToIndex(index, tab1);
        } else {
            UGui.setSelectedRow(tab1);
        }
    }

    public void loadingTab2() {
        int index = -1;
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(qProject, qPrjprod, qPrjkit).forEach(q -> q.execsql());
        if (tab1.getSelectedRow() != -1) {

            Record projectRec = qProject.get(UGui.getIndexRec(tab1));
            int id = projectRec.getInt(eProject.id);
            qPrjprod.sql(ePrjprod.data(), ePrjprod.project_id, id);

            for (Record record : qPrjprod) {
                try {
                    String script = record.getStr(ePrjprod.script);
                    Wincalc iwin2 = new Wincalc(script);
                    iwin2.imageIcon = Canvas.createIcon(iwin2, 68);
                    record.add(iwin2);

                } catch (Exception e) {
                    System.err.println("Ошибка:Project.loadingTab2() " + e);
                }
            }
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();

            //Выделяем конструкцию если сохранена в Property
            int prjprodID = Integer.valueOf(eProp.prjprodID.getProp());
            for (int i = 0; i < qPrjprod.size(); ++i) {
                if (qPrjprod.get(i).getInt(ePrjprod.id) == prjprodID) {
                    index = i;
                }
            }
            if (index != -1) {
                UGui.setSelectedIndex(tab2, index);
            } else {
                UGui.setSelectedRow(tab2);
            }
        }
    }

    public void loadingTab4() {
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        UGui.updateBorderAndSql(tab4, List.of(tab1, tab2, tab3, tab4));
        Record projectRec = qProject.get(UGui.getIndexRec(tab1));

        int id = projectRec.getInt(eProject.id);
        qPrjkit.sql(ePrjkit.data(), ePrjkit.project_id, id);
        qPrjkit.table(eArtikl.up).join(qPrjkit, eArtikl.data(), ePrjkit.artikl_id, eArtikl.id);
        ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
        UGui.setSelectedRow(tab4);
    }

    public void loadingTab5() {

        Record projectRec = qProject.get(UGui.getIndexRec(tab1));
        double disc_all = projectRec.getDbl(eProject.disc_all, 0);
        double cost1_win = projectRec.getDbl(eProject.cost1_win, 0);
        double cost1_kit = projectRec.getDbl(eProject.cost1_kit, 0);
        double cost2_win = projectRec.getDbl(eProject.cost2_win, 0);
        double cost2_kit = projectRec.getDbl(eProject.cost2_kit, 0);

        Object data[][] = {
            {" Конструкции", projectRec.getDbl(eProject.disc_win, 0),
                cost1_win, cost2_win},
            {" Комплектации", projectRec.getDbl(eProject.disc_kit, 0),
                cost1_kit, cost2_kit},
            {" Итого за заказ", projectRec.getDbl(eProject.disc_all, 0),
                cost1_win + cost1_kit, (cost2_win + cost2_kit) - (cost2_win + cost2_kit) * disc_all / 100}};

        ((DefaultTableModel) tab5.getModel()).setDataVector(data, column);
        tab5.getColumnModel().getColumn(0).setCellRenderer(defaultTableCellRenderer);
        tab5.getColumnModel().getColumn(2).setCellRenderer(defaultTableCellRenderer);
        tab5.getColumnModel().getColumn(3).setCellRenderer(defaultTableCellRenderer);
    }

    public void loadingTree(Wincalc winc) {
        try {
            DefMutableTreeNode root = UTree.loadWinTree(winc);
            winTree.setModel(new DefaultTreeModel(root));
            winTree.setRootVisible(false);

        } catch (Exception e) {
            System.err.println("Ошибка:Project.loadingWinTree() " + e);
        }
    }

    public void selectionTab1() {

        UGui.clearTable(tab2, tab4);
        List.of(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        if (tab1.getSelectedRow() != -1) {

            Record projectRec = qProject.get(UGui.getIndexRec(tab1));
            //lab2.setText("Заказ № " + projectRec.getStr(eProject.num_ord));
            int orderID = qProject.getAs(UGui.getIndexRec(tab1), eProject.id);
            eProp.orderID.putProp(String.valueOf(orderID));

            Record currencRec = qCurrenc.stream().filter(rec -> rec.get(eCurrenc.id).equals(projectRec.get(eProject.currenc_id))).findFirst().orElse(eCurrenc.up.newRecord(Query.SEL));
            txt7.setText(UCom.format(projectRec.getDbl(eProject.weight), 1));
            txt8.setText(UCom.format(projectRec.getDbl(eProject.square) / 1000000, 2));

            loadingTab2();
            loadingTab4();
            loadingTab5();
        }
    }

    //При выборе другой конструкции
    public void selectionTab2() {
        Arrays.asList(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            Record prjprodRec = qPrjprod.get(index);
            qSysprof.sql(eSysprof.data(), eSysprof.systree_id, prjprodRec.getInt(ePrjprod.systree_id)).sort(eSysprof.npp);
            qSysprof.table(eArtikl.up).join(qSysprof, eArtikl.data(), eSysprof.artikl_id, eArtikl.id);
            eProp.prjprodID.putProp(prjprodRec.getStr(ePrjprod.id)); //запишем текущий prjprodID в файл
            //App.Top.frame.setTitle(UGui.designTitle());
            Object w = prjprodRec.get(ePrjprod.values().length);
            if (w instanceof Wincalc) { //прорисовка окна               
                Wincalc winc = (Wincalc) w;

                winc.sizeEvent = cardPanel.sizeEvent;

                GsonElem.setMaxID(winc); //установим генератор идентификаторов  

                scene.init(winc);
                canvas.draw();

                loadingTree(winc);

                winTree.setSelectionRow(1);
            }
        } else {
            canvas.draw();
            winTree.setModel(new DefaultTreeModel(new DefMutableTreeNode("")));
        }
    }

    //При выборе элемента конструкции
    public void selectionTree() {
        try {
            //Выделенный элемент
            Object selNode = winTree.getLastSelectedPathComponent();
            if (selNode instanceof DefMutableTreeNode) {
                winNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
                Com5t com5t = winNode.com5t();
                Wincalc winc = wincalc();
                //Параметры
                if (com5t.type == enums.Type.PARAM) {
                    ((CardLayout) pan7.getLayout()).show(pan7, "card14");
                    qSyspar1.clear();
                    winc.mapPardef.forEach((pk, syspar1Rec) -> qSyspar1.add(syspar1Rec));
                    Collections.sort(qSyspar1, (o1, o2) -> qGroups.find(eGroups.data(), eGroups.id, o1.getInt(eSyspar1.groups_id)).getStr(eGroups.name)
                            .compareTo(qGroups.find(eGroups.data(), eGroups.id, o2.getInt(eSyspar1.groups_id)).getStr(eGroups.name)));
                    ((DefTableModel) tab3.getModel()).fireTableDataChanged();

                } else {
                    cardPanel.selectionTree();
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Project.selectionTree() " + e);
        }
    }

    public void listenerAdd() {

        UGui.buttonCellEditor(tab1, 2).addActionListener(event -> {
            new DicDate(this, (obj) -> {
                GregorianCalendar calendar = (GregorianCalendar) obj;
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.date4, calendar.getTime());
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
                return true;
            }, 0);
        });

        UGui.buttonCellEditor(tab1, 3).addActionListener(event -> {
            new DicDate(this, (obj) -> {
                GregorianCalendar calendar = (GregorianCalendar) obj;
                UGui.stopCellEditing(tab1);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.date5, calendar.getTime());
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
                return true;
            }, 0);
        });

        UGui.buttonCellEditor(tab1, 4).addActionListener(event -> {
            new DicDate(this, (obj) -> {
                GregorianCalendar calendar = (GregorianCalendar) obj;
                UGui.stopCellEditing(tab1);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.date6, calendar.getTime());
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
                return true;
            }, 0);
        });

        UGui.buttonCellEditor(tab1, 5).addActionListener(event -> {
            new Partner(Project.this, (record) -> {
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.prjpart_id, record.getInt(ePrjpart.id));
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
            });
        });

        UGui.buttonCellEditor(tab1, 6).addActionListener(event -> {
            new Partner(Project.this, (record) -> {
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.vendor_id, record.getInt(ePrjpart.id));
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
            });
        });

        UGui.buttonCellEditor(tab3, 1).addActionListener(event -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            Wincalc winc = wincalc();
            int id = qSyspar1.getAs(UGui.getIndexRec(tab3), eSyspar1.id);
            int fixed = eSyspar1.find(id).getInt(eSyspar1.fixed);
            if (fixed != 1) {
                Integer grup = qSyspar1.getAs(UGui.getIndexRec(tab3), eSyspar1.groups_id);
                new ParDefVal(this, record -> {

                    int index = UGui.getIndexRec(tab2);
                    int index2 = UGui.getIndexRec(tab3);
                    if (index != -1) {

                        //Экземпляр нового скрипта
                        Record sypar1Rec = qSyspar1.get(index2);
                        Record prjprodRec = qPrjprod.get(index);
                        String script2 = UGui.ioknaParamUpdate(winc, sypar1Rec.getInt(eSyspar1.groups_id), record.getInt(0));
                        prjprodRec.set(ePrjprod.script, script2);
                        winc.build(script2);

                        //Пересчёт фурнитуры с учётом настроек                    
                        new TFurniture(winc, true).furn();

                        //Установим курсор
                        selectionTree();
                        UGui.setSelectedIndex(tab3, index2);

                        //Перерисуем конструкцию
                        canvas.init(winc);
                        canvas.draw();
                    }
                }, grup);
            } else {
                JOptionPane.showMessageDialog(Project.this, "Неизменяемый параметр в системе", "ВНИМАНИЕ!", 1);
            }
        });

        UGui.buttonCellEditor(tab4, 0).addActionListener(event -> {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record projectRec = qProject.get(index);

                new DicPrjprod(Project.this, (record) -> {
                    UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                    Record record2 = qPrjkit.get(UGui.getIndexRec(tab4));
                    if (record.size() == 2) {
                        record2.set(ePrjkit.prjprod_id, null);
                    } else {
                        record2.set(ePrjkit.prjprod_id, record.getInt(ePrjprod.id));
                    }
                    ((DefaultTableModel) tab4.getModel()).fireTableRowsUpdated(tab4.getSelectedRow(), tab4.getSelectedRow());
                }, projectRec.getInt(eProject.id));
            }
        });

        UGui.buttonCellEditor(tab4, 1).addActionListener(event -> {
            Record prjkitRec = qPrjkit.get(UGui.getIndexRec(tab4));
            int id = prjkitRec.getInt(ePrjkit.artikl_id, -1);
            DicArtikl2 frame = new DicArtikl2(this, id, (record) -> {
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                if (record.size() == 2) {
                    JOptionPane.showMessageDialog(this, "Поле артикул должно иметь значеение");
                } else {
                    qPrjkit.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab4), ePrjkit.artikl_id);
                    qPrjkit.table(eArtikl.up).set(record.get(eArtikl.code), UGui.getIndexRec(tab4), eArtikl.code);
                    qPrjkit.table(eArtikl.up).set(record.get(eArtikl.name), UGui.getIndexRec(tab4), eArtikl.name);
                    UGui.fireTableRowUpdated(tab4);
                }
            }, 1, 2, 3, 4, 5);
        });

        UGui.buttonCellEditor(tab4, 2).addActionListener(event -> {
            Record prjkitRec = qPrjkit.get(UGui.getIndexRec(tab4));
            int id = prjkitRec.getInt(ePrjkit.artikl_id, -1);
            DicArtikl2 frame = new DicArtikl2(this, id, (record) -> {
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                if (record.size() == 2) {
                    JOptionPane.showMessageDialog(this, "Поле артикул должно иметь значеение");
                } else {
                    qPrjkit.set(record.getInt(eArtikl.id), UGui.getIndexRec(tab4), ePrjkit.artikl_id);
                    qPrjkit.table(eArtikl.up).set(record.get(eArtikl.code), UGui.getIndexRec(tab4), eArtikl.code);
                    qPrjkit.table(eArtikl.up).set(record.get(eArtikl.name), UGui.getIndexRec(tab4), eArtikl.name);
                    UGui.fireTableRowUpdated(tab4);
                }
            }, 1, 2, 3, 4, 5);
        });

        UGui.buttonCellEditor(tab4, 3).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab4);
            Record record = qPrjkit.get(index);
            HashSet<Record> colorSet = UGui.artiklToColorSet(record.getInt(ePrjkit.artikl_id), 1);

            DicColor frame = new DicColor(this, (record2) -> {
                record.set(ePrjkit.color1_id, record2.get(eColor.id));
                UGui.fireTableRowUpdated(tab4);

            }, colorSet, true, false);
        });

        UGui.buttonCellEditor(tab4, 4).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab4);
            Record record = qPrjkit.get(index);
            HashSet<Record> colorSet = UGui.artiklToColorSet(record.getInt(ePrjkit.artikl_id), 2);

            DicColor frame = new DicColor(this, (record2) -> {
                record.set(ePrjkit.color2_id, record2.get(eColor.id));
                UGui.fireTableRowUpdated(tab4);

            }, colorSet, true, false);
        });

        UGui.buttonCellEditor(tab4, 5).addActionListener(event -> {
            UGui.stopCellEditing(tab1, tab2, tab3, tab4);
            int index = UGui.getIndexRec(tab4);
            Record record = qPrjkit.get(index);
            HashSet<Record> colorSet = UGui.artiklToColorSet(record.getInt(ePrjkit.artikl_id), 3);

            DicColor frame = new DicColor(this, (record2) -> {
                record.set(ePrjkit.color3_id, record2.get(eColor.id));
                UGui.fireTableRowUpdated(tab4);

            }, colorSet, true, false);
        });

    }

    //Изменить скрипт в базе и перерисовать
    public void changeAndRedraw() {
        try {
            //Экземпляр нового скрипта
            Wincalc winc = wincalc();
            String script = wincalc().gson.toJson();
            Record prjprodRec = qPrjprod.get(UGui.getIndexRec(tab2));
            prjprodRec.set(ePrjprod.script, script);
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
            System.err.println("Ошибка:Project.updateScript() " + e);
        }
    }

    //Отменить все изменения
    private void undoChanges() {
        try {
            int index = UGui.getIndexRec(tab2);
            if (index != -1) {

                Record prjprodRow = qPrjprod.get(index);
                Record prjprodRec = new Query(ePrjprod.values()).select(ePrjprod.up, "where", ePrjprod.id, "=", prjprodRow.getInt(ePrjprod.id)).get(0);
                String script = prjprodRec.getStr(ePrjprod.script);

                Wincalc winc = wincalc();
                winc.build(script);
                prjprodRow.set(ePrjprod.up, Query.SEL);

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

            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.undoChanges() " + e);
        }
    }

    private Wincalc wincalc() {
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            Record sysprodRec = qPrjprod.table(ePrjprod.up).get(index);
            Object v = sysprodRec.get(ePrjprod.values().length);
            if (v instanceof Wincalc) { //прорисовка окна               
                return (Wincalc) v;
            }
        }
        return null;
    }

    @Override
    public Query reload(boolean b) {
        changeAndRedraw();
        return qPrjprod;
    }

    @Override
    public void action() {
        undoChanges();
    }

    public void calculate() {
        try {
            int index = UGui.getIndexRec(tab1);
            UGui.stopCellEditingAndExecSql();
            Record projectRec = qProject.get(UGui.getIndexRec(tab1));
            if (UGui.getIndexRec(tab1) != -1) {

                TTariffic.calculate(projectRec, norm_otx);

                //Заполним вес, площадь
                txt8.setText(UCom.format(projectRec.getDbl(eProject.square) / 1000000, 2)); //площадь
                txt7.setText(UCom.format(projectRec.getDbl(eProject.weight), 1)); //вес 

                //Заполним таблицу
                loadingTab5();

                if (index != -1) {
                    ((DefaultTableModel) tab1.getModel()).fireTableDataChanged();
                    UGui.setSelectedIndex(tab1, index);
                } else {
                    UGui.setSelectedRow(tab1);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Project.btnCalc() " + e);
        }
    }

    private void setText(JTextComponent comp, Object txt) {
        if (txt == null) {
            comp.setText("");
        }
        comp.setText(txt.toString());
        comp.setCaretPosition(0);
    }

    private void setIcon(JButton btn, boolean b) {
        if (b == true) {
            btn.setText("");
            btn.setIcon(icon);
        } else {
            btn.setText("...");
            btn.setIcon(null);
        }
    }

    private void dicArtiklToFurniture(String PKjsonColor, int level2) {
        try {
            double stvorkaID = winNode.com5t().id;
            int furnitureID = ((AreaStvorka) winNode.com5t()).sysfurnRec.getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).sql(eArtikl.data(), eArtikl.level1, 2, eArtikl.level2, level2);
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = UCom.gson(wincalc().listAll, stvorkaID);
                stvArea.param.remove(PKjsonColor);
                if (artiklRec.get(1) == null) {
                    stvArea.param.remove(PKjsonColor);
                } else {
                    stvArea.param.addProperty(PKjsonColor, artiklRec.getStr(eArtikl.id));
                }
                changeAndRedraw();

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.artiklToFurniture() " + e);
        }
    }

    private void dicColorToProfile(java.awt.event.ActionEvent evt, JButton btn1, JButton btn2) {
        try {
            List<String> keys = new ArrayList();
            Com5t comElem = winNode.com5t();
            Record systreeRec = eSystree.find(comElem.winc.nuni);
            Field colorFilterMark = (evt.getSource() == btn1) ? eArtdet.mark_c1 : (evt.getSource() == btn2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
            String colorFilterTxt = (evt.getSource() == btn1) ? systreeRec.getStr(eSystree.col1) : (evt.getSource() == btn2)
                    ? systreeRec.getStr(eSystree.col2) : systreeRec.getStr(eSystree.col3);
            Query artdetList = new Query(eArtdet.values()).sql(eArtdet.data(), eArtdet.artikl_id, comElem.artiklRec.getInt(eArtikl.id));

            HashSet<Record> colorFilterSet = DicColor.filterTxt(eColor.data(), colorFilterTxt);
            HashSet<Record> colorSet = DicColor.filterDet(colorFilterSet, artdetList, colorFilterMark);
            if (comElem.type == enums.Type.STV_SIDE) {
                if (comElem.layout() == Layout.BOT) {
                    keys.add(PKjson.stvorkaBot);
                } else if (comElem.layout() == Layout.RIG) {
                    keys.add(PKjson.stvorkaRig);
                } else if (comElem.layout() == Layout.TOP) {
                    keys.add(PKjson.stvorkaTop);
                } else if (comElem.layout() == Layout.LEF) {
                    keys.add(PKjson.stvorkaLef);
                }
            }
            if (evt.getSource() == btn1) {
                keys.add(PKjson.colorID1);
            } else if (evt.getSource() == btn2) {
                keys.add(PKjson.colorID2);
            } else {
                keys.add(PKjson.colorID3);
            }

            new DicColor(this, (colorRec) -> {
                final Com5t com5t = (comElem.type == enums.Type.STV_SIDE) ? comElem.owner : comElem;

                if (colorRec.get(1) == null) {
                    UPar.remove(com5t.gson.param, keys);
                } else {
                    UPar.addProperty(com5t.gson, keys, colorRec.getInt(eColor.id));
                }
                changeAndRedraw(); //обновим конструкцию
            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToProfile() " + e);
        }
    }

    private void dicColorToElement(String PKjsonColor, Record artiklFurn) {
        try {
            double elemID = winNode.com5t().id;
            HashSet<Record> colorSet = UGui.artiklToColorSet(artiklFurn.getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem gsonElem = UCom.gson(wincalc().listAll, elemID);
                if (colorRec.get(1) == null) {
                    UPar.remove(gsonElem.param, List.of(PKjsonColor));
                } else {
                    UPar.addProperty(gsonElem, List.of(PKjsonColor), colorRec.getInt(eColor.id));
                }
                changeAndRedraw();

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToElement() " + e);
        }
    }

    private void dicColorToElement(GsonElem gsonElem, String PKjsonColor, Record artiklElem) {
        try {
            if (gsonElem != null) {
                double elemID = winNode.com5t().id;
                HashSet<Record> colorSet = UGui.artiklToColorSet(artiklElem.getInt(eArtikl.id));
                DicColor frame = new DicColor(this, (colorRec) -> {

                    if (colorRec.get(1) == null) {
                        UPar.remove(gsonElem.param, List.of(PKjsonColor));
                    } else {
                        UPar.addProperty(gsonElem, List.of(PKjsonColor), colorRec.getInt(eColor.id));
                    }
                    changeAndRedraw();

                }, colorSet, true, false);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToElement() " + e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        ppReport = new javax.swing.JPopupMenu();
        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        ppmTree = new javax.swing.JPopupMenu();
        addImpost = new javax.swing.JMenu();
        addImpostHor = new javax.swing.JMenuItem();
        addImpostVer = new javax.swing.JMenuItem();
        removeImpost = new javax.swing.JMenuItem();
        addStvorka = new javax.swing.JMenuItem();
        removeStvorka = new javax.swing.JMenuItem();
        removeMosquit = new javax.swing.JMenuItem();
        elements = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSet = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnCalc = new javax.swing.JButton();
        btnF1 = new javax.swing.JToggleButton();
        btnF2 = new javax.swing.JToggleButton();
        btnF3 = new javax.swing.JToggleButton();
        btnTest = new javax.swing.JButton();
        btnFind = new javax.swing.JButton();
        btnReport = new javax.swing.JToggleButton();
        centr = new javax.swing.JPanel();
        tabb1 = new javax.swing.JTabbedPane();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan11 = new javax.swing.JPanel();
        pan9 = new javax.swing.JPanel();
        pan19 = new javax.swing.JPanel();
        lab7 = new javax.swing.JLabel();
        txt7 = new javax.swing.JTextField();
        txt8 = new javax.swing.JTextField();
        lab8 = new javax.swing.JLabel();
        scr5 = new javax.swing.JScrollPane();
        tab5 = new javax.swing.JTable();
        pan8 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan3 = new javax.swing.JPanel();
        pan5 = new javax.swing.JPanel();
        pan7 = new javax.swing.JPanel();
        pan14 = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        pan12 = new javax.swing.JPanel();
        scr6 = new javax.swing.JScrollPane();
        winTree = new javax.swing.JTree();
        panDesign = new javax.swing.JPanel();
        pan6 = new javax.swing.JPanel();
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

        addImpost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        addImpost.setText("Добавить импост");

        addImpostHor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        addImpostHor.setText("горизонтальный");
        addImpostHor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImpostHorAction(evt);
            }
        });
        addImpost.add(addImpostHor);

        addImpostVer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        addImpostVer.setText("вертикальный");
        addImpostVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImpostVerAction(evt);
            }
        });
        addImpost.add(addImpostVer);

        ppmTree.add(addImpost);

        removeImpost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        removeImpost.setText("Удалить импост");
        removeImpost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeImpostAction(evt);
            }
        });
        ppmTree.add(removeImpost);

        addStvorka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        addStvorka.setText("Добавить сворку");
        addStvorka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStvorkaAction(evt);
            }
        });
        ppmTree.add(addStvorka);

        removeStvorka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        removeStvorka.setText("Удалить створку");
        removeStvorka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeStvorkaAction(evt);
            }
        });
        ppmTree.add(removeStvorka);

        removeMosquit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        removeMosquit.setText("Удалить москитку");
        removeMosquit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMosquitAction(evt);
            }
        });
        ppmTree.add(removeMosquit);

        elements.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        elements.setText("Составы элементов");
        elements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elementsView(evt);
            }
        });
        ppmTree.add(elements);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Заказы");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(900, 580));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Project.this.windowClosed(evt);
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

        btnSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c031.gif"))); // NOI18N
        btnSet.setToolTipText(bundle.getString("Выбрать список")); // NOI18N
        btnSet.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnSet.setEnabled(false);
        btnSet.setMaximumSize(new java.awt.Dimension(25, 25));
        btnSet.setMinimumSize(new java.awt.Dimension(25, 25));
        btnSet.setPreferredSize(new java.awt.Dimension(25, 25));
        btnSet.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsert(evt);
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

        btnCalc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c041.gif"))); // NOI18N
        btnCalc.setToolTipText(bundle.getString("Пересчитать")); // NOI18N
        btnCalc.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnCalc.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnCalc.setFocusable(false);
        btnCalc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCalc.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCalc.setMaximumSize(new java.awt.Dimension(25, 25));
        btnCalc.setMinimumSize(new java.awt.Dimension(25, 25));
        btnCalc.setPreferredSize(new java.awt.Dimension(25, 25));
        btnCalc.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c001.gif"))); // NOI18N
        btnCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalc(evt);
            }
        });

        buttonGroup.add(btnF1);
        btnF1.setFont(frames.UGui.getFont(1,0));
        btnF1.setSelected(true);
        btnF1.setText("10");
        btnF1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnF1.setFocusable(false);
        btnF1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnF1.setMaximumSize(new java.awt.Dimension(28, 25));
        btnF1.setMinimumSize(new java.awt.Dimension(28, 25));
        btnF1.setPreferredSize(new java.awt.Dimension(32, 25));
        btnF1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter(evt);
            }
        });

        buttonGroup.add(btnF2);
        btnF2.setFont(frames.UGui.getFont(1,0));
        btnF2.setText("30");
        btnF2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnF2.setFocusable(false);
        btnF2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnF2.setMaximumSize(new java.awt.Dimension(28, 25));
        btnF2.setMinimumSize(new java.awt.Dimension(28, 25));
        btnF2.setPreferredSize(new java.awt.Dimension(32, 25));
        btnF2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter(evt);
            }
        });

        buttonGroup.add(btnF3);
        btnF3.setFont(frames.UGui.getFont(1,0));
        btnF3.setText(">");
        btnF3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnF3.setFocusable(false);
        btnF3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnF3.setMaximumSize(new java.awt.Dimension(28, 25));
        btnF3.setMinimumSize(new java.awt.Dimension(28, 25));
        btnF3.setPreferredSize(new java.awt.Dimension(32, 25));
        btnF3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnF3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilter(evt);
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

        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c093.gif"))); // NOI18N
        btnReport.setText(bundle.getString("Меню.Отчёты")); // NOI18N
        btnReport.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnReport.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnReport.setMaximumSize(new java.awt.Dimension(80, 250));
        btnReport.setMinimumSize(new java.awt.Dimension(25, 25));
        btnReport.setPreferredSize(new java.awt.Dimension(80, 25));
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepor(evt);
            }
        });

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCalc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFind, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnF2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnF3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 448, Short.MAX_VALUE)
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
                    .addComponent(btnCalc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFind, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(northLayout.createSequentialGroup()
                        .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnDel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnIns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnF2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnF3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(800, 550));
        centr.setLayout(new java.awt.BorderLayout());

        tabb1.setFont(frames.UGui.getFont(1,0));
        tabb1.setPreferredSize(new java.awt.Dimension(800, 500));
        tabb1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Project.this.stateChanged(evt);
            }
        });

        pan1.setPreferredSize(new java.awt.Dimension(800, 500));
        pan1.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);
        scr1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Номер заказа", "Номер счёта", "Дата регистрации", "Дата расчёта", "Дата в произволство", "Контрагент", "Продавец", "User", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, true, true, false
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
                Project.this.mousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(4).setPreferredWidth(80);
            tab1.getColumnModel().getColumn(5).setPreferredWidth(200);
            tab1.getColumnModel().getColumn(6).setPreferredWidth(200);
            tab1.getColumnModel().getColumn(7).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(8).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(8).setMaxWidth(60);
        }

        pan1.add(scr1, java.awt.BorderLayout.CENTER);

        pan11.setPreferredSize(new java.awt.Dimension(400, 500));
        pan11.setLayout(new java.awt.BorderLayout());

        pan9.setPreferredSize(new java.awt.Dimension(450, 180));
        pan9.setLayout(new java.awt.BorderLayout());

        pan19.setPreferredSize(new java.awt.Dimension(450, 45));

        lab7.setFont(frames.UGui.getFont(0,0));
        lab7.setText("Вес");
        lab7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab7.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab7.setMinimumSize(new java.awt.Dimension(4, 14));
        lab7.setPreferredSize(new java.awt.Dimension(36, 18));

        txt7.setFont(frames.UGui.getFont(0,0));
        txt7.setText("0");
        txt7.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt7.setFocusable(false);
        txt7.setPreferredSize(new java.awt.Dimension(40, 20));

        txt8.setFont(frames.UGui.getFont(0,0));
        txt8.setText("0");
        txt8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt8.setFocusable(false);
        txt8.setPreferredSize(new java.awt.Dimension(40, 20));

        lab8.setFont(frames.UGui.getFont(0,0));
        lab8.setText("Площадь");
        lab8.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab8.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab8.setMinimumSize(new java.awt.Dimension(34, 14));
        lab8.setPreferredSize(new java.awt.Dimension(86, 18));

        javax.swing.GroupLayout pan19Layout = new javax.swing.GroupLayout(pan19);
        pan19.setLayout(pan19Layout);
        pan19Layout.setHorizontalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lab7, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122))
        );
        pan19Layout.setVerticalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan19Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pan9.add(pan19, java.awt.BorderLayout.NORTH);

        scr5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scr5.setPreferredSize(new java.awt.Dimension(450, 76));

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"   Конструкции", Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(0.0)},
                {"   Комплектации",  Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(0.0)},
                {"   Итого за заказ",  Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(0.0)}
            },
            new String [] {
                "", "Скидка (%)", "Без скидок", "Со скидкой"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class

                , java.lang.Double.class

                , java.lang.Double.class

                , java.lang.Double.class

            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }

            public void setValueAt(Object aValue, int row, int column) {
                try {
                    super.setValueAt(aValue, row, column);
                    Record projectRec = qProject.get(UGui.getIndexRec(tab1));

                    if (row == 0 && column == 1 && aValue.equals(projectRec.get(eProject.disc_win)) == false) {
                        projectRec.set(eProject.disc_win, aValue);
                    } else if (row == 1 && column == 1 && aValue.equals(projectRec.get(eProject.disc_kit)) == false) {
                        projectRec.set(eProject.disc_kit, aValue);
                    } else if (row == 2 && column == 1 && aValue.equals(projectRec.get(eProject.disc_all)) == false) {
                        projectRec.set(eProject.disc_all, aValue);
                    }
                    loadingTab5();
                } catch (Exception e) {
                    System.out.println("Ошибка: tab5.setValueAt() " + e);
                }
            }
        });
        tab5.getTableHeader().setReorderingAllowed(false);
        scr5.setViewportView(tab5);

        pan9.add(scr5, java.awt.BorderLayout.CENTER);

        pan11.add(pan9, java.awt.BorderLayout.NORTH);

        pan8.setPreferredSize(new java.awt.Dimension(450, 350));
        pan8.setLayout(new java.awt.BorderLayout());

        scr2.setPreferredSize(new java.awt.Dimension(204, 404));

        tab2.setFont(frames.UGui.getFont(0,0));
        tab2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Наименование", "Кол-во", "Рисунок", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tab2.setFillsViewportHeight(true);
        tab2.setName("tab2x"); // NOI18N
        tab2.setRowHeight(68);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Project.this.mousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(180);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(30);
            tab2.getColumnModel().getColumn(1).setMaxWidth(80);
            tab2.getColumnModel().getColumn(2).setMinWidth(68);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(68);
            tab2.getColumnModel().getColumn(2).setMaxWidth(68);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(30);
            tab2.getColumnModel().getColumn(3).setMaxWidth(60);
        }

        pan8.add(scr2, java.awt.BorderLayout.CENTER);

        pan11.add(pan8, java.awt.BorderLayout.CENTER);

        pan1.add(pan11, java.awt.BorderLayout.EAST);

        tabb1.addTab("       Заказы       ", pan1);

        pan3.setPreferredSize(new java.awt.Dimension(800, 500));
        pan3.setLayout(new java.awt.BorderLayout());

        pan5.setPreferredSize(new java.awt.Dimension(400, 450));
        pan5.setLayout(new java.awt.BorderLayout());

        pan7.setPreferredSize(new java.awt.Dimension(10, 300));
        pan7.setLayout(new java.awt.CardLayout());

        pan14.setLayout(new java.awt.BorderLayout());

        scr3.setBorder(null);
        scr3.setPreferredSize(new java.awt.Dimension(450, 300));

        tab3.setFont(frames.UGui.getFont(0,0));
        tab3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Параметры конструкции", "Значение по умолчанию", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
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
        tab3.setFillsViewportHeight(true);
        tab3.setName("tab3"); // NOI18N
        tab3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scr3.setViewportView(tab3);
        if (tab3.getColumnModel().getColumnCount() > 0) {
            tab3.getColumnModel().getColumn(0).setPreferredWidth(400);
            tab3.getColumnModel().getColumn(1).setPreferredWidth(200);
            tab3.getColumnModel().getColumn(2).setPreferredWidth(40);
            tab3.getColumnModel().getColumn(2).setMaxWidth(60);
        }

        pan14.add(scr3, java.awt.BorderLayout.CENTER);

        pan7.add(pan14, "card14");

        pan12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Основные", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan12.setToolTipText("");

        javax.swing.GroupLayout pan12Layout = new javax.swing.GroupLayout(pan12);
        pan12.setLayout(pan12Layout);
        pan12Layout.setHorizontalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );
        pan12Layout.setVerticalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
        );

        pan7.add(pan12, "card12");

        pan5.add(pan7, java.awt.BorderLayout.NORTH);

        scr6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        scr6.setPreferredSize(new java.awt.Dimension(4, 300));

        winTree.setFont(frames.UGui.getFont(0,0));
        scr6.setViewportView(winTree);

        pan5.add(scr6, java.awt.BorderLayout.CENTER);

        pan3.add(pan5, java.awt.BorderLayout.EAST);

        panDesign.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        panDesign.setLayout(new java.awt.BorderLayout());
        pan3.add(panDesign, java.awt.BorderLayout.CENTER);

        tabb1.addTab("        Изделия        ", pan3);

        pan6.setPreferredSize(new java.awt.Dimension(800, 500));
        pan6.setLayout(new java.awt.BorderLayout());

        scr4.setPreferredSize(new java.awt.Dimension(700, 240));

        tab4.setFont(frames.UGui.getFont(0,0));
        tab4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Изделие", "Артикул", "Название", "Текстура", "Внутренняя", "Внешняя", "Длина", "Ширина", "Кол-во", "Угол 1", "Угол 2", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class
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
        tab4.setFillsViewportHeight(true);
        tab4.setPreferredSize(new java.awt.Dimension(700, 32));
        tab4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });
        scr4.setViewportView(tab4);
        if (tab4.getColumnModel().getColumnCount() > 0) {
            tab4.getColumnModel().getColumn(0).setPreferredWidth(240);
            tab4.getColumnModel().getColumn(1).setPreferredWidth(100);
            tab4.getColumnModel().getColumn(2).setPreferredWidth(240);
            tab4.getColumnModel().getColumn(3).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(4).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(5).setPreferredWidth(80);
            tab4.getColumnModel().getColumn(6).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(7).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(8).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(9).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(10).setPreferredWidth(50);
            tab4.getColumnModel().getColumn(11).setPreferredWidth(40);
            tab4.getColumnModel().getColumn(11).setMaxWidth(60);
        }

        pan6.add(scr4, java.awt.BorderLayout.CENTER);

        tabb1.addTab("   Комплектация   ", pan6);

        centr.add(tabb1, java.awt.BorderLayout.CENTER);

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
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        if (tab1.getBorder() != null) {
            if (UGui.isDeleteRecord(tab1, this, tab2) == 0) {
                UGui.deleteRecord(tab1);
            }
        } else if (tab2.getBorder() != null) {
            ImageIcon img = new ImageIcon(this.getClass().getResource("/resource/img24/c014.gif"));
            if (tab2.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Ни одна из записей не вывыделена", "Предупреждение", JOptionPane.NO_OPTION, img);
            } else {
                Record prjprod = qPrjprod.get(UGui.getIndexRec(tab2));
                long count = qPrjkit.stream().filter(rec -> rec.getInt(ePrjkit.prjprod_id) == prjprod.getInt(ePrjprod.id)).count();
                if (count == 0) {
                    if (JOptionPane.showConfirmDialog(this, "Вы действительно хотите удалить текущую запись?", "Подтверждение",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                        UGui.deleteRecord(tab2);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Перед удалением записи, удалите данные в зависимых таблицах",
                            "Предупреждение", JOptionPane.NO_OPTION, img);
                }
            }
        } else if (tab4.getBorder() != null) {
            if (UGui.isDeleteRecord(tab4, this) == 0) {
                UGui.deleteRecord(tab4);
            }
        }
    }//GEN-LAST:event_btnDelete

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert

        UGui.stopCellEditingAndExecSql(getRootPane());

        if (tab1.getBorder() != null) {
            UGui.insertRecordCur(tab1, eProject.up, (projectRec) -> {
                projectRec.set(eProject.manager, eProfile.user);
                projectRec.set(eProject.date4, UGui.getDateCur());
            });

        } else if (tab2.getBorder() != null) {
            //Вставка без UGui.insertRecordCur() т.к. рисунок добавляется в доп. поле
            new DicSyspod(this, (record) -> {
                Record prjprodRec = ePrjprod.up.newRecord(INS);
                prjprodRec.set(ePrjprod.id, Connect.genId(ePrjprod.up));
                prjprodRec.set(ePrjprod.name, record.getStr(eSysprod.name));
                prjprodRec.set(ePrjprod.num, 1);
                prjprodRec.set(ePrjprod.script, record.getStr(eSysprod.script));
                prjprodRec.set(ePrjprod.systree_id, record.getStr(eSysprod.systree_id));
                prjprodRec.set(ePrjprod.project_id, qProject.getAs(UGui.getIndexRec(tab1), eProject.id));
                ePrjprod.data().add(prjprodRec); //добавим в кэш новую запись
                qPrjprod.insert(prjprodRec);
            });

            loadingTab2();

            for (int index = 0; index < qPrjprod.size(); ++index) {
                if (qPrjprod.get(index, ePrjprod.id) == qPrjprod.get(index, ePrjprod.id)) {
                    UGui.setSelectedIndex(tab2, index);
                    UGui.scrollRectToRow(index, tab2);
                    winTree.setSelectionRow(0);
                }
            }
        } else if (tab4.getBorder() != null) {
            int index1 = UGui.getIndexRec(tab1);
            int index2 = UGui.getIndexRec(tab2);
            if (index1 != -1) {
                if (((JButton) evt.getSource()) == btnIns) {
                    if (index2 != -1) { //вставляем запись в продукт
                        UGui.insertRecordCur(tab4, ePrjkit.up, (record) -> {
                            record.set(ePrjkit.prjprod_id, qPrjprod.get(index2, ePrjprod.id));
                            record.set(ePrjkit.project_id, qProject.get(index1, eProject.id));
                            record.set(ePrjkit.numb, 0);
                            int index3 = UGui.getIndexFind(tab4, ePrjkit.id, record.get(ePrjkit.id));
                            qPrjkit.table(eArtikl.up).add(index3, eArtikl.up.newRecord(Query.SEL));
                        });
                    } else { //вставляем запись в проект
                        UGui.insertRecordCur(tab4, ePrjkit.up, (record) -> {
                            record.set(ePrjkit.project_id, qProject.get(index1, eProject.id));
                            record.set(ePrjkit.numb, 0);
                            int index3 = UGui.getIndexFind(tab4, ePrjkit.id, record.get(ePrjkit.id));
                            qPrjkit.table(eArtikl.up).add(index3, eArtikl.up.newRecord(Query.SEL));
                        });
                    }
                } else if (((JButton) evt.getSource()) == btnSet) { //вставляем сразу несколько записей
                    DicKits frame = new DicKits(Project.this, (q) -> {
                        loadingTab4();
                        return true;
                    }, qProject.getAs(index1, eProject.id), qPrjprod.getAs(index2, ePrjprod.id));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Заказ не выбран.", "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnInsert

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditingAndExecSql(getRootPane());
    }//GEN-LAST:event_windowClosed

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab1, tab2, tab3, tab4));
    }//GEN-LAST:event_mousePressed

    private void stateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stateChanged
        UGui.stopCellEditing(tab1, tab2, tab4, tab3);
        if (tabb1.getSelectedIndex() == 0) {
            UGui.updateBorderAndSql(tab1, List.of(tab1, tab2, tab3, tab4));
        } else if (tabb1.getSelectedIndex() == 1) {
            UGui.updateBorderAndSql(tab2, List.of(tab1, tab2, tab3, tab4));
        } else if (tabb1.getSelectedIndex() == 2) {
            UGui.updateBorderAndSql(tab4, List.of(tab1, tab2, tab3, tab4));
        }
    }//GEN-LAST:event_stateChanged

    private void btnCalc(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalc
        ProgressBar.create(Project.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                calculate();
            }
        });
    }//GEN-LAST:event_btnCalc

    private void btnFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter
        loadingTab1();
    }//GEN-LAST:event_btnFilter

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(wincalc().gson.toJson())));
    }//GEN-LAST:event_btnTest

    private void btnFind(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind
        if (tab1.getBorder() != null) {
            Record record = ((DefTableModel) tab1.getModel()).getQuery().get(UGui.getIndexRec(tab1));
            if (record != null) {
                ProgressBar.create(this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        if (tab1.getSelectedColumn() == 5) {
                            App.Partner.createFrame(Project.this, record.getInt(eProject.prjpart_id));
                        } else {
                            App.Partner.createFrame(Project.this, record.getInt(eProject.vendor_id));
                        }
                    }
                });
            }
        } else if (tab4.getBorder() != null) {
            Record record = ((DefTableModel) tab4.getModel()).getQuery().get(UGui.getIndexRec(tab4));
            if (record != null) {
                Record record2 = eArtikl.find(record.getInt(ePrjkit.artikl_id), false);
                ProgressBar.create(this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        App.Artikles.createFrame(Project.this, record2);
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
        JTable table = List.of(tab1, tab2, tab4).stream().filter(it -> it == evt.getSource()).findFirst().get();
        List.of(tab1, tab2, tab4).forEach(tab -> tab.setBorder(null));
        table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
        if (evt.getButton() == MouseEvent.BUTTON3) {
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked

    private void btnRepor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepor
        ppReport.show(north, btnReport.getX(), btnReport.getY() + 18);
    }//GEN-LAST:event_btnRepor

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
            System.err.println("Ошибка: Systree.addStvorkaAction()");
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

    private void elementsView(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elementsView
        ProgressBar.create(Project.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                Com5t com5t = ((DefMutableTreeNode) winTree.getLastSelectedPathComponent()).com5t();
                int sysprodID = qPrjprod.getAs(UGui.getIndexRec(tab5), ePrjprod.id);
                App.Element.createFrame(Project.this, sysprodID, com5t);
            }
        });
    }//GEN-LAST:event_elementsView

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu addImpost;
    private javax.swing.JMenuItem addImpostHor;
    private javax.swing.JMenuItem addImpostVer;
    private javax.swing.JMenuItem addStvorka;
    private javax.swing.JButton btnCalc;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JToggleButton btnF1;
    private javax.swing.JToggleButton btnF2;
    private javax.swing.JToggleButton btnF3;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnIns;
    private javax.swing.JToggleButton btnReport;
    private javax.swing.JButton btnSet;
    private javax.swing.JButton btnTest;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel centr;
    private javax.swing.JMenuItem elements;
    private javax.swing.JLabel lab7;
    private javax.swing.JLabel lab8;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan11;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan14;
    private javax.swing.JPanel pan19;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan7;
    private javax.swing.JPanel pan8;
    private javax.swing.JPanel pan9;
    private javax.swing.JPanel panDesign;
    private javax.swing.JPopupMenu ppReport;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JPopupMenu ppmTree;
    private javax.swing.JMenuItem removeImpost;
    private javax.swing.JMenuItem removeMosquit;
    private javax.swing.JMenuItem removeStvorka;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JScrollPane scr6;
    private javax.swing.JPanel south;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JTable tab5;
    private javax.swing.JTabbedPane tabb1;
    private javax.swing.JTextField txt7;
    private javax.swing.JTextField txt8;
    private javax.swing.JTree winTree;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    private void initElements() {

        btnTest.setVisible(eProp.typuse.equals("99"));
        App.loadLocationWin(this, btnClose, (e) -> {
            App.saveLocationWin(this, btnClose);
        });
        new UColor();

        TableFieldFilter filterTable = new TableFieldFilter(0, tab1);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();
        MainMenu.init(ppReport, this, common.eProp.locale);
        panDesign.add(scene, java.awt.BorderLayout.CENTER);

        List.of(btnIns, btnDel).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab1)));
        winTree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree());
        DefaultTreeCellRenderer rnd2 = (DefaultTreeCellRenderer) winTree.getCellRenderer();
        rnd2.setLeafIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b038.gif")));
        rnd2.setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b007.gif")));
        rnd2.setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b006.gif")));
        tab1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false) {
                    selectionTab1();
                }
            }
        });
        tab2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting() == false && tab2.getSelectedRow() != -1) {
                    tab2.setRowSelectionInterval(tab2.getSelectedRow(), tab2.getSelectedRow());
                    selectionTab2();
                }
            }
        });
        winTree.getSelectionModel().addTreeSelectionListener(tse -> selectionTree());
        DefaultTreeModel model = (DefaultTreeModel) winTree.getModel();
        ((DefaultMutableTreeNode) model.getRoot()).removeAllChildren();
        model.reload();
        tabb1.addChangeListener(new javax.swing.event.ChangeListener() {

            public void stateChanged(javax.swing.event.ChangeEvent evt) {

                if (tabb1.getSelectedIndex() == 0) {
                    btnSet.setEnabled(false);
                    btnIns.setEnabled(true);
                    btnDel.setEnabled(true);
                    btnFind.setEnabled(true);
                    btnFind.setEnabled(true);
                    btnCalc.setEnabled(true);
                    btnF1.setEnabled(true);
                    btnF2.setEnabled(true);
                    btnF3.setEnabled(true);
                    filterTable.setVisible(true);
                }
                if (tabb1.getSelectedIndex() == 1) {
                    canvas.init(wincalc());  //т.к. при смене вклвдки терятся keyPressed(KeyEvent event)
                    btnSet.setEnabled(false);
                    btnIns.setEnabled(false);
                    btnDel.setEnabled(false);
                    btnFind.setEnabled(false);
                    btnCalc.setEnabled(false);
                    btnF1.setEnabled(false);
                    btnF2.setEnabled(false);
                    btnF3.setEnabled(false);
                    filterTable.setVisible(false);

                } else if (tabb1.getSelectedIndex() == 2) {
                    btnSet.setEnabled(true);
                    btnIns.setEnabled(true);
                    btnDel.setEnabled(true);
                    btnFind.setEnabled(true);
                    btnCalc.setEnabled(true);
                    btnF1.setEnabled(true);
                    btnF2.setEnabled(true);
                    btnF3.setEnabled(true);
                    filterTable.setVisible(true);

                }
            }
        });

        listenerWincalc = () -> {
            return wincalc();
        };

        listenerCangeAndRedraw = () -> {
            changeAndRedraw();
        };
    }
}
