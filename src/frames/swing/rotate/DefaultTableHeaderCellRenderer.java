package frames.swing.rotate;

import java.awt.Component;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;


//table.getTableHeader().setDefaultRenderer(new DefaultTableHeaderCellRenderer());
//https://tips4java.wordpress.com/2009/02/27/default-table-header-cell-renderer/
public class DefaultTableHeaderCellRenderer extends DefaultTableCellRenderer {

    public DefaultTableHeaderCellRenderer() {
        setHorizontalAlignment(CENTER);
        setHorizontalTextPosition(LEFT);
        setVerticalAlignment(BOTTOM);
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        JTableHeader tableHeader = table.getTableHeader();
        if (tableHeader != null) {
            setForeground(tableHeader.getForeground());
        }
        setIcon(getIcon(table, column));
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return this;
    }

    protected Icon getIcon(JTable table, int column) {
        SortKey sortKey = getSortKey(table, column);
        if (sortKey != null && table.convertColumnIndexToView(sortKey.getColumn()) == column) {
            switch (sortKey.getSortOrder()) {
                case ASCENDING:
                    return UIManager.getIcon("Table.ascendingSortIcon");
                case DESCENDING:
                    return UIManager.getIcon("Table.descendingSortIcon");
            }
        }
        return null;
    }


    protected SortKey getSortKey(JTable table, int column) {
        RowSorter rowSorter = table.getRowSorter();
        if (rowSorter == null) {
            return null;
        }

        List sortedColumns = rowSorter.getSortKeys();
        if (sortedColumns.size() > 0) {
            return (SortKey) sortedColumns.get(0);
        }
        return null;
    }
}
