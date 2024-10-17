package enums;

import dataset.Field;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;

public interface Enam {

    public String name();

    public int ordinal();

    default int numb() {
        return -1;
    }

    default int pass() {
        return -1;
    }

    default String text() {
        return null;
    }

    default Enam[] fields() {
        return null;
    }

    default Enam find(int id) {
        return Stream.of(fields()).filter(en -> en.numb() == id).findFirst().orElse(null);
    }

    default List<String> dict() {
        return null;
    }

    default String def() {
        return null;
    }

    default AbstractFormatterFactory format() {
        return null;
    }

    default boolean check(String c) {
        return false;
    }
}
