package frames;

import frames.swing.ProgressBar;
import frames.swing.FrameToFile;
import frames.dialog.DicCurrenc;
import builder.IArea5e;
import builder.ICom5t;
import builder.Wincalc;
import builder.making.Furniture;
import builder.model1.ElemJoining;
import builder.making.Joining;
import builder.making.Specific;
import builder.making.Tariffic;
import builder.making.UColor;
import builder.IElem5e;
import builder.IStvorka;
import builder.making.Cal5e;
import builder.model1.ElemMosquit;
import builder.script.GsonElem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import common.ArraySpc;
import common.LinkedCom;
import common.UCom;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eProject;
import domain.ePrjpart;
import frames.dialog.DicDate;
import javax.swing.JTable;
import frames.swing.DefTableModel;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;
import javax.swing.table.DefaultTableModel;
import common.eProfile;
import common.eProp;
import common.listener.ListenerFrame;
import common.listener.ListenerObject;
import common.listener.ListenerRecord;
import common.listener.ListenerReload;
import dataset.Conn;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eCurrenc;
import domain.eElement;
import domain.eFurniture;
import domain.eGroups;
import domain.eJoining;
import domain.eJoinvar;
import domain.eParams;
import domain.ePrjkit;
import domain.ePrjprod;
import domain.eSysfurn;
import domain.eSyspar1;
import domain.eSysprod;
import domain.eSysprof;
import domain.eSystree;
import enums.Layout;
import enums.LayoutHandle;
import enums.PKjson;
import enums.TypeGrup;
import enums.TypeOpen1;
import enums.UseSide;
import frames.dialog.DicArtikl;
import frames.dialog.DicArtikl2;
import frames.dialog.DicColor;
import frames.dialog.DicEnums;
import frames.dialog.DicHandl;
import frames.dialog.DicKits;
import frames.dialog.DicName;
import frames.dialog.DicPrjprod;
import frames.dialog.DicSyspod;
import frames.dialog.DicSysprof;
import frames.dialog.ParDefVal;
import frames.swing.draw.Canvas;
import frames.swing.DefMutableTreeNode;
import frames.swing.TableFieldFilter;
import frames.swing.draw.Scene;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import report.HtmlOfInvoice;
import report.HtmlOfMaterial;
import report.HtmlOfOffer;
import report.HtmlOfSmeta;
import report.HtmlOfSpecific;
import startup.App;

public class Orders extends javax.swing.JFrame implements ListenerReload {

    private ImageIcon icon = new ImageIcon(getClass().getResource("/resource/img16/b031.gif"));
    private Query qGroups = new Query(eGroups.values());
    private Query qParams = new Query(eParams.values());
    private Query qCurrenc = new Query(eCurrenc.values());
    private Query qProjectAll = new Query(eProject.values());
    private Query qProject = new Query(eProject.values());
    private Query qPrjpart = new Query(ePrjpart.values());
    private Query qPrjprod = new Query(ePrjprod.values());
    private Query qPrjkit = new Query(ePrjkit.values(), eArtikl.values());
    private Query qSyspar1 = new Query(eSyspar1.values());
    private DefMutableTreeNode winNode = null;
    private Canvas canvas = new Canvas();
    private Scene scene = null;
    Object[] column = new String[]{"", "Скидка (%)", "Без скидок", "Со скидкой"};
    private Gson gson = new GsonBuilder().create();
    private ListenerObject<Query> listenerQuery = null;
    DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            value = (value instanceof Double) ? UCom.format(value, 19) : value;
            JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            lab.setBackground(new java.awt.Color(212, 208, 200));
            return lab;
        }
    };

    public Orders() {
        initComponents();
        scene = new Scene(canvas, spinner, this);
        initElements();
        loadingData();
        loadingModel();
        listenerAdd();
        loadingTab1();
    }

    private void loadingData() {
        qGroups.select(eGroups.up, "where", eGroups.grup, "=", TypeGrup.PARAM_USER.id, "or", eGroups.grup, "=", TypeGrup.COLOR_MAP.id);
        qParams.select(eParams.up);
        qCurrenc.select(eCurrenc.up, "order by", eCurrenc.name);
        qProjectAll.select(eProject.up, "order by", eProject.date4);
        qPrjpart.select(ePrjpart.up);
    }

    public void loadingModel() {
        new DefTableModel(tab1, qProject, eProject.num_ord, eProject.num_acc, eProject.date4, eProject.date6, eProject.prjpart_id, eProject.manager) {
            @Override
            public Object getValueAt(int col, int row, Object val) {
                Field field = columns[col];
                if (field == eProject.prjpart_id) {
                    Record record = qPrjpart.stream().filter(rec -> rec.get(ePrjpart.id).equals(val)).findFirst().orElse(ePrjpart.up.newRecord());
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
                    //Record paramsRec = qParams.find(val, eParams.id);
                    //return qGroups.find(paramsRec.get(eParams.groups_id), eGroups.id).getDev(eGroups.name, val);
                    return qGroups.find(val, eGroups.id).getDev(eGroups.name, val);
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
                                .findFirst().orElse(ePrjprod.up.newRecord()).getStr(ePrjprod.name);
                    } else {
                        return "Заказ № " + qProject.get(UGui.getIndexRec(tab1)).get(eProject.num_ord);
                    }
                } else if (val != null && columns[col] == ePrjkit.color1_id) {
                    return eColor.get((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == ePrjkit.color2_id) {
                    return eColor.get((int) val).getStr(eColor.name);

                } else if (val != null && columns[col] == ePrjkit.color3_id) {
                    return eColor.get((int) val).getStr(eColor.name);

                }
                return val;
            }
        };

        DefaultTableCellRenderer defaultTableDateRenderer = new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                Field field = ((DefTableModel) table.getModel()).columns[column];
                if (field.meta().type() == Field.TYPE.DATE) {
                    value = UGui.DateToStr(value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        tab1.getColumnModel().getColumn(2).setCellRenderer(defaultTableDateRenderer);
        tab1.getColumnModel().getColumn(3).setCellRenderer(defaultTableDateRenderer);
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
        canvas.setVisible(true);
    }

    public void loadingTab1() {
        qProject.clear();
        int first = (btnF1.isSelected()) ? qProjectAll.size() - 10 : (btnF2.isSelected()) ? qProjectAll.size() - 30 : 0;
        first = (first < 0) ? 0 : first;
        for (int i = first; i < qProjectAll.size(); ++i) {
            qProject.add(qProjectAll.get(i));
        }
        //Выделяем заказ если сохранён в Property
        int orderID = Integer.valueOf(eProp.orderID.read());
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
            qPrjprod.select(ePrjprod.up, "where", ePrjprod.project_id, "=", id);

            for (Record record : qPrjprod) {
                try {
                    String script = record.getStr(ePrjprod.script);
                    Wincalc iwin2 = new Wincalc(script);
                    Cal5e joining = new Joining(iwin2, true);//заполним соединения из конструктива
                    joining.calc();
                    iwin2.imageIcon = Canvas.createIcon(iwin2, 68);
                    record.add(iwin2);

                } catch (Exception e) {
                    System.err.println("Ошибка:Order.loadingTab2() " + e);
                }
            }
            ((DefaultTableModel) tab2.getModel()).fireTableDataChanged();

            //Выделяем конструкцию если сохранена в Property
            int prjprodID = Integer.valueOf(eProp.prjprodID.read());
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
        Record projectRec = qProject.get(UGui.getIndexRec(tab1));
        int id = projectRec.getInt(eProject.id);
        qPrjkit.select(ePrjkit.up, "left join", eArtikl.up, "on", ePrjkit.artikl_id, "=", eArtikl.id, "where", ePrjkit.project_id, "=", id);
        ((DefaultTableModel) tab4.getModel()).fireTableDataChanged();
        UGui.setSelectedRow(tab4);
    }

    public void loadingTree(Wincalc winc) {
        try {
            DefMutableTreeNode root = UGui.loadWinTree(winc);
            winTree.setModel(new DefaultTreeModel(root));

        } catch (Exception e) {
            System.err.println("Ошибка:Order.loadingWinTree() " + e);
        }
    }

    public void selectionTab1() {
        UGui.clearTable(tab2, tab4);
        List.of(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        if (tab1.getSelectedRow() != -1) {

            Record projectRec = qProject.get(UGui.getIndexRec(tab1));
            lab2.setText("Заказ № " + projectRec.getStr(eProject.num_ord));
            int orderID = qProject.getAs(UGui.getIndexRec(tab1), eProject.id);
            eProp.orderID.write(String.valueOf(orderID));

            loadingTab2();

            for (Record prjprodRec : qPrjprod) {
                Object w = prjprodRec.get(ePrjprod.values().length);
                if (w instanceof Wincalc) {
                    Wincalc win = (Wincalc) w;
                }
            }
            Record currencRec = qCurrenc.stream().filter(rec -> rec.get(eCurrenc.id).equals(projectRec.get(eProject.currenc_id))).findFirst().orElse(eCurrenc.up.newRecord());
            txt3.setText(currencRec.getStr(eCurrenc.name));
            txt7.setText(UCom.format(projectRec.getDbl(eProject.weight) / 1000, 1));
            txt8.setText(UCom.format(projectRec.getDbl(eProject.square) / 1000000, 1));

            Object data[][] = {{
                " Конструкции", projectRec.getDbl(eProject.disc2), projectRec.getDbl(eProject.price2), projectRec.getDbl(eProject.cost2)},
            {" Комплектации", projectRec.getDbl(eProject.disc3), projectRec.getDbl(eProject.price3), projectRec.getDbl(eProject.cost3)},
            {" Итого за заказ", projectRec.getDbl(eProject.disc4), projectRec.getDbl(eProject.price4), projectRec.getDbl(eProject.cost4)}};
            ((DefaultTableModel) tab5.getModel()).setDataVector(data, column);
            tab5.getColumnModel().getColumn(2).setCellRenderer(defaultTableCellRenderer);
            tab5.getColumnModel().getColumn(3).setCellRenderer(defaultTableCellRenderer);
        }
    }

    public void selectionTab2() {
        UGui.clearTable(tab4);
        Arrays.asList(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            Record prjprodRec = qPrjprod.get(index);
            eProp.prjprodID.write(prjprodRec.getStr(ePrjprod.id)); //запишем текущий prjprodID в файл
            App.Top.frame.setTitle(eProfile.profile.title + UGui.designTitle());
            Object w = prjprodRec.get(ePrjprod.values().length);
            if (w instanceof Wincalc) { //прорисовка окна               
                Wincalc win = (Wincalc) w;
                scene.init(win);
                canvas.draw();
                scene.draw();
                loadingTree(win);
                winTree.setSelectionInterval(0, 0);
            }
        } else {
            scene.init(null);
            scene.draw();
            canvas.draw();
            winTree.setModel(new DefaultTreeModel(new DefMutableTreeNode("")));
        }

        loadingTab4();
    }

    public void selectionTree() {
        try {
            Object selNode = winTree.getLastSelectedPathComponent();
            if (selNode instanceof DefMutableTreeNode) {
                winNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
                Wincalc winc = winc();

                //Конструкции
                if (winNode.com5t().type() == enums.Type.RECTANGL || winNode.com5t().type() == enums.Type.DOOR || winNode.com5t().type() == enums.Type.TRAPEZE || winNode.com5t().type() == enums.Type.ARCH) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card12");
                    ((TitledBorder) pan12.getBorder()).setTitle(winc.rootArea.type().name);
                    setText(txt9, eColor.find(winc.colorID1).getStr(eColor.name));
                    setText(txt13, eColor.find(winc.colorID2).getStr(eColor.name));
                    setText(txt14, eColor.find(winc.colorID3).getStr(eColor.name));
                    setText(txt17, String.valueOf(winc.rootGson.width()));
                    setText(txt22, String.valueOf(winc.rootGson.height1()));
                    setText(txt23, UCom.format(winc.rootGson.height2(), 1));
                    txt23.setEditable(List.of(enums.Type.ARCH, enums.Type.TRIANGL, enums.Type.TRAPEZE).contains(winNode.com5t().type()));
                    int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);
                    setText(txt12, eSystree.find(systreeID).getStr(eSystree.note));

                    //Параметры
                } else if (winNode.com5t().type() == enums.Type.PARAM) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card14");
                    qSyspar1.clear();
                    winc.mapPardef().forEach((pk, syspar1Rec) -> qSyspar1.add(syspar1Rec));
                    Collections.sort(qSyspar1, (o1, o2) -> qGroups.find(o1.getInt(eSyspar1.groups_id), eGroups.id).getStr(eGroups.name)
                            .compareTo(qGroups.find(o2.getInt(eSyspar1.groups_id), eGroups.id).getStr(eGroups.name)));                    
                    ((DefTableModel) tab3.getModel()).fireTableDataChanged();

                    //Рама, импост...
                } else if (winNode.com5t().type() == enums.Type.FRAME_SIDE
                        || winNode.com5t().type() == enums.Type.STVORKA_SIDE
                        || winNode.com5t().type() == enums.Type.IMPOST
                        || winNode.com5t().type() == enums.Type.STOIKA
                        || winNode.com5t().type() == enums.Type.SHTULP) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card13");
                    ((TitledBorder) pan13.getBorder()).setTitle(winNode.toString());
                    txt32.setText(winNode.com5t().artiklRecAn().getStr(eArtikl.code));
                    txt33.setText(winNode.com5t().artiklRecAn().getStr(eArtikl.name));
                    txt27.setText(eColor.find(winNode.com5t().colorID1()).getStr(eColor.name));
                    txt28.setText(eColor.find(winNode.com5t().colorID2()).getStr(eColor.name));
                    txt29.setText(eColor.find(winNode.com5t().colorID3()).getStr(eColor.name));

                    //Стеклопакет
                } else if (winNode.com5t().type() == enums.Type.GLASS) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card15");
                    Record artiklRec = winNode.com5t().artiklRec();
                    txt19.setText(artiklRec.getStr(eArtikl.code));
                    txt18.setText(artiklRec.getStr(eArtikl.name));
                    Record colorRec = eColor.find(winNode.com5t().colorID1());
                    setText(txt34, colorRec.getStr(eColor.name));

                    //Створка
                } else if (winNode.com5t().type() == enums.Type.STVORKA) {
                    new Furniture(winc(), true); //найдём ручку створки
                    ((CardLayout) pan8.getLayout()).show(pan8, "card16");
                    IStvorka stv = (IStvorka) winNode.com5t();
                    IArea5e sta = (IArea5e) winNode.com5t();
                    int id = stv.sysfurnRec().getInt(eSysfurn.furniture_id);
                    setText(txt24, UCom.format(sta.frames().get(Layout.BOTT).width(), 1));
                    double h = (sta.frames().get(Layout.RIGHT).height() > sta.frames().get(Layout.LEFT).height()) ? sta.frames().get(Layout.RIGHT).height() : sta.frames().get(Layout.LEFT).height();
                    setText(txt26, UCom.format(h, 1));
                    setText(txt20, eFurniture.find(id).getStr(eFurniture.name));
                    setIcon(btn10, stv.paramCheck()[0]);
                    setText(txt30, stv.typeOpen().name2);
                    setText(txt16, stv.handleLayout().name);
                    if (stv.handleLayout() == LayoutHandle.VARIAT) {
                        txt31.setEditable(true);
                        setText(txt31, UCom.format(stv.handleHeight(), 1));
                    } else {
                        txt31.setEditable(false);
                        setText(txt31, "");
                    }
                    setText(txt21, stv.handleRec().getStr(eArtikl.code) + " ÷ " + stv.handleRec().getStr(eArtikl.name));
                    setIcon(btn12, stv.paramCheck()[1]);
                    setText(txt25, eColor.find(stv.handleColor()).getStr(eColor.name));
                    setIcon(btn14, stv.paramCheck()[2]);
                    setText(txt45, stv.loopRec().getStr(eArtikl.code));
                    setText(txt58, stv.loopRec().getStr(eArtikl.name));
                    setIcon(btn15, stv.paramCheck()[3]);
                    setText(txt47, eColor.find(stv.loopColor()).getStr(eColor.name));
                    setIcon(btn17, stv.paramCheck()[4]);
                    setText(txt46, stv.lockRec().getStr(eArtikl.code));
                    setText(txt57, stv.lockRec().getStr(eArtikl.name));
                    setIcon(btn23, stv.paramCheck()[5]);
                    setText(txt48, eColor.find(stv.lockColor()).getStr(eColor.name));
                    setIcon(btn24, stv.paramCheck()[6]);
                    //Москитка
                    LinkedCom<ICom5t> mosqList = ((IArea5e) stv).childs().filter(enums.Type.MOSKITKA);
                    if (mosqList.isEmpty() == false) {
                        IElem5e mosq = (IElem5e) mosqList.get(0);
                        setText(txt54, mosq.artiklRec().getStr(eArtikl.code));
                        setText(txt55, mosq.artiklRec().getStr(eArtikl.name));
                        setText(txt60, eColor.find(mosq.colorID1()).getStr(eColor.name));
                        setText(txt56, mosq.sysprofRec().getStr(eElement.name));
                    }

                    //Соединения
                } else if (winNode.com5t().type() == enums.Type.JOINING) {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card17");
                    DefMutableTreeNode nodeParent = (DefMutableTreeNode) winNode.getParent();
                    IElem5e elem5e = (IElem5e) nodeParent.com5t();
                    ElemJoining ej1 = winc.listJoin.get(elem5e, 0);
                    ElemJoining ej2 = winc.listJoin.get(elem5e, 1);
                    ElemJoining ej3 = winc.listJoin.get(elem5e, 2);

                    if (ej1 != null) {
                        setText(txt36, ej1.joiningRec.getStr(eJoining.name));
                        setText(txt42, ej1.name());
                        setText(txt38, ej1.joinvarRec.getStr(eJoinvar.name));
                        lab55.setIcon(UColor.iconFromTypeJoin2(ej1.type.id));
                    }
                    if (ej2 != null) {
                        setText(txt37, ej2.joiningRec.getStr(eJoining.name));
                        setText(txt43, ej2.name());
                        setText(txt39, ej2.joinvarRec.getStr(eJoinvar.name));
                        lab56.setIcon(UColor.iconFromTypeJoin2(ej2.type.id));
                    }
                    if (ej3 != null) {
                        setText(txt40, ej3.joiningRec.getStr(eJoining.name));
                        setText(txt44, ej3.name());
                        setText(txt41, ej3.joinvarRec.getStr(eJoinvar.name));
                        lab57.setIcon(UColor.iconFromTypeJoin2(ej3.type.id));
                    }
                } else {
                    ((CardLayout) pan8.getLayout()).show(pan8, "card18");
                }
                List.of(pan12, pan13, pan15, pan16).forEach(it -> it.repaint());
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Orders.selectionTree() " + e);
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
                record2.set(eProject.date6, calendar.getTime());
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
                return true;
            }, 0);
        });

        UGui.buttonCellEditor(tab1, 4).addActionListener(event -> {
            new Partner(Orders.this, (record) -> {
                UGui.stopCellEditing(tab1, tab2, tab3, tab4);
                Record record2 = qProject.get(UGui.getIndexRec(tab1));
                record2.set(eProject.prjpart_id, record.getInt(ePrjpart.id));
                ((DefaultTableModel) tab1.getModel()).fireTableRowsUpdated(tab1.getSelectedRow(), tab1.getSelectedRow());
            });
        });

        UGui.buttonCellEditor(tab3, 1).addActionListener(event -> {
            UGui.stopCellEditing(tab2, tab3, tab4, tab5);
            int id = qSyspar1.getAs(UGui.getIndexRec(tab3), eSyspar1.id);
            int fixed = eSyspar1.find2(id).getInt(eSyspar1.fixed);
            if (fixed != 1) {
                Integer grup = qSyspar1.getAs(UGui.getIndexRec(tab3), eSyspar1.groups_id);
                new ParDefVal(this, record -> {
                    
                    int index = UGui.getIndexRec(tab2);
                    int index2 = UGui.getIndexRec(tab3);
                    if (index != -1) {
                        Record prjprodRec = qPrjprod.get(index);
                        String script = prjprodRec.getStr(ePrjprod.script);
                        String script2 = UGui.ioknaParamUpdate(script, record.getInt(0));
                        prjprodRec.set(ePrjprod.script, script2);
                        qPrjprod.execsql();
                        winc().build(script2);
                        selectionTree();
                        UGui.setSelectedIndex(tab3, index2);
                    }
                }, grup);
            } else {
                JOptionPane.showMessageDialog(Orders.this, "Неизменяемый параметр в системе", "ВНИМАНИЕ!", 1);
            }
        });

        UGui.buttonCellEditor(tab4, 0).addActionListener(event -> {
            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record projectRec = qProject.get(index);

                new DicPrjprod(Orders.this, (record) -> {
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

    public void listenerSet() {

        listenerQuery = (q) -> {
            return true;
        };
    }

    private Wincalc winc() {
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

    public void updateScript(double selectID) {
        try {
            //Сохраним скрипт в базе
            String script = winc().rootGson.toJson();
            Record prjprodRec = qPrjprod.get(UGui.getIndexRec(tab2));
            prjprodRec.set(eSysprod.script, script);
            qPrjprod.update(prjprodRec);

            //Экземпляр нового скрипта
            Wincalc iwin2 = new Wincalc(script);
            Cal5e joining = new Joining(iwin2, true);//заполним соединения из конструктива
            joining.calc();
            iwin2.imageIcon = Canvas.createIcon(iwin2, 68);
            prjprodRec.setNo(ePrjprod.values().length, iwin2);

            //Запомним курсор
            DefMutableTreeNode selectNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
            double id = (selectNode != null) ? selectNode.com5t().id() : -1;

            //Перегрузим winTree
            loadingTree(iwin2);

            //Перерисуем конструкцию
            scene.init(iwin2);
            canvas.draw();
            scene.draw();

            //Обновим поля форм
            selectionTree();

            //Установим курсор
            UGui.selectionPathWin(id, winTree);

        } catch (Exception e) {
            System.err.println("Ошибка:Order.updateScript() " + e);
        }
    }

    @Override
    public void reload() {
        Wincalc win = winc();
        int index = UGui.getIndexRec(tab2);
        if (index != -1) {
            String script = win.rootGson.toJson();
            win.build(script);
            win.imageIcon = Canvas.createIcon(win, 68);
            Record sysprodRec = qPrjprod.get(index);
            sysprodRec.set(ePrjprod.script, script);
            sysprodRec.set(ePrjprod.values().length, win);
            canvas.draw();
            scene.lineHoriz.forEach(e -> e.init());
            scene.lineVert.forEach(e -> e.init());
            scene.draw();
            selectionTree();
        }
    }

    private void setText(JTextComponent comp, String txt) {
        comp.setText(txt);
        comp.setCaretPosition(0);
    }

    private void setIcon(JButton btn, boolean b) {
        if (b == false) {
            btn.setText("");
            btn.setIcon(icon);
        } else {
            btn.setText("...");
            btn.setIcon(null);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        ppReport = new javax.swing.JPopupMenu();
        menuItem11 = new javax.swing.JMenuItem();
        menuItem12 = new javax.swing.JMenuItem();
        sep1 = new javax.swing.JPopupMenu.Separator();
        menuItem14 = new javax.swing.JMenuItem();
        menuItem13 = new javax.swing.JMenuItem();
        sep2 = new javax.swing.JPopupMenu.Separator();
        menuItem15 = new javax.swing.JMenuItem();
        menuItem16 = new javax.swing.JMenuItem();
        sep3 = new javax.swing.JPopupMenu.Separator();
        menuItem17 = new javax.swing.JMenuItem();
        ppmCrud = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        mDelit = new javax.swing.JMenuItem();
        north = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnSet = new javax.swing.JButton();
        btnRef = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnIns = new javax.swing.JButton();
        btnCalc = new javax.swing.JButton();
        btnF1 = new javax.swing.JToggleButton();
        btnF2 = new javax.swing.JToggleButton();
        btnF3 = new javax.swing.JToggleButton();
        btnTest = new javax.swing.JButton();
        lab2 = new javax.swing.JLabel();
        btnFind = new javax.swing.JButton();
        panSpinner = new javax.swing.JPanel();
        spinner = new javax.swing.JSpinner();
        btnReport = new javax.swing.JButton();
        centr = new javax.swing.JPanel();
        tabb1 = new javax.swing.JTabbedPane();
        pan1 = new javax.swing.JPanel();
        scr1 = new javax.swing.JScrollPane();
        tab1 = new javax.swing.JTable();
        pan11 = new javax.swing.JPanel();
        pan9 = new javax.swing.JPanel();
        pan19 = new javax.swing.JPanel();
        lab1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        lab3 = new javax.swing.JLabel();
        txt3 = new javax.swing.JTextField();
        btn1 = new javax.swing.JButton();
        lab7 = new javax.swing.JLabel();
        txt7 = new javax.swing.JTextField();
        txt8 = new javax.swing.JTextField();
        lab8 = new javax.swing.JLabel();
        scr5 = new javax.swing.JScrollPane();
        tab5 = new javax.swing.JTable();
        pan7 = new javax.swing.JPanel();
        scr2 = new javax.swing.JScrollPane();
        tab2 = new javax.swing.JTable();
        pan3 = new javax.swing.JPanel();
        pan5 = new javax.swing.JPanel();
        pan8 = new javax.swing.JPanel();
        pan14 = new javax.swing.JPanel();
        scr3 = new javax.swing.JScrollPane();
        tab3 = new javax.swing.JTable();
        pan12 = new javax.swing.JPanel();
        pan21 = new javax.swing.JPanel();
        lab27 = new javax.swing.JLabel();
        lab31 = new javax.swing.JLabel();
        lab32 = new javax.swing.JLabel();
        btn9 = new javax.swing.JButton();
        btn13 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        txt9 = new javax.swing.JTextField();
        txt13 = new javax.swing.JTextField();
        txt14 = new javax.swing.JTextField();
        lab35 = new javax.swing.JLabel();
        txt17 = new javax.swing.JTextField();
        lab38 = new javax.swing.JLabel();
        txt22 = new javax.swing.JTextField();
        lab40 = new javax.swing.JLabel();
        txt23 = new javax.swing.JTextField();
        lab69 = new javax.swing.JLabel();
        txt12 = new javax.swing.JTextArea();
        pan13 = new javax.swing.JPanel();
        pan20 = new javax.swing.JPanel();
        lab28 = new javax.swing.JLabel();
        lab43 = new javax.swing.JLabel();
        lab44 = new javax.swing.JLabel();
        btn18 = new javax.swing.JButton();
        btn19 = new javax.swing.JButton();
        btn20 = new javax.swing.JButton();
        txt27 = new javax.swing.JTextField();
        txt28 = new javax.swing.JTextField();
        txt29 = new javax.swing.JTextField();
        lab33 = new javax.swing.JLabel();
        lab34 = new javax.swing.JLabel();
        txt32 = new javax.swing.JTextField();
        txt33 = new javax.swing.JTextField();
        btn22 = new javax.swing.JButton();
        pan15 = new javax.swing.JPanel();
        lab29 = new javax.swing.JLabel();
        lab36 = new javax.swing.JLabel();
        btn3 = new javax.swing.JButton();
        txt19 = new javax.swing.JTextField();
        txt18 = new javax.swing.JTextField();
        lab61 = new javax.swing.JLabel();
        txt34 = new javax.swing.JTextField();
        btn25 = new javax.swing.JButton();
        pan16 = new javax.swing.JPanel();
        tabb2 = new javax.swing.JTabbedPane();
        pan22 = new javax.swing.JPanel();
        lab41 = new javax.swing.JLabel();
        txt24 = new javax.swing.JTextField();
        lab42 = new javax.swing.JLabel();
        txt26 = new javax.swing.JTextField();
        lab30 = new javax.swing.JLabel();
        txt20 = new javax.swing.JTextField();
        btn10 = new javax.swing.JButton();
        lab45 = new javax.swing.JLabel();
        txt30 = new javax.swing.JTextField();
        btn21 = new javax.swing.JButton();
        lab37 = new javax.swing.JLabel();
        txt21 = new javax.swing.JTextField();
        btn12 = new javax.swing.JButton();
        lab39 = new javax.swing.JLabel();
        txt25 = new javax.swing.JTextField();
        btn14 = new javax.swing.JButton();
        lab46 = new javax.swing.JLabel();
        txt16 = new javax.swing.JTextField();
        txt31 = new javax.swing.JTextField();
        btn6 = new javax.swing.JButton();
        pan23 = new javax.swing.JPanel();
        lab26 = new javax.swing.JLabel();
        txt45 = new javax.swing.JTextField();
        btn15 = new javax.swing.JButton();
        lab48 = new javax.swing.JLabel();
        txt47 = new javax.swing.JTextField();
        btn17 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txt46 = new javax.swing.JTextField();
        btn23 = new javax.swing.JButton();
        lab63 = new javax.swing.JLabel();
        txt48 = new javax.swing.JTextField();
        btn24 = new javax.swing.JButton();
        txt57 = new javax.swing.JTextField();
        lab51 = new javax.swing.JLabel();
        lab52 = new javax.swing.JLabel();
        txt58 = new javax.swing.JTextField();
        pan24 = new javax.swing.JPanel();
        lab47 = new javax.swing.JLabel();
        txt54 = new javax.swing.JTextField();
        btn16 = new javax.swing.JButton();
        lab53 = new javax.swing.JLabel();
        txt55 = new javax.swing.JTextField();
        lab66 = new javax.swing.JLabel();
        txt60 = new javax.swing.JTextField();
        btn32 = new javax.swing.JButton();
        lab62 = new javax.swing.JLabel();
        txt56 = new javax.swing.JTextField();
        btn31 = new javax.swing.JButton();
        pan17 = new javax.swing.JPanel();
        lab49 = new javax.swing.JLabel();
        lab50 = new javax.swing.JLabel();
        txt36 = new javax.swing.JTextField();
        txt37 = new javax.swing.JTextField();
        lab55 = new javax.swing.JLabel();
        txt38 = new javax.swing.JTextField();
        lab56 = new javax.swing.JLabel();
        txt39 = new javax.swing.JTextField();
        lab54 = new javax.swing.JLabel();
        txt40 = new javax.swing.JTextField();
        lab57 = new javax.swing.JLabel();
        txt41 = new javax.swing.JTextField();
        lab58 = new javax.swing.JLabel();
        txt42 = new javax.swing.JTextField();
        lab59 = new javax.swing.JLabel();
        txt43 = new javax.swing.JTextField();
        lab60 = new javax.swing.JLabel();
        txt44 = new javax.swing.JTextField();
        pan18 = new javax.swing.JPanel();
        scr6 = new javax.swing.JScrollPane();
        winTree = new javax.swing.JTree();
        panDesign = new javax.swing.JPanel();
        pan6 = new javax.swing.JPanel();
        scr4 = new javax.swing.JScrollPane();
        tab4 = new javax.swing.JTable();
        south = new javax.swing.JPanel();

        menuItem11.setFont(frames.UGui.getFont(0,1));
        menuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        menuItem11.setText("Расход материалов");
        menuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem11(evt);
            }
        });
        ppReport.add(menuItem11);

        menuItem12.setFont(frames.UGui.getFont(0,1));
        menuItem12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        menuItem12.setText("Спецификация");
        menuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem12(evt);
            }
        });
        ppReport.add(menuItem12);
        ppReport.add(sep1);

        menuItem14.setFont(frames.UGui.getFont(0,1));
        menuItem14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        menuItem14.setText("Смета");
        menuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem14(evt);
            }
        });
        ppReport.add(menuItem14);

        menuItem13.setFont(frames.UGui.getFont(0,1));
        menuItem13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b055.gif"))); // NOI18N
        menuItem13.setText("Смета подробная");
        menuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem13(evt);
            }
        });
        ppReport.add(menuItem13);
        ppReport.add(sep2);

        menuItem15.setFont(frames.UGui.getFont(0,1));
        menuItem15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        menuItem15.setText("Счёт");
        menuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem15(evt);
            }
        });
        ppReport.add(menuItem15);

        menuItem16.setFont(frames.UGui.getFont(0,1));
        menuItem16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b057.gif"))); // NOI18N
        menuItem16.setText("Счёт-фактура");
        menuItem16.setToolTipText("");
        menuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem16(evt);
            }
        });
        ppReport.add(menuItem16);
        ppReport.add(sep3);

        menuItem17.setFont(frames.UGui.getFont(0,1));
        menuItem17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"))); // NOI18N
        menuItem17.setText("Коммерческое предл...");
        menuItem17.setToolTipText("");
        menuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem17(evt);
            }
        });
        ppReport.add(menuItem17);

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
        setTitle("Заказы");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/resource/img32/d033.gif")).getImage()));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(900, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                Orders.this.windowClosed(evt);
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

        btnSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c086.gif"))); // NOI18N
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
        btnF3.setText("∑");
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

        lab2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lab2.setText("Заказ №");
        lab2.setMaximumSize(new java.awt.Dimension(120, 24));
        lab2.setPreferredSize(new java.awt.Dimension(120, 24));

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

        panSpinner.setMinimumSize(new java.awt.Dimension(50, 20));
        panSpinner.setPreferredSize(new java.awt.Dimension(100, 24));
        panSpinner.setLayout(new java.awt.BorderLayout());

        spinner.setFont(frames.UGui.getFont(1,0));
        spinner.setModel(new javax.swing.SpinnerNumberModel());
        spinner.setBorder(null);
        spinner.setEnabled(false);
        spinner.setPreferredSize(new java.awt.Dimension(50, 24));
        panSpinner.add(spinner, java.awt.BorderLayout.CENTER);

        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/img24/c032.gif"))); // NOI18N
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
                .addComponent(btnSet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGap(18, 18, 18)
                .addComponent(panSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(lab2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGroup(northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(btnRef, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCalc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFind, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                .addComponent(lab2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(panSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        centr.setPreferredSize(new java.awt.Dimension(800, 550));
        centr.setLayout(new java.awt.BorderLayout());

        tabb1.setFont(frames.UGui.getFont(1,0));
        tabb1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Orders.this.stateChanged(evt);
            }
        });

        pan1.setLayout(new java.awt.BorderLayout());

        scr1.setBorder(null);
        scr1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tab1.setFont(frames.UGui.getFont(0,0));
        tab1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Номер заказа", "Номер счёта", "Дата от...", "Дата до...", "Контрагент", "User", "ID"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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
        tab1.setFillsViewportHeight(true);
        tab1.setName("tab1"); // NOI18N
        tab1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tab1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Orders.this.mousePressed(evt);
            }
        });
        scr1.setViewportView(tab1);
        if (tab1.getColumnModel().getColumnCount() > 0) {
            tab1.getColumnModel().getColumn(0).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(1).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(2).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(3).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(4).setPreferredWidth(200);
            tab1.getColumnModel().getColumn(5).setPreferredWidth(60);
            tab1.getColumnModel().getColumn(6).setPreferredWidth(40);
            tab1.getColumnModel().getColumn(6).setMaxWidth(60);
        }

        pan1.add(scr1, java.awt.BorderLayout.CENTER);

        pan11.setPreferredSize(new java.awt.Dimension(400, 550));
        pan11.setLayout(new java.awt.BorderLayout());

        pan9.setPreferredSize(new java.awt.Dimension(450, 142));
        pan9.setLayout(new java.awt.BorderLayout());

        pan19.setPreferredSize(new java.awt.Dimension(450, 68));

        lab1.setFont(frames.UGui.getFont(0,0));
        lab1.setText("Тип расчтета");
        lab1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab1.setMinimumSize(new java.awt.Dimension(34, 14));
        lab1.setPreferredSize(new java.awt.Dimension(86, 20));

        jComboBox1.setFont(frames.UGui.getFont(0,0));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "По спецификации" }));
        jComboBox1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jComboBox1.setPreferredSize(new java.awt.Dimension(180, 20));

        lab3.setFont(frames.UGui.getFont(0,0));
        lab3.setText("Валюта");
        lab3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab3.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        lab3.setPreferredSize(new java.awt.Dimension(60, 18));

        txt3.setFont(frames.UGui.getFont(0,0));
        txt3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt3.setFocusable(false);
        txt3.setPreferredSize(new java.awt.Dimension(62, 20));

        btn1.setText("...");
        btn1.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn1.setMaximumSize(new java.awt.Dimension(21, 20));
        btn1.setMinimumSize(new java.awt.Dimension(21, 20));
        btn1.setName("btn1"); // NOI18N
        btn1.setPreferredSize(new java.awt.Dimension(21, 20));
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });

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
        lab8.setPreferredSize(new java.awt.Dimension(60, 18));

        javax.swing.GroupLayout pan19Layout = new javax.swing.GroupLayout(pan19);
        pan19.setLayout(pan19Layout);
        pan19Layout.setHorizontalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan19Layout.createSequentialGroup()
                        .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lab7, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(18, Short.MAX_VALUE))
                    .addGroup(pan19Layout.createSequentialGroup()
                        .addComponent(lab1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pan19Layout.setVerticalGroup(
            pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan19Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        pan9.add(pan19, java.awt.BorderLayout.NORTH);

        scr5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scr5.setPreferredSize(new java.awt.Dimension(450, 90));

        tab5.setFont(frames.UGui.getFont(0,0));
        tab5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"   Конструкции", Double.valueOf(0.0), Double.valueOf(0.0), Double.valueOf(0.0)},
                {"   Комплектации",  new Double(0.0), Double.valueOf(0.0), Double.valueOf(0.0)},
                {"   Итого за заказ",  new Double(0.0), Double.valueOf(0.0), Double.valueOf(0.0)}
            },
            new String [] {
                "", "Скидка (%)", "Без скидок", "Со скидкой"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
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
                super.setValueAt(aValue, row, column);
                Record projectRec = qProject.get(UGui.getIndexRec(tab1));
                if (row == 0 && column == 1 && projectRec.get(eProject.disc2).equals(aValue) == false) {
                    projectRec.set(eProject.disc2, aValue);
                } else if (row == 1 && column == 1 && projectRec.get(eProject.disc3).equals(aValue) == false) {
                    projectRec.set(eProject.disc3, aValue);
                } else if (row == 2 && column == 1 && projectRec.get(eProject.disc4).equals(aValue) == false) {
                    projectRec.set(eProject.disc4, aValue);
                }
            }
        });
        tab5.getTableHeader().setReorderingAllowed(false);
        scr5.setViewportView(tab5);

        pan9.add(scr5, java.awt.BorderLayout.CENTER);

        pan11.add(pan9, java.awt.BorderLayout.NORTH);

        pan7.setPreferredSize(new java.awt.Dimension(404, 320));
        pan7.setLayout(new java.awt.BorderLayout());

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
        tab2.setRowHeight(68);
        tab2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Orders.this.mousePressed(evt);
            }
        });
        scr2.setViewportView(tab2);
        if (tab2.getColumnModel().getColumnCount() > 0) {
            tab2.getColumnModel().getColumn(0).setPreferredWidth(80);
            tab2.getColumnModel().getColumn(1).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(1).setMaxWidth(80);
            tab2.getColumnModel().getColumn(2).setMinWidth(68);
            tab2.getColumnModel().getColumn(2).setPreferredWidth(68);
            tab2.getColumnModel().getColumn(2).setMaxWidth(68);
            tab2.getColumnModel().getColumn(3).setPreferredWidth(40);
            tab2.getColumnModel().getColumn(3).setMaxWidth(60);
        }

        pan7.add(scr2, java.awt.BorderLayout.CENTER);

        pan11.add(pan7, java.awt.BorderLayout.CENTER);

        pan1.add(pan11, java.awt.BorderLayout.EAST);

        tabb1.addTab("       Заказы       ", pan1);

        pan3.setLayout(new java.awt.BorderLayout());

        pan5.setPreferredSize(new java.awt.Dimension(400, 450));
        pan5.setLayout(new java.awt.BorderLayout());

        pan8.setPreferredSize(new java.awt.Dimension(10, 292));
        pan8.setLayout(new java.awt.CardLayout());

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

        pan8.add(pan14, "card14");

        pan12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Основные", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan12.setToolTipText("");
        pan12.setPreferredSize(new java.awt.Dimension(300, 200));

        pan21.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Текстура изделия", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 0)));
        pan21.setPreferredSize(new java.awt.Dimension(308, 104));

        lab27.setFont(frames.UGui.getFont(0,0));
        lab27.setText("Основная");
        lab27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab27.setPreferredSize(new java.awt.Dimension(80, 18));

        lab31.setFont(frames.UGui.getFont(0,0));
        lab31.setText("Внутренняя");
        lab31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab31.setPreferredSize(new java.awt.Dimension(80, 18));

        lab32.setFont(frames.UGui.getFont(0,0));
        lab32.setText("Внешняя");
        lab32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab32.setPreferredSize(new java.awt.Dimension(80, 18));

        btn9.setText("...");
        btn9.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn9.setMaximumSize(new java.awt.Dimension(21, 20));
        btn9.setMinimumSize(new java.awt.Dimension(21, 20));
        btn9.setName("btn9"); // NOI18N
        btn9.setPreferredSize(new java.awt.Dimension(21, 20));
        btn9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToWindows(evt);
            }
        });

        btn13.setText("...");
        btn13.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn13.setMaximumSize(new java.awt.Dimension(21, 20));
        btn13.setMinimumSize(new java.awt.Dimension(21, 20));
        btn13.setName("btn13"); // NOI18N
        btn13.setPreferredSize(new java.awt.Dimension(21, 20));
        btn13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToWindows(evt);
            }
        });

        btn2.setText("...");
        btn2.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn2.setMaximumSize(new java.awt.Dimension(21, 20));
        btn2.setMinimumSize(new java.awt.Dimension(21, 20));
        btn2.setName("btn2"); // NOI18N
        btn2.setPreferredSize(new java.awt.Dimension(21, 20));
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToWindows(evt);
            }
        });

        txt9.setEditable(false);
        txt9.setFont(frames.UGui.getFont(0,0));
        txt9.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt9.setPreferredSize(new java.awt.Dimension(180, 18));

        txt13.setEditable(false);
        txt13.setFont(frames.UGui.getFont(0,0));
        txt13.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt13.setPreferredSize(new java.awt.Dimension(180, 18));

        txt14.setEditable(false);
        txt14.setFont(frames.UGui.getFont(0,0));
        txt14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt14.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan21Layout = new javax.swing.GroupLayout(pan21);
        pan21.setLayout(pan21Layout);
        pan21Layout.setHorizontalGroup(
            pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan21Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt14, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan21Layout.createSequentialGroup()
                        .addComponent(lab31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );
        pan21Layout.setVerticalGroup(
            pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan21Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        lab35.setFont(frames.UGui.getFont(0,0));
        lab35.setText("Ширина");
        lab35.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab35.setPreferredSize(new java.awt.Dimension(80, 18));

        txt17.setFont(frames.UGui.getFont(0,0));
        txt17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt17.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt17.setPreferredSize(new java.awt.Dimension(60, 18));
        txt17.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtKeyEnter(evt);
            }
        });

        lab38.setFont(frames.UGui.getFont(0,0));
        lab38.setText("Высота1");
        lab38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab38.setPreferredSize(new java.awt.Dimension(80, 18));

        txt22.setFont(frames.UGui.getFont(0,0));
        txt22.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt22.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt22.setPreferredSize(new java.awt.Dimension(60, 18));
        txt22.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKeyEnter(evt);
            }
        });

        lab40.setFont(frames.UGui.getFont(0,0));
        lab40.setText("Высота2");
        lab40.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab40.setPreferredSize(new java.awt.Dimension(80, 18));

        txt23.setFont(frames.UGui.getFont(0,0));
        txt23.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt23.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt23.setPreferredSize(new java.awt.Dimension(60, 18));
        txt23.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtKeyEnter(evt);
            }
        });

        lab69.setFont(frames.UGui.getFont(0,0));
        lab69.setText("Примечание");
        lab69.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab69.setMaximumSize(new java.awt.Dimension(600, 36));
        lab69.setMinimumSize(new java.awt.Dimension(80, 36));
        lab69.setPreferredSize(new java.awt.Dimension(80, 36));

        txt12.setEditable(false);
        txt12.setFont(frames.UGui.getFont(0,0));
        txt12.setLineWrap(true);
        txt12.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt12.setMinimumSize(new java.awt.Dimension(5, 36));
        txt12.setPreferredSize(new java.awt.Dimension(164, 36));

        javax.swing.GroupLayout pan12Layout = new javax.swing.GroupLayout(pan12);
        pan12.setLayout(pan12Layout);
        pan12Layout.setHorizontalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pan21, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
            .addGroup(pan12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addComponent(lab69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan12Layout.createSequentialGroup()
                        .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pan12Layout.createSequentialGroup()
                                .addComponent(lab35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pan12Layout.createSequentialGroup()
                                .addComponent(lab38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pan12Layout.createSequentialGroup()
                                .addComponent(lab40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan12Layout.setVerticalGroup(
            pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan12Layout.createSequentialGroup()
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lab69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pan21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );

        pan8.add(pan12, "card12");

        pan13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Рама, импост..", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan13.setPreferredSize(new java.awt.Dimension(300, 200));

        pan20.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Текстура элемента", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 0)));
        pan20.setPreferredSize(new java.awt.Dimension(308, 104));

        lab28.setFont(frames.UGui.getFont(0,0));
        lab28.setText("Основная");
        lab28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab28.setPreferredSize(new java.awt.Dimension(80, 18));

        lab43.setFont(frames.UGui.getFont(0,0));
        lab43.setText("Внутренняя");
        lab43.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab43.setPreferredSize(new java.awt.Dimension(80, 18));

        lab44.setFont(frames.UGui.getFont(0,0));
        lab44.setText("Внешняя");
        lab44.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab44.setPreferredSize(new java.awt.Dimension(80, 18));

        btn18.setText("...");
        btn18.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn18.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn18.setMaximumSize(new java.awt.Dimension(21, 20));
        btn18.setMinimumSize(new java.awt.Dimension(21, 20));
        btn18.setName("btnField17"); // NOI18N
        btn18.setPreferredSize(new java.awt.Dimension(21, 20));
        btn18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToFrame(evt);
            }
        });

        btn19.setText("...");
        btn19.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn19.setMaximumSize(new java.awt.Dimension(21, 20));
        btn19.setMinimumSize(new java.awt.Dimension(21, 20));
        btn19.setName("btnField17"); // NOI18N
        btn19.setPreferredSize(new java.awt.Dimension(21, 20));
        btn19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToFrame(evt);
            }
        });

        btn20.setText("...");
        btn20.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn20.setMaximumSize(new java.awt.Dimension(21, 20));
        btn20.setMinimumSize(new java.awt.Dimension(21, 20));
        btn20.setName("btnField17"); // NOI18N
        btn20.setPreferredSize(new java.awt.Dimension(21, 20));
        btn20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToFrame(evt);
            }
        });

        txt27.setEditable(false);
        txt27.setFont(frames.UGui.getFont(0,0));
        txt27.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt27.setPreferredSize(new java.awt.Dimension(180, 18));

        txt28.setEditable(false);
        txt28.setFont(frames.UGui.getFont(0,0));
        txt28.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt28.setPreferredSize(new java.awt.Dimension(180, 18));

        txt29.setEditable(false);
        txt29.setFont(frames.UGui.getFont(0,0));
        txt29.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt29.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan20Layout = new javax.swing.GroupLayout(pan20);
        pan20.setLayout(pan20Layout);
        pan20Layout.setHorizontalGroup(
            pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan20Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt29, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt27, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(pan20Layout.createSequentialGroup()
                        .addComponent(lab43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt28, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );
        pan20Layout.setVerticalGroup(
            pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan20Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        lab33.setFont(frames.UGui.getFont(0,0));
        lab33.setText("  Артикул");
        lab33.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab33.setPreferredSize(new java.awt.Dimension(80, 18));

        lab34.setFont(frames.UGui.getFont(0,0));
        lab34.setText("  Название");
        lab34.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab34.setPreferredSize(new java.awt.Dimension(80, 18));

        txt32.setEditable(false);
        txt32.setFont(frames.UGui.getFont(0,0));
        txt32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt32.setPreferredSize(new java.awt.Dimension(180, 18));

        txt33.setEditable(false);
        txt33.setFont(frames.UGui.getFont(0,0));
        txt33.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt33.setPreferredSize(new java.awt.Dimension(180, 18));

        btn22.setText("...");
        btn22.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn22.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn22.setMaximumSize(new java.awt.Dimension(21, 20));
        btn22.setMinimumSize(new java.awt.Dimension(21, 20));
        btn22.setName("btnField17"); // NOI18N
        btn22.setPreferredSize(new java.awt.Dimension(21, 20));
        btn22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sysprofToFrame(evt);
            }
        });

        javax.swing.GroupLayout pan13Layout = new javax.swing.GroupLayout(pan13);
        pan13.setLayout(pan13Layout);
        pan13Layout.setHorizontalGroup(
            pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pan20, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addGroup(pan13Layout.createSequentialGroup()
                        .addComponent(lab33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt32, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan13Layout.createSequentialGroup()
                        .addComponent(lab34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt33, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan13Layout.setVerticalGroup(
            pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pan20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(109, Short.MAX_VALUE))
        );

        pan8.add(pan13, "card13");

        pan15.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Стеклопакет", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan15.setPreferredSize(new java.awt.Dimension(300, 200));

        lab29.setFont(frames.UGui.getFont(0,0));
        lab29.setText("Артикул");
        lab29.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab29.setPreferredSize(new java.awt.Dimension(80, 18));

        lab36.setFont(frames.UGui.getFont(0,0));
        lab36.setText("Название");
        lab36.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab36.setPreferredSize(new java.awt.Dimension(80, 18));

        btn3.setText("...");
        btn3.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn3.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn3.setMaximumSize(new java.awt.Dimension(21, 20));
        btn3.setMinimumSize(new java.awt.Dimension(21, 20));
        btn3.setName("btnField17"); // NOI18N
        btn3.setPreferredSize(new java.awt.Dimension(21, 20));
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToArtiklGlass(evt);
            }
        });

        txt19.setEditable(false);
        txt19.setFont(frames.UGui.getFont(0,0));
        txt19.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt19.setPreferredSize(new java.awt.Dimension(180, 18));

        txt18.setEditable(false);
        txt18.setFont(frames.UGui.getFont(0,0));
        txt18.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt18.setPreferredSize(new java.awt.Dimension(180, 18));

        lab61.setFont(frames.UGui.getFont(0,0));
        lab61.setText("Цвет");
        lab61.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab61.setPreferredSize(new java.awt.Dimension(80, 18));

        txt34.setEditable(false);
        txt34.setFont(frames.UGui.getFont(0,0));
        txt34.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt34.setPreferredSize(new java.awt.Dimension(180, 18));

        btn25.setText("...");
        btn25.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn25.setMaximumSize(new java.awt.Dimension(21, 20));
        btn25.setMinimumSize(new java.awt.Dimension(21, 20));
        btn25.setName("btnField17"); // NOI18N
        btn25.setPreferredSize(new java.awt.Dimension(21, 20));
        btn25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorFromGlass(evt);
            }
        });

        javax.swing.GroupLayout pan15Layout = new javax.swing.GroupLayout(pan15);
        pan15.setLayout(pan15Layout);
        pan15Layout.setHorizontalGroup(
            pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt19, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt18, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                    .addGroup(pan15Layout.createSequentialGroup()
                        .addComponent(lab61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt34, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pan15Layout.setVerticalGroup(
            pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(193, Short.MAX_VALUE))
        );

        pan8.add(pan15, "card15");

        pan16.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Створка", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan16.setPreferredSize(new java.awt.Dimension(3100, 220));
        pan16.setLayout(new java.awt.BorderLayout());

        tabb2.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabb2.setFont(frames.UGui.getFont(-1,0));

        lab41.setFont(frames.UGui.getFont(0,0));
        lab41.setText("Ширина");
        lab41.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab41.setPreferredSize(new java.awt.Dimension(80, 18));

        txt24.setFont(frames.UGui.getFont(0,0));
        txt24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt24.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt24.setPreferredSize(new java.awt.Dimension(60, 18));

        lab42.setFont(frames.UGui.getFont(0,0));
        lab42.setText("Высота");
        lab42.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab42.setPreferredSize(new java.awt.Dimension(80, 18));

        txt26.setFont(frames.UGui.getFont(0,0));
        txt26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt26.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txt26.setPreferredSize(new java.awt.Dimension(60, 18));
        txt26.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtKeyEnter(evt);
            }
        });

        lab30.setFont(frames.UGui.getFont(0,0));
        lab30.setText("Фурнитура");
        lab30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab30.setPreferredSize(new java.awt.Dimension(80, 18));

        txt20.setEditable(false);
        txt20.setFont(frames.UGui.getFont(0,0));
        txt20.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt20.setPreferredSize(new java.awt.Dimension(178, 18));

        btn10.setText("...");
        btn10.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn10.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn10.setMaximumSize(new java.awt.Dimension(21, 20));
        btn10.setMinimumSize(new java.awt.Dimension(21, 20));
        btn10.setName("btnField17"); // NOI18N
        btn10.setPreferredSize(new java.awt.Dimension(21, 20));
        btn10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sysfurnToStvorka(evt);
            }
        });

        lab45.setFont(frames.UGui.getFont(0,0));
        lab45.setText("Напр. откр.");
        lab45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab45.setPreferredSize(new java.awt.Dimension(80, 18));

        txt30.setEditable(false);
        txt30.setFont(frames.UGui.getFont(0,0));
        txt30.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt30.setPreferredSize(new java.awt.Dimension(178, 18));

        btn21.setText("...");
        btn21.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn21.setMaximumSize(new java.awt.Dimension(21, 20));
        btn21.setMinimumSize(new java.awt.Dimension(21, 20));
        btn21.setName("btnField17"); // NOI18N
        btn21.setPreferredSize(new java.awt.Dimension(21, 20));
        btn21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeOpenToStvorka(evt);
            }
        });

        lab37.setFont(frames.UGui.getFont(0,0));
        lab37.setText("Ручка");
        lab37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab37.setPreferredSize(new java.awt.Dimension(80, 18));

        txt21.setEditable(false);
        txt21.setFont(frames.UGui.getFont(0,0));
        txt21.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt21.setPreferredSize(new java.awt.Dimension(178, 18));

        btn12.setText("...");
        btn12.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn12.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn12.setMaximumSize(new java.awt.Dimension(21, 20));
        btn12.setMinimumSize(new java.awt.Dimension(21, 20));
        btn12.setName("btnField17"); // NOI18N
        btn12.setPreferredSize(new java.awt.Dimension(21, 20));
        btn12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handkToStvorka(evt);
            }
        });

        lab39.setFont(frames.UGui.getFont(0,0));
        lab39.setText("Текстура");
        lab39.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab39.setPreferredSize(new java.awt.Dimension(80, 18));

        txt25.setEditable(false);
        txt25.setFont(frames.UGui.getFont(0,0));
        txt25.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt25.setPreferredSize(new java.awt.Dimension(178, 18));

        btn14.setText("...");
        btn14.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn14.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn14.setMaximumSize(new java.awt.Dimension(21, 20));
        btn14.setMinimumSize(new java.awt.Dimension(21, 20));
        btn14.setName("btnField17"); // NOI18N
        btn14.setPreferredSize(new java.awt.Dimension(21, 20));
        btn14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorToHandl(evt);
            }
        });

        lab46.setFont(frames.UGui.getFont(0,0));
        lab46.setText("Высота ручки");
        lab46.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab46.setPreferredSize(new java.awt.Dimension(80, 18));

        txt16.setEditable(false);
        txt16.setFont(frames.UGui.getFont(0,0));
        txt16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt16.setPreferredSize(new java.awt.Dimension(208, 18));

        txt31.setFont(frames.UGui.getFont(0,0));
        txt31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt31.setPreferredSize(new java.awt.Dimension(56, 18));

        btn6.setText("...");
        btn6.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn6.setMaximumSize(new java.awt.Dimension(21, 20));
        btn6.setMinimumSize(new java.awt.Dimension(21, 20));
        btn6.setName("btnField17"); // NOI18N
        btn6.setPreferredSize(new java.awt.Dimension(21, 20));
        btn6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightHandlToStvorka(evt);
            }
        });

        javax.swing.GroupLayout pan22Layout = new javax.swing.GroupLayout(pan22);
        pan22.setLayout(pan22Layout);
        pan22Layout.setHorizontalGroup(
            pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lab45, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lab30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt30, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addComponent(lab41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lab42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lab39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lab37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt25, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pan22Layout.createSequentialGroup()
                        .addComponent(lab46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt16, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pan22Layout.setVerticalGroup(
            pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabb2.addTab("Основн...", pan22);

        lab26.setFont(frames.UGui.getFont(0,0));
        lab26.setText("Подвес");
        lab26.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab26.setMaximumSize(new java.awt.Dimension(80, 18));
        lab26.setMinimumSize(new java.awt.Dimension(80, 18));
        lab26.setPreferredSize(new java.awt.Dimension(80, 18));

        txt45.setEditable(false);
        txt45.setFont(frames.UGui.getFont(0,0));
        txt45.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt45.setPreferredSize(new java.awt.Dimension(178, 18));

        btn15.setText("...");
        btn15.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn15.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn15.setMaximumSize(new java.awt.Dimension(21, 20));
        btn15.setMinimumSize(new java.awt.Dimension(21, 20));
        btn15.setName("btnField17"); // NOI18N
        btn15.setPreferredSize(new java.awt.Dimension(21, 20));
        btn15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopToStvorka(evt);
            }
        });

        lab48.setFont(frames.UGui.getFont(0,0));
        lab48.setText("Текстура");
        lab48.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab48.setMaximumSize(new java.awt.Dimension(80, 18));
        lab48.setMinimumSize(new java.awt.Dimension(80, 18));
        lab48.setPreferredSize(new java.awt.Dimension(80, 18));

        txt47.setEditable(false);
        txt47.setFont(frames.UGui.getFont(0,0));
        txt47.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt47.setPreferredSize(new java.awt.Dimension(178, 18));

        btn17.setText("...");
        btn17.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn17.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn17.setMaximumSize(new java.awt.Dimension(21, 20));
        btn17.setMinimumSize(new java.awt.Dimension(21, 20));
        btn17.setName("btnField17"); // NOI18N
        btn17.setPreferredSize(new java.awt.Dimension(21, 20));
        btn17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorFromLoop(evt);
            }
        });

        jLabel1.setFont(frames.UGui.getFont(0,0));
        jLabel1.setText("Замок");
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jLabel1.setMaximumSize(new java.awt.Dimension(80, 18));
        jLabel1.setMinimumSize(new java.awt.Dimension(80, 18));
        jLabel1.setPreferredSize(new java.awt.Dimension(80, 18));

        txt46.setEditable(false);
        txt46.setFont(frames.UGui.getFont(0,0));
        txt46.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt46.setPreferredSize(new java.awt.Dimension(178, 18));

        btn23.setText("...");
        btn23.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn23.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn23.setMaximumSize(new java.awt.Dimension(21, 20));
        btn23.setMinimumSize(new java.awt.Dimension(21, 20));
        btn23.setName("btnField17"); // NOI18N
        btn23.setPreferredSize(new java.awt.Dimension(21, 20));
        btn23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockToStvorka(evt);
            }
        });

        lab63.setFont(frames.UGui.getFont(0,0));
        lab63.setText("Текстура");
        lab63.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab63.setMaximumSize(new java.awt.Dimension(80, 18));
        lab63.setMinimumSize(new java.awt.Dimension(80, 18));
        lab63.setPreferredSize(new java.awt.Dimension(80, 18));

        txt48.setEditable(false);
        txt48.setFont(frames.UGui.getFont(0,0));
        txt48.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt48.setPreferredSize(new java.awt.Dimension(178, 18));

        btn24.setText("...");
        btn24.setToolTipText(bundle.getString("Выбрать")); // NOI18N
        btn24.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn24.setMaximumSize(new java.awt.Dimension(21, 20));
        btn24.setMinimumSize(new java.awt.Dimension(21, 20));
        btn24.setName("btnField17"); // NOI18N
        btn24.setPreferredSize(new java.awt.Dimension(21, 20));
        btn24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorFromLock(evt);
            }
        });

        txt57.setEditable(false);
        txt57.setFont(frames.UGui.getFont(0,0));
        txt57.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt57.setPreferredSize(new java.awt.Dimension(180, 18));

        lab51.setFont(frames.UGui.getFont(0,0));
        lab51.setText("Название");
        lab51.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab51.setMaximumSize(new java.awt.Dimension(80, 18));
        lab51.setMinimumSize(new java.awt.Dimension(80, 18));
        lab51.setPreferredSize(new java.awt.Dimension(80, 18));

        lab52.setFont(frames.UGui.getFont(0,0));
        lab52.setText("Название");
        lab52.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab52.setMaximumSize(new java.awt.Dimension(80, 18));
        lab52.setMinimumSize(new java.awt.Dimension(80, 18));
        lab52.setPreferredSize(new java.awt.Dimension(80, 18));

        txt58.setEditable(false);
        txt58.setFont(frames.UGui.getFont(0,0));
        txt58.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt58.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan23Layout = new javax.swing.GroupLayout(pan23);
        pan23.setLayout(pan23Layout);
        pan23Layout.setHorizontalGroup(
            pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txt45, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(txt47, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txt46, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab63, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txt48, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan23Layout.createSequentialGroup()
                        .addComponent(lab52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan23Layout.setVerticalGroup(
            pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lab63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(89, Short.MAX_VALUE))
        );

        tabb2.addTab("Дополн...", pan23);

        lab47.setFont(frames.UGui.getFont(0,0));
        lab47.setText("Моск. сетка");
        lab47.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab47.setMaximumSize(new java.awt.Dimension(80, 18));
        lab47.setMinimumSize(new java.awt.Dimension(80, 18));
        lab47.setPreferredSize(new java.awt.Dimension(80, 18));

        txt54.setEditable(false);
        txt54.setFont(frames.UGui.getFont(0,0));
        txt54.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt54.setPreferredSize(new java.awt.Dimension(180, 18));

        btn16.setText("...");
        btn16.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn16.setMaximumSize(new java.awt.Dimension(21, 20));
        btn16.setMinimumSize(new java.awt.Dimension(21, 20));
        btn16.setName("btnField17"); // NOI18N
        btn16.setPreferredSize(new java.awt.Dimension(21, 20));
        btn16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mosquitToStvorka(evt);
            }
        });

        lab53.setFont(frames.UGui.getFont(0,0));
        lab53.setText("Название");
        lab53.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab53.setMaximumSize(new java.awt.Dimension(80, 18));
        lab53.setMinimumSize(new java.awt.Dimension(80, 18));
        lab53.setPreferredSize(new java.awt.Dimension(80, 18));

        txt55.setEditable(false);
        txt55.setFont(frames.UGui.getFont(0,0));
        txt55.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt55.setPreferredSize(new java.awt.Dimension(212, 18));

        lab66.setFont(frames.UGui.getFont(0,0));
        lab66.setText("Текстура");
        lab66.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab66.setMaximumSize(new java.awt.Dimension(80, 18));
        lab66.setMinimumSize(new java.awt.Dimension(80, 18));
        lab66.setPreferredSize(new java.awt.Dimension(80, 18));

        txt60.setEditable(false);
        txt60.setFont(frames.UGui.getFont(0,0));
        txt60.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt60.setPreferredSize(new java.awt.Dimension(180, 18));

        btn32.setText("...");
        btn32.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn32.setMaximumSize(new java.awt.Dimension(21, 20));
        btn32.setMinimumSize(new java.awt.Dimension(21, 20));
        btn32.setName("btnField17"); // NOI18N
        btn32.setPreferredSize(new java.awt.Dimension(21, 20));
        btn32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mosqToColor(evt);
            }
        });

        lab62.setFont(frames.UGui.getFont(0,0));
        lab62.setText("Состав");
        lab62.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab62.setMaximumSize(new java.awt.Dimension(80, 18));
        lab62.setMinimumSize(new java.awt.Dimension(80, 18));
        lab62.setPreferredSize(new java.awt.Dimension(80, 18));

        txt56.setEditable(false);
        txt56.setFont(frames.UGui.getFont(0,0));
        txt56.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt56.setPreferredSize(new java.awt.Dimension(180, 18));

        btn31.setText("...");
        btn31.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btn31.setMaximumSize(new java.awt.Dimension(21, 20));
        btn31.setMinimumSize(new java.awt.Dimension(21, 20));
        btn31.setName("btnField17"); // NOI18N
        btn31.setPreferredSize(new java.awt.Dimension(21, 20));
        btn31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mosqToElements(evt);
            }
        });

        javax.swing.GroupLayout pan24Layout = new javax.swing.GroupLayout(pan24);
        pan24.setLayout(pan24Layout);
        pan24Layout.setHorizontalGroup(
            pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
            .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pan24Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pan24Layout.createSequentialGroup()
                            .addComponent(lab47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt54, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pan24Layout.createSequentialGroup()
                            .addComponent(lab62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt56, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pan24Layout.createSequentialGroup()
                            .addComponent(lab53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt55, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan24Layout.createSequentialGroup()
                            .addComponent(lab66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt60, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btn32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap()))
        );
        pan24Layout.setVerticalGroup(
            pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 238, Short.MAX_VALUE)
            .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(pan24Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(lab47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lab53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lab66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(pan24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txt56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lab62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(134, Short.MAX_VALUE)))
        );

        tabb2.addTab("Маскитка", pan24);

        pan16.add(tabb2, java.awt.BorderLayout.CENTER);

        pan8.add(pan16, "card16");

        pan17.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Соединения", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, frames.UGui.getFont(0, 1)));
        pan17.setPreferredSize(new java.awt.Dimension(300, 200));

        lab49.setFont(frames.UGui.getFont(0,0));
        lab49.setText("1  соединение");
        lab49.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab49.setPreferredSize(new java.awt.Dimension(80, 18));

        lab50.setFont(frames.UGui.getFont(0,0));
        lab50.setText("2  соединение");
        lab50.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab50.setPreferredSize(new java.awt.Dimension(80, 18));

        txt36.setEditable(false);
        txt36.setFont(frames.UGui.getFont(0,0));
        txt36.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt36.setPreferredSize(new java.awt.Dimension(180, 18));

        txt37.setEditable(false);
        txt37.setFont(frames.UGui.getFont(0,0));
        txt37.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt37.setPreferredSize(new java.awt.Dimension(180, 18));

        lab55.setFont(frames.UGui.getFont(0,0));
        lab55.setText("Вариант");
        lab55.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab55.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lab55.setIconTextGap(6);
        lab55.setMaximumSize(new java.awt.Dimension(80, 19));
        lab55.setMinimumSize(new java.awt.Dimension(80, 19));
        lab55.setPreferredSize(new java.awt.Dimension(80, 19));

        txt38.setEditable(false);
        txt38.setFont(frames.UGui.getFont(0,0));
        txt38.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt38.setPreferredSize(new java.awt.Dimension(180, 18));

        lab56.setFont(frames.UGui.getFont(0,0));
        lab56.setText("Вариант");
        lab56.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab56.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lab56.setIconTextGap(6);
        lab56.setMaximumSize(new java.awt.Dimension(80, 19));
        lab56.setMinimumSize(new java.awt.Dimension(80, 19));
        lab56.setPreferredSize(new java.awt.Dimension(80, 19));

        txt39.setEditable(false);
        txt39.setFont(frames.UGui.getFont(0,0));
        txt39.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt39.setPreferredSize(new java.awt.Dimension(180, 18));

        lab54.setFont(frames.UGui.getFont(0,0));
        lab54.setText("3  соединение");
        lab54.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab54.setPreferredSize(new java.awt.Dimension(80, 18));

        txt40.setEditable(false);
        txt40.setFont(frames.UGui.getFont(0,0));
        txt40.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt40.setPreferredSize(new java.awt.Dimension(180, 18));

        lab57.setFont(frames.UGui.getFont(0,0));
        lab57.setText("Вариант");
        lab57.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab57.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lab57.setIconTextGap(6);
        lab57.setMaximumSize(new java.awt.Dimension(80, 19));
        lab57.setMinimumSize(new java.awt.Dimension(80, 19));
        lab57.setPreferredSize(new java.awt.Dimension(80, 19));

        txt41.setEditable(false);
        txt41.setFont(frames.UGui.getFont(0,0));
        txt41.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt41.setPreferredSize(new java.awt.Dimension(180, 18));

        lab58.setFont(frames.UGui.getFont(0,0));
        lab58.setText("Артикул 1,2");
        lab58.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab58.setIconTextGap(1);
        lab58.setPreferredSize(new java.awt.Dimension(80, 18));

        txt42.setEditable(false);
        txt42.setFont(frames.UGui.getFont(0,0));
        txt42.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt42.setPreferredSize(new java.awt.Dimension(180, 18));

        lab59.setFont(frames.UGui.getFont(0,0));
        lab59.setText("Артикул 1,2");
        lab59.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab59.setIconTextGap(1);
        lab59.setPreferredSize(new java.awt.Dimension(80, 18));

        txt43.setEditable(false);
        txt43.setFont(frames.UGui.getFont(0,0));
        txt43.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt43.setPreferredSize(new java.awt.Dimension(180, 18));

        lab60.setFont(frames.UGui.getFont(0,0));
        lab60.setText("Артикул 1,2");
        lab60.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lab60.setIconTextGap(1);
        lab60.setPreferredSize(new java.awt.Dimension(80, 18));

        txt44.setEditable(false);
        txt44.setFont(frames.UGui.getFont(0,0));
        txt44.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        txt44.setPreferredSize(new java.awt.Dimension(180, 18));

        javax.swing.GroupLayout pan17Layout = new javax.swing.GroupLayout(pan17);
        pan17.setLayout(pan17Layout);
        pan17Layout.setHorizontalGroup(
            pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt42, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt43, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt44, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                    .addGroup(pan17Layout.createSequentialGroup()
                        .addComponent(lab54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pan17Layout.createSequentialGroup()
                        .addComponent(lab50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pan17Layout.setVerticalGroup(
            pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pan17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pan17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lab57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        pan8.add(pan17, "card17");

        javax.swing.GroupLayout pan18Layout = new javax.swing.GroupLayout(pan18);
        pan18.setLayout(pan18Layout);
        pan18Layout.setHorizontalGroup(
            pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        pan18Layout.setVerticalGroup(
            pan18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 292, Short.MAX_VALUE)
        );

        pan8.add(pan18, "card18");

        pan5.add(pan8, java.awt.BorderLayout.NORTH);

        scr6.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        scr6.setPreferredSize(new java.awt.Dimension(4, 260));

        winTree.setFont(frames.UGui.getFont(0,0));
        scr6.setViewportView(winTree);

        pan5.add(scr6, java.awt.BorderLayout.CENTER);

        pan3.add(pan5, java.awt.BorderLayout.EAST);

        panDesign.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        panDesign.setLayout(new java.awt.BorderLayout());
        pan3.add(panDesign, java.awt.BorderLayout.CENTER);

        tabb1.addTab("        Изделия        ", pan3);

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

    private void btnRefresh(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefresh
        DefMutableTreeNode selectNode = (DefMutableTreeNode) winTree.getLastSelectedPathComponent();
        double id = (selectNode != null) ? selectNode.com5t().id() : -1;

        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        Query.listOpenTable.forEach(q -> q.clear());
        int index1 = UGui.getIndexRec(tab1);
        int index2 = UGui.getIndexRec(tab2);
        loadingData();

        UGui.setSelectedIndex(tab1, index1);
        UGui.setSelectedIndex(tab2, index2);
        UGui.selectionPathWin(id, winTree);
    }//GEN-LAST:event_btnRefresh

    private void btnDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        if (tab1.getBorder() != null) {
            if (UGui.isDeleteRecord(tab1, this, tab2) == 0) {
                UGui.deleteRecord(tab1);
            }
        } else if (tab2.getBorder() != null) {
            if (UGui.isDeleteRecord(tab2, this, tab4) == 0) {
                UGui.deleteRecord(tab2);
            }
        } else if (tab4.getBorder() != null) {
            if (UGui.isDeleteRecord(tab4, this) == 0) {
                UGui.deleteRecord(tab4);
            }
        }
    }//GEN-LAST:event_btnDelete

    private void btnInsert(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());

        if (tab1.getBorder() != null) {
            UGui.insertRecordCur(tab1, eProject.up, (projectRec) -> {
                projectRec.set(eProject.manager, eProfile.user);
                projectRec.set(eProject.date4, UGui.getDateCur());
            });

        } else if (tab2.getBorder() != null) {
            new DicSyspod(this, (record) -> {
                UGui.insertRecordCur(tab2, eProject.up, (record2) -> {
                    record2.set(ePrjprod.id, Conn.genId(ePrjprod.up));
                    record2.set(ePrjprod.name, record.getStr(eSysprod.name));
                    record2.set(ePrjprod.num, 1);
                    record2.set(ePrjprod.script, record.getStr(eSysprod.script));
                    record2.set(ePrjprod.systree_id, record.getStr(eSysprod.systree_id));
                    record2.set(ePrjprod.project_id, qProject.getAs(UGui.getIndexRec(tab1), eProject.id));
                });
            });
            Object ID = qPrjprod.get(UGui.getIndexRec(tab2), ePrjprod.id);
            loadingTab2();
            for (int index = 0; index < qPrjprod.size(); ++index) {
                if (qPrjprod.get(index, ePrjprod.id) == ID) {
                    UGui.setSelectedIndex(tab2, index);
                    UGui.scrollRectToRow(index, tab2);
                    winTree.setSelectionRow(0);
                }
            }
        } else if (tab4.getBorder() != null) {
            int index = UGui.getIndexRec(tab1);
            int index2 = UGui.getIndexRec(tab2);
            if (index != -1) {
                if (((JButton) evt.getSource()) == btnIns) {
                    UGui.insertRecordCur(tab4, ePrjkit.up, (record) -> {
                        if (index2 != -1) {
                            record.set(ePrjkit.prjprod_id, qPrjprod.get(index2, ePrjprod.id));
                        }
                        record.set(ePrjkit.project_id, qProject.get(index, eProject.id));
                        int index3 = UGui.getIndexKeyValue(tab2, record, ePrjkit.id);
                        qPrjkit.table(eArtikl.up).add(index3, eArtikl.up.newRecord());
                    });
                } else if (((JButton) evt.getSource()) == btnSet) {
                    DicKits frame = new DicKits(Orders.this, (q) -> {
                        loadingTab4();
                        return true;
                    }, qProject.getAs(index, eProject.id), qPrjprod.getAs(index2, ePrjprod.id));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Заказ не выбран.", "Предупреждение", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnInsert

    private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        eProp.save(); //запишем текущий ordersId в файл
        List.of(qProject, qPrjprod, qPrjkit).forEach(q -> q.execsql());
    }//GEN-LAST:event_windowClosed

    private void mousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mousePressed
        JTable table = (JTable) evt.getSource();
        UGui.updateBorderAndSql(table, List.of(tab1, tab2, tab4, tab3));
    }//GEN-LAST:event_mousePressed

    private void stateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stateChanged
        UGui.stopCellEditing(tab1, tab2, tab4, tab3);
        if (tabb1.getSelectedIndex() == 0) {
            spinner.setEnabled(false);
            UGui.updateBorderAndSql(tab1, List.of(tab1, tab2, tab4, tab3));
        } else if (tabb1.getSelectedIndex() == 1) {
            spinner.setEnabled(true);
            UGui.updateBorderAndSql(tab2, List.of(tab1, tab2, tab4, tab3));
        } else if (tabb1.getSelectedIndex() == 2) {
            spinner.setEnabled(false);
            UGui.updateBorderAndSql(tab4, List.of(tab1, tab2, tab4, tab3));
        }
    }//GEN-LAST:event_stateChanged

    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        DicCurrenc frame = new DicCurrenc(this, (currencRec) -> {

            int index = UGui.getIndexRec(tab1);
            if (index != -1) {
                Record record2 = qProject.get(index);
                record2.set(eProject.currenc_id, currencRec.get(eCurrenc.id));
                txt3.setText(currencRec.getStr(eCurrenc.name));
            }
            UGui.stopCellEditing(tab1, tab2, tab4, tab3);
        });
    }//GEN-LAST:event_btn1ActionPerformed

    private void colorToWindows(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToWindows
        try {
            if (winNode != null) {
                double selectID = winNode.com5t().id();
                HashSet<Record> colorSet = new HashSet();
                int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);
                Record systreeRec = eSystree.find(systreeID);
                eSystree col = (evt.getSource() == btn9) ? eSystree.col1 : (evt.getSource() == btn13) ? eSystree.col2 : eSystree.col3;
                Integer[] arr2 = UCom.parserInt(systreeRec.getStr(col));
                if (arr2.length != 0) {
                    for (Record rec : eColor.query()) {
                        for (int i = 0; i < arr2.length; i = i + 2) { //тестуры
                            if (rec.getInt(eColor.code) >= arr2[i] && rec.getInt(eColor.code) <= arr2[i + 1]) {
                                colorSet.add(rec);
                            }
                        }
                    }
                }

                ListenerRecord listenerColor = (colorRec) -> {

                    if (colorRec.get(1) != null) {
                        GsonElem jsonElem2 = winc().listAll.gson(selectID);
                        GsonElem jsonElem = winc().listAll.gson(selectID);
                        if (jsonElem != null) {
                            if (evt.getSource() == btn9) {
                                winc().rootGson.color1(colorRec.getInt(eColor.id));
                            } else if (evt.getSource() == btn13) {
                                winc().rootGson.color2(colorRec.getInt(eColor.id));
                            } else {
                                winc().rootGson.color3(colorRec.getInt(eColor.id));
                            }
                            updateScript(selectID);
                        }
                    }
                };
                if (arr2.length == 0) {
                    new DicColor(this, listenerColor, false, false);
                } else {
                    new DicColor(this, listenerColor, colorSet, false, false);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_colorToWindows

    private void sysprofToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sysprofToFrame
        try {
            if (winNode != null) {
                Layout layout = winNode.com5t().layout();
                double selectID = winNode.com5t().id();
                int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);
                Query qSysprof = new Query(eSysprof.values(), eArtikl.values()).select(eSysprof.up, "left join",
                        eArtikl.up, "on", eArtikl.id, "=", eSysprof.artikl_id, "where", eSysprof.systree_id, "=", systreeID);
                Query qSysprof2 = new Query(eSysprof.values(), eArtikl.values());

                //Отфильтруем подходящие по параметрам
                for (int index = 0; index < qSysprof.size(); ++index) {
                    Record sysprofRec = qSysprof.get(index);
                    if (winNode.com5t().type().id2 == sysprofRec.getInt(eSysprof.use_type)) {
                        if (sysprofRec.getInt(eSysprof.use_side) == winNode.com5t().layout().id
                                || sysprofRec.getInt(eSysprof.use_side) == UseSide.ANY.id
                                || sysprofRec.getInt(eSysprof.use_side) == UseSide.MANUAL.id) {
                            qSysprof2.add(sysprofRec);
                            qSysprof2.table(eArtikl.up).add(qSysprof.table(eArtikl.up).get(index));
                        }
                    }
                }
                new DicSysprof(this, (sysprofRec) -> {
                    Wincalc winc = winc();
                    if (winNode.com5t().type() == enums.Type.FRAME_SIDE) { //рама окна
                        double gsonId = winNode.com5t().id();
                        GsonElem gsonRama = winc().listAll.gson(gsonId);
                        if (sysprofRec.get(1) == null) {
                            gsonRama.param().remove(PKjson.sysprofID);
                        } else {
                            gsonRama.param().addProperty(PKjson.sysprofID, sysprofRec.getInt(eSysprof.id));
                        }
                        updateScript(selectID);

                    } else if (winNode.com5t().type() == enums.Type.STVORKA_SIDE) { //рама створки
                        double stvId = winNode.com5t().owner().id();
                        GsonElem stvArea = (GsonElem) winc.listAll.gson(stvId);
                        JsonObject paramObj = stvArea.param();
                        String stvKey = null;
                        if (layout == Layout.BOTT) {
                            stvKey = PKjson.stvorkaBottom;
                        } else if (layout == Layout.RIGHT) {
                            stvKey = PKjson.stvorkaRight;
                        } else if (layout == Layout.TOP) {
                            stvKey = PKjson.stvorkaTop;
                        } else if (layout == Layout.LEFT) {
                            stvKey = PKjson.stvorkaLeft;
                        }
                        JsonObject jso = UGui.getAsJsonObject(paramObj, stvKey);
                        if (sysprofRec.get(1) == null) {
                            jso.remove(PKjson.sysprofID);
                        } else {
                            jso.addProperty(PKjson.sysprofID, sysprofRec.getStr(eSysprof.id));
                        }
                        updateScript(selectID);

                    } else {  //импост
                        double elemId = winNode.com5t().id();
                        GsonElem gsonElem = winc.listAll.gson(elemId);
                        if (sysprofRec.get(1) == null) {
                            gsonElem.param().remove(PKjson.sysprofID);
                        } else {
                            gsonElem.param().addProperty(PKjson.sysprofID, sysprofRec.getInt(eSysprof.id));
                        }
                        updateScript(selectID);
                    }

                }, qSysprof2);
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_sysprofToFrame

    private void colorToFrame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToFrame
        try {
            double selectID = winNode.com5t().id();
            HashSet<Record> colorSet = new HashSet();
            Query artdetList = new Query(eArtdet.values()).select(eArtdet.up, "where", eArtdet.artikl_id, "=", winNode.com5t().artiklRec().getInt(eArtikl.id));
            artdetList.forEach(rec -> {

                if (rec.getInt(eArtdet.color_fk) < 0) {
                    eColor.query().forEach(rec2 -> {
                        if (rec2.getInt(eColor.groups_id) == rec.getInt(eArtdet.color_fk)) {
                            colorSet.add(rec2);
                        }
                    });
                } else {
                    colorSet.add(eColor.find(rec.getInt(eArtdet.color_fk)));
                }
            });
            DicColor frame = new DicColor(this, (colorRec) -> {

                String colorID = (evt.getSource() == btn18) ? PKjson.colorID1 : (evt.getSource() == btn19) ? PKjson.colorID2 : PKjson.colorID3;
                double parentId = winNode.com5t().owner().id();
                GsonElem parentArea = (GsonElem) winc().listAll.gson(parentId);

                if (winNode.com5t().type() == enums.Type.STVORKA_SIDE) {
                    JsonObject paramObj = parentArea.param();
                    String stvKey = null;
                    if (winNode.com5t().layout() == Layout.BOTT) {
                        stvKey = PKjson.stvorkaBottom;
                    } else if (winNode.com5t().layout() == Layout.RIGHT) {
                        stvKey = PKjson.stvorkaRight;
                    } else if (winNode.com5t().layout() == Layout.TOP) {
                        stvKey = PKjson.stvorkaTop;
                    } else if (winNode.com5t().layout() == Layout.LEFT) {
                        stvKey = PKjson.stvorkaLeft;
                    }

                    JsonObject jso = UGui.getAsJsonObject(paramObj, stvKey);
                    if (colorRec.get(1) == null) {
                        jso.remove(colorID);
                    } else {
                        jso.addProperty(colorID, colorRec.getStr(eColor.id));
                    }
                    updateScript(selectID);

                } else if (winNode.com5t().type() == enums.Type.FRAME_SIDE) {
                    for (GsonElem elem : parentArea.childs()) {
                        if (elem.id() == ((DefMutableTreeNode) winNode).com5t().id()) {
                            if (colorRec.get(1) == null) {
                                elem.param().remove(colorID);
                            } else {
                                elem.param().addProperty(colorID, colorRec.getStr(eColor.id));
                            }
                            updateScript(selectID);
                        }
                    }
                } else if (winNode.com5t().type() == enums.Type.IMPOST
                        || winNode.com5t().type() == enums.Type.STOIKA
                        || winNode.com5t().type() == enums.Type.SHTULP) {
                    for (GsonElem elem : parentArea.childs()) {
                        if (elem.id() == ((DefMutableTreeNode) winNode).com5t().id()) {
                            if (colorRec.get(1) == null) {
                                elem.param().remove(colorID);
                            } else {
                                elem.param().addProperty(colorID, colorRec.getStr(eColor.id));
                            }
                            updateScript(selectID);
                        }
                    }
                }

            }, colorSet, true, false);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_colorToFrame

    private void sysfurnToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sysfurnToStvorka
        try {
            double windowsID = winNode.com5t().id();
            int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);

            Query qSysfurn = new Query(eSysfurn.values(), eFurniture.values()).select(eSysfurn.up, "left join", eFurniture.up, "on",
                    eSysfurn.furniture_id, "=", eFurniture.id, "where", eSysfurn.systree_id, "=", systreeID);

            new DicName(this, (sysfurnRec) -> {

                GsonElem stvArea = (GsonElem) winc().listAll.gson(windowsID);
                if (sysfurnRec.get(1) == null) {
                    stvArea.param().remove(PKjson.sysfurnID);
                } else {
                    stvArea.param().addProperty(PKjson.sysfurnID, sysfurnRec.getStr(eSysfurn.id));
                }
                updateScript(windowsID);

            }, qSysfurn, eFurniture.name);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_sysfurnToStvorka

    private void typeOpenToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeOpenToStvorka
        try {
            new DicEnums(this, (typeopenRec) -> {

                double elemID = winNode.com5t().id();
                GsonElem stvArea = (GsonElem) winc().listAll.gson(elemID);
                if (typeopenRec.get(1) == null) {
                    stvArea.param().remove(PKjson.typeOpen);
                } else {
                    stvArea.param().addProperty(PKjson.typeOpen, typeopenRec.getInt(0));
                }
                updateScript(elemID);

            }, TypeOpen1.LEFT, TypeOpen1.LEFTUP, TypeOpen1.LEFTMOV,
                    TypeOpen1.RIGHT, TypeOpen1.RIGHTUP, TypeOpen1.RIGHTMOV, TypeOpen1.UPPER, TypeOpen1.FIXED);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_typeOpenToStvorka

    private void colorToHandl(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToHandl
        try {
            double selectID = winNode.com5t().id();
            IStvorka stv = (IStvorka) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(stv.handleRec().getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = (GsonElem) winc().listAll.gson(selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param().remove(PKjson.colorHandl);
                } else {
                    stvArea.param().addProperty(PKjson.colorHandl, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_colorToHandl

    private void handkToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handkToStvorka
        try {
            double stvorkaID = winNode.com5t().id();
            int furnitureID = ((IStvorka) winNode.com5t()).sysfurnRec().getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up, "where", eArtikl.level1, "= 2 and", eArtikl.level2, " = 11");
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = (GsonElem) winc().listAll.gson(stvorkaID);
                stvArea.param().remove(PKjson.colorHandl);
                if (artiklRec.get(1) == null) {
                    stvArea.param().remove(PKjson.artiklHandl);
                } else {
                    stvArea.param().addProperty(PKjson.artiklHandl, artiklRec.getStr(eArtikl.id));
                }
                updateScript(stvorkaID);

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_handkToStvorka

    private void heightHandlToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightHandlToStvorka

        IArea5e areaStv = (IArea5e) winNode.com5t();
        int indexLayoutHandl = 0;
        if (LayoutHandle.CONST.name.equals(txt16.getText())) {
            indexLayoutHandl = 1;
        } else if (LayoutHandle.VARIAT.name.equals(txt16.getText())) {
            indexLayoutHandl = 2;
        }
        new DicHandl(this, (record) -> {
            try {
                double selectID = areaStv.id();
                GsonElem jsonStv = (GsonElem) winc().listAll.gson(selectID);

                if (record.getInt(0) == 0) {
                    jsonStv.param().addProperty(PKjson.positionHandl, LayoutHandle.MIDL.id);
                    txt31.setEditable(false);

                } else if (record.getInt(0) == 1) {
                    jsonStv.param().addProperty(PKjson.positionHandl, LayoutHandle.CONST.id);
                    txt31.setEditable(false);

                } else if (record.getInt(0) == 2) {
                    jsonStv.param().addProperty(PKjson.positionHandl, LayoutHandle.VARIAT.id);
                    jsonStv.param().addProperty(PKjson.heightHandl, record.getInt(1));
                    txt31.setEditable(true);
                }
                updateScript(selectID);

            } catch (Exception e) {
                System.err.println("Ошибка: " + e);
            }

        }, indexLayoutHandl);
    }//GEN-LAST:event_heightHandlToStvorka

    private void btnToArtiklGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToArtiklGlass
        try {
            double selectID = winNode.com5t().id();
            int systreeID = qPrjprod.getAs(UGui.getIndexRec(tab2), ePrjprod.systree_id);
            Record systreeRec = eSystree.find(systreeID);
            String depth = systreeRec.getStr(eSystree.depth);
            if (depth != null && depth.isEmpty() == false) {
                depth = depth.replace(";;;", ";").replace(";;", ";");
                depth = depth.replace(";", ",");
                if (depth.charAt(depth.length() - 1) == ',') {
                    depth = depth.substring(0, depth.length() - 1);
                }
            }
            depth = (depth != null && depth.isEmpty() == false) ? " and " + eArtikl.depth.name() + " in (" + depth + ")" : "";
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up, "where",
                    eArtikl.level1, "= 5 and", eArtikl.level2, "in (1,2,3)", depth);

            new DicArtikl(this, (artiklRec) -> {

                GsonElem glassElem = (GsonElem) winc().listAll.gson(selectID);
                if (artiklRec.get(1) == null) {
                    glassElem.param().remove(PKjson.artglasID);
                } else {
                    glassElem.param().addProperty(PKjson.artglasID, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_btnToArtiklGlass

    private void btnCalc(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalc
        UGui.stopCellEditing(tab1, tab2, tab3, tab4);
        List.of(tab1, tab2, tab3, tab4).forEach(tab -> ((DefTableModel) tab.getModel()).getQuery().execsql());
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                try {
                    Record projectRec = qProject.get(UGui.getIndexRec(tab1));
                    Record currencRec = qCurrenc.stream().filter(rec -> rec.get(eCurrenc.id).equals(projectRec.get(eProject.currenc_id))).findFirst().orElse(eCurrenc.up.newRecord());

                    projectRec.set(eProject.weight, 0);
                    projectRec.set(eProject.square, 0);
                    projectRec.set(eProject.price2, 0);
                    projectRec.set(eProject.cost2, 0);
                    projectRec.set(eProject.price3, 0);
                    projectRec.set(eProject.cost3, 0);
                    projectRec.set(eProject.price4, 0);
                    projectRec.set(eProject.cost4, 0);

                    //Пересчёт заказа
                    if (UGui.getIndexRec(tab1) != -1) {

                        //Цикл по конструкциям
                        for (Record prjprodRec : qPrjprod) {
                            Object w = prjprodRec.get(ePrjprod.values().length);
                            if (w instanceof Wincalc) {

                                Wincalc win = (Wincalc) w;
                                String script = prjprodRec.getStr(ePrjprod.script);
                                JsonElement jsonElem = new Gson().fromJson(script, JsonElement.class);

                                win.build(jsonElem.toString()); //калкуляция
                                Query.listOpenTable.forEach(q -> q.clear()); //очистим кэш                                
                                win.constructiv(true); //конструктив                                                               

                                //Площадь изделий
                                double square = prjprodRec.getDbl(ePrjprod.num, 1) * win.width() * win.height();
                                projectRec.set(eProject.square, projectRec.getDbl(eProject.square) + square);

                                //Суммируем коонструкции заказа
                                projectRec.set(eProject.weight, projectRec.getDbl(eProject.weight) + win.weight()); //вес изделий
                                projectRec.set(eProject.price2, projectRec.getDbl(eProject.price2) + win.price()); //стоимость без скидки
                                projectRec.set(eProject.cost2, projectRec.getDbl(eProject.cost2) + win.cost2()); //стоимость со скидками

                                //Комплектация
                                ArraySpc<Specific> kitList = Tariffic.kits(prjprodRec, win, true); //комплекты
                                for (Specific spc : kitList) {
                                    projectRec.set(eProject.price3, projectRec.getDbl(eProject.price3) + spc.price * spc.count);
                                    projectRec.set(eProject.cost3, projectRec.getDbl(eProject.cost3) + spc.cost2 * spc.count);
                                }
                            }
                        }
                        double cost2 = projectRec.getDbl(eProject.cost2) - projectRec.getDbl(eProject.cost2) * projectRec.getDbl(eProject.disc2) / 100;
                        projectRec.set(eProject.cost2, cost2); //стоимость проекта со скидкой менеджера

                        double cost3 = projectRec.getDbl(eProject.cost3) - projectRec.getDbl(eProject.cost3) * projectRec.getDbl(eProject.disc3) / 100;
                        projectRec.set(eProject.cost3, cost3); //стоимость проекта комплектации со скидкой менеджера

                        projectRec.set(eProject.price4, projectRec.getDbl(eProject.price2) + projectRec.getDbl(eProject.price3)); //стоимость проекта без скидок

                        double cost4 = (projectRec.getDbl(eProject.cost2) + projectRec.getDbl(eProject.cost3))
                                - (projectRec.getDbl(eProject.cost2) + projectRec.getDbl(eProject.cost3)) * projectRec.getDbl(eProject.disc4) / 100;
                        projectRec.set(eProject.cost4, cost4); //проекта со скидками менеджера

                        //Вес, площадь
                        txt7.setText(UCom.format(projectRec.getDbl(eProject.weight) / 1000, 1)); //вес
                        txt8.setText(UCom.format(projectRec.getDbl(eProject.square) / 1000000, 1)); //площадь

                        //Стоимость
                        tab5.setValueAt(projectRec.getDbl(eProject.price2), 0, 2); //стоимость конструкций без скидки
                        tab5.setValueAt(projectRec.getDbl(eProject.cost2), 0, 3); //стоимость конструкций со скидкой
                        tab5.setValueAt(projectRec.getDbl(eProject.price3), 1, 2); //стоимость комплектации без скидки
                        tab5.setValueAt(projectRec.getDbl(eProject.cost3), 1, 3); //стоимость комплектации со скидкой
                        //Итого
                        tab5.setValueAt(projectRec.getDbl(eProject.price4), 2, 2); //итого стоимость без скидки
                        tab5.setValueAt(projectRec.getDbl(eProject.cost4), 2, 3); //итого стоимость со скидкой
                    }

                } catch (Exception e) {
                    System.err.println("Ошибка:Orders.btnCalc() " + e);
                }
            }
        });
    }//GEN-LAST:event_btnCalc

    private void btnFilter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilter
        loadingTab1();
    }//GEN-LAST:event_btnFilter

    private void btnTest(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTest

    }//GEN-LAST:event_btnTest

    private void loopToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopToStvorka
        try {
            double selectID = winNode.com5t().id();
            int furnitureID = ((IStvorka) winNode.com5t()).sysfurnRec().getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up, "where", eArtikl.level1, "= 2 and", eArtikl.level2, " = 12");
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = (GsonElem) winc().listAll.gson(selectID);
                stvArea.param().remove(PKjson.colorLoop);
                if (artiklRec.get(1) == null) {
                    stvArea.param().remove(PKjson.artiklLoop);
                } else {
                    stvArea.param().addProperty(PKjson.artiklLoop, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_loopToStvorka

    private void colorFromLoop(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFromLoop
        try {
            double selectID = winNode.com5t().id();
            IStvorka stv = (IStvorka) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(stv.loopRec().getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = (GsonElem) winc().listAll.gson(selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param().remove(PKjson.colorLoop);
                } else {
                    stvArea.param().addProperty(PKjson.colorLoop, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_colorFromLoop

    private void lockToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockToStvorka
        try {
            double selectID = winNode.com5t().id();
            int furnitureID = ((IStvorka) winNode.com5t()).sysfurnRec().getInt(eSysfurn.furniture_id);
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up, "where", eArtikl.level1, "= 2 and", eArtikl.level2, " = 9");
            Query qResult = UGui.artTypeToFurndetList(furnitureID, qArtikl);
            new DicArtikl(this, (artiklRec) -> {

                GsonElem stvArea = (GsonElem) winc().listAll.gson(selectID);
                stvArea.param().remove(PKjson.colorLock);
                if (artiklRec.get(1) == null) {
                    stvArea.param().remove(PKjson.artiklLock);
                } else {
                    stvArea.param().addProperty(PKjson.artiklLock, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qResult);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e);
        }
    }//GEN-LAST:event_lockToStvorka

    private void colorFromLock(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFromLock
        try {
            double selectID = winNode.com5t().id();
            IStvorka stv = (IStvorka) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(stv.lockRec().getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = (GsonElem) winc().listAll.gson(selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param().remove(PKjson.colorLock);
                } else {
                    stvArea.param().addProperty(PKjson.colorLock, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, true, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_colorFromLock

    private void txtKeyEnter(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKeyEnter

    }//GEN-LAST:event_txtKeyEnter

    private void btnFind(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFind
        if (tab1.getBorder() != null) {
            Record record = ((DefTableModel) tab1.getModel()).getQuery().get(UGui.getIndexRec(tab1));
            if (record != null) {
                ProgressBar.create(this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        App.Partner.createFrame(Orders.this, record.getInt(eProject.prjpart_id));
                    }
                });
            }
        } else if (tab4.getBorder() != null) {
            Record record = ((DefTableModel) tab4.getModel()).getQuery().get(UGui.getIndexRec(tab4));
            if (record != null) {
                Record record2 = eArtikl.find(record.getInt(ePrjkit.artikl_id), false);
                ProgressBar.create(this, new ListenerFrame() {
                    public void actionRequest(Object obj) {
                        App.Artikles.createFrame(Orders.this, record2);
                    }
                });
            }
        }
    }//GEN-LAST:event_btnFind

    private void colorFromGlass(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorFromGlass
        try {
            double selectID = winNode.com5t().id();
            IElem5e glas = (IElem5e) winNode.com5t();
            HashSet<Record> colorSet = UGui.artiklToColorSet(glas.artiklRec().getInt(eArtikl.id));
            DicColor frame = new DicColor(this, (colorRec) -> {

                GsonElem stvArea = (GsonElem) winc().listAll.gson(selectID);
                if (colorRec.get(1) == null) {
                    stvArea.param().remove(PKjson.colorGlass);
                } else {
                    stvArea.param().addProperty(PKjson.colorGlass, colorRec.getStr(eColor.id));
                }
                updateScript(selectID);

            }, colorSet, false, false);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorFromGlass() " + e);
        }
    }//GEN-LAST:event_colorFromGlass

    private void btnReport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReport
        ppReport.show(north, btnReport.getX(), btnReport.getY() + 18);
    }//GEN-LAST:event_btnReport

    private void menuItem11(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem11
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                //Отчёт
                HtmlOfMaterial.material(qProject.get(UGui.getIndexRec(tab1, 0)));
            }
        });
    }//GEN-LAST:event_menuItem11

    private void menuItem12(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem12
        ProgressBar.create(Orders.this, new ListenerFrame() {
            public void actionRequest(Object obj) {
                //Отчёт
                HtmlOfSpecific.specific(qProject.get(UGui.getIndexRec(tab1, 0)));
            }
        });
    }//GEN-LAST:event_menuItem12

    private void menuItem13(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem13
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Отчёт
                HtmlOfSmeta.smeta2(qProject.get(UGui.getIndexRec(tab1, 0)));
            }
        });
    }//GEN-LAST:event_menuItem13

    private void menuItem14(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem14
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Отчёт
                HtmlOfSmeta.smeta1(qProject.get(UGui.getIndexRec(tab1, 0)));
            }
        });
    }//GEN-LAST:event_menuItem14

    private void menuItem15(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem15
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Отчёт
                HtmlOfInvoice.invoice1(qProject.get(UGui.getIndexRec(tab1, 0)));
            }
        });
    }//GEN-LAST:event_menuItem15

    private void menuItem16(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem16
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Отчёт
                HtmlOfInvoice.invoice2(qProject.get(UGui.getIndexRec(tab1, 0)));
            }
        });
    }//GEN-LAST:event_menuItem16

    private void menuItem17(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem17
        ProgressBar.create(Orders.this, new ListenerFrame() {
            @Override
            public void actionRequest(Object obj) {
                //Отчёт
                HtmlOfOffer.offer(qProject.get(UGui.getIndexRec(tab1, 0)));
            }
        });
    }//GEN-LAST:event_menuItem17

    private void mosquitToStvorka(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosquitToStvorka
        try {
            IArea5e stvElem = (IArea5e) winNode.com5t();
            double selectID = winNode.com5t().id();
            Query qArtikl = new Query(eArtikl.values()).select(eArtikl.up,
                    "where", eArtikl.level1, "= 5 and", eArtikl.level2, "= 20");

            new DicArtikl(this, (artiklRec) -> {

                GsonElem gsonElem = null;
                LinkedCom<ICom5t> mosqList = ((IArea5e) stvElem).childs().filter(enums.Type.MOSKITKA);

                if (mosqList.isEmpty() == false) {
                    IElem5e mosqElem = (IElem5e) mosqList.get(0);
                    gsonElem = mosqElem.gson();
                } else {
                    gsonElem = new GsonElem(enums.Type.MOSKITKA);
                    GsonElem stvArea = stvElem.gson();
                    stvArea.childs().add(gsonElem);
                }
                if (artiklRec.get(1) == null) {
                    gsonElem.param().remove(PKjson.artiklID);
                    gsonElem.param().remove(PKjson.elementID);
                } else {
                    gsonElem.param().addProperty(PKjson.artiklID, artiklRec.getStr(eArtikl.id));
                }
                updateScript(selectID);

            }, qArtikl);

        } catch (Exception e) {
            System.err.println("Ошибка:Systree.mosquitToStvorka() " + e);
        }
    }//GEN-LAST:event_mosquitToStvorka

    private void mosqToColor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosqToColor
        try {
            double selectID = winNode.com5t().id();
            IStvorka stvElem = (IStvorka) winNode.com5t();
            LinkedCom<ICom5t> mosqList = ((IArea5e) stvElem).childs().filter(enums.Type.MOSKITKA);
            if (mosqList.isEmpty() == false) {
                ElemMosquit mosqElem = (ElemMosquit) mosqList.get(0);
                HashSet<Record> colorSet = UGui.artiklToColorSet(mosqElem.artiklRec().getInt(eArtikl.id));
                DicColor frame = new DicColor(this, (colorRec) -> {

                    if (colorRec.get(1) == null) {
                        mosqElem.gson().param().remove(PKjson.colorID1);
                    } else {
                        mosqElem.gson().param().addProperty(PKjson.colorID1, colorRec.getStr(eColor.id));
                    }
                    updateScript(selectID);

                }, colorSet, true, false);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.colorToHandl() " + e);
        }
    }//GEN-LAST:event_mosqToColor

    private void mosqToElements(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mosqToElements
        try {
            double selectID = winNode.com5t().id();
            IArea5e stvElem = (IArea5e) winNode.com5t();
            LinkedCom<ICom5t> mosqList = ((IArea5e) stvElem).childs().filter(enums.Type.MOSKITKA);
            if (mosqList.isEmpty() == false) {
                IElem5e mosqElem = (IElem5e) mosqList.get(0);
                Record artiklRec = mosqElem.artiklRec();
                Query qElements = new Query(eElement.values()).select(eElement.up,
                        "where", eElement.artikl_id, "=", artiklRec.getInt(eArtikl.id));

                new DicName(this, (elementRec) -> {

                    if (elementRec.get(1) == null) {
                        mosqElem.gson().param().remove(PKjson.elementID);
                    } else {
                        mosqElem.gson().param().addProperty(PKjson.elementID, elementRec.getStr(eElement.id));
                    }
                    updateScript(selectID);

                }, qElements, eElement.name);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:Systree.mosqToElements() " + e);
        }
    }//GEN-LAST:event_mosqToElements

    private void ppmActionItems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppmActionItems
        if (evt.getSource() == mInsert) {
            btnInsert(new java.awt.event.ActionEvent(btnIns, -1, ""));
        } else if (evt.getSource() == mDelit) {
            btnDelete(new java.awt.event.ActionEvent(btnDel, -1, ""));
        }
    }//GEN-LAST:event_ppmActionItems

    private void tabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            JTable table = List.of(tab1, tab4).stream().filter(it -> it == evt.getSource()).findFirst().get();
            List.of(tab1, tab4).forEach(tab -> tab.setBorder(null));
            table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
            ppmCrud.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tabMouseClicked

// <editor-fold defaultstate="collapsed" desc="Generated Code"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn10;
    private javax.swing.JButton btn12;
    private javax.swing.JButton btn13;
    private javax.swing.JButton btn14;
    private javax.swing.JButton btn15;
    private javax.swing.JButton btn16;
    private javax.swing.JButton btn17;
    private javax.swing.JButton btn18;
    private javax.swing.JButton btn19;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn20;
    private javax.swing.JButton btn21;
    private javax.swing.JButton btn22;
    private javax.swing.JButton btn23;
    private javax.swing.JButton btn24;
    private javax.swing.JButton btn25;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn31;
    private javax.swing.JButton btn32;
    private javax.swing.JButton btn6;
    private javax.swing.JButton btn9;
    private javax.swing.JButton btnCalc;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JToggleButton btnF1;
    private javax.swing.JToggleButton btnF2;
    private javax.swing.JToggleButton btnF3;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnIns;
    private javax.swing.JButton btnRef;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSet;
    private javax.swing.JButton btnTest;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JPanel centr;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lab1;
    private javax.swing.JLabel lab2;
    private javax.swing.JLabel lab26;
    private javax.swing.JLabel lab27;
    private javax.swing.JLabel lab28;
    private javax.swing.JLabel lab29;
    private javax.swing.JLabel lab3;
    private javax.swing.JLabel lab30;
    private javax.swing.JLabel lab31;
    private javax.swing.JLabel lab32;
    private javax.swing.JLabel lab33;
    private javax.swing.JLabel lab34;
    private javax.swing.JLabel lab35;
    private javax.swing.JLabel lab36;
    private javax.swing.JLabel lab37;
    private javax.swing.JLabel lab38;
    private javax.swing.JLabel lab39;
    private javax.swing.JLabel lab40;
    private javax.swing.JLabel lab41;
    private javax.swing.JLabel lab42;
    private javax.swing.JLabel lab43;
    private javax.swing.JLabel lab44;
    private javax.swing.JLabel lab45;
    private javax.swing.JLabel lab46;
    private javax.swing.JLabel lab47;
    private javax.swing.JLabel lab48;
    private javax.swing.JLabel lab49;
    private javax.swing.JLabel lab50;
    private javax.swing.JLabel lab51;
    private javax.swing.JLabel lab52;
    private javax.swing.JLabel lab53;
    private javax.swing.JLabel lab54;
    private javax.swing.JLabel lab55;
    private javax.swing.JLabel lab56;
    private javax.swing.JLabel lab57;
    private javax.swing.JLabel lab58;
    private javax.swing.JLabel lab59;
    private javax.swing.JLabel lab60;
    private javax.swing.JLabel lab61;
    private javax.swing.JLabel lab62;
    private javax.swing.JLabel lab63;
    private javax.swing.JLabel lab66;
    private javax.swing.JLabel lab69;
    private javax.swing.JLabel lab7;
    private javax.swing.JLabel lab8;
    private javax.swing.JMenuItem mDelit;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JMenuItem menuItem11;
    private javax.swing.JMenuItem menuItem12;
    private javax.swing.JMenuItem menuItem13;
    private javax.swing.JMenuItem menuItem14;
    private javax.swing.JMenuItem menuItem15;
    private javax.swing.JMenuItem menuItem16;
    private javax.swing.JMenuItem menuItem17;
    private javax.swing.JPanel north;
    private javax.swing.JPanel pan1;
    private javax.swing.JPanel pan11;
    private javax.swing.JPanel pan12;
    private javax.swing.JPanel pan13;
    private javax.swing.JPanel pan14;
    private javax.swing.JPanel pan15;
    private javax.swing.JPanel pan16;
    private javax.swing.JPanel pan17;
    private javax.swing.JPanel pan18;
    private javax.swing.JPanel pan19;
    private javax.swing.JPanel pan20;
    private javax.swing.JPanel pan21;
    private javax.swing.JPanel pan22;
    private javax.swing.JPanel pan23;
    private javax.swing.JPanel pan24;
    private javax.swing.JPanel pan3;
    private javax.swing.JPanel pan5;
    private javax.swing.JPanel pan6;
    private javax.swing.JPanel pan7;
    private javax.swing.JPanel pan8;
    private javax.swing.JPanel pan9;
    private javax.swing.JPanel panDesign;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPopupMenu ppReport;
    private javax.swing.JPopupMenu ppmCrud;
    private javax.swing.JScrollPane scr1;
    private javax.swing.JScrollPane scr2;
    private javax.swing.JScrollPane scr3;
    private javax.swing.JScrollPane scr4;
    private javax.swing.JScrollPane scr5;
    private javax.swing.JScrollPane scr6;
    private javax.swing.JPopupMenu.Separator sep1;
    private javax.swing.JPopupMenu.Separator sep2;
    private javax.swing.JPopupMenu.Separator sep3;
    private javax.swing.JPanel south;
    private javax.swing.JSpinner spinner;
    private javax.swing.JTable tab1;
    private javax.swing.JTable tab2;
    private javax.swing.JTable tab3;
    private javax.swing.JTable tab4;
    private javax.swing.JTable tab5;
    private javax.swing.JTabbedPane tabb1;
    private javax.swing.JTabbedPane tabb2;
    private javax.swing.JTextArea txt12;
    private javax.swing.JTextField txt13;
    private javax.swing.JTextField txt14;
    private javax.swing.JTextField txt16;
    private javax.swing.JTextField txt17;
    private javax.swing.JTextField txt18;
    private javax.swing.JTextField txt19;
    private javax.swing.JTextField txt20;
    private javax.swing.JTextField txt21;
    private javax.swing.JTextField txt22;
    private javax.swing.JTextField txt23;
    private javax.swing.JTextField txt24;
    private javax.swing.JTextField txt25;
    private javax.swing.JTextField txt26;
    private javax.swing.JTextField txt27;
    private javax.swing.JTextField txt28;
    private javax.swing.JTextField txt29;
    private javax.swing.JTextField txt3;
    private javax.swing.JTextField txt30;
    private javax.swing.JTextField txt31;
    private javax.swing.JTextField txt32;
    private javax.swing.JTextField txt33;
    private javax.swing.JTextField txt34;
    private javax.swing.JTextField txt36;
    private javax.swing.JTextField txt37;
    private javax.swing.JTextField txt38;
    private javax.swing.JTextField txt39;
    private javax.swing.JTextField txt40;
    private javax.swing.JTextField txt41;
    private javax.swing.JTextField txt42;
    private javax.swing.JTextField txt43;
    private javax.swing.JTextField txt44;
    private javax.swing.JTextField txt45;
    private javax.swing.JTextField txt46;
    private javax.swing.JTextField txt47;
    private javax.swing.JTextField txt48;
    private javax.swing.JTextField txt54;
    private javax.swing.JTextField txt55;
    private javax.swing.JTextField txt56;
    private javax.swing.JTextField txt57;
    private javax.swing.JTextField txt58;
    private javax.swing.JTextField txt60;
    private javax.swing.JTextField txt7;
    private javax.swing.JTextField txt8;
    private javax.swing.JTextField txt9;
    private javax.swing.JTree winTree;
    // End of variables declaration//GEN-END:variables
// </editor-fold> 

    private void initElements() {
        new FrameToFile(this, btnClose);
        new UColor();

        TableFieldFilter filterTable = new TableFieldFilter(0, tab1);
        south.add(filterTable, 0);
        filterTable.getTxt().grabFocus();

        panDesign.add(scene, java.awt.BorderLayout.CENTER);
        //UGui.documentFilter(3, txt7);
        List.of(btnIns, btnDel, btnRef).forEach(b -> b.addActionListener(l -> UGui.stopCellEditing(tab1)));
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
                if (tabb1.getSelectedIndex() == 2) {
                    btnSet.setEnabled(true);
                    btnFind.setEnabled(true);
                } else {
                    btnSet.setEnabled(false);
                    btnFind.setEnabled(false);
                }
            }
        });
    }
}
