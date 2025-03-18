package frames;

import builder.model.Com5t;
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
import frames.swing.cmp.DefCellEditorBtn;
import frames.swing.cmp.DefTableModel;
import java.util.Enumeration;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import common.listener.ListenerObject;
import common.eProfile;
import common.listener.ListenerRecord;
import dataset.Table;
import domain.eArtdet;
import domain.eArtikl;
import domain.eColor;
import domain.eFurndet;
import domain.eParmap;
import domain.ePrjprod;
import enums.PKjson;
import enums.Type;
import frames.swing.cmp.DefMutableTreeNode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.tree.DefaultMutableTreeNode;
import org.locationtech.jts.geom.Geometry;
import startup.App;

/**
 * <p>
 * ��������� ���������� </p>
 */
public class UGui {

    private static GregorianCalendar appCalendar = new GregorianCalendar(); //��������� ���������    
    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM); //������ ����
    public static SimpleDateFormat simpleFormat = new SimpleDateFormat("dd.MM.yyyy"); //"yyyy-MM-dd" ������ ��� ��� ��� ���� utf8
    private static int mes = 0;

    // <editor-fold defaultstate="collapsed" desc="������ � �����"> 
    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    // �������������� ���� � ������
    public static String getDateInWords(Object obj) {
        if (obj == null) {
            return dateFormat.format(appCalendar.getTime());
        }
        if (obj instanceof Date) {
            GregorianCalendar gk = new GregorianCalendar();
            gk.setTime((Date) obj);
            int index = gk.get(GregorianCalendar.MONTH);
            String monthName[] = {"������", "�������", "�����", "������", "���",
                "����", "����", "�������", "��������", "�������", "������", "�������"};
            return "\" " + String.valueOf(gk.get(GregorianCalendar.DAY_OF_MONTH)) + " \"   "
                    + monthName[index] + "    " + String.valueOf(gk.get(GregorianCalendar.YEAR)) + " �.";
        } else {
            return "";
        }
    }

    //�������������� ������� ���� � ������
    public static int getDateField(Object obj, int field) {
        if (obj instanceof Date) {
            GregorianCalendar gk = new GregorianCalendar();
            gk.setTime((Date) obj);
            return gk.get(field);
        } else {
            return 0;
        }
    }

    // ������� ����
    public static Date getDateCur() {
        return appCalendar.getTime();
    }

    //�������������� string � date
    public static Date convert1Date(String str) {
        try {
            return (Date) dateFormat.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    // �������������� date � string
    public static String convert2Date(Object value) {
        return (value instanceof java.util.Date) ? simpleFormat.format(value) : "";
    }

//    //�������������� date � string
//    public static String convert3Date(Object value) {
//        if (value == null) {
//            return simpleFormat.format(appCalendar.getTime());
//        }
//        if (value instanceof java.util.Date) {
//            return simpleFormat.format(value);
//        }
//        return "";
//    }

    //������� ���
    public static int getYearCur() {
        return appCalendar.get(Calendar.YEAR);
    }

    public static GregorianCalendar �alendar() {
        return appCalendar;
    }
// </editor-fold> 

    public static Font getFont(int size, int bold) {
        return new Font(eProp.fontname.getProp(), bold, Integer.valueOf(eProp.fontsize.getProp()) + size);
    }

    public static <T extends JComponent> List<T> findComponents(final Container container, final Class<T> componentType) {
        return Stream.concat(
                Arrays.stream(container.getComponents())
                        .filter(componentType::isInstance).map(componentType::cast),
                Arrays.stream(container.getComponents())
                        .filter(Container.class::isInstance).map(Container.class::cast).flatMap(c -> findComponents(c, componentType).stream())
        ).collect(Collectors.toList());
    }

    public static <T extends JComponent> List<T> findComponents(final JRootPane rootPane, final Class<T> componentType) {
        Container container = rootPane.getContentPane();
        return Stream.concat(
                Arrays.stream(container.getComponents())
                        .filter(componentType::isInstance).map(componentType::cast),
                Arrays.stream(container.getComponents())
                        .filter(Container.class::isInstance).map(Container.class::cast).flatMap(c -> findComponents(c, componentType).stream())
        ).collect(Collectors.toList());
    }

    public static void stopCellEditingAndExecSql() {
        for (App app : App.values()) {
            if (app.frame != null) {
                UGui.findComponents(app.frame.getRootPane(), JTable.class).forEach(c -> UGui.stopCellEditing(c));
            }
        }
        Query.listOpenTable.forEach(q -> q.execsql());
    }

    public static void stopCellEditingAndExecSql(JRootPane rootPane) {
        UGui.findComponents(rootPane, JTable.class).forEach(c -> UGui.stopCellEditing(c));
        Query.listOpenTable.forEach(q -> q.execsql());
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
                if (id == ((DefMutableTreeNode) curNode).com5t().id) {
                    TreePath path = new TreePath(curNode.getPath());
                    tree.setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                    return;
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
        JsonObject jsonObj = gson.fromJson(gsonRoot.param, JsonObject.class);
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
        gsonRoot.param = jsonObj;
        return gsonRoot.toJson();
    }

    public static String designTitle() {
        try {
            if (eProfile.profile == eProfile.P02) {
                int productID = Integer.valueOf(eProp.sysprodID.getProp());
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
                    return eProfile.profile.title + "   �������: " + eSystree.patch(productRec.getInt(eSysprod.systree_id), "") + "/" + str;
                }

            } else if (eProfile.profile == eProfile.P03) {
                int productID = Integer.valueOf(eProp.prjprodID.getProp());
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
                    return eProfile.profile.title + "   �������: " + eSystree.patch(productRec.getInt(ePrjprod.systree_id), "") + "/" + str;
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
                int productID = Integer.valueOf(eProp.sysprodID.getProp());
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
                    return "<html><font size='3' color='blue'>������: " + str;
                }

            } else if (eProfile.profile == eProfile.P03) {
                int productID = Integer.valueOf(eProp.prjprodID.getProp());
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
                    return "   �������: " + eSystree.patch(productRec.getInt(ePrjprod.systree_id), "") + "/" + str;
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

    //���������� ������ ������
    public static void createEmptyBorder(final Container c) {
        List<Component> comps = getAllComponents(c);
        for (Component comp : comps) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            }
        }
    }

    //�������� ������� �� ��������� �������
    public static JTable tableFromBorder(JTable... tables) {
        for (JTable table : tables) {
            if (table.getBorder() != null) {
                return table;
            }
        }
        return null;
    }

    //��� ���������� �����
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

    //���� ������ � ����
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

    //�������� ������ ��������
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

    //���������� ��������� � ������� ������ �������
    public static void scrollRectToIndex(int index, JTable table) {
        try {
            int row = table.convertRowIndexToView(index);
            if (table.getRowCount() > row) {
                scrollRectToRow(row, table);
            }
        } catch (Exception e) {
            System.err.println("������:UGui.scrollRectToIndex() " + e);
        }
    }

    //���������� ��������� � ������� ������ �������
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
            System.err.println("������:UGui.scrollRectToRow() " + e);
        }
    }

    //�������� ������
    public static void setSelectedRow(JTable table) {

        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);

            int column = table.getSelectedColumn();
            if (column != -1) {
                table.setColumnSelectionInterval(column, column);
            }
        }
    }

    //�������� ������
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

    //�������� ������ �� �����
    public static void setSelectedKey(JTable table, int id) {
        if (id != -1 && id != -3) {
            Query query = ((DefTableModel) table.getModel()).getQuery();
            for (int i = 0; i < query.size(); ++i) {
                if (query.get(i).getInt(1) == id) {
                    UGui.setSelectedIndex(table, i);
                    UGui.scrollRectToRow(i, table);
                    return;
                }
            }
        }
        UGui.setSelectedRow(table);
    }

    //�������� ������ �� �����
    public static void setSelectedKey(JTable table, Query query, int id) {
        if (id != -1 && id != -3) {
            for (int i = 0; i < query.size(); ++i) {
                if (query.get(i).getInt(1) == id) {
                    UGui.setSelectedIndex(table, i);
                    UGui.scrollRectToRow(i, table);
                    return;
                }
            }
        }
        UGui.setSelectedRow(table);
    }

    public static int getIndexFind(JTable table, Field field, Object val) {
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

    //�������� convertRowIndexToModel
    public static int getIndexRec(JTable table) {
        if (table.getSelectedRow() != -1) {
            return table.convertRowIndexToModel(table.getSelectedRow());
        }
        return -1;
    }

    //�������� convertRowIndexToModel
    public static int getIndexRec(JTable table, int def) {
        if (table.getSelectedRow() != -1) {
            return table.convertRowIndexToModel(table.getSelectedRow());
        }
        return def;
    }

    //����� Record � ������ �� row table
    public static Record findRecordModel(Query q, JTable table, int row) {
        int id = (int) table.getValueAt(row, table.getColumnCount() - 1);
        return q.stream().filter(rec -> rec.getInt(1) == 1).findFirst().orElse(null);
    }

    //�������� ������
    public static void insertRecordCur(JTable table, Field field, ListenerRecord listener) {

        int index = UGui.getIndexRec(table);
        index = (index == -1) ? 0 : index;
        Query query = ((DefTableModel) table.getModel()).getQuery();
        Record record = field.newRecord(Query.INS);
        record.setNo(field.fields()[1], Conn.genId(field));

        if (++index <= table.getRowCount()) {
            query.add(index, record);
        } else {
            query.add(--index, record);
        }

        field.query().add(record);  //������� ������ � ���
        listener.action(record);

        ((DefaultTableModel) table.getModel()).fireTableRowsInserted(index, index);
        UGui.setSelectedIndex(table, index);
        UGui.scrollRectToIndex(index, table);
    }

    //�������� ������
    public static void insertRecordEnd(JTable table, Field field, ListenerRecord listener) {

        Query query = ((DefTableModel) table.getModel()).getQuery();
        Record record = field.newRecord(Query.INS);
        record.setNo(field.fields()[1], Conn.genId(field));

        listener.action(record);

        query.add(record);  //������� ������ � ������
        field.query().add(record);  //������� ������ � ���

        ((DefaultTableModel) table.getModel()).fireTableRowsInserted(query.size() - 1, query.size() - 1);
        UGui.setSelectedIndex(table, query.size() - 1);
        UGui.scrollRectToIndex(query.size() - 1, table);
    }

    //�������� ������
    public static void updateRecord(JTable table, ListenerRecord listener) {
        Record record = ((DefTableModel) table.getModel()).getQuery().get(UGui.getIndexRec(table));
        listener.action(record);
        ((DefaultTableModel) table.getModel()).fireTableRowsUpdated(table.getSelectedRow(), table.getSelectedRow());
    }

    //������� ������
    public static void deleteRecord(JTable table) {
        if (table.getSelectedRow() != -1) {
            Query query = ((DefTableModel) table.getModel()).getQuery();
            int row = table.getSelectedRow();
            int index = UGui.getIndexRec(table);
            Record record = query.get(index);
            record.set(0, Query.DEL);
            if (query.delete(record)) {
                Field f = query.fields().get(0); //Field ������

                query.removeRec(index);
                delDomainRec(f.query(), record.getInt(1));

                ((DefTableModel) table.getModel()).fireTableRowsDeleted(row, row);
                row = (row > 0) ? --row : 0;
                if (query.size() > 0) {
                    index = table.convertRowIndexToModel(row);
                    UGui.setSelectedIndex(table, index);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "�� ���� �� ������� �� �������", "��������������", JOptionPane.NO_OPTION);
        }
    }

    //�������� ������������ �������� �������
    public static int isDeleteRecord(JTable table, java.awt.Window owner, JTable... tables) {
        ImageIcon img = new ImageIcon(owner.getClass().getResource("/resource/img24/c014.gif"));
        if (table.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "�� ���� �� ������� �� ����������", "��������������", JOptionPane.NO_OPTION, img);
            return 1;
        }
        for (JTable tab : tables) {
            if (tab.getRowCount() != 0) {
                JOptionPane.showMessageDialog(owner, "����� ��������� ������, ������� ������ � ��������� ��������", "��������������", JOptionPane.NO_OPTION, img);
                return 1;
            }
        }
        //return JOptionPane.showConfirmDialog(owner, "�� �������, ��� ������ ������� ������� ������?", "�������������", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return JOptionPane.showConfirmDialog(owner, "�� ������������� ������ ������� ������� ������?", "�������������", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    //�������� ����� ������ � �����t   
    public static Record addDomainRec(Field field) {
        Record record = field.newRecord(Query.INS);
        field.query().add(record);
        return record;
    }

    public static boolean upDomainRec() {
        return false;
    }

    //������� ������ � ������
    public static boolean delDomainRec(Query query, int id) {
        for (int i = 0; i < query.size(); i++) {
            Record record = query.get(i);
            if (id == record.getInt(1)) {
                query.remove(i);
                return true;
            }
        }
        return false;
    }

    //���������� ������ � ������� JTable
    public static void fireTableRowUpdated(JTable table) {
        ((DefaultTableModel) table.getModel()).fireTableRowsUpdated(table.getSelectedRow(), table.getSelectedRow());
    }

    //���������� border � ��������� sql
    public static void updateBorderAndSql(JTable table, List<JTable> tabList) {
        if (tabList != null) {
            tabList.forEach(tab -> tab.setBorder(null));
            tabList.forEach(tab -> {
                if (tab != table) {
                    if (tab.isEditing()) {
                        UGui.stopCellEditing(tab);
                    }
                    if (tab.getModel() instanceof DefTableModel) {
                        ((DefTableModel) tab.getModel()).getQuery().execsql();
                    }
                }
            });
        }
        table.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 255)));
    }

    //�������� Query �� �������
    public static Query getQuery(JTable table) {
        return ((DefTableModel) table.getModel()).getQuery();
    }

    //�������� �������
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

    //������������ ������ � ������ �������
    public static JButton buttonCellEditor(JTable table, int column) {
        JButton btn = new JButton("...");
        table.getColumnModel().getColumn(column).setCellEditor(new DefCellEditorBtn(btn));
        return btn;
    }

    //������������ ������ � ������ �������
    public static JButton buttonCellEditor(JTable table, int column, ListenerObject listenerCell) {
        JButton btn = new JButton("...");
        table.getColumnModel().getColumn(column).setCellEditor(new DefCellEditorBtn(listenerCell, btn));
        return btn;
    }

    //��������� ����� ��������������
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

    //�������������� ���������� ������
    public static void cellParamNameOrValue(Record record, JTable table, Field id, Field text) {
        try {
            Query query = ((DefTableModel) table.getModel()).getQuery();
            Record record2 = query.get(UGui.getIndexRec(table));

            //�������� ����������������� ���������
            if (eGroups.values().length == record.size()) {
                record2.set(id, record.getInt(eGroups.id));
                record2.set(text, "");

                //�������� ���������� ��������� �� PalList
            } else if (record.size() == 2) {
                record2.set(id, record.getInt(0));
                record2.set(text, ParamList.find(record.getInt(0)).def());

                //������ �������� � ������
            } else if (record.size() == 1) {
                String val = record2.getStr(text);

                //�������� ������ � ������
                if (record.get(0) == null) {
                    record2.set(text, "");

                    //���������� ������ � ������
                } else if (val != null && val.isEmpty() == false) {
                    record2.set(text, val + ";" + record.getStr(0));

                } else {
                    record2.set(text, record.getStr(0)); //???
                }
            }
            int index = UGui.getIndexRec(table);
            ((DefaultTableModel) table.getModel()).fireTableRowsUpdated(index, index);
        } catch (Exception e) {
            System.err.println("������:UGui.cellParamNameOrValue() " + e);
        }
    }

    //�������������� ���� ������ � ���� ������ ������� 
    //componentCell - DefCellEditorBtn ���� String ��. ����� DefCellEditorBtn
    public static boolean cellParamTypeOrVid(JTable table, Object componentCell, Field groups_id) {
        try {
            Query qXxxpar = ((DefTableModel) table.getModel()).getQuery();
            int groupsID = qXxxpar.getAs(UGui.getIndexRec(table), groups_id);

            //���� ��������� ����� DefCellEditorBtn
            //��������� ��� � ��� ������
            if (componentCell instanceof DefCellEditorBtn) {
                DefCellEditorBtn editor = (DefCellEditorBtn) componentCell;

                //���������������� ������ ����������
                if (groupsID < 0) {
                    editor.getButton().setVisible(true);
                    editor.getTextField().setEnabled(false);

                    //�������� ���������
                } else {
                    Enam enam = ParamList.find(groupsID);

                    //���������, ����� �� �����������
                    if (enam.dict() != null) {
                        editor.getButton().setVisible(true);
                        editor.getTextField().setEnabled(false);

                        //��������� �������� �������������
                    } else {
                        editor.getButton().setVisible(false);
                        editor.getTextField().setEnabled(true);
                        editor.getTextField().setEditable(true);
                    }
                }

                //���� ��������� ������ �����, ��� �������� �� ����������� �����
                //���������������� �������� ������� ���� �� ����� � �� �����������
            } else if (groupsID > 0 && componentCell != null && componentCell instanceof String) {
                String txt = (String) componentCell;
                return ParamList.find(qXxxpar.getAs(UGui.getIndexRec(table), groups_id)).check(txt);
            }
        } catch (Exception e) {
            System.err.println("������:UGui.cellParamTypeOrVid() " + e);
        }
        return true;
    }

    //�������������� ��������� ��������
    public static void cellParamColor(Record record, JTable table, Field color_fk, Field color_us, JTable... tables) {
        UGui.stopCellEditing(tables);
        int index = UGui.getIndexRec(table);
        Query query = ((DefTableModel) table.getModel()).getQuery();
        Record detailRec = query.get(index);
        int group = (eGroups.values().length == record.size()) ? record.getInt(eGroups.id) : record.getInt(eColor.id);
        detailRec.set(color_fk, group);

        if (group == 0 || group == 100000) { //����������/����.������
            int val = UseColor.PROF.id + (UseColor.PROF.id << 4) + (UseColor.PROF.id << 8);
            detailRec.set(color_us, val);

        } else if (group > 0) { //������� � ������
            detailRec.set(color_us, 0);

        } else { //��������� ������������
            int val = UseColor.PROF.id + (UseColor.PROF.id << 4) + (UseColor.PROF.id << 8);
            detailRec.set(color_us, val);
        }
        ((DefaultTableModel) table.getModel()).fireTableRowsUpdated(index, index);
        UGui.setSelectedIndex(table, index);
    }

    public static void cellParamEnum(Record record, JTable table, Field field_fk, JTable... tables) {
        UGui.stopCellEditing(tables);
        Query query = ((DefTableModel) table.getModel()).getQuery();
        int index = UGui.getIndexRec(table);
        query.set(record.getInt(0), UGui.getIndexRec(table), field_fk);
        ((DefaultTableModel) table.getModel()).fireTableDataChanged();
        UGui.setSelectedIndex(table, index);
    }

    //����������� ���� �� ����������
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

    //������ ��� ������ �����, ��������, �������� � �������  
    public static Query artTypeToFurndetList(int furnitureID, Query qArtikl) {
        try {
            HashSet<Integer> filterSet = new HashSet<Integer>();
            Query qResult = new Query(eArtikl.values());
            Query qFurndet = new Query(eFurndet.values()).sql(eFurndet.data(), eFurndet.up); //��� ����������� ���������

            //���� �����������
            for (Record furndetRec1 : qFurndet) { //������ �������
                if (furndetRec1.getInt(eFurndet.furniture_id1) == furnitureID) {

                    //������ �� ����������� ������������ ���� ����������� ���������
                    if (furndetRec1.get(eFurndet.furniture_id2) == null) { //�� �����
                        filterSet.add(furndetRec1.getInt(eFurndet.artikl_id));

                    } else { //��� �����
                        for (Record furndetRec2 : qFurndet) { //������ �������
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
            System.err.println("������: frames.artTypeToFurndetList " + e);
            return null;
        }
    }

    //��� ��������� ����� ������� artiklID
    public static HashSet<Record> artiklToColorSet(int artiklID) {
        HashSet<Record> colorSet = new HashSet<Record>();
        Query artdetList = new Query(eArtdet.values()).sql(eArtdet.data(), eArtdet.artikl_id, artiklID);
        artdetList.stream().forEach(rec -> {

            if (rec.getInt(eArtdet.color_fk) < 0) {
                eColor.data().forEach(rec2 -> {
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

    //��� ��������� ����� ������� artiklID
    public static HashSet<Record> artiklToColorSet(int artiklID, int side) {
        HashSet<Record> colorSet = new HashSet<Record>();
        Field field = (side == 1) ? eArtdet.mark_c1 : (side == 2) ? eArtdet.mark_c2 : eArtdet.mark_c3;
        Query artdetList = new Query(eArtdet.values()).sql(eArtdet.data(), eArtdet.artikl_id, artiklID, field, 1);
        artdetList.stream().forEach(rec -> {

            if (rec.getInt(eArtdet.color_fk) < 0) {
                eColor.data().forEach(rec2 -> {
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

    //�������� �� ����������� �����
    public static void setDocumentFilter(int pattern, JTextField... txtField) {
        for (JTextField txtField2 : txtField) {
            ((PlainDocument) txtField2.getDocument()).setDocumentFilter(new DocumentFilter() {

                public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                    if (string != null && string.length() > 1 || UCom.check(string, pattern)) { //�������� �� ����������� �����
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

    public static void changePpmTree(JTree winTree, JPopupMenu ppm, Com5t com5t) {
        winTree.setComponentPopupMenu(null);

        if (com5t.type == Type.GLASS && com5t.owner.type != Type.STVORKA) {
            winTree.setComponentPopupMenu(ppm);
            boolean b[] = {true, false, true, false, false};
            List.of(0, 1, 2, 3, 4).forEach(i -> ppm.getComponent(i).setVisible(b[i]));

        } else if (enums.Type.IMPOST == com5t.type) {
            winTree.setComponentPopupMenu(ppm);
            boolean b[] = {false, true, false, false, false};
            List.of(0, 1, 2, 3, 4).forEach(i -> ppm.getComponent(i).setVisible(b[i]));

        } else if (enums.Type.STVORKA == com5t.type) {
            winTree.setComponentPopupMenu(ppm);
            boolean b[] = {false, false, false, true, false};
            List.of(0, 1, 2, 3, 4).forEach(i -> ppm.getComponent(i).setVisible(b[i]));

        } else if (enums.Type.MOSQUIT == com5t.type) {
            winTree.setComponentPopupMenu(ppm);
            boolean b[] = {false, false, false, false, true};
            List.of(0, 1, 2, 3, 4).forEach(i -> ppm.getComponent(i).setVisible(b[i]));
        }
    }
    
    public static void PRINT(Field field, Record record) {
        List list = new ArrayList();
        for (Field f : field.fields()) {
            list.add(f.meta().fname + "=" + record.get(f));
        }
        System.out.println(list);
    }
}
