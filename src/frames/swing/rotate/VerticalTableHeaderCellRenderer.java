package frames.swing.rotate;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.UIManager;

/*
TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();
Enumeration columns = table.getColumnModel().getColumns();
while (columns.hasMoreElements()) {((TableColumn) columns.nextElement()).setHeaderRenderer(headerRenderer);}
 */
//https://tips4java.wordpress.com/2009/03/06/vertical-table-header-cell-renderer/
public class VerticalTableHeaderCellRenderer
        extends DefaultTableHeaderCellRenderer {

    public VerticalTableHeaderCellRenderer() {
        setHorizontalAlignment(LEFT);
        setHorizontalTextPosition(CENTER);
        setVerticalAlignment(CENTER);
        setVerticalTextPosition(TOP);
        setUI(new VerticalLabelUI(true));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    }

    @Override
    protected Icon getIcon(JTable table, int column) {
        SortKey sortKey = getSortKey(table, column);
        if (sortKey != null && table.convertColumnIndexToView(sortKey.getColumn()) == column) {
            SortOrder sortOrder = sortKey.getSortOrder();
            switch (sortOrder) {
                case ASCENDING:
                    return VerticalSortIcon.ASCENDING;
                case DESCENDING:
                    return VerticalSortIcon.DESCENDING;
            }
        }
        return null;
    }

    private enum VerticalSortIcon implements Icon {

        ASCENDING(UIManager.getIcon("Table.ascendingSortIcon")),
        DESCENDING(UIManager.getIcon("Table.descendingSortIcon"));
        private final Icon icon;// = ;

        private VerticalSortIcon(Icon icon) {
            this.icon = icon;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int maxSide = Math.max(getIconWidth(), getIconHeight());
            Graphics2D g2 = (Graphics2D) g.create(x, y, maxSide, maxSide);
            g2.rotate((Math.PI / 2));
            g2.translate(0, -maxSide);
            icon.paintIcon(c, g2, 0, 0);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return icon.getIconHeight();
        }

        @Override
        public int getIconHeight() {
            return icon.getIconWidth();
        }
    }
}
