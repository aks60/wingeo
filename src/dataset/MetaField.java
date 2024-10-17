package dataset;

import dataset.Field.TYPE;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Date;

/**
 *
 * @author Aksenov Sergey
 *
 * <p>
 * ���������� �������</p>
 */
public class MetaField {

    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private boolean isnull = false; //��������� null � �������
    private Field field = null; //enum
    public String fname = null; //��������� ��� ��������������� ��� ����
    public String descr = ""; //�������� ����
    private TYPE type = TYPE.OBJ; //��� ����
    private Integer size = 0; //������ ����
    private Field.EDIT edit = Field.EDIT.TRUE; //��������� �������������� � ���������� ����������

    public MetaField(Field e) {
        field = e;
        if (e instanceof Enum) {
            this.fname = ((Enum) e).name(); //��� ���� �� ���������
        }
    }

    public void init(Object... p) {

        if (p.length < 5) {
            System.err.println("MetaField.init() - ������! ���������� ���������� ������ 4. ���� <" + p[0].toString() + ">");
            return;
        }
        type(Integer.valueOf(p[Entity.type.ordinal()].toString())); //��� ����
        this.size = Integer.valueOf(p[Entity.size.ordinal()].toString());
        this.isnull = p[Entity.nullable.ordinal()].equals("1"); //��������� null
        this.descr = p[Entity.comment.ordinal()].toString(); //�������� ���� 
        this.fname = p[Entity.fname.ordinal()].toString(); //��������������� ����� ����
    }

    /**
     * �������� ����� ��� ����������� ����� ������ �������������
     */
    public String validateField(Object value) {

        if (fname == null || size == 0) {
            return null;
        }
        try {
            if (value == null && isnull == true) {
                return null;
            } else if (value == null && isnull == false) {
                return "���� <" + descr + "> ������ ����� ��������";
            } else if (type.type == Integer.class) {
                Integer.valueOf(String.valueOf(value));
            } else if (type.type == Double.class) {
                String str = String.valueOf(value).replace(',', '.');
                Double.valueOf(str);
            } else if (type.type == Double.class) {
                String str = String.valueOf(value).replace(',', '.');
                Double.valueOf(str);
            } else if (type.type == Date.class) {
                if (value instanceof Date) {
                    return null;
                } else {
                    try {
                        dateFormat.parse(value.toString());
                    } catch (ParseException e) {
                        return "���� <" + descr + "> ��������� �� ���������";
                    }

                }
            } else if (value.toString().length() > size) {
                return "���� <" + descr + "> �� ������ ����� ������ "
                        + String.valueOf(size) + " ������";
            }
        } catch (Exception e) {
            return "���� <" + descr + "> ��������� �� ���������";
        }
        return null;
    }

    public void descr(String descr) {
        this.descr = descr;
    }
    
    public String descr() {
        return descr;
    }

    public void isnull(boolean emty) {
        this.isnull = emty;
    }

    public boolean isnull() {
        return isnull;
    }

    public void type(int type) {

        for (TYPE type2 : TYPE.values()) {
            if (type2.hsConv.contains(type) == true) {
                this.type = type2;
            }
        }
    }

    public TYPE type() {
        return type;
    }

    public boolean edit() {
        return edit.edit;
    }

    public void size(Integer size) {
        this.size = size;
    }

    public Integer size() {
        return size;
    }

    public int width() {
        return size;
    }
}
