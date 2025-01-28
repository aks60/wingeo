package frames.swing.cmp;

import common.UCom;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class DefCellEditorCheck extends DefaultCellEditor {

    private int pattern = 0;

    public DefCellEditorCheck(int pattern) {
        super(new JTextField());
        this.pattern = pattern;
        editorComponent.setFont(frames.UGui.getFont(0, 0));
        editorComponent.setPreferredSize(new java.awt.Dimension(60, 18));
        editorComponent.setBorder(null);
        filter();
    }

    private void filter() {

        JTextField editorText = (JTextField) editorComponent;
        PlainDocument doc = (PlainDocument) editorText.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.length() > 1 || UCom.check(string, pattern)) { //проверка на коррекность ввода
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
                if (string.length() > 1 || UCom.check(string, pattern)) {  //проверка на коррекность ввода
                    super.replace(fb, offset, length, string, attrs);
                }
            }
        });
    }
}
