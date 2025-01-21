package dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

public class Table extends ArrayList<Record> {

    protected Query root = null;
    protected HashMap<String, Query> mapQuery = new HashMap<String, Query>();
    protected ArrayList<Field> fields = new ArrayList<Field>();

    public Table() {
    }

    public List<Field> fields() {
        return fields;
    }

    public void removeRec(int index) {
        for (Map.Entry<String, Query> entry : mapQuery.entrySet()) {
            Table table = entry.getValue();
            table.remove(index);
        }
    }

    public void set(Object value, int index, Field field) {
        Object value2 = get(index, field);
        if (value2 != null && value != null && value2.equals(value)) {
            return;
        }
        Record record = get(index);
        record.set(field.ordinal(), value);
    }

    public Object get(int index, Field field) {
        if (index != -1) {
            Record record = get(index);
            return (record == null) ? null : record.get(field);
        }
        return null;
    }

    public <T> T getAs(int index, Field field) {
        Object obj = get(index, field);
        obj = (obj == null) ? -1 : obj;
        return (T) obj;
    }

    public <T> T getAs(int index, Field field, Object def) {
        Object obj = get(index, field);
        return (obj == null) ? (T) def : (T) obj;
    }

    public Record find(Field field, Object value) {
        return this.stream().filter(rec -> rec.get(field).equals(value)).findFirst().orElse(field.newRecord(Query.SEL));
    }
    
    public Record find(Field field, int value, Field field2, int value2) {
        return this.stream().filter(rec -> rec.get(field).equals(value) && rec.get(field2).equals(value2)).findFirst().orElse(field.newRecord(Query.SEL));
    }    

    public Record find(List<Record> data, Field field, Object value) {
        return data.stream().filter(rec -> rec.get(field).equals(value)).findFirst().orElse(field.newRecord(Query.SEL));
    }

    public Record find(List<Record> data, Field field, int value, Field field2, int value2) {
        return data.stream().filter(rec -> rec.get(field).equals(value) && rec.get(field2).equals(value2)).findFirst().orElse(field.newRecord(Query.SEL));
    }

    public List<Record> filter(Field field, Object value) {
        return this.stream().filter(rec -> rec.get(field).equals(value)).collect(toList());
    }
    
    public List<Record> filter(Field field, int value, Field field2, int value2) {
        return this.stream().filter(rec -> rec.get(field).equals(value) && rec.get(field2).equals(value2)).collect(toList());
    }       
}
