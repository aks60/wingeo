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
 * Методанные таблицы</p>
 */
public class MetaField {

    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private boolean isnull = false; //запретить null в таблице
    private Field field = null; //enum
    public String fname = null; //служебное для переопределения имя поля
    public String descr = ""; //описание поля
    private TYPE type = TYPE.OBJ; //тип поля
    private Integer size = 0; //размер поля
    private Field.EDIT edit = Field.EDIT.TRUE; //запретить редактирование в визуальном компоненте

    public MetaField(Field e) {
        field = e;
        if (e instanceof Enum) {
            this.fname = ((Enum) e).name(); //имя поля по умолчанию
        }
    }

    public void init(Object... p) {

        if (p.length < 5) {
            System.err.println("MetaField.init() - ОШИБКА! Количество параметров меньше 4. Поле <" + p[0].toString() + ">");
            return;
        }
        type(Integer.valueOf(p[Entity.type.ordinal()].toString())); //тип поля
        this.size = Integer.valueOf(p[Entity.size.ordinal()].toString());
        this.isnull = p[Entity.nullable.ordinal()].equals("1"); //разрешено null
        this.descr = p[Entity.comment.ordinal()].toString(); //описание поля 
        this.fname = p[Entity.fname.ordinal()].toString(); //переопределение имени поля
    }

    /**
     * Проверка нужна для корректного ввода данных пользователем
     */
    public String validateField(Object value) {

        if (fname == null || size == 0) {
            return null;
        }
        try {
            if (value == null && isnull == true) {
                return null;
            } else if (value == null && isnull == false) {
                return "Поле <" + descr + "> должно иметь значение";
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
                        return "Поле <" + descr + "> заполнено не корректно";
                    }

                }
            } else if (value.toString().length() > size) {
                return "Поле <" + descr + "> не должно иметь больше "
                        + String.valueOf(size) + " знаков";
            }
        } catch (Exception e) {
            return "Поле <" + descr + "> заполнено не корректно";
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
