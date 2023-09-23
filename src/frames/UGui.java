package frames;

import builder.Wincalc;
import builder.model1.Com5t;
import builder.IArea5e;
import builder.ICom5t;
import builder.IElem5e;
import builder.script.GsonRoot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import common.eProp;
import dataset.Conn;
import dataset.Field;
import dataset.Query;
import dataset.Record;
import domain.eGroups;
import domain.eParams;
import domain.eSysprod;
import domain.eSystree;
import enums.Enam;
import builder.param.ParamList;
import common.UCom;
import enums.UseColor;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import frames.swing.DefCellEditorBtn;
import frames.swing.DefTableModel;
import java.util.Enumeration;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import common.listener.ListenerObject;
import common.eProfile;
import common.listener.ListenerRecord;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eFurndet;
import domain.eParmap;
import domain.ePrjprod;
import enums.Layout;
import enums.PKjson;
import enums.Type;
import frames.swing.DefMutableTreeNode;
import java.util.HashSet;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * <p>
 * Параметры приложения </p>
 */
public class UGui {

    private static GregorianCalendar appCalendar = new GregorianCalendar(); //календарь программы    
    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM); //формат даты
    public static SimpleDateFormat simpleFormat = new SimpleDateFormat("dd.MM.yyyy"); //"yyyy-MM-dd" формат для баз где даты utf8
    private static int mes = 0;

    // <editor-fold defaultstate="collapsed" desc="Работа с датой..."> 
    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    // Преобразование даты в строку
    public static String getDateStr(Object obj) {
        if (obj == null) {
            return dateFormat.format(appCalendar.getTime());
        }
        if (obj instanceof Date) {
            GregorianCalendar gk = new GregorianCalendar();
            gk.setTime((Date) obj);
            int index = gk.get(GregorianCalendar.MONTH);
            String monthName[] = {"января", "февраля", "марта", "апреля", "мая",
                "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
            return "\" " + String.valueOf(gk.get(GregorianCalendar.DAY_OF_MONTH)) + " \"   "
                    + monthName[index] + "    " + String.valueOf(gk.get(GregorianCalendar.YEAR)) + " г.";
        } else {
            return "";
        }
    }

    //Преобразование текущей даты в строку
    public static int getDateField(Object obj, int field) {
        if (obj instanceof Date) {
            GregorianCalendar gk = new GregorianCalendar();
            gk.setTime((Date) obj);
            return gk.get(field);
        } else {
            return 0;
        }
    }

    // Текущая дата
    public static Date getDateCur() {
        return appCalendar.getTime();
    }

    //Преобразование string в date
    public static Date StrToDate(String str) {
        try {
            return (Date) dateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    // Преобразование date в string
    public static String DateToStr(Object date) {
        return (date instanceof java.util.Date) ? simpleFormat.format(date) : "";
    }

    //Преобразование date в string
    public static String DateToSql(Object date) {
        if (date == null) {
            return simpleFormat.format(appCalendar.getTime());
        }
        if (date instanceof java.util.Date) {
            return simpleFormat.format(date);
        }
        return "";
    }

    //Текущий год
    public static int getYearCur() {
        return appCalendar.get(Calendar.YEAR);
    }

    public static GregorianCalendar сalendar() {
        return appCalendar;
    }
// </editor-fold> 

    public static Font getFont(int size, int bold) {
        return new Font(eProp.fontname.read(), bold, Integer.valueOf(eProp.fontsize.read()) + size);
    }

    /**
     * Загрузка TreeList
     */
    public static DefMutableTreeNode loadWinTree(Wincalc winc) {

        DefMutableTreeNode root = new DefMutableTreeNode(winc.rootArea);
        root.add(new DefMutableTreeNode(new Com5t(Type.PARAM)));
        //Рама
        DefMutableTreeNode frm = root.add(new DefMutableTreeNode(new Com5t(Type.FRAME)));
        frm.add(new DefMutableTreeNode(winc.rootArea.frames().get(Layout.BOTT)));
        frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
        frm.add(new DefMutableTreeNode(winc.rootArea.frames().get(Layout.RIGHT)));
        frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
        frm.add(new DefMutableTreeNode(winc.rootArea.frames().get(Layout.TOP)));
        frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
        frm.add(new DefMutableTreeNode(winc.rootArea.frames().get(Layout.LEFT)));
        frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));

        //Цикл по всем детям ареа
        for (ICom5t com : winc.rootArea.childs()) {
            //Если это не створка
            if (com.type() != Type.STVORKA) {
                //Если это элемент
                if (com instanceof IElem5e) {
                    frm.add(new DefMutableTreeNode(com));
                    if (com.type() != Type.GLASS) {
                        frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING) {
                        }));
                    }
                    //Если это ареа
                } else {
                    for (ICom5t com2 : ((IArea5e) com).childs()) {
                        if (com2.type() != Type.STVORKA) {
                            if (com2 instanceof IElem5e) {
                                frm.add(new DefMutableTreeNode(com2));
                                if (com2.type() != Type.GLASS) {
                                    frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING) {
                                    }));
                                }
                            } else {
                                for (ICom5t com3 : ((IArea5e) com2).childs()) {
                                    if (com3.type() != Type.STVORKA) {
                                        if (com3 instanceof IElem5e) {
                                            frm.add(new DefMutableTreeNode(com3));
                                            if (com3.type() != Type.GLASS) {
                                                frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING) {
                                                }));
                                            }
                                        } else {
                                            for (ICom5t com4 : ((IArea5e) com3).childs()) {
                                                if (com4.type() != Type.STVORKA) {
                                                    if (com4 instanceof IElem5e) {
                                                        frm.add(new DefMutableTreeNode(com4));
                                                        if (com4.type() != Type.GLASS) {
                                                            frm.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING) {
                                                            }));
                                                        }
                                                    }
                                                } else {
                                                    loadWinTree(winc, root, com4); //створка
                                                }
                                            }
                                        }
                                    } else {
                                        loadWinTree(winc, root, com3); //створка
                                    }
                                }
                            }
                        } else {
                            loadWinTree(winc, root, com2); //створка
                        }
                    }
                }
            } else {
                loadWinTree(winc, root, com); //створка
            }
        }
        return root;
    }

    /**
     * Створка
     */
    public static void loadWinTree(Wincalc winc, DefMutableTreeNode root, ICom5t com) {
        DefMutableTreeNode nodeStv = root.add(new DefMutableTreeNode(com));
        IArea5e stv = (IArea5e) com;
        nodeStv.add(new DefMutableTreeNode(stv.frames().get(Layout.BOTT)));
        nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
        nodeStv.add(new DefMutableTreeNode(stv.frames().get(Layout.RIGHT)));
        nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
        nodeStv.add(new DefMutableTreeNode(stv.frames().get(Layout.TOP)));
        nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
        nodeStv.add(new DefMutableTreeNode(stv.frames().get(Layout.LEFT)));
        nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
        //Цикл по детям створки
        for (ICom5t com2 : ((IArea5e) com).childs()) {
            //Если это элемент
            if (com2 instanceof IElem5e) {
                nodeStv.add(new DefMutableTreeNode(com2));
                if (com2.type() != Type.GLASS && com2.type() != Type.MOSKITKA) { //В стекле нет соединений
                    nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
                }
                //Это ареа
            } else {
                for (ICom5t com3 : ((IArea5e) com2).childs()) {
                    if (com3 instanceof IElem5e) {
                        nodeStv.add(new DefMutableTreeNode(com3));
                        if (com3.type() != Type.GLASS && com3.type() != Type.MOSKITKA) {
                            nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
                        }
                    } else {
                        for (ICom5t com4 : ((IArea5e) com3).childs()) {
                            if (com4 instanceof IElem5e) {
                                nodeStv.add(new DefMutableTreeNode(com4));
                                if (com4.type() != Type.GLASS && com4.type() != Type.MOSKITKA) {
                                    nodeStv.getLastChild().add(new DefMutableTreeNode(new Com5t(Type.JOINING)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void selectionPathSys(double id, JTree tree) {
        if (id != -1) {
            DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
            do {
                if (id == ((DefMutableTreeNode) curNode).rec().getDbl(eSystree.id)) {
                    TreePath path = new TreePath(curNode.getPath());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
                curNode = curNode.getNextNode();
            } while (curNode != null);
        }
    }

    public static void selectionPathWin(double id, JTree tree) {
        if (id != -1) {
            DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
            do {
                if (id == ((DefMutableTreeNode) curNode).com5t().id()) {
                    TreePath path = new TreePath(curNode.getPath());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                }
                curNode = curNode.getNextNode();
            } while (curNode != null);
        }
    }

    public static void expandTree(JTree tree, TreePath path, boolean expand) {
        TreeNode node = (TreeNode) path.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            Enumeration enumeration = node.children();
            while (enumeration.hasMoreElements()) {
                TreeNode n = (TreeNode) enumeration.nextElement();
                TreePath p = path.pathByAddingChild(n);

                expandTree(tree, p, expand);
            }
        }

        if (expand) {
            tree.expandPath(path);
        } else {
            tree.collapsePath(path);
        }
    }

    public static String ioknaParamUpdate(String script, int ioknaID) {
        Gson gson = new GsonBuilder().create();
        GsonRoot gsonRoot = gson.fromJson(script, GsonRoot.class);
        JsonObject jsonObj = gson.fromJson(gsonRoot.param(), JsonObject.class);
        JsonArray jsonArr = jsonObj.getAsJsonArray(PKjson.ioknaParam);
        jsonArr = (jsonArr == null) ? new JsonArray() : jsonArr;

        int titleID, titleID2;
        if (ioknaID < 0) {
            titleID = eParams.find(ioknaID).getInt(eParams.groups_id);
        } else {
            titleID = eParmap.find(ioknaID).getInt(eParmap.groups_id);
        }
        for (int i = 0; i < jsonArr.size(); i++) {
            int ioknaID2 = jsonArr.get(i).getAsInt();
            if (ioknaID < 0) {
                titleID2 = eParams.find(ioknaID2).getInt(eParams.groups_id);
            } else {
                titleID2 = eParmap.find(ioknaID2).getInt(eParmap.groups_id);
            }
            if (titleID == titleID2) {
                jsonArr.remove(i);
            }
        }
        jsonArr.add(ioknaID);
        jsonObj.add(PKjson.ioknaParam, jsonArr);
        gsonRoot.param(jsonObj);
        return gsonRoot.toJson();
    }

    public static String designTitle() {
        try {
            if (eProfile.profile == eProfile.P02) {
                int productID = Integer.valueOf(eProp.sysprodID.read());
                Record productRec = eSysprod.find(productID);
                if (productRec != null) {

                    String str = productRec.getStr(eSysprod.name);
                    if (str.length() > 6) {
                        if (str.length() < 128) {
                            str = str.substring(6, str.length());
                        } else {
                            str = str.substring(6, 128);
                        }
                    }
                    return "   Изделие: " + eSystree.patch(productRec.getInt(eSysprod.systree_id), "") + "/" + str;
                }

            } else if (eProfile.profile == eProfile.P03) {
                int productID = Integer.valueOf(eProp.prjprodID.read());
                Record productRec = ePrjprod.find(productID);
                if (productRec != null) {

                    String str = productRec.getStr(ePrjprod.name);
                    if (str.length() > 6) {
                        if (str.length() < 128) {
                            str = str.substring(6, str.length());
                        } else {
                            str = str.substring(6, 128);
                        }
                    }
                    return "   Изделие: " + eSystree.patch(productRec.getInt(ePrjprod.systree_id), "") + "/" + str;
                }
            }
            return "";

        } catch (Exception e) {
            System.err.println("frames.Util.designName() " + e);
            return "";
        }
    }

    public static String designProject() {
        try {
            if (eProfile.profile == eProfile.P02) {
                int productID = Integer.valueOf(eProp.sysprodID.read());
                Record productRec = eSysprod.find(productID);
                if (productRec != null) {

                    String str = productRec.getStr(eSysprod.name);
                    if (str.length() > 6) {
                        if (str.length() < 128) {
                            str = str.substring(6, str.length());
                        } else {
                            str = str.substring(6, 128);
                        }
                    }
                    return "<html><font size='3' color='blue'>Проект: " + str;
                }

            } else if (eProfile.profile == eProfile.P03) {
                int productID = Integer.valueOf(eProp.prjprodID.read());
                Record productRec = ePrjprod.find(productID);
                if (productRec != null) {

                    String str = productRec.getStr(ePrjprod.name);
                    if (str.length() > 6) {
                        if (str.length() < 128) {
                            str = str.substring(6, str.length());
                        } else {
                            str = str.substring(6, 128);
                        }
                    }
                    return "   Изделие: " + eSystree.patch(productRec.getInt(ePrjprod.systree_id), "") + "/" + str;
                }
            }
            return "";

        } catch (Exception e) {
            System.err.println("frames.Util.designName() " + e);
            return "";
        }
    }

    public static String consoleColor(Object clr) {

        if (clr == java.awt.Color.RED) {
            return "\u001B[31m";
        } else if (clr == java.awt.Color.GREEN) {
            return "\u001B[32m";
        } else if (clr == java.awt.Color.BLUE) {
            return "\u001B[34m";
        } else {
            return "\u001B[0m";
        }
    }

    //Установить пустой бордер
    public static void createEmptyBorder(final Container c) {
        List<Component> comps = getAllComponents(c);
        for (Component comp : comps) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }
        }
    }

    //Получить таблицу от непустого бордера
    public static JTable tableFromBorder(JTable... tables) {
        for (JTable table : tables) {
            if (table.getBorder() != null) {
                return table;
            }
        }
        return null;
    }

    //Все компоненты формы
    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

    //Типы данных в базе
    public static String typeField(Field.TYPE type, Object size) {

        if (type == Field.TYPE.INT) {
            return "INTEGER";
        } else if (type == Field.TYPE.DBL) {
            return "DOUBLE PRECISION";
        } else if (type == Field.TYPE.FLT) {
            return "FLOAT";
        } else if (type == Field.TYPE.STR) {
            return "VARCHAR(" + size + ")";
        } else if (type == Field.TYPE.DATE) {
            return "DATE";
        } else if (type == Field.TYPE.BLOB) {
            return "BLOB SUB_TYPE 1 SEGMENT SIZE " + size;
        } else if (type == Field.TYPE.BOOL) {
            return "SMALLINT";
        }
        return "";
    }

    //Рекурсия поиска родителя
    public static Record findParent(Query table, int key) {
        for (Record record : table) {
            if (key == record.getInt(eSystree.id)) {
                if (record.getInt(eSystree.id) == record.getInt(eSystree.parent_id)) {
                    return record;
                } else {
                    return findParent(table, record.getInt(eSystree.parent_id));
                }
            }
        }
        return null;
    }

    //Прокрутить скроллинг и сделать ячейку видимой
    public static void scrollRectToIndex(int index, JTable table) {
        try {
            int row = table.convertRowIndexToView(index);
            if (table.getRowCount() > row) {
                scrollRectToRow(row, table);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UGui.scrollRectToIndex() " + e);
        }
    }

    //Прокрутить скроллинг и сделать ячейку видимой
    public static void scrollRectToRow(int row, JTable table) {
        try {
            if (table.getRowCount() > row + 4) {
                Rectangle cellRect = table.getCellRect(row + 4, 0, false);
                table.scrollRectToVisible(cellRect);
            } else if (table.getRowCount() > row) {
                Rectangle cellRect = table.getCellRect(row, 0, false);
                table.scrollRectToVisible(cellRect);
            }
        } catch (Exception e) {
            System.err.println("Ошибка:UGui.scrollRectToRow() " + e);
        }
    }

    //Выделить запись
    public static void setSelectedRow(JTable table) {

        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);

            int column = table.getSelectedColumn();
            if (column != -1) {
                table.setColumnSelectionInterval(column, column);
            }
        }
    }

    //Выделить запись
    public static void setSelectedIndex(JTable table, int index) {
        if (table.getRowCount() > 0 && index != -1) {

            int row = table.convertRowIndexToView(index);
            if (row < table.getRowCount()) {

                table.setRowSelectionInterval(row, row);
            } else {
                table.setRowSelectionInterval(0, 0);
            }
        }
    }

    //Выделить запись по ключу
    public static void setSelectedKey(JTable table, int id) {
        Query query = ((DefTableModel) table.getModel()).getQuery();
        if (id == -1) {
            UGui.setSelectedRow(table);
        } else {
            for (int i = 0; i < query.size(); ++i) {
                if (query.get(i).getInt(1) == id) {
                    UGui.setSelectedIndex(table, i);
                    UGui.scrollRectToRow(i, table);
                }
            }
        }
    }

    public static int getIndexKeyValue(JTable table, Record record, Field field) {
        Object val = record.get(field);
        Query query = getQuery(table);
        if (val != null) {
            for (int i = 0; i < query.size(); i++) {
                if (val.equals(query.get(i, field))) {
                    return i;
                }
            }
        }
        return -1;
    }

    //Получить convertRowIndexToModel
    public static int getIndexRec(JTable table) {
        if (table.getSelectedRow() != -1) {
            return table.convertRowIndexToModel(table.getSelectedRow());
        }
        return -1;
    }

    //Получить convertRowIndexToModel
    public static int getIndexRec(JTable table, int def) {
        if (table.getSelectedRow() != -1) {
            return table.convertRowIndexToModel(table.getSelectedRow());
        }
        return def;
    }

    //Поиск Record в модели по row table
    public static Record findRecordModel(Query q, JTable table, int row) {
        int id = (int) table.getValueAt(row, table.getColumnCount() - 1);
        return q.stream().filter(rec -> rec.getInt(1) == 1).findFirst().orElse(null);
    }

    //Вставить запись
    public static void insertRecordCur(JTable table, Field field, ListenerRecord listener) {

        int index = UGui.getIndexRec(table);
        index = (index == -1) ? 0 : index;
        Query query = ((DefTableModel) table.getModel()).getQuery();
        Record record = field.newRecord(Query.INS);
        record.setNo(field.fields()[1], Conn.genId(field));
//        if (index == 0) {
//            query.add(index, record);      
//        } else 
        if (++index <= table.getRowCount()) {
            query.add(index, record);

        } else {
            query.add(--index, record);
        }
        listener.action(record);
        ((DefaultTableModel) table.getModel()).fireTableRowsInserted(index, index);
        UGui.setSelectedIndex(table, index);
        UGui.scrollRectToIndex(index, table);
    }

    //Вставить запись
    public static void insertRecordEnd(JTable table, Field field, ListenerRecord listener) {

        Query query = ((DefTableModel) table.getModel()).getQuery();
        Record record = field.newRecord(Query.INS);
        record.setNo(field.fields()[1], Conn.genId(field));
        query.add(record);
        listener.action(record);
        ((DefaultTableModel) table.getModel()).fireTableRowsInserted(query.size() - 1, query.size() - 1);
        UGui.setSelectedIndex(table, query.size() - 1);
        UGui.scrollRectToIndex(query.size() - 1, table);
    }

    //Изменить запись
    public static void updateRecord(JTable table, ListenerRecord listener) {
        Record record = ((DefTableModel) table.getModel()).getQuery().get(UGui.getIndexRec(table));
        listener.action(record);
        ((DefaultTableModel) table.getModel()).fireTableRowsUpdated(table.getSelectedRow(), table.getSelectedRow());
    }

    //Удалить запись
    public static void deleteRecord(JTable table) {
        if (table.getSelectedRow() != -1) {
            Query query = ((DefTableModel) table.getModel()).getQuery();
            int row = table.getSelectedRow();
            int index = getIndexRec(table);
            Record record = query.get(index);
            record.set(0, Query.DEL);
            if (query.delete(record)) {
                query.removeRec(index);
                ((DefTableModel) table.getModel()).fireTableRowsDeleted(row, row);
                row = (row > 0) ? --row : 0;
                if (query.size() > 0) {
                    index = table.convertRowIndexToModel(row);
                    UGui.setSelectedIndex(table, index);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ни одна из записей не выбрана", "Предупреждение", JOptionPane.NO_OPTION);
        }
    }

    //Проверка допустимости удаления таблицы
    public static int isDeleteRecord(JTable table, java.awt.Window owner, JTable... tables) {
        if (table.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Ни одна из записей не вывыделена", "Предупреждение", JOptionPane.NO_OPTION);
            return 1;
        }
        for (JTable tab : tables) {
            if (tab.getRowCount() != 0) {
                JOptionPane.showMessageDialog(owner, "Перед удалением записи, удалите данные в зависимых таблицах", "Предупреждение", JOptionPane.NO_OPTION);
                return 1;
            }
        }
        //return JOptionPane.showConfirmDialog(owner, "Вы уверены, что хотите удалить текущую запись?", "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return JOptionPane.showConfirmDialog(owner, "Вы действительно хотите удалить текущую запись?", "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    //Обновление записи в таблице JTable
    public static void fireTableRowUpdated(JTable table) {
        ((DefaultTableModel) table.getModel()).fireTableRowsUpdated(table.getSelectedRow(), table.getSelectedRow());
    }

    //Установить border и выполнить sql
    public static void updateBorderAndSql(JTable table, List<JTable> tabList) {
        if (tabList != null) {
            tabList.forEach(tab -> tab.setBorder(null));
            tabList.forEach(tab -> {
                if (tab != table) {
                    UGui.stopCellEditing(tab);
                    if (tab.getModel() instanceof DefTableModel) {
                        ((DefTableModel) tab.getModel()).getQuery().execsql();
                    }
                }
            });
        }
        table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
    }

    //Получить Query из таблицы
    public static Query getQuery(JTable table) {
        return ((DefTableModel) table.getModel()).getQuery();
    }

    //Очистить таблицы
    public static void clearTable(JTable... jTable) {
        for (JTable table : jTable) {
            if (table.getModel() instanceof DefTableModel) {
                ((DefTableModel) table.getModel()).getQuery().clear();

            } else if (table.getModel() instanceof DefaultTableModel) {
                ((DefaultTableModel) table.getModel()).getDataVector().clear();
            }
            ((DefaultTableModel) table.getModel()).fireTableDataChanged();
        }
    }

    //Инкапсуляция кнопки в ячейку таблицы
    public static JButton buttonCellEditor(JTable table, int column) {
        JButton btn = new JButton("...");
        table.getColumnModel().getColumn(column).setCellEditor(new DefCellEditorBtn(btn));
        return btn;
    }

    //Инкапсуляция кнопки в ячейку таблицы
    public static JButton buttonCellEditor(JTable table, int column, ListenerObject listenerCell) {
        JButton btn = new JButton("...");
        table.getColumnModel().getColumn(column).setCellEditor(new DefCellEditorBtn(listenerCell, btn));
        return btn;
    }

    //Выключить режим редактирования
    public static void stopCellEditing(JComponent... compList) {
        for (JComponent comp : compList) {
            if (comp instanceof JTable) {
                if (((JTable) comp).isEditing()) {
                    ((JTable) comp).getCellEditor().stopCellEditing();
                }
            } else if (comp instanceof JTree) {
                if (((JTree) comp).isEditing()) {
                    ((JTree) comp).getCellEditor().stopCellEditing();
                }
            }
        }
    }

    //Редактирование параметров ячейки
    public static void cellParamNameOrValue(Record record, JTable table, Field id, Field text) {
        try {
            Query query = ((DefTableModel) table.getModel()).getQuery();
            Record record2 = query.get(UGui.getIndexRec(table));

            //Название пользовательского параметра
            if (eGroups.values().length == record.size()) {
                record2.set(id, record.getInt(eGroups.id));
                record2.set(text, "");

                //Название системного параметра из PalList
            } else if (record.size() == 2) {
                record2.set(id, record.getInt(0));
                record2.set(text, ParamList.find(record.getInt(0)).def());

                //Запись значения в ячейку
            } else if (record.size() == 1) {
                String val = record2.getStr(text);

                //Удаление данных в ячейке
                if (record.get(0) == null) {
                    record2.set(text, "");

                    //Добавление данных в ячейке
                } else if (val != null && val.isEmpty() == false) {
                    record2.set(text, val + ";" + record.getStr(0));

                } else {
                    record2.set(text, record.getStr(0)); //???
                }
            }
            int index = getIndexRec(table);
            ((DefaultTableModel) table.getModel()).fireTableRowsUpdated(index, index);
        } catch (Exception e) {
            System.out.println("Ошибка:UGui.cellParamNameOrValue() " + e);
        }
    }

    //Редактирование типа данных и вида ячейки таблицы 
    //componentCell - DefCellEditorBtn либо String см. класс DefCellEditorBtn
    public static boolean cellParamTypeOrVid(JTable table, Object componentCell, Field groups_id) {
        try {
            Query qXxxpar = ((DefTableModel) table.getModel()).getQuery();
            int groupsID = qXxxpar.getAs(getIndexRec(table), groups_id);

            //Если компонент класс DefCellEditorBtn
            //установим вид и тип ячейки
            if (componentCell instanceof DefCellEditorBtn) {
                DefCellEditorBtn editor = (DefCellEditorBtn) componentCell;

                //Пользовательский список параметров
                if (groupsID < 0) {
                    editor.getButton().setVisible(true);
                    editor.getTextField().setEnabled(false);

                    //Системне параметры
                } else {
                    Enam enam = ParamList.find(groupsID);

                    //Системные, выбор из справочника
                    if (enam.dict() != null) {
                        editor.getButton().setVisible(true);
                        editor.getTextField().setEnabled(false);

                        //Системные вводимые пользователем
                    } else {
                        editor.getButton().setVisible(false);
                        editor.getTextField().setEnabled(true);
                        editor.getTextField().setEditable(true);
                    }
                }

                //Если компонент просто текст, идёт проверка на коррекность ввода
                //пользовательский параметр текстом бвть не может и не проверяется
            } else if (groupsID > 0 && componentCell != null && componentCell instanceof String) {
                String txt = (String) componentCell;
                return ParamList.find(qXxxpar.getAs(UGui.getIndexRec(table), groups_id)).check(txt);
            }
        } catch (Exception e) {
            System.out.println("Ошибка:UGui.cellParamTypeOrVid() " + e);
        }
        return true;
    }

    //Редактирование параметра текстуры
    public static void cellParamColor(Record record, JTable table, Field color_fk, Field color_us, JTable... tables) {
        UGui.stopCellEditing(tables);
        int index = getIndexRec(table);
        Query query = ((DefTableModel) table.getModel()).getQuery();
        Record detailRec = query.get(index);
        int group = (eGroups.values().length == record.size()) ? record.getInt(eGroups.id) : record.getInt(eColor.id);
        detailRec.set(color_fk, group);

        if (group == 0 || group == 100000) { //автополбор/точн.подбор
            int val = UseColor.PROF.id + (UseColor.PROF.id << 4) + (UseColor.PROF.id << 8);
            detailRec.set(color_us, val);

        } else if (group > 0) { //выбраны в ручную
            detailRec.set(color_us, 0);

        } else { //параметры соответствия
            int val = UseColor.PROF.id + (UseColor.PROF.id << 4) + (UseColor.PROF.id << 8);
            detailRec.set(color_us, val);
        }
        ((DefaultTableModel) table.getModel()).fireTableRowsUpdated(index, index);
        UGui.setSelectedIndex(table, index);
    }

    public static void cellParamEnum(Record record, JTable table, Field field_fk, JTable... tables) {
        UGui.stopCellEditing(tables);
        Query query = ((DefTableModel) table.getModel()).getQuery();
        int index = getIndexRec(table);
        query.set(record.getInt(0), getIndexRec(table), field_fk);
        ((DefaultTableModel) table.getModel()).fireTableDataChanged();
        UGui.setSelectedIndex(table, index);
    }

    //Программный клик на компоненте
    public static void componentClick(JComponent comp) {
        try {
            Point p = comp.getLocationOnScreen();
            Robot r = new Robot();
            r.mouseMove(p.x + comp.getWidth() / 2, p.y + comp.getHeight() / 2);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(0);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    //Список для выбора ручек, подвесов, накладок в створке   
    public static Query artTypeToFurndetList(int furnitureID, Query qArtikl) {
        try {
            HashSet<Integer> filterSet = new HashSet();
            Query qResult = new Query(eArtikl.values());
            Query qFurndet = new Query(eFurndet.values()).select(eFurndet.up); //вся детализация фурнитуры

            //Цикл детализаций
            for (Record furndetRec1 : qFurndet) { //первый уровень
                if (furndetRec1.getInt(eFurndet.furniture_id1) == furnitureID) {

                    //Фильтр по детализации определённого типа определённой фурнитуры
                    if (furndetRec1.get(eFurndet.furniture_id2) == null) { //НЕ НАБОР
                        filterSet.add(furndetRec1.getInt(eFurndet.artikl_id));

                    } else { //ЭТО НАБОР
                        for (Record furndetRec2 : qFurndet) { //второй уровень
                            if (furndetRec1.getInt(eFurndet.furniture_id2) == furndetRec2.getInt(eFurndet.furniture_id1)) {
                                filterSet.add(furndetRec2.getInt(eFurndet.artikl_id));
                            }
                        }
                    }
                }
            }
            for (Integer id : filterSet) {
                Record artiklRec = qArtikl.stream().filter(rec -> rec.getInt(eArtikl.id) == id).findFirst().orElse(null);
                if (artiklRec != null) {
                    qResult.add(artiklRec);
                }
            }
            return qResult;

        } catch (Exception e) {
            System.err.println("Ошибка: frames.artTypeToFurndetList " + e);
            return null;
        }
    }

    public static HashSet<Record> artiklToColorSet(int artiklID) {
        HashSet<Record> colorSet = new HashSet();
        Query artdetList = new Query(eArtdet.values()).select(eArtdet.up, "where", eArtdet.artikl_id, "=", artiklID);
        artdetList.stream().forEach(rec -> {

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
        return colorSet;
    }

    public static HashSet<Record> artiklToColorSet(int artiklID, int side) {
        HashSet<Record> colorSet = new HashSet();
        Field field = (side == 1) ? eArtdet.mark_c1 : (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
        Query artdetList = new Query(eArtdet.values()).select(eArtdet.up, "where", eArtdet.artikl_id, "=", artiklID, "and", field, "= 1");
        artdetList.stream().forEach(rec -> {

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
        return colorSet;
    }

    //Проверка на коррекность ввода
    public static void setDocumentFilter(int pattern, JTextField... txtField) {
        for (JTextField txtField2 : txtField) {
            ((PlainDocument) txtField2.getDocument()).setDocumentFilter(new DocumentFilter() {

                public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                    if (string != null && string.length() > 1 || UCom.check(string, pattern)) { //проверка на коррекность ввода
                        super.replace(fb, offset, length, string, attrs);
                    }
                }
            });
        }
    }

    public static JsonObject getAsJsonObject(JsonObject obj, String key) {

        if (obj.getAsJsonObject(key) == null) {
            obj.add(key, new JsonObject());
        }
        return obj.getAsJsonObject(key);
    }    
}
