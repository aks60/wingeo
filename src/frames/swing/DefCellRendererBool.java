package frames.swing;

import java.awt.Component;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

public class DefCellRendererBool extends JCheckBox implements TableCellRenderer, UIResource {

    private Icon ico = new javax.swing.ImageIcon(getClass().getResource("/resource/img16/b053.gif"));
    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public DefCellRendererBool() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setBorderPainted(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        if (value instanceof Integer) {
            value = (Integer.valueOf(value.toString()) == 0) ? false : true;
        } else if (value instanceof Double) {
            value = (Double.valueOf(value.toString()) == 0) ? false : true;
        } else if (value instanceof Double) {
            value = (Double.valueOf(value.toString()) == 0) ? false : true;
        }
        setSelected(value.equals(true) || value.equals("true"));
        setIcon((value.equals(true) || value.equals("true")) ? ico : null);

        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(noFocusBorder);
        }

        setBorderPaintedFlat(true);
        return this;
    }
}
