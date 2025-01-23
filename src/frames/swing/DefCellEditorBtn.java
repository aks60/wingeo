package frames.swing;

import common.ePref;
import dataset.Field;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import common.listener.ListenerObject;
import frames.UGui;
import static javax.swing.BorderFactory.createEtchedBorder;
import javax.swing.border.EtchedBorder;

public class DefCellEditorBtn extends DefaultCellEditor {

    private ListenerObject listenerCell = null;
    private JComponent panel = new javax.swing.JPanel();
    private JButton button = null;

    public DefCellEditorBtn(JButton button) {
        super(new JTextField());
        init(false);
        button(button);
        this.getComponent().setLocale(ePref.locale);
    }

    public DefCellEditorBtn(ListenerObject listenerCell, JButton button) {
        super(new JTextField());
        this.listenerCell = listenerCell;
        init(false);
        button(button);
        filter();
        this.getComponent().setLocale(ePref.locale);
    }

    private void init(boolean editable) {
        JTextField editorText = (JTextField) editorComponent;
        panel.setBorder(null);
        panel.setBackground(new java.awt.Color(240, 240, 240));
        panel.setLayout(new java.awt.BorderLayout());
        editorText.setFont(frames.UGui.getFont(0, 0));
        editorText.setPreferredSize(new java.awt.Dimension(60, 18));
        editorText.setEditable(editable);
        editorText.setBorder(null);
        editorText.setBackground(new java.awt.Color(255, 255, 255));
        panel.add(editorText, java.awt.BorderLayout.CENTER);
    }

    private void button(JButton button) {
        setClickCountToStart(2);
        this.button = button;
        button.setBorder(createEtchedBorder(EtchedBorder.RAISED));
        button.setFocusable(false);
        button.setPreferredSize(new java.awt.Dimension(24, 18));
        panel.add(button, java.awt.BorderLayout.EAST);
    }

    private void filter() {

        JTextField editorText = (JTextField) editorComponent;
        PlainDocument doc = (PlainDocument) editorText.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (listenerCell != null) {
                    if (string.length() > 1 || listenerCell.action(string)) { //проверка на коррекность ввода
                        super.insertString(fb, offset, string, attr);
                    }
                } else {
                    if (string.length() > 1) { //проверка на коррекность ввода
                        super.insertString(fb, offset, string, attr);
                    }
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                if (listenerCell != null) {
                    if (string.length() > 1 || listenerCell.action(string)) {  //проверка на коррекность ввода
                        super.replace(fb, offset, length, string, attrs);
                    }
                } else {
                    if (string.length() > 1) {  //проверка на коррекность ввода
                        super.replace(fb, offset, length, string, attrs);
                    }
                }
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        Field field = ((DefTableModel) table.getModel()).columns[column];
        if (((JTextField) editorComponent).isEditable() == false) {
            ((JTextField) editorComponent).setEditable(field.meta().type() == Field.TYPE.STR); //разрешить редактирование стрингу
        }
        if (field.meta().type() == Field.TYPE.DATE) {
            value = UGui.convert2Date(value);
        }
        delegate.setValue(value);
        return panel;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent == true) {
            if (listenerCell != null && ((MouseEvent) anEvent).getClickCount() == 2) {
                listenerCell.action(this);
            }
        }
        return delegate.isCellEditable(anEvent);
    }

    public JButton getButton() {
        return button;
    }

    public JTextField getTextField() {
        return (JTextField) editorComponent;
    }
}
