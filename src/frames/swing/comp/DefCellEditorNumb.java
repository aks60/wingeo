package frames.swing.comp;

import common.UCom;
import dataset.Field;
import frames.UGui;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class DefCellEditorNumb extends DefaultCellEditor {

    private int scale = 0;
    private int patt = 0;
    private DecimalFormat df = null;

    public DefCellEditorNumb(int scale) {
        super(new JTextField());
        this.scale = scale;
        this.patt = 3;
        init();
        JTextComponent editorField = (JTextComponent) editorComponent;
        PlainDocument doc = (PlainDocument) editorField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.length() > 1 || UCom.check(string, patt)) { //проверка на коррекность ввода
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                if (string != null && string.length() > 1 || UCom.check(string, patt)) {  //проверка на коррекность ввода
                    super.replace(fb, offset, length, string, attrs);
                }
            }
        });        
    }

    public DefCellEditorNumb(String pattern) {
        super(new JTextField());
        this.patt = Integer.parseInt(pattern);
        init();
        JTextComponent editorField = (JTextComponent) editorComponent;
        PlainDocument doc = (PlainDocument) editorField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.length() > 1 || UCom.check(string, patt)) { //проверка на коррекность ввода
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                if (string != null && string.length() > 1 || UCom.check(string, patt)) {  //проверка на коррекность ввода
                    super.replace(fb, offset, length, string, attrs);
                }
            }
        });
    }

    public DefCellEditorNumb(DecimalFormat df) {
        super(new JTextField());
        this.df = df;
        init();
    }

    private void init() {
        editorComponent.setFont(frames.UGui.getFont(0, 0));
        editorComponent.setPreferredSize(new java.awt.Dimension(60, 18));
        editorComponent.setBorder(null);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        try {
            Field field = ((DefTableModel) table.getModel()).columns[column];
            Object val = value;
            if (value instanceof Double || value instanceof Double) {
                 val = (df != null) ? df.format(value) : UCom.format(value, scale);
            } else if (field.meta().type() == Field.TYPE.DATE) {
                 val = UGui.convert2Date(value);
            }
            return super.getTableCellEditorComponent(table, val, isSelected, row, column);

        } catch (Exception e) {
            System.err.println("Ошибка:DefCellEditor.getTableCellEditorComponent() " + e);
            return null;
        }
    }
}
