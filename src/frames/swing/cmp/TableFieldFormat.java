/*
 * Связывание и форматирование полей JTextField с моделью данных
 */
package frames.swing.cmp;

import common.UCom;
import frames.UGui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import dataset.Field;
import dataset.Query;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * <p>
 * Визуализация полей </p>
 */
public class TableFieldFormat {

    private JComponent comp = null;
    private Query query = null;
    private HashMap<JTextComponent, Field> mapTxt = new HashMap(16);
    public static boolean update = false;

    //Конструктор
    public TableFieldFormat(JTree comp) {
        this.comp = comp;
    }

    //Конструктор
    public TableFieldFormat(JTable comp) {
        this.comp = comp;
        this.query = ((DefTableModel) comp.getModel()).getQuery();
    }

    //Конструктор
    public TableFieldFormat(JTable comp, Query query) {
        this.comp = comp;
        this.query = query;
    }

    //Добавить компонент отображения
    public void add(Field field, JTextComponent jtxt) {
        mapTxt.put(jtxt, field);

        if (field.meta().edit() == false) { //если редактирование запрещено
            jtxt.setEditable(false);
            jtxt.setBackground(new java.awt.Color(255, 255, 255));
        } else {
            jtxt.getDocument().addDocumentListener(new DocListiner(jtxt));
            PlainDocument doc = (PlainDocument) jtxt.getDocument();

            if (jtxt.getName() != null && jtxt.getName().isEmpty() == false
                    && List.of("{2}", "{3}", "{4}", "{5}", "{6}").contains(jtxt.getName()) == true) {

                int pattern = Integer.parseInt(jtxt.getName().substring(1, jtxt.getName().length() - 1));

                doc.setDocumentFilter(new DocumentFilter() {

                    @Override
                    public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                        if (string != null && string.length() > 1 || UCom.check(string, pattern)) { //проверка на коррекность ввода
                            super.insertString(fb, offset, string, attr);
                        }
                    }

                    @Override
                    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                        if (string != null && string.length() > 1 || UCom.check(string, pattern)) {  //проверка на коррекность ввода
                            super.replace(fb, offset, length, string, attrs);
                        }
                    }
                });

            }
        }
    }

    //Очистить текст
    public void clear() {
        try {
            update = false;
            for (Map.Entry<JTextComponent, Field> me : mapTxt.entrySet()) {
                JTextComponent textcomp = me.getKey();
                Field field = me.getValue();
                textcomp.setText(null);
                if (field.meta().type().equals(Field.TYPE.STR)) {
                    textcomp.setText("");
                } else {
                    textcomp.setText("0");
                }
            }
        } finally {
            update = true;
        }
    }

    //Загрузить данные в компоненты
    public void load() {
        try {
            if (comp instanceof JTable) {
                load(UGui.getIndexRec((JTable) comp));

            } else if (comp instanceof JTree) {
                DefMutableTreeNode node = (DefMutableTreeNode) ((JTree) comp).getLastSelectedPathComponent();
                update = false;
                try {
                    for (Map.Entry<JTextComponent, Field> me : mapTxt.entrySet()) {
                        JTextComponent jtxt = me.getKey();
                        Field field = me.getValue();
                        Object val = node.rec().get(field);
                        setText(jtxt, field, val);
                    }
                } finally {
                    update = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка:TableFieldFormat.load1() " + e);
        }
    }

    //Загрузить данные в компоненты
    public void load(Integer index) {
        try {
            update = false;
            try {
                if (index != null && index != -1) {
                    for (Map.Entry<JTextComponent, Field> me : mapTxt.entrySet()) {
                        JTextComponent jtxt = me.getKey();
                        Field field = me.getValue();
                        Object val = query.table(field).get(index, field);
                        setText(jtxt, field, val);
                    }
                }
            } finally {
                update = true;
            }
        } catch (Exception e) {
            System.err.println("Ошибка:TableFieldFormat.load2() " + e);
        }
    }

    private void setText(JTextComponent jtxt, Field field, Object val) {
        try {
            if (val == null || (val != null && val.toString().isEmpty())) {
                Object o = (field.meta().type().equals(Field.TYPE.STR) == true) ? "" : 0;
                jtxt.setText(o.toString());

            } else if (field.meta().type().equals(Field.TYPE.DATE)) {
                jtxt.setText(UGui.convert2Date(val));

            } else if (field.meta().type().equals(Field.TYPE.DBL)) {
                val = String.valueOf(val).replace(',', '.');
                double v = Double.parseDouble(val.toString());
                jtxt.setText(UCom.format(v, 3));

            } else if (field.meta().type().equals(Field.TYPE.FLT)) {
                val = String.valueOf(val).replace(',', '.');
                double v = Double.parseDouble(val.toString());
                jtxt.setText(UCom.format(v, 3));

            } else {
                jtxt.setText(val.toString());
            }
            jtxt.getCaret().setDot(1);

        } catch (Exception e) {
            System.err.println("Ошибка:TableFieldFormat.setText() " + e);
        }
    }

//Редактирование
    class DocListiner implements DocumentListener, ActionListener {

        private JTextComponent jtxt;

        DocListiner(JTextComponent jtxt) {
            this.jtxt = jtxt;
        }

        public void actionPerformed(ActionEvent e) {
            fieldUpdate();
        }

        public void insertUpdate(DocumentEvent e) {
            fieldUpdate();
        }

        public void removeUpdate(DocumentEvent e) {
            fieldUpdate();
        }

        public void changedUpdate(DocumentEvent e) {
            fieldUpdate();
        }

        //При редактированиии одного из полей
        public void fieldUpdate() {
            try {
                if (update == true) {
                    if (comp instanceof JTable) {
                        int index = UGui.getIndexRec((JTable) comp);
                        if (index != -1) {
                            Field field = mapTxt.get(jtxt);
                            Object str = jtxt.getText();

                            if (((JTable) comp).getRowCount() > 0) {
                                if (List.of(Field.TYPE.FLT, Field.TYPE.DBL).contains(field.meta().type())) {
                                    str = String.valueOf(str).replace(',', '.');
                                }
                                query.table(field).set(str, index, field);
                            }
                        }
                    } else if (comp instanceof JTree) {
                        DefMutableTreeNode node = (DefMutableTreeNode) ((JTree) comp).getLastSelectedPathComponent();
                        Field field = mapTxt.get(jtxt);
                        Object str = jtxt.getText();

                        if (List.of(Field.TYPE.FLT, Field.TYPE.DBL).contains(field.meta().type())) {
                            str = String.valueOf(str).replace(',', '.');
                        }
                        node.rec().set(field, str);
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка:TableFieldFormat.DocListiner.fieldUpdate() " + e);
            }
        }
    }
}
