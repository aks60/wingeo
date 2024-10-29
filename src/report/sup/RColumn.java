package report.sup;

import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import dataset.Field;


public class RColumn {

    //�������� ����
    private DefaultMutableTreeNode parent;
    //��� ���� ������� � ������������������ ������ �� ������
    private ArrayList<Field> fields = new ArrayList<Field>();

    RColumn(Field field, DefaultMutableTreeNode parent) {
        this.parent = parent;
        fields.add(field);
    }

    public DefaultMutableTreeNode parent() {
        return parent;
    }

    public ArrayList<Field> fields() {
        return fields;
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < fields.size(); i++) {
            if (i > 1) {
                str += fields.get(i).meta().fname+ "; ";
            } else if (i == 1) {
                str += "; " + fields.get(i).meta().fname + "; ";
            } else if (i == 0) {
                str += fields.get(i).meta().fname;
            }
        }
        return str;
    }
}