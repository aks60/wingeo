package frames.swing;

import common.UCom;
import frames.UGui;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DefCellRendererNumb extends DefaultTableCellRenderer {

    private DecimalFormat df = null;
    protected int scale = 2;
    
    private String pattern = null;

    public DefCellRendererNumb(int scale) {
        this.scale = scale;
    }

    public DefCellRendererNumb(String pattern) {
        this.pattern = pattern;
    }

    public DefCellRendererNumb(DecimalFormat df) {
        this.df = df;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        try {
            if (value instanceof java.util.Date) {
                return super.getTableCellRendererComponent(table, UGui.simpleFormat.format(value), isSelected, hasFocus, row, column);
            }
            if (value instanceof Double) {
                String val = (df != null) ? df.format(value) : (pattern != null) ? UCom.format(value, pattern) : UCom.format(value, scale);
                return super.getTableCellRendererComponent(table, val, isSelected, hasFocus, row, column);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
        } catch (Exception e) {
            System.err.println("Ошибка:DefCellRendererNumb.getTableCellRendererComponent() " + e);
            return null;
        }
    }
}
